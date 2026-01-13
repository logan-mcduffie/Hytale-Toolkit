package com.hypixel.hytale.protocol;

import com.hypixel.hytale.protocol.io.PacketIO;
import com.hypixel.hytale.protocol.io.ProtocolException;
import com.hypixel.hytale.protocol.io.ValidationResult;
import com.hypixel.hytale.protocol.io.VarInt;
import io.netty.buffer.ByteBuf;
import java.util.Arrays;
import java.util.Objects;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class Fluid {
   public static final int NULLABLE_BIT_FIELD_SIZE = 1;
   public static final int FIXED_BLOCK_SIZE = 22;
   public static final int VARIABLE_FIELD_COUNT = 5;
   public static final int VARIABLE_BLOCK_START = 42;
   public static final int MAX_SIZE = 1677721600;
   @Nullable
   public String id;
   public int maxFluidLevel;
   @Nullable
   public BlockTextures[] cubeTextures;
   public boolean requiresAlphaBlending;
   @Nonnull
   public Opacity opacity = Opacity.Solid;
   @Nullable
   public ShaderType[] shaderEffect;
   @Nullable
   public ColorLight light;
   public int fluidFXIndex;
   public int blockSoundSetIndex;
   @Nullable
   public String blockParticleSetId;
   @Nullable
   public Color particleColor;
   @Nullable
   public int[] tagIndexes;

   public Fluid() {
   }

   public Fluid(
      @Nullable String id,
      int maxFluidLevel,
      @Nullable BlockTextures[] cubeTextures,
      boolean requiresAlphaBlending,
      @Nonnull Opacity opacity,
      @Nullable ShaderType[] shaderEffect,
      @Nullable ColorLight light,
      int fluidFXIndex,
      int blockSoundSetIndex,
      @Nullable String blockParticleSetId,
      @Nullable Color particleColor,
      @Nullable int[] tagIndexes
   ) {
      this.id = id;
      this.maxFluidLevel = maxFluidLevel;
      this.cubeTextures = cubeTextures;
      this.requiresAlphaBlending = requiresAlphaBlending;
      this.opacity = opacity;
      this.shaderEffect = shaderEffect;
      this.light = light;
      this.fluidFXIndex = fluidFXIndex;
      this.blockSoundSetIndex = blockSoundSetIndex;
      this.blockParticleSetId = blockParticleSetId;
      this.particleColor = particleColor;
      this.tagIndexes = tagIndexes;
   }

   public Fluid(@Nonnull Fluid other) {
      this.id = other.id;
      this.maxFluidLevel = other.maxFluidLevel;
      this.cubeTextures = other.cubeTextures;
      this.requiresAlphaBlending = other.requiresAlphaBlending;
      this.opacity = other.opacity;
      this.shaderEffect = other.shaderEffect;
      this.light = other.light;
      this.fluidFXIndex = other.fluidFXIndex;
      this.blockSoundSetIndex = other.blockSoundSetIndex;
      this.blockParticleSetId = other.blockParticleSetId;
      this.particleColor = other.particleColor;
      this.tagIndexes = other.tagIndexes;
   }

   @Nonnull
   public static Fluid deserialize(@Nonnull ByteBuf buf, int offset) {
      Fluid obj = new Fluid();
      byte nullBits = buf.getByte(offset);
      obj.maxFluidLevel = buf.getIntLE(offset + 1);
      obj.requiresAlphaBlending = buf.getByte(offset + 5) != 0;
      obj.opacity = Opacity.fromValue(buf.getByte(offset + 6));
      if ((nullBits & 8) != 0) {
         obj.light = ColorLight.deserialize(buf, offset + 7);
      }

      obj.fluidFXIndex = buf.getIntLE(offset + 11);
      obj.blockSoundSetIndex = buf.getIntLE(offset + 15);
      if ((nullBits & 32) != 0) {
         obj.particleColor = Color.deserialize(buf, offset + 19);
      }

      if ((nullBits & 1) != 0) {
         int varPos0 = offset + 42 + buf.getIntLE(offset + 22);
         int idLen = VarInt.peek(buf, varPos0);
         if (idLen < 0) {
            throw ProtocolException.negativeLength("Id", idLen);
         }

         if (idLen > 4096000) {
            throw ProtocolException.stringTooLong("Id", idLen, 4096000);
         }

         obj.id = PacketIO.readVarString(buf, varPos0, PacketIO.UTF8);
      }

      if ((nullBits & 2) != 0) {
         int varPos1 = offset + 42 + buf.getIntLE(offset + 26);
         int cubeTexturesCount = VarInt.peek(buf, varPos1);
         if (cubeTexturesCount < 0) {
            throw ProtocolException.negativeLength("CubeTextures", cubeTexturesCount);
         }

         if (cubeTexturesCount > 4096000) {
            throw ProtocolException.arrayTooLong("CubeTextures", cubeTexturesCount, 4096000);
         }

         int varIntLen = VarInt.length(buf, varPos1);
         if (varPos1 + varIntLen + cubeTexturesCount * 5L > buf.readableBytes()) {
            throw ProtocolException.bufferTooSmall("CubeTextures", varPos1 + varIntLen + cubeTexturesCount * 5, buf.readableBytes());
         }

         obj.cubeTextures = new BlockTextures[cubeTexturesCount];
         int elemPos = varPos1 + varIntLen;

         for (int i = 0; i < cubeTexturesCount; i++) {
            obj.cubeTextures[i] = BlockTextures.deserialize(buf, elemPos);
            elemPos += BlockTextures.computeBytesConsumed(buf, elemPos);
         }
      }

      if ((nullBits & 4) != 0) {
         int varPos2 = offset + 42 + buf.getIntLE(offset + 30);
         int shaderEffectCount = VarInt.peek(buf, varPos2);
         if (shaderEffectCount < 0) {
            throw ProtocolException.negativeLength("ShaderEffect", shaderEffectCount);
         }

         if (shaderEffectCount > 4096000) {
            throw ProtocolException.arrayTooLong("ShaderEffect", shaderEffectCount, 4096000);
         }

         int varIntLen = VarInt.length(buf, varPos2);
         if (varPos2 + varIntLen + shaderEffectCount * 1L > buf.readableBytes()) {
            throw ProtocolException.bufferTooSmall("ShaderEffect", varPos2 + varIntLen + shaderEffectCount * 1, buf.readableBytes());
         }

         obj.shaderEffect = new ShaderType[shaderEffectCount];
         int elemPos = varPos2 + varIntLen;

         for (int i = 0; i < shaderEffectCount; i++) {
            obj.shaderEffect[i] = ShaderType.fromValue(buf.getByte(elemPos));
            elemPos++;
         }
      }

      if ((nullBits & 16) != 0) {
         int varPos3 = offset + 42 + buf.getIntLE(offset + 34);
         int blockParticleSetIdLen = VarInt.peek(buf, varPos3);
         if (blockParticleSetIdLen < 0) {
            throw ProtocolException.negativeLength("BlockParticleSetId", blockParticleSetIdLen);
         }

         if (blockParticleSetIdLen > 4096000) {
            throw ProtocolException.stringTooLong("BlockParticleSetId", blockParticleSetIdLen, 4096000);
         }

         obj.blockParticleSetId = PacketIO.readVarString(buf, varPos3, PacketIO.UTF8);
      }

      if ((nullBits & 64) != 0) {
         int varPos4 = offset + 42 + buf.getIntLE(offset + 38);
         int tagIndexesCount = VarInt.peek(buf, varPos4);
         if (tagIndexesCount < 0) {
            throw ProtocolException.negativeLength("TagIndexes", tagIndexesCount);
         }

         if (tagIndexesCount > 4096000) {
            throw ProtocolException.arrayTooLong("TagIndexes", tagIndexesCount, 4096000);
         }

         int varIntLen = VarInt.length(buf, varPos4);
         if (varPos4 + varIntLen + tagIndexesCount * 4L > buf.readableBytes()) {
            throw ProtocolException.bufferTooSmall("TagIndexes", varPos4 + varIntLen + tagIndexesCount * 4, buf.readableBytes());
         }

         obj.tagIndexes = new int[tagIndexesCount];

         for (int i = 0; i < tagIndexesCount; i++) {
            obj.tagIndexes[i] = buf.getIntLE(varPos4 + varIntLen + i * 4);
         }
      }

      return obj;
   }

   public static int computeBytesConsumed(@Nonnull ByteBuf buf, int offset) {
      byte nullBits = buf.getByte(offset);
      int maxEnd = 42;
      if ((nullBits & 1) != 0) {
         int fieldOffset0 = buf.getIntLE(offset + 22);
         int pos0 = offset + 42 + fieldOffset0;
         int sl = VarInt.peek(buf, pos0);
         pos0 += VarInt.length(buf, pos0) + sl;
         if (pos0 - offset > maxEnd) {
            maxEnd = pos0 - offset;
         }
      }

      if ((nullBits & 2) != 0) {
         int fieldOffset1 = buf.getIntLE(offset + 26);
         int pos1 = offset + 42 + fieldOffset1;
         int arrLen = VarInt.peek(buf, pos1);
         pos1 += VarInt.length(buf, pos1);

         for (int i = 0; i < arrLen; i++) {
            pos1 += BlockTextures.computeBytesConsumed(buf, pos1);
         }

         if (pos1 - offset > maxEnd) {
            maxEnd = pos1 - offset;
         }
      }

      if ((nullBits & 4) != 0) {
         int fieldOffset2 = buf.getIntLE(offset + 30);
         int pos2 = offset + 42 + fieldOffset2;
         int arrLen = VarInt.peek(buf, pos2);
         pos2 += VarInt.length(buf, pos2) + arrLen * 1;
         if (pos2 - offset > maxEnd) {
            maxEnd = pos2 - offset;
         }
      }

      if ((nullBits & 16) != 0) {
         int fieldOffset3 = buf.getIntLE(offset + 34);
         int pos3 = offset + 42 + fieldOffset3;
         int sl = VarInt.peek(buf, pos3);
         pos3 += VarInt.length(buf, pos3) + sl;
         if (pos3 - offset > maxEnd) {
            maxEnd = pos3 - offset;
         }
      }

      if ((nullBits & 64) != 0) {
         int fieldOffset4 = buf.getIntLE(offset + 38);
         int pos4 = offset + 42 + fieldOffset4;
         int arrLen = VarInt.peek(buf, pos4);
         pos4 += VarInt.length(buf, pos4) + arrLen * 4;
         if (pos4 - offset > maxEnd) {
            maxEnd = pos4 - offset;
         }
      }

      return maxEnd;
   }

   public void serialize(@Nonnull ByteBuf buf) {
      int startPos = buf.writerIndex();
      byte nullBits = 0;
      if (this.id != null) {
         nullBits = (byte)(nullBits | 1);
      }

      if (this.cubeTextures != null) {
         nullBits = (byte)(nullBits | 2);
      }

      if (this.shaderEffect != null) {
         nullBits = (byte)(nullBits | 4);
      }

      if (this.light != null) {
         nullBits = (byte)(nullBits | 8);
      }

      if (this.blockParticleSetId != null) {
         nullBits = (byte)(nullBits | 16);
      }

      if (this.particleColor != null) {
         nullBits = (byte)(nullBits | 32);
      }

      if (this.tagIndexes != null) {
         nullBits = (byte)(nullBits | 64);
      }

      buf.writeByte(nullBits);
      buf.writeIntLE(this.maxFluidLevel);
      buf.writeByte(this.requiresAlphaBlending ? 1 : 0);
      buf.writeByte(this.opacity.getValue());
      if (this.light != null) {
         this.light.serialize(buf);
      } else {
         buf.writeZero(4);
      }

      buf.writeIntLE(this.fluidFXIndex);
      buf.writeIntLE(this.blockSoundSetIndex);
      if (this.particleColor != null) {
         this.particleColor.serialize(buf);
      } else {
         buf.writeZero(3);
      }

      int idOffsetSlot = buf.writerIndex();
      buf.writeIntLE(0);
      int cubeTexturesOffsetSlot = buf.writerIndex();
      buf.writeIntLE(0);
      int shaderEffectOffsetSlot = buf.writerIndex();
      buf.writeIntLE(0);
      int blockParticleSetIdOffsetSlot = buf.writerIndex();
      buf.writeIntLE(0);
      int tagIndexesOffsetSlot = buf.writerIndex();
      buf.writeIntLE(0);
      int varBlockStart = buf.writerIndex();
      if (this.id != null) {
         buf.setIntLE(idOffsetSlot, buf.writerIndex() - varBlockStart);
         PacketIO.writeVarString(buf, this.id, 4096000);
      } else {
         buf.setIntLE(idOffsetSlot, -1);
      }

      if (this.cubeTextures != null) {
         buf.setIntLE(cubeTexturesOffsetSlot, buf.writerIndex() - varBlockStart);
         if (this.cubeTextures.length > 4096000) {
            throw ProtocolException.arrayTooLong("CubeTextures", this.cubeTextures.length, 4096000);
         }

         VarInt.write(buf, this.cubeTextures.length);

         for (BlockTextures item : this.cubeTextures) {
            item.serialize(buf);
         }
      } else {
         buf.setIntLE(cubeTexturesOffsetSlot, -1);
      }

      if (this.shaderEffect != null) {
         buf.setIntLE(shaderEffectOffsetSlot, buf.writerIndex() - varBlockStart);
         if (this.shaderEffect.length > 4096000) {
            throw ProtocolException.arrayTooLong("ShaderEffect", this.shaderEffect.length, 4096000);
         }

         VarInt.write(buf, this.shaderEffect.length);

         for (ShaderType item : this.shaderEffect) {
            buf.writeByte(item.getValue());
         }
      } else {
         buf.setIntLE(shaderEffectOffsetSlot, -1);
      }

      if (this.blockParticleSetId != null) {
         buf.setIntLE(blockParticleSetIdOffsetSlot, buf.writerIndex() - varBlockStart);
         PacketIO.writeVarString(buf, this.blockParticleSetId, 4096000);
      } else {
         buf.setIntLE(blockParticleSetIdOffsetSlot, -1);
      }

      if (this.tagIndexes != null) {
         buf.setIntLE(tagIndexesOffsetSlot, buf.writerIndex() - varBlockStart);
         if (this.tagIndexes.length > 4096000) {
            throw ProtocolException.arrayTooLong("TagIndexes", this.tagIndexes.length, 4096000);
         }

         VarInt.write(buf, this.tagIndexes.length);

         for (int item : this.tagIndexes) {
            buf.writeIntLE(item);
         }
      } else {
         buf.setIntLE(tagIndexesOffsetSlot, -1);
      }
   }

   public int computeSize() {
      int size = 42;
      if (this.id != null) {
         size += PacketIO.stringSize(this.id);
      }

      if (this.cubeTextures != null) {
         int cubeTexturesSize = 0;

         for (BlockTextures elem : this.cubeTextures) {
            cubeTexturesSize += elem.computeSize();
         }

         size += VarInt.size(this.cubeTextures.length) + cubeTexturesSize;
      }

      if (this.shaderEffect != null) {
         size += VarInt.size(this.shaderEffect.length) + this.shaderEffect.length * 1;
      }

      if (this.blockParticleSetId != null) {
         size += PacketIO.stringSize(this.blockParticleSetId);
      }

      if (this.tagIndexes != null) {
         size += VarInt.size(this.tagIndexes.length) + this.tagIndexes.length * 4;
      }

      return size;
   }

   public static ValidationResult validateStructure(@Nonnull ByteBuf buffer, int offset) {
      if (buffer.readableBytes() - offset < 42) {
         return ValidationResult.error("Buffer too small: expected at least 42 bytes");
      } else {
         byte nullBits = buffer.getByte(offset);
         if ((nullBits & 1) != 0) {
            int idOffset = buffer.getIntLE(offset + 22);
            if (idOffset < 0) {
               return ValidationResult.error("Invalid offset for Id");
            }

            int pos = offset + 42 + idOffset;
            if (pos >= buffer.writerIndex()) {
               return ValidationResult.error("Offset out of bounds for Id");
            }

            int idLen = VarInt.peek(buffer, pos);
            if (idLen < 0) {
               return ValidationResult.error("Invalid string length for Id");
            }

            if (idLen > 4096000) {
               return ValidationResult.error("Id exceeds max length 4096000");
            }

            pos += VarInt.length(buffer, pos);
            pos += idLen;
            if (pos > buffer.writerIndex()) {
               return ValidationResult.error("Buffer overflow reading Id");
            }
         }

         if ((nullBits & 2) != 0) {
            int cubeTexturesOffset = buffer.getIntLE(offset + 26);
            if (cubeTexturesOffset < 0) {
               return ValidationResult.error("Invalid offset for CubeTextures");
            }

            int posx = offset + 42 + cubeTexturesOffset;
            if (posx >= buffer.writerIndex()) {
               return ValidationResult.error("Offset out of bounds for CubeTextures");
            }

            int cubeTexturesCount = VarInt.peek(buffer, posx);
            if (cubeTexturesCount < 0) {
               return ValidationResult.error("Invalid array count for CubeTextures");
            }

            if (cubeTexturesCount > 4096000) {
               return ValidationResult.error("CubeTextures exceeds max length 4096000");
            }

            posx += VarInt.length(buffer, posx);

            for (int i = 0; i < cubeTexturesCount; i++) {
               ValidationResult structResult = BlockTextures.validateStructure(buffer, posx);
               if (!structResult.isValid()) {
                  return ValidationResult.error("Invalid BlockTextures in CubeTextures[" + i + "]: " + structResult.error());
               }

               posx += BlockTextures.computeBytesConsumed(buffer, posx);
            }
         }

         if ((nullBits & 4) != 0) {
            int shaderEffectOffset = buffer.getIntLE(offset + 30);
            if (shaderEffectOffset < 0) {
               return ValidationResult.error("Invalid offset for ShaderEffect");
            }

            int posxx = offset + 42 + shaderEffectOffset;
            if (posxx >= buffer.writerIndex()) {
               return ValidationResult.error("Offset out of bounds for ShaderEffect");
            }

            int shaderEffectCount = VarInt.peek(buffer, posxx);
            if (shaderEffectCount < 0) {
               return ValidationResult.error("Invalid array count for ShaderEffect");
            }

            if (shaderEffectCount > 4096000) {
               return ValidationResult.error("ShaderEffect exceeds max length 4096000");
            }

            posxx += VarInt.length(buffer, posxx);
            posxx += shaderEffectCount * 1;
            if (posxx > buffer.writerIndex()) {
               return ValidationResult.error("Buffer overflow reading ShaderEffect");
            }
         }

         if ((nullBits & 16) != 0) {
            int blockParticleSetIdOffset = buffer.getIntLE(offset + 34);
            if (blockParticleSetIdOffset < 0) {
               return ValidationResult.error("Invalid offset for BlockParticleSetId");
            }

            int posxxx = offset + 42 + blockParticleSetIdOffset;
            if (posxxx >= buffer.writerIndex()) {
               return ValidationResult.error("Offset out of bounds for BlockParticleSetId");
            }

            int blockParticleSetIdLen = VarInt.peek(buffer, posxxx);
            if (blockParticleSetIdLen < 0) {
               return ValidationResult.error("Invalid string length for BlockParticleSetId");
            }

            if (blockParticleSetIdLen > 4096000) {
               return ValidationResult.error("BlockParticleSetId exceeds max length 4096000");
            }

            posxxx += VarInt.length(buffer, posxxx);
            posxxx += blockParticleSetIdLen;
            if (posxxx > buffer.writerIndex()) {
               return ValidationResult.error("Buffer overflow reading BlockParticleSetId");
            }
         }

         if ((nullBits & 64) != 0) {
            int tagIndexesOffset = buffer.getIntLE(offset + 38);
            if (tagIndexesOffset < 0) {
               return ValidationResult.error("Invalid offset for TagIndexes");
            }

            int posxxxx = offset + 42 + tagIndexesOffset;
            if (posxxxx >= buffer.writerIndex()) {
               return ValidationResult.error("Offset out of bounds for TagIndexes");
            }

            int tagIndexesCount = VarInt.peek(buffer, posxxxx);
            if (tagIndexesCount < 0) {
               return ValidationResult.error("Invalid array count for TagIndexes");
            }

            if (tagIndexesCount > 4096000) {
               return ValidationResult.error("TagIndexes exceeds max length 4096000");
            }

            posxxxx += VarInt.length(buffer, posxxxx);
            posxxxx += tagIndexesCount * 4;
            if (posxxxx > buffer.writerIndex()) {
               return ValidationResult.error("Buffer overflow reading TagIndexes");
            }
         }

         return ValidationResult.OK;
      }
   }

   public Fluid clone() {
      Fluid copy = new Fluid();
      copy.id = this.id;
      copy.maxFluidLevel = this.maxFluidLevel;
      copy.cubeTextures = this.cubeTextures != null ? Arrays.stream(this.cubeTextures).map(e -> e.clone()).toArray(BlockTextures[]::new) : null;
      copy.requiresAlphaBlending = this.requiresAlphaBlending;
      copy.opacity = this.opacity;
      copy.shaderEffect = this.shaderEffect != null ? Arrays.copyOf(this.shaderEffect, this.shaderEffect.length) : null;
      copy.light = this.light != null ? this.light.clone() : null;
      copy.fluidFXIndex = this.fluidFXIndex;
      copy.blockSoundSetIndex = this.blockSoundSetIndex;
      copy.blockParticleSetId = this.blockParticleSetId;
      copy.particleColor = this.particleColor != null ? this.particleColor.clone() : null;
      copy.tagIndexes = this.tagIndexes != null ? Arrays.copyOf(this.tagIndexes, this.tagIndexes.length) : null;
      return copy;
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj) {
         return true;
      } else {
         return !(obj instanceof Fluid other)
            ? false
            : Objects.equals(this.id, other.id)
               && this.maxFluidLevel == other.maxFluidLevel
               && Arrays.equals((Object[])this.cubeTextures, (Object[])other.cubeTextures)
               && this.requiresAlphaBlending == other.requiresAlphaBlending
               && Objects.equals(this.opacity, other.opacity)
               && Arrays.equals((Object[])this.shaderEffect, (Object[])other.shaderEffect)
               && Objects.equals(this.light, other.light)
               && this.fluidFXIndex == other.fluidFXIndex
               && this.blockSoundSetIndex == other.blockSoundSetIndex
               && Objects.equals(this.blockParticleSetId, other.blockParticleSetId)
               && Objects.equals(this.particleColor, other.particleColor)
               && Arrays.equals(this.tagIndexes, other.tagIndexes);
      }
   }

   @Override
   public int hashCode() {
      int result = 1;
      result = 31 * result + Objects.hashCode(this.id);
      result = 31 * result + Integer.hashCode(this.maxFluidLevel);
      result = 31 * result + Arrays.hashCode((Object[])this.cubeTextures);
      result = 31 * result + Boolean.hashCode(this.requiresAlphaBlending);
      result = 31 * result + Objects.hashCode(this.opacity);
      result = 31 * result + Arrays.hashCode((Object[])this.shaderEffect);
      result = 31 * result + Objects.hashCode(this.light);
      result = 31 * result + Integer.hashCode(this.fluidFXIndex);
      result = 31 * result + Integer.hashCode(this.blockSoundSetIndex);
      result = 31 * result + Objects.hashCode(this.blockParticleSetId);
      result = 31 * result + Objects.hashCode(this.particleColor);
      return 31 * result + Arrays.hashCode(this.tagIndexes);
   }
}
