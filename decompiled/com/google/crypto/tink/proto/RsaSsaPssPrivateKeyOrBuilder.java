package com.google.crypto.tink.proto;

import com.google.protobuf.ByteString;
import com.google.protobuf.MessageOrBuilder;

public interface RsaSsaPssPrivateKeyOrBuilder extends MessageOrBuilder {
   int getVersion();

   boolean hasPublicKey();

   RsaSsaPssPublicKey getPublicKey();

   RsaSsaPssPublicKeyOrBuilder getPublicKeyOrBuilder();

   ByteString getD();

   ByteString getP();

   ByteString getQ();

   ByteString getDp();

   ByteString getDq();

   ByteString getCrt();
}
