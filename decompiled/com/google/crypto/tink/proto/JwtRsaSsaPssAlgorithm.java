package com.google.crypto.tink.proto;

import com.google.protobuf.Descriptors;
import com.google.protobuf.Internal;
import com.google.protobuf.ProtocolMessageEnum;
import com.google.protobuf.RuntimeVersion;

public enum JwtRsaSsaPssAlgorithm implements ProtocolMessageEnum {
   PS_UNKNOWN(0),
   PS256(1),
   PS384(2),
   PS512(3),
   UNRECOGNIZED(-1);

   public static final int PS_UNKNOWN_VALUE = 0;
   public static final int PS256_VALUE = 1;
   public static final int PS384_VALUE = 2;
   public static final int PS512_VALUE = 3;
   private static final Internal.EnumLiteMap<JwtRsaSsaPssAlgorithm> internalValueMap = new Internal.EnumLiteMap<JwtRsaSsaPssAlgorithm>() {
      public JwtRsaSsaPssAlgorithm findValueByNumber(int number) {
         return JwtRsaSsaPssAlgorithm.forNumber(number);
      }
   };
   private static final JwtRsaSsaPssAlgorithm[] VALUES = values();
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
   public static JwtRsaSsaPssAlgorithm valueOf(int value) {
      return forNumber(value);
   }

   public static JwtRsaSsaPssAlgorithm forNumber(int value) {
      switch (value) {
         case 0:
            return PS_UNKNOWN;
         case 1:
            return PS256;
         case 2:
            return PS384;
         case 3:
            return PS512;
         default:
            return null;
      }
   }

   public static Internal.EnumLiteMap<JwtRsaSsaPssAlgorithm> internalGetValueMap() {
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
      return JwtRsaSsaPss.getDescriptor().getEnumTypes().get(0);
   }

   public static JwtRsaSsaPssAlgorithm valueOf(Descriptors.EnumValueDescriptor desc) {
      if (desc.getType() != getDescriptor()) {
         throw new IllegalArgumentException("EnumValueDescriptor is not for this type.");
      } else {
         return desc.getIndex() == -1 ? UNRECOGNIZED : VALUES[desc.getIndex()];
      }
   }

   private JwtRsaSsaPssAlgorithm(int value) {
      this.value = value;
   }

   static {
      RuntimeVersion.validateProtobufGencodeVersion(RuntimeVersion.RuntimeDomain.PUBLIC, 4, 32, 1, "", JwtRsaSsaPssAlgorithm.class.getName());
   }
}
