package shared_health.manager;

import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.ChatType;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.OutgoingChatMessage;
import net.minecraft.network.chat.PlayerChatMessage;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.scores.PlayerTeam;
import shared_health.damage_type.ModDamageTypes;
import shared_health.data_type.SharedHealthData;
import shared_health.model.SharedHealthTeamData;

public class SharedHealthManager {
    public static void onTakeDamage(ServerPlayer player, DamageSource source) {
        if(source.is(ModDamageTypes.SHARED_DAMAGE)) return;
        if(player.getHealth() <= 0) return;

        ServerLevel level = player.level();
        PlayerTeam team = player.getTeam();

        if(team == null) return;

        SharedHealthData teamData = SharedHealthData.get(level.getServer());
        teamData.setHealth(team, player.getHealth());

        syncTeam(level, team, player);
    }

    public static void onHeal(ServerPlayer player) {
        PlayerTeam team = player.getTeam();
        ServerLevel level = player.level();

        if(team == null) return;

        SharedHealthData teamData = SharedHealthData.get(level.getServer());
        teamData.setHealth(team, player.getHealth());

        syncTeam(level, team, player);
    }

    public static void onDeath(ServerPlayer player) {
        PlayerTeam team = player.getTeam();
        ServerLevel level = player.level();
        if(team == null) return;

        for(String name : team.getPlayers()) {
            ServerPlayer p = level.getServer().getPlayerList().getPlayerByName(name);
            if(p == null) return;
            killPlayer(p);
            resetTeam(level.getServer(), team);
        }
    }

    public static void onFoodLevelChange(ServerPlayer player) {
        PlayerTeam team = player.getTeam();
        ServerLevel level = player.level();

        if(team == null) return;

        SharedHealthData teamData = SharedHealthData.get(level.getServer());
        teamData.setFood(team, player.getFoodData().getFoodLevel());

        syncTeam(level, team, player);
    }

    public static void onSaturationLevelChange(ServerPlayer player) {
        PlayerTeam team = player.getTeam();
        ServerLevel level = player.level();

        if(team == null) return;

        SharedHealthData teamData = SharedHealthData.get(level.getServer());
        teamData.setSaturation(team, player.getFoodData().getSaturationLevel());

        syncTeam(level, team, player);
    }

    private static void killPlayer(ServerPlayer player) {
        dealDamage(player, player.getHealth() + 1);
    }

    private static void syncHealth(ServerPlayer player, SharedHealthTeamData data) {
        float diff = data.health - player.getHealth();
        if(diff < 0) dealDamage(player, Math.abs(diff));
        else heal(player, diff);
    }

    private static void syncFood(ServerPlayer player, SharedHealthTeamData data) {
        player.getFoodData().setFoodLevel(data.food);
    }

    private static void syncSaturation(ServerPlayer player, SharedHealthTeamData data) {
        player.getFoodData().setSaturation(data.saturation);
    }

    public static void syncAll(ServerPlayer player) {
        PlayerTeam team = player.getTeam();
        if(team == null) return;
        SharedHealthTeamData teamData = SharedHealthData.get(player.level().getServer()).getTeamData(player.getTeam());
        if(teamData == null) return;
        syncHealth(player, teamData);
        syncFood(player, teamData);
        syncSaturation(player, teamData);
    }

    private static void resetTeam(MinecraftServer server, PlayerTeam team) {
        SharedHealthData.get(server).setTeamData(team, new SharedHealthTeamData());
    }

    private static void syncTeam(ServerLevel level, PlayerTeam team, ServerPlayer triggeredBy) {
        for(String name : team.getPlayers()) {
            ServerPlayer p = level.getServer().getPlayerList().getPlayerByName(name);
            if(p == null || triggeredBy.getName().getString().equals(name)) continue;
            syncAll(p);
        }
    }

    private static DamageSource getDamageSource(ServerLevel level) {
        return new DamageSource(
                level.registryAccess()
                        .lookupOrThrow(Registries.DAMAGE_TYPE)
                        .getOrThrow(ModDamageTypes.SHARED_DAMAGE));
    }

    private static void dealDamage(ServerPlayer player, float amount) {
        DamageSource source = getDamageSource(player.level());
        player.hurtServer(player.level(), source, amount);
    }

    private static void heal(ServerPlayer player, float amount) {
        player.setHealth(player.getHealth() + amount);
    }
}