package org.bouncycastle.asn1.pkcs;

import java.util.Enumeration;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.DERSequence;

public class PBES2Parameters extends ASN1Object implements PKCSObjectIdentifiers {
   private KeyDerivationFunc func;
   private EncryptionScheme scheme;

   public static PBES2Parameters getInstance(Object var0) {
      if (var0 instanceof PBES2Parameters) {
         return (PBES2Parameters)var0;
      } else {
         return var0 != null ? new PBES2Parameters(ASN1Sequence.getInstance(var0)) : null;
      }
   }

   public PBES2Parameters(KeyDerivationFunc var1, EncryptionScheme var2) {
      this.func = var1;
      this.scheme = var2;
   }

   private PBES2Parameters(ASN1Sequence var1) {
      Enumeration var2 = var1.getObjects();
      ASN1Sequence var3 = ASN1Sequence.getInstance(((ASN1Encodable)var2.nextElement()).toASN1Primitive());
      if (var3.getObjectAt(0).equals(id_PBKDF2)) {
         this.func = new KeyDerivationFunc(id_PBKDF2, PBKDF2Params.getInstance(var3.getObjectAt(1)));
      } else {
         this.func = KeyDerivationFunc.getInstance(var3);
      }

      this.scheme = EncryptionScheme.getInstance(var2.nextElement());
   }

   public KeyDerivationFunc getKeyDerivationFunc() {
      return this.func;
   }

   public EncryptionScheme getEncryptionScheme() {
      return this.scheme;
   }

   @Override
   public ASN1Primitive toASN1Primitive() {
      return new DERSequence(this.func, this.scheme);
   }
}
