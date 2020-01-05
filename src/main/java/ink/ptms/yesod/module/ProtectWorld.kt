package ink.ptms.yesod.module

import ink.ptms.yesod.Yesod
import io.izzel.taboolib.module.inject.TListener
import io.izzel.taboolib.util.lite.Effects
import io.izzel.taboolib.util.lite.Materials
import org.bukkit.GameMode
import org.bukkit.Material
import org.bukkit.Particle
import org.bukkit.entity.*
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.*
import org.bukkit.event.entity.*
import org.bukkit.event.hanging.HangingBreakByEntityEvent
import org.bukkit.event.hanging.HangingBreakEvent
import org.bukkit.event.player.*
import org.bukkit.event.raid.RaidTriggerEvent

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
    fun e(e: EntityChangeBlockEvent) {
        if (e.entity is LivingEntity) {
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
        if (!Yesod.isAllow1(e.remover)) {
            e.isCancelled = true
        }
    }

    @EventHandler
    fun e(e: PlayerInteractAtEntityEvent) {
        if (!Yesod.isAllow1(e.player) && e.rightClicked is Hanging) {
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
    fun e(e: PlayerJoinEvent) {
        e.player.isSleepingIgnored = true
    }

    @EventHandler
    fun e(e: RaidTriggerEvent) {
        e.isCancelled = true
    }
}