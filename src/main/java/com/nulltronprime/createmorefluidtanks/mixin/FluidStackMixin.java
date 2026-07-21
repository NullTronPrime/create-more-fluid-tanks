package com.nulltronprime.createmorefluidtanks.mixin;

import org.spongepowered.asm.mixin.Implements;
import org.spongepowered.asm.mixin.Interface;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.neoforged.neoforge.fluids.FluidStack;

import com.nulltronprime.createmorefluidtanks.api.LongFluidAmount;

@Mixin(value = FluidStack.class, remap = false)
@Implements(@Interface(iface = LongFluidAmount.class, prefix = "cmft$"))
public abstract class FluidStackMixin {

    @Shadow
    private int amount;

    @Shadow
    private Fluid fluid;

    @Unique
    private long cmft_amount;

    @Inject(method = "<init>*", at = @At("TAIL"))
    private void onConstruct(CallbackInfo ci) {
        this.cmft_amount = this.amount;
    }

    @Overwrite
    public int getAmount() {
        return this.cmft_amount <= 0 ? 0 : (int) Math.min(this.cmft_amount, Integer.MAX_VALUE);
    }

    @Overwrite
    public void setAmount(int amount) {
        this.cmft_amount = amount;
        this.amount = (int) Math.min(amount, Integer.MAX_VALUE);
    }

    @Overwrite
    public void grow(int addedAmount) {
        this.cmft_amount += addedAmount;
        this.amount = (int) Math.min(this.cmft_amount, Integer.MAX_VALUE);
    }

    @Overwrite
    public void shrink(int removedAmount) {
        this.cmft_amount -= removedAmount;
        this.amount = (int) Math.max(0, Math.min(this.cmft_amount, Integer.MAX_VALUE));
    }

    @Overwrite
    public boolean isEmpty() {
        return FluidStack.EMPTY == (Object) this || this.fluid == Fluids.EMPTY || this.cmft_amount <= 0;
    }

    @Overwrite
    public FluidStack split(int amount) {
        int i = (int) Math.min(amount, this.cmft_amount);
        FluidStack fluidstack = this.copyWithAmount(i);
        this.shrink(i);
        return fluidstack;
    }

    @Overwrite
    public FluidStack copyWithAmount(int amount) {
        if (((FluidStack)(Object) this).isEmpty() && amount == 0)
            return FluidStack.EMPTY;
        FluidStack copy = ((FluidStack)(Object) this).copy();
        copy.setAmount(amount);
        return copy;
    }

    @Overwrite
    public void limitSize(int amount) {
        if (!((FluidStack)(Object) this).isEmpty() && this.cmft_amount > amount)
            setAmount(amount);
    }

    @Overwrite
    public static boolean matches(FluidStack first, FluidStack second) {
        if (first == second)
            return true;
        if (first.isEmpty() || second.isEmpty())
            return first.isEmpty() && second.isEmpty();
        long firstAmount = first.isEmpty() ? 0 : ((LongFluidAmount)(Object) first).getActualAmount();
        long secondAmount = second.isEmpty() ? 0 : ((LongFluidAmount)(Object) second).getActualAmount();
        return firstAmount != secondAmount ? false
            : FluidStack.isSameFluidSameComponents(first, second);
    }

    @Overwrite
    public FluidStack copyAndClear() {
        FluidStack copy = ((FluidStack)(Object) this).copy();
        this.cmft_amount = 0;
        this.amount = 0;
        return copy;
    }

    public long cmft$getActualAmount() {
        return this.cmft_amount;
    }

    public void cmft$setActualAmount(long amount) {
        this.cmft_amount = amount;
        this.amount = (int) Math.min(amount, Integer.MAX_VALUE);
    }
}
