package com.hypixel.hytale.server.core.asset.type.blocktype.config;

import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.codec.codecs.EnumCodec;
import com.hypixel.hytale.server.core.io.NetworkSerializable;
import javax.annotation.Nonnull;

public class BlockPlacementSettings implements NetworkSerializable<com.hypixel.hytale.protocol.BlockPlacementSettings> {
   public static final BuilderCodec<BlockPlacementSettings> CODEC = BuilderCodec.builder(BlockPlacementSettings.class, BlockPlacementSettings::new)
      .append(
         new KeyedCodec<>("AllowRotationKey", Codec.BOOLEAN),
         (placementSettings, o) -> placementSettings.allowRotationKey = o,
         placementSettings -> placementSettings.allowRotationKey
      )
      .add()
      .<Boolean>append(
         new KeyedCodec<>("PlaceInEmptyBlocks", Codec.BOOLEAN),
         (placementSettings, o) -> placementSettings.placeInEmptyBlocks = o,
         placementSettings -> placementSettings.placeInEmptyBlocks
      )
      .documentation("If this block is allowed to be placed inside other blocks with an Empty Material (destroying them).")
      .add()
      .<BlockPlacementSettings.RotationMode>append(
         new KeyedCodec<>("RotationMode", BlockPlacementSettings.RotationMode.CODEC),
         (placementSettings, o) -> placementSettings.rotationMode = o,
         placementSettings -> placementSettings.rotationMode
      )
      .documentation("The mode determining the rotation of this block when placed.")
      .add()
      .<BlockPlacementSettings.BlockPreviewVisibility>append(
         new KeyedCodec<>("BlockPreviewVisibility", BlockPlacementSettings.BlockPreviewVisibility.CODEC),
         (placementSettings, o) -> placementSettings.previewVisibility = o,
         placementSettings -> placementSettings.previewVisibility
      )
      .documentation("An override for the block preview visibility")
      .add()
      .append(
         new KeyedCodec<>("WallPlacementOverrideBlockId", Codec.STRING),
         (placementSettings, o) -> placementSettings.wallPlacementOverrideBlockId = o,
         placementSettings -> placementSettings.wallPlacementOverrideBlockId
      )
      .add()
      .append(
         new KeyedCodec<>("FloorPlacementOverrideBlockId", Codec.STRING),
         (placementSettings, o) -> placementSettings.floorPlacementOverrideBlockId = o,
         placementSettings -> placementSettings.floorPlacementOverrideBlockId
      )
      .add()
      .append(
         new KeyedCodec<>("CeilingPlacementOverrideBlockId", Codec.STRING),
         (placementSettings, o) -> placementSettings.ceilingPlacementOverrideBlockId = o,
         placementSettings -> placementSettings.ceilingPlacementOverrideBlockId
      )
      .add()
      .build();
   protected String wallPlacementOverrideBlockId;
   protected String floorPlacementOverrideBlockId;
   protected String ceilingPlacementOverrideBlockId;
   private boolean allowRotationKey = true;
   private boolean placeInEmptyBlocks;
   private BlockPlacementSettings.BlockPreviewVisibility previewVisibility = BlockPlacementSettings.BlockPreviewVisibility.DEFAULT;
   private BlockPlacementSettings.RotationMode rotationMode = BlockPlacementSettings.RotationMode.DEFAULT;

   protected BlockPlacementSettings() {
   }

   @Nonnull
   public com.hypixel.hytale.protocol.BlockPlacementSettings toPacket() {
      // $VF: Couldn't be decompiled
      // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
      // java.lang.IllegalStateException: Invalid switch case set: [[const(null)], [const(0)], [const(1)], [const(2)], [null]] for selector of type Lcom/hypixel/hytale/server/core/asset/type/blocktype/config/BlockPlacementSettings$BlockPreviewVisibility;
      //   at org.jetbrains.java.decompiler.modules.decompiler.exps.SwitchHeadExprent.checkExprTypeBounds(SwitchHeadExprent.java:66)
      //   at org.jetbrains.java.decompiler.modules.decompiler.vars.VarTypeProcessor.checkTypeExpr(VarTypeProcessor.java:140)
      //   at org.jetbrains.java.decompiler.modules.decompiler.vars.VarTypeProcessor.checkTypeExprent(VarTypeProcessor.java:126)
      //   at org.jetbrains.java.decompiler.modules.decompiler.vars.VarTypeProcessor.lambda$processVarTypes$2(VarTypeProcessor.java:114)
      //   at org.jetbrains.java.decompiler.modules.decompiler.flow.DirectGraph.iterateExprents(DirectGraph.java:107)
      //   at org.jetbrains.java.decompiler.modules.decompiler.vars.VarTypeProcessor.processVarTypes(VarTypeProcessor.java:114)
      //   at org.jetbrains.java.decompiler.modules.decompiler.vars.VarTypeProcessor.calculateVarTypes(VarTypeProcessor.java:44)
      //   at org.jetbrains.java.decompiler.modules.decompiler.vars.VarVersionsProcessor.setVarVersions(VarVersionsProcessor.java:68)
      //   at org.jetbrains.java.decompiler.modules.decompiler.vars.VarProcessor.setVarVersions(VarProcessor.java:47)
      //   at org.jetbrains.java.decompiler.main.rels.MethodProcessor.codeToJava(MethodProcessor.java:302)
      //
      // Bytecode:
      // 000: new com/hypixel/hytale/protocol/BlockPlacementSettings
      // 003: dup
      // 004: invokespecial com/hypixel/hytale/protocol/BlockPlacementSettings.<init> ()V
      // 007: astore 1
      // 008: aload 1
      // 009: aload 0
      // 00a: getfield com/hypixel/hytale/server/core/asset/type/blocktype/config/BlockPlacementSettings.allowRotationKey Z
      // 00d: putfield com/hypixel/hytale/protocol/BlockPlacementSettings.allowRotationKey Z
      // 010: aload 1
      // 011: aload 0
      // 012: getfield com/hypixel/hytale/server/core/asset/type/blocktype/config/BlockPlacementSettings.placeInEmptyBlocks Z
      // 015: putfield com/hypixel/hytale/protocol/BlockPlacementSettings.placeInEmptyBlocks Z
      // 018: aload 1
      // 019: aload 0
      // 01a: getfield com/hypixel/hytale/server/core/asset/type/blocktype/config/BlockPlacementSettings.previewVisibility Lcom/hypixel/hytale/server/core/asset/type/blocktype/config/BlockPlacementSettings$BlockPreviewVisibility;
      // 01d: astore 2
      // 01e: bipush 0
      // 01f: istore 3
      // 020: aload 2
      // 021: iload 3
      // 022: invokedynamic typeSwitch (Ljava/lang/Object;I)I bsm=java/lang/runtime/SwitchBootstraps.typeSwitch (Ljava/lang/invoke/MethodHandles$Lookup;Ljava/lang/String;Ljava/lang/invoke/MethodType;[Ljava/lang/Object;)Ljava/lang/invoke/CallSite; args=[ null.invoke Ljava/lang/Enum$EnumDesc;, null.invoke Ljava/lang/Enum$EnumDesc;, null.invoke Ljava/lang/Enum$EnumDesc; ]
      // 027: tableswitch 29 -1 2 39 45 51 57
      // 044: new java/lang/MatchException
      // 047: dup
      // 048: aconst_null
      // 049: aconst_null
      // 04a: invokespecial java/lang/MatchException.<init> (Ljava/lang/String;Ljava/lang/Throwable;)V
      // 04d: athrow
      // 04e: getstatic com/hypixel/hytale/protocol/BlockPreviewVisibility.Default Lcom/hypixel/hytale/protocol/BlockPreviewVisibility;
      // 051: goto 063
      // 054: getstatic com/hypixel/hytale/protocol/BlockPreviewVisibility.Default Lcom/hypixel/hytale/protocol/BlockPreviewVisibility;
      // 057: goto 063
      // 05a: getstatic com/hypixel/hytale/protocol/BlockPreviewVisibility.AlwaysHidden Lcom/hypixel/hytale/protocol/BlockPreviewVisibility;
      // 05d: goto 063
      // 060: getstatic com/hypixel/hytale/protocol/BlockPreviewVisibility.AlwaysVisible Lcom/hypixel/hytale/protocol/BlockPreviewVisibility;
      // 063: putfield com/hypixel/hytale/protocol/BlockPlacementSettings.previewVisibility Lcom/hypixel/hytale/protocol/BlockPreviewVisibility;
      // 066: aload 1
      // 067: aload 0
      // 068: getfield com/hypixel/hytale/server/core/asset/type/blocktype/config/BlockPlacementSettings.rotationMode Lcom/hypixel/hytale/server/core/asset/type/blocktype/config/BlockPlacementSettings$RotationMode;
      // 06b: astore 2
      // 06c: bipush 0
      // 06d: istore 3
      // 06e: aload 2
      // 06f: iload 3
      // 070: invokedynamic typeSwitch (Ljava/lang/Object;I)I bsm=java/lang/runtime/SwitchBootstraps.typeSwitch (Ljava/lang/invoke/MethodHandles$Lookup;Ljava/lang/String;Ljava/lang/invoke/MethodType;[Ljava/lang/Object;)Ljava/lang/invoke/CallSite; args=[ null.invoke Ljava/lang/Enum$EnumDesc;, null.invoke Ljava/lang/Enum$EnumDesc;, null.invoke Ljava/lang/Enum$EnumDesc;, null.invoke Ljava/lang/Enum$EnumDesc; ]
      // 075: tableswitch 35 -1 3 45 51 57 63 69
      // 098: new java/lang/MatchException
      // 09b: dup
      // 09c: aconst_null
      // 09d: aconst_null
      // 09e: invokespecial java/lang/MatchException.<init> (Ljava/lang/String;Ljava/lang/Throwable;)V
      // 0a1: athrow
      // 0a2: getstatic com/hypixel/hytale/protocol/BlockPlacementRotationMode.Default Lcom/hypixel/hytale/protocol/BlockPlacementRotationMode;
      // 0a5: goto 0bd
      // 0a8: getstatic com/hypixel/hytale/protocol/BlockPlacementRotationMode.Default Lcom/hypixel/hytale/protocol/BlockPlacementRotationMode;
      // 0ab: goto 0bd
      // 0ae: getstatic com/hypixel/hytale/protocol/BlockPlacementRotationMode.FacingPlayer Lcom/hypixel/hytale/protocol/BlockPlacementRotationMode;
      // 0b1: goto 0bd
      // 0b4: getstatic com/hypixel/hytale/protocol/BlockPlacementRotationMode.StairFacingPlayer Lcom/hypixel/hytale/protocol/BlockPlacementRotationMode;
      // 0b7: goto 0bd
      // 0ba: getstatic com/hypixel/hytale/protocol/BlockPlacementRotationMode.BlockNormal Lcom/hypixel/hytale/protocol/BlockPlacementRotationMode;
      // 0bd: putfield com/hypixel/hytale/protocol/BlockPlacementSettings.rotationMode Lcom/hypixel/hytale/protocol/BlockPlacementRotationMode;
      // 0c0: aload 1
      // 0c1: aload 0
      // 0c2: getfield com/hypixel/hytale/server/core/asset/type/blocktype/config/BlockPlacementSettings.wallPlacementOverrideBlockId Ljava/lang/String;
      // 0c5: ifnonnull 0cc
      // 0c8: bipush -1
      // 0c9: goto 0d6
      // 0cc: invokestatic com/hypixel/hytale/server/core/asset/type/blocktype/config/BlockType.getAssetMap ()Lcom/hypixel/hytale/assetstore/map/BlockTypeAssetMap;
      // 0cf: aload 0
      // 0d0: getfield com/hypixel/hytale/server/core/asset/type/blocktype/config/BlockPlacementSettings.wallPlacementOverrideBlockId Ljava/lang/String;
      // 0d3: invokevirtual com/hypixel/hytale/assetstore/map/BlockTypeAssetMap.getIndex (Ljava/lang/Object;)I
      // 0d6: putfield com/hypixel/hytale/protocol/BlockPlacementSettings.wallPlacementOverrideBlockId I
      // 0d9: aload 1
      // 0da: getfield com/hypixel/hytale/protocol/BlockPlacementSettings.wallPlacementOverrideBlockId I
      // 0dd: ldc -2147483648
      // 0df: if_icmpne 0f3
      // 0e2: new java/lang/IllegalArgumentException
      // 0e5: dup
      // 0e6: aload 0
      // 0e7: getfield com/hypixel/hytale/server/core/asset/type/blocktype/config/BlockPlacementSettings.wallPlacementOverrideBlockId Ljava/lang/String;
      // 0ea: invokedynamic makeConcatWithConstants (Ljava/lang/String;)Ljava/lang/String; bsm=java/lang/invoke/StringConcatFactory.makeConcatWithConstants (Ljava/lang/invoke/MethodHandles$Lookup;Ljava/lang/String;Ljava/lang/invoke/MethodType;Ljava/lang/String;[Ljava/lang/Object;)Ljava/lang/invoke/CallSite; args=[ "Unknown key! \u0001" ]
      // 0ef: invokespecial java/lang/IllegalArgumentException.<init> (Ljava/lang/String;)V
      // 0f2: athrow
      // 0f3: aload 1
      // 0f4: aload 0
      // 0f5: getfield com/hypixel/hytale/server/core/asset/type/blocktype/config/BlockPlacementSettings.floorPlacementOverrideBlockId Ljava/lang/String;
      // 0f8: ifnonnull 0ff
      // 0fb: bipush -1
      // 0fc: goto 109
      // 0ff: invokestatic com/hypixel/hytale/server/core/asset/type/blocktype/config/BlockType.getAssetMap ()Lcom/hypixel/hytale/assetstore/map/BlockTypeAssetMap;
      // 102: aload 0
      // 103: getfield com/hypixel/hytale/server/core/asset/type/blocktype/config/BlockPlacementSettings.floorPlacementOverrideBlockId Ljava/lang/String;
      // 106: invokevirtual com/hypixel/hytale/assetstore/map/BlockTypeAssetMap.getIndex (Ljava/lang/Object;)I
      // 109: putfield com/hypixel/hytale/protocol/BlockPlacementSettings.floorPlacementOverrideBlockId I
      // 10c: aload 1
      // 10d: getfield com/hypixel/hytale/protocol/BlockPlacementSettings.floorPlacementOverrideBlockId I
      // 110: ldc -2147483648
      // 112: if_icmpne 126
      // 115: new java/lang/IllegalArgumentException
      // 118: dup
      // 119: aload 0
      // 11a: getfield com/hypixel/hytale/server/core/asset/type/blocktype/config/BlockPlacementSettings.floorPlacementOverrideBlockId Ljava/lang/String;
      // 11d: invokedynamic makeConcatWithConstants (Ljava/lang/String;)Ljava/lang/String; bsm=java/lang/invoke/StringConcatFactory.makeConcatWithConstants (Ljava/lang/invoke/MethodHandles$Lookup;Ljava/lang/String;Ljava/lang/invoke/MethodType;Ljava/lang/String;[Ljava/lang/Object;)Ljava/lang/invoke/CallSite; args=[ "Unknown key! \u0001" ]
      // 122: invokespecial java/lang/IllegalArgumentException.<init> (Ljava/lang/String;)V
      // 125: athrow
      // 126: aload 1
      // 127: aload 0
      // 128: getfield com/hypixel/hytale/server/core/asset/type/blocktype/config/BlockPlacementSettings.ceilingPlacementOverrideBlockId Ljava/lang/String;
      // 12b: ifnonnull 132
      // 12e: bipush -1
      // 12f: goto 13c
      // 132: invokestatic com/hypixel/hytale/server/core/asset/type/blocktype/config/BlockType.getAssetMap ()Lcom/hypixel/hytale/assetstore/map/BlockTypeAssetMap;
      // 135: aload 0
      // 136: getfield com/hypixel/hytale/server/core/asset/type/blocktype/config/BlockPlacementSettings.ceilingPlacementOverrideBlockId Ljava/lang/String;
      // 139: invokevirtual com/hypixel/hytale/assetstore/map/BlockTypeAssetMap.getIndex (Ljava/lang/Object;)I
      // 13c: putfield com/hypixel/hytale/protocol/BlockPlacementSettings.ceilingPlacementOverrideBlockId I
      // 13f: aload 1
      // 140: getfield com/hypixel/hytale/protocol/BlockPlacementSettings.ceilingPlacementOverrideBlockId I
      // 143: ldc -2147483648
      // 145: if_icmpne 159
      // 148: new java/lang/IllegalArgumentException
      // 14b: dup
      // 14c: aload 0
      // 14d: getfield com/hypixel/hytale/server/core/asset/type/blocktype/config/BlockPlacementSettings.ceilingPlacementOverrideBlockId Ljava/lang/String;
      // 150: invokedynamic makeConcatWithConstants (Ljava/lang/String;)Ljava/lang/String; bsm=java/lang/invoke/StringConcatFactory.makeConcatWithConstants (Ljava/lang/invoke/MethodHandles$Lookup;Ljava/lang/String;Ljava/lang/invoke/MethodType;Ljava/lang/String;[Ljava/lang/Object;)Ljava/lang/invoke/CallSite; args=[ "Unknown key! \u0001" ]
      // 155: invokespecial java/lang/IllegalArgumentException.<init> (Ljava/lang/String;)V
      // 158: athrow
      // 159: aload 1
      // 15a: areturn
   }

   public String getWallPlacementOverrideBlockId() {
      return this.wallPlacementOverrideBlockId;
   }

   public String getFloorPlacementOverrideBlockId() {
      return this.floorPlacementOverrideBlockId;
   }

   public String getCeilingPlacementOverrideBlockId() {
      return this.ceilingPlacementOverrideBlockId;
   }

   public static enum BlockPreviewVisibility {
      ALWAYS_VISIBLE,
      ALWAYS_HIDDEN,
      DEFAULT;

      public static final EnumCodec<BlockPlacementSettings.BlockPreviewVisibility> CODEC = new EnumCodec<>(BlockPlacementSettings.BlockPreviewVisibility.class);
   }

   public static enum RotationMode {
      FACING_PLAYER,
      BLOCK_NORMAL,
      STAIR_FACING_PLAYER,
      DEFAULT;

      public static final EnumCodec<BlockPlacementSettings.RotationMode> CODEC = new EnumCodec<>(BlockPlacementSettings.RotationMode.class);
   }
}
