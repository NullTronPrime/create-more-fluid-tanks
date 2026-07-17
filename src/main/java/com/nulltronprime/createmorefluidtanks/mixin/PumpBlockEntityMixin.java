package com.nulltronprime.createmorefluidtanks.mixin;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.HashMap;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
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

    private static final ThreadLocal<PumpBlockEntity> cmft_pumpForRange = new ThreadLocal<>();

    @Unique
    private int cmft_bfsCooldown = 0;
    @Unique
    private float cmft_cachedMinFactor = Float.MAX_VALUE;
    @Unique
    private int cmft_cachedMaxDistance = 0;

    private static int cmft_computeMaxDistance(Level level, BlockPos startPos) {
        int baseRange = FluidPropagator.getPumpRange();
        int maxDistance = baseRange;
        Set<BlockPos> visited = new HashSet<>();
        Queue<BlockPos> queue = new LinkedList<>();
        Map<BlockPos, Integer> distances = new HashMap<>();
        queue.add(startPos);
        distances.put(startPos, 0);

        while (!queue.isEmpty()) {
            BlockPos pos = queue.poll();
            if (!visited.add(pos))
                continue;
            if (!level.isLoaded(pos))
                continue;
            int dist = distances.get(pos);

            FluidTransportBehaviour pipe = FluidPropagator.getPipe(level, pos);
            if (pipe == null)
                continue;

            if (pipe instanceof MaterialPipeFluidTransportBehaviour matPipe) {
                int pipeRange = (int) (baseRange * matPipe.propagationMultiplier);
                if (pipeRange > maxDistance)
                    maxDistance = pipeRange;
            }

            if (dist >= maxDistance)
                continue;

            BlockState state = level.getBlockState(pos);
            for (Direction face : FluidPropagator.getPipeConnections(state, pipe)) {
                BlockPos next = pos.relative(face);
                if (!visited.contains(next) && level.isLoaded(next) && !distances.containsKey(next)) {
                    distances.put(next, dist + 1);
                    queue.add(next);
                }
            }
        }
        return maxDistance;
    }

    @Inject(method = "distributePressureTo", at = @At("HEAD"))
    private void cmft_onDistributePressureTo(Direction side, CallbackInfo ci) {
        cmft_pumpForRange.set((PumpBlockEntity) (Object) this);
    }

    @Redirect(
        method = "distributePressureTo",
        at = @At(value = "INVOKE", target = "Lcom/simibubi/create/content/fluids/FluidPropagator;getPumpRange()I"),
        remap = false
    )
    private static int cmft_getPumpRange() {
        PumpBlockEntity self = cmft_pumpForRange.get();
        if (self == null)
            return FluidPropagator.getPumpRange();

        int base = FluidPropagator.getPumpRange();

        PumpBlockEntityMixin mixin = (PumpBlockEntityMixin) (Object) self;
        if (mixin.cmft_cachedMaxDistance > base)
            return mixin.cmft_cachedMaxDistance;

        if (mixin.cmft_cachedMaxDistance <= 0) {
            Level level = self.getLevel();
            if (level != null) {
                Direction front = self.getBlockState().getValue(PumpBlock.FACING);
                BlockPos frontPos = self.getBlockPos().relative(front);
                BlockPos backPos = self.getBlockPos().relative(front.getOpposite());
                int frontRange = cmft_computeMaxDistance(level, frontPos);
                int backRange = cmft_computeMaxDistance(level, backPos);
                int expanded = Math.max(frontRange, backRange);
                if (expanded > base) {
                    mixin.cmft_cachedMaxDistance = expanded;
                    return expanded;
                }
            }
        }

        return base;
    }

    @Inject(method = "tick", at = @At("TAIL"))
    private void onTickEnd(CallbackInfo ci) {
        PumpBlockEntity self = (PumpBlockEntity) (Object) this;
        Level level = self.getLevel();
        if (level == null || level.isClientSide || self.getSpeed() == 0) {
            cmft_cachedMaxDistance = 0;
            return;
        }
        FluidTransportBehaviour behaviour =
            BlockEntityBehaviour.get(level, self.getBlockPos(), FluidTransportBehaviour.TYPE);
        if (behaviour == null || behaviour.interfaces == null) {
            cmft_cachedMaxDistance = 0;
            return;
        }
        Direction front = self.getBlockState().getValue(PumpBlock.FACING);
        Direction back = front.getOpposite();
        var backConn = behaviour.interfaces.get(back);
        if (backConn == null || backConn.getPressure().getFirst() <= 0)
            return;

        if (cmft_bfsCooldown > 0) {
            cmft_bfsCooldown--;
            if (cmft_cachedMinFactor < Float.MAX_VALUE) {
                backConn.getPressure().setFirst(backConn.getPressure().getFirst() * cmft_cachedMinFactor);
            }
            return;
        }
        cmft_bfsCooldown = 20;

        int baseRange = FluidPropagator.getPumpRange();
        cmft_cachedMinFactor = Float.MAX_VALUE;

        BlockPos startPos = self.getBlockPos().relative(back);
        int newMaxDistance = cmft_computeMaxDistance(level, startPos);
        int frontMax = cmft_computeMaxDistance(level, self.getBlockPos().relative(front));
        if (frontMax > newMaxDistance)
            newMaxDistance = frontMax;
        cmft_cachedMaxDistance = newMaxDistance;

        Set<BlockPos> visited = new HashSet<>();
        Queue<BlockPos> queue = new LinkedList<>();
        Map<BlockPos, Integer> distances = new HashMap<>();
        int maxDistance = baseRange;
        queue.add(startPos);
        distances.put(startPos, 0);

        while (!queue.isEmpty()) {
            BlockPos pos = queue.poll();
            if (!visited.add(pos))
                continue;
            if (!level.isLoaded(pos))
                continue;
            int dist = distances.get(pos);

            FluidTransportBehaviour pipe = FluidPropagator.getPipe(level, pos);
            if (pipe == null)
                continue;

            if (pipe instanceof MaterialPipeFluidTransportBehaviour matPipe) {
                int pipeRange = (int) (baseRange * matPipe.propagationMultiplier);
                if (dist < pipeRange && pipeRange > maxDistance)
                    maxDistance = pipeRange;
                float factor = matPipe.flowMultiplier / matPipe.propagationMultiplier;
                if (factor < cmft_cachedMinFactor)
                    cmft_cachedMinFactor = factor;
            }

            if (dist >= maxDistance)
                continue;

            BlockState state = level.getBlockState(pos);
            for (Direction face : FluidPropagator.getPipeConnections(state, pipe)) {
                BlockPos next = pos.relative(face);
                if (!visited.contains(next) && level.isLoaded(next) && !distances.containsKey(next)) {
                    distances.put(next, dist + 1);
                    queue.add(next);
                }
            }
        }

        if (cmft_cachedMinFactor < Float.MAX_VALUE) {
            backConn.getPressure().setFirst(backConn.getPressure().getFirst() * cmft_cachedMinFactor);
        }
    }

}
