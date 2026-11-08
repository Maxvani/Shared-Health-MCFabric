package shared_health.model;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public class SharedHealthTeamData {

    public static final Codec<SharedHealthTeamData> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                    Codec.FLOAT.fieldOf("health").forGetter(data -> data.health),
                    Codec.INT.fieldOf("food").forGetter(data -> data.food),
                    Codec.FLOAT.fieldOf("saturation").forGetter(data -> data.saturation)
            ).apply(instance, SharedHealthTeamData::new)
    );

    public float health = 20.0f;
    public int food = 20;
    public float saturation = 20.0f;

    public SharedHealthTeamData() {

    }

    public SharedHealthTeamData(float health, int food, float saturation) {
        this.health = health;
        this.food = food;
        this.saturation = saturation;
    }
}
