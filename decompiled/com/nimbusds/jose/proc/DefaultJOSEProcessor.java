package com.nimbusds.jose.proc;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JOSEObject;
import com.nimbusds.jose.JWEDecrypter;
import com.nimbusds.jose.JWEObject;
import com.nimbusds.jose.JWSObject;
import com.nimbusds.jose.JWSVerifier;
import com.nimbusds.jose.Payload;
import com.nimbusds.jose.PlainObject;
import com.nimbusds.jose.crypto.factories.DefaultJWEDecrypterFactory;
import com.nimbusds.jose.crypto.factories.DefaultJWSVerifierFactory;
import com.nimbusds.jose.shaded.jcip.ThreadSafe;
import java.security.Key;
import java.text.ParseException;
import java.util.List;
import java.util.ListIterator;

@ThreadSafe
public class DefaultJOSEProcessor<C extends SecurityContext> implements ConfigurableJOSEProcessor<C> {
   private JOSEObjectTypeVerifier<C> jwsTypeVerifier = DefaultJOSEObjectTypeVerifier.JOSE;
   private JOSEObjectTypeVerifier<C> jweTypeVerifier = DefaultJOSEObjectTypeVerifier.JOSE;
   private JWSKeySelector<C> jwsKeySelector;
   private JWEKeySelector<C> jweKeySelector;
   private JWSVerifierFactory jwsVerifierFactory = new DefaultJWSVerifierFactory();
   private JWEDecrypterFactory jweDecrypterFactory = new DefaultJWEDecrypterFactory();

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
   public Payload process(String compactJOSE, C context) throws ParseException, BadJOSEException, JOSEException {
      return this.process(JOSEObject.parse(compactJOSE), context);
   }

   @Override
   public Payload process(JOSEObject joseObject, C context) throws BadJOSEException, JOSEException {
      if (joseObject instanceof JWSObject) {
         return this.process((JWSObject)joseObject, context);
      } else if (joseObject instanceof JWEObject) {
         return this.process((JWEObject)joseObject, context);
      } else if (joseObject instanceof PlainObject) {
         return this.process((PlainObject)joseObject, context);
      } else {
         throw new JOSEException("Unexpected JOSE object type: " + joseObject.getClass());
      }
   }

   @Override
   public Payload process(PlainObject plainObject, C context) throws BadJOSEException {
      if (this.jwsTypeVerifier == null) {
         throw new BadJOSEException("Unsecured (plain) JOSE object rejected: No JWS header typ (type) verifier is configured");
      } else {
         this.jwsTypeVerifier.verify(plainObject.getHeader().getType(), context);
         throw new BadJOSEException("Unsecured (plain) JOSE objects are rejected, extend class to handle");
      }
   }

   @Override
   public Payload process(JWSObject jwsObject, C context) throws BadJOSEException, JOSEException {
      if (this.jwsTypeVerifier == null) {
         throw new BadJOSEException("JWS object rejected: No JWS header typ (type) verifier is configured");
      } else {
         this.jwsTypeVerifier.verify(jwsObject.getHeader().getType(), context);
         if (this.getJWSKeySelector() == null) {
            throw new BadJOSEException("JWS object rejected: No JWS key selector is configured");
         } else if (this.getJWSVerifierFactory() == null) {
            throw new JOSEException("No JWS verifier is configured");
         } else {
            List<? extends Key> keyCandidates = this.getJWSKeySelector().selectJWSKeys(jwsObject.getHeader(), context);
            if (keyCandidates != null && !keyCandidates.isEmpty()) {
               ListIterator<? extends Key> it = keyCandidates.listIterator();

               while (it.hasNext()) {
                  JWSVerifier verifier = this.getJWSVerifierFactory().createJWSVerifier(jwsObject.getHeader(), it.next());
                  if (verifier != null) {
                     boolean validSignature = jwsObject.verify(verifier);
                     if (validSignature) {
                        return jwsObject.getPayload();
                     }

                     if (!it.hasNext()) {
                        throw new BadJWSException("JWS object rejected: Invalid signature");
                     }
                  }
               }

               throw new BadJOSEException("JWS object rejected: No matching verifier(s) found");
            } else {
               throw new BadJOSEException("JWS object rejected: Another algorithm expected, or no matching key(s) found");
            }
         }
      }
   }

   @Override
   public Payload process(JWEObject jweObject, C context) throws BadJOSEException, JOSEException {
      if (this.jweTypeVerifier == null) {
         throw new BadJOSEException("JWE object rejected: No JWE header typ (type) verifier is configured");
      } else {
         this.jweTypeVerifier.verify(jweObject.getHeader().getType(), context);
         if (this.getJWEKeySelector() == null) {
            throw new BadJOSEException("JWE object rejected: No JWE key selector is configured");
         } else if (this.getJWEDecrypterFactory() == null) {
            throw new JOSEException("No JWE decrypter is configured");
         } else {
            List<? extends Key> keyCandidates = this.getJWEKeySelector().selectJWEKeys(jweObject.getHeader(), context);
            if (keyCandidates != null && !keyCandidates.isEmpty()) {
               ListIterator<? extends Key> it = keyCandidates.listIterator();

               while (true) {
                  if (!it.hasNext()) {
                     throw new BadJOSEException("JWE object rejected: No matching decrypter(s) found");
                  }

                  JWEDecrypter decrypter = this.getJWEDecrypterFactory().createJWEDecrypter(jweObject.getHeader(), it.next());
                  if (decrypter != null) {
                     try {
                        jweObject.decrypt(decrypter);
                        break;
                     } catch (JOSEException var7) {
                        if (!it.hasNext()) {
                           throw new BadJWEException("JWE object rejected: " + var7.getMessage(), var7);
                        }
                     }
                  }
               }

               if ("JWT".equalsIgnoreCase(jweObject.getHeader().getContentType())) {
                  JWSObject nestedJWS = jweObject.getPayload().toJWSObject();
                  return nestedJWS == null ? jweObject.getPayload() : this.process(nestedJWS, context);
               } else {
                  return jweObject.getPayload();
               }
            } else {
               throw new BadJOSEException("JWE object rejected: Another algorithm expected, or no matching key(s) found");
            }
         }
      }
   }
}
