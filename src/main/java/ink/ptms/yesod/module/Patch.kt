package ink.ptms.yesod.module

import ink.ptms.yesod.Yesod
import ink.ptms.yesod.Yesod.bypass
import io.izzel.taboolib.module.inject.TListener
import io.izzel.taboolib.util.item.Items
import io.izzel.taboolib.util.lite.Effects
import io.izzel.taboolib.util.lite.Numbers
import org.bukkit.Particle
import org.bukkit.enchantments.Enchantment
import org.bukkit.entity.*
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.block.Action
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.event.entity.EntityDamageEvent
import org.bukkit.event.entity.ProjectileHitEvent
import org.bukkit.event.inventory.*
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.inventory.ItemStack

/**
 * Yesod
 * ink.ptms.yesod.module.Fixed
 *
 * @author sky
 * @since 2021/3/11 10:46 上午
 */
@TListener
class Patch : Listener {

    /**
     * 禁止合成物品
     */
    @EventHandler
    fun e(e: CraftItemEvent) {
        if (!Yesod.allowCraft) {
            e.isCancelled = true
        }
    }

    /**
     * 禁止合成
     */
    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    fun e(e: InventoryClickEvent) {
        if (!Yesod.allowCraft) {
            e.isCancelled = e.clickedInventory?.type == InventoryType.CRAFTING
        }
    }

    /**
     * 禁止合成
     */
    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    fun e(e: InventoryDragEvent) {
        if (!Yesod.allowCraft) {
            e.isCancelled = e.inventory.type == InventoryType.CRAFTING && e.rawSlots.any { it < 5 }
        }
    }

    /**
     * 禁止打开界面
     */
    @EventHandler
    fun e(e: InventoryOpenEvent) {
        e.isCancelled = e.inventory.type.name in Yesod.blockInventory
    }

    /**
     * 禁止方块交互
     */
    @EventHandler
    fun e(e: PlayerInteractEvent) {
        if ((e.action == Action.RIGHT_CLICK_BLOCK || e.action == Action.LEFT_CLICK_BLOCK) && !e.player.bypass()) {
            val type = e.clickedBlock!!.type.name
            if (Yesod.blockInteract.any { if (it.endsWith("?")) it.substring(0, it.length - 1) in type else it == type }) {
                e.isCancelled = true
            }
        }
    }

    /**
     * 禁止鱼竿移动公民
     * 禁止创造模式射出的弓箭在世界停留
     */
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

    /**
     * 重做荆棘伤害
     */
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    fun e(e: EntityDamageByEntityEvent) {
        if (e.cause == EntityDamageEvent.DamageCause.THORNS && e.damager is LivingEntity && Yesod.thornOverride) {
            e.damage = 1.0
            getArmor(e.damager as LivingEntity)
                .filter { Items.nonNull(it) }
                .forEach { item ->
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