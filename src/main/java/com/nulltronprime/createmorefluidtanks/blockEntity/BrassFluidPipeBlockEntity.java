package com.nulltronprime.createmorefluidtanks.blockEntity;

import com.nulltronprime.createmorefluidtanks.config.CMoreTanksPipeConfig;
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
    public float getPropagationMultiplier() {
        return CMoreTanksPipeConfig.BRASS_PROPAGATION.get().floatValue();
    }

    @Override
    protected float getFlowMultiplier() {
        return CMoreTanksPipeConfig.BRASS_FLOW.get().floatValue();
    }
}
