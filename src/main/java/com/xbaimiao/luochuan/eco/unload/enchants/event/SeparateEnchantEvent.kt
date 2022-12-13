package com.xbaimiao.luochuan.eco.unload.enchants.event

import org.bukkit.entity.Player
import org.bukkit.event.HandlerList
import org.bukkit.event.player.PlayerEvent
import org.bukkit.inventory.ItemStack

class SeparateEnchantEvent(
    player: Player,
    var itemStack: ItemStack
) : PlayerEvent(player) {

    override fun getHandlers(): HandlerList {
        return handlerList
    }

    companion object {
        @JvmField
        val handlerList = HandlerList()

        @JvmStatic
        fun getHandlerList(): HandlerList {
            return handlerList
        }
    }

}