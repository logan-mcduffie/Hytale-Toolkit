package com.google.crypto.tink.aead;

import com.google.crypto.tink.Key;
import com.google.crypto.tink.util.Bytes;

public abstract class AeadKey extends Key {
   public abstract Bytes getOutputPrefix();

   public abstract AeadParameters getParameters();
}
