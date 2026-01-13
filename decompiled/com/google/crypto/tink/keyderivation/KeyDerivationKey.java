package com.google.crypto.tink.keyderivation;

import com.google.crypto.tink.Key;

public abstract class KeyDerivationKey extends Key {
   public abstract KeyDerivationParameters getParameters();
}
