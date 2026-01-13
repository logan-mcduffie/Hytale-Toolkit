package com.google.crypto.tink.proto;

import com.google.protobuf.ByteString;
import com.google.protobuf.MessageOrBuilder;

public interface JwtRsaSsaPkcs1PrivateKeyOrBuilder extends MessageOrBuilder {
   int getVersion();

   boolean hasPublicKey();

   JwtRsaSsaPkcs1PublicKey getPublicKey();

   JwtRsaSsaPkcs1PublicKeyOrBuilder getPublicKeyOrBuilder();

   ByteString getD();

   ByteString getP();

   ByteString getQ();

   ByteString getDp();

   ByteString getDq();

   ByteString getCrt();
}
