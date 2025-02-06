package com.github.anduoera.errorcodefix.languages

import com.github.anduoera.errorcodefix.constants.AKBindingRuleConstants
import com.github.anduoera.errorcodefix.constants.DEFAULT_RULE_BINDING
import com.github.anduoera.errorcodefix.setting.ParamValidationAppSettings
import com.intellij.codeInsight.completion.CompletionParameters
import com.intellij.codeInsight.completion.CompletionProvider
import com.intellij.codeInsight.completion.CompletionResultSet
import com.intellij.codeInsight.lookup.LookupElementBuilder
import com.intellij.openapi.util.TextRange
import com.intellij.psi.PsiElement
import com.intellij.ui.JBColor
import com.intellij.util.ProcessingContext
import java.awt.Color

class GoTagCompletionProvider : CompletionProvider<CompletionParameters>() {
    private val aKBindingRule by lazy { AKBindingRuleConstants.instance }
    override fun addCompletions(
        parameters: CompletionParameters,
        context: ProcessingContext,
        result: CompletionResultSet
    ) {
        val state = ParamValidationAppSettings.getInstance().state
        //系统默认binding导入
        val element = parameters.position
        DEFAULT_RULE_BINDING
            .forEach { (tag, description) ->
                result.addElement(
                    LookupElementBuilder.create(tag)
                        .withPresentableText(tag)
                        .withInsertHandler { context, item ->
                            var insertStr = tag
                            var startOffset = context.startOffset - 1
                            if (!element.text.contains("binding:")) {
                                insertStr = "binding:\"$tag\""
                                startOffset += 1
                            }
                            context.document.replaceString(
                                startOffset,
                                context.tailOffset,
                                insertStr
                            )
                        }
                        .withTypeText(description)
                )
            }

        aKBindingRule.getAllRules().forEach { (tag, rule) ->
            var description = rule.remark
            if (state.showTextType == "show validation value") {
                description = rule.validatorValue
            }
            var textColor = JBColor(Color(0, 103, 255, 100), Color(0, 103, 255, 100))
            if (rule.validatorPattern != "regexp") {
                textColor = JBColor(Color(255, 0, 255, 100), Color(255, 0, 255, 100))
            }
            result.addElement(
                LookupElementBuilder.create(tag)
                    .withPresentableText(tag)
                    .withItemTextForeground(textColor)
                    .withInsertHandler { context, item ->
                        var insertStr = tag
                        var startOffset = context.startOffset - 1
                        if (!element.text.contains("binding:")) {
                            insertStr = "binding:\"$tag\""
                            startOffset += 1
                        }
                        context.document.replaceString(
                            startOffset,
                            context.tailOffset,
                            insertStr
                        )
                    }
                    .withTypeText(description)
            )
        }
    }
}