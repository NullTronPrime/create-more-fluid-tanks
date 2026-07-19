package com.nulltronprime.createmorefluidtanks.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.simibubi.create.content.fluids.tank.FluidTankBlock;
import com.simibubi.create.content.fluids.tank.FluidTankCTBehaviour;
import com.simibubi.create.foundation.block.connected.CTSpriteShiftEntry;

import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;

@Mixin(FluidTankCTBehaviour.class)
public class FluidTankCTBehaviourMixin {

    @Unique
    private CTSpriteShiftEntry cmft$moreGlassShift;

    @Unique
    private CTSpriteShiftEntry cmft$moreGlassTopShift;

    @Unique
    private CTSpriteShiftEntry cmft$moreGlassInnerShift;

    @Unique
    private CTSpriteShiftEntry cmft$superGlassShift;

    @Unique
    private CTSpriteShiftEntry cmft$superGlassTopShift;

    @Unique
    private CTSpriteShiftEntry cmft$superGlassInnerShift;

    @Inject(method = "getShift(Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/core/Direction;Lnet/minecraft/client/renderer/texture/TextureAtlasSprite;)Lcom/simibubi/create/foundation/block/connected/CTSpriteShiftEntry;",
            at = @At("HEAD"), cancellable = true, remap = false)
    private void cmft$handleMoreGlass(BlockState state, Direction direction, TextureAtlasSprite sprite, CallbackInfoReturnable<CTSpriteShiftEntry> cir) {
        if (state.hasProperty(FluidTankBlock.SHAPE)) {
            String shapeName = state.getValue(FluidTankBlock.SHAPE).name();
            if ("MORE_GLASS".equals(shapeName) && cmft$moreGlassShift != null) {
                if (direction.getAxis() == Direction.Axis.Y) {
                    if (sprite != null && sprite == cmft$moreGlassInnerShift.getOriginal()) {
                        cir.setReturnValue(cmft$moreGlassInnerShift);
                        return;
                    }
                    cir.setReturnValue(cmft$moreGlassTopShift);
                    return;
                }
                cir.setReturnValue(cmft$moreGlassShift);
            } else if ("SUPER_GLASS".equals(shapeName) && cmft$superGlassShift != null) {
                if (direction.getAxis() == Direction.Axis.Y) {
                    if (sprite != null && sprite == cmft$superGlassInnerShift.getOriginal()) {
                        cir.setReturnValue(cmft$superGlassInnerShift);
                        return;
                    }
                    cir.setReturnValue(cmft$superGlassTopShift);
                    return;
                }
                cir.setReturnValue(cmft$superGlassShift);
            }
        }
    }

    @Unique
    public void cmft$setMoreGlassShifts(CTSpriteShiftEntry mgShift, CTSpriteShiftEntry mgTopShift, CTSpriteShiftEntry mgInnerShift) {
        this.cmft$moreGlassShift = mgShift;
        this.cmft$moreGlassTopShift = mgTopShift;
        this.cmft$moreGlassInnerShift = mgInnerShift;
    }

    @Unique
    public void cmft$setSuperGlassShifts(CTSpriteShiftEntry sgShift, CTSpriteShiftEntry sgTopShift, CTSpriteShiftEntry sgInnerShift) {
        this.cmft$superGlassShift = sgShift;
        this.cmft$superGlassTopShift = sgTopShift;
        this.cmft$superGlassInnerShift = sgInnerShift;
    }
}
