package ink.ptms.yesod.asm;

import io.izzel.taboolib.module.inject.TInject;

/**
 * @Author sky
 * @Since 2019-11-21 23:47
 */
public abstract class Asm {

    @TInject(asm = "ink.ptms.yesod.asm.AsmImpl")
    public static final Asm HANDLE = null;

    abstract public String readParticlePacket(Object j);

}
