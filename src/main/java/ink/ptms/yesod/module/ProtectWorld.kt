package ink.ptms.yesod.module

import ink.ptms.yesod.Yesod
import ink.ptms.yesod.Yesod.bypass
import io.izzel.taboolib.common.event.PlayerAttackEvent
import io.izzel.taboolib.kotlin.Tasks
import io.izzel.taboolib.module.command.lite.CommandBuilder
import io.izzel.taboolib.module.inject.TInject
import io.izzel.taboolib.module.inject.TListener
import io.izzel.taboolib.util.lite.Servers
import org.bukkit.Material
import org.bukkit.entity.Hanging
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.Action
import org.bukkit.event.block.BlockExplodeEvent
import org.bukkit.event.block.LeavesDecayEvent
import org.bukkit.event.entity.*
import org.bukkit.event.hanging.HangingBreakByEntityEvent
import org.bukkit.event.hanging.HangingBreakEvent
import org.bukkit.event.player.*
import org.bukkit.event.raid.RaidTriggerEvent
import org.bukkit.util.Vector

/**
 * @author sky
 * @since 2019-11-20 21:21
 */
@TListener
class ProtectWorld : Listener {

    @TInject
    val setSpawnLocation = CommandBuilder.create("setSpawnLocation", Yesod.plugin)
        .forceRegister()
        .permission("admin")
        .execute { sender, _ ->
            if (sender is Player) {
                val loc = sender.location.clone()
                sender.world.spawnLocation = loc
                sender.sendMessage("世界${sender.world.name}的出生点已被重设在${loc.x},${loc.y},${loc.z}(${loc.yaw},${loc.pitch})")
            }
        }!!

    @EventHandler
    fun e(e: PlayerJoinEvent) {
        if (!e.player.hasPlayedBefore()) {
            e.player.teleport(e.player.world.spawnLocation)
        }
    }

    @EventHandler
    fun e(e: PlayerRespawnEvent) {
        e.respawnLocation = e.respawnLocation.world!!.spawnLocation
    }

    @EventHandler
    fun e(e: EntityBreedEvent) {
        if ("BREED" in Yesod.blockFeatures) {
            e.isCancelled = true
        }
    }

    @EventHandler
    fun e(e: LeavesDecayEvent) {
        if ("LEAVES_DECAY" in Yesod.blockFeatures) {
            e.isCancelled = true
        }
    }

    @EventHandler
    fun e(e: EntityChangeBlockEvent) {
        if ("ENTITY_CHANGE_BLOCK" in Yesod.blockFeatures && e.entity is LivingEntity) {
            e.isCancelled = true
        }
    }

    @EventHandler
    fun e(e: PlayerInteractEvent) {
        if ("FARMLAND_PHYSICAL" in Yesod.blockFeatures && e.action == Action.PHYSICAL && e.clickedBlock!!.type == Material.FARMLAND) {
            e.isCancelled = true
        }
    }

    @EventHandler
    fun e(e: EntityInteractEvent) {
        if ("FARMLAND_PHYSICAL" in Yesod.blockFeatures && e.block.type == Material.FARMLAND) {
            e.isCancelled = true
        }
    }

    @EventHandler
    fun e(e: HangingBreakEvent) {
        if ("HANGING_BREAK" in Yesod.blockFeatures && e.cause != HangingBreakEvent.RemoveCause.ENTITY) {
            e.isCancelled = true
        }
    }

    @EventHandler
    fun e(e: HangingBreakByEntityEvent) {
        if ("HANGING_BREAK" in Yesod.blockFeatures && e.remover?.bypass(true) == false) {
            e.isCancelled = true
        }
    }

    @EventHandler
    fun e(e: PlayerInteractEntityEvent) {
        if ("HANGING_BREAK" in Yesod.blockFeatures && !e.player.bypass(true) && e.rightClicked is Hanging) {
            e.isCancelled = true
        }
    }

    @EventHandler
    fun e(e: EntityDamageByEntityEvent) {
        val player = Servers.getAttackerInDamageEvent(e) ?: return
        if ("HANGING_BREAK" in Yesod.blockFeatures && !player.bypass(true) && e.entity is Hanging) {
            e.isCancelled = true
        }
    }

    @EventHandler
    fun e(e: PlayerTeleportEvent) {
        e.isCancelled = e.cause.name in Yesod.blockTeleport
    }

    @EventHandler
    fun e(e: EntityExplodeEvent) {
        if ("ENTITY_EXPLODE" in Yesod.blockFeatures) {
            e.blockList().clear()
        }
    }

    @EventHandler
    fun e(e: BlockExplodeEvent) {
        if ("BLOCK_EXPLODE" in Yesod.blockFeatures) {
            e.blockList().clear()
        }
    }

    @EventHandler
    fun e(e: RaidTriggerEvent) {
        if ("RAID" in Yesod.blockFeatures) {
            e.isCancelled = true
        }
    }

    @EventHandler
    fun e(e: PlayerMoveEvent) {
        val to = e.to!!
        if (e.from.x != to.x || e.from.y != to.y || e.from.z != to.z) {
            if (to.y < 10 && Yesod.voidProtect) {
                e.isCancelled = true
                // 返回大厅
                Tasks.task {
                    e.player.velocity = Vector(0, 0, 0)
                    e.player.teleport(e.player.world.spawnLocation)
                }
            }
        }
    }
}