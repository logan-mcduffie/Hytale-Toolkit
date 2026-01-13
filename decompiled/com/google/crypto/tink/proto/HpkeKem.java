package com.google.crypto.tink.proto;

import com.google.protobuf.Descriptors;
import com.google.protobuf.Internal;
import com.google.protobuf.ProtocolMessageEnum;
import com.google.protobuf.RuntimeVersion;

public enum HpkeKem implements ProtocolMessageEnum {
   KEM_UNKNOWN(0),
   DHKEM_X25519_HKDF_SHA256(1),
   DHKEM_P256_HKDF_SHA256(2),
   DHKEM_P384_HKDF_SHA384(3),
   DHKEM_P521_HKDF_SHA512(4),
   X_WING(5),
   ML_KEM768(6),
   ML_KEM1024(7),
   UNRECOGNIZED(-1);

   public static final int KEM_UNKNOWN_VALUE = 0;
   public static final int DHKEM_X25519_HKDF_SHA256_VALUE = 1;
   public static final int DHKEM_P256_HKDF_SHA256_VALUE = 2;
   public static final int DHKEM_P384_HKDF_SHA384_VALUE = 3;
   public static final int DHKEM_P521_HKDF_SHA512_VALUE = 4;
   public static final int X_WING_VALUE = 5;
   public static final int ML_KEM768_VALUE = 6;
   public static final int ML_KEM1024_VALUE = 7;
   private static final Internal.EnumLiteMap<HpkeKem> internalValueMap = new Internal.EnumLiteMap<HpkeKem>() {
      public HpkeKem findValueByNumber(int number) {
         return HpkeKem.forNumber(number);
      }
   };
   private static final HpkeKem[] VALUES = values();
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
   public static HpkeKem valueOf(int value) {
      return forNumber(value);
   }

   public static HpkeKem forNumber(int value) {
      switch (value) {
         case 0:
            return KEM_UNKNOWN;
         case 1:
            return DHKEM_X25519_HKDF_SHA256;
         case 2:
            return DHKEM_P256_HKDF_SHA256;
         case 3:
            return DHKEM_P384_HKDF_SHA384;
         case 4:
            return DHKEM_P521_HKDF_SHA512;
         case 5:
            return X_WING;
         case 6:
            return ML_KEM768;
         case 7:
            return ML_KEM1024;
         default:
            return null;
      }
   }

   public static Internal.EnumLiteMap<HpkeKem> internalGetValueMap() {
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
      return Hpke.getDescriptor().getEnumTypes().get(0);
   }

   public static HpkeKem valueOf(Descriptors.EnumValueDescriptor desc) {
      if (desc.getType() != getDescriptor()) {
         throw new IllegalArgumentException("EnumValueDescriptor is not for this type.");
      } else {
         return desc.getIndex() == -1 ? UNRECOGNIZED : VALUES[desc.getIndex()];
      }
   }

   private HpkeKem(int value) {
      this.value = value;
   }

   static {
      RuntimeVersion.validateProtobufGencodeVersion(RuntimeVersion.RuntimeDomain.PUBLIC, 4, 32, 1, "", HpkeKem.class.getName());
   }
}
