package com.google.protobuf;

public abstract class GeneratedFile {
   protected GeneratedFile() {
   }

   protected static void addOptionalExtension(ExtensionRegistry registry, final String className, final String fieldName) {
      try {
         GeneratedMessage.GeneratedExtension<?, ?> ext = (GeneratedMessage.GeneratedExtension<?, ?>)Class.forName(className).getField(fieldName).get(null);
         registry.add(ext);
      } catch (ClassNotFoundException var4) {
      } catch (NoSuchFieldException var5) {
      } catch (IllegalAccessException var6) {
      }
   }
}
