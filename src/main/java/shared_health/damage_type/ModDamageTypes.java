package shared_health.damage_type;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.damagesource.DamageType;
import shared_health.SharedHealth;

public final class ModDamageTypes {
    public static final ResourceKey<DamageType> SHARED_DAMAGE =
            ResourceKey.create(
                    Registries.DAMAGE_TYPE,
                    SharedHealth.id("shared_damage"));
}