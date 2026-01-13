package org.bson.codecs.pojo;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.bson.assertions.Assertions;
import org.bson.codecs.Codec;
import org.bson.codecs.configuration.CodecProvider;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.diagnostics.Logger;
import org.bson.diagnostics.Loggers;

public final class PojoCodecProvider implements CodecProvider {
   static final Logger LOGGER = Loggers.getLogger("codecs.pojo");
   private final boolean automatic;
   private final Map<Class<?>, ClassModel<?>> classModels;
   private final Set<String> packages;
   private final List<Convention> conventions;
   private final DiscriminatorLookup discriminatorLookup;
   private final List<PropertyCodecProvider> propertyCodecProviders;

   private PojoCodecProvider(
      boolean automatic,
      Map<Class<?>, ClassModel<?>> classModels,
      Set<String> packages,
      List<Convention> conventions,
      List<PropertyCodecProvider> propertyCodecProviders
   ) {
      this.automatic = automatic;
      this.classModels = classModels;
      this.packages = packages;
      this.conventions = conventions;
      this.discriminatorLookup = new DiscriminatorLookup(classModels, packages);
      this.propertyCodecProviders = propertyCodecProviders;
   }

   public static PojoCodecProvider.Builder builder() {
      return new PojoCodecProvider.Builder();
   }

   @Override
   public <T> Codec<T> get(Class<T> clazz, CodecRegistry registry) {
      return this.getPojoCodec(clazz, registry);
   }

   private <T> PojoCodec<T> getPojoCodec(Class<T> clazz, CodecRegistry registry) {
      ClassModel<T> classModel = (ClassModel<T>)this.classModels.get(clazz);
      if (classModel != null) {
         return new PojoCodecImpl<>(classModel, registry, this.propertyCodecProviders, this.discriminatorLookup);
      } else {
         if (this.automatic || clazz.getPackage() != null && this.packages.contains(clazz.getPackage().getName())) {
            try {
               classModel = createClassModel(clazz, this.conventions);
               if (clazz.isInterface() || !classModel.getPropertyModels().isEmpty()) {
                  this.discriminatorLookup.addClassModel(classModel);
                  return new AutomaticPojoCodec<>(new PojoCodecImpl<>(classModel, registry, this.propertyCodecProviders, this.discriminatorLookup));
               }
            } catch (Exception var5) {
               LOGGER.warn(String.format("Cannot use '%s' with the PojoCodec.", clazz.getSimpleName()), var5);
               return null;
            }
         }

         return null;
      }
   }

   private static <T> ClassModel<T> createClassModel(Class<T> clazz, List<Convention> conventions) {
      ClassModelBuilder<T> builder = ClassModel.builder(clazz);
      if (conventions != null) {
         builder.conventions(conventions);
      }

      return builder.build();
   }

   public static final class Builder {
      private final Set<String> packages = new HashSet<>();
      private final Map<Class<?>, ClassModel<?>> classModels = new HashMap<>();
      private final List<Class<?>> clazzes = new ArrayList<>();
      private List<Convention> conventions = null;
      private final List<PropertyCodecProvider> propertyCodecProviders = new ArrayList<>();
      private boolean automatic;

      public PojoCodecProvider build() {
         List<Convention> immutableConventions = this.conventions != null ? Collections.unmodifiableList(new ArrayList<>(this.conventions)) : null;

         for (Class<?> clazz : this.clazzes) {
            if (!this.classModels.containsKey(clazz)) {
               this.register(PojoCodecProvider.createClassModel(clazz, immutableConventions));
            }
         }

         return new PojoCodecProvider(this.automatic, this.classModels, this.packages, immutableConventions, this.propertyCodecProviders);
      }

      public PojoCodecProvider.Builder automatic(boolean automatic) {
         this.automatic = automatic;
         return this;
      }

      public PojoCodecProvider.Builder conventions(List<Convention> conventions) {
         this.conventions = Assertions.notNull("conventions", conventions);
         return this;
      }

      public PojoCodecProvider.Builder register(Class<?>... classes) {
         this.clazzes.addAll(Arrays.asList(classes));
         return this;
      }

      public PojoCodecProvider.Builder register(ClassModel<?>... classModels) {
         Assertions.notNull("classModels", classModels);

         for (ClassModel<?> classModel : classModels) {
            this.classModels.put(classModel.getType(), classModel);
         }

         return this;
      }

      public PojoCodecProvider.Builder register(String... packageNames) {
         this.packages.addAll(Arrays.asList(Assertions.notNull("packageNames", packageNames)));
         return this;
      }

      public PojoCodecProvider.Builder register(PropertyCodecProvider... providers) {
         this.propertyCodecProviders.addAll(Arrays.asList(Assertions.notNull("providers", providers)));
         return this;
      }

      private Builder() {
      }
   }
}
