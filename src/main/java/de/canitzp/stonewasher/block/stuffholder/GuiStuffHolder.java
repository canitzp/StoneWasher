package de.canitzp.stonewasher.block.stuffholder;

import de.canitzp.stonewasher.StoneWasher;
import de.canitzp.stonewasher.util.GuiContainerBase;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;

public class GuiStuffHolder extends GuiContainerBase<TileStuffHolder>{
    
    public static final ResourceLocation GUI = new ResourceLocation(StoneWasher.MODID, "textures/gui/stuff_holder.png");
    
    public GuiStuffHolder(TileStuffHolder tile, EntityPlayer player){
        super(tile, player, 182, 140);
    }
    
    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY){
        this.drawDefaultBackground();
    
        GlStateManager.pushMatrix();
        GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        this.mc.getTextureManager().bindTexture(GUI);
        this.drawTexturedModalRect(this.guiLeft, this.guiTop, 0, 0, this.xSize, this.ySize);
    
        GuiInventory.drawEntityOnScreen(this.guiLeft + 35, this.guiTop + 129, 22, this.guiLeft + 33 - mouseX, this.guiTop + 130 - 30 - mouseY, this.getPlayer());
    
        GlStateManager.popMatrix();
    }
}
