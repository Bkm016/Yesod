package ink.ptms.yesod.module

import com.google.common.collect.Maps
import ink.ptms.yesod.Yesod
import io.izzel.taboolib.TabooLib
import io.izzel.taboolib.TabooLibAPI
import io.izzel.taboolib.module.command.TCommandHandler
import io.izzel.taboolib.module.inject.TListener
import io.izzel.taboolib.module.inject.TSchedule
import io.izzel.taboolib.module.locale.TLocale
import io.izzel.taboolib.util.Reflection
import net.minecraft.server.v1_14_R1.CommandList
import net.minecraft.server.v1_14_R1.CommandListenerWrapper
import net.minecraft.server.v1_14_R1.PacketPlayOutCollect
import net.minecraft.server.v1_14_R1.PacketPlayOutCommands
import org.bukkit.Bukkit
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.PluginCommand
import org.bukkit.command.SimpleCommandMap
import org.bukkit.command.TabCompleter
import org.bukkit.craftbukkit.v1_14_R1.CraftServer
import org.bukkit.craftbukkit.v1_14_R1.CraftWorld
import org.bukkit.craftbukkit.v1_14_R1.command.CraftCommandMap
import org.bukkit.craftbukkit.v1_14_R1.command.VanillaCommandWrapper
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerCommandPreprocessEvent
import org.bukkit.event.player.PlayerCommandSendEvent
import org.bukkit.plugin.Plugin
import org.spigotmc.SpigotConfig
import java.util.stream.Collectors

/**
 * @Author sky
 * @Since 2019-11-20 21:48
 */
@TListener
class ProtectCommand : Listener {

    @EventHandler
    fun e(e: PlayerCommandSendEvent) {
        if (!e.player.isOp) {
            e.commands.removeAll(Yesod.CONF.getStringList("block-command-name"))
            e.commands.removeAll(Yesod.CONF.getStringList("block-command-send"))
        }
    }

    @EventHandler
    fun e(e: PlayerCommandPreprocessEvent) {
        if (e.player.isOp) {
            return
        }
        val v = e.message.split(" ")[0].toLowerCase().substring(1)
        if (v.contains(":") || v in Yesod.CONF.getStringList("block-command-name")) {
            e.isCancelled = true
            e.player.sendMessage(SpigotConfig.unknownCommandMessage)
        }
    }

    @TSchedule
    fun e() {
        TCommandHandler.getCommandMap().commands.forEach { command ->
            if (Yesod.CONF.getStringList("block-command-path").any { name -> command.javaClass.name.startsWith(name) }) {
                if (command !is PluginCommand || !TabooLibAPI.isDependTabooLib(command.plugin)) {
                    command.permission = "*"
                }
            }
        }
    }
}