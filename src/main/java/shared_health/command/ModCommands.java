package shared_health.command;

import com.mojang.brigadier.context.CommandContext;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;

public class ModCommands {
    public static void register() {
        CommandRegistrationCallback.EVENT.register(
                ((dispatcher, buildContext, selection) ->
                        SharedHealthCommands.register(dispatcher))
        );
    }

    public static void sendSuccessMessage(CommandContext<CommandSourceStack> context, String message, boolean broadcast) {
        context.getSource().sendSuccess(
                () -> Component.literal(message),
                broadcast
        );
    }

    public static void sendSuccessMessage(CommandContext<CommandSourceStack> context, String message) {
        sendSuccessMessage(context, message, true);
    }
}