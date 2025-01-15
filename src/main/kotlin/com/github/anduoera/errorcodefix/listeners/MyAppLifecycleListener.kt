package com.github.anduoera.errorcodefix.listeners

import com.github.anduoera.errorcodefix.constants.AKBindingRuleConstants
import com.github.anduoera.errorcodefix.setting.ParamValidationAppSettings
import com.intellij.ide.AppLifecycleListener

class MyAppLifecycleListener : AppLifecycleListener {
    private val aKBindingRule by lazy { AKBindingRuleConstants.instance }
    override fun appFrameCreated(commandLineArgs: MutableList<String>) {
        val state = ParamValidationAppSettings.getInstance().state
        aKBindingRule.flushedBySettingUri(state.uri.toString())
    }
}