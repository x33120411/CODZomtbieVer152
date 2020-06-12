package codzombie.codzombie.Store;

import org.bukkit.inventory.ItemStack;

public class BuyItemData {

    public boolean isCompleteBuy;
    public ItemStack BuyItem;

    public BuyItemData(boolean isComplete, ItemStack Item){
        isCompleteBuy = isComplete;
        BuyItem = Item;
    }

}
