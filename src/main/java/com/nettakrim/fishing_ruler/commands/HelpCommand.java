package com.nettakrim.fishing_ruler.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.tree.LiteralCommandNode;
import com.mojang.brigadier.tree.RootCommandNode;
import com.nettakrim.fishing_ruler.FishingRulerClient;
import com.nettakrim.fishing_ruler.FishingRulerClient.State;

import net.fabricmc.fabric.api.client.command.v2.ClientCommands;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

public class HelpCommand implements Command<FabricClientCommandSource> {
	public static void register(RootCommandNode<FabricClientCommandSource> root) {
		LiteralCommandNode<FabricClientCommandSource> helpNode = ClientCommands
				.literal("fishingruler:help")
				.executes(new HelpCommand())
				.build();

		root.addChild(helpNode);
	}

	@Override
	public int run(CommandContext<FabricClientCommandSource> context) throws CommandSyntaxException {
		Component text = getColoredText("length", State.ON_GROUND)

			.append(getColoredText("air", State.IN_AIR))
			.append(getColoredText("ground", State.ON_GROUND))
			.append(getColoredText("entity", State.IN_ENTITY))
		
			.append(getColoredText("small_water", State.IN_SMALL_WATER))
			.append(getColoredText("large_water", State.IN_OPEN_WATER))
			.append(getColoredText("not_exposed", State.NOT_EXPOSED))
			.append(getColoredText("in_rain", State.RAINED_ON))
			.append(getColoredText("fish_on_hook", State.HAS_FISH))
		
			.append(getColoredText("length_amber", State.NEAR_SNAP))
			.append(getColoredText("length_red", State.VERY_NEAR_SNAP))
			.append(getColoredText("snapped", State.SNAPPED))
			.append(getColoredText("despawn", State.NEAR_DESPAWN));
		
		FishingRulerClient.sendMessage(text, false);
        return 1;
	}

	private MutableComponent getColoredText(String key, State state) {
		return Component.translatable(FishingRulerClient.MODID+".help."+key).setStyle(FishingRulerClient.getStyle(state));
	}
}
