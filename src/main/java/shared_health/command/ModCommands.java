package shared_health.command;

import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;

public class ModCommands {
    public static void register() {
        CommandRegistrationCallback.EVENT.register(
                ((dispatcher, buildContext, selection) ->
                        SharedHealthCommands.register(dispatcher))
        );
    }
}