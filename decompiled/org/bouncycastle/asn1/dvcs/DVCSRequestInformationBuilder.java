package org.bouncycastle.asn1.dvcs;

import java.math.BigInteger;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.DERTaggedObject;
import org.bouncycastle.asn1.x509.Extensions;
import org.bouncycastle.asn1.x509.GeneralName;
import org.bouncycastle.asn1.x509.GeneralNames;
import org.bouncycastle.asn1.x509.PolicyInformation;
import org.bouncycastle.util.BigIntegers;

public class DVCSRequestInformationBuilder {
   private int version = 1;
   private final ServiceType service;
   private DVCSRequestInformation initialInfo;
   private BigInteger nonce;
   private DVCSTime requestTime;
   private GeneralNames requester;
   private PolicyInformation requestPolicy;
   private GeneralNames dvcs;
   private GeneralNames dataLocations;
   private Extensions extensions;
   private static final int DEFAULT_VERSION = 1;
   private static final int TAG_REQUESTER = 0;
   private static final int TAG_REQUEST_POLICY = 1;
   private static final int TAG_DVCS = 2;
   private static final int TAG_DATA_LOCATIONS = 3;
   private static final int TAG_EXTENSIONS = 4;

   public DVCSRequestInformationBuilder(ServiceType var1) {
      this.service = var1;
   }

   public DVCSRequestInformationBuilder(DVCSRequestInformation var1) {
      this.initialInfo = var1;
      this.service = var1.getService();
      this.version = var1.getVersion();
      this.nonce = var1.getNonce();
      this.requestTime = var1.getRequestTime();
      this.requestPolicy = var1.getRequestPolicy();
      this.dvcs = var1.getDVCS();
      this.dataLocations = var1.getDataLocations();
   }

   public DVCSRequestInformation build() {
      ASN1EncodableVector var1 = new ASN1EncodableVector(9);
      if (this.version != 1) {
         var1.add(new ASN1Integer(this.version));
      }

      var1.add(this.service);
      if (this.nonce != null) {
         var1.add(new ASN1Integer(this.nonce));
      }

      if (this.requestTime != null) {
         var1.add(this.requestTime);
      }

      int[] var2 = new int[]{0, 1, 2, 3, 4};
      ASN1Encodable[] var3 = new ASN1Encodable[]{this.requester, this.requestPolicy, this.dvcs, this.dataLocations, this.extensions};

      for (int var4 = 0; var4 < var2.length; var4++) {
         int var5 = var2[var4];
         ASN1Encodable var6 = var3[var4];
         if (var6 != null) {
            var1.add(new DERTaggedObject(false, var5, var6));
         }
      }

      return DVCSRequestInformation.getInstance(new DERSequence(var1));
   }

   public void setVersion(int var1) {
      if (this.initialInfo != null) {
         throw new IllegalStateException("cannot change version in existing DVCSRequestInformation");
      } else {
         this.version = var1;
      }
   }

   public void setNonce(BigInteger var1) {
      if (this.initialInfo != null) {
         if (this.initialInfo.getNonce() == null) {
            this.nonce = var1;
         } else {
            byte[] var2 = this.initialInfo.getNonce().toByteArray();
            byte[] var3 = BigIntegers.asUnsignedByteArray(var1);
            byte[] var4 = new byte[var2.length + var3.length];
            System.arraycopy(var2, 0, var4, 0, var2.length);
            System.arraycopy(var3, 0, var4, var2.length, var3.length);
            this.nonce = new BigInteger(var4);
         }
      }

      this.nonce = var1;
   }

   public void setRequestTime(DVCSTime var1) {
      if (this.initialInfo != null) {
         throw new IllegalStateException("cannot change request time in existing DVCSRequestInformation");
      } else {
         this.requestTime = var1;
      }
   }

   public void setRequester(GeneralName var1) {
      this.setRequester(new GeneralNames(var1));
   }

   public void setRequester(GeneralNames var1) {
      this.requester = var1;
   }

   public void setRequestPolicy(PolicyInformation var1) {
      if (this.initialInfo != null) {
         throw new IllegalStateException("cannot change request policy in existing DVCSRequestInformation");
      } else {
         this.requestPolicy = var1;
      }
   }

   public void setDVCS(GeneralName var1) {
      this.setDVCS(new GeneralNames(var1));
   }

   public void setDVCS(GeneralNames var1) {
      this.dvcs = var1;
   }

   public void setDataLocations(GeneralName var1) {
      this.setDataLocations(new GeneralNames(var1));
   }

   public void setDataLocations(GeneralNames var1) {
      this.dataLocations = var1;
   }

   public void setExtensions(Extensions var1) {
      if (this.initialInfo != null) {
         throw new IllegalStateException("cannot change extensions in existing DVCSRequestInformation");
      } else {
         this.extensions = var1;
      }
   }
}
