package de.canitzp.stonewasher.block.stuffholder;

import de.canitzp.stonewasher.StoneWasher;
import de.canitzp.stonewasher.util.GuiContainerBase;
import de.canitzp.stonewasher.util.IGuiHolder;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.InventoryBasic;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.NonNullList;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.IInteractionObject;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.wrapper.InvWrapper;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class TileStuffHolder extends TileEntity implements IInteractionObject, IGuiHolder<TileStuffHolder>{
    
    public InventoryBasic inventory = new InventoryBasic(new TextComponentString("stuffholder"), 84);
    
    public TileStuffHolder(){
        super(StoneWasher.RegistryEvents.stuffHolderTileType);
    }
    
    @Nonnull
    @Override
    public Container createContainer(@Nonnull InventoryPlayer playerInventory, @Nonnull EntityPlayer player){
        return new ContainerStuffHolder(this, player);
    }
    
    @Override
    public GuiContainerBase<TileStuffHolder> createGui(TileStuffHolder tile, EntityPlayer player){
        return new GuiStuffHolder(tile, player);
    }
    
    @Nonnull
    @Override
    public String getGuiID(){
        return "stonewasher:stuffholder";
    }
    
    @Nonnull
    @Override
    public ITextComponent getName(){
        return new TextComponentString(this.getGuiID());
    }
    
    @Override
    public boolean hasCustomName(){
        return false;
    }
    
    @Nullable
    @Override
    public ITextComponent getCustomName(){
        return null;
    }
    
    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable EnumFacing side) {
        if(cap == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY){
            return CapabilityItemHandler.ITEM_HANDLER_CAPABILITY.orEmpty(cap, LazyOptional.of(() -> new InvWrapper(this.inventory)));
        }
        return super.getCapability(cap, side);
    }
    
    @Override
    public void read(NBTTagCompound compound){
        super.read(compound);
        this.readSyncNBT(compound); // don't sync, but it needs to be read anyway
    }
    
    @Override
    public NBTTagCompound write(NBTTagCompound compound){
        this.writeSyncNBT(compound); // don't sync but it needs to be saved anyway
        return super.write(compound);
    }
    
    private void readSyncNBT(NBTTagCompound compound){
        NonNullList<ItemStack> inventoryItems = NonNullList.withSize(this.inventory.getSizeInventory(), ItemStack.EMPTY);
        ItemStackHelper.loadAllItems(compound, inventoryItems);
        for(int i = 0; i < this.inventory.getSizeInventory(); i++){
            this.inventory.setInventorySlotContents(i, inventoryItems.get(i));
        }
    }
    
    private void writeSyncNBT(NBTTagCompound compound){
        NonNullList<ItemStack> inventoryItems = NonNullList.create();
        for(int i = 0; i < this.inventory.getSizeInventory(); i++){
            inventoryItems.add(this.inventory.getStackInSlot(i));
        }
        ItemStackHelper.saveAllItems(compound, inventoryItems);
    }
    
    @Nullable
    @Override
    public SPacketUpdateTileEntity getUpdatePacket(){
        NBTTagCompound compound = new NBTTagCompound();
        this.writeSyncNBT(compound);
        return new SPacketUpdateTileEntity(this.pos, -1, compound);
    }
    
    @Nonnull
    @Override
    public NBTTagCompound getUpdateTag(){
        return this.write(new NBTTagCompound());
    }
    
    @Override
    public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity pkt){
        if(pkt != null){
            this.readSyncNBT(pkt.getNbtCompound());
        }
    }
}
