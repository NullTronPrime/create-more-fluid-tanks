package com.nulltronprime.createmorefluidtanks.mixin;

import java.util.function.Predicate;

import org.spongepowered.asm.mixin.Implements;
import org.spongepowered.asm.mixin.Interface;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler.FluidAction;
import net.neoforged.neoforge.fluids.capability.templates.FluidTank;

import com.nulltronprime.createmorefluidtanks.api.LongFluidAmount;
import com.nulltronprime.createmorefluidtanks.api.LongFluidTank;

@Mixin(value = FluidTank.class, remap = false)
@Implements(@Interface(iface = LongFluidTank.class, prefix = "cmft$"))
public abstract class FluidTankMixin {

    @Shadow
    protected FluidStack fluid;

    @Shadow
    protected int capacity;

    @Shadow
    protected Predicate<FluidStack> validator;

    @Shadow
    protected abstract void onContentsChanged();

    @Shadow
    public abstract boolean isFluidValid(int tank, FluidStack stack);

    @Unique
    private long cmft_actualCapacity;

    @Inject(method = "<init>(ILjava/util/function/Predicate;)V", at = @At("TAIL"))
    private void onInitCapacity(int capacity, Predicate<FluidStack> validator, CallbackInfo ci) {
        this.cmft_actualCapacity = capacity;
    }

    @Overwrite
    public FluidTank setCapacity(int capacity) {
        this.capacity = (int) Math.min(capacity, Integer.MAX_VALUE);
        this.cmft_actualCapacity = capacity;
        return (FluidTank)(Object) this;
    }

    @Overwrite
    public int getCapacity() {
        return (int) Math.min(this.cmft_actualCapacity, Integer.MAX_VALUE);
    }

    @Overwrite
    public int getSpace() {
        long currentAmount = ((LongFluidAmount)(Object) fluid).getActualAmount();
        return (int) Math.min(Math.max(0, cmft_actualCapacity - currentAmount), Integer.MAX_VALUE);
    }

    @Overwrite
    public int fill(FluidStack resource, FluidAction action) {
        if (resource.isEmpty() || !isFluidValid(0, resource))
            return 0;

        long currentAmount = ((LongFluidAmount)(Object) fluid).getActualAmount();
        long space = cmft_actualCapacity - currentAmount;
        int toFill = (int) Math.min(resource.getAmount(), Math.min(space, Integer.MAX_VALUE));

        if (!action.simulate()) {
            long newAmount = currentAmount + toFill;
            if (fluid.isEmpty()) {
                fluid = resource.copyWithAmount(toFill);
            } else {
                fluid.grow(toFill);
            }
            ((LongFluidAmount)(Object) fluid).setActualAmount(newAmount);
            fluid.setAmount((int) Math.min(newAmount, Integer.MAX_VALUE));
            onContentsChanged();
        }

        return toFill;
    }

    @Overwrite
    public FluidStack drain(int maxDrain, FluidAction action) {
        if (fluid.isEmpty() || maxDrain <= 0)
            return FluidStack.EMPTY;

        long currentAmount = ((LongFluidAmount)(Object) fluid).getActualAmount();
        long drained = Math.min(maxDrain, currentAmount);
        int intDrained = (int) Math.min(drained, Integer.MAX_VALUE);

        FluidStack result = fluid.copyWithAmount(intDrained);

        if (!action.simulate()) {
            long newAmount = currentAmount - drained;
            ((LongFluidAmount)(Object) fluid).setActualAmount(Math.max(0, newAmount));
            fluid.setAmount((int) Math.min(Math.max(0, newAmount), Integer.MAX_VALUE));
            if (newAmount <= 0)
                fluid = FluidStack.EMPTY;
            onContentsChanged();
        }

        return result;
    }

    public long cmft$getActualCapacity() {
        return cmft_actualCapacity;
    }

    public void cmft$setActualCapacity(long capacity) {
        this.cmft_actualCapacity = capacity;
        this.capacity = (int) Math.min(capacity, Integer.MAX_VALUE);
    }

    public long cmft$getActualAmount() {
        return ((LongFluidAmount)(Object) fluid).getActualAmount();
    }
}
