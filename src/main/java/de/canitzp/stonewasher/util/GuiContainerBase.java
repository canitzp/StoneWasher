package de.canitzp.stonewasher.util;

import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Util;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.IInteractionObject;

public abstract class GuiContainerBase<T extends TileEntity & IInteractionObject> extends GuiContainer{
    
    private T tile;
    private EntityPlayer player;
    
    public GuiContainerBase(T tile, EntityPlayer player, int xSize, int ySize){
        super(tile.createContainer(player.inventory, player));
        this.tile = tile;
        this.player = player;
        this.xSize = xSize;
        this.ySize = ySize;
    }
    
    public T getTile(){
        return tile;
    }
    
    public EntityPlayer getPlayer(){
        return player;
    }
    
    @Override
    public void render(int mouseX, int mouseY, float partialTicks){
        int i = this.guiLeft;
        int j = this.guiTop;
        this.drawGuiContainerBackgroundLayer(partialTicks, mouseX, mouseY);
        GlStateManager.disableRescaleNormal();
        RenderHelper.disableStandardItemLighting();
        GlStateManager.disableLighting();
        GlStateManager.disableDepthTest();
        //super.render(mouseX, mouseY, partialTicks);
        RenderHelper.enableGUIStandardItemLighting();
        GlStateManager.pushMatrix();
        GlStateManager.translatef((float)i, (float)j, 0.0F);
        GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        GlStateManager.enableRescaleNormal();
        this.hoveredSlot = null;
        int k = 240;
        int l = 240;
        OpenGlHelper.glMultiTexCoord2f(OpenGlHelper.GL_TEXTURE1, 240.0F, 240.0F);
        GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        
        for(int i1 = 0; i1 < this.inventorySlots.inventorySlots.size(); ++i1) {
            Slot slot = this.inventorySlots.inventorySlots.get(i1);
            if (slot.isEnabled()) {
                this.drawSlot(slot);
            }
            
            if (this.isSlotSelected(slot, (double)mouseX, (double)mouseY) && slot.isEnabled()) {
                this.hoveredSlot = slot;
                GlStateManager.disableLighting();
                GlStateManager.disableDepthTest();
                int j1 = slot.xPos;
                int k1 = slot.yPos;
                int size = slot instanceof SlotSized ? ((SlotSized) slot).getSize() : 16;
                GlStateManager.colorMask(true, true, true, false);
                int slotColor = this.getSlotColor(i1);
                this.drawGradientRect(j1, k1, j1 + size, k1 + size, slotColor, slotColor);
                GlStateManager.colorMask(true, true, true, true);
                GlStateManager.enableLighting();
                GlStateManager.enableDepthTest();
            }
        }
        
        RenderHelper.disableStandardItemLighting();
        this.drawGuiContainerForegroundLayer(mouseX, mouseY);
        RenderHelper.enableGUIStandardItemLighting();
        net.minecraftforge.common.MinecraftForge.EVENT_BUS.post(new net.minecraftforge.client.event.GuiContainerEvent.DrawForeground(this, mouseX, mouseY));
        InventoryPlayer inventoryplayer = this.mc.player.inventory;
        ItemStack itemstack = this.draggedStack.isEmpty() ? inventoryplayer.getItemStack() : this.draggedStack;
        if (!itemstack.isEmpty()) {
            int j2 = 8;
            int k2 = this.draggedStack.isEmpty() ? 8 : 16;
            String s = null;
            if (!this.draggedStack.isEmpty() && this.isRightMouseClick) {
                itemstack = itemstack.copy();
                itemstack.setCount(MathHelper.ceil((float)itemstack.getCount() / 2.0F));
            } else if (this.dragSplitting && this.dragSplittingSlots.size() > 1) {
                itemstack = itemstack.copy();
                itemstack.setCount(super.dragSplittingRemnant);
                if (itemstack.isEmpty()) {
                    s = "" + TextFormatting.YELLOW + "0";
                }
            }
            
            super.drawItemStack(itemstack, mouseX - i - 8, mouseY - j - k2, s);
        }
        
        if (!super.returningStack.isEmpty()) {
            float f = (float)(Util.milliTime() - this.returningStackTime) / 100.0F;
            if (f >= 1.0F) {
                f = 1.0F;
                super.returningStack = ItemStack.EMPTY;
            }
            
            int l2 = super.returningStackDestSlot.xPos - super.touchUpX;
            int i3 = super.returningStackDestSlot.yPos - super.touchUpY;
            int l1 = super.touchUpX + (int)((float)l2 * f);
            int i2 = super.touchUpY + (int)((float)i3 * f);
            super.drawItemStack(super.returningStack, l1, i2, (String)null);
        }
        
        GlStateManager.popMatrix();
        GlStateManager.enableLighting();
        GlStateManager.enableDepthTest();
        RenderHelper.enableStandardItemLighting();
        
        this.renderHoveredToolTip(mouseX, mouseY);
    }
    
    @Override
    public void drawSlot(Slot slot){
        int size = 16;
        if(slot instanceof SlotSized){
            size = ((SlotSized) slot).getSize();
        }
        int i = slot.xPos;
        int j = slot.yPos;
        ItemStack itemstack = slot.getStack();
        boolean flag = false;
        boolean flag1 = slot == super.clickedSlot && !super.draggedStack.isEmpty() && !super.isRightMouseClick;
        ItemStack itemstack1 = this.mc.player.inventory.getItemStack();
        String s = null;
        if (slot == super.clickedSlot && !super.draggedStack.isEmpty() && super.isRightMouseClick && !itemstack.isEmpty()) {
            itemstack = itemstack.copy();
            itemstack.setCount(itemstack.getCount() / 2);
        } else if (this.dragSplitting && this.dragSplittingSlots.contains(slot) && !itemstack1.isEmpty()) {
            if (this.dragSplittingSlots.size() == 1) {
                return;
            }
            
            if (Container.canAddItemToSlot(slot, itemstack1, true) && this.inventorySlots.canDragIntoSlot(slot)) {
                itemstack = itemstack1.copy();
                flag = true;
                Container.computeStackSize(this.dragSplittingSlots, super.dragSplittingLimit, itemstack, slot.getStack().isEmpty() ? 0 : slot.getStack().getCount());
                int k = Math.min(itemstack.getMaxStackSize(), slot.getItemStackLimit(itemstack));
                if (itemstack.getCount() > k) {
                    s = TextFormatting.YELLOW.toString() + k;
                    itemstack.setCount(k);
                }
            } else {
                this.dragSplittingSlots.remove(slot);
                super.updateDragSplitting();
            }
        }
        
        this.zLevel = 100.0F;
        this.itemRender.zLevel = 100.0F;
        if (itemstack.isEmpty() && slot.isEnabled()) {
            TextureAtlasSprite textureatlassprite = slot.getBackgroundSprite();
            if (textureatlassprite != null) {
                GlStateManager.disableLighting();
                this.mc.getTextureManager().bindTexture(slot.getBackgroundLocation());
                this.drawTexturedModalRect(i, j, textureatlassprite, size, size);
                GlStateManager.enableLighting();
                flag1 = true;
            }
        }
        
        if (!flag1) {
            if (flag) {
                drawRect(i, j, i + size, j + size, -2130706433);
            }
            
            GlStateManager.pushMatrix();
            GlStateManager.translatef(i, j, 0);
            GlStateManager.scalef(size / 16F, size / 16F, size / 16F);
            GlStateManager.enableDepthTest();
            this.itemRender.renderItemAndEffectIntoGUI(this.mc.player, itemstack, 0, 0);
            this.itemRender.renderItemOverlayIntoGUI(this.fontRenderer, itemstack, 0, 0, s);
            GlStateManager.popMatrix();
        }
        
        this.itemRender.zLevel = 0.0F;
        this.zLevel = 0.0F;
    }
    
    @Override
    public boolean isSlotSelected(Slot slot, double mouseX, double mouseY){
        int slotSize = slot instanceof SlotSized ? ((SlotSized) slot).getSize() : 16;
        return this.isPointInRegion(slot.xPos, slot.yPos, slotSize, slotSize, mouseX, mouseY);
    }
}
