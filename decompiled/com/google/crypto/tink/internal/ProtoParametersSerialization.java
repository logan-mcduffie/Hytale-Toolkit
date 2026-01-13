package com.google.crypto.tink.internal;

import com.google.crypto.tink.proto.KeyTemplate;
import com.google.crypto.tink.proto.OutputPrefixType;
import com.google.crypto.tink.util.Bytes;
import com.google.errorprone.annotations.Immutable;
import com.google.protobuf.MessageLite;
import java.security.GeneralSecurityException;

@Immutable
public final class ProtoParametersSerialization implements Serialization {
   private final Bytes objectIdentifier;
   private final KeyTemplate keyTemplate;

   private ProtoParametersSerialization(KeyTemplate keyTemplate, Bytes objectIdentifier) {
      this.keyTemplate = keyTemplate;
      this.objectIdentifier = objectIdentifier;
   }

   public static ProtoParametersSerialization create(String typeUrl, OutputPrefixType outputPrefixType, MessageLite value) {
      return create(KeyTemplate.newBuilder().setTypeUrl(typeUrl).setOutputPrefixType(outputPrefixType).setValue(value.toByteString()).build());
   }

   public static ProtoParametersSerialization create(KeyTemplate keyTemplate) {
      return new ProtoParametersSerialization(keyTemplate, Util.toBytesFromPrintableAscii(keyTemplate.getTypeUrl()));
   }

   public static ProtoParametersSerialization checkedCreate(KeyTemplate keyTemplate) throws GeneralSecurityException {
      return new ProtoParametersSerialization(keyTemplate, Util.checkedToBytesFromPrintableAscii(keyTemplate.getTypeUrl()));
   }

   public KeyTemplate getKeyTemplate() {
      return this.keyTemplate;
   }

   @Override
   public Bytes getObjectIdentifier() {
      return this.objectIdentifier;
   }
}
