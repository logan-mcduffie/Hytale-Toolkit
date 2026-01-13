package com.google.crypto.tink.proto;

import com.google.protobuf.MessageOrBuilder;

public interface PrfBasedDeriverParamsOrBuilder extends MessageOrBuilder {
   boolean hasDerivedKeyTemplate();

   KeyTemplate getDerivedKeyTemplate();

   KeyTemplateOrBuilder getDerivedKeyTemplateOrBuilder();
}
