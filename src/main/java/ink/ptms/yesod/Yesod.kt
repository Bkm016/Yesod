package ink.ptms.yesod

import ink.ptms.yesod.module.Void
import io.izzel.taboolib.loader.Plugin
import io.izzel.taboolib.module.config.TConfig
import io.izzel.taboolib.module.inject.TInject
import org.bukkit.GameMode
import org.bukkit.entity.Entity
import org.bukkit.entity.Player
import org.bukkit.generator.ChunkGenerator

object Yesod : Plugin() {

    @TInject(migrate = true)
    lateinit var conf: TConfig
        private set

    val voidProtect: Boolean
        get() = conf.getBoolean("void-protect")

    val allowCraft: Boolean
        get() = conf.getBoolean("allow-craft")

    val blockInventory: List<String>
        get() = conf.getStringList("block-inventory")

    val blockInteract: List<String>
        get() = conf.getStringList("block-interact")

    val thornOverride: Boolean
        get() = conf.getBoolean("thorn-override")

    val blockFeatures: List<String>
        get() = conf.getStringList("block-features")

    val blockTeleport: List<String>
        get() = conf.getStringList("block-teleport")

    fun Entity.bypass(hard: Boolean = false): Boolean {
        return this !is Player || isOp && gameMode == GameMode.CREATIVE && (!hard || isSneaking)
    }

    override fun getDefaultWorldGenerator(worldName: String, id: String?): ChunkGenerator {
        return Void()
    }
}