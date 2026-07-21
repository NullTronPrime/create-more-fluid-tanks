package com.nulltronprime.createmorefluidtanks.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.simibubi.create.content.fluids.tank.FluidTankBlock;
import com.simibubi.create.content.fluids.tank.FluidTankBlock.Shape;
import com.simibubi.create.content.fluids.tank.FluidTankBlockEntity;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

@Mixin(FluidTankBlock.class)
public class FluidTankBlockMixin {

    @Inject(method = "getLightEmission", at = @At("HEAD"), cancellable = true, remap = false)
    private void cmftLightEmission(BlockState state, BlockGetter world, BlockPos pos, CallbackInfoReturnable<Integer> cir) {
        BlockEntity be = world.getBlockEntity(pos);
        if (!(be instanceof FluidTankBlockEntity tankBE) || !tankBE.hasLevel())
            return;

        int lum = ((TankFieldAccessor) tankBE).getLuminosity();
        if (lum <= 0) {
            cir.setReturnValue(0);
            return;
        }

        if (tankBE.isController()) {
            cir.setReturnValue(((TankFieldAccessor) tankBE).getWindow() ? lum : 0);
            return;
        }

        Shape shape = state.getValue(FluidTankBlock.SHAPE);
        if (shape == Shape.PLAIN) {
            cir.setReturnValue(0);
            return;
        }

        cir.setReturnValue(lum);
    }

}
