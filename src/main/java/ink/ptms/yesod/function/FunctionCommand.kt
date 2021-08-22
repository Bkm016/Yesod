package ink.ptms.yesod.function

import ink.ptms.yesod.Yesod
import org.bukkit.command.PluginCommand
import org.bukkit.event.player.PlayerCommandPreprocessEvent
import org.bukkit.event.player.PlayerCommandSendEvent
import org.spigotmc.SpigotConfig
import taboolib.common.LifeCycle
import taboolib.common.io.taboolibId
import taboolib.common.platform.Awake
import taboolib.common.platform.PlatformFactory
import taboolib.common.platform.event.SubscribeEvent
import taboolib.platform.BukkitCommand

/**
 * @author sky
 * @since 2019-11-20 21:48
 */
object FunctionCommand {

    @Awake(LifeCycle.ACTIVE)
    fun e() {
        PlatformFactory.getAPI<BukkitCommand>().commandMap.commands.forEach { command ->
            if (Yesod.conf.getStringList("block-command-path").any { name -> command.javaClass.name.startsWith(name) }) {
                if (command !is PluginCommand || !command.javaClass.name.startsWith("io.izzel.$taboolibId")) {
                    command.permission = "*"
                }
            }
        }
    }

    @SubscribeEvent
    fun e(e: PlayerCommandSendEvent) {
        if (!e.player.isOp) {
            e.commands.removeAll(Yesod.conf.getStringList("block-command-name"))
            e.commands.removeAll(Yesod.conf.getStringList("block-command-send"))
            e.commands.removeIf { it.contains(":") }
        }
    }

    @SubscribeEvent
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
}