package com.nulltronprime.createmorefluidtanks.mixin;

import com.nulltronprime.createmorefluidtanks.blockEntity.ModBoilerData;
import com.simibubi.create.content.fluids.tank.FluidTankBlock;
import com.simibubi.create.content.fluids.tank.FluidTankBlockEntity;
import com.simibubi.create.content.kinetics.steamEngine.SteamEngineBlock;
import com.simibubi.create.content.kinetics.steamEngine.SteamEngineBlockEntity;

import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = SteamEngineBlockEntity.class, remap = false)
public class SteamEngineBlockEntityMixin {

    @Redirect(method = "tick",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/util/Mth;clamp(FFF)F"))
    private float onEngineEfficiencyClamp(float value, float min, float max) {
        SteamEngineBlockEntity self = (SteamEngineBlockEntity) (Object) this;
        FluidTankBlockEntity tank = self.getTank();
        if (tank == null || !(tank.boiler instanceof ModBoilerData boiler))
            return Mth.clamp(value, min, max);
        float heatMultiplier = boiler.getHeatMultiplier();
        if (boiler.isPassive(tank.getTotalTankSize()))
            return Mth.clamp(Math.min(heatMultiplier, value), min, max);
        int attachedEngines = boiler.attachedEngines;
        if (attachedEngines <= 0)
            return Mth.clamp(value, min, max);
        float boilerLevel = Math.min(boiler.activeHeat,
                Math.min(boiler.getMaxHeatLevelForWaterSupply(),
                        boiler.getMaxHeatLevelForBoilerSize(tank.getTotalTankSize())));
        return Math.min(heatMultiplier, boilerLevel / attachedEngines);
    }

    @Inject(method = "isValid", at = @At("HEAD"), cancellable = true)
    private void onIsValid(CallbackInfoReturnable<Boolean> cir) {
        SteamEngineBlockEntity self = (SteamEngineBlockEntity) (Object) this;
        Direction dir = SteamEngineBlock.getConnectedDirection(self.getBlockState()).getOpposite();
        Level level = self.getLevel();
        if (level == null) {
            cir.setReturnValue(false);
            return;
        }
        BlockState state = level.getBlockState(self.getBlockPos().relative(dir));
        cir.setReturnValue(state.getBlock() instanceof FluidTankBlock);
    }
}
