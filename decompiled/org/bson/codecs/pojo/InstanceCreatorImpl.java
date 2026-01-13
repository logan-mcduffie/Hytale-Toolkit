package org.bson.codecs.pojo;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import org.bson.codecs.configuration.CodecConfigurationException;

final class InstanceCreatorImpl<T> implements InstanceCreator<T> {
   private final CreatorExecutable<T> creatorExecutable;
   private final Map<PropertyModel<?>, Object> cachedValues;
   private final Map<String, Integer> properties;
   private final Object[] params;
   private T newInstance;

   InstanceCreatorImpl(CreatorExecutable<T> creatorExecutable) {
      this.creatorExecutable = creatorExecutable;
      if (creatorExecutable.getProperties().isEmpty()) {
         this.cachedValues = null;
         this.properties = null;
         this.params = null;
         this.newInstance = creatorExecutable.getInstance();
      } else {
         this.cachedValues = new HashMap<>();
         this.properties = new HashMap<>();

         for (int i = 0; i < creatorExecutable.getProperties().size(); i++) {
            if (creatorExecutable.getIdPropertyIndex() != null && creatorExecutable.getIdPropertyIndex() == i) {
               this.properties.put("_id", creatorExecutable.getIdPropertyIndex());
            } else {
               this.properties.put(creatorExecutable.getProperties().get(i).value(), i);
            }
         }

         this.params = new Object[this.properties.size()];
      }
   }

   @Override
   public <S> void set(S value, PropertyModel<S> propertyModel) {
      if (this.newInstance != null) {
         propertyModel.getPropertyAccessor().set(this.newInstance, value);
      } else {
         if (!this.properties.isEmpty()) {
            String propertyName = propertyModel.getWriteName();
            if (!this.properties.containsKey(propertyName)) {
               propertyName = propertyModel.getName();
            }

            Integer index = this.properties.get(propertyName);
            if (index != null) {
               this.params[index] = value;
            }

            this.properties.remove(propertyName);
         }

         if (this.properties.isEmpty()) {
            this.constructInstanceAndProcessCachedValues();
         } else {
            this.cachedValues.put(propertyModel, value);
         }
      }
   }

   @Override
   public T getInstance() {
      if (this.newInstance == null) {
         try {
            for (Entry<String, Integer> entry : this.properties.entrySet()) {
               this.params[entry.getValue()] = null;
            }

            this.constructInstanceAndProcessCachedValues();
         } catch (CodecConfigurationException var3) {
            throw new CodecConfigurationException(
               String.format(
                  "Could not construct new instance of: %s. Missing the following properties: %s",
                  this.creatorExecutable.getType().getSimpleName(),
                  this.properties.keySet()
               ),
               var3
            );
         }
      }

      return this.newInstance;
   }

   private void constructInstanceAndProcessCachedValues() {
      try {
         this.newInstance = this.creatorExecutable.getInstance(this.params);
      } catch (Exception var3) {
         throw new CodecConfigurationException(var3.getMessage(), var3);
      }

      for (Entry<PropertyModel<?>, Object> entry : this.cachedValues.entrySet()) {
         this.setPropertyValue(entry.getKey(), entry.getValue());
      }
   }

   private <S> void setPropertyValue(PropertyModel<S> propertyModel, Object value) {
      this.set(value, propertyModel);
   }
}
