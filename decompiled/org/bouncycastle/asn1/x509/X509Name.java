package org.bouncycastle.asn1.x509;

import java.io.IOException;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1Set;
import org.bouncycastle.asn1.ASN1String;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.ASN1UniversalString;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.DERSet;
import org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.util.Strings;
import org.bouncycastle.util.encoders.Hex;

/** @deprecated */
public class X509Name extends ASN1Object {
   /** @deprecated */
   public static final ASN1ObjectIdentifier C = new ASN1ObjectIdentifier("2.5.4.6");
   /** @deprecated */
   public static final ASN1ObjectIdentifier O = new ASN1ObjectIdentifier("2.5.4.10");
   /** @deprecated */
   public static final ASN1ObjectIdentifier OU = new ASN1ObjectIdentifier("2.5.4.11");
   /** @deprecated */
   public static final ASN1ObjectIdentifier T = new ASN1ObjectIdentifier("2.5.4.12");
   /** @deprecated */
   public static final ASN1ObjectIdentifier CN = new ASN1ObjectIdentifier("2.5.4.3");
   public static final ASN1ObjectIdentifier SN = new ASN1ObjectIdentifier("2.5.4.5");
   public static final ASN1ObjectIdentifier STREET = new ASN1ObjectIdentifier("2.5.4.9");
   public static final ASN1ObjectIdentifier SERIALNUMBER = SN;
   public static final ASN1ObjectIdentifier L = new ASN1ObjectIdentifier("2.5.4.7");
   public static final ASN1ObjectIdentifier ST = new ASN1ObjectIdentifier("2.5.4.8");
   public static final ASN1ObjectIdentifier SURNAME = new ASN1ObjectIdentifier("2.5.4.4");
   public static final ASN1ObjectIdentifier GIVENNAME = new ASN1ObjectIdentifier("2.5.4.42");
   public static final ASN1ObjectIdentifier INITIALS = new ASN1ObjectIdentifier("2.5.4.43");
   public static final ASN1ObjectIdentifier GENERATION = new ASN1ObjectIdentifier("2.5.4.44");
   public static final ASN1ObjectIdentifier UNIQUE_IDENTIFIER = new ASN1ObjectIdentifier("2.5.4.45");
   public static final ASN1ObjectIdentifier BUSINESS_CATEGORY = new ASN1ObjectIdentifier("2.5.4.15");
   public static final ASN1ObjectIdentifier POSTAL_CODE = new ASN1ObjectIdentifier("2.5.4.17");
   public static final ASN1ObjectIdentifier DN_QUALIFIER = new ASN1ObjectIdentifier("2.5.4.46");
   public static final ASN1ObjectIdentifier PSEUDONYM = new ASN1ObjectIdentifier("2.5.4.65");
   public static final ASN1ObjectIdentifier DATE_OF_BIRTH = new ASN1ObjectIdentifier("1.3.6.1.5.5.7.9.1");
   public static final ASN1ObjectIdentifier PLACE_OF_BIRTH = new ASN1ObjectIdentifier("1.3.6.1.5.5.7.9.2");
   public static final ASN1ObjectIdentifier GENDER = new ASN1ObjectIdentifier("1.3.6.1.5.5.7.9.3");
   public static final ASN1ObjectIdentifier COUNTRY_OF_CITIZENSHIP = new ASN1ObjectIdentifier("1.3.6.1.5.5.7.9.4");
   public static final ASN1ObjectIdentifier COUNTRY_OF_RESIDENCE = new ASN1ObjectIdentifier("1.3.6.1.5.5.7.9.5");
   public static final ASN1ObjectIdentifier NAME_AT_BIRTH = new ASN1ObjectIdentifier("1.3.36.8.3.14");
   public static final ASN1ObjectIdentifier POSTAL_ADDRESS = new ASN1ObjectIdentifier("2.5.4.16");
   public static final ASN1ObjectIdentifier DMD_NAME = new ASN1ObjectIdentifier("2.5.4.54");
   public static final ASN1ObjectIdentifier TELEPHONE_NUMBER = X509ObjectIdentifiers.id_at_telephoneNumber;
   public static final ASN1ObjectIdentifier NAME = X509ObjectIdentifiers.id_at_name;
   /** @deprecated */
   public static final ASN1ObjectIdentifier EmailAddress = PKCSObjectIdentifiers.pkcs_9_at_emailAddress;
   public static final ASN1ObjectIdentifier UnstructuredName = PKCSObjectIdentifiers.pkcs_9_at_unstructuredName;
   public static final ASN1ObjectIdentifier UnstructuredAddress = PKCSObjectIdentifiers.pkcs_9_at_unstructuredAddress;
   public static final ASN1ObjectIdentifier E = EmailAddress;
   public static final ASN1ObjectIdentifier DC = new ASN1ObjectIdentifier("0.9.2342.19200300.100.1.25");
   public static final ASN1ObjectIdentifier UID = new ASN1ObjectIdentifier("0.9.2342.19200300.100.1.1");
   public static boolean DefaultReverse = false;
   public static final Hashtable DefaultSymbols = new Hashtable();
   public static final Hashtable RFC2253Symbols = new Hashtable();
   public static final Hashtable RFC1779Symbols = new Hashtable();
   public static final Hashtable DefaultLookUp = new Hashtable();
   /** @deprecated */
   public static final Hashtable OIDLookUp = DefaultSymbols;
   /** @deprecated */
   public static final Hashtable SymbolLookUp = DefaultLookUp;
   private static final Boolean TRUE = new Boolean(true);
   private static final Boolean FALSE = new Boolean(false);
   private X509NameEntryConverter converter = null;
   private Vector ordering = new Vector();
   private Vector values = new Vector();
   private Vector added = new Vector();
   private ASN1Sequence seq;
   private boolean isHashCodeCalculated;
   private int hashCodeValue;

   public static X509Name getInstance(ASN1TaggedObject var0, boolean var1) {
      return getInstance(ASN1Sequence.getInstance(var0, var1));
   }

   public static X509Name getInstance(Object var0) {
      if (var0 instanceof X509Name) {
         return (X509Name)var0;
      } else if (var0 instanceof X500Name) {
         return new X509Name(ASN1Sequence.getInstance(((X500Name)var0).toASN1Primitive()));
      } else {
         return var0 != null ? new X509Name(ASN1Sequence.getInstance(var0)) : null;
      }
   }

   protected X509Name() {
   }

   /** @deprecated */
   public X509Name(ASN1Sequence var1) {
      this.seq = var1;
      Enumeration var2 = var1.getObjects();

      while (var2.hasMoreElements()) {
         ASN1Set var3 = ASN1Set.getInstance(((ASN1Encodable)var2.nextElement()).toASN1Primitive());

         for (int var4 = 0; var4 < var3.size(); var4++) {
            ASN1Sequence var5 = ASN1Sequence.getInstance(var3.getObjectAt(var4).toASN1Primitive());
            if (var5.size() != 2) {
               throw new IllegalArgumentException("badly sized pair");
            }

            this.ordering.addElement(ASN1ObjectIdentifier.getInstance(var5.getObjectAt(0)));
            ASN1Encodable var6 = var5.getObjectAt(1);
            if (var6 instanceof ASN1String && !(var6 instanceof ASN1UniversalString)) {
               String var7 = ((ASN1String)var6).getString();
               if (var7.length() > 0 && var7.charAt(0) == '#') {
                  this.values.addElement("\\" + var7);
               } else {
                  this.values.addElement(var7);
               }
            } else {
               try {
                  this.values.addElement("#" + this.bytesToString(Hex.encode(var6.toASN1Primitive().getEncoded("DER"))));
               } catch (IOException var8) {
                  throw new IllegalArgumentException("cannot encode value");
               }
            }

            this.added.addElement(var4 != 0 ? TRUE : FALSE);
         }
      }
   }

   /** @deprecated */
   public X509Name(Hashtable var1) {
      this(null, var1);
   }

   public X509Name(Vector var1, Hashtable var2) {
      this(var1, var2, new X509DefaultEntryConverter());
   }

   /** @deprecated */
   public X509Name(Vector var1, Hashtable var2, X509NameEntryConverter var3) {
      this.converter = var3;
      if (var1 != null) {
         for (int var4 = 0; var4 != var1.size(); var4++) {
            this.ordering.addElement(var1.elementAt(var4));
            this.added.addElement(FALSE);
         }
      } else {
         Enumeration var6 = var2.keys();

         while (var6.hasMoreElements()) {
            this.ordering.addElement(var6.nextElement());
            this.added.addElement(FALSE);
         }
      }

      for (int var7 = 0; var7 != this.ordering.size(); var7++) {
         ASN1ObjectIdentifier var5 = (ASN1ObjectIdentifier)this.ordering.elementAt(var7);
         if (var2.get(var5) == null) {
            throw new IllegalArgumentException("No attribute for object id - " + var5.getId() + " - passed to distinguished name");
         }

         this.values.addElement(var2.get(var5));
      }
   }

   /** @deprecated */
   public X509Name(Vector var1, Vector var2) {
      this(var1, var2, new X509DefaultEntryConverter());
   }

   /** @deprecated */
   public X509Name(Vector var1, Vector var2, X509NameEntryConverter var3) {
      this.converter = var3;
      if (var1.size() != var2.size()) {
         throw new IllegalArgumentException("oids vector must be same length as values.");
      } else {
         for (int var4 = 0; var4 < var1.size(); var4++) {
            this.ordering.addElement(var1.elementAt(var4));
            this.values.addElement(var2.elementAt(var4));
            this.added.addElement(FALSE);
         }
      }
   }

   /** @deprecated */
   public X509Name(String var1) {
      this(DefaultReverse, DefaultLookUp, var1);
   }

   /** @deprecated */
   public X509Name(String var1, X509NameEntryConverter var2) {
      this(DefaultReverse, DefaultLookUp, var1, var2);
   }

   /** @deprecated */
   public X509Name(boolean var1, String var2) {
      this(var1, DefaultLookUp, var2);
   }

   /** @deprecated */
   public X509Name(boolean var1, String var2, X509NameEntryConverter var3) {
      this(var1, DefaultLookUp, var2, var3);
   }

   /** @deprecated */
   public X509Name(boolean var1, Hashtable var2, String var3) {
      this(var1, var2, var3, new X509DefaultEntryConverter());
   }

   private ASN1ObjectIdentifier decodeOID(String var1, Hashtable var2) {
      var1 = var1.trim();
      if (var1.regionMatches(true, 0, "OID.", 0, 4)) {
         return new ASN1ObjectIdentifier(var1.substring(4));
      } else {
         ASN1ObjectIdentifier var3 = ASN1ObjectIdentifier.tryFromID(var1);
         if (var3 != null) {
            return var3;
         } else {
            var3 = (ASN1ObjectIdentifier)var2.get(Strings.toLowerCase(var1));
            if (var3 != null) {
               return var3;
            } else {
               throw new IllegalArgumentException("Unknown object id - " + var1 + " - passed to distinguished name");
            }
         }
      }
   }

   private String unescape(String var1) {
      if (var1.length() != 0 && (var1.indexOf(92) >= 0 || var1.indexOf(34) >= 0)) {
         char[] var2 = var1.toCharArray();
         boolean var3 = false;
         boolean var4 = false;
         StringBuffer var5 = new StringBuffer(var1.length());
         byte var6 = 0;
         if (var2[0] == '\\' && var2[1] == '#') {
            var6 = 2;
            var5.append("\\#");
         }

         boolean var7 = false;
         int var8 = 0;

         for (int var9 = var6; var9 != var2.length; var9++) {
            char var10 = var2[var9];
            if (var10 != ' ') {
               var7 = true;
            }

            if (var10 == '"') {
               if (!var3) {
                  var4 = !var4;
               } else {
                  var5.append(var10);
               }

               var3 = false;
            } else if (var10 == '\\' && !var3 && !var4) {
               var3 = true;
               var8 = var5.length();
            } else if (var10 != ' ' || var3 || var7) {
               var5.append(var10);
               var3 = false;
            }
         }

         if (var5.length() > 0) {
            while (var5.charAt(var5.length() - 1) == ' ' && var8 != var5.length() - 1) {
               var5.setLength(var5.length() - 1);
            }
         }

         return var5.toString();
      } else {
         return var1.trim();
      }
   }

   public X509Name(boolean var1, Hashtable var2, String var3, X509NameEntryConverter var4) {
      this.converter = var4;
      X509NameTokenizer var5 = new X509NameTokenizer(var3);

      while (var5.hasMoreTokens()) {
         String var6 = var5.nextToken();
         if (var6.indexOf(43) > 0) {
            X509NameTokenizer var7 = new X509NameTokenizer(var6, '+');
            this.addEntry(var2, var7.nextToken(), FALSE);

            while (var7.hasMoreTokens()) {
               this.addEntry(var2, var7.nextToken(), TRUE);
            }
         } else {
            this.addEntry(var2, var6, FALSE);
         }
      }

      if (var1) {
         Vector var11 = new Vector();
         Vector var12 = new Vector();
         Vector var8 = new Vector();
         int var9 = 1;

         for (int var10 = 0; var10 < this.ordering.size(); var10++) {
            if ((Boolean)this.added.elementAt(var10)) {
               var11.insertElementAt(this.ordering.elementAt(var10), var9);
               var12.insertElementAt(this.values.elementAt(var10), var9);
               var8.insertElementAt(this.added.elementAt(var10), var9);
               var9++;
            } else {
               var11.insertElementAt(this.ordering.elementAt(var10), 0);
               var12.insertElementAt(this.values.elementAt(var10), 0);
               var8.insertElementAt(this.added.elementAt(var10), 0);
               var9 = 1;
            }
         }

         this.ordering = var11;
         this.values = var12;
         this.added = var8;
      }
   }

   private void addEntry(Hashtable var1, String var2, Boolean var3) {
      X509NameTokenizer var4 = new X509NameTokenizer(var2, '=');
      String var5 = var4.nextToken();
      if (!var4.hasMoreTokens()) {
         throw new IllegalArgumentException("badly formatted directory string");
      } else {
         String var6 = var4.nextToken();
         ASN1ObjectIdentifier var7 = this.decodeOID(var5, var1);
         this.ordering.addElement(var7);
         this.values.addElement(this.unescape(var6));
         this.added.addElement(var3);
      }
   }

   public Vector getOIDs() {
      Vector var1 = new Vector();

      for (int var2 = 0; var2 != this.ordering.size(); var2++) {
         var1.addElement(this.ordering.elementAt(var2));
      }

      return var1;
   }

   public Vector getValues() {
      Vector var1 = new Vector();

      for (int var2 = 0; var2 != this.values.size(); var2++) {
         var1.addElement(this.values.elementAt(var2));
      }

      return var1;
   }

   public Vector getValues(ASN1ObjectIdentifier var1) {
      Vector var2 = new Vector();

      for (int var3 = 0; var3 != this.values.size(); var3++) {
         if (this.ordering.elementAt(var3).equals(var1)) {
            String var4 = (String)this.values.elementAt(var3);
            if (var4.length() > 2 && var4.charAt(0) == '\\' && var4.charAt(1) == '#') {
               var2.addElement(var4.substring(1));
            } else {
               var2.addElement(var4);
            }
         }
      }

      return var2;
   }

   @Override
   public ASN1Primitive toASN1Primitive() {
      if (this.seq == null) {
         ASN1EncodableVector var1 = new ASN1EncodableVector();
         ASN1EncodableVector var2 = new ASN1EncodableVector();
         ASN1ObjectIdentifier var3 = null;

         for (int var4 = 0; var4 != this.ordering.size(); var4++) {
            if (var3 != null && !(Boolean)this.added.elementAt(var4)) {
               var1.add(new DERSet(var2));
               var2 = new ASN1EncodableVector();
            }

            var3 = (ASN1ObjectIdentifier)this.ordering.elementAt(var4);
            ASN1Primitive var5 = this.converter.getConvertedValue(var3, (String)this.values.elementAt(var4));
            var2.add(new DERSequence(var3, var5));
         }

         var1.add(new DERSet(var2));
         this.seq = new DERSequence(var1);
      }

      return this.seq;
   }

   public boolean equals(Object var1, boolean var2) {
      if (!var2) {
         return this.equals(var1);
      } else if (var1 == this) {
         return true;
      } else if (!(var1 instanceof X509Name) && !(var1 instanceof ASN1Sequence)) {
         return false;
      } else {
         ASN1Primitive var3 = ((ASN1Encodable)var1).toASN1Primitive();
         if (this.toASN1Primitive().equals(var3)) {
            return true;
         } else {
            X509Name var4;
            try {
               var4 = getInstance(var1);
            } catch (IllegalArgumentException var11) {
               return false;
            }

            int var5 = this.ordering.size();
            if (var5 != var4.ordering.size()) {
               return false;
            } else {
               for (int var6 = 0; var6 < var5; var6++) {
                  ASN1ObjectIdentifier var7 = (ASN1ObjectIdentifier)this.ordering.elementAt(var6);
                  ASN1ObjectIdentifier var8 = (ASN1ObjectIdentifier)var4.ordering.elementAt(var6);
                  if (!var7.equals(var8)) {
                     return false;
                  }

                  String var9 = (String)this.values.elementAt(var6);
                  String var10 = (String)var4.values.elementAt(var6);
                  if (!this.equivalentStrings(var9, var10)) {
                     return false;
                  }
               }

               return true;
            }
         }
      }
   }

   @Override
   public int hashCode() {
      if (this.isHashCodeCalculated) {
         return this.hashCodeValue;
      } else {
         this.isHashCodeCalculated = true;

         for (int var1 = 0; var1 != this.ordering.size(); var1++) {
            String var2 = (String)this.values.elementAt(var1);
            var2 = this.canonicalize(var2);
            var2 = this.stripInternalSpaces(var2);
            this.hashCodeValue = this.hashCodeValue ^ this.ordering.elementAt(var1).hashCode();
            this.hashCodeValue = this.hashCodeValue ^ var2.hashCode();
         }

         return this.hashCodeValue;
      }
   }

   @Override
   public boolean equals(Object var1) {
      if (var1 == this) {
         return true;
      } else if (!(var1 instanceof X509Name) && !(var1 instanceof ASN1Sequence)) {
         return false;
      } else {
         ASN1Primitive var2 = ((ASN1Encodable)var1).toASN1Primitive();
         if (this.toASN1Primitive().equals(var2)) {
            return true;
         } else {
            X509Name var3;
            try {
               var3 = getInstance(var1);
            } catch (IllegalArgumentException var16) {
               return false;
            }

            int var4 = this.ordering.size();
            if (var4 != var3.ordering.size()) {
               return false;
            } else if (var4 == 0) {
               return true;
            } else {
               boolean[] var5 = new boolean[var4];
               int var6;
               int var7;
               byte var8;
               if (this.ordering.elementAt(0).equals(var3.ordering.elementAt(0))) {
                  var6 = 0;
                  var7 = var4;
                  var8 = 1;
               } else {
                  var6 = var4 - 1;
                  var7 = -1;
                  var8 = -1;
               }

               for (int var9 = var6; var9 != var7; var9 += var8) {
                  boolean var10 = false;
                  ASN1ObjectIdentifier var11 = (ASN1ObjectIdentifier)this.ordering.elementAt(var9);
                  String var12 = (String)this.values.elementAt(var9);

                  for (int var13 = 0; var13 < var4; var13++) {
                     if (!var5[var13]) {
                        ASN1ObjectIdentifier var14 = (ASN1ObjectIdentifier)var3.ordering.elementAt(var13);
                        if (var11.equals(var14)) {
                           String var15 = (String)var3.values.elementAt(var13);
                           if (this.equivalentStrings(var12, var15)) {
                              var5[var13] = true;
                              var10 = true;
                              break;
                           }
                        }
                     }
                  }

                  if (!var10) {
                     return false;
                  }
               }

               return true;
            }
         }
      }
   }

   private boolean equivalentStrings(String var1, String var2) {
      String var3 = this.canonicalize(var1);
      String var4 = this.canonicalize(var2);
      if (!var3.equals(var4)) {
         var3 = this.stripInternalSpaces(var3);
         var4 = this.stripInternalSpaces(var4);
         if (!var3.equals(var4)) {
            return false;
         }
      }

      return true;
   }

   private String canonicalize(String var1) {
      String var2 = Strings.toLowerCase(var1.trim());
      if (var2.length() > 0 && var2.charAt(0) == '#') {
         ASN1Primitive var3 = this.decodeObject(var2);
         if (var3 instanceof ASN1String) {
            var2 = Strings.toLowerCase(((ASN1String)var3).getString().trim());
         }
      }

      return var2;
   }

   private ASN1Primitive decodeObject(String var1) {
      try {
         return ASN1Primitive.fromByteArray(Hex.decodeStrict(var1, 1, var1.length() - 1));
      } catch (IOException var3) {
         throw new IllegalStateException("unknown encoding in name: " + var3);
      }
   }

   private String stripInternalSpaces(String var1) {
      StringBuffer var2 = new StringBuffer();
      if (var1.length() != 0) {
         char var3 = var1.charAt(0);
         var2.append(var3);

         for (int var4 = 1; var4 < var1.length(); var4++) {
            char var5 = var1.charAt(var4);
            if (var3 != ' ' || var5 != ' ') {
               var2.append(var5);
            }

            var3 = var5;
         }
      }

      return var2.toString();
   }

   private void appendValue(StringBuffer var1, Hashtable var2, ASN1ObjectIdentifier var3, String var4) {
      String var5 = (String)var2.get(var3);
      if (var5 != null) {
         var1.append(var5);
      } else {
         var1.append(var3.getId());
      }

      var1.append('=');
      int var6 = var1.length();
      var1.append(var4);
      int var7 = var1.length();
      if (var4.length() >= 2 && var4.charAt(0) == '\\' && var4.charAt(1) == '#') {
         var6 += 2;
      }

      while (var6 < var7 && var1.charAt(var6) == ' ') {
         var1.insert(var6, "\\");
         var6 += 2;
         var7++;
      }

      while (true) {
         var7--;
         if (var7 <= var6 || var1.charAt(var7) != ' ') {
            while (var6 <= var7) {
               switch (var1.charAt(var6)) {
                  case '"':
                  case '+':
                  case ',':
                  case ';':
                  case '<':
                  case '=':
                  case '>':
                  case '\\':
                     var1.insert(var6, "\\");
                     var6 += 2;
                     var7++;
                     break;
                  default:
                     var6++;
               }
            }

            return;
         }

         var1.insert(var7, '\\');
      }
   }

   public String toString(boolean var1, Hashtable var2) {
      StringBuffer var3 = new StringBuffer();
      Vector var4 = new Vector();
      boolean var5 = true;
      StringBuffer var6 = null;

      for (int var7 = 0; var7 < this.ordering.size(); var7++) {
         if ((Boolean)this.added.elementAt(var7)) {
            var6.append('+');
            this.appendValue(var6, var2, (ASN1ObjectIdentifier)this.ordering.elementAt(var7), (String)this.values.elementAt(var7));
         } else {
            var6 = new StringBuffer();
            this.appendValue(var6, var2, (ASN1ObjectIdentifier)this.ordering.elementAt(var7), (String)this.values.elementAt(var7));
            var4.addElement(var6);
         }
      }

      if (var1) {
         for (int var8 = var4.size() - 1; var8 >= 0; var8--) {
            if (var5) {
               var5 = false;
            } else {
               var3.append(',');
            }

            var3.append(var4.elementAt(var8).toString());
         }
      } else {
         for (int var9 = 0; var9 < var4.size(); var9++) {
            if (var5) {
               var5 = false;
            } else {
               var3.append(',');
            }

            var3.append(var4.elementAt(var9).toString());
         }
      }

      return var3.toString();
   }

   private String bytesToString(byte[] var1) {
      char[] var2 = new char[var1.length];

      for (int var3 = 0; var3 != var2.length; var3++) {
         var2[var3] = (char)(var1[var3] & 255);
      }

      return new String(var2);
   }

   @Override
   public String toString() {
      return this.toString(DefaultReverse, DefaultSymbols);
   }

   static {
      DefaultSymbols.put(C, "C");
      DefaultSymbols.put(O, "O");
      DefaultSymbols.put(T, "T");
      DefaultSymbols.put(OU, "OU");
      DefaultSymbols.put(CN, "CN");
      DefaultSymbols.put(L, "L");
      DefaultSymbols.put(ST, "ST");
      DefaultSymbols.put(SN, "SERIALNUMBER");
      DefaultSymbols.put(EmailAddress, "E");
      DefaultSymbols.put(DC, "DC");
      DefaultSymbols.put(UID, "UID");
      DefaultSymbols.put(STREET, "STREET");
      DefaultSymbols.put(SURNAME, "SURNAME");
      DefaultSymbols.put(GIVENNAME, "GIVENNAME");
      DefaultSymbols.put(INITIALS, "INITIALS");
      DefaultSymbols.put(GENERATION, "GENERATION");
      DefaultSymbols.put(UnstructuredAddress, "unstructuredAddress");
      DefaultSymbols.put(UnstructuredName, "unstructuredName");
      DefaultSymbols.put(UNIQUE_IDENTIFIER, "UniqueIdentifier");
      DefaultSymbols.put(DN_QUALIFIER, "DN");
      DefaultSymbols.put(PSEUDONYM, "Pseudonym");
      DefaultSymbols.put(POSTAL_ADDRESS, "PostalAddress");
      DefaultSymbols.put(NAME_AT_BIRTH, "NameAtBirth");
      DefaultSymbols.put(COUNTRY_OF_CITIZENSHIP, "CountryOfCitizenship");
      DefaultSymbols.put(COUNTRY_OF_RESIDENCE, "CountryOfResidence");
      DefaultSymbols.put(GENDER, "Gender");
      DefaultSymbols.put(PLACE_OF_BIRTH, "PlaceOfBirth");
      DefaultSymbols.put(DATE_OF_BIRTH, "DateOfBirth");
      DefaultSymbols.put(POSTAL_CODE, "PostalCode");
      DefaultSymbols.put(BUSINESS_CATEGORY, "BusinessCategory");
      DefaultSymbols.put(TELEPHONE_NUMBER, "TelephoneNumber");
      DefaultSymbols.put(NAME, "Name");
      RFC2253Symbols.put(C, "C");
      RFC2253Symbols.put(O, "O");
      RFC2253Symbols.put(OU, "OU");
      RFC2253Symbols.put(CN, "CN");
      RFC2253Symbols.put(L, "L");
      RFC2253Symbols.put(ST, "ST");
      RFC2253Symbols.put(STREET, "STREET");
      RFC2253Symbols.put(DC, "DC");
      RFC2253Symbols.put(UID, "UID");
      RFC1779Symbols.put(C, "C");
      RFC1779Symbols.put(O, "O");
      RFC1779Symbols.put(OU, "OU");
      RFC1779Symbols.put(CN, "CN");
      RFC1779Symbols.put(L, "L");
      RFC1779Symbols.put(ST, "ST");
      RFC1779Symbols.put(STREET, "STREET");
      DefaultLookUp.put("c", C);
      DefaultLookUp.put("o", O);
      DefaultLookUp.put("t", T);
      DefaultLookUp.put("ou", OU);
      DefaultLookUp.put("cn", CN);
      DefaultLookUp.put("l", L);
      DefaultLookUp.put("st", ST);
      DefaultLookUp.put("sn", SN);
      DefaultLookUp.put("serialnumber", SN);
      DefaultLookUp.put("street", STREET);
      DefaultLookUp.put("emailaddress", E);
      DefaultLookUp.put("dc", DC);
      DefaultLookUp.put("e", E);
      DefaultLookUp.put("uid", UID);
      DefaultLookUp.put("surname", SURNAME);
      DefaultLookUp.put("givenname", GIVENNAME);
      DefaultLookUp.put("initials", INITIALS);
      DefaultLookUp.put("generation", GENERATION);
      DefaultLookUp.put("unstructuredaddress", UnstructuredAddress);
      DefaultLookUp.put("unstructuredname", UnstructuredName);
      DefaultLookUp.put("uniqueidentifier", UNIQUE_IDENTIFIER);
      DefaultLookUp.put("dn", DN_QUALIFIER);
      DefaultLookUp.put("pseudonym", PSEUDONYM);
      DefaultLookUp.put("postaladdress", POSTAL_ADDRESS);
      DefaultLookUp.put("nameofbirth", NAME_AT_BIRTH);
      DefaultLookUp.put("countryofcitizenship", COUNTRY_OF_CITIZENSHIP);
      DefaultLookUp.put("countryofresidence", COUNTRY_OF_RESIDENCE);
      DefaultLookUp.put("gender", GENDER);
      DefaultLookUp.put("placeofbirth", PLACE_OF_BIRTH);
      DefaultLookUp.put("dateofbirth", DATE_OF_BIRTH);
      DefaultLookUp.put("postalcode", POSTAL_CODE);
      DefaultLookUp.put("businesscategory", BUSINESS_CATEGORY);
      DefaultLookUp.put("telephonenumber", TELEPHONE_NUMBER);
      DefaultLookUp.put("name", NAME);
   }
}
