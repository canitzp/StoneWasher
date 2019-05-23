package de.canitzp.stonewasher.block.stuffholder;

import de.canitzp.stonewasher.block.manualwasher.ContainerManualStoneWasher;
import de.canitzp.stonewasher.util.ContainerBase;
import de.canitzp.stonewasher.util.SlotSized;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class ContainerStuffHolder extends ContainerBase<TileStuffHolder>{
    
    public ContainerStuffHolder(TileStuffHolder tile, EntityPlayer player){
        super(tile, player);
    
        for(int i = 0; i < 6; ++i) {
            for(int j = 0; j < 14; ++j) {
                this.addSlot(new SlotSized(10, tile.inventory, j + i * 14, 8 + j * 12, 8 + i * 12));
            }
        }
    
        for(int i = 0; i < 3; ++i) {
            for(int j = 0; j < 9; ++j) {
                this.addSlot(new SlotSized(10, player.inventory, j + i * 9 + 9, 68 + j * 12, 84 + i * 12));
            }
        }
    
        for(int k = 0; k < 9; ++k) {
            this.addSlot(new SlotSized(10, player.inventory, k, 68 + k * 12, 122));
        }
    
        for(int k = 0; k < 4; ++k) {
            final EntityEquipmentSlot entityequipmentslot = ContainerManualStoneWasher.VALID_EQUIPMENT_SLOTS[k];
            this.addSlot(new SlotSized(10, player.inventory, 39 - k, 8, 85 + k * 12) {
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
                    return ContainerManualStoneWasher.EQUIPMENT_TEXTURES[entityequipmentslot.getIndex()];
                }
            });
        }
        this.addSlot(new SlotSized(10, player.inventory, 40, 52, 121) {
            @OnlyIn(Dist.CLIENT)
            public String getSlotTexture() {
                return "item/empty_armor_slot_shield";
            }
        });
    }
    
}
