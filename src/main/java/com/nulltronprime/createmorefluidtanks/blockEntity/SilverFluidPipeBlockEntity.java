package com.nulltronprime.createmorefluidtanks.blockEntity;

import com.nulltronprime.createmorefluidtanks.registry.ModBlockEntityTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public class SilverFluidPipeBlockEntity extends MaterialFluidPipeBlockEntity {

    public SilverFluidPipeBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    public SilverFluidPipeBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntityTypes.SILVER_FLUID_PIPE.get(), pos, state);
    }

    @Override
    protected float getPropagationMultiplier() {
        return 2.0f;
    }

    @Override
    protected float getFlowMultiplier() {
        return 2.25f;
    }
}
