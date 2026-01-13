package org.bouncycastle.pqc.asn1;

import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.DERTaggedObject;
import org.bouncycastle.util.Arrays;

public class XMSSPrivateKey extends ASN1Object {
   private final int version;
   private final int index;
   private final byte[] secretKeySeed;
   private final byte[] secretKeyPRF;
   private final byte[] publicSeed;
   private final byte[] root;
   private final int maxIndex;
   private final byte[] bdsState;

   public XMSSPrivateKey(int var1, byte[] var2, byte[] var3, byte[] var4, byte[] var5, byte[] var6) {
      this.version = 0;
      this.index = var1;
      this.secretKeySeed = Arrays.clone(var2);
      this.secretKeyPRF = Arrays.clone(var3);
      this.publicSeed = Arrays.clone(var4);
      this.root = Arrays.clone(var5);
      this.bdsState = Arrays.clone(var6);
      this.maxIndex = -1;
   }

   public XMSSPrivateKey(int var1, byte[] var2, byte[] var3, byte[] var4, byte[] var5, byte[] var6, int var7) {
      this.version = 1;
      this.index = var1;
      this.secretKeySeed = Arrays.clone(var2);
      this.secretKeyPRF = Arrays.clone(var3);
      this.publicSeed = Arrays.clone(var4);
      this.root = Arrays.clone(var5);
      this.bdsState = Arrays.clone(var6);
      this.maxIndex = var7;
   }

   private XMSSPrivateKey(ASN1Sequence var1) {
      ASN1Integer var2 = ASN1Integer.getInstance(var1.getObjectAt(0));
      if (!var2.hasValue(0) && !var2.hasValue(1)) {
         throw new IllegalArgumentException("unknown version of sequence");
      } else {
         this.version = var2.intValueExact();
         if (var1.size() != 2 && var1.size() != 3) {
            throw new IllegalArgumentException("key sequence wrong size");
         } else {
            ASN1Sequence var3 = ASN1Sequence.getInstance(var1.getObjectAt(1));
            this.index = ASN1Integer.getInstance(var3.getObjectAt(0)).intValueExact();
            this.secretKeySeed = Arrays.clone(DEROctetString.getInstance(var3.getObjectAt(1)).getOctets());
            this.secretKeyPRF = Arrays.clone(DEROctetString.getInstance(var3.getObjectAt(2)).getOctets());
            this.publicSeed = Arrays.clone(DEROctetString.getInstance(var3.getObjectAt(3)).getOctets());
            this.root = Arrays.clone(DEROctetString.getInstance(var3.getObjectAt(4)).getOctets());
            if (var3.size() == 6) {
               ASN1TaggedObject var4 = ASN1TaggedObject.getInstance(var3.getObjectAt(5));
               if (var4.getTagNo() != 0) {
                  throw new IllegalArgumentException("unknown tag in XMSSPrivateKey");
               }

               this.maxIndex = ASN1Integer.getInstance(var4, false).intValueExact();
            } else {
               if (var3.size() != 5) {
                  throw new IllegalArgumentException("keySeq should be 5 or 6 in length");
               }

               this.maxIndex = -1;
            }

            if (var1.size() == 3) {
               this.bdsState = Arrays.clone(DEROctetString.getInstance(ASN1TaggedObject.getInstance(var1.getObjectAt(2)), true).getOctets());
            } else {
               this.bdsState = null;
            }
         }
      }
   }

   public static XMSSPrivateKey getInstance(Object var0) {
      if (var0 instanceof XMSSPrivateKey) {
         return (XMSSPrivateKey)var0;
      } else {
         return var0 != null ? new XMSSPrivateKey(ASN1Sequence.getInstance(var0)) : null;
      }
   }

   public int getVersion() {
      return this.version;
   }

   public int getIndex() {
      return this.index;
   }

   public int getMaxIndex() {
      return this.maxIndex;
   }

   public byte[] getSecretKeySeed() {
      return Arrays.clone(this.secretKeySeed);
   }

   public byte[] getSecretKeyPRF() {
      return Arrays.clone(this.secretKeyPRF);
   }

   public byte[] getPublicSeed() {
      return Arrays.clone(this.publicSeed);
   }

   public byte[] getRoot() {
      return Arrays.clone(this.root);
   }

   public byte[] getBdsState() {
      return Arrays.clone(this.bdsState);
   }

   @Override
   public ASN1Primitive toASN1Primitive() {
      ASN1EncodableVector var1 = new ASN1EncodableVector();
      if (this.maxIndex >= 0) {
         var1.add(new ASN1Integer(1L));
      } else {
         var1.add(new ASN1Integer(0L));
      }

      ASN1EncodableVector var2 = new ASN1EncodableVector();
      var2.add(new ASN1Integer(this.index));
      var2.add(new DEROctetString(this.secretKeySeed));
      var2.add(new DEROctetString(this.secretKeyPRF));
      var2.add(new DEROctetString(this.publicSeed));
      var2.add(new DEROctetString(this.root));
      if (this.maxIndex >= 0) {
         var2.add(new DERTaggedObject(false, 0, new ASN1Integer(this.maxIndex)));
      }

      var1.add(new DERSequence(var2));
      var1.add(new DERTaggedObject(true, 0, new DEROctetString(this.bdsState)));
      return new DERSequence(var1);
   }
}
