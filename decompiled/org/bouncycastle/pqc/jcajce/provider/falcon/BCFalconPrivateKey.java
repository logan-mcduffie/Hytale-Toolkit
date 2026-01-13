package org.bouncycastle.pqc.jcajce.provider.falcon;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import org.bouncycastle.asn1.ASN1Set;
import org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
import org.bouncycastle.pqc.crypto.falcon.FalconPrivateKeyParameters;
import org.bouncycastle.pqc.crypto.falcon.FalconPublicKeyParameters;
import org.bouncycastle.pqc.crypto.util.PrivateKeyFactory;
import org.bouncycastle.pqc.jcajce.interfaces.FalconPrivateKey;
import org.bouncycastle.pqc.jcajce.interfaces.FalconPublicKey;
import org.bouncycastle.pqc.jcajce.provider.util.KeyUtil;
import org.bouncycastle.pqc.jcajce.spec.FalconParameterSpec;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.util.Strings;

public class BCFalconPrivateKey implements FalconPrivateKey {
   private static final long serialVersionUID = 1L;
   private transient FalconPrivateKeyParameters params;
   private transient String algorithm;
   private transient byte[] encoding;
   private transient ASN1Set attributes;

   public BCFalconPrivateKey(FalconPrivateKeyParameters var1) {
      this.init(var1, null);
   }

   public BCFalconPrivateKey(PrivateKeyInfo var1) throws IOException {
      this.init(var1);
   }

   private void init(PrivateKeyInfo var1) throws IOException {
      this.init((FalconPrivateKeyParameters)PrivateKeyFactory.createKey(var1), var1.getAttributes());
   }

   private void init(FalconPrivateKeyParameters var1, ASN1Set var2) {
      this.attributes = var2;
      this.params = var1;
      this.algorithm = Strings.toUpperCase(var1.getParameters().getName());
   }

   @Override
   public boolean equals(Object var1) {
      if (var1 == this) {
         return true;
      } else if (var1 instanceof BCFalconPrivateKey) {
         BCFalconPrivateKey var2 = (BCFalconPrivateKey)var1;
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
         this.encoding = KeyUtil.getEncodedPrivateKeyInfo(this.params, this.attributes);
      }

      return Arrays.clone(this.encoding);
   }

   @Override
   public FalconParameterSpec getParameterSpec() {
      return FalconParameterSpec.fromName(this.params.getParameters().getName());
   }

   @Override
   public String getFormat() {
      return "PKCS#8";
   }

   @Override
   public FalconPublicKey getPublicKey() {
      return new BCFalconPublicKey(new FalconPublicKeyParameters(this.params.getParameters(), this.params.getPublicKey()));
   }

   FalconPrivateKeyParameters getKeyParams() {
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
