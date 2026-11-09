package shared_health.data_type;

import com.mojang.serialization.Codec;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.scores.PlayerTeam;
import shared_health.SharedHealth;
import shared_health.model.SharedHealthTeamData;

import java.util.HashMap;
import java.util.Map;

public class SharedHealthData extends SavedData {
    private final Map<String, SharedHealthTeamData> teamValues = new HashMap<>();

    public static final Codec<SharedHealthData> CODEC =
            Codec.unboundedMap(Codec.STRING, SharedHealthTeamData.CODEC)
                    .xmap(SharedHealthData::new, data -> data.teamValues);

    public static SharedHealthData get(MinecraftServer server) {
        return server.getDataStorage().computeIfAbsent(ModDataTypes.SHARED_HEALTH_DATA_TYPE);
    }

    public SharedHealthData() {}

    public SharedHealthData(Map<String, SharedHealthTeamData> teamValues) {
        this.teamValues.putAll(teamValues);
    }

    public SharedHealthTeamData getTeamData(String teamName) {
        ensureTeamExists(teamName);
        return teamValues.get(teamName);
    }

    public SharedHealthTeamData getTeamData(PlayerTeam team) {
        return getTeamData(team.getName());
    }

    public void setTeamData(String teamName, SharedHealthTeamData data) {
        if(!teamValues.containsKey(teamName)) return;
        teamValues.put(teamName, data);
        setDirty();
    }

    public void setTeamData(PlayerTeam team, SharedHealthTeamData data) {
        setTeamData(team.getName(), data);
    }

    public void setHealth(String teamName, float health) {
        ensureTeamExists(teamName);
        SharedHealthTeamData data = getTeamData(teamName);
        if(data == null) return;

        data.health = health;
        setDirty();
    }

    public void setHealth(PlayerTeam team, float health) {
        setHealth(team.getName(), health);
    }

    public void setFood(String teamName, int food) {
        ensureTeamExists(teamName);
        SharedHealthTeamData data = getTeamData(teamName);
        if(data == null) return;

        data.food = food;
        setDirty();
    }

    public void setFood(PlayerTeam team, int food) {
        setFood(team.getName(), food);
    }

    public void setSaturation(String teamName, float saturation) {
        ensureTeamExists(teamName);
        SharedHealthTeamData data = getTeamData(teamName);
        if(data == null) return;

        data.saturation = saturation;
        setDirty();
    }

    public void setSaturation(PlayerTeam team, float saturation) {
        setSaturation(team.getName(), saturation);
    }

    public void setEnabled(String teamName, boolean enabled) {
        if(!teamExists(teamName) && !enabled) return;
        ensureTeamExists(teamName);

        SharedHealthTeamData data = getTeamData(teamName);
        if(data == null) return;

        data.enabled = enabled;
        setDirty();
    }

    public void setEnabled(PlayerTeam team, boolean enabled) {
        setEnabled(team.getName(), enabled);
    }

    private void ensureTeamExists(String teamName) throws IllegalArgumentException {
        MinecraftServer server = SharedHealth.getServerInstance();
        PlayerTeam team = server.getScoreboard().getPlayerTeam(teamName);
        if(team == null) throw new IllegalArgumentException();
        if(teamValues.containsKey(teamName)) return;
        teamValues.put(teamName, new SharedHealthTeamData());
    }

    private void ensureTeamExists(PlayerTeam team) {
        ensureTeamExists(team.getName());
    }

    private boolean teamExists(String teamName) {
        return teamValues.containsKey(teamName);
    }

    private boolean teamExists(PlayerTeam team) {
        return teamExists(team.getName());
    }
}