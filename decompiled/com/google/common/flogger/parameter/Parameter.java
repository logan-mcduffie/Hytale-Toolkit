package com.google.common.flogger.parameter;

import com.google.common.flogger.backend.FormatOptions;

public abstract class Parameter {
   private final int index;
   private final FormatOptions options;

   protected Parameter(FormatOptions options, int index) {
      if (options == null) {
         throw new IllegalArgumentException("format options cannot be null");
      } else if (index < 0) {
         throw new IllegalArgumentException("invalid index: " + index);
      } else {
         this.index = index;
         this.options = options;
      }
   }

   public final int getIndex() {
      return this.index;
   }

   protected final FormatOptions getFormatOptions() {
      return this.options;
   }

   public final void accept(ParameterVisitor visitor, Object[] args) {
      if (this.getIndex() < args.length) {
         Object value = args[this.getIndex()];
         if (value != null) {
            this.accept(visitor, value);
         } else {
            visitor.visitNull();
         }
      } else {
         visitor.visitMissing();
      }
   }

   protected abstract void accept(ParameterVisitor var1, Object var2);

   public abstract String getFormat();
}
