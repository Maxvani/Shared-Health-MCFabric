package shared_health.mixin;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.scores.PlayerTeam;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import shared_health.damage_type.ModDamageTypes;
import shared_health.manager.SharedHealthManager;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin {
    @Inject(
            method = "heal",
            at = @At("RETURN")
    )
    private void onHeal(float heal, CallbackInfo ci) {
        LivingEntity player = (LivingEntity)(Object)this;
        if(!(player instanceof ServerPlayer)) return;

        SharedHealthManager.onHeal((ServerPlayer) player);
    }

    @Inject(
            method = "knockback(DDDLnet/minecraft/world/damagesource/DamageSource;F)V",
            at = @At("HEAD"),
            cancellable = true
    )
    private void preventKnockbackOnSharedDamage(double power, double xd, double zd, DamageSource source, float damage, CallbackInfo ci) {
        if(source != null && source.is(ModDamageTypes.SHARED_DAMAGE)) {
            ci.cancel();
        }
    }

    @Inject(
            method = "knockback(DDDLnet/minecraft/world/damagesource/DamageSource;FZ)V",
            at = @At("HEAD"),
            cancellable = true
    )
    private void preventKnockbackOnSharedDamage(double power, double xd, double zd, DamageSource source, float damage, boolean comesFromEffect, CallbackInfo ci) {
        preventKnockbackOnSharedDamage(power, xd, zd, source, damage, ci);
    }
}