package xyz.theprogramsrc.uismodule.objects

import com.cryptomorin.xseries.XMaterial
import org.bukkit.ChatColor
import org.bukkit.Color
import org.bukkit.Material
import org.bukkit.enchantments.Enchantment
import org.bukkit.inventory.ItemFlag
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.Damageable
import org.bukkit.inventory.meta.LeatherArmorMeta

/**
 * Turns this string into a bukkit colored string
 * @return the colored string
 */
fun String.bukkitColor(): String = ChatColor.translateAlternateColorCodes('&', this)

/**
 * Turns this string into a bukkit colored string
 * @param args the arguments to be inserted into the string
 * @return the colored string
 */
fun String.bukkitColor(vararg args: Any): String = ChatColor.translateAlternateColorCodes('&', this.format(*args))

/**
 * Strips the colors from this string
 * @return the string without colors
 */
fun String.bukkitStripColor(): String = ChatColor.stripColor(this) ?: this

/**
 * Gets the name of this [ItemStack]
 * @return the name of the item
 */
val ItemStack.name: String
    get() = this.itemMeta?.displayName ?: this.type.name

/**
 * Sets the name of this [ItemStack]
 * @param name the name of the item
 * @return this [ItemStack]
 */
fun ItemStack.setName(name: String): ItemStack = this.apply {
    this.itemMeta = this.itemMeta?.apply {
        setDisplayName(name.bukkitColor())
    }
}

/**
 * Gets the lore of this [ItemStack]
 * @return the lore of the item
 */
val ItemStack.lore: List<String>
    get() = this.itemMeta?.lore ?: emptyList()

/**
 * Sets the lore of this [ItemStack]
 * @param lore the lore of the item
 * @param append if the lore should be appended or replaced
 * @return this [ItemStack]
 */
fun ItemStack.lore(lore: List<String>, append: Boolean = false): ItemStack = this.apply {
    this.itemMeta = this.itemMeta?.apply {
        this.lore = if(append){
            this.lore?.plus(lore.map { it.bukkitColor() })
        } else {
            lore.map { it.bukkitColor() }
        }
    }
}

/**
 * Sets the lore of this [ItemStack]
 * @param lore the lore of the item
 * @param append if the lore should be appended or replaced
 * @return this [ItemStack]
 */
fun ItemStack.lore(vararg lore: String, append: Boolean = false): ItemStack = this.lore(lore.toList(), append)

/**
 * Adds a line to the lore of this [ItemStack]
 * @param line the lore of the item
 * @return this [ItemStack]
 */
fun ItemStack.loreLine(line: String): ItemStack = this.apply {
    this.itemMeta = this.itemMeta?.apply {
        lore = this.lore?.plus(line.bukkitColor())
    }
}

/**
 * Adds the given lines to the lore of this [ItemStack]
 * @param lines the lore of the item
 * @return this [ItemStack]
 */
fun ItemStack.loreLines(vararg lines: String): ItemStack = this.apply {
    lines.forEach(this::loreLine)
}

/**
 * Sets the amount of this [ItemStack]
 * @param amount the amount of the item
 * @return this [ItemStack]
 */
fun ItemStack.amount(amount: Int): ItemStack = this.apply {
    this.amount = amount
}

/**
 * Gets the flags of this [ItemStack]
 * @return the flags of the item
 */
val ItemStack.flags: List<ItemFlag>
    get() = this.itemMeta?.itemFlags?.toList() ?: emptyList()

/**
 * Toggles the given flags on this [ItemStack]. If the flag is already set, it will be removed.
 * @param flags the flags to toggle
 * @return this [ItemStack]
 */
fun ItemStack.toggleFlags(vararg flags: ItemFlag): ItemStack = this.apply {
    this.itemMeta = this.itemMeta?.apply {
        flags.forEach {
            if(this.hasItemFlag(it)){
                this.removeItemFlags(it)
            } else {
                this.addItemFlags(it)
            }
        }
    }
}

/**
 * Adds the given flags to this [ItemStack]
 * @param flags the flags to add
 * @return this [ItemStack]
 */
fun ItemStack.addFlags(vararg flags: ItemFlag): ItemStack = this.apply {
    this.itemMeta = this.itemMeta?.apply {
        addItemFlags(*flags)
    }
}

/**
 * Removes the given flags from this [ItemStack]
 * @param flags the flags to remove
 * @return this [ItemStack]
 */
fun ItemStack.removeFlags(vararg flags: ItemFlag): ItemStack = this.apply {
    this.itemMeta = this.itemMeta?.apply {
        removeItemFlags(*flags)
    }
}

/**
 * Gets the enchantments of this [ItemStack]
 * @return the enchantments of the item
 */
val ItemStack.enchantments: Map<Enchantment, Int>
    get() = this.itemMeta?.enchants ?: mapOf()

/**
 * Toggles the given enchantments on this [ItemStack]. If the enchantment is already set, it will be removed.
 * @param enchantments the enchantments to toggle
 * @return this [ItemStack]
 */
fun ItemStack.toggleEnchantments(vararg enchantments: SimpleEnchantment): ItemStack = this.apply {
    this.itemMeta = this.itemMeta?.apply {
        enchantments.forEach {
            if (this.hasEnchant(it.enchantment)) {
                this.removeEnchant(it.enchantment)
            } else {
                this.addEnchant(it.enchantment, it.level, true)
            }
        }
    }
}

/**
 * Adds the given enchantments to this [ItemStack]
 * @param enchantments the enchantments to add
 * @return this [ItemStack]
 */
fun ItemStack.addEnchantments(vararg enchantments: SimpleEnchantment): ItemStack = this.apply {
    this.itemMeta = this.itemMeta?.apply {
        enchantments.forEach {
            this.addEnchant(it.enchantment, it.level, true)
        }
    }
}

/**
 * Adds the given enchantments to this [ItemStack] with default level 1
 * @param enchantments the enchantments to add
 * @return this [ItemStack]
 */
fun ItemStack.addEnchantments(vararg enchantments: Enchantment): ItemStack = this.apply {
    this.addEnchantments(*enchantments.map { SimpleEnchantment(it) }.toTypedArray())
}

/**
 * Removes the given enchantments from this [ItemStack]
 * @param enchantments the enchantments to remove
 * @return this [ItemStack]
 */
fun ItemStack.removeEnchantments(vararg enchantments: SimpleEnchantment): ItemStack = this.apply {
    this.itemMeta = this.itemMeta?.apply {
        enchantments.forEach {
            this.removeEnchant(it.enchantment)
        }
    }
}

/**
 * Removes the given enchantments from this [ItemStack]
 * @param enchantments the enchantments to remove
 * @return this [ItemStack]
 */
fun ItemStack.removeEnchantments(vararg enchantments: Enchantment): ItemStack = this.apply {
    this.removeEnchantments(*enchantments.map { SimpleEnchantment(it) }.toTypedArray())
}

/**
 * Sets the glowing effect on this [ItemStack]. (If true the enchantments will be hidden)
 * @param glowing if the item should glow. (Defaults to true)
 * @return this [ItemStack]
 */
fun ItemStack.setGlowing(glowing: Boolean = true): ItemStack = this.apply {
    if(glowing) {
        this.addFlags(ItemFlag.HIDE_ENCHANTS)
        this.addEnchantments(SimpleEnchantment(Enchantment.LUCK, 1))
    } else {
        this.removeFlags(ItemFlag.HIDE_ENCHANTS)
        this.removeEnchantments(SimpleEnchantment(Enchantment.LUCK))
    }
}

/**
 * Sets the damage to this [ItemStack]
 * @param damage the damage to the item
 * @return this [ItemStack]
 */
fun ItemStack.damage(damage: Int): ItemStack = this.apply {
    val meta = this.itemMeta ?: return this
    if(meta is Damageable) {
        meta.damage = damage
        this.itemMeta = meta
    }
}

/**
 * Sets the color if the item is leather armor
 * @param color the color of the item
 *
 */
fun ItemStack.color(color: Color): ItemStack = this.apply {
    val meta = this.itemMeta ?: return this
    if ((type == Material.LEATHER_BOOTS || type == Material.LEATHER_CHESTPLATE || type == Material.LEATHER_HELMET || type == Material.LEATHER_LEGGINGS) && meta is LeatherArmorMeta) {
        meta.setColor(color)
        this.itemMeta = meta
    } else {
        throw IllegalArgumentException("Colors only applicable for leather armor!")
    }
}

fun XMaterial.itemStack(): ItemStack = this.let {
    if(this == XMaterial.AIR) {
        throw IllegalArgumentException("Cannot create an itemstack of air!")
    }
    val material = this.parseMaterial() ?: throw IllegalArgumentException("${this.name} is an invalid material!")
    ItemStack(material)
}

/**
 * Represents an enchantment
 * @param enchantment the enchantment
 * @param level the level of the enchantment
 */
data class SimpleEnchantment(val enchantment: Enchantment, val level: Int = 1)




