package shared_health.mixin;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.scores.PlayerTeam;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import shared_health.manager.SharedHealthManager;

@Mixin(LivingEntity.class)
public class LivingEntityMixin {
    @Inject(
            method = "heal",
            at = @At("RETURN")
    )
    private void onHeal(float heal, CallbackInfo ci) {
        LivingEntity player = (LivingEntity)(Object)this;
        if(!(player instanceof ServerPlayer)) return;

        SharedHealthManager.onHeal((ServerPlayer) player);
    }
}
