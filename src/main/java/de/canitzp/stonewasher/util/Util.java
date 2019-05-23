package de.canitzp.stonewasher.util;

import net.minecraft.item.ItemStack;

/**
 * @author canitzp
 */
public class Util {

    public static boolean stacksEqualIgnoreCount(ItemStack a, ItemStack b){
        return a.isItemEqual(b) && ItemStack.areItemStackTagsEqual(a, b);
    }

    public static boolean hasStackHigherOrEqualCountAndIsEqual(ItemStack higher, ItemStack base){
        return stacksEqualIgnoreCount(higher, base) && higher.getCount() >= base.getCount();
    }

}
