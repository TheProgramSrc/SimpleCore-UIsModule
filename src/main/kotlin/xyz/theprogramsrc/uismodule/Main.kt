package xyz.theprogramsrc.uismodule

import org.bukkit.event.Listener
import xyz.theprogramsrc.simplecoreapi.global.module.Module
import xyz.theprogramsrc.translationsmodule.Translation

class Main: Module(), Listener {

    companion object {
        val HOW_TO_CLOSE_DIALOG_TRANSLATION = Translation(
            id = "Dialog.HowToClose",
            defaultValue = "Use **Left Click** to **close** this Dialog.",
            colors = arrayOf("&b","&c"),
            mainColor = "&7",
        )
        val DIALOG_CLOSED_TRANSLATION = Translation(
            id = "Dialog.Closed",
            defaultValue = "You've **closed** this Dialog.",
            colors = arrayOf("&c"),
            mainColor = "&7"
        )
    }
}