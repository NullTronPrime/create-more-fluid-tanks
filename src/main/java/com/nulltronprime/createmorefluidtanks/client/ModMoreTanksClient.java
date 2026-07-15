package com.nulltronprime.createmorefluidtanks.client;

import com.nulltronprime.createmorefluidtanks.CreateMoreFluidTanks;
import com.nulltronprime.createmorefluidtanks.registry.ModBlocks;
import com.simibubi.create.content.fluids.tank.FluidTankModel;
import com.simibubi.create.foundation.block.connected.AllCTTypes;
import com.simibubi.create.foundation.block.connected.CTSpriteShiftEntry;
import com.simibubi.create.foundation.block.connected.CTSpriteShifter;
import com.simibubi.create.foundation.model.ModelSwapper;

import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.client.event.ModelEvent;

import java.lang.reflect.Constructor;
import java.util.List;
import java.util.Map;

public class ModMoreTanksClient {

    private static final List<String> MATERIALS = List.of("brass", "silver", "sturdy", "rose_gold", "amethyst");
    private static final List<String> PARTIAL_TYPES = List.of("connection", "rim", "drain", "rim_connector");

    public static void onRegisterAdditionalModels(ModelEvent.RegisterAdditional event) {
        for (String mat : MATERIALS) {
            event.register(new ModelResourceLocation(
                    ResourceLocation.fromNamespaceAndPath(CreateMoreFluidTanks.MOD_ID, "block/" + mat + "_engine_block"),
                    ModelResourceLocation.STANDALONE_VARIANT));

            for (String type : PARTIAL_TYPES) {
                for (Direction dir : Direction.values()) {
                    event.register(new ModelResourceLocation(
                            ResourceLocation.fromNamespaceAndPath(CreateMoreFluidTanks.MOD_ID,
                                    "block/" + mat + "_fluid_pipe/" + type + "/" + dir.getName()),
                            ModelResourceLocation.STANDALONE_VARIANT));
                }
            }
            event.register(new ModelResourceLocation(
                    ResourceLocation.fromNamespaceAndPath(CreateMoreFluidTanks.MOD_ID,
                            "block/" + mat + "_fluid_pipe/casing"),
                    ModelResourceLocation.STANDALONE_VARIANT));
        }
    }

    public static void onModelBake(ModelEvent.ModifyBakingResult event) {
        var models = event.getModels();

        MaterialPipeModel.populateCache(models, MATERIALS);

        applyFluidTankModel(models, ModBlocks.BRASS_FLUID_TANK.get(),
                "block/brass_fluid_tank");
        applyFluidTankModel(models, ModBlocks.STURDY_FLUID_TANK.get(),
                "block/sturdy_fluid_tank");
        applyFluidTankModel(models, ModBlocks.ROSE_GOLD_FLUID_TANK.get(),
                "block/rose_gold_fluid_tank");
        applyFluidTankModel(models, ModBlocks.SILVER_FLUID_TANK.get(),
                "block/silver_fluid_tank");
        applyFluidTankModel(models, ModBlocks.AMETHYST_FLUID_TANK.get(),
                "block/amethyst_fluid_tank");

        applyPipeModel(models, ModBlocks.BRASS_FLUID_PIPE.get(), "brass");
        applyPipeModel(models, ModBlocks.SILVER_FLUID_PIPE.get(), "silver");
        applyPipeModel(models, ModBlocks.STURDY_FLUID_PIPE.get(), "sturdy");
        applyPipeModel(models, ModBlocks.ROSE_GOLD_FLUID_PIPE.get(), "rose_gold");
        applyPipeModel(models, ModBlocks.AMETHYST_FLUID_PIPE.get(), "amethyst");

        applySteamEngineModel(models);
    }

    private static void applySteamEngineModel(Map<ModelResourceLocation, BakedModel> models) {
        Block steamEngineBlock = BuiltInRegistries.BLOCK.get(ResourceLocation.parse("create:steam_engine"));
        if (steamEngineBlock == null) return;

        List<ModelResourceLocation> locations = ModelSwapper.getAllBlockStateModelLocations(steamEngineBlock);
        for (ModelResourceLocation loc : locations) {
            BakedModel original = models.get(loc);
            if (original != null) {
                models.put(loc, new MaterialEngineModel(original));
            }
        }
    }

    private static void applyPipeModel(Map<ModelResourceLocation, BakedModel> models,
                                        net.minecraft.world.level.block.Block block, String material) {
        List<ModelResourceLocation> locations = ModelSwapper.getAllBlockStateModelLocations(block);
        ModelSwapper.swapModels(models, locations, original -> {
            return new MaterialPipeModel(original, material);
        });
    }

    private static void applyFluidTankModel(Map<ModelResourceLocation, BakedModel> models,
                                             net.minecraft.world.level.block.Block block, String textureBase) {
        List<ModelResourceLocation> locations = ModelSwapper.getAllBlockStateModelLocations(block);
        var shift = getCT(textureBase, textureBase + "_connected");
        var topShift = getCT(textureBase + "_top", textureBase + "_top_connected");
        var innerShift = getCT(textureBase + "_inner", textureBase + "_inner_connected");
        ModelSwapper.swapModels(models, locations, original -> {
            try {
                Constructor<FluidTankModel> ctor = FluidTankModel.class.getDeclaredConstructor(
                        BakedModel.class, CTSpriteShiftEntry.class, CTSpriteShiftEntry.class, CTSpriteShiftEntry.class);
                ctor.setAccessible(true);
                return ctor.newInstance(original, shift, topShift, innerShift);
            } catch (Exception e) {
                CreateMoreFluidTanks.LOGGER.error("Failed to create FluidTankModel for " + textureBase, e);
                return original;
            }
        });
    }

    private static CTSpriteShiftEntry getCT(String base, String connected) {
        return CTSpriteShifter.getCT(AllCTTypes.RECTANGLE,
                ResourceLocation.fromNamespaceAndPath(CreateMoreFluidTanks.MOD_ID, base),
                ResourceLocation.fromNamespaceAndPath(CreateMoreFluidTanks.MOD_ID, connected));
    }
}
