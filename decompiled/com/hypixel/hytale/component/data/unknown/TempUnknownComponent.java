package com.hypixel.hytale.component.data.unknown;

import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.function.FunctionCodec;
import com.hypixel.hytale.component.Component;
import javax.annotation.Nonnull;
import org.bson.BsonDocument;

public class TempUnknownComponent<ECS_TYPE> implements Component<ECS_TYPE> {
   public static final Codec<Component> COMPONENT_CODEC = new FunctionCodec<>(
      Codec.BSON_DOCUMENT, TempUnknownComponent::new, component -> ((TempUnknownComponent)component).document
   );
   private BsonDocument document;

   public TempUnknownComponent(BsonDocument document) {
      this.document = document;
   }

   public BsonDocument getDocument() {
      return this.document;
   }

   @Nonnull
   @Override
   public Component<ECS_TYPE> clone() {
      return new TempUnknownComponent<>(this.document.clone());
   }
}
