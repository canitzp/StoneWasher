package de.canitzp.stonewasher;

import de.canitzp.stonewasher.block.manualwasher.BlockManualStoneWasher;
import de.canitzp.stonewasher.block.manualwasher.TileManualStoneWasher;
import de.canitzp.stonewasher.block.stuffholder.BlockStuffHolder;
import de.canitzp.stonewasher.block.stuffholder.TileStuffHolder;
import de.canitzp.stonewasher.recipe.RecipeStoneWasher;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ExtensionPoint;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(StoneWasher.MODID)
public class StoneWasher{
    
    public static final String MODID = "stonewasher";
    private static final Logger LOGGER = LogManager.getLogger("StoneWasher");

    public StoneWasher() {
        RecipeStoneWasher.REGISTRY = new RegistryBuilder<RecipeStoneWasher>()
                .setName(new ResourceLocation(MODID, "recipes"))
                .setType(RecipeStoneWasher.class)
                .setMaxID(Integer.MAX_VALUE >> 5)
                .disableSaving()
                .create();

        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setup);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::initClient);
    }

    private void setup(FMLCommonSetupEvent event){
        LOGGER.info("Welcome to my stone washer empire! Please watch stones carefully! No warranty!");

        ModLoadingContext.get().registerExtensionPoint(ExtensionPoint.GUIFACTORY, () -> GuiHandler::openGui);
        LOGGER.info("Extension Point for my GuiFactory is registered.");
    }

    private void initClient(FMLClientSetupEvent event) {}
    
    @Mod.EventBusSubscriber(bus=Mod.EventBusSubscriber.Bus.MOD)
    public static class RegistryEvents {
        
        public static BlockManualStoneWasher manualStoneWasher = new BlockManualStoneWasher();
        public static BlockStuffHolder stuffHolder = new BlockStuffHolder();
        
        public static ItemBlock manualStoneWasherItem = new ItemBlock(manualStoneWasher, new Item.Properties());
        public static ItemBlock stuffHolderItem = new ItemBlock(stuffHolder, new Item.Properties());
        public static Item stonePebble = new Item(new Item.Properties()).setRegistryName(MODID, "stonepebble");
        
        public static TileEntityType<TileManualStoneWasher> manualStoneWasherTileType = TileEntityType.register("manualstonewasher", TileEntityType.Builder.create(TileManualStoneWasher::new));
        public static TileEntityType<TileStuffHolder> stuffHolderTileType = TileEntityType.register("stuffholder", TileEntityType.Builder.create(TileStuffHolder::new));
        
        @SubscribeEvent
        public static void onBlocksRegistry(RegistryEvent.Register<Block> registryEvent) {
            IForgeRegistry<Block> registry = registryEvent.getRegistry();
            
            registry.register(manualStoneWasher);
            registry.register(stuffHolder);
            
            LOGGER.info("All blocks are registered.");
        }
        
        @SubscribeEvent
        public static void onItemRegistry(RegistryEvent.Register<Item> registryEvent){
            IForgeRegistry<Item> registry = registryEvent.getRegistry();
            
            manualStoneWasherItem.setRegistryName(MODID, "manualstonewasher");
            stuffHolderItem.setRegistryName(MODID, "stuffholder");
            
            registry.register(manualStoneWasherItem);
            registry.register(stonePebble);
            registry.register(stuffHolderItem);
            
            LOGGER.info("All items are registered.");
        }
        
        @SubscribeEvent
        public static void onTileRegistry(RegistryEvent.Register<TileEntityType<?>> registryEvent){
            IForgeRegistry<TileEntityType<?>> registry = registryEvent.getRegistry();
            
            registry.register(manualStoneWasherTileType);
            registry.register(stuffHolderTileType);
            
            LOGGER.info("All tile types are registered.");
        }
        
        @SubscribeEvent
        public static void onStoneWasherRecipeRegistry(RegistryEvent.Register<RecipeStoneWasher> registryEvent){
            IForgeRegistry<RecipeStoneWasher> registry = registryEvent.getRegistry();
            registry.register(new RecipeStoneWasher(
                new ItemStack(Blocks.COBBLESTONE),
                outputStacks -> {
                    outputStacks.add(new RecipeStoneWasher.OutputStack(new ItemStack(stonePebble), 500));
                })
                .setNeededProgress(10)
                .setUseOreWeight(500)
                .setRegistryName(new ResourceLocation(MODID, "cobblestone")));
        }
        
    }
}
