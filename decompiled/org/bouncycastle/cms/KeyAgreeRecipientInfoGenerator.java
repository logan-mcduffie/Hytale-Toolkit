package org.bouncycastle.cms;

import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.DERNull;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.asn1.cms.KeyAgreeRecipientInfo;
import org.bouncycastle.asn1.cms.OriginatorIdentifierOrKey;
import org.bouncycastle.asn1.cms.OriginatorPublicKey;
import org.bouncycastle.asn1.cms.RecipientInfo;
import org.bouncycastle.asn1.cryptopro.CryptoProObjectIdentifiers;
import org.bouncycastle.asn1.cryptopro.Gost2814789KeyWrapParameters;
import org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.operator.GenericKey;

public abstract class KeyAgreeRecipientInfoGenerator implements RecipientInfoGenerator {
   private ASN1ObjectIdentifier keyAgreementOID;
   private ASN1ObjectIdentifier keyEncryptionOID;
   private SubjectPublicKeyInfo originatorKeyInfo;

   protected KeyAgreeRecipientInfoGenerator(ASN1ObjectIdentifier var1, SubjectPublicKeyInfo var2, ASN1ObjectIdentifier var3) {
      this.originatorKeyInfo = var2;
      this.keyAgreementOID = var1;
      this.keyEncryptionOID = var3;
   }

   @Override
   public RecipientInfo generate(GenericKey var1) throws CMSException {
      OriginatorIdentifierOrKey var2 = new OriginatorIdentifierOrKey(this.createOriginatorPublicKey(this.originatorKeyInfo));
      AlgorithmIdentifier var3;
      if (CMSUtils.isDES(this.keyEncryptionOID.getId()) || this.keyEncryptionOID.equals(PKCSObjectIdentifiers.id_alg_CMSRC2wrap)) {
         var3 = new AlgorithmIdentifier(this.keyEncryptionOID, DERNull.INSTANCE);
      } else if (CMSUtils.isGOST(this.keyAgreementOID)) {
         var3 = new AlgorithmIdentifier(
            this.keyEncryptionOID, new Gost2814789KeyWrapParameters(CryptoProObjectIdentifiers.id_Gost28147_89_CryptoPro_A_ParamSet)
         );
      } else {
         var3 = new AlgorithmIdentifier(this.keyEncryptionOID);
      }

      AlgorithmIdentifier var4 = new AlgorithmIdentifier(this.keyAgreementOID, var3);
      ASN1Sequence var5 = this.generateRecipientEncryptedKeys(var4, var3, var1);
      byte[] var6 = this.getUserKeyingMaterial(var4);
      return var6 != null
         ? new RecipientInfo(new KeyAgreeRecipientInfo(var2, new DEROctetString(var6), var4, var5))
         : new RecipientInfo(new KeyAgreeRecipientInfo(var2, null, var4, var5));
   }

   protected OriginatorPublicKey createOriginatorPublicKey(SubjectPublicKeyInfo var1) {
      return new OriginatorPublicKey(var1.getAlgorithm(), var1.getPublicKeyData().getBytes());
   }

   protected abstract ASN1Sequence generateRecipientEncryptedKeys(AlgorithmIdentifier var1, AlgorithmIdentifier var2, GenericKey var3) throws CMSException;

   protected abstract byte[] getUserKeyingMaterial(AlgorithmIdentifier var1) throws CMSException;
}
