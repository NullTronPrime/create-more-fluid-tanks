package com.nulltronprime.createmorefluidtanks.client;

import com.nulltronprime.createmorefluidtanks.CreateMoreFluidTanks;
import com.nulltronprime.createmorefluidtanks.registry.ModBlockEntityTypes;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.ModelEvent;

@Mod(value = CreateMoreFluidTanks.MOD_ID, dist = Dist.CLIENT)
public class CreateMoreFluidTanksClient {

    public CreateMoreFluidTanksClient(IEventBus modEventBus, ModContainer modContainer) {
        modEventBus.addListener(FMLClientSetupEvent.class, this::clientSetup);
        modEventBus.addListener(this::registerBERs);
        modEventBus.addListener(ModelEvent.RegisterAdditional.class, ModMoreTanksClient::onRegisterAdditionalModels);
        modEventBus.addListener(ModelEvent.ModifyBakingResult.class, ModMoreTanksClient::onModelBake);
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
}
