package com.google.crypto.tink.mac;

import java.nio.ByteBuffer;
import java.security.GeneralSecurityException;

public interface ChunkedMacComputation {
   void update(ByteBuffer data) throws GeneralSecurityException;

   byte[] computeMac() throws GeneralSecurityException;
}
