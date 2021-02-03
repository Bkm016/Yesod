package ink.ptms.yesod.module

import com.mojang.brigadier.suggestion.Suggestions
import ink.ptms.yesod.api.NMS
import io.izzel.taboolib.module.packet.Packet
import io.izzel.taboolib.module.packet.TPacket
import org.bukkit.Bukkit
import org.bukkit.entity.Player

/**
 * @Author sky
 * @Since 2019-11-20 21:49
 */
object ProtectPacket {

    @TPacket(type = TPacket.Type.SEND)
    fun send(player: Player, packet: Packet): Boolean {
        if (packet.`is`("PacketPlayOutChat") && packet.read("a").toString().contains("chat.type.advancement")) {
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
    fun receive(player: Player?, packet: Packet): Boolean {
        if (packet.`is`("PacketPlayInAutoRecipe") || packet.`is`("PacketPlayInRecipeDisplayed")) {
            return false
        }
        return true
    }
}