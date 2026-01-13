package org.bouncycastle.asn1.x509;

import java.io.IOException;
import java.util.StringTokenizer;
import org.bouncycastle.asn1.ASN1Choice;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1IA5String;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.DERIA5String;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.asn1.DERTaggedObject;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.util.IPAddress;

public class GeneralName extends ASN1Object implements ASN1Choice {
   public static final int otherName = 0;
   public static final int rfc822Name = 1;
   public static final int dNSName = 2;
   public static final int x400Address = 3;
   public static final int directoryName = 4;
   public static final int ediPartyName = 5;
   public static final int uniformResourceIdentifier = 6;
   public static final int iPAddress = 7;
   public static final int registeredID = 8;
   private ASN1Encodable obj;
   private int tag;

   /** @deprecated */
   public GeneralName(X509Name var1) {
      this.obj = X500Name.getInstance(var1);
      this.tag = 4;
   }

   public GeneralName(X500Name var1) {
      this.obj = var1;
      this.tag = 4;
   }

   public GeneralName(int var1, ASN1Encodable var2) {
      this.obj = var2;
      this.tag = var1;
   }

   public GeneralName(int var1, String var2) {
      this.tag = var1;
      if (var1 == 1 || var1 == 2 || var1 == 6) {
         this.obj = new DERIA5String(var2);
      } else if (var1 == 8) {
         this.obj = new ASN1ObjectIdentifier(var2);
      } else if (var1 == 4) {
         this.obj = new X500Name(var2);
      } else {
         if (var1 != 7) {
            throw new IllegalArgumentException("can't process String for tag: " + var1);
         }

         byte[] var3 = this.toGeneralNameEncoding(var2);
         if (var3 == null) {
            throw new IllegalArgumentException("IP Address is invalid");
         }

         this.obj = new DEROctetString(var3);
      }
   }

   public static GeneralName getInstance(Object var0) {
      if (var0 == null || var0 instanceof GeneralName) {
         return (GeneralName)var0;
      } else if (var0 instanceof ASN1TaggedObject) {
         ASN1TaggedObject var1 = (ASN1TaggedObject)var0;
         int var2 = var1.getTagNo();
         switch (var2) {
            case 0:
            case 3:
            case 5:
               return new GeneralName(var2, ASN1Sequence.getInstance(var1, false));
            case 1:
            case 2:
            case 6:
               return new GeneralName(var2, ASN1IA5String.getInstance(var1, false));
            case 4:
               return new GeneralName(var2, X500Name.getInstance(var1, true));
            case 7:
               return new GeneralName(var2, ASN1OctetString.getInstance(var1, false));
            case 8:
               return new GeneralName(var2, ASN1ObjectIdentifier.getInstance(var1, false));
            default:
               throw new IllegalArgumentException("unknown tag: " + var2);
         }
      } else if (var0 instanceof byte[]) {
         try {
            return getInstance(ASN1Primitive.fromByteArray((byte[])var0));
         } catch (IOException var3) {
            throw new IllegalArgumentException("unable to parse encoded general name");
         }
      } else {
         throw new IllegalArgumentException("unknown object in getInstance: " + var0.getClass().getName());
      }
   }

   public static GeneralName getInstance(ASN1TaggedObject var0, boolean var1) {
      if (!var1) {
         throw new IllegalArgumentException("choice item must be explicitly tagged");
      } else {
         return getInstance(ASN1TaggedObject.getInstance(var0, true));
      }
   }

   public int getTagNo() {
      return this.tag;
   }

   public ASN1Encodable getName() {
      return this.obj;
   }

   @Override
   public String toString() {
      StringBuilder var1 = new StringBuilder();
      var1.append(this.tag);
      var1.append(": ");
      switch (this.tag) {
         case 1:
         case 2:
         case 6:
            var1.append(ASN1IA5String.getInstance(this.obj).getString());
            break;
         case 3:
         case 5:
         default:
            var1.append(this.obj.toString());
            break;
         case 4:
            var1.append(X500Name.getInstance(this.obj).toString());
      }

      return var1.toString();
   }

   private byte[] toGeneralNameEncoding(String var1) {
      if (IPAddress.isValidIPv6WithNetmask(var1) || IPAddress.isValidIPv6(var1)) {
         int var6 = var1.indexOf(47);
         if (var6 < 0) {
            byte[] var9 = new byte[16];
            int[] var12 = this.parseIPv6(var1);
            this.copyInts(var12, var9, 0);
            return var9;
         } else {
            byte[] var8 = new byte[32];
            int[] var10 = this.parseIPv6(var1.substring(0, var6));
            this.copyInts(var10, var8, 0);
            String var5 = var1.substring(var6 + 1);
            if (var5.indexOf(58) > 0) {
               var10 = this.parseIPv6(var5);
            } else {
               var10 = this.parseMask(var5);
            }

            this.copyInts(var10, var8, 16);
            return var8;
         }
      } else if (!IPAddress.isValidIPv4WithNetmask(var1) && !IPAddress.isValidIPv4(var1)) {
         return null;
      } else {
         int var2 = var1.indexOf(47);
         if (var2 < 0) {
            byte[] var7 = new byte[4];
            this.parseIPv4(var1, var7, 0);
            return var7;
         } else {
            byte[] var3 = new byte[8];
            this.parseIPv4(var1.substring(0, var2), var3, 0);
            String var4 = var1.substring(var2 + 1);
            if (var4.indexOf(46) > 0) {
               this.parseIPv4(var4, var3, 4);
            } else {
               this.parseIPv4Mask(var4, var3, 4);
            }

            return var3;
         }
      }
   }

   private void parseIPv4Mask(String var1, byte[] var2, int var3) {
      int var4 = Integer.parseInt(var1);

      for (int var5 = 0; var5 != var4; var5++) {
         var2[var5 / 8 + var3] = (byte)(var2[var5 / 8 + var3] | 1 << 7 - var5 % 8);
      }
   }

   private void parseIPv4(String var1, byte[] var2, int var3) {
      StringTokenizer var4 = new StringTokenizer(var1, "./");
      int var5 = 0;

      while (var4.hasMoreTokens()) {
         var2[var3 + var5++] = (byte)Integer.parseInt(var4.nextToken());
      }
   }

   private int[] parseMask(String var1) {
      int[] var2 = new int[8];
      int var3 = Integer.parseInt(var1);

      for (int var4 = 0; var4 != var3; var4++) {
         var2[var4 / 16] = var2[var4 / 16] | 1 << 15 - var4 % 16;
      }

      return var2;
   }

   private void copyInts(int[] var1, byte[] var2, int var3) {
      for (int var4 = 0; var4 != var1.length; var4++) {
         var2[var4 * 2 + var3] = (byte)(var1[var4] >> 8);
         var2[var4 * 2 + 1 + var3] = (byte)var1[var4];
      }
   }

   private int[] parseIPv6(String var1) {
      StringTokenizer var2 = new StringTokenizer(var1, ":", true);
      int var3 = 0;
      int[] var4 = new int[8];
      if (var1.charAt(0) == ':' && var1.charAt(1) == ':') {
         var2.nextToken();
      }

      int var5 = -1;

      while (var2.hasMoreTokens()) {
         String var6 = var2.nextToken();
         if (var6.equals(":")) {
            var5 = var3;
            var4[var3++] = 0;
         } else if (var6.indexOf(46) < 0) {
            var4[var3++] = Integer.parseInt(var6, 16);
            if (var2.hasMoreTokens()) {
               var2.nextToken();
            }
         } else {
            StringTokenizer var7 = new StringTokenizer(var6, ".");
            var4[var3++] = Integer.parseInt(var7.nextToken()) << 8 | Integer.parseInt(var7.nextToken());
            var4[var3++] = Integer.parseInt(var7.nextToken()) << 8 | Integer.parseInt(var7.nextToken());
         }
      }

      if (var3 != var4.length) {
         System.arraycopy(var4, var5, var4, var4.length - (var3 - var5), var3 - var5);

         for (int var9 = var5; var9 != var4.length - (var3 - var5); var9++) {
            var4[var9] = 0;
         }
      }

      return var4;
   }

   @Override
   public ASN1Primitive toASN1Primitive() {
      boolean var1 = this.tag == 4;
      return new DERTaggedObject(var1, this.tag, this.obj);
   }
}
