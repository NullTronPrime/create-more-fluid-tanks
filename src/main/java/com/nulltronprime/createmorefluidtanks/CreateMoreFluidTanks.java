package com.nulltronprime.createmorefluidtanks;

import com.nulltronprime.createmorefluidtanks.blockEntity.AmethystFluidTankBlockEntity;
import com.nulltronprime.createmorefluidtanks.blockEntity.BrassFluidTankBlockEntity;
import com.nulltronprime.createmorefluidtanks.blockEntity.RoseGoldFluidTankBlockEntity;
import com.nulltronprime.createmorefluidtanks.blockEntity.SilverFluidTankBlockEntity;
import com.nulltronprime.createmorefluidtanks.blockEntity.SturdyFluidTankBlockEntity;
import com.nulltronprime.createmorefluidtanks.client.ModFluidTankRenderer;
import com.nulltronprime.createmorefluidtanks.client.ModMoreTanksClient;
import com.nulltronprime.createmorefluidtanks.client.ModPartialModels;
import com.nulltronprime.createmorefluidtanks.display.PipeFlowDisplaySource;
import com.nulltronprime.createmorefluidtanks.registry.ModBlockEntityTypes;
import com.nulltronprime.createmorefluidtanks.registry.ModBlocks;
import com.nulltronprime.createmorefluidtanks.registry.ModCreativeTabs;
import com.nulltronprime.createmorefluidtanks.registry.ModItems;
import com.mojang.logging.LogUtils;
import com.simibubi.create.Create;
import com.simibubi.create.api.behaviour.display.DisplaySource;
import com.simibubi.create.api.registry.CreateBuiltInRegistries;
import com.simibubi.create.api.registry.CreateRegistries;

import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.ModelEvent;
import net.neoforged.neoforge.registries.RegisterEvent;
import org.slf4j.Logger;

@Mod(CreateMoreFluidTanks.MOD_ID)
public class CreateMoreFluidTanks {

    public static final String MOD_ID = "create_more_fluid_tanks";
    public static final Logger LOGGER = LogUtils.getLogger();

    public CreateMoreFluidTanks(IEventBus modEventBus, ModContainer modContainer) {
        ModBlocks.BLOCKS.register(modEventBus);
        ModItems.ITEMS.register(modEventBus);
        ModBlockEntityTypes.BLOCK_ENTITY_TYPES.register(modEventBus);
        ModCreativeTabs.CREATIVE_MODE_TABS.register(modEventBus);

        modEventBus.addListener(FMLClientSetupEvent.class, this::clientSetup);
        modEventBus.addListener(this::registerBERs);
        modEventBus.addListener(this::registerCapabilities);
        modEventBus.addListener(ModelEvent.RegisterAdditional.class, ModMoreTanksClient::onRegisterAdditionalModels);
        modEventBus.addListener(ModelEvent.ModifyBakingResult.class, ModMoreTanksClient::onModelBake);
        modEventBus.addListener(this::commonSetup);
        modEventBus.addListener(this::registerDisplaySources);
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
        registerBlockSources();
    }

    private void clientSetup(final FMLClientSetupEvent event) {
        ModPartialModels.init();
    }

    private void registerBERs(final EntityRenderersEvent.RegisterRenderers event) {
        event.registerBlockEntityRenderer(ModBlockEntityTypes.BRASS_FLUID_TANK.get(),
                ctx -> new ModFluidTankRenderer(ctx, "brass"));
        event.registerBlockEntityRenderer(ModBlockEntityTypes.STURDY_FLUID_TANK.get(),
                ctx -> new ModFluidTankRenderer(ctx, "sturdy"));
        event.registerBlockEntityRenderer(ModBlockEntityTypes.ROSE_GOLD_FLUID_TANK.get(),
                ctx -> new ModFluidTankRenderer(ctx, "rose_gold"));
        event.registerBlockEntityRenderer(ModBlockEntityTypes.SILVER_FLUID_TANK.get(),
                ctx -> new ModFluidTankRenderer(ctx, "silver"));
        event.registerBlockEntityRenderer(ModBlockEntityTypes.AMETHYST_FLUID_TANK.get(),
                ctx -> new ModFluidTankRenderer(ctx, "amethyst"));
    }

    private void registerCapabilities(final RegisterCapabilitiesEvent event) {
        BrassFluidTankBlockEntity.registerCapabilities(event);
        SturdyFluidTankBlockEntity.registerCapabilities(event);
        RoseGoldFluidTankBlockEntity.registerCapabilities(event);
        SilverFluidTankBlockEntity.registerCapabilities(event);
        AmethystFluidTankBlockEntity.registerCapabilities(event);
    }

    private void registerDisplaySources(final RegisterEvent event) {
        if (!event.getRegistryKey().equals(CreateRegistries.DISPLAY_SOURCE))
            return;
        event.register(CreateRegistries.DISPLAY_SOURCE,
            ResourceLocation.fromNamespaceAndPath(MOD_ID, "pipe_flow"),
            PipeFlowDisplaySource::new);
    }

    private void registerBlockSources() {
        ResourceKey<DisplaySource> boilerKey = ResourceKey.create(CreateRegistries.DISPLAY_SOURCE, Create.asResource("boiler"));
        DisplaySource boilerSource = CreateBuiltInRegistries.DISPLAY_SOURCE.get(boilerKey);

        ResourceLocation pipeFlowId = ResourceLocation.fromNamespaceAndPath(MOD_ID, "pipe_flow");
        DisplaySource pipeFlowSource = CreateBuiltInRegistries.DISPLAY_SOURCE.get(pipeFlowId);

        DisplaySource.BY_BLOCK.add(ModBlocks.BRASS_FLUID_TANK.get(), boilerSource);
        DisplaySource.BY_BLOCK.add(ModBlocks.STURDY_FLUID_TANK.get(), boilerSource);
        DisplaySource.BY_BLOCK.add(ModBlocks.ROSE_GOLD_FLUID_TANK.get(), boilerSource);
        DisplaySource.BY_BLOCK.add(ModBlocks.SILVER_FLUID_TANK.get(), boilerSource);
        DisplaySource.BY_BLOCK.add(ModBlocks.AMETHYST_FLUID_TANK.get(), boilerSource);

        DisplaySource.BY_BLOCK.add(ModBlocks.BRASS_FLUID_PIPE.get(), pipeFlowSource);
        DisplaySource.BY_BLOCK.add(ModBlocks.SILVER_FLUID_PIPE.get(), pipeFlowSource);
        DisplaySource.BY_BLOCK.add(ModBlocks.STURDY_FLUID_PIPE.get(), pipeFlowSource);
        DisplaySource.BY_BLOCK.add(ModBlocks.ROSE_GOLD_FLUID_PIPE.get(), pipeFlowSource);
        DisplaySource.BY_BLOCK.add(ModBlocks.AMETHYST_FLUID_PIPE.get(), pipeFlowSource);
    }
}
