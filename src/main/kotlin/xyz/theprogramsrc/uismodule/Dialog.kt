package xyz.theprogramsrc.uismodule

import com.cryptomorin.xseries.messages.ActionBar
import com.cryptomorin.xseries.messages.Titles
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.HandlerList
import org.bukkit.event.Listener
import org.bukkit.event.block.Action
import org.bukkit.event.player.AsyncPlayerChatEvent
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.event.player.PlayerMoveEvent
import xyz.theprogramsrc.simplecoreapi.spigot.SpigotLoader
import xyz.theprogramsrc.tasksmodule.objects.RecurringTask
import xyz.theprogramsrc.tasksmodule.spigot.SpigotTasks
import xyz.theprogramsrc.uismodule.objects.bukkitColor
import xyz.theprogramsrc.uismodule.objects.bukkitStripColor

/**
 * Representation of a dialog
 * @param player The player that will see the dialog
 * @param title The title of the dialog. If null it will be empty. (Defaults to null)
 * @param subtitle The subtitle of the dialog. If null it will be empty. (Defaults to null)
 * @param actionbar The actionbar of the dialog. If null it will be empty. (Defaults to null)
 * @param onClose The function that will be called when the player closes the dialog. (Defaults to empty function)
 */
class Dialog(
    val player: Player,
    val title: String? = null,
    val subtitle: String? = null,
    val actionbar: String? = null,
    val onClose: (Player) -> Unit = {},
    val onChat: (Player, String) -> Boolean = { _: Player, _: String -> true },
): Listener{

    private var task: RecurringTask? = null
    private var lastMovementAt = 0L

    /**
     * Should the player be able to close this dialog?
     */
    var canBeClosed = true

    /**
     * Was this dialog closed manually or programmatically?
     */
    var manuallyClosed = false
        private set

    private fun send() {
        if(this.player.isOnline) { // Check if the player is online
            if(this.title != null || this.subtitle != null) {
                Titles.sendTitle(
                    this.player,
                    0, // Fade in
                    20, // Stay
                    0, // Fade out
                    (this.title ?: "&7").bukkitColor(),
                    (this.subtitle ?: "&7").bukkitColor()
                )
            } else {
                Titles.clearTitle(this.player)
            }

            val now = System.currentTimeMillis() // Get the current time
            val calc = now - lastMovementAt // Calculate the difference between the last movement and now. (If the player hasn't moved the calc will be equals to now)
            // Show the close actionbar translation if the player has moved in the last 5 seconds, otherwise show the actionbar
            val actionbar = if(calc < 5000L && calc != now && this.canBeClosed) {
                Main.HOW_TO_CLOSE_DIALOG_TRANSLATION.translate()
            } else {
                this.actionbar
            }

            if(actionbar != null){
                ActionBar.sendActionBar(this.player, actionbar.bukkitColor())
            }else{
                ActionBar.clearActionBar(this.player)
            }
        } else {
            this.task?.stop()
        }
    }

    fun open(): Dialog = this.apply {
        if(this.task == null){
            this.task = SpigotTasks.instance.runTaskTimerAsynchronously(delay = 1L, period = 5L, task = this::send)
        }
        this.task?.stop() // Stop the previous task if there is any
        HandlerList.unregisterAll(this) // Unregister all the listeners
        SpigotTasks.instance.runTask(this.player::closeInventory) // Close the inventory of the player
        Bukkit.getPluginManager().registerEvents(this, SpigotLoader.instance) // Register the listeners
        this.task?.start() // Start the task
        this.manuallyClosed = false // Reset the manually closed flag
    }

    fun close(sendMessage: Boolean = true): Dialog = this.apply {
        this.task?.stop() // Stop the task
        HandlerList.unregisterAll(this) // Unregister all the listeners
        this.manuallyClosed = true // Set the manually closed flag
        Titles.clearTitle(this.player) // Clear the title
        ActionBar.clearActionBar(this.player) // Clear the actionbar
        if(sendMessage) { // Send the message if the flag is true
            this.player.sendMessage(Main.DIALOG_CLOSED_TRANSLATION.translate().bukkitColor())
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    fun onInteract(e: PlayerInteractEvent) {
        if(this.player == e.player && this.canBeClosed) {
            if(e.action == Action.LEFT_CLICK_AIR || e.action == Action.LEFT_CLICK_BLOCK) {
                this.close()
            }
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    fun onMessageReceived(event: AsyncPlayerChatEvent) {
        if(this.player == event.player) {
            event.isCancelled = true
            val msg = event.message.bukkitStripColor()
            SpigotTasks.instance.runTask {
                if(this.onChat(this.player, msg)) {
                    this.manuallyClosed = false
                    this.close()
                }
            }
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.LOWEST)
    fun onMove(e: PlayerMoveEvent) {
        if(this.player == e.player){ // Check if the player is the same
            this.lastMovementAt = System.currentTimeMillis() // Update the last movement time
        }
    }


}