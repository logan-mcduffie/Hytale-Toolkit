package org.bouncycastle.jcajce;

import java.io.IOException;
import java.security.Provider;
import java.security.PublicKey;
import java.security.Security;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.internal.asn1.iana.IANAObjectIdentifiers;
import org.bouncycastle.internal.asn1.misc.MiscObjectIdentifiers;
import org.bouncycastle.jcajce.provider.asymmetric.compositesignatures.CompositeIndex;
import org.bouncycastle.jcajce.provider.asymmetric.compositesignatures.KeyFactorySpi;
import org.bouncycastle.pqc.crypto.util.PublicKeyFactory;
import org.bouncycastle.pqc.crypto.util.SubjectPublicKeyInfoFactory;
import org.bouncycastle.util.Arrays;

public class CompositePublicKey implements PublicKey {
   private final List<PublicKey> keys;
   private final List<Provider> providers;
   private final AlgorithmIdentifier algorithmIdentifier;

   public static CompositePublicKey.Builder builder(ASN1ObjectIdentifier var0) {
      return new CompositePublicKey.Builder(new AlgorithmIdentifier(var0));
   }

   public static CompositePublicKey.Builder builder(String var0) {
      return builder(CompositeUtil.getOid(var0));
   }

   public CompositePublicKey(PublicKey... var1) {
      this(MiscObjectIdentifiers.id_composite_key, var1);
   }

   public CompositePublicKey(ASN1ObjectIdentifier var1, PublicKey... var2) {
      this(new AlgorithmIdentifier(var1), var2);
   }

   public CompositePublicKey(AlgorithmIdentifier var1, PublicKey... var2) {
      this.algorithmIdentifier = var1;
      if (var2 != null && var2.length != 0) {
         ArrayList var3 = new ArrayList(var2.length);

         for (int var4 = 0; var4 < var2.length; var4++) {
            var3.add(var2[var4]);
         }

         this.keys = Collections.unmodifiableList(var3);
         this.providers = null;
      } else {
         throw new IllegalArgumentException("at least one public key must be provided for the composite public key");
      }
   }

   public CompositePublicKey(SubjectPublicKeyInfo var1) {
      ASN1ObjectIdentifier var2 = var1.getAlgorithm().getAlgorithm();
      CompositePublicKey var3 = null;

      try {
         if (!CompositeIndex.isAlgorithmSupported(var2)) {
            throw new IllegalStateException("unable to create CompositePublicKey from SubjectPublicKeyInfo");
         }

         KeyFactorySpi var4 = new KeyFactorySpi();
         var3 = (CompositePublicKey)var4.generatePublic(var1);
         if (var3 == null) {
            throw new IllegalStateException("unable to create CompositePublicKey from SubjectPublicKeyInfo");
         }
      } catch (IOException var5) {
         throw new IllegalStateException(var5.getMessage(), var5);
      }

      this.keys = var3.getPublicKeys();
      this.algorithmIdentifier = var3.getAlgorithmIdentifier();
      this.providers = null;
   }

   private CompositePublicKey(AlgorithmIdentifier var1, PublicKey[] var2, Provider[] var3) {
      this.algorithmIdentifier = var1;
      if (var2.length != 2) {
         throw new IllegalArgumentException("two keys required for composite private key");
      } else {
         ArrayList var4 = new ArrayList(var2.length);
         if (var3 == null) {
            for (int var5 = 0; var5 < var2.length; var5++) {
               var4.add(var2[var5]);
            }

            this.providers = null;
         } else {
            ArrayList var7 = new ArrayList(var3.length);

            for (int var6 = 0; var6 < var2.length; var6++) {
               var7.add(var3[var6]);
               var4.add(var2[var6]);
            }

            this.providers = Collections.unmodifiableList(var7);
         }

         this.keys = Collections.unmodifiableList(var4);
      }
   }

   public List<PublicKey> getPublicKeys() {
      return this.keys;
   }

   public List<Provider> getProviders() {
      return this.providers;
   }

   @Override
   public String getAlgorithm() {
      return CompositeIndex.getAlgorithmName(this.algorithmIdentifier.getAlgorithm());
   }

   public AlgorithmIdentifier getAlgorithmIdentifier() {
      return this.algorithmIdentifier;
   }

   @Override
   public String getFormat() {
      return "X.509";
   }

   @Override
   public byte[] getEncoded() {
      if (this.algorithmIdentifier.getAlgorithm().on(IANAObjectIdentifiers.id_alg)) {
         try {
            byte[] var6 = SubjectPublicKeyInfoFactory.createSubjectPublicKeyInfo(PublicKeyFactory.createKey(this.keys.get(0).getEncoded()))
               .getPublicKeyData()
               .getBytes();
            byte[] var7 = org.bouncycastle.crypto.util.SubjectPublicKeyInfoFactory.createSubjectPublicKeyInfo(
                  org.bouncycastle.crypto.util.PublicKeyFactory.createKey(this.keys.get(1).getEncoded())
               )
               .getPublicKeyData()
               .getBytes();
            return new SubjectPublicKeyInfo(this.getAlgorithmIdentifier(), Arrays.concatenate(var6, var7)).getEncoded();
         } catch (IOException var4) {
            throw new IllegalStateException("unable to encode composite public key: " + var4.getMessage());
         }
      } else {
         ASN1EncodableVector var1 = new ASN1EncodableVector();

         for (int var2 = 0; var2 < this.keys.size(); var2++) {
            if (this.algorithmIdentifier.getAlgorithm().equals(MiscObjectIdentifiers.id_composite_key)) {
               var1.add(SubjectPublicKeyInfo.getInstance(this.keys.get(var2).getEncoded()));
            } else {
               SubjectPublicKeyInfo var3 = SubjectPublicKeyInfo.getInstance(this.keys.get(var2).getEncoded());
               var1.add(var3.getPublicKeyData());
            }
         }

         try {
            return new SubjectPublicKeyInfo(this.algorithmIdentifier, new DERSequence(var1)).getEncoded("DER");
         } catch (IOException var5) {
            throw new IllegalStateException("unable to encode composite public key: " + var5.getMessage());
         }
      }
   }

   @Override
   public int hashCode() {
      return this.keys.hashCode();
   }

   @Override
   public boolean equals(Object var1) {
      if (var1 == this) {
         return true;
      } else if (!(var1 instanceof CompositePublicKey)) {
         return false;
      } else {
         boolean var2 = true;
         CompositePublicKey var3 = (CompositePublicKey)var1;
         if (!var3.getAlgorithmIdentifier().equals(this.algorithmIdentifier) || !this.keys.equals(var3.keys)) {
            var2 = false;
         }

         return var2;
      }
   }

   public static class Builder {
      private final AlgorithmIdentifier algorithmIdentifier;
      private final PublicKey[] keys = new PublicKey[2];
      private final Provider[] providers = new Provider[2];
      private int count = 0;

      private Builder(AlgorithmIdentifier var1) {
         this.algorithmIdentifier = var1;
      }

      public CompositePublicKey.Builder addPublicKey(PublicKey var1) {
         return this.addPublicKey(var1, (Provider)null);
      }

      public CompositePublicKey.Builder addPublicKey(PublicKey var1, String var2) {
         return this.addPublicKey(var1, Security.getProvider(var2));
      }

      public CompositePublicKey.Builder addPublicKey(PublicKey var1, Provider var2) {
         if (this.count == this.keys.length) {
            throw new IllegalStateException("only " + this.keys.length + " allowed in composite");
         } else {
            this.keys[this.count] = var1;
            this.providers[this.count++] = var2;
            return this;
         }
      }

      public CompositePublicKey build() {
         return this.providers[0] == null && this.providers[1] == null
            ? new CompositePublicKey(this.algorithmIdentifier, this.keys, null)
            : new CompositePublicKey(this.algorithmIdentifier, this.keys, this.providers);
      }
   }
}
