package com.nulltronprime.createmorefluidtanks.client;

import com.nulltronprime.createmorefluidtanks.CreateMoreFluidTanks;
import dev.engine_room.flywheel.lib.model.baked.PartialModel;
import net.minecraft.resources.ResourceLocation;

import java.util.Map;

public class ModPartialModels {

    public static final Map<String, GaugeModels> GAUGE_MODELS = Map.of(
        "brass", makeGauge("brass"),
        "silver", makeGauge("silver"),
        "sturdy", makeGauge("sturdy"),
        "rose_gold", makeGauge("rose_gold"),
        "amethyst", makeGauge("amethyst")
    );

    private static GaugeModels makeGauge(String material) {
        PartialModel gauge = PartialModel.of(
            ResourceLocation.fromNamespaceAndPath(CreateMoreFluidTanks.MOD_ID, "block/" + material + "_boiler_gauge"));
        PartialModel dial = PartialModel.of(
            ResourceLocation.fromNamespaceAndPath(CreateMoreFluidTanks.MOD_ID, "block/" + material + "_boiler_gauge_dial"));
        return new GaugeModels(gauge, dial);
    }

    public record GaugeModels(PartialModel gauge, PartialModel dial) {}

    public static void init() {}
}
