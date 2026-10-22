package com.nulltronprime.createmorefluidtanks.blockEntity;

import java.util.List;

import com.simibubi.create.content.decoration.bracket.BracketedBlockEntityBehaviour;
import com.simibubi.create.content.fluids.FluidPropagator;
import com.simibubi.create.content.fluids.pipes.EncasedPipeBlock;
import com.simibubi.create.content.fluids.pipes.FluidPipeBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public abstract class MaterialFluidPipeBlockEntity extends FluidPipeBlockEntity {

    public MaterialFluidPipeBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    public abstract float getPropagationMultiplier();

    protected abstract float getFlowMultiplier();

    @Override
    public void addBehaviours(List<BlockEntityBehaviour> behaviours) {
        behaviours.add(new MaterialPipeFluidTransportBehaviour(this, getPropagationMultiplier(), getFlowMultiplier()));
        behaviours.add(new BracketedBlockEntityBehaviour(this, this::canHaveBracket));
        registerAwardables(behaviours, FluidPropagator.getSharedTriggers());
    }

    private boolean canHaveBracket(BlockState state) {
        return !(state.getBlock() instanceof EncasedPipeBlock);
    }
}
