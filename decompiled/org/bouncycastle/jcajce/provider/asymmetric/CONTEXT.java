package org.bouncycastle.jcajce.provider.asymmetric;

import java.io.IOException;
import java.security.AlgorithmParametersSpi;
import java.security.spec.AlgorithmParameterSpec;
import java.security.spec.InvalidParameterSpecException;
import org.bouncycastle.jcajce.provider.config.ConfigurableProvider;
import org.bouncycastle.jcajce.provider.util.AsymmetricAlgorithmProvider;
import org.bouncycastle.jcajce.spec.ContextParameterSpec;

public class CONTEXT {
   private static final String PREFIX = "org.bouncycastle.jcajce.provider.asymmetric.CONTEXT$";

   public static class ContextAlgorithmParametersSpi extends AlgorithmParametersSpi {
      private ContextParameterSpec contextParameterSpec;

      protected boolean isASN1FormatString(String var1) {
         return var1 == null || var1.equals("ASN.1");
      }

      @Override
      protected AlgorithmParameterSpec engineGetParameterSpec(Class var1) throws InvalidParameterSpecException {
         if (var1 == null) {
            throw new NullPointerException("argument to getParameterSpec must not be null");
         } else if (var1 != ContextParameterSpec.class) {
            throw new IllegalArgumentException("argument to getParameterSpec must be ContextParameterSpec.class");
         } else {
            return this.contextParameterSpec;
         }
      }

      @Override
      protected void engineInit(AlgorithmParameterSpec var1) throws InvalidParameterSpecException {
         if (!(var1 instanceof ContextParameterSpec)) {
            throw new IllegalArgumentException("argument to engineInit must be a ContextParameterSpec");
         } else {
            this.contextParameterSpec = (ContextParameterSpec)var1;
         }
      }

      @Override
      protected void engineInit(byte[] var1) throws IOException {
         throw new IllegalStateException("not implemented");
      }

      @Override
      protected void engineInit(byte[] var1, String var2) throws IOException {
         throw new IllegalStateException("not implemented");
      }

      @Override
      protected byte[] engineGetEncoded() throws IOException {
         throw new IllegalStateException("not implemented");
      }

      @Override
      protected byte[] engineGetEncoded(String var1) throws IOException {
         throw new IllegalStateException("not implemented");
      }

      @Override
      protected String engineToString() {
         return "ContextParameterSpec";
      }
   }

   public static class Mappings extends AsymmetricAlgorithmProvider {
      @Override
      public void configure(ConfigurableProvider var1) {
         var1.addAlgorithm("AlgorithmParameters.CONTEXT", "org.bouncycastle.jcajce.provider.asymmetric.CONTEXT$ContextAlgorithmParametersSpi");
      }
   }
}
