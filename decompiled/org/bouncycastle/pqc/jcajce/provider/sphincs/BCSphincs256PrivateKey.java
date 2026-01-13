package org.bouncycastle.pqc.jcajce.provider.sphincs;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.security.PrivateKey;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1Set;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.pqc.asn1.PQCObjectIdentifiers;
import org.bouncycastle.pqc.asn1.SPHINCS256KeyParams;
import org.bouncycastle.pqc.crypto.sphincs.SPHINCSPrivateKeyParameters;
import org.bouncycastle.pqc.crypto.util.PrivateKeyFactory;
import org.bouncycastle.pqc.crypto.util.PrivateKeyInfoFactory;
import org.bouncycastle.pqc.jcajce.interfaces.SPHINCSKey;
import org.bouncycastle.util.Arrays;

public class BCSphincs256PrivateKey implements PrivateKey, SPHINCSKey {
   private static final long serialVersionUID = 1L;
   private transient ASN1ObjectIdentifier treeDigest;
   private transient SPHINCSPrivateKeyParameters params;
   private transient ASN1Set attributes;

   public BCSphincs256PrivateKey(ASN1ObjectIdentifier var1, SPHINCSPrivateKeyParameters var2) {
      this.treeDigest = var1;
      this.params = var2;
   }

   public BCSphincs256PrivateKey(PrivateKeyInfo var1) throws IOException {
      this.init(var1);
   }

   private void init(PrivateKeyInfo var1) throws IOException {
      this.attributes = var1.getAttributes();
      this.treeDigest = SPHINCS256KeyParams.getInstance(var1.getPrivateKeyAlgorithm().getParameters()).getTreeDigest().getAlgorithm();
      this.params = (SPHINCSPrivateKeyParameters)PrivateKeyFactory.createKey(var1);
   }

   @Override
   public boolean equals(Object var1) {
      if (var1 == this) {
         return true;
      } else if (!(var1 instanceof BCSphincs256PrivateKey)) {
         return false;
      } else {
         BCSphincs256PrivateKey var2 = (BCSphincs256PrivateKey)var1;
         return this.treeDigest.equals(var2.treeDigest) && Arrays.areEqual(this.params.getKeyData(), var2.params.getKeyData());
      }
   }

   @Override
   public int hashCode() {
      return this.treeDigest.hashCode() + 37 * Arrays.hashCode(this.params.getKeyData());
   }

   @Override
   public final String getAlgorithm() {
      return "SPHINCS-256";
   }

   @Override
   public byte[] getEncoded() {
      try {
         PrivateKeyInfo var1;
         if (this.params.getTreeDigest() != null) {
            var1 = PrivateKeyInfoFactory.createPrivateKeyInfo(this.params, this.attributes);
         } else {
            AlgorithmIdentifier var2 = new AlgorithmIdentifier(
               PQCObjectIdentifiers.sphincs256, new SPHINCS256KeyParams(new AlgorithmIdentifier(this.treeDigest))
            );
            var1 = new PrivateKeyInfo(var2, new DEROctetString(this.params.getKeyData()), this.attributes);
         }

         return var1.getEncoded();
      } catch (IOException var3) {
         return null;
      }
   }

   @Override
   public String getFormat() {
      return "PKCS#8";
   }

   ASN1ObjectIdentifier getTreeDigest() {
      return this.treeDigest;
   }

   @Override
   public byte[] getKeyData() {
      return this.params.getKeyData();
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
