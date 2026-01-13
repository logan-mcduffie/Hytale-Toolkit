package com.nimbusds.jose.crypto;

import com.nimbusds.jose.CriticalHeaderParamsAware;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.JWSVerifier;
import com.nimbusds.jose.crypto.impl.AlgorithmSupportMessage;
import com.nimbusds.jose.crypto.impl.CriticalHeaderParamsDeferral;
import com.nimbusds.jose.crypto.impl.ECDSA;
import com.nimbusds.jose.crypto.impl.ECDSAProvider;
import com.nimbusds.jose.crypto.utils.ECChecks;
import com.nimbusds.jose.jwk.Curve;
import com.nimbusds.jose.jwk.ECKey;
import com.nimbusds.jose.shaded.jcip.ThreadSafe;
import com.nimbusds.jose.util.Base64URL;
import java.security.InvalidKeyException;
import java.security.Signature;
import java.security.SignatureException;
import java.security.interfaces.ECPublicKey;
import java.util.Set;

@ThreadSafe
public class ECDSAVerifier extends ECDSAProvider implements JWSVerifier, CriticalHeaderParamsAware {
   private final CriticalHeaderParamsDeferral critPolicy = new CriticalHeaderParamsDeferral();
   private final ECPublicKey publicKey;

   public ECDSAVerifier(ECPublicKey publicKey) throws JOSEException {
      this(publicKey, null);
   }

   public ECDSAVerifier(ECKey ecJWK) throws JOSEException {
      this(ecJWK.toECPublicKey());
   }

   public ECDSAVerifier(ECPublicKey publicKey, Set<String> defCritHeaders) throws JOSEException {
      super(ECDSA.resolveAlgorithm(publicKey));
      this.publicKey = publicKey;
      if (!ECChecks.isPointOnCurve(publicKey, Curve.forJWSAlgorithm(this.supportedECDSAAlgorithm()).iterator().next().toECParameterSpec())) {
         throw new JOSEException("Curve / public key parameters mismatch");
      } else {
         this.critPolicy.setDeferredCriticalHeaderParams(defCritHeaders);
      }
   }

   public ECPublicKey getPublicKey() {
      return this.publicKey;
   }

   @Override
   public Set<String> getProcessedCriticalHeaderParams() {
      return this.critPolicy.getProcessedCriticalHeaderParams();
   }

   @Override
   public Set<String> getDeferredCriticalHeaderParams() {
      return this.critPolicy.getProcessedCriticalHeaderParams();
   }

   @Override
   public boolean verify(JWSHeader header, byte[] signedContent, Base64URL signature) throws JOSEException {
      JWSAlgorithm alg = header.getAlgorithm();
      if (!this.supportedJWSAlgorithms().contains(alg)) {
         throw new JOSEException(AlgorithmSupportMessage.unsupportedJWSAlgorithm(alg, this.supportedJWSAlgorithms()));
      } else if (!this.critPolicy.headerPasses(header)) {
         return false;
      } else {
         byte[] jwsSignature = signature.decode();

         try {
            ECDSA.ensureLegalSignature(jwsSignature, alg);
         } catch (JOSEException var12) {
            return false;
         }

         byte[] derSignature;
         try {
            derSignature = ECDSA.transcodeSignatureToDER(jwsSignature);
         } catch (JOSEException var11) {
            return false;
         }

         Signature sig = ECDSA.getSignerAndVerifier(alg, this.getJCAContext().getProvider());

         try {
            sig.initVerify(this.publicKey);
            sig.update(signedContent);
            return sig.verify(derSignature);
         } catch (InvalidKeyException var9) {
            throw new JOSEException("Invalid EC public key: " + var9.getMessage(), var9);
         } catch (SignatureException var10) {
            return false;
         }
      }
   }
}
