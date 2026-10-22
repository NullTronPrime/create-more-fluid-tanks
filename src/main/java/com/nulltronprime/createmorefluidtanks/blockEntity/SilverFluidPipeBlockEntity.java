package com.nulltronprime.createmorefluidtanks.blockEntity;

import com.nulltronprime.createmorefluidtanks.config.CMoreTanksPipeConfig;
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
    public float getPropagationMultiplier() {
        return CMoreTanksPipeConfig.SILVER_PROPAGATION.get().floatValue();
    }

    @Override
    protected float getFlowMultiplier() {
        return CMoreTanksPipeConfig.SILVER_FLOW.get().floatValue();
    }
}
