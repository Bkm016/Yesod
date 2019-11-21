package ink.ptms.yesod.asm;

import net.minecraft.server.v1_14_R1.ParticleParam;

/**
 * @Author sky
 * @Since 2019-11-21 23:48
 */
public class AsmImpl extends Asm {

    @Override
    public String readParticlePacket(Object j) {
        return ((ParticleParam) j).a();
    }
}
