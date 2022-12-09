package com.xbaimiao.luochuan.eco.unload.enchants

import com.xbaimiao.easylib.colored
import top.mcplugin.lib.Plugin

object ConfigManager {

    private val config = Plugin.getPlugin().config

    val title = config.getString("title").colored()

}