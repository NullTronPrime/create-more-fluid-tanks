package com.nulltronprime.createmorefluidtanks.blockEntity;

import com.simibubi.create.api.boiler.BoilerHeater;
import com.simibubi.create.content.fluids.tank.BoilerData;
import com.simibubi.create.content.fluids.tank.FluidTankBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public class ModBoilerData extends BoilerData {

    private final float heatMultiplier;

    public ModBoilerData(float heatMultiplier) {
        this.heatMultiplier = heatMultiplier;
    }

    public float getHeatMultiplier() {
        return heatMultiplier;
    }

    @Override
    public boolean updateTemperature(FluidTankBlockEntity controller) {
        BlockPos controllerPos = controller.getBlockPos();
        Level level = controller.getLevel();
        needsHeatLevelUpdate = false;

        boolean prevPassive = passiveHeat;
        int prevActive = activeHeat;
        passiveHeat = false;
        activeHeat = 0;

        for (int xOffset = 0; xOffset < controller.getWidth(); xOffset++) {
            for (int zOffset = 0; zOffset < controller.getWidth(); zOffset++) {
                BlockPos pos = controllerPos.offset(xOffset, -1, zOffset);
                BlockState blockState = level.getBlockState(pos);
                float heat = BoilerHeater.findHeat(level, pos, blockState);
                if (heat == 0) {
                    passiveHeat = true;
                } else if (heat > 0) {
                    activeHeat += Math.round(heat * heatMultiplier);
                }
            }
        }

        passiveHeat &= activeHeat == 0;

        return prevActive != activeHeat || prevPassive != passiveHeat;
    }
}
