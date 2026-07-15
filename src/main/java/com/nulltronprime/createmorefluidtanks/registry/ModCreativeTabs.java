package com.nulltronprime.createmorefluidtanks.registry;

import com.nulltronprime.createmorefluidtanks.CreateMoreFluidTanks;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModCreativeTabs {

    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, CreateMoreFluidTanks.MOD_ID);

    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> MORE_FLUID_TANKS_TAB =
            CREATIVE_MODE_TABS.register("more_fluid_tanks", () ->
                    CreativeModeTab.builder()
                            .title(Component.translatable("itemGroup." + CreateMoreFluidTanks.MOD_ID + ".more_fluid_tanks"))
                            .withTabsBefore(CreativeModeTabs.COMBAT)
                            .icon(() -> ModItems.BRASS_FLUID_TANK_ITEM.get().getDefaultInstance())
                            .displayItems((parameters, output) -> {
                                output.accept(ModItems.BRASS_FLUID_TANK_ITEM.get());
                                output.accept(ModItems.STURDY_FLUID_TANK_ITEM.get());
                                output.accept(ModItems.ROSE_GOLD_FLUID_TANK_ITEM.get());
                                output.accept(ModItems.SILVER_FLUID_TANK_ITEM.get());
                                output.accept(ModItems.BRASS_FLUID_PIPE_ITEM.get());
                                output.accept(ModItems.SILVER_FLUID_PIPE_ITEM.get());
                                output.accept(ModItems.STURDY_FLUID_PIPE_ITEM.get());
                                output.accept(ModItems.ROSE_GOLD_FLUID_PIPE_ITEM.get());
                                output.accept(ModItems.AMETHYST_FLUID_TANK_ITEM.get());
                                output.accept(ModItems.AMETHYST_FLUID_PIPE_ITEM.get());
                            })
                            .build()
            );
}
