package io.netty.handler.codec.http;

import io.netty.handler.codec.DecoderResult;
import io.netty.util.internal.ObjectUtil;

public class DefaultHttpObject implements HttpObject {
   private static final int HASH_CODE_PRIME = 31;
   private DecoderResult decoderResult = DecoderResult.SUCCESS;

   protected DefaultHttpObject() {
   }

   @Override
   public DecoderResult decoderResult() {
      return this.decoderResult;
   }

   @Deprecated
   @Override
   public DecoderResult getDecoderResult() {
      return this.decoderResult();
   }

   @Override
   public void setDecoderResult(DecoderResult decoderResult) {
      this.decoderResult = ObjectUtil.checkNotNull(decoderResult, "decoderResult");
   }

   @Override
   public int hashCode() {
      int result = 1;
      return 31 * result + this.decoderResult.hashCode();
   }

   @Override
   public boolean equals(Object o) {
      if (!(o instanceof DefaultHttpObject)) {
         return false;
      } else {
         DefaultHttpObject other = (DefaultHttpObject)o;
         return this.decoderResult().equals(other.decoderResult());
      }
   }
}
