package com.github.anduoera.errorcodefix.languages

import com.intellij.model.Pointer
import com.intellij.platform.backend.documentation.DocumentationTarget
import com.intellij.platform.backend.presentation.TargetPresentation
import com.intellij.psi.PsiElement
import com.intellij.codeInsight.navigation.targetPresentation
import com.intellij.platform.backend.documentation.DocumentationResult
import com.intellij.refactoring.suggested.createSmartPointer

class BuildingDocumentationTarget(
    private val element: PsiElement,
    private val paramName: String,
    private val validatorPattern: String,
    private val validatorValue: String,
    private val remark: String,
    private val isDefault: Boolean
) : DocumentationTarget {

    override fun computeDocumentation(): DocumentationResult {

        val (patternColor, patternBg) = when (validatorPattern) {
            "regexp" -> "#007acc" to "#e6f7ff"  // 蓝色主题
            else -> "#d32f2f" to "#ffebee"      // 红色主题
        }

        var documentationText = """
        <html>
            <head>
                <style>
                    body {
                        font-family: Arial, sans-serif;
                        font-size: 12px;
                        margin-top: 8px;
                    }
                    .tag {
                        display: inline-block;
                        font-size: 12px;
                        font-weight: bold;
                        border-radius: 4px;
                        color: $patternColor;
                        background-color: $patternBg;
                        margin-top: 8px;
                    }
					.key{
                        font-weight: bold;
						font-size: 13px;
					}
                    .remark {
                        color: gray;
                        font-size: 11px;
                        margin-top: 8px;
                    }
					.value{
                        margin-top: 8px;
                        font-size: 13px;
					}
                </style>
            </head>
            <body>
                <div class="key">$paramName</div>
                <div class="tag">$validatorPattern</div>
                <div class="value">$validatorValue</div>
                <div class="remark">$remark</div>
            </body>
        </html>
    """.trimIndent()

        if (isDefault) {
            documentationText = """
             <html>
            <head>
                <style>
                    body {
                        font-family: Arial, sans-serif;
                        font-size: 12px;
                    }
					.key{
                        font-weight: bold;
						font-size: 13px;
					}
                    .remark {
                        color: gray;
                        font-size: 11px;
                        margin-top: 8px;
                    }
                </style>
            </head>
            <body>
                <div class="key">$paramName</div>
                <div class="remark"> $remark</div>
            </body>
        </html>
        """.trimIndent()

        }

        return DocumentationResult.documentation(documentationText)
    }


    override fun computePresentation(): TargetPresentation {
        return TargetPresentation.builder(paramName)
            .icon(null)  // 可以添加合适的图标
            .presentation()
    }

    // 创建指针，用于引用文档目标
    override fun createPointer(): Pointer<out DocumentationTarget> {
        return DocumentationTargetPointer(element, paramName, validatorPattern, validatorValue, remark, isDefault)
    }
}

// 可能需要一个指针类来实现 createPointer
class DocumentationTargetPointer(
    private val element: PsiElement,
    private val paramName: String,
    private val validatorPattern: String,
    private val validatorValue: String,
    private val remark: String,
    private val isDefault: Boolean
) : Pointer<DocumentationTarget> {
    override fun dereference(): DocumentationTarget {
        return BuildingDocumentationTarget(
            element,
            paramName,
            validatorPattern,
            validatorValue,
            remark,
            isDefault
        )
    }
}
