package de.canitzp.stonewasher.block.manualwasher;

import de.canitzp.stonewasher.*;
import de.canitzp.stonewasher.recipe.RecipeStoneWasher;
import de.canitzp.stonewasher.util.*;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.IInteractionObject;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;

public class TileManualStoneWasher extends TileEntity implements IInteractionObject, IGuiHolder<TileManualStoneWasher>{

    private ResourceLocation currentRecipe;
    private int progress;
    public InventoryBasicSided inventory = new InventoryBasicSided(new TextComponentString("manualstonewasher"), 2)
        .addInsertSlot(0, EnumFacing.UP, EnumFacing.NORTH, EnumFacing.SOUTH, EnumFacing.EAST, EnumFacing.WEST)
        .addExtractSlot(1, EnumFacing.DOWN);
    
    public TileManualStoneWasher(){
        super(StoneWasher.RegistryEvents.manualStoneWasherTileType);
    }

    @Nonnull
    @Override
    public Container createContainer(@Nonnull InventoryPlayer playerInventory, @Nonnull EntityPlayer player){
        return new ContainerManualStoneWasher(this, player);
    }
    
    @Override
    public GuiContainerBase<TileManualStoneWasher> createGui(TileManualStoneWasher tile, EntityPlayer player){
        return new GuiManualStoneWasher(tile, player);
    }
    
    @Nonnull
    @Override
    public String getGuiID(){
        return "stonewasher:manualstonewasher";
    }

    @Nonnull
    @Override
    public ITextComponent getName(){
        return new TextComponentString(getGuiID());
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
        this.progress = compound.getInt("Progress");
        if(compound.hasKey("Recipe")){
            this.currentRecipe = new ResourceLocation(compound.getString("Recipe"));
        }
        NonNullList<ItemStack> inventoryItems = NonNullList.withSize(this.inventory.getSizeInventory(), ItemStack.EMPTY);
        ItemStackHelper.loadAllItems(compound, inventoryItems);
        for(int i = 0; i < this.inventory.getSizeInventory(); i++){
            this.inventory.setInventorySlotContents(i, inventoryItems.get(i));
        }
    }
    
    private void writeSyncNBT(NBTTagCompound compound){
        compound.setInt("Progress", this.progress);
        if(this.currentRecipe != null){
            compound.setString("Recipe", this.currentRecipe.toString());
        }
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
    
    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable EnumFacing side) {
        if(cap == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY){
            return CapabilityItemHandler.ITEM_HANDLER_CAPABILITY.orEmpty(cap, LazyOptional.of(() -> new InvWrapperCustom(this.inventory, side)));
        }
        return super.getCapability(cap, side);
    }

    public boolean tryToAddItemStack(ItemStack stack){
        if(RecipeStoneWasher.getRecipeForInput(stack) != null){
            ItemStack current = this.inventory.getStackInSlot(0);
            if(current.isEmpty()){
                this.inventory.setInventorySlotContents(0, stack.copy());
                this.checkStatus();
                return true;
            } else if(Util.stacksEqualIgnoreCount(current, stack) && current.getCount() < current.getMaxStackSize()){
                if(stack.getCount() + current.getCount() <= stack.getMaxStackSize()){
                    current.grow(stack.getCount());
                    this.checkStatus();
                    return true;
                } else {
                    int leftOverCount = stack.getMaxStackSize() - current.getCount();
                    current.grow(leftOverCount);
                    stack.shrink(leftOverCount);
                    this.checkStatus();
                    return false;
                }
            }
        }
        return false;
    }
    
    public void doProgress(int amount){
        if(!this.world.isRemote){
            if(this.currentRecipe != null){
                this.progress -= amount;
                checkStatus();
            }
        }
    }
    
    public void checkStatus(){
        if(this.currentRecipe != null){
            if(this.progress <= 0){
                RecipeStoneWasher recipe = RecipeStoneWasher.getRecipeByName(this.currentRecipe);
                if(recipe != null){
                    ItemStack putIn = this.getRecipeOutput(recipe);
                    this.putInOutputSlotsOrDrop(putIn);
                    this.currentRecipe = null;
                } else {
                    System.out.println("The stone washer recipe: '" + this.currentRecipe + "' couldn't be found. TileEntity is getting a reset!");
                    this.progress = 0;
                    this.currentRecipe = null;
                }
            }
        }
        if(this.currentRecipe == null){ // check this after the completion of a recipe
            ItemStack input = this.inventory.getStackInSlot(0);
            RecipeStoneWasher recipe = RecipeStoneWasher.getRecipeForInput(input);
            if(recipe != null && Util.hasStackHigherOrEqualCountAndIsEqual(input, recipe.getInput())){
                this.currentRecipe = recipe.getName();
                this.progress += recipe.getNeededProgress();
                this.inventory.getStackInSlot(0).shrink(recipe.getInput().getCount());
                if(this.progress <= 0){
                    this.checkStatus();
                }
            } else {
                this.progress = 0;
            }
        }
        for(EntityPlayer player : this.world.playerEntities){
            if(player instanceof EntityPlayerMP && this.getDistanceSq(player.posX, player.posY, player.posZ) <= this.getMaxRenderDistanceSquared()){
                ((EntityPlayerMP) player).connection.sendPacket(this.getUpdatePacket());
            }
        }
    }
    
    // this method returns the output stack, based on the weight they have
    private ItemStack getRecipeOutput(RecipeStoneWasher recipe){
        List<RecipeStoneWasher.OutputStack> list = recipe.getOutputStacks();
        Collections.shuffle(list);
        int sum = list.stream().mapToInt(RecipeStoneWasher.OutputStack::getWeight).sum();
        int rnd = this.world.getRandom().nextInt(sum + 1);
        for(RecipeStoneWasher.OutputStack outputStack : list){
            rnd -= outputStack.getWeight();
            if(rnd <= 0){
                return outputStack.getStack().copy();
            }
        }
        System.out.println("Oh no no item stack available!!!" + recipe.getOutputStacks());
        return ItemStack.EMPTY;
    }
    
    private void putInOutputSlotsOrDrop(@Nonnull ItemStack stack){
        TileEntity down = this.world.getTileEntity(this.pos.down()); // try to put it in a inventory under ourself
        if(down != null){
            LazyOptional<IItemHandler> capability = down.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, EnumFacing.UP);
            if(capability.isPresent()){
                capability.ifPresent(iItemHandler -> putInItemHandlerOrDrop(iItemHandler, stack));
                return;
            }
        }
        putInItemHandlerOrDrop(new InvWrapperCustom(this.inventory, EnumFacing.NORTH), stack);
    }
    
    private void putInItemHandlerOrDrop(IItemHandler handler, ItemStack stack){
        ItemStack remaining;
        if(handler instanceof InvWrapperCustom){
            remaining = ((InvWrapperCustom) handler).insertItemForce(1, stack, false);
        } else {
            remaining = ItemHandlerHelper.insertItemStacked(handler, stack, false);
        }
        if (!remaining.isEmpty()) {
            EntityItem entityItem = new EntityItem(this.world, this.getPos().getX(), this.getPos().getY() + 0.5D, this.getPos().getZ());
            entityItem.setItem(remaining);
            this.world.spawnEntity(entityItem);
        }
    }

}
