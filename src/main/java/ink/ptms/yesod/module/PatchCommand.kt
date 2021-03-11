package ink.ptms.yesod.module

import ink.ptms.yesod.Yesod
import io.izzel.taboolib.TabooLibAPI
import io.izzel.taboolib.module.command.TCommandHandler
import io.izzel.taboolib.module.inject.TListener
import io.izzel.taboolib.module.inject.TSchedule
import org.bukkit.command.PluginCommand
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerCommandPreprocessEvent
import org.bukkit.event.player.PlayerCommandSendEvent
import org.spigotmc.SpigotConfig

/**
 * @author sky
 * @since 2019-11-20 21:48
 */
@TListener
class PatchCommand : Listener {

    @EventHandler
    fun e(e: PlayerCommandSendEvent) {
        if (!e.player.isOp) {
            e.commands.removeAll(Yesod.conf.getStringList("block-command-name"))
            e.commands.removeAll(Yesod.conf.getStringList("block-command-send"))
            e.commands.removeIf { it.contains(":") }
        }
    }

    @EventHandler
    fun e(e: PlayerCommandPreprocessEvent) {
        if (e.player.isOp) {
            return
        }
        val v = e.message.split(" ")[0].toLowerCase().substring(1)
        if (v.contains(":") || v in Yesod.conf.getStringList("block-command-name")) {
            e.isCancelled = true
            e.player.sendMessage(SpigotConfig.unknownCommandMessage)
        }
    }

    @TSchedule
    fun e() {
        TCommandHandler.getCommandMap().commands.forEach { command ->
            if (Yesod.conf.getStringList("block-command-path").any { name -> command.javaClass.name.startsWith(name) }) {
                if (command !is PluginCommand || !TabooLibAPI.isDependTabooLib(command.plugin)) {
                    command.permission = "*"
                }
            }
        }
    }
}