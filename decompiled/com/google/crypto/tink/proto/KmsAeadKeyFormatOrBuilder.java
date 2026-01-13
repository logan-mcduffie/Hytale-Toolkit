package com.google.crypto.tink.proto;

import com.google.protobuf.ByteString;
import com.google.protobuf.MessageOrBuilder;

public interface KmsAeadKeyFormatOrBuilder extends MessageOrBuilder {
   String getKeyUri();

   ByteString getKeyUriBytes();
}
