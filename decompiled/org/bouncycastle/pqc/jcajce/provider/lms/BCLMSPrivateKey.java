package org.bouncycastle.pqc.jcajce.provider.lms;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.security.PrivateKey;
import org.bouncycastle.asn1.ASN1Set;
import org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.pqc.crypto.lms.HSSPrivateKeyParameters;
import org.bouncycastle.pqc.crypto.lms.LMSKeyParameters;
import org.bouncycastle.pqc.crypto.lms.LMSPrivateKeyParameters;
import org.bouncycastle.pqc.crypto.util.PrivateKeyFactory;
import org.bouncycastle.pqc.crypto.util.PrivateKeyInfoFactory;
import org.bouncycastle.pqc.jcajce.interfaces.LMSPrivateKey;
import org.bouncycastle.util.Arrays;

public class BCLMSPrivateKey implements PrivateKey, LMSPrivateKey {
   private static final long serialVersionUID = 8568701712864512338L;
   private transient LMSKeyParameters keyParams;
   private transient ASN1Set attributes;

   public BCLMSPrivateKey(LMSKeyParameters var1) {
      this.keyParams = var1 instanceof HSSPrivateKeyParameters
         ? (HSSPrivateKeyParameters)var1
         : new HSSPrivateKeyParameters(
            (LMSPrivateKeyParameters)var1,
            ((LMSPrivateKeyParameters)var1).getIndex(),
            ((LMSPrivateKeyParameters)var1).getIndex() + ((LMSPrivateKeyParameters)var1).getUsagesRemaining()
         );
   }

   public BCLMSPrivateKey(PrivateKeyInfo var1) throws IOException {
      this.init(var1);
   }

   private void init(PrivateKeyInfo var1) throws IOException {
      this.attributes = var1.getAttributes();
      this.keyParams = (LMSKeyParameters)PrivateKeyFactory.createKey(var1);
   }

   @Override
   public long getIndex() {
      if (this.getUsagesRemaining() == 0L) {
         throw new IllegalStateException("key exhausted");
      } else {
         return this.keyParams instanceof LMSPrivateKeyParameters
            ? ((LMSPrivateKeyParameters)this.keyParams).getIndex()
            : ((HSSPrivateKeyParameters)this.keyParams).getIndex();
      }
   }

   @Override
   public long getUsagesRemaining() {
      return this.keyParams instanceof LMSPrivateKeyParameters
         ? ((LMSPrivateKeyParameters)this.keyParams).getUsagesRemaining()
         : ((HSSPrivateKeyParameters)this.keyParams).getUsagesRemaining();
   }

   @Override
   public LMSPrivateKey extractKeyShard(int var1) {
      return this.keyParams instanceof LMSPrivateKeyParameters
         ? new BCLMSPrivateKey(((LMSPrivateKeyParameters)this.keyParams).extractKeyShard(var1))
         : new BCLMSPrivateKey(((HSSPrivateKeyParameters)this.keyParams).extractKeyShard(var1));
   }

   @Override
   public String getAlgorithm() {
      return "LMS";
   }

   @Override
   public String getFormat() {
      return "PKCS#8";
   }

   @Override
   public byte[] getEncoded() {
      try {
         PrivateKeyInfo var1 = PrivateKeyInfoFactory.createPrivateKeyInfo(this.keyParams, this.attributes);
         return var1.getEncoded();
      } catch (IOException var2) {
         return null;
      }
   }

   @Override
   public boolean equals(Object var1) {
      if (var1 == this) {
         return true;
      } else if (var1 instanceof BCLMSPrivateKey) {
         BCLMSPrivateKey var2 = (BCLMSPrivateKey)var1;

         try {
            return Arrays.areEqual(this.keyParams.getEncoded(), var2.keyParams.getEncoded());
         } catch (IOException var4) {
            throw new IllegalStateException("unable to perform equals");
         }
      } else {
         return false;
      }
   }

   @Override
   public int hashCode() {
      try {
         return Arrays.hashCode(this.keyParams.getEncoded());
      } catch (IOException var2) {
         throw new IllegalStateException("unable to calculate hashCode");
      }
   }

   CipherParameters getKeyParams() {
      return this.keyParams;
   }

   @Override
   public int getLevels() {
      return this.keyParams instanceof LMSPrivateKeyParameters ? 1 : ((HSSPrivateKeyParameters)this.keyParams).getL();
   }

   private void readObject(ObjectInputStream var1) throws IOException, ClassNotFoundException {
      var1.defaultReadObject();
      byte[] var2 = (byte[])var1.readObject();
      this.init(PrivateKeyInfo.getInstance(var2));
   }

   private void writeObject(ObjectOutputStream var1) throws IOException {
      var1.defaultWriteObject();
      var1.writeObject(this.getEncoded());
   }
}
