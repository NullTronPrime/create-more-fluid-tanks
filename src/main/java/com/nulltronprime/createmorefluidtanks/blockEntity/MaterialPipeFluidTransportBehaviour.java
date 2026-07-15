package com.nulltronprime.createmorefluidtanks.blockEntity;

import com.simibubi.create.content.fluids.FluidPropagator;
import com.simibubi.create.content.fluids.FluidTransportBehaviour;
import com.simibubi.create.content.fluids.PipeConnection;
import com.simibubi.create.content.fluids.pipes.EncasedPipeBlock;
import com.simibubi.create.content.fluids.pipes.FluidPipeBlock;
import com.simibubi.create.content.fluids.pipes.GlassFluidPipeBlock;
import com.simibubi.create.content.fluids.pump.PumpBlockEntity;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public class MaterialPipeFluidTransportBehaviour extends FluidTransportBehaviour {

    public final float propagationMultiplier;
    public final float flowMultiplier;

    public MaterialPipeFluidTransportBehaviour(SmartBlockEntity be, float propagationMultiplier, float flowMultiplier) {
        super(be);
        this.propagationMultiplier = propagationMultiplier;
        this.flowMultiplier = flowMultiplier;
    }

    @Override
    public boolean canHaveFlowToward(BlockState state, Direction direction) {
        return (FluidPipeBlock.isPipe(state) || state.getBlock() instanceof EncasedPipeBlock)
            && state.getValue(FluidPipeBlock.PROPERTY_BY_DIRECTION.get(direction));
    }

    @Override
    public void addPressure(Direction side, boolean inbound, float pressure) {
        float scaled = pressure * propagationMultiplier;
        super.addPressure(side, inbound, scaled);

        if (!inbound && scaled > 0) {
            Level level = blockEntity.getLevel();
            if (level == null) return;
            BlockPos here = blockEntity.getBlockPos();
            for (Direction dir : interfaces.keySet()) {
                if (dir == side) continue;
                PipeConnection conn = interfaces.get(dir);
                if (conn.getPressure().getFirst() > 0) continue;
                if (level.getBlockEntity(here.relative(dir)) instanceof PumpBlockEntity) {
                    super.addPressure(dir, true, scaled);
                }
            }
        }
    }

    @Override
    public AttachmentTypes getRenderedRimAttachment(BlockAndTintGetter world, BlockPos pos, BlockState state,
        Direction direction) {
        AttachmentTypes attachment = super.getRenderedRimAttachment(world, pos, state, direction);

        BlockPos offsetPos = pos.relative(direction);
        BlockState otherState = world.getBlockState(offsetPos);

        if (state.getBlock() instanceof EncasedPipeBlock && attachment != AttachmentTypes.DRAIN)
            return AttachmentTypes.NONE;

        if (attachment == AttachmentTypes.RIM) {
            if (!FluidPipeBlock.isPipe(otherState) && !(otherState.getBlock() instanceof EncasedPipeBlock)
                && !(otherState.getBlock() instanceof GlassFluidPipeBlock)) {
                FluidTransportBehaviour pipeBehaviour =
                    BlockEntityBehaviour.get(world, offsetPos, FluidTransportBehaviour.TYPE);
                if (pipeBehaviour != null && pipeBehaviour.canHaveFlowToward(otherState, direction.getOpposite()))
                    return AttachmentTypes.DETAILED_CONNECTION;
            }

            if (!FluidPipeBlock.shouldDrawRim(world, pos, state, direction))
                return FluidPropagator.getStraightPipeAxis(state) == direction.getAxis()
                    ? AttachmentTypes.CONNECTION
                    : AttachmentTypes.DETAILED_CONNECTION;
        }

        if (attachment == AttachmentTypes.NONE
            && state.getValue(FluidPipeBlock.PROPERTY_BY_DIRECTION.get(direction)))
            return AttachmentTypes.DETAILED_CONNECTION;

        return attachment;
    }
}
