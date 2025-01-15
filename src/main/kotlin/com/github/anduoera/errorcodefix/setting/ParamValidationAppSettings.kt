package com.github.anduoera.errorcodefix.setting

import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.components.PersistentStateComponent
import com.intellij.openapi.components.State
import com.intellij.openapi.components.Storage
import org.jetbrains.annotations.NonNls


@State(
    name = "com.github.anduoera.errorcodefix.setting.ParamValidationAppSettings",
    storages = [Storage("ParamValidationAppSettings.xml")]
)
class ParamValidationAppSettings : PersistentStateComponent<ParamValidationAppSettings.State> {

    class State {
        var uri: @NonNls String? = ""
        var showTextType: String = "show remark"
    }

    companion object {
        fun getInstance(): ParamValidationAppSettings {
            return ApplicationManager.getApplication()
                .getService(ParamValidationAppSettings::class.java)
        }
    }

    private var myState = State()

    override fun getState(): State {
        return myState
    }

    override fun loadState(validationSettings: State) {
        myState = validationSettings
    }


}