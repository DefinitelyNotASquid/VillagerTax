package com.mith.VillagerTax.listeners;


import com.mith.VillagerTax.VillagerTax;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Merchant;
import org.bukkit.inventory.MerchantInventory;
import org.bukkit.inventory.MerchantRecipe;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;

import java.util.*;


public class VillagerListener implements Listener {

    private static VillagerTax plugin = VillagerTax.getInstance();

    @EventHandler
    public void onDatazenExploit(CreatureSpawnEvent spawnEvent){

        if(spawnEvent.getSpawnReason() == CreatureSpawnEvent.SpawnReason.CURED){

           LivingEntity villagerTemp = spawnEvent.getEntity();

           villagerTemp.getWorld().spawnEntity(villagerTemp.getLocation(), EntityType.VILLAGER);

           villagerTemp.remove();
        }

    }



    @EventHandler
    public void onPlayerMerchantOpen(InventoryOpenEvent event) {

        if (event.getInventory() instanceof MerchantInventory) {
            Merchant merchant = ((MerchantInventory) event.getInventory()).getMerchant();
            List<MerchantRecipe> recipes = new ArrayList<>(merchant.getRecipes());

            recipes.removeIf(recipe -> recipe.getResult().getType().equals(Material.EMERALD));
            merchant.setRecipes(recipes);
            if (recipes.size() == 0){
                event.setCancelled(true);
            }

            merchant.setRecipes(sanityCheck(recipes));

            List<MerchantRecipe> recipesAfterSanityCheck = new ArrayList<>(merchant.getRecipes());

            for (MerchantRecipe k: recipesAfterSanityCheck) {
              if(k.getResult().getType() == Material.ENCHANTED_BOOK){
                  List<ItemStack> ingredients = k.getIngredients();

                  EnchantmentStorageMeta meta = (EnchantmentStorageMeta) k.getResult().getItemMeta();
                  Map<Enchantment, Integer> enchantments = meta.getStoredEnchants();
                  List<Enchantment> enchantmentList = new ArrayList<>(enchantments.keySet());
                  List<Integer> levelsList = new ArrayList<>(enchantments.values());
                  Enchantment enchant = enchantmentList.get(0);
                  String name = enchant.getKey().getKey();
                  int level = levelsList.get(0);
                  for (ItemStack i: ingredients) {
                      if(i.getType() == Material.EMERALD){
                          if(plugin.getConfig().getBoolean(name + ".blocks", false)){
                            i.setType(Material.EMERALD_BLOCK);
                          }
                          i.setAmount(level * plugin.getConfig().getInt(name + ".level_cost"));
                      }
                  }
                  k.setIngredients(ingredients);
                  merchant.setRecipes(sanityCheck(recipesAfterSanityCheck));
              }
              else if(k.getResult().getType() != Material.DIAMOND){

                  List<ItemStack> nonEbookIngredients = k.getIngredients();
                  Random rand = new Random();
                  int random = rand.nextInt(3);
                  String type;
                  switch (random){
                      case 1:
                          type = "gold";
                          break;
                      case 2:
                          type = "iron";
                          break;
                      case 0:
                      default:
                          type = "diamond";
                          break;
                  }

                  for (ItemStack i: nonEbookIngredients) {
                      if(i.getType() == Material.EMERALD){
                          switch(type){

                              case "diamond":
                                  int diamondTempAmount = (i.getAmount() * 2);
                                  if(diamondTempAmount > 64){
                                      i.setType(Material.DIAMOND_BLOCK);
                                      i.setAmount(diamondTempAmount/9);
                                  }
                                  else{
                                      i.setType(Material.DIAMOND);
                                      i.setAmount(diamondTempAmount);
                                  }
                                  break;

                              case "gold":
                                  int goldTempAmount = (i.getAmount() * 5);
                                  if(goldTempAmount > 64){
                                      i.setType(Material.GOLD_BLOCK);
                                      i.setAmount(goldTempAmount/9);
                                  }
                                  else{
                                      i.setType(Material.GOLD_INGOT);
                                      i.setAmount(goldTempAmount);
                                  }
                                  break;

                              case "iron":
                                  int ironTempAmount = (i.getAmount() * 6);
                                  if(ironTempAmount > 64){
                                      i.setType(Material.IRON_BLOCK);
                                      i.setAmount(ironTempAmount/9);
                                  }
                                  else{
                                      i.setType(Material.IRON_INGOT);
                                      i.setAmount(ironTempAmount);
                                  }
                                  break;
                          }
                      }
                  }
                  k.setIngredients(nonEbookIngredients);
                  merchant.setRecipes(sanityCheck(recipesAfterSanityCheck));
              }
            }
        }
    }

    public List<MerchantRecipe> sanityCheck(List<MerchantRecipe> recipes){

        //Sanity Check to ensure that shit doesnt happen.
        for (MerchantRecipe k: recipes) {

            List<ItemStack> ingredients = k.getIngredients();
            if(k.getResult().getType() == Material.ENCHANTED_BOOK){
                List<Material> materials = new ArrayList<>();
                for (ItemStack i:ingredients) {
                    materials.add(i.getType());
                }

                if (!materials.contains(Material.BOOK) || (!materials.contains(Material.EMERALD) && !materials.contains(Material.BOOK) ) ){
                    List<ItemStack> newItemStack = new ArrayList<>();

                    ItemStack book = new ItemStack(Material.BOOK);
                    ItemStack emerald = new ItemStack(Material.EMERALD);

                    book.setAmount(1);
                    emerald.setAmount(1);

                    k.setIngredients(newItemStack);
                }
            }
        }

        for (MerchantRecipe k: recipes) {

            List<ItemStack> ingredients = k.getIngredients();
            for (ItemStack i: ingredients) {
                if(i.getType().toString().equals("LEGACY_EMERALD"))
                {
                    i.setType(Material.EMERALD);
                    System.out.println("[Villager Tax] ERROR: Item Was found to be a Legacy_Item, Fixing this.");
                    System.out.println(i.getType());
                    System.out.println(i.hasItemMeta());
                }

                if(i.getAmount() > 64){
                    i.setAmount(64);
                    System.out.println("[Villager Tax] ERROR: Item " + i.getType() + " Was found to be outside of normal bounds with a value of " + i.getAmount() + ", Fixing this.");
                }

                if(i.getAmount() < 1){
                    i.setAmount(64);
                    System.out.println("[Villager Tax] ERROR: Item " + i.getType() + " Was found to be outside of normal bounds with a value of " + i.getAmount() + ", Fixing this.");
                }

            }
            k.setIngredients(ingredients);
        }

        return recipes;
    }


}
