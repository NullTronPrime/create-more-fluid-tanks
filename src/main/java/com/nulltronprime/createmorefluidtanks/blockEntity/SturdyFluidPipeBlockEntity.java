package com.nulltronprime.createmorefluidtanks.blockEntity;

import com.nulltronprime.createmorefluidtanks.registry.ModBlockEntityTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public class SturdyFluidPipeBlockEntity extends MaterialFluidPipeBlockEntity {

    public SturdyFluidPipeBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    public SturdyFluidPipeBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntityTypes.STURDY_FLUID_PIPE.get(), pos, state);
    }

    @Override
    protected float getPropagationMultiplier() {
        return 1.5f;
    }

    @Override
    protected float getFlowMultiplier() {
        return 1.25f;
    }
}
