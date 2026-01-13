package org.bouncycastle.jcajce.provider.asymmetric.gost;

import java.io.IOException;
import java.security.spec.AlgorithmParameterSpec;
import java.security.spec.InvalidParameterSpecException;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.cryptopro.GOST3410PublicKeyAlgParameters;
import org.bouncycastle.jce.spec.GOST3410ParameterSpec;
import org.bouncycastle.jce.spec.GOST3410PublicKeyParameterSetSpec;

public class AlgorithmParametersSpi extends java.security.AlgorithmParametersSpi {
   GOST3410ParameterSpec currentSpec;

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
      GOST3410PublicKeyAlgParameters var1 = new GOST3410PublicKeyAlgParameters(
         new ASN1ObjectIdentifier(this.currentSpec.getPublicKeyParamSetOID()),
         new ASN1ObjectIdentifier(this.currentSpec.getDigestParamSetOID()),
         new ASN1ObjectIdentifier(this.currentSpec.getEncryptionParamSetOID())
      );

      try {
         return var1.getEncoded("DER");
      } catch (IOException var3) {
         throw new RuntimeException("Error encoding GOST3410Parameters");
      }
   }

   @Override
   protected byte[] engineGetEncoded(String var1) {
      return !this.isASN1FormatString(var1) && !var1.equalsIgnoreCase("X.509") ? null : this.engineGetEncoded();
   }

   protected AlgorithmParameterSpec localEngineGetParameterSpec(Class var1) throws InvalidParameterSpecException {
      if (var1 != GOST3410PublicKeyParameterSetSpec.class && var1 != AlgorithmParameterSpec.class) {
         throw new InvalidParameterSpecException("unknown parameter spec passed to GOST3410 parameters object.");
      } else {
         return this.currentSpec;
      }
   }

   @Override
   protected void engineInit(AlgorithmParameterSpec var1) throws InvalidParameterSpecException {
      if (!(var1 instanceof GOST3410ParameterSpec)) {
         throw new InvalidParameterSpecException("GOST3410ParameterSpec required to initialise a GOST3410 algorithm parameters object");
      } else {
         this.currentSpec = (GOST3410ParameterSpec)var1;
      }
   }

   @Override
   protected void engineInit(byte[] var1) throws IOException {
      try {
         ASN1Sequence var2 = (ASN1Sequence)ASN1Primitive.fromByteArray(var1);
         this.currentSpec = GOST3410ParameterSpec.fromPublicKeyAlg(GOST3410PublicKeyAlgParameters.getInstance(var2));
      } catch (ClassCastException var3) {
         throw new IOException("Not a valid GOST3410 Parameter encoding.");
      } catch (ArrayIndexOutOfBoundsException var4) {
         throw new IOException("Not a valid GOST3410 Parameter encoding.");
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
      return "GOST3410 Parameters";
   }
}
