package org.bouncycastle.pqc.jcajce.provider.newhope;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import org.bouncycastle.asn1.ASN1Set;
import org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.pqc.crypto.newhope.NHPrivateKeyParameters;
import org.bouncycastle.pqc.crypto.util.PrivateKeyFactory;
import org.bouncycastle.pqc.crypto.util.PrivateKeyInfoFactory;
import org.bouncycastle.pqc.jcajce.interfaces.NHPrivateKey;
import org.bouncycastle.util.Arrays;

public class BCNHPrivateKey implements NHPrivateKey {
   private static final long serialVersionUID = 1L;
   private transient NHPrivateKeyParameters params;
   private transient ASN1Set attributes;

   public BCNHPrivateKey(NHPrivateKeyParameters var1) {
      this.params = var1;
   }

   public BCNHPrivateKey(PrivateKeyInfo var1) throws IOException {
      this.init(var1);
   }

   private void init(PrivateKeyInfo var1) throws IOException {
      this.attributes = var1.getAttributes();
      this.params = (NHPrivateKeyParameters)PrivateKeyFactory.createKey(var1);
   }

   @Override
   public boolean equals(Object var1) {
      if (!(var1 instanceof BCNHPrivateKey)) {
         return false;
      } else {
         BCNHPrivateKey var2 = (BCNHPrivateKey)var1;
         return Arrays.areEqual(this.params.getSecData(), var2.params.getSecData());
      }
   }

   @Override
   public int hashCode() {
      return Arrays.hashCode(this.params.getSecData());
   }

   @Override
   public final String getAlgorithm() {
      return "NH";
   }

   @Override
   public byte[] getEncoded() {
      try {
         PrivateKeyInfo var1 = PrivateKeyInfoFactory.createPrivateKeyInfo(this.params, this.attributes);
         return var1.getEncoded();
      } catch (IOException var2) {
         return null;
      }
   }

   @Override
   public String getFormat() {
      return "PKCS#8";
   }

   @Override
   public short[] getSecretData() {
      return this.params.getSecData();
   }

   CipherParameters getKeyParams() {
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
