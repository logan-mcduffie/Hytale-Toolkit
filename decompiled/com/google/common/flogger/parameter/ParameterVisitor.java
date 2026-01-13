package com.google.common.flogger.parameter;

import com.google.common.flogger.backend.FormatChar;
import com.google.common.flogger.backend.FormatOptions;

public interface ParameterVisitor {
   void visit(Object var1, FormatChar var2, FormatOptions var3);

   void visitDateTime(Object var1, DateTimeFormat var2, FormatOptions var3);

   void visitPreformatted(Object var1, String var2);

   void visitMissing();

   void visitNull();
}
