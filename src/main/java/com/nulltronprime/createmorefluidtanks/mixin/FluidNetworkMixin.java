package com.nulltronprime.createmorefluidtanks.mixin;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.nulltronprime.createmorefluidtanks.blockEntity.MaterialPipeFluidTransportBehaviour;
import com.simibubi.create.content.fluids.FluidNetwork;
import com.simibubi.create.content.fluids.FluidPropagator;
import com.simibubi.create.content.fluids.FluidTransportBehaviour;

import net.createmod.catnip.math.BlockFace;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

@Mixin(value = FluidNetwork.class, remap = false)
public class FluidNetworkMixin {

    @Shadow
    private Level world;

    @Shadow
    private BlockFace start;

    @Shadow
    private int pauseBeforePropagation;

    @Unique
    private int cmft_scanCooldown = 0;
    @Unique
    private float cmft_cachedFactor = 1.0f;

    @Unique
    private void cmft_scanNetworkForFactor() {
        if (world == null || start == null)
            return;
        int baseRange = FluidPropagator.getPumpRange();
        float minFactor = Float.MAX_VALUE;
        Set<BlockPos> visited = new HashSet<>();
        Queue<BlockPos> queue = new LinkedList<>();
        Map<BlockPos, Integer> distances = new HashMap<>();
        BlockPos startPos = start.getPos();
        queue.add(startPos);
        distances.put(startPos, 0);

        while (!queue.isEmpty()) {
            BlockPos pos = queue.poll();
            if (!visited.add(pos))
                continue;
            if (!world.isLoaded(pos))
                continue;
            int dist = distances.get(pos);

            FluidTransportBehaviour pipe = FluidPropagator.getPipe(world, pos);
            if (pipe == null)
                continue;

            if (pipe instanceof MaterialPipeFluidTransportBehaviour matPipe) {
                if (matPipe.flowMultiplier < minFactor)
                    minFactor = matPipe.flowMultiplier;
            }

            if (dist >= baseRange)
                continue;

            BlockState state = world.getBlockState(pos);
            for (Direction face : FluidPropagator.getPipeConnections(state, pipe)) {
                BlockPos next = pos.relative(face);
                if (!visited.contains(next) && world.isLoaded(next) && !distances.containsKey(next)) {
                    distances.put(next, dist + 1);
                    queue.add(next);
                }
            }
        }

        cmft_cachedFactor = minFactor < Float.MAX_VALUE ? minFactor : 1.0f;
    }

    @ModifyArg(
        method = "tick",
        at = @At(value = "INVOKE", target = "Ljava/lang/Math;max(FF)F"),
        index = 1,
        remap = false
    )
    private float cmft_correctTransferSpeed(float b) {
        if (cmft_scanCooldown <= 0) {
            cmft_scanCooldown = 20;
            cmft_scanNetworkForFactor();
        } else {
            cmft_scanCooldown--;
        }
        return b * cmft_cachedFactor;
    }

    @Inject(method = "reset", at = @At("TAIL"))
    private void onResetTail(CallbackInfo ci) {
        pauseBeforePropagation = 0;
    }

}
