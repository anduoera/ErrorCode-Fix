package com.github.anduoera.errorcodefix.languages

import com.github.anduoera.errorcodefix.constants.AKBindingRuleConstants
import com.github.anduoera.errorcodefix.constants.DEFAULT_RULE_BINDING
import com.goide.GoLanguage
import com.goide.psi.GoElement
import com.goide.psi.GoFile
import com.goide.psi.GoLiteral
import com.goide.psi.GoStringLiteral
import com.goide.psi.GoTag
import com.intellij.model.Symbol
import com.intellij.openapi.project.Project
import com.intellij.platform.backend.documentation.DocumentationTarget
import com.intellij.platform.backend.documentation.DocumentationTargetProvider
import com.intellij.platform.backend.documentation.PsiDocumentationTargetProvider
import com.intellij.platform.backend.documentation.SymbolDocumentationTargetProvider
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import com.intellij.refactoring.suggested.endOffset
import com.intellij.refactoring.suggested.startOffset

class BindingPsiDocumentationTargetProvider : DocumentationTargetProvider {
    private val aKBindingRule by lazy { AKBindingRuleConstants.instance }
    override fun documentationTargets(psiFile: PsiFile, offset: Int): MutableList<out DocumentationTarget> {

        val elementAtOffset = psiFile.findElementAt(offset)

        val outDocumentationTargets = mutableListOf<DocumentationTarget>()

        val tagElement = elementAtOffset?.parent?.let { parent ->
            if (parent is GoStringLiteral) {
                if (parent.parent is GoTag) {
                    parent.parent
                } else {
                    null
                }
            } else if (parent is GoTag) {
                parent
            } else {
                null
            }
        }
        if (tagElement == null) return outDocumentationTargets

        val tags = extractBindingValues(tagElement.text)
        val nearestOffsetTag = getNearestOffsetFromString(tagElement, tags, offset)

        var tagKey = nearestOffsetTag.trim()
        if (tagKey.contains("=")) {
            if (tagKey.split("=").size > 1) {
                tagKey = tagKey.split("=")[0]
            }
        }
        if (DEFAULT_RULE_BINDING.containsKey(tagKey)) {
            DEFAULT_RULE_BINDING[tagKey]?.let {
                outDocumentationTargets.add(
                    BuildingDocumentationTarget(
                        tagElement,
                        tagKey,
                        "default",
                        "default",
                        it,
                        true
                    )
                )
            }
            return outDocumentationTargets
        }

        if (aKBindingRule.containsKey(tagKey)) {
            aKBindingRule.getRule(tagKey).let {
                outDocumentationTargets.add(
                    BuildingDocumentationTarget(
                        tagElement,
                        tagKey,
                        it.validatorPattern,
                        it.validatorValue,
                        it.remark,
                        false
                    )
                )

            }
            return outDocumentationTargets
        }


        return outDocumentationTargets
    }
}

fun extractBindingValues(input: String): List<String> {
    // 使用正则表达式提取 binding 后面的内容
    val regex = """binding:"([^"]+)"""".toRegex()
    val matchResult = regex.find(input)

    // 如果匹配成功，提取并分割
    return matchResult?.groups?.get(1)?.value?.split(",") ?: emptyList()
}

fun getNearestOffsetFromString(tagElement: PsiElement, tags: List<String>, offset: Int): String {
    val tagText = tagElement.text
    val tagStartOffset = tagElement.startOffset

    var nearestTag: String? = null
    var minDistance = Int.MAX_VALUE

    for (tag in tags) {
        var index = tagText.indexOf(tag)
        while (index >= 0) {
            val absoluteOffset = tagStartOffset + index
            val distance = Math.abs(absoluteOffset - offset)

            if (distance < minDistance) {
                minDistance = distance
                nearestTag = tag
            }

            index = tagText.indexOf(tag, index + 1)
        }
    }

    return nearestTag ?: ""
}