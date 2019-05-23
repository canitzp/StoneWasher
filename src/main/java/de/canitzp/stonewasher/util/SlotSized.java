package de.canitzp.stonewasher.util;

import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;

public class SlotSized extends Slot{
    
    private int size;
    
    public SlotSized(int size, IInventory inventory, int slotId, int x, int y){
        super(inventory, slotId, x, y);
        this.size = size;
    }
    
    public int getSize(){
        return size;
    }
}
