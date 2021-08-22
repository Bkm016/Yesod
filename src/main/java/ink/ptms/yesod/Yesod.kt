package ink.ptms.yesod

import org.bukkit.GameMode
import org.bukkit.entity.Entity
import org.bukkit.entity.Player
import org.bukkit.generator.ChunkGenerator
import taboolib.common.platform.Plugin
import taboolib.module.configuration.Config
import taboolib.module.configuration.ConfigNode
import taboolib.module.configuration.SecuredFile
import taboolib.platform.BukkitWorldGenerator

object Yesod : Plugin(), BukkitWorldGenerator {

    @Config(migrate = true)
    lateinit var conf: SecuredFile
        private set

    @ConfigNode("void-protect")
    var voidProtect = false
        private set

    @ConfigNode("allow-craft")
    var allowCraft = false
        private set

    @ConfigNode("allow-craft-display")
    var allowCraftDisplay = false
        private set

    @ConfigNode("block-inventory")
    lateinit var blockInventory: List<String>
        private set

    @ConfigNode("block-interact")
    lateinit var blockInteract: List<String>
        private set

    @ConfigNode("thorn-override")
    var thornOverride = false
        private set

    @ConfigNode("block-features")
    lateinit var blockFeatures: List<String>
        private set

    @ConfigNode("block-teleport")
    lateinit var blockTeleport: List<String>
        private set

    fun Entity.bypass(hard: Boolean = false): Boolean {
        return this !is Player || isOp && gameMode == GameMode.CREATIVE && (!hard || isSneaking)
    }

    override fun getDefaultWorldGenerator(worldName: String, name: String?): ChunkGenerator {
        return YesodGenerator()
    }
}