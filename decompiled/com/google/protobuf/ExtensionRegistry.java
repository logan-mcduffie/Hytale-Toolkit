package com.google.protobuf;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class ExtensionRegistry extends ExtensionRegistryLite {
   private final Map<String, ExtensionRegistry.ExtensionInfo> immutableExtensionsByName;
   private final Map<String, ExtensionRegistry.ExtensionInfo> mutableExtensionsByName;
   private final Map<ExtensionRegistry.DescriptorIntPair, ExtensionRegistry.ExtensionInfo> immutableExtensionsByNumber;
   private final Map<ExtensionRegistry.DescriptorIntPair, ExtensionRegistry.ExtensionInfo> mutableExtensionsByNumber;
   static final ExtensionRegistry EMPTY_REGISTRY = new ExtensionRegistry(true);

   public static ExtensionRegistry newInstance() {
      return new ExtensionRegistry();
   }

   public static ExtensionRegistry getEmptyRegistry() {
      return EMPTY_REGISTRY;
   }

   public ExtensionRegistry getUnmodifiable() {
      return new ExtensionRegistry(this);
   }

   @Deprecated
   public ExtensionRegistry.ExtensionInfo findExtensionByName(final String fullName) {
      return this.findImmutableExtensionByName(fullName);
   }

   public ExtensionRegistry.ExtensionInfo findImmutableExtensionByName(final String fullName) {
      return this.immutableExtensionsByName.get(fullName);
   }

   @Deprecated
   public ExtensionRegistry.ExtensionInfo findMutableExtensionByName(final String fullName) {
      return this.mutableExtensionsByName.get(fullName);
   }

   @Deprecated
   public ExtensionRegistry.ExtensionInfo findExtensionByNumber(final Descriptors.Descriptor containingType, final int fieldNumber) {
      return this.findImmutableExtensionByNumber(containingType, fieldNumber);
   }

   public ExtensionRegistry.ExtensionInfo findImmutableExtensionByNumber(final Descriptors.Descriptor containingType, final int fieldNumber) {
      return this.immutableExtensionsByNumber.get(new ExtensionRegistry.DescriptorIntPair(containingType, fieldNumber));
   }

   @Deprecated
   public ExtensionRegistry.ExtensionInfo findMutableExtensionByNumber(final Descriptors.Descriptor containingType, final int fieldNumber) {
      return this.mutableExtensionsByNumber.get(new ExtensionRegistry.DescriptorIntPair(containingType, fieldNumber));
   }

   @Deprecated
   public Set<ExtensionRegistry.ExtensionInfo> getAllMutableExtensionsByExtendedType(final String fullName) {
      HashSet<ExtensionRegistry.ExtensionInfo> extensions = new HashSet<>();

      for (ExtensionRegistry.DescriptorIntPair pair : this.mutableExtensionsByNumber.keySet()) {
         if (pair.descriptor.getFullName().equals(fullName)) {
            extensions.add(this.mutableExtensionsByNumber.get(pair));
         }
      }

      return extensions;
   }

   public Set<ExtensionRegistry.ExtensionInfo> getAllImmutableExtensionsByExtendedType(final String fullName) {
      HashSet<ExtensionRegistry.ExtensionInfo> extensions = new HashSet<>();

      for (ExtensionRegistry.DescriptorIntPair pair : this.immutableExtensionsByNumber.keySet()) {
         if (pair.descriptor.getFullName().equals(fullName)) {
            extensions.add(this.immutableExtensionsByNumber.get(pair));
         }
      }

      return extensions;
   }

   public void add(final Extension<?, ?> extension) {
      if (extension.getExtensionType() == Extension.ExtensionType.IMMUTABLE || extension.getExtensionType() == Extension.ExtensionType.MUTABLE) {
         this.add(newExtensionInfo(extension), extension.getExtensionType());
      }
   }

   public void add(final GeneratedMessage.GeneratedExtension<?, ?> extension) {
      this.add(extension);
   }

   static ExtensionRegistry.ExtensionInfo newExtensionInfo(final Extension<?, ?> extension) {
      if (extension.getDescriptor().getJavaType() == Descriptors.FieldDescriptor.JavaType.MESSAGE) {
         if (extension.getMessageDefaultInstance() == null) {
            throw new IllegalStateException("Registered message-type extension had null default instance: " + extension.getDescriptor().getFullName());
         } else {
            return new ExtensionRegistry.ExtensionInfo(extension.getDescriptor(), extension.getMessageDefaultInstance());
         }
      } else {
         return new ExtensionRegistry.ExtensionInfo(extension.getDescriptor(), null);
      }
   }

   public void add(final Descriptors.FieldDescriptor type) {
      if (type.getJavaType() == Descriptors.FieldDescriptor.JavaType.MESSAGE) {
         throw new IllegalArgumentException("ExtensionRegistry.add() must be provided a default instance when adding an embedded message extension.");
      } else {
         ExtensionRegistry.ExtensionInfo info = new ExtensionRegistry.ExtensionInfo(type, null);
         this.add(info, Extension.ExtensionType.IMMUTABLE);
         this.add(info, Extension.ExtensionType.MUTABLE);
      }
   }

   public void add(final Descriptors.FieldDescriptor type, final Message defaultInstance) {
      if (type.getJavaType() != Descriptors.FieldDescriptor.JavaType.MESSAGE) {
         throw new IllegalArgumentException("ExtensionRegistry.add() provided a default instance for a non-message extension.");
      } else {
         this.add(new ExtensionRegistry.ExtensionInfo(type, defaultInstance), Extension.ExtensionType.IMMUTABLE);
      }
   }

   private ExtensionRegistry() {
      this.immutableExtensionsByName = new HashMap<>();
      this.mutableExtensionsByName = new HashMap<>();
      this.immutableExtensionsByNumber = new HashMap<>();
      this.mutableExtensionsByNumber = new HashMap<>();
   }

   private ExtensionRegistry(ExtensionRegistry other) {
      super(other);
      this.immutableExtensionsByName = Collections.unmodifiableMap(other.immutableExtensionsByName);
      this.mutableExtensionsByName = Collections.unmodifiableMap(other.mutableExtensionsByName);
      this.immutableExtensionsByNumber = Collections.unmodifiableMap(other.immutableExtensionsByNumber);
      this.mutableExtensionsByNumber = Collections.unmodifiableMap(other.mutableExtensionsByNumber);
   }

   ExtensionRegistry(boolean empty) {
      super(EMPTY_REGISTRY_LITE);
      this.immutableExtensionsByName = Collections.emptyMap();
      this.mutableExtensionsByName = Collections.emptyMap();
      this.immutableExtensionsByNumber = Collections.emptyMap();
      this.mutableExtensionsByNumber = Collections.emptyMap();
   }

   private void add(final ExtensionRegistry.ExtensionInfo extension, final Extension.ExtensionType extensionType) {
      if (!extension.descriptor.isExtension()) {
         throw new IllegalArgumentException("ExtensionRegistry.add() was given a FieldDescriptor for a regular (non-extension) field.");
      } else {
         Map<String, ExtensionRegistry.ExtensionInfo> extensionsByName;
         Map<ExtensionRegistry.DescriptorIntPair, ExtensionRegistry.ExtensionInfo> extensionsByNumber;
         switch (extensionType) {
            case IMMUTABLE:
               extensionsByName = this.immutableExtensionsByName;
               extensionsByNumber = this.immutableExtensionsByNumber;
               break;
            case MUTABLE:
               extensionsByName = this.mutableExtensionsByName;
               extensionsByNumber = this.mutableExtensionsByNumber;
               break;
            default:
               return;
         }

         extensionsByName.put(extension.descriptor.getFullName(), extension);
         extensionsByNumber.put(new ExtensionRegistry.DescriptorIntPair(extension.descriptor.getContainingType(), extension.descriptor.getNumber()), extension);
         Descriptors.FieldDescriptor field = extension.descriptor;
         if (field.getContainingType().getOptions().getMessageSetWireFormat()
            && field.getType() == Descriptors.FieldDescriptor.Type.MESSAGE
            && field.isOptional()
            && field.getExtensionScope() == field.getMessageType()) {
            extensionsByName.put(field.getMessageType().getFullName(), extension);
         }
      }
   }

   private static final class DescriptorIntPair {
      private final Descriptors.Descriptor descriptor;
      private final int number;

      DescriptorIntPair(final Descriptors.Descriptor descriptor, final int number) {
         this.descriptor = descriptor;
         this.number = number;
      }

      @Override
      public int hashCode() {
         return this.descriptor.hashCode() * 65535 + this.number;
      }

      @Override
      public boolean equals(final Object obj) {
         if (!(obj instanceof ExtensionRegistry.DescriptorIntPair)) {
            return false;
         } else {
            ExtensionRegistry.DescriptorIntPair other = (ExtensionRegistry.DescriptorIntPair)obj;
            return this.descriptor == other.descriptor && this.number == other.number;
         }
      }
   }

   public static final class ExtensionInfo {
      public final Descriptors.FieldDescriptor descriptor;
      public final Message defaultInstance;

      private ExtensionInfo(final Descriptors.FieldDescriptor descriptor) {
         this.descriptor = descriptor;
         this.defaultInstance = null;
      }

      private ExtensionInfo(final Descriptors.FieldDescriptor descriptor, final Message defaultInstance) {
         this.descriptor = descriptor;
         this.defaultInstance = defaultInstance;
      }
   }
}
