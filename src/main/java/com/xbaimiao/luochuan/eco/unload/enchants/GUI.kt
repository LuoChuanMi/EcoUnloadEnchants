package com.xbaimiao.luochuan.eco.unload.enchants

import com.willfp.ecoenchants.enchants.EcoEnchants
import com.xbaimiao.easylib.giveItem
import com.xbaimiao.easylib.sendLang
import com.xbaimiao.luochuan.eco.unload.enchants.event.SeparateEnchantEvent
import com.xbaimiao.luochuan.eco.unload.enchants.event.UnloadEnchantEvent
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.enchantments.Enchantment
import org.bukkit.entity.Player
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.EnchantmentStorageMeta
import top.mcplugin.lib.module.PlayerPoints.HookPlayerPoints
import top.mcplugin.lib.module.item.ItemUtil
import top.mcplugin.lib.module.lang.Lang
import top.mcplugin.lib.module.ui.type.Basic
import top.mcplugin.lib.module.vault.HookVault

object GUI {

    private val enchantSlots = arrayOf(3, 4, 5, 6, 7, 8, 12, 13, 14, 15, 16, 17, 21, 22, 23, 24, 25, 26)
    private const val itemSlot = 10

    private fun Map<Enchantment, Int>.filter(): Map<Enchantment, Int> {
        return this.filter { EcoEnchants.getByKey(it.key.key) != null }
    }

    fun open(player: Player) {
        val item = player.inventory.itemInMainHand
        if (ItemUtil.isAir(item)) {
            player.sendLang("item-null")
            return
        }
        if (item.enchantments.filter().isEmpty()) {
            player.sendLang("item-no-enchant")
            return
        }

        val basic = Basic(ConfigManager.title)
        basic.rows = 3
        basic.set(itemSlot, item)

        val enchantBooks = ArrayList<ItemStack>()

        for (key in item.enchantments.filter()) {

            val book = ItemStack(Material.ENCHANTED_BOOK)
            val storageMeta = book.itemMeta as EnchantmentStorageMeta? ?: return
            storageMeta.addStoredEnchant(key.key, key.value, true)
            book.itemMeta = storageMeta

            enchantBooks.add(book)
        }

        val mapping = HashMap<Int, ItemStack>()

        for (slot in enchantBooks.withIndex()) {
            mapping[enchantSlots[slot.index]] = slot.value.clone()
            val displayItem = slot.value
            val meta = displayItem.itemMeta!!
            val lore = meta.lore ?: arrayListOf()
            lore.add(" ")
            lore.add(Lang.asLang("gui-right"))
            lore.add(Lang.asLang("gui-left"))
            meta.lore = lore
            displayItem.itemMeta = meta
            basic.set(enchantSlots[slot.index], displayItem)
        }

        basic.onClick {
            it.isCancelled = true

            if (it.bukkitEvent !is InventoryClickEvent) {
                return@onClick
            }

            val book = mapping[it.rawSlot] ?: return@onClick

            if (!checkMoney(it.clickEvent, player)) {
                return@onClick
            }

            val storageMeta = book.itemMeta as EnchantmentStorageMeta? ?: return@onClick
            val enchant = storageMeta.storedEnchants.entries.first()

            val meta = item.itemMeta!!
            meta.removeEnchant(enchant.key)
            item.itemMeta = meta
            player.inventory.setItemInMainHand(item)

            if (it.clickEvent.isRightClick) {
                val event = SeparateEnchantEvent(player, item)
                Bukkit.getPluginManager().callEvent(event)
                player.giveItem(book)
                player.sendLang("separate-success")
            } else if (it.clickEvent.isLeftClick) {
                val event = UnloadEnchantEvent(player, item)
                Bukkit.getPluginManager().callEvent(event)
                player.sendLang("unload-success")
            }

            if (item.enchantments.filter().isEmpty()) {
                player.closeInventory()
            } else {
                open(player)
            }
        }

        player.openInventory(basic.build())
    }

    private fun checkMoney(event: InventoryClickEvent, player: Player): Boolean {
        if (event.isRightClick) {
            when (ConfigManager.separateMoney.first) {
                MoneyType.VAULT -> {
                    if (!HookVault.hasMoney(player, ConfigManager.separateMoney.second.toDouble())) {
                        player.sendLang("separate-not-money")
                        return false
                    }
                    HookVault.takeMoney(player, ConfigManager.separateMoney.second.toDouble())
                    return true
                }

                MoneyType.POINTS -> {
                    if (!HookPlayerPoints.hasPoints(player, ConfigManager.separateMoney.second)) {
                        player.sendLang("separate-not-money")
                        return false
                    }
                    HookPlayerPoints.takePoints(player, ConfigManager.separateMoney.second)
                    return true
                }
            }
        } else if (event.isLeftClick) {
            when (ConfigManager.unloadMoney.first) {
                MoneyType.VAULT -> {
                    if (!HookVault.hasMoney(player, ConfigManager.unloadMoney.second.toDouble())) {
                        player.sendLang("unload-not-money")
                        return false
                    }
                    HookVault.takeMoney(player, ConfigManager.unloadMoney.second.toDouble())
                    return true
                }

                MoneyType.POINTS -> {
                    if (!HookPlayerPoints.hasPoints(player, ConfigManager.unloadMoney.second)) {
                        player.sendLang("unload-not-money")
                        return false
                    }
                    HookPlayerPoints.takePoints(player, ConfigManager.unloadMoney.second)
                    return true
                }
            }
        }
        return false
    }


}