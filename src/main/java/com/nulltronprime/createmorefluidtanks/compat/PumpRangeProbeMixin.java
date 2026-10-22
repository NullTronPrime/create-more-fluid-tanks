package com.nulltronprime.createmorefluidtanks.compat;

import com.nulltronprime.createmorefluidtanks.blockEntity.MaterialFluidPipeBlockEntity;
import de.devin.pipesnphysics.engine.net.PumpRangePayload;
import de.devin.pipesnphysics.engine.PumpRangeProbe;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.common.ModConfigSpec;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Scales the visual pump-range indicator to match the head scaling applied by
 * {@link FlowSolverMixin} — otherwise the in-world arrows would show a shorter
 * range than what actually works.
 */
@Mixin(value = PumpRangeProbe.class, remap = false)
public abstract class PumpRangeProbeMixin {

    @Unique
    private static final ThreadLocal<Double> cmft$probeMultiplier = ThreadLocal.withInitial(() -> 1.0);

    @Inject(method = "probe", at = @At("HEAD"), remap = false)
    private static void cmft$scanProbePipes(ServerLevel level, BlockPos pumpPos,
                                            CallbackInfoReturnable<PumpRangePayload> cir) {
        double mult = 1.0;
        for (Direction dir : Direction.values()) {
            BlockPos pos = pumpPos.relative(dir);
            BlockEntity be = level.getBlockEntity(pos);
            if (be instanceof MaterialFluidPipeBlockEntity pipe) {
                mult = Math.max(mult, pipe.getPropagationMultiplier());
            }
        }
        cmft$probeMultiplier.set(mult);
    }

    @Redirect(
        method = "probe",
        at = @At(value = "INVOKE", target = "Lnet/neoforged/neoforge/common/ModConfigSpec$DoubleValue;get()D", ordinal = 0),
        require = 1,
        remap = false
    )
    private static double cmft$scaleProbeHead(ModConfigSpec.DoubleValue value) {
        return value.get() * cmft$probeMultiplier.get();
    }
}
