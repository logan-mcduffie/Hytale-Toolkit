package org.bouncycastle.jcajce.provider.asymmetric.mlkem;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import org.bouncycastle.asn1.ASN1Set;
import org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
import org.bouncycastle.jcajce.interfaces.BCKey;
import org.bouncycastle.jcajce.interfaces.MLKEMPrivateKey;
import org.bouncycastle.jcajce.interfaces.MLKEMPublicKey;
import org.bouncycastle.jcajce.spec.MLKEMParameterSpec;
import org.bouncycastle.pqc.crypto.mlkem.MLKEMPrivateKeyParameters;
import org.bouncycastle.pqc.crypto.util.PrivateKeyFactory;
import org.bouncycastle.pqc.crypto.util.PrivateKeyInfoFactory;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.util.Fingerprint;
import org.bouncycastle.util.Strings;
import org.bouncycastle.util.encoders.Hex;

public class BCMLKEMPrivateKey implements MLKEMPrivateKey, BCKey {
   private static final long serialVersionUID = 1L;
   private transient MLKEMPrivateKeyParameters params;
   private transient String algorithm;
   private transient ASN1Set attributes;
   private transient byte[] priorEncoding;

   public BCMLKEMPrivateKey(MLKEMPrivateKeyParameters var1) {
      this.params = var1;
      this.algorithm = Strings.toUpperCase(var1.getParameters().getName());
   }

   public BCMLKEMPrivateKey(PrivateKeyInfo var1) throws IOException {
      this.init(var1);
   }

   private void init(PrivateKeyInfo var1) throws IOException {
      this.attributes = var1.getAttributes();
      this.priorEncoding = var1.getEncoded();
      this.params = (MLKEMPrivateKeyParameters)PrivateKeyFactory.createKey(var1);
      this.algorithm = Strings.toUpperCase(MLKEMParameterSpec.fromName(this.params.getParameters().getName()).getName());
   }

   @Override
   public boolean equals(Object var1) {
      if (var1 == this) {
         return true;
      } else if (var1 instanceof BCMLKEMPrivateKey) {
         BCMLKEMPrivateKey var2 = (BCMLKEMPrivateKey)var1;
         return Arrays.areEqual(this.params.getEncoded(), var2.params.getEncoded());
      } else {
         return false;
      }
   }

   @Override
   public int hashCode() {
      return Arrays.hashCode(this.params.getEncoded());
   }

   @Override
   public final String getAlgorithm() {
      return this.algorithm;
   }

   @Override
   public byte[] getEncoded() {
      try {
         if (this.priorEncoding != null) {
            return this.priorEncoding;
         } else {
            PrivateKeyInfo var1 = PrivateKeyInfoFactory.createPrivateKeyInfo(this.params, this.attributes);
            return var1.getEncoded();
         }
      } catch (IOException var2) {
         return null;
      }
   }

   @Override
   public MLKEMPublicKey getPublicKey() {
      return new BCMLKEMPublicKey(this.params.getPublicKeyParameters());
   }

   @Override
   public byte[] getPrivateData() {
      return this.params.getEncoded();
   }

   @Override
   public byte[] getSeed() {
      return this.params.getSeed();
   }

   @Override
   public MLKEMPrivateKey getPrivateKey(boolean var1) {
      if (var1) {
         byte[] var2 = this.params.getSeed();
         if (var2 != null) {
            return new BCMLKEMPrivateKey(this.params.getParametersWithFormat(1));
         }
      }

      return new BCMLKEMPrivateKey(this.params.getParametersWithFormat(2));
   }

   @Override
   public MLKEMParameterSpec getParameterSpec() {
      return MLKEMParameterSpec.fromName(this.params.getParameters().getName());
   }

   @Override
   public String getFormat() {
      return "PKCS#8";
   }

   @Override
   public String toString() {
      StringBuilder var1 = new StringBuilder();
      String var2 = Strings.lineSeparator();
      byte[] var3 = this.params.getPublicKey();
      var1.append(this.getAlgorithm())
         .append(" ")
         .append("Private Key")
         .append(" [")
         .append(new Fingerprint(var3).toString())
         .append("]")
         .append(var2)
         .append("    public data: ")
         .append(Hex.toHexString(var3))
         .append(var2);
      return var1.toString();
   }

   MLKEMPrivateKeyParameters getKeyParams() {
      return this.params;
   }

   private void readObject(ObjectInputStream var1) throws IOException, ClassNotFoundException {
      var1.defaultReadObject();
      byte[] var2 = (byte[])var1.readObject();
      this.init(PrivateKeyInfo.getInstance(var2));
   }

   private void writeObject(ObjectOutputStream var1) throws IOException {
      var1.defaultWriteObject();
      var1.writeObject(this.getEncoded());
   }
}
