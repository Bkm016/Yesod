package ink.ptms.yesod.api;

import io.izzel.taboolib.module.inject.TInject;

/**
 * @Author sky
 * @Since 2019-11-21 23:47
 */
public abstract class NMS {

    @TInject(asm = "ink.ptms.yesod.asm.NMSImpl")
    public static final NMS HANDLE = null;

    abstract public String readParticlePacket(Object j);

}
