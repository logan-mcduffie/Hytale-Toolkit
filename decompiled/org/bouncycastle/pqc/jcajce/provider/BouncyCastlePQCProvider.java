package org.bouncycastle.pqc.jcajce.provider;

import java.io.IOException;
import java.security.AccessController;
import java.security.PrivateKey;
import java.security.PrivilegedAction;
import java.security.Provider;
import java.security.PublicKey;
import java.util.HashMap;
import java.util.Map;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.jcajce.provider.config.ConfigurableProvider;
import org.bouncycastle.jcajce.provider.config.ProviderConfiguration;
import org.bouncycastle.jcajce.provider.util.AlgorithmProvider;
import org.bouncycastle.jcajce.provider.util.AsymmetricKeyInfoConverter;

public class BouncyCastlePQCProvider extends Provider implements ConfigurableProvider {
   private static String info = "BouncyCastle Post-Quantum Security Provider v1.83";
   public static String PROVIDER_NAME = "BCPQC";
   public static final ProviderConfiguration CONFIGURATION = null;
   private static final Map keyInfoConverters = new HashMap();
   private static final String ALGORITHM_PACKAGE = "org.bouncycastle.pqc.jcajce.provider.";
   private static final String[] ALGORITHMS = new String[]{
      "SPHINCS",
      "LMS",
      "NH",
      "XMSS",
      "SPHINCSPlus",
      "CMCE",
      "Frodo",
      "SABER",
      "Picnic",
      "NTRU",
      "Falcon",
      "Kyber",
      "Dilithium",
      "NTRUPrime",
      "BIKE",
      "HQC",
      "Rainbow",
      "Mayo",
      "Snova"
   };

   public BouncyCastlePQCProvider() {
      super(PROVIDER_NAME, 1.83, info);
      AccessController.doPrivileged(new PrivilegedAction() {
         @Override
         public Object run() {
            BouncyCastlePQCProvider.this.setup();
            return null;
         }
      });
   }

   private void setup() {
      this.loadAlgorithms("org.bouncycastle.pqc.jcajce.provider.", ALGORITHMS);
   }

   private void loadAlgorithms(String var1, String[] var2) {
      for (int var3 = 0; var3 != var2.length; var3++) {
         Class var4 = loadClass(BouncyCastlePQCProvider.class, var1 + var2[var3] + "$Mappings");
         if (var4 != null) {
            try {
               ((AlgorithmProvider)var4.newInstance()).configure(this);
            } catch (Exception var6) {
               throw new InternalError("cannot create instance of " + var1 + var2[var3] + "$Mappings : " + var6);
            }
         }
      }
   }

   @Override
   public void setParameter(String var1, Object var2) {
      synchronized (CONFIGURATION) {
         ;
      }
   }

   @Override
   public boolean hasAlgorithm(String var1, String var2) {
      return this.containsKey(var1 + "." + var2) || this.containsKey("Alg.Alias." + var1 + "." + var2);
   }

   @Override
   public void addAlgorithm(String var1, String var2) {
      if (this.containsKey(var1)) {
         throw new IllegalStateException("duplicate provider key (" + var1 + ") found");
      } else {
         this.put(var1, var2);
      }
   }

   @Override
   public void addAlgorithm(String var1, String var2, Map<String, String> var3) {
      this.addAlgorithm(var1, var2);
      this.addAttributes(var1, var3);
   }

   @Override
   public void addAlgorithm(String var1, ASN1ObjectIdentifier var2, String var3) {
      if (!this.containsKey(var1 + "." + var3)) {
         throw new IllegalStateException("primary key (" + var1 + "." + var3 + ") not found");
      } else {
         this.addAlgorithm(var1 + "." + var2, var3);
         this.addAlgorithm(var1 + ".OID." + var2, var3);
      }
   }

   @Override
   public void addAlgorithm(String var1, ASN1ObjectIdentifier var2, String var3, Map<String, String> var4) {
      this.addAlgorithm(var1, var2, var3);
      this.addAttributes(var1 + "." + var2, var4);
      this.addAttributes(var1 + ".OID." + var2, var4);
   }

   @Override
   public void addKeyInfoConverter(ASN1ObjectIdentifier var1, AsymmetricKeyInfoConverter var2) {
      synchronized (keyInfoConverters) {
         keyInfoConverters.put(var1, var2);
      }
   }

   @Override
   public AsymmetricKeyInfoConverter getKeyInfoConverter(ASN1ObjectIdentifier var1) {
      return (AsymmetricKeyInfoConverter)keyInfoConverters.get(var1);
   }

   @Override
   public void addAttributes(String var1, Map<String, String> var2) {
      for (String var4 : var2.keySet()) {
         String var5 = var1 + " " + var4;
         if (this.containsKey(var5)) {
            throw new IllegalStateException("duplicate provider attribute key (" + var5 + ") found");
         }

         this.put(var5, var2.get(var4));
      }
   }

   private static AsymmetricKeyInfoConverter getAsymmetricKeyInfoConverter(ASN1ObjectIdentifier var0) {
      synchronized (keyInfoConverters) {
         return (AsymmetricKeyInfoConverter)keyInfoConverters.get(var0);
      }
   }

   public static PublicKey getPublicKey(SubjectPublicKeyInfo var0) throws IOException {
      AsymmetricKeyInfoConverter var1 = getAsymmetricKeyInfoConverter(var0.getAlgorithm().getAlgorithm());
      return var1 == null ? null : var1.generatePublic(var0);
   }

   public static PrivateKey getPrivateKey(PrivateKeyInfo var0) throws IOException {
      AsymmetricKeyInfoConverter var1 = getAsymmetricKeyInfoConverter(var0.getPrivateKeyAlgorithm().getAlgorithm());
      return var1 == null ? null : var1.generatePrivate(var0);
   }

   static Class loadClass(Class var0, final String var1) {
      try {
         ClassLoader var2 = var0.getClassLoader();
         return var2 != null ? var2.loadClass(var1) : AccessController.doPrivileged(new PrivilegedAction() {
            @Override
            public Object run() {
               try {
                  return Class.forName(var1);
               } catch (Exception var2x) {
                  return null;
               }
            }
         });
      } catch (ClassNotFoundException var3) {
         return null;
      }
   }
}
