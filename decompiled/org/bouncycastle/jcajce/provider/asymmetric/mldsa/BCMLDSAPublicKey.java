package org.bouncycastle.jcajce.provider.asymmetric.mldsa;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.jcajce.interfaces.BCKey;
import org.bouncycastle.jcajce.interfaces.MLDSAPublicKey;
import org.bouncycastle.jcajce.spec.MLDSAParameterSpec;
import org.bouncycastle.pqc.crypto.mldsa.MLDSAPublicKeyParameters;
import org.bouncycastle.pqc.crypto.util.PublicKeyFactory;
import org.bouncycastle.pqc.crypto.util.SubjectPublicKeyInfoFactory;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.util.Fingerprint;
import org.bouncycastle.util.Strings;
import org.bouncycastle.util.encoders.Hex;

public class BCMLDSAPublicKey implements MLDSAPublicKey, BCKey {
   private static final long serialVersionUID = 1L;
   private transient MLDSAPublicKeyParameters params;
   private transient String algorithm;

   public BCMLDSAPublicKey(MLDSAPublicKeyParameters var1) {
      this.params = var1;
      this.algorithm = Strings.toUpperCase(MLDSAParameterSpec.fromName(var1.getParameters().getName()).getName());
   }

   public BCMLDSAPublicKey(SubjectPublicKeyInfo var1) throws IOException {
      this.init(var1);
   }

   private void init(SubjectPublicKeyInfo var1) throws IOException {
      this.params = (MLDSAPublicKeyParameters)PublicKeyFactory.createKey(var1);
      this.algorithm = Strings.toUpperCase(MLDSAParameterSpec.fromName(this.params.getParameters().getName()).getName());
   }

   @Override
   public boolean equals(Object var1) {
      if (var1 == this) {
         return true;
      } else if (var1 instanceof BCMLDSAPublicKey) {
         BCMLDSAPublicKey var2 = (BCMLDSAPublicKey)var1;
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
   public byte[] getPublicData() {
      return this.params.getEncoded();
   }

   @Override
   public byte[] getEncoded() {
      try {
         SubjectPublicKeyInfo var1 = SubjectPublicKeyInfoFactory.createSubjectPublicKeyInfo(this.params);
         return var1.getEncoded();
      } catch (IOException var2) {
         return null;
      }
   }

   @Override
   public String getFormat() {
      return "X.509";
   }

   @Override
   public MLDSAParameterSpec getParameterSpec() {
      return MLDSAParameterSpec.fromName(this.params.getParameters().getName());
   }

   @Override
   public String toString() {
      StringBuilder var1 = new StringBuilder();
      String var2 = Strings.lineSeparator();
      byte[] var3 = this.params.getEncoded();
      var1.append(this.getAlgorithm())
         .append(" ")
         .append("Public Key")
         .append(" [")
         .append(new Fingerprint(var3).toString())
         .append("]")
         .append(var2)
         .append("    public data: ")
         .append(Hex.toHexString(var3))
         .append(var2);
      return var1.toString();
   }

   MLDSAPublicKeyParameters getKeyParams() {
      return this.params;
   }

   private void readObject(ObjectInputStream var1) throws IOException, ClassNotFoundException {
      var1.defaultReadObject();
      byte[] var2 = (byte[])var1.readObject();
      this.init(SubjectPublicKeyInfo.getInstance(var2));
   }

   private void writeObject(ObjectOutputStream var1) throws IOException {
      var1.defaultWriteObject();
      var1.writeObject(this.getEncoded());
   }
}
