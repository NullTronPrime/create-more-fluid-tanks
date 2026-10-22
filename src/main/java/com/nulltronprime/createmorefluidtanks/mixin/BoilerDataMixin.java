package com.nulltronprime.createmorefluidtanks.mixin;

import com.simibubi.create.content.fluids.tank.BoilerData;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(BoilerData.class)
public class BoilerDataMixin {

    @Redirect(method = "getMaxHeatLevelForWaterSupply", at = @At(value = "INVOKE", target = "Ljava/lang/Math;min(II)I"), remap = false)
    private int removeWaterSupplyCap(int a, int b) {
        return b;
    }

    @Redirect(method = "getMaxHeatLevelForBoilerSize", at = @At(value = "INVOKE", target = "Ljava/lang/Math;min(II)I"), remap = false)
    private int removeBoilerSizeCap(int a, int b) {
        return b;
    }
}
