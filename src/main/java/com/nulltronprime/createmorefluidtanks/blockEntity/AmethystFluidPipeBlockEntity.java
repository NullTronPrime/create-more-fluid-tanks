package com.nulltronprime.createmorefluidtanks.blockEntity;

import com.nulltronprime.createmorefluidtanks.registry.ModBlockEntityTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public class AmethystFluidPipeBlockEntity extends MaterialFluidPipeBlockEntity {

    public AmethystFluidPipeBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    public AmethystFluidPipeBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntityTypes.AMETHYST_FLUID_PIPE.get(), pos, state);
    }

    @Override
    protected float getPropagationMultiplier() {
        return 10.0f;
    }

    @Override
    protected float getFlowMultiplier() {
        return 0.4f;
    }
}
