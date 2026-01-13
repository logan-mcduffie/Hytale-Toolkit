package org.bouncycastle.cms;

import java.security.SecureRandom;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.cms.PasswordRecipientInfo;
import org.bouncycastle.asn1.cms.RecipientInfo;
import org.bouncycastle.asn1.pkcs.PBKDF2Params;
import org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.operator.GenericKey;
import org.bouncycastle.util.Arrays;

public abstract class PasswordRecipientInfoGenerator implements RecipientInfoGenerator {
   protected char[] password;
   private AlgorithmIdentifier keyDerivationAlgorithm;
   private ASN1ObjectIdentifier kekAlgorithm;
   private SecureRandom random;
   private int schemeID;
   private int keySize;
   private int blockSize;
   private PasswordRecipient.PRF prf;
   private byte[] salt;
   private int iterationCount;

   protected PasswordRecipientInfoGenerator(ASN1ObjectIdentifier var1, char[] var2) {
      this(var1, var2, getKeySize(var1), (Integer)PasswordRecipientInformation.BLOCKSIZES.get(var1));
   }

   protected PasswordRecipientInfoGenerator(ASN1ObjectIdentifier var1, char[] var2, int var3, int var4) {
      this.password = var2;
      this.schemeID = 1;
      this.kekAlgorithm = var1;
      this.keySize = var3;
      this.blockSize = var4;
      this.prf = PasswordRecipient.PRF.HMacSHA1;
      this.iterationCount = 1024;
   }

   private static int getKeySize(ASN1ObjectIdentifier var0) {
      Integer var1 = (Integer)PasswordRecipientInformation.KEYSIZES.get(var0);
      if (var1 == null) {
         throw new IllegalArgumentException("cannot find key size for algorithm: " + var0);
      } else {
         return var1;
      }
   }

   public PasswordRecipientInfoGenerator setPasswordConversionScheme(int var1) {
      this.schemeID = var1;
      return this;
   }

   public PasswordRecipientInfoGenerator setPRF(PasswordRecipient.PRF var1) {
      this.prf = var1;
      return this;
   }

   public PasswordRecipientInfoGenerator setSaltAndIterationCount(byte[] var1, int var2) {
      this.salt = Arrays.clone(var1);
      this.iterationCount = var2;
      return this;
   }

   public PasswordRecipientInfoGenerator setSecureRandom(SecureRandom var1) {
      this.random = var1;
      return this;
   }

   @Override
   public RecipientInfo generate(GenericKey var1) throws CMSException {
      byte[] var2 = new byte[this.blockSize];
      if (this.random == null) {
         this.random = new SecureRandom();
      }

      this.random.nextBytes(var2);
      if (this.salt == null) {
         this.salt = new byte[20];
         this.random.nextBytes(this.salt);
      }

      this.keyDerivationAlgorithm = new AlgorithmIdentifier(
         PKCSObjectIdentifiers.id_PBKDF2, new PBKDF2Params(this.salt, this.iterationCount, this.prf.prfAlgID)
      );
      byte[] var3 = this.calculateDerivedKey(this.schemeID, this.keyDerivationAlgorithm, this.keySize);
      AlgorithmIdentifier var4 = new AlgorithmIdentifier(this.kekAlgorithm, new DEROctetString(var2));
      byte[] var5 = this.generateEncryptedBytes(var4, var3, var1);
      DEROctetString var6 = new DEROctetString(var5);
      ASN1EncodableVector var7 = new ASN1EncodableVector();
      var7.add(this.kekAlgorithm);
      var7.add(new DEROctetString(var2));
      AlgorithmIdentifier var8 = new AlgorithmIdentifier(PKCSObjectIdentifiers.id_alg_PWRI_KEK, new DERSequence(var7));
      return new RecipientInfo(new PasswordRecipientInfo(this.keyDerivationAlgorithm, var8, var6));
   }

   protected abstract byte[] calculateDerivedKey(int var1, AlgorithmIdentifier var2, int var3) throws CMSException;

   protected abstract byte[] generateEncryptedBytes(AlgorithmIdentifier var1, byte[] var2, GenericKey var3) throws CMSException;
}
