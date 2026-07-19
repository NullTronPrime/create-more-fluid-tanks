package com.nulltronprime.createmorefluidtanks.mixin;

import java.lang.reflect.Field;
import java.util.Arrays;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.simibubi.create.content.fluids.tank.FluidTankBlock.Shape;

import sun.misc.Unsafe;

@Mixin(Shape.class)
public class FluidTankShapeMixin {

    @Inject(method = "<clinit>", at = @At("TAIL"))
    private static void addMoreGlassShapes(CallbackInfo ci) {
        try {
            Field valuesField = Shape.class.getDeclaredField("$VALUES");
            Field unsafeField = Unsafe.class.getDeclaredField("theUnsafe");
            unsafeField.setAccessible(true);
            Unsafe unsafe = (Unsafe) unsafeField.get(null);

            Shape[] old = (Shape[]) unsafe.getObject(unsafe.staticFieldBase(valuesField),
                    unsafe.staticFieldOffset(valuesField));
            Shape[] newArray = Arrays.copyOf(old, old.length + 2);

            Field nameField = Enum.class.getDeclaredField("name");
            Field ordinalField = Enum.class.getDeclaredField("ordinal");
            long nameOffset = unsafe.objectFieldOffset(nameField);
            long ordinalOffset = unsafe.objectFieldOffset(ordinalField);

            Shape moreGlass = (Shape) unsafe.allocateInstance(Shape.class);
            unsafe.putObject(moreGlass, nameOffset, "MORE_GLASS");
            unsafe.putInt(moreGlass, ordinalOffset, old.length);
            newArray[old.length] = moreGlass;

            Shape superGlass = (Shape) unsafe.allocateInstance(Shape.class);
            unsafe.putObject(superGlass, nameOffset, "SUPER_GLASS");
            unsafe.putInt(superGlass, ordinalOffset, old.length + 1);
            newArray[old.length + 1] = superGlass;

            unsafe.putObject(unsafe.staticFieldBase(valuesField), unsafe.staticFieldOffset(valuesField), newArray);
        } catch (Exception e) {
            throw new RuntimeException("Failed to add MORE_GLASS/SUPER_GLASS shapes", e);
        }
    }
}
