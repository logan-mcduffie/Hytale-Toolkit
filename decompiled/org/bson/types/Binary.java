package org.bson.types;

import java.io.Serializable;
import java.util.Arrays;
import org.bson.BsonBinarySubType;

public class Binary implements Serializable {
   private static final long serialVersionUID = 7902997490338209467L;
   private final byte type;
   private final byte[] data;

   public Binary(byte[] data) {
      this(BsonBinarySubType.BINARY, data);
   }

   public Binary(BsonBinarySubType type, byte[] data) {
      this(type.getValue(), data);
   }

   public Binary(byte type, byte[] data) {
      this.type = type;
      this.data = (byte[])data.clone();
   }

   public byte getType() {
      return this.type;
   }

   public byte[] getData() {
      return (byte[])this.data.clone();
   }

   public int length() {
      return this.data.length;
   }

   @Override
   public boolean equals(Object o) {
      if (this == o) {
         return true;
      } else if (o != null && this.getClass() == o.getClass()) {
         Binary binary = (Binary)o;
         return this.type != binary.type ? false : Arrays.equals(this.data, binary.data);
      } else {
         return false;
      }
   }

   @Override
   public int hashCode() {
      int result = this.type;
      return 31 * result + Arrays.hashCode(this.data);
   }
}
