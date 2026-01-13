package org.bouncycastle.asn1.isismtt.x509;

import java.util.Enumeration;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1PrintableString;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.asn1.DERPrintableString;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.DERTaggedObject;
import org.bouncycastle.asn1.x500.DirectoryString;

public class ProfessionInfo extends ASN1Object {
   public static final ASN1ObjectIdentifier Rechtsanwltin = new ASN1ObjectIdentifier(
      NamingAuthority.id_isismtt_at_namingAuthorities_RechtWirtschaftSteuern + ".1"
   );
   public static final ASN1ObjectIdentifier Rechtsanwalt = new ASN1ObjectIdentifier(
      NamingAuthority.id_isismtt_at_namingAuthorities_RechtWirtschaftSteuern + ".2"
   );
   public static final ASN1ObjectIdentifier Rechtsbeistand = new ASN1ObjectIdentifier(
      NamingAuthority.id_isismtt_at_namingAuthorities_RechtWirtschaftSteuern + ".3"
   );
   public static final ASN1ObjectIdentifier Steuerberaterin = new ASN1ObjectIdentifier(
      NamingAuthority.id_isismtt_at_namingAuthorities_RechtWirtschaftSteuern + ".4"
   );
   public static final ASN1ObjectIdentifier Steuerberater = new ASN1ObjectIdentifier(
      NamingAuthority.id_isismtt_at_namingAuthorities_RechtWirtschaftSteuern + ".5"
   );
   public static final ASN1ObjectIdentifier Steuerbevollmchtigte = new ASN1ObjectIdentifier(
      NamingAuthority.id_isismtt_at_namingAuthorities_RechtWirtschaftSteuern + ".6"
   );
   public static final ASN1ObjectIdentifier Steuerbevollmchtigter = new ASN1ObjectIdentifier(
      NamingAuthority.id_isismtt_at_namingAuthorities_RechtWirtschaftSteuern + ".7"
   );
   public static final ASN1ObjectIdentifier Notarin = new ASN1ObjectIdentifier(NamingAuthority.id_isismtt_at_namingAuthorities_RechtWirtschaftSteuern + ".8");
   public static final ASN1ObjectIdentifier Notar = new ASN1ObjectIdentifier(NamingAuthority.id_isismtt_at_namingAuthorities_RechtWirtschaftSteuern + ".9");
   public static final ASN1ObjectIdentifier Notarvertreterin = new ASN1ObjectIdentifier(
      NamingAuthority.id_isismtt_at_namingAuthorities_RechtWirtschaftSteuern + ".10"
   );
   public static final ASN1ObjectIdentifier Notarvertreter = new ASN1ObjectIdentifier(
      NamingAuthority.id_isismtt_at_namingAuthorities_RechtWirtschaftSteuern + ".11"
   );
   public static final ASN1ObjectIdentifier Notariatsverwalterin = new ASN1ObjectIdentifier(
      NamingAuthority.id_isismtt_at_namingAuthorities_RechtWirtschaftSteuern + ".12"
   );
   public static final ASN1ObjectIdentifier Notariatsverwalter = new ASN1ObjectIdentifier(
      NamingAuthority.id_isismtt_at_namingAuthorities_RechtWirtschaftSteuern + ".13"
   );
   public static final ASN1ObjectIdentifier Wirtschaftsprferin = new ASN1ObjectIdentifier(
      NamingAuthority.id_isismtt_at_namingAuthorities_RechtWirtschaftSteuern + ".14"
   );
   public static final ASN1ObjectIdentifier Wirtschaftsprfer = new ASN1ObjectIdentifier(
      NamingAuthority.id_isismtt_at_namingAuthorities_RechtWirtschaftSteuern + ".15"
   );
   public static final ASN1ObjectIdentifier VereidigteBuchprferin = new ASN1ObjectIdentifier(
      NamingAuthority.id_isismtt_at_namingAuthorities_RechtWirtschaftSteuern + ".16"
   );
   public static final ASN1ObjectIdentifier VereidigterBuchprfer = new ASN1ObjectIdentifier(
      NamingAuthority.id_isismtt_at_namingAuthorities_RechtWirtschaftSteuern + ".17"
   );
   public static final ASN1ObjectIdentifier Patentanwltin = new ASN1ObjectIdentifier(
      NamingAuthority.id_isismtt_at_namingAuthorities_RechtWirtschaftSteuern + ".18"
   );
   public static final ASN1ObjectIdentifier Patentanwalt = new ASN1ObjectIdentifier(
      NamingAuthority.id_isismtt_at_namingAuthorities_RechtWirtschaftSteuern + ".19"
   );
   private NamingAuthority namingAuthority;
   private ASN1Sequence professionItems;
   private ASN1Sequence professionOIDs;
   private String registrationNumber;
   private ASN1OctetString addProfessionInfo;

   public static ProfessionInfo getInstance(Object var0) {
      if (var0 == null || var0 instanceof ProfessionInfo) {
         return (ProfessionInfo)var0;
      } else if (var0 instanceof ASN1Sequence) {
         return new ProfessionInfo((ASN1Sequence)var0);
      } else {
         throw new IllegalArgumentException("illegal object in getInstance: " + var0.getClass().getName());
      }
   }

   private ProfessionInfo(ASN1Sequence var1) {
      if (var1.size() > 5) {
         throw new IllegalArgumentException("Bad sequence size: " + var1.size());
      } else {
         Enumeration var2 = var1.getObjects();
         ASN1Encodable var3 = (ASN1Encodable)var2.nextElement();
         if (var3 instanceof ASN1TaggedObject) {
            if (((ASN1TaggedObject)var3).getTagNo() != 0) {
               throw new IllegalArgumentException("Bad tag number: " + ((ASN1TaggedObject)var3).getTagNo());
            }

            this.namingAuthority = NamingAuthority.getInstance((ASN1TaggedObject)var3, true);
            var3 = (ASN1Encodable)var2.nextElement();
         }

         this.professionItems = ASN1Sequence.getInstance(var3);
         if (var2.hasMoreElements()) {
            var3 = (ASN1Encodable)var2.nextElement();
            if (var3 instanceof ASN1Sequence) {
               this.professionOIDs = ASN1Sequence.getInstance(var3);
            } else if (var3 instanceof ASN1PrintableString) {
               this.registrationNumber = ASN1PrintableString.getInstance(var3).getString();
            } else {
               if (!(var3 instanceof ASN1OctetString)) {
                  throw new IllegalArgumentException("Bad object encountered: " + var3.getClass());
               }

               this.addProfessionInfo = ASN1OctetString.getInstance(var3);
            }
         }

         if (var2.hasMoreElements()) {
            var3 = (ASN1Encodable)var2.nextElement();
            if (var3 instanceof ASN1PrintableString) {
               this.registrationNumber = ASN1PrintableString.getInstance(var3).getString();
            } else {
               if (!(var3 instanceof DEROctetString)) {
                  throw new IllegalArgumentException("Bad object encountered: " + var3.getClass());
               }

               this.addProfessionInfo = (DEROctetString)var3;
            }
         }

         if (var2.hasMoreElements()) {
            var3 = (ASN1Encodable)var2.nextElement();
            if (!(var3 instanceof DEROctetString)) {
               throw new IllegalArgumentException("Bad object encountered: " + var3.getClass());
            }

            this.addProfessionInfo = (DEROctetString)var3;
         }
      }
   }

   public ProfessionInfo(NamingAuthority var1, DirectoryString[] var2, ASN1ObjectIdentifier[] var3, String var4, ASN1OctetString var5) {
      this.namingAuthority = var1;
      this.professionItems = new DERSequence(var2);
      if (var3 != null) {
         this.professionOIDs = new DERSequence(var3);
      }

      this.registrationNumber = var4;
      this.addProfessionInfo = var5;
   }

   @Override
   public ASN1Primitive toASN1Primitive() {
      ASN1EncodableVector var1 = new ASN1EncodableVector(5);
      if (this.namingAuthority != null) {
         var1.add(new DERTaggedObject(true, 0, this.namingAuthority));
      }

      var1.add(this.professionItems);
      if (this.professionOIDs != null) {
         var1.add(this.professionOIDs);
      }

      if (this.registrationNumber != null) {
         var1.add(new DERPrintableString(this.registrationNumber, true));
      }

      if (this.addProfessionInfo != null) {
         var1.add(this.addProfessionInfo);
      }

      return new DERSequence(var1);
   }

   public ASN1OctetString getAddProfessionInfo() {
      return this.addProfessionInfo;
   }

   public NamingAuthority getNamingAuthority() {
      return this.namingAuthority;
   }

   public DirectoryString[] getProfessionItems() {
      DirectoryString[] var1 = new DirectoryString[this.professionItems.size()];
      int var2 = 0;
      Enumeration var3 = this.professionItems.getObjects();

      while (var3.hasMoreElements()) {
         var1[var2++] = DirectoryString.getInstance(var3.nextElement());
      }

      return var1;
   }

   public ASN1ObjectIdentifier[] getProfessionOIDs() {
      if (this.professionOIDs == null) {
         return new ASN1ObjectIdentifier[0];
      } else {
         ASN1ObjectIdentifier[] var1 = new ASN1ObjectIdentifier[this.professionOIDs.size()];
         int var2 = 0;
         Enumeration var3 = this.professionOIDs.getObjects();

         while (var3.hasMoreElements()) {
            var1[var2++] = ASN1ObjectIdentifier.getInstance(var3.nextElement());
         }

         return var1;
      }
   }

   public String getRegistrationNumber() {
      return this.registrationNumber;
   }
}
