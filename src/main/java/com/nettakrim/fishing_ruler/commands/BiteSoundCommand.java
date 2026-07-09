package com.nettakrim.fishing_ruler.commands;

import com.mojang.brigadier.suggestion.SuggestionProvider;
import com.mojang.brigadier.tree.LiteralCommandNode;
import com.mojang.brigadier.tree.RootCommandNode;
import com.nettakrim.fishing_ruler.FishingRulerClient;
import net.fabricmc.fabric.api.client.command.v2.ClientCommands;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.commands.arguments.IdentifierArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.Identifier;

public class BiteSoundCommand {
    private static final SuggestionProvider<FabricClientCommandSource> sounds = (context, builder) -> SharedSuggestionProvider.suggestResource(context.getSource().getAvailableSounds(), builder);

    public static void register(RootCommandNode<FabricClientCommandSource> root) {
        LiteralCommandNode<FabricClientCommandSource> biteSoundNode = ClientCommands
        .literal("fishingruler:bite_sound")
        .then(
            ClientCommands.literal("set").then(
                ClientCommands.argument("sound", IdentifierArgument.id()).suggests(sounds)
                .executes(context -> {
                    setSound(context.getArgument("sound", Identifier.class));
                    FishingRulerClient.playBiteSound();
                    return 1;
                })
            )
        )
        .then(
            ClientCommands.literal("query")
            .executes(context -> {
                FishingRulerClient.sendMessage(Component.literal(String.valueOf(FishingRulerClient.biteSound)).setStyle(Style.EMPTY.withColor(0xAAAAAA)), false);
                FishingRulerClient.playBiteSound();
                return 1;
            })
        )
        .then(
            ClientCommands.literal("clear")
            .executes(context -> {
                setSound(null);
                return 1;
            })
        )
        .build();

        root.addChild(biteSoundNode);
    }

    private static void setSound(Identifier to) {
        FishingRulerClient.biteSound = to;
        FishingRulerClient.data.save();
    }
}
