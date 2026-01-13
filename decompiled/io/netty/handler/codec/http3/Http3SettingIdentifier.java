package io.netty.handler.codec.http3;

import java.util.Arrays;
import java.util.Collections;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.jetbrains.annotations.Nullable;

public enum Http3SettingIdentifier {
   HTTP3_SETTINGS_QPACK_MAX_TABLE_CAPACITY(1L),
   HTTP3_SETTINGS_MAX_FIELD_SECTION_SIZE(6L),
   HTTP3_SETTINGS_QPACK_BLOCKED_STREAMS(7L),
   HTTP3_SETTINGS_ENABLE_CONNECT_PROTOCOL(8L),
   HTTP3_SETTINGS_H3_DATAGRAM(51L);

   private final long id;
   private static final Map<Long, Http3SettingIdentifier> LOOKUP = Collections.unmodifiableMap(
      Arrays.stream(values()).collect(Collectors.toMap(Http3SettingIdentifier::id, Function.identity()))
   );

   private Http3SettingIdentifier(long id) {
      this.id = id;
   }

   public long id() {
      return this.id;
   }

   @Nullable
   public static Http3SettingIdentifier fromId(long id) {
      return LOOKUP.get(id);
   }
}
