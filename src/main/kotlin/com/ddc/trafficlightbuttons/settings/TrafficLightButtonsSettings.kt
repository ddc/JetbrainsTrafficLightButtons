package com.ddc.trafficlightbuttons.settings

import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.components.PersistentStateComponent
import com.intellij.openapi.components.Service
import com.intellij.openapi.components.State
import com.intellij.openapi.components.Storage

@Service(Service.Level.APP)
@State(name = "TrafficLightButtonsSettings", storages = [Storage("traffic-light-buttons.xml")])
class TrafficLightButtonsSettings : PersistentStateComponent<TrafficLightButtonsSettings.State> {
    data class State(
        var buttonPlacement: String = "RIGHT",
    )

    private var state = State()

    override fun getState(): State = state

    override fun loadState(state: State) {
        this.state = state
    }

    var buttonPlacement: String
        get() = state.buttonPlacement
        set(value) {
            state.buttonPlacement = value
        }

    companion object {
        fun getInstance(): TrafficLightButtonsSettings =
            ApplicationManager.getApplication().getService(TrafficLightButtonsSettings::class.java)
    }
}
