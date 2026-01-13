package com.google.crypto.tink.internal;

import com.google.crypto.tink.proto.KeyData;
import com.google.crypto.tink.proto.OutputPrefixType;
import com.google.crypto.tink.util.Bytes;
import com.google.errorprone.annotations.Immutable;
import com.google.protobuf.ByteString;
import java.security.GeneralSecurityException;
import javax.annotation.Nullable;

@Immutable
public final class ProtoKeySerialization implements Serialization {
   private final String typeUrl;
   private final Bytes objectIdentifier;
   private final ByteString value;
   private final KeyData.KeyMaterialType keyMaterialType;
   private final OutputPrefixType outputPrefixType;
   @Nullable
   private final Integer idRequirement;

   private ProtoKeySerialization(
      String typeUrl,
      Bytes objectIdentifier,
      ByteString value,
      KeyData.KeyMaterialType keyMaterialType,
      OutputPrefixType outputPrefixType,
      @Nullable Integer idRequirement
   ) {
      this.typeUrl = typeUrl;
      this.objectIdentifier = objectIdentifier;
      this.value = value;
      this.keyMaterialType = keyMaterialType;
      this.outputPrefixType = outputPrefixType;
      this.idRequirement = idRequirement;
   }

   public static ProtoKeySerialization create(
      String typeUrl, ByteString value, KeyData.KeyMaterialType keyMaterialType, OutputPrefixType outputPrefixType, @Nullable Integer idRequirement
   ) throws GeneralSecurityException {
      if (outputPrefixType == OutputPrefixType.RAW) {
         if (idRequirement != null) {
            throw new GeneralSecurityException("Keys with output prefix type raw should not have an id requirement.");
         }
      } else if (idRequirement == null) {
         throw new GeneralSecurityException("Keys with output prefix type different from raw should have an id requirement.");
      }

      Bytes objectIdentifier = Util.checkedToBytesFromPrintableAscii(typeUrl);
      return new ProtoKeySerialization(typeUrl, objectIdentifier, value, keyMaterialType, outputPrefixType, idRequirement);
   }

   public ByteString getValue() {
      return this.value;
   }

   public KeyData.KeyMaterialType getKeyMaterialType() {
      return this.keyMaterialType;
   }

   public OutputPrefixType getOutputPrefixType() {
      return this.outputPrefixType;
   }

   @Nullable
   public Integer getIdRequirementOrNull() {
      return this.idRequirement;
   }

   @Override
   public Bytes getObjectIdentifier() {
      return this.objectIdentifier;
   }

   public String getTypeUrl() {
      return this.typeUrl;
   }
}
