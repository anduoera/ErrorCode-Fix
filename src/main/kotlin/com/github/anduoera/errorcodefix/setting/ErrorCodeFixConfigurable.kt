package com.github.anduoera.errorcodefix.setting

import com.intellij.openapi.options.Configurable
import javax.swing.JComponent
import javax.swing.JPanel

class ErrorCodeFixConfigurable : Configurable {
    private var settingsPanel: JPanel? = null
    override fun createComponent(): JComponent? {
        if (settingsPanel == null) {
            settingsPanel = JPanel()
        }
        return null
    }

    override fun isModified(): Boolean {
        return false
    }

    override fun apply() {
    }

    override fun getDisplayName(): String {
        return "ErrorCode Fix"
    }
}