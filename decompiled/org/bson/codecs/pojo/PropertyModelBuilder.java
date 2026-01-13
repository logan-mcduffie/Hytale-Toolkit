package org.bson.codecs.pojo;

import java.lang.annotation.Annotation;
import java.util.Collections;
import java.util.List;
import org.bson.BsonType;
import org.bson.assertions.Assertions;
import org.bson.codecs.Codec;

public final class PropertyModelBuilder<T> {
   private String name;
   private String readName;
   private String writeName;
   private TypeData<T> typeData;
   private PropertySerialization<T> propertySerialization;
   private Codec<T> codec;
   private PropertyAccessor<T> propertyAccessor;
   private List<Annotation> readAnnotations = Collections.emptyList();
   private List<Annotation> writeAnnotations = Collections.emptyList();
   private Boolean discriminatorEnabled;
   private String error;
   private BsonType bsonRepresentation;

   PropertyModelBuilder() {
   }

   public String getName() {
      return this.name;
   }

   public String getReadName() {
      return this.readName;
   }

   public PropertyModelBuilder<T> readName(String readName) {
      this.readName = readName;
      return this;
   }

   public String getWriteName() {
      return this.writeName;
   }

   public PropertyModelBuilder<T> writeName(String writeName) {
      this.writeName = writeName;
      return this;
   }

   public PropertyModelBuilder<T> codec(Codec<T> codec) {
      this.codec = codec;
      return this;
   }

   Codec<T> getCodec() {
      return this.codec;
   }

   public PropertyModelBuilder<T> propertySerialization(PropertySerialization<T> propertySerialization) {
      this.propertySerialization = Assertions.notNull("propertySerialization", propertySerialization);
      return this;
   }

   public PropertySerialization<T> getPropertySerialization() {
      return this.propertySerialization;
   }

   public List<Annotation> getReadAnnotations() {
      return this.readAnnotations;
   }

   public PropertyModelBuilder<T> readAnnotations(List<Annotation> annotations) {
      this.readAnnotations = Collections.unmodifiableList(Assertions.notNull("annotations", annotations));
      return this;
   }

   public List<Annotation> getWriteAnnotations() {
      return this.writeAnnotations;
   }

   public PropertyModelBuilder<T> writeAnnotations(List<Annotation> writeAnnotations) {
      this.writeAnnotations = writeAnnotations;
      return this;
   }

   public boolean isWritable() {
      return this.writeName != null;
   }

   public boolean isReadable() {
      return this.readName != null;
   }

   public Boolean isDiscriminatorEnabled() {
      return this.discriminatorEnabled;
   }

   public PropertyModelBuilder<T> discriminatorEnabled(boolean discriminatorEnabled) {
      this.discriminatorEnabled = discriminatorEnabled;
      return this;
   }

   public PropertyAccessor<T> getPropertyAccessor() {
      return this.propertyAccessor;
   }

   public PropertyModelBuilder<T> propertyAccessor(PropertyAccessor<T> propertyAccessor) {
      this.propertyAccessor = propertyAccessor;
      return this;
   }

   public BsonType getBsonRepresentation() {
      return this.bsonRepresentation;
   }

   public PropertyModelBuilder<T> bsonRepresentation(BsonType bsonRepresentation) {
      this.bsonRepresentation = bsonRepresentation;
      return this;
   }

   public PropertyModel<T> build() {
      if (!this.isReadable() && !this.isWritable()) {
         throw new IllegalStateException(String.format("Invalid PropertyModel '%s', neither readable or writable,", this.name));
      } else {
         return new PropertyModel<>(
            PojoBuilderHelper.stateNotNull("propertyName", this.name),
            this.readName,
            this.writeName,
            PojoBuilderHelper.stateNotNull("typeData", this.typeData),
            this.codec,
            PojoBuilderHelper.stateNotNull("propertySerialization", this.propertySerialization),
            this.discriminatorEnabled,
            PojoBuilderHelper.stateNotNull("propertyAccessor", this.propertyAccessor),
            this.error,
            this.bsonRepresentation
         );
      }
   }

   @Override
   public String toString() {
      return String.format("PropertyModelBuilder{propertyName=%s, typeData=%s}", this.name, this.typeData);
   }

   PropertyModelBuilder<T> propertyName(String propertyName) {
      this.name = Assertions.notNull("propertyName", propertyName);
      return this;
   }

   TypeData<T> getTypeData() {
      return this.typeData;
   }

   PropertyModelBuilder<T> typeData(TypeData<T> typeData) {
      this.typeData = Assertions.notNull("typeData", typeData);
      return this;
   }

   PropertyModelBuilder<T> setError(String error) {
      this.error = error;
      return this;
   }
}
