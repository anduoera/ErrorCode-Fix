package com.github.anduoera.errorcodefix.setting

import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.components.PersistentStateComponent
import com.intellij.openapi.components.State
import com.intellij.openapi.components.Storage
import org.jetbrains.annotations.NonNls

@State(
    name = "com.github.anduoera.errorcodefix.setting.ErrorCodeGenerateAppSettings",
    storages = [Storage("ErrorCodeGenerateAppSettings.xml")]
)
class ErrorCodeGenerateAppSettings : PersistentStateComponent<ErrorCodeGenerateAppSettings.State> {
    class State {
        var errorCodes: List<String> = listOf()
    }

    companion object {
        fun getInstance(): ErrorCodeGenerateAppSettings {
            return ApplicationManager.getApplication()
                .getService(ErrorCodeGenerateAppSettings::class.java)
        }
    }

    private var myState = State()

    override fun getState(): State {
        return myState
    }

    override fun loadState(p0: State) {
        myState = p0
    }
}