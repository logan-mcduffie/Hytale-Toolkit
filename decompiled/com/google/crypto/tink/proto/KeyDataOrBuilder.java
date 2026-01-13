package com.google.crypto.tink.proto;

import com.google.protobuf.ByteString;
import com.google.protobuf.MessageOrBuilder;

public interface KeyDataOrBuilder extends MessageOrBuilder {
   String getTypeUrl();

   ByteString getTypeUrlBytes();

   ByteString getValue();

   int getKeyMaterialTypeValue();

   KeyData.KeyMaterialType getKeyMaterialType();
}
