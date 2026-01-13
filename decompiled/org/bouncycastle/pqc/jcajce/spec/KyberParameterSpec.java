package org.bouncycastle.pqc.jcajce.spec;

import java.security.spec.AlgorithmParameterSpec;
import java.util.HashMap;
import java.util.Map;
import org.bouncycastle.pqc.crypto.mlkem.MLKEMParameters;
import org.bouncycastle.util.Strings;

public class KyberParameterSpec implements AlgorithmParameterSpec {
   public static final KyberParameterSpec kyber512 = new KyberParameterSpec(MLKEMParameters.ml_kem_512);
   public static final KyberParameterSpec kyber768 = new KyberParameterSpec(MLKEMParameters.ml_kem_768);
   public static final KyberParameterSpec kyber1024 = new KyberParameterSpec(MLKEMParameters.ml_kem_1024);
   private static Map parameters = new HashMap();
   private final String name;

   private KyberParameterSpec(MLKEMParameters var1) {
      this.name = Strings.toUpperCase(var1.getName());
   }

   public String getName() {
      return this.name;
   }

   public static KyberParameterSpec fromName(String var0) {
      return (KyberParameterSpec)parameters.get(Strings.toLowerCase(var0));
   }

   static {
      parameters.put("kyber512", kyber512);
      parameters.put("kyber768", kyber768);
      parameters.put("kyber1024", kyber1024);
   }
}
