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

    public static final DeferredBlock<BrassFluidTankBlock> BRASS_FLUID_TANK = BLOCKS.register(
            "brass_fluid_tank",
            () -> new BrassFluidTankBlock(
                    BlockBehaviour.Properties.of()
                            .mapColor(MapColor.GOLD)
                            .strength(5.0f, 6.0f)
                            .sound(SoundType.METAL)
                            .noOcclusion()
            )
    );

    public static final DeferredBlock<SturdyFluidTankBlock> STURDY_FLUID_TANK = BLOCKS.register(
            "sturdy_fluid_tank",
            () -> new SturdyFluidTankBlock(
                    BlockBehaviour.Properties.of()
                            .mapColor(MapColor.METAL)
                            .strength(8.0f, 12.0f)
                            .sound(SoundType.METAL)
                            .noOcclusion()
            )
    );

    public static final DeferredBlock<RoseGoldFluidTankBlock> ROSE_GOLD_FLUID_TANK = BLOCKS.register(
            "rose_gold_fluid_tank",
            () -> new RoseGoldFluidTankBlock(
                    BlockBehaviour.Properties.of()
                            .mapColor(MapColor.COLOR_PINK)
                            .strength(10.0f, 15.0f)
                            .sound(SoundType.METAL)
                            .noOcclusion()
            )
    );

    public static final DeferredBlock<SilverFluidTankBlock> SILVER_FLUID_TANK = BLOCKS.register(
            "silver_fluid_tank",
            () -> new SilverFluidTankBlock(
                    BlockBehaviour.Properties.of()
                            .mapColor(MapColor.SNOW)
                            .strength(6.0f, 8.0f)
                            .sound(SoundType.METAL)
                            .noOcclusion()
            )
    );

    public static final DeferredBlock<AmethystFluidTankBlock> AMETHYST_FLUID_TANK = BLOCKS.register(
            "amethyst_fluid_tank",
            () -> new AmethystFluidTankBlock(
                    BlockBehaviour.Properties.of()
                            .mapColor(MapColor.COLOR_PURPLE)
                            .strength(15.0f, 25.0f)
                            .sound(SoundType.AMETHYST)
                            .noOcclusion()
            )
    );

    public static final DeferredBlock<CosmeticFluidPipeBlock> BRASS_FLUID_PIPE = BLOCKS.register(
            "brass_fluid_pipe",
            () -> new CosmeticFluidPipeBlock(
                    BlockBehaviour.Properties.of()
                            .mapColor(MapColor.GOLD)
                            .strength(5.0f, 6.0f)
                            .sound(SoundType.METAL)
                            .noOcclusion()
                            .forceSolidOff(),
                    () -> ModBlockEntityTypes.BRASS_FLUID_PIPE.get()
            )
    );

    public static final DeferredBlock<CosmeticFluidPipeBlock> SILVER_FLUID_PIPE = BLOCKS.register(
            "silver_fluid_pipe",
            () -> new CosmeticFluidPipeBlock(
                    BlockBehaviour.Properties.of()
                            .mapColor(MapColor.SNOW)
                            .strength(5.0f, 6.0f)
                            .sound(SoundType.METAL)
                            .noOcclusion()
                            .forceSolidOff(),
                    () -> ModBlockEntityTypes.SILVER_FLUID_PIPE.get()
            )
    );

    public static final DeferredBlock<CosmeticFluidPipeBlock> STURDY_FLUID_PIPE = BLOCKS.register(
            "sturdy_fluid_pipe",
            () -> new CosmeticFluidPipeBlock(
                    BlockBehaviour.Properties.of()
                            .mapColor(MapColor.METAL)
                            .strength(8.0f, 12.0f)
                            .sound(SoundType.METAL)
                            .noOcclusion()
                            .forceSolidOff(),
                    () -> ModBlockEntityTypes.STURDY_FLUID_PIPE.get()
            )
    );

    public static final DeferredBlock<CosmeticFluidPipeBlock> ROSE_GOLD_FLUID_PIPE = BLOCKS.register(
            "rose_gold_fluid_pipe",
            () -> new CosmeticFluidPipeBlock(
                    BlockBehaviour.Properties.of()
                            .mapColor(MapColor.COLOR_PINK)
                            .strength(10.0f, 15.0f)
                            .sound(SoundType.METAL)
                            .noOcclusion()
                            .forceSolidOff(),
                    () -> ModBlockEntityTypes.ROSE_GOLD_FLUID_PIPE.get()
            )
    );

    public static final DeferredBlock<CosmeticFluidPipeBlock> AMETHYST_FLUID_PIPE = BLOCKS.register(
            "amethyst_fluid_pipe",
            () -> new CosmeticFluidPipeBlock(
                    BlockBehaviour.Properties.of()
                            .mapColor(MapColor.COLOR_PURPLE)
                            .strength(10.0f, 15.0f)
                            .sound(SoundType.AMETHYST)
                            .noOcclusion()
                            .forceSolidOff(),
                    () -> ModBlockEntityTypes.AMETHYST_FLUID_PIPE.get()
            )
    );
}
