package com.google.crypto.tink;

import com.google.errorprone.annotations.Immutable;

@Immutable
public abstract class Parameters {
   public abstract boolean hasIdRequirement();
}
