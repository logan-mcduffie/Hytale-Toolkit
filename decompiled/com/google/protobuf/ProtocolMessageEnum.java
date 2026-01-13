package com.google.protobuf;

public interface ProtocolMessageEnum extends Internal.EnumLite {
   @Override
   int getNumber();

   Descriptors.EnumValueDescriptor getValueDescriptor();

   Descriptors.EnumDescriptor getDescriptorForType();
}
