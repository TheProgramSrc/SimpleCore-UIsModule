package xyz.theprogramsrc.uismodule.ui.events

import org.bukkit.entity.Player
import org.bukkit.event.Event
import org.bukkit.event.HandlerList
import xyz.theprogramsrc.uismodule.ui.Ui

open class UiEvent(val ui: Ui, val player: Player): Event() {

    companion object {
        private val handlerList = HandlerList()

        @JvmStatic
        fun getHandlerList(): HandlerList {
            return handlerList
        }
    }


    override fun getHandlers(): HandlerList = handlerList
}