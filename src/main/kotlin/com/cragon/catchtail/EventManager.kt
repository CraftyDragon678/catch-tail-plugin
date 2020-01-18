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
import org.bukkit.event.entity.PlayerDeathEvent
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.event.player.PlayerMoveEvent
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.SkullMeta
import java.util.*
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
                armorStand.isInvulnerable = true
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
                            p.damage(e.damage, e.damager)
                        }
                    }
                }
            }
        }
    }

    @EventHandler
    fun onPlayerInteractive(e: PlayerInteractEvent) {
        if (main.status == MinigameStatus.STARTED) {
            if (e.material == Material.PLAYER_HEAD && e.item != null) {
                if (e.item!!.itemMeta != null && (e.item!!.itemMeta as SkullMeta?)!!.owningPlayer != null) {
                    if (Objects.requireNonNull((e.item!!.itemMeta as SkullMeta?)!!.owningPlayer)!!.player != null) {
                        val v = e.player.location.direction.normalize().multiply(1.5)
                        val l = e.player.eyeLocation
                        val i = ItemStack(Material.PLAYER_HEAD)
                        //                    i.setItemMeta(((SkullMeta)i.getItemMeta()).setOwningPlayer(((SkullMeta) e.getItem().getItemMeta()).getOwningPlayer()));
                        val meta = i.itemMeta as SkullMeta?
                        meta!!.owningPlayer = (e.item!!.itemMeta as SkullMeta?)!!.owningPlayer
                        i.itemMeta = meta
                        val block = e.player.world.spawnFallingBlock(e.player.location, i.data!!)
                        block.dropItem = false
                        block.setHurtEntities(false)
                        block.velocity = v
                        e.player.sendMessage(Objects.requireNonNull(Objects.requireNonNull((e.item!!.itemMeta as SkullMeta?)!!.owningPlayer)!!.player)!!.displayName)
                    }
                }
            }
        }
    }

    @EventHandler
    fun onPlayerDeath(e: PlayerDeathEvent) {
        if (main.status == MinigameStatus.STARTED && main.armorStandList.containsKey(e.entity)) {
            main.removeParticipant(e.entity)
            if (main.participants.size == 1) {
                main.destroy(false)
                for (a in main.armorStandList.values) {
                    a.remove()
                }
                main.tailLists = HashMap()
                main.armorStandList = HashMap()
            }
        }
    }
}