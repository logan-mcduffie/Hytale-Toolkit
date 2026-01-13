package org.bouncycastle.asn1;

import java.io.OutputStream;

public abstract class ASN1Generator {
   protected OutputStream _out;

   public ASN1Generator(OutputStream var1) {
      this._out = var1;
   }

   public abstract OutputStream getRawOutputStream();
}
