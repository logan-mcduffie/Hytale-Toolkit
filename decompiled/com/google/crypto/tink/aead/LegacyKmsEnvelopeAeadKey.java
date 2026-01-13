package com.google.crypto.tink.aead;

import com.google.crypto.tink.Key;
import com.google.crypto.tink.internal.OutputPrefixUtil;
import com.google.crypto.tink.util.Bytes;
import java.security.GeneralSecurityException;
import java.util.Objects;
import javax.annotation.Nullable;

public final class LegacyKmsEnvelopeAeadKey extends AeadKey {
   private final LegacyKmsEnvelopeAeadParameters parameters;
   private final Bytes outputPrefix;
   @Nullable
   private final Integer idRequirement;

   private LegacyKmsEnvelopeAeadKey(LegacyKmsEnvelopeAeadParameters parameters, Bytes outputPrefix, @Nullable Integer idRequirement) {
      this.parameters = parameters;
      this.outputPrefix = outputPrefix;
      this.idRequirement = idRequirement;
   }

   public static LegacyKmsEnvelopeAeadKey create(LegacyKmsEnvelopeAeadParameters parameters, @Nullable Integer idRequirement) throws GeneralSecurityException {
      Bytes outputPrefix;
      if (parameters.getVariant() == LegacyKmsEnvelopeAeadParameters.Variant.NO_PREFIX) {
         if (idRequirement != null) {
            throw new GeneralSecurityException("For given Variant NO_PREFIX the value of idRequirement must be null");
         }

         outputPrefix = OutputPrefixUtil.EMPTY_PREFIX;
      } else {
         if (parameters.getVariant() != LegacyKmsEnvelopeAeadParameters.Variant.TINK) {
            throw new GeneralSecurityException("Unknown Variant: " + parameters.getVariant());
         }

         if (idRequirement == null) {
            throw new GeneralSecurityException("For given Variant TINK the value of idRequirement must be non-null");
         }

         outputPrefix = OutputPrefixUtil.getTinkOutputPrefix(idRequirement);
      }

      return new LegacyKmsEnvelopeAeadKey(parameters, outputPrefix, idRequirement);
   }

   public static LegacyKmsEnvelopeAeadKey create(LegacyKmsEnvelopeAeadParameters parameters) throws GeneralSecurityException {
      return create(parameters, null);
   }

   @Override
   public Bytes getOutputPrefix() {
      return this.outputPrefix;
   }

   public LegacyKmsEnvelopeAeadParameters getParameters() {
      return this.parameters;
   }

   @Override
   public Integer getIdRequirementOrNull() {
      return this.idRequirement;
   }

   @Override
   public boolean equalsKey(Key o) {
      if (!(o instanceof LegacyKmsEnvelopeAeadKey)) {
         return false;
      } else {
         LegacyKmsEnvelopeAeadKey that = (LegacyKmsEnvelopeAeadKey)o;
         return that.parameters.equals(this.parameters) && Objects.equals(that.idRequirement, this.idRequirement);
      }
   }
}
