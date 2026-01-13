package org.bouncycastle.pqc.jcajce.provider.kyber;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.pqc.crypto.mlkem.MLKEMPublicKeyParameters;
import org.bouncycastle.pqc.crypto.util.PublicKeyFactory;
import org.bouncycastle.pqc.jcajce.interfaces.KyberPublicKey;
import org.bouncycastle.pqc.jcajce.provider.util.KeyUtil;
import org.bouncycastle.pqc.jcajce.spec.KyberParameterSpec;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.util.Strings;

public class BCKyberPublicKey implements KyberPublicKey {
   private static final long serialVersionUID = 1L;
   private transient MLKEMPublicKeyParameters params;
   private transient String algorithm;
   private transient byte[] encoding;

   public BCKyberPublicKey(MLKEMPublicKeyParameters var1) {
      this.init(var1);
   }

   public BCKyberPublicKey(SubjectPublicKeyInfo var1) throws IOException {
      this.init(var1);
   }

   private void init(SubjectPublicKeyInfo var1) throws IOException {
      this.init((MLKEMPublicKeyParameters)PublicKeyFactory.createKey(var1));
   }

   private void init(MLKEMPublicKeyParameters var1) {
      this.params = var1;
      this.algorithm = Strings.toUpperCase(var1.getParameters().getName());
   }

   @Override
   public boolean equals(Object var1) {
      if (var1 == this) {
         return true;
      } else if (var1 instanceof BCKyberPublicKey) {
         BCKyberPublicKey var2 = (BCKyberPublicKey)var1;
         return Arrays.areEqual(this.getEncoded(), var2.getEncoded());
      } else {
         return false;
      }
   }

   @Override
   public int hashCode() {
      return Arrays.hashCode(this.getEncoded());
   }

   @Override
   public final String getAlgorithm() {
      return this.algorithm;
   }

   @Override
   public byte[] getEncoded() {
      if (this.encoding == null) {
         this.encoding = KeyUtil.getEncodedSubjectPublicKeyInfo(this.params);
      }

      return Arrays.clone(this.encoding);
   }

   @Override
   public String getFormat() {
      return "X.509";
   }

   @Override
   public KyberParameterSpec getParameterSpec() {
      return KyberParameterSpec.fromName(this.params.getParameters().getName());
   }

   MLKEMPublicKeyParameters getKeyParams() {
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
