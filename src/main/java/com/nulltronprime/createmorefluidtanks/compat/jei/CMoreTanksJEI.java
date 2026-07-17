package com.nulltronprime.createmorefluidtanks.compat.jei;

import java.util.ArrayList;
import java.util.List;

import com.nulltronprime.createmorefluidtanks.CreateMoreFluidTanks;

import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.constants.RecipeTypes;
import mezz.jei.api.recipe.IRecipeManager;
import mezz.jei.api.runtime.IJeiRuntime;

import net.minecraft.client.Minecraft;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeManager;

@JeiPlugin
public class CMoreTanksJEI implements IModPlugin {

    @Override
    public net.minecraft.resources.ResourceLocation getPluginUid() {
        return CreateMoreFluidTanks.id("jei_plugin");
    }

    @Override
    public void onRuntimeAvailable(IJeiRuntime jeiRuntime) {
        IRecipeManager recipeManager = jeiRuntime.getRecipeManager();
        Minecraft mc = Minecraft.getInstance();
        if (mc.level == null) {
            CreateMoreFluidTanks.LOGGER.warn("JEI plugin: no level available, skipping recipe hide");
            return;
        }

        RecipeManager vanilla = mc.level.getRecipeManager();
        List<RecipeHolder<CraftingRecipe>> toHide = new ArrayList<>();

        addIfCrafting(vanilla, CreateMoreFluidTanks.id("amethyst_fluid_tank"), toHide);
        addIfCrafting(vanilla, CreateMoreFluidTanks.id("amethyst_fluid_pipe"), toHide);

        if (!toHide.isEmpty()) {
            CreateMoreFluidTanks.LOGGER.info("Hiding {} amethyst recipes from JEI", toHide.size());
            recipeManager.hideRecipes(RecipeTypes.CRAFTING, toHide);
        }
    }

    @SuppressWarnings("unchecked")
    private static void addIfCrafting(RecipeManager vanilla, net.minecraft.resources.ResourceLocation id,
                                      List<RecipeHolder<CraftingRecipe>> out) {
        vanilla.byKey(id).ifPresent(holder -> {
            if (holder.value() instanceof CraftingRecipe)
                out.add((RecipeHolder<CraftingRecipe>) holder);
        });
    }
}
