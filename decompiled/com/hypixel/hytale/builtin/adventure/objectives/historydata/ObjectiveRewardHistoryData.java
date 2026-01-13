package com.hypixel.hytale.builtin.adventure.objectives.historydata;

import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.codec.lookup.CodecMapCodec;

public abstract class ObjectiveRewardHistoryData {
   public static final CodecMapCodec<ObjectiveRewardHistoryData> CODEC = new CodecMapCodec<>("Type");
   public static final BuilderCodec<ObjectiveRewardHistoryData> BASE_CODEC = BuilderCodec.abstractBuilder(ObjectiveRewardHistoryData.class).build();
}
