package com.nulltronprime.createmorefluidtanks.client;

import com.simibubi.create.content.kinetics.steamEngine.SteamEngineBlock;
import com.simibubi.create.foundation.model.BakedModelHelper;
import com.simibubi.create.foundation.model.BakedModelWrapperWithData;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.client.model.data.ModelData;
import net.neoforged.neoforge.client.model.data.ModelData.Builder;
import net.neoforged.neoforge.client.model.data.ModelProperty;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;

public class MaterialEngineModel extends BakedModelWrapperWithData {

    private static final ModelProperty<String> MATERIAL_PROPERTY = new ModelProperty<>();
    private static final ResourceLocation CREATE_ENGINE_SPRITE = ResourceLocation.parse("create:block/engine");

    private static final Map<String, String> BLOCK_TO_MATERIAL = Map.of(
        "brass_fluid_tank", "brass",
        "silver_fluid_tank", "silver",
        "sturdy_fluid_tank", "sturdy",
        "rose_gold_fluid_tank", "rose_gold",
        "amethyst_fluid_tank", "amethyst"
    );

    public MaterialEngineModel(BakedModel wrapped) {
        super(wrapped);
    }

    @Override
    protected ModelData.Builder gatherModelData(Builder builder, BlockAndTintGetter level, BlockPos pos,
                                                BlockState state, ModelData modelData) {
        String material = resolveMaterial(level, pos, state);
        return builder.with(MATERIAL_PROPERTY, material);
    }

    @Override
    public List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction side, RandomSource rand,
                                    ModelData data, @Nullable RenderType renderType) {
        String material = data.get(MATERIAL_PROPERTY);
        if (material == null || material.equals("default")) {
            return super.getQuads(state, side, rand, data, renderType);
        }

        TextureAtlasSprite materialSprite = Minecraft.getInstance()
                .getModelManager().getAtlas(InventoryMenu.BLOCK_ATLAS)
                .getSprite(ResourceLocation.fromNamespaceAndPath(
                        "create_more_fluid_tanks", "block/" + material + "_engine"));

        if (materialSprite == null || materialSprite.contents().name()
                .equals(ResourceLocation.parse("minecraft:missingno"))) {
            return super.getQuads(state, side, rand, data, renderType);
        }

        return BakedModelHelper.swapSprites(
                super.getQuads(state, side, rand, data, renderType),
                sprite -> sprite.contents().name().equals(CREATE_ENGINE_SPRITE) ? materialSprite : null
        );
    }

    private static String resolveMaterial(BlockAndTintGetter level, BlockPos pos, BlockState state) {
        try {
            Direction connected = SteamEngineBlock.getConnectedDirection(state);
            BlockPos[] candidates = {
                pos.relative(connected),
                pos.relative(connected.getOpposite())
            };
            for (BlockPos tankPos : candidates) {
                BlockState tankState = level.getBlockState(tankPos);
                Block tankBlock = tankState.getBlock();
                if (tankBlock != null) {
                    String path = BuiltInRegistries.BLOCK.getKey(tankBlock).getPath();
                    String material = BLOCK_TO_MATERIAL.get(path);
                    if (material != null) return material;
                }
            }
            return "default";
        } catch (Exception e) {
            return "default";
        }
    }
}
