package com.google.crypto.tink.proto;

import com.google.protobuf.ByteString;
import com.google.protobuf.MessageOrBuilder;

public interface EcdsaPublicKeyOrBuilder extends MessageOrBuilder {
   int getVersion();

   boolean hasParams();

   EcdsaParams getParams();

   EcdsaParamsOrBuilder getParamsOrBuilder();

   ByteString getX();

   ByteString getY();
}
