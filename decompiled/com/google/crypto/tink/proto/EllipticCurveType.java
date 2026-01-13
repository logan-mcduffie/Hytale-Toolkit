package com.google.crypto.tink.proto;

import com.google.protobuf.Descriptors;
import com.google.protobuf.Internal;
import com.google.protobuf.ProtocolMessageEnum;
import com.google.protobuf.RuntimeVersion;

public enum EllipticCurveType implements ProtocolMessageEnum {
   UNKNOWN_CURVE(0),
   NIST_P256(2),
   NIST_P384(3),
   NIST_P521(4),
   CURVE25519(5),
   UNRECOGNIZED(-1);

   public static final int UNKNOWN_CURVE_VALUE = 0;
   public static final int NIST_P256_VALUE = 2;
   public static final int NIST_P384_VALUE = 3;
   public static final int NIST_P521_VALUE = 4;
   public static final int CURVE25519_VALUE = 5;
   private static final Internal.EnumLiteMap<EllipticCurveType> internalValueMap = new Internal.EnumLiteMap<EllipticCurveType>() {
      public EllipticCurveType findValueByNumber(int number) {
         return EllipticCurveType.forNumber(number);
      }
   };
   private static final EllipticCurveType[] VALUES = values();
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
   public static EllipticCurveType valueOf(int value) {
      return forNumber(value);
   }

   public static EllipticCurveType forNumber(int value) {
      switch (value) {
         case 0:
            return UNKNOWN_CURVE;
         case 1:
         default:
            return null;
         case 2:
            return NIST_P256;
         case 3:
            return NIST_P384;
         case 4:
            return NIST_P521;
         case 5:
            return CURVE25519;
      }
   }

   public static Internal.EnumLiteMap<EllipticCurveType> internalGetValueMap() {
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
      return Common.getDescriptor().getEnumTypes().get(0);
   }

   public static EllipticCurveType valueOf(Descriptors.EnumValueDescriptor desc) {
      if (desc.getType() != getDescriptor()) {
         throw new IllegalArgumentException("EnumValueDescriptor is not for this type.");
      } else {
         return desc.getIndex() == -1 ? UNRECOGNIZED : VALUES[desc.getIndex()];
      }
   }

   private EllipticCurveType(int value) {
      this.value = value;
   }

   static {
      RuntimeVersion.validateProtobufGencodeVersion(RuntimeVersion.RuntimeDomain.PUBLIC, 4, 32, 1, "", EllipticCurveType.class.getName());
   }
}
