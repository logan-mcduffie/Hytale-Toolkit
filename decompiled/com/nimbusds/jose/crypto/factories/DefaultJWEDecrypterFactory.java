package com.nimbusds.jose.crypto.factories;

import com.nimbusds.jose.EncryptionMethod;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWEAlgorithm;
import com.nimbusds.jose.JWEDecrypter;
import com.nimbusds.jose.JWEHeader;
import com.nimbusds.jose.KeyLengthException;
import com.nimbusds.jose.KeyTypeException;
import com.nimbusds.jose.crypto.AESDecrypter;
import com.nimbusds.jose.crypto.DirectDecrypter;
import com.nimbusds.jose.crypto.ECDHDecrypter;
import com.nimbusds.jose.crypto.PasswordBasedDecrypter;
import com.nimbusds.jose.crypto.RSADecrypter;
import com.nimbusds.jose.jca.JWEJCAContext;
import com.nimbusds.jose.jwk.Curve;
import com.nimbusds.jose.proc.JWEDecrypterFactory;
import com.nimbusds.jose.shaded.jcip.ThreadSafe;
import java.security.Key;
import java.security.PrivateKey;
import java.security.interfaces.ECKey;
import java.security.interfaces.RSAKey;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;
import javax.crypto.SecretKey;

@ThreadSafe
public class DefaultJWEDecrypterFactory implements JWEDecrypterFactory {
   public static final Set<JWEAlgorithm> SUPPORTED_ALGORITHMS;
   public static final Set<EncryptionMethod> SUPPORTED_ENCRYPTION_METHODS;
   private final JWEJCAContext jcaContext = new JWEJCAContext();

   @Override
   public Set<JWEAlgorithm> supportedJWEAlgorithms() {
      return SUPPORTED_ALGORITHMS;
   }

   @Override
   public Set<EncryptionMethod> supportedEncryptionMethods() {
      return SUPPORTED_ENCRYPTION_METHODS;
   }

   public JWEJCAContext getJCAContext() {
      return this.jcaContext;
   }

   @Override
   public JWEDecrypter createJWEDecrypter(JWEHeader header, Key key) throws JOSEException {
      JWEDecrypter decrypter;
      if (RSADecrypter.SUPPORTED_ALGORITHMS.contains(header.getAlgorithm()) && RSADecrypter.SUPPORTED_ENCRYPTION_METHODS.contains(header.getEncryptionMethod())
         )
       {
         if (!(key instanceof PrivateKey) || !(key instanceof RSAKey)) {
            throw new KeyTypeException(PrivateKey.class, RSAKey.class);
         }

         PrivateKey rsaPrivateKey = (PrivateKey)key;
         decrypter = new RSADecrypter(rsaPrivateKey);
      } else if (ECDHDecrypter.SUPPORTED_ALGORITHMS.contains(header.getAlgorithm())
         && ECDHDecrypter.SUPPORTED_ENCRYPTION_METHODS.contains(header.getEncryptionMethod())) {
         if (!(key instanceof PrivateKey) || !(key instanceof ECKey)) {
            throw new KeyTypeException(PrivateKey.class, ECKey.class);
         }

         PrivateKey ecPrivateKey = (PrivateKey)key;
         Curve curve = Curve.forECParameterSpec(((ECKey)key).getParams());
         decrypter = new ECDHDecrypter(ecPrivateKey, null, curve);
      } else if (DirectDecrypter.SUPPORTED_ALGORITHMS.contains(header.getAlgorithm())
         && DirectDecrypter.SUPPORTED_ENCRYPTION_METHODS.contains(header.getEncryptionMethod())) {
         if (!(key instanceof SecretKey)) {
            throw new KeyTypeException(SecretKey.class);
         }

         SecretKey aesKey = (SecretKey)key;
         DirectDecrypter directDecrypter = new DirectDecrypter(aesKey);
         if (!directDecrypter.supportedEncryptionMethods().contains(header.getEncryptionMethod())) {
            throw new KeyLengthException(header.getEncryptionMethod().cekBitLength(), header.getEncryptionMethod());
         }

         decrypter = directDecrypter;
      } else if (AESDecrypter.SUPPORTED_ALGORITHMS.contains(header.getAlgorithm())
         && AESDecrypter.SUPPORTED_ENCRYPTION_METHODS.contains(header.getEncryptionMethod())) {
         if (!(key instanceof SecretKey)) {
            throw new KeyTypeException(SecretKey.class);
         }

         SecretKey aesKey = (SecretKey)key;
         AESDecrypter aesDecrypter = new AESDecrypter(aesKey);
         if (!aesDecrypter.supportedJWEAlgorithms().contains(header.getAlgorithm())) {
            throw new KeyLengthException(header.getAlgorithm());
         }

         decrypter = aesDecrypter;
      } else {
         if (!PasswordBasedDecrypter.SUPPORTED_ALGORITHMS.contains(header.getAlgorithm())
            || !PasswordBasedDecrypter.SUPPORTED_ENCRYPTION_METHODS.contains(header.getEncryptionMethod())) {
            throw new JOSEException("Unsupported JWE algorithm or encryption method");
         }

         if (!(key instanceof SecretKey)) {
            throw new KeyTypeException(SecretKey.class);
         }

         byte[] password = key.getEncoded();
         decrypter = new PasswordBasedDecrypter(password);
      }

      decrypter.getJCAContext().setSecureRandom(this.jcaContext.getSecureRandom());
      decrypter.getJCAContext().setProvider(this.jcaContext.getProvider());
      decrypter.getJCAContext().setKeyEncryptionProvider(this.jcaContext.getKeyEncryptionProvider());
      decrypter.getJCAContext().setMACProvider(this.jcaContext.getMACProvider());
      decrypter.getJCAContext().setContentEncryptionProvider(this.jcaContext.getContentEncryptionProvider());
      return decrypter;
   }

   static {
      Set<JWEAlgorithm> algs = new LinkedHashSet<>();
      algs.addAll(RSADecrypter.SUPPORTED_ALGORITHMS);
      algs.addAll(ECDHDecrypter.SUPPORTED_ALGORITHMS);
      algs.addAll(DirectDecrypter.SUPPORTED_ALGORITHMS);
      algs.addAll(AESDecrypter.SUPPORTED_ALGORITHMS);
      algs.addAll(PasswordBasedDecrypter.SUPPORTED_ALGORITHMS);
      SUPPORTED_ALGORITHMS = Collections.unmodifiableSet(algs);
      Set<EncryptionMethod> encs = new LinkedHashSet<>();
      encs.addAll(RSADecrypter.SUPPORTED_ENCRYPTION_METHODS);
      encs.addAll(ECDHDecrypter.SUPPORTED_ENCRYPTION_METHODS);
      encs.addAll(DirectDecrypter.SUPPORTED_ENCRYPTION_METHODS);
      encs.addAll(AESDecrypter.SUPPORTED_ENCRYPTION_METHODS);
      encs.addAll(PasswordBasedDecrypter.SUPPORTED_ENCRYPTION_METHODS);
      SUPPORTED_ENCRYPTION_METHODS = Collections.unmodifiableSet(encs);
   }
}
