package xyz.theprogramsrc.uismodule.objects

import org.bukkit.entity.Player
import org.bukkit.event.inventory.InventoryClickEvent

abstract class UiAction(val player: Player, val type: ClickType, val event: InventoryClickEvent) {

    /**
     * Opens the UI
     */
    abstract fun openUi()

    /**
     * Closes the UI
     */
    abstract fun closeUi()

}

enum class ClickType {

    LEFT_CLICK,
    SHIFT_LEFT_CLICK,
    MIDDLE_CLICK,
    RIGHT_CLICK,
    SHIFT_RIGHT_CLICK,
    Q,
    CTRL_Q,
    DOUBLE_CLICK,
    WINDOW_BORDER_LEFT,
    WINDOW_BORDER_RIGHT,

    ;

    companion object {

        /**
         * Generates the ClickType from the [InventoryClickEvent]
         * @param event The InventoryClickEvent
         * @return The [ClickType]
         */
        fun fromEvent(event: InventoryClickEvent) = when(event.click) {
            org.bukkit.event.inventory.ClickType.SHIFT_LEFT -> SHIFT_LEFT_CLICK
            org.bukkit.event.inventory.ClickType.MIDDLE -> MIDDLE_CLICK
            org.bukkit.event.inventory.ClickType.RIGHT -> RIGHT_CLICK
            org.bukkit.event.inventory.ClickType.SHIFT_RIGHT -> SHIFT_RIGHT_CLICK
            org.bukkit.event.inventory.ClickType.DROP -> Q
            org.bukkit.event.inventory.ClickType.CONTROL_DROP -> CTRL_Q
            org.bukkit.event.inventory.ClickType.DOUBLE_CLICK -> DOUBLE_CLICK
            org.bukkit.event.inventory.ClickType.WINDOW_BORDER_LEFT -> WINDOW_BORDER_LEFT
            org.bukkit.event.inventory.ClickType.WINDOW_BORDER_RIGHT -> WINDOW_BORDER_RIGHT
            else -> LEFT_CLICK
        }
    }
}