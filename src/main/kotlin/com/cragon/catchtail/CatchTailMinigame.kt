package com.cragon.catchtail

import me.nuty.minigamecore.MinigameCore
import me.nuty.minigamecore.minigame.AbstractMinigame
import org.bukkit.Bukkit
import org.bukkit.Bukkit.getServer
import org.bukkit.Location
import org.bukkit.Particle
import org.bukkit.entity.ArmorStand
import org.bukkit.entity.Player

class CatchTailMinigame : AbstractMinigame() {
    var tailLists: HashMap<Player, ArrayList<Location>> = HashMap()
    var armorStandList: HashMap<Player, ArmorStand> = HashMap()
//    private val fireHiddenList: Set<Player> = HashSet()

    override fun start() {

        for (p in participants) {
            tailLists[p] = ArrayList()
        }




        getServer().scheduler.scheduleSyncRepeatingTask(MinigameCore.getInstance(), Runnable {
            for (p in participants) {
//                if (fireHiddenList.contains(p)) continue
                for (l in tailLists[p]!!) {
                    spawnParticleAtLocation(l, p)
                }
            }
        }, 0L, 1L)
    }


    override fun playerLeft(p0: Player?) {

    }

    override fun join(p0: Player?) {

    }

    override fun initialize(p0: Int) {
        maxPlayers = 8
        minPlayers = 1
        identifier = "catchTail"
        name = "꼬리 잡기"

        initConstructor()
        Bukkit.getPluginManager().registerEvents(EventManager(this), MinigameCore.getInstance())
    }

    private fun spawnParticleAtLocation(loc: Location, player: Player) {
        player.spawnParticle(Particle.FLAME,
                loc.x, (loc.y + .5), loc.z,
                1, 0.05, 0.05, 0.05, 0.0, 0
        )
        for (p in participants) {
            if (p != player) {
                if (loc.distance(p.location) < 0.2) {
                    p.damage(1.0)
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
