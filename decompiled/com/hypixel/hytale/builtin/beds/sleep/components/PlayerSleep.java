package com.hypixel.hytale.builtin.beds.sleep.components;

import com.hypixel.hytale.server.core.modules.time.WorldTimeResource;
import java.time.Instant;

public sealed interface PlayerSleep permits PlayerSleep.FullyAwake, PlayerSleep.MorningWakeUp, PlayerSleep.NoddingOff, PlayerSleep.Slumber {
   public static enum FullyAwake implements PlayerSleep {
      INSTANCE;
   }

   public record MorningWakeUp(Instant gameTimeStart) implements PlayerSleep {
      public static PlayerSomnolence createComponent(WorldTimeResource worldTimeResource) {
         Instant now = worldTimeResource.getGameTime();
         PlayerSleep.MorningWakeUp state = new PlayerSleep.MorningWakeUp(now);
         return new PlayerSomnolence(state);
      }
   }

   public record NoddingOff(Instant realTimeStart) implements PlayerSleep {
      public static PlayerSomnolence createComponent() {
         Instant now = Instant.now();
         PlayerSleep.NoddingOff state = new PlayerSleep.NoddingOff(now);
         return new PlayerSomnolence(state);
      }
   }

   public record Slumber(Instant gameTimeStart) implements PlayerSleep {
      public static PlayerSomnolence createComponent(WorldTimeResource worldTimeResource) {
         Instant now = worldTimeResource.getGameTime();
         PlayerSleep.Slumber state = new PlayerSleep.Slumber(now);
         return new PlayerSomnolence(state);
      }
   }
}
