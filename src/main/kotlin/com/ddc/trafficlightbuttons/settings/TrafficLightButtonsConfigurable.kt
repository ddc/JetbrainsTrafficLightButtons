package com.ddc.trafficlightbuttons.settings

import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.application.ex.ApplicationManagerEx
import com.intellij.openapi.options.Configurable
import com.intellij.openapi.ui.Messages
import com.intellij.ui.dsl.builder.panel
import javax.swing.JComponent

class TrafficLightButtonsConfigurable : Configurable {
    private val settings = TrafficLightButtonsSettings.getInstance()
    private var buttonPlacement = settings.buttonPlacement

    override fun getDisplayName() = "Traffic Light Buttons"

    override fun createComponent(): JComponent =
        panel {
            group("Window Buttons") {
                row("Button placement:") {
                    comboBox(listOf("LEFT", "RIGHT"))
                        .applyToComponent {
                            selectedItem = buttonPlacement
                        }.onChanged {
                            buttonPlacement = it.selectedItem as? String ?: "RIGHT"
                        }
                }
            }
        }

    override fun isModified() = buttonPlacement != settings.buttonPlacement

    override fun apply() {
        settings.buttonPlacement = buttonPlacement
        ApplicationManager.getApplication().saveSettings()
        // Show restart dialog AFTER the Settings dialog closes
        ApplicationManager.getApplication().invokeLater {
            val result =
                Messages.showYesNoDialog(
                    "Button placement changed to $buttonPlacement. Restart the IDE to apply?",
                    "Traffic Light Buttons",
                    "Restart",
                    "Later",
                    Messages.getQuestionIcon(),
                )
            if (result == Messages.YES) {
                ApplicationManagerEx.getApplicationEx().restart(true)
            }
        }
    }

    override fun reset() {
        buttonPlacement = settings.buttonPlacement
    }
}
