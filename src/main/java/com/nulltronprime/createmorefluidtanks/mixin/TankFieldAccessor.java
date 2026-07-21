package com.nulltronprime.createmorefluidtanks.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import com.simibubi.create.content.fluids.tank.FluidTankBlockEntity;

@Mixin(FluidTankBlockEntity.class)
public interface TankFieldAccessor {

    @Accessor(value = "luminosity", remap = false)
    int getLuminosity();

    @Accessor(value = "window", remap = false)
    boolean getWindow();

}
