package ink.ptms.yesod.module;

import com.google.common.collect.Maps;
import com.mojang.brigadier.suggestion.Suggestion;
import com.mojang.brigadier.suggestion.Suggestions;
import ink.ptms.yesod.asm.Asm;
import io.izzel.taboolib.module.inject.TInject;
import io.izzel.taboolib.module.inject.TListener;
import io.izzel.taboolib.module.inject.TSchedule;
import io.izzel.taboolib.module.packet.Packet;
import io.izzel.taboolib.module.packet.TPacketListener;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @Author sky
 * @Since 2019-11-20 21:49
 */
@TListener
public class ProtectPacket implements Listener {

    static Map<String, AtomicInteger> map = Maps.newConcurrentMap();

    @TSchedule(period = 20)
    static void timer() {
        map.clear();
    }

    @TInject
    static TPacketListener listener = new TPacketListener() {
        @Override
        public boolean onSend(Player player, Packet packet) {
            if (packet.is("PacketPlayOutTabComplete") && !player.isOp()) {
                return packet.read("b", Suggestions.class).getList().stream().anyMatch(suggestion -> Bukkit.getPlayerExact(suggestion.getText()) != null);
            }
            if (packet.is("PacketPlayOutChat") && String.valueOf(packet.read("a")).contains("chat.type.advancement")) {
                return false;
            }
            if (packet.is("PacketPlayOutAdvancements")) {
                return false;
            }
            if (packet.is("PacketPlayOutWorldParticles") && Asm.HANDLE.readParticlePacket(packet.read("j")).equals("minecraft:damage_indicator")) {
//                return packet.read("g", Float.TYPE) == 0.2f && packet.read("h", Integer.TYPE) == 5f;
                return false;
            }
            return true;
        }

        @Override
        public boolean onReceive(Player player, Packet packet) {
            if (packet.is("PacketPlayInAutoRecipe") || packet.is("PacketPlayInRecipeDisplayed")) {
                return false;
            }
            return true;
        }
    };
}
