package com.xbaimiao.luochuan.eco.unload.enchants

import com.xbaimiao.easylib.sendLang
import org.bukkit.Bukkit
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import top.mcplugin.lib.Plugin

@Suppress("unused")
class EcoUnloadEnchants : Plugin() {

    init {
        super.ignoreScan("shadow")
    }

    override fun enable() {
        saveDefaultConfig()
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

}