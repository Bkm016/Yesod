package ink.ptms.yesod.module

import io.izzel.taboolib.module.inject.TListener
import org.bukkit.block.Container
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.block.BlockPlaceEvent
import org.bukkit.event.player.PlayerItemConsumeEvent
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.BlockStateMeta
import org.bukkit.inventory.meta.PotionMeta
import org.bukkit.potion.PotionEffectType

/**
 * @Author sky
 * @Since 2019-11-21 22:52
 */
@TListener
class ModuleItem : Listener {

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    fun onPlace(e: BlockPlaceEvent) {
        if (e.blockPlaced.state is Container && isContainer(e.itemInHand)) {
            try {
                (e.blockPlaced.state as Container).inventory.contents = ((e.itemInHand.itemMeta as BlockStateMeta).blockState as Container).snapshotInventory.contents
            } catch (t: Throwable) {
                e.isCancelled = true
                e.player.sendMessage("§cI'm sorry, but you do not have permission to perform this command. Please contact the server administrators if you believe that this is in error.")
                t.printStackTrace()
            }
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    fun onConsume(e: PlayerItemConsumeEvent) {
        if (e.item.itemMeta is PotionMeta) {
            (e.item.itemMeta as PotionMeta).customEffects
                    .filter { it.type == PotionEffectType.SATURATION }
                    .forEach { e.player.addPotionEffect(it, true) }
        }
    }

    fun isContainer(item: ItemStack): Boolean {
        return item.itemMeta is BlockStateMeta && (item.itemMeta as BlockStateMeta).blockState is Container
    }
}