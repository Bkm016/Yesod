package ink.ptms.yesod.module

import ink.ptms.yesod.Yesod
import io.izzel.taboolib.module.inject.TListener
import io.izzel.taboolib.module.lite.SimpleEquip
import io.izzel.taboolib.util.item.Items
import io.izzel.taboolib.util.lite.Effects
import io.izzel.taboolib.util.lite.Numbers
import io.izzel.taboolib.util.lite.Servers
import net.minecraft.server.v1_14_R1.EntityHuman
import org.bukkit.Bukkit
import org.bukkit.Particle
import org.bukkit.Sound
import org.bukkit.craftbukkit.v1_14_R1.CraftSound
import org.bukkit.enchantments.Enchantment
import org.bukkit.entity.EntityType
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.event.entity.EntityDamageEvent
import org.bukkit.inventory.ItemStack
import java.util.*

/**
 * @Author sky
 * @Since 2019-11-21 22:41
 */
@TListener
class ModuleDamage : Listener {

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    fun e(e: EntityDamageEvent) {
        if (e.cause == EntityDamageEvent.DamageCause.ENTITY_ATTACK || e.cause == EntityDamageEvent.DamageCause.ENTITY_SWEEP_ATTACK) {
            when (e.entityType) {
                EntityType.SKELETON, EntityType.WITHER_SKELETON, EntityType.SKELETON_HORSE, EntityType.BLAZE, EntityType.SLIME, EntityType.MAGMA_CUBE -> {
                }
                else -> Bukkit.getScheduler().runTaskAsynchronously(Yesod.getPlugin(), Runnable {
                    Effects.create(Particle.DAMAGE_INDICATOR, e.entity.location.add(0.0, 0.5, 0.0)).speed(0.2).count(5).range(100.0).play()
                })
            }
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    fun e(e: EntityDamageByEntityEvent) {
        if (e.cause == EntityDamageEvent.DamageCause.ENTITY_ATTACK || e.cause == EntityDamageEvent.DamageCause.ENTITY_SWEEP_ATTACK) {
            Servers.getAttackerInDamageEvent(e)?.let { it.playSound(it.location, Sound.ENTITY_PLAYER_ATTACK_CRIT, 1f, 1f) }
        }
        if (e.cause == EntityDamageEvent.DamageCause.THORNS && e.damager is LivingEntity) {
            e.damage = 1.0
            getArmor(e.damager as LivingEntity).filter(Objects::nonNull).forEach { item ->
                val level = item!!.getEnchantmentLevel(Enchantment.THORNS)
                if (level <= 5) {
                    if (Math.random() <= level * 0.2) {
                        e.damage += Numbers.getRandomInteger(1, 4)
                    }
                } else {
                    e.damage += (level - 5)
                }
            }
        }
    }

    fun getArmor(entity: LivingEntity): Array<ItemStack?> {
        val items = arrayOfNulls<ItemStack>(6)
        items[0] = entity.equipment?.helmet
        items[1] = entity.equipment?.chestplate
        items[2] = entity.equipment?.leggings
        items[3] = entity.equipment?.boots
        return items
    }
}