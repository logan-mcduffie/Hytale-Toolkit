package org.bouncycastle.jcajce.provider.util;

import java.util.Map;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.jcajce.provider.config.ConfigurableProvider;

public abstract class AsymmetricAlgorithmProvider extends AlgorithmProvider {
   protected void addSignatureAlgorithm(ConfigurableProvider var1, String var2, String var3, ASN1ObjectIdentifier var4) {
      var1.addAlgorithm("Signature." + var2, var3);
      if (var4 != null) {
         var1.addAlgorithm("Alg.Alias.Signature." + var4, var2);
         var1.addAlgorithm("Alg.Alias.Signature.OID." + var4, var2);
      }
   }

   protected void addSignatureAlias(ConfigurableProvider var1, String var2, ASN1ObjectIdentifier var3) {
      var1.addAlgorithm("Alg.Alias.Signature." + var3, var2);
      var1.addAlgorithm("Alg.Alias.Signature.OID." + var3, var2);
   }

   protected void addSignatureAlgorithm(ConfigurableProvider var1, String var2, String var3, String var4) {
      this.addSignatureAlgorithm(var1, var2, var3, var4, null);
   }

   protected void addSignatureAlgorithm(ConfigurableProvider var1, String var2, String var3, String var4, ASN1ObjectIdentifier var5) {
      String var6 = var2 + "WITH" + var3;
      String var7 = var2 + "with" + var3;
      String var8 = var2 + "With" + var3;
      String var9 = var2 + "/" + var3;
      var1.addAlgorithm("Signature." + var6, var4);
      var1.addAlgorithm("Alg.Alias.Signature." + var7, var6);
      var1.addAlgorithm("Alg.Alias.Signature." + var8, var6);
      var1.addAlgorithm("Alg.Alias.Signature." + var9, var6);
      if (var5 != null) {
         var1.addAlgorithm("Alg.Alias.Signature." + var5, var6);
         var1.addAlgorithm("Alg.Alias.Signature.OID." + var5, var6);
      }
   }

   protected void addSignatureAlgorithm(ConfigurableProvider var1, String var2, String var3, String var4, ASN1ObjectIdentifier var5, Map<String, String> var6) {
      String var7 = var2 + "WITH" + var3;
      String var8 = var2 + "with" + var3;
      String var9 = var2 + "With" + var3;
      String var10 = var2 + "/" + var3;
      var1.addAlgorithm("Signature." + var7, var4);
      var1.addAlgorithm("Alg.Alias.Signature." + var8, var7);
      var1.addAlgorithm("Alg.Alias.Signature." + var9, var7);
      var1.addAlgorithm("Alg.Alias.Signature." + var10, var7);
      if (var5 != null) {
         var1.addAlgorithm("Alg.Alias.Signature." + var5, var7);
         var1.addAlgorithm("Alg.Alias.Signature.OID." + var5, var7);
      }

      var1.addAttributes("Signature." + var7, var6);
   }

   protected void addKeyPairGeneratorAlgorithm(ConfigurableProvider var1, String var2, String var3, ASN1ObjectIdentifier var4) {
      var1.addAlgorithm("KeyPairGenerator." + var2, var3);
      if (var4 != null) {
         var1.addAlgorithm("Alg.Alias.KeyPairGenerator." + var4, var2);
         var1.addAlgorithm("Alg.Alias.KeyPairGenerator.OID." + var4, var2);
      }
   }

   protected void addKeyFactoryAlgorithm(ConfigurableProvider var1, String var2, String var3, ASN1ObjectIdentifier var4, AsymmetricKeyInfoConverter var5) {
      var1.addAlgorithm("KeyFactory." + var2, var3);
      if (var4 != null) {
         var1.addAlgorithm("Alg.Alias.KeyFactory." + var4, var2);
         var1.addAlgorithm("Alg.Alias.KeyFactory.OID." + var4, var2);
         var1.addKeyInfoConverter(var4, var5);
      }
   }

   protected void addKeyGeneratorAlgorithm(ConfigurableProvider var1, String var2, String var3, ASN1ObjectIdentifier var4) {
      var1.addAlgorithm("KeyGenerator." + var2, var3);
      if (var4 != null) {
         var1.addAlgorithm("Alg.Alias.KeyGenerator." + var4, var2);
         var1.addAlgorithm("Alg.Alias.KeyGenerator.OID." + var4, var2);
      }
   }

   protected void addCipherAlgorithm(ConfigurableProvider var1, String var2, String var3, ASN1ObjectIdentifier var4) {
      var1.addAlgorithm("Cipher." + var2, var3);
      if (var4 != null) {
         var1.addAlgorithm("Alg.Alias.Cipher." + var4, var2);
         var1.addAlgorithm("Alg.Alias.Cipher.OID." + var4, var2);
      }
   }

   protected void registerKeyFactoryOid(ConfigurableProvider var1, ASN1ObjectIdentifier var2, String var3, AsymmetricKeyInfoConverter var4) {
      var1.addAlgorithm("Alg.Alias.KeyFactory." + var2, var3);
      var1.addAlgorithm("Alg.Alias.KeyFactory.OID." + var2, var3);
      var1.addKeyInfoConverter(var2, var4);
   }

   protected void registerOid(ConfigurableProvider var1, ASN1ObjectIdentifier var2, String var3, AsymmetricKeyInfoConverter var4) {
      var1.addAlgorithm("Alg.Alias.KeyFactory." + var2, var3);
      var1.addAlgorithm("Alg.Alias.KeyPairGenerator." + var2, var3);
      var1.addKeyInfoConverter(var2, var4);
   }

   protected void registerOidAlgorithmParameters(ConfigurableProvider var1, ASN1ObjectIdentifier var2, String var3) {
      var1.addAlgorithm("Alg.Alias.AlgorithmParameters." + var2, var3);
   }

   protected void registerOidAlgorithmParameterGenerator(ConfigurableProvider var1, ASN1ObjectIdentifier var2, String var3) {
      var1.addAlgorithm("Alg.Alias.AlgorithmParameterGenerator." + var2, var3);
      var1.addAlgorithm("Alg.Alias.AlgorithmParameters." + var2, var3);
   }
}
