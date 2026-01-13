package org.bouncycastle.jcajce.provider.asymmetric.dh;

import java.io.IOException;
import java.security.spec.AlgorithmParameterSpec;
import java.security.spec.InvalidParameterSpecException;
import javax.crypto.spec.DHParameterSpec;
import org.bouncycastle.asn1.pkcs.DHParameter;

public class AlgorithmParametersSpi extends java.security.AlgorithmParametersSpi {
   DHParameterSpec currentSpec;

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
      DHParameter var1 = new DHParameter(this.currentSpec.getP(), this.currentSpec.getG(), this.currentSpec.getL());

      try {
         return var1.getEncoded("DER");
      } catch (IOException var3) {
         throw new RuntimeException("Error encoding DHParameters");
      }
   }

   @Override
   protected byte[] engineGetEncoded(String var1) {
      return this.isASN1FormatString(var1) ? this.engineGetEncoded() : null;
   }

   protected AlgorithmParameterSpec localEngineGetParameterSpec(Class var1) throws InvalidParameterSpecException {
      if (var1 != DHParameterSpec.class && var1 != AlgorithmParameterSpec.class) {
         throw new InvalidParameterSpecException("unknown parameter spec passed to DH parameters object.");
      } else {
         return this.currentSpec;
      }
   }

   @Override
   protected void engineInit(AlgorithmParameterSpec var1) throws InvalidParameterSpecException {
      if (!(var1 instanceof DHParameterSpec)) {
         throw new InvalidParameterSpecException("DHParameterSpec required to initialise a Diffie-Hellman algorithm parameters object");
      } else {
         this.currentSpec = (DHParameterSpec)var1;
      }
   }

   @Override
   protected void engineInit(byte[] var1) throws IOException {
      try {
         DHParameter var2 = DHParameter.getInstance(var1);
         if (var2.getL() != null) {
            this.currentSpec = new DHParameterSpec(var2.getP(), var2.getG(), var2.getL().intValue());
         } else {
            this.currentSpec = new DHParameterSpec(var2.getP(), var2.getG());
         }
      } catch (ClassCastException var3) {
         throw new IOException("Not a valid DH Parameter encoding.");
      } catch (ArrayIndexOutOfBoundsException var4) {
         throw new IOException("Not a valid DH Parameter encoding.");
      }
   }

   @Override
   protected void engineInit(byte[] var1, String var2) throws IOException {
      if (this.isASN1FormatString(var2)) {
         this.engineInit(var1);
      } else {
         throw new IOException("Unknown parameter format " + var2);
      }
   }

   @Override
   protected String engineToString() {
      return "Diffie-Hellman Parameters";
   }
}
