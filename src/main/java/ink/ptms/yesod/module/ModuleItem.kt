package ink.ptms.yesod.module

import io.izzel.taboolib.module.inject.TListener
import net.minecraft.server.v1_14_R1.BlockTileEntity
import net.minecraft.server.v1_14_R1.IInventory
import net.minecraft.server.v1_14_R1.World
import org.bukkit.Material
import org.bukkit.block.Chest
import org.bukkit.block.Container
import org.bukkit.block.Hopper
import org.bukkit.craftbukkit.v1_14_R1.CraftWorld
import org.bukkit.craftbukkit.v1_14_R1.block.CraftHopper
import org.bukkit.craftbukkit.v1_14_R1.block.impl.CraftChest
import org.bukkit.craftbukkit.v1_14_R1.entity.CraftHumanEntity
import org.bukkit.craftbukkit.v1_14_R1.entity.CraftPlayer
import org.bukkit.craftbukkit.v1_14_R1.inventory.CraftContainer
import org.bukkit.craftbukkit.v1_14_R1.inventory.CraftInventory
import org.bukkit.entity.EntityType
import org.bukkit.entity.Phantom
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.block.BlockPlaceEvent
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.BlockStateMeta

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

    fun isContainer(item: ItemStack): Boolean {
        return item.itemMeta is BlockStateMeta && (item.itemMeta as BlockStateMeta).blockState is Container
    }
}