package com.google.crypto.tink.proto;

import com.google.protobuf.MessageOrBuilder;

public interface PrfBasedDeriverKeyOrBuilder extends MessageOrBuilder {
   int getVersion();

   boolean hasPrfKey();

   KeyData getPrfKey();

   KeyDataOrBuilder getPrfKeyOrBuilder();

   boolean hasParams();

   PrfBasedDeriverParams getParams();

   PrfBasedDeriverParamsOrBuilder getParamsOrBuilder();
}
