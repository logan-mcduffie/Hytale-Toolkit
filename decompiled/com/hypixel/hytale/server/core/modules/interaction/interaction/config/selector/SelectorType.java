package com.hypixel.hytale.server.core.modules.interaction.interaction.config.selector;

import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.codec.lookup.CodecMapCodec;
import com.hypixel.hytale.server.core.io.NetworkSerializable;

public abstract class SelectorType implements NetworkSerializable<com.hypixel.hytale.protocol.Selector> {
   public static final CodecMapCodec<SelectorType> CODEC = new CodecMapCodec<>();
   public static final BuilderCodec<SelectorType> BASE_CODEC = BuilderCodec.abstractBuilder(SelectorType.class).build();

   public abstract Selector newSelector();
}
