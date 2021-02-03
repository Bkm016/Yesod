package ink.ptms.yesod.module

import io.izzel.taboolib.module.inject.TListener
import io.izzel.taboolib.util.lite.Numbers
import io.izzel.taboolib.util.lite.Servers
import org.bukkit.Sound
import org.bukkit.enchantments.Enchantment
import org.bukkit.entity.LivingEntity
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
    fun e(e: EntityDamageByEntityEvent) {
        if (e.cause == EntityDamageEvent.DamageCause.ENTITY_ATTACK || e.cause == EntityDamageEvent.DamageCause.ENTITY_SWEEP_ATTACK) {
            Servers.getAttackerInDamageEvent(e)?.let { it.playSound(it.location, Sound.ENTITY_PLAYER_ATTACK_CRIT, 0.2f, Numbers.getRandomDouble(0.8, 1.2).toFloat()) }
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