package com.google.protobuf;

public interface BlockingRpcChannel {
   Message callBlockingMethod(Descriptors.MethodDescriptor method, RpcController controller, Message request, Message responsePrototype) throws ServiceException;
}
