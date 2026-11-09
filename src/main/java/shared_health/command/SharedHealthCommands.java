package shared_health.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.TeamArgument;
import net.minecraft.server.permissions.Permissions;
import net.minecraft.world.scores.PlayerTeam;
import shared_health.data_type.SharedHealthData;
import shared_health.manager.SharedHealthManager;


public class SharedHealthCommands {
    public static void register(
            CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(
                Commands.literal("sharedhealth")
                        .requires(source -> source.permissions().hasPermission(Permissions.COMMANDS_ADMIN))
                        .then(Commands.literal("team"))
                            .then(Commands.argument("team", TeamArgument.team())
                                .then(Commands.literal("enable")
                                    .executes(SharedHealthCommands::enableTeam)))

                        .then(Commands.literal("team"))
                            .then(Commands.argument("team", TeamArgument.team())
                                .then(Commands.literal("disable")
                                    .executes(SharedHealthCommands::disableTeam)))

                        .then(Commands.literal("team"))
                            .then(Commands.argument("team", TeamArgument.team())
                                .then(Commands.literal("toggle")
                                    .executes(SharedHealthCommands::toggleTeam)))

                        .then(Commands.literal("team"))
                            .then(Commands.argument("team", TeamArgument.team())
                                .executes(SharedHealthCommands::sendStatusMessage))

                        .then(Commands.literal("player")
                            .then(Commands.literal("unmark")
                                .then(Commands.argument("player", StringArgumentType.word())
                                    .executes(SharedHealthCommands::unmarkPlayer))))
        );
    }

    private static int setTeamEnabled(CommandContext<CommandSourceStack> context, boolean enabled) throws CommandSyntaxException {
        SharedHealthData data = SharedHealthData.get(context.getSource().getServer());
        PlayerTeam team = TeamArgument.getTeam(context, "team");
        data.setEnabled(team, enabled);
        sendSuccessMessage(context, team, enabled);
        return 1;
    }

    private static int enableTeam(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        int res = setTeamEnabled(context, true);
        SharedHealthManager.syncTeam(context.getSource().getLevel(), TeamArgument.getTeam(context, "team"), null);
        return res;
    }

    private static int disableTeam(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        return setTeamEnabled(context, false);
    }

    private static int toggleTeam(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        SharedHealthData data = SharedHealthData.get(context.getSource().getServer());
        PlayerTeam team = TeamArgument.getTeam(context, "team");
        return setTeamEnabled(context, !data.getTeamData(team).enabled);
    }

    private static int unmarkPlayer(CommandContext<CommandSourceStack> context) {
        SharedHealthData data = SharedHealthData.get(context.getSource().getServer());
        String playerName = StringArgumentType.getString(context, "player");
        data.unmarkPlayer(playerName);
        ModCommands.sendSuccessMessage(context, "Successfully unmarked player: " + playerName);
        return 1;
    }

    private static void sendSuccessMessage(CommandContext<CommandSourceStack> context, PlayerTeam team, boolean enabled) {
        ModCommands.sendSuccessMessage(context, "Health sharing successfully " + (enabled ? "enabled" : "disabled") + " for team: " + team.getName());
    }

    private static int sendStatusMessage(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        SharedHealthData data = SharedHealthData.get(context.getSource().getServer());
        PlayerTeam team = TeamArgument.getTeam(context, "team");
        boolean enabled = data.getTeamData(team).enabled;

        ModCommands.sendSuccessMessage(
                context,
                "Health sharing is currently " + (enabled ? "enabled" : "disabled") + " for team: " + team.getName(),
                false);
        return 1;
    }
}