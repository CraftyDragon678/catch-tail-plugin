package com.cragon.catchtail

import me.nuty.minigamecore.MinigameCore
import me.nuty.minigamecore.minigame.AbstractMinigame
import me.nuty.minigamecore.minigame.MinigameResult
import me.nuty.minigamecore.minigame.MinigameStatus
import org.bukkit.*
import org.bukkit.Bukkit.getServer
import org.bukkit.entity.ArmorStand
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.SkullMeta
import java.util.*
import kotlin.collections.HashMap
import kotlin.collections.set


class CatchTailMinigame : AbstractMinigame() {
    var tailLists: HashMap<Player, ArrayList<Location>> = HashMap()
    var armorStandList: HashMap<Player, ArmorStand> = HashMap()
    companion object {
        var instance: CatchTailMinigame? = null
    }
//    private val fireHiddenList: Set<Player> = HashSet()

    override fun start() {
        for (p in participants) {
            tailLists[p] = ArrayList()
            tailLists[p]!!.add(p.location)

            val loc: Location = tailLists[p]!![0].clone()

            loc.y = loc.y - 1.2
            val armorStand = p.world.spawn(loc, ArmorStand::class.java)
            armorStand.setGravity(false)
            armorStand.isVisible = false
//                armorStand.isInvulnerable = true
            val head = ItemStack(Material.PLAYER_HEAD)
            val meta = head.itemMeta as SkullMeta?
            meta!!.owningPlayer = p
            head.itemMeta = meta
            armorStand.setHelmet(head)
            armorStandList[p] = armorStand
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

        val deadList: ArrayList<Player> = ArrayList()
        for (p in armorStandList.keys) {
            if (p != player) {
                if (loc.distance(p.location) < 0.2) {
                    if (p.health - 1 < 1)
                        deadList.add(p)
                    else p.damage(1.0)
                }
            }
        }

        deadList.forEach {
            playerDead(it)
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
