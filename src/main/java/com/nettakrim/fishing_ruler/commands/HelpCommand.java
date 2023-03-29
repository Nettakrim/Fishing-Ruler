package com.nettakrim.fishing_ruler.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.nettakrim.fishing_ruler.FishingRulerClient;

import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.text.MutableText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

public class HelpCommand implements Command<FabricClientCommandSource> {
	@Override
	public int run(CommandContext<FabricClientCommandSource> context) throws CommandSyntaxException {
		Text text = getColoredText("length", Formatting.WHITE)

			.append(getColoredText("air", Formatting.GRAY))
			.append(getColoredText("ground", Formatting.WHITE))
			.append(getColoredText("entity", Formatting.GREEN))
		
			.append(getColoredText("small_water", Formatting.DARK_AQUA))
			.append(getColoredText("large_water", Formatting.AQUA))
			.append(getColoredText("not_exposed", Formatting.DARK_AQUA))
			.append(getColoredText("in_rain", Formatting.BLUE))
			.append(getColoredText("fish_on_hook", Formatting.GREEN))
		
			.append(getColoredText("length_amber", Formatting.GOLD))
			.append(getColoredText("length_red", Formatting.RED))
			.append(getColoredText("snapped", Formatting.DARK_RED))
			.append(getColoredText("despawn", Formatting.GOLD));
		
		FishingRulerClient.client.player.sendMessage(text);
        return 1;
	}

	private MutableText getColoredText(String key, Formatting color) {
		return Text.translatable(FishingRulerClient.MODID+".help."+key).setStyle(Style.EMPTY.withColor(color));
	}
}
