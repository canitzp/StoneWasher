package de.canitzp.stonewasher.util;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;

import javax.annotation.Nonnull;

public abstract class ContainerBase<T extends TileEntity> extends Container{
    
    private T tile;
    private EntityPlayer player;
    
    public ContainerBase(T tile, EntityPlayer player){
        this.tile = tile;
        this.player = player;
    }
    
    public T getTile(){
        return tile;
    }
    
    public EntityPlayer getPlayer(){
        return player;
    }
    
    @Override
    public boolean canInteractWith(@Nonnull EntityPlayer player){
        return true; // todo
    }
    
    @Override
    public ItemStack transferStackInSlot(EntityPlayer playerIn, int index){
        return ItemStack.EMPTY;
    }
}
