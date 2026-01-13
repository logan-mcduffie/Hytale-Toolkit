package com.google.protobuf;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class MapFieldBuilder<KeyT, MessageOrBuilderT extends MessageOrBuilder, MessageT extends MessageOrBuilderT, BuilderT extends MessageOrBuilderT>
   extends MapFieldReflectionAccessor {
   Map<KeyT, MessageOrBuilderT> builderMap = new LinkedHashMap<>();
   Map<KeyT, MessageT> messageMap = null;
   List<Message> messageList = null;
   MapFieldBuilder.Converter<KeyT, MessageOrBuilderT, MessageT> converter;

   public MapFieldBuilder(MapFieldBuilder.Converter<KeyT, MessageOrBuilderT, MessageT> converter) {
      this.converter = converter;
   }

   private List<MapEntry<KeyT, MessageT>> getMapEntryList() {
      ArrayList<MapEntry<KeyT, MessageT>> list = new ArrayList<>(this.messageList.size());
      Class<?> valueClass = ((MessageOrBuilder)this.converter.defaultEntry().getValue()).getClass();

      for (Message entry : this.messageList) {
         MapEntry<KeyT, ?> typedEntry = (MapEntry<KeyT, ?>)entry;
         if (valueClass.isInstance(typedEntry.getValue())) {
            list.add((MapEntry<KeyT, MessageT>)typedEntry);
         } else {
            list.add(this.converter.defaultEntry().toBuilder().mergeFrom(entry).build());
         }
      }

      return list;
   }

   public Map<KeyT, MessageOrBuilderT> ensureBuilderMap() {
      if (this.builderMap != null) {
         return this.builderMap;
      } else if (this.messageMap != null) {
         this.builderMap = new LinkedHashMap<>(this.messageMap.size());

         for (Entry<KeyT, MessageT> entry : this.messageMap.entrySet()) {
            this.builderMap.put(entry.getKey(), (MessageOrBuilderT)((MessageOrBuilder)entry.getValue()));
         }

         this.messageMap = null;
         return this.builderMap;
      } else {
         this.builderMap = new LinkedHashMap<>(this.messageList.size());

         for (MapEntry<KeyT, MessageT> entry : this.getMapEntryList()) {
            this.builderMap.put(entry.getKey(), (MessageOrBuilderT)((MessageOrBuilder)entry.getValue()));
         }

         this.messageList = null;
         return this.builderMap;
      }
   }

   public List<Message> ensureMessageList() {
      if (this.messageList != null) {
         return this.messageList;
      } else if (this.builderMap != null) {
         this.messageList = new ArrayList<>(this.builderMap.size());

         for (Entry<KeyT, MessageOrBuilderT> entry : this.builderMap.entrySet()) {
            this.messageList.add(this.converter.defaultEntry().toBuilder().setKey(entry.getKey()).setValue(this.converter.build(entry.getValue())).build());
         }

         this.builderMap = null;
         return this.messageList;
      } else {
         this.messageList = new ArrayList<>(this.messageMap.size());

         for (Entry<KeyT, MessageT> entry : this.messageMap.entrySet()) {
            this.messageList
               .add(this.converter.defaultEntry().toBuilder().setKey(entry.getKey()).setValue((MessageT)((MessageOrBuilder)entry.getValue())).build());
         }

         this.messageMap = null;
         return this.messageList;
      }
   }

   public Map<KeyT, MessageT> ensureMessageMap() {
      this.messageMap = this.populateMutableMap();
      this.builderMap = null;
      this.messageList = null;
      return this.messageMap;
   }

   public Map<KeyT, MessageT> getImmutableMap() {
      return new MapField.MutabilityAwareMap<>(MutabilityOracle.IMMUTABLE, this.populateMutableMap());
   }

   private Map<KeyT, MessageT> populateMutableMap() {
      if (this.messageMap != null) {
         return this.messageMap;
      } else if (this.builderMap != null) {
         Map<KeyT, MessageT> toReturn = new LinkedHashMap<>(this.builderMap.size());

         for (Entry<KeyT, MessageOrBuilderT> entry : this.builderMap.entrySet()) {
            toReturn.put(entry.getKey(), this.converter.build(entry.getValue()));
         }

         return toReturn;
      } else {
         Map<KeyT, MessageT> toReturn = new LinkedHashMap<>(this.messageList.size());

         for (MapEntry<KeyT, MessageT> entry : this.getMapEntryList()) {
            toReturn.put(entry.getKey(), (MessageT)((MessageOrBuilder)entry.getValue()));
         }

         return toReturn;
      }
   }

   public void mergeFrom(MapField<KeyT, MessageT> other) {
      this.ensureBuilderMap().putAll(MapFieldLite.copy(other.getMap()));
   }

   public void clear() {
      this.builderMap = new LinkedHashMap<>();
      this.messageMap = null;
      this.messageList = null;
   }

   private boolean typedEquals(MapFieldBuilder<KeyT, MessageOrBuilderT, MessageT, BuilderT> other) {
      return MapFieldLite.equals(this.ensureBuilderMap(), other.ensureBuilderMap());
   }

   @Override
   public boolean equals(Object object) {
      return !(object instanceof MapFieldBuilder) ? false : this.typedEquals((MapFieldBuilder<KeyT, MessageOrBuilderT, MessageT, BuilderT>)object);
   }

   @Override
   public int hashCode() {
      return MapFieldLite.calculateHashCodeForMap(this.ensureBuilderMap());
   }

   public MapFieldBuilder<KeyT, MessageOrBuilderT, MessageT, BuilderT> copy() {
      MapFieldBuilder<KeyT, MessageOrBuilderT, MessageT, BuilderT> clone = new MapFieldBuilder<>(this.converter);
      clone.ensureBuilderMap().putAll(this.ensureBuilderMap());
      return clone;
   }

   public MapField<KeyT, MessageT> build(MapEntry<KeyT, MessageT> defaultEntry) {
      MapField<KeyT, MessageT> mapField = MapField.newMapField(defaultEntry);
      Map<KeyT, MessageT> map = mapField.getMutableMap();

      for (Entry<KeyT, MessageOrBuilderT> entry : this.ensureBuilderMap().entrySet()) {
         map.put(entry.getKey(), this.converter.build(entry.getValue()));
      }

      mapField.makeImmutable();
      return mapField;
   }

   @Override
   List<Message> getList() {
      return this.ensureMessageList();
   }

   @Override
   List<Message> getMutableList() {
      return this.ensureMessageList();
   }

   @Override
   Message getMapEntryMessageDefaultInstance() {
      return this.converter.defaultEntry();
   }

   public interface Converter<KeyT, MessageOrBuilderT extends MessageOrBuilder, MessageT extends MessageOrBuilderT> {
      MessageT build(MessageOrBuilderT val);

      MapEntry<KeyT, MessageT> defaultEntry();
   }
}
