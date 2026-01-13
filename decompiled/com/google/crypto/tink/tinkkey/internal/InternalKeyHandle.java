package com.google.crypto.tink.tinkkey.internal;

import com.google.crypto.tink.internal.KeyStatusTypeProtoConverter;
import com.google.crypto.tink.tinkkey.KeyHandle;
import com.google.crypto.tink.tinkkey.TinkKey;

public final class InternalKeyHandle extends KeyHandle {
   public InternalKeyHandle(TinkKey key, com.google.crypto.tink.proto.KeyStatusType status, int keyId) {
      super(key, KeyStatusTypeProtoConverter.fromProto(status), keyId);
   }
}
