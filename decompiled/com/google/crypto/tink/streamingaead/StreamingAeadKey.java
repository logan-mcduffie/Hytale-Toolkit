package com.google.crypto.tink.streamingaead;

import com.google.crypto.tink.Key;
import javax.annotation.Nullable;

public abstract class StreamingAeadKey extends Key {
   @Nullable
   @Override
   public final Integer getIdRequirementOrNull() {
      return null;
   }

   public abstract StreamingAeadParameters getParameters();
}
