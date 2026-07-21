package com.nulltronprime.createmorefluidtanks.item;

import com.nulltronprime.createmorefluidtanks.CreateMoreFluidTanks;
import com.nulltronprime.createmorefluidtanks.accessor.GlassStateAccessor;
import com.nulltronprime.createmorefluidtanks.blockEntity.AmethystFluidTankBlockEntity;
import com.nulltronprime.createmorefluidtanks.blockEntity.BrassFluidTankBlockEntity;
import com.nulltronprime.createmorefluidtanks.blockEntity.RoseGoldFluidTankBlockEntity;
import com.nulltronprime.createmorefluidtanks.blockEntity.SilverFluidTankBlockEntity;
import com.nulltronprime.createmorefluidtanks.blockEntity.SturdyFluidTankBlockEntity;
import com.nulltronprime.createmorefluidtanks.registry.ModBlockEntityTypes;
import com.simibubi.create.api.connectivity.ConnectivityHandler;
import com.simibubi.create.content.equipment.symmetryWand.SymmetryWandItem;
import com.simibubi.create.content.fluids.tank.FluidTankBlock;
import com.simibubi.create.content.fluids.tank.FluidTankBlockEntity;
import com.simibubi.create.foundation.block.IBE;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

import net.neoforged.neoforge.fluids.capability.IFluidHandler.FluidAction;
import net.neoforged.neoforge.fluids.FluidStack;

public class MultiTankBlockItem extends BlockItem {

	public MultiTankBlockItem(Block block, Properties properties) {
		super(block, properties);
	}

	private int getPerBlockCapacity() {
		BlockEntityType<?> beType = ((IBE<?>) this.getBlock()).getBlockEntityType();
		if (beType == ModBlockEntityTypes.STURDY_FLUID_TANK.get())
			return SturdyFluidTankBlockEntity.getStaticPerBlockCapacity();
		if (beType == ModBlockEntityTypes.SILVER_FLUID_TANK.get())
			return SilverFluidTankBlockEntity.getStaticPerBlockCapacity();
		if (beType == ModBlockEntityTypes.ROSE_GOLD_FLUID_TANK.get())
			return RoseGoldFluidTankBlockEntity.getStaticPerBlockCapacity();
		if (beType == ModBlockEntityTypes.BRASS_FLUID_TANK.get())
			return BrassFluidTankBlockEntity.getStaticPerBlockCapacity();
		if (beType == ModBlockEntityTypes.AMETHYST_FLUID_TANK.get())
			return AmethystFluidTankBlockEntity.getStaticPerBlockCapacity();
		return FluidTankBlockEntity.getCapacityMultiplier();
	}

	@Override
	public InteractionResult place(BlockPlaceContext ctx) {
		CreateMoreFluidTanks.suppressConnectivityUpdates.set(true);
		try {
			InteractionResult initialResult = super.place(ctx);
			if (!initialResult.consumesAction())
				return initialResult;
			tryMultiPlace(ctx);
			return initialResult;
		} finally {
			CreateMoreFluidTanks.suppressConnectivityUpdates.set(false);
		}
	}

	@Override
	protected boolean updateCustomBlockEntityTag(BlockPos blockPos, Level level, Player player,
		ItemStack itemStack, BlockState blockState) {
		MinecraftServer minecraftserver = level.getServer();
		if (minecraftserver == null)
			return false;
		CustomData blockEntityData = itemStack.get(DataComponents.BLOCK_ENTITY_DATA);
		if (blockEntityData != null) {
			CompoundTag nbt = blockEntityData.copyTag();
			nbt.remove("Luminosity");
			nbt.remove("Size");
			nbt.remove("Height");
			nbt.remove("Controller");
			nbt.remove("LastKnownPos");
			if (nbt.contains("TankContent")) {
				FluidStack fluid = FluidStack.parseOptional(minecraftserver.registryAccess(), nbt.getCompound("TankContent"));
				if (!fluid.isEmpty()) {
					int perBlockCap = getPerBlockCapacity();
					fluid.setAmount(Math.min(perBlockCap, fluid.getAmount()));
					nbt.put("TankContent", fluid.saveOptional(minecraftserver.registryAccess()));
				}
			}
			BlockEntity.addEntityType(nbt, ((IBE<?>) this.getBlock()).getBlockEntityType());
			itemStack.set(DataComponents.BLOCK_ENTITY_DATA, CustomData.of(nbt));
		}
		return super.updateCustomBlockEntityTag(blockPos, level, player, itemStack, blockState);
	}

	private void tryMultiPlace(BlockPlaceContext ctx) {
		Player player = ctx.getPlayer();
		if (player == null)
			return;
		if (player.isShiftKeyDown())
			return;
		Direction face = ctx.getClickedFace();
		if (!face.getAxis().isVertical())
			return;
		ItemStack stack = ctx.getItemInHand();
		Item itemType = stack.getItem();
		Level world = ctx.getLevel();
		BlockPos pos = ctx.getClickedPos();
		BlockPos placedOnPos = pos.relative(face.getOpposite());
		BlockState placedOnState = world.getBlockState(placedOnPos);

		if (!FluidTankBlock.isTank(placedOnState))
			return;
		if (SymmetryWandItem.presentInHotbar(player))
			return;

		BlockEntityType<? extends FluidTankBlockEntity> beType = ((FluidTankBlock) getBlock()).getBlockEntityType();
		FluidTankBlockEntity tankAt = ConnectivityHandler.partAt(beType, world, placedOnPos);
		if (tankAt == null)
			return;
		FluidTankBlockEntity controllerBE = tankAt.getControllerBE();
		if (controllerBE == null)
			return;

		int width = controllerBE.getWidth();
		if (width == 1)
			return;

		int tanksToPlace = 0;
		BlockPos startPos = face == Direction.DOWN
			? controllerBE.getBlockPos().below()
			: controllerBE.getBlockPos().above(controllerBE.getHeight());

		if (startPos.getY() != pos.getY())
			return;

		for (int xOffset = 0; xOffset < width; xOffset++) {
			for (int zOffset = 0; zOffset < width; zOffset++) {
				BlockPos offsetPos = startPos.offset(xOffset, 0, zOffset);
				BlockState blockState = world.getBlockState(offsetPos);
				if (FluidTankBlock.isTank(blockState))
					continue;
				if (!blockState.canBeReplaced())
					return;
				tanksToPlace++;
			}
		}

		if (!player.isCreative()) {
			int total = 0;
			for (ItemStack s : player.getInventory().items)
				if (s.getItem() == itemType)
					total += s.getCount();
			if (total < tanksToPlace)
				return;

			int maxStack = stack.getMaxStackSize();
			int inHand = stack.getCount();
			int toPull = Math.min(tanksToPlace, maxStack) - inHand;
			if (toPull > 0) {
				for (int i = 0; i < player.getInventory().items.size(); i++) {
					if (i == player.getInventory().selected)
						continue;
					ItemStack slot = player.getInventory().items.get(i);
					if (!slot.isEmpty() && slot.getItem() == itemType) {
						int take = Math.min(slot.getCount(), toPull);
						slot.shrink(take);
						stack.grow(take);
						toPull -= take;
						if (toPull <= 0)
							break;
					}
				}
			}
		}

		int placedCount = 0;
		for (int xOffset = 0; xOffset < width; xOffset++) {
			for (int zOffset = 0; zOffset < width; zOffset++) {
				BlockPos offsetPos = startPos.offset(xOffset, 0, zOffset);
				BlockState blockState = world.getBlockState(offsetPos);
				if (FluidTankBlock.isTank(blockState))
					continue;

				if (!player.isCreative() && ctx.getItemInHand().isEmpty()) {
					boolean pulled = false;
					for (int i = 0; i < player.getInventory().items.size(); i++) {
						if (i == player.getInventory().selected)
							continue;
						ItemStack slot = player.getInventory().items.get(i);
						if (!slot.isEmpty() && slot.getItem() == itemType) {
							slot.shrink(1);
							ctx.getItemInHand().setCount(1);
							pulled = true;
							break;
						}
					}
					if (!pulled)
						continue;
				}

				BlockPlaceContext context = BlockPlaceContext.at(ctx, offsetPos, face);
				player.getPersistentData().putBoolean("SilenceTankSound", true);
				super.place(context);
				player.getPersistentData().remove("SilenceTankSound");
				placedCount++;
			}
		}

		CreateMoreFluidTanks.suppressConnectivityUpdates.set(false);

		if (placedCount > 0 && face == Direction.DOWN) {
			int oldHeight = controllerBE.getHeight();
			int newHeight = oldHeight + 1;
			BlockPos oldCtrlPos = controllerBE.getBlockPos();
			BlockPos newCtrlPos = startPos;

			FluidStack fluid = controllerBE.getTankInventory().getFluid();
			int glassState = ((GlassStateAccessor) controllerBE).getGlassState();

			for (int x = 0; x < width; x++) {
				for (int z = 0; z < width; z++) {
					for (int y = 0; y < oldHeight; y++) {
						BlockPos p = oldCtrlPos.offset(x, y, z);
						BlockEntity be = world.getBlockEntity(p);
						if (be instanceof FluidTankBlockEntity tankBE) {
							tankBE.setController(newCtrlPos);
							tankBE.setHeight(newHeight);
							tankBE.setWidth(width);
							tankBE.preventConnectivityUpdate();
						}
					}
				}
			}

			for (int x = 0; x < width; x++) {
				for (int z = 0; z < width; z++) {
					BlockPos p = oldCtrlPos.offset(x, 0, z);
					BlockState bs = world.getBlockState(p);
					if (FluidTankBlock.isTank(bs))
						world.setBlock(p, bs.setValue(FluidTankBlock.BOTTOM, false), 3);
				}
			}

			for (int xOffset = 0; xOffset < width; xOffset++) {
				for (int zOffset = 0; zOffset < width; zOffset++) {
					BlockPos p = startPos.offset(xOffset, 0, zOffset);
					BlockEntity be = world.getBlockEntity(p);
					if (be instanceof FluidTankBlockEntity tankBE) {
						tankBE.setController(newCtrlPos);
						tankBE.setHeight(newHeight);
						tankBE.setWidth(width);
						tankBE.preventConnectivityUpdate();

						BlockState bs = world.getBlockState(p);
						if (FluidTankBlock.isTank(bs))
							world.setBlock(p, bs.setValue(FluidTankBlock.TOP, false)
								.setValue(FluidTankBlock.BOTTOM, true), 3);

						((GlassStateAccessor) tankBE).setGlassState(glassState);
					}
				}
			}

			BlockEntity newCtrlBE = world.getBlockEntity(newCtrlPos);
			if (newCtrlBE instanceof FluidTankBlockEntity newCtrl) {
				if (!fluid.isEmpty())
					newCtrl.getTankInventory().setFluid(fluid);
				newCtrl.setHeight(newHeight);
				newCtrl.setWidth(width);
				newCtrl.preventConnectivityUpdate();
				((GlassStateAccessor) newCtrl).setGlassState(glassState);
				newCtrl.applyFluidTankSize(width * width * newHeight);

				boolean boilerActive = newCtrl.boiler.isActive();
				if (!boilerActive)
					newCtrl.setWindows(glassState != 0);
				newCtrl.updateBoilerState();
				if (newCtrl.boiler.isActive())
					newCtrl.setWindows(false);
			}

			controllerBE.getTankInventory().setCapacity(0);
			controllerBE.getTankInventory().drain(Integer.MAX_VALUE, FluidAction.EXECUTE);
		} else if (placedCount > 0) {
			int oldHeight = controllerBE.getHeight();
			int newHeight = oldHeight + 1;
			BlockPos controllerPos = controllerBE.getBlockPos();

			for (int x = 0; x < width; x++) {
				for (int z = 0; z < width; z++) {
					BlockPos p = controllerPos.offset(x, oldHeight - 1, z);
					BlockState bs = world.getBlockState(p);
					if (FluidTankBlock.isTank(bs))
						world.setBlock(p, bs.setValue(FluidTankBlock.TOP, false), 3);
				}
			}

			for (int xOffset = 0; xOffset < width; xOffset++) {
				for (int zOffset = 0; zOffset < width; zOffset++) {
					BlockPos p = startPos.offset(xOffset, 0, zOffset);
					BlockEntity be = world.getBlockEntity(p);
					if (be instanceof FluidTankBlockEntity tankBE) {
						tankBE.setController(controllerPos);
						tankBE.setHeight(newHeight);
						tankBE.setWidth(width);
						tankBE.preventConnectivityUpdate();

						BlockState bs = world.getBlockState(p);
						if (FluidTankBlock.isTank(bs))
							world.setBlock(p, bs.setValue(FluidTankBlock.TOP, true)
								.setValue(FluidTankBlock.BOTTOM, false), 3);

						GlassStateAccessor gsCtrl = (GlassStateAccessor) controllerBE;
						((GlassStateAccessor) tankBE).setGlassState(gsCtrl.getGlassState());
					}
				}
			}

			controllerBE.setHeight(newHeight);
			controllerBE.applyFluidTankSize(width * width * newHeight);

			GlassStateAccessor gsCtrl = (GlassStateAccessor) controllerBE;
			boolean boilerActive = controllerBE.boiler.isActive();
			if (!boilerActive)
				controllerBE.setWindows(gsCtrl.getGlassState() != 0);
			controllerBE.updateBoilerState();
			if (controllerBE.boiler.isActive())
				controllerBE.setWindows(false);
		}
	}
}
