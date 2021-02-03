package ink.ptms.yesod

import ink.ptms.yesod.module.generator.Void
import io.izzel.taboolib.loader.Plugin
import io.izzel.taboolib.module.config.TConfig
import io.izzel.taboolib.module.inject.TInject
import org.bukkit.GameMode
import org.bukkit.entity.Entity
import org.bukkit.entity.Player
import org.bukkit.generator.ChunkGenerator

object Yesod : Plugin() {

    @TInject
    lateinit var conf: TConfig
        private set

    fun Entity.bypass(hard: Boolean = false): Boolean {
        return this !is Player || isOp && gameMode == GameMode.CREATIVE && (!hard || isSneaking)
    }

    override fun getDefaultWorldGenerator(worldName: String, id: String?): ChunkGenerator? {
        return Void()
    }
}