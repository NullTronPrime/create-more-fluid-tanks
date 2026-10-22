package com.nulltronprime.createmorefluidtanks.compat;

import com.nulltronprime.createmorefluidtanks.blockEntity.MaterialFluidPipeBlockEntity;
import de.devin.pipesnphysics.engine.FlowSolver;
import de.devin.pipesnphysics.engine.Graph;
import de.devin.pipesnphysics.engine.Node;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.common.ModConfigSpec;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Changes when create-pipes-n-physics is loaded:
 * <ol>
 *   <li>{@code MAX_FLOW_PER_ENDPOINT} → multiplied by 1000
 *       (default 256 → 256000 mB/tick, config max 8192 → 8192000)</li>
 *   <li>{@code PUMP_HEAD_PER_RPM} / {@code PUMP_FLOW_PER_RPM} → multiplied by the
 *       {@link MaterialFluidPipeBlockEntity#getPropagationMultiplier()} of any material
 *       pipe block adjacent to ANY pump in the network (1.2x brass … 10x amethyst).
 *       All pumps share the same multiplier (the max found) for simplicity.</li>
 * </ol>
 */
@Mixin(value = FlowSolver.class, remap = false)
public abstract class FlowSolverMixin {

    // -------------------------------------------------------------------------
    // 1) MAX_FLOW_PER_ENDPOINT × 1000
    // -------------------------------------------------------------------------
    private static final int CMFT_FLOW_SCALE = 1000;

    @Redirect(
        method = "planTransfers",
        at = @At(value = "INVOKE", target = "Lnet/neoforged/neoforge/common/ModConfigSpec$IntValue;get()I"),
        remap = false
    )
    private static int cmft$scaleMaxFlow(ModConfigSpec.IntValue value) {
        return value.get() * CMFT_FLOW_SCALE;
    }

    // -------------------------------------------------------------------------
    // 2) Pump head / flow scaling by material pipe multiplier
    // -------------------------------------------------------------------------
    @Unique
    private static final ThreadLocal<Double> cmft$pumpMultiplier = ThreadLocal.withInitial(() -> 1.0);

    @Inject(method = "collectPumps", at = @At("HEAD"), remap = false)
    private static void cmft$scanMaterialPipes(Level level, Graph graph, CallbackInfo ci) {
        double mult = 1.0;
        for (Node pump : graph.pumps()) {
            for (Direction dir : Direction.values()) {
                BlockPos pos = pump.pos().relative(dir);
                BlockEntity be = level.getBlockEntity(pos);
                if (be instanceof MaterialFluidPipeBlockEntity pipe) {
                    mult = Math.max(mult, pipe.getPropagationMultiplier());
                }
            }
        }
        cmft$pumpMultiplier.set(mult);
    }

    @Redirect(
        method = "collectPumps",
        at = @At(value = "INVOKE", target = "Lnet/neoforged/neoforge/common/ModConfigSpec$DoubleValue;get()D"),
        remap = false
    )
    private static double cmft$scalePumpConfig(ModConfigSpec.DoubleValue value) {
        return value.get() * cmft$pumpMultiplier.get();
    }
}
