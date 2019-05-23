package de.canitzp.stonewasher.recipe;

import de.canitzp.stonewasher.util.Util;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.ForgeRegistryEntry;
import net.minecraftforge.registries.IForgeRegistry;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;

/**
 * @author canitzp
 */
public class RecipeStoneWasher extends ForgeRegistryEntry<RecipeStoneWasher> implements ICustomRecipe<RecipeStoneWasher>{

    public static IForgeRegistry<RecipeStoneWasher> REGISTRY;
    
    private static Map<ItemStack, Integer> ORE_WEIGHT_MAP = new HashMap<>();

    private ItemStack input;
    private NonNullList<OutputStack> outputStacks = NonNullList.create();
    private int neededProgress = 100;
    private int oreWeight = 0;
    
    static {
        addOre(new ItemStack(Blocks.COAL_ORE), 0, 128, 17);
        addOre(new ItemStack(Blocks.IRON_ORE), 0, 64, 9);
        addOre(new ItemStack(Blocks.GOLD_ORE), 0, 32, 9);
        addOre(new ItemStack(Blocks.REDSTONE_ORE), 0, 16, 8);
        addOre(new ItemStack(Blocks.DIAMOND_ORE), 0, 16, 8);
        addOre(new ItemStack(Blocks.LAPIS_ORE), 0, 23, 7);
    }
    
    public RecipeStoneWasher(ItemStack input, Consumer<NonNullList<OutputStack>> outputStacks){
        this.input = input;
        outputStacks.accept(this.outputStacks);
    }

    @Nullable
    public static RecipeStoneWasher getRecipeForInput(ItemStack input){
        if(!input.isEmpty() && REGISTRY != null){
            Optional<Map.Entry<ResourceLocation, RecipeStoneWasher>> entry = REGISTRY.getEntries().stream()
                    .filter(internalEntry -> Util.stacksEqualIgnoreCount(input, internalEntry.getValue().input))
                    .findFirst();
            if(entry.isPresent()){
                return entry.get().getValue();
            }
        }
        return null;
    }

    @Nullable
    public static RecipeStoneWasher getRecipeByName(ResourceLocation name){
        Optional<Map.Entry<ResourceLocation, RecipeStoneWasher>> entry = REGISTRY.getEntries().stream().filter(internalEntry -> internalEntry.getKey().equals(name)).findFirst();
        return entry.map(Map.Entry::getValue).orElse(null);
    }

    @Nullable
    public ResourceLocation getName(){
        Optional<Map.Entry<ResourceLocation, RecipeStoneWasher>> entry = REGISTRY.getEntries().stream().filter(internalEntry -> internalEntry.getValue().equals(this)).findFirst();
        return entry.map(Map.Entry::getKey).orElse(null);
    }

    public ItemStack getInput() {
        return input;
    }

    public NonNullList<OutputStack> getOutputStacks() {
        if(this.oreWeight > 0){
            NonNullList<OutputStack> ret = NonNullList.create();
            ret.addAll(this.outputStacks);
    
            int allOreWeight = ORE_WEIGHT_MAP.values().stream().mapToInt(i -> i).sum();
            float multiplier = this.oreWeight / (allOreWeight * 1.0F);
            ORE_WEIGHT_MAP.forEach((key, value) -> ret.add(new OutputStack(key, Math.round(value * multiplier))));
            
            return ret;
        }
        return outputStacks;
    }
    
    public RecipeStoneWasher setNeededProgress(int neededProgress){
        this.neededProgress = neededProgress;
        return this;
    }
    
    public RecipeStoneWasher setUseOreWeight(int oreWeight){
        this.oreWeight = oreWeight;
        return this;
    }
    
    public int getNeededProgress() {
        return neededProgress;
    }
    
    public static void addOre(ItemStack ore, int minY, int maxY, int veinSize){
        if(minY > maxY){
            throw new IllegalArgumentException("minY has to be smaller than maxY!");
        } else {
            ORE_WEIGHT_MAP.put(ore, maxY - minY * veinSize);
        }
    }

    public static class OutputStack{
        private ItemStack stack;
        private int weight;

        public OutputStack(ItemStack stack, int weight) {
            this.stack = stack;
            this.weight = weight;
        }

        public ItemStack getStack() {
            return stack;
        }

        public int getWeight() {
            return weight;
        }
    
        @Override
        public String toString(){
            return String.format("OutputStack{weight: '%d', stack: '%s'}", this.weight, this.stack.toString());
        }
    }
    
}
