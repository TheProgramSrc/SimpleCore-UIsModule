package xyz.theprogramsrc.uismodule

import com.cryptomorin.xseries.XMaterial
import org.bukkit.Bukkit
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.AsyncPlayerChatEvent
import xyz.theprogramsrc.simplecoreapi.global.module.Module
import xyz.theprogramsrc.simplecoreapi.spigot.SpigotLoader
import xyz.theprogramsrc.tasksmodule.spigot.SpigotTasks
import xyz.theprogramsrc.uismodule.objects.*
import xyz.theprogramsrc.uismodule.simple.SimpleUi
import xyz.theprogramsrc.uismodule.ui.Ui

class Main: Module(), Listener {

    // TODO: Test this :p

    override fun onEnable() {
        Bukkit.getPluginManager().registerEvents(this, SpigotLoader.instance)
    }

    private val item = XMaterial.DIAMOND_SWORD.itemStack()
        .setName("&bDiamond Sword")
        .lore(
            "&7",
            "&7&l&m-------------------",
            "&7&l&m&oDescription",
            "&7&l&m-------------------",
            "&7&l&m&oThis is a test",
            "&7&l&m&oof the Ui",
            "&7"
        )
        .setGlowing()

    @EventHandler fun onCmd(event: AsyncPlayerChatEvent){
        SpigotTasks.instance.run {
            if(event.message.lowercase().contains("simpleui")){
                SimpleUi.Builder()
                    .title("&a&lWelcome to the server! (MC)")
                    .slots(9)
                    .entries(mapOf(
                        4 to UiEntry(item = item){
                            it.player.inventory.addItem(item)
                            it.closeUi()
                        }
                    ))
                    .closeAfterClick(false)
                    .build(event.player)
            } else if (event.message.lowercase().contains("ui")){
                Ui("&a&lWelcome to the server! (MC)", 9, event.player).apply {
                    onBuild = {
                        set(4, UiEntry(item = item){
                            it.player.inventory.addItem(item)
                            it.closeUi()
                        })
                    }
                }
            } else {

            }
        }
    }
}