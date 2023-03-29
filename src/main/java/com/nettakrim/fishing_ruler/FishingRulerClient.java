package com.nettakrim.fishing_ruler;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
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
	private static int framesSinceUpdate = 8;
	private static double lastLength = 0;

	@Override
	public void onInitializeClient() {
		client = MinecraftClient.getInstance();

		ClientTickEvents.END_CLIENT_TICK.register(client -> {update();});
	}

	public static void update() {
		//throwing the line straight down can cause the snapped indicator to not show up
		if (framesSinceUpdate == 1 && lastLength > 31 && lastLength <= 32) {
			updateText(32.1D, false, State.DEFAULT);
		}
		if (framesSinceUpdate < 8) {
			framesSinceUpdate++;
		}
	}

	public static void updateText(double length, boolean updateLast, State state) {
		MutableText text;
		if (length > 32) {
			text = Text.translatable(MODID+".snapped", formatDecimalPlaces(length));
			framesSinceUpdate = 8;
		} else {
			text = Text.translatable(MODID+".length", formatDecimalPlaces(length));
		}

		if (length > 32) {
			text.setStyle(Style.EMPTY.withColor(Formatting.DARK_RED));
		} else if (length > 30) {
			text.setStyle(Style.EMPTY.withColor(Formatting.RED));
		} else if (length > 28) {
			text.setStyle(Style.EMPTY.withColor(Formatting.GOLD));
		} else {
			switch (state) {
				case DEFAULT:
					text.setStyle(Style.EMPTY.withColor(Formatting.GRAY));
					break;
				case ON_GROUND:
					text.setStyle(Style.EMPTY.withColor(Formatting.WHITE));
					break;
				case IN_ENTITY:
					text.setStyle(Style.EMPTY.withColor(Formatting.GREEN));
					break;
				case IN_SMALL_WATER:
					text.setStyle(Style.EMPTY.withColor(Formatting.DARK_AQUA));
					break;
				case IN_OPEN_WATER:
					text.setStyle(Style.EMPTY.withColor(Formatting.AQUA));
					break;
				case NOT_EXPOSED:
					text.setStyle(Style.EMPTY.withColor(Formatting.DARK_AQUA));
					break;
				case RAINED_ON:
					text.setStyle(Style.EMPTY.withColor(Formatting.BLUE));
					break;
				case HAS_FISH:
					text.setStyle(Style.EMPTY.withColor(Formatting.GREEN));
					break;
				case NEAR_DESPAWN:
					text.setStyle(Style.EMPTY.withColor(Formatting.GOLD));
					break;
			}
		}

		if (updateLast) {
			framesSinceUpdate = 0;
			lastLength = length;
		}

		client.player.sendMessage(text, true); 
	}

	private static String formatDecimalPlaces(double d) {
		String s = Double.toString(Math.floor(d*10)/10);
		if (s.length() == 3) s = "0"+s;
		return s;
	}

	public enum State {
		DEFAULT,
		ON_GROUND,
		IN_ENTITY,
		IN_SMALL_WATER,
		IN_OPEN_WATER,
		NOT_EXPOSED,
		RAINED_ON,
		HAS_FISH,
		NEAR_DESPAWN
	}
}
