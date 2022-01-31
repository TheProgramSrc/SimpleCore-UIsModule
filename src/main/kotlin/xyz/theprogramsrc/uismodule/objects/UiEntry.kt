package xyz.theprogramsrc.uismodule.objects

import com.cryptomorin.xseries.XMaterial
import org.bukkit.inventory.ItemStack

data class UiEntry(val item: ItemStack, val action: (action: UiAction) -> Unit = {}) {

    /**
     * Should this entry be updated every tick?
     * If true it'll be updated every tick, if false it'll be updated only when the player opens the inventory
     */
    var shouldUpdate = false

    /**
     * Inline function to edit the item
     * @param builder the function to edit the item
     * @return This [UiEntry]
     */
    fun itemEditor(builder: ItemStack.() -> Unit): UiEntry {
        val item = item.clone()
        item.builder()
        return UiEntry(item, action)
    }

    companion object {

        /**
         * Creates a [UiEntry] with the given [XMaterial]
         * @param material The [XMaterial] to create the [UiEntry] with
         * @param action The action to perform when the [UiEntry] is clicked. Defaults to empty function
         * @return The created [UiEntry]
         */
        fun create(material: XMaterial, action: (action: UiAction) -> Unit = {}) = UiEntry(material.itemStack(), action)

        /**
         * Creates a [UiEntry] with the given [XMaterial] and the given name
         * @param material The [XMaterial] to create the [UiEntry] with
         * @param name The name of the [UiEntry]
         * @param action The action to perform when the [UiEntry] is clicked. Defaults to empty function
         * @return The created [UiEntry]
         */
        fun create(material: XMaterial, name: String, action: (action: UiAction) -> Unit = {}) = UiEntry(material.itemStack().setName(name), action)

    }
}