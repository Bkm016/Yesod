package ink.ptms.yesod.api;

import net.minecraft.server.v1_16_R1.ParticleParam;

/**
 * @Author sky
 * @Since 2019-11-21 23:48
 */
public class NMSImpl extends NMS {

    @Override
    public String readParticlePacket(Object j) {
        return ((ParticleParam) j).a();
    }
}
