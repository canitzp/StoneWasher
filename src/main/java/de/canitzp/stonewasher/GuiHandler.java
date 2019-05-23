package de.canitzp.stonewasher;

import de.canitzp.stonewasher.block.manualwasher.GuiManualStoneWasher;
import de.canitzp.stonewasher.block.manualwasher.TileManualStoneWasher;
import de.canitzp.stonewasher.block.stuffholder.TileStuffHolder;
import de.canitzp.stonewasher.util.IGuiHolder;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IInteractionObject;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.network.FMLPlayMessages;
import net.minecraftforge.fml.network.NetworkHooks;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Map;
import java.util.Optional;

/**
 * @author canitzp
 */
public class GuiHandler {

    // can not be called client side
    // return if the gui open packet was send
    public static boolean openTile(@Nonnull World world, @Nonnull EntityPlayer player, @Nonnull BlockPos pos){
        if(!world.isRemote && player instanceof EntityPlayerMP){
            TileEntity tile = world.getTileEntity(pos);
            if(tile instanceof IInteractionObject){
                NetworkHooks.openGui((EntityPlayerMP) player, (IInteractionObject) tile, buffer -> buffer.writeBlockPos(pos));
                return true;
            }
        }
        return false;
    }

    @OnlyIn(Dist.CLIENT)
    public static GuiContainer openGui(FMLPlayMessages.OpenContainer container){
        if(container.getId().getNamespace().equals(StoneWasher.MODID)){
            World world = Minecraft.getInstance().world;
            if(world != null){
                BlockPos pos = container.getAdditionalData().readBlockPos();
                TileEntity tile = world.getTileEntity(pos);
                if(tile instanceof IGuiHolder<?>){
                    return ((IGuiHolder) tile).createGui(tile, Minecraft.getInstance().player);
                }
            }
        }
        return null;
    }

}
