package com.google.crypto.tink.daead;

import com.google.crypto.tink.Key;
import com.google.crypto.tink.util.Bytes;

public abstract class DeterministicAeadKey extends Key {
   public abstract Bytes getOutputPrefix();

   public abstract DeterministicAeadParameters getParameters();
}
