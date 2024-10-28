package com.nettakrim.fishing_ruler;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.text.*;

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

	public static boolean expectingFish;
	private static Entity expectingBobber;

	private static ItemEntity caughtItem;
	private static int caughtForTicks;


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
				updateText(32.1D, false, State.DEFAULT, null);
			}
		} else {
			allowSnapInfer = true;
			framesSinceUpdate = 8;
		}
		if (framesSinceUpdate < 8) {
			framesSinceUpdate++;
		}

		if (caughtItem != null) {
			if (caughtForTicks > 0) {
				caughtForTicks--;
			} else {
				caughtItem = null;
				return;
			}

			ItemStack itemStack = caughtItem.getStack();
			if (itemStack.isEmpty()) return;

			MutableText text = itemStack.getName().copy();
			if (itemStack.hasEnchantments()) {
				try {
					MutableText enchantmentText = MutableText.of(new LiteralTextContent(""));

					EnchantmentHelper.get(itemStack).forEach((enchantment, level) -> {
						enchantmentText.append(" ").append(enchantment.getName(level));
					});

					text.append(":");
					text.append(enchantmentText);
				} catch (Exception ignored) {}
			}

			sendMessage(text.setStyle(getStyle(State.HAS_FISH)), true);
		}
	}

	public static void updateText(double length, boolean updateLast, State state, Entity bobber) {
		if (caughtItem != null) return;

		if (state == State.HAS_FISH) {
			expectingFish = true;
			expectingBobber = bobber;
		} else {
			expectingFish = false;
			expectingBobber = null;
		}

		MutableText text;
		if (length > 32) {
			text = Text.translatable(MODID+".snapped", formatDecimalPlaces(length));
			framesSinceUpdate = 8;
		} else {
			text = Text.translatable(MODID+".length", formatDecimalPlaces(length));
		}

		if (length > 32) {
			state = State.SNAPPED;
		} else if (length > 30 && !expectingFish) {
			state = State.VERY_NEAR_SNAP;
		} else if (length > 28 && !expectingFish) {
			state = State.NEAR_SNAP;
		}
		text.setStyle(getStyle(state));

		if (updateLast && allowSnapInfer) {
			framesSinceUpdate = 0;
			lastLength = length;
		}

		sendMessage(text, true);
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

	public static void onEntitySpawn(Entity entity) {
		if (client.player == null) return;
		if (entity instanceof ItemEntity item) {
			if (entity.distanceTo(expectingBobber) < 4) {
				caughtItem = item;
				expectingFish = false;
				caughtForTicks = 10;
			}
		}
	}

	public static void sendMessage(Text text, boolean isActionBar) {
		if (client.player == null) {
			return;
		}
		client.player.sendMessage(text, isActionBar);
	}
}
