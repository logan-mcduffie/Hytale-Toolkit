package org.bson.codecs.pojo;

import org.bson.BsonType;
import org.bson.codecs.Codec;

public final class PropertyModel<T> {
   private final String name;
   private final String readName;
   private final String writeName;
   private final TypeData<T> typeData;
   private final Codec<T> codec;
   private final PropertySerialization<T> propertySerialization;
   private final Boolean useDiscriminator;
   private final PropertyAccessor<T> propertyAccessor;
   private final String error;
   private volatile Codec<T> cachedCodec;
   private final BsonType bsonRepresentation;

   PropertyModel(
      String name,
      String readName,
      String writeName,
      TypeData<T> typeData,
      Codec<T> codec,
      PropertySerialization<T> propertySerialization,
      Boolean useDiscriminator,
      PropertyAccessor<T> propertyAccessor,
      String error,
      BsonType bsonRepresentation
   ) {
      this.name = name;
      this.readName = readName;
      this.writeName = writeName;
      this.typeData = typeData;
      this.codec = codec;
      this.cachedCodec = codec;
      this.propertySerialization = propertySerialization;
      this.useDiscriminator = useDiscriminator;
      this.propertyAccessor = propertyAccessor;
      this.error = error;
      this.bsonRepresentation = bsonRepresentation;
   }

   public static <T> PropertyModelBuilder<T> builder() {
      return new PropertyModelBuilder<>();
   }

   public String getName() {
      return this.name;
   }

   public String getWriteName() {
      return this.writeName;
   }

   public String getReadName() {
      return this.readName;
   }

   public boolean isWritable() {
      return this.writeName != null;
   }

   public boolean isReadable() {
      return this.readName != null;
   }

   public TypeData<T> getTypeData() {
      return this.typeData;
   }

   public Codec<T> getCodec() {
      return this.codec;
   }

   public BsonType getBsonRepresentation() {
      return this.bsonRepresentation;
   }

   public boolean shouldSerialize(T value) {
      return this.propertySerialization.shouldSerialize(value);
   }

   public PropertyAccessor<T> getPropertyAccessor() {
      return this.propertyAccessor;
   }

   public Boolean useDiscriminator() {
      return this.useDiscriminator;
   }

   @Override
   public String toString() {
      return "PropertyModel{propertyName='"
         + this.name
         + "', readName='"
         + this.readName
         + "', writeName='"
         + this.writeName
         + "', typeData="
         + this.typeData
         + "}";
   }

   @Override
   public boolean equals(Object o) {
      if (this == o) {
         return true;
      } else if (o != null && this.getClass() == o.getClass()) {
         PropertyModel<?> that = (PropertyModel<?>)o;
         if (this.getName() != null ? this.getName().equals(that.getName()) : that.getName() == null) {
            if (this.getReadName() != null ? this.getReadName().equals(that.getReadName()) : that.getReadName() == null) {
               if (this.getWriteName() != null ? this.getWriteName().equals(that.getWriteName()) : that.getWriteName() == null) {
                  if (this.getTypeData() != null ? this.getTypeData().equals(that.getTypeData()) : that.getTypeData() == null) {
                     if (this.getCodec() != null ? this.getCodec().equals(that.getCodec()) : that.getCodec() == null) {
                        if (this.getPropertySerialization() != null
                           ? this.getPropertySerialization().equals(that.getPropertySerialization())
                           : that.getPropertySerialization() == null) {
                           if (this.useDiscriminator != null ? this.useDiscriminator.equals(that.useDiscriminator) : that.useDiscriminator == null) {
                              if (this.getPropertyAccessor() != null
                                 ? this.getPropertyAccessor().equals(that.getPropertyAccessor())
                                 : that.getPropertyAccessor() == null) {
                                 if (this.getError() != null ? this.getError().equals(that.getError()) : that.getError() == null) {
                                    return this.getCachedCodec() != null ? this.getCachedCodec().equals(that.getCachedCodec()) : that.getCachedCodec() == null;
                                 } else {
                                    return false;
                                 }
                              } else {
                                 return false;
                              }
                           } else {
                              return false;
                           }
                        } else {
                           return false;
                        }
                     } else {
                        return false;
                     }
                  } else {
                     return false;
                  }
               } else {
                  return false;
               }
            } else {
               return false;
            }
         } else {
            return false;
         }
      } else {
         return false;
      }
   }

   @Override
   public int hashCode() {
      int result = this.getName() != null ? this.getName().hashCode() : 0;
      result = 31 * result + (this.getReadName() != null ? this.getReadName().hashCode() : 0);
      result = 31 * result + (this.getWriteName() != null ? this.getWriteName().hashCode() : 0);
      result = 31 * result + (this.getTypeData() != null ? this.getTypeData().hashCode() : 0);
      result = 31 * result + (this.getCodec() != null ? this.getCodec().hashCode() : 0);
      result = 31 * result + (this.getPropertySerialization() != null ? this.getPropertySerialization().hashCode() : 0);
      result = 31 * result + (this.useDiscriminator != null ? this.useDiscriminator.hashCode() : 0);
      result = 31 * result + (this.getPropertyAccessor() != null ? this.getPropertyAccessor().hashCode() : 0);
      result = 31 * result + (this.getError() != null ? this.getError().hashCode() : 0);
      return 31 * result + (this.getCachedCodec() != null ? this.getCachedCodec().hashCode() : 0);
   }

   boolean hasError() {
      return this.error != null;
   }

   String getError() {
      return this.error;
   }

   PropertySerialization<T> getPropertySerialization() {
      return this.propertySerialization;
   }

   void cachedCodec(Codec<T> codec) {
      this.cachedCodec = codec;
   }

   Codec<T> getCachedCodec() {
      return this.cachedCodec;
   }
}
