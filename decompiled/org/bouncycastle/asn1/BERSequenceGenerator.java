package org.bouncycastle.asn1;

import java.io.IOException;
import java.io.OutputStream;

public class BERSequenceGenerator extends BERGenerator {
   public BERSequenceGenerator(OutputStream var1) throws IOException {
      super(var1);
      this.writeBERHeader(48);
   }

   public BERSequenceGenerator(OutputStream var1, int var2, boolean var3) throws IOException {
      super(var1, var2, var3);
      this.writeBERHeader(48);
   }

   public void addObject(ASN1Encodable var1) throws IOException {
      var1.toASN1Primitive().encodeTo(this._out);
   }

   public void addObject(ASN1Primitive var1) throws IOException {
      var1.encodeTo(this._out);
   }

   public void close() throws IOException {
      this.writeBEREnd();
   }
}
