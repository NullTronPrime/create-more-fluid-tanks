package com.nulltronprime.createmorefluidtanks.mixin;

import com.simibubi.create.content.fluids.tank.FluidTankBlock;
import com.simibubi.create.content.kinetics.steamEngine.SteamEngineBlock;
import com.simibubi.create.content.kinetics.steamEngine.SteamEngineBlockEntity;

import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = SteamEngineBlockEntity.class, remap = false)
public class SteamEngineBlockEntityMixin {

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
