package xyz.theprogramsrc.uismodule.ui

import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.HandlerList
import org.bukkit.event.Listener
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryCloseEvent
import org.bukkit.event.inventory.InventoryDragEvent
import org.bukkit.event.inventory.InventoryType
import org.bukkit.event.player.PlayerQuitEvent
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.PlayerInventory
import xyz.theprogramsrc.simplecoreapi.spigot.SpigotLoader
import xyz.theprogramsrc.tasksmodule.objects.RecurringTask
import xyz.theprogramsrc.tasksmodule.spigot.SpigotTasks
import xyz.theprogramsrc.uismodule.objects.*
import xyz.theprogramsrc.uismodule.ui.events.UiClickEvent
import xyz.theprogramsrc.uismodule.ui.events.UiCloseEvent
import xyz.theprogramsrc.uismodule.ui.events.UiOpenEvent

/**
 * Represents a UI
 * @param title The title of the UI
 * @param slots The slots of the UI
 * @param player The player that will see the UI
 * @param automaticallyOpen If the UI should be automatically opened. Defaults to true
 */
open class Ui(private val title: String, private val slots: Int, private val player: Player, private val automaticallyOpen: Boolean = true): Listener {

    /**
     * The inventory of the UI
     */
    var inventory: Inventory? = null
        private set

    /**
     * Should the Ui be closed after
     * the user clicks on an entry?
     * Defaults to true
     */
    var closeAfterClick = true

    /**
     * Should the Ui be able to be closed?
     * Defaults to true
     */
    var canCloseUi = true

    /**
     * Executed to build the UI
     */
    var onBuild: UiModel.() -> Unit = {}

    private val entries = mutableMapOf<Int, UiEntry>()
    private var preventOpening = false

    private lateinit var task: RecurringTask

    init {
        task = SpigotTasks.instance.runTaskTimer(0L, 1L){
            if(!(this.inventory == null || this.player.openInventory.title.bukkitStripColor() != this.title.bukkitStripColor() || this.inventory?.size != this.slots)) {
                if(preventOpening) {
                    task.stop()
                    HandlerList.unregisterAll(this)
                    return@runTaskTimer
                }

                val model = UiModel().apply {
                    title = this@Ui.title
                    size = this@Ui.slots
                }
                this.onBuild(model)
                val entries = model.items
                IntRange(0, this.slots).forEach { slot ->
                    val entry = entries[slot]
                    if(entry == null) {
                        this.inventory?.setItem(slot, null)
                        this.entries.remove(slot)
                    } else {
                        this.inventory?.setItem(slot, entry.item)
                        this.entries[slot] = entry
                    }
                }
                this.player.updateInventory()
            }
        }

        if(automaticallyOpen){
            this.open()
        }
    }

    /**
     * Opens the UI for the player
     * @return This [Ui]
     */
    open fun open(): Ui = this.apply {
        if(player.openInventory == this.inventory) return@apply
        this.preventOpening = false
        if(inventory == null){
            HandlerList.unregisterAll(this)
            Bukkit.getPluginManager().registerEvents(this, SpigotLoader.instance)
            this.task.start()
        }

        this.inventory = Bukkit.createInventory(null, slots, title.bukkitColor())
        SpigotTasks.instance.runTask {
            Bukkit.getPluginManager().callEvent(UiOpenEvent(this, this.player))
            player.openInventory(inventory ?: throw IllegalStateException("An unknown error occurred while creating the inventory"))
        }
    }

    /**
     * Closes the UI for the player
     * @return This [Ui]
     */
    open fun close(): Ui = this.apply {
        this.preventOpening = true
        SpigotTasks.instance.runTask {
            Bukkit.getPluginManager().callEvent(UiCloseEvent(this, this.player))
            player.closeInventory()
        }
    }

    @EventHandler
    fun onClick(event: InventoryClickEvent){
        if(this.inventory == null || event.whoClicked != this.player) return
        event.isCancelled = true
        if(event.slotType == InventoryType.SlotType.OUTSIDE || event.clickedInventory is PlayerInventory) return
        val entry = this.entries[event.slot] ?: return
        entry.action.invoke(object:UiAction(this.player, ClickType.fromEvent(event), event) {
            override fun openUi() {
                this@Ui.open()
            }

            override fun closeUi() {
                this@Ui.close()
            }
        })
        if(this.closeAfterClick) this.close()
        Bukkit.getPluginManager().callEvent(UiClickEvent(this, this.player, entry))
    }

    @EventHandler
    fun onDrag(event: InventoryDragEvent) {
        if(this.inventory == null || event.whoClicked != this.player || event.inventory is PlayerInventory) return
        if(event.inventorySlots.size > 1) event.isCancelled = true
    }

    @EventHandler(priority = EventPriority.MONITOR)
    fun onQuit(event: PlayerQuitEvent){
        if(this.player == event.player) this.close()
    }

    @EventHandler
    fun onClose(event: InventoryCloseEvent){
        if(event.inventory == this.inventory && event.player == this.player) {
            this.inventory = null
            this.task.stop()
            HandlerList.unregisterAll(this)
            if(!this.canCloseUi && !this.preventOpening) {
                this.open()
            }
        }
    }
}