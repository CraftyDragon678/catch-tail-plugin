package com.cragon.catchtail

import me.nuty.minigamecore.MinigameCore
import org.bukkit.Bukkit
import org.bukkit.plugin.java.JavaPlugin

class CatchTail : JavaPlugin() {
    override fun onEnable() {
        MinigameCore.getInstance().minigameManager
                .registerMinigame("catchTail", IceRunMinigame::class.java)

    }

    override fun onDisable() { // Plugin shutdown logic
    }
}