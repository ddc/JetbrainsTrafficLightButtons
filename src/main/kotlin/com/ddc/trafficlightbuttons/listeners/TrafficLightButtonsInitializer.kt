package com.ddc.trafficlightbuttons.listeners

import com.ddc.trafficlightbuttons.settings.TrafficLightButtonsSettings
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.diagnostic.logger
import com.intellij.openapi.project.Project
import com.intellij.openapi.startup.ProjectActivity
import com.intellij.openapi.util.IconLoader
import java.awt.BorderLayout
import java.awt.Container
import java.awt.Dimension
import java.awt.Window
import javax.swing.Icon
import javax.swing.JButton
import javax.swing.JFrame
import javax.swing.Timer

class TrafficLightButtonsInitializer : ProjectActivity {
    companion object {
        private val LOG = logger<TrafficLightButtonsInitializer>()
        private const val ICONS_PATH = "/icons"
        private const val PATCHED_KEY = "trafficLightButtons.patched"

        @Volatile
        private var initialized = false
    }

    private data class ButtonIcons(
        val active: Icon,
        val hovered: Icon,
        val pressed: Icon,
        val inactive: Icon,
    )

    override suspend fun execute(project: Project) {
        if (initialized) return
        initialized = true

        val settings = TrafficLightButtonsSettings.getInstance()
        val placement = settings.buttonPlacement
        val buttonOrder = settings.buttonOrder

        LOG.info("Traffic Light Buttons: initializing with placement=$placement, order=$buttonOrder")

        val closeIcons = loadButtonIcons("close")
        val minimizeIcons = loadButtonIcons("minimize")
        val maximizeIcons = loadButtonIcons("maximize")

        // Use a timer to wait for the IDE to finish building the header,
        // then patch the internal IconSet via reflection.
        val timer =
            Timer(300) {
                try {
                    for (window in Window.getWindows()) {
                        if (window is JFrame) {
                            enforce(window, placement, buttonOrder, closeIcons, minimizeIcons, maximizeIcons)
                        }
                    }
                } catch (e: Exception) {
                    LOG.warn("Traffic Light Buttons: timer error", e)
                }
            }
        timer.isRepeats = true
        ApplicationManager.getApplication().invokeLater { timer.start() }
    }

    private fun loadButtonIcons(name: String): ButtonIcons =
        ButtonIcons(
            active = IconLoader.getIcon("$ICONS_PATH/$name.svg", javaClass),
            hovered = IconLoader.getIcon("$ICONS_PATH/${name}Hovered.svg", javaClass),
            pressed = IconLoader.getIcon("$ICONS_PATH/${name}Pressed.svg", javaClass),
            inactive = IconLoader.getIcon("$ICONS_PATH/${name}Inactive.svg", javaClass),
        )

    private fun enforce(
        frame: JFrame,
        placement: String,
        buttonOrder: String,
        closeIcons: ButtonIcons,
        minimizeIcons: ButtonIcons,
        maximizeIcons: ButtonIcons,
    ) {
        // Enforce button placement via WindowButtonsConfiguration
        enforceButtonPosition(placement, buttonOrder)

        val allButtons = mutableListOf<JButton>()
        findAllWindowButtons(frame, allButtons)
        if (allButtons.isEmpty()) return

        for (button in allButtons) {
            val tooltip = button.toolTipText?.lowercase() ?: ""
            val icons =
                when {
                    tooltip.contains("close") -> closeIcons
                    tooltip.contains("minimize") -> minimizeIcons
                    tooltip.contains("maximize") || tooltip.contains("restore") -> maximizeIcons
                    else -> continue
                }

            // Check if this button is a LinuxFrameButton and patch its internal icons
            if (button.javaClass.simpleName == "LinuxFrameButton") {
                patchLinuxFrameButton(button, icons)
            }

            // Enforce smaller button width to reduce gap between buttons.
            // TitleButtonsPanel.setScaledPreferredSize() resets this on updateUI(),
            // so we enforce it every tick. Keep the height from the IDE.
            val currentPref = button.preferredSize
            val desiredWidth = currentPref.height - 4 // tighter than square
            if (currentPref.width != desiredWidth) {
                val size = Dimension(desiredWidth, currentPref.height)
                button.preferredSize = size
                button.minimumSize = size
                button.maximumSize = size
                button.parent?.revalidate()
            }
        }
    }

    private fun patchLinuxFrameButton(
        button: JButton,
        icons: ButtonIcons,
    ) {
        try {
            // Access the internal 'icons' field: Map<Boolean, IconSet>
            val iconsField = button.javaClass.getDeclaredField("icons")
            iconsField.isAccessible = true

            @Suppress("UNCHECKED_CAST")
            val iconsMap = iconsField.get(button) as? MutableMap<Boolean, Any> ?: return

            // Get the IconSet class
            val iconSetClass =
                Class.forName(
                    "com.intellij.openapi.wm.impl.customFrameDecorations.frameButtons.IconSet",
                )
            val iconSetConstructor =
                iconSetClass.getDeclaredConstructor(
                    Icon::class.java, // defaultIcon
                    Icon::class.java, // hoverIcon
                    Icon::class.java, // pressedIcon
                    Icon::class.java, // inactiveIcon
                )
            iconSetConstructor.isAccessible = true

            // Create our IconSet with traffic light icons
            val ourIconSet =
                iconSetConstructor.newInstance(
                    icons.active,
                    icons.hovered,
                    icons.pressed,
                    icons.inactive,
                )

            // Check if already patched (compare icon reference)
            val existingSet = iconsMap[true] ?: iconsMap[false]
            if (existingSet != null) {
                val getDefaultIcon = iconSetClass.getDeclaredMethod("getDefaultIcon")
                getDefaultIcon.isAccessible = true
                val currentDefault = getDefaultIcon.invoke(existingSet)
                if (currentDefault === icons.active) {
                    return // already patched
                }
            }

            // Replace both dark and light icon sets with our icons
            iconsMap[true] = ourIconSet
            iconsMap[false] = ourIconSet

            // Trigger updateStyle() to apply the new icons immediately
            val updateStyle = button.javaClass.getDeclaredMethod("updateStyle")
            updateStyle.isAccessible = true
            updateStyle.invoke(button)

            // Mark as patched for logging
            if (button.getClientProperty(PATCHED_KEY) != true) {
                button.putClientProperty(PATCHED_KEY, true)
                LOG.info("Traffic Light Buttons: patched '${button.toolTipText}' button icons via reflection")
            }
        } catch (e: Exception) {
            LOG.warn("Traffic Light Buttons: failed to patch button '${button.toolTipText}'", e)
        }
    }

    private fun findAllWindowButtons(
        container: Container,
        result: MutableList<JButton>,
    ) {
        for (component in container.components) {
            if (component is JButton) {
                val tooltip = component.toolTipText?.lowercase() ?: ""
                if (tooltip.contains("close") ||
                    tooltip.contains("minimize") ||
                    tooltip.contains("maximize") ||
                    tooltip.contains("restore")
                ) {
                    result.add(component)
                }
            }
            if (component is Container) {
                findAllWindowButtons(component, result)
            }
        }
    }

    private fun enforceButtonPosition(
        placement: String,
        buttonOrder: String,
    ) {
        val wantRight = placement == "RIGHT"
        val useMacOrder = buttonOrder == "MACOS"
        try {
            val stateClass =
                Class.forName(
                    "com.intellij.openapi.wm.impl.WindowButtonsConfiguration\$State",
                )
            val windowButtonClass =
                Class.forName(
                    "com.intellij.openapi.wm.impl.WindowButtonsConfiguration\$WindowButton",
                )

            for (window in Window.getWindows()) {
                if (window !is JFrame) continue
                val header = findComponentByClass(window, "ToolbarFrameHeader") ?: continue

                // Check current header position
                val contentStateField = header.javaClass.getDeclaredField("currentContentState")
                contentStateField.isAccessible = true
                val currentContentState = contentStateField.get(header)
                val needsHeaderUpdate =
                    currentContentState == null ||
                        stateClass.getField("rightPosition").getBoolean(currentContentState) != wantRight

                // Check current button order via getButtonPanes (protected method on FrameHeader superclass)
                val frameHeaderClass =
                    Class.forName(
                        "com.intellij.openapi.wm.impl.customFrameDecorations.header.FrameHeader",
                    )
                val getButtonPanes = frameHeaderClass.getDeclaredMethod("getButtonPanes")
                getButtonPanes.isAccessible = true
                val buttonPanesObj = getButtonPanes.invoke(header) ?: continue

                // Build desired state
                val desiredState = stateClass.getDeclaredConstructor().newInstance()
                stateClass.getField("rightPosition").setBoolean(desiredState, wantRight)
                val close = windowButtonClass.getField("CLOSE").get(null)
                val minimize = windowButtonClass.getField("MINIMIZE").get(null)
                val maximize = windowButtonClass.getField("MAXIMIZE").get(null)
                // LEFT: Close, Minimize, Maximize (always)
                // RIGHT + IDE Default: Minimize, Maximize, Close
                // RIGHT + macOS Style: Maximize, Minimize, Close
                val desiredOrder =
                    if (!wantRight) {
                        listOf(close, minimize, maximize)
                    } else if (useMacOrder) {
                        listOf(maximize, minimize, close)
                    } else {
                        listOf(minimize, maximize, close)
                    }
                stateClass.getField("buttons").set(desiredState, desiredOrder)

                // Check if button order needs fixing by looking at TitleButtonsPanel children
                val content = buttonPanesObj.javaClass.getMethod("getContent")
                content.isAccessible = true
                val titlePanel = content.invoke(buttonPanesObj) as? Container
                var needsOrderUpdate = false
                if (titlePanel != null && titlePanel.componentCount > 0) {
                    val firstButton = titlePanel.getComponent(0)
                    if (firstButton is JButton) {
                        val firstTooltip = firstButton.toolTipText?.lowercase() ?: ""
                        val expectedFirst =
                            if (!wantRight) {
                                "close"
                            } else if (useMacOrder) {
                                "maximize"
                            } else {
                                "minimize"
                            }
                        needsOrderUpdate = !firstTooltip.contains(expectedFirst)
                    }
                }

                if (needsHeaderUpdate) {
                    val fillContent = header.javaClass.getDeclaredMethod("fillContent", stateClass)
                    fillContent.isAccessible = true
                    fillContent.invoke(header, desiredState)
                    LOG.info("Traffic Light Buttons: header fillContent called, rightPosition=$wantRight")
                }

                if (needsOrderUpdate) {
                    try {
                        val bpFillContent = buttonPanesObj.javaClass.getDeclaredMethod("fillContent", stateClass)
                        bpFillContent.isAccessible = true
                        bpFillContent.invoke(buttonPanesObj, desiredState)
                        LOG.info("Traffic Light Buttons: button order set to [Close,Minimize,Maximize]")
                    } catch (e: Exception) {
                        LOG.warn("Traffic Light Buttons: failed to reorder buttons", e)
                    }
                }
            }
        } catch (e: Exception) {
            LOG.warn("Traffic Light Buttons: failed to enforce button position", e)
        }
    }

    private fun findComponentByClass(
        container: Container,
        className: String,
    ): Container? {
        for (component in container.components) {
            if (component.javaClass.simpleName == className) {
                return component as? Container
            }
            if (component is Container) {
                val found = findComponentByClass(component, className)
                if (found != null) return found
            }
        }
        return null
    }
}
