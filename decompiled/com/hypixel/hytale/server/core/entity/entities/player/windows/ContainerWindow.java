package com.hypixel.hytale.server.core.entity.entities.player.windows;

import com.google.gson.JsonObject;
import com.hypixel.hytale.protocol.packets.window.WindowType;
import com.hypixel.hytale.server.core.inventory.container.ItemContainer;
import javax.annotation.Nonnull;

public class ContainerWindow extends Window implements ItemContainerWindow {
   @Nonnull
   private final JsonObject windowData;
   @Nonnull
   private final ItemContainer itemContainer;

   public ContainerWindow(@Nonnull ItemContainer itemContainer) {
      super(WindowType.Container);
      this.itemContainer = itemContainer;
      this.windowData = new JsonObject();
   }

   @Nonnull
   @Override
   public JsonObject getData() {
      return this.windowData;
   }

   @Override
   public boolean onOpen0() {
      return true;
   }

   @Override
   public void onClose0() {
   }

   @Nonnull
   @Override
   public ItemContainer getItemContainer() {
      return this.itemContainer;
   }
}
