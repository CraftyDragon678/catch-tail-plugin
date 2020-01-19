package com.cragon.catchtail

import me.nuty.minigamecore.MinigameCore
import org.bukkit.plugin.java.JavaPlugin

class CatchTail : JavaPlugin() {
    override fun onEnable() {
        MinigameCore.getInstance().minigameManager
                .registerMinigame("catchTail", CatchTailMinigame::class.java)

    }

    override fun onDisable() {
        CatchTailMinigame.instance?.let {
            it.armorStandList.keys.forEach { p ->
                    it.armorStandList[p]!!.remove()
            }
        }
    }
}