package ink.ptms.yesod.module

import com.mojang.brigadier.suggestion.Suggestions
import ink.ptms.yesod.Yesod
import ink.ptms.yesod.api.NMS
import io.izzel.taboolib.internal.xseries.XSound
import io.izzel.taboolib.kotlin.Reflex
import io.izzel.taboolib.kotlin.Tasks
import io.izzel.taboolib.module.inject.PlayerContainer
import io.izzel.taboolib.module.packet.Packet
import io.izzel.taboolib.module.packet.TPacket
import org.bukkit.Bukkit
import org.bukkit.entity.Entity
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.player.PlayerFishEvent
import org.bukkit.inventory.CraftingInventory
import java.util.concurrent.ConcurrentHashMap

/**
 * @author sky
 * @since 2019-11-20 21:49
 */
object PatchPacket {

    @PlayerContainer
    val bite = ConcurrentHashMap<String, Int>()

    @TPacket(type = TPacket.Type.SEND)
    fun send(player: Player, packet: Packet): Boolean {
        if (packet.`is`("PacketPlayOutChat") && packet.read("a")?.toString()?.contains("chat.type.advancement") == true) {
            return false
        }
        if (packet.`is`("PacketPlayOutTabComplete") && !player.isOp) {
            return packet.read("b", Suggestions.empty().get()).list.any { Bukkit.getPlayerExact(it.text) != null }
        }
        if (packet.`is`("PacketPlayOutWorldParticles") && NMS.HANDLE.readParticlePacket(packet.read("j")) == "minecraft:damage_indicator") {
            return false
        }
        return true
    }

    @TPacket(type = TPacket.Type.RECEIVE)
    fun receive(player: Player, packet: Packet): Boolean {
        if (packet.`is`("PacketPlayInAutoRecipe") || packet.`is`("PacketPlayInRecipeDisplayed")) {
            return Yesod.allowCraftDisplay
        }
        if (packet.`is`("PacketPlayInUseItem") || packet.`is`("PacketPlayInUseEntity") || packet.`is`("PacketPlayInArmAnimation")) {
            return player.openInventory.topInventory is CraftingInventory
        }
        return true
    }

    @TPacket(type = TPacket.Type.SEND)
    fun e2(player: Player, packet: Packet): Boolean {
        // 视觉欺骗
        if (packet.any("PacketPlayOutEntityVelocity") && packet.read("a", 0) == bite[player.name]) {
            return false
        }
        if (packet.any("PacketPlayOutEntityMetadata") && packet.read("a", 0) == bite[player.name]) {
            return false
        }
        if (packet.any("PacketPlayOutEntityStatus") && packet.read("a", 0) == bite[player.name]) {
            return false
        }
        if (packet.any("PacketPlayOutEntityEffect") && packet.read("a", 0) == bite[player.name]) {
            return false
        }
        return true
    }

    @EventHandler
    fun e(e: PlayerFishEvent) {
        if (e.state == PlayerFishEvent.State.REEL_IN) {
            // 声音欺骗
            val hook = Reflex.Companion.of(e).invoke<Entity>("getHook")!!
            Tasks.delay(20) {
                XSound.ENTITY_FISHING_BOBBER_SPLASH.play(hook.location, 0f, 0f)
            }
            Tasks.delay(40) {
                XSound.ENTITY_FISHING_BOBBER_SPLASH.play(hook.location, 0f, 0f)
            }
        }
        if (e.state == PlayerFishEvent.State.BITE) {
            bite[e.player.name] = Reflex.Companion.of(e).invoke<Entity>("getHook")!!.entityId
            Tasks.delay(40) {
                bite.remove(e.player.name)
            }
        }
    }
}