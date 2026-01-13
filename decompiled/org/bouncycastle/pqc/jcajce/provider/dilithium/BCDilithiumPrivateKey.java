package org.bouncycastle.pqc.jcajce.provider.dilithium;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import org.bouncycastle.asn1.ASN1Set;
import org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
import org.bouncycastle.pqc.crypto.crystals.dilithium.DilithiumPrivateKeyParameters;
import org.bouncycastle.pqc.crypto.util.PrivateKeyFactory;
import org.bouncycastle.pqc.jcajce.interfaces.DilithiumPrivateKey;
import org.bouncycastle.pqc.jcajce.interfaces.DilithiumPublicKey;
import org.bouncycastle.pqc.jcajce.provider.util.KeyUtil;
import org.bouncycastle.pqc.jcajce.spec.DilithiumParameterSpec;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.util.Strings;

public class BCDilithiumPrivateKey implements DilithiumPrivateKey {
   private static final long serialVersionUID = 1L;
   private transient DilithiumPrivateKeyParameters params;
   private transient String algorithm;
   private transient byte[] encoding;
   private transient ASN1Set attributes;

   public BCDilithiumPrivateKey(DilithiumPrivateKeyParameters var1) {
      this.init(var1, null);
   }

   public BCDilithiumPrivateKey(PrivateKeyInfo var1) throws IOException {
      this.init(var1);
   }

   private void init(PrivateKeyInfo var1) throws IOException {
      this.init((DilithiumPrivateKeyParameters)PrivateKeyFactory.createKey(var1), var1.getAttributes());
   }

   private void init(DilithiumPrivateKeyParameters var1, ASN1Set var2) {
      this.attributes = var2;
      this.params = var1;
      this.algorithm = Strings.toUpperCase(var1.getParameters().getName());
   }

   @Override
   public boolean equals(Object var1) {
      if (var1 == this) {
         return true;
      } else if (var1 instanceof BCDilithiumPrivateKey) {
         BCDilithiumPrivateKey var2 = (BCDilithiumPrivateKey)var1;
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
   public DilithiumPublicKey getPublicKey() {
      return new BCDilithiumPublicKey(this.params.getPublicKeyParameters());
   }

   @Override
   public DilithiumParameterSpec getParameterSpec() {
      return DilithiumParameterSpec.fromName(this.params.getParameters().getName());
   }

   @Override
   public String getFormat() {
      return "PKCS#8";
   }

   DilithiumPrivateKeyParameters getKeyParams() {
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
