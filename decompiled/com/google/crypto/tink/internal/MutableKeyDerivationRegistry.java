package com.google.crypto.tink.internal;

import com.google.crypto.tink.AccessesPartialKey;
import com.google.crypto.tink.Key;
import com.google.crypto.tink.Parameters;
import com.google.crypto.tink.SecretKeyAccess;
import com.google.errorprone.annotations.RestrictedApi;
import java.io.InputStream;
import java.security.GeneralSecurityException;
import java.util.HashMap;
import java.util.Map;
import javax.annotation.Nullable;

public final class MutableKeyDerivationRegistry {
   private final Map<Class<? extends Parameters>, MutableKeyDerivationRegistry.InsecureKeyCreator<? extends Parameters>> creators = new HashMap<>();
   private static final MutableKeyDerivationRegistry globalInstance = new MutableKeyDerivationRegistry();

   public static MutableKeyDerivationRegistry globalInstance() {
      return globalInstance;
   }

   public synchronized <ParametersT extends Parameters> void add(
      MutableKeyDerivationRegistry.InsecureKeyCreator<ParametersT> creator, Class<ParametersT> parametersClass
   ) throws GeneralSecurityException {
      MutableKeyDerivationRegistry.InsecureKeyCreator<?> existingCreator = this.creators.get(parametersClass);
      if (existingCreator != null && !existingCreator.equals(creator)) {
         throw new GeneralSecurityException("Different key creator for parameters class already inserted");
      } else {
         this.creators.put(parametersClass, creator);
      }
   }

   @RestrictedApi(
      explanation = "Accessing parts of keys can produce unexpected incompatibilities, annotate the function with @AccessesPartialKey",
      link = "https://developers.google.com/tink/design/access_control#accessing_partial_keys",
      allowedOnPath = ".*Test\\.java",
      allowlistAnnotations = AccessesPartialKey.class
   )
   public Key createKeyFromRandomness(Parameters parameters, InputStream inputStream, @Nullable Integer idRequirement, SecretKeyAccess access) throws GeneralSecurityException {
      return this.createKeyFromRandomnessTyped(parameters, inputStream, idRequirement, access);
   }

   private synchronized <ParametersT extends Parameters> Key createKeyFromRandomnessTyped(
      ParametersT parameters, InputStream inputStream, @Nullable Integer idRequirement, SecretKeyAccess access
   ) throws GeneralSecurityException {
      Class<?> parametersClass = parameters.getClass();
      MutableKeyDerivationRegistry.InsecureKeyCreator<?> creator = this.creators.get(parametersClass);
      if (creator == null) {
         throw new GeneralSecurityException(
            "Cannot use key derivation to derive key for parameters " + parameters + ": no key creator for this class was registered."
         );
      } else {
         return ((MutableKeyDerivationRegistry.InsecureKeyCreator<ParametersT>)creator).createKeyFromRandomness(parameters, inputStream, idRequirement, access);
      }
   }

   public interface InsecureKeyCreator<ParametersT extends Parameters> {
      Key createKeyFromRandomness(ParametersT parameters, InputStream inputStream, @Nullable Integer idRequirement, SecretKeyAccess access) throws GeneralSecurityException;
   }
}
