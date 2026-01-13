package com.google.protobuf;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class ExtensionRegistryLite {
   private static volatile boolean eagerlyParseMessageSets = false;
   static final String EXTENSION_CLASS_NAME = "com.google.protobuf.Extension";
   private static volatile ExtensionRegistryLite emptyRegistry;
   static final ExtensionRegistryLite EMPTY_REGISTRY_LITE = new ExtensionRegistryLite(true);
   private final Map<ExtensionRegistryLite.ObjectIntPair, GeneratedMessageLite.GeneratedExtension<?, ?>> extensionsByNumber;

   public static boolean isEagerlyParseMessageSets() {
      return eagerlyParseMessageSets;
   }

   public static void setEagerlyParseMessageSets(boolean isEagerlyParse) {
      eagerlyParseMessageSets = isEagerlyParse;
   }

   public static ExtensionRegistryLite newInstance() {
      return Android.assumeLiteRuntime ? new ExtensionRegistryLite() : ExtensionRegistryFactory.create();
   }

   public static ExtensionRegistryLite getEmptyRegistry() {
      if (Android.assumeLiteRuntime) {
         return EMPTY_REGISTRY_LITE;
      } else {
         ExtensionRegistryLite result = emptyRegistry;
         if (result == null) {
            synchronized (ExtensionRegistryLite.class) {
               result = emptyRegistry;
               if (result == null) {
                  emptyRegistry = result = ExtensionRegistryFactory.createEmpty();
               }
            }
         }

         return result;
      }
   }

   public ExtensionRegistryLite getUnmodifiable() {
      return new ExtensionRegistryLite(this);
   }

   public <ContainingType extends MessageLite> GeneratedMessageLite.GeneratedExtension<ContainingType, ?> findLiteExtensionByNumber(
      final ContainingType containingTypeDefaultInstance, final int fieldNumber
   ) {
      return (GeneratedMessageLite.GeneratedExtension<ContainingType, ?>)this.extensionsByNumber
         .get(new ExtensionRegistryLite.ObjectIntPair(containingTypeDefaultInstance, fieldNumber));
   }

   public final void add(final GeneratedMessageLite.GeneratedExtension<?, ?> extension) {
      this.extensionsByNumber.put(new ExtensionRegistryLite.ObjectIntPair(extension.getContainingTypeDefaultInstance(), extension.getNumber()), extension);
   }

   public final void add(ExtensionLite<?, ?> extension) {
      if (extension instanceof GeneratedMessageLite.GeneratedExtension) {
         this.add((GeneratedMessageLite.GeneratedExtension<?, ?>)extension);
      }

      if (!Android.assumeLiteRuntime && ExtensionRegistryFactory.isFullRegistry(this)) {
         try {
            this.getClass().getMethod("add", ExtensionRegistryLite.ExtensionClassHolder.INSTANCE).invoke(this, extension);
         } catch (Exception var3) {
            throw new IllegalArgumentException(String.format("Could not invoke ExtensionRegistry#add for %s", extension), var3);
         }
      }
   }

   ExtensionRegistryLite() {
      this.extensionsByNumber = new HashMap<>();
   }

   ExtensionRegistryLite(ExtensionRegistryLite other) {
      if (other == EMPTY_REGISTRY_LITE) {
         this.extensionsByNumber = Collections.emptyMap();
      } else {
         this.extensionsByNumber = Collections.unmodifiableMap(other.extensionsByNumber);
      }
   }

   ExtensionRegistryLite(boolean empty) {
      this.extensionsByNumber = Collections.emptyMap();
   }

   private static class ExtensionClassHolder {
      static final Class<?> INSTANCE = resolveExtensionClass();

      static Class<?> resolveExtensionClass() {
         try {
            return Class.forName("com.google.protobuf.Extension");
         } catch (ClassNotFoundException var1) {
            return null;
         }
      }
   }

   private static final class ObjectIntPair {
      private final Object object;
      private final int number;

      ObjectIntPair(final Object object, final int number) {
         this.object = object;
         this.number = number;
      }

      @Override
      public int hashCode() {
         return System.identityHashCode(this.object) * 65535 + this.number;
      }

      @Override
      public boolean equals(final Object obj) {
         if (!(obj instanceof ExtensionRegistryLite.ObjectIntPair)) {
            return false;
         } else {
            ExtensionRegistryLite.ObjectIntPair other = (ExtensionRegistryLite.ObjectIntPair)obj;
            return this.object == other.object && this.number == other.number;
         }
      }
   }
}
