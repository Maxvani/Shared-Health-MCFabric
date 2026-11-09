package shared_health.mixin;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.CommonListenerCookie;
import net.minecraft.server.players.PlayerList;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import shared_health.manager.SharedHealthManager;

@Mixin(PlayerList.class)
public abstract class PlayerListMixin {
    @Inject(
            method = "placeNewPlayer",
            at = @At("TAIL")
    )
    private void onPlayerJoin(
            net.minecraft.network.Connection connection, ServerPlayer player, CommonListenerCookie cookie, CallbackInfo ci
    )
    {
        SharedHealthManager.syncPlayer(player);
    }
}