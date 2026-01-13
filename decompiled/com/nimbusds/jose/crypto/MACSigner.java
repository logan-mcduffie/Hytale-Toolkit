package com.nimbusds.jose.crypto;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.JWSSigner;
import com.nimbusds.jose.KeyLengthException;
import com.nimbusds.jose.crypto.impl.HMAC;
import com.nimbusds.jose.crypto.impl.MACProvider;
import com.nimbusds.jose.jwk.OctetSequenceKey;
import com.nimbusds.jose.shaded.jcip.ThreadSafe;
import com.nimbusds.jose.util.Base64URL;
import com.nimbusds.jose.util.StandardCharset;
import javax.crypto.SecretKey;

@ThreadSafe
public class MACSigner extends MACProvider implements JWSSigner {
   public MACSigner(byte[] secret) throws KeyLengthException {
      super(secret);
   }

   public MACSigner(String secretString) throws KeyLengthException {
      this(secretString.getBytes(StandardCharset.UTF_8));
   }

   public MACSigner(SecretKey secretKey) throws KeyLengthException {
      super(secretKey);
   }

   public MACSigner(OctetSequenceKey jwk) throws KeyLengthException {
      this(jwk.toByteArray());
   }

   @Override
   public Base64URL sign(JWSHeader header, byte[] signingInput) throws JOSEException {
      this.ensureSecretLengthSatisfiesAlgorithm(header.getAlgorithm());
      String jcaAlg = getJCAAlgorithmName(header.getAlgorithm());
      byte[] hmac = HMAC.compute(jcaAlg, this.getSecretKey(), signingInput, this.getJCAContext().getProvider());
      return Base64URL.encode(hmac);
   }
}
