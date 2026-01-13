package com.hypixel.hytale.builtin.adventure.npcobjectives.task;

import com.hypixel.hytale.builtin.adventure.npcobjectives.assets.BountyObjectiveTaskAsset;
import com.hypixel.hytale.builtin.adventure.npcobjectives.resources.KillTrackerResource;
import com.hypixel.hytale.builtin.adventure.npcobjectives.transaction.KillTaskTransaction;
import com.hypixel.hytale.builtin.adventure.objectives.Objective;
import com.hypixel.hytale.builtin.adventure.objectives.ObjectivePlugin;
import com.hypixel.hytale.builtin.adventure.objectives.config.task.ObjectiveTaskAsset;
import com.hypixel.hytale.builtin.adventure.objectives.task.ObjectiveTask;
import com.hypixel.hytale.builtin.adventure.objectives.transaction.RegistrationTransactionRecord;
import com.hypixel.hytale.builtin.adventure.objectives.transaction.SpawnEntityTransactionRecord;
import com.hypixel.hytale.builtin.adventure.objectives.transaction.TransactionRecord;
import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.math.vector.Transform;
import com.hypixel.hytale.math.vector.Vector3d;
import com.hypixel.hytale.math.vector.Vector3f;
import com.hypixel.hytale.math.vector.Vector3i;
import com.hypixel.hytale.protocol.packets.worldmap.MapMarker;
import com.hypixel.hytale.server.core.entity.UUIDComponent;
import com.hypixel.hytale.server.core.modules.entity.damage.Damage;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.npc.INonPlayerCharacter;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.hypixel.hytale.server.core.util.PositionUtil;
import com.hypixel.hytale.server.npc.NPCPlugin;
import com.hypixel.hytale.server.npc.entities.NPCEntity;
import it.unimi.dsi.fastutil.Pair;
import java.util.UUID;
import java.util.logging.Level;
import javax.annotation.Nonnull;

public class BountyObjectiveTask extends ObjectiveTask implements KillTask {
   public static final BuilderCodec<BountyObjectiveTask> CODEC = BuilderCodec.builder(
         BountyObjectiveTask.class, BountyObjectiveTask::new, ObjectiveTask.BASE_CODEC
      )
      .append(
         new KeyedCodec<>("Completed", Codec.BOOLEAN),
         (bountyObjectiveTask, aBoolean) -> bountyObjectiveTask.completed = aBoolean,
         bountyObjectiveTask -> bountyObjectiveTask.completed
      )
      .add()
      .append(
         new KeyedCodec<>("EntityUUID", Codec.UUID_BINARY),
         (bountyObjectiveTask, uuid) -> bountyObjectiveTask.entityUuid = uuid,
         bountyObjectiveTask -> bountyObjectiveTask.entityUuid
      )
      .add()
      .build();
   boolean completed;
   UUID entityUuid;

   public BountyObjectiveTask(@Nonnull ObjectiveTaskAsset asset, int taskSetIndex, int taskIndex) {
      super(asset, taskSetIndex, taskIndex);
   }

   protected BountyObjectiveTask() {
   }

   @Nonnull
   public BountyObjectiveTaskAsset getAsset() {
      return (BountyObjectiveTaskAsset)super.getAsset();
   }

   @Nonnull
   @Override
   protected TransactionRecord[] setup0(@Nonnull Objective objective, @Nonnull World world, @Nonnull Store<EntityStore> store) {
      if (this.serializedTransactionRecords != null) {
         return RegistrationTransactionRecord.append(this.serializedTransactionRecords, this.eventRegistry);
      } else {
         Vector3d objectivePosition = objective.getPosition(store);

         assert objectivePosition != null;

         Vector3i spawnPosition = this.getAsset().getWorldLocationProvider().runCondition(world, objectivePosition.clone().floor().toVector3i());
         TransactionRecord[] transactionRecords = new TransactionRecord[2];
         Pair<Ref<EntityStore>, INonPlayerCharacter> npcPair = NPCPlugin.get()
            .spawnNPC(store, this.getAsset().getNpcId(), null, spawnPosition.toVector3d(), Vector3f.ZERO);
         Ref<EntityStore> npcReference = npcPair.first();
         UUIDComponent npcUuidComponent = store.getComponent(npcReference, UUIDComponent.getComponentType());

         assert npcUuidComponent != null;

         UUID npcUuid = npcUuidComponent.getUuid();
         ObjectivePlugin.get().getLogger().at(Level.INFO).log("Spawned Entity '" + this.getAsset().getNpcId() + "' at position: " + spawnPosition);
         transactionRecords[0] = new SpawnEntityTransactionRecord(world.getWorldConfig().getUuid(), npcUuid);
         this.entityUuid = npcUuid;
         this.addMarker(
            new MapMarker(getBountyMarkerIDFromUUID(npcUuid), "Bounty Target", "Home.png", PositionUtil.toTransformPacket(new Transform(spawnPosition)), null)
         );
         KillTaskTransaction transaction = new KillTaskTransaction(this, objective, store);
         store.getResource(KillTrackerResource.getResourceType()).watch(transaction);
         transactionRecords[1] = transaction;
         return transactionRecords;
      }
   }

   @Override
   public boolean checkCompletion() {
      return this.completed;
   }

   @Nonnull
   public static String getBountyMarkerIDFromUUID(@Nonnull UUID uuid) {
      return "Bounty_" + uuid;
   }

   @Override
   public void checkKilledEntity(
      @Nonnull Store<EntityStore> store, @Nonnull Ref<EntityStore> npcRef, @Nonnull Objective objective, NPCEntity npc, Damage damageInfo
   ) {
      UUIDComponent uuidComponent = store.getComponent(npcRef, UUIDComponent.getComponentType());

      assert uuidComponent != null;

      UUID uuid = uuidComponent.getUuid();
      if (this.entityUuid.equals(uuid)) {
         this.completed = true;
         this.consumeTaskConditions(store, npcRef, objective.getPlayerUUIDs());
         this.complete(objective, store);
         objective.checkTaskSetCompletion(store);
         this.removeMarker(getBountyMarkerIDFromUUID(uuid));
      }
   }

   @Nonnull
   public com.hypixel.hytale.protocol.ObjectiveTask toPacket(@Nonnull Objective objective) {
      com.hypixel.hytale.protocol.ObjectiveTask packet = new com.hypixel.hytale.protocol.ObjectiveTask();
      packet.taskDescriptionKey = this.asset.getDescriptionKey(objective.getObjectiveId(), this.taskSetIndex, this.taskIndex);
      packet.currentCompletion = this.completed ? 1 : 0;
      packet.completionNeeded = 1;
      return packet;
   }

   @Nonnull
   @Override
   public String toString() {
      return "BountyObjectiveTask{completed=" + this.completed + ", entityUuid=" + this.entityUuid + "} " + super.toString();
   }
}
