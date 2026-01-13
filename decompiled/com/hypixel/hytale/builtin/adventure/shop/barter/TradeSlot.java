package com.hypixel.hytale.builtin.adventure.shop.barter;

import com.hypixel.hytale.codec.lookup.CodecMapCodec;
import java.util.List;
import java.util.Random;
import javax.annotation.Nonnull;

public abstract class TradeSlot {
   public static final CodecMapCodec<TradeSlot> CODEC = new CodecMapCodec<>("Type");
   public static final TradeSlot[] EMPTY_ARRAY = new TradeSlot[0];

   protected TradeSlot() {
   }

   @Nonnull
   public abstract List<BarterTrade> resolve(@Nonnull Random var1);

   public abstract int getSlotCount();

   static {
      CODEC.register("Fixed", FixedTradeSlot.class, FixedTradeSlot.CODEC);
      CODEC.register("Pool", PoolTradeSlot.class, PoolTradeSlot.CODEC);
   }
}
