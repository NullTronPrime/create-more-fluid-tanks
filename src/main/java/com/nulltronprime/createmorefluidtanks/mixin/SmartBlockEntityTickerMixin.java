package com.nulltronprime.createmorefluidtanks.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.simibubi.create.content.fluids.tank.FluidTankBlockEntity;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntityTicker;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

@Mixin(SmartBlockEntityTicker.class)
public class SmartBlockEntityTickerMixin {

    @Inject(method = "tick", at = @At("HEAD"), cancellable = true, remap = false)
    private void skipNonControllerTankClientTick(Level level, BlockPos pos, BlockState state, BlockEntity be, CallbackInfo ci) {
        if (level.isClientSide && be instanceof FluidTankBlockEntity tank && !tank.isController())
            ci.cancel();
    }

}
