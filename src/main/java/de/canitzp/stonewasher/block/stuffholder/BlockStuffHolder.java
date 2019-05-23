package de.canitzp.stonewasher.block.stuffholder;

import de.canitzp.stonewasher.GuiHandler;
import de.canitzp.stonewasher.StoneWasher;
import de.canitzp.stonewasher.block.manualwasher.TileManualStoneWasher;
import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.BlockFurnace;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.state.StateContainer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class BlockStuffHolder extends BlockContainer{
    
    public BlockStuffHolder(){
        super(Properties.create(Material.IRON));
        this.setRegistryName(StoneWasher.MODID, "stuffholder");
        this.setDefaultState(this.getStateContainer().getBaseState().with(BlockFurnace.FACING, EnumFacing.NORTH));
    }
    
    @Nonnull
    @Override
    public EnumBlockRenderType getRenderType(IBlockState state) {
        return EnumBlockRenderType.MODEL;
    }
    
    @Nullable
    @Override
    public TileEntity createNewTileEntity(@Nonnull IBlockReader world){
        return new TileStuffHolder();
    }
    
    @Override
    public IBlockState getStateForPlacement(BlockItemUseContext context) {
        return this.getDefaultState().with(BlockFurnace.FACING, context.getPlacementHorizontalFacing().getOpposite());
    }
    
    @Override
    public IBlockState rotate(IBlockState state, Rotation rot) {
        return state.with(BlockFurnace.FACING, rot.rotate(state.get(BlockFurnace.FACING)));
    }
    
    @Override
    public IBlockState mirror(IBlockState state, Mirror mirrorIn) {
        return state.rotate(mirrorIn.toRotation(state.get(BlockFurnace.FACING)));
    }
    
    @Override
    protected void fillStateContainer(StateContainer.Builder<Block, IBlockState> builder) {
        builder.add(BlockFurnace.FACING);
    }
    
    @Override
    public boolean onBlockActivated(IBlockState state, World world, BlockPos pos, EntityPlayer player, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ){
        GuiHandler.openTile(world, player, pos);
        return true;
    }
    
    // to not remove the tile entity every time the block state is updated!
    public void onReplaced(IBlockState state, @Nonnull World world, @Nonnull BlockPos pos, IBlockState newState, boolean isMoving){
        if(state.getBlock() != newState.getBlock()){
            TileEntity tile = world.getTileEntity(pos);
            if(tile instanceof TileManualStoneWasher){
                InventoryHelper.dropInventoryItems(world, pos, ((TileManualStoneWasher) tile).inventory);
                world.updateComparatorOutputLevel(pos, this);
            }
            super.onReplaced(state, world, pos, newState, isMoving);
        }
    }
}
