package com.google.crypto.tink.proto;

import com.google.protobuf.Descriptors;
import com.google.protobuf.Internal;
import com.google.protobuf.ProtocolMessageEnum;
import com.google.protobuf.RuntimeVersion;

public enum JwtRsaSsaPkcs1Algorithm implements ProtocolMessageEnum {
   RS_UNKNOWN(0),
   RS256(1),
   RS384(2),
   RS512(3),
   UNRECOGNIZED(-1);

   public static final int RS_UNKNOWN_VALUE = 0;
   public static final int RS256_VALUE = 1;
   public static final int RS384_VALUE = 2;
   public static final int RS512_VALUE = 3;
   private static final Internal.EnumLiteMap<JwtRsaSsaPkcs1Algorithm> internalValueMap = new Internal.EnumLiteMap<JwtRsaSsaPkcs1Algorithm>() {
      public JwtRsaSsaPkcs1Algorithm findValueByNumber(int number) {
         return JwtRsaSsaPkcs1Algorithm.forNumber(number);
      }
   };
   private static final JwtRsaSsaPkcs1Algorithm[] VALUES = values();
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
   public static JwtRsaSsaPkcs1Algorithm valueOf(int value) {
      return forNumber(value);
   }

   public static JwtRsaSsaPkcs1Algorithm forNumber(int value) {
      switch (value) {
         case 0:
            return RS_UNKNOWN;
         case 1:
            return RS256;
         case 2:
            return RS384;
         case 3:
            return RS512;
         default:
            return null;
      }
   }

   public static Internal.EnumLiteMap<JwtRsaSsaPkcs1Algorithm> internalGetValueMap() {
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
      return JwtRsaSsaPkcs1.getDescriptor().getEnumTypes().get(0);
   }

   public static JwtRsaSsaPkcs1Algorithm valueOf(Descriptors.EnumValueDescriptor desc) {
      if (desc.getType() != getDescriptor()) {
         throw new IllegalArgumentException("EnumValueDescriptor is not for this type.");
      } else {
         return desc.getIndex() == -1 ? UNRECOGNIZED : VALUES[desc.getIndex()];
      }
   }

   private JwtRsaSsaPkcs1Algorithm(int value) {
      this.value = value;
   }

   static {
      RuntimeVersion.validateProtobufGencodeVersion(RuntimeVersion.RuntimeDomain.PUBLIC, 4, 32, 1, "", JwtRsaSsaPkcs1Algorithm.class.getName());
   }
}
