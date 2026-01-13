package org.bouncycastle.asn1.dvcs;

import java.math.BigInteger;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1GeneralizedTime;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.DERTaggedObject;
import org.bouncycastle.asn1.x509.Extensions;
import org.bouncycastle.asn1.x509.GeneralNames;
import org.bouncycastle.asn1.x509.PolicyInformation;

public class DVCSRequestInformation extends ASN1Object {
   private int version = 1;
   private ServiceType service;
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

   private DVCSRequestInformation(ASN1Sequence var1) {
      int var2 = 0;
      if (var1.getObjectAt(0) instanceof ASN1Integer) {
         ASN1Integer var3 = ASN1Integer.getInstance(var1.getObjectAt(var2++));
         this.version = var3.intValueExact();
      } else {
         this.version = 1;
      }

      for (this.service = ServiceType.getInstance(var1.getObjectAt(var2++)); var2 < var1.size(); var2++) {
         ASN1Encodable var7 = var1.getObjectAt(var2);
         if (var7 instanceof ASN1Integer) {
            this.nonce = ASN1Integer.getInstance(var7).getValue();
         } else if (var7 instanceof ASN1GeneralizedTime) {
            this.requestTime = DVCSTime.getInstance(var7);
         } else if (var7 instanceof ASN1TaggedObject) {
            ASN1TaggedObject var4 = ASN1TaggedObject.getInstance(var7);
            int var5 = var4.getTagNo();
            switch (var5) {
               case 0:
                  this.requester = GeneralNames.getInstance(var4, false);
                  break;
               case 1:
                  this.requestPolicy = PolicyInformation.getInstance(ASN1Sequence.getInstance(var4, false));
                  break;
               case 2:
                  this.dvcs = GeneralNames.getInstance(var4, false);
                  break;
               case 3:
                  this.dataLocations = GeneralNames.getInstance(var4, false);
                  break;
               case 4:
                  this.extensions = Extensions.getInstance(var4, false);
                  break;
               default:
                  throw new IllegalArgumentException("unknown tag number encountered: " + var5);
            }
         } else {
            this.requestTime = DVCSTime.getInstance(var7);
         }
      }
   }

   public static DVCSRequestInformation getInstance(Object var0) {
      if (var0 instanceof DVCSRequestInformation) {
         return (DVCSRequestInformation)var0;
      } else {
         return var0 != null ? new DVCSRequestInformation(ASN1Sequence.getInstance(var0)) : null;
      }
   }

   public static DVCSRequestInformation getInstance(ASN1TaggedObject var0, boolean var1) {
      return getInstance(ASN1Sequence.getInstance(var0, var1));
   }

   @Override
   public ASN1Primitive toASN1Primitive() {
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

      return new DERSequence(var1);
   }

   @Override
   public String toString() {
      StringBuilder var1 = new StringBuilder();
      var1.append("DVCSRequestInformation {\n");
      if (this.version != 1) {
         var1.append("version: " + this.version + "\n");
      }

      var1.append("service: " + this.service + "\n");
      if (this.nonce != null) {
         var1.append("nonce: " + this.nonce + "\n");
      }

      if (this.requestTime != null) {
         var1.append("requestTime: " + this.requestTime + "\n");
      }

      if (this.requester != null) {
         var1.append("requester: " + this.requester + "\n");
      }

      if (this.requestPolicy != null) {
         var1.append("requestPolicy: " + this.requestPolicy + "\n");
      }

      if (this.dvcs != null) {
         var1.append("dvcs: " + this.dvcs + "\n");
      }

      if (this.dataLocations != null) {
         var1.append("dataLocations: " + this.dataLocations + "\n");
      }

      if (this.extensions != null) {
         var1.append("extensions: " + this.extensions + "\n");
      }

      var1.append("}\n");
      return var1.toString();
   }

   public int getVersion() {
      return this.version;
   }

   public ServiceType getService() {
      return this.service;
   }

   public BigInteger getNonce() {
      return this.nonce;
   }

   public DVCSTime getRequestTime() {
      return this.requestTime;
   }

   public GeneralNames getRequester() {
      return this.requester;
   }

   public PolicyInformation getRequestPolicy() {
      return this.requestPolicy;
   }

   public GeneralNames getDVCS() {
      return this.dvcs;
   }

   public GeneralNames getDataLocations() {
      return this.dataLocations;
   }

   public Extensions getExtensions() {
      return this.extensions;
   }
}
