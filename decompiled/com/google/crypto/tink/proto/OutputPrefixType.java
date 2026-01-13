package com.google.crypto.tink.proto;

import com.google.protobuf.Descriptors;
import com.google.protobuf.Internal;
import com.google.protobuf.ProtocolMessageEnum;
import com.google.protobuf.RuntimeVersion;

public enum OutputPrefixType implements ProtocolMessageEnum {
   UNKNOWN_PREFIX(0),
   TINK(1),
   LEGACY(2),
   RAW(3),
   CRUNCHY(4),
   WITH_ID_REQUIREMENT(5),
   UNRECOGNIZED(-1);

   public static final int UNKNOWN_PREFIX_VALUE = 0;
   public static final int TINK_VALUE = 1;
   public static final int LEGACY_VALUE = 2;
   public static final int RAW_VALUE = 3;
   public static final int CRUNCHY_VALUE = 4;
   public static final int WITH_ID_REQUIREMENT_VALUE = 5;
   private static final Internal.EnumLiteMap<OutputPrefixType> internalValueMap = new Internal.EnumLiteMap<OutputPrefixType>() {
      public OutputPrefixType findValueByNumber(int number) {
         return OutputPrefixType.forNumber(number);
      }
   };
   private static final OutputPrefixType[] VALUES = values();
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
   public static OutputPrefixType valueOf(int value) {
      return forNumber(value);
   }

   public static OutputPrefixType forNumber(int value) {
      switch (value) {
         case 0:
            return UNKNOWN_PREFIX;
         case 1:
            return TINK;
         case 2:
            return LEGACY;
         case 3:
            return RAW;
         case 4:
            return CRUNCHY;
         case 5:
            return WITH_ID_REQUIREMENT;
         default:
            return null;
      }
   }

   public static Internal.EnumLiteMap<OutputPrefixType> internalGetValueMap() {
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
      return Tink.getDescriptor().getEnumTypes().get(1);
   }

   public static OutputPrefixType valueOf(Descriptors.EnumValueDescriptor desc) {
      if (desc.getType() != getDescriptor()) {
         throw new IllegalArgumentException("EnumValueDescriptor is not for this type.");
      } else {
         return desc.getIndex() == -1 ? UNRECOGNIZED : VALUES[desc.getIndex()];
      }
   }

   private OutputPrefixType(int value) {
      this.value = value;
   }

   static {
      RuntimeVersion.validateProtobufGencodeVersion(RuntimeVersion.RuntimeDomain.PUBLIC, 4, 32, 1, "", OutputPrefixType.class.getName());
   }
}
