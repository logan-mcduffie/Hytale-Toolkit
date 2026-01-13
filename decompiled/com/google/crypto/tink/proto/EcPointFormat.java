package com.google.crypto.tink.proto;

import com.google.protobuf.Descriptors;
import com.google.protobuf.Internal;
import com.google.protobuf.ProtocolMessageEnum;
import com.google.protobuf.RuntimeVersion;

public enum EcPointFormat implements ProtocolMessageEnum {
   UNKNOWN_FORMAT(0),
   UNCOMPRESSED(1),
   COMPRESSED(2),
   DO_NOT_USE_CRUNCHY_UNCOMPRESSED(3),
   UNRECOGNIZED(-1);

   public static final int UNKNOWN_FORMAT_VALUE = 0;
   public static final int UNCOMPRESSED_VALUE = 1;
   public static final int COMPRESSED_VALUE = 2;
   public static final int DO_NOT_USE_CRUNCHY_UNCOMPRESSED_VALUE = 3;
   private static final Internal.EnumLiteMap<EcPointFormat> internalValueMap = new Internal.EnumLiteMap<EcPointFormat>() {
      public EcPointFormat findValueByNumber(int number) {
         return EcPointFormat.forNumber(number);
      }
   };
   private static final EcPointFormat[] VALUES = values();
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
   public static EcPointFormat valueOf(int value) {
      return forNumber(value);
   }

   public static EcPointFormat forNumber(int value) {
      switch (value) {
         case 0:
            return UNKNOWN_FORMAT;
         case 1:
            return UNCOMPRESSED;
         case 2:
            return COMPRESSED;
         case 3:
            return DO_NOT_USE_CRUNCHY_UNCOMPRESSED;
         default:
            return null;
      }
   }

   public static Internal.EnumLiteMap<EcPointFormat> internalGetValueMap() {
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
      return Common.getDescriptor().getEnumTypes().get(1);
   }

   public static EcPointFormat valueOf(Descriptors.EnumValueDescriptor desc) {
      if (desc.getType() != getDescriptor()) {
         throw new IllegalArgumentException("EnumValueDescriptor is not for this type.");
      } else {
         return desc.getIndex() == -1 ? UNRECOGNIZED : VALUES[desc.getIndex()];
      }
   }

   private EcPointFormat(int value) {
      this.value = value;
   }

   static {
      RuntimeVersion.validateProtobufGencodeVersion(RuntimeVersion.RuntimeDomain.PUBLIC, 4, 32, 1, "", EcPointFormat.class.getName());
   }
}
