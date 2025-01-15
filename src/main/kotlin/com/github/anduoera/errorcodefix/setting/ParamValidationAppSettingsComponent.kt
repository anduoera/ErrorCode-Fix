package com.github.anduoera.errorcodefix.setting

import com.intellij.ui.components.JBLabel
import com.intellij.ui.components.JBTextField
import com.intellij.util.ui.FormBuilder
import java.awt.Component
import javax.swing.JComboBox
import javax.swing.JPanel

class ParamValidationAppSettingsComponent() {
    private var myMainPanel = JPanel()
    private val myUriText = JBTextField()
    private val myShowTextTypeComboBox = JComboBox<String>()

    init {
        myShowTextTypeComboBox.addItem("show remark")
        myShowTextTypeComboBox.addItem("show validation value")
        myMainPanel = FormBuilder.createFormBuilder()
            .addLabeledComponent(JBLabel("Uri"), myUriText, 1, false)
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