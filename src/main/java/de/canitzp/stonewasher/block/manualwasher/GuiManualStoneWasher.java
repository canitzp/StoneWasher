package de.canitzp.stonewasher.block.manualwasher;

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
    
        GuiInventory.drawEntityOnScreen(this.guiLeft + 30, this.guiTop + 46, 18, this.guiLeft + 30 - mouseX, this.guiTop + 46 - 25 - mouseY, this.getPlayer());
        
        GlStateManager.popMatrix();
    }
    
}
