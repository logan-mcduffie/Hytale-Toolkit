package com.google.crypto.tink.proto;

import com.google.protobuf.Descriptors;
import com.google.protobuf.Internal;
import com.google.protobuf.ProtocolMessageEnum;
import com.google.protobuf.RuntimeVersion;

public enum SlhDsaSignatureType implements ProtocolMessageEnum {
   SLH_DSA_SIGNATURE_TYPE_UNSPECIFIED(0),
   FAST_SIGNING(1),
   SMALL_SIGNATURE(2),
   UNRECOGNIZED(-1);

   public static final int SLH_DSA_SIGNATURE_TYPE_UNSPECIFIED_VALUE = 0;
   public static final int FAST_SIGNING_VALUE = 1;
   public static final int SMALL_SIGNATURE_VALUE = 2;
   private static final Internal.EnumLiteMap<SlhDsaSignatureType> internalValueMap = new Internal.EnumLiteMap<SlhDsaSignatureType>() {
      public SlhDsaSignatureType findValueByNumber(int number) {
         return SlhDsaSignatureType.forNumber(number);
      }
   };
   private static final SlhDsaSignatureType[] VALUES = values();
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
   public static SlhDsaSignatureType valueOf(int value) {
      return forNumber(value);
   }

   public static SlhDsaSignatureType forNumber(int value) {
      switch (value) {
         case 0:
            return SLH_DSA_SIGNATURE_TYPE_UNSPECIFIED;
         case 1:
            return FAST_SIGNING;
         case 2:
            return SMALL_SIGNATURE;
         default:
            return null;
      }
   }

   public static Internal.EnumLiteMap<SlhDsaSignatureType> internalGetValueMap() {
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
      return SlhDsa.getDescriptor().getEnumTypes().get(1);
   }

   public static SlhDsaSignatureType valueOf(Descriptors.EnumValueDescriptor desc) {
      if (desc.getType() != getDescriptor()) {
         throw new IllegalArgumentException("EnumValueDescriptor is not for this type.");
      } else {
         return desc.getIndex() == -1 ? UNRECOGNIZED : VALUES[desc.getIndex()];
      }
   }

   private SlhDsaSignatureType(int value) {
      this.value = value;
   }

   static {
      RuntimeVersion.validateProtobufGencodeVersion(RuntimeVersion.RuntimeDomain.PUBLIC, 4, 32, 1, "", SlhDsaSignatureType.class.getName());
   }
}
