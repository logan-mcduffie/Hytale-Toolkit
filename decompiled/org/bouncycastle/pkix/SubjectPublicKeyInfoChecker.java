package org.bouncycastle.pkix;

import java.io.IOException;
import java.math.BigInteger;
import java.security.AccessControlException;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.security.Security;
import java.util.HashMap;
import java.util.Map;
import java.util.WeakHashMap;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;
import org.bouncycastle.asn1.pkcs.RSAPublicKey;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.asn1.x509.X509ObjectIdentifiers;
import org.bouncycastle.asn1.x9.X962Parameters;
import org.bouncycastle.asn1.x9.X9FieldID;
import org.bouncycastle.asn1.x9.X9ObjectIdentifiers;
import org.bouncycastle.crypto.CryptoServicesRegistrar;
import org.bouncycastle.math.Primes;
import org.bouncycastle.util.BigIntegers;
import org.bouncycastle.util.Strings;

public class SubjectPublicKeyInfoChecker {
   private static final SubjectPublicKeyInfoChecker.Cache validatedQs = new SubjectPublicKeyInfoChecker.Cache();
   private static final SubjectPublicKeyInfoChecker.Cache validatedMods = new SubjectPublicKeyInfoChecker.Cache();
   private static final BigInteger SMALL_PRIMES_PRODUCT = new BigInteger(
      "8138e8a0fcf3a4e84a771d40fd305d7f4aa59306d7251de54d98af8fe95729a1f73d893fa424cd2edc8636a6c3285e022b0e3866a565ae8108eed8591cd4fe8d2ce86165a978d719ebf647f362d33fca29cd179fb42401cbaf3df0c614056f9c8f3cfd51e474afb6bc6974f78db8aba8e9e517fded658591ab7502bd41849462f",
      16
   );

   public static void checkInfo(SubjectPublicKeyInfo var0) {
      ASN1ObjectIdentifier var1 = var0.getAlgorithm().getAlgorithm();
      if (X9ObjectIdentifiers.id_ecPublicKey.equals(var1)) {
         X962Parameters var2 = X962Parameters.getInstance(var0.getAlgorithm().getParameters());
         if (var2.isImplicitlyCA() || var2.isNamedCurve()) {
            return;
         }

         ASN1Sequence var3 = ASN1Sequence.getInstance(var2.getParameters());
         X9FieldID var4 = X9FieldID.getInstance(var3.getObjectAt(1));
         if (var4.getIdentifier().equals(X9FieldID.prime_field)) {
            BigInteger var5 = ASN1Integer.getInstance(var4.getParameters()).getValue();
            if (validatedQs.contains(var5)) {
               return;
            }

            int var6 = SubjectPublicKeyInfoChecker.Properties.asInteger("org.bouncycastle.ec.fp_max_size", 1042);
            int var7 = SubjectPublicKeyInfoChecker.Properties.asInteger("org.bouncycastle.ec.fp_certainty", 100);
            int var8 = var5.bitLength();
            if (var6 < var8) {
               throw new IllegalArgumentException("Fp q value out of range");
            }

            if (Primes.hasAnySmallFactors(var5)
               || !Primes.isMRProbablePrime(var5, CryptoServicesRegistrar.getSecureRandom(), getNumberOfIterations(var8, var7))) {
               throw new IllegalArgumentException("Fp q value not prime");
            }

            validatedQs.add(var5);
         }
      } else if (PKCSObjectIdentifiers.rsaEncryption.equals(var1)
         || X509ObjectIdentifiers.id_ea_rsa.equals(var1)
         || PKCSObjectIdentifiers.id_RSAES_OAEP.equals(var1)
         || PKCSObjectIdentifiers.id_RSASSA_PSS.equals(var1)) {
         RSAPublicKey var10;
         try {
            var10 = RSAPublicKey.getInstance(var0.parsePublicKey());
         } catch (IOException var9) {
            throw new IllegalArgumentException("unable to parse RSA key");
         }

         if ((var10.getPublicExponent().intValue() & 1) == 0) {
            throw new IllegalArgumentException("RSA publicExponent is even");
         }

         if (!validatedMods.contains(var10.getModulus())) {
            validate(var10.getModulus());
            validatedMods.add(var10.getModulus());
         }
      }
   }

   private static boolean hasAnySmallFactors(BigInteger var0) {
      BigInteger var1 = var0;
      BigInteger var2 = SMALL_PRIMES_PRODUCT;
      if (var0.compareTo(SMALL_PRIMES_PRODUCT) < 0) {
         var1 = SMALL_PRIMES_PRODUCT;
         var2 = var0;
      }

      return !BigIntegers.modOddIsCoprimeVar(var1, var2);
   }

   private static void validate(BigInteger var0) {
      if ((var0.intValue() & 1) == 0) {
         throw new IllegalArgumentException("RSA modulus is even");
      } else if (!SubjectPublicKeyInfoChecker.Properties.isOverrideSet("org.bouncycastle.rsa.allow_unsafe_mod")) {
         int var1 = SubjectPublicKeyInfoChecker.Properties.asInteger("org.bouncycastle.rsa.max_size", 16384);
         if (var1 < var0.bitLength()) {
            throw new IllegalArgumentException("RSA modulus out of range");
         } else if (hasAnySmallFactors(var0)) {
            throw new IllegalArgumentException("RSA modulus has a small prime factor");
         } else {
            int var2 = var0.bitLength() / 2;
            int var3 = var2 >= 1536 ? 3 : (var2 >= 1024 ? 4 : (var2 >= 512 ? 7 : 50));
            Primes.MROutput var4 = Primes.enhancedMRProbablePrimeTest(var0, CryptoServicesRegistrar.getSecureRandom(), var3);
            if (!var4.isProvablyComposite()) {
               throw new IllegalArgumentException("RSA modulus is not composite");
            }
         }
      }
   }

   private static int getNumberOfIterations(int var0, int var1) {
      if (var0 >= 1536) {
         return var1 <= 100 ? 3 : (var1 <= 128 ? 4 : 4 + (var1 - 128 + 1) / 2);
      } else if (var0 >= 1024) {
         return var1 <= 100 ? 4 : (var1 <= 112 ? 5 : 5 + (var1 - 112 + 1) / 2);
      } else if (var0 >= 512) {
         return var1 <= 80 ? 5 : (var1 <= 100 ? 7 : 7 + (var1 - 100 + 1) / 2);
      } else {
         return var1 <= 80 ? 40 : 40 + (var1 - 80 + 1) / 2;
      }
   }

   public static boolean setThreadOverride(String var0, boolean var1) {
      return SubjectPublicKeyInfoChecker.Properties.setThreadOverride(var0, var1);
   }

   public static boolean removeThreadOverride(String var0) {
      return SubjectPublicKeyInfoChecker.Properties.removeThreadOverride(var0);
   }

   private static class Cache {
      private final Map<BigInteger, Boolean> values = new WeakHashMap<>();
      private final BigInteger[] preserve = new BigInteger[8];
      private int preserveCounter = 0;

      private Cache() {
      }

      public synchronized void add(BigInteger var1) {
         this.values.put(var1, Boolean.TRUE);
         this.preserve[this.preserveCounter] = var1;
         this.preserveCounter = (this.preserveCounter + 1) % this.preserve.length;
      }

      public synchronized boolean contains(BigInteger var1) {
         return this.values.containsKey(var1);
      }

      public synchronized int size() {
         return this.values.size();
      }

      public synchronized void clear() {
         this.values.clear();

         for (int var1 = 0; var1 != this.preserve.length; var1++) {
            this.preserve[var1] = null;
         }
      }
   }

   private static class Properties {
      private static final ThreadLocal threadProperties = new ThreadLocal();

      static boolean isOverrideSet(String var0) {
         try {
            return isSetTrue(getPropertyValue(var0));
         } catch (AccessControlException var2) {
            return false;
         }
      }

      static boolean setThreadOverride(String var0, boolean var1) {
         boolean var2 = isOverrideSet(var0);
         Object var3 = (Map)threadProperties.get();
         if (var3 == null) {
            var3 = new HashMap();
            threadProperties.set(var3);
         }

         var3.put(var0, var1 ? "true" : "false");
         return var2;
      }

      static boolean removeThreadOverride(String var0) {
         Map var1 = (Map)threadProperties.get();
         if (var1 != null) {
            String var2 = (String)var1.remove(var0);
            if (var2 != null) {
               if (var1.isEmpty()) {
                  threadProperties.remove();
               }

               return "true".equals(Strings.toLowerCase(var2));
            }
         }

         return false;
      }

      static int asInteger(String var0, int var1) {
         String var2 = getPropertyValue(var0);
         return var2 != null ? Integer.parseInt(var2) : var1;
      }

      static String getPropertyValue(final String var0) {
         String var1 = AccessController.doPrivileged(new PrivilegedAction() {
            @Override
            public Object run() {
               return Security.getProperty(var0);
            }
         });
         if (var1 != null) {
            return var1;
         } else {
            Map var2 = (Map)threadProperties.get();
            if (var2 != null) {
               String var3 = (String)var2.get(var0);
               if (var3 != null) {
                  return var3;
               }
            }

            return AccessController.doPrivileged(new PrivilegedAction() {
               @Override
               public Object run() {
                  return System.getProperty(var0);
               }
            });
         }
      }

      private static boolean isSetTrue(String var0) {
         return var0 != null && var0.length() == 4
            ? (var0.charAt(0) == 't' || var0.charAt(0) == 'T')
               && (var0.charAt(1) == 'r' || var0.charAt(1) == 'R')
               && (var0.charAt(2) == 'u' || var0.charAt(2) == 'U')
               && (var0.charAt(3) == 'e' || var0.charAt(3) == 'E')
            : false;
      }
   }
}
