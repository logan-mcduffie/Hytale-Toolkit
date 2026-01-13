package org.bouncycastle.pqc.jcajce.provider.sphincs;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.security.PublicKey;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.pqc.asn1.PQCObjectIdentifiers;
import org.bouncycastle.pqc.asn1.SPHINCS256KeyParams;
import org.bouncycastle.pqc.crypto.sphincs.SPHINCSPublicKeyParameters;
import org.bouncycastle.pqc.crypto.util.PublicKeyFactory;
import org.bouncycastle.pqc.crypto.util.SubjectPublicKeyInfoFactory;
import org.bouncycastle.pqc.jcajce.interfaces.SPHINCSKey;
import org.bouncycastle.util.Arrays;

public class BCSphincs256PublicKey implements PublicKey, SPHINCSKey {
   private static final long serialVersionUID = 1L;
   private transient ASN1ObjectIdentifier treeDigest;
   private transient SPHINCSPublicKeyParameters params;

   public BCSphincs256PublicKey(ASN1ObjectIdentifier var1, SPHINCSPublicKeyParameters var2) {
      this.treeDigest = var1;
      this.params = var2;
   }

   public BCSphincs256PublicKey(SubjectPublicKeyInfo var1) throws IOException {
      this.init(var1);
   }

   private void init(SubjectPublicKeyInfo var1) throws IOException {
      this.treeDigest = SPHINCS256KeyParams.getInstance(var1.getAlgorithm().getParameters()).getTreeDigest().getAlgorithm();
      this.params = (SPHINCSPublicKeyParameters)PublicKeyFactory.createKey(var1);
   }

   @Override
   public boolean equals(Object var1) {
      if (var1 == this) {
         return true;
      } else if (!(var1 instanceof BCSphincs256PublicKey)) {
         return false;
      } else {
         BCSphincs256PublicKey var2 = (BCSphincs256PublicKey)var1;
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
         SubjectPublicKeyInfo var1;
         if (this.params.getTreeDigest() != null) {
            var1 = SubjectPublicKeyInfoFactory.createSubjectPublicKeyInfo(this.params);
         } else {
            AlgorithmIdentifier var2 = new AlgorithmIdentifier(
               PQCObjectIdentifiers.sphincs256, new SPHINCS256KeyParams(new AlgorithmIdentifier(this.treeDigest))
            );
            var1 = new SubjectPublicKeyInfo(var2, this.params.getKeyData());
         }

         return var1.getEncoded();
      } catch (IOException var3) {
         return null;
      }
   }

   @Override
   public String getFormat() {
      return "X.509";
   }

   @Override
   public byte[] getKeyData() {
      return this.params.getKeyData();
   }

   ASN1ObjectIdentifier getTreeDigest() {
      return this.treeDigest;
   }

   CipherParameters getKeyParams() {
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
