package com.xbaimiao.luochuan.eco.unload.enchants

import com.xbaimiao.easylib.giveItem
import com.xbaimiao.easylib.sendLang
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.EnchantmentStorageMeta
import top.mcplugin.lib.module.item.ItemUtil
import top.mcplugin.lib.module.ui.type.Basic

object GUI {

    private val enchantSlots = arrayOf(3, 4, 5, 6, 7, 8, 12, 13, 14, 15, 16, 17, 21, 22, 23, 24, 25, 26)
    private const val itemSlot = 10

    fun open(player: Player) {
        val item = player.inventory.itemInMainHand
        if (ItemUtil.isAir(item)) {
            player.sendLang("item-null")
            return
        }
        if (item.enchantments.isEmpty()) {
            player.sendLang("item-no-enchant")
            return
        }

        val basic = Basic(ConfigManager.title)
        basic.rows = 3
        basic.set(itemSlot, item)

        val enchantBooks = ArrayList<ItemStack>()

        for (key in item.enchantments) {

            val book = ItemStack(Material.ENCHANTED_BOOK)
            val storageMeta = book.itemMeta as EnchantmentStorageMeta? ?: return
            storageMeta.addStoredEnchant(key.key, key.value, true)
            book.itemMeta = storageMeta

            enchantBooks.add(book)
        }

        val mapping = HashMap<Int, ItemStack>()

        for (slot in enchantBooks.withIndex()) {
            mapping[enchantSlots[slot.index]] = slot.value
            basic.set(enchantSlots[slot.index], slot.value)
        }

        basic.onClick {
            it.isCancelled = true

            val book = mapping[it.rawSlot] ?: return@onClick

            val storageMeta = book.itemMeta as EnchantmentStorageMeta? ?: return@onClick
            val enchant = storageMeta.storedEnchants.entries.first()

            val meta = item.itemMeta!!
            meta.removeEnchant(enchant.key)
            item.itemMeta = meta

            player.inventory.setItemInMainHand(item)
            player.giveItem(book)

            player.sendLang("enchant-success")
            if (item.enchantments.isEmpty()) {
                player.closeInventory()
            } else {
                open(player)
            }
        }

        player.openInventory(basic.build())
    }

}