package shared_health.mixin;

import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.scores.PlayerTeam;
import net.minecraft.world.scores.Scoreboard;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import shared_health.SharedHealth;
import shared_health.manager.SharedHealthManager;

@Mixin(Scoreboard.class)
public abstract class ScoreboardMixin {

    @Inject(
            method = "addPlayerToTeam",
            at = @At("TAIL")
    )
    private void onPlayerJoinTeam(String player, PlayerTeam team, CallbackInfoReturnable<Boolean> cir) {
        if(!cir.getReturnValue()) return;

        MinecraftServer server = SharedHealth.getServerInstance();
        if(server == null) return;
        ServerPlayer p = server.getPlayerList().getPlayerByName(player);
        if(p == null) return;
        SharedHealthManager.syncAll(p);
    }
}