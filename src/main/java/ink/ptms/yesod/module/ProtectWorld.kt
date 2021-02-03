package ink.ptms.yesod.module

import ink.ptms.yesod.Yesod.bypass
import io.izzel.taboolib.kotlin.Tasks
import io.izzel.taboolib.module.inject.TListener
import io.izzel.taboolib.util.lite.Effects
import org.bukkit.Material
import org.bukkit.Particle
import org.bukkit.entity.*
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.Action
import org.bukkit.event.block.BlockExplodeEvent
import org.bukkit.event.block.LeavesDecayEvent
import org.bukkit.event.entity.*
import org.bukkit.event.hanging.HangingBreakByEntityEvent
import org.bukkit.event.hanging.HangingBreakEvent
import org.bukkit.event.player.PlayerInteractAtEntityEvent
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.event.player.PlayerMoveEvent
import org.bukkit.event.player.PlayerTeleportEvent
import org.bukkit.event.raid.RaidTriggerEvent
import org.bukkit.util.Vector

/**
 * @Author sky
 * @Since 2019-11-20 21:21
 */
@TListener
class ProtectWorld : Listener {

    @EventHandler
    fun e(e: EntityBreedEvent) {
        e.isCancelled = true
    }

    @EventHandler
    fun e(e: LeavesDecayEvent) {
        e.isCancelled = true
    }

    @EventHandler
    fun e(e: EntityChangeBlockEvent) {
        if (e.entity is LivingEntity) {
            e.isCancelled = true
        }
    }

    @EventHandler
    fun e(e: PlayerInteractEvent) {
        if (e.action == Action.PHYSICAL && e.clickedBlock!!.type == Material.FARMLAND) {
            e.isCancelled = true
        }
    }

    @EventHandler
    fun e(e: EntityInteractEvent) {
        if (e.block.type == Material.FARMLAND) {
            e.isCancelled = true
        }
    }

    @EventHandler
    fun e(e: HangingBreakEvent) {
        if (e.cause != HangingBreakEvent.RemoveCause.ENTITY) {
            e.isCancelled = true
        }
    }

    @EventHandler
    fun e(e: HangingBreakByEntityEvent) {
        if (e.remover?.bypass(true) == false) {
            e.isCancelled = true
        }
    }

    @EventHandler
    fun e(e: PlayerInteractAtEntityEvent) {
        if (!e.player.bypass(true) && e.rightClicked is Hanging) {
            e.isCancelled = true
        }
    }

    @EventHandler
    fun e(e: ProjectileHitEvent) {
        if (e.entity is FishHook && (e.hitEntity is ArmorStand || e.hitEntity?.hasMetadata("NPC") == true)) {
            e.entity.remove()
            Effects.create(Particle.SMOKE_NORMAL, e.entity.location).speed(0.1).count(8).range(50.0).play()
        }
        if (e.entity is Arrow && (e.entity as Arrow).pickupStatus != AbstractArrow.PickupStatus.ALLOWED) {
            e.entity.remove()
            Effects.create(Particle.SMOKE_NORMAL, e.entity.location).speed(0.1).count(8).range(50.0).play()
        }
    }

    @EventHandler
    fun e(e: PlayerTeleportEvent) {
        when (e.cause) {
            PlayerTeleportEvent.TeleportCause.NETHER_PORTAL,
            PlayerTeleportEvent.TeleportCause.ENDER_PEARL,
            PlayerTeleportEvent.TeleportCause.END_GATEWAY,
            PlayerTeleportEvent.TeleportCause.END_PORTAL -> e.isCancelled = true
            else -> {
            }
        }
    }

    @EventHandler
    fun e(e: EntityDamageEvent) {
        if (e.entity !is Player && e.cause == EntityDamageEvent.DamageCause.VOID) {
            e.entity.remove()
        }
    }

    @EventHandler
    fun e(e: EntityExplodeEvent) {
        e.blockList().clear()
    }

    @EventHandler
    fun e(e: BlockExplodeEvent) {
        e.blockList().clear()
    }

    @EventHandler
    fun e(e: RaidTriggerEvent) {
        e.isCancelled = true
    }

    @EventHandler
    fun e(e: PlayerMoveEvent) {
        val to = e.to!!
        if (e.from.x != to.x || e.from.y != to.y || e.from.z != to.z) {
            if (to.y < 10) {
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