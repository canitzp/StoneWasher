package de.canitzp.stonewasher.integration.jei;

import de.canitzp.stonewasher.StoneWasher;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nonnull;

@JeiPlugin
public class StoneWasherJEI implements IModPlugin{
    
    @Nonnull
    @Override
    public ResourceLocation getPluginUid(){
        return new ResourceLocation(StoneWasher.MODID, "jei");
    }
    
    @Override
    public void registerCategories(IRecipeCategoryRegistration registration){
        registration.addRecipeCategories();
    }
}
