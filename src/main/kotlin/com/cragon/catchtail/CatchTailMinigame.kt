package com.cragon.catchtail

import me.nuty.minigamecore.MinigameCore
import me.nuty.minigamecore.minigame.AbstractMinigame
import me.nuty.minigamecore.minigame.MinigameResult
import me.nuty.minigamecore.minigame.MinigameStatus
import org.bukkit.Bukkit
import org.bukkit.Bukkit.getServer
import org.bukkit.GameMode
import org.bukkit.Location
import org.bukkit.Particle
import org.bukkit.entity.ArmorStand
import org.bukkit.entity.Player
import java.util.*
import kotlin.collections.HashMap
import kotlin.collections.set


class CatchTailMinigame : AbstractMinigame() {
    var tailLists: HashMap<Player, ArrayList<Location>> = HashMap()
    var armorStandList: HashMap<Player, ArmorStand> = HashMap()
    companion object {
        lateinit var instance: CatchTailMinigame
    }
//    private val fireHiddenList: Set<Player> = HashSet()

    override fun start() {
        for (p in participants) {
            tailLists[p] = ArrayList()
            tailLists[p]!!.add(p.location)
        }

        getServer().scheduler.scheduleSyncRepeatingTask(MinigameCore.getInstance(), Runnable {
            for (p in tailLists.keys) {
//                if (fireHiddenList.contains(p)) continue
                for (l in tailLists[p]!!) {
                    spawnParticleAtLocation(l, p)
                }
            }
        }, 0L, 1L)


    }

    fun playerDead(p: Player) {
        armorStandList[p]!!.remove()
        tailLists[p] = ArrayList()
        armorStandList.remove(p)
        tailLists.remove(p)
        p.allowFlight = true
        p.isInvulnerable = true

        if (armorStandList.size == 1) {
            result.winners = armorStandList.keys.toList()
            armorStandList.keys.forEach {
                armorStandList[it]!!.remove()
                it.allowFlight = true
                it.isInvulnerable = true
            }

            destroy(false)
            tailLists = HashMap()
            armorStandList = HashMap()
        }
    }


    override fun playerLeft(p0: Player?) {

    }


    override fun join(p0: Player?) {
        setStartLeftTime(5, true)
    }


    override fun initialize(p0: Int) {
        instance = this
        maxPlayers = 8
        minPlayers = 2
        identifier = "catchTail"
        name = "꼬리 잡기"

        initConstructor()
        Bukkit.getPluginManager().registerEvents(EventManager(this), MinigameCore.getInstance())
    }

    private fun spawnParticleAtLocation(loc: Location, player: Player) {
        for (p in participants) {
            p.spawnParticle(Particle.FLAME,
                    loc.x, (loc.y + .5), loc.z,
                    1, 0.05, 0.05, 0.05, 0.0, null
            )

        }
        for (p in armorStandList.keys) {
            if (p != player) {
                if (loc.distance(p.location) < 0.2) {
                    if (p.health - 1 < 1)
                        playerDead(p)
                    else p.damage(1.0)
                }
            }
        }
        /*val packet = PacketPlayOutWorldParticles(
                Particles.FLAME,
                true,
                loc.x.toFloat(),
                (loc.y + .5).toFloat(),
                loc.z.toFloat(),
                .05f,
                .05f,
                .05f,
                0,
                1
        )
        for (p in getServer().onlinePlayers) {
            (p as CraftPlayer).getHandle().playerConnection.sendPacket(packet)
            if (p != player) {
                if (loc.distance(p.location) < 0.2) {
                    p.damage(1.0)
                }
            }
        }*/
    }
}
