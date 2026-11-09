package shared_health.data_type;

import net.minecraft.resources.Identifier;
import net.minecraft.world.level.saveddata.SavedDataType;
import shared_health.SharedHealth;

public class ModDataTypes {
    public static final SavedDataType<SharedHealthData> SHARED_HEALTH_DATA_TYPE =
            new SavedDataType<>(
                    SharedHealth.id("shared_health"),
                    SharedHealthData::new,
                    SharedHealthData.CODEC,
                    null
            );
}