package com.google.crypto.tink.proto;

import com.google.protobuf.Descriptors;
import com.google.protobuf.Internal;
import com.google.protobuf.ProtocolMessageEnum;
import com.google.protobuf.RuntimeVersion;

public enum JwtHmacAlgorithm implements ProtocolMessageEnum {
   HS_UNKNOWN(0),
   HS256(1),
   HS384(2),
   HS512(3),
   UNRECOGNIZED(-1);

   public static final int HS_UNKNOWN_VALUE = 0;
   public static final int HS256_VALUE = 1;
   public static final int HS384_VALUE = 2;
   public static final int HS512_VALUE = 3;
   private static final Internal.EnumLiteMap<JwtHmacAlgorithm> internalValueMap = new Internal.EnumLiteMap<JwtHmacAlgorithm>() {
      public JwtHmacAlgorithm findValueByNumber(int number) {
         return JwtHmacAlgorithm.forNumber(number);
      }
   };
   private static final JwtHmacAlgorithm[] VALUES = values();
   private final int value;

   @Override
   public final int getNumber() {
      if (this == UNRECOGNIZED) {
         throw new IllegalArgumentException("Can't get the number of an unknown enum value.");
      } else {
         return this.value;
      }
   }

   @Deprecated
   public static JwtHmacAlgorithm valueOf(int value) {
      return forNumber(value);
   }

   public static JwtHmacAlgorithm forNumber(int value) {
      switch (value) {
         case 0:
            return HS_UNKNOWN;
         case 1:
            return HS256;
         case 2:
            return HS384;
         case 3:
            return HS512;
         default:
            return null;
      }
   }

   public static Internal.EnumLiteMap<JwtHmacAlgorithm> internalGetValueMap() {
      return internalValueMap;
   }

   @Override
   public final Descriptors.EnumValueDescriptor getValueDescriptor() {
      if (this == UNRECOGNIZED) {
         throw new IllegalStateException("Can't get the descriptor of an unrecognized enum value.");
      } else {
         return getDescriptor().getValues().get(this.ordinal());
      }
   }

   @Override
   public final Descriptors.EnumDescriptor getDescriptorForType() {
      return getDescriptor();
   }

   public static Descriptors.EnumDescriptor getDescriptor() {
      return JwtHmac.getDescriptor().getEnumTypes().get(0);
   }

   public static JwtHmacAlgorithm valueOf(Descriptors.EnumValueDescriptor desc) {
      if (desc.getType() != getDescriptor()) {
         throw new IllegalArgumentException("EnumValueDescriptor is not for this type.");
      } else {
         return desc.getIndex() == -1 ? UNRECOGNIZED : VALUES[desc.getIndex()];
      }
   }

   private JwtHmacAlgorithm(int value) {
      this.value = value;
   }

   static {
      RuntimeVersion.validateProtobufGencodeVersion(RuntimeVersion.RuntimeDomain.PUBLIC, 4, 32, 1, "", JwtHmacAlgorithm.class.getName());
   }
}
