package com.github.anduoera.errorcodefix.languages

import com.goide.psi.GoTag
import com.intellij.codeInsight.completion.CompletionContributor
import com.intellij.codeInsight.completion.CompletionType
import com.intellij.patterns.PlatformPatterns

/**
 * binding代码完成
 */
class BindingCompletionContributor : CompletionContributor() {
    init {
        extend(
            CompletionType.BASIC,
            PlatformPatterns.psiElement().inside(GoTag::class.java),
            GoTagCompletionProvider()
        )
    }
}