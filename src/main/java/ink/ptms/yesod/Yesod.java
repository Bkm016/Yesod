package ink.ptms.yesod;

import io.izzel.taboolib.loader.Plugin;
import io.izzel.taboolib.module.config.TConfig;
import io.izzel.taboolib.module.inject.TInject;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

@Plugin.Version(5.11)
public final class Yesod extends Plugin {

    @TInject
    public static final TConfig CONF = null;

    public static boolean isAllow0(Entity entity) {
        return entity instanceof Player && ((Player) entity).getGameMode() == GameMode.CREATIVE;
    }

    public static boolean isAllow1(Entity entity) {
        return entity instanceof Player && ((Player) entity).getGameMode() == GameMode.CREATIVE && ((Player) entity).isSneaking();
    }
}
