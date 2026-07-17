package com.nulltronprime.createmorefluidtanks.blockEntity;

import com.nulltronprime.createmorefluidtanks.config.CMoreTanksPipeConfig;
import com.nulltronprime.createmorefluidtanks.registry.ModBlockEntityTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public class RoseGoldFluidPipeBlockEntity extends MaterialFluidPipeBlockEntity {

    public RoseGoldFluidPipeBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    public RoseGoldFluidPipeBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntityTypes.ROSE_GOLD_FLUID_PIPE.get(), pos, state);
    }

    @Override
    protected float getPropagationMultiplier() {
        return CMoreTanksPipeConfig.ROSE_GOLD_PROPAGATION.get().floatValue();
    }

    @Override
    protected float getFlowMultiplier() {
        return CMoreTanksPipeConfig.ROSE_GOLD_FLOW.get().floatValue();
    }
}
