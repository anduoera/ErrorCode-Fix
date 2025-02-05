package com.github.anduoera.errorcodefix.listeners

import com.github.anduoera.errorcodefix.constants.AKBindingRuleConstants
import com.github.anduoera.errorcodefix.setting.ParamValidationAppSettings
import com.intellij.ide.AppLifecycleListener
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MyAppLifecycleListener : AppLifecycleListener {
    private val aKBindingRule by lazy { AKBindingRuleConstants.instance }
    override fun appFrameCreated(commandLineArgs: MutableList<String>) {
        val state = ParamValidationAppSettings.getInstance().state
        CoroutineScope(Dispatchers.IO).launch {
            aKBindingRule.flushedBySettingUri(state.uri.toString())
        }
    }
}