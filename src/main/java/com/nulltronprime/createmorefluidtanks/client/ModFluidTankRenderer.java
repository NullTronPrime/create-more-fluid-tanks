package com.nulltronprime.createmorefluidtanks.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.nulltronprime.createmorefluidtanks.CreateMoreFluidTanks;
import com.simibubi.create.content.fluids.tank.FluidTankBlockEntity;
import com.simibubi.create.content.fluids.tank.FluidTankRenderer;
import dev.engine_room.flywheel.lib.transform.TransformStack;
import net.createmod.catnip.data.Iterate;
import net.createmod.catnip.render.CachedBuffers;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;

public class ModFluidTankRenderer extends FluidTankRenderer {

    private final String material;

    public ModFluidTankRenderer(BlockEntityRendererProvider.Context context, String material) {
        super(context);
        this.material = material;
    }

    @Override
    protected void renderAsBoiler(FluidTankBlockEntity be, float partialTicks, PoseStack ms,
                                   MultiBufferSource buffer, int light, int overlay) {
        BlockState blockState = be.getBlockState();
        VertexConsumer vb = buffer.getBuffer(RenderType.cutout());
        ms.pushPose();
        var msr = TransformStack.of(ms);
        float w = be.getWidth();
        msr.translate(w / 2f, 0.5, w / 2f);

        float dialPivotY = 6f / 16;
        float dialPivotZ = 8f / 16;
        float progress = be.boiler.gauge.getValue(partialTicks);

        var models = ModPartialModels.GAUGE_MODELS.get(material);
        if (models == null) {
            super.renderAsBoiler(be, partialTicks, ms, buffer, light, overlay);
            ms.popPose();
            return;
        }

        for (Direction d : Iterate.horizontalDirections) {
            if (be.boiler.occludedDirections[d.get2DDataValue()])
                continue;
            ms.pushPose();
            float yRot = -d.toYRot() - 90;
            CachedBuffers.partial(models.gauge(), blockState)
                .rotateYDegrees(yRot)
                .uncenter()
                .translate(w / 2f - 6 / 16f, 0, 0)
                .light(light)
                .renderInto(ms, vb);
            CachedBuffers.partial(models.dial(), blockState)
                .rotateYDegrees(yRot)
                .uncenter()
                .translate(w / 2f - 6 / 16f, 0, 0)
                .translate(0, dialPivotY, dialPivotZ)
                .rotateXDegrees(-145 * progress + 90)
                .translate(0, -dialPivotY, -dialPivotZ)
                .light(light)
                .renderInto(ms, vb);
            ms.popPose();
        }

        ms.popPose();
    }
}
