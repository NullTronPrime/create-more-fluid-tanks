package com.nulltronprime.createmorefluidtanks.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.simibubi.create.content.fluids.tank.FluidTankModel;
import com.simibubi.create.foundation.block.connected.AllCTTypes;
import com.simibubi.create.foundation.block.connected.CTSpriteShiftEntry;
import com.simibubi.create.foundation.block.connected.CTSpriteShifter;
import com.simibubi.create.foundation.block.connected.CTModel;

import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.resources.ResourceLocation;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

@Mixin(FluidTankModel.class)
public class FluidTankModelMixin {

    @Inject(method = "standard", at = @At("RETURN"), remap = false)
    private static void onStandard(BakedModel wrappedModel, CallbackInfoReturnable<FluidTankModel> cir) {
        cmft$setMoreGlassShifts(cir.getReturnValue());
    }

    @Inject(method = "creative", at = @At("RETURN"), remap = false)
    private static void onCreative(BakedModel wrappedModel, CallbackInfoReturnable<FluidTankModel> cir) {
        cmft$setMoreGlassShifts(cir.getReturnValue());
    }

    @Unique
    private static void cmft$setMoreGlassShifts(FluidTankModel model) {
        try {
            Field behaviourField = CTModel.class.getDeclaredField("behaviour");
            behaviourField.setAccessible(true);
            Object behaviour = behaviourField.get(model);

            CTSpriteShiftEntry mgShift = CTSpriteShifter.getCT(AllCTTypes.RECTANGLE,
                ResourceLocation.fromNamespaceAndPath("create", "block/fluid_tank_more_glass"),
                ResourceLocation.fromNamespaceAndPath("create", "block/fluid_tank_connected_more_glass"));
            CTSpriteShiftEntry mgTopShift = CTSpriteShifter.getCT(AllCTTypes.RECTANGLE,
                ResourceLocation.fromNamespaceAndPath("create", "block/fluid_tank_top_more_glass"),
                ResourceLocation.fromNamespaceAndPath("create", "block/fluid_tank_top_connected_more_glass"));
            CTSpriteShiftEntry mgInnerShift = CTSpriteShifter.getCT(AllCTTypes.RECTANGLE,
                ResourceLocation.fromNamespaceAndPath("create", "block/fluid_tank_inner_more_glass"),
                ResourceLocation.fromNamespaceAndPath("create", "block/fluid_tank_inner_connected_more_glass"));

            CTSpriteShiftEntry sgShift = CTSpriteShifter.getCT(AllCTTypes.RECTANGLE,
                ResourceLocation.fromNamespaceAndPath("create", "block/fluid_tank_super_glass"),
                ResourceLocation.fromNamespaceAndPath("create", "block/fluid_tank_connected_super_glass"));
            CTSpriteShiftEntry sgTopShift = CTSpriteShifter.getCT(AllCTTypes.RECTANGLE,
                ResourceLocation.fromNamespaceAndPath("create", "block/fluid_tank_top_super_glass"),
                ResourceLocation.fromNamespaceAndPath("create", "block/fluid_tank_top_connected_super_glass"));
            CTSpriteShiftEntry sgInnerShift = CTSpriteShifter.getCT(AllCTTypes.RECTANGLE,
                ResourceLocation.fromNamespaceAndPath("create", "block/fluid_tank_inner_super_glass"),
                ResourceLocation.fromNamespaceAndPath("create", "block/fluid_tank_inner_connected_super_glass"));

            Method setter = behaviour.getClass().getDeclaredMethod("cmft$setMoreGlassShifts",
                CTSpriteShiftEntry.class, CTSpriteShiftEntry.class, CTSpriteShiftEntry.class);
            setter.invoke(behaviour, mgShift, mgTopShift, mgInnerShift);

            Method sgSetter = behaviour.getClass().getDeclaredMethod("cmft$setSuperGlassShifts",
                CTSpriteShiftEntry.class, CTSpriteShiftEntry.class, CTSpriteShiftEntry.class);
            sgSetter.invoke(behaviour, sgShift, sgTopShift, sgInnerShift);
        } catch (Exception e) {
            // Create's FluidTankModel will be used without more_glass/super_glass support
        }
    }
}
