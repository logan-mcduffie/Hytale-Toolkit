package com.hypixel.hytale.builtin.adventure.memories.memories.npc;

import com.hypixel.hytale.builtin.adventure.memories.MemoriesGameplayConfig;
import com.hypixel.hytale.builtin.adventure.memories.MemoriesPlugin;
import com.hypixel.hytale.builtin.adventure.memories.component.PlayerMemories;
import com.hypixel.hytale.builtin.adventure.memories.memories.Memory;
import com.hypixel.hytale.builtin.instances.config.InstanceDiscoveryConfig;
import com.hypixel.hytale.builtin.instances.config.InstanceWorldConfig;
import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.codec.validation.Validators;
import com.hypixel.hytale.component.AddReason;
import com.hypixel.hytale.component.ArchetypeChunk;
import com.hypixel.hytale.component.CommandBuffer;
import com.hypixel.hytale.component.Holder;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.component.query.Query;
import com.hypixel.hytale.component.spatial.SpatialResource;
import com.hypixel.hytale.component.system.tick.EntityTickingSystem;
import com.hypixel.hytale.math.util.MathUtil;
import com.hypixel.hytale.math.vector.Vector3d;
import com.hypixel.hytale.protocol.GameMode;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.inventory.ItemStack;
import com.hypixel.hytale.server.core.modules.entity.component.BoundingBox;
import com.hypixel.hytale.server.core.modules.entity.component.TransformComponent;
import com.hypixel.hytale.server.core.modules.entity.item.ItemComponent;
import com.hypixel.hytale.server.core.modules.entity.item.PickupItemComponent;
import com.hypixel.hytale.server.core.modules.i18n.I18nModule;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.hypixel.hytale.server.core.util.NotificationUtil;
import com.hypixel.hytale.server.npc.NPCPlugin;
import com.hypixel.hytale.server.npc.entities.NPCEntity;
import com.hypixel.hytale.server.npc.role.Role;
import com.hypixel.hytale.server.worldgen.chunk.ChunkGenerator;
import com.hypixel.hytale.server.worldgen.chunk.ZoneBiomeResult;
import it.unimi.dsi.fastutil.objects.ObjectList;
import java.util.Objects;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class NPCMemory extends Memory {
   @Nonnull
   public static final String ID = "NPC";
   @Nonnull
   public static final BuilderCodec<NPCMemory> CODEC = BuilderCodec.builder(NPCMemory.class, NPCMemory::new)
      .append(new KeyedCodec<>("NPCRole", Codec.STRING), (npcMemory, s) -> npcMemory.npcRole = s, npcMemory -> npcMemory.npcRole)
      .addValidator(Validators.nonNull())
      .add()
      .append(new KeyedCodec<>("TranslationKey", Codec.STRING), (npcMemory, s) -> npcMemory.memoryTitleKey = s, npcMemory -> npcMemory.memoryTitleKey)
      .add()
      .append(
         new KeyedCodec<>("IsMemoriesNameOverridden", Codec.BOOLEAN),
         (npcMemory, aBoolean) -> npcMemory.isMemoriesNameOverridden = aBoolean,
         npcMemory -> npcMemory.isMemoriesNameOverridden
      )
      .add()
      .append(
         new KeyedCodec<>("CapturedTimestamp", Codec.LONG),
         (npcMemory, aDouble) -> npcMemory.capturedTimestamp = aDouble,
         npcMemory -> npcMemory.capturedTimestamp
      )
      .add()
      .append(
         new KeyedCodec<>("FoundLocationZoneNameKey", Codec.STRING),
         (npcMemory, s) -> npcMemory.foundLocationZoneNameKey = s,
         npcMemory -> npcMemory.foundLocationZoneNameKey
      )
      .add()
      .append(
         new KeyedCodec<>("FoundLocationNameKey", Codec.STRING),
         (npcMemory, s) -> npcMemory.foundLocationGeneralNameKey = s,
         npcMemory -> npcMemory.foundLocationGeneralNameKey
      )
      .add()
      .afterDecode(NPCMemory::processConfig)
      .build();
   private String npcRole;
   private boolean isMemoriesNameOverridden;
   private long capturedTimestamp;
   private String foundLocationZoneNameKey;
   private String foundLocationGeneralNameKey;
   private String memoryTitleKey;

   private NPCMemory() {
   }

   public NPCMemory(@Nonnull String npcRole, @Nonnull String nameTranslationKey, boolean isMemoriesNameOverridden) {
      this.npcRole = npcRole;
      this.memoryTitleKey = nameTranslationKey;
      this.isMemoriesNameOverridden = isMemoriesNameOverridden;
      this.processConfig();
   }

   @Override
   public String getId() {
      return this.npcRole;
   }

   @Nonnull
   @Override
   public String getTitle() {
      return this.memoryTitleKey;
   }

   @Nonnull
   @Override
   public Message getTooltipText() {
      return Message.translation("server.memories.general.discovered.tooltipText");
   }

   @Nullable
   @Override
   public String getIconPath() {
      return "UI/Custom/Pages/Memories/npcs/" + this.npcRole + ".png";
   }

   public void processConfig() {
      if (this.isMemoriesNameOverridden) {
         this.memoryTitleKey = "server.npcRoles." + this.npcRole + ".name";
         if (I18nModule.get().getMessage("en-US", this.memoryTitleKey) == null) {
            this.memoryTitleKey = "server.memories.names." + this.npcRole;
         }
      }

      if (this.memoryTitleKey == null || this.memoryTitleKey.isEmpty()) {
         this.memoryTitleKey = "server.npcRoles." + this.npcRole + ".name";
      }
   }

   @Nonnull
   @Override
   public Message getUndiscoveredTooltipText() {
      return Message.translation("server.memories.general.undiscovered.tooltipText");
   }

   @Nonnull
   public String getNpcRole() {
      return this.npcRole;
   }

   public long getCapturedTimestamp() {
      return this.capturedTimestamp;
   }

   public String getFoundLocationZoneNameKey() {
      return this.foundLocationZoneNameKey;
   }

   public Message getLocationMessage() {
      if (this.foundLocationGeneralNameKey != null) {
         return Message.translation(this.foundLocationGeneralNameKey);
      } else {
         return this.foundLocationZoneNameKey != null ? Message.translation("server.map.region." + this.foundLocationZoneNameKey) : Message.raw("???");
      }
   }

   @Override
   public boolean equals(Object o) {
      if (o == null || this.getClass() != o.getClass()) {
         return false;
      } else if (!super.equals(o)) {
         return false;
      } else {
         NPCMemory npcMemory = (NPCMemory)o;
         return this.isMemoriesNameOverridden == npcMemory.isMemoriesNameOverridden && Objects.equals(this.npcRole, npcMemory.npcRole);
      }
   }

   @Override
   public int hashCode() {
      int result = super.hashCode();
      result = 31 * result + Objects.hashCode(this.npcRole);
      return 31 * result + Boolean.hashCode(this.isMemoriesNameOverridden);
   }

   @Override
   public String toString() {
      return "NPCMemory{npcRole='"
         + this.npcRole
         + "', isMemoriesNameOverride="
         + this.isMemoriesNameOverridden
         + "', capturedTimestamp="
         + this.capturedTimestamp
         + "', foundLocationZoneNameKey='"
         + this.foundLocationZoneNameKey
         + "}";
   }

   public static class GatherMemoriesSystem extends EntityTickingSystem<EntityStore> {
      @Nonnull
      public static final Query<EntityStore> QUERY = Query.and(
         TransformComponent.getComponentType(), Player.getComponentType(), PlayerMemories.getComponentType()
      );
      private final double radius;

      public GatherMemoriesSystem(double radius) {
         this.radius = radius;
      }

      @Override
      public void tick(
         float dt,
         int index,
         @Nonnull ArchetypeChunk<EntityStore> archetypeChunk,
         @Nonnull Store<EntityStore> store,
         @Nonnull CommandBuffer<EntityStore> commandBuffer
      ) {
         Player playerComponent = archetypeChunk.getComponent(index, Player.getComponentType());

         assert playerComponent != null;

         if (playerComponent.getGameMode() == GameMode.Adventure) {
            TransformComponent transformComponent = archetypeChunk.getComponent(index, TransformComponent.getComponentType());

            assert transformComponent != null;

            Vector3d position = transformComponent.getPosition();
            SpatialResource<Ref<EntityStore>, EntityStore> npcSpatialResource = store.getResource(NPCPlugin.get().getNpcSpatialResource());
            ObjectList<Ref<EntityStore>> results = SpatialResource.getThreadLocalReferenceList();
            npcSpatialResource.getSpatialStructure().collect(position, this.radius, results);
            if (!results.isEmpty()) {
               PlayerRef playerRefComponent = archetypeChunk.getComponent(index, PlayerRef.getComponentType());

               assert playerRefComponent != null;

               MemoriesPlugin memoriesPlugin = MemoriesPlugin.get();
               PlayerMemories playerMemoriesComponent = archetypeChunk.getComponent(index, PlayerMemories.getComponentType());

               assert playerMemoriesComponent != null;

               NPCMemory temp = new NPCMemory();
               World world = commandBuffer.getExternalData().getWorld();
               String foundLocationZoneNameKey = findLocationZoneName(world, position);

               for (Ref<EntityStore> npcRef : results) {
                  NPCEntity npcComponent = commandBuffer.getComponent(npcRef, NPCEntity.getComponentType());
                  if (npcComponent != null) {
                     Role role = npcComponent.getRole();

                     assert role != null;

                     if (role.isMemory()) {
                        temp.isMemoriesNameOverridden = role.isMemoriesNameOverriden();
                        temp.npcRole = temp.isMemoriesNameOverridden ? role.getMemoriesNameOverride() : npcComponent.getRoleName();
                        temp.memoryTitleKey = role.getNameTranslationKey();
                        temp.capturedTimestamp = System.currentTimeMillis();
                        temp.foundLocationGeneralNameKey = foundLocationZoneNameKey;
                        if (!memoriesPlugin.hasRecordedMemory(temp)) {
                           temp.processConfig();
                           if (playerMemoriesComponent.recordMemory(temp)) {
                              NotificationUtil.sendNotification(
                                 playerRefComponent.getPacketHandler(),
                                 Message.translation("server.memories.general.collected").param("memoryTitle", Message.translation(temp.getTitle())),
                                 null,
                                 "NotificationIcons/MemoriesIcon.png"
                              );
                              temp = new NPCMemory();
                              TransformComponent npcTransformComponent = commandBuffer.getComponent(npcRef, TransformComponent.getComponentType());

                              assert npcTransformComponent != null;

                              MemoriesGameplayConfig memoriesGameplayConfig = MemoriesGameplayConfig.get(store.getExternalData().getWorld().getGameplayConfig());
                              if (memoriesGameplayConfig != null) {
                                 ItemStack memoryItemStack = new ItemStack(memoriesGameplayConfig.getMemoriesCatchItemId());
                                 Vector3d memoryItemHolderPosition = npcTransformComponent.getPosition().clone();
                                 BoundingBox boundingBox = commandBuffer.getComponent(npcRef, BoundingBox.getComponentType());
                                 if (boundingBox != null) {
                                    memoryItemHolderPosition.y = memoryItemHolderPosition.y + boundingBox.getBoundingBox().middleY();
                                 }

                                 Holder<EntityStore> memoryItemHolder = ItemComponent.generatePickedUpItem(
                                    memoryItemStack, memoryItemHolderPosition, commandBuffer, playerRefComponent.getReference()
                                 );
                                 float memoryCatchItemLifetimeS = 0.62F;
                                 memoryItemHolder.getComponent(PickupItemComponent.getComponentType()).setInitialLifeTime(memoryCatchItemLifetimeS);
                                 commandBuffer.addEntity(memoryItemHolder, AddReason.SPAWN);
                              }
                           }
                        }
                     }
                  }
               }
            }
         }
      }

      private static String findLocationZoneName(World world, Vector3d position) {
         if (world.getChunkStore().getGenerator() instanceof ChunkGenerator generator) {
            int seed = (int)world.getWorldConfig().getSeed();
            ZoneBiomeResult result = generator.getZoneBiomeResultAt(seed, MathUtil.floor(position.x), MathUtil.floor(position.z));
            return "server.map.region." + result.getZoneResult().getZone().name();
         } else {
            InstanceWorldConfig instanceConfig = world.getWorldConfig().getPluginConfig().get(InstanceWorldConfig.class);
            if (instanceConfig != null) {
               InstanceDiscoveryConfig discovery = instanceConfig.getDiscovery();
               if (discovery != null && discovery.getTitleKey() != null) {
                  return discovery.getTitleKey();
               }
            }

            return "???";
         }
      }

      @Nonnull
      @Override
      public Query<EntityStore> getQuery() {
         return QUERY;
      }
   }
}
