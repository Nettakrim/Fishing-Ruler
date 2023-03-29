package com.nettakrim.fishing_ruler.mixin;

import com.nettakrim.fishing_ruler.FishingRulerClient;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.FishingBobberEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
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

	@Inject(at = @At("HEAD"), method = "tick()V")
	private void tick(CallbackInfo info) {
		PlayerEntity owner = getPlayerOwner();
		if (owner == (PlayerEntity)FishingRulerClient.client.player) {
			FishingRulerClient.updateText(Math.sqrt(squaredDistanceTo(owner)));
		}
	}
}
