package com.google.crypto.tink.proto;

import com.google.protobuf.Descriptors;
import com.google.protobuf.Internal;
import com.google.protobuf.ProtocolMessageEnum;
import com.google.protobuf.RuntimeVersion;

public enum HpkeKdf implements ProtocolMessageEnum {
   KDF_UNKNOWN(0),
   HKDF_SHA256(1),
   HKDF_SHA384(2),
   HKDF_SHA512(3),
   UNRECOGNIZED(-1);

   public static final int KDF_UNKNOWN_VALUE = 0;
   public static final int HKDF_SHA256_VALUE = 1;
   public static final int HKDF_SHA384_VALUE = 2;
   public static final int HKDF_SHA512_VALUE = 3;
   private static final Internal.EnumLiteMap<HpkeKdf> internalValueMap = new Internal.EnumLiteMap<HpkeKdf>() {
      public HpkeKdf findValueByNumber(int number) {
         return HpkeKdf.forNumber(number);
      }
   };
   private static final HpkeKdf[] VALUES = values();
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
   public static HpkeKdf valueOf(int value) {
      return forNumber(value);
   }

   public static HpkeKdf forNumber(int value) {
      switch (value) {
         case 0:
            return KDF_UNKNOWN;
         case 1:
            return HKDF_SHA256;
         case 2:
            return HKDF_SHA384;
         case 3:
            return HKDF_SHA512;
         default:
            return null;
      }
   }

   public static Internal.EnumLiteMap<HpkeKdf> internalGetValueMap() {
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
      return Hpke.getDescriptor().getEnumTypes().get(1);
   }

   public static HpkeKdf valueOf(Descriptors.EnumValueDescriptor desc) {
      if (desc.getType() != getDescriptor()) {
         throw new IllegalArgumentException("EnumValueDescriptor is not for this type.");
      } else {
         return desc.getIndex() == -1 ? UNRECOGNIZED : VALUES[desc.getIndex()];
      }
   }

   private HpkeKdf(int value) {
      this.value = value;
   }

   static {
      RuntimeVersion.validateProtobufGencodeVersion(RuntimeVersion.RuntimeDomain.PUBLIC, 4, 32, 1, "", HpkeKdf.class.getName());
   }
}
