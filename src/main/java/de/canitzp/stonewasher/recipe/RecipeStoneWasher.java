package de.canitzp.stonewasher.recipe;

import de.canitzp.stonewasher.util.Util;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.ForgeRegistryEntry;
import net.minecraftforge.registries.IForgeRegistry;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;
import java.util.function.Consumer;

/**
 * @author canitzp
 */
public class RecipeStoneWasher extends ForgeRegistryEntry<RecipeStoneWasher> implements ICustomRecipe<RecipeStoneWasher>{

    public static IForgeRegistry<RecipeStoneWasher> REGISTRY;
    
    private static Map<ItemStack, List<FilteredChance>> ORE_WEIGHT_MAP = new HashMap<>();

    private ItemStack input;
    private NonNullList<OutputStack> outputStacks = NonNullList.create();
    private int neededProgress = 100;
    private int oreWeight = 0;
    
    static {
        // Values according to BeachBiome.java
        addOre(new ItemStack(Blocks.COAL_ORE),
            filteredChances -> filteredChances.add(new FilteredChance(ItemStack.EMPTY, 0, 128, 17, 20))
        );
        addOre(new ItemStack(Blocks.IRON_ORE),
            filteredChances -> filteredChances.add(new FilteredChance(ItemStack.EMPTY, 0, 64, 9, 20))
        );
        addOre(new ItemStack(Blocks.GOLD_ORE),
            filteredChances -> filteredChances.add(new FilteredChance(ItemStack.EMPTY, 0, 32, 9, 2))
        );
        addOre(new ItemStack(Blocks.REDSTONE_ORE),
            filteredChances -> filteredChances.add(new FilteredChance(ItemStack.EMPTY, 0, 16, 8, 8))
        );
        addOre(new ItemStack(Blocks.DIAMOND_ORE),
            filteredChances -> filteredChances.add(new FilteredChance(ItemStack.EMPTY, 0, 16, 8, 1))
        );
        addOre(new ItemStack(Blocks.LAPIS_ORE),
            filteredChances -> filteredChances.add(new FilteredChance(ItemStack.EMPTY, 0, 23, 7, 1))
        );
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

    public NonNullList<OutputStack> getOutputStacks(ItemStack filter) {
        if(this.oreWeight > 0){
            NonNullList<OutputStack> ret = NonNullList.create();
            ret.addAll(this.outputStacks);
            
            Map<ItemStack, Integer> filteredWeight = new HashMap<>();
    
            for(Map.Entry<ItemStack, List<FilteredChance>> entry : ORE_WEIGHT_MAP.entrySet()){
                for(FilteredChance chance : entry.getValue()){
                    if(Util.stacksEqualIgnoreCount(chance.getFilter(), filter)){
                        filteredWeight.put(entry.getKey(), chance.getWeight());
                        break;
                    }
                }
                // when no filter applies then use ItemStack.EMPTY as filter if it exists, but decrease the chances by 3/4
                for(FilteredChance chance : entry.getValue()){
                    if(chance.getFilter().isEmpty()){
                        filteredWeight.put(entry.getKey(), Math.round(chance.getWeight() * 0.25F));
                        break;
                    }
                }
            }
            
            int allOreWeight = filteredWeight.values().stream().mapToInt(i -> i).sum();
            float multiplier = this.oreWeight / (allOreWeight * 1.0F);
            filteredWeight.forEach((key, value) -> ret.add(new OutputStack(key, Math.round(value * multiplier))));
            
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
    
    public static void addOre(ItemStack ore, Consumer<List<FilteredChance>> consumer){
        List<FilteredChance> filteredOres = ORE_WEIGHT_MAP.getOrDefault(ore, new ArrayList<>());
        consumer.accept(filteredOres);
        ORE_WEIGHT_MAP.put(ore, filteredOres);
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
    
    public static class FilteredChance {
        @Nonnull private ItemStack filter;
        private int weight;
    
        public FilteredChance(@Nonnull ItemStack filter, int weight){
            this.filter = filter;
            this.weight = weight;
        }
    
        public FilteredChance(@Nonnull ItemStack filter, int minY, int maxY, int veinSize, int count){
            if(minY > maxY){
                throw new IllegalArgumentException("minY has to be smaller than maxY!");
            } else {
                this.filter = filter;
                this.weight = maxY - minY * veinSize * count;
            }
        }
    
        @Nonnull
        public ItemStack getFilter(){
            return filter;
        }
    
        public int getWeight(){
            return weight;
        }
    
    }
    
}
