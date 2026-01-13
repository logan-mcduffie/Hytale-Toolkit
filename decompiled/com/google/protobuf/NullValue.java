package com.google.protobuf;

public enum NullValue implements ProtocolMessageEnum {
   NULL_VALUE(0),
   UNRECOGNIZED(-1);

   public static final int NULL_VALUE_VALUE = 0;
   private static final Internal.EnumLiteMap<NullValue> internalValueMap = new Internal.EnumLiteMap<NullValue>() {
      public NullValue findValueByNumber(int number) {
         return NullValue.forNumber(number);
      }
   };
   private static final NullValue[] VALUES = values();
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
   public static NullValue valueOf(int value) {
      return forNumber(value);
   }

   public static NullValue forNumber(int value) {
      switch (value) {
         case 0:
            return NULL_VALUE;
         default:
            return null;
      }
   }

   public static Internal.EnumLiteMap<NullValue> internalGetValueMap() {
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
      return StructProto.getDescriptor().getEnumTypes().get(0);
   }

   public static NullValue valueOf(Descriptors.EnumValueDescriptor desc) {
      if (desc.getType() != getDescriptor()) {
         throw new IllegalArgumentException("EnumValueDescriptor is not for this type.");
      } else {
         return desc.getIndex() == -1 ? UNRECOGNIZED : VALUES[desc.getIndex()];
      }
   }

   private NullValue(int value) {
      this.value = value;
   }

   static {
      RuntimeVersion.validateProtobufGencodeVersion(RuntimeVersion.RuntimeDomain.PUBLIC, 4, 33, 0, "", "NullValue");
   }
}
