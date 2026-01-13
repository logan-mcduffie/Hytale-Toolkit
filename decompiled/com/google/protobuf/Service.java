package com.google.protobuf;

public interface Service {
   Descriptors.ServiceDescriptor getDescriptorForType();

   void callMethod(Descriptors.MethodDescriptor method, RpcController controller, Message request, RpcCallback<Message> done);

   Message getRequestPrototype(Descriptors.MethodDescriptor method);

   Message getResponsePrototype(Descriptors.MethodDescriptor method);
}
