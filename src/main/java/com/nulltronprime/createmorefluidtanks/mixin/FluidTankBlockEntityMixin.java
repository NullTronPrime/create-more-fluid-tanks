package com.nulltronprime.createmorefluidtanks.mixin;

import org.spongepowered.asm.mixin.Implements;
import org.spongepowered.asm.mixin.Interface;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.simibubi.create.content.fluids.tank.FluidTankBlock;
import com.simibubi.create.content.fluids.tank.FluidTankBlock.Shape;
import com.simibubi.create.content.fluids.tank.FluidTankBlockEntity;

import com.nulltronprime.createmorefluidtanks.CreateMoreFluidTanks;
import com.nulltronprime.createmorefluidtanks.accessor.GlassStateAccessor;

import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;

@Mixin(FluidTankBlockEntity.class)
@Implements(@Interface(iface = GlassStateAccessor.class, prefix = "cmft$"))
public class FluidTankBlockEntityMixin {

    @Shadow(remap = false)
    protected boolean window;

    @Unique
    private int cmft$glassState = 1;

    @Unique
    public int cmft$getGlassState() {
        return cmft$glassState;
    }

    @Unique
    public void cmft$setGlassState(int state) {
        cmft$glassState = state;
    }

    @Inject(method = "toggleWindows", at = @At("HEAD"), cancellable = true, remap = false)
    private void onToggleWindows(CallbackInfo ci) {
        FluidTankBlockEntity be = ((FluidTankBlockEntity) (Object) this).getControllerBE();
        if (be == null)
            return;
        FluidTankBlockEntityMixin mixin = (FluidTankBlockEntityMixin) (Object) be;
        if (be.boiler.isActive())
            return;

        Block beBlock = be.getBlockState().getBlock();
        ResourceLocation beBlockId = BuiltInRegistries.BLOCK.getKey(beBlock);
        boolean isCopper = beBlockId.equals(ResourceLocation.fromNamespaceAndPath("create", "fluid_tank"));
        int maxStates = isCopper ? 4 : 3;
        int next = (mixin.cmft$glassState + 1) % maxStates;
        mixin.cmft$glassState = next;
        if (next == 2) {
            mixin.window = true;
            be.setChanged();
            for (int y = 0; y < be.getHeight(); y++) {
                for (int x = 0; x < be.getWidth(); x++) {
                    for (int z = 0; z < be.getWidth(); z++) {
                        BlockPos p = be.getBlockPos().offset(x, y, z);
                        BlockState bs = be.getLevel().getBlockState(p);
                        if (FluidTankBlock.isTank(bs))
                            be.getLevel().setBlock(p, bs.setValue(FluidTankBlock.SHAPE,
                                    Shape.valueOf("MORE_GLASS")), 3);
                    }
                }
            }
        } else if (next == 3) {
            mixin.window = true;
            be.setChanged();
            for (int y = 0; y < be.getHeight(); y++) {
                for (int x = 0; x < be.getWidth(); x++) {
                    for (int z = 0; z < be.getWidth(); z++) {
                        BlockPos p = be.getBlockPos().offset(x, y, z);
                        BlockState bs = be.getLevel().getBlockState(p);
                        if (FluidTankBlock.isTank(bs))
                            be.getLevel().setBlock(p, bs.setValue(FluidTankBlock.SHAPE,
                                    Shape.valueOf("SUPER_GLASS")), 3);
                    }
                }
            }
        } else {
            be.setWindows(next != 0);
        }
        ci.cancel();
    }

    @Inject(method = "setWindows", at = @At("HEAD"), cancellable = true, remap = false)
    private void onSetWindows(boolean window, CallbackInfo ci) {
        if (cmft$glassState == 2 && window) {
            FluidTankBlockEntity self = (FluidTankBlockEntity) (Object) this;
            if (self.getLevel() == null || !self.isController())
                return;
            for (int y = 0; y < self.getHeight(); y++) {
                for (int x = 0; x < self.getWidth(); x++) {
                    for (int z = 0; z < self.getWidth(); z++) {
                        BlockPos p = self.getBlockPos().offset(x, y, z);
                        BlockState bs = self.getLevel().getBlockState(p);
                        if (FluidTankBlock.isTank(bs))
                            self.getLevel().setBlock(p, bs.setValue(FluidTankBlock.SHAPE,
                                    Shape.valueOf("MORE_GLASS")), 3);
                    }
                }
            }
            ci.cancel();
        } else if (cmft$glassState == 3 && window) {
            FluidTankBlockEntity self = (FluidTankBlockEntity) (Object) this;
            if (self.getLevel() == null || !self.isController())
                return;
            for (int y = 0; y < self.getHeight(); y++) {
                for (int x = 0; x < self.getWidth(); x++) {
                    for (int z = 0; z < self.getWidth(); z++) {
                        BlockPos p = self.getBlockPos().offset(x, y, z);
                        BlockState bs = self.getLevel().getBlockState(p);
                        if (FluidTankBlock.isTank(bs))
                            self.getLevel().setBlock(p, bs.setValue(FluidTankBlock.SHAPE,
                                    Shape.valueOf("SUPER_GLASS")), 3);
                    }
                }
            }
            ci.cancel();
        }
    }

    @Inject(method = "read", at = @At("TAIL"), remap = false)
    private void onRead(CompoundTag compound, HolderLookup.Provider registries, boolean clientPacket, CallbackInfo ci) {
        if (((FluidTankBlockEntity) (Object) this).isController() && compound.contains("GlassState")) {
            cmft$glassState = compound.getInt("GlassState");
        }
    }

    @Inject(method = "write", at = @At("TAIL"), remap = false)
    private void onWrite(CompoundTag compound, HolderLookup.Provider registries, boolean clientPacket, CallbackInfo ci) {
        if (((FluidTankBlockEntity) (Object) this).isController()) {
            compound.putInt("GlassState", cmft$glassState);
        }
    }

    @Inject(method = "writeSafe", at = @At("TAIL"), remap = false)
    private void onWriteSafe(CompoundTag compound, HolderLookup.Provider registries, CallbackInfo ci) {
        if (((FluidTankBlockEntity) (Object) this).isController()) {
            compound.putInt("GlassState", cmft$glassState);
        }
    }

    @Inject(method = "removeController", at = @At("HEAD"), remap = false)
    private void onRemoveController(boolean keepFluids, CallbackInfo ci) {
        cmft$glassState = 1;
    }

    @Inject(method = "getExtraData", at = @At("HEAD"), remap = false, cancellable = true)
    private void onGetExtraData(CallbackInfoReturnable<Object> cir) {
        cir.setReturnValue(cmft$glassState);
    }

    @Inject(method = "modifyExtraData", at = @At("HEAD"), remap = false, cancellable = true)
    private void onModifyExtraData(Object data, CallbackInfoReturnable<Object> cir) {
        if (data instanceof Integer d) {
            cmft$glassState = Math.max(cmft$glassState, d);
            cir.setReturnValue(cmft$glassState);
        }
    }

    @Inject(method = "setExtraData", at = @At("HEAD"), remap = false, cancellable = true)
    private void onSetExtraData(Object data, CallbackInfo ci) {
        if (data instanceof Integer d) {
            cmft$glassState = d;
            window = d != 0;
            ci.cancel();
        }
    }
}
