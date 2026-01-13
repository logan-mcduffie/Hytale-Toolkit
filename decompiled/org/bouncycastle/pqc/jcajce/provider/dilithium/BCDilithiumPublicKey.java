package org.bouncycastle.pqc.jcajce.provider.dilithium;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.pqc.crypto.crystals.dilithium.DilithiumPublicKeyParameters;
import org.bouncycastle.pqc.crypto.util.PublicKeyFactory;
import org.bouncycastle.pqc.jcajce.interfaces.DilithiumPublicKey;
import org.bouncycastle.pqc.jcajce.provider.util.KeyUtil;
import org.bouncycastle.pqc.jcajce.spec.DilithiumParameterSpec;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.util.Strings;

public class BCDilithiumPublicKey implements DilithiumPublicKey {
   private static final long serialVersionUID = 1L;
   private transient DilithiumPublicKeyParameters params;
   private transient String algorithm;
   private transient byte[] encoding;

   public BCDilithiumPublicKey(DilithiumPublicKeyParameters var1) {
      this.init(var1);
   }

   public BCDilithiumPublicKey(SubjectPublicKeyInfo var1) throws IOException {
      this.init(var1);
   }

   private void init(SubjectPublicKeyInfo var1) throws IOException {
      this.init((DilithiumPublicKeyParameters)PublicKeyFactory.createKey(var1));
   }

   private void init(DilithiumPublicKeyParameters var1) {
      this.params = var1;
      this.algorithm = Strings.toUpperCase(var1.getParameters().getName());
   }

   @Override
   public boolean equals(Object var1) {
      if (var1 == this) {
         return true;
      } else if (var1 instanceof BCDilithiumPublicKey) {
         BCDilithiumPublicKey var2 = (BCDilithiumPublicKey)var1;
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
   public DilithiumParameterSpec getParameterSpec() {
      return DilithiumParameterSpec.fromName(this.params.getParameters().getName());
   }

   DilithiumPublicKeyParameters getKeyParams() {
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
