package tech.thatgravyboat.skyblockapi.impl.events

import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents
import net.fabricmc.fabric.api.client.screen.v1.ScreenKeyboardEvents
import net.fabricmc.fabric.api.client.screen.v1.ScreenMouseEvents
import tech.thatgravyboat.skyblockapi.api.SkyBlockAPI
import tech.thatgravyboat.skyblockapi.api.events.render.RenderScreenBackgroundEvent
import tech.thatgravyboat.skyblockapi.api.events.render.RenderScreenForegroundEvent
import tech.thatgravyboat.skyblockapi.api.events.screen.ScreenKeyPressedEvent
import tech.thatgravyboat.skyblockapi.api.events.screen.ScreenKeyReleasedEvent
import tech.thatgravyboat.skyblockapi.api.events.screen.ScreenMouseClickEvent
import tech.thatgravyboat.skyblockapi.api.events.screen.ScreenMouseReleasedEvent
import tech.thatgravyboat.skyblockapi.modules.Module

@Module
object ScreenEventHandler {

    init {
        ScreenEvents.BEFORE_INIT.register { _, screen, _, _ ->

            ScreenMouseEvents.beforeMouseClick(screen).register { _, mouseX, mouseY, button ->
                ScreenMouseClickEvent.Pre(screen, mouseX, mouseY, button).post(SkyBlockAPI.eventBus)
            }
            ScreenMouseEvents.afterMouseClick(screen).register { _, mouseX, mouseY, button ->
                ScreenMouseClickEvent.Post(screen, mouseX, mouseY, button).post(SkyBlockAPI.eventBus)
            }

            ScreenMouseEvents.beforeMouseRelease(screen).register { _, mouseX, mouseY, button ->
                ScreenMouseReleasedEvent.Pre(screen, mouseX, mouseY, button).post(SkyBlockAPI.eventBus)
            }
            ScreenMouseEvents.afterMouseRelease(screen).register { _, mouseX, mouseY, button ->
                ScreenMouseReleasedEvent.Post(screen, mouseX, mouseY, button).post(SkyBlockAPI.eventBus)
            }

            ScreenKeyboardEvents.beforeKeyPress(screen).register { _, keyCode, scanCode, modifiers ->
                ScreenKeyPressedEvent.Pre(screen, keyCode, scanCode, modifiers).post(SkyBlockAPI.eventBus)
            }
            ScreenKeyboardEvents.afterKeyPress(screen).register { _, keyCode, scanCode, modifiers ->
                ScreenKeyPressedEvent.Post(screen, keyCode, scanCode, modifiers).post(SkyBlockAPI.eventBus)
            }

            ScreenKeyboardEvents.beforeKeyRelease(screen).register { _, keyCode, scanCode, modifiers ->
                ScreenKeyReleasedEvent.Pre(screen, keyCode, scanCode, modifiers).post(SkyBlockAPI.eventBus)
            }
            ScreenKeyboardEvents.afterKeyRelease(screen).register { _, keyCode, scanCode, modifiers ->
                ScreenKeyReleasedEvent.Post(screen, keyCode, scanCode, modifiers).post(SkyBlockAPI.eventBus)
            }

            ScreenEvents.beforeRender(screen).register { _, graphics, _, _, _ ->
                RenderScreenBackgroundEvent(screen, graphics).post(SkyBlockAPI.eventBus)
            }
            ScreenEvents.afterRender(screen).register { _, graphics, _, _, _ ->
                RenderScreenForegroundEvent(screen, graphics).post(SkyBlockAPI.eventBus)
            }
        }
    }
}
