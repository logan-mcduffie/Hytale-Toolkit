package com.google.crypto.tink.proto;

import com.google.protobuf.MessageOrBuilder;

public interface AesCtrHmacAeadKeyFormatOrBuilder extends MessageOrBuilder {
   boolean hasAesCtrKeyFormat();

   AesCtrKeyFormat getAesCtrKeyFormat();

   AesCtrKeyFormatOrBuilder getAesCtrKeyFormatOrBuilder();

   boolean hasHmacKeyFormat();

   HmacKeyFormat getHmacKeyFormat();

   HmacKeyFormatOrBuilder getHmacKeyFormatOrBuilder();
}
