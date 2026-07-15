package com.nulltronprime.createmorefluidtanks.registry;

import com.nulltronprime.createmorefluidtanks.CreateMoreFluidTanks;
import com.nulltronprime.createmorefluidtanks.blockEntity.AmethystFluidPipeBlockEntity;
import com.nulltronprime.createmorefluidtanks.blockEntity.AmethystFluidTankBlockEntity;
import com.nulltronprime.createmorefluidtanks.blockEntity.BrassFluidPipeBlockEntity;
import com.nulltronprime.createmorefluidtanks.blockEntity.BrassFluidTankBlockEntity;
import com.nulltronprime.createmorefluidtanks.blockEntity.RoseGoldFluidPipeBlockEntity;
import com.nulltronprime.createmorefluidtanks.blockEntity.RoseGoldFluidTankBlockEntity;
import com.nulltronprime.createmorefluidtanks.blockEntity.SilverFluidPipeBlockEntity;
import com.nulltronprime.createmorefluidtanks.blockEntity.SilverFluidTankBlockEntity;
import com.nulltronprime.createmorefluidtanks.blockEntity.SturdyFluidPipeBlockEntity;
import com.nulltronprime.createmorefluidtanks.blockEntity.SturdyFluidTankBlockEntity;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class ModBlockEntityTypes {

    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITY_TYPES =
            DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, CreateMoreFluidTanks.MOD_ID);

    public static final Supplier<BlockEntityType<BrassFluidTankBlockEntity>> BRASS_FLUID_TANK =
            BLOCK_ENTITY_TYPES.register("brass_fluid_tank",
                    () -> BlockEntityType.Builder.of(BrassFluidTankBlockEntity::new,
                            ModBlocks.BRASS_FLUID_TANK.get()).build(null));

    public static final Supplier<BlockEntityType<SturdyFluidTankBlockEntity>> STURDY_FLUID_TANK =
            BLOCK_ENTITY_TYPES.register("sturdy_fluid_tank",
                    () -> BlockEntityType.Builder.of(SturdyFluidTankBlockEntity::new,
                            ModBlocks.STURDY_FLUID_TANK.get()).build(null));

    public static final Supplier<BlockEntityType<RoseGoldFluidTankBlockEntity>> ROSE_GOLD_FLUID_TANK =
            BLOCK_ENTITY_TYPES.register("rose_gold_fluid_tank",
                    () -> BlockEntityType.Builder.of(RoseGoldFluidTankBlockEntity::new,
                            ModBlocks.ROSE_GOLD_FLUID_TANK.get()).build(null));

    public static final Supplier<BlockEntityType<SilverFluidTankBlockEntity>> SILVER_FLUID_TANK =
            BLOCK_ENTITY_TYPES.register("silver_fluid_tank",
                    () -> BlockEntityType.Builder.of(SilverFluidTankBlockEntity::new,
                            ModBlocks.SILVER_FLUID_TANK.get()).build(null));

    public static final Supplier<BlockEntityType<AmethystFluidTankBlockEntity>> AMETHYST_FLUID_TANK =
            BLOCK_ENTITY_TYPES.register("amethyst_fluid_tank",
                    () -> BlockEntityType.Builder.of(AmethystFluidTankBlockEntity::new,
                            ModBlocks.AMETHYST_FLUID_TANK.get()).build(null));

    public static final Supplier<BlockEntityType<BrassFluidPipeBlockEntity>> BRASS_FLUID_PIPE =
            BLOCK_ENTITY_TYPES.register("brass_fluid_pipe",
                    () -> BlockEntityType.Builder.of(BrassFluidPipeBlockEntity::new,
                            ModBlocks.BRASS_FLUID_PIPE.get()).build(null));

    public static final Supplier<BlockEntityType<SilverFluidPipeBlockEntity>> SILVER_FLUID_PIPE =
            BLOCK_ENTITY_TYPES.register("silver_fluid_pipe",
                    () -> BlockEntityType.Builder.of(SilverFluidPipeBlockEntity::new,
                            ModBlocks.SILVER_FLUID_PIPE.get()).build(null));

    public static final Supplier<BlockEntityType<SturdyFluidPipeBlockEntity>> STURDY_FLUID_PIPE =
            BLOCK_ENTITY_TYPES.register("sturdy_fluid_pipe",
                    () -> BlockEntityType.Builder.of(SturdyFluidPipeBlockEntity::new,
                            ModBlocks.STURDY_FLUID_PIPE.get()).build(null));

    public static final Supplier<BlockEntityType<RoseGoldFluidPipeBlockEntity>> ROSE_GOLD_FLUID_PIPE =
            BLOCK_ENTITY_TYPES.register("rose_gold_fluid_pipe",
                    () -> BlockEntityType.Builder.of(RoseGoldFluidPipeBlockEntity::new,
                            ModBlocks.ROSE_GOLD_FLUID_PIPE.get()).build(null));

    public static final Supplier<BlockEntityType<AmethystFluidPipeBlockEntity>> AMETHYST_FLUID_PIPE =
            BLOCK_ENTITY_TYPES.register("amethyst_fluid_pipe",
                    () -> BlockEntityType.Builder.of(AmethystFluidPipeBlockEntity::new,
                            ModBlocks.AMETHYST_FLUID_PIPE.get()).build(null));
}
