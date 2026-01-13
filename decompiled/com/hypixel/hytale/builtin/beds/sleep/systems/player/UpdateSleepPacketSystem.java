package com.hypixel.hytale.builtin.beds.sleep.systems.player;

import com.hypixel.hytale.builtin.beds.sleep.components.PlayerSleep;
import com.hypixel.hytale.builtin.beds.sleep.components.PlayerSomnolence;
import com.hypixel.hytale.builtin.beds.sleep.components.SleepTracker;
import com.hypixel.hytale.builtin.beds.sleep.resources.WorldSleep;
import com.hypixel.hytale.builtin.beds.sleep.resources.WorldSlumber;
import com.hypixel.hytale.builtin.beds.sleep.resources.WorldSomnolence;
import com.hypixel.hytale.builtin.beds.sleep.systems.world.CanSleepInWorld;
import com.hypixel.hytale.builtin.beds.sleep.systems.world.StartSlumberSystem;
import com.hypixel.hytale.component.ArchetypeChunk;
import com.hypixel.hytale.component.CommandBuffer;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.component.query.Query;
import com.hypixel.hytale.component.system.tick.DelayedEntitySystem;
import com.hypixel.hytale.protocol.packets.world.SleepClock;
import com.hypixel.hytale.protocol.packets.world.SleepMultiplayer;
import com.hypixel.hytale.protocol.packets.world.UpdateSleepState;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import javax.annotation.Nullable;
import org.checkerframework.checker.nullness.compatqual.NonNullDecl;

public class UpdateSleepPacketSystem extends DelayedEntitySystem<EntityStore> {
   public static final Query<EntityStore> QUERY = Query.and(PlayerRef.getComponentType(), PlayerSomnolence.getComponentType(), SleepTracker.getComponentType());
   public static final Duration SPAN_BEFORE_BLACK_SCREEN = Duration.ofMillis(1200L);
   public static final int MAX_SAMPLE_COUNT = 5;
   private static final UUID[] EMPTY_UUIDS = new UUID[0];
   private static final UpdateSleepState PACKET_NO_SLEEP_UI = new UpdateSleepState(false, false, null, null);

   @Override
   public Query<EntityStore> getQuery() {
      return QUERY;
   }

   public UpdateSleepPacketSystem() {
      super(0.25F);
   }

   @Override
   public void tick(
      float dt,
      int index,
      @NonNullDecl ArchetypeChunk<EntityStore> archetypeChunk,
      @NonNullDecl Store<EntityStore> store,
      @NonNullDecl CommandBuffer<EntityStore> commandBuffer
   ) {
      UpdateSleepState packet = this.createSleepPacket(store, index, archetypeChunk);
      SleepTracker sleepTracker = archetypeChunk.getComponent(index, SleepTracker.getComponentType());
      packet = sleepTracker.generatePacketToSend(packet);
      if (packet != null) {
         PlayerRef playerRef = archetypeChunk.getComponent(index, PlayerRef.getComponentType());
         playerRef.getPacketHandler().write(packet);
      }
   }

   private UpdateSleepState createSleepPacket(Store<EntityStore> store, int index, ArchetypeChunk<EntityStore> archetypeChunk) {
      World world = store.getExternalData().getWorld();
      WorldSomnolence worldSomnolence = store.getResource(WorldSomnolence.getResourceType());
      WorldSleep worldSleepState = worldSomnolence.getState();
      PlayerSomnolence playerSomnolence = archetypeChunk.getComponent(index, PlayerSomnolence.getComponentType());
      PlayerSleep playerSleepState = playerSomnolence.getSleepState();
      SleepClock clock = worldSleepState instanceof WorldSlumber slumber ? slumber.createSleepClock() : null;

      return switch (playerSleepState) {
         case PlayerSleep.FullyAwake ignored -> PACKET_NO_SLEEP_UI;
         case PlayerSleep.MorningWakeUp ignoredx -> PACKET_NO_SLEEP_UI;
         case PlayerSleep.NoddingOff noddingOff -> {
            if (CanSleepInWorld.check(world).isNegative()) {
               yield PACKET_NO_SLEEP_UI;
            } else {
               long elapsedMs = Duration.between(noddingOff.realTimeStart(), Instant.now()).toMillis();
               boolean grayFade = elapsedMs > SPAN_BEFORE_BLACK_SCREEN.toMillis();
               Ref<EntityStore> ref = archetypeChunk.getReferenceTo(index);
               boolean readyToSleep = StartSlumberSystem.isReadyToSleep(store, ref);
               yield new UpdateSleepState(grayFade, false, clock, readyToSleep ? this.createSleepMultiplayer(store) : null);
            }
         }
         case PlayerSleep.Slumber ignoredxx -> new UpdateSleepState(true, true, clock, null);
         default -> throw new MatchException(null, null);
      };
   }

   @Nullable
   private SleepMultiplayer createSleepMultiplayer(Store<EntityStore> store) {
      World world = store.getExternalData().getWorld();
      List<PlayerRef> playerRefs = new ArrayList<>(world.getPlayerRefs());
      if (playerRefs.size() <= 1) {
         return null;
      } else {
         playerRefs.sort(Comparator.comparingLong(refx -> refx.getUuid().hashCode() + world.hashCode()));
         int sleepersCount = 0;
         int awakeCount = 0;
         List<UUID> awakeSampleList = new ArrayList<>(playerRefs.size());

         for (PlayerRef playerRef : playerRefs) {
            Ref<EntityStore> ref = playerRef.getReference();
            boolean readyToSleep = StartSlumberSystem.isReadyToSleep(store, ref);
            if (readyToSleep) {
               sleepersCount++;
            } else {
               awakeCount++;
               awakeSampleList.add(playerRef.getUuid());
            }
         }

         UUID[] awakeSample = awakeSampleList.size() > 5 ? EMPTY_UUIDS : awakeSampleList.toArray(UUID[]::new);
         return new SleepMultiplayer(sleepersCount, awakeCount, awakeSample);
      }
   }
}
