package de.canitzp.stonewasher.util;

import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.items.ItemHandlerHelper;
import net.minecraftforge.items.wrapper.SidedInvWrapper;

import javax.annotation.Nonnull;

public class InvWrapperCustom extends SidedInvWrapper{
    
    public InvWrapperCustom(ISidedInventory inv, EnumFacing side){
        super(inv, side);
    }
    
    @Nonnull
    public ItemStack insertItemForce(int slot, @Nonnull ItemStack stack, boolean simulate){
        if(stack.isEmpty()){
            return ItemStack.EMPTY;
        }
        ItemStack stackInSlot = this.getStackInSlot(slot);
        int m;
        if(!stackInSlot.isEmpty()){
            if(stackInSlot.getCount() >= Math.min(stackInSlot.getMaxStackSize(), getSlotLimit(slot))){
                return stack;
            }
    
            if(!ItemHandlerHelper.canItemStacksStack(stack, stackInSlot)){
                return stack;
            }
    
            m = Math.min(stack.getMaxStackSize(), getSlotLimit(slot)) - stackInSlot.getCount();
    
            if(stack.getCount() <= m){
                if(!simulate){
                    ItemStack copy = stack.copy();
                    copy.grow(stackInSlot.getCount());
                    this.setStackInSlot(slot, copy);
                }
                return ItemStack.EMPTY;
            }else{
                // copy the stack to not modify the original one
                stack = stack.copy();
                if(!simulate){
                    ItemStack copy = stack.split(m);
                    copy.grow(stackInSlot.getCount());
                    this.setStackInSlot(slot, copy);
                    return stack;
                }else{
                    stack.shrink(m);
                    return stack;
                }
            }
        }else{
            m = Math.min(stack.getMaxStackSize(), getSlotLimit(slot));
            if(m < stack.getCount()){
                // copy the stack to not modify the original one
                stack = stack.copy();
                if(!simulate){
                    this.setStackInSlot(slot, stack.split(m));
                    return stack;
                }else{
                    stack.shrink(m);
                    return stack;
                }
            }else{
                if(!simulate){
                    this.setStackInSlot(slot, stack);
                }
                return ItemStack.EMPTY;
            }
        }
    }
}