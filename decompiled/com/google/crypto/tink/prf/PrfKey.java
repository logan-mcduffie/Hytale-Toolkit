package com.google.crypto.tink.prf;

import com.google.crypto.tink.Key;

public abstract class PrfKey extends Key {
   public abstract PrfParameters getParameters();
}
