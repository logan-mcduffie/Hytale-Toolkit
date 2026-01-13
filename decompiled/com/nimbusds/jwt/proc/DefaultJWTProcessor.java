package com.nimbusds.jwt.proc;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWEDecrypter;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.JWSVerifier;
import com.nimbusds.jose.KeySourceException;
import com.nimbusds.jose.crypto.factories.DefaultJWEDecrypterFactory;
import com.nimbusds.jose.crypto.factories.DefaultJWSVerifierFactory;
import com.nimbusds.jose.proc.BadJOSEException;
import com.nimbusds.jose.proc.BadJWEException;
import com.nimbusds.jose.proc.BadJWSException;
import com.nimbusds.jose.proc.DefaultJOSEObjectTypeVerifier;
import com.nimbusds.jose.proc.JOSEObjectTypeVerifier;
import com.nimbusds.jose.proc.JWEDecrypterFactory;
import com.nimbusds.jose.proc.JWEKeySelector;
import com.nimbusds.jose.proc.JWSKeySelector;
import com.nimbusds.jose.proc.JWSVerifierFactory;
import com.nimbusds.jose.proc.SecurityContext;
import com.nimbusds.jwt.EncryptedJWT;
import com.nimbusds.jwt.JWT;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.JWTParser;
import com.nimbusds.jwt.PlainJWT;
import com.nimbusds.jwt.SignedJWT;
import java.security.Key;
import java.text.ParseException;
import java.util.List;
import java.util.ListIterator;

public class DefaultJWTProcessor<C extends SecurityContext> implements ConfigurableJWTProcessor<C> {
   private JOSEObjectTypeVerifier<C> jwsTypeVerifier = DefaultJOSEObjectTypeVerifier.JWT;
   private JOSEObjectTypeVerifier<C> jweTypeVerifier = DefaultJOSEObjectTypeVerifier.JWT;
   private JWSKeySelector<C> jwsKeySelector;
   private JWTClaimsSetAwareJWSKeySelector<C> claimsSetAwareJWSKeySelector;
   private JWEKeySelector<C> jweKeySelector;
   private JWSVerifierFactory jwsVerifierFactory = new DefaultJWSVerifierFactory();
   private JWEDecrypterFactory jweDecrypterFactory = new DefaultJWEDecrypterFactory();
   private JWTClaimsSetVerifier<C> claimsVerifier = new DefaultJWTClaimsVerifier<>(null, null);

   @Override
   public JOSEObjectTypeVerifier<C> getJWSTypeVerifier() {
      return this.jwsTypeVerifier;
   }

   @Override
   public void setJWSTypeVerifier(JOSEObjectTypeVerifier<C> jwsTypeVerifier) {
      this.jwsTypeVerifier = jwsTypeVerifier;
   }

   @Override
   public JWSKeySelector<C> getJWSKeySelector() {
      return this.jwsKeySelector;
   }

   @Override
   public void setJWSKeySelector(JWSKeySelector<C> jwsKeySelector) {
      this.jwsKeySelector = jwsKeySelector;
   }

   @Override
   public JWTClaimsSetAwareJWSKeySelector<C> getJWTClaimsSetAwareJWSKeySelector() {
      return this.claimsSetAwareJWSKeySelector;
   }

   @Override
   public void setJWTClaimsSetAwareJWSKeySelector(JWTClaimsSetAwareJWSKeySelector<C> jwsKeySelector) {
      this.claimsSetAwareJWSKeySelector = jwsKeySelector;
   }

   @Override
   public JOSEObjectTypeVerifier<C> getJWETypeVerifier() {
      return this.jweTypeVerifier;
   }

   @Override
   public void setJWETypeVerifier(JOSEObjectTypeVerifier<C> jweTypeVerifier) {
      this.jweTypeVerifier = jweTypeVerifier;
   }

   @Override
   public JWEKeySelector<C> getJWEKeySelector() {
      return this.jweKeySelector;
   }

   @Override
   public void setJWEKeySelector(JWEKeySelector<C> jweKeySelector) {
      this.jweKeySelector = jweKeySelector;
   }

   @Override
   public JWSVerifierFactory getJWSVerifierFactory() {
      return this.jwsVerifierFactory;
   }

   @Override
   public void setJWSVerifierFactory(JWSVerifierFactory factory) {
      this.jwsVerifierFactory = factory;
   }

   @Override
   public JWEDecrypterFactory getJWEDecrypterFactory() {
      return this.jweDecrypterFactory;
   }

   @Override
   public void setJWEDecrypterFactory(JWEDecrypterFactory factory) {
      this.jweDecrypterFactory = factory;
   }

   @Override
   public JWTClaimsSetVerifier<C> getJWTClaimsSetVerifier() {
      return this.claimsVerifier;
   }

   @Override
   public void setJWTClaimsSetVerifier(JWTClaimsSetVerifier<C> claimsVerifier) {
      this.claimsVerifier = claimsVerifier;
   }

   protected JWTClaimsSet extractJWTClaimsSet(JWT jwt) throws BadJWTException {
      try {
         return jwt.getJWTClaimsSet();
      } catch (ParseException var3) {
         throw new BadJWTException(var3.getMessage(), var3);
      }
   }

   protected JWTClaimsSet verifyJWTClaimsSet(JWTClaimsSet claimsSet, C context) throws BadJWTException {
      if (this.getJWTClaimsSetVerifier() != null) {
         this.getJWTClaimsSetVerifier().verify(claimsSet, context);
      }

      return claimsSet;
   }

   protected List<? extends Key> selectKeys(JWSHeader header, JWTClaimsSet claimsSet, C context) throws KeySourceException, BadJOSEException {
      if (this.getJWTClaimsSetAwareJWSKeySelector() != null) {
         return this.getJWTClaimsSetAwareJWSKeySelector().selectKeys(header, claimsSet, context);
      } else if (this.getJWSKeySelector() != null) {
         return this.getJWSKeySelector().selectJWSKeys(header, context);
      } else {
         throw new BadJOSEException("Signed JWT rejected: No JWS key selector is configured");
      }
   }

   @Override
   public JWTClaimsSet process(String jwtString, C context) throws ParseException, BadJOSEException, JOSEException {
      return this.process(JWTParser.parse(jwtString), context);
   }

   @Override
   public JWTClaimsSet process(JWT jwt, C context) throws BadJOSEException, JOSEException {
      if (jwt instanceof SignedJWT) {
         return this.process((SignedJWT)jwt, context);
      } else if (jwt instanceof EncryptedJWT) {
         return this.process((EncryptedJWT)jwt, context);
      } else if (jwt instanceof PlainJWT) {
         return this.process((PlainJWT)jwt, context);
      } else {
         throw new JOSEException("Unexpected JWT object type: " + jwt.getClass());
      }
   }

   @Override
   public JWTClaimsSet process(PlainJWT plainJWT, C context) throws BadJOSEException, JOSEException {
      if (this.jwsTypeVerifier == null) {
         throw new BadJOSEException("Plain JWT rejected: No JWS header typ (type) verifier is configured");
      } else {
         this.jwsTypeVerifier.verify(plainJWT.getHeader().getType(), context);
         throw new BadJOSEException("Unsecured (plain) JWTs are rejected, extend class to handle");
      }
   }

   @Override
   public JWTClaimsSet process(SignedJWT signedJWT, C context) throws BadJOSEException, JOSEException {
      if (this.jwsTypeVerifier == null) {
         throw new BadJOSEException("Signed JWT rejected: No JWS header typ (type) verifier is configured");
      } else {
         this.jwsTypeVerifier.verify(signedJWT.getHeader().getType(), context);
         if (this.getJWSKeySelector() == null && this.getJWTClaimsSetAwareJWSKeySelector() == null) {
            throw new BadJOSEException("Signed JWT rejected: No JWS key selector is configured");
         } else if (this.getJWSVerifierFactory() == null) {
            throw new JOSEException("No JWS verifier is configured");
         } else {
            JWTClaimsSet claimsSet = this.extractJWTClaimsSet(signedJWT);
            List<? extends Key> keyCandidates = this.selectKeys(signedJWT.getHeader(), claimsSet, context);
            if (keyCandidates != null && !keyCandidates.isEmpty()) {
               ListIterator<? extends Key> it = keyCandidates.listIterator();

               while (it.hasNext()) {
                  JWSVerifier verifier = this.getJWSVerifierFactory().createJWSVerifier(signedJWT.getHeader(), it.next());
                  if (verifier != null) {
                     boolean validSignature = signedJWT.verify(verifier);
                     if (validSignature) {
                        return this.verifyJWTClaimsSet(claimsSet, context);
                     }

                     if (!it.hasNext()) {
                        throw new BadJWSException("Signed JWT rejected: Invalid signature");
                     }
                  }
               }

               throw new BadJOSEException("JWS object rejected: No matching verifier(s) found");
            } else {
               throw new BadJOSEException("Signed JWT rejected: Another algorithm expected, or no matching key(s) found");
            }
         }
      }
   }

   @Override
   public JWTClaimsSet process(EncryptedJWT encryptedJWT, C context) throws BadJOSEException, JOSEException {
      if (this.jweTypeVerifier == null) {
         throw new BadJOSEException("Encrypted JWT rejected: No JWE header typ (type) verifier is configured");
      } else {
         this.jweTypeVerifier.verify(encryptedJWT.getHeader().getType(), context);
         if (this.getJWEKeySelector() == null) {
            throw new BadJOSEException("Encrypted JWT rejected: No JWE key selector is configured");
         } else if (this.getJWEDecrypterFactory() == null) {
            throw new JOSEException("No JWE decrypter is configured");
         } else {
            List<? extends Key> keyCandidates = this.getJWEKeySelector().selectJWEKeys(encryptedJWT.getHeader(), context);
            if (keyCandidates != null && !keyCandidates.isEmpty()) {
               ListIterator<? extends Key> it = keyCandidates.listIterator();

               while (true) {
                  if (!it.hasNext()) {
                     throw new BadJOSEException("Encrypted JWT rejected: No matching decrypter(s) found");
                  }

                  JWEDecrypter decrypter = this.getJWEDecrypterFactory().createJWEDecrypter(encryptedJWT.getHeader(), it.next());
                  if (decrypter != null) {
                     try {
                        encryptedJWT.decrypt(decrypter);
                        break;
                     } catch (JOSEException var7) {
                        if (!it.hasNext()) {
                           throw new BadJWEException("Encrypted JWT rejected: " + var7.getMessage(), var7);
                        }
                     }
                  }
               }

               if ("JWT".equalsIgnoreCase(encryptedJWT.getHeader().getContentType())) {
                  SignedJWT signedJWTPayload = encryptedJWT.getPayload().toSignedJWT();
                  if (signedJWTPayload == null) {
                     throw new BadJWTException("The payload is not a nested signed JWT");
                  } else {
                     return this.process(signedJWTPayload, context);
                  }
               } else {
                  JWTClaimsSet claimsSet = this.extractJWTClaimsSet(encryptedJWT);
                  return this.verifyJWTClaimsSet(claimsSet, context);
               }
            } else {
               throw new BadJOSEException("Encrypted JWT rejected: Another algorithm expected, or no matching key(s) found");
            }
         }
      }
   }
}
