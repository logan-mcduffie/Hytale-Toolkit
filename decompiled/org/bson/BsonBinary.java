package org.bson;

import java.util.Arrays;
import java.util.UUID;
import org.bson.assertions.Assertions;
import org.bson.internal.UuidHelper;

public class BsonBinary extends BsonValue {
   private final byte type;
   private final byte[] data;

   public BsonBinary(byte[] data) {
      this(BsonBinarySubType.BINARY, data);
   }

   public BsonBinary(BsonBinarySubType type, byte[] data) {
      if (type == null) {
         throw new IllegalArgumentException("type may not be null");
      } else if (data == null) {
         throw new IllegalArgumentException("data may not be null");
      } else {
         this.type = type.getValue();
         this.data = data;
      }
   }

   public BsonBinary(byte type, byte[] data) {
      if (data == null) {
         throw new IllegalArgumentException("data may not be null");
      } else {
         this.type = type;
         this.data = data;
      }
   }

   public BsonBinary(UUID uuid) {
      this(uuid, UuidRepresentation.STANDARD);
   }

   public BsonBinary(UUID uuid, UuidRepresentation uuidRepresentation) {
      if (uuid == null) {
         throw new IllegalArgumentException("uuid may not be null");
      } else if (uuidRepresentation == null) {
         throw new IllegalArgumentException("uuidRepresentation may not be null");
      } else {
         this.data = UuidHelper.encodeUuidToBinary(uuid, uuidRepresentation);
         this.type = uuidRepresentation == UuidRepresentation.STANDARD ? BsonBinarySubType.UUID_STANDARD.getValue() : BsonBinarySubType.UUID_LEGACY.getValue();
      }
   }

   public UUID asUuid() {
      if (!BsonBinarySubType.isUuid(this.type)) {
         throw new BsonInvalidOperationException("type must be a UUID subtype.");
      } else if (this.type != BsonBinarySubType.UUID_STANDARD.getValue()) {
         throw new BsonInvalidOperationException("uuidRepresentation must be set to return the correct UUID.");
      } else {
         return UuidHelper.decodeBinaryToUuid((byte[])this.data.clone(), this.type, UuidRepresentation.STANDARD);
      }
   }

   public UUID asUuid(UuidRepresentation uuidRepresentation) {
      Assertions.notNull("uuidRepresentation", uuidRepresentation);
      byte uuidType = uuidRepresentation == UuidRepresentation.STANDARD ? BsonBinarySubType.UUID_STANDARD.getValue() : BsonBinarySubType.UUID_LEGACY.getValue();
      if (this.type != uuidType) {
         throw new BsonInvalidOperationException("uuidRepresentation does not match current uuidRepresentation.");
      } else {
         return UuidHelper.decodeBinaryToUuid((byte[])this.data.clone(), this.type, uuidRepresentation);
      }
   }

   @Override
   public BsonType getBsonType() {
      return BsonType.BINARY;
   }

   public byte getType() {
      return this.type;
   }

   public byte[] getData() {
      return this.data;
   }

   @Override
   public boolean equals(Object o) {
      if (this == o) {
         return true;
      } else if (o != null && this.getClass() == o.getClass()) {
         BsonBinary that = (BsonBinary)o;
         return !Arrays.equals(this.data, that.data) ? false : this.type == that.type;
      } else {
         return false;
      }
   }

   @Override
   public int hashCode() {
      int result = this.type;
      return 31 * result + Arrays.hashCode(this.data);
   }

   @Override
   public String toString() {
      return "BsonBinary{type=" + this.type + ", data=" + Arrays.toString(this.data) + '}';
   }

   static BsonBinary clone(BsonBinary from) {
      return new BsonBinary(from.type, (byte[])from.data.clone());
   }
}
