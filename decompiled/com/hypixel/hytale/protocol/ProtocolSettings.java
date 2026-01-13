package com.hypixel.hytale.protocol;

public final class ProtocolSettings {
   public static final String PROTOCOL_HASH = "6708f121966c1c443f4b0eb525b2f81d0a8dc61f5003a692a8fa157e5e02cea9";
   public static final int PROTOCOL_VERSION = 1;
   public static final int PACKET_COUNT = 268;
   public static final int STRUCT_COUNT = 315;
   public static final int ENUM_COUNT = 136;
   public static final int MAX_PACKET_SIZE = 1677721600;

   private ProtocolSettings() {
   }

   public static boolean validateHash(String hash) {
      return "6708f121966c1c443f4b0eb525b2f81d0a8dc61f5003a692a8fa157e5e02cea9".equals(hash);
   }
}
