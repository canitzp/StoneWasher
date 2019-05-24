package de.canitzp.stonewasher.integration.jei;

import de.canitzp.stonewasher.StoneWasher;
import de.canitzp.stonewasher.recipe.RecipeStoneWasher;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nonnull;
import java.util.stream.Collectors;

public class StoneWasherCategory implements IRecipeCategory<RecipeStoneWasher>{
    
    private static final ResourceLocation ID = new ResourceLocation(StoneWasher.MODID, "stonewasher");
    
    @Nonnull
    @Override
    public ResourceLocation getUid(){
        return ID;
    }
    
    @Nonnull
    @Override
    public Class<? extends RecipeStoneWasher> getRecipeClass(){
        return RecipeStoneWasher.class;
    }
    
    @Nonnull
    @Override
    public String getTitle(){
        return StoneWasher.RegistryEvents.manualStoneWasher.getNameTextComponent().getFormattedText();
    }
    
    @Override
    public IDrawable getBackground(){
        return null;
    }
    
    @Override
    public IDrawable getIcon(){
        return null;
    }
    
    @Override
    public void setIngredients(RecipeStoneWasher recipe, IIngredients ingredients){
        ingredients.setInput(VanillaTypes.ITEM, recipe.getInput());
        ingredients.setOutputs(VanillaTypes.ITEM, recipe.getOutputStacks(ItemStack.EMPTY).stream().map(RecipeStoneWasher.OutputStack::getStack).collect(Collectors.toList()));
    }
    
    @Override
    public void setRecipe(IRecipeLayout recipeLayout, RecipeStoneWasher recipe, IIngredients ingredients){
    
    }
}
