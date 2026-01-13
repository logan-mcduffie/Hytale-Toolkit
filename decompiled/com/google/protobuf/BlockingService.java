package com.google.protobuf;

public interface BlockingService {
   Descriptors.ServiceDescriptor getDescriptorForType();

   Message callBlockingMethod(Descriptors.MethodDescriptor method, RpcController controller, Message request) throws ServiceException;

   Message getRequestPrototype(Descriptors.MethodDescriptor method);

   Message getResponsePrototype(Descriptors.MethodDescriptor method);
}
