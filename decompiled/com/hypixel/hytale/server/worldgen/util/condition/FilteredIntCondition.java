package com.hypixel.hytale.server.worldgen.util.condition;

import com.hypixel.hytale.procedurallib.condition.IIntCondition;
import it.unimi.dsi.fastutil.ints.IntSets;
import javax.annotation.Nonnull;

public class FilteredIntCondition implements IIntCondition {
   private final IIntCondition filter;
   private final IIntCondition condition;

   public FilteredIntCondition(int filterValue, IIntCondition condition) {
      this(new HashSetIntCondition(IntSets.singleton(filterValue)), condition);
   }

   public FilteredIntCondition(IIntCondition filter, IIntCondition condition) {
      this.filter = filter;
      this.condition = condition;
   }

   @Override
   public boolean eval(int value) {
      return this.filter.eval(value) ? false : this.condition.eval(value);
   }

   @Nonnull
   @Override
   public String toString() {
      return "FilteredIntCondition{filter=" + this.filter + ", condition=" + this.condition + "}";
   }
}
