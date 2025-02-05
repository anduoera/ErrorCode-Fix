package com.github.anduoera.errorcodefix.setting

import com.github.anduoera.errorcodefix.constants.AKBindingRuleConstants
import com.github.anduoera.errorcodefix.constants.ErrorCodeListConstants
import com.intellij.openapi.options.Configurable
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.swing.JComponent

class ParamValidationAppSettingsConfigurable : Configurable {

    private lateinit var myComponent: ParamValidationAppSettingsComponent
    private val aKBindingRule by lazy { AKBindingRuleConstants.instance }
    override fun createComponent(): JComponent {
        myComponent = ParamValidationAppSettingsComponent()
        val state = ParamValidationAppSettings.getInstance().state
        myComponent.setUriText(state.uri.toString())
        myComponent.setShowTextType(state.showTextType)
        CoroutineScope(Dispatchers.IO).launch {
            aKBindingRule.flushedBySettingUri(state.uri.toString())
        }
        return myComponent.getPanel()
    }

    override fun isModified(): Boolean {
        val state = ParamValidationAppSettings.getInstance().state
        return (myComponent.getUriText() != state.uri) ||
                (myComponent.getShowTextType() != state.showTextType)
    }

    override fun apply() {
        val state = ParamValidationAppSettings.getInstance().state
        state.uri = myComponent.getUriText()
        state.showTextType = myComponent.getShowTextType()
        CoroutineScope(Dispatchers.IO).launch {
            aKBindingRule.flushedBySettingUri(state.uri.toString())
        }
    }

    override fun getDisplayName(): String {
        return "Validation Settings"
    }
}