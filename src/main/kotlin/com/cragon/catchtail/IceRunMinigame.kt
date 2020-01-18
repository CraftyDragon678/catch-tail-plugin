package com.cragon.catchtail

import me.nuty.minigamecore.minigame.AbstractMinigame
import me.nuty.minigamecore.minigame.IMinigame
import org.bukkit.entity.Player

class IceRunMinigame : AbstractMinigame() {
    override fun start() {

    }

    override fun playerLeft(p0: Player?) {

    }

    override fun join(p0: Player?) {

    }

    override fun initialize(p0: Int) {
        maxPlayers = 8
        minPlayers = 2
        identifier = "catchTail"
        name = "꼬리 잡기"

        initConstructor()
    }
}