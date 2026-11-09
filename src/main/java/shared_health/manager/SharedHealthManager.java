package shared_health.manager;

import net.minecraft.core.registries.Registries;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.scores.PlayerTeam;
import shared_health.SharedHealth;
import shared_health.damage_type.ModDamageTypes;
import shared_health.data_type.SharedHealthData;
import shared_health.data_type.SharedHealthTeamData;
import shared_health.helper.ServerPlayerUtil;

public class SharedHealthManager {
    public static void onTakeDamage(ServerPlayer player, DamageSource source) {
        if(source.is(ModDamageTypes.SHARED_DAMAGE)) return;
        if(player.getHealth() <= 0 || player.isDeadOrDying()) return;

        ServerLevel level = player.level();
        PlayerTeam team = player.getTeam();

        if(!shouldSync(team)) return;

        SharedHealthData teamData = SharedHealthData.get(level.getServer());
        teamData.setHealth(team, player.getHealth());

        syncTeam(level, team, player);
    }

    public static void onHeal(ServerPlayer player) {
        PlayerTeam team = player.getTeam();
        ServerLevel level = player.level();

        if(!shouldSync(team)) return;

        SharedHealthData teamData = SharedHealthData.get(level.getServer());
        teamData.setHealth(team, player.getHealth());

        syncTeam(level, team, player);
    }

    public static void onDeath(ServerPlayer player, DamageSource source) {
        if(source.is(ModDamageTypes.SHARED_DAMAGE)) return;
        PlayerTeam team = player.getTeam();
        ServerLevel level = player.level();
        if(!shouldSync(team)) return;

        killTeam(team, player, level);
    }

    public static void onFoodLevelChange(ServerPlayer player) {
        PlayerTeam team = player.getTeam();
        ServerLevel level = player.level();

        if(!shouldSync(team)) return;

        SharedHealthData teamData = SharedHealthData.get(level.getServer());
        teamData.setFood(team, player.getFoodData().getFoodLevel());

        syncTeam(level, team, player);
    }

    public static void onSaturationLevelChange(ServerPlayer player) {
        PlayerTeam team = player.getTeam();
        ServerLevel level = player.level();

        if(!shouldSync(team)) return;

        SharedHealthData teamData = SharedHealthData.get(level.getServer());
        teamData.setSaturation(team, player.getFoodData().getSaturationLevel());

        syncTeam(level, team, player);
    }

    public static void syncTeam(ServerLevel level, PlayerTeam team, ServerPlayer triggeredBy) {
        for(String name : team.getPlayers()) {
            ServerPlayer p = level.getServer().getPlayerList().getPlayerByName(name);
            if(p == null || (triggeredBy != null && triggeredBy.getName().getString().equals(name))) continue;
            syncPlayer(p);
        }
    }

    public static void killIfMarked(ServerPlayer player) {
        SharedHealthData data = SharedHealthData.get(SharedHealth.getServerInstance());
        PlayerTeam team = player.getTeam();
        if(!data.playerIsMarked(player)) return;
        if(team == null) {
            unmarkPlayer(player);
            return;
        }

        killPlayer(player);
        if(ServerPlayerUtil.isDead(player)) {
            unmarkPlayer(player);
        }
    }

    private static void killPlayer(ServerPlayer player) {
        dealDamage(player, player.getHealth() + 1);
    }

    private static void killTeam(PlayerTeam team, ServerPlayer triggeredBy, ServerLevel level) {
        for(String name : team.getPlayers()) {
            if(triggeredBy.getName().getString().equals(name)) continue;

            ServerPlayer p = ServerPlayerUtil.getPlayerByName(name);
            if(p == null) {
               markPlayer(name);
               continue;
            }

            killPlayer(p);
        }
        resetTeam(level.getServer(), team);
    }

    private static void markPlayer(String playerName) {
        SharedHealthData data = SharedHealthData.get(SharedHealth.getServerInstance());
        data.markPlayer(playerName);
    }

    private static void unmarkPlayer(ServerPlayer player) {
        SharedHealthData data = SharedHealthData.get(SharedHealth.getServerInstance());
        data.unmarkPlayer(player);
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

    public static void syncPlayer(ServerPlayer player) {
        PlayerTeam team = player.getTeam();
        if(team == null || player.isDeadOrDying() || player.getHealth() <= 0) return;

        SharedHealthTeamData teamData = SharedHealthData.get(player.level().getServer()).getTeamData(player.getTeam());
        if(teamData == null) return;

        syncHealth(player, teamData);
        syncFood(player, teamData);
        syncSaturation(player, teamData);
    }

    public static void resetPlayer(ServerPlayer player) {
        SharedHealthData data = SharedHealthData.get(SharedHealth.getServerInstance());
        data.unmarkPlayer(player);
    }

    private static void resetTeam(MinecraftServer server, PlayerTeam team) {
        SharedHealthData.get(server).setTeamData(team, new SharedHealthTeamData());
    }

    private static DamageSource getDamageSource(ServerLevel level) {
        return new DamageSource(
                level.registryAccess()
                        .lookupOrThrow(Registries.DAMAGE_TYPE)
                        .getOrThrow(ModDamageTypes.SHARED_DAMAGE),
                (Entity)null);
    }

    private static void dealDamage(ServerPlayer player, float amount) {
        if(player.isDeadOrDying()) return;
        DamageSource source = getDamageSource(player.level());
        player.hurtServer(player.level(), source, amount);
    }

    private static void heal(ServerPlayer player, float amount) {
        player.setHealth(player.getHealth() + amount);
    }

    private static boolean shouldSync(PlayerTeam team) {
        if(team == null) return false;
        SharedHealthData data = SharedHealthData.get(SharedHealth.getServerInstance());
        return data.getTeamData(team).enabled;
    }
}