package xyz.theprogramsrc.uismodule.ui

import org.bukkit.Bukkit
import org.bukkit.inventory.Inventory
import xyz.theprogramsrc.uismodule.objects.UiEntry
import xyz.theprogramsrc.uismodule.objects.bukkitColor

class UiModel {

    /**
     * The title of the model
     */
    var title: String = "&9Unnamed Ui"

    /**
     * The size of the model
     */
    var size: Int = 54

    /**
     * The entries to be displayed in the model
     */
    val items: MutableMap<Int, UiEntry> = mutableMapOf()

    /**
     * Clears the entries in the model
     * @return This [UiModel]
     */
    fun clear() = this.apply {
        items.clear()
    }

    /**
     * Adds an [UiEntry] to the model
     * @param item The [UiEntry] to be added
     * @return This [UiModel]
     */
    fun add(item: UiEntry) = this.apply {
        IntRange(0, this.size.dec()).forEach {
            if(!items.containsKey(it)) {
                items[it] = item
                return@forEach
            }
        }
    }

    /**
     * Sets the [UiEntry] at the specified index
     * @param index The index of the [UiEntry]
     * @param item The [UiEntry] to be set
     * @return This [UiModel]
     */
    fun set(index: Int, item: UiEntry) = this.apply {
        items[index] = item
    }

    /**
     * Removes the [UiEntry] at the specified index
     * @param index The index of the [UiEntry]
     * @return This [UiModel]
     */
    fun remove(index: Int) = this.apply {
        items.remove(index)
    }

    /**
     * Fills the empty slots with the given entry
     * @param entry The [UiEntry] to be used to fill the empty slots
     * @return This [UiModel]
     */
    fun fillEmptySlots(entry: UiEntry) = this.apply {
        IntRange(0, this.size.dec()).forEach {
            if (!items.containsKey(it)) {
                items[it] = entry
            }
        }
    }

    /**
     * Fills the given slots with the given entry
     * @param slots The slots to be filled
     * @param entry The [UiEntry] to be used to fill the slots
     * @return This [UiModel]
     */
    fun fillSlots(slots: List<Int>, entry: UiEntry) = this.apply {
        slots.forEach {
            items[it] = entry
        }
    }

    /**
     * Generates an [Inventory] from this [UiModel]
     * @return The generated [Inventory]
     */
    fun generateInventory() = Bukkit.createInventory(null, this.size, this.title.bukkitColor()).apply {
        items.forEach {
            this.setItem(it.key, it.value.item)
        }
    }
}