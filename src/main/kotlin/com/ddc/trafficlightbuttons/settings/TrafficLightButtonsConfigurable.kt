package com.ddc.trafficlightbuttons.settings

import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.application.ex.ApplicationManagerEx
import com.intellij.openapi.options.Configurable
import com.intellij.openapi.ui.Messages
import com.intellij.ui.dsl.builder.panel
import javax.swing.JComboBox
import javax.swing.JComponent

class TrafficLightButtonsConfigurable : Configurable {
    private val settings = TrafficLightButtonsSettings.getInstance()
    private var buttonPlacement = settings.buttonPlacement
    private var buttonOrder = settings.buttonOrder
    private lateinit var orderComboBox: JComboBox<String>

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
                            orderComboBox.isEnabled = buttonPlacement == "RIGHT"
                        }
                }
                row("Button order:") {
                    comboBox(listOf("IDE Default", "macOS Style"))
                        .applyToComponent {
                            orderComboBox = this
                            selectedItem = if (buttonOrder == "MACOS") "macOS Style" else "IDE Default"
                            isEnabled = buttonPlacement == "RIGHT"
                        }.onChanged {
                            buttonOrder = if (it.selectedItem == "macOS Style") "MACOS" else "IDE_DEFAULT"
                        }
                }
            }
        }

    override fun isModified() =
        buttonPlacement != settings.buttonPlacement ||
            buttonOrder != settings.buttonOrder

    override fun apply() {
        settings.buttonPlacement = buttonPlacement
        settings.buttonOrder = buttonOrder
        ApplicationManager.getApplication().saveSettings()
        ApplicationManager.getApplication().invokeLater {
            val result =
                Messages.showYesNoDialog(
                    "Settings changed. Restart the IDE to apply?",
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
        buttonOrder = settings.buttonOrder
    }
}
