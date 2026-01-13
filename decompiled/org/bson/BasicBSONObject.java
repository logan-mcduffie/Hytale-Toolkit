package org.bson;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;
import java.util.Map.Entry;
import org.bson.types.BasicBSONList;
import org.bson.types.ObjectId;

public class BasicBSONObject extends LinkedHashMap<String, Object> implements BSONObject {
   private static final long serialVersionUID = -4415279469780082174L;

   public BasicBSONObject() {
   }

   public BasicBSONObject(int size) {
      super(size);
   }

   public BasicBSONObject(String key, Object value) {
      this.put(key, value);
   }

   public BasicBSONObject(Map map) {
      super(map);
   }

   @Override
   public Map toMap() {
      return new LinkedHashMap<>(this);
   }

   @Override
   public Object removeField(String key) {
      return this.remove(key);
   }

   @Override
   public boolean containsField(String field) {
      return super.containsKey(field);
   }

   @Override
   public Object get(String key) {
      return super.get(key);
   }

   public int getInt(String key) {
      Object o = this.get(key);
      if (o == null) {
         throw new NullPointerException("no value for: " + key);
      } else {
         return this.toInt(o);
      }
   }

   public int getInt(String key, int def) {
      Object foo = this.get(key);
      return foo == null ? def : this.toInt(foo);
   }

   public long getLong(String key) {
      Object foo = this.get(key);
      return ((Number)foo).longValue();
   }

   public long getLong(String key, long def) {
      Object foo = this.get(key);
      return foo == null ? def : ((Number)foo).longValue();
   }

   public double getDouble(String key) {
      Object foo = this.get(key);
      return ((Number)foo).doubleValue();
   }

   public double getDouble(String key, double def) {
      Object foo = this.get(key);
      return foo == null ? def : ((Number)foo).doubleValue();
   }

   public String getString(String key) {
      Object foo = this.get(key);
      return foo == null ? null : foo.toString();
   }

   public String getString(String key, String def) {
      Object foo = this.get(key);
      return foo == null ? def : foo.toString();
   }

   public boolean getBoolean(String key) {
      return this.getBoolean(key, false);
   }

   public boolean getBoolean(String key, boolean def) {
      Object foo = this.get(key);
      if (foo == null) {
         return def;
      } else if (foo instanceof Number) {
         return ((Number)foo).intValue() > 0;
      } else if (foo instanceof Boolean) {
         return (Boolean)foo;
      } else {
         throw new IllegalArgumentException("can't coerce to bool:" + foo.getClass());
      }
   }

   public ObjectId getObjectId(String field) {
      return (ObjectId)this.get(field);
   }

   public ObjectId getObjectId(String field, ObjectId def) {
      Object foo = this.get(field);
      return foo != null ? (ObjectId)foo : def;
   }

   public Date getDate(String field) {
      return (Date)this.get(field);
   }

   public Date getDate(String field, Date def) {
      Object foo = this.get(field);
      return foo != null ? (Date)foo : def;
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

   public BasicBSONObject append(String key, Object val) {
      this.put(key, val);
      return this;
   }

   @Override
   public boolean equals(Object o) {
      if (o == this) {
         return true;
      } else if (!(o instanceof BSONObject)) {
         return false;
      } else {
         BSONObject other = (BSONObject)o;
         return !this.keySet().equals(other.keySet())
            ? false
            : Arrays.equals(this.getEncoder().encode(canonicalizeBSONObject(this)), this.getEncoder().encode(canonicalizeBSONObject(other)));
      }
   }

   @Override
   public int hashCode() {
      return Arrays.hashCode(canonicalizeBSONObject(this).encode());
   }

   private byte[] encode() {
      return this.getEncoder().encode(this);
   }

   private BSONEncoder getEncoder() {
      return new BasicBSONEncoder();
   }

   private static Object canonicalize(Object from) {
      if (from instanceof BSONObject && !(from instanceof BasicBSONList)) {
         return canonicalizeBSONObject((BSONObject)from);
      } else if (from instanceof List) {
         return canonicalizeList((List<Object>)from);
      } else {
         return from instanceof Map ? canonicalizeMap((Map<String, Object>)from) : from;
      }
   }

   private static Map<String, Object> canonicalizeMap(Map<String, Object> from) {
      Map<String, Object> canonicalized = new LinkedHashMap<>(from.size());

      for (String key : new TreeSet<>(from.keySet())) {
         Object val = from.get(key);
         canonicalized.put(key, canonicalize(val));
      }

      return canonicalized;
   }

   private static BasicBSONObject canonicalizeBSONObject(BSONObject from) {
      BasicBSONObject canonicalized = new BasicBSONObject();

      for (String key : new TreeSet<>(from.keySet())) {
         Object val = from.get(key);
         canonicalized.put(key, canonicalize(val));
      }

      return canonicalized;
   }

   private static List canonicalizeList(List<Object> list) {
      List<Object> canonicalized = new ArrayList<>(list.size());

      for (Object cur : list) {
         canonicalized.add(canonicalize(cur));
      }

      return canonicalized;
   }

   private int toInt(Object o) {
      if (o instanceof Number) {
         return ((Number)o).intValue();
      } else if (o instanceof Boolean) {
         return (Boolean)o ? 1 : 0;
      } else {
         throw new IllegalArgumentException("can't convert: " + o.getClass().getName() + " to int");
      }
   }
}
