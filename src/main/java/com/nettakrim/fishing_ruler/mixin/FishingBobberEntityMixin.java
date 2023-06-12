package com.nettakrim.fishing_ruler.mixin;

import com.nettakrim.fishing_ruler.FishingRulerClient;
import com.nettakrim.fishing_ruler.FishingRulerClient.State;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.FishingBobberEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.registry.tag.FluidTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(FishingBobberEntity.class)
public abstract class FishingBobberEntityMixin extends ProjectileEntity {
	//constructor needed for extend to work - extend needed for squaredDistanceTo method
	public FishingBobberEntityMixin(EntityType<? extends ProjectileEntity> entityType, World world) {
		super(entityType, world);
	}

	@Shadow abstract public PlayerEntity getPlayerOwner();
	@Shadow private Entity hookedEntity;
	@Shadow abstract protected boolean isOpenOrWaterAround(BlockPos pos);
	@Shadow private boolean caughtFish;
	@Shadow private int removalTimer;

	private int haveFishCountdown;
	private int inWaterCountdown;
	private boolean isOpenWater;

	@Inject(at = @At("HEAD"), method = "tick()V")
	private void tick(CallbackInfo info) {
		PlayerEntity owner = getPlayerOwner();
		if (owner == FishingRulerClient.client.player) {
			FishingRulerClient.updateText(Math.sqrt(squaredDistanceTo(owner)), true, getState());
		}
	}

	private State getState() {
		FishingRulerClient.State state = State.DEFAULT;

		World world = getWorld();

		BlockPos blockPos = this.getBlockPos();
		FluidState fluidState = world.getFluidState(blockPos);
		if (fluidState.isIn(FluidTags.WATER)) {
			float f = fluidState.getHeight(world, blockPos);
			if (f > 0) {
				inWaterCountdown = 10;
				isOpenWater = isOpenOrWaterAround(blockPos);
			}
		}

		if (inWaterCountdown > 0) {
			BlockPos up = blockPos.up();
			if (!world.isSkyVisible(up) && !world.getFluidState(up).isIn(FluidTags.WATER)) {
				state = State.NOT_EXPOSED;
			} else if (isOpenWater) {
				if (world.hasRain(up)) {
					state = State.RAINED_ON;
				} else {
					state = State.IN_OPEN_WATER;
				}
			} else {
				state = State.IN_SMALL_WATER;
			}
			inWaterCountdown--;
		}

		if (isOnGround()) {
			if (removalTimer >= 1100) {
				state = State.NEAR_DESPAWN;
			} else {
				state = State.ON_GROUND;
			}
		}

		if (hookedEntity != null) {
			state = State.IN_ENTITY;
		}

		if (caughtFish) {
			haveFishCountdown = 20;
		}

		if (haveFishCountdown > 0) {
			haveFishCountdown--;
			state = State.HAS_FISH;
		}

		return state;
	}
}
