package com.google.crypto.tink.proto;

import com.google.protobuf.MessageOrBuilder;

public interface PrfBasedDeriverKeyFormatOrBuilder extends MessageOrBuilder {
   boolean hasPrfKeyTemplate();

   KeyTemplate getPrfKeyTemplate();

   KeyTemplateOrBuilder getPrfKeyTemplateOrBuilder();

   boolean hasParams();

   PrfBasedDeriverParams getParams();

   PrfBasedDeriverParamsOrBuilder getParamsOrBuilder();
}
