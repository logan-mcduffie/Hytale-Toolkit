package com.google.crypto.tink.mac.internal;

import com.google.crypto.tink.mac.ChunkedMacComputation;
import com.google.crypto.tink.mac.ChunkedMacVerification;
import com.google.crypto.tink.util.Bytes;
import java.nio.ByteBuffer;
import java.security.GeneralSecurityException;

final class ChunkedMacVerificationFromComputation implements ChunkedMacVerification {
   private final Bytes tag;
   private final ChunkedMacComputation macComputation;

   private ChunkedMacVerificationFromComputation(ChunkedMacComputation macComputation, byte[] tag) {
      this.macComputation = macComputation;
      this.tag = Bytes.copyFrom(tag);
   }

   @Override
   public void update(ByteBuffer data) throws GeneralSecurityException {
      this.macComputation.update(data);
   }

   @Override
   public void verifyMac() throws GeneralSecurityException {
      byte[] other = this.macComputation.computeMac();
      if (!this.tag.equals(Bytes.copyFrom(other))) {
         throw new GeneralSecurityException("invalid MAC");
      }
   }

   static ChunkedMacVerification create(ChunkedMacComputation macComputation, byte[] tag) {
      return new ChunkedMacVerificationFromComputation(macComputation, tag);
   }
}
