package com.google.crypto.tink.hybrid;

import com.google.crypto.tink.Key;
import com.google.crypto.tink.util.Bytes;
import com.google.errorprone.annotations.Immutable;

@Immutable
public abstract class HybridPublicKey extends Key {
   public abstract Bytes getOutputPrefix();

   public abstract HybridParameters getParameters();
}
