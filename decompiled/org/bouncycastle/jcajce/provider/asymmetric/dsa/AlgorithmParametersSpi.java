package org.bouncycastle.jcajce.provider.asymmetric.dsa;

import java.io.IOException;
import java.security.spec.AlgorithmParameterSpec;
import java.security.spec.DSAParameterSpec;
import java.security.spec.InvalidParameterSpecException;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.x509.DSAParameter;

public class AlgorithmParametersSpi extends java.security.AlgorithmParametersSpi {
   DSAParameterSpec currentSpec;

   protected boolean isASN1FormatString(String var1) {
      return var1 == null || var1.equals("ASN.1");
   }

   @Override
   protected AlgorithmParameterSpec engineGetParameterSpec(Class var1) throws InvalidParameterSpecException {
      if (var1 == null) {
         throw new NullPointerException("argument to getParameterSpec must not be null");
      } else {
         return this.localEngineGetParameterSpec(var1);
      }
   }

   @Override
   protected byte[] engineGetEncoded() {
      DSAParameter var1 = new DSAParameter(this.currentSpec.getP(), this.currentSpec.getQ(), this.currentSpec.getG());

      try {
         return var1.getEncoded("DER");
      } catch (IOException var3) {
         throw new RuntimeException("Error encoding DSAParameters");
      }
   }

   @Override
   protected byte[] engineGetEncoded(String var1) {
      return this.isASN1FormatString(var1) ? this.engineGetEncoded() : null;
   }

   protected AlgorithmParameterSpec localEngineGetParameterSpec(Class var1) throws InvalidParameterSpecException {
      if (var1 != DSAParameterSpec.class && var1 != AlgorithmParameterSpec.class) {
         throw new InvalidParameterSpecException("unknown parameter spec passed to DSA parameters object.");
      } else {
         return this.currentSpec;
      }
   }

   @Override
   protected void engineInit(AlgorithmParameterSpec var1) throws InvalidParameterSpecException {
      if (!(var1 instanceof DSAParameterSpec)) {
         throw new InvalidParameterSpecException("DSAParameterSpec required to initialise a DSA algorithm parameters object");
      } else {
         this.currentSpec = (DSAParameterSpec)var1;
      }
   }

   @Override
   protected void engineInit(byte[] var1) throws IOException {
      try {
         DSAParameter var2 = DSAParameter.getInstance(ASN1Primitive.fromByteArray(var1));
         this.currentSpec = new DSAParameterSpec(var2.getP(), var2.getQ(), var2.getG());
      } catch (ClassCastException var3) {
         throw new IOException("Not a valid DSA Parameter encoding.");
      } catch (ArrayIndexOutOfBoundsException var4) {
         throw new IOException("Not a valid DSA Parameter encoding.");
      }
   }

   @Override
   protected void engineInit(byte[] var1, String var2) throws IOException {
      if (!this.isASN1FormatString(var2) && !var2.equalsIgnoreCase("X.509")) {
         throw new IOException("Unknown parameter format " + var2);
      } else {
         this.engineInit(var1);
      }
   }

   @Override
   protected String engineToString() {
      return "DSA Parameters";
   }
}
