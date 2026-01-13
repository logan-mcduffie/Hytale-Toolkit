package io.netty.handler.codec.http3;

import io.netty.util.internal.ObjectUtil;
import io.netty.util.internal.StringUtil;
import java.util.Iterator;
import java.util.Map.Entry;
import org.jetbrains.annotations.Nullable;

public final class DefaultHttp3SettingsFrame implements Http3SettingsFrame {
   private final Http3Settings settings;

   public DefaultHttp3SettingsFrame(Http3Settings settings) {
      this.settings = ObjectUtil.checkNotNull(settings, "settings");
   }

   public DefaultHttp3SettingsFrame() {
      this.settings = new Http3Settings();
   }

   @Override
   public Http3Settings settings() {
      return this.settings;
   }

   @Deprecated
   @Nullable
   @Override
   public Long get(long key) {
      return this.settings.get(key);
   }

   @Deprecated
   @Nullable
   @Override
   public Long put(long key, Long value) {
      return this.settings.put(key, value);
   }

   @Override
   public Iterator<Entry<Long, Long>> iterator() {
      return this.settings.iterator();
   }

   @Override
   public int hashCode() {
      return this.settings.hashCode();
   }

   @Override
   public boolean equals(Object o) {
      if (this == o) {
         return true;
      } else if (o != null && this.getClass() == o.getClass()) {
         DefaultHttp3SettingsFrame that = (DefaultHttp3SettingsFrame)o;
         return that.settings.equals(this.settings);
      } else {
         return false;
      }
   }

   @Override
   public String toString() {
      return StringUtil.simpleClassName(this) + "(settings=" + this.settings + ')';
   }

   public static DefaultHttp3SettingsFrame copyOf(Http3SettingsFrame settingsFrame) {
      DefaultHttp3SettingsFrame copy = new DefaultHttp3SettingsFrame();
      if (settingsFrame instanceof DefaultHttp3SettingsFrame) {
         copy.settings.putAll(((DefaultHttp3SettingsFrame)settingsFrame).settings);
      } else {
         for (Entry<Long, Long> entry : settingsFrame) {
            copy.put(entry.getKey(), entry.getValue());
         }
      }

      return copy;
   }
}
