package shared_health.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.TeamArgument;
import net.minecraft.server.permissions.Permissions;
import net.minecraft.world.scores.PlayerTeam;
import shared_health.data_type.SharedHealthData;

public class SharedHealthCommands {
    public static void register(
            CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(
                Commands.literal("sharedhealth")
                        .requires(source -> source.permissions().hasPermission(Permissions.COMMANDS_ADMIN))

                        .then(Commands.argument("team", TeamArgument.team())
                                .then(Commands.literal("enable")
                                        .executes(SharedHealthCommands::enableTeam)))

                        .then(Commands.argument("team", TeamArgument.team())
                                .then(Commands.literal("disable")
                                        .executes(SharedHealthCommands::disableTeam)))

                        .then(Commands.argument("team", TeamArgument.team())
                                .executes(SharedHealthCommands::sendStatusMessage))
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
        return setTeamEnabled(context, true);
    }

    private static int disableTeam(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        return setTeamEnabled(context, false);
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