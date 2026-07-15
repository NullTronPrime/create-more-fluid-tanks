package com.nulltronprime.createmorefluidtanks.registry;

import com.nulltronprime.createmorefluidtanks.CreateMoreFluidTanks;
import com.nulltronprime.createmorefluidtanks.item.MultiTankBlockItem;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModItems {

    public static final DeferredRegister.Items ITEMS =
            DeferredRegister.createItems(CreateMoreFluidTanks.MOD_ID);

    public static final DeferredItem<MultiTankBlockItem> BRASS_FLUID_TANK_ITEM = ITEMS.register(
            "brass_fluid_tank",
            () -> new MultiTankBlockItem(ModBlocks.BRASS_FLUID_TANK.get(),
                    new Item.Properties().stacksTo(64))
    );

    public static final DeferredItem<MultiTankBlockItem> STURDY_FLUID_TANK_ITEM = ITEMS.register(
            "sturdy_fluid_tank",
            () -> new MultiTankBlockItem(ModBlocks.STURDY_FLUID_TANK.get(),
                    new Item.Properties().stacksTo(64))
    );

    public static final DeferredItem<MultiTankBlockItem> ROSE_GOLD_FLUID_TANK_ITEM = ITEMS.register(
            "rose_gold_fluid_tank",
            () -> new MultiTankBlockItem(ModBlocks.ROSE_GOLD_FLUID_TANK.get(),
                    new Item.Properties().stacksTo(64))
    );

    public static final DeferredItem<MultiTankBlockItem> SILVER_FLUID_TANK_ITEM = ITEMS.register(
            "silver_fluid_tank",
            () -> new MultiTankBlockItem(ModBlocks.SILVER_FLUID_TANK.get(),
                    new Item.Properties().stacksTo(64))
    );

    public static final DeferredItem<MultiTankBlockItem> AMETHYST_FLUID_TANK_ITEM = ITEMS.register(
            "amethyst_fluid_tank",
            () -> new MultiTankBlockItem(ModBlocks.AMETHYST_FLUID_TANK.get(),
                    new Item.Properties().stacksTo(64))
    );

    public static final DeferredItem<BlockItem> BRASS_FLUID_PIPE_ITEM = ITEMS.register(
            "brass_fluid_pipe",
            () -> new BlockItem(ModBlocks.BRASS_FLUID_PIPE.get(),
                    new Item.Properties())
    );

    public static final DeferredItem<BlockItem> SILVER_FLUID_PIPE_ITEM = ITEMS.register(
            "silver_fluid_pipe",
            () -> new BlockItem(ModBlocks.SILVER_FLUID_PIPE.get(),
                    new Item.Properties())
    );

    public static final DeferredItem<BlockItem> STURDY_FLUID_PIPE_ITEM = ITEMS.register(
            "sturdy_fluid_pipe",
            () -> new BlockItem(ModBlocks.STURDY_FLUID_PIPE.get(),
                    new Item.Properties())
    );

    public static final DeferredItem<BlockItem> ROSE_GOLD_FLUID_PIPE_ITEM = ITEMS.register(
            "rose_gold_fluid_pipe",
            () -> new BlockItem(ModBlocks.ROSE_GOLD_FLUID_PIPE.get(),
                    new Item.Properties())
    );

    public static final DeferredItem<BlockItem> AMETHYST_FLUID_PIPE_ITEM = ITEMS.register(
            "amethyst_fluid_pipe",
            () -> new BlockItem(ModBlocks.AMETHYST_FLUID_PIPE.get(),
                    new Item.Properties())
    );
}
