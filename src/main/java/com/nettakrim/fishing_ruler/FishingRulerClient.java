package com.nettakrim.fishing_ruler;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.MutableText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.text.TextColor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.nettakrim.fishing_ruler.commands.FishingRulerCommands;

public class FishingRulerClient implements ClientModInitializer {
	public static final String MODID = "fishing_ruler";
	public static final Logger LOGGER = LoggerFactory.getLogger("Fishing Ruler");

	public static MinecraftClient client;
	private static int framesSinceUpdate = 8;
	private static double lastLength = 0;
	public static boolean allowSnapInfer = true;

	@Override
	public void onInitializeClient() {
		client = MinecraftClient.getInstance();

		FishingRulerCommands.initialize();

		ClientTickEvents.END_CLIENT_TICK.register(client -> update());
	}

	public static void update() {
		//throwing the line straight down can cause the snapped indicator to not show up
		if (allowSnapInfer) {
			if (framesSinceUpdate == 1 && lastLength > 31 && lastLength <= 32) {
				updateText(32.1D, false, State.DEFAULT);
			}
		} else {
			allowSnapInfer = true;
			framesSinceUpdate = 8;
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
			state = State.SNAPPED;
		} else if (length > 30) {
			state = State.VERY_NEAR_SNAP;
		} else if (length > 28) {
			state = State.NEAR_SNAP;
		} 
		text.setStyle(getStyle(state));

		if (updateLast && allowSnapInfer) {
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
		NEAR_DESPAWN,
		NEAR_SNAP,
		VERY_NEAR_SNAP,
		SNAPPED
	}

	public static Style getStyle(State state) {
		return Style.EMPTY.withColor(stateToColor[state.ordinal()]);
	}

	private static final TextColor[] stateToColor = new TextColor[] {
		TextColor.fromRgb(0xAAAAAA),  // DEFAULT
		TextColor.fromRgb(0xFFFFFF),  // ON_GROUND
		TextColor.fromRgb(0x8EFF80),  // IN_ENTITY
		TextColor.fromRgb(0x009C9C),  // IN_SMALL_WATER
		TextColor.fromRgb(0x53EDED),  // IN_OPEN_WATER
		TextColor.fromRgb(0x265694),  // NOT_EXPOSED
		TextColor.fromRgb(0x518AED),  // RAINED_ON
		TextColor.fromRgb(0x54fBA8),  // HAS_FISH
		TextColor.fromRgb(0xED691C),  // NEAR_DESPAWN
		TextColor.fromRgb(0xFFAA00),  // NEAR_SNAP
		TextColor.fromRgb(0xFF5555),  // VERY_NEAR_SNAP
		TextColor.fromRgb(0xDE2F2F)   // SNAPPED
	};
}
