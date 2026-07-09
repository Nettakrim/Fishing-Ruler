package com.nettakrim.fishing_ruler.mixin;

import com.nettakrim.fishing_ruler.FishingRulerClient;
import com.nettakrim.fishing_ruler.FishingRulerClient.State;
import net.minecraft.core.BlockPos;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.FishingHook;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.FluidState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(FishingHook.class)
public abstract class FishingBobberEntityMixin extends Projectile {
	//constructor needed for extend to work - extend needed for squaredDistanceTo method
	public FishingBobberEntityMixin(EntityType<? extends Projectile> entityType, Level world) {
		super(entityType, world);
	}

	@Shadow abstract public Player getPlayerOwner();
	@Shadow private Entity hookedIn;
	@Shadow abstract protected boolean calculateOpenWater(BlockPos pos);
	@Shadow private boolean biting;
	@Shadow private int life;

	@Unique private int haveFishCountdown;
	@Unique private int inWaterCountdown;
	@Unique private boolean isOpenWater;
	@Unique private boolean lastGrounded;

	@Inject(at = @At("HEAD"), method = "tick()V")
	private void tick(CallbackInfo info) {
		Player owner = getPlayerOwner();
		if (owner == FishingRulerClient.client.player) {
			FishingRulerClient.updateText(Math.sqrt(distanceToSqr(owner)), true, getState(), this);
		}
	}

	@Unique
	private State getState() {
		FishingRulerClient.State state = State.IN_AIR;

		Level world = level();

		BlockPos blockPos = this.blockPosition();
		FluidState fluidState = world.getFluidState(blockPos);
		if (fluidState.is(FluidTags.WATER)) {
			float f = fluidState.getHeight(world, blockPos);
			if (f > 0) {
				inWaterCountdown = 10;
				isOpenWater = calculateOpenWater(blockPos);
			}
		}

		if (inWaterCountdown > 0) {
			BlockPos up = blockPos.above();
			if (!world.canSeeSky(up) && !world.getFluidState(up).is(FluidTags.WATER)) {
				state = State.NOT_EXPOSED;
			} else if (isOpenWater) {
				if (world.isRainingAt(up)) {
					state = State.RAINED_ON;
				} else {
					state = State.IN_OPEN_WATER;
				}
			} else {
				state = State.IN_SMALL_WATER;
			}
			inWaterCountdown--;
		}

		// onGround seems to flicker false for a tick every second, it is easy to smooth
		boolean grounded = onGround();
		if (grounded || lastGrounded) {
			if (life >= 1100) {
				state = State.NEAR_DESPAWN;
			} else {
				state = State.ON_GROUND;
			}
		}
		lastGrounded = grounded;

		if (hookedIn != null) {
			state = State.IN_ENTITY;
		}

		if (biting) {
			if (haveFishCountdown == 0) {
				FishingRulerClient.playBiteSound();
			}
			haveFishCountdown = 20;
		}

		if (haveFishCountdown > 0) {
			haveFishCountdown--;
			state = State.HAS_FISH;
		}

		return state;
	}
}
