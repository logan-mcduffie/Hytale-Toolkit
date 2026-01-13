package com.nimbusds.jose.crypto;

import com.google.crypto.tink.subtle.Ed25519Sign;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.JWSSigner;
import com.nimbusds.jose.crypto.impl.EdDSAProvider;
import com.nimbusds.jose.jwk.Curve;
import com.nimbusds.jose.jwk.OctetKeyPair;
import com.nimbusds.jose.shaded.jcip.ThreadSafe;
import com.nimbusds.jose.util.Base64URL;
import java.security.GeneralSecurityException;

@ThreadSafe
public class Ed25519Signer extends EdDSAProvider implements JWSSigner {
   private final OctetKeyPair privateKey;
   private final Ed25519Sign tinkSigner;

   public Ed25519Signer(OctetKeyPair privateKey) throws JOSEException {
      if (!Curve.Ed25519.equals(privateKey.getCurve())) {
         throw new JOSEException("Ed25519Signer only supports OctetKeyPairs with crv=Ed25519");
      } else if (!privateKey.isPrivate()) {
         throw new JOSEException("The OctetKeyPair doesn't contain a private part");
      } else {
         this.privateKey = privateKey;

         try {
            this.tinkSigner = new Ed25519Sign(privateKey.getDecodedD());
         } catch (GeneralSecurityException var3) {
            throw new JOSEException(var3.getMessage(), var3);
         }
      }
   }

   public OctetKeyPair getPrivateKey() {
      return this.privateKey;
   }

   @Override
   public Base64URL sign(JWSHeader header, byte[] signingInput) throws JOSEException {
      JWSAlgorithm alg = header.getAlgorithm();
      if (!JWSAlgorithm.Ed25519.equals(alg) && !JWSAlgorithm.EdDSA.equals(alg)) {
         throw new JOSEException("Ed25519Verifier requires alg=Ed25519 or alg=EdDSA in JWSHeader");
      } else {
         byte[] jwsSignature;
         try {
            jwsSignature = this.tinkSigner.sign(signingInput);
         } catch (GeneralSecurityException var6) {
            throw new JOSEException(var6.getMessage(), var6);
         }

         return Base64URL.encode(jwsSignature);
      }
   }
}
