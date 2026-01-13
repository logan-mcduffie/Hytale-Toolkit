package com.google.protobuf;

public final class GeneratorNames {
   private GeneratorNames() {
   }

   public static String getFileJavaPackage(DescriptorProtos.FileDescriptorProtoOrBuilder file) {
      return getProto2ApiDefaultJavaPackage(file.getOptions(), file.getPackage());
   }

   public static String getFileJavaPackage(Descriptors.FileDescriptor file) {
      return getProto2ApiDefaultJavaPackage(file.getOptions(), file.getPackage());
   }

   static String getDefaultJavaPackage(DescriptorProtos.FileOptions fileOptions, String filePackage) {
      return fileOptions.hasJavaPackage() ? fileOptions.getJavaPackage() : filePackage;
   }

   static String joinPackage(String a, String b) {
      if (a.isEmpty()) {
         return b;
      } else {
         return b.isEmpty() ? a : a + '.' + b;
      }
   }

   static String getProto2ApiDefaultJavaPackage(DescriptorProtos.FileOptions fileOptions, String filePackage) {
      return getDefaultJavaPackage(fileOptions, filePackage);
   }

   public static String getFileClassName(DescriptorProtos.FileDescriptorProtoOrBuilder file) {
      return getFileClassNameImpl(file, getResolvedFileFeatures(JavaFeaturesProto.java_, file));
   }

   public static String getFileClassName(Descriptors.FileDescriptor file) {
      return getFileClassNameImpl(file.toProto(), file.getFeatures().getExtension(JavaFeaturesProto.java_));
   }

   private static String getFileClassNameImpl(DescriptorProtos.FileDescriptorProtoOrBuilder file, JavaFeaturesProto.JavaFeatures resolvedFeatures) {
      if (file.getOptions().hasJavaOuterClassname()) {
         return file.getOptions().getJavaOuterClassname();
      } else {
         String className = getDefaultFileClassName(file, resolvedFeatures.getUseOldOuterClassnameDefault());
         return resolvedFeatures.getUseOldOuterClassnameDefault() && hasConflictingClassName(file, className) ? className + "OuterClass" : className;
      }
   }

   static <T extends Message> T getResolvedFileFeatures(
      GeneratedMessage.GeneratedExtension<DescriptorProtos.FeatureSet, T> ext, DescriptorProtos.FileDescriptorProtoOrBuilder file
   ) {
      DescriptorProtos.Edition edition;
      if (file.getSyntax().equals("proto3")) {
         edition = DescriptorProtos.Edition.EDITION_PROTO3;
      } else if (!file.hasEdition()) {
         edition = DescriptorProtos.Edition.EDITION_PROTO2;
      } else {
         edition = file.getEdition();
      }

      DescriptorProtos.FeatureSet features = file.getOptions().getFeatures();
      if (features.getUnknownFields().hasField(ext.getNumber())) {
         ExtensionRegistry registry = ExtensionRegistry.newInstance();
         registry.add(ext);

         try {
            features = DescriptorProtos.FeatureSet.newBuilder().mergeFrom(features.getUnknownFields().toByteString(), registry).build();
         } catch (InvalidProtocolBufferException var6) {
            throw new IllegalArgumentException("Failed to parse features", var6);
         }
      }

      return (T)Descriptors.getEditionDefaults(edition).<T>getExtension(ext).toBuilder().mergeFrom(features.getExtension(ext)).build();
   }

   static String getDefaultFileClassName(DescriptorProtos.FileDescriptorProtoOrBuilder file, boolean useOldOuterClassnameDefault) {
      String name = file.getName();
      name = name.substring(name.lastIndexOf(47) + 1);
      name = underscoresToCamelCase(stripProto(name));
      return useOldOuterClassnameDefault ? name : name + "Proto";
   }

   private static String stripProto(String filename) {
      if (filename.endsWith(".protodevel")) {
         return filename.substring(0, filename.length() - ".protodevel".length());
      } else {
         return filename.endsWith(".proto") ? filename.substring(0, filename.length() - ".proto".length()) : filename;
      }
   }

   private static boolean hasConflictingClassName(DescriptorProtos.FileDescriptorProtoOrBuilder file, String name) {
      for (DescriptorProtos.EnumDescriptorProto enumDesc : file.getEnumTypeList()) {
         if (name.equals(enumDesc.getName())) {
            return true;
         }
      }

      for (DescriptorProtos.ServiceDescriptorProto serviceDesc : file.getServiceList()) {
         if (name.equals(serviceDesc.getName())) {
            return true;
         }
      }

      for (DescriptorProtos.DescriptorProto messageDesc : file.getMessageTypeList()) {
         if (hasConflictingClassName(messageDesc, name)) {
            return true;
         }
      }

      return false;
   }

   private static boolean hasConflictingClassName(DescriptorProtos.DescriptorProto messageDesc, String name) {
      if (name.equals(messageDesc.getName())) {
         return true;
      } else {
         for (DescriptorProtos.EnumDescriptorProto enumDesc : messageDesc.getEnumTypeList()) {
            if (name.equals(enumDesc.getName())) {
               return true;
            }
         }

         for (DescriptorProtos.DescriptorProto nestedMessageDesc : messageDesc.getNestedTypeList()) {
            if (hasConflictingClassName(nestedMessageDesc, name)) {
               return true;
            }
         }

         return false;
      }
   }

   static String underscoresToCamelCase(String input, boolean capitalizeNextLetter) {
      StringBuilder result = new StringBuilder();

      for (int i = 0; i < input.length(); i++) {
         char ch = input.charAt(i);
         if ('a' <= ch && ch <= 'z') {
            if (capitalizeNextLetter) {
               result.append((char)(ch + -32));
            } else {
               result.append(ch);
            }

            capitalizeNextLetter = false;
         } else if ('A' <= ch && ch <= 'Z') {
            if (i == 0 && !capitalizeNextLetter) {
               result.append((char)(ch + ' '));
            } else {
               result.append(ch);
            }

            capitalizeNextLetter = false;
         } else if ('0' <= ch && ch <= '9') {
            result.append(ch);
            capitalizeNextLetter = true;
         } else {
            capitalizeNextLetter = true;
         }
      }

      return result.toString();
   }

   static String underscoresToCamelCase(String input) {
      return underscoresToCamelCase(input, true);
   }

   public static String getBytecodeClassName(Descriptors.Descriptor message) {
      return getClassFullName(getClassNameWithoutPackage(message), message.getFile(), !getNestInFileClass(message));
   }

   public static String getBytecodeClassName(Descriptors.EnumDescriptor enm) {
      return getClassFullName(getClassNameWithoutPackage(enm), enm.getFile(), !getNestInFileClass(enm));
   }

   static String getBytecodeClassName(Descriptors.ServiceDescriptor service) {
      String suffix = "";
      boolean isOwnFile = !getNestInFileClass(service);
      return getClassFullName(getClassNameWithoutPackage(service), service.getFile(), isOwnFile) + suffix;
   }

   static String getQualifiedFromBytecodeClassName(String bytecodeClassName) {
      return bytecodeClassName.replace('$', '.');
   }

   public static String getQualifiedClassName(Descriptors.Descriptor message) {
      return getQualifiedFromBytecodeClassName(getBytecodeClassName(message));
   }

   public static String getQualifiedClassName(Descriptors.EnumDescriptor enm) {
      return getQualifiedFromBytecodeClassName(getBytecodeClassName(enm));
   }

   public static String getQualifiedClassName(Descriptors.ServiceDescriptor service) {
      return getQualifiedFromBytecodeClassName(getBytecodeClassName(service));
   }

   private static String getClassFullName(String nameWithoutPackage, Descriptors.FileDescriptor file, boolean isOwnFile) {
      StringBuilder result = new StringBuilder();
      if (isOwnFile) {
         result.append(getFileJavaPackage(file.toProto()));
         if (result.length() > 0) {
            result.append(".");
         }
      } else {
         result.append(joinPackage(getFileJavaPackage(file.toProto()), getFileClassName(file)));
         if (result.length() > 0) {
            result.append("$");
         }
      }

      result.append(nameWithoutPackage.replace('.', '$'));
      return result.toString();
   }

   private static boolean getNestInFileClass(Descriptors.FileDescriptor file, JavaFeaturesProto.JavaFeatures resolvedFeatures) {
      switch (resolvedFeatures.getNestInFileClass()) {
         case YES:
            return true;
         case NO:
            return false;
         case LEGACY:
            return !file.getOptions().getJavaMultipleFiles();
         default:
            throw new IllegalArgumentException("Java features are not resolved");
      }
   }

   public static boolean getNestInFileClass(Descriptors.Descriptor descriptor) {
      return getNestInFileClass(descriptor.getFile(), descriptor.getFeatures().getExtension(JavaFeaturesProto.java_));
   }

   public static boolean getNestInFileClass(Descriptors.EnumDescriptor descriptor) {
      return getNestInFileClass(descriptor.getFile(), descriptor.getFeatures().getExtension(JavaFeaturesProto.java_));
   }

   private static boolean getNestInFileClass(Descriptors.ServiceDescriptor descriptor) {
      return getNestInFileClass(descriptor.getFile(), descriptor.getFeatures().getExtension(JavaFeaturesProto.java_));
   }

   static String stripPackageName(String fullName, Descriptors.FileDescriptor file) {
      return file.getPackage().isEmpty() ? fullName : fullName.substring(file.getPackage().length() + 1);
   }

   static String getClassNameWithoutPackage(Descriptors.Descriptor message) {
      return stripPackageName(message.getFullName(), message.getFile());
   }

   static String getClassNameWithoutPackage(Descriptors.EnumDescriptor enm) {
      Descriptors.Descriptor containingType = enm.getContainingType();
      return containingType == null ? enm.getName() : joinPackage(getClassNameWithoutPackage(containingType), enm.getName());
   }

   static String getClassNameWithoutPackage(Descriptors.ServiceDescriptor service) {
      return stripPackageName(service.getFullName(), service.getFile());
   }
}
