package com.google.crypto.tink.mac;

import com.google.crypto.tink.Key;
import com.google.crypto.tink.util.Bytes;

public abstract class MacKey extends Key {
   public abstract Bytes getOutputPrefix();

   public abstract MacParameters getParameters();
}
