package xyz.theprogramsrc.uismodule.simple

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
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.PlayerInventory
import xyz.theprogramsrc.simplecoreapi.spigot.SpigotLoader
import xyz.theprogramsrc.uismodule.objects.ClickType
import xyz.theprogramsrc.uismodule.objects.UiAction
import xyz.theprogramsrc.uismodule.objects.UiEntry
import xyz.theprogramsrc.uismodule.objects.bukkitColor

/**
 * Represents a UI
 * @param title The title of the UI
 * @param slots The slots of the UI
 * @param entries The entries of the UI
 * @param player The player that will see the UI
 */
open class SimpleUi(private val title: String, private val slots: Int, private val entries: Map<Int, UiEntry>, private val player: Player): Listener {

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

    private val actions = mutableMapOf<Int, (UiAction) -> Unit>()

    /**
     * Opens the UI for the player
     * @return This [SimpleUi]
     */
    open fun open(): SimpleUi = this.apply {
        if(player.openInventory == this.inventory) return@apply
        if(inventory == null){
            HandlerList.unregisterAll(this)
            Bukkit.getPluginManager().registerEvents(this, SpigotLoader.instance)
            this.inventory = Bukkit.createInventory(null, slots, title.bukkitColor())
            entries.forEach {
                inventory?.setItem(it.key, it.value.item)
            }
        }

        player.openInventory(inventory ?: throw IllegalStateException("An unknown error occurred while creating the inventory"))
    }

    /**
     * Closes the UI for the player
     * @return This [SimpleUi]
     */
    open fun close(): SimpleUi = this.apply {
        HandlerList.unregisterAll(this)
        this.player.closeInventory()
        inventory = null
    }

    @EventHandler
    fun onClick(event: InventoryClickEvent){
        if(this.inventory == null || event.whoClicked != this.player) return
        event.isCancelled = true
        if(event.slotType == InventoryType.SlotType.OUTSIDE) return
        if(event.clickedInventory !is PlayerInventory && this.actions.containsKey(event.slot)){
            val action = object:UiAction(this.player, ClickType.fromEvent(event), event) {

                override fun openUi() {
                    this@SimpleUi.open()
                }

                override fun closeUi() {
                    this@SimpleUi.close()
                }
            }
            this.actions[event.slot]?.invoke(action)
            if(this.closeAfterClick) this.close()
        }
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
            HandlerList.unregisterAll(this)
            this.inventory = null
        }
    }

    /**
     * Represents the UiBuilder
     */
    class Builder {

        private var title = "&cUnnamed Ui"
        private var slots = 27
        private var entries = mutableMapOf<Int, UiEntry>()
        private var closeAfterClickBuilder = false

        /**
         * Creates a new Ui Builder
         */
        constructor()

        /**
         * Creates a new Ui Builder
         * @param title The title of the UI
         */
        constructor(title: String) {
            this.title = title
        }

        /**
         * Creates a new Ui Builder
         * @param title The title of the UI
         * @param slots The slots of the UI
         */
        constructor(title: String, slots: Int) {
            this.title = title
            this.slots = slots
        }

        /**
         * Sets the title of the UI
         * @param title The title of the UI
         * @return This [Builder]
         */
        fun title(title: String) = this.apply {
            this.title = title
        }

        /**
         * Sets the slots of the UI
         * @param slots The slots of the UI
         * @return This [Builder]
         */
        fun slots(slots: Int) = this.apply {
            this.slots = slots
        }

        /**
         * Adds an entry to the UI
         * @param entry The entry to add
         * @return This [Builder]
         */
        fun entry(entry: UiEntry) = this.apply {
            IntRange(0, this.slots-1).forEach {
                if(!this.entries.containsKey(it)){
                    this.entries[it] = entry
                    return@apply
                }
            }
        }

        /**
         * Adds an entry to the UI
         * @param slot The slot of the entry
         * @param item The item of the entry
         * @param action The action of the entry
         * @return This [Builder]
         */
        fun entry(slot: Int, item: ItemStack, action: (UiAction) -> Unit) = this.apply {
            entries[slot] = UiEntry(item, action)
        }

        /**
         * Adds an entry to the UI with no action
         * @param slot The slot of the entry
         * @param item The item of the entry
         * @return This [Builder]
         */
        fun entry(slot: Int, item: ItemStack) = this.entry(slot, item, {})

        /**
         * Adds the given entries to the UI
         * @param entries The entries to add
         * @return This [Builder]
         */
        fun entries(entries: List<UiEntry>) = this.apply {
            entries.forEach(this::entry)
        }

        /**
         * Adds the given entries to the UI
         * @param entries The entries to add
         * @return This [Builder]
         */
        fun entries(vararg entries: UiEntry) = this.entries(entries.toList())

        /**
         * Adds the given entries to the UI
         * @param entries The entries to add
         * @param override Whether to override the existing entries or not. Defaults to true
         * @return This [Builder]
         */
        fun entries(entries: Map<Int, UiEntry>, override: Boolean = true) = this.apply {
            if (override) {
                this.entries.putAll(entries)
            } else {
                entries.forEach {
                    if(!this.entries.containsKey(it.key)) {
                        this.entries[it.key] = it.value
                    }
                }
            }
        }

        /**
         * Should the Ui close after an entry is clicked?
         * @param closeAfterClick Should the Ui close after an entry is clicked? (Defaults to true)
         * @return This [Builder]
         */
        fun closeAfterClick(closeAfterClick: Boolean = true) = this.apply {
            this.closeAfterClickBuilder = closeAfterClick
        }

        /**
         * Builds the UI
         * @param player The player who will see the UI
         * @param openUi True to open the UI, false to close it
         * @return The built [SimpleUi]
         */
        fun build(player: Player, openUi: Boolean = true) = SimpleUi(title, slots, entries, player).apply {
            this.closeAfterClick = closeAfterClickBuilder
        }.let {
            if(openUi) it.open()
            it
        }
    }
}