package org.bouncycastle.crypto.digests;

import org.bouncycastle.crypto.EncodableService;

public interface EncodableDigest extends EncodableService {
   @Override
   byte[] getEncodedState();
}
