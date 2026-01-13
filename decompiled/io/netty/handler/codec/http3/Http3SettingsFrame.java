package io.netty.handler.codec.http3;

import java.util.Map.Entry;
import org.jetbrains.annotations.Nullable;

public interface Http3SettingsFrame extends Http3ControlStreamFrame, Iterable<Entry<Long, Long>> {
   @Deprecated
   long HTTP3_SETTINGS_QPACK_MAX_TABLE_CAPACITY = Http3SettingIdentifier.HTTP3_SETTINGS_QPACK_MAX_TABLE_CAPACITY.id();
   @Deprecated
   long HTTP3_SETTINGS_QPACK_BLOCKED_STREAMS = Http3SettingIdentifier.HTTP3_SETTINGS_QPACK_BLOCKED_STREAMS.id();
   @Deprecated
   long HTTP3_SETTINGS_ENABLE_CONNECT_PROTOCOL = Http3SettingIdentifier.HTTP3_SETTINGS_ENABLE_CONNECT_PROTOCOL.id();
   @Deprecated
   long HTTP3_SETTINGS_MAX_FIELD_SECTION_SIZE = Http3SettingIdentifier.HTTP3_SETTINGS_MAX_FIELD_SECTION_SIZE.id();

   default Http3Settings settings() {
      throw new UnsupportedOperationException("Http3SettingsFrame.settings() not implemented in this version");
   }

   @Override
   default long type() {
      return 4L;
   }

   @Deprecated
   @Nullable
   default Long get(long key) {
      return this.settings().get(key);
   }

   @Deprecated
   default Long getOrDefault(long key, long defaultValue) {
      Long val = this.get(key);
      return val == null ? defaultValue : val;
   }

   /** @deprecated */
   @Nullable
   default Long put(long key, Long value) {
      return this.settings().put(key, value);
   }
}
