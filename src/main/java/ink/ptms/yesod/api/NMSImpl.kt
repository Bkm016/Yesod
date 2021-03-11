package ink.ptms.yesod.api

import net.minecraft.server.v1_16_R1.ParticleParam

/**
 * @author sky
 * @since 2019-11-21 23:48
 */
class NMSImpl : NMS() {

    override fun readParticlePacket(j: Any): String {
        return (j as ParticleParam).a()
    }
}