package com.hypixel.hytale.builtin.crafting.window;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.hypixel.hytale.builtin.crafting.CraftingPlugin;
import com.hypixel.hytale.builtin.crafting.component.CraftingManager;
import com.hypixel.hytale.builtin.crafting.state.BenchState;
import com.hypixel.hytale.component.ComponentAccessor;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.event.EventPriority;
import com.hypixel.hytale.event.EventRegistration;
import com.hypixel.hytale.logger.HytaleLogger;
import com.hypixel.hytale.protocol.SoundCategory;
import com.hypixel.hytale.protocol.packets.window.CancelCraftingAction;
import com.hypixel.hytale.protocol.packets.window.CraftItemAction;
import com.hypixel.hytale.protocol.packets.window.UpdateCategoryAction;
import com.hypixel.hytale.protocol.packets.window.WindowAction;
import com.hypixel.hytale.protocol.packets.window.WindowType;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.asset.type.blocktype.config.bench.CraftingBench;
import com.hypixel.hytale.server.core.asset.type.blocktype.config.bench.DiagramCraftingBench;
import com.hypixel.hytale.server.core.asset.type.item.config.CraftingRecipe;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.entity.entities.player.windows.ItemContainerWindow;
import com.hypixel.hytale.server.core.inventory.Inventory;
import com.hypixel.hytale.server.core.inventory.ItemStack;
import com.hypixel.hytale.server.core.inventory.container.CombinedItemContainer;
import com.hypixel.hytale.server.core.inventory.container.ItemContainer;
import com.hypixel.hytale.server.core.inventory.container.SimpleItemContainer;
import com.hypixel.hytale.server.core.inventory.container.filter.FilterActionType;
import com.hypixel.hytale.server.core.inventory.container.filter.FilterType;
import com.hypixel.hytale.server.core.inventory.container.filter.SlotFilter;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.SoundUtil;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectList;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class DiagramCraftingWindow extends CraftingWindow implements ItemContainerWindow {
   private static final HytaleLogger LOGGER = HytaleLogger.forEnclosingClass();
   private String category;
   private String itemCategory;
   private CraftingBench.BenchItemCategory benchItemCategory;
   private SimpleItemContainer inputPrimaryContainer;
   private SimpleItemContainer inputSecondaryContainer;
   private CombinedItemContainer combinedInputItemContainer;
   private SimpleItemContainer outputContainer;
   private CombinedItemContainer combinedItemContainer;
   private EventRegistration inventoryRegistration;

   public DiagramCraftingWindow(@Nonnull ComponentAccessor<EntityStore> store, BenchState benchState) {
      super(WindowType.DiagramCrafting, benchState);
      DiagramCraftingBench bench = (DiagramCraftingBench)this.bench;
      if (bench.getCategories() != null && bench.getCategories().length > 0) {
         CraftingBench.BenchCategory benchCategory = bench.getCategories()[0];
         this.category = benchCategory.getId();
         if (benchCategory.getItemCategories() != null && benchCategory.getItemCategories().length > 0) {
            this.itemCategory = benchCategory.getItemCategories()[0].getId();
         }
      }

      this.benchItemCategory = this.getBenchItemCategory(this.category, this.itemCategory);
      if (this.benchItemCategory == null) {
         throw new IllegalArgumentException("Failed to get category!");
      } else {
         this.updateInventory(store, this.benchItemCategory);
      }
   }

   @Override
   protected void finalize() {
      if (this.inventoryRegistration.isRegistered()) {
         throw new IllegalStateException("Failed to unregister inventory event!");
      }
   }

   @Override
   public boolean onOpen0() {
      boolean result = super.onOpen0();
      PlayerRef playerRef = this.getPlayerRef();
      Ref<EntityStore> ref = playerRef.getReference();
      Store<EntityStore> store = ref.getStore();
      Player player = store.getComponent(ref, Player.getComponentType());
      Inventory inventory = player.getInventory();
      this.updateInput((ItemContainer)null);
      this.inventoryRegistration = inventory.getCombinedHotbarFirst().registerChangeEvent(event -> {
         ObjectList<CraftingRecipe> recipes = new ObjectArrayList<>();
         this.windowData.add("slots", this.generateSlots(inventory.getCombinedHotbarFirst(), recipes));
         this.invalidate();
      });
      return result;
   }

   @Override
   public void onClose0() {
      PlayerRef playerRef = this.getPlayerRef();
      Ref<EntityStore> ref = playerRef.getReference();
      Store<EntityStore> store = ref.getStore();
      Player player = store.getComponent(ref, Player.getComponentType());
      List<ItemStack> itemStacks = this.combinedInputItemContainer.dropAllItemStacks();
      SimpleItemContainer.addOrDropItemStacks(store, ref, player.getInventory().getCombinedHotbarFirst(), itemStacks);
      CraftingManager craftingManager = store.getComponent(ref, CraftingManager.getComponentType());
      craftingManager.cancelAllCrafting(ref, store);
      this.inventoryRegistration.unregister();
      super.onClose0();
   }

   @Override
   public void handleAction(@Nonnull Ref<EntityStore> ref, @Nonnull Store<EntityStore> store, @Nonnull WindowAction action) {
      World world = store.getExternalData().getWorld();
      PlayerRef playerRef = this.getPlayerRef();
      CraftingManager craftingManager = store.getComponent(ref, CraftingManager.getComponentType());
      switch (action) {
         case CancelCraftingAction ignored:
            craftingManager.cancelAllCrafting(ref, store);
            break;
         case UpdateCategoryAction updateAction:
            this.category = updateAction.category;
            this.itemCategory = updateAction.itemCategory;
            this.benchItemCategory = this.getBenchItemCategory(this.category, this.itemCategory);
            if (this.benchItemCategory != null) {
               this.updateInventory(store, this.benchItemCategory);
            } else {
               this.getPlayerRef().sendMessage(Message.translation("server.ui.diagramcraftingwindow.invalidCategory"));
               this.close();
            }
            break;
         case CraftItemAction ignoredx:
            label45: {
               ItemStack itemStack = this.outputContainer.getItemStack((short)0);
               if (itemStack == null || itemStack.isEmpty()) {
                  playerRef.sendMessage(Message.translation("server.ui.diagramcraftingwindow.noOutputItem"));
                  return;
               }

               ObjectList<CraftingRecipe> recipes = new ObjectArrayList<>();
               boolean allSlotsFull = this.collectRecipes(recipes);
               if (recipes.size() != 1 || !allSlotsFull) {
                  playerRef.sendMessage(Message.translation("server.ui.diagramcraftingwindow.failedVerifyRecipy"));
                  return;
               }

               CraftingRecipe recipe = recipes.getFirst();
               craftingManager.queueCraft(ref, store, this, 0, recipe, 1, this.combinedInputItemContainer, CraftingManager.InputRemovalType.ORDERED);
               String completedState = recipe.getTimeSeconds() > 0.0F ? "CraftCompleted" : "CraftCompletedInstant";
               this.setBlockInteractionState(completedState, world, 70);
               if (this.bench.getCompletedSoundEventIndex() != 0) {
                  SoundUtil.playSoundEvent3d(this.bench.getCompletedSoundEventIndex(), SoundCategory.SFX, this.x + 0.5, this.y + 0.5, this.z + 0.5, store);
               }

               if (CraftingPlugin.learnRecipe(ref, recipe.getId(), store)) {
                  this.updateInput(this.outputContainer);
               }
               break label45;
            }
         default:
      }
   }

   @Nonnull
   @Override
   public ItemContainer getItemContainer() {
      return this.combinedItemContainer;
   }

   private CraftingBench.BenchItemCategory getBenchItemCategory(@Nullable String category, @Nullable String itemCategory) {
      if (category != null && itemCategory != null) {
         DiagramCraftingBench craftingBench = (DiagramCraftingBench)this.bench;

         for (CraftingBench.BenchCategory benchCategory : craftingBench.getCategories()) {
            if (category.equals(benchCategory.getId())) {
               for (CraftingBench.BenchItemCategory benchItemCategory : benchCategory.getItemCategories()) {
                  if (itemCategory.equals(benchItemCategory.getId())) {
                     return benchItemCategory;
                  }
               }
            }
         }

         return null;
      } else {
         return null;
      }
   }

   private void updateInventory(@Nonnull ComponentAccessor<EntityStore> store, @Nonnull CraftingBench.BenchItemCategory benchItemCategory) {
      if (this.combinedInputItemContainer != null) {
         PlayerRef playerRef = this.getPlayerRef();
         Ref<EntityStore> ref = playerRef.getReference();
         Player playerComponent = store.getComponent(ref, Player.getComponentType());
         List<ItemStack> itemStacks = this.combinedInputItemContainer.dropAllItemStacks();
         SimpleItemContainer.addOrDropItemStacks(store, ref, playerComponent.getInventory().getCombinedHotbarFirst(), itemStacks);
      }

      this.inputPrimaryContainer = new SimpleItemContainer((short)1);
      this.inputSecondaryContainer = new SimpleItemContainer((short)(benchItemCategory.getSlots() + (benchItemCategory.isSpecialSlot() ? 1 : 0)));
      this.inputSecondaryContainer.setGlobalFilter(FilterType.ALLOW_OUTPUT_ONLY);
      this.combinedInputItemContainer = new CombinedItemContainer(this.inputPrimaryContainer, this.inputSecondaryContainer);
      this.combinedInputItemContainer.registerChangeEvent(EventPriority.LAST, this::updateInput);
      this.outputContainer = new SimpleItemContainer((short)1);
      this.outputContainer.setGlobalFilter(FilterType.DENY_ALL);
      this.combinedItemContainer = new CombinedItemContainer(this.combinedInputItemContainer, this.outputContainer);
   }

   private void updateInput(@Nonnull ItemContainer.ItemContainerChangeEvent event) {
      this.updateInput(event.container());
   }

   private void updateInput(@Nullable ItemContainer container) {
      PlayerRef playerRef = this.getPlayerRef();
      Ref<EntityStore> ref = playerRef.getReference();
      Store<EntityStore> store = ref.getStore();
      Player player = store.getComponent(ref, Player.getComponentType());
      ItemStack primaryItemStack = this.inputPrimaryContainer.getItemStack((short)0);
      CombinedItemContainer combinedStorage = player.getInventory().getCombinedHotbarFirst();
      if (primaryItemStack != null && !primaryItemStack.isEmpty()) {
         this.inputSecondaryContainer.setGlobalFilter(FilterType.ALLOW_ALL);
         boolean needsDropSlot = true;

         for (short i = 0; i < this.inputSecondaryContainer.getCapacity(); i++) {
            ItemStack itemStack = this.inputSecondaryContainer.getItemStack(i);
            if (itemStack != null && !itemStack.isEmpty()) {
               this.inputSecondaryContainer.setSlotFilter(FilterActionType.ADD, i, null);
            } else if (needsDropSlot) {
               this.inputSecondaryContainer.setSlotFilter(FilterActionType.ADD, i, null);
               needsDropSlot = false;
            } else {
               this.inputSecondaryContainer.setSlotFilter(FilterActionType.ADD, i, SlotFilter.DENY);
            }
         }
      } else {
         this.inputSecondaryContainer.setGlobalFilter(FilterType.ALLOW_OUTPUT_ONLY);
         if (container != this.inputSecondaryContainer && !this.inputSecondaryContainer.isEmpty()) {
            List<ItemStack> itemStacks = this.inputSecondaryContainer.dropAllItemStacks();
            SimpleItemContainer.addOrDropItemStacks(store, ref, combinedStorage, itemStacks);
         }
      }

      List<CraftingRecipe> recipes = new ObjectArrayList<>();
      boolean allSlotsFull = this.collectRecipes(recipes);
      this.windowData.add("slots", this.generateSlots(combinedStorage, recipes));
      if (recipes.size() == 1 && allSlotsFull) {
         CraftingRecipe recipe = recipes.getFirst();
         ItemStack output = CraftingManager.getOutputItemStacks(recipe).getFirst();
         if (player.getPlayerConfigData().getKnownRecipes().contains(recipe.getId())) {
            this.outputContainer.setItemStackForSlot((short)0, output);
         } else {
            this.outputContainer.setItemStackForSlot((short)0, new ItemStack("Unknown", 1));
         }
      } else {
         if (!recipes.isEmpty() && allSlotsFull) {
            LOGGER.at(Level.WARNING).log("Multiple recipes defined for the same materials! %s", recipes);
         }

         this.outputContainer.setItemStackForSlot((short)0, ItemStack.EMPTY);
      }

      this.invalidate();
   }

   private boolean collectRecipes(@Nonnull List<CraftingRecipe> recipes) {
      ItemStack primaryItemStack = this.inputPrimaryContainer.getItemStack((short)0);
      if (primaryItemStack != null && !primaryItemStack.isEmpty()) {
         PlayerRef playerRef = this.getPlayerRef();
         Ref<EntityStore> ref = playerRef.getReference();
         Store<EntityStore> store = ref.getStore();
         Player player = store.getComponent(ref, Player.getComponentType());
         Set<String> knownRecipes = player.getPlayerConfigData().getKnownRecipes();
         short inputCapacity = this.combinedInputItemContainer.getCapacity();
         boolean allSlotsFull = true;

         label54:
         for (CraftingRecipe recipe : this.getBenchRecipes()) {
            if (recipe.getInput().length != inputCapacity && (!this.benchItemCategory.isSpecialSlot() || recipe.getInput().length != inputCapacity - 1)) {
               LOGGER.at(Level.WARNING)
                  .log(
                     "Recipe for %s has different input length than the diagram! %s - %s, %s, %s",
                     recipe.getId(),
                     recipe,
                     this.bench,
                     this.category,
                     this.itemCategory
                  );
            } else if (!recipe.isKnowledgeRequired() || knownRecipes.contains(recipe.getId())) {
               for (short i = 0; i < inputCapacity; i++) {
                  ItemStack itemStack = this.combinedInputItemContainer.getItemStack(i);
                  if (itemStack != null && !itemStack.isEmpty()) {
                     if (!CraftingManager.matches(recipe.getInput()[i], itemStack)) {
                        continue label54;
                     }
                  } else if (!this.benchItemCategory.isSpecialSlot() && i == inputCapacity - 1) {
                     allSlotsFull = false;
                  }
               }

               recipes.add(recipe);
            }
         }

         return allSlotsFull;
      } else {
         return false;
      }
   }

   @Nonnull
   private JsonArray generateSlots(@Nonnull CombinedItemContainer combinedStorage, @Nonnull List<CraftingRecipe> recipes) {
      JsonArray slots = new JsonArray();
      if (recipes.isEmpty()) {
         List<CraftingRecipe> benchRecipes = this.getBenchRecipes();
         JsonObject slot = new JsonObject();
         slot.add("inventoryHints", CraftingManager.generateInventoryHints(benchRecipes, 0, combinedStorage));
         slots.add(slot);
      } else {
         for (short i = 0; i < this.combinedInputItemContainer.getCapacity(); i++) {
            JsonObject slot = new JsonObject();
            ItemStack itemStack = this.combinedInputItemContainer.getItemStack(i);
            if (itemStack == null || itemStack.isEmpty()) {
               slot.add("inventoryHints", CraftingManager.generateInventoryHints(recipes, i, combinedStorage));
            }

            int requiredAmount = -1;
            if (recipes.size() == 1) {
               CraftingRecipe recipe = recipes.getFirst();
               if (i < recipe.getInput().length) {
                  requiredAmount = recipe.getInput()[i].getQuantity();
               }
            }

            slot.addProperty("requiredAmount", requiredAmount);
            slots.add(slot);
         }
      }

      return slots;
   }

   @Nonnull
   public List<CraftingRecipe> getBenchRecipes() {
      return CraftingPlugin.getBenchRecipes(this.bench.getType(), this.bench.getId(), this.category + "." + this.itemCategory);
   }
}
