package com.nulltronprime.createmorefluidtanks.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.nulltronprime.createmorefluidtanks.blockEntity.MaterialPipeFluidTransportBehaviour;
import com.simibubi.create.content.fluids.FluidNetwork;
import com.simibubi.create.content.fluids.FluidTransportBehaviour;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;

import net.createmod.catnip.math.BlockFace;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;

@Mixin(value = FluidNetwork.class, remap = false)
public class FluidNetworkMixin {

    @Shadow
    private Level world;

    @Shadow
    private BlockFace start;

    @Shadow
    private int pauseBeforePropagation;

    @ModifyArg(method = "tick", at = @At(value = "INVOKE", target = "Ljava/lang/Math;max(FF)F"), index = 1, remap = false)
    private float correctTransferSpeed(float b) {
        BlockPos startPos = start.getPos();
        if (startPos == null || world == null)
            return b;
        FluidTransportBehaviour transport =
            BlockEntityBehaviour.get(world, startPos, FluidTransportBehaviour.TYPE);
        if (transport instanceof MaterialPipeFluidTransportBehaviour matTransport) {
            return b * matTransport.flowMultiplier / matTransport.propagationMultiplier;
        }
        return b;
    }

    @Inject(method = "reset", at = @At("TAIL"))
    private void onResetTail(CallbackInfo ci) {
        pauseBeforePropagation = 0;
    }
}
