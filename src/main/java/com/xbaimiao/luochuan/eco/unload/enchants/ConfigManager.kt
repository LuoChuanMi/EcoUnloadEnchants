package com.xbaimiao.luochuan.eco.unload.enchants

import com.xbaimiao.easylib.colored
import top.mcplugin.lib.Plugin

object ConfigManager {

    private val config = Plugin.getPlugin().config

    val title = config.getString("title").colored()

    // 洗去不给附魔书的花费
    val unloadMoney = config.getString("money.unload-enchant")!!
        .split(":")
        .let { MoneyType.valueOf(it[0].uppercase()) to it[1].toInt() }

    // 拆卸附魔书的花费
    val separateMoney = config.getString("money.separate-enchant")!!
        .split(":")
        .let { MoneyType.valueOf(it[0].uppercase()) to it[1].toInt() }

}

enum class MoneyType {
    VAULT, POINTS
}