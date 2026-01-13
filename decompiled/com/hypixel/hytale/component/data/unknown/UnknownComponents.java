package com.hypixel.hytale.component.data.unknown;

import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.ExtraInfo;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.codec.codecs.map.MapCodec;
import com.hypixel.hytale.component.Component;
import com.hypixel.hytale.logger.HytaleLogger;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import java.util.Map;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import org.bson.BsonDocument;
import org.bson.BsonValue;

public class UnknownComponents<ECS_TYPE> implements Component<ECS_TYPE> {
   public static final HytaleLogger LOGGER = HytaleLogger.forEnclosingClass();
   public static final String ID = "Unknown";
   public static final BuilderCodec<UnknownComponents> CODEC = BuilderCodec.builder(UnknownComponents.class, UnknownComponents::new)
      .addField(
         new KeyedCodec<>("Components", new MapCodec<>(Codec.BSON_DOCUMENT, Object2ObjectOpenHashMap::new, false)),
         (o, map) -> o.unknownComponents = map,
         o -> o.unknownComponents
      )
      .build();
   private Map<String, BsonDocument> unknownComponents;

   public UnknownComponents() {
      this.unknownComponents = new Object2ObjectOpenHashMap<>();
   }

   public UnknownComponents(Map<String, BsonDocument> unknownComponents) {
      this.unknownComponents = unknownComponents;
   }

   public void addComponent(String componentId, Component<ECS_TYPE> component, @Nonnull Codec<Component<ECS_TYPE>> codec) {
      ExtraInfo extraInfo = ExtraInfo.THREAD_LOCAL.get();
      BsonValue bsonValue = codec.encode(component, extraInfo);
      extraInfo.getValidationResults().logOrThrowValidatorExceptions(LOGGER);
      this.unknownComponents.put(componentId, bsonValue.asDocument());
   }

   public void addComponent(String componentId, @Nonnull TempUnknownComponent<ECS_TYPE> component) {
      this.unknownComponents.put(componentId, component.getDocument());
   }

   public boolean contains(String componentId) {
      return this.unknownComponents.containsKey(componentId);
   }

   @Nullable
   public <T extends Component<ECS_TYPE>> T removeComponent(String componentId, @Nonnull Codec<T> codec) {
      BsonDocument bsonDocument = this.unknownComponents.remove(componentId);
      if (bsonDocument == null) {
         return null;
      } else {
         ExtraInfo extraInfo = ExtraInfo.THREAD_LOCAL.get();
         T component = (T)codec.decode(bsonDocument, extraInfo);
         extraInfo.getValidationResults().logOrThrowValidatorExceptions(LOGGER);
         return component;
      }
   }

   public Map<String, BsonDocument> getUnknownComponents() {
      return this.unknownComponents;
   }

   @Nonnull
   @Override
   public Component<ECS_TYPE> clone() {
      return new UnknownComponents<>(new Object2ObjectOpenHashMap<>(this.unknownComponents));
   }
}
