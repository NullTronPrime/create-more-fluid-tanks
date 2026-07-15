package com.nulltronprime.createmorefluidtanks.blockEntity;

import java.util.List;

import com.nulltronprime.createmorefluidtanks.registry.ModBlockEntityTypes;
import com.simibubi.create.content.fluids.tank.FluidTankBlock;
import com.simibubi.create.content.fluids.tank.FluidTankBlock.Shape;
import com.simibubi.create.content.fluids.tank.FluidTankBlockEntity;
import com.simibubi.create.foundation.fluid.SmartFluidTank;

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
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.fluids.capability.IFluidHandler.FluidAction;
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

    @Override
    public int getMaxLength(Direction.Axis axis, int w) {
        if (axis == Direction.Axis.Y)
            return getMaxHeight();
        return getMaxWidth();
    }

    public int getPerBlockCapacity() {
        return getCapacityMultiplier() * CAPACITY_MULTIPLIER;
    }

    @Override
    protected SmartFluidTank createInventory() {
        return new SmartFluidTank(getPerBlockCapacity(), this::onFluidStackChanged);
    }

    @Override
    public void applyFluidTankSize(int blocks) {
        tankInventory.setCapacity(blocks * getPerBlockCapacity());
        int overflow = tankInventory.getFluidAmount() - tankInventory.getCapacity();
        if (overflow > 0)
            tankInventory.drain(overflow, FluidAction.EXECUTE);
        forceFluidLevelUpdate = true;
    }

    @Override
    public int getTankSize(int tank) {
        return getPerBlockCapacity();
    }

    @Override
    protected void read(CompoundTag compound, HolderLookup.Provider registries, boolean clientPacket) {
        super.read(compound, registries, clientPacket);
        if (isController()) {
            tankInventory.setCapacity(getTotalTankSize() * getPerBlockCapacity());
            if (tankInventory.getSpace() < 0)
                tankInventory.drain(-tankInventory.getSpace(), FluidAction.EXECUTE);
        }
    }

    @Override
    public void setWindows(boolean window) {
        this.window = window;
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

                    Shape shape = Shape.PLAIN;
                    if (window) {
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
                        }
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

    @Override
    public boolean addToGoggleTooltip(List<Component> tooltip, boolean isPlayerSneaking) {
        FluidTankBlockEntity controllerBE = getControllerBE();
        if (controllerBE == null)
            return false;
        if (controllerBE.boiler.addToGoggleTooltip(tooltip, isPlayerSneaking, controllerBE.getTotalTankSize()))
            return true;
        return containedFluidTooltip(tooltip, isPlayerSneaking,
                level.getCapability(Capabilities.FluidHandler.BLOCK, controllerBE.getBlockPos(), null));
    }
}
