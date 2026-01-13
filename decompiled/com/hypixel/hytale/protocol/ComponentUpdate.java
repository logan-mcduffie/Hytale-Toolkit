package com.hypixel.hytale.protocol;

import com.hypixel.hytale.protocol.io.PacketIO;
import com.hypixel.hytale.protocol.io.ProtocolException;
import com.hypixel.hytale.protocol.io.ValidationResult;
import com.hypixel.hytale.protocol.io.VarInt;
import io.netty.buffer.ByteBuf;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.Map.Entry;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class ComponentUpdate {
   public static final int NULLABLE_BIT_FIELD_SIZE = 3;
   public static final int FIXED_BLOCK_SIZE = 159;
   public static final int VARIABLE_FIELD_COUNT = 13;
   public static final int VARIABLE_BLOCK_START = 211;
   public static final int MAX_SIZE = 1677721600;
   @Nonnull
   public ComponentUpdateType type = ComponentUpdateType.Nameplate;
   @Nullable
   public Nameplate nameplate;
   @Nullable
   public int[] entityUIComponents;
   @Nullable
   public CombatTextUpdate combatTextUpdate;
   @Nullable
   public Model model;
   @Nullable
   public PlayerSkin skin;
   @Nullable
   public ItemWithAllMetadata item;
   public int blockId;
   public float entityScale;
   @Nullable
   public Equipment equipment;
   @Nullable
   public Map<Integer, EntityStatUpdate[]> entityStatUpdates;
   @Nullable
   public ModelTransform transform;
   @Nullable
   public MovementStates movementStates;
   @Nullable
   public EntityEffectUpdate[] entityEffectUpdates;
   @Nullable
   public Map<InteractionType, Integer> interactions;
   @Nullable
   public ColorLight dynamicLight;
   public int hitboxCollisionConfigIndex;
   public int repulsionConfigIndex;
   @Nonnull
   public UUID predictionId = new UUID(0L, 0L);
   @Nullable
   public int[] soundEventIds;
   @Nullable
   public String interactionHint;
   @Nullable
   public MountedUpdate mounted;
   @Nullable
   public String[] activeAnimations;

   public ComponentUpdate() {
   }

   public ComponentUpdate(
      @Nonnull ComponentUpdateType type,
      @Nullable Nameplate nameplate,
      @Nullable int[] entityUIComponents,
      @Nullable CombatTextUpdate combatTextUpdate,
      @Nullable Model model,
      @Nullable PlayerSkin skin,
      @Nullable ItemWithAllMetadata item,
      int blockId,
      float entityScale,
      @Nullable Equipment equipment,
      @Nullable Map<Integer, EntityStatUpdate[]> entityStatUpdates,
      @Nullable ModelTransform transform,
      @Nullable MovementStates movementStates,
      @Nullable EntityEffectUpdate[] entityEffectUpdates,
      @Nullable Map<InteractionType, Integer> interactions,
      @Nullable ColorLight dynamicLight,
      int hitboxCollisionConfigIndex,
      int repulsionConfigIndex,
      @Nonnull UUID predictionId,
      @Nullable int[] soundEventIds,
      @Nullable String interactionHint,
      @Nullable MountedUpdate mounted,
      @Nullable String[] activeAnimations
   ) {
      this.type = type;
      this.nameplate = nameplate;
      this.entityUIComponents = entityUIComponents;
      this.combatTextUpdate = combatTextUpdate;
      this.model = model;
      this.skin = skin;
      this.item = item;
      this.blockId = blockId;
      this.entityScale = entityScale;
      this.equipment = equipment;
      this.entityStatUpdates = entityStatUpdates;
      this.transform = transform;
      this.movementStates = movementStates;
      this.entityEffectUpdates = entityEffectUpdates;
      this.interactions = interactions;
      this.dynamicLight = dynamicLight;
      this.hitboxCollisionConfigIndex = hitboxCollisionConfigIndex;
      this.repulsionConfigIndex = repulsionConfigIndex;
      this.predictionId = predictionId;
      this.soundEventIds = soundEventIds;
      this.interactionHint = interactionHint;
      this.mounted = mounted;
      this.activeAnimations = activeAnimations;
   }

   public ComponentUpdate(@Nonnull ComponentUpdate other) {
      this.type = other.type;
      this.nameplate = other.nameplate;
      this.entityUIComponents = other.entityUIComponents;
      this.combatTextUpdate = other.combatTextUpdate;
      this.model = other.model;
      this.skin = other.skin;
      this.item = other.item;
      this.blockId = other.blockId;
      this.entityScale = other.entityScale;
      this.equipment = other.equipment;
      this.entityStatUpdates = other.entityStatUpdates;
      this.transform = other.transform;
      this.movementStates = other.movementStates;
      this.entityEffectUpdates = other.entityEffectUpdates;
      this.interactions = other.interactions;
      this.dynamicLight = other.dynamicLight;
      this.hitboxCollisionConfigIndex = other.hitboxCollisionConfigIndex;
      this.repulsionConfigIndex = other.repulsionConfigIndex;
      this.predictionId = other.predictionId;
      this.soundEventIds = other.soundEventIds;
      this.interactionHint = other.interactionHint;
      this.mounted = other.mounted;
      this.activeAnimations = other.activeAnimations;
   }

   @Nonnull
   public static ComponentUpdate deserialize(@Nonnull ByteBuf buf, int offset) {
      ComponentUpdate obj = new ComponentUpdate();
      byte[] nullBits = PacketIO.readBytes(buf, offset, 3);
      obj.type = ComponentUpdateType.fromValue(buf.getByte(offset + 3));
      obj.blockId = buf.getIntLE(offset + 4);
      obj.entityScale = buf.getFloatLE(offset + 8);
      if ((nullBits[1] & 1) != 0) {
         obj.transform = ModelTransform.deserialize(buf, offset + 12);
      }

      if ((nullBits[1] & 2) != 0) {
         obj.movementStates = MovementStates.deserialize(buf, offset + 61);
      }

      if ((nullBits[1] & 16) != 0) {
         obj.dynamicLight = ColorLight.deserialize(buf, offset + 83);
      }

      obj.hitboxCollisionConfigIndex = buf.getIntLE(offset + 87);
      obj.repulsionConfigIndex = buf.getIntLE(offset + 91);
      obj.predictionId = PacketIO.readUUID(buf, offset + 95);
      if ((nullBits[1] & 128) != 0) {
         obj.mounted = MountedUpdate.deserialize(buf, offset + 111);
      }

      if ((nullBits[0] & 1) != 0) {
         int varPos0 = offset + 211 + buf.getIntLE(offset + 159);
         obj.nameplate = Nameplate.deserialize(buf, varPos0);
      }

      if ((nullBits[0] & 2) != 0) {
         int varPos1 = offset + 211 + buf.getIntLE(offset + 163);
         int entityUIComponentsCount = VarInt.peek(buf, varPos1);
         if (entityUIComponentsCount < 0) {
            throw ProtocolException.negativeLength("EntityUIComponents", entityUIComponentsCount);
         }

         if (entityUIComponentsCount > 4096000) {
            throw ProtocolException.arrayTooLong("EntityUIComponents", entityUIComponentsCount, 4096000);
         }

         int varIntLen = VarInt.length(buf, varPos1);
         if (varPos1 + varIntLen + entityUIComponentsCount * 4L > buf.readableBytes()) {
            throw ProtocolException.bufferTooSmall("EntityUIComponents", varPos1 + varIntLen + entityUIComponentsCount * 4, buf.readableBytes());
         }

         obj.entityUIComponents = new int[entityUIComponentsCount];

         for (int i = 0; i < entityUIComponentsCount; i++) {
            obj.entityUIComponents[i] = buf.getIntLE(varPos1 + varIntLen + i * 4);
         }
      }

      if ((nullBits[0] & 4) != 0) {
         int varPos2 = offset + 211 + buf.getIntLE(offset + 167);
         obj.combatTextUpdate = CombatTextUpdate.deserialize(buf, varPos2);
      }

      if ((nullBits[0] & 8) != 0) {
         int varPos3 = offset + 211 + buf.getIntLE(offset + 171);
         obj.model = Model.deserialize(buf, varPos3);
      }

      if ((nullBits[0] & 16) != 0) {
         int varPos4 = offset + 211 + buf.getIntLE(offset + 175);
         obj.skin = PlayerSkin.deserialize(buf, varPos4);
      }

      if ((nullBits[0] & 32) != 0) {
         int varPos5 = offset + 211 + buf.getIntLE(offset + 179);
         obj.item = ItemWithAllMetadata.deserialize(buf, varPos5);
      }

      if ((nullBits[0] & 64) != 0) {
         int varPos6 = offset + 211 + buf.getIntLE(offset + 183);
         obj.equipment = Equipment.deserialize(buf, varPos6);
      }

      if ((nullBits[0] & 128) != 0) {
         int varPos7 = offset + 211 + buf.getIntLE(offset + 187);
         int entityStatUpdatesCount = VarInt.peek(buf, varPos7);
         if (entityStatUpdatesCount < 0) {
            throw ProtocolException.negativeLength("EntityStatUpdates", entityStatUpdatesCount);
         }

         if (entityStatUpdatesCount > 4096000) {
            throw ProtocolException.dictionaryTooLarge("EntityStatUpdates", entityStatUpdatesCount, 4096000);
         }

         int varIntLen = VarInt.length(buf, varPos7);
         obj.entityStatUpdates = new HashMap<>(entityStatUpdatesCount);
         int dictPos = varPos7 + varIntLen;

         for (int i = 0; i < entityStatUpdatesCount; i++) {
            int key = buf.getIntLE(dictPos);
            dictPos += 4;
            int valLen = VarInt.peek(buf, dictPos);
            if (valLen < 0) {
               throw ProtocolException.negativeLength("val", valLen);
            }

            if (valLen > 64) {
               throw ProtocolException.arrayTooLong("val", valLen, 64);
            }

            int valVarLen = VarInt.length(buf, dictPos);
            if (dictPos + valVarLen + valLen * 13L > buf.readableBytes()) {
               throw ProtocolException.bufferTooSmall("val", dictPos + valVarLen + valLen * 13, buf.readableBytes());
            }

            dictPos += valVarLen;
            EntityStatUpdate[] val = new EntityStatUpdate[valLen];

            for (int valIdx = 0; valIdx < valLen; valIdx++) {
               val[valIdx] = EntityStatUpdate.deserialize(buf, dictPos);
               dictPos += EntityStatUpdate.computeBytesConsumed(buf, dictPos);
            }

            if (obj.entityStatUpdates.put(key, val) != null) {
               throw ProtocolException.duplicateKey("entityStatUpdates", key);
            }
         }
      }

      if ((nullBits[1] & 4) != 0) {
         int varPos8 = offset + 211 + buf.getIntLE(offset + 191);
         int entityEffectUpdatesCount = VarInt.peek(buf, varPos8);
         if (entityEffectUpdatesCount < 0) {
            throw ProtocolException.negativeLength("EntityEffectUpdates", entityEffectUpdatesCount);
         }

         if (entityEffectUpdatesCount > 4096000) {
            throw ProtocolException.arrayTooLong("EntityEffectUpdates", entityEffectUpdatesCount, 4096000);
         }

         int varIntLen = VarInt.length(buf, varPos8);
         if (varPos8 + varIntLen + entityEffectUpdatesCount * 12L > buf.readableBytes()) {
            throw ProtocolException.bufferTooSmall("EntityEffectUpdates", varPos8 + varIntLen + entityEffectUpdatesCount * 12, buf.readableBytes());
         }

         obj.entityEffectUpdates = new EntityEffectUpdate[entityEffectUpdatesCount];
         int elemPos = varPos8 + varIntLen;

         for (int i = 0; i < entityEffectUpdatesCount; i++) {
            obj.entityEffectUpdates[i] = EntityEffectUpdate.deserialize(buf, elemPos);
            elemPos += EntityEffectUpdate.computeBytesConsumed(buf, elemPos);
         }
      }

      if ((nullBits[1] & 8) != 0) {
         int varPos9 = offset + 211 + buf.getIntLE(offset + 195);
         int interactionsCount = VarInt.peek(buf, varPos9);
         if (interactionsCount < 0) {
            throw ProtocolException.negativeLength("Interactions", interactionsCount);
         }

         if (interactionsCount > 4096000) {
            throw ProtocolException.dictionaryTooLarge("Interactions", interactionsCount, 4096000);
         }

         int varIntLen = VarInt.length(buf, varPos9);
         obj.interactions = new HashMap<>(interactionsCount);
         int dictPos = varPos9 + varIntLen;

         for (int i = 0; i < interactionsCount; i++) {
            InteractionType keyx = InteractionType.fromValue(buf.getByte(dictPos));
            int val = buf.getIntLE(++dictPos);
            dictPos += 4;
            if (obj.interactions.put(keyx, val) != null) {
               throw ProtocolException.duplicateKey("interactions", keyx);
            }
         }
      }

      if ((nullBits[1] & 32) != 0) {
         int varPos10 = offset + 211 + buf.getIntLE(offset + 199);
         int soundEventIdsCount = VarInt.peek(buf, varPos10);
         if (soundEventIdsCount < 0) {
            throw ProtocolException.negativeLength("SoundEventIds", soundEventIdsCount);
         }

         if (soundEventIdsCount > 4096000) {
            throw ProtocolException.arrayTooLong("SoundEventIds", soundEventIdsCount, 4096000);
         }

         int varIntLen = VarInt.length(buf, varPos10);
         if (varPos10 + varIntLen + soundEventIdsCount * 4L > buf.readableBytes()) {
            throw ProtocolException.bufferTooSmall("SoundEventIds", varPos10 + varIntLen + soundEventIdsCount * 4, buf.readableBytes());
         }

         obj.soundEventIds = new int[soundEventIdsCount];

         for (int ix = 0; ix < soundEventIdsCount; ix++) {
            obj.soundEventIds[ix] = buf.getIntLE(varPos10 + varIntLen + ix * 4);
         }
      }

      if ((nullBits[1] & 64) != 0) {
         int varPos11 = offset + 211 + buf.getIntLE(offset + 203);
         int interactionHintLen = VarInt.peek(buf, varPos11);
         if (interactionHintLen < 0) {
            throw ProtocolException.negativeLength("InteractionHint", interactionHintLen);
         }

         if (interactionHintLen > 4096000) {
            throw ProtocolException.stringTooLong("InteractionHint", interactionHintLen, 4096000);
         }

         obj.interactionHint = PacketIO.readVarString(buf, varPos11, PacketIO.UTF8);
      }

      if ((nullBits[2] & 1) != 0) {
         int varPos12 = offset + 211 + buf.getIntLE(offset + 207);
         int activeAnimationsCount = VarInt.peek(buf, varPos12);
         if (activeAnimationsCount < 0) {
            throw ProtocolException.negativeLength("ActiveAnimations", activeAnimationsCount);
         }

         if (activeAnimationsCount > 4096000) {
            throw ProtocolException.arrayTooLong("ActiveAnimations", activeAnimationsCount, 4096000);
         }

         int varIntLen = VarInt.length(buf, varPos12);
         int activeAnimationsBitfieldSize = (activeAnimationsCount + 7) / 8;
         byte[] activeAnimationsBitfield = PacketIO.readBytes(buf, varPos12 + varIntLen, activeAnimationsBitfieldSize);
         obj.activeAnimations = new String[activeAnimationsCount];
         int elemPos = varPos12 + varIntLen + activeAnimationsBitfieldSize;

         for (int ix = 0; ix < activeAnimationsCount; ix++) {
            if ((activeAnimationsBitfield[ix / 8] & 1 << ix % 8) != 0) {
               int strLen = VarInt.peek(buf, elemPos);
               if (strLen < 0) {
                  throw ProtocolException.negativeLength("activeAnimations[" + ix + "]", strLen);
               }

               if (strLen > 4096000) {
                  throw ProtocolException.stringTooLong("activeAnimations[" + ix + "]", strLen, 4096000);
               }

               int strVarLen = VarInt.length(buf, elemPos);
               obj.activeAnimations[ix] = PacketIO.readVarString(buf, elemPos);
               elemPos += strVarLen + strLen;
            }
         }
      }

      return obj;
   }

   public static int computeBytesConsumed(@Nonnull ByteBuf buf, int offset) {
      byte[] nullBits = PacketIO.readBytes(buf, offset, 3);
      int maxEnd = 211;
      if ((nullBits[0] & 1) != 0) {
         int fieldOffset0 = buf.getIntLE(offset + 159);
         int pos0 = offset + 211 + fieldOffset0;
         pos0 += Nameplate.computeBytesConsumed(buf, pos0);
         if (pos0 - offset > maxEnd) {
            maxEnd = pos0 - offset;
         }
      }

      if ((nullBits[0] & 2) != 0) {
         int fieldOffset1 = buf.getIntLE(offset + 163);
         int pos1 = offset + 211 + fieldOffset1;
         int arrLen = VarInt.peek(buf, pos1);
         pos1 += VarInt.length(buf, pos1) + arrLen * 4;
         if (pos1 - offset > maxEnd) {
            maxEnd = pos1 - offset;
         }
      }

      if ((nullBits[0] & 4) != 0) {
         int fieldOffset2 = buf.getIntLE(offset + 167);
         int pos2 = offset + 211 + fieldOffset2;
         pos2 += CombatTextUpdate.computeBytesConsumed(buf, pos2);
         if (pos2 - offset > maxEnd) {
            maxEnd = pos2 - offset;
         }
      }

      if ((nullBits[0] & 8) != 0) {
         int fieldOffset3 = buf.getIntLE(offset + 171);
         int pos3 = offset + 211 + fieldOffset3;
         pos3 += Model.computeBytesConsumed(buf, pos3);
         if (pos3 - offset > maxEnd) {
            maxEnd = pos3 - offset;
         }
      }

      if ((nullBits[0] & 16) != 0) {
         int fieldOffset4 = buf.getIntLE(offset + 175);
         int pos4 = offset + 211 + fieldOffset4;
         pos4 += PlayerSkin.computeBytesConsumed(buf, pos4);
         if (pos4 - offset > maxEnd) {
            maxEnd = pos4 - offset;
         }
      }

      if ((nullBits[0] & 32) != 0) {
         int fieldOffset5 = buf.getIntLE(offset + 179);
         int pos5 = offset + 211 + fieldOffset5;
         pos5 += ItemWithAllMetadata.computeBytesConsumed(buf, pos5);
         if (pos5 - offset > maxEnd) {
            maxEnd = pos5 - offset;
         }
      }

      if ((nullBits[0] & 64) != 0) {
         int fieldOffset6 = buf.getIntLE(offset + 183);
         int pos6 = offset + 211 + fieldOffset6;
         pos6 += Equipment.computeBytesConsumed(buf, pos6);
         if (pos6 - offset > maxEnd) {
            maxEnd = pos6 - offset;
         }
      }

      if ((nullBits[0] & 128) != 0) {
         int fieldOffset7 = buf.getIntLE(offset + 187);
         int pos7 = offset + 211 + fieldOffset7;
         int dictLen = VarInt.peek(buf, pos7);
         pos7 += VarInt.length(buf, pos7);

         for (int i = 0; i < dictLen; i++) {
            pos7 += 4;
            int al = VarInt.peek(buf, pos7);
            pos7 += VarInt.length(buf, pos7);

            for (int j = 0; j < al; j++) {
               pos7 += EntityStatUpdate.computeBytesConsumed(buf, pos7);
            }
         }

         if (pos7 - offset > maxEnd) {
            maxEnd = pos7 - offset;
         }
      }

      if ((nullBits[1] & 4) != 0) {
         int fieldOffset8 = buf.getIntLE(offset + 191);
         int pos8 = offset + 211 + fieldOffset8;
         int arrLen = VarInt.peek(buf, pos8);
         pos8 += VarInt.length(buf, pos8);

         for (int i = 0; i < arrLen; i++) {
            pos8 += EntityEffectUpdate.computeBytesConsumed(buf, pos8);
         }

         if (pos8 - offset > maxEnd) {
            maxEnd = pos8 - offset;
         }
      }

      if ((nullBits[1] & 8) != 0) {
         int fieldOffset9 = buf.getIntLE(offset + 195);
         int pos9 = offset + 211 + fieldOffset9;
         int dictLen = VarInt.peek(buf, pos9);
         pos9 += VarInt.length(buf, pos9);

         for (int i = 0; i < dictLen; i++) {
            pos9 = ++pos9 + 4;
         }

         if (pos9 - offset > maxEnd) {
            maxEnd = pos9 - offset;
         }
      }

      if ((nullBits[1] & 32) != 0) {
         int fieldOffset10 = buf.getIntLE(offset + 199);
         int pos10 = offset + 211 + fieldOffset10;
         int arrLen = VarInt.peek(buf, pos10);
         pos10 += VarInt.length(buf, pos10) + arrLen * 4;
         if (pos10 - offset > maxEnd) {
            maxEnd = pos10 - offset;
         }
      }

      if ((nullBits[1] & 64) != 0) {
         int fieldOffset11 = buf.getIntLE(offset + 203);
         int pos11 = offset + 211 + fieldOffset11;
         int sl = VarInt.peek(buf, pos11);
         pos11 += VarInt.length(buf, pos11) + sl;
         if (pos11 - offset > maxEnd) {
            maxEnd = pos11 - offset;
         }
      }

      if ((nullBits[2] & 1) != 0) {
         int fieldOffset12 = buf.getIntLE(offset + 207);
         int pos12 = offset + 211 + fieldOffset12;
         int arrLen = VarInt.peek(buf, pos12);
         pos12 += VarInt.length(buf, pos12);
         int bitfieldSize = (arrLen + 7) / 8;
         byte[] bitfield = PacketIO.readBytes(buf, pos12, bitfieldSize);
         pos12 += bitfieldSize;

         for (int i = 0; i < arrLen; i++) {
            if ((bitfield[i / 8] & 1 << i % 8) != 0) {
               int sl = VarInt.peek(buf, pos12);
               pos12 += VarInt.length(buf, pos12) + sl;
            }
         }

         if (pos12 - offset > maxEnd) {
            maxEnd = pos12 - offset;
         }
      }

      return maxEnd;
   }

   public void serialize(@Nonnull ByteBuf buf) {
      int startPos = buf.writerIndex();
      byte[] nullBits = new byte[3];
      if (this.nameplate != null) {
         nullBits[0] = (byte)(nullBits[0] | 1);
      }

      if (this.entityUIComponents != null) {
         nullBits[0] = (byte)(nullBits[0] | 2);
      }

      if (this.combatTextUpdate != null) {
         nullBits[0] = (byte)(nullBits[0] | 4);
      }

      if (this.model != null) {
         nullBits[0] = (byte)(nullBits[0] | 8);
      }

      if (this.skin != null) {
         nullBits[0] = (byte)(nullBits[0] | 16);
      }

      if (this.item != null) {
         nullBits[0] = (byte)(nullBits[0] | 32);
      }

      if (this.equipment != null) {
         nullBits[0] = (byte)(nullBits[0] | 64);
      }

      if (this.entityStatUpdates != null) {
         nullBits[0] = (byte)(nullBits[0] | 128);
      }

      if (this.transform != null) {
         nullBits[1] = (byte)(nullBits[1] | 1);
      }

      if (this.movementStates != null) {
         nullBits[1] = (byte)(nullBits[1] | 2);
      }

      if (this.entityEffectUpdates != null) {
         nullBits[1] = (byte)(nullBits[1] | 4);
      }

      if (this.interactions != null) {
         nullBits[1] = (byte)(nullBits[1] | 8);
      }

      if (this.dynamicLight != null) {
         nullBits[1] = (byte)(nullBits[1] | 16);
      }

      if (this.soundEventIds != null) {
         nullBits[1] = (byte)(nullBits[1] | 32);
      }

      if (this.interactionHint != null) {
         nullBits[1] = (byte)(nullBits[1] | 64);
      }

      if (this.mounted != null) {
         nullBits[1] = (byte)(nullBits[1] | 128);
      }

      if (this.activeAnimations != null) {
         nullBits[2] = (byte)(nullBits[2] | 1);
      }

      buf.writeBytes(nullBits);
      buf.writeByte(this.type.getValue());
      buf.writeIntLE(this.blockId);
      buf.writeFloatLE(this.entityScale);
      if (this.transform != null) {
         this.transform.serialize(buf);
      } else {
         buf.writeZero(49);
      }

      if (this.movementStates != null) {
         this.movementStates.serialize(buf);
      } else {
         buf.writeZero(22);
      }

      if (this.dynamicLight != null) {
         this.dynamicLight.serialize(buf);
      } else {
         buf.writeZero(4);
      }

      buf.writeIntLE(this.hitboxCollisionConfigIndex);
      buf.writeIntLE(this.repulsionConfigIndex);
      PacketIO.writeUUID(buf, this.predictionId);
      if (this.mounted != null) {
         this.mounted.serialize(buf);
      } else {
         buf.writeZero(48);
      }

      int nameplateOffsetSlot = buf.writerIndex();
      buf.writeIntLE(0);
      int entityUIComponentsOffsetSlot = buf.writerIndex();
      buf.writeIntLE(0);
      int combatTextUpdateOffsetSlot = buf.writerIndex();
      buf.writeIntLE(0);
      int modelOffsetSlot = buf.writerIndex();
      buf.writeIntLE(0);
      int skinOffsetSlot = buf.writerIndex();
      buf.writeIntLE(0);
      int itemOffsetSlot = buf.writerIndex();
      buf.writeIntLE(0);
      int equipmentOffsetSlot = buf.writerIndex();
      buf.writeIntLE(0);
      int entityStatUpdatesOffsetSlot = buf.writerIndex();
      buf.writeIntLE(0);
      int entityEffectUpdatesOffsetSlot = buf.writerIndex();
      buf.writeIntLE(0);
      int interactionsOffsetSlot = buf.writerIndex();
      buf.writeIntLE(0);
      int soundEventIdsOffsetSlot = buf.writerIndex();
      buf.writeIntLE(0);
      int interactionHintOffsetSlot = buf.writerIndex();
      buf.writeIntLE(0);
      int activeAnimationsOffsetSlot = buf.writerIndex();
      buf.writeIntLE(0);
      int varBlockStart = buf.writerIndex();
      if (this.nameplate != null) {
         buf.setIntLE(nameplateOffsetSlot, buf.writerIndex() - varBlockStart);
         this.nameplate.serialize(buf);
      } else {
         buf.setIntLE(nameplateOffsetSlot, -1);
      }

      if (this.entityUIComponents != null) {
         buf.setIntLE(entityUIComponentsOffsetSlot, buf.writerIndex() - varBlockStart);
         if (this.entityUIComponents.length > 4096000) {
            throw ProtocolException.arrayTooLong("EntityUIComponents", this.entityUIComponents.length, 4096000);
         }

         VarInt.write(buf, this.entityUIComponents.length);

         for (int item : this.entityUIComponents) {
            buf.writeIntLE(item);
         }
      } else {
         buf.setIntLE(entityUIComponentsOffsetSlot, -1);
      }

      if (this.combatTextUpdate != null) {
         buf.setIntLE(combatTextUpdateOffsetSlot, buf.writerIndex() - varBlockStart);
         this.combatTextUpdate.serialize(buf);
      } else {
         buf.setIntLE(combatTextUpdateOffsetSlot, -1);
      }

      if (this.model != null) {
         buf.setIntLE(modelOffsetSlot, buf.writerIndex() - varBlockStart);
         this.model.serialize(buf);
      } else {
         buf.setIntLE(modelOffsetSlot, -1);
      }

      if (this.skin != null) {
         buf.setIntLE(skinOffsetSlot, buf.writerIndex() - varBlockStart);
         this.skin.serialize(buf);
      } else {
         buf.setIntLE(skinOffsetSlot, -1);
      }

      if (this.item != null) {
         buf.setIntLE(itemOffsetSlot, buf.writerIndex() - varBlockStart);
         this.item.serialize(buf);
      } else {
         buf.setIntLE(itemOffsetSlot, -1);
      }

      if (this.equipment != null) {
         buf.setIntLE(equipmentOffsetSlot, buf.writerIndex() - varBlockStart);
         this.equipment.serialize(buf);
      } else {
         buf.setIntLE(equipmentOffsetSlot, -1);
      }

      if (this.entityStatUpdates != null) {
         buf.setIntLE(entityStatUpdatesOffsetSlot, buf.writerIndex() - varBlockStart);
         if (this.entityStatUpdates.size() > 4096000) {
            throw ProtocolException.dictionaryTooLarge("EntityStatUpdates", this.entityStatUpdates.size(), 4096000);
         }

         VarInt.write(buf, this.entityStatUpdates.size());

         for (Entry<Integer, EntityStatUpdate[]> e : this.entityStatUpdates.entrySet()) {
            buf.writeIntLE(e.getKey());
            VarInt.write(buf, e.getValue().length);

            for (EntityStatUpdate arrItem : e.getValue()) {
               arrItem.serialize(buf);
            }
         }
      } else {
         buf.setIntLE(entityStatUpdatesOffsetSlot, -1);
      }

      if (this.entityEffectUpdates != null) {
         buf.setIntLE(entityEffectUpdatesOffsetSlot, buf.writerIndex() - varBlockStart);
         if (this.entityEffectUpdates.length > 4096000) {
            throw ProtocolException.arrayTooLong("EntityEffectUpdates", this.entityEffectUpdates.length, 4096000);
         }

         VarInt.write(buf, this.entityEffectUpdates.length);

         for (EntityEffectUpdate item : this.entityEffectUpdates) {
            item.serialize(buf);
         }
      } else {
         buf.setIntLE(entityEffectUpdatesOffsetSlot, -1);
      }

      if (this.interactions != null) {
         buf.setIntLE(interactionsOffsetSlot, buf.writerIndex() - varBlockStart);
         if (this.interactions.size() > 4096000) {
            throw ProtocolException.dictionaryTooLarge("Interactions", this.interactions.size(), 4096000);
         }

         VarInt.write(buf, this.interactions.size());

         for (Entry<InteractionType, Integer> e : this.interactions.entrySet()) {
            buf.writeByte(e.getKey().getValue());
            buf.writeIntLE(e.getValue());
         }
      } else {
         buf.setIntLE(interactionsOffsetSlot, -1);
      }

      if (this.soundEventIds != null) {
         buf.setIntLE(soundEventIdsOffsetSlot, buf.writerIndex() - varBlockStart);
         if (this.soundEventIds.length > 4096000) {
            throw ProtocolException.arrayTooLong("SoundEventIds", this.soundEventIds.length, 4096000);
         }

         VarInt.write(buf, this.soundEventIds.length);

         for (int item : this.soundEventIds) {
            buf.writeIntLE(item);
         }
      } else {
         buf.setIntLE(soundEventIdsOffsetSlot, -1);
      }

      if (this.interactionHint != null) {
         buf.setIntLE(interactionHintOffsetSlot, buf.writerIndex() - varBlockStart);
         PacketIO.writeVarString(buf, this.interactionHint, 4096000);
      } else {
         buf.setIntLE(interactionHintOffsetSlot, -1);
      }

      if (this.activeAnimations != null) {
         buf.setIntLE(activeAnimationsOffsetSlot, buf.writerIndex() - varBlockStart);
         if (this.activeAnimations.length > 4096000) {
            throw ProtocolException.arrayTooLong("ActiveAnimations", this.activeAnimations.length, 4096000);
         }

         VarInt.write(buf, this.activeAnimations.length);
         int activeAnimationsBitfieldSize = (this.activeAnimations.length + 7) / 8;
         byte[] activeAnimationsBitfield = new byte[activeAnimationsBitfieldSize];

         for (int i = 0; i < this.activeAnimations.length; i++) {
            if (this.activeAnimations[i] != null) {
               activeAnimationsBitfield[i / 8] = (byte)(activeAnimationsBitfield[i / 8] | (byte)(1 << i % 8));
            }
         }

         buf.writeBytes(activeAnimationsBitfield);

         for (int ix = 0; ix < this.activeAnimations.length; ix++) {
            if (this.activeAnimations[ix] != null) {
               PacketIO.writeVarString(buf, this.activeAnimations[ix], 4096000);
            }
         }
      } else {
         buf.setIntLE(activeAnimationsOffsetSlot, -1);
      }
   }

   public int computeSize() {
      int size = 211;
      if (this.nameplate != null) {
         size += this.nameplate.computeSize();
      }

      if (this.entityUIComponents != null) {
         size += VarInt.size(this.entityUIComponents.length) + this.entityUIComponents.length * 4;
      }

      if (this.combatTextUpdate != null) {
         size += this.combatTextUpdate.computeSize();
      }

      if (this.model != null) {
         size += this.model.computeSize();
      }

      if (this.skin != null) {
         size += this.skin.computeSize();
      }

      if (this.item != null) {
         size += this.item.computeSize();
      }

      if (this.equipment != null) {
         size += this.equipment.computeSize();
      }

      if (this.entityStatUpdates != null) {
         int entityStatUpdatesSize = 0;

         for (Entry<Integer, EntityStatUpdate[]> kvp : this.entityStatUpdates.entrySet()) {
            entityStatUpdatesSize += 4 + VarInt.size(kvp.getValue().length) + Arrays.stream(kvp.getValue()).mapToInt(inner -> inner.computeSize()).sum();
         }

         size += VarInt.size(this.entityStatUpdates.size()) + entityStatUpdatesSize;
      }

      if (this.entityEffectUpdates != null) {
         int entityEffectUpdatesSize = 0;

         for (EntityEffectUpdate elem : this.entityEffectUpdates) {
            entityEffectUpdatesSize += elem.computeSize();
         }

         size += VarInt.size(this.entityEffectUpdates.length) + entityEffectUpdatesSize;
      }

      if (this.interactions != null) {
         size += VarInt.size(this.interactions.size()) + this.interactions.size() * 5;
      }

      if (this.soundEventIds != null) {
         size += VarInt.size(this.soundEventIds.length) + this.soundEventIds.length * 4;
      }

      if (this.interactionHint != null) {
         size += PacketIO.stringSize(this.interactionHint);
      }

      if (this.activeAnimations != null) {
         int activeAnimationsSize = 0;

         for (String elem : this.activeAnimations) {
            if (elem != null) {
               activeAnimationsSize += PacketIO.stringSize(elem);
            }
         }

         size += VarInt.size(this.activeAnimations.length) + (this.activeAnimations.length + 7) / 8 + activeAnimationsSize;
      }

      return size;
   }

   public static ValidationResult validateStructure(@Nonnull ByteBuf buffer, int offset) {
      if (buffer.readableBytes() - offset < 211) {
         return ValidationResult.error("Buffer too small: expected at least 211 bytes");
      } else {
         byte[] nullBits = PacketIO.readBytes(buffer, offset, 3);
         if ((nullBits[0] & 1) != 0) {
            int nameplateOffset = buffer.getIntLE(offset + 159);
            if (nameplateOffset < 0) {
               return ValidationResult.error("Invalid offset for Nameplate");
            }

            int pos = offset + 211 + nameplateOffset;
            if (pos >= buffer.writerIndex()) {
               return ValidationResult.error("Offset out of bounds for Nameplate");
            }

            ValidationResult nameplateResult = Nameplate.validateStructure(buffer, pos);
            if (!nameplateResult.isValid()) {
               return ValidationResult.error("Invalid Nameplate: " + nameplateResult.error());
            }

            pos += Nameplate.computeBytesConsumed(buffer, pos);
         }

         if ((nullBits[0] & 2) != 0) {
            int entityUIComponentsOffset = buffer.getIntLE(offset + 163);
            if (entityUIComponentsOffset < 0) {
               return ValidationResult.error("Invalid offset for EntityUIComponents");
            }

            int posx = offset + 211 + entityUIComponentsOffset;
            if (posx >= buffer.writerIndex()) {
               return ValidationResult.error("Offset out of bounds for EntityUIComponents");
            }

            int entityUIComponentsCount = VarInt.peek(buffer, posx);
            if (entityUIComponentsCount < 0) {
               return ValidationResult.error("Invalid array count for EntityUIComponents");
            }

            if (entityUIComponentsCount > 4096000) {
               return ValidationResult.error("EntityUIComponents exceeds max length 4096000");
            }

            posx += VarInt.length(buffer, posx);
            posx += entityUIComponentsCount * 4;
            if (posx > buffer.writerIndex()) {
               return ValidationResult.error("Buffer overflow reading EntityUIComponents");
            }
         }

         if ((nullBits[0] & 4) != 0) {
            int combatTextUpdateOffset = buffer.getIntLE(offset + 167);
            if (combatTextUpdateOffset < 0) {
               return ValidationResult.error("Invalid offset for CombatTextUpdate");
            }

            int posxx = offset + 211 + combatTextUpdateOffset;
            if (posxx >= buffer.writerIndex()) {
               return ValidationResult.error("Offset out of bounds for CombatTextUpdate");
            }

            ValidationResult combatTextUpdateResult = CombatTextUpdate.validateStructure(buffer, posxx);
            if (!combatTextUpdateResult.isValid()) {
               return ValidationResult.error("Invalid CombatTextUpdate: " + combatTextUpdateResult.error());
            }

            posxx += CombatTextUpdate.computeBytesConsumed(buffer, posxx);
         }

         if ((nullBits[0] & 8) != 0) {
            int modelOffset = buffer.getIntLE(offset + 171);
            if (modelOffset < 0) {
               return ValidationResult.error("Invalid offset for Model");
            }

            int posxxx = offset + 211 + modelOffset;
            if (posxxx >= buffer.writerIndex()) {
               return ValidationResult.error("Offset out of bounds for Model");
            }

            ValidationResult modelResult = Model.validateStructure(buffer, posxxx);
            if (!modelResult.isValid()) {
               return ValidationResult.error("Invalid Model: " + modelResult.error());
            }

            posxxx += Model.computeBytesConsumed(buffer, posxxx);
         }

         if ((nullBits[0] & 16) != 0) {
            int skinOffset = buffer.getIntLE(offset + 175);
            if (skinOffset < 0) {
               return ValidationResult.error("Invalid offset for Skin");
            }

            int posxxxx = offset + 211 + skinOffset;
            if (posxxxx >= buffer.writerIndex()) {
               return ValidationResult.error("Offset out of bounds for Skin");
            }

            ValidationResult skinResult = PlayerSkin.validateStructure(buffer, posxxxx);
            if (!skinResult.isValid()) {
               return ValidationResult.error("Invalid Skin: " + skinResult.error());
            }

            posxxxx += PlayerSkin.computeBytesConsumed(buffer, posxxxx);
         }

         if ((nullBits[0] & 32) != 0) {
            int itemOffset = buffer.getIntLE(offset + 179);
            if (itemOffset < 0) {
               return ValidationResult.error("Invalid offset for Item");
            }

            int posxxxxx = offset + 211 + itemOffset;
            if (posxxxxx >= buffer.writerIndex()) {
               return ValidationResult.error("Offset out of bounds for Item");
            }

            ValidationResult itemResult = ItemWithAllMetadata.validateStructure(buffer, posxxxxx);
            if (!itemResult.isValid()) {
               return ValidationResult.error("Invalid Item: " + itemResult.error());
            }

            posxxxxx += ItemWithAllMetadata.computeBytesConsumed(buffer, posxxxxx);
         }

         if ((nullBits[0] & 64) != 0) {
            int equipmentOffset = buffer.getIntLE(offset + 183);
            if (equipmentOffset < 0) {
               return ValidationResult.error("Invalid offset for Equipment");
            }

            int posxxxxxx = offset + 211 + equipmentOffset;
            if (posxxxxxx >= buffer.writerIndex()) {
               return ValidationResult.error("Offset out of bounds for Equipment");
            }

            ValidationResult equipmentResult = Equipment.validateStructure(buffer, posxxxxxx);
            if (!equipmentResult.isValid()) {
               return ValidationResult.error("Invalid Equipment: " + equipmentResult.error());
            }

            posxxxxxx += Equipment.computeBytesConsumed(buffer, posxxxxxx);
         }

         if ((nullBits[0] & 128) != 0) {
            int entityStatUpdatesOffset = buffer.getIntLE(offset + 187);
            if (entityStatUpdatesOffset < 0) {
               return ValidationResult.error("Invalid offset for EntityStatUpdates");
            }

            int posxxxxxxx = offset + 211 + entityStatUpdatesOffset;
            if (posxxxxxxx >= buffer.writerIndex()) {
               return ValidationResult.error("Offset out of bounds for EntityStatUpdates");
            }

            int entityStatUpdatesCount = VarInt.peek(buffer, posxxxxxxx);
            if (entityStatUpdatesCount < 0) {
               return ValidationResult.error("Invalid dictionary count for EntityStatUpdates");
            }

            if (entityStatUpdatesCount > 4096000) {
               return ValidationResult.error("EntityStatUpdates exceeds max length 4096000");
            }

            posxxxxxxx += VarInt.length(buffer, posxxxxxxx);

            for (int i = 0; i < entityStatUpdatesCount; i++) {
               posxxxxxxx += 4;
               if (posxxxxxxx > buffer.writerIndex()) {
                  return ValidationResult.error("Buffer overflow reading key");
               }

               int valueArrCount = VarInt.peek(buffer, posxxxxxxx);
               if (valueArrCount < 0) {
                  return ValidationResult.error("Invalid array count for value");
               }

               posxxxxxxx += VarInt.length(buffer, posxxxxxxx);

               for (int valueArrIdx = 0; valueArrIdx < valueArrCount; valueArrIdx++) {
                  posxxxxxxx += EntityStatUpdate.computeBytesConsumed(buffer, posxxxxxxx);
               }
            }
         }

         if ((nullBits[1] & 4) != 0) {
            int entityEffectUpdatesOffset = buffer.getIntLE(offset + 191);
            if (entityEffectUpdatesOffset < 0) {
               return ValidationResult.error("Invalid offset for EntityEffectUpdates");
            }

            int posxxxxxxxx = offset + 211 + entityEffectUpdatesOffset;
            if (posxxxxxxxx >= buffer.writerIndex()) {
               return ValidationResult.error("Offset out of bounds for EntityEffectUpdates");
            }

            int entityEffectUpdatesCount = VarInt.peek(buffer, posxxxxxxxx);
            if (entityEffectUpdatesCount < 0) {
               return ValidationResult.error("Invalid array count for EntityEffectUpdates");
            }

            if (entityEffectUpdatesCount > 4096000) {
               return ValidationResult.error("EntityEffectUpdates exceeds max length 4096000");
            }

            posxxxxxxxx += VarInt.length(buffer, posxxxxxxxx);

            for (int i = 0; i < entityEffectUpdatesCount; i++) {
               ValidationResult structResult = EntityEffectUpdate.validateStructure(buffer, posxxxxxxxx);
               if (!structResult.isValid()) {
                  return ValidationResult.error("Invalid EntityEffectUpdate in EntityEffectUpdates[" + i + "]: " + structResult.error());
               }

               posxxxxxxxx += EntityEffectUpdate.computeBytesConsumed(buffer, posxxxxxxxx);
            }
         }

         if ((nullBits[1] & 8) != 0) {
            int interactionsOffset = buffer.getIntLE(offset + 195);
            if (interactionsOffset < 0) {
               return ValidationResult.error("Invalid offset for Interactions");
            }

            int posxxxxxxxxx = offset + 211 + interactionsOffset;
            if (posxxxxxxxxx >= buffer.writerIndex()) {
               return ValidationResult.error("Offset out of bounds for Interactions");
            }

            int interactionsCount = VarInt.peek(buffer, posxxxxxxxxx);
            if (interactionsCount < 0) {
               return ValidationResult.error("Invalid dictionary count for Interactions");
            }

            if (interactionsCount > 4096000) {
               return ValidationResult.error("Interactions exceeds max length 4096000");
            }

            posxxxxxxxxx += VarInt.length(buffer, posxxxxxxxxx);

            for (int i = 0; i < interactionsCount; i++) {
               posxxxxxxxxx = ++posxxxxxxxxx + 4;
               if (posxxxxxxxxx > buffer.writerIndex()) {
                  return ValidationResult.error("Buffer overflow reading value");
               }
            }
         }

         if ((nullBits[1] & 32) != 0) {
            int soundEventIdsOffset = buffer.getIntLE(offset + 199);
            if (soundEventIdsOffset < 0) {
               return ValidationResult.error("Invalid offset for SoundEventIds");
            }

            int posxxxxxxxxxx = offset + 211 + soundEventIdsOffset;
            if (posxxxxxxxxxx >= buffer.writerIndex()) {
               return ValidationResult.error("Offset out of bounds for SoundEventIds");
            }

            int soundEventIdsCount = VarInt.peek(buffer, posxxxxxxxxxx);
            if (soundEventIdsCount < 0) {
               return ValidationResult.error("Invalid array count for SoundEventIds");
            }

            if (soundEventIdsCount > 4096000) {
               return ValidationResult.error("SoundEventIds exceeds max length 4096000");
            }

            posxxxxxxxxxx += VarInt.length(buffer, posxxxxxxxxxx);
            posxxxxxxxxxx += soundEventIdsCount * 4;
            if (posxxxxxxxxxx > buffer.writerIndex()) {
               return ValidationResult.error("Buffer overflow reading SoundEventIds");
            }
         }

         if ((nullBits[1] & 64) != 0) {
            int interactionHintOffset = buffer.getIntLE(offset + 203);
            if (interactionHintOffset < 0) {
               return ValidationResult.error("Invalid offset for InteractionHint");
            }

            int posxxxxxxxxxxx = offset + 211 + interactionHintOffset;
            if (posxxxxxxxxxxx >= buffer.writerIndex()) {
               return ValidationResult.error("Offset out of bounds for InteractionHint");
            }

            int interactionHintLen = VarInt.peek(buffer, posxxxxxxxxxxx);
            if (interactionHintLen < 0) {
               return ValidationResult.error("Invalid string length for InteractionHint");
            }

            if (interactionHintLen > 4096000) {
               return ValidationResult.error("InteractionHint exceeds max length 4096000");
            }

            posxxxxxxxxxxx += VarInt.length(buffer, posxxxxxxxxxxx);
            posxxxxxxxxxxx += interactionHintLen;
            if (posxxxxxxxxxxx > buffer.writerIndex()) {
               return ValidationResult.error("Buffer overflow reading InteractionHint");
            }
         }

         if ((nullBits[2] & 1) != 0) {
            int activeAnimationsOffset = buffer.getIntLE(offset + 207);
            if (activeAnimationsOffset < 0) {
               return ValidationResult.error("Invalid offset for ActiveAnimations");
            }

            int posxxxxxxxxxxxx = offset + 211 + activeAnimationsOffset;
            if (posxxxxxxxxxxxx >= buffer.writerIndex()) {
               return ValidationResult.error("Offset out of bounds for ActiveAnimations");
            }

            int activeAnimationsCount = VarInt.peek(buffer, posxxxxxxxxxxxx);
            if (activeAnimationsCount < 0) {
               return ValidationResult.error("Invalid array count for ActiveAnimations");
            }

            if (activeAnimationsCount > 4096000) {
               return ValidationResult.error("ActiveAnimations exceeds max length 4096000");
            }

            posxxxxxxxxxxxx += VarInt.length(buffer, posxxxxxxxxxxxx);

            for (int ix = 0; ix < activeAnimationsCount; ix++) {
               int strLen = VarInt.peek(buffer, posxxxxxxxxxxxx);
               if (strLen < 0) {
                  return ValidationResult.error("Invalid string length in ActiveAnimations");
               }

               posxxxxxxxxxxxx += VarInt.length(buffer, posxxxxxxxxxxxx);
               posxxxxxxxxxxxx += strLen;
               if (posxxxxxxxxxxxx > buffer.writerIndex()) {
                  return ValidationResult.error("Buffer overflow reading string in ActiveAnimations");
               }
            }
         }

         return ValidationResult.OK;
      }
   }

   public ComponentUpdate clone() {
      ComponentUpdate copy = new ComponentUpdate();
      copy.type = this.type;
      copy.nameplate = this.nameplate != null ? this.nameplate.clone() : null;
      copy.entityUIComponents = this.entityUIComponents != null ? Arrays.copyOf(this.entityUIComponents, this.entityUIComponents.length) : null;
      copy.combatTextUpdate = this.combatTextUpdate != null ? this.combatTextUpdate.clone() : null;
      copy.model = this.model != null ? this.model.clone() : null;
      copy.skin = this.skin != null ? this.skin.clone() : null;
      copy.item = this.item != null ? this.item.clone() : null;
      copy.blockId = this.blockId;
      copy.entityScale = this.entityScale;
      copy.equipment = this.equipment != null ? this.equipment.clone() : null;
      if (this.entityStatUpdates != null) {
         Map<Integer, EntityStatUpdate[]> m = new HashMap<>();

         for (Entry<Integer, EntityStatUpdate[]> e : this.entityStatUpdates.entrySet()) {
            m.put(e.getKey(), Arrays.stream(e.getValue()).map(x -> x.clone()).toArray(EntityStatUpdate[]::new));
         }

         copy.entityStatUpdates = m;
      }

      copy.transform = this.transform != null ? this.transform.clone() : null;
      copy.movementStates = this.movementStates != null ? this.movementStates.clone() : null;
      copy.entityEffectUpdates = this.entityEffectUpdates != null
         ? Arrays.stream(this.entityEffectUpdates).map(ex -> ex.clone()).toArray(EntityEffectUpdate[]::new)
         : null;
      copy.interactions = this.interactions != null ? new HashMap<>(this.interactions) : null;
      copy.dynamicLight = this.dynamicLight != null ? this.dynamicLight.clone() : null;
      copy.hitboxCollisionConfigIndex = this.hitboxCollisionConfigIndex;
      copy.repulsionConfigIndex = this.repulsionConfigIndex;
      copy.predictionId = this.predictionId;
      copy.soundEventIds = this.soundEventIds != null ? Arrays.copyOf(this.soundEventIds, this.soundEventIds.length) : null;
      copy.interactionHint = this.interactionHint;
      copy.mounted = this.mounted != null ? this.mounted.clone() : null;
      copy.activeAnimations = this.activeAnimations != null ? Arrays.copyOf(this.activeAnimations, this.activeAnimations.length) : null;
      return copy;
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj) {
         return true;
      } else {
         return !(obj instanceof ComponentUpdate other)
            ? false
            : Objects.equals(this.type, other.type)
               && Objects.equals(this.nameplate, other.nameplate)
               && Arrays.equals(this.entityUIComponents, other.entityUIComponents)
               && Objects.equals(this.combatTextUpdate, other.combatTextUpdate)
               && Objects.equals(this.model, other.model)
               && Objects.equals(this.skin, other.skin)
               && Objects.equals(this.item, other.item)
               && this.blockId == other.blockId
               && this.entityScale == other.entityScale
               && Objects.equals(this.equipment, other.equipment)
               && Objects.equals(this.entityStatUpdates, other.entityStatUpdates)
               && Objects.equals(this.transform, other.transform)
               && Objects.equals(this.movementStates, other.movementStates)
               && Arrays.equals((Object[])this.entityEffectUpdates, (Object[])other.entityEffectUpdates)
               && Objects.equals(this.interactions, other.interactions)
               && Objects.equals(this.dynamicLight, other.dynamicLight)
               && this.hitboxCollisionConfigIndex == other.hitboxCollisionConfigIndex
               && this.repulsionConfigIndex == other.repulsionConfigIndex
               && Objects.equals(this.predictionId, other.predictionId)
               && Arrays.equals(this.soundEventIds, other.soundEventIds)
               && Objects.equals(this.interactionHint, other.interactionHint)
               && Objects.equals(this.mounted, other.mounted)
               && Arrays.equals((Object[])this.activeAnimations, (Object[])other.activeAnimations);
      }
   }

   @Override
   public int hashCode() {
      int result = 1;
      result = 31 * result + Objects.hashCode(this.type);
      result = 31 * result + Objects.hashCode(this.nameplate);
      result = 31 * result + Arrays.hashCode(this.entityUIComponents);
      result = 31 * result + Objects.hashCode(this.combatTextUpdate);
      result = 31 * result + Objects.hashCode(this.model);
      result = 31 * result + Objects.hashCode(this.skin);
      result = 31 * result + Objects.hashCode(this.item);
      result = 31 * result + Integer.hashCode(this.blockId);
      result = 31 * result + Float.hashCode(this.entityScale);
      result = 31 * result + Objects.hashCode(this.equipment);
      result = 31 * result + Objects.hashCode(this.entityStatUpdates);
      result = 31 * result + Objects.hashCode(this.transform);
      result = 31 * result + Objects.hashCode(this.movementStates);
      result = 31 * result + Arrays.hashCode((Object[])this.entityEffectUpdates);
      result = 31 * result + Objects.hashCode(this.interactions);
      result = 31 * result + Objects.hashCode(this.dynamicLight);
      result = 31 * result + Integer.hashCode(this.hitboxCollisionConfigIndex);
      result = 31 * result + Integer.hashCode(this.repulsionConfigIndex);
      result = 31 * result + Objects.hashCode(this.predictionId);
      result = 31 * result + Arrays.hashCode(this.soundEventIds);
      result = 31 * result + Objects.hashCode(this.interactionHint);
      result = 31 * result + Objects.hashCode(this.mounted);
      return 31 * result + Arrays.hashCode((Object[])this.activeAnimations);
   }
}
