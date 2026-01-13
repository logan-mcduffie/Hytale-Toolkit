package com.google.crypto.tink.proto;

import com.google.protobuf.Descriptors;
import com.google.protobuf.Internal;
import com.google.protobuf.ProtocolMessageEnum;
import com.google.protobuf.RuntimeVersion;

public enum MlDsaInstance implements ProtocolMessageEnum {
   ML_DSA_UNKNOWN_INSTANCE(0),
   ML_DSA_65(1),
   ML_DSA_87(2),
   UNRECOGNIZED(-1);

   public static final int ML_DSA_UNKNOWN_INSTANCE_VALUE = 0;
   public static final int ML_DSA_65_VALUE = 1;
   public static final int ML_DSA_87_VALUE = 2;
   private static final Internal.EnumLiteMap<MlDsaInstance> internalValueMap = new Internal.EnumLiteMap<MlDsaInstance>() {
      public MlDsaInstance findValueByNumber(int number) {
         return MlDsaInstance.forNumber(number);
      }
   };
   private static final MlDsaInstance[] VALUES = values();
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
   public static MlDsaInstance valueOf(int value) {
      return forNumber(value);
   }

   public static MlDsaInstance forNumber(int value) {
      switch (value) {
         case 0:
            return ML_DSA_UNKNOWN_INSTANCE;
         case 1:
            return ML_DSA_65;
         case 2:
            return ML_DSA_87;
         default:
            return null;
      }
   }

   public static Internal.EnumLiteMap<MlDsaInstance> internalGetValueMap() {
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
      return MlDsa.getDescriptor().getEnumTypes().get(0);
   }

   public static MlDsaInstance valueOf(Descriptors.EnumValueDescriptor desc) {
      if (desc.getType() != getDescriptor()) {
         throw new IllegalArgumentException("EnumValueDescriptor is not for this type.");
      } else {
         return desc.getIndex() == -1 ? UNRECOGNIZED : VALUES[desc.getIndex()];
      }
   }

   private MlDsaInstance(int value) {
      this.value = value;
   }

   static {
      RuntimeVersion.validateProtobufGencodeVersion(RuntimeVersion.RuntimeDomain.PUBLIC, 4, 32, 1, "", MlDsaInstance.class.getName());
   }
}
