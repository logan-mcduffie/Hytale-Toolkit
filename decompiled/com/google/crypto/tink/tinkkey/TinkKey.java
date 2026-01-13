package com.google.crypto.tink.tinkkey;

import com.google.crypto.tink.KeyTemplate;
import com.google.errorprone.annotations.Immutable;

@Immutable
public interface TinkKey {
   boolean hasSecret();

   KeyTemplate getKeyTemplate();
}
