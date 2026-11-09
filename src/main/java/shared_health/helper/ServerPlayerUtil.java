package shared_health.helper;

import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import shared_health.SharedHealth;

public class ServerPlayerUtil {
    public static boolean isDead(ServerPlayer player) {
        return  player.isDeadOrDying() || player.getHealth() <= 0;
    }

    public static ServerPlayer getPlayerByName(String playerName) {
        MinecraftServer server = SharedHealth.getServerInstance();
        return server.getPlayerList().getPlayerByName(playerName);
    }
}
