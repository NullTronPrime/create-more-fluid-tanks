package com.nulltronprime.createmorefluidtanks.config;

import net.neoforged.neoforge.common.ModConfigSpec;

public class CMoreTanksPipeConfig {

    public static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();

    public static final ModConfigSpec.DoubleValue BRASS_PROPAGATION = BUILDER
        .comment("Brass fluid pipe propagation multiplier")
        .defineInRange("brassPropagationMultiplier", 1.2, 0.1, 100.0);

    public static final ModConfigSpec.DoubleValue BRASS_FLOW = BUILDER
        .comment("Brass fluid pipe flow multiplier")
        .defineInRange("brassFlowMultiplier", 1.75, 0.1, 100.0);

    public static final ModConfigSpec.DoubleValue SILVER_PROPAGATION = BUILDER
        .comment("Silver fluid pipe propagation multiplier")
        .defineInRange("silverPropagationMultiplier", 2.0, 0.1, 100.0);

    public static final ModConfigSpec.DoubleValue SILVER_FLOW = BUILDER
        .comment("Silver fluid pipe flow multiplier")
        .defineInRange("silverFlowMultiplier", 2.25, 0.1, 100.0);

    public static final ModConfigSpec.DoubleValue STURDY_PROPAGATION = BUILDER
        .comment("Sturdy fluid pipe propagation multiplier")
        .defineInRange("sturdyPropagationMultiplier", 1.5, 0.1, 100.0);

    public static final ModConfigSpec.DoubleValue STURDY_FLOW = BUILDER
        .comment("Sturdy fluid pipe flow multiplier")
        .defineInRange("sturdyFlowMultiplier", 1.25, 0.1, 100.0);

    public static final ModConfigSpec.DoubleValue ROSE_GOLD_PROPAGATION = BUILDER
        .comment("Rose gold fluid pipe propagation multiplier")
        .defineInRange("roseGoldPropagationMultiplier", 8.0, 0.1, 100.0);

    public static final ModConfigSpec.DoubleValue ROSE_GOLD_FLOW = BUILDER
        .comment("Rose gold fluid pipe flow multiplier")
        .defineInRange("roseGoldFlowMultiplier", 0.75, 0.1, 100.0);

    public static final ModConfigSpec.DoubleValue AMETHYST_PROPAGATION = BUILDER
        .comment("Amethyst fluid pipe propagation multiplier")
        .defineInRange("amethystPropagationMultiplier", 10.0, 0.1, 100.0);

    public static final ModConfigSpec.DoubleValue AMETHYST_FLOW = BUILDER
        .comment("Amethyst fluid pipe flow multiplier")
        .defineInRange("amethystFlowMultiplier", 0.4, 0.1, 100.0);

    public static final ModConfigSpec SPEC = BUILDER.build();
}
