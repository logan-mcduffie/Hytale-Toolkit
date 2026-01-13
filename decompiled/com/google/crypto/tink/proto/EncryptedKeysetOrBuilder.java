package com.google.crypto.tink.proto;

import com.google.protobuf.ByteString;
import com.google.protobuf.MessageOrBuilder;

public interface EncryptedKeysetOrBuilder extends MessageOrBuilder {
   ByteString getEncryptedKeyset();

   boolean hasKeysetInfo();

   KeysetInfo getKeysetInfo();

   KeysetInfoOrBuilder getKeysetInfoOrBuilder();
}
