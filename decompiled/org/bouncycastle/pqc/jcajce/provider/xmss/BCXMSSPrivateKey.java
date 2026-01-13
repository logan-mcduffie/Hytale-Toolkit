package org.bouncycastle.pqc.jcajce.provider.xmss;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.security.PrivateKey;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1Set;
import org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.pqc.asn1.XMSSKeyParams;
import org.bouncycastle.pqc.crypto.util.PrivateKeyFactory;
import org.bouncycastle.pqc.crypto.util.PrivateKeyInfoFactory;
import org.bouncycastle.pqc.crypto.xmss.XMSSPrivateKeyParameters;
import org.bouncycastle.pqc.jcajce.interfaces.XMSSPrivateKey;
import org.bouncycastle.util.Arrays;

public class BCXMSSPrivateKey implements PrivateKey, XMSSPrivateKey {
   private static final long serialVersionUID = 8568701712864512338L;
   private transient XMSSPrivateKeyParameters keyParams;
   private transient ASN1ObjectIdentifier treeDigest;
   private transient ASN1Set attributes;

   public BCXMSSPrivateKey(ASN1ObjectIdentifier var1, XMSSPrivateKeyParameters var2) {
      this.treeDigest = var1;
      this.keyParams = var2;
   }

   public BCXMSSPrivateKey(PrivateKeyInfo var1) throws IOException {
      this.init(var1);
   }

   private void init(PrivateKeyInfo var1) throws IOException {
      this.attributes = var1.getAttributes();
      XMSSKeyParams var2 = XMSSKeyParams.getInstance(var1.getPrivateKeyAlgorithm().getParameters());
      this.treeDigest = var2.getTreeDigest().getAlgorithm();
      this.keyParams = (XMSSPrivateKeyParameters)PrivateKeyFactory.createKey(var1);
   }

   @Override
   public long getIndex() {
      if (this.getUsagesRemaining() == 0L) {
         throw new IllegalStateException("key exhausted");
      } else {
         return this.keyParams.getIndex();
      }
   }

   @Override
   public long getUsagesRemaining() {
      return this.keyParams.getUsagesRemaining();
   }

   @Override
   public XMSSPrivateKey extractKeyShard(int var1) {
      return new BCXMSSPrivateKey(this.treeDigest, this.keyParams.extractKeyShard(var1));
   }

   @Override
   public String getAlgorithm() {
      return "XMSS";
   }

   @Override
   public String getFormat() {
      return "PKCS#8";
   }

   @Override
   public byte[] getEncoded() {
      try {
         PrivateKeyInfo var1 = PrivateKeyInfoFactory.createPrivateKeyInfo(this.keyParams, this.attributes);
         return var1.getEncoded();
      } catch (IOException var2) {
         return null;
      }
   }

   @Override
   public boolean equals(Object var1) {
      if (var1 == this) {
         return true;
      } else if (!(var1 instanceof BCXMSSPrivateKey)) {
         return false;
      } else {
         BCXMSSPrivateKey var2 = (BCXMSSPrivateKey)var1;
         return this.treeDigest.equals(var2.treeDigest) && Arrays.areEqual(this.keyParams.toByteArray(), var2.keyParams.toByteArray());
      }
   }

   @Override
   public int hashCode() {
      return this.treeDigest.hashCode() + 37 * Arrays.hashCode(this.keyParams.toByteArray());
   }

   CipherParameters getKeyParams() {
      return this.keyParams;
   }

   ASN1ObjectIdentifier getTreeDigestOID() {
      return this.treeDigest;
   }

   @Override
   public int getHeight() {
      return this.keyParams.getParameters().getHeight();
   }

   @Override
   public String getTreeDigest() {
      return DigestUtil.getXMSSDigestName(this.treeDigest);
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
