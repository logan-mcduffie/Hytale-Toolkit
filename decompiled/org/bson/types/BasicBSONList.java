package org.bson.types;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import org.bson.BSONObject;

public class BasicBSONList extends ArrayList<Object> implements BSONObject {
   private static final long serialVersionUID = -4415279469780082174L;

   @Override
   public Object put(String key, Object v) {
      return this.put(this._getInt(key), v);
   }

   public Object put(int key, Object value) {
      while (key >= this.size()) {
         this.add(null);
      }

      this.set(key, value);
      return value;
   }

   @Override
   public void putAll(Map m) {
      for (Entry entry : m.entrySet()) {
         this.put(entry.getKey().toString(), entry.getValue());
      }
   }

   @Override
   public void putAll(BSONObject o) {
      for (String k : o.keySet()) {
         this.put(k, o.get(k));
      }
   }

   @Override
   public Object get(String key) {
      int i = this._getInt(key);
      if (i < 0) {
         return null;
      } else {
         return i >= this.size() ? null : this.get(i);
      }
   }

   @Override
   public Object removeField(String key) {
      int i = this._getInt(key);
      if (i < 0) {
         return null;
      } else {
         return i >= this.size() ? null : this.remove(i);
      }
   }

   @Override
   public boolean containsField(String key) {
      int i = this._getInt(key, false);
      return i < 0 ? false : i >= 0 && i < this.size();
   }

   @Override
   public Set<String> keySet() {
      return new StringRangeSet(this.size());
   }

   @Override
   public Map toMap() {
      Map m = new HashMap();

      for (Object s : this.keySet()) {
         m.put(s, this.get(String.valueOf(s)));
      }

      return m;
   }

   int _getInt(String s) {
      return this._getInt(s, true);
   }

   int _getInt(String s, boolean err) {
      try {
         return Integer.parseInt(s);
      } catch (Exception var4) {
         if (err) {
            throw new IllegalArgumentException("BasicBSONList can only work with numeric keys, not: [" + s + "]");
         } else {
            return -1;
         }
      }
   }
}
