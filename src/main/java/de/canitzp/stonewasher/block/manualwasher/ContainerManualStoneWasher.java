package de.canitzp.stonewasher.block.manualwasher;

import de.canitzp.stonewasher.util.ContainerBase;
import de.canitzp.stonewasher.util.SlotSized;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nonnull;

public class ContainerManualStoneWasher extends ContainerBase<TileManualStoneWasher>{
    
    public static final String[] EQUIPMENT_TEXTURES = new String[]{"item/empty_armor_slot_boots", "item/empty_armor_slot_leggings", "item/empty_armor_slot_chestplate", "item/empty_armor_slot_helmet"};
    public static final EntityEquipmentSlot[] VALID_EQUIPMENT_SLOTS = new EntityEquipmentSlot[]{EntityEquipmentSlot.HEAD, EntityEquipmentSlot.CHEST, EntityEquipmentSlot.LEGS, EntityEquipmentSlot.FEET};
    
    public ContainerManualStoneWasher(TileManualStoneWasher tile, EntityPlayer player){
        super(tile, player);
        
        this.addSlot(new Slot(tile.inventory, 0, 85, 20){
            @Override
            public void putStack(@Nonnull ItemStack stack){
                super.putStack(stack);
                ContainerManualStoneWasher.this.getTile().checkStatus();
            }
        });
        this.addSlot(new Slot(tile.inventory, 1, 124, 20){
            @Override
            public boolean isItemValid(ItemStack stack){
                return false;
            }
        });
    
        for(int i = 0; i < 3; ++i) {
            for(int j = 0; j < 9; ++j) {
                this.addSlot(new Slot(player.inventory, j + i * 9 + 9, 8 + j * 18, 54 + i * 18));
            }
        }
        
        for(int k = 0; k < 9; ++k) {
            this.addSlot(new Slot(player.inventory, k, 8 + k * 18, 111));
        }
        
        for(int k = 0; k < 4; ++k) {
            final EntityEquipmentSlot entityequipmentslot = VALID_EQUIPMENT_SLOTS[k];
            this.addSlot(new SlotSized(8, player.inventory, 39 - k, 8, 10 + k * 10) {
                public int getSlotStackLimit() {
                    return 1;
                }
                public boolean isItemValid(ItemStack stack) {
                    return stack.canEquip(entityequipmentslot, player);
                }
                public boolean canTakeStack(EntityPlayer playerIn) {
                    ItemStack itemstack = this.getStack();
                    return (itemstack.isEmpty() || playerIn.isCreative() || !EnchantmentHelper.hasBindingCurse(itemstack)) && super.canTakeStack(playerIn);
                }
                @OnlyIn(Dist.CLIENT)
                public String getSlotTexture() {
                    return EQUIPMENT_TEXTURES[entityequipmentslot.getIndex()];
                }
            });
        }
        this.addSlot(new SlotSized(8, player.inventory, 40, 45, 40) {
            @OnlyIn(Dist.CLIENT)
            public String getSlotTexture() {
                return "item/empty_armor_slot_shield";
            }
        });
    }
    
}
