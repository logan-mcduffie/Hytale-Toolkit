package org.bouncycastle.asn1.x509.qualified;

import java.util.Enumeration;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1IA5String;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.DERIA5String;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;

public class BiometricData extends ASN1Object {
   private TypeOfBiometricData typeOfBiometricData;
   private AlgorithmIdentifier hashAlgorithm;
   private ASN1OctetString biometricDataHash;
   private ASN1IA5String sourceDataUri;

   public static BiometricData getInstance(Object var0) {
      if (var0 instanceof BiometricData) {
         return (BiometricData)var0;
      } else {
         return var0 != null ? new BiometricData(ASN1Sequence.getInstance(var0)) : null;
      }
   }

   private BiometricData(ASN1Sequence var1) {
      Enumeration var2 = var1.getObjects();
      this.typeOfBiometricData = TypeOfBiometricData.getInstance(var2.nextElement());
      this.hashAlgorithm = AlgorithmIdentifier.getInstance(var2.nextElement());
      this.biometricDataHash = ASN1OctetString.getInstance(var2.nextElement());
      if (var2.hasMoreElements()) {
         this.sourceDataUri = ASN1IA5String.getInstance(var2.nextElement());
      }
   }

   public BiometricData(TypeOfBiometricData var1, AlgorithmIdentifier var2, ASN1OctetString var3, ASN1IA5String var4) {
      this.typeOfBiometricData = var1;
      this.hashAlgorithm = var2;
      this.biometricDataHash = var3;
      this.sourceDataUri = var4;
   }

   public BiometricData(TypeOfBiometricData var1, AlgorithmIdentifier var2, ASN1OctetString var3) {
      this.typeOfBiometricData = var1;
      this.hashAlgorithm = var2;
      this.biometricDataHash = var3;
      this.sourceDataUri = null;
   }

   public TypeOfBiometricData getTypeOfBiometricData() {
      return this.typeOfBiometricData;
   }

   public AlgorithmIdentifier getHashAlgorithm() {
      return this.hashAlgorithm;
   }

   public ASN1OctetString getBiometricDataHash() {
      return this.biometricDataHash;
   }

   /** @deprecated */
   public DERIA5String getSourceDataUri() {
      return null != this.sourceDataUri && !(this.sourceDataUri instanceof DERIA5String)
         ? new DERIA5String(this.sourceDataUri.getString(), false)
         : (DERIA5String)this.sourceDataUri;
   }

   public ASN1IA5String getSourceDataUriIA5() {
      return this.sourceDataUri;
   }

   @Override
   public ASN1Primitive toASN1Primitive() {
      ASN1EncodableVector var1 = new ASN1EncodableVector(4);
      var1.add(this.typeOfBiometricData);
      var1.add(this.hashAlgorithm);
      var1.add(this.biometricDataHash);
      if (this.sourceDataUri != null) {
         var1.add(this.sourceDataUri);
      }

      return new DERSequence(var1);
   }
}
