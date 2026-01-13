package com.nimbusds.jose.crypto;

import com.nimbusds.jose.CriticalHeaderParamsAware;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.JWSVerifier;
import com.nimbusds.jose.crypto.impl.CriticalHeaderParamsDeferral;
import com.nimbusds.jose.crypto.impl.HMAC;
import com.nimbusds.jose.crypto.impl.MACProvider;
import com.nimbusds.jose.crypto.utils.ConstantTimeUtils;
import com.nimbusds.jose.jwk.OctetSequenceKey;
import com.nimbusds.jose.shaded.jcip.ThreadSafe;
import com.nimbusds.jose.util.Base64URL;
import com.nimbusds.jose.util.StandardCharset;
import java.util.Set;
import javax.crypto.SecretKey;

@ThreadSafe
public class MACVerifier extends MACProvider implements JWSVerifier, CriticalHeaderParamsAware {
   private final CriticalHeaderParamsDeferral critPolicy = new CriticalHeaderParamsDeferral();

   public MACVerifier(byte[] secret) throws JOSEException {
      super(secret);
   }

   public MACVerifier(String secretString) throws JOSEException {
      this(secretString.getBytes(StandardCharset.UTF_8));
   }

   public MACVerifier(SecretKey secretKey) throws JOSEException {
      this(secretKey, null);
   }

   public MACVerifier(OctetSequenceKey jwk) throws JOSEException {
      this(jwk.toByteArray());
   }

   public MACVerifier(byte[] secret, Set<String> defCritHeaders) throws JOSEException {
      super(secret);
      this.critPolicy.setDeferredCriticalHeaderParams(defCritHeaders);
   }

   public MACVerifier(SecretKey secretKey, Set<String> defCritHeaders) throws JOSEException {
      super(secretKey);
      this.critPolicy.setDeferredCriticalHeaderParams(defCritHeaders);
   }

   public MACVerifier(OctetSequenceKey jwk, Set<String> defCritHeaders) throws JOSEException {
      this(jwk.toByteArray(), defCritHeaders);
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
      this.ensureSecretLengthSatisfiesAlgorithm(header.getAlgorithm());
      if (!this.critPolicy.headerPasses(header)) {
         return false;
      } else {
         String jcaAlg = getJCAAlgorithmName(header.getAlgorithm());
         byte[] expectedHMAC = HMAC.compute(jcaAlg, this.getSecretKey(), signedContent, this.getJCAContext().getProvider());
         return ConstantTimeUtils.areEqual(expectedHMAC, signature.decode());
      }
   }
}
