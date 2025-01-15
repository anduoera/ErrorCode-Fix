package com.github.anduoera.errorcodefix.languages

import com.github.anduoera.errorcodefix.constants.DEFAULT_RULE_BINDING
import com.github.anduoera.errorcodefix.setting.ErrorCodeGenerateAppSettings
import com.github.anduoera.errorcodefix.setting.ParamValidationAppSettings
import com.goide.psi.GoFunctionDeclaration
import com.goide.psi.GoMethodDeclaration
import com.goide.psi.GoReceiver
import com.goide.psi.impl.GoReceiverImpl
import com.intellij.codeInsight.completion.CompletionParameters
import com.intellij.codeInsight.completion.CompletionProvider
import com.intellij.codeInsight.completion.CompletionResultSet
import com.intellij.codeInsight.lookup.LookupElementBuilder
import com.intellij.psi.util.PsiTreeUtil
import com.intellij.util.ProcessingContext

class ErrorCodeGenerateCompletionProvider : CompletionProvider<CompletionParameters>() {
    override fun addCompletions(
        parameters: CompletionParameters,
        context: ProcessingContext,
        result: CompletionResultSet
    ) {

        val goMethodDeclaration = PsiTreeUtil.getParentOfType(
            parameters.position,
            GoMethodDeclaration::class.java
        )

        val functionName = goMethodDeclaration?.name ?: ""

        val goReceiverName = PsiTreeUtil.getChildOfType(goMethodDeclaration, GoReceiverImpl::class.java)?.name ?: ""

        val file = parameters.position.containingFile
        var fileName = file?.name ?: ""
        fileName = fileName.replace(".go", "")
        val errorCodes = ErrorCodeGenerateAppSettings.getInstance().state.errorCodes

        errorCodes.forEach {
            val str = it
                .replace("`file`", toCamelCase(fileName, true))
                .replace("`func`", toCamelCase(functionName, true))
            val tag = it
                .replace("`file`", "")
                .replace("`func`", "")
            result.addElement(
                LookupElementBuilder.create(tag)
                    .withPresentableText(tag)
                    .withInsertHandler { context, item ->

                        val errorCode = "$goReceiverName.throw.ErrorCode = exception.ErrorCode$str"
                        val errorMessage = "$goReceiverName.throw.ErrorMessage = exception.ErrorMessage$str"
                        val insertStr = "$errorCode\n$errorMessage"
                        context.document.replaceString(
                            context.startOffset,
                            context.tailOffset,
                            insertStr
                        )
                    }
                    .withTypeText(it)
            )
        }
    }
}

class ErrorCodeGenerateFuncCompletionProvider : CompletionProvider<CompletionParameters>() {
    override fun addCompletions(
        parameters: CompletionParameters,
        context: ProcessingContext,
        result: CompletionResultSet
    ) {

        val goFunctionDeclaration = PsiTreeUtil.getParentOfType(
            parameters.position,
            GoFunctionDeclaration::class.java
        )

        val functionName = goFunctionDeclaration?.name ?: ""


        val file = parameters.position.containingFile
        var fileName = file?.name ?: ""
        fileName = fileName.replace(".go", "")
        val errorCodes = ErrorCodeGenerateAppSettings.getInstance().state.errorCodes

        errorCodes.forEach {
            val str = it
                .replace("`file`", toCamelCase(fileName, true))
                .replace("`func`", toCamelCase(functionName, true))
            val tag = it
                .replace("`file`", "")
                .replace("`func`", "")
            result.addElement(
                LookupElementBuilder.create(tag)
                    .withPresentableText(tag)
                    .withInsertHandler { context, item ->

                        val errorCode = "throw.ErrorCode = exception.ErrorCode$str"
                        val errorMessage = "throw.ErrorMessage = exception.ErrorMessage$str"
                        val insertStr = "$errorCode\n$errorMessage"
                        context.document.replaceString(
                            context.startOffset,
                            context.tailOffset,
                            insertStr
                        )
                    }
                    .withTypeText(it)
            )
        }
    }
}

fun toCamelCase(input: String, pascalCase: Boolean = false): String {
    if (input == "") {
        return ""
    }
    return if (input.contains('_')) {
        // 处理蛇形命名
        snakeToCamelCase(input, pascalCase)
    } else {
        // 处理驼峰命名
        if (pascalCase) camelToPascalCase(input) else input
    }
}

fun snakeToCamelCase(snakeStr: String, pascalCase: Boolean = false): String {
    return snakeStr.split('_')
        .mapIndexed { index, word ->
            if (index == 0 && !pascalCase) word
            else word.replaceFirstChar { it.uppercase() }
        }
        .joinToString("")
}

fun camelToPascalCase(camelStr: String): String {
    return camelStr.replaceFirstChar { it.uppercase() }
}