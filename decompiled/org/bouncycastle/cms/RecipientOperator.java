package org.bouncycastle.cms;

import java.io.InputStream;
import java.io.OutputStream;
import org.bouncycastle.operator.InputAEADDecryptor;
import org.bouncycastle.operator.InputDecryptor;
import org.bouncycastle.operator.MacCalculator;
import org.bouncycastle.util.io.TeeInputStream;

public class RecipientOperator {
   private final Object operator;

   public RecipientOperator(InputDecryptor var1) {
      this.operator = var1;
   }

   public RecipientOperator(MacCalculator var1) {
      this.operator = var1;
   }

   public InputStream getInputStream(InputStream var1) {
      return (InputStream)(this.operator instanceof InputDecryptor
         ? ((InputDecryptor)this.operator).getInputStream(var1)
         : new TeeInputStream(var1, ((MacCalculator)this.operator).getOutputStream()));
   }

   public boolean isAEADBased() {
      return this.operator instanceof InputAEADDecryptor;
   }

   public OutputStream getAADStream() {
      return ((InputAEADDecryptor)this.operator).getAADStream();
   }

   public boolean isMacBased() {
      return this.operator instanceof MacCalculator;
   }

   public byte[] getMac() {
      if (this.operator instanceof MacCalculator) {
         return ((MacCalculator)this.operator).getMac();
      } else {
         return this.operator instanceof InputAEADDecryptor ? ((InputAEADDecryptor)this.operator).getMAC() : null;
      }
   }
}
