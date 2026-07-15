package com.nulltronprime.createmorefluidtanks.block;

import com.nulltronprime.createmorefluidtanks.registry.ModBlockEntityTypes;
import com.simibubi.create.content.fluids.tank.FluidTankBlock;
import com.simibubi.create.content.fluids.tank.FluidTankBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;

public class SturdyFluidTankBlock extends FluidTankBlock {

    public SturdyFluidTankBlock(Properties properties) {
        super(properties, false);
    }

    @Override
    public BlockEntityType<? extends FluidTankBlockEntity> getBlockEntityType() {
        return ModBlockEntityTypes.STURDY_FLUID_TANK.get();
    }
}
