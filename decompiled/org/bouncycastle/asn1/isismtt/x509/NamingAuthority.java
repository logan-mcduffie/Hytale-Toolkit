package org.bouncycastle.asn1.isismtt.x509;

import java.util.Enumeration;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1IA5String;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1String;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.DERIA5String;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.isismtt.ISISMTTObjectIdentifiers;
import org.bouncycastle.asn1.x500.DirectoryString;

public class NamingAuthority extends ASN1Object {
   public static final ASN1ObjectIdentifier id_isismtt_at_namingAuthorities_RechtWirtschaftSteuern = new ASN1ObjectIdentifier(
      ISISMTTObjectIdentifiers.id_isismtt_at_namingAuthorities + ".1"
   );
   private ASN1ObjectIdentifier namingAuthorityId;
   private String namingAuthorityUrl;
   private DirectoryString namingAuthorityText;

   public static NamingAuthority getInstance(Object var0) {
      if (var0 == null || var0 instanceof NamingAuthority) {
         return (NamingAuthority)var0;
      } else if (var0 instanceof ASN1Sequence) {
         return new NamingAuthority((ASN1Sequence)var0);
      } else {
         throw new IllegalArgumentException("illegal object in getInstance: " + var0.getClass().getName());
      }
   }

   public static NamingAuthority getInstance(ASN1TaggedObject var0, boolean var1) {
      return getInstance(ASN1Sequence.getInstance(var0, var1));
   }

   private NamingAuthority(ASN1Sequence var1) {
      if (var1.size() > 3) {
         throw new IllegalArgumentException("Bad sequence size: " + var1.size());
      } else {
         Enumeration var2 = var1.getObjects();
         if (var2.hasMoreElements()) {
            ASN1Encodable var3 = (ASN1Encodable)var2.nextElement();
            if (var3 instanceof ASN1ObjectIdentifier) {
               this.namingAuthorityId = (ASN1ObjectIdentifier)var3;
            } else if (var3 instanceof ASN1IA5String) {
               this.namingAuthorityUrl = ASN1IA5String.getInstance(var3).getString();
            } else {
               if (!(var3 instanceof ASN1String)) {
                  throw new IllegalArgumentException("Bad object encountered: " + var3.getClass());
               }

               this.namingAuthorityText = DirectoryString.getInstance(var3);
            }
         }

         if (var2.hasMoreElements()) {
            ASN1Encodable var4 = (ASN1Encodable)var2.nextElement();
            if (var4 instanceof ASN1IA5String) {
               this.namingAuthorityUrl = ASN1IA5String.getInstance(var4).getString();
            } else {
               if (!(var4 instanceof ASN1String)) {
                  throw new IllegalArgumentException("Bad object encountered: " + var4.getClass());
               }

               this.namingAuthorityText = DirectoryString.getInstance(var4);
            }
         }

         if (var2.hasMoreElements()) {
            ASN1Encodable var5 = (ASN1Encodable)var2.nextElement();
            if (!(var5 instanceof ASN1String)) {
               throw new IllegalArgumentException("Bad object encountered: " + var5.getClass());
            }

            this.namingAuthorityText = DirectoryString.getInstance(var5);
         }
      }
   }

   public ASN1ObjectIdentifier getNamingAuthorityId() {
      return this.namingAuthorityId;
   }

   public DirectoryString getNamingAuthorityText() {
      return this.namingAuthorityText;
   }

   public String getNamingAuthorityUrl() {
      return this.namingAuthorityUrl;
   }

   public NamingAuthority(ASN1ObjectIdentifier var1, String var2, DirectoryString var3) {
      this.namingAuthorityId = var1;
      this.namingAuthorityUrl = var2;
      this.namingAuthorityText = var3;
   }

   @Override
   public ASN1Primitive toASN1Primitive() {
      ASN1EncodableVector var1 = new ASN1EncodableVector(3);
      if (this.namingAuthorityId != null) {
         var1.add(this.namingAuthorityId);
      }

      if (this.namingAuthorityUrl != null) {
         var1.add(new DERIA5String(this.namingAuthorityUrl, true));
      }

      if (this.namingAuthorityText != null) {
         var1.add(this.namingAuthorityText);
      }

      return new DERSequence(var1);
   }
}
