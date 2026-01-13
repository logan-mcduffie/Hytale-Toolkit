package org.bouncycastle.jcajce;

import java.io.IOException;
import java.security.PrivateKey;
import java.security.Provider;
import java.security.Security;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
import org.bouncycastle.asn1.sec.ECPrivateKey;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.asn1.x9.ECNamedCurveTable;
import org.bouncycastle.crypto.util.PrivateKeyFactory;
import org.bouncycastle.crypto.util.PrivateKeyInfoFactory;
import org.bouncycastle.internal.asn1.iana.IANAObjectIdentifiers;
import org.bouncycastle.internal.asn1.misc.MiscObjectIdentifiers;
import org.bouncycastle.jcajce.interfaces.MLDSAPrivateKey;
import org.bouncycastle.jcajce.provider.asymmetric.compositesignatures.CompositeIndex;
import org.bouncycastle.jcajce.provider.asymmetric.compositesignatures.KeyFactorySpi;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.util.Exceptions;

public class CompositePrivateKey implements PrivateKey {
   private final List<PrivateKey> keys;
   private final List<Provider> providers;
   private AlgorithmIdentifier algorithmIdentifier;

   public static CompositePrivateKey.Builder builder(ASN1ObjectIdentifier var0) {
      return new CompositePrivateKey.Builder(new AlgorithmIdentifier(var0));
   }

   public static CompositePrivateKey.Builder builder(String var0) {
      return builder(CompositeUtil.getOid(var0));
   }

   public CompositePrivateKey(PrivateKey... var1) {
      this(MiscObjectIdentifiers.id_composite_key, var1);
   }

   public CompositePrivateKey(ASN1ObjectIdentifier var1, PrivateKey... var2) {
      this(new AlgorithmIdentifier(var1), var2);
   }

   public CompositePrivateKey(AlgorithmIdentifier var1, PrivateKey... var2) {
      this.algorithmIdentifier = var1;
      if (var2 != null && var2.length != 0) {
         ArrayList var3 = new ArrayList(var2.length);

         for (int var4 = 0; var4 < var2.length; var4++) {
            var3.add(this.processKey(var2[var4]));
         }

         this.keys = Collections.unmodifiableList(var3);
         this.providers = null;
      } else {
         throw new IllegalArgumentException("at least one private key must be provided for the composite private key");
      }
   }

   private PrivateKey processKey(PrivateKey var1) {
      if (var1 instanceof MLDSAPrivateKey) {
         try {
            return ((MLDSAPrivateKey)var1).getPrivateKey(true);
         } catch (Exception var3) {
            return var1;
         }
      } else {
         return var1;
      }
   }

   private CompositePrivateKey(AlgorithmIdentifier var1, PrivateKey[] var2, Provider[] var3) {
      this.algorithmIdentifier = var1;
      if (var2.length != 2) {
         throw new IllegalArgumentException("two keys required for composite private key");
      } else {
         ArrayList var4 = new ArrayList(var2.length);
         if (var3 == null) {
            for (int var5 = 0; var5 < var2.length; var5++) {
               var4.add(this.processKey(var2[var5]));
            }

            this.providers = null;
         } else {
            ArrayList var7 = new ArrayList(var3.length);

            for (int var6 = 0; var6 < var2.length; var6++) {
               var7.add(var3[var6]);
               var4.add(this.processKey(var2[var6]));
            }

            this.providers = Collections.unmodifiableList(var7);
         }

         this.keys = Collections.unmodifiableList(var4);
      }
   }

   public CompositePrivateKey(PrivateKeyInfo var1) {
      CompositePrivateKey var2 = null;
      ASN1ObjectIdentifier var3 = var1.getPrivateKeyAlgorithm().getAlgorithm();

      try {
         if (!CompositeIndex.isAlgorithmSupported(var3)) {
            throw new IllegalStateException("Unable to create CompositePrivateKey from PrivateKeyInfo");
         }

         KeyFactorySpi var4 = new KeyFactorySpi();
         var2 = (CompositePrivateKey)var4.generatePrivate(var1);
         if (var2 == null) {
            throw new IllegalStateException("Unable to create CompositePrivateKey from PrivateKeyInfo");
         }
      } catch (IOException var5) {
         throw Exceptions.illegalStateException(var5.getMessage(), var5);
      }

      this.keys = var2.getPrivateKeys();
      this.providers = null;
      this.algorithmIdentifier = var2.getAlgorithmIdentifier();
   }

   public List<PrivateKey> getPrivateKeys() {
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
      return "PKCS#8";
   }

   @Override
   public byte[] getEncoded() {
      if (this.algorithmIdentifier.getAlgorithm().on(IANAObjectIdentifiers.id_alg)) {
         try {
            byte[] var8 = ((MLDSAPrivateKey)this.keys.get(0)).getSeed();
            PrivateKeyInfo var10 = PrivateKeyInfoFactory.createPrivateKeyInfo(PrivateKeyFactory.createKey(this.keys.get(1).getEncoded()));
            byte[] var12 = var10.getPrivateKey().getOctets();
            if (this.keys.get(1).getAlgorithm().contains("Ed")) {
               var12 = ASN1OctetString.getInstance(var12).getOctets();
            } else if (this.keys.get(1).getAlgorithm().contains("EC")) {
               ECPrivateKey var13 = ECPrivateKey.getInstance(var12);
               var12 = new ECPrivateKey(
                     ECNamedCurveTable.getByOID(ASN1ObjectIdentifier.getInstance(var13.getParametersObject())).getCurve().getFieldSize(),
                     var13.getKey(),
                     var13.getParametersObject()
                  )
                  .getEncoded();
            }

            return new PrivateKeyInfo(this.algorithmIdentifier, Arrays.concatenate(var8, var12)).getEncoded();
         } catch (IOException var5) {
            throw new IllegalStateException("unable to encode composite public key: " + var5.getMessage());
         }
      } else {
         ASN1EncodableVector var1 = new ASN1EncodableVector();
         if (this.algorithmIdentifier.getAlgorithm().equals(MiscObjectIdentifiers.id_composite_key)) {
            for (int var9 = 0; var9 < this.keys.size(); var9++) {
               PrivateKeyInfo var11 = PrivateKeyInfo.getInstance(this.keys.get(var9).getEncoded());
               var1.add(var11);
            }

            try {
               return new PrivateKeyInfo(this.algorithmIdentifier, new DERSequence(var1)).getEncoded("DER");
            } catch (IOException var6) {
               throw new IllegalStateException("unable to encode composite private key: " + var6.getMessage());
            }
         } else {
            byte[] var2 = null;

            for (int var3 = 0; var3 < this.keys.size(); var3++) {
               PrivateKeyInfo var4 = PrivateKeyInfo.getInstance(this.keys.get(var3).getEncoded());
               var2 = Arrays.concatenate(var2, var4.getPrivateKey().getOctets());
            }

            try {
               return new PrivateKeyInfo(this.algorithmIdentifier, var2).getEncoded("DER");
            } catch (IOException var7) {
               throw new IllegalStateException("unable to encode composite private key: " + var7.getMessage());
            }
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
      } else if (!(var1 instanceof CompositePrivateKey)) {
         return false;
      } else {
         boolean var2 = true;
         CompositePrivateKey var3 = (CompositePrivateKey)var1;
         if (!var3.getAlgorithmIdentifier().equals(this.algorithmIdentifier) || !this.keys.equals(var3.keys)) {
            var2 = false;
         }

         return var2;
      }
   }

   public static class Builder {
      private final AlgorithmIdentifier algorithmIdentifier;
      private final PrivateKey[] keys = new PrivateKey[2];
      private final Provider[] providers = new Provider[2];
      private int count = 0;

      private Builder(AlgorithmIdentifier var1) {
         this.algorithmIdentifier = var1;
      }

      public CompositePrivateKey.Builder addPrivateKey(PrivateKey var1) {
         return this.addPrivateKey(var1, (Provider)null);
      }

      public CompositePrivateKey.Builder addPrivateKey(PrivateKey var1, String var2) {
         return this.addPrivateKey(var1, Security.getProvider(var2));
      }

      public CompositePrivateKey.Builder addPrivateKey(PrivateKey var1, Provider var2) {
         if (this.count == this.keys.length) {
            throw new IllegalStateException("only " + this.keys.length + " allowed in composite");
         } else {
            this.keys[this.count] = var1;
            this.providers[this.count++] = var2;
            return this;
         }
      }

      public CompositePrivateKey build() {
         return this.providers[0] == null && this.providers[1] == null
            ? new CompositePrivateKey(this.algorithmIdentifier, this.keys, null)
            : new CompositePrivateKey(this.algorithmIdentifier, this.keys, this.providers);
      }
   }
}
