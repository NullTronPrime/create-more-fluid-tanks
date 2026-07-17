package com.nulltronprime.createmorefluidtanks.display;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.nulltronprime.createmorefluidtanks.CreateMoreFluidTanks;
import com.nulltronprime.createmorefluidtanks.blockEntity.MaterialPipeFluidTransportBehaviour;
import com.simibubi.create.infrastructure.config.AllConfigs;
import com.simibubi.create.api.behaviour.display.DisplaySource;
import com.simibubi.create.content.fluids.FluidPropagator;
import com.simibubi.create.content.fluids.FluidTransportBehaviour;
import com.simibubi.create.content.fluids.PipeConnection;
import com.simibubi.create.content.fluids.pipes.FluidPipeBlockEntity;
import com.simibubi.create.content.fluids.pump.PumpBlock;
import com.simibubi.create.content.redstone.displayLink.DisplayLinkContext;
import com.simibubi.create.content.redstone.displayLink.target.DisplayTargetStats;
import com.simibubi.create.foundation.gui.ModularGuiLineBuilder;
import com.simibubi.create.foundation.utility.CreateLang;

import net.createmod.catnip.data.Pair;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

public class PipeFlowDisplaySource extends DisplaySource {

    private static final int CYCLES_PER_TICK = 16;

    @Override
    public List<MutableComponent> provideText(DisplayLinkContext context, DisplayTargetStats stats) {
        BlockEntity be = context.getSourceBlockEntity();
        if (!(be instanceof FluidPipeBlockEntity pipe))
            return EMPTY;

        FluidTransportBehaviour transport = pipe.getBehaviour(FluidTransportBehaviour.TYPE);
        if (transport == null)
            return EMPTY;

        float maxPressure = 0;
        float propMult = 1.0f;
        float flowMult = 1.0f;

        for (net.minecraft.core.Direction dir : net.minecraft.core.Direction.values()) {
            PipeConnection conn = transport.getConnection(dir);
            if (conn != null && conn.hasPressure()) {
                var pressure = conn.getPressure();
                float p = Math.max(pressure.getFirst(), pressure.getSecond());
                if (p > maxPressure)
                    maxPressure = p;
            }
        }

        if (transport instanceof MaterialPipeFluidTransportBehaviour matTransport) {
            propMult = matTransport.propagationMultiplier;
            flowMult = matTransport.flowMultiplier;
        }

        int mode = context.sourceConfig().getInt("Mode");

        if (maxPressure <= 0) {
            return List.of(Component.translatable(
                CreateMoreFluidTanks.MOD_ID + ".display_source.pipe_flow.no_flow_" + (mode == 0 ? "flow" : "range")));
        }

        if (mode == 0) {
            int throughput = (int) (Math.max(1, maxPressure / propMult * flowMult / 2f) * CYCLES_PER_TICK);
            return List.of(Component.translatable(
                CreateMoreFluidTanks.MOD_ID + ".display_source.pipe_flow.throughput", throughput));
        }

        int pumpRange = AllConfigs.server().fluids.mechanicalPumpRange.get();
        int maxRange = Math.max(1, (int) (pumpRange * propMult));
        int dist = findNearestPumpDistance(be, transport);
        int remaining = dist >= 0 ? Math.max(1, maxRange - dist) : maxRange;
        return List.of(Component.translatable(
            CreateMoreFluidTanks.MOD_ID + ".display_source.pipe_flow.range_display", remaining));
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void initConfigurationWidgets(DisplayLinkContext context, ModularGuiLineBuilder builder,
        boolean isFirstLine) {
        super.initConfigurationWidgets(context, builder, isFirstLine);
        if (isFirstLine)
            return;

        builder.addSelectionScrollInput(0, 95, (selectionScrollInput, label) -> {
            selectionScrollInput
                .forOptions(CreateLang.translatedOptions(
                    CreateMoreFluidTanks.MOD_ID + ".display_source.pipe_flow", "flow", "range"));
        }, "Mode");
    }

    private static int findNearestPumpDistance(BlockEntity be, FluidTransportBehaviour startTransport) {
        Level level = be.getLevel();
        if (level == null)
            return -1;

        BlockPos start = be.getBlockPos();
        Set<BlockPos> visited = new HashSet<>();
        List<Pair<Integer, BlockPos>> frontier = new ArrayList<>();
        frontier.add(Pair.of(0, start));

        int pumpRange = AllConfigs.server().fluids.mechanicalPumpRange.get();
        if (startTransport instanceof MaterialPipeFluidTransportBehaviour matTransport)
            pumpRange = Math.max(pumpRange, (int) (pumpRange * matTransport.propagationMultiplier));

        while (!frontier.isEmpty()) {
            Pair<Integer, BlockPos> entry = frontier.remove(0);
            int distance = entry.getFirst();
            BlockPos currentPos = entry.getSecond();

            if (!visited.add(currentPos))
                continue;

            if (distance > 0) {
                BlockState state = level.getBlockState(currentPos);
                if (state.getBlock() instanceof PumpBlock)
                    return distance;
            }

            if (distance >= pumpRange)
                continue;

            FluidTransportBehaviour pipe = FluidPropagator.getPipe(level, currentPos);
            if (pipe == null)
                continue;

            if (pipe instanceof MaterialPipeFluidTransportBehaviour matPipe) {
                int adjusted = (int) (AllConfigs.server().fluids.mechanicalPumpRange.get() * matPipe.propagationMultiplier);
                if (adjusted > pumpRange)
                    pumpRange = adjusted;
            }

            BlockState currentState = level.getBlockState(currentPos);
            for (Direction dir : FluidPropagator.getPipeConnections(currentState, pipe)) {
                BlockPos next = currentPos.relative(dir);
                if (!visited.contains(next))
                    frontier.add(Pair.of(distance + 1, next));
            }
        }

        return -1;
    }

    @Override
    public Component getName() {
        return Component.translatable(CreateMoreFluidTanks.MOD_ID + ".display_source.pipe_flow");
    }
}
