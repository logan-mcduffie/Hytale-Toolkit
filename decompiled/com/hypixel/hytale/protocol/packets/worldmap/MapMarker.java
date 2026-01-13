package com.hypixel.hytale.protocol.packets.worldmap;

import com.hypixel.hytale.protocol.Transform;
import com.hypixel.hytale.protocol.io.PacketIO;
import com.hypixel.hytale.protocol.io.ProtocolException;
import com.hypixel.hytale.protocol.io.ValidationResult;
import com.hypixel.hytale.protocol.io.VarInt;
import io.netty.buffer.ByteBuf;
import java.util.Arrays;
import java.util.Objects;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class MapMarker {
   public static final int NULLABLE_BIT_FIELD_SIZE = 1;
   public static final int FIXED_BLOCK_SIZE = 38;
   public static final int VARIABLE_FIELD_COUNT = 4;
   public static final int VARIABLE_BLOCK_START = 54;
   public static final int MAX_SIZE = 1677721600;
   @Nullable
   public String id;
   @Nullable
   public String name;
   @Nullable
   public String markerImage;
   @Nullable
   public Transform transform;
   @Nullable
   public ContextMenuItem[] contextMenuItems;

   public MapMarker() {
   }

   public MapMarker(
      @Nullable String id, @Nullable String name, @Nullable String markerImage, @Nullable Transform transform, @Nullable ContextMenuItem[] contextMenuItems
   ) {
      this.id = id;
      this.name = name;
      this.markerImage = markerImage;
      this.transform = transform;
      this.contextMenuItems = contextMenuItems;
   }

   public MapMarker(@Nonnull MapMarker other) {
      this.id = other.id;
      this.name = other.name;
      this.markerImage = other.markerImage;
      this.transform = other.transform;
      this.contextMenuItems = other.contextMenuItems;
   }

   @Nonnull
   public static MapMarker deserialize(@Nonnull ByteBuf buf, int offset) {
      MapMarker obj = new MapMarker();
      byte nullBits = buf.getByte(offset);
      if ((nullBits & 8) != 0) {
         obj.transform = Transform.deserialize(buf, offset + 1);
      }

      if ((nullBits & 1) != 0) {
         int varPos0 = offset + 54 + buf.getIntLE(offset + 38);
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
         int varPos1 = offset + 54 + buf.getIntLE(offset + 42);
         int nameLen = VarInt.peek(buf, varPos1);
         if (nameLen < 0) {
            throw ProtocolException.negativeLength("Name", nameLen);
         }

         if (nameLen > 4096000) {
            throw ProtocolException.stringTooLong("Name", nameLen, 4096000);
         }

         obj.name = PacketIO.readVarString(buf, varPos1, PacketIO.UTF8);
      }

      if ((nullBits & 4) != 0) {
         int varPos2 = offset + 54 + buf.getIntLE(offset + 46);
         int markerImageLen = VarInt.peek(buf, varPos2);
         if (markerImageLen < 0) {
            throw ProtocolException.negativeLength("MarkerImage", markerImageLen);
         }

         if (markerImageLen > 4096000) {
            throw ProtocolException.stringTooLong("MarkerImage", markerImageLen, 4096000);
         }

         obj.markerImage = PacketIO.readVarString(buf, varPos2, PacketIO.UTF8);
      }

      if ((nullBits & 16) != 0) {
         int varPos3 = offset + 54 + buf.getIntLE(offset + 50);
         int contextMenuItemsCount = VarInt.peek(buf, varPos3);
         if (contextMenuItemsCount < 0) {
            throw ProtocolException.negativeLength("ContextMenuItems", contextMenuItemsCount);
         }

         if (contextMenuItemsCount > 4096000) {
            throw ProtocolException.arrayTooLong("ContextMenuItems", contextMenuItemsCount, 4096000);
         }

         int varIntLen = VarInt.length(buf, varPos3);
         if (varPos3 + varIntLen + contextMenuItemsCount * 0L > buf.readableBytes()) {
            throw ProtocolException.bufferTooSmall("ContextMenuItems", varPos3 + varIntLen + contextMenuItemsCount * 0, buf.readableBytes());
         }

         obj.contextMenuItems = new ContextMenuItem[contextMenuItemsCount];
         int elemPos = varPos3 + varIntLen;

         for (int i = 0; i < contextMenuItemsCount; i++) {
            obj.contextMenuItems[i] = ContextMenuItem.deserialize(buf, elemPos);
            elemPos += ContextMenuItem.computeBytesConsumed(buf, elemPos);
         }
      }

      return obj;
   }

   public static int computeBytesConsumed(@Nonnull ByteBuf buf, int offset) {
      byte nullBits = buf.getByte(offset);
      int maxEnd = 54;
      if ((nullBits & 1) != 0) {
         int fieldOffset0 = buf.getIntLE(offset + 38);
         int pos0 = offset + 54 + fieldOffset0;
         int sl = VarInt.peek(buf, pos0);
         pos0 += VarInt.length(buf, pos0) + sl;
         if (pos0 - offset > maxEnd) {
            maxEnd = pos0 - offset;
         }
      }

      if ((nullBits & 2) != 0) {
         int fieldOffset1 = buf.getIntLE(offset + 42);
         int pos1 = offset + 54 + fieldOffset1;
         int sl = VarInt.peek(buf, pos1);
         pos1 += VarInt.length(buf, pos1) + sl;
         if (pos1 - offset > maxEnd) {
            maxEnd = pos1 - offset;
         }
      }

      if ((nullBits & 4) != 0) {
         int fieldOffset2 = buf.getIntLE(offset + 46);
         int pos2 = offset + 54 + fieldOffset2;
         int sl = VarInt.peek(buf, pos2);
         pos2 += VarInt.length(buf, pos2) + sl;
         if (pos2 - offset > maxEnd) {
            maxEnd = pos2 - offset;
         }
      }

      if ((nullBits & 16) != 0) {
         int fieldOffset3 = buf.getIntLE(offset + 50);
         int pos3 = offset + 54 + fieldOffset3;
         int arrLen = VarInt.peek(buf, pos3);
         pos3 += VarInt.length(buf, pos3);

         for (int i = 0; i < arrLen; i++) {
            pos3 += ContextMenuItem.computeBytesConsumed(buf, pos3);
         }

         if (pos3 - offset > maxEnd) {
            maxEnd = pos3 - offset;
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

      if (this.name != null) {
         nullBits = (byte)(nullBits | 2);
      }

      if (this.markerImage != null) {
         nullBits = (byte)(nullBits | 4);
      }

      if (this.transform != null) {
         nullBits = (byte)(nullBits | 8);
      }

      if (this.contextMenuItems != null) {
         nullBits = (byte)(nullBits | 16);
      }

      buf.writeByte(nullBits);
      if (this.transform != null) {
         this.transform.serialize(buf);
      } else {
         buf.writeZero(37);
      }

      int idOffsetSlot = buf.writerIndex();
      buf.writeIntLE(0);
      int nameOffsetSlot = buf.writerIndex();
      buf.writeIntLE(0);
      int markerImageOffsetSlot = buf.writerIndex();
      buf.writeIntLE(0);
      int contextMenuItemsOffsetSlot = buf.writerIndex();
      buf.writeIntLE(0);
      int varBlockStart = buf.writerIndex();
      if (this.id != null) {
         buf.setIntLE(idOffsetSlot, buf.writerIndex() - varBlockStart);
         PacketIO.writeVarString(buf, this.id, 4096000);
      } else {
         buf.setIntLE(idOffsetSlot, -1);
      }

      if (this.name != null) {
         buf.setIntLE(nameOffsetSlot, buf.writerIndex() - varBlockStart);
         PacketIO.writeVarString(buf, this.name, 4096000);
      } else {
         buf.setIntLE(nameOffsetSlot, -1);
      }

      if (this.markerImage != null) {
         buf.setIntLE(markerImageOffsetSlot, buf.writerIndex() - varBlockStart);
         PacketIO.writeVarString(buf, this.markerImage, 4096000);
      } else {
         buf.setIntLE(markerImageOffsetSlot, -1);
      }

      if (this.contextMenuItems != null) {
         buf.setIntLE(contextMenuItemsOffsetSlot, buf.writerIndex() - varBlockStart);
         if (this.contextMenuItems.length > 4096000) {
            throw ProtocolException.arrayTooLong("ContextMenuItems", this.contextMenuItems.length, 4096000);
         }

         VarInt.write(buf, this.contextMenuItems.length);

         for (ContextMenuItem item : this.contextMenuItems) {
            item.serialize(buf);
         }
      } else {
         buf.setIntLE(contextMenuItemsOffsetSlot, -1);
      }
   }

   public int computeSize() {
      int size = 54;
      if (this.id != null) {
         size += PacketIO.stringSize(this.id);
      }

      if (this.name != null) {
         size += PacketIO.stringSize(this.name);
      }

      if (this.markerImage != null) {
         size += PacketIO.stringSize(this.markerImage);
      }

      if (this.contextMenuItems != null) {
         int contextMenuItemsSize = 0;

         for (ContextMenuItem elem : this.contextMenuItems) {
            contextMenuItemsSize += elem.computeSize();
         }

         size += VarInt.size(this.contextMenuItems.length) + contextMenuItemsSize;
      }

      return size;
   }

   public static ValidationResult validateStructure(@Nonnull ByteBuf buffer, int offset) {
      if (buffer.readableBytes() - offset < 54) {
         return ValidationResult.error("Buffer too small: expected at least 54 bytes");
      } else {
         byte nullBits = buffer.getByte(offset);
         if ((nullBits & 1) != 0) {
            int idOffset = buffer.getIntLE(offset + 38);
            if (idOffset < 0) {
               return ValidationResult.error("Invalid offset for Id");
            }

            int pos = offset + 54 + idOffset;
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
            int nameOffset = buffer.getIntLE(offset + 42);
            if (nameOffset < 0) {
               return ValidationResult.error("Invalid offset for Name");
            }

            int posx = offset + 54 + nameOffset;
            if (posx >= buffer.writerIndex()) {
               return ValidationResult.error("Offset out of bounds for Name");
            }

            int nameLen = VarInt.peek(buffer, posx);
            if (nameLen < 0) {
               return ValidationResult.error("Invalid string length for Name");
            }

            if (nameLen > 4096000) {
               return ValidationResult.error("Name exceeds max length 4096000");
            }

            posx += VarInt.length(buffer, posx);
            posx += nameLen;
            if (posx > buffer.writerIndex()) {
               return ValidationResult.error("Buffer overflow reading Name");
            }
         }

         if ((nullBits & 4) != 0) {
            int markerImageOffset = buffer.getIntLE(offset + 46);
            if (markerImageOffset < 0) {
               return ValidationResult.error("Invalid offset for MarkerImage");
            }

            int posxx = offset + 54 + markerImageOffset;
            if (posxx >= buffer.writerIndex()) {
               return ValidationResult.error("Offset out of bounds for MarkerImage");
            }

            int markerImageLen = VarInt.peek(buffer, posxx);
            if (markerImageLen < 0) {
               return ValidationResult.error("Invalid string length for MarkerImage");
            }

            if (markerImageLen > 4096000) {
               return ValidationResult.error("MarkerImage exceeds max length 4096000");
            }

            posxx += VarInt.length(buffer, posxx);
            posxx += markerImageLen;
            if (posxx > buffer.writerIndex()) {
               return ValidationResult.error("Buffer overflow reading MarkerImage");
            }
         }

         if ((nullBits & 16) != 0) {
            int contextMenuItemsOffset = buffer.getIntLE(offset + 50);
            if (contextMenuItemsOffset < 0) {
               return ValidationResult.error("Invalid offset for ContextMenuItems");
            }

            int posxxx = offset + 54 + contextMenuItemsOffset;
            if (posxxx >= buffer.writerIndex()) {
               return ValidationResult.error("Offset out of bounds for ContextMenuItems");
            }

            int contextMenuItemsCount = VarInt.peek(buffer, posxxx);
            if (contextMenuItemsCount < 0) {
               return ValidationResult.error("Invalid array count for ContextMenuItems");
            }

            if (contextMenuItemsCount > 4096000) {
               return ValidationResult.error("ContextMenuItems exceeds max length 4096000");
            }

            posxxx += VarInt.length(buffer, posxxx);

            for (int i = 0; i < contextMenuItemsCount; i++) {
               ValidationResult structResult = ContextMenuItem.validateStructure(buffer, posxxx);
               if (!structResult.isValid()) {
                  return ValidationResult.error("Invalid ContextMenuItem in ContextMenuItems[" + i + "]: " + structResult.error());
               }

               posxxx += ContextMenuItem.computeBytesConsumed(buffer, posxxx);
            }
         }

         return ValidationResult.OK;
      }
   }

   public MapMarker clone() {
      MapMarker copy = new MapMarker();
      copy.id = this.id;
      copy.name = this.name;
      copy.markerImage = this.markerImage;
      copy.transform = this.transform != null ? this.transform.clone() : null;
      copy.contextMenuItems = this.contextMenuItems != null ? Arrays.stream(this.contextMenuItems).map(e -> e.clone()).toArray(ContextMenuItem[]::new) : null;
      return copy;
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj) {
         return true;
      } else {
         return !(obj instanceof MapMarker other)
            ? false
            : Objects.equals(this.id, other.id)
               && Objects.equals(this.name, other.name)
               && Objects.equals(this.markerImage, other.markerImage)
               && Objects.equals(this.transform, other.transform)
               && Arrays.equals((Object[])this.contextMenuItems, (Object[])other.contextMenuItems);
      }
   }

   @Override
   public int hashCode() {
      int result = 1;
      result = 31 * result + Objects.hashCode(this.id);
      result = 31 * result + Objects.hashCode(this.name);
      result = 31 * result + Objects.hashCode(this.markerImage);
      result = 31 * result + Objects.hashCode(this.transform);
      return 31 * result + Arrays.hashCode((Object[])this.contextMenuItems);
   }
}
