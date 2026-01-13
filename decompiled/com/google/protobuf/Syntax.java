package com.google.protobuf;

public enum Syntax implements ProtocolMessageEnum {
   SYNTAX_PROTO2(0),
   SYNTAX_PROTO3(1),
   SYNTAX_EDITIONS(2),
   UNRECOGNIZED(-1);

   public static final int SYNTAX_PROTO2_VALUE = 0;
   public static final int SYNTAX_PROTO3_VALUE = 1;
   public static final int SYNTAX_EDITIONS_VALUE = 2;
   private static final Internal.EnumLiteMap<Syntax> internalValueMap = new Internal.EnumLiteMap<Syntax>() {
      public Syntax findValueByNumber(int number) {
         return Syntax.forNumber(number);
      }
   };
   private static final Syntax[] VALUES = values();
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
   public static Syntax valueOf(int value) {
      return forNumber(value);
   }

   public static Syntax forNumber(int value) {
      switch (value) {
         case 0:
            return SYNTAX_PROTO2;
         case 1:
            return SYNTAX_PROTO3;
         case 2:
            return SYNTAX_EDITIONS;
         default:
            return null;
      }
   }

   public static Internal.EnumLiteMap<Syntax> internalGetValueMap() {
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
      return TypeProto.getDescriptor().getEnumTypes().get(0);
   }

   public static Syntax valueOf(Descriptors.EnumValueDescriptor desc) {
      if (desc.getType() != getDescriptor()) {
         throw new IllegalArgumentException("EnumValueDescriptor is not for this type.");
      } else {
         return desc.getIndex() == -1 ? UNRECOGNIZED : VALUES[desc.getIndex()];
      }
   }

   private Syntax(int value) {
      this.value = value;
   }

   static {
      RuntimeVersion.validateProtobufGencodeVersion(RuntimeVersion.RuntimeDomain.PUBLIC, 4, 33, 0, "", "Syntax");
   }
}
