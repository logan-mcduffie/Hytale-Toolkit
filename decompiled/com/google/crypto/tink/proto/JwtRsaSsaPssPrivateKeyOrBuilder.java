package com.google.crypto.tink.proto;

import com.google.protobuf.ByteString;
import com.google.protobuf.MessageOrBuilder;

public interface JwtRsaSsaPssPrivateKeyOrBuilder extends MessageOrBuilder {
   int getVersion();

   boolean hasPublicKey();

   JwtRsaSsaPssPublicKey getPublicKey();

   JwtRsaSsaPssPublicKeyOrBuilder getPublicKeyOrBuilder();

   ByteString getD();

   ByteString getP();

   ByteString getQ();

   ByteString getDp();

   ByteString getDq();

   ByteString getCrt();
}
