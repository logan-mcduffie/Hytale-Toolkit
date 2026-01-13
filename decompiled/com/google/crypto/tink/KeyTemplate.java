package com.google.crypto.tink;

import com.google.crypto.tink.internal.LegacyProtoParameters;
import com.google.crypto.tink.internal.MutableSerializationRegistry;
import com.google.crypto.tink.internal.ProtoParametersSerialization;
import com.google.crypto.tink.internal.TinkBugException;
import com.google.errorprone.annotations.Immutable;
import com.google.protobuf.ByteString;
import java.security.GeneralSecurityException;
import javax.annotation.Nullable;

@Immutable
public final class KeyTemplate {
   @Nullable
   private final com.google.crypto.tink.proto.KeyTemplate kt;
   @Nullable
   private final Parameters parameters;

   static KeyTemplate.OutputPrefixType fromProto(com.google.crypto.tink.proto.OutputPrefixType outputPrefixType) {
      switch (outputPrefixType) {
         case TINK:
            return KeyTemplate.OutputPrefixType.TINK;
         case LEGACY:
            return KeyTemplate.OutputPrefixType.LEGACY;
         case RAW:
            return KeyTemplate.OutputPrefixType.RAW;
         case CRUNCHY:
            return KeyTemplate.OutputPrefixType.CRUNCHY;
         default:
            throw new IllegalArgumentException("Unknown output prefix type");
      }
   }

   static com.google.crypto.tink.proto.OutputPrefixType toProto(KeyTemplate.OutputPrefixType outputPrefixType) {
      switch (outputPrefixType) {
         case TINK:
            return com.google.crypto.tink.proto.OutputPrefixType.TINK;
         case LEGACY:
            return com.google.crypto.tink.proto.OutputPrefixType.LEGACY;
         case RAW:
            return com.google.crypto.tink.proto.OutputPrefixType.RAW;
         case CRUNCHY:
            return com.google.crypto.tink.proto.OutputPrefixType.CRUNCHY;
         default:
            throw new IllegalArgumentException("Unknown output prefix type");
      }
   }

   @Deprecated
   public static KeyTemplate create(String typeUrl, byte[] value, KeyTemplate.OutputPrefixType outputPrefixType) {
      return new KeyTemplate(
         com.google.crypto.tink.proto.KeyTemplate.newBuilder()
            .setTypeUrl(typeUrl)
            .setValue(ByteString.copyFrom(value))
            .setOutputPrefixType(toProto(outputPrefixType))
            .build()
      );
   }

   public static KeyTemplate createFrom(Parameters p) throws GeneralSecurityException {
      return new KeyTemplate(p);
   }

   private KeyTemplate(com.google.crypto.tink.proto.KeyTemplate kt) {
      this.kt = kt;
      this.parameters = null;
   }

   private KeyTemplate(Parameters parameters) {
      this.kt = null;
      this.parameters = parameters;
   }

   com.google.crypto.tink.proto.KeyTemplate getProto() {
      try {
         return this.getProtoMaybeThrow();
      } catch (GeneralSecurityException var2) {
         throw new TinkBugException(
            "Parsing parameters failed in getProto(). You probably want to call some Tink register function for " + this.parameters, var2
         );
      }
   }

   com.google.crypto.tink.proto.KeyTemplate getProtoMaybeThrow() throws GeneralSecurityException {
      if (this.kt != null) {
         return this.kt;
      } else if (this.parameters instanceof LegacyProtoParameters) {
         return ((LegacyProtoParameters)this.parameters).getSerialization().getKeyTemplate();
      } else {
         ProtoParametersSerialization s = MutableSerializationRegistry.globalInstance()
            .serializeParameters(this.parameters, ProtoParametersSerialization.class);
         return s.getKeyTemplate();
      }
   }

   @Deprecated
   public String getTypeUrl() {
      return this.getProto().getTypeUrl();
   }

   @Deprecated
   public byte[] getValue() {
      return this.getProto().getValue().toByteArray();
   }

   @Deprecated
   public KeyTemplate.OutputPrefixType getOutputPrefixType() {
      return fromProto(this.getProto().getOutputPrefixType());
   }

   public Parameters toParameters() throws GeneralSecurityException {
      return this.parameters != null ? this.parameters : TinkProtoParametersFormat.parse(this.getProto().toByteArray());
   }

   public static enum OutputPrefixType {
      TINK,
      LEGACY,
      RAW,
      CRUNCHY;
   }
}
