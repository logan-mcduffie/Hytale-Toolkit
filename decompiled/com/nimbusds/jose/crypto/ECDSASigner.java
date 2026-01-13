package com.nimbusds.jose.crypto;

import com.nimbusds.jose.ActionRequiredForJWSCompletionException;
import com.nimbusds.jose.CompletableJWSObjectSigning;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.JWSSigner;
import com.nimbusds.jose.JWSSignerOption;
import com.nimbusds.jose.crypto.impl.AlgorithmSupportMessage;
import com.nimbusds.jose.crypto.impl.ECDSA;
import com.nimbusds.jose.crypto.impl.ECDSAProvider;
import com.nimbusds.jose.crypto.opts.UserAuthenticationRequired;
import com.nimbusds.jose.jwk.Curve;
import com.nimbusds.jose.jwk.ECKey;
import com.nimbusds.jose.shaded.jcip.ThreadSafe;
import com.nimbusds.jose.util.Base64URL;
import java.security.InvalidKeyException;
import java.security.PrivateKey;
import java.security.Signature;
import java.security.SignatureException;
import java.security.interfaces.ECPrivateKey;
import java.util.Collections;
import java.util.Set;

@ThreadSafe
public class ECDSASigner extends ECDSAProvider implements JWSSigner {
   private final PrivateKey privateKey;
   private final Set<JWSSignerOption> opts;

   public ECDSASigner(ECPrivateKey privateKey) throws JOSEException {
      this(privateKey, Collections.emptySet());
   }

   public ECDSASigner(ECPrivateKey privateKey, Set<JWSSignerOption> opts) throws JOSEException {
      super(ECDSA.resolveAlgorithm(privateKey));
      this.privateKey = privateKey;
      this.opts = opts != null ? opts : Collections.emptySet();
   }

   public ECDSASigner(PrivateKey privateKey, Curve curve) throws JOSEException {
      this(privateKey, curve, Collections.emptySet());
   }

   public ECDSASigner(PrivateKey privateKey, Curve curve, Set<JWSSignerOption> opts) throws JOSEException {
      super(ECDSA.resolveAlgorithm(curve));
      if (!"EC".equalsIgnoreCase(privateKey.getAlgorithm())) {
         throw new IllegalArgumentException("The private key algorithm must be EC");
      } else {
         this.privateKey = privateKey;
         this.opts = opts != null ? opts : Collections.emptySet();
      }
   }

   public ECDSASigner(ECKey ecJWK) throws JOSEException {
      this(ecJWK, null);
   }

   public ECDSASigner(ECKey ecJWK, Set<JWSSignerOption> opts) throws JOSEException {
      super(ECDSA.resolveAlgorithm(ecJWK.getCurve()));
      if (!ecJWK.isPrivate()) {
         throw new JOSEException("The EC JWK doesn't contain a private part");
      } else {
         this.privateKey = ecJWK.toPrivateKey();
         this.opts = opts != null ? opts : Collections.emptySet();
      }
   }

   public PrivateKey getPrivateKey() {
      return this.privateKey;
   }

   @Override
   public Base64URL sign(final JWSHeader header, final byte[] signingInput) throws JOSEException {
      JWSAlgorithm alg = header.getAlgorithm();
      if (!this.supportedJWSAlgorithms().contains(alg)) {
         throw new JOSEException(AlgorithmSupportMessage.unsupportedJWSAlgorithm(alg, this.supportedJWSAlgorithms()));
      } else {
         byte[] jcaSignature;
         try {
            final Signature dsa = ECDSA.getSignerAndVerifier(alg, this.getJCAContext().getProvider());
            dsa.initSign(this.privateKey, this.getJCAContext().getSecureRandom());
            if (this.opts.contains(UserAuthenticationRequired.getInstance())) {
               throw new ActionRequiredForJWSCompletionException(
                  "Authenticate user to complete signing", UserAuthenticationRequired.getInstance(), new CompletableJWSObjectSigning() {
                     @Override
                     public Signature getInitializedSignature() {
                        return dsa;
                     }

                     @Override
                     public Base64URL complete() throws JOSEException {
                        try {
                           dsa.update(signingInput);
                           byte[] jcaSignaturex = dsa.sign();
                           int rsByteArrayLength = ECDSA.getSignatureByteArrayLength(header.getAlgorithm());
                           byte[] jwsSignature = ECDSA.transcodeSignatureToConcat(jcaSignaturex, rsByteArrayLength);
                           return Base64URL.encode(jwsSignature);
                        } catch (SignatureException var4) {
                           throw new JOSEException(var4.getMessage(), var4);
                        }
                     }
                  }
               );
            }

            dsa.update(signingInput);
            jcaSignature = dsa.sign();
         } catch (SignatureException | InvalidKeyException var7) {
            throw new JOSEException(var7.getMessage(), var7);
         }

         int rsByteArrayLength = ECDSA.getSignatureByteArrayLength(header.getAlgorithm());
         byte[] jwsSignature = ECDSA.transcodeSignatureToConcat(jcaSignature, rsByteArrayLength);
         return Base64URL.encode(jwsSignature);
      }
   }
}
