package org.bouncycastle.asn1.x509;

import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.Map.Entry;
import org.bouncycastle.asn1.ASN1IA5String;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.x500.RDN;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x500.style.IETFUtils;
import org.bouncycastle.asn1.x500.style.RFC4519Style;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.util.Integers;
import org.bouncycastle.util.Strings;
import org.bouncycastle.util.encoders.Hex;

public class PKIXNameConstraintValidator implements NameConstraintValidator {
   private Set excludedSubtreesDN = new HashSet();
   private Set excludedSubtreesDNS = new HashSet();
   private Set excludedSubtreesEmail = new HashSet();
   private Set excludedSubtreesURI = new HashSet();
   private Set excludedSubtreesIP = new HashSet();
   private Set excludedSubtreesOtherName = new HashSet();
   private Set permittedSubtreesDN;
   private Set permittedSubtreesDNS;
   private Set permittedSubtreesEmail;
   private Set permittedSubtreesURI;
   private Set permittedSubtreesIP;
   private Set permittedSubtreesOtherName;

   @Override
   public void checkPermitted(GeneralName var1) throws NameConstraintValidatorException {
      switch (var1.getTagNo()) {
         case 0:
            this.checkPermittedOtherName(this.permittedSubtreesOtherName, OtherName.getInstance(var1.getName()));
            break;
         case 1:
            this.checkPermittedEmail(this.permittedSubtreesEmail, this.extractNameAsString(var1));
            break;
         case 2:
            this.checkPermittedDNS(this.permittedSubtreesDNS, this.extractNameAsString(var1));
         case 3:
         case 5:
         default:
            break;
         case 4:
            this.checkPermittedDN(X500Name.getInstance(var1.getName()));
            break;
         case 6:
            this.checkPermittedURI(this.permittedSubtreesURI, this.extractNameAsString(var1));
            break;
         case 7:
            this.checkPermittedIP(this.permittedSubtreesIP, ASN1OctetString.getInstance(var1.getName()).getOctets());
      }
   }

   @Override
   public void checkExcluded(GeneralName var1) throws NameConstraintValidatorException {
      switch (var1.getTagNo()) {
         case 0:
            this.checkExcludedOtherName(this.excludedSubtreesOtherName, OtherName.getInstance(var1.getName()));
            break;
         case 1:
            this.checkExcludedEmail(this.excludedSubtreesEmail, this.extractNameAsString(var1));
            break;
         case 2:
            this.checkExcludedDNS(this.excludedSubtreesDNS, this.extractNameAsString(var1));
         case 3:
         case 5:
         default:
            break;
         case 4:
            this.checkExcludedDN(X500Name.getInstance(var1.getName()));
            break;
         case 6:
            this.checkExcludedURI(this.excludedSubtreesURI, this.extractNameAsString(var1));
            break;
         case 7:
            this.checkExcludedIP(this.excludedSubtreesIP, ASN1OctetString.getInstance(var1.getName()).getOctets());
      }
   }

   @Override
   public void intersectPermittedSubtree(GeneralSubtree var1) {
      this.intersectPermittedSubtree(new GeneralSubtree[]{var1});
   }

   @Override
   public void intersectPermittedSubtree(GeneralSubtree[] var1) {
      HashMap var2 = new HashMap();

      for (int var3 = 0; var3 != var1.length; var3++) {
         GeneralSubtree var4 = var1[var3];
         Integer var5 = Integers.valueOf(var4.getBase().getTagNo());
         if (var2.get(var5) == null) {
            var2.put(var5, new HashSet());
         }

         ((Set)var2.get(var5)).add(var4);
      }

      for (Entry var7 : var2.entrySet()) {
         int var8 = (Integer)var7.getKey();
         switch (var8) {
            case 0:
               this.permittedSubtreesOtherName = this.intersectOtherName(this.permittedSubtreesOtherName, (Set)var7.getValue());
               break;
            case 1:
               this.permittedSubtreesEmail = this.intersectEmail(this.permittedSubtreesEmail, (Set)var7.getValue());
               break;
            case 2:
               this.permittedSubtreesDNS = this.intersectDNS(this.permittedSubtreesDNS, (Set)var7.getValue());
               break;
            case 3:
            case 5:
            default:
               throw new IllegalStateException("Unknown tag encountered: " + var8);
            case 4:
               this.permittedSubtreesDN = this.intersectDN(this.permittedSubtreesDN, (Set)var7.getValue());
               break;
            case 6:
               this.permittedSubtreesURI = this.intersectURI(this.permittedSubtreesURI, (Set)var7.getValue());
               break;
            case 7:
               this.permittedSubtreesIP = this.intersectIP(this.permittedSubtreesIP, (Set)var7.getValue());
         }
      }
   }

   @Override
   public void intersectEmptyPermittedSubtree(int var1) {
      switch (var1) {
         case 0:
            this.permittedSubtreesOtherName = new HashSet();
            break;
         case 1:
            this.permittedSubtreesEmail = new HashSet();
            break;
         case 2:
            this.permittedSubtreesDNS = new HashSet();
            break;
         case 3:
         case 5:
         default:
            throw new IllegalStateException("Unknown tag encountered: " + var1);
         case 4:
            this.permittedSubtreesDN = new HashSet();
            break;
         case 6:
            this.permittedSubtreesURI = new HashSet();
            break;
         case 7:
            this.permittedSubtreesIP = new HashSet();
      }
   }

   @Override
   public void addExcludedSubtree(GeneralSubtree var1) {
      GeneralName var2 = var1.getBase();
      switch (var2.getTagNo()) {
         case 0:
            this.excludedSubtreesOtherName = this.unionOtherName(this.excludedSubtreesOtherName, OtherName.getInstance(var2.getName()));
            break;
         case 1:
            this.excludedSubtreesEmail = this.unionEmail(this.excludedSubtreesEmail, this.extractNameAsString(var2));
            break;
         case 2:
            this.excludedSubtreesDNS = this.unionDNS(this.excludedSubtreesDNS, this.extractNameAsString(var2));
            break;
         case 3:
         case 5:
         default:
            throw new IllegalStateException("Unknown tag encountered: " + var2.getTagNo());
         case 4:
            this.excludedSubtreesDN = this.unionDN(this.excludedSubtreesDN, (ASN1Sequence)var2.getName().toASN1Primitive());
            break;
         case 6:
            this.excludedSubtreesURI = this.unionURI(this.excludedSubtreesURI, this.extractNameAsString(var2));
            break;
         case 7:
            this.excludedSubtreesIP = this.unionIP(this.excludedSubtreesIP, ASN1OctetString.getInstance(var2.getName()).getOctets());
      }
   }

   @Override
   public int hashCode() {
      return this.hashCollection(this.excludedSubtreesDN)
         + this.hashCollection(this.excludedSubtreesDNS)
         + this.hashCollection(this.excludedSubtreesEmail)
         + this.hashCollection(this.excludedSubtreesIP)
         + this.hashCollection(this.excludedSubtreesURI)
         + this.hashCollection(this.excludedSubtreesOtherName)
         + this.hashCollection(this.permittedSubtreesDN)
         + this.hashCollection(this.permittedSubtreesDNS)
         + this.hashCollection(this.permittedSubtreesEmail)
         + this.hashCollection(this.permittedSubtreesIP)
         + this.hashCollection(this.permittedSubtreesURI)
         + this.hashCollection(this.permittedSubtreesOtherName);
   }

   @Override
   public boolean equals(Object var1) {
      if (!(var1 instanceof PKIXNameConstraintValidator)) {
         return false;
      } else {
         PKIXNameConstraintValidator var2 = (PKIXNameConstraintValidator)var1;
         return this.collectionsAreEqual(var2.excludedSubtreesDN, this.excludedSubtreesDN)
            && this.collectionsAreEqual(var2.excludedSubtreesDNS, this.excludedSubtreesDNS)
            && this.collectionsAreEqual(var2.excludedSubtreesEmail, this.excludedSubtreesEmail)
            && this.collectionsAreEqual(var2.excludedSubtreesIP, this.excludedSubtreesIP)
            && this.collectionsAreEqual(var2.excludedSubtreesURI, this.excludedSubtreesURI)
            && this.collectionsAreEqual(var2.excludedSubtreesOtherName, this.excludedSubtreesOtherName)
            && this.collectionsAreEqual(var2.permittedSubtreesDN, this.permittedSubtreesDN)
            && this.collectionsAreEqual(var2.permittedSubtreesDNS, this.permittedSubtreesDNS)
            && this.collectionsAreEqual(var2.permittedSubtreesEmail, this.permittedSubtreesEmail)
            && this.collectionsAreEqual(var2.permittedSubtreesIP, this.permittedSubtreesIP)
            && this.collectionsAreEqual(var2.permittedSubtreesURI, this.permittedSubtreesURI)
            && this.collectionsAreEqual(var2.permittedSubtreesOtherName, this.permittedSubtreesOtherName);
      }
   }

   public void checkPermittedDN(X500Name var1) throws NameConstraintValidatorException {
      this.checkPermittedDN(this.permittedSubtreesDN, ASN1Sequence.getInstance(var1.toASN1Primitive()));
   }

   public void checkExcludedDN(X500Name var1) throws NameConstraintValidatorException {
      this.checkExcludedDN(this.excludedSubtreesDN, ASN1Sequence.getInstance(var1));
   }

   private static boolean withinDNSubtree(ASN1Sequence var0, ASN1Sequence var1) {
      if (var1.size() < 1) {
         return false;
      } else if (var1.size() > var0.size()) {
         return false;
      } else {
         int var2 = 0;
         RDN var3 = RDN.getInstance(var1.getObjectAt(0));

         for (int var4 = 0; var4 < var0.size(); var4++) {
            var2 = var4;
            RDN var5 = RDN.getInstance(var0.getObjectAt(var4));
            if (IETFUtils.rDNAreEqual(var3, var5)) {
               break;
            }
         }

         if (var1.size() > var0.size() - var2) {
            return false;
         } else {
            for (int var7 = 0; var7 < var1.size(); var7++) {
               RDN var8 = RDN.getInstance(var1.getObjectAt(var7));
               RDN var6 = RDN.getInstance(var0.getObjectAt(var2 + var7));
               if (var8.size() != var6.size()) {
                  return false;
               }

               if (!var8.getFirst().getType().equals(var6.getFirst().getType())) {
                  return false;
               }

               if (var8.size() == 1 && var8.getFirst().getType().equals(RFC4519Style.serialNumber)) {
                  if (!var6.getFirst().getValue().toString().startsWith(var8.getFirst().getValue().toString())) {
                     return false;
                  }
               } else if (!IETFUtils.rDNAreEqual(var8, var6)) {
                  return false;
               }
            }

            return true;
         }
      }
   }

   private void checkPermittedDN(Set var1, ASN1Sequence var2) throws NameConstraintValidatorException {
      if (var1 != null) {
         if (!var1.isEmpty() || var2.size() != 0) {
            for (ASN1Sequence var4 : var1) {
               if (withinDNSubtree(var2, var4)) {
                  return;
               }
            }

            throw new NameConstraintValidatorException("Subject distinguished name is not from a permitted subtree");
         }
      }
   }

   private void checkExcludedDN(Set var1, ASN1Sequence var2) throws NameConstraintValidatorException {
      if (!var1.isEmpty()) {
         for (ASN1Sequence var4 : var1) {
            if (withinDNSubtree(var2, var4)) {
               throw new NameConstraintValidatorException("Subject distinguished name is from an excluded subtree");
            }
         }
      }
   }

   private Set intersectDN(Set var1, Set var2) {
      HashSet var3 = new HashSet();
      Iterator var4 = var2.iterator();

      while (var4.hasNext()) {
         ASN1Sequence var5 = ASN1Sequence.getInstance(((GeneralSubtree)var4.next()).getBase().getName().toASN1Primitive());
         if (var1 != null) {
            for (ASN1Sequence var7 : var1) {
               if (withinDNSubtree(var5, var7)) {
                  var3.add(var5);
               } else if (withinDNSubtree(var7, var5)) {
                  var3.add(var7);
               }
            }
         } else if (var5 != null) {
            var3.add(var5);
         }
      }

      return var3;
   }

   private Set unionDN(Set var1, ASN1Sequence var2) {
      if (var1.isEmpty()) {
         if (var2 == null) {
            return var1;
         } else {
            var1.add(var2);
            return var1;
         }
      } else {
         HashSet var3 = new HashSet();
         Iterator var4 = var1.iterator();

         while (var4.hasNext()) {
            ASN1Sequence var5 = ASN1Sequence.getInstance(var4.next());
            if (withinDNSubtree(var2, var5)) {
               var3.add(var5);
            } else if (withinDNSubtree(var5, var2)) {
               var3.add(var2);
            } else {
               var3.add(var5);
               var3.add(var2);
            }
         }

         return var3;
      }
   }

   private Set intersectOtherName(Set var1, Set var2) {
      HashSet var3 = new HashSet();
      Iterator var4 = var2.iterator();

      while (var4.hasNext()) {
         OtherName var5 = OtherName.getInstance(((GeneralSubtree)var4.next()).getBase().getName());
         if (var1 != null) {
            Iterator var6 = var1.iterator();

            while (var6.hasNext()) {
               OtherName var7 = OtherName.getInstance(var6.next());
               this.intersectOtherName(var5, var7, var3);
            }
         } else if (var5 != null) {
            var3.add(var5);
         }
      }

      return var3;
   }

   private void intersectOtherName(OtherName var1, OtherName var2, Set var3) {
      if (var1.equals(var2)) {
         var3.add(var1);
      }
   }

   private Set unionOtherName(Set var1, OtherName var2) {
      HashSet var3 = var1 != null ? new HashSet(var1) : new HashSet();
      var3.add(var2);
      return var3;
   }

   private Set intersectEmail(Set var1, Set var2) {
      HashSet var3 = new HashSet();
      Iterator var4 = var2.iterator();

      while (var4.hasNext()) {
         String var5 = this.extractNameAsString(((GeneralSubtree)var4.next()).getBase());
         if (var1 != null) {
            for (String var7 : var1) {
               this.intersectEmail(var5, var7, var3);
            }
         } else if (var5 != null) {
            var3.add(var5);
         }
      }

      return var3;
   }

   private Set unionEmail(Set var1, String var2) {
      if (var1.isEmpty()) {
         if (var2 == null) {
            return var1;
         } else {
            var1.add(var2);
            return var1;
         }
      } else {
         HashSet var3 = new HashSet();

         for (String var5 : var1) {
            this.unionEmail(var5, var2, var3);
         }

         return var3;
      }
   }

   private Set intersectIP(Set var1, Set var2) {
      HashSet var3 = new HashSet();
      Iterator var4 = var2.iterator();

      while (var4.hasNext()) {
         byte[] var5 = ASN1OctetString.getInstance(((GeneralSubtree)var4.next()).getBase().getName()).getOctets();
         if (var1 != null) {
            for (byte[] var7 : var1) {
               var3.addAll(this.intersectIPRange(var7, var5));
            }
         } else if (var5 != null) {
            var3.add(var5);
         }
      }

      return var3;
   }

   private Set unionIP(Set var1, byte[] var2) {
      if (var1.isEmpty()) {
         if (var2 == null) {
            return var1;
         } else {
            var1.add(var2);
            return var1;
         }
      } else {
         HashSet var3 = new HashSet();

         for (byte[] var5 : var1) {
            var3.addAll(this.unionIPRange(var5, var2));
         }

         return var3;
      }
   }

   private Set unionIPRange(byte[] var1, byte[] var2) {
      HashSet var3 = new HashSet();
      if (Arrays.areEqual(var1, var2)) {
         var3.add(var1);
      } else {
         var3.add(var1);
         var3.add(var2);
      }

      return var3;
   }

   private Set intersectIPRange(byte[] var1, byte[] var2) {
      if (var1.length != var2.length) {
         return Collections.EMPTY_SET;
      } else {
         byte[][] var3 = this.extractIPsAndSubnetMasks(var1, var2);
         byte[] var4 = var3[0];
         byte[] var5 = var3[1];
         byte[] var6 = var3[2];
         byte[] var7 = var3[3];
         byte[][] var8 = this.minMaxIPs(var4, var5, var6, var7);
         byte[] var10 = min(var8[1], var8[3]);
         byte[] var9 = max(var8[0], var8[2]);
         if (compareTo(var9, var10) == 1) {
            return Collections.EMPTY_SET;
         } else {
            byte[] var11 = or(var8[0], var8[2]);
            byte[] var12 = or(var5, var7);
            return Collections.singleton(this.ipWithSubnetMask(var11, var12));
         }
      }
   }

   private byte[] ipWithSubnetMask(byte[] var1, byte[] var2) {
      int var3 = var1.length;
      byte[] var4 = new byte[var3 * 2];
      System.arraycopy(var1, 0, var4, 0, var3);
      System.arraycopy(var2, 0, var4, var3, var3);
      return var4;
   }

   private byte[][] extractIPsAndSubnetMasks(byte[] var1, byte[] var2) {
      int var3 = var1.length / 2;
      byte[] var4 = new byte[var3];
      byte[] var5 = new byte[var3];
      System.arraycopy(var1, 0, var4, 0, var3);
      System.arraycopy(var1, var3, var5, 0, var3);
      byte[] var6 = new byte[var3];
      byte[] var7 = new byte[var3];
      System.arraycopy(var2, 0, var6, 0, var3);
      System.arraycopy(var2, var3, var7, 0, var3);
      return new byte[][]{var4, var5, var6, var7};
   }

   private byte[][] minMaxIPs(byte[] var1, byte[] var2, byte[] var3, byte[] var4) {
      int var5 = var1.length;
      byte[] var6 = new byte[var5];
      byte[] var7 = new byte[var5];
      byte[] var8 = new byte[var5];
      byte[] var9 = new byte[var5];

      for (int var10 = 0; var10 < var5; var10++) {
         var6[var10] = (byte)(var1[var10] & var2[var10]);
         var7[var10] = (byte)(var1[var10] & var2[var10] | ~var2[var10]);
         var8[var10] = (byte)(var3[var10] & var4[var10]);
         var9[var10] = (byte)(var3[var10] & var4[var10] | ~var4[var10]);
      }

      return new byte[][]{var6, var7, var8, var9};
   }

   private void checkPermittedEmail(Set var1, String var2) throws NameConstraintValidatorException {
      if (var1 != null) {
         for (String var4 : var1) {
            if (this.emailIsConstrained(var2, var4)) {
               return;
            }
         }

         if (var2.length() != 0 || var1.size() != 0) {
            throw new NameConstraintValidatorException("Subject email address is not from a permitted subtree.");
         }
      }
   }

   private void checkPermittedOtherName(Set var1, OtherName var2) throws NameConstraintValidatorException {
      if (var1 != null) {
         Iterator var3 = var1.iterator();

         while (var3.hasNext()) {
            OtherName var4 = OtherName.getInstance(var3.next());
            if (this.otherNameIsConstrained(var2, var4)) {
               return;
            }
         }

         throw new NameConstraintValidatorException("Subject OtherName is not from a permitted subtree.");
      }
   }

   private void checkExcludedOtherName(Set var1, OtherName var2) throws NameConstraintValidatorException {
      if (!var1.isEmpty()) {
         Iterator var3 = var1.iterator();

         while (var3.hasNext()) {
            OtherName var4 = OtherName.getInstance(var3.next());
            if (this.otherNameIsConstrained(var2, var4)) {
               throw new NameConstraintValidatorException("OtherName is from an excluded subtree.");
            }
         }
      }
   }

   private void checkExcludedEmail(Set var1, String var2) throws NameConstraintValidatorException {
      if (!var1.isEmpty()) {
         for (String var4 : var1) {
            if (this.emailIsConstrained(var2, var4)) {
               throw new NameConstraintValidatorException("Email address is from an excluded subtree.");
            }
         }
      }
   }

   private void checkPermittedIP(Set var1, byte[] var2) throws NameConstraintValidatorException {
      if (var1 != null) {
         for (byte[] var4 : var1) {
            if (this.isIPConstrained(var2, var4)) {
               return;
            }
         }

         if (var2.length != 0 || var1.size() != 0) {
            throw new NameConstraintValidatorException("IP is not from a permitted subtree.");
         }
      }
   }

   private void checkExcludedIP(Set var1, byte[] var2) throws NameConstraintValidatorException {
      if (!var1.isEmpty()) {
         for (byte[] var4 : var1) {
            if (this.isIPConstrained(var2, var4)) {
               throw new NameConstraintValidatorException("IP is from an excluded subtree.");
            }
         }
      }
   }

   private boolean isIPConstrained(byte[] var1, byte[] var2) {
      int var3 = var1.length;
      if (var3 != var2.length / 2) {
         return false;
      } else {
         byte[] var4 = new byte[var3];
         System.arraycopy(var2, var3, var4, 0, var3);
         byte[] var5 = new byte[var3];
         byte[] var6 = new byte[var3];

         for (int var7 = 0; var7 < var3; var7++) {
            var5[var7] = (byte)(var2[var7] & var4[var7]);
            var6[var7] = (byte)(var1[var7] & var4[var7]);
         }

         return Arrays.areEqual(var5, var6);
      }
   }

   private boolean otherNameIsConstrained(OtherName var1, OtherName var2) {
      return var2.equals(var1);
   }

   private boolean emailIsConstrained(String var1, String var2) {
      String var3 = var1.substring(var1.indexOf(64) + 1);
      if (var2.indexOf(64) != -1) {
         if (var1.equalsIgnoreCase(var2)) {
            return true;
         }

         if (var3.equalsIgnoreCase(var2.substring(1))) {
            return true;
         }
      } else if (var2.charAt(0) != '.') {
         if (var3.equalsIgnoreCase(var2)) {
            return true;
         }
      } else if (this.withinDomain(var3, var2)) {
         return true;
      }

      return false;
   }

   private boolean withinDomain(String var1, String var2) {
      String var3 = var2;
      if (var2.startsWith(".")) {
         var3 = var2.substring(1);
      }

      String[] var4 = Strings.split(var3, '.');
      String[] var5 = Strings.split(var1, '.');
      if (var5.length <= var4.length) {
         return false;
      } else {
         int var6 = var5.length - var4.length;

         for (int var7 = -1; var7 < var4.length; var7++) {
            if (var7 == -1) {
               if (var5[var7 + var6].equals("")) {
                  return false;
               }
            } else if (!var4[var7].equalsIgnoreCase(var5[var7 + var6])) {
               return false;
            }
         }

         return true;
      }
   }

   private void checkPermittedDNS(Set var1, String var2) throws NameConstraintValidatorException {
      if (var1 != null) {
         for (String var4 : var1) {
            if (this.withinDomain(var2, var4) || var2.equalsIgnoreCase(var4)) {
               return;
            }
         }

         if (var2.length() != 0 || var1.size() != 0) {
            throw new NameConstraintValidatorException("DNS is not from a permitted subtree.");
         }
      }
   }

   private void checkExcludedDNS(Set var1, String var2) throws NameConstraintValidatorException {
      if (!var1.isEmpty()) {
         for (String var4 : var1) {
            if (this.withinDomain(var2, var4) || var2.equalsIgnoreCase(var4)) {
               throw new NameConstraintValidatorException("DNS is from an excluded subtree.");
            }
         }
      }
   }

   private void unionEmail(String var1, String var2, Set var3) {
      if (var1.indexOf(64) != -1) {
         String var4 = var1.substring(var1.indexOf(64) + 1);
         if (var2.indexOf(64) != -1) {
            if (var1.equalsIgnoreCase(var2)) {
               var3.add(var1);
            } else {
               var3.add(var1);
               var3.add(var2);
            }
         } else if (var2.startsWith(".")) {
            if (this.withinDomain(var4, var2)) {
               var3.add(var2);
            } else {
               var3.add(var1);
               var3.add(var2);
            }
         } else if (var4.equalsIgnoreCase(var2)) {
            var3.add(var2);
         } else {
            var3.add(var1);
            var3.add(var2);
         }
      } else if (var1.startsWith(".")) {
         if (var2.indexOf(64) != -1) {
            String var5 = var2.substring(var1.indexOf(64) + 1);
            if (this.withinDomain(var5, var1)) {
               var3.add(var1);
            } else {
               var3.add(var1);
               var3.add(var2);
            }
         } else if (var2.startsWith(".")) {
            if (this.withinDomain(var1, var2) || var1.equalsIgnoreCase(var2)) {
               var3.add(var2);
            } else if (this.withinDomain(var2, var1)) {
               var3.add(var1);
            } else {
               var3.add(var1);
               var3.add(var2);
            }
         } else if (this.withinDomain(var2, var1)) {
            var3.add(var1);
         } else {
            var3.add(var1);
            var3.add(var2);
         }
      } else if (var2.indexOf(64) != -1) {
         String var6 = var2.substring(var1.indexOf(64) + 1);
         if (var6.equalsIgnoreCase(var1)) {
            var3.add(var1);
         } else {
            var3.add(var1);
            var3.add(var2);
         }
      } else if (var2.startsWith(".")) {
         if (this.withinDomain(var1, var2)) {
            var3.add(var2);
         } else {
            var3.add(var1);
            var3.add(var2);
         }
      } else if (var1.equalsIgnoreCase(var2)) {
         var3.add(var1);
      } else {
         var3.add(var1);
         var3.add(var2);
      }
   }

   private void unionURI(String var1, String var2, Set var3) {
      if (var1.indexOf(64) != -1) {
         String var4 = var1.substring(var1.indexOf(64) + 1);
         if (var2.indexOf(64) != -1) {
            if (var1.equalsIgnoreCase(var2)) {
               var3.add(var1);
            } else {
               var3.add(var1);
               var3.add(var2);
            }
         } else if (var2.startsWith(".")) {
            if (this.withinDomain(var4, var2)) {
               var3.add(var2);
            } else {
               var3.add(var1);
               var3.add(var2);
            }
         } else if (var4.equalsIgnoreCase(var2)) {
            var3.add(var2);
         } else {
            var3.add(var1);
            var3.add(var2);
         }
      } else if (var1.startsWith(".")) {
         if (var2.indexOf(64) != -1) {
            String var5 = var2.substring(var1.indexOf(64) + 1);
            if (this.withinDomain(var5, var1)) {
               var3.add(var1);
            } else {
               var3.add(var1);
               var3.add(var2);
            }
         } else if (var2.startsWith(".")) {
            if (this.withinDomain(var1, var2) || var1.equalsIgnoreCase(var2)) {
               var3.add(var2);
            } else if (this.withinDomain(var2, var1)) {
               var3.add(var1);
            } else {
               var3.add(var1);
               var3.add(var2);
            }
         } else if (this.withinDomain(var2, var1)) {
            var3.add(var1);
         } else {
            var3.add(var1);
            var3.add(var2);
         }
      } else if (var2.indexOf(64) != -1) {
         String var6 = var2.substring(var1.indexOf(64) + 1);
         if (var6.equalsIgnoreCase(var1)) {
            var3.add(var1);
         } else {
            var3.add(var1);
            var3.add(var2);
         }
      } else if (var2.startsWith(".")) {
         if (this.withinDomain(var1, var2)) {
            var3.add(var2);
         } else {
            var3.add(var1);
            var3.add(var2);
         }
      } else if (var1.equalsIgnoreCase(var2)) {
         var3.add(var1);
      } else {
         var3.add(var1);
         var3.add(var2);
      }
   }

   private Set intersectDNS(Set var1, Set var2) {
      HashSet var3 = new HashSet();
      Iterator var4 = var2.iterator();

      while (var4.hasNext()) {
         String var5 = this.extractNameAsString(((GeneralSubtree)var4.next()).getBase());
         if (var1 != null) {
            for (String var7 : var1) {
               if (this.withinDomain(var7, var5)) {
                  var3.add(var7);
               } else if (this.withinDomain(var5, var7)) {
                  var3.add(var5);
               }
            }
         } else if (var5 != null) {
            var3.add(var5);
         }
      }

      return var3;
   }

   private Set unionDNS(Set var1, String var2) {
      if (var1.isEmpty()) {
         if (var2 == null) {
            return var1;
         } else {
            var1.add(var2);
            return var1;
         }
      } else {
         HashSet var3 = new HashSet();

         for (String var5 : var1) {
            if (this.withinDomain(var5, var2)) {
               var3.add(var2);
            } else if (this.withinDomain(var2, var5)) {
               var3.add(var5);
            } else {
               var3.add(var5);
               var3.add(var2);
            }
         }

         return var3;
      }
   }

   private void intersectEmail(String var1, String var2, Set var3) {
      if (var1.indexOf(64) != -1) {
         String var4 = var1.substring(var1.indexOf(64) + 1);
         if (var2.indexOf(64) != -1) {
            if (var1.equalsIgnoreCase(var2)) {
               var3.add(var1);
            }
         } else if (var2.startsWith(".")) {
            if (this.withinDomain(var4, var2)) {
               var3.add(var1);
            }
         } else if (var4.equalsIgnoreCase(var2)) {
            var3.add(var1);
         }
      } else if (var1.startsWith(".")) {
         if (var2.indexOf(64) != -1) {
            String var5 = var2.substring(var1.indexOf(64) + 1);
            if (this.withinDomain(var5, var1)) {
               var3.add(var2);
            }
         } else if (var2.startsWith(".")) {
            if (this.withinDomain(var1, var2) || var1.equalsIgnoreCase(var2)) {
               var3.add(var1);
            } else if (this.withinDomain(var2, var1)) {
               var3.add(var2);
            }
         } else if (this.withinDomain(var2, var1)) {
            var3.add(var2);
         }
      } else if (var2.indexOf(64) != -1) {
         String var6 = var2.substring(var2.indexOf(64) + 1);
         if (var6.equalsIgnoreCase(var1)) {
            var3.add(var2);
         }
      } else if (var2.startsWith(".")) {
         if (this.withinDomain(var1, var2)) {
            var3.add(var1);
         }
      } else if (var1.equalsIgnoreCase(var2)) {
         var3.add(var1);
      }
   }

   private void checkExcludedURI(Set var1, String var2) throws NameConstraintValidatorException {
      if (!var1.isEmpty()) {
         for (String var4 : var1) {
            if (this.isUriConstrained(var2, var4)) {
               throw new NameConstraintValidatorException("URI is from an excluded subtree.");
            }
         }
      }
   }

   private Set intersectURI(Set var1, Set var2) {
      HashSet var3 = new HashSet();
      Iterator var4 = var2.iterator();

      while (var4.hasNext()) {
         String var5 = this.extractNameAsString(((GeneralSubtree)var4.next()).getBase());
         if (var1 != null) {
            for (String var7 : var1) {
               this.intersectURI(var7, var5, var3);
            }
         } else if (var5 != null) {
            var3.add(var5);
         }
      }

      return var3;
   }

   private Set unionURI(Set var1, String var2) {
      if (var1.isEmpty()) {
         if (var2 == null) {
            return var1;
         } else {
            var1.add(var2);
            return var1;
         }
      } else {
         HashSet var3 = new HashSet();

         for (String var5 : var1) {
            this.unionURI(var5, var2, var3);
         }

         return var3;
      }
   }

   private void intersectURI(String var1, String var2, Set var3) {
      if (var1.indexOf(64) != -1) {
         String var4 = var1.substring(var1.indexOf(64) + 1);
         if (var2.indexOf(64) != -1) {
            if (var1.equalsIgnoreCase(var2)) {
               var3.add(var1);
            }
         } else if (var2.startsWith(".")) {
            if (this.withinDomain(var4, var2)) {
               var3.add(var1);
            }
         } else if (var4.equalsIgnoreCase(var2)) {
            var3.add(var1);
         }
      } else if (var1.startsWith(".")) {
         if (var2.indexOf(64) != -1) {
            String var5 = var2.substring(var1.indexOf(64) + 1);
            if (this.withinDomain(var5, var1)) {
               var3.add(var2);
            }
         } else if (var2.startsWith(".")) {
            if (this.withinDomain(var1, var2) || var1.equalsIgnoreCase(var2)) {
               var3.add(var1);
            } else if (this.withinDomain(var2, var1)) {
               var3.add(var2);
            }
         } else if (this.withinDomain(var2, var1)) {
            var3.add(var2);
         }
      } else if (var2.indexOf(64) != -1) {
         String var6 = var2.substring(var2.indexOf(64) + 1);
         if (var6.equalsIgnoreCase(var1)) {
            var3.add(var2);
         }
      } else if (var2.startsWith(".")) {
         if (this.withinDomain(var1, var2)) {
            var3.add(var1);
         }
      } else if (var1.equalsIgnoreCase(var2)) {
         var3.add(var1);
      }
   }

   private void checkPermittedURI(Set var1, String var2) throws NameConstraintValidatorException {
      if (var1 != null) {
         for (String var4 : var1) {
            if (this.isUriConstrained(var2, var4)) {
               return;
            }
         }

         if (var2.length() != 0 || var1.size() != 0) {
            throw new NameConstraintValidatorException("URI is not from a permitted subtree.");
         }
      }
   }

   private boolean isUriConstrained(String var1, String var2) {
      String var3 = extractHostFromURL(var1);
      if (!var2.startsWith(".")) {
         if (var3.equalsIgnoreCase(var2)) {
            return true;
         }
      } else if (this.withinDomain(var3, var2)) {
         return true;
      }

      return false;
   }

   private static String extractHostFromURL(String var0) {
      String var1 = var0.substring(var0.indexOf(58) + 1);
      if (var1.indexOf("//") != -1) {
         var1 = var1.substring(var1.indexOf("//") + 2);
      }

      if (var1.lastIndexOf(58) != -1) {
         var1 = var1.substring(0, var1.lastIndexOf(58));
      }

      var1 = var1.substring(var1.indexOf(58) + 1);
      var1 = var1.substring(var1.indexOf(64) + 1);
      if (var1.indexOf(47) != -1) {
         var1 = var1.substring(0, var1.indexOf(47));
      }

      return var1;
   }

   private String extractNameAsString(GeneralName var1) {
      return ASN1IA5String.getInstance(var1.getName()).getString();
   }

   private static byte[] max(byte[] var0, byte[] var1) {
      for (int var2 = 0; var2 < var0.length; var2++) {
         if ((var0[var2] & '\uffff') > (var1[var2] & '\uffff')) {
            return var0;
         }
      }

      return var1;
   }

   private static byte[] min(byte[] var0, byte[] var1) {
      for (int var2 = 0; var2 < var0.length; var2++) {
         if ((var0[var2] & '\uffff') < (var1[var2] & '\uffff')) {
            return var0;
         }
      }

      return var1;
   }

   private static int compareTo(byte[] var0, byte[] var1) {
      if (Arrays.areEqual(var0, var1)) {
         return 0;
      } else {
         return Arrays.areEqual(max(var0, var1), var0) ? 1 : -1;
      }
   }

   private static byte[] or(byte[] var0, byte[] var1) {
      byte[] var2 = new byte[var0.length];

      for (int var3 = 0; var3 < var0.length; var3++) {
         var2[var3] = (byte)(var0[var3] | var1[var3]);
      }

      return var2;
   }

   private int hashCollection(Collection var1) {
      if (var1 == null) {
         return 0;
      } else {
         int var2 = 0;

         for (Object var4 : var1) {
            if (var4 instanceof byte[]) {
               var2 += Arrays.hashCode((byte[])var4);
            } else {
               var2 += var4.hashCode();
            }
         }

         return var2;
      }
   }

   private boolean collectionsAreEqual(Collection var1, Collection var2) {
      if (var1 == var2) {
         return true;
      } else if (var1 != null && var2 != null) {
         if (var1.size() != var2.size()) {
            return false;
         } else {
            for (Object var4 : var1) {
               Iterator var5 = var2.iterator();
               boolean var6 = false;

               while (var5.hasNext()) {
                  Object var7 = var5.next();
                  if (this.equals(var4, var7)) {
                     var6 = true;
                     break;
                  }
               }

               if (!var6) {
                  return false;
               }
            }

            return true;
         }
      } else {
         return false;
      }
   }

   private boolean equals(Object var1, Object var2) {
      if (var1 == var2) {
         return true;
      } else if (var1 == null || var2 == null) {
         return false;
      } else {
         return var1 instanceof byte[] && var2 instanceof byte[] ? Arrays.areEqual((byte[])var1, (byte[])var2) : var1.equals(var2);
      }
   }

   private String stringifyIP(byte[] var1) {
      StringBuilder var2 = new StringBuilder();

      for (int var3 = 0; var3 < var1.length / 2; var3++) {
         if (var2.length() > 0) {
            var2.append(".");
         }

         var2.append(Integer.toString(var1[var3] & 255));
      }

      var2.append("/");
      boolean var5 = true;

      for (int var4 = var1.length / 2; var4 < var1.length; var4++) {
         if (var5) {
            var5 = false;
         } else {
            var2.append(".");
         }

         var2.append(Integer.toString(var1[var4] & 255));
      }

      return var2.toString();
   }

   private String stringifyIPCollection(Set var1) {
      StringBuilder var2 = new StringBuilder();
      var2.append("[");
      Iterator var3 = var1.iterator();

      while (var3.hasNext()) {
         if (var2.length() > 1) {
            var2.append(",");
         }

         var2.append(this.stringifyIP((byte[])var3.next()));
      }

      var2.append("]");
      return var2.toString();
   }

   private String stringifyOtherNameCollection(Set var1) {
      StringBuilder var2 = new StringBuilder();
      var2.append("[");
      Iterator var3 = var1.iterator();

      while (var3.hasNext()) {
         if (var2.length() > 1) {
            var2.append(",");
         }

         OtherName var4 = OtherName.getInstance(var3.next());
         var2.append(var4.getTypeID().getId());
         var2.append(":");

         try {
            var2.append(Hex.toHexString(var4.getValue().toASN1Primitive().getEncoded()));
         } catch (IOException var6) {
            var2.append(var6.toString());
         }
      }

      var2.append("]");
      return var2.toString();
   }

   private final void addLine(StringBuilder var1, String var2) {
      var1.append(var2).append(Strings.lineSeparator());
   }

   @Override
   public String toString() {
      StringBuilder var1 = new StringBuilder();
      this.addLine(var1, "permitted:");
      if (this.permittedSubtreesDN != null) {
         this.addLine(var1, "DN:");
         this.addLine(var1, this.permittedSubtreesDN.toString());
      }

      if (this.permittedSubtreesDNS != null) {
         this.addLine(var1, "DNS:");
         this.addLine(var1, this.permittedSubtreesDNS.toString());
      }

      if (this.permittedSubtreesEmail != null) {
         this.addLine(var1, "Email:");
         this.addLine(var1, this.permittedSubtreesEmail.toString());
      }

      if (this.permittedSubtreesURI != null) {
         this.addLine(var1, "URI:");
         this.addLine(var1, this.permittedSubtreesURI.toString());
      }

      if (this.permittedSubtreesIP != null) {
         this.addLine(var1, "IP:");
         this.addLine(var1, this.stringifyIPCollection(this.permittedSubtreesIP));
      }

      if (this.permittedSubtreesOtherName != null) {
         this.addLine(var1, "OtherName:");
         this.addLine(var1, this.stringifyOtherNameCollection(this.permittedSubtreesOtherName));
      }

      this.addLine(var1, "excluded:");
      if (!this.excludedSubtreesDN.isEmpty()) {
         this.addLine(var1, "DN:");
         this.addLine(var1, this.excludedSubtreesDN.toString());
      }

      if (!this.excludedSubtreesDNS.isEmpty()) {
         this.addLine(var1, "DNS:");
         this.addLine(var1, this.excludedSubtreesDNS.toString());
      }

      if (!this.excludedSubtreesEmail.isEmpty()) {
         this.addLine(var1, "Email:");
         this.addLine(var1, this.excludedSubtreesEmail.toString());
      }

      if (!this.excludedSubtreesURI.isEmpty()) {
         this.addLine(var1, "URI:");
         this.addLine(var1, this.excludedSubtreesURI.toString());
      }

      if (!this.excludedSubtreesIP.isEmpty()) {
         this.addLine(var1, "IP:");
         this.addLine(var1, this.stringifyIPCollection(this.excludedSubtreesIP));
      }

      if (!this.excludedSubtreesOtherName.isEmpty()) {
         this.addLine(var1, "OtherName:");
         this.addLine(var1, this.stringifyOtherNameCollection(this.excludedSubtreesOtherName));
      }

      return var1.toString();
   }
}
