package com.nulltronprime.createmorefluidtanks.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.nulltronprime.createmorefluidtanks.blockEntity.BrassFluidTankBlockEntity;
import com.nulltronprime.createmorefluidtanks.blockEntity.SilverFluidTankBlockEntity;
import com.nulltronprime.createmorefluidtanks.blockEntity.SturdyFluidTankBlockEntity;
import com.nulltronprime.createmorefluidtanks.blockEntity.RoseGoldFluidTankBlockEntity;
import com.nulltronprime.createmorefluidtanks.blockEntity.AmethystFluidTankBlockEntity;
import com.simibubi.create.content.fluids.tank.FluidTankBlock;
import com.simibubi.create.content.fluids.tank.FluidTankBlock.Shape;
import com.simibubi.create.content.fluids.tank.FluidTankBlockEntity;

import com.nulltronprime.createmorefluidtanks.accessor.GlassStateAccessor;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

@Mixin({
    BrassFluidTankBlockEntity.class,
    SilverFluidTankBlockEntity.class,
    SturdyFluidTankBlockEntity.class,
    RoseGoldFluidTankBlockEntity.class,
    AmethystFluidTankBlockEntity.class
})
public class ModTankBEMoreGlassMixin {

    @Inject(method = "setWindows", at = @At("HEAD"), cancellable = true, remap = false)
    private void cmft$onSetWindows(boolean window, CallbackInfo ci) {
        FluidTankBlockEntity self = (FluidTankBlockEntity) (Object) this;
        GlassStateAccessor accessor = (GlassStateAccessor) self;
        if (accessor.getGlassState() != 2 || !window)
            return;
        if (self.getLevel() == null)
            return;

        for (int yOffset = 0; yOffset < self.getHeight(); yOffset++) {
            for (int xOffset = 0; xOffset < self.getWidth(); xOffset++) {
                for (int zOffset = 0; zOffset < self.getWidth(); zOffset++) {
                    if (self.getWidth() > 2 && xOffset > 0 && xOffset < self.getWidth() - 1
                        && zOffset > 0 && zOffset < self.getWidth() - 1
                        && yOffset > 0 && yOffset < self.getHeight() - 1)
                        continue;
                    BlockPos pos = self.getBlockPos().offset(xOffset, yOffset, zOffset);
                    BlockState blockState = self.getLevel().getBlockState(pos);
                    if (!FluidTankBlock.isTank(blockState))
                        continue;
                    self.getLevel().setBlock(pos,
                            blockState.setValue(FluidTankBlock.SHAPE, Shape.valueOf("MORE_GLASS")),
                            3);
                }
            }
        }
        ci.cancel();
    }
}
