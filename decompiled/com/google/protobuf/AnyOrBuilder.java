package com.google.protobuf;

public interface AnyOrBuilder extends MessageOrBuilder {
   String getTypeUrl();

   ByteString getTypeUrlBytes();

   ByteString getValue();
}
