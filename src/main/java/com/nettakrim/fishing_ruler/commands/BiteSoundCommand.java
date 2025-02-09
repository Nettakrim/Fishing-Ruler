package com.nettakrim.fishing_ruler.commands;

import com.mojang.brigadier.suggestion.SuggestionProvider;
import com.mojang.brigadier.tree.LiteralCommandNode;
import com.mojang.brigadier.tree.RootCommandNode;
import com.nettakrim.fishing_ruler.FishingRulerClient;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.command.CommandSource;
import net.minecraft.command.argument.IdentifierArgumentType;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class BiteSoundCommand {
    private static final SuggestionProvider<FabricClientCommandSource> sounds = (context, builder) -> CommandSource.suggestIdentifiers(context.getSource().getSoundIds(), builder);

    public static void register(RootCommandNode<FabricClientCommandSource> root) {
        LiteralCommandNode<FabricClientCommandSource> biteSoundNode = ClientCommandManager
        .literal("fishingruler:bite_sound")
        .then(
            ClientCommandManager.literal("set").then(
                ClientCommandManager.argument("sound", IdentifierArgumentType.identifier()).suggests(sounds)
                .executes(context -> {
                    setSound(context.getArgument("sound", Identifier.class));
                    FishingRulerClient.playBiteSound();
                    return 1;
                })
            )
        )
        .then(
            ClientCommandManager.literal("query")
            .executes(context -> {
                FishingRulerClient.sendMessage(Text.literal(String.valueOf(FishingRulerClient.biteSound)).setStyle(Style.EMPTY.withColor(0xAAAAAA)), false);
                FishingRulerClient.playBiteSound();
                return 1;
            })
        )
        .then(
            ClientCommandManager.literal("clear")
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
