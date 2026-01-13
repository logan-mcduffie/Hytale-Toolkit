package org.bouncycastle.crypto;

import java.security.SecureRandom;

public interface SecureRandomProvider {
   SecureRandom get();
}
