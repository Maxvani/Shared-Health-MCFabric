package shared_health.mixin;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import shared_health.manager.SharedHealthManager;

@Mixin(ServerPlayer.class)
public abstract class ServerPlayerMixin {

    @Unique
    private int lastFoodLevel = -1;

    @Unique
    private float lastSaturationLevel = -1f;

    @Inject(
            method = "hurtServer",
            at = @At("RETURN")
    )
    private void onDamage(ServerLevel level, DamageSource source, float damage, CallbackInfoReturnable<Boolean> cir) {
        SharedHealthManager.onTakeDamage((ServerPlayer)(Object)this, source);
    }

    @Inject(
            method = "die",
            at = @At("RETURN")
    )
    private void onDeath(DamageSource source, CallbackInfo ci) {
        SharedHealthManager.onDeath((ServerPlayer)(Object)this);
    }

    @Inject(
            method = "tick",
            at = @At("TAIL")
    )
    private void onTick(CallbackInfo ci) {
        ServerPlayer player = (ServerPlayer)(Object)this;
        int foodLevel = player.getFoodData().getFoodLevel();
        float saturationLevel = player.getFoodData().getSaturationLevel();

        if(foodLevel != -1 && foodLevel != lastFoodLevel) {
            SharedHealthManager.onFoodLevelChange(player);
        }
        if(saturationLevel != -1 && saturationLevel != lastSaturationLevel) {
            SharedHealthManager.onSaturationLevelChange(player);
        }

        lastFoodLevel = foodLevel;
        lastSaturationLevel = saturationLevel;
    }
}