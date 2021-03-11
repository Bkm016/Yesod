package ink.ptms.yesod.api

import io.izzel.taboolib.module.inject.TInject

/**
 * @author sky
 * @since 2019-11-21 23:47
 */
abstract class NMS {

    abstract fun readParticlePacket(j: Any): String

    companion object {

        @TInject(asm = "ink.ptms.yesod.api.NMSImpl")
        lateinit var HANDLE: NMS
    }
}