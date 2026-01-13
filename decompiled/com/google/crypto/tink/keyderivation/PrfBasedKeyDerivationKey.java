package com.google.crypto.tink.keyderivation;

import com.google.crypto.tink.AccessesPartialKey;
import com.google.crypto.tink.Key;
import com.google.crypto.tink.prf.PrfKey;
import com.google.errorprone.annotations.RestrictedApi;
import java.security.GeneralSecurityException;
import java.util.Objects;
import javax.annotation.Nullable;

public final class PrfBasedKeyDerivationKey extends KeyDerivationKey {
   private final PrfBasedKeyDerivationParameters parameters;
   private final PrfKey prfKey;
   private final Integer idRequirementOrNull;

   @RestrictedApi(
      explanation = "Accessing parts of keys can produce unexpected incompatibilities, annotate the function with @AccessesPartialKey",
      link = "https://developers.google.com/tink/design/access_control#accessing_partial_keys",
      allowedOnPath = ".*Test\\.java",
      allowlistAnnotations = AccessesPartialKey.class
   )
   public static PrfBasedKeyDerivationKey create(PrfBasedKeyDerivationParameters parameters, PrfKey prfKey, @Nullable Integer idRequirement) throws GeneralSecurityException {
      if (!parameters.getPrfParameters().equals(prfKey.getParameters())) {
         throw new GeneralSecurityException(
            "PrfParameters of passed in PrfBasedKeyDerivationParameters and passed in prfKey parameters object must match. DerivationParameters gave: "
               + parameters.getPrfParameters()
               + ", key gives: "
               + prfKey.getParameters()
         );
      } else if (parameters.getDerivedKeyParameters().hasIdRequirement() && idRequirement == null) {
         throw new GeneralSecurityException("Derived key has an ID requirement, but no idRequirement was passed in on creation of this key");
      } else if (!parameters.getDerivedKeyParameters().hasIdRequirement() && idRequirement != null) {
         throw new GeneralSecurityException("Derived key has no ID requirement, but idRequirement was passed in on creation of this key");
      } else {
         return new PrfBasedKeyDerivationKey(parameters, prfKey, idRequirement);
      }
   }

   private PrfBasedKeyDerivationKey(PrfBasedKeyDerivationParameters parameters, PrfKey prfKey, @Nullable Integer idRequirement) {
      this.parameters = parameters;
      this.prfKey = prfKey;
      this.idRequirementOrNull = idRequirement;
   }

   @RestrictedApi(
      explanation = "Accessing parts of keys can produce unexpected incompatibilities, annotate the function with @AccessesPartialKey",
      link = "https://developers.google.com/tink/design/access_control#accessing_partial_keys",
      allowedOnPath = ".*Test\\.java",
      allowlistAnnotations = AccessesPartialKey.class
   )
   public PrfKey getPrfKey() {
      return this.prfKey;
   }

   public PrfBasedKeyDerivationParameters getParameters() {
      return this.parameters;
   }

   @Nullable
   @Override
   public Integer getIdRequirementOrNull() {
      return this.idRequirementOrNull;
   }

   @Override
   public boolean equalsKey(Key other) {
      if (!(other instanceof PrfBasedKeyDerivationKey)) {
         return false;
      } else {
         PrfBasedKeyDerivationKey otherKey = (PrfBasedKeyDerivationKey)other;
         return otherKey.getParameters().equals(this.getParameters())
            && otherKey.prfKey.equalsKey(this.prfKey)
            && Objects.equals(otherKey.idRequirementOrNull, this.idRequirementOrNull);
      }
   }
}
