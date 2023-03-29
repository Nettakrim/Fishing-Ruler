package com.nettakrim.fishing_ruler;

import net.fabricmc.api.ClientModInitializer;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.MutableText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FishingRulerClient implements ClientModInitializer {
	public static final String MODID = "fishing_ruler";
	public static final Logger LOGGER = LoggerFactory.getLogger("Fishing Ruler");

	public static MinecraftClient client;

	@Override
	public void onInitializeClient() {
		client = MinecraftClient.getInstance();
	}

	public static void updateText(double length) {
		MutableText text;
		if (length > 32D) {
			text = Text.translatable(MODID+".snapped");
		} else {
			text = Text.translatable(MODID+".length", formatDecimalPlaces(length));
		}
		if (length > 30) {
			text.setStyle(Style.EMPTY.withColor(Formatting.RED));
		} else if (length > 28) {
			text.setStyle(Style.EMPTY.withColor(Formatting.GOLD));
		}
		client.player.sendMessage(text, true); 
	}

	private static String formatDecimalPlaces(double d) {
		return Double.toString(Math.floor(d*10)/10);
	}
}
