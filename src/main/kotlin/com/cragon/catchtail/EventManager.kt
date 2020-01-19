package com.cragon.catchtail

import me.nuty.minigamecore.minigame.MinigameStatus
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.entity.ArmorStand
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.event.entity.EntityDamageEvent
import org.bukkit.event.entity.PlayerDeathEvent
import org.bukkit.event.player.PlayerInteractAtEntityEvent
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.event.player.PlayerMoveEvent
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.SkullMeta
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

class EventManager(val main: CatchTailMinigame) : Listener {

    @EventHandler
    fun onPlayerMove(e: PlayerMoveEvent) {
        if  (main.status == MinigameStatus.STARTED && main.participants.contains(e.player)) {
            val player = e.player

            if (main.tailLists[player]?.get(main.tailLists[player]?.size!! - 1)?.distance(e.to!!)!! < 0.1) {
                return
            } else if (main.tailLists[player]!!.size >= 100) {
                main.tailLists[player]!!.removeAt(0)
            }

            main.tailLists[player]?.add(player.location)
            var loc: Location = main.tailLists[player]!![0].clone()

            if (!main.armorStandList.containsKey(player)) {
                loc.y = loc.y - 1.2
                val armorStand = player.world.spawn(loc, ArmorStand::class.java)
                armorStand.setGravity(false)
                armorStand.isVisible = false
//                armorStand.isInvulnerable = true
                val head = ItemStack(Material.PLAYER_HEAD)
                val meta = head.itemMeta as SkullMeta?
                meta!!.owningPlayer = player
                head.itemMeta = meta
                armorStand.setHelmet(head)
                main.armorStandList[player] = armorStand
            }

            loc = main.tailLists[player]?.get(0)!!.clone()
            loc.y = loc.y - 1.2
            main.armorStandList[player]?.teleport(loc)
        }
    }

    @EventHandler
    fun onEntityDamageByEntity(e: EntityDamageByEntityEvent) {

        if (main.status == MinigameStatus.STARTED) {
            if (e.entity is ArmorStand && e.damager is Player) {
                if (main.armorStandList.containsValue(e.entity as ArmorStand)) {
                    for (p in main.armorStandList.keys) {
                        if (main.armorStandList[p] == e.entity) {
                            if (p.health - e.damage < 1)
                                main.playerDead(p)
                            else p.damage(e.damage, e.damager)
                        }
                    }
                }
            }

            if (e.entity is Player && main.armorStandList.containsKey(e.entity as Player)) {
                if ((e.entity as Player).health - e.damage < 1) {
                    main.playerDead(e.entity as Player)
                    e.isCancelled = true
                }
            }
        }
    }

    @EventHandler
    fun onEntityDamageByEntity(e: EntityDamageEvent) {
        if (main.status == MinigameStatus.STARTED) {
            if (e.entity is Player) {
                val p = e.entity as Player
                if (main.armorStandList.containsKey(p)) {
                    if (p.health - e.damage < 1) {
                        main.playerDead(p)
                        e.isCancelled = true
                    }
                }
            }
        }
    }
}