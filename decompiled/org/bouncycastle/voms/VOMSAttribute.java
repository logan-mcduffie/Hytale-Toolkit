package org.bouncycastle.voms;

import java.util.ArrayList;
import java.util.List;
import org.bouncycastle.asn1.ASN1IA5String;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.x509.Attribute;
import org.bouncycastle.asn1.x509.IetfAttrSyntax;
import org.bouncycastle.cert.X509AttributeCertificateHolder;

public class VOMSAttribute {
   public static final String VOMS_ATTR_OID = "1.3.6.1.4.1.8005.100.100.4";
   private X509AttributeCertificateHolder myAC;
   private String myHostPort;
   private String myVo;
   private List myStringList = new ArrayList();
   private List myFQANs = new ArrayList();

   public VOMSAttribute(X509AttributeCertificateHolder var1) {
      if (var1 == null) {
         throw new IllegalArgumentException("VOMSAttribute: AttributeCertificate is NULL");
      } else {
         this.myAC = var1;
         Attribute[] var2 = var1.getAttributes(new ASN1ObjectIdentifier("1.3.6.1.4.1.8005.100.100.4"));
         if (var2 != null) {
            try {
               for (int var3 = 0; var3 != var2.length; var3++) {
                  IetfAttrSyntax var4 = IetfAttrSyntax.getInstance(var2[var3].getAttributeValues()[0]);
                  String var5 = ((ASN1IA5String)var4.getPolicyAuthority().getNames()[0].getName()).getString();
                  int var6 = var5.indexOf("://");
                  if (var6 < 0 || var6 == var5.length() - 1) {
                     throw new IllegalArgumentException("Bad encoding of VOMS policyAuthority : [" + var5 + "]");
                  }

                  this.myVo = var5.substring(0, var6);
                  this.myHostPort = var5.substring(var6 + 3);
                  if (var4.getValueType() != 1) {
                     throw new IllegalArgumentException("VOMS attribute values are not encoded as octet strings, policyAuthority = " + var5);
                  }

                  ASN1OctetString[] var7 = (ASN1OctetString[])var4.getValues();

                  for (int var8 = 0; var8 != var7.length; var8++) {
                     String var9 = new String(var7[var8].getOctets());
                     VOMSAttribute.FQAN var10 = new VOMSAttribute.FQAN(var9);
                     if (!this.myStringList.contains(var9) && var9.startsWith("/" + this.myVo + "/")) {
                        this.myStringList.add(var9);
                        this.myFQANs.add(var10);
                     }
                  }
               }
            } catch (IllegalArgumentException var11) {
               throw var11;
            } catch (Exception var12) {
               throw new IllegalArgumentException("Badly encoded VOMS extension in AC issued by " + var1.getIssuer());
            }
         }
      }
   }

   public X509AttributeCertificateHolder getAC() {
      return this.myAC;
   }

   public List getFullyQualifiedAttributes() {
      return this.myStringList;
   }

   public List getListOfFQAN() {
      return this.myFQANs;
   }

   public String getHostPort() {
      return this.myHostPort;
   }

   public String getVO() {
      return this.myVo;
   }

   @Override
   public String toString() {
      return "VO      :" + this.myVo + "\nHostPort:" + this.myHostPort + "\nFQANs   :" + this.myFQANs;
   }

   public static class FQAN {
      String fqan;
      String group;
      String role;
      String capability;

      public FQAN(String var1) {
         this.fqan = var1;
      }

      public FQAN(String var1, String var2, String var3) {
         this.group = var1;
         this.role = var2;
         this.capability = var3;
      }

      public String getFQAN() {
         if (this.fqan != null) {
            return this.fqan;
         } else {
            this.fqan = this.group + "/Role=" + (this.role != null ? this.role : "") + (this.capability != null ? "/Capability=" + this.capability : "");
            return this.fqan;
         }
      }

      protected void split() {
         int var1 = this.fqan.length();
         int var2 = this.fqan.indexOf("/Role=");
         if (var2 >= 0) {
            this.group = this.fqan.substring(0, var2);
            int var3 = this.fqan.indexOf("/Capability=", var2 + 6);
            String var4 = var3 < 0 ? this.fqan.substring(var2 + 6) : this.fqan.substring(var2 + 6, var3);
            this.role = var4.length() == 0 ? null : var4;
            var4 = var3 < 0 ? null : this.fqan.substring(var3 + 12);
            this.capability = var4 != null && var4.length() != 0 ? var4 : null;
         }
      }

      public String getGroup() {
         if (this.group == null && this.fqan != null) {
            this.split();
         }

         return this.group;
      }

      public String getRole() {
         if (this.group == null && this.fqan != null) {
            this.split();
         }

         return this.role;
      }

      public String getCapability() {
         if (this.group == null && this.fqan != null) {
            this.split();
         }

         return this.capability;
      }

      @Override
      public String toString() {
         return this.getFQAN();
      }
   }
}
