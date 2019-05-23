package de.canitzp.stonewasher.util;

import net.minecraft.inventory.ISidedInventory;
import net.minecraft.inventory.InventoryBasic;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.text.ITextComponent;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InventoryBasicSided extends InventoryBasic implements ISidedInventory{
    
    private Map<EnumFacing, List<Integer>> insertSlots = new HashMap<>();
    private Map<EnumFacing, List<Integer>> extractSlots = new HashMap<>();
    
    public InventoryBasicSided(ITextComponent title, int slotCount){
        super(title, slotCount);
    }
    
    public InventoryBasicSided addInsertSlot(int slot, EnumFacing... sides){
        for(EnumFacing side : sides){
            List<Integer> slots = this.insertSlots.getOrDefault(side, new ArrayList<>());
            slots.add(slot);
            this.insertSlots.put(side, slots);
        }
        return this;
    }
    
    public InventoryBasicSided addExtractSlot(int slot, EnumFacing... sides){
        for(EnumFacing side : sides){
            List<Integer> slots = this.extractSlots.getOrDefault(side, new ArrayList<>());
            slots.add(slot);
            this.extractSlots.put(side, slots);
        }
        return this;
    }
    
    @Override
    public int[] getSlotsForFace(EnumFacing side){
        int[] ret = new int[super.getSizeInventory()];
        for(int i = 0; i < super.getSizeInventory(); i++){
            ret[i] = i;
        }
        return ret;
    }
    
    @Override
    public boolean canInsertItem(int index, @Nonnull ItemStack stack, @Nullable EnumFacing side){
        return this.insertSlots.getOrDefault(side, new ArrayList<>()).contains(index);
    }
    
    @Override
    public boolean canExtractItem(int index, @Nonnull ItemStack stack, EnumFacing side){
        return this.extractSlots.getOrDefault(side, new ArrayList<>()).contains(index);
    }
}
