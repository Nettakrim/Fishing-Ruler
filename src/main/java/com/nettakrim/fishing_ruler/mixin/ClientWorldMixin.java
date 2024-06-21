package com.nettakrim.fishing_ruler.mixin;

import com.nettakrim.fishing_ruler.FishingRulerClient;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientWorld.class)
public class ClientWorldMixin {
    @Inject(method = "addEntity", at = @At("TAIL"))
    private void onEntitySpawn(Entity entity, CallbackInfo ci) {
        if (FishingRulerClient.expectingFish) {
            FishingRulerClient.onEntitySpawn(entity);
        }
    }
}
