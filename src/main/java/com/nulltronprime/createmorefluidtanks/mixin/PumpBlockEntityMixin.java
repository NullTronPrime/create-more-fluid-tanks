package com.nulltronprime.createmorefluidtanks.mixin;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.nulltronprime.createmorefluidtanks.blockEntity.MaterialPipeFluidTransportBehaviour;
import com.simibubi.create.content.fluids.FluidPropagator;
import com.simibubi.create.content.fluids.FluidTransportBehaviour;
import com.simibubi.create.content.fluids.pump.PumpBlock;
import com.simibubi.create.content.fluids.pump.PumpBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

@Mixin(value = PumpBlockEntity.class, remap = false)
public class PumpBlockEntityMixin {

    @Inject(method = "tick", at = @At("TAIL"))
    private void onTickEnd(CallbackInfo ci) {
        PumpBlockEntity self = (PumpBlockEntity) (Object) this;
        Level level = self.getLevel();
        if (level == null || level.isClientSide || self.getSpeed() == 0)
            return;
        FluidTransportBehaviour behaviour =
            BlockEntityBehaviour.get(level, self.getBlockPos(), FluidTransportBehaviour.TYPE);
        if (behaviour == null || behaviour.interfaces == null)
            return;
        Direction front = self.getBlockState().getValue(PumpBlock.FACING);
        Direction back = front.getOpposite();
        var backConn = behaviour.interfaces.get(back);
        if (backConn == null || backConn.getPressure().getFirst() <= 0)
            return;

        float minFactor = Float.MAX_VALUE;
        Set<BlockPos> visited = new HashSet<>();
        Queue<BlockPos> queue = new LinkedList<>();
        Map<BlockPos, Integer> distances = new HashMap<>();
        BlockPos startPos = self.getBlockPos().relative(back);
        int maxDistance = FluidPropagator.getPumpRange();
        queue.add(startPos);
        distances.put(startPos, 0);

        while (!queue.isEmpty()) {
            BlockPos pos = queue.poll();
            if (!visited.add(pos)) continue;
            if (!level.isLoaded(pos)) continue;
            int dist = distances.get(pos);
            if (dist >= maxDistance) continue;

            FluidTransportBehaviour pipe = FluidPropagator.getPipe(level, pos);
            if (pipe == null) continue;

            if (pipe instanceof MaterialPipeFluidTransportBehaviour matPipe) {
                float factor = matPipe.flowMultiplier / matPipe.propagationMultiplier;
                if (factor < minFactor) {
                    minFactor = factor;
                }
            }

            BlockState state = level.getBlockState(pos);
            for (Direction face : FluidPropagator.getPipeConnections(state, pipe)) {
                BlockPos next = pos.relative(face);
                if (!visited.contains(next) && level.isLoaded(next) && !distances.containsKey(next)) {
                    distances.put(next, dist + 1);
                    queue.add(next);
                }
            }
        }

        if (minFactor < Float.MAX_VALUE) {
            backConn.getPressure().setFirst(backConn.getPressure().getFirst() * minFactor);
        }
    }
}
