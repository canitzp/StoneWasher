package de.canitzp.stonewasher.util;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.IInteractionObject;

public interface IGuiHolder<T extends TileEntity & IInteractionObject>{
    
    GuiContainerBase<T> createGui(T tile, EntityPlayer player);
    
    String getGuiID();
    
}
