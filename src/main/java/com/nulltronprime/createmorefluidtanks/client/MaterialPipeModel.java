package com.nulltronprime.createmorefluidtanks.client;

import com.simibubi.create.content.fluids.FluidTransportBehaviour;
import com.simibubi.create.content.fluids.FluidTransportBehaviour.AttachmentTypes;
import com.simibubi.create.content.fluids.FluidTransportBehaviour.AttachmentTypes.ComponentPartials;
import com.simibubi.create.content.fluids.pipes.FluidPipeBlock;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import com.simibubi.create.foundation.model.BakedModelWrapperWithData;
import net.createmod.catnip.lang.Lang;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.client.model.data.ModelData;
import net.neoforged.neoforge.client.model.data.ModelData.Builder;
import net.neoforged.neoforge.client.model.data.ModelProperty;
import net.neoforged.neoforge.common.util.TriState;
import net.createmod.catnip.data.Iterate;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class MaterialPipeModel extends BakedModelWrapperWithData {

    private static final ModelProperty<PipeModelData> PIPE_PROPERTY = new ModelProperty<>();
    private static final ModelProperty<String> MATERIAL_PROPERTY = new ModelProperty<>();

    private static final String[] PARTIAL_TYPES = {"connection", "rim", "drain", "rim_connector"};

    private static Map<String, Map<String, Map<Direction, BakedModel>>> partialCache = new HashMap<>();

    private final String material;

    public static void populateCache(Map<ModelResourceLocation, BakedModel> modelMap, List<String> materials) {
        for (String mat : materials) {
            Map<String, Map<Direction, BakedModel>> typeMap = new HashMap<>();
            for (String type : PARTIAL_TYPES) {
                Map<Direction, BakedModel> dirMap = new HashMap<>();
                for (Direction dir : Direction.values()) {
                    ModelResourceLocation mrl = new ModelResourceLocation(
                            ResourceLocation.fromNamespaceAndPath("create_more_fluid_tanks",
                                    "block/" + mat + "_fluid_pipe/" + type + "/" + dir.getName()),
                            ModelResourceLocation.STANDALONE_VARIANT);
                    BakedModel model = modelMap.get(mrl);
                    if (model != null) {
                        dirMap.put(dir, model);
                    }
                }
                typeMap.put(type, dirMap);
            }
            partialCache.put(mat, typeMap);
        }
    }

    public MaterialPipeModel(BakedModel wrapped, String material) {
        super(wrapped);
        this.material = material;
    }

    @Override
    protected ModelData.Builder gatherModelData(Builder builder, BlockAndTintGetter world, BlockPos pos,
                                                BlockState state, ModelData blockEntityData) {
        PipeModelData data = new PipeModelData();
        FluidTransportBehaviour transport = BlockEntityBehaviour.get(world, pos, FluidTransportBehaviour.TYPE);

        if (transport != null)
            for (Direction d : Iterate.directions)
                data.putAttachment(d, transport.getRenderedRimAttachment(world, pos, state, d));

        data.setEncased(FluidPipeBlock.shouldDrawCasing(world, pos, state));

        return builder.with(PIPE_PROPERTY, data).with(MATERIAL_PROPERTY, material);
    }

    @Override
    public List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction side, RandomSource rand,
                                    ModelData data, @Nullable RenderType renderType) {
        List<BakedQuad> quads = new ArrayList<>(super.getQuads(state, side, rand, data, renderType));

        String mat = data.get(MATERIAL_PROPERTY);
        if (mat == null) mat = material;

        PipeModelData pipeData = data.get(PIPE_PROPERTY);
        if (pipeData != null) {
            Map<String, Map<Direction, BakedModel>> matCache = partialCache.get(mat);
            if (matCache != null) {
                for (Direction d : Iterate.directions) {
                    AttachmentTypes type = pipeData.getAttachment(d);
                    for (ComponentPartials partial : type.partials) {
                        String partialName = Lang.asId(partial.name());
                        Map<Direction, BakedModel> dirMap = matCache.get(partialName);
                        if (dirMap != null) {
                            BakedModel partialModel = dirMap.get(d);
                            if (partialModel != null) {
                                quads.addAll(partialModel.getQuads(state, side, rand, data, renderType));
                            }
                        }
                    }
                }
            }

            if (pipeData.isEncased()) {
                ModelResourceLocation casingMRL = new ModelResourceLocation(
                        ResourceLocation.fromNamespaceAndPath("create_more_fluid_tanks",
                                "block/" + mat + "_fluid_pipe/casing"),
                        ModelResourceLocation.STANDALONE_VARIANT);
                BakedModel casingModel = Minecraft.getInstance().getModelManager().getModel(casingMRL);
                if (casingModel != null) {
                    quads.addAll(casingModel.getQuads(state, side, rand, data, renderType));
                }
            }
        }

        return quads;
    }

    @Override
    public TriState useAmbientOcclusion(BlockState state, ModelData data, RenderType renderType) {
        return TriState.TRUE;
    }

    @Override
    public boolean useAmbientOcclusion() {
        return true;
    }

    private static class PipeModelData {
        private AttachmentTypes[] attachments;
        private boolean encased;

        public PipeModelData() {
            attachments = new AttachmentTypes[6];
            Arrays.fill(attachments, AttachmentTypes.NONE);
        }

        public void putAttachment(Direction face, AttachmentTypes rim) {
            attachments[face.get3DDataValue()] = rim;
        }

        public AttachmentTypes getAttachment(Direction face) {
            return attachments[face.get3DDataValue()];
        }

        public void setEncased(boolean encased) {
            this.encased = encased;
        }

        public boolean isEncased() {
            return encased;
        }
    }
}
