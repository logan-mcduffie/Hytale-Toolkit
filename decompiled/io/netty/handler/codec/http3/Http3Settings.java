package io.netty.handler.codec.http3;

import io.netty.util.collection.LongObjectHashMap;
import io.netty.util.collection.LongObjectMap;
import io.netty.util.internal.ObjectUtil;
import java.util.Iterator;
import java.util.AbstractMap.SimpleImmutableEntry;
import java.util.Map.Entry;
import javax.annotation.Nullable;

public final class Http3Settings implements Iterable<Entry<Long, Long>> {
   private final LongObjectMap<Long> settings;
   private static final Long TRUE = 1L;
   private static final Long FALSE = 0L;

   public Http3Settings() {
      this.settings = new LongObjectHashMap<>(Http3SettingIdentifier.values().length);
   }

   Http3Settings(int initialCapacity) {
      this.settings = new LongObjectHashMap<>(initialCapacity);
   }

   Http3Settings(int initialCapacity, float loadFactor) {
      this.settings = new LongObjectHashMap<>(initialCapacity, loadFactor);
   }

   @Nullable
   public Long put(long key, Long value) {
      if (Http3CodecUtils.isReservedHttp2Setting(key)) {
         throw new IllegalArgumentException("Setting is reserved for HTTP/2: " + key);
      } else {
         Http3SettingIdentifier identifier = Http3SettingIdentifier.fromId(key);
         if (identifier == null) {
            return null;
         } else {
            verifyStandardSetting(identifier, value);
            return this.settings.put(key, value);
         }
      }
   }

   @Nullable
   public Long get(long key) {
      return this.settings.get(key);
   }

   @Nullable
   public Long qpackMaxTableCapacity() {
      return this.get(Http3SettingIdentifier.HTTP3_SETTINGS_QPACK_MAX_TABLE_CAPACITY.id());
   }

   public Http3Settings qpackMaxTableCapacity(long value) {
      this.put(Http3SettingIdentifier.HTTP3_SETTINGS_QPACK_MAX_TABLE_CAPACITY.id(), value);
      return this;
   }

   @Nullable
   public Long maxFieldSectionSize() {
      return this.get(Http3SettingIdentifier.HTTP3_SETTINGS_MAX_FIELD_SECTION_SIZE.id());
   }

   public Http3Settings maxFieldSectionSize(long value) {
      this.put(Http3SettingIdentifier.HTTP3_SETTINGS_MAX_FIELD_SECTION_SIZE.id(), value);
      return this;
   }

   @Nullable
   public Long qpackBlockedStreams() {
      return this.get(Http3SettingIdentifier.HTTP3_SETTINGS_QPACK_BLOCKED_STREAMS.id());
   }

   public Http3Settings qpackBlockedStreams(long value) {
      this.put(Http3SettingIdentifier.HTTP3_SETTINGS_QPACK_BLOCKED_STREAMS.id(), value);
      return this;
   }

   @Nullable
   public Boolean connectProtocolEnabled() {
      Long value = this.get(Http3SettingIdentifier.HTTP3_SETTINGS_ENABLE_CONNECT_PROTOCOL.id());
      return value == null ? null : TRUE.equals(value);
   }

   public Http3Settings enableConnectProtocol(boolean enabled) {
      this.put(Http3SettingIdentifier.HTTP3_SETTINGS_ENABLE_CONNECT_PROTOCOL.id(), enabled ? TRUE : FALSE);
      return this;
   }

   @Nullable
   public Boolean h3DatagramEnabled() {
      Long value = this.get(Http3SettingIdentifier.HTTP3_SETTINGS_H3_DATAGRAM.id());
      return value == null ? null : TRUE.equals(value);
   }

   public Http3Settings enableH3Datagram(boolean enabled) {
      this.put(Http3SettingIdentifier.HTTP3_SETTINGS_H3_DATAGRAM.id(), enabled ? TRUE : FALSE);
      return this;
   }

   public Http3Settings putAll(Http3Settings http3Settings) {
      ObjectUtil.checkNotNull(http3Settings, "http3Settings");
      this.settings.putAll(http3Settings.settings);
      return this;
   }

   public static Http3Settings defaultSettings() {
      return new Http3Settings()
         .qpackMaxTableCapacity(0L)
         .qpackBlockedStreams(0L)
         .maxFieldSectionSize(Long.MAX_VALUE)
         .enableConnectProtocol(false)
         .enableH3Datagram(false);
   }

   @Override
   public Iterator<Entry<Long, Long>> iterator() {
      final Iterator<LongObjectMap.PrimitiveEntry<Long>> it = this.settings.entries().iterator();
      return new Iterator<Entry<Long, Long>>() {
         @Override
         public boolean hasNext() {
            return it.hasNext();
         }

         public Entry<Long, Long> next() {
            LongObjectMap.PrimitiveEntry<Long> entry = it.next();
            return new SimpleImmutableEntry<>(entry.key(), entry.value());
         }
      };
   }

   @Override
   public boolean equals(Object o) {
      if (this == o) {
         return true;
      } else if (!(o instanceof Http3Settings)) {
         return false;
      } else {
         Http3Settings that = (Http3Settings)o;
         return this.settings.equals(that.settings);
      }
   }

   @Override
   public int hashCode() {
      return this.settings.hashCode();
   }

   @Override
   public String toString() {
      StringBuilder sb = new StringBuilder("Http3Settings{");
      boolean first = true;

      for (LongObjectMap.PrimitiveEntry<Long> e : this.settings.entries()) {
         if (!first) {
            sb.append(", ");
         }

         first = false;
         sb.append("0x").append(Long.toHexString(e.key())).append('=').append(e.value());
      }

      return sb.append('}').toString();
   }

   private static void verifyStandardSetting(Http3SettingIdentifier identifier, Long value) {
      ObjectUtil.checkNotNull(value, "value");
      ObjectUtil.checkNotNull(identifier, "identifier");
      switch (identifier) {
         case HTTP3_SETTINGS_QPACK_MAX_TABLE_CAPACITY:
         case HTTP3_SETTINGS_QPACK_BLOCKED_STREAMS:
         case HTTP3_SETTINGS_MAX_FIELD_SECTION_SIZE:
            if (value < 0L) {
               throw new IllegalArgumentException("Setting 0x" + Long.toHexString(identifier.id()) + " invalid: " + value + " (must be >= 0)");
            }
            break;
         case HTTP3_SETTINGS_ENABLE_CONNECT_PROTOCOL:
         case HTTP3_SETTINGS_H3_DATAGRAM:
            if (value != 0L && value != 1L) {
               throw new IllegalArgumentException(
                  "Invalid: " + value + "for " + Http3SettingIdentifier.valueOf(String.valueOf(identifier)) + " (expected 0 or 1)"
               );
            }
            break;
         default:
            if (value < 0L) {
               throw new IllegalArgumentException("Setting 0x" + Long.toHexString(identifier.id()) + " invalid: " + value);
            }
      }
   }
}
