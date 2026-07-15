package com.nulltronprime.createmorefluidtanks.block;

import com.simibubi.create.content.fluids.pipes.FluidPipeBlock;
import com.simibubi.create.content.fluids.pipes.FluidPipeBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;

import java.util.function.Supplier;

public class CosmeticFluidPipeBlock extends FluidPipeBlock {

    private final Supplier<BlockEntityType<? extends FluidPipeBlockEntity>> beType;

    public CosmeticFluidPipeBlock(Properties properties, Supplier<BlockEntityType<? extends FluidPipeBlockEntity>> beType) {
        super(properties);
        this.beType = beType;
    }

    @Override
    public BlockEntityType<? extends FluidPipeBlockEntity> getBlockEntityType() {
        return beType.get();
    }
}
