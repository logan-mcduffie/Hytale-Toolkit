package com.hypixel.hytale.protocol.packets.player;

import com.hypixel.hytale.protocol.Packet;
import com.hypixel.hytale.protocol.PickupLocation;
import com.hypixel.hytale.protocol.io.ValidationResult;
import io.netty.buffer.ByteBuf;
import java.util.Objects;
import javax.annotation.Nonnull;

public class SyncPlayerPreferences implements Packet {
   public static final int PACKET_ID = 116;
   public static final boolean IS_COMPRESSED = false;
   public static final int NULLABLE_BIT_FIELD_SIZE = 0;
   public static final int FIXED_BLOCK_SIZE = 8;
   public static final int VARIABLE_FIELD_COUNT = 0;
   public static final int VARIABLE_BLOCK_START = 8;
   public static final int MAX_SIZE = 8;
   public boolean showEntityMarkers;
   @Nonnull
   public PickupLocation armorItemsPreferredPickupLocation = PickupLocation.Hotbar;
   @Nonnull
   public PickupLocation weaponAndToolItemsPreferredPickupLocation = PickupLocation.Hotbar;
   @Nonnull
   public PickupLocation usableItemsItemsPreferredPickupLocation = PickupLocation.Hotbar;
   @Nonnull
   public PickupLocation solidBlockItemsPreferredPickupLocation = PickupLocation.Hotbar;
   @Nonnull
   public PickupLocation miscItemsPreferredPickupLocation = PickupLocation.Hotbar;
   public boolean allowNPCDetection;
   public boolean respondToHit;

   @Override
   public int getId() {
      return 116;
   }

   public SyncPlayerPreferences() {
   }

   public SyncPlayerPreferences(
      boolean showEntityMarkers,
      @Nonnull PickupLocation armorItemsPreferredPickupLocation,
      @Nonnull PickupLocation weaponAndToolItemsPreferredPickupLocation,
      @Nonnull PickupLocation usableItemsItemsPreferredPickupLocation,
      @Nonnull PickupLocation solidBlockItemsPreferredPickupLocation,
      @Nonnull PickupLocation miscItemsPreferredPickupLocation,
      boolean allowNPCDetection,
      boolean respondToHit
   ) {
      this.showEntityMarkers = showEntityMarkers;
      this.armorItemsPreferredPickupLocation = armorItemsPreferredPickupLocation;
      this.weaponAndToolItemsPreferredPickupLocation = weaponAndToolItemsPreferredPickupLocation;
      this.usableItemsItemsPreferredPickupLocation = usableItemsItemsPreferredPickupLocation;
      this.solidBlockItemsPreferredPickupLocation = solidBlockItemsPreferredPickupLocation;
      this.miscItemsPreferredPickupLocation = miscItemsPreferredPickupLocation;
      this.allowNPCDetection = allowNPCDetection;
      this.respondToHit = respondToHit;
   }

   public SyncPlayerPreferences(@Nonnull SyncPlayerPreferences other) {
      this.showEntityMarkers = other.showEntityMarkers;
      this.armorItemsPreferredPickupLocation = other.armorItemsPreferredPickupLocation;
      this.weaponAndToolItemsPreferredPickupLocation = other.weaponAndToolItemsPreferredPickupLocation;
      this.usableItemsItemsPreferredPickupLocation = other.usableItemsItemsPreferredPickupLocation;
      this.solidBlockItemsPreferredPickupLocation = other.solidBlockItemsPreferredPickupLocation;
      this.miscItemsPreferredPickupLocation = other.miscItemsPreferredPickupLocation;
      this.allowNPCDetection = other.allowNPCDetection;
      this.respondToHit = other.respondToHit;
   }

   @Nonnull
   public static SyncPlayerPreferences deserialize(@Nonnull ByteBuf buf, int offset) {
      SyncPlayerPreferences obj = new SyncPlayerPreferences();
      obj.showEntityMarkers = buf.getByte(offset + 0) != 0;
      obj.armorItemsPreferredPickupLocation = PickupLocation.fromValue(buf.getByte(offset + 1));
      obj.weaponAndToolItemsPreferredPickupLocation = PickupLocation.fromValue(buf.getByte(offset + 2));
      obj.usableItemsItemsPreferredPickupLocation = PickupLocation.fromValue(buf.getByte(offset + 3));
      obj.solidBlockItemsPreferredPickupLocation = PickupLocation.fromValue(buf.getByte(offset + 4));
      obj.miscItemsPreferredPickupLocation = PickupLocation.fromValue(buf.getByte(offset + 5));
      obj.allowNPCDetection = buf.getByte(offset + 6) != 0;
      obj.respondToHit = buf.getByte(offset + 7) != 0;
      return obj;
   }

   public static int computeBytesConsumed(@Nonnull ByteBuf buf, int offset) {
      return 8;
   }

   @Override
   public void serialize(@Nonnull ByteBuf buf) {
      buf.writeByte(this.showEntityMarkers ? 1 : 0);
      buf.writeByte(this.armorItemsPreferredPickupLocation.getValue());
      buf.writeByte(this.weaponAndToolItemsPreferredPickupLocation.getValue());
      buf.writeByte(this.usableItemsItemsPreferredPickupLocation.getValue());
      buf.writeByte(this.solidBlockItemsPreferredPickupLocation.getValue());
      buf.writeByte(this.miscItemsPreferredPickupLocation.getValue());
      buf.writeByte(this.allowNPCDetection ? 1 : 0);
      buf.writeByte(this.respondToHit ? 1 : 0);
   }

   @Override
   public int computeSize() {
      return 8;
   }

   public static ValidationResult validateStructure(@Nonnull ByteBuf buffer, int offset) {
      return buffer.readableBytes() - offset < 8 ? ValidationResult.error("Buffer too small: expected at least 8 bytes") : ValidationResult.OK;
   }

   public SyncPlayerPreferences clone() {
      SyncPlayerPreferences copy = new SyncPlayerPreferences();
      copy.showEntityMarkers = this.showEntityMarkers;
      copy.armorItemsPreferredPickupLocation = this.armorItemsPreferredPickupLocation;
      copy.weaponAndToolItemsPreferredPickupLocation = this.weaponAndToolItemsPreferredPickupLocation;
      copy.usableItemsItemsPreferredPickupLocation = this.usableItemsItemsPreferredPickupLocation;
      copy.solidBlockItemsPreferredPickupLocation = this.solidBlockItemsPreferredPickupLocation;
      copy.miscItemsPreferredPickupLocation = this.miscItemsPreferredPickupLocation;
      copy.allowNPCDetection = this.allowNPCDetection;
      copy.respondToHit = this.respondToHit;
      return copy;
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj) {
         return true;
      } else {
         return !(obj instanceof SyncPlayerPreferences other)
            ? false
            : this.showEntityMarkers == other.showEntityMarkers
               && Objects.equals(this.armorItemsPreferredPickupLocation, other.armorItemsPreferredPickupLocation)
               && Objects.equals(this.weaponAndToolItemsPreferredPickupLocation, other.weaponAndToolItemsPreferredPickupLocation)
               && Objects.equals(this.usableItemsItemsPreferredPickupLocation, other.usableItemsItemsPreferredPickupLocation)
               && Objects.equals(this.solidBlockItemsPreferredPickupLocation, other.solidBlockItemsPreferredPickupLocation)
               && Objects.equals(this.miscItemsPreferredPickupLocation, other.miscItemsPreferredPickupLocation)
               && this.allowNPCDetection == other.allowNPCDetection
               && this.respondToHit == other.respondToHit;
      }
   }

   @Override
   public int hashCode() {
      return Objects.hash(
         this.showEntityMarkers,
         this.armorItemsPreferredPickupLocation,
         this.weaponAndToolItemsPreferredPickupLocation,
         this.usableItemsItemsPreferredPickupLocation,
         this.solidBlockItemsPreferredPickupLocation,
         this.miscItemsPreferredPickupLocation,
         this.allowNPCDetection,
         this.respondToHit
      );
   }
}
