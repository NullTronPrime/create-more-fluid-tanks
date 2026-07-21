package com.nulltronprime.createmorefluidtanks.blockEntity;

import java.util.List;

import com.nulltronprime.createmorefluidtanks.api.LongFluidTank;
import com.nulltronprime.createmorefluidtanks.accessor.GlassStateAccessor;
import com.nulltronprime.createmorefluidtanks.registry.ModBlockEntityTypes;
import com.simibubi.create.content.fluids.tank.FluidTankBlock;
import com.simibubi.create.content.fluids.tank.FluidTankBlock.Shape;
import com.simibubi.create.content.fluids.tank.FluidTankBlockEntity;
import com.simibubi.create.foundation.fluid.SmartFluidTank;

import net.createmod.catnip.animation.LerpedFloat;
import net.createmod.catnip.animation.LerpedFloat.Chaser;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.FluidType;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.fluids.capability.IFluidHandler.FluidAction;
import com.simibubi.create.foundation.utility.CreateLang;
import net.minecraft.ChatFormatting;
import net.neoforged.neoforge.fluids.capability.templates.FluidTank;

public class SilverFluidTankBlockEntity extends FluidTankBlockEntity {

    private static final int MAX_TANK_SIZE = 7;
    private static final int CAPACITY_MULTIPLIER = 3;
    private static final float HEAT_MULTIPLIER = 2.25f;

    public static void registerCapabilities(RegisterCapabilitiesEvent event) {
        event.registerBlockEntity(
                Capabilities.FluidHandler.BLOCK,
                ModBlockEntityTypes.SILVER_FLUID_TANK.get(),
                (be, context) -> {
                    if (be.isController()) {
                        if (be.boiler.isActive())
                            return be.boiler.createHandler();
                        return be.getTankInventory();
                    }
                    FluidTankBlockEntity controller = be.getControllerBE();
                    if (controller != null) {
                        if (controller.boiler.isActive())
                            return controller.boiler.createHandler();
                        return controller.getTankInventory();
                    }
                    return new FluidTank(0);
                }
        );
    }

    public SilverFluidTankBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
        boiler = new ModBoilerData(HEAT_MULTIPLIER);
    }

    public SilverFluidTankBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntityTypes.SILVER_FLUID_TANK.get(), pos, state);
        boiler = new ModBoilerData(HEAT_MULTIPLIER);
    }

    @Override
    public int getMaxWidth() {
        return MAX_TANK_SIZE;
    }

    public static int maxHeight() {
        return 50;
    }

    @Override
    public int getMaxLength(Direction.Axis axis, int w) {
        if (axis == Direction.Axis.Y)
            return maxHeight();
        return getMaxWidth();
    }

    public int getPerBlockCapacity() {
        return getCapacityMultiplier() * CAPACITY_MULTIPLIER;
    }

    public static int getStaticPerBlockCapacity() {
        return FluidTankBlockEntity.getCapacityMultiplier() * CAPACITY_MULTIPLIER;
    }

    private int cmft_lastLuminosity = -1;
    private boolean cmft_lastReversed = false;

    @Override
    protected SmartFluidTank createInventory() {
        return new SmartFluidTank(getPerBlockCapacity(), this::onFluidStackChanged);
    }

    @Override
    protected void onFluidStackChanged(FluidStack newFluidStack) {
        if (!hasLevel())
            return;

        FluidType attributes = newFluidStack.getFluid().getFluidType();
        int luminosity = (int) (attributes.getLightLevel(newFluidStack) / 1.2f);
        boolean reversed = attributes.isLighterThanAir();

        if (luminosity != cmft_lastLuminosity || reversed != cmft_lastReversed) {
            cmft_lastLuminosity = luminosity;
            cmft_lastReversed = reversed;
            super.onFluidStackChanged(newFluidStack);
            return;
        }

        if (!level.isClientSide) {
            setChanged();
            sendData();
        }

        if (isVirtual()) {
            if (getFluidLevel() == null)
                setFluidLevel(LerpedFloat.linear().startWithValue(getFillState()));
            getFluidLevel().chase(getFillState(), 0.5f, Chaser.EXP);
        }
    }

    @Override
    public void applyFluidTankSize(int blocks) {
        LongFluidTank lft = (LongFluidTank)(Object) tankInventory;
        lft.setActualCapacity((long) blocks * getPerBlockCapacity());
        long overflow = lft.getActualAmount() - lft.getActualCapacity();
        if (overflow > 0)
            tankInventory.drain((int) Math.min(overflow, Integer.MAX_VALUE), FluidAction.EXECUTE);
        forceFluidLevelUpdate = true;
    }

    @Override
    public int getTankSize(int tank) {
        return getPerBlockCapacity();
    }

    @Override
    public float getFillState() {
        LongFluidTank lft = (LongFluidTank)(Object) tankInventory;
        long amount = lft.getActualAmount();
        long capacity = lft.getActualCapacity();
        if (capacity <= 0) return 0;
        return (float) ((double) amount / (double) capacity);
    }

    @Override
    protected void read(CompoundTag compound, HolderLookup.Provider registries, boolean clientPacket) {
        super.read(compound, registries, clientPacket);
        if (isController()) {
            LongFluidTank lft = (LongFluidTank)(Object) tankInventory;
            lft.setActualCapacity((long) getTotalTankSize() * getPerBlockCapacity());
            if (lft.getActualCapacity() < lft.getActualAmount())
                tankInventory.drain((int) Math.min(lft.getActualAmount() - lft.getActualCapacity(), Integer.MAX_VALUE), FluidAction.EXECUTE);
            if (clientPacket) {
                float fillState = getFillState();
                if (getFluidLevel() == null)
                    setFluidLevel(LerpedFloat.linear().startWithValue(fillState));
                getFluidLevel().chase(fillState, 0.5f, Chaser.EXP);
            }
        }
    }

    @Override
    public void setWindows(boolean window) {
        this.window = window;

        GlassStateAccessor accessor = (GlassStateAccessor) this;
        int gs = accessor.getGlassState();
        boolean useSpecialGlass = window && (gs == 2 || gs == 3);
        Shape specialShape = useSpecialGlass
            ? Shape.valueOf(gs == 3 ? "SUPER_GLASS" : "MORE_GLASS")
            : null;

        for (int yOffset = 0; yOffset < height; yOffset++) {
            for (int xOffset = 0; xOffset < width; xOffset++) {
                for (int zOffset = 0; zOffset < width; zOffset++) {
                    if (width > 2 && xOffset > 0 && xOffset < width - 1
                        && zOffset > 0 && zOffset < width - 1
                        && yOffset > 0 && yOffset < height - 1)
                        continue;

                    BlockPos pos = this.worldPosition.offset(xOffset, yOffset, zOffset);
                    BlockState blockState = level.getBlockState(pos);
                    if (!FluidTankBlock.isTank(blockState))
                        continue;

                    Shape shape;
                    if (useSpecialGlass) {
                        shape = specialShape;
                    } else if (window) {
                        if (width == 1) {
                            shape = Shape.WINDOW;
                        } else if (width == 2) {
                            if (xOffset == 0 && zOffset == 0) shape = Shape.WINDOW_NW;
                            else if (xOffset == 0 && zOffset == 1) shape = Shape.WINDOW_SW;
                            else if (xOffset == 1 && zOffset == 0) shape = Shape.WINDOW_NE;
                            else shape = Shape.WINDOW_SE;
                        } else {
                            int perSide = Math.round(width / 3f) + Math.round((width % 3) / 2f);
                            int start = (width - perSide) / 2;
                            boolean onNorth = zOffset == 0 && xOffset >= start && xOffset < start + perSide;
                            boolean onSouth = zOffset == width - 1 && xOffset >= start && xOffset < start + perSide;
                            boolean onWest = xOffset == 0 && zOffset >= start && zOffset < start + perSide;
                            boolean onEast = xOffset == width - 1 && zOffset >= start && zOffset < start + perSide;
                            if (onNorth || onSouth || onWest || onEast)
                                shape = Shape.WINDOW;
                            else
                                shape = Shape.PLAIN;
                        }
                    } else {
                        shape = Shape.PLAIN;
                    }

                    Shape current = blockState.getValue(FluidTankBlock.SHAPE);
                    if (current == shape)
                        continue;

                    level.setBlock(pos, blockState.setValue(FluidTankBlock.SHAPE, shape),
                            Block.UPDATE_CLIENTS | Block.UPDATE_INVISIBLE | Block.UPDATE_KNOWN_SHAPE);
                    level.getChunkSource().getLightEngine().checkBlock(pos);
                }
            }
        }
        sendData();
    }

    private static String fmt(long mb) {
        if (mb >= 10_000_000) {
            String s = String.format("%,.3f", mb / 1000.0);
            s = s.replaceAll("0+$", "").replaceAll("\\.$", "");
            return s + " B";
        }
        return String.format("%,d", mb) + " mB";
    }

    @Override
    public boolean addToGoggleTooltip(List<Component> tooltip, boolean isPlayerSneaking) {
        FluidTankBlockEntity controllerBE = getControllerBE();
        if (controllerBE == null)
            return false;
        if (controllerBE.boiler.addToGoggleTooltip(tooltip, isPlayerSneaking, controllerBE.getTotalTankSize()))
            return true;

        IFluidHandler handler = level.getCapability(Capabilities.FluidHandler.BLOCK,
            controllerBE.getBlockPos(), null);
        if (handler == null || handler.getTanks() == 0)
            return false;

        boolean isLong = handler instanceof LongFluidTank lft;
        long actualAmount = isLong ? ((LongFluidTank) handler).getActualAmount()
            : handler.getFluidInTank(0).getAmount();
        long actualCapacity = isLong ? ((LongFluidTank) handler).getActualCapacity()
            : handler.getTankCapacity(0);
        FluidStack fluidStack = handler.getFluidInTank(0);

        CreateLang.translate("gui.goggles.fluid_container")
            .forGoggles(tooltip);

        if (!fluidStack.isEmpty()) {
            CreateLang.fluidName(fluidStack)
                .style(ChatFormatting.GRAY)
                .forGoggles(tooltip, 1);

            CreateLang.builder()
                .add(Component.literal(fmt(actualAmount)).withStyle(ChatFormatting.GOLD))
                .text(ChatFormatting.GRAY, " / ")
                .add(Component.literal(fmt(actualCapacity)).withStyle(ChatFormatting.DARK_GRAY))
                .forGoggles(tooltip, 1);
        } else {
            CreateLang.translate("gui.goggles.fluid_container.capacity")
                .add(Component.literal(fmt(actualCapacity)).withStyle(ChatFormatting.GOLD))
                .style(ChatFormatting.GRAY)
                .forGoggles(tooltip, 1);
        }
        return true;
    }
}
