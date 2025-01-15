package com.github.anduoera.errorcodefix.setting

import com.intellij.openapi.options.Configurable
import javax.swing.JComponent

class ErrorCodeGenerateAppSettingsConfigurable : Configurable {

    private lateinit var myComponent: ErrorCodeGenerateAppSettingsComponent

    override fun createComponent(): JComponent {
        myComponent = ErrorCodeGenerateAppSettingsComponent()
        val state = ErrorCodeGenerateAppSettings.getInstance().state
        state.errorCodes.forEach {
            myComponent.setErrorCode(it)
        }
        return myComponent.getPanel()
    }

    override fun isModified(): Boolean {
        val errorCodeList = myComponent.getAllErrorCode()
        val state = ErrorCodeGenerateAppSettings.getInstance().state
        if (state.errorCodes.size != errorCodeList.size) {
            return true
        }

        for ((index, s) in state.errorCodes.withIndex()) {
            if (errorCodeList[index] != s) {
                return true
            }
        }
        return false
    }

    override fun apply() {
        ErrorCodeGenerateAppSettings.getInstance().state.errorCodes = myComponent.getAllErrorCode()
    }

    override fun getDisplayName(): String {
        return "ErrorCode Generate"
    }
}