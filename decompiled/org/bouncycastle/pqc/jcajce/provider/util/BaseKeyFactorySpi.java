package org.bouncycastle.pqc.jcajce.provider.util;

import java.security.KeyFactorySpi;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Set;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.jcajce.provider.util.AsymmetricKeyInfoConverter;

public abstract class BaseKeyFactorySpi extends KeyFactorySpi implements AsymmetricKeyInfoConverter {
   private final Set<ASN1ObjectIdentifier> keyOids;
   private final ASN1ObjectIdentifier keyOid;

   protected BaseKeyFactorySpi(Set<ASN1ObjectIdentifier> var1) {
      this.keyOid = null;
      this.keyOids = var1;
   }

   protected BaseKeyFactorySpi(ASN1ObjectIdentifier var1) {
      this.keyOid = var1;
      this.keyOids = null;
   }

   @Override
   public PrivateKey engineGeneratePrivate(KeySpec var1) throws InvalidKeySpecException {
      if (var1 instanceof PKCS8EncodedKeySpec) {
         byte[] var2 = ((PKCS8EncodedKeySpec)var1).getEncoded();

         try {
            PrivateKeyInfo var3 = PrivateKeyInfo.getInstance(var2);
            this.checkAlgorithm(var3.getPrivateKeyAlgorithm().getAlgorithm());
            return this.generatePrivate(var3);
         } catch (InvalidKeySpecException var4) {
            throw var4;
         } catch (IllegalStateException var5) {
            throw new InvalidKeySpecException(var5.getMessage());
         } catch (Exception var6) {
            throw new InvalidKeySpecException(var6.toString());
         }
      } else {
         throw new InvalidKeySpecException("Unsupported key specification: " + var1.getClass() + ".");
      }
   }

   @Override
   public PublicKey engineGeneratePublic(KeySpec var1) throws InvalidKeySpecException {
      if (var1 instanceof X509EncodedKeySpec) {
         byte[] var2 = ((X509EncodedKeySpec)var1).getEncoded();

         try {
            SubjectPublicKeyInfo var3 = SubjectPublicKeyInfo.getInstance(var2);
            this.checkAlgorithm(var3.getAlgorithm().getAlgorithm());
            return this.generatePublic(var3);
         } catch (InvalidKeySpecException var4) {
            throw var4;
         } catch (Exception var5) {
            throw new InvalidKeySpecException(var5.toString());
         }
      } else {
         throw new InvalidKeySpecException("Unknown key specification: " + var1 + ".");
      }
   }

   private void checkAlgorithm(ASN1ObjectIdentifier var1) throws InvalidKeySpecException {
      if (this.keyOid != null) {
         if (!this.keyOid.equals(var1)) {
            throw new InvalidKeySpecException("incorrect algorithm OID for key: " + var1);
         }
      } else if (!this.keyOids.contains(var1)) {
         throw new InvalidKeySpecException("incorrect algorithm OID for key: " + var1);
      }
   }
}
