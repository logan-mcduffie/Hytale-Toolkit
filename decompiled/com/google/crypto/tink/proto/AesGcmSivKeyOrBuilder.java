package com.google.crypto.tink.proto;

import com.google.protobuf.ByteString;
import com.google.protobuf.MessageOrBuilder;

public interface AesGcmSivKeyOrBuilder extends MessageOrBuilder {
   int getVersion();

   ByteString getKeyValue();
}
