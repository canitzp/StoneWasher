package de.canitzp.stonewasher.block.manualwasher;

import de.canitzp.stonewasher.recipe.RecipeStoneWasher;
import de.canitzp.stonewasher.util.GuiContainerBase;
import de.canitzp.stonewasher.util.SlotSized;
import de.canitzp.stonewasher.StoneWasher;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Util;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.TextFormatting;

public class GuiManualStoneWasher extends GuiContainerBase<TileManualStoneWasher>{
    
    public static final ResourceLocation GUI = new ResourceLocation(StoneWasher.MODID, "textures/gui/manual_stonewasher.png");
    
    public GuiManualStoneWasher(TileManualStoneWasher tile, EntityPlayer player){
        super(tile, player, 176, 135);
    }
    
    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY){
        this.drawDefaultBackground();
    
        GlStateManager.pushMatrix();
        GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        this.mc.getTextureManager().bindTexture(GUI);
        this.drawTexturedModalRect(this.guiLeft, this.guiTop, 0, 0, this.xSize, this.ySize);
        
        if(this.getTile().getProgress() >= 0){
            RecipeStoneWasher recipe = RecipeStoneWasher.getRecipeByName(this.getTile().getCurrentRecipe());
            if(recipe != null){
                float f = this.getTile().getProgress() / (recipe.getNeededProgress() * 1.0F);
                this.drawTexturedModalRect(this.guiLeft + 104, this.guiTop + 20, 0, this.ySize, Math.round(17 * (1 - f)), 16);
            }
        }
    
        GuiInventory.drawEntityOnScreen(this.guiLeft + 30, this.guiTop + 46, 18, this.guiLeft + 30 - mouseX, this.guiTop + 46 - 25 - mouseY, this.getPlayer());
        
        GlStateManager.popMatrix();
    }
    
    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY){
        if(isPointInRegion(104, 20, 17, 16, mouseX, mouseY)){
            if(this.getTile().getProgress() >= 0){
                RecipeStoneWasher recipe = RecipeStoneWasher.getRecipeByName(this.getTile().getCurrentRecipe());
                if(recipe != null){
                    float f = this.getTile().getProgress() / (recipe.getNeededProgress() * 1.0F);
                    String text = String.format("Progress: %d/%d (%d%%)", recipe.getNeededProgress() - this.getTile().getProgress(), recipe.getNeededProgress(), Math.round((1 - f) * 10));
                    this.drawHoveringText(text, mouseX - this.guiLeft, mouseY - this.guiTop);
                }
            }
        }
    }
}
