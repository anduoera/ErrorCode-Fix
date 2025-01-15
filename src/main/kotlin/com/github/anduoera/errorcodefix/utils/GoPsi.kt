package com.github.anduoera.errorcodefix.utils

import com.github.anduoera.errorcodefix.constants.*
import com.goide.psi.*
import com.goide.psi.impl.*
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.command.WriteCommandAction
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.psi.*
import com.intellij.psi.codeStyle.CodeStyleManager
import com.intellij.psi.impl.source.tree.LeafPsiElement
import com.intellij.psi.util.PsiTreeUtil


private val errorCodeList by lazy { ErrorCodeListConstants.instance }

/**
 * 修复对应的错误码
 */
fun errorCodeFix(project: Project, psiFile: PsiFile, virtualFile: VirtualFile) {
    val stringLiteralList = mutableListOf<GoStringLiteral>()
    try {
        val goConstSpecList = PsiTreeUtil.findChildrenOfAnyType(psiFile, GoConstSpecImpl::class.java)
        goConstSpecList.forEach { goConstSpec ->
            try {
                val goConstDefinition =
                    PsiTreeUtil.findChildOfType(goConstSpec, GoConstDefinitionImpl::class.java)
                val goStringLiteral = PsiTreeUtil.findChildOfType(goConstSpec, GoStringLiteralImpl::class.java)

                if (goConstDefinition != null && goStringLiteral != null) {
                    goConstDefinition.text?.let {
                        if (it.startsWith(ERROR_CODE_BASE_START)) {
                            val errorCodeValue = goStringLiteral.decodedText
                            if (errorCodeValue.isEmpty() ||
                                errorCodeList.isErrorCodeDuplicate(project.name, errorCodeValue) ||
                                errorCodeValue.length != 4 ||
                                errorCodeValue.toLongOrNull() == null
                            ) {
                                stringLiteralList.add(goStringLiteral)
                                errorCodeList.removeErrorCode(project.name, psiFile.name, errorCodeValue)
                            }
                        }
                    }
                }
            } catch (e: Exception) {
                // 捕获并处理异常，避免程序停止
                println("Error processing GoConstSpec: ${e.message}")
                e.printStackTrace() // 打印异常堆栈信息（可选）
            }
        }
        insertErrorCodeByPsiElement(project, psiFile, stringLiteralList)
    } catch (e: Exception) {
        println("Error processing file ${psiFile.name}: ${e.message}")
        e.printStackTrace()
    }
}

/**
 * 修复文件错误码
 */
fun insertErrorCodeByPsiElement(project: Project, psiFile: PsiFile, goStringLiterals: List<GoStringLiteral>) {
    WriteCommandAction.runWriteCommandAction(project) {
        goStringLiterals.forEach { goStringLiteral ->
            val newErrorCodes = errorCodeList.getNewErrorCodes(project.name, psiFile.name)
            val createStringLiteral =
                GoElementFactory.createStringLiteral(project, "\"$newErrorCodes\"")
            goStringLiteral.replace(createStringLiteral)
            errorCodeList.addErrorCode(project.name, psiFile.name, newErrorCodes)
        }


        val document = PsiDocumentManager.getInstance(project).getDocument(psiFile)
        if (document != null) {
            PsiDocumentManager.getInstance(project).commitDocument(document)
        }
    }
}

/**
 * 根据error code file添加错误码
 */
fun generateErrorCodeToSelectFile(file: String, psiFile: PsiFile, project: Project) {
    val chooseFile = project.baseDir.findFileByRelativePath("$ERROR_CODE_BASE_DIR/$file")
    val chooseErrorCodeFile = chooseFile?.let { PsiManager.getInstance(project).findFile(it) }
    if (chooseErrorCodeFile != null) {
        val noReferenceErrorCode = getFileNoReferenceErrorCode(psiFile)
        generateNoReferenceErrorCode(chooseErrorCodeFile, noReferenceErrorCode, project)
    }
}

/**
 * 获取当前文件下所有未引用的错误码
 */
fun getFileNoReferenceErrorCode(psiFile: PsiFile): Set<String> {
    val errorCodes = mutableSetOf<String>()
    try {
        val goReferenceExpressionList =
            PsiTreeUtil.findChildrenOfAnyType(psiFile, GoReferenceExpressionImpl::class.java)
        goReferenceExpressionList.forEach { goReferenceExpression ->
            if (goReferenceExpression != null && goReferenceExpression.isConstant) {
                val resolve = goReferenceExpression.resolve()
                val constants = goReferenceExpression.text
                if (resolve == null && constants.startsWith(ERROR_CODE_RESOLVE_START)) {
                    errorCodes.add(constants.replace(ERROR_CODE_RESOLVE_START, "").trim())
                }
                if (resolve == null && constants.startsWith(ERROR_MESSAGE_RESOLVE_START)) {
                    errorCodes.add(constants.replace(ERROR_MESSAGE_RESOLVE_START, "").trim())
                }

            }
        }
    } catch (e: Exception) {
        println("Error GoReferenceExpressionImpl file ${psiFile.name}: ${e.message}")
        e.printStackTrace()
    }
    return errorCodes
}

/**
 * 将未定义的错误码刷入文件
 */
fun generateNoReferenceErrorCode(psiFile: PsiFile, errorCodes: Set<String>, project: Project) {
    try {
        val goConstDeclaration = PsiTreeUtil.findChildrenOfAnyType(psiFile, GoConstDeclaration::class.java)
        var errorCodeGoConstSpec = goConstDeclaration.first()
        var errorMessageGoConstSpec = goConstDeclaration.last()
        goConstDeclaration.forEach { constDeclaration ->
            constDeclaration.constSpecList.last().text.let {
                if (it.startsWith(ERROR_CODE_BASE_START)) {
                    errorCodeGoConstSpec = constDeclaration

                }
                if (it.startsWith(ERROR_MESSAGE_BASE_START)) {
                    errorMessageGoConstSpec = constDeclaration

                }
            }
        }


        WriteCommandAction.runWriteCommandAction(psiFile.project) {
            errorCodes.forEach {
                val errorCodeStr = ERROR_CODE_BASE_START + it
                val errormessageStr = ERROR_MESSAGE_BASE_START + it

                val errorCodeConstSpecLast = errorCodeGoConstSpec.lastChild
                val errorMessageConstSpecLast = errorMessageGoConstSpec.lastChild
                //写入ErrorCode
                if (!hasVariableName(errorCodeGoConstSpec, errorCodeStr)) {
                    val newErrorCodes = errorCodeList.getNewErrorCodes(project.name, psiFile.name)
                    val newGoErrorCodeConst =
                        GoElementFactory.createConstSpec(
                            psiFile.project,
                            errorCodeStr,
                            "",
                            "\"$newErrorCodes\""
                        )

                    errorCodeGoConstSpec.addBefore(newGoErrorCodeConst, errorCodeConstSpecLast)
                    errorCodeList.addErrorCode(project.name, psiFile.name, newErrorCodes)

                }

                //写入ErrorMessage
                if (!hasVariableName(errorMessageGoConstSpec, errormessageStr)) {
                    val errorMessage = it.replace(Regex("([A-Z])"), " $1").lowercase().trim()
                    val goErrorMessageConst =
                        GoElementFactory.createConstSpec(
                            psiFile.project,
                            errormessageStr,
                            "",
                            "\"$errorMessage\""
                        )
                    errorMessageGoConstSpec.addBefore(goErrorMessageConst, errorMessageConstSpecLast)

                }
            }

            val document = PsiDocumentManager.getInstance(project).getDocument(psiFile)
            if (document != null) {
                PsiDocumentManager.getInstance(project).commitDocument(document)
            }
        }

    } catch (e: Exception) {
        println("Error GoReferenceExpressionImpl file ${psiFile.name}: ${e.message}")
        e.printStackTrace()
    }

}


/**
 * 判断 GoConstDeclaration 是否包含指定的变量名
 *
 * @param constDeclaration GoConstDeclaration 实例
 * @param variableName 要查找的变量名
 * @return 如果找到匹配的变量名，返回 true；否则返回 false
 */
fun hasVariableName(constDeclaration: GoConstDeclaration, variableName: String): Boolean {
    for (constSpec in constDeclaration.constSpecList) {
        if (constSpec.constDefinitionList.any { it.name == variableName }) {
            return true
        }
    }
    return false
}