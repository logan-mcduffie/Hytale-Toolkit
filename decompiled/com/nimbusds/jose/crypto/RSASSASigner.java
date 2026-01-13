package com.nimbusds.jose.crypto;

import com.nimbusds.jose.ActionRequiredForJWSCompletionException;
import com.nimbusds.jose.CompletableJWSObjectSigning;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.JWSSigner;
import com.nimbusds.jose.JWSSignerOption;
import com.nimbusds.jose.crypto.impl.RSAKeyUtils;
import com.nimbusds.jose.crypto.impl.RSASSA;
import com.nimbusds.jose.crypto.impl.RSASSAProvider;
import com.nimbusds.jose.crypto.opts.AllowWeakRSAKey;
import com.nimbusds.jose.crypto.opts.OptionUtils;
import com.nimbusds.jose.crypto.opts.UserAuthenticationRequired;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.shaded.jcip.ThreadSafe;
import com.nimbusds.jose.util.Base64URL;
import java.security.InvalidKeyException;
import java.security.PrivateKey;
import java.security.Signature;
import java.security.SignatureException;
import java.security.interfaces.RSAPrivateKey;
import java.util.Collections;
import java.util.Set;

@ThreadSafe
public class RSASSASigner extends RSASSAProvider implements JWSSigner {
   private final PrivateKey privateKey;
   private final Set<JWSSignerOption> opts;

   public RSASSASigner(PrivateKey privateKey) {
      this(privateKey, false);
   }

   @Deprecated
   public RSASSASigner(PrivateKey privateKey, boolean allowWeakKey) {
      this(privateKey, allowWeakKey ? Collections.singleton(AllowWeakRSAKey.getInstance()) : Collections.emptySet());
   }

   public RSASSASigner(PrivateKey privateKey, Set<JWSSignerOption> opts) {
      if (!(privateKey instanceof RSAPrivateKey) && !"RSA".equalsIgnoreCase(privateKey.getAlgorithm())) {
         throw new IllegalArgumentException("The private key algorithm must be RSA");
      } else {
         this.privateKey = privateKey;
         this.opts = opts != null ? opts : Collections.emptySet();
         OptionUtils.ensureMinRSAPrivateKeySize(privateKey, this.opts);
      }
   }

   public RSASSASigner(RSAKey rsaJWK) throws JOSEException {
      this(rsaJWK, Collections.emptySet());
   }

   @Deprecated
   public RSASSASigner(RSAKey rsaJWK, boolean allowWeakKey) throws JOSEException {
      this(RSAKeyUtils.toRSAPrivateKey(rsaJWK), allowWeakKey);
   }

   public RSASSASigner(RSAKey rsaJWK, Set<JWSSignerOption> opts) throws JOSEException {
      this(RSAKeyUtils.toRSAPrivateKey(rsaJWK), opts);
   }

   public PrivateKey getPrivateKey() {
      return this.privateKey;
   }

   @Override
   public Base64URL sign(JWSHeader header, final byte[] signingInput) throws JOSEException {
      final Signature signer = this.getInitiatedSignature(header);
      if (this.opts.contains(UserAuthenticationRequired.getInstance())) {
         throw new ActionRequiredForJWSCompletionException(
            "Authenticate user to complete signing", UserAuthenticationRequired.getInstance(), new CompletableJWSObjectSigning() {
               @Override
               public Signature getInitializedSignature() {
                  return signer;
               }

               @Override
               public Base64URL complete() throws JOSEException {
                  return RSASSASigner.this.sign(signingInput, signer);
               }
            }
         );
      } else {
         return this.sign(signingInput, signer);
      }
   }

   private Signature getInitiatedSignature(JWSHeader header) throws JOSEException {
      Signature signer = RSASSA.getSignerAndVerifier(header.getAlgorithm(), this.getJCAContext().getProvider());

      try {
         signer.initSign(this.privateKey);
         return signer;
      } catch (InvalidKeyException var4) {
         throw new JOSEException("Invalid private RSA key: " + var4.getMessage(), var4);
      }
   }

   private Base64URL sign(byte[] signingInput, Signature signer) throws JOSEException {
      try {
         signer.update(signingInput);
         return Base64URL.encode(signer.sign());
      } catch (SignatureException var4) {
         throw new JOSEException("RSA signature exception: " + var4.getMessage(), var4);
      }
   }
}
