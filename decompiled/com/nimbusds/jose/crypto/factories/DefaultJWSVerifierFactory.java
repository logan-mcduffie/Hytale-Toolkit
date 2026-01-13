package com.nimbusds.jose.crypto.factories;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.JWSVerifier;
import com.nimbusds.jose.KeyTypeException;
import com.nimbusds.jose.crypto.ECDSAVerifier;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jose.crypto.RSASSAVerifier;
import com.nimbusds.jose.jca.JCAContext;
import com.nimbusds.jose.proc.JWSVerifierFactory;
import com.nimbusds.jose.shaded.jcip.ThreadSafe;
import java.security.Key;
import java.security.interfaces.ECPublicKey;
import java.security.interfaces.RSAPublicKey;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;
import javax.crypto.SecretKey;

@ThreadSafe
public class DefaultJWSVerifierFactory implements JWSVerifierFactory {
   public static final Set<JWSAlgorithm> SUPPORTED_ALGORITHMS;
   private final JCAContext jcaContext = new JCAContext();

   @Override
   public Set<JWSAlgorithm> supportedJWSAlgorithms() {
      return SUPPORTED_ALGORITHMS;
   }

   @Override
   public JCAContext getJCAContext() {
      return this.jcaContext;
   }

   @Override
   public JWSVerifier createJWSVerifier(JWSHeader header, Key key) throws JOSEException {
      JWSVerifier verifier;
      if (MACVerifier.SUPPORTED_ALGORITHMS.contains(header.getAlgorithm())) {
         if (!(key instanceof SecretKey)) {
            throw new KeyTypeException(SecretKey.class);
         }

         SecretKey macKey = (SecretKey)key;
         verifier = new MACVerifier(macKey);
      } else if (RSASSAVerifier.SUPPORTED_ALGORITHMS.contains(header.getAlgorithm())) {
         if (!(key instanceof RSAPublicKey)) {
            throw new KeyTypeException(RSAPublicKey.class);
         }

         RSAPublicKey rsaPublicKey = (RSAPublicKey)key;
         verifier = new RSASSAVerifier(rsaPublicKey);
      } else {
         if (!ECDSAVerifier.SUPPORTED_ALGORITHMS.contains(header.getAlgorithm())) {
            throw new JOSEException("Unsupported JWS algorithm: " + header.getAlgorithm());
         }

         if (!(key instanceof ECPublicKey)) {
            throw new KeyTypeException(ECPublicKey.class);
         }

         ECPublicKey ecPublicKey = (ECPublicKey)key;
         verifier = new ECDSAVerifier(ecPublicKey);
      }

      verifier.getJCAContext().setProvider(this.jcaContext.getProvider());
      return verifier;
   }

   static {
      Set<JWSAlgorithm> algs = new LinkedHashSet<>();
      algs.addAll(MACVerifier.SUPPORTED_ALGORITHMS);
      algs.addAll(RSASSAVerifier.SUPPORTED_ALGORITHMS);
      algs.addAll(ECDSAVerifier.SUPPORTED_ALGORITHMS);
      SUPPORTED_ALGORITHMS = Collections.unmodifiableSet(algs);
   }
}
