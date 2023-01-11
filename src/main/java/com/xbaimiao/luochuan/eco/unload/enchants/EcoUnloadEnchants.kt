package com.xbaimiao.luochuan.eco.unload.enchants

import com.xbaimiao.easylib.sendLang
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerInteractEvent
import top.mcplugin.lib.Plugin

@Suppress("unused")
class EcoUnloadEnchants : Plugin() {

    init {
        super.ignoreScan("shadow")
    }

    override fun enable() {
        saveDefaultConfig()
        Bukkit.getPluginManager().registerEvents(object : Listener {
            val blocks = config.getStringList("blocks").map { it.toLocation() }

            @EventHandler
            fun click(event: PlayerInteractEvent) {
                val block = event.clickedBlock ?: return
                if (blocks.any { it.blockZ == block.z && it.blockX == block.x && it.blockY == block.y }) {
                    event.isCancelled = true
                }
            }

        }, this)
    }

    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        if (args.isEmpty()) {
            val player = sender as? Player ?: return true
            GUI.open(player)
        } else {
            val player = Bukkit.getPlayerExact(args[0])
            if (player == null) {
                sender.sendLang("player-not-online", args[0])
                return true
            }
            GUI.open(player)
        }
        return true
    }

    private fun String.toLocation(): Location {
        val split = this.split("/")
        return Location(
            Bukkit.getWorld(split[0]),
            split[1].toDouble(),
            split[2].toDouble(),
            split[3].toDouble()
        )
    }

}