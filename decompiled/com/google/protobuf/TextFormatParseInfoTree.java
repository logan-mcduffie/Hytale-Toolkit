package com.google.protobuf;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class TextFormatParseInfoTree {
   private Map<Descriptors.FieldDescriptor, List<TextFormatParseLocation>> locationsFromField;
   Map<Descriptors.FieldDescriptor, List<TextFormatParseInfoTree>> subtreesFromField;

   private TextFormatParseInfoTree(
      Map<Descriptors.FieldDescriptor, List<TextFormatParseLocation>> locationsFromField,
      Map<Descriptors.FieldDescriptor, List<TextFormatParseInfoTree.Builder>> subtreeBuildersFromField
   ) {
      Map<Descriptors.FieldDescriptor, List<TextFormatParseLocation>> locs = new HashMap<>();

      for (Entry<Descriptors.FieldDescriptor, List<TextFormatParseLocation>> kv : locationsFromField.entrySet()) {
         locs.put(kv.getKey(), Collections.unmodifiableList(kv.getValue()));
      }

      this.locationsFromField = Collections.unmodifiableMap(locs);
      Map<Descriptors.FieldDescriptor, List<TextFormatParseInfoTree>> subs = new HashMap<>();

      for (Entry<Descriptors.FieldDescriptor, List<TextFormatParseInfoTree.Builder>> kv : subtreeBuildersFromField.entrySet()) {
         List<TextFormatParseInfoTree> submessagesOfField = new ArrayList<>();

         for (TextFormatParseInfoTree.Builder subBuilder : kv.getValue()) {
            submessagesOfField.add(subBuilder.build());
         }

         subs.put(kv.getKey(), Collections.unmodifiableList(submessagesOfField));
      }

      this.subtreesFromField = Collections.unmodifiableMap(subs);
   }

   public List<TextFormatParseLocation> getLocations(final Descriptors.FieldDescriptor fieldDescriptor) {
      List<TextFormatParseLocation> result = this.locationsFromField.get(fieldDescriptor);
      return result == null ? Collections.emptyList() : result;
   }

   public TextFormatParseLocation getLocation(final Descriptors.FieldDescriptor fieldDescriptor, int index) {
      return getFromList(this.getLocations(fieldDescriptor), index, fieldDescriptor);
   }

   public List<TextFormatParseInfoTree> getNestedTrees(final Descriptors.FieldDescriptor fieldDescriptor) {
      List<TextFormatParseInfoTree> result = this.subtreesFromField.get(fieldDescriptor);
      return result == null ? Collections.emptyList() : result;
   }

   public TextFormatParseInfoTree getNestedTree(final Descriptors.FieldDescriptor fieldDescriptor, int index) {
      return getFromList(this.getNestedTrees(fieldDescriptor), index, fieldDescriptor);
   }

   public static TextFormatParseInfoTree.Builder builder() {
      return new TextFormatParseInfoTree.Builder();
   }

   private static <T> T getFromList(List<T> list, int index, Descriptors.FieldDescriptor fieldDescriptor) {
      if (index < list.size() && index >= 0) {
         return list.get(index);
      } else {
         throw new IllegalArgumentException(
            String.format("Illegal index field: %s, index %d", fieldDescriptor == null ? "<null>" : fieldDescriptor.getName(), index)
         );
      }
   }

   public static class Builder {
      private Map<Descriptors.FieldDescriptor, List<TextFormatParseLocation>> locationsFromField = new HashMap<>();
      private Map<Descriptors.FieldDescriptor, List<TextFormatParseInfoTree.Builder>> subtreeBuildersFromField = new HashMap<>();

      private Builder() {
      }

      public TextFormatParseInfoTree.Builder setLocation(final Descriptors.FieldDescriptor fieldDescriptor, TextFormatParseLocation location) {
         List<TextFormatParseLocation> fieldLocations = this.locationsFromField.get(fieldDescriptor);
         if (fieldLocations == null) {
            fieldLocations = new ArrayList<>();
            this.locationsFromField.put(fieldDescriptor, fieldLocations);
         }

         fieldLocations.add(location);
         return this;
      }

      public TextFormatParseInfoTree.Builder getBuilderForSubMessageField(final Descriptors.FieldDescriptor fieldDescriptor) {
         List<TextFormatParseInfoTree.Builder> submessageBuilders = this.subtreeBuildersFromField.get(fieldDescriptor);
         if (submessageBuilders == null) {
            submessageBuilders = new ArrayList<>();
            this.subtreeBuildersFromField.put(fieldDescriptor, submessageBuilders);
         }

         TextFormatParseInfoTree.Builder subtreeBuilder = new TextFormatParseInfoTree.Builder();
         submessageBuilders.add(subtreeBuilder);
         return subtreeBuilder;
      }

      public TextFormatParseInfoTree build() {
         return new TextFormatParseInfoTree(this.locationsFromField, this.subtreeBuildersFromField);
      }
   }
}
