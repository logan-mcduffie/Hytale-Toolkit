package com.google.crypto.tink.proto;

import com.google.protobuf.ByteString;
import com.google.protobuf.MessageOrBuilder;

public interface KmsEnvelopeAeadKeyFormatOrBuilder extends MessageOrBuilder {
   String getKekUri();

   ByteString getKekUriBytes();

   boolean hasDekTemplate();

   KeyTemplate getDekTemplate();

   KeyTemplateOrBuilder getDekTemplateOrBuilder();
}
