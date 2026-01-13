package com.google.crypto.tink.proto;

import com.google.protobuf.Descriptors;
import com.google.protobuf.Internal;
import com.google.protobuf.ProtocolMessageEnum;
import com.google.protobuf.RuntimeVersion;

public enum KeyStatusType implements ProtocolMessageEnum {
   UNKNOWN_STATUS(0),
   ENABLED(1),
   DISABLED(2),
   DESTROYED(3),
   UNRECOGNIZED(-1);

   public static final int UNKNOWN_STATUS_VALUE = 0;
   public static final int ENABLED_VALUE = 1;
   public static final int DISABLED_VALUE = 2;
   public static final int DESTROYED_VALUE = 3;
   private static final Internal.EnumLiteMap<KeyStatusType> internalValueMap = new Internal.EnumLiteMap<KeyStatusType>() {
      public KeyStatusType findValueByNumber(int number) {
         return KeyStatusType.forNumber(number);
      }
   };
   private static final KeyStatusType[] VALUES = values();
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
   public static KeyStatusType valueOf(int value) {
      return forNumber(value);
   }

   public static KeyStatusType forNumber(int value) {
      switch (value) {
         case 0:
            return UNKNOWN_STATUS;
         case 1:
            return ENABLED;
         case 2:
            return DISABLED;
         case 3:
            return DESTROYED;
         default:
            return null;
      }
   }

   public static Internal.EnumLiteMap<KeyStatusType> internalGetValueMap() {
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
      return Tink.getDescriptor().getEnumTypes().get(0);
   }

   public static KeyStatusType valueOf(Descriptors.EnumValueDescriptor desc) {
      if (desc.getType() != getDescriptor()) {
         throw new IllegalArgumentException("EnumValueDescriptor is not for this type.");
      } else {
         return desc.getIndex() == -1 ? UNRECOGNIZED : VALUES[desc.getIndex()];
      }
   }

   private KeyStatusType(int value) {
      this.value = value;
   }

   static {
      RuntimeVersion.validateProtobufGencodeVersion(RuntimeVersion.RuntimeDomain.PUBLIC, 4, 32, 1, "", KeyStatusType.class.getName());
   }
}
