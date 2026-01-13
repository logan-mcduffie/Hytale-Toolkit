package org.bouncycastle.pqc.jcajce.provider.xmss;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.security.PublicKey;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.pqc.crypto.util.PublicKeyFactory;
import org.bouncycastle.pqc.crypto.util.SubjectPublicKeyInfoFactory;
import org.bouncycastle.pqc.crypto.xmss.XMSSPublicKeyParameters;
import org.bouncycastle.pqc.jcajce.interfaces.XMSSKey;
import org.bouncycastle.util.Arrays;

public class BCXMSSPublicKey implements PublicKey, XMSSKey {
   private static final long serialVersionUID = -5617456225328969766L;
   private transient XMSSPublicKeyParameters keyParams;
   private transient ASN1ObjectIdentifier treeDigest;

   public BCXMSSPublicKey(ASN1ObjectIdentifier var1, XMSSPublicKeyParameters var2) {
      this.treeDigest = var1;
      this.keyParams = var2;
   }

   public BCXMSSPublicKey(SubjectPublicKeyInfo var1) throws IOException {
      this.init(var1);
   }

   private void init(SubjectPublicKeyInfo var1) throws IOException {
      this.keyParams = (XMSSPublicKeyParameters)PublicKeyFactory.createKey(var1);
      this.treeDigest = DigestUtil.getDigestOID(this.keyParams.getTreeDigest());
   }

   @Override
   public final String getAlgorithm() {
      return "XMSS";
   }

   @Override
   public byte[] getEncoded() {
      try {
         SubjectPublicKeyInfo var1 = SubjectPublicKeyInfoFactory.createSubjectPublicKeyInfo(this.keyParams);
         return var1.getEncoded();
      } catch (IOException var2) {
         return null;
      }
   }

   @Override
   public String getFormat() {
      return "X.509";
   }

   CipherParameters getKeyParams() {
      return this.keyParams;
   }

   @Override
   public boolean equals(Object var1) {
      if (var1 == this) {
         return true;
      } else if (var1 instanceof BCXMSSPublicKey) {
         BCXMSSPublicKey var2 = (BCXMSSPublicKey)var1;

         try {
            return this.treeDigest.equals(var2.treeDigest) && Arrays.areEqual(this.keyParams.getEncoded(), var2.keyParams.getEncoded());
         } catch (IOException var4) {
            return false;
         }
      } else {
         return false;
      }
   }

   @Override
   public int hashCode() {
      try {
         return this.treeDigest.hashCode() + 37 * Arrays.hashCode(this.keyParams.getEncoded());
      } catch (IOException var2) {
         return this.treeDigest.hashCode();
      }
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
      this.init(SubjectPublicKeyInfo.getInstance(var2));
   }

   private void writeObject(ObjectOutputStream var1) throws IOException {
      var1.defaultWriteObject();
      var1.writeObject(this.getEncoded());
   }
}
