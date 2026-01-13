package com.hypixel.hytale.protocol.packets.connection;

import com.hypixel.hytale.protocol.HostAddress;
import com.hypixel.hytale.protocol.Packet;
import com.hypixel.hytale.protocol.io.PacketIO;
import com.hypixel.hytale.protocol.io.ProtocolException;
import com.hypixel.hytale.protocol.io.ValidationResult;
import com.hypixel.hytale.protocol.io.VarInt;
import io.netty.buffer.ByteBuf;
import java.util.Arrays;
import java.util.Objects;
import java.util.UUID;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class Connect implements Packet {
   public static final int PACKET_ID = 0;
   public static final boolean IS_COMPRESSED = false;
   public static final int NULLABLE_BIT_FIELD_SIZE = 1;
   public static final int FIXED_BLOCK_SIZE = 82;
   public static final int VARIABLE_FIELD_COUNT = 5;
   public static final int VARIABLE_BLOCK_START = 102;
   public static final int MAX_SIZE = 38161;
   @Nonnull
   public String protocolHash = "";
   @Nonnull
   public ClientType clientType = ClientType.Game;
   @Nullable
   public String language;
   @Nullable
   public String identityToken;
   @Nonnull
   public UUID uuid = new UUID(0L, 0L);
   @Nonnull
   public String username = "";
   @Nullable
   public byte[] referralData;
   @Nullable
   public HostAddress referralSource;

   @Override
   public int getId() {
      return 0;
   }

   public Connect() {
   }

   public Connect(
      @Nonnull String protocolHash,
      @Nonnull ClientType clientType,
      @Nullable String language,
      @Nullable String identityToken,
      @Nonnull UUID uuid,
      @Nonnull String username,
      @Nullable byte[] referralData,
      @Nullable HostAddress referralSource
   ) {
      this.protocolHash = protocolHash;
      this.clientType = clientType;
      this.language = language;
      this.identityToken = identityToken;
      this.uuid = uuid;
      this.username = username;
      this.referralData = referralData;
      this.referralSource = referralSource;
   }

   public Connect(@Nonnull Connect other) {
      this.protocolHash = other.protocolHash;
      this.clientType = other.clientType;
      this.language = other.language;
      this.identityToken = other.identityToken;
      this.uuid = other.uuid;
      this.username = other.username;
      this.referralData = other.referralData;
      this.referralSource = other.referralSource;
   }

   @Nonnull
   public static Connect deserialize(@Nonnull ByteBuf buf, int offset) {
      Connect obj = new Connect();
      byte nullBits = buf.getByte(offset);
      obj.protocolHash = PacketIO.readFixedAsciiString(buf, offset + 1, 64);
      obj.clientType = ClientType.fromValue(buf.getByte(offset + 65));
      obj.uuid = PacketIO.readUUID(buf, offset + 66);
      if ((nullBits & 1) != 0) {
         int varPos0 = offset + 102 + buf.getIntLE(offset + 82);
         int languageLen = VarInt.peek(buf, varPos0);
         if (languageLen < 0) {
            throw ProtocolException.negativeLength("Language", languageLen);
         }

         if (languageLen > 128) {
            throw ProtocolException.stringTooLong("Language", languageLen, 128);
         }

         obj.language = PacketIO.readVarString(buf, varPos0, PacketIO.ASCII);
      }

      if ((nullBits & 2) != 0) {
         int varPos1 = offset + 102 + buf.getIntLE(offset + 86);
         int identityTokenLen = VarInt.peek(buf, varPos1);
         if (identityTokenLen < 0) {
            throw ProtocolException.negativeLength("IdentityToken", identityTokenLen);
         }

         if (identityTokenLen > 8192) {
            throw ProtocolException.stringTooLong("IdentityToken", identityTokenLen, 8192);
         }

         obj.identityToken = PacketIO.readVarString(buf, varPos1, PacketIO.UTF8);
      }

      int varPos2 = offset + 102 + buf.getIntLE(offset + 90);
      int usernameLen = VarInt.peek(buf, varPos2);
      if (usernameLen < 0) {
         throw ProtocolException.negativeLength("Username", usernameLen);
      } else if (usernameLen > 16) {
         throw ProtocolException.stringTooLong("Username", usernameLen, 16);
      } else {
         obj.username = PacketIO.readVarString(buf, varPos2, PacketIO.ASCII);
         if ((nullBits & 4) != 0) {
            varPos2 = offset + 102 + buf.getIntLE(offset + 94);
            usernameLen = VarInt.peek(buf, varPos2);
            if (usernameLen < 0) {
               throw ProtocolException.negativeLength("ReferralData", usernameLen);
            }

            if (usernameLen > 4096) {
               throw ProtocolException.arrayTooLong("ReferralData", usernameLen, 4096);
            }

            int varIntLen = VarInt.length(buf, varPos2);
            if (varPos2 + varIntLen + usernameLen * 1L > buf.readableBytes()) {
               throw ProtocolException.bufferTooSmall("ReferralData", varPos2 + varIntLen + usernameLen * 1, buf.readableBytes());
            }

            obj.referralData = new byte[usernameLen];

            for (int i = 0; i < usernameLen; i++) {
               obj.referralData[i] = buf.getByte(varPos2 + varIntLen + i * 1);
            }
         }

         if ((nullBits & 8) != 0) {
            varPos2 = offset + 102 + buf.getIntLE(offset + 98);
            obj.referralSource = HostAddress.deserialize(buf, varPos2);
         }

         return obj;
      }
   }

   public static int computeBytesConsumed(@Nonnull ByteBuf buf, int offset) {
      byte nullBits = buf.getByte(offset);
      int maxEnd = 102;
      if ((nullBits & 1) != 0) {
         int fieldOffset0 = buf.getIntLE(offset + 82);
         int pos0 = offset + 102 + fieldOffset0;
         int sl = VarInt.peek(buf, pos0);
         pos0 += VarInt.length(buf, pos0) + sl;
         if (pos0 - offset > maxEnd) {
            maxEnd = pos0 - offset;
         }
      }

      if ((nullBits & 2) != 0) {
         int fieldOffset1 = buf.getIntLE(offset + 86);
         int pos1 = offset + 102 + fieldOffset1;
         int sl = VarInt.peek(buf, pos1);
         pos1 += VarInt.length(buf, pos1) + sl;
         if (pos1 - offset > maxEnd) {
            maxEnd = pos1 - offset;
         }
      }

      int fieldOffset2 = buf.getIntLE(offset + 90);
      int pos2 = offset + 102 + fieldOffset2;
      int sl = VarInt.peek(buf, pos2);
      pos2 += VarInt.length(buf, pos2) + sl;
      if (pos2 - offset > maxEnd) {
         maxEnd = pos2 - offset;
      }

      if ((nullBits & 4) != 0) {
         fieldOffset2 = buf.getIntLE(offset + 94);
         pos2 = offset + 102 + fieldOffset2;
         sl = VarInt.peek(buf, pos2);
         pos2 += VarInt.length(buf, pos2) + sl * 1;
         if (pos2 - offset > maxEnd) {
            maxEnd = pos2 - offset;
         }
      }

      if ((nullBits & 8) != 0) {
         fieldOffset2 = buf.getIntLE(offset + 98);
         pos2 = offset + 102 + fieldOffset2;
         pos2 += HostAddress.computeBytesConsumed(buf, pos2);
         if (pos2 - offset > maxEnd) {
            maxEnd = pos2 - offset;
         }
      }

      return maxEnd;
   }

   @Override
   public void serialize(@Nonnull ByteBuf buf) {
      int startPos = buf.writerIndex();
      byte nullBits = 0;
      if (this.language != null) {
         nullBits = (byte)(nullBits | 1);
      }

      if (this.identityToken != null) {
         nullBits = (byte)(nullBits | 2);
      }

      if (this.referralData != null) {
         nullBits = (byte)(nullBits | 4);
      }

      if (this.referralSource != null) {
         nullBits = (byte)(nullBits | 8);
      }

      buf.writeByte(nullBits);
      PacketIO.writeFixedAsciiString(buf, this.protocolHash, 64);
      buf.writeByte(this.clientType.getValue());
      PacketIO.writeUUID(buf, this.uuid);
      int languageOffsetSlot = buf.writerIndex();
      buf.writeIntLE(0);
      int identityTokenOffsetSlot = buf.writerIndex();
      buf.writeIntLE(0);
      int usernameOffsetSlot = buf.writerIndex();
      buf.writeIntLE(0);
      int referralDataOffsetSlot = buf.writerIndex();
      buf.writeIntLE(0);
      int referralSourceOffsetSlot = buf.writerIndex();
      buf.writeIntLE(0);
      int varBlockStart = buf.writerIndex();
      if (this.language != null) {
         buf.setIntLE(languageOffsetSlot, buf.writerIndex() - varBlockStart);
         PacketIO.writeVarAsciiString(buf, this.language, 128);
      } else {
         buf.setIntLE(languageOffsetSlot, -1);
      }

      if (this.identityToken != null) {
         buf.setIntLE(identityTokenOffsetSlot, buf.writerIndex() - varBlockStart);
         PacketIO.writeVarString(buf, this.identityToken, 8192);
      } else {
         buf.setIntLE(identityTokenOffsetSlot, -1);
      }

      buf.setIntLE(usernameOffsetSlot, buf.writerIndex() - varBlockStart);
      PacketIO.writeVarAsciiString(buf, this.username, 16);
      if (this.referralData != null) {
         buf.setIntLE(referralDataOffsetSlot, buf.writerIndex() - varBlockStart);
         if (this.referralData.length > 4096) {
            throw ProtocolException.arrayTooLong("ReferralData", this.referralData.length, 4096);
         }

         VarInt.write(buf, this.referralData.length);

         for (byte item : this.referralData) {
            buf.writeByte(item);
         }
      } else {
         buf.setIntLE(referralDataOffsetSlot, -1);
      }

      if (this.referralSource != null) {
         buf.setIntLE(referralSourceOffsetSlot, buf.writerIndex() - varBlockStart);
         this.referralSource.serialize(buf);
      } else {
         buf.setIntLE(referralSourceOffsetSlot, -1);
      }
   }

   @Override
   public int computeSize() {
      int size = 102;
      if (this.language != null) {
         size += VarInt.size(this.language.length()) + this.language.length();
      }

      if (this.identityToken != null) {
         size += PacketIO.stringSize(this.identityToken);
      }

      size += VarInt.size(this.username.length()) + this.username.length();
      if (this.referralData != null) {
         size += VarInt.size(this.referralData.length) + this.referralData.length * 1;
      }

      if (this.referralSource != null) {
         size += this.referralSource.computeSize();
      }

      return size;
   }

   public static ValidationResult validateStructure(@Nonnull ByteBuf buffer, int offset) {
      if (buffer.readableBytes() - offset < 102) {
         return ValidationResult.error("Buffer too small: expected at least 102 bytes");
      } else {
         byte nullBits = buffer.getByte(offset);
         if ((nullBits & 1) != 0) {
            int languageOffset = buffer.getIntLE(offset + 82);
            if (languageOffset < 0) {
               return ValidationResult.error("Invalid offset for Language");
            }

            int pos = offset + 102 + languageOffset;
            if (pos >= buffer.writerIndex()) {
               return ValidationResult.error("Offset out of bounds for Language");
            }

            int languageLen = VarInt.peek(buffer, pos);
            if (languageLen < 0) {
               return ValidationResult.error("Invalid string length for Language");
            }

            if (languageLen > 128) {
               return ValidationResult.error("Language exceeds max length 128");
            }

            pos += VarInt.length(buffer, pos);
            pos += languageLen;
            if (pos > buffer.writerIndex()) {
               return ValidationResult.error("Buffer overflow reading Language");
            }
         }

         if ((nullBits & 2) != 0) {
            int identityTokenOffset = buffer.getIntLE(offset + 86);
            if (identityTokenOffset < 0) {
               return ValidationResult.error("Invalid offset for IdentityToken");
            }

            int posx = offset + 102 + identityTokenOffset;
            if (posx >= buffer.writerIndex()) {
               return ValidationResult.error("Offset out of bounds for IdentityToken");
            }

            int identityTokenLen = VarInt.peek(buffer, posx);
            if (identityTokenLen < 0) {
               return ValidationResult.error("Invalid string length for IdentityToken");
            }

            if (identityTokenLen > 8192) {
               return ValidationResult.error("IdentityToken exceeds max length 8192");
            }

            posx += VarInt.length(buffer, posx);
            posx += identityTokenLen;
            if (posx > buffer.writerIndex()) {
               return ValidationResult.error("Buffer overflow reading IdentityToken");
            }
         }

         int usernameOffset = buffer.getIntLE(offset + 90);
         if (usernameOffset < 0) {
            return ValidationResult.error("Invalid offset for Username");
         } else {
            int posxx = offset + 102 + usernameOffset;
            if (posxx >= buffer.writerIndex()) {
               return ValidationResult.error("Offset out of bounds for Username");
            } else {
               int usernameLen = VarInt.peek(buffer, posxx);
               if (usernameLen < 0) {
                  return ValidationResult.error("Invalid string length for Username");
               } else if (usernameLen > 16) {
                  return ValidationResult.error("Username exceeds max length 16");
               } else {
                  posxx += VarInt.length(buffer, posxx);
                  posxx += usernameLen;
                  if (posxx > buffer.writerIndex()) {
                     return ValidationResult.error("Buffer overflow reading Username");
                  } else {
                     if ((nullBits & 4) != 0) {
                        usernameOffset = buffer.getIntLE(offset + 94);
                        if (usernameOffset < 0) {
                           return ValidationResult.error("Invalid offset for ReferralData");
                        }

                        posxx = offset + 102 + usernameOffset;
                        if (posxx >= buffer.writerIndex()) {
                           return ValidationResult.error("Offset out of bounds for ReferralData");
                        }

                        usernameLen = VarInt.peek(buffer, posxx);
                        if (usernameLen < 0) {
                           return ValidationResult.error("Invalid array count for ReferralData");
                        }

                        if (usernameLen > 4096) {
                           return ValidationResult.error("ReferralData exceeds max length 4096");
                        }

                        posxx += VarInt.length(buffer, posxx);
                        posxx += usernameLen * 1;
                        if (posxx > buffer.writerIndex()) {
                           return ValidationResult.error("Buffer overflow reading ReferralData");
                        }
                     }

                     if ((nullBits & 8) != 0) {
                        usernameOffset = buffer.getIntLE(offset + 98);
                        if (usernameOffset < 0) {
                           return ValidationResult.error("Invalid offset for ReferralSource");
                        }

                        posxx = offset + 102 + usernameOffset;
                        if (posxx >= buffer.writerIndex()) {
                           return ValidationResult.error("Offset out of bounds for ReferralSource");
                        }

                        ValidationResult referralSourceResult = HostAddress.validateStructure(buffer, posxx);
                        if (!referralSourceResult.isValid()) {
                           return ValidationResult.error("Invalid ReferralSource: " + referralSourceResult.error());
                        }

                        posxx += HostAddress.computeBytesConsumed(buffer, posxx);
                     }

                     return ValidationResult.OK;
                  }
               }
            }
         }
      }
   }

   public Connect clone() {
      Connect copy = new Connect();
      copy.protocolHash = this.protocolHash;
      copy.clientType = this.clientType;
      copy.language = this.language;
      copy.identityToken = this.identityToken;
      copy.uuid = this.uuid;
      copy.username = this.username;
      copy.referralData = this.referralData != null ? Arrays.copyOf(this.referralData, this.referralData.length) : null;
      copy.referralSource = this.referralSource != null ? this.referralSource.clone() : null;
      return copy;
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj) {
         return true;
      } else {
         return !(obj instanceof Connect other)
            ? false
            : Objects.equals(this.protocolHash, other.protocolHash)
               && Objects.equals(this.clientType, other.clientType)
               && Objects.equals(this.language, other.language)
               && Objects.equals(this.identityToken, other.identityToken)
               && Objects.equals(this.uuid, other.uuid)
               && Objects.equals(this.username, other.username)
               && Arrays.equals(this.referralData, other.referralData)
               && Objects.equals(this.referralSource, other.referralSource);
      }
   }

   @Override
   public int hashCode() {
      int result = 1;
      result = 31 * result + Objects.hashCode(this.protocolHash);
      result = 31 * result + Objects.hashCode(this.clientType);
      result = 31 * result + Objects.hashCode(this.language);
      result = 31 * result + Objects.hashCode(this.identityToken);
      result = 31 * result + Objects.hashCode(this.uuid);
      result = 31 * result + Objects.hashCode(this.username);
      result = 31 * result + Arrays.hashCode(this.referralData);
      return 31 * result + Objects.hashCode(this.referralSource);
   }
}
