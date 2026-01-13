package com.google.common.flogger.parser;

import com.google.common.flogger.backend.TemplateContext;
import com.google.common.flogger.parameter.Parameter;
import com.google.common.flogger.util.Checks;

public abstract class MessageBuilder<T> {
   private final TemplateContext context;
   private int pmask = 0;
   private int maxIndex = -1;

   public MessageBuilder(TemplateContext context) {
      this.context = Checks.checkNotNull(context, "context");
   }

   public final MessageParser getParser() {
      return this.context.getParser();
   }

   public final String getMessage() {
      return this.context.getMessage();
   }

   public final int getExpectedArgumentCount() {
      return this.maxIndex + 1;
   }

   public final void addParameter(int termStart, int termEnd, Parameter param) {
      if (param.getIndex() < 32) {
         this.pmask = this.pmask | 1 << param.getIndex();
      }

      this.maxIndex = Math.max(this.maxIndex, param.getIndex());
      this.addParameterImpl(termStart, termEnd, param);
   }

   protected abstract void addParameterImpl(int var1, int var2, Parameter var3);

   protected abstract T buildImpl();

   public final T build() {
      this.getParser().parseImpl(this);
      if ((this.pmask & this.pmask + 1) == 0 && (this.maxIndex <= 31 || this.pmask == -1)) {
         return this.buildImpl();
      } else {
         int firstMissing = Integer.numberOfTrailingZeros(~this.pmask);
         throw ParseException.generic(String.format("unreferenced arguments [first missing index=%d]", firstMissing), this.getMessage());
      }
   }
}
