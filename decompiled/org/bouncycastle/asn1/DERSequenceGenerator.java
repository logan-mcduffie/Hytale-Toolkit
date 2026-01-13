package org.bouncycastle.asn1;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class DERSequenceGenerator extends DERGenerator {
   private final ByteArrayOutputStream _bOut = new ByteArrayOutputStream();

   public DERSequenceGenerator(OutputStream var1) throws IOException {
      super(var1);
   }

   public DERSequenceGenerator(OutputStream var1, int var2, boolean var3) throws IOException {
      super(var1, var2, var3);
   }

   public void addObject(ASN1Encodable var1) throws IOException {
      var1.toASN1Primitive().encodeTo(this._bOut, "DER");
   }

   public void addObject(ASN1Primitive var1) throws IOException {
      var1.encodeTo(this._bOut, "DER");
   }

   @Override
   public OutputStream getRawOutputStream() {
      return this._bOut;
   }

   public void close() throws IOException {
      this.writeDEREncoded(48, this._bOut.toByteArray());
   }
}
