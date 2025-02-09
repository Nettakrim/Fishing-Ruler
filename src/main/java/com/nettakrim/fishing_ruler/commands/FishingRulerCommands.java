package com.nettakrim.fishing_ruler.commands;

import com.mojang.brigadier.tree.RootCommandNode;

import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;

public class FishingRulerCommands {
    public static void initialize() {
        ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) -> {
            RootCommandNode<FabricClientCommandSource> root = dispatcher.getRoot();

            HelpCommand.register(root);
            BiteSoundCommand.register(root);
        });
    }
}
