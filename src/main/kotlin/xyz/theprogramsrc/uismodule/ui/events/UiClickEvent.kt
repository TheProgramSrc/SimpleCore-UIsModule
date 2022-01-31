package xyz.theprogramsrc.uismodule.ui.events

import org.bukkit.entity.Player
import xyz.theprogramsrc.uismodule.objects.UiEntry
import xyz.theprogramsrc.uismodule.ui.Ui

class UiClickEvent(ui: Ui, player: Player, val entry: UiEntry) : UiEvent(ui, player) {
}