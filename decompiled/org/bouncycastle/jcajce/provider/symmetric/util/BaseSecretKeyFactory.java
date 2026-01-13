package org.bouncycastle.jcajce.provider.symmetric.util;

import java.lang.reflect.Constructor;
import java.security.InvalidKeyException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactorySpi;
import javax.crypto.spec.SecretKeySpec;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;

public class BaseSecretKeyFactory extends SecretKeyFactorySpi implements PBE {
   protected String algName;
   protected ASN1ObjectIdentifier algOid;

   protected BaseSecretKeyFactory(String var1, ASN1ObjectIdentifier var2) {
      this.algName = var1;
      this.algOid = var2;
   }

   @Override
   protected SecretKey engineGenerateSecret(KeySpec var1) throws InvalidKeySpecException {
      if (var1 instanceof SecretKeySpec) {
         return new SecretKeySpec(((SecretKeySpec)var1).getEncoded(), this.algName);
      } else {
         throw new InvalidKeySpecException("Invalid KeySpec");
      }
   }

   @Override
   protected KeySpec engineGetKeySpec(SecretKey var1, Class var2) throws InvalidKeySpecException {
      if (var2 == null) {
         throw new InvalidKeySpecException("keySpec parameter is null");
      } else if (var1 == null) {
         throw new InvalidKeySpecException("key parameter is null");
      } else if (SecretKeySpec.class.isAssignableFrom(var2)) {
         return new SecretKeySpec(var1.getEncoded(), this.algName);
      } else {
         try {
            Class[] var3 = new Class[]{byte[].class};
            Constructor var4 = var2.getConstructor(var3);
            Object[] var5 = new Object[]{var1.getEncoded()};
            return (KeySpec)var4.newInstance(var5);
         } catch (Exception var6) {
            throw new InvalidKeySpecException(var6.toString());
         }
      }
   }

   @Override
   protected SecretKey engineTranslateKey(SecretKey var1) throws InvalidKeyException {
      if (var1 == null) {
         throw new InvalidKeyException("key parameter is null");
      } else if (!var1.getAlgorithm().equalsIgnoreCase(this.algName)) {
         throw new InvalidKeyException("Key not of type " + this.algName + ".");
      } else {
         return new SecretKeySpec(var1.getEncoded(), this.algName);
      }
   }
}
