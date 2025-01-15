package com.github.anduoera.errorcodefix.utils

import com.github.anduoera.errorcodefix.constants.*
import com.goide.psi.impl.GoConstDefinitionImpl
import com.goide.psi.impl.GoConstSpecImpl
import com.goide.psi.impl.GoStringLiteralImpl
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.psi.PsiFile
import com.intellij.psi.PsiManager
import com.intellij.psi.util.PsiTreeUtil


private val errorCodeSetConstants by lazy { ErrorCodeListConstants.instance }

/**
 * 判断指定路径是否存在
 *
 * @param project 当前项目
 * @param relativePath 相对路径（相对于项目根目录）
 * @return 如果路径存在则返回 true，否则返回 false
 */
fun hasPath(project: Project, relativePath: String): Boolean {
    val baseDir = project.baseDir
    val targetFile = baseDir.findFileByRelativePath(relativePath)
    return targetFile != null
}

/**
 * 在指定目录中查找 PSI 文件
 *
 * @param project 当前项目
 * @param relativePath 相对路径（相对于项目根目录）
 * @return 找到的 PSI 文件列表
 */
fun findPsiFilesInDirectory(project: Project, relativePath: String): List<PsiFile> {
    val baseDir = project.baseDir
    val targetDir = baseDir.findFileByRelativePath(relativePath) ?: return emptyList()

    val psiManager = PsiManager.getInstance(project)
    val psiFileList = mutableListOf<PsiFile>()
    findPsiFilesRecursively(psiManager, targetDir, psiFileList)
    return psiFileList
}

/**
 * 递归查找 PSI 文件
 */
private fun findPsiFilesRecursively(psiManager: PsiManager, dir: VirtualFile, psiFileList: MutableList<PsiFile>) {
    if (dir.isDirectory) {
        dir.children.forEach { child ->
            if (child.isDirectory) {
                findPsiFilesRecursively(psiManager, child, psiFileList)
            } else {
                // 将 PSI 操作包装在读操作中
                ApplicationManager.getApplication().runReadAction {
                    val psiFile = psiManager.findFile(child)
                    if (psiFile != null && psiFile.name != ERROR_CODE_BASE_DIR_SKIP_FILE) {
                        psiFileList.add(psiFile)
                    }
                }
            }
        }
    }
}

/**
 * 查询文件夹中的所有ErrorCode Const
 */
fun getErrorCodeMapConstants(
    files: List<PsiFile>,
    projectName: String
) {
    ApplicationManager.getApplication().runReadAction {
        files.forEach { psiFile ->
            errorCodeSetConstants.removeErrorCodesByFileName(projectName, psiFile.name)
            try {
                // 获取文件中的所有 GoConstDeclaration 节点
                val goConstSpecList = PsiTreeUtil.findChildrenOfAnyType(psiFile, GoConstSpecImpl::class.java)

                goConstSpecList.forEach { goConstSpec ->
                    try {
                        val goConstDefinition =
                            PsiTreeUtil.findChildOfType(goConstSpec, GoConstDefinitionImpl::class.java)
                        val goStringLiteral = PsiTreeUtil.findChildOfType(goConstSpec, GoStringLiteralImpl::class.java)

                        if (goConstDefinition != null) {
                            goConstDefinition.text?.let {
                                val errorCodeValue = goStringLiteral?.decodedText
                                if (it.startsWith(ERROR_CODE_BASE_START)) {

                                    val numLong = errorCodeValue?.toLongOrNull()
                                    if (numLong != null) {
                                        errorCodeSetConstants.addErrorCode(projectName, psiFile.name, errorCodeValue)
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
            } catch (e: Exception) {
                println("Error processing file ${psiFile.name}: ${e.message}")
                e.printStackTrace()
            }
        }
    }
}

/**
 * 确认在指定规则的项目下
 */
fun checkIsErrorCodeFileInTargetPath(file: VirtualFile, project: Project): Boolean {
    return !isErrorCodeFileInTargetPath(file) || file.name == ERROR_CODE_BASE_DIR_SKIP_FILE || project.name.endsWith(
        FOUNDATION_TAG
    )
}