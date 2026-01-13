package org.bouncycastle.cms;

import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.asn1.cms.KEKIdentifier;
import org.bouncycastle.asn1.cms.KEKRecipientInfo;
import org.bouncycastle.asn1.cms.RecipientInfo;
import org.bouncycastle.operator.GenericKey;
import org.bouncycastle.operator.OperatorException;
import org.bouncycastle.operator.SymmetricKeyWrapper;

public abstract class KEKRecipientInfoGenerator implements RecipientInfoGenerator {
   private final KEKIdentifier kekIdentifier;
   protected final SymmetricKeyWrapper wrapper;

   protected KEKRecipientInfoGenerator(KEKIdentifier var1, SymmetricKeyWrapper var2) {
      this.kekIdentifier = var1;
      this.wrapper = var2;
   }

   @Override
   public final RecipientInfo generate(GenericKey var1) throws CMSException {
      try {
         DEROctetString var2 = new DEROctetString(this.wrapper.generateWrappedKey(var1));
         return new RecipientInfo(new KEKRecipientInfo(this.kekIdentifier, this.wrapper.getAlgorithmIdentifier(), var2));
      } catch (OperatorException var3) {
         throw new CMSException("exception wrapping content key: " + var3.getMessage(), var3);
      }
   }
}
