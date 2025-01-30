package com.github.anduoera.errorcodefix.setting

import com.intellij.ui.components.JBLabel
import com.intellij.ui.components.JBTextArea
import com.intellij.ui.components.JBTextField
import com.intellij.util.ui.FormBuilder
import java.awt.Component
import javax.swing.JComboBox
import javax.swing.JPanel

class ParamValidationAppSettingsComponent() {
    private var myMainPanel = JPanel()
    private val myUriText = JBTextField()
    private val myShowTextTypeComboBox = JComboBox<String>()
    private val myExampleTextArea = JBTextArea(7, 40)

    init {
        myShowTextTypeComboBox.addItem("show remark")
        myShowTextTypeComboBox.addItem("show validation value")

        myExampleTextArea.isEditable = false
        myExampleTextArea.lineWrap = true // 自动换行
        myExampleTextArea.wrapStyleWord = true // 使换行更自然
        myExampleTextArea.isOpaque = false // 透明背景
        myExampleTextArea.background = null // 继承父级背景色
        myExampleTextArea.border = null // 移除边框
        myExampleTextArea.text = """
           [
                {
                    "param_name": "ak_pin",
                    "validator_pattern": "alias",
                    "validator_value": "numeric,min=2,max=8",
                    "remark": "Pin Code\n支持字符类型：数字\n字符长度：2~8"
                }
           ]
        """.trimIndent()

        myMainPanel = FormBuilder.createFormBuilder()
            .addLabeledComponent(JBLabel("Uri"), myUriText, 1, false)
            .addLabeledComponent(JBLabel("Example"), myExampleTextArea, 1, false)
            .addLabeledComponent(JBLabel("Show "), myShowTextTypeComboBox, 1, false)
            .addComponentFillVertically(JPanel(), 0)
            .panel
    }

    fun getPanel(): JPanel {
        return myMainPanel
    }

    fun getPreferredFocusedComponent(): Component {
        return myUriText
    }

    fun getUriText(): String {
        return myUriText.text
    }

    fun setUriText(newText: String) {
        myUriText.text = newText
    }

    fun getShowTextType(): String {
        return myShowTextTypeComboBox.selectedItem as String
    }

    fun setShowTextType(role: String) {
        myShowTextTypeComboBox.selectedItem = role
    }
}