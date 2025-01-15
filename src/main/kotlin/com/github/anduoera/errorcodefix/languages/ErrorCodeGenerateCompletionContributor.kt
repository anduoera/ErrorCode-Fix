package com.github.anduoera.errorcodefix.languages

import com.goide.psi.GoFunctionDeclaration
import com.goide.psi.GoFunctionType
import com.goide.psi.GoMethodDeclaration
import com.goide.psi.GoTag
import com.intellij.codeInsight.completion.CompletionContributor
import com.intellij.codeInsight.completion.CompletionType
import com.intellij.patterns.PlatformPatterns

class ErrorCodeGenerateCompletionContributor : CompletionContributor() {
    init {
        extend(
            CompletionType.BASIC,
            PlatformPatterns.psiElement().inside(GoMethodDeclaration::class.java),
            ErrorCodeGenerateCompletionProvider()
        )
    }
}

class ErrorCodeGenerateFuncCompletionContributor : CompletionContributor() {
    init {
        extend(
            CompletionType.BASIC,
            PlatformPatterns.psiElement().inside(GoFunctionDeclaration::class.java),
            ErrorCodeGenerateFuncCompletionProvider()
        )
    }
}


