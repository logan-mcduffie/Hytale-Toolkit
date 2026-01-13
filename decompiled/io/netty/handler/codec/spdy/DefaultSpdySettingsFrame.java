package io.netty.handler.codec.spdy;

import io.netty.util.internal.StringUtil;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.Map.Entry;

public class DefaultSpdySettingsFrame implements SpdySettingsFrame {
   private boolean clear;
   private final Map<Integer, DefaultSpdySettingsFrame.Setting> settingsMap = new TreeMap<>();

   @Override
   public Set<Integer> ids() {
      return this.settingsMap.keySet();
   }

   @Override
   public boolean isSet(int id) {
      return this.settingsMap.containsKey(id);
   }

   @Override
   public int getValue(int id) {
      DefaultSpdySettingsFrame.Setting setting = this.settingsMap.get(id);
      return setting != null ? setting.getValue() : -1;
   }

   @Override
   public SpdySettingsFrame setValue(int id, int value) {
      return this.setValue(id, value, false, false);
   }

   @Override
   public SpdySettingsFrame setValue(int id, int value, boolean persistValue, boolean persisted) {
      if (id >= 0 && id <= 16777215) {
         Integer key = id;
         DefaultSpdySettingsFrame.Setting setting = this.settingsMap.get(key);
         if (setting != null) {
            setting.setValue(value);
            setting.setPersist(persistValue);
            setting.setPersisted(persisted);
         } else {
            this.settingsMap.put(key, new DefaultSpdySettingsFrame.Setting(value, persistValue, persisted));
         }

         return this;
      } else {
         throw new IllegalArgumentException("Setting ID is not valid: " + id);
      }
   }

   @Override
   public SpdySettingsFrame removeValue(int id) {
      this.settingsMap.remove(id);
      return this;
   }

   @Override
   public boolean isPersistValue(int id) {
      DefaultSpdySettingsFrame.Setting setting = this.settingsMap.get(id);
      return setting != null && setting.isPersist();
   }

   @Override
   public SpdySettingsFrame setPersistValue(int id, boolean persistValue) {
      DefaultSpdySettingsFrame.Setting setting = this.settingsMap.get(id);
      if (setting != null) {
         setting.setPersist(persistValue);
      }

      return this;
   }

   @Override
   public boolean isPersisted(int id) {
      DefaultSpdySettingsFrame.Setting setting = this.settingsMap.get(id);
      return setting != null && setting.isPersisted();
   }

   @Override
   public SpdySettingsFrame setPersisted(int id, boolean persisted) {
      DefaultSpdySettingsFrame.Setting setting = this.settingsMap.get(id);
      if (setting != null) {
         setting.setPersisted(persisted);
      }

      return this;
   }

   @Override
   public boolean clearPreviouslyPersistedSettings() {
      return this.clear;
   }

   @Override
   public SpdySettingsFrame setClearPreviouslyPersistedSettings(boolean clear) {
      this.clear = clear;
      return this;
   }

   private Set<Entry<Integer, DefaultSpdySettingsFrame.Setting>> getSettings() {
      return this.settingsMap.entrySet();
   }

   private void appendSettings(StringBuilder buf) {
      for (Entry<Integer, DefaultSpdySettingsFrame.Setting> e : this.getSettings()) {
         DefaultSpdySettingsFrame.Setting setting = e.getValue();
         buf.append("--> ");
         buf.append(e.getKey());
         buf.append(':');
         buf.append(setting.getValue());
         buf.append(" (persist value: ");
         buf.append(setting.isPersist());
         buf.append("; persisted: ");
         buf.append(setting.isPersisted());
         buf.append(')');
         buf.append(StringUtil.NEWLINE);
      }
   }

   @Override
   public String toString() {
      StringBuilder buf = new StringBuilder().append(StringUtil.simpleClassName(this)).append(StringUtil.NEWLINE);
      this.appendSettings(buf);
      buf.setLength(buf.length() - StringUtil.NEWLINE.length());
      return buf.toString();
   }

   private static final class Setting {
      private int value;
      private boolean persist;
      private boolean persisted;

      Setting(int value, boolean persist, boolean persisted) {
         this.value = value;
         this.persist = persist;
         this.persisted = persisted;
      }

      int getValue() {
         return this.value;
      }

      void setValue(int value) {
         this.value = value;
      }

      boolean isPersist() {
         return this.persist;
      }

      void setPersist(boolean persist) {
         this.persist = persist;
      }

      boolean isPersisted() {
         return this.persisted;
      }

      void setPersisted(boolean persisted) {
         this.persisted = persisted;
      }
   }
}
