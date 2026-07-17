package com.nulltronprime.createmorefluidtanks.registry;

import com.nulltronprime.createmorefluidtanks.CreateMoreFluidTanks;
import com.nulltronprime.createmorefluidtanks.block.AmethystFluidTankBlock;
import com.nulltronprime.createmorefluidtanks.block.BrassFluidTankBlock;
import com.nulltronprime.createmorefluidtanks.block.CosmeticFluidPipeBlock;
import com.nulltronprime.createmorefluidtanks.block.RoseGoldFluidTankBlock;
import com.nulltronprime.createmorefluidtanks.block.SilverFluidTankBlock;
import com.nulltronprime.createmorefluidtanks.block.SturdyFluidTankBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModBlocks {

    public static final DeferredRegister.Blocks BLOCKS =
            DeferredRegister.createBlocks(CreateMoreFluidTanks.MOD_ID);

    private static BlockBehaviour.Properties tankProps(MapColor color, float strength, float resistance, SoundType sound) {
        return BlockBehaviour.Properties.of()
                .mapColor(color)
                .strength(strength, resistance)
                .sound(sound)
                .noOcclusion()
                .requiresCorrectToolForDrops();
    }

    private static BlockBehaviour.Properties pipeProps(MapColor color, float strength, float resistance, SoundType sound) {
        return BlockBehaviour.Properties.of()
                .mapColor(color)
                .strength(strength, resistance)
                .sound(sound)
                .noOcclusion()
                .forceSolidOff()
                .requiresCorrectToolForDrops();
    }

    public static final DeferredBlock<BrassFluidTankBlock> BRASS_FLUID_TANK = BLOCKS.register(
            "brass_fluid_tank",
            () -> new BrassFluidTankBlock(tankProps(MapColor.GOLD, 5.0f, 6.0f, SoundType.METAL))
    );

    public static final DeferredBlock<SturdyFluidTankBlock> STURDY_FLUID_TANK = BLOCKS.register(
            "sturdy_fluid_tank",
            () -> new SturdyFluidTankBlock(tankProps(MapColor.METAL, 8.0f, 12.0f, SoundType.METAL))
    );

    public static final DeferredBlock<RoseGoldFluidTankBlock> ROSE_GOLD_FLUID_TANK = BLOCKS.register(
            "rose_gold_fluid_tank",
            () -> new RoseGoldFluidTankBlock(tankProps(MapColor.COLOR_PINK, 10.0f, 15.0f, SoundType.METAL))
    );

    public static final DeferredBlock<SilverFluidTankBlock> SILVER_FLUID_TANK = BLOCKS.register(
            "silver_fluid_tank",
            () -> new SilverFluidTankBlock(tankProps(MapColor.SNOW, 6.0f, 8.0f, SoundType.METAL))
    );

    public static final DeferredBlock<AmethystFluidTankBlock> AMETHYST_FLUID_TANK = BLOCKS.register(
            "amethyst_fluid_tank",
            () -> new AmethystFluidTankBlock(tankProps(MapColor.COLOR_PURPLE, 15.0f, 25.0f, SoundType.AMETHYST))
    );

    public static final DeferredBlock<CosmeticFluidPipeBlock> BRASS_FLUID_PIPE = BLOCKS.register(
            "brass_fluid_pipe",
            () -> new CosmeticFluidPipeBlock(
                    pipeProps(MapColor.GOLD, 5.0f, 6.0f, SoundType.METAL),
                    () -> ModBlockEntityTypes.BRASS_FLUID_PIPE.get()
            )
    );

    public static final DeferredBlock<CosmeticFluidPipeBlock> SILVER_FLUID_PIPE = BLOCKS.register(
            "silver_fluid_pipe",
            () -> new CosmeticFluidPipeBlock(
                    pipeProps(MapColor.SNOW, 5.0f, 6.0f, SoundType.METAL),
                    () -> ModBlockEntityTypes.SILVER_FLUID_PIPE.get()
            )
    );

    public static final DeferredBlock<CosmeticFluidPipeBlock> STURDY_FLUID_PIPE = BLOCKS.register(
            "sturdy_fluid_pipe",
            () -> new CosmeticFluidPipeBlock(
                    pipeProps(MapColor.METAL, 8.0f, 12.0f, SoundType.METAL),
                    () -> ModBlockEntityTypes.STURDY_FLUID_PIPE.get()
            )
    );

    public static final DeferredBlock<CosmeticFluidPipeBlock> ROSE_GOLD_FLUID_PIPE = BLOCKS.register(
            "rose_gold_fluid_pipe",
            () -> new CosmeticFluidPipeBlock(
                    pipeProps(MapColor.COLOR_PINK, 10.0f, 15.0f, SoundType.METAL),
                    () -> ModBlockEntityTypes.ROSE_GOLD_FLUID_PIPE.get()
            )
    );

    public static final DeferredBlock<CosmeticFluidPipeBlock> AMETHYST_FLUID_PIPE = BLOCKS.register(
            "amethyst_fluid_pipe",
            () -> new CosmeticFluidPipeBlock(
                    pipeProps(MapColor.COLOR_PURPLE, 10.0f, 15.0f, SoundType.AMETHYST),
                    () -> ModBlockEntityTypes.AMETHYST_FLUID_PIPE.get()
            )
    );
}
