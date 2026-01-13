package com.hypixel.hytale.builtin.adventure.npcreputation;

import com.hypixel.hytale.builtin.adventure.reputation.ReputationGroupComponent;
import com.hypixel.hytale.server.core.plugin.JavaPlugin;
import com.hypixel.hytale.server.core.plugin.JavaPluginInit;
import com.hypixel.hytale.server.npc.entities.NPCEntity;
import javax.annotation.Nonnull;

public class NPCReputationPlugin extends JavaPlugin {
   public NPCReputationPlugin(@Nonnull JavaPluginInit init) {
      super(init);
   }

   @Override
   protected void setup() {
      this.getEntityStoreRegistry().registerSystem(new ReputationAttitudeSystem());
      this.getEntityStoreRegistry().registerSystem(new NPCReputationHolderSystem(ReputationGroupComponent.getComponentType(), NPCEntity.getComponentType()));
   }
}
