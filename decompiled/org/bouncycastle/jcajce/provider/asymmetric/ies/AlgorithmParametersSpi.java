package org.bouncycastle.jcajce.provider.asymmetric.ies;

import java.io.IOException;
import java.math.BigInteger;
import java.security.spec.AlgorithmParameterSpec;
import java.security.spec.InvalidParameterSpecException;
import java.util.Enumeration;
import org.bouncycastle.asn1.ASN1Boolean;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.DERTaggedObject;
import org.bouncycastle.jce.spec.IESParameterSpec;

public class AlgorithmParametersSpi extends java.security.AlgorithmParametersSpi {
   IESParameterSpec currentSpec;

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
      try {
         ASN1EncodableVector var1 = new ASN1EncodableVector();
         if (this.currentSpec.getDerivationV() != null) {
            var1.add(new DERTaggedObject(false, 0, new DEROctetString(this.currentSpec.getDerivationV())));
         }

         if (this.currentSpec.getEncodingV() != null) {
            var1.add(new DERTaggedObject(false, 1, new DEROctetString(this.currentSpec.getEncodingV())));
         }

         var1.add(new ASN1Integer(this.currentSpec.getMacKeySize()));
         byte[] var2 = this.currentSpec.getNonce();
         if (var2 != null) {
            ASN1EncodableVector var3 = new ASN1EncodableVector();
            var3.add(new ASN1Integer(this.currentSpec.getCipherKeySize()));
            var3.add(new DEROctetString(var2));
            var1.add(new DERSequence(var3));
         }

         var1.add(this.currentSpec.getPointCompression() ? ASN1Boolean.TRUE : ASN1Boolean.FALSE);
         return new DERSequence(var1).getEncoded("DER");
      } catch (IOException var4) {
         throw new RuntimeException("Error encoding IESParameters");
      }
   }

   @Override
   protected byte[] engineGetEncoded(String var1) {
      return !this.isASN1FormatString(var1) && !var1.equalsIgnoreCase("X.509") ? null : this.engineGetEncoded();
   }

   protected AlgorithmParameterSpec localEngineGetParameterSpec(Class var1) throws InvalidParameterSpecException {
      if (var1 != IESParameterSpec.class && var1 != AlgorithmParameterSpec.class) {
         throw new InvalidParameterSpecException("unknown parameter spec passed to ElGamal parameters object.");
      } else {
         return this.currentSpec;
      }
   }

   @Override
   protected void engineInit(AlgorithmParameterSpec var1) throws InvalidParameterSpecException {
      if (!(var1 instanceof IESParameterSpec)) {
         throw new InvalidParameterSpecException("IESParameterSpec required to initialise a IES algorithm parameters object");
      } else {
         this.currentSpec = (IESParameterSpec)var1;
      }
   }

   @Override
   protected void engineInit(byte[] var1) throws IOException {
      try {
         ASN1Sequence var2 = (ASN1Sequence)ASN1Primitive.fromByteArray(var1);
         if (var2.size() > 5) {
            throw new IOException("sequence too big");
         } else {
            byte[] var3 = null;
            byte[] var4 = null;
            BigInteger var5 = null;
            BigInteger var6 = null;
            byte[] var7 = null;
            boolean var8 = false;
            Enumeration var9 = var2.getObjects();

            while (var9.hasMoreElements()) {
               Object var10 = var9.nextElement();
               if (var10 instanceof ASN1TaggedObject) {
                  ASN1TaggedObject var11 = ASN1TaggedObject.getInstance(var10);
                  if (var11.getTagNo() == 0) {
                     var3 = ASN1OctetString.getInstance(var11, false).getOctets();
                  } else if (var11.getTagNo() == 1) {
                     var4 = ASN1OctetString.getInstance(var11, false).getOctets();
                  }
               } else if (var10 instanceof ASN1Integer) {
                  var5 = ASN1Integer.getInstance(var10).getValue();
               } else if (var10 instanceof ASN1Sequence) {
                  ASN1Sequence var14 = ASN1Sequence.getInstance(var10);
                  var6 = ASN1Integer.getInstance(var14.getObjectAt(0)).getValue();
                  var7 = ASN1OctetString.getInstance(var14.getObjectAt(1)).getOctets();
               } else if (var10 instanceof ASN1Boolean) {
                  var8 = ASN1Boolean.getInstance(var10).isTrue();
               }
            }

            if (var6 != null) {
               this.currentSpec = new IESParameterSpec(var3, var4, var5.intValue(), var6.intValue(), var7, var8);
            } else {
               this.currentSpec = new IESParameterSpec(var3, var4, var5.intValue(), -1, null, var8);
            }
         }
      } catch (ClassCastException var12) {
         throw new IOException("Not a valid IES Parameter encoding.");
      } catch (ArrayIndexOutOfBoundsException var13) {
         throw new IOException("Not a valid IES Parameter encoding.");
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
      return "IES Parameters";
   }
}
