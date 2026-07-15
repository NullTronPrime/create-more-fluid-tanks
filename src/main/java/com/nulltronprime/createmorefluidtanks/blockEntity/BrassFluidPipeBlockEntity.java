package com.nulltronprime.createmorefluidtanks.blockEntity;

import com.nulltronprime.createmorefluidtanks.registry.ModBlockEntityTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public class BrassFluidPipeBlockEntity extends MaterialFluidPipeBlockEntity {

    public BrassFluidPipeBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    public BrassFluidPipeBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntityTypes.BRASS_FLUID_PIPE.get(), pos, state);
    }

    @Override
    protected float getPropagationMultiplier() {
        return 1.2f;
    }

    @Override
    protected float getFlowMultiplier() {
        return 1.75f;
    }
}
