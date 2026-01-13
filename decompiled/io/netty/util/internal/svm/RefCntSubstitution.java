package io.netty.util.internal.svm;

import com.oracle.svm.core.annotate.Alias;
import com.oracle.svm.core.annotate.RecomputeFieldValue;
import com.oracle.svm.core.annotate.TargetClass;
import com.oracle.svm.core.annotate.RecomputeFieldValue.Kind;

@TargetClass(className = "io.netty.util.internal.RefCnt$UnsafeRefCnt")
final class RefCntSubstitution {
   @Alias
   @RecomputeFieldValue(kind = Kind.FieldOffset, declClassName = "io.netty.util.internal.RefCnt", name = "value")
   public static long VALUE_OFFSET;

   private RefCntSubstitution() {
   }
}
