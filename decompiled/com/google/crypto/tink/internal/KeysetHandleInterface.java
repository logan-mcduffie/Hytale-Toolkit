package com.google.crypto.tink.internal;

import com.google.crypto.tink.Key;
import com.google.crypto.tink.KeyStatus;

public interface KeysetHandleInterface {
   KeysetHandleInterface.Entry getPrimary();

   int size();

   KeysetHandleInterface.Entry getAt(int i);

   public interface Entry {
      Key getKey();

      KeyStatus getStatus();

      int getId();

      boolean isPrimary();
   }
}
