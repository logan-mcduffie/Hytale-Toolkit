package com.hypixel.hytale.protocol.packets.buildertools;

import com.hypixel.hytale.protocol.BlockPosition;
import com.hypixel.hytale.protocol.Packet;
import com.hypixel.hytale.protocol.Vector3f;
import com.hypixel.hytale.protocol.io.ProtocolException;
import com.hypixel.hytale.protocol.io.ValidationResult;
import com.hypixel.hytale.protocol.io.VarInt;
import io.netty.buffer.ByteBuf;
import java.util.Arrays;
import java.util.Objects;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class BuilderToolSelectionTransform implements Packet {
   public static final int PACKET_ID = 405;
   public static final boolean IS_COMPRESSED = false;
   public static final int NULLABLE_BIT_FIELD_SIZE = 1;
   public static final int FIXED_BLOCK_SIZE = 52;
   public static final int VARIABLE_FIELD_COUNT = 1;
   public static final int VARIABLE_BLOCK_START = 52;
   public static final int MAX_SIZE = 16384057;
   @Nullable
   public float[] transformationMatrix;
   @Nullable
   public BlockPosition initialSelectionMin;
   @Nullable
   public BlockPosition initialSelectionMax;
   @Nullable
   public Vector3f initialRotationOrigin;
   public boolean cutOriginal;
   public boolean applyTransformationToSelectionMinMax;
   public boolean isExitingTransformMode;
   @Nullable
   public BlockPosition initialPastePointForClipboardPaste;

   @Override
   public int getId() {
      return 405;
   }

   public BuilderToolSelectionTransform() {
   }

   public BuilderToolSelectionTransform(
      @Nullable float[] transformationMatrix,
      @Nullable BlockPosition initialSelectionMin,
      @Nullable BlockPosition initialSelectionMax,
      @Nullable Vector3f initialRotationOrigin,
      boolean cutOriginal,
      boolean applyTransformationToSelectionMinMax,
      boolean isExitingTransformMode,
      @Nullable BlockPosition initialPastePointForClipboardPaste
   ) {
      this.transformationMatrix = transformationMatrix;
      this.initialSelectionMin = initialSelectionMin;
      this.initialSelectionMax = initialSelectionMax;
      this.initialRotationOrigin = initialRotationOrigin;
      this.cutOriginal = cutOriginal;
      this.applyTransformationToSelectionMinMax = applyTransformationToSelectionMinMax;
      this.isExitingTransformMode = isExitingTransformMode;
      this.initialPastePointForClipboardPaste = initialPastePointForClipboardPaste;
   }

   public BuilderToolSelectionTransform(@Nonnull BuilderToolSelectionTransform other) {
      this.transformationMatrix = other.transformationMatrix;
      this.initialSelectionMin = other.initialSelectionMin;
      this.initialSelectionMax = other.initialSelectionMax;
      this.initialRotationOrigin = other.initialRotationOrigin;
      this.cutOriginal = other.cutOriginal;
      this.applyTransformationToSelectionMinMax = other.applyTransformationToSelectionMinMax;
      this.isExitingTransformMode = other.isExitingTransformMode;
      this.initialPastePointForClipboardPaste = other.initialPastePointForClipboardPaste;
   }

   @Nonnull
   public static BuilderToolSelectionTransform deserialize(@Nonnull ByteBuf buf, int offset) {
      BuilderToolSelectionTransform obj = new BuilderToolSelectionTransform();
      byte nullBits = buf.getByte(offset);
      if ((nullBits & 2) != 0) {
         obj.initialSelectionMin = BlockPosition.deserialize(buf, offset + 1);
      }

      if ((nullBits & 4) != 0) {
         obj.initialSelectionMax = BlockPosition.deserialize(buf, offset + 13);
      }

      if ((nullBits & 8) != 0) {
         obj.initialRotationOrigin = Vector3f.deserialize(buf, offset + 25);
      }

      obj.cutOriginal = buf.getByte(offset + 37) != 0;
      obj.applyTransformationToSelectionMinMax = buf.getByte(offset + 38) != 0;
      obj.isExitingTransformMode = buf.getByte(offset + 39) != 0;
      if ((nullBits & 16) != 0) {
         obj.initialPastePointForClipboardPaste = BlockPosition.deserialize(buf, offset + 40);
      }

      int pos = offset + 52;
      if ((nullBits & 1) != 0) {
         int transformationMatrixCount = VarInt.peek(buf, pos);
         if (transformationMatrixCount < 0) {
            throw ProtocolException.negativeLength("TransformationMatrix", transformationMatrixCount);
         }

         if (transformationMatrixCount > 4096000) {
            throw ProtocolException.arrayTooLong("TransformationMatrix", transformationMatrixCount, 4096000);
         }

         int transformationMatrixVarLen = VarInt.size(transformationMatrixCount);
         if (pos + transformationMatrixVarLen + transformationMatrixCount * 4L > buf.readableBytes()) {
            throw ProtocolException.bufferTooSmall(
               "TransformationMatrix", pos + transformationMatrixVarLen + transformationMatrixCount * 4, buf.readableBytes()
            );
         }

         pos += transformationMatrixVarLen;
         obj.transformationMatrix = new float[transformationMatrixCount];

         for (int i = 0; i < transformationMatrixCount; i++) {
            obj.transformationMatrix[i] = buf.getFloatLE(pos + i * 4);
         }

         pos += transformationMatrixCount * 4;
      }

      return obj;
   }

   public static int computeBytesConsumed(@Nonnull ByteBuf buf, int offset) {
      byte nullBits = buf.getByte(offset);
      int pos = offset + 52;
      if ((nullBits & 1) != 0) {
         int arrLen = VarInt.peek(buf, pos);
         pos += VarInt.length(buf, pos) + arrLen * 4;
      }

      return pos - offset;
   }

   @Override
   public void serialize(@Nonnull ByteBuf buf) {
      byte nullBits = 0;
      if (this.transformationMatrix != null) {
         nullBits = (byte)(nullBits | 1);
      }

      if (this.initialSelectionMin != null) {
         nullBits = (byte)(nullBits | 2);
      }

      if (this.initialSelectionMax != null) {
         nullBits = (byte)(nullBits | 4);
      }

      if (this.initialRotationOrigin != null) {
         nullBits = (byte)(nullBits | 8);
      }

      if (this.initialPastePointForClipboardPaste != null) {
         nullBits = (byte)(nullBits | 16);
      }

      buf.writeByte(nullBits);
      if (this.initialSelectionMin != null) {
         this.initialSelectionMin.serialize(buf);
      } else {
         buf.writeZero(12);
      }

      if (this.initialSelectionMax != null) {
         this.initialSelectionMax.serialize(buf);
      } else {
         buf.writeZero(12);
      }

      if (this.initialRotationOrigin != null) {
         this.initialRotationOrigin.serialize(buf);
      } else {
         buf.writeZero(12);
      }

      buf.writeByte(this.cutOriginal ? 1 : 0);
      buf.writeByte(this.applyTransformationToSelectionMinMax ? 1 : 0);
      buf.writeByte(this.isExitingTransformMode ? 1 : 0);
      if (this.initialPastePointForClipboardPaste != null) {
         this.initialPastePointForClipboardPaste.serialize(buf);
      } else {
         buf.writeZero(12);
      }

      if (this.transformationMatrix != null) {
         if (this.transformationMatrix.length > 4096000) {
            throw ProtocolException.arrayTooLong("TransformationMatrix", this.transformationMatrix.length, 4096000);
         }

         VarInt.write(buf, this.transformationMatrix.length);

         for (float item : this.transformationMatrix) {
            buf.writeFloatLE(item);
         }
      }
   }

   @Override
   public int computeSize() {
      int size = 52;
      if (this.transformationMatrix != null) {
         size += VarInt.size(this.transformationMatrix.length) + this.transformationMatrix.length * 4;
      }

      return size;
   }

   public static ValidationResult validateStructure(@Nonnull ByteBuf buffer, int offset) {
      if (buffer.readableBytes() - offset < 52) {
         return ValidationResult.error("Buffer too small: expected at least 52 bytes");
      } else {
         byte nullBits = buffer.getByte(offset);
         int pos = offset + 52;
         if ((nullBits & 1) != 0) {
            int transformationMatrixCount = VarInt.peek(buffer, pos);
            if (transformationMatrixCount < 0) {
               return ValidationResult.error("Invalid array count for TransformationMatrix");
            }

            if (transformationMatrixCount > 4096000) {
               return ValidationResult.error("TransformationMatrix exceeds max length 4096000");
            }

            pos += VarInt.length(buffer, pos);
            pos += transformationMatrixCount * 4;
            if (pos > buffer.writerIndex()) {
               return ValidationResult.error("Buffer overflow reading TransformationMatrix");
            }
         }

         return ValidationResult.OK;
      }
   }

   public BuilderToolSelectionTransform clone() {
      BuilderToolSelectionTransform copy = new BuilderToolSelectionTransform();
      copy.transformationMatrix = this.transformationMatrix != null ? Arrays.copyOf(this.transformationMatrix, this.transformationMatrix.length) : null;
      copy.initialSelectionMin = this.initialSelectionMin != null ? this.initialSelectionMin.clone() : null;
      copy.initialSelectionMax = this.initialSelectionMax != null ? this.initialSelectionMax.clone() : null;
      copy.initialRotationOrigin = this.initialRotationOrigin != null ? this.initialRotationOrigin.clone() : null;
      copy.cutOriginal = this.cutOriginal;
      copy.applyTransformationToSelectionMinMax = this.applyTransformationToSelectionMinMax;
      copy.isExitingTransformMode = this.isExitingTransformMode;
      copy.initialPastePointForClipboardPaste = this.initialPastePointForClipboardPaste != null ? this.initialPastePointForClipboardPaste.clone() : null;
      return copy;
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj) {
         return true;
      } else {
         return !(obj instanceof BuilderToolSelectionTransform other)
            ? false
            : Arrays.equals(this.transformationMatrix, other.transformationMatrix)
               && Objects.equals(this.initialSelectionMin, other.initialSelectionMin)
               && Objects.equals(this.initialSelectionMax, other.initialSelectionMax)
               && Objects.equals(this.initialRotationOrigin, other.initialRotationOrigin)
               && this.cutOriginal == other.cutOriginal
               && this.applyTransformationToSelectionMinMax == other.applyTransformationToSelectionMinMax
               && this.isExitingTransformMode == other.isExitingTransformMode
               && Objects.equals(this.initialPastePointForClipboardPaste, other.initialPastePointForClipboardPaste);
      }
   }

   @Override
   public int hashCode() {
      int result = 1;
      result = 31 * result + Arrays.hashCode(this.transformationMatrix);
      result = 31 * result + Objects.hashCode(this.initialSelectionMin);
      result = 31 * result + Objects.hashCode(this.initialSelectionMax);
      result = 31 * result + Objects.hashCode(this.initialRotationOrigin);
      result = 31 * result + Boolean.hashCode(this.cutOriginal);
      result = 31 * result + Boolean.hashCode(this.applyTransformationToSelectionMinMax);
      result = 31 * result + Boolean.hashCode(this.isExitingTransformMode);
      return 31 * result + Objects.hashCode(this.initialPastePointForClipboardPaste);
   }
}
