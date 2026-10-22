package com.nulltronprime.createmorefluidtanks.blockEntity;

import com.nulltronprime.createmorefluidtanks.config.CMoreTanksPipeConfig;
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
    public float getPropagationMultiplier() {
        return CMoreTanksPipeConfig.AMETHYST_PROPAGATION.get().floatValue();
    }

    @Override
    protected float getFlowMultiplier() {
        return CMoreTanksPipeConfig.AMETHYST_FLOW.get().floatValue();
    }
}
