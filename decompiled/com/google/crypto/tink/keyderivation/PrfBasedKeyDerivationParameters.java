package com.google.crypto.tink.keyderivation;

import com.google.crypto.tink.Parameters;
import com.google.crypto.tink.prf.PrfParameters;
import com.google.errorprone.annotations.CanIgnoreReturnValue;
import com.google.errorprone.annotations.Immutable;
import java.security.GeneralSecurityException;
import java.util.Objects;
import javax.annotation.Nullable;

@Immutable
public final class PrfBasedKeyDerivationParameters extends KeyDerivationParameters {
   private final PrfParameters prfParameters;
   private final Parameters derivedKeyParameters;

   private PrfBasedKeyDerivationParameters(PrfParameters prfParameters, Parameters derivedKeyParameters) {
      this.prfParameters = prfParameters;
      this.derivedKeyParameters = derivedKeyParameters;
   }

   public static PrfBasedKeyDerivationParameters.Builder builder() {
      return new PrfBasedKeyDerivationParameters.Builder();
   }

   public PrfParameters getPrfParameters() {
      return this.prfParameters;
   }

   @Override
   public Parameters getDerivedKeyParameters() {
      return this.derivedKeyParameters;
   }

   @Override
   public boolean equals(Object o) {
      if (!(o instanceof PrfBasedKeyDerivationParameters)) {
         return false;
      } else {
         PrfBasedKeyDerivationParameters that = (PrfBasedKeyDerivationParameters)o;
         return that.getPrfParameters().equals(this.getPrfParameters()) && that.getDerivedKeyParameters().equals(this.getDerivedKeyParameters());
      }
   }

   @Override
   public int hashCode() {
      return Objects.hash(PrfBasedKeyDerivationParameters.class, this.prfParameters, this.derivedKeyParameters);
   }

   @Override
   public String toString() {
      return String.format("PrfBasedKeyDerivationParameters(%s, %s)", this.prfParameters, this.derivedKeyParameters);
   }

   public static class Builder {
      @Nullable
      private PrfParameters prfParameters = null;
      @Nullable
      private Parameters derivedKeyParameters = null;

      @CanIgnoreReturnValue
      public PrfBasedKeyDerivationParameters.Builder setPrfParameters(PrfParameters prfParameters) {
         this.prfParameters = prfParameters;
         return this;
      }

      @CanIgnoreReturnValue
      public PrfBasedKeyDerivationParameters.Builder setDerivedKeyParameters(Parameters derivedKeyParameters) {
         this.derivedKeyParameters = derivedKeyParameters;
         return this;
      }

      public PrfBasedKeyDerivationParameters build() throws GeneralSecurityException {
         if (this.prfParameters == null) {
            throw new GeneralSecurityException("PrfParameters must be set.");
         } else if (this.derivedKeyParameters == null) {
            throw new GeneralSecurityException("DerivedKeyParameters must be set.");
         } else {
            return new PrfBasedKeyDerivationParameters(this.prfParameters, this.derivedKeyParameters);
         }
      }
   }
}
