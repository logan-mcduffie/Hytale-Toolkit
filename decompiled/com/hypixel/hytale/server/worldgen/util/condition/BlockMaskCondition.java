package com.hypixel.hytale.server.worldgen.util.condition;

import com.hypixel.hytale.server.worldgen.util.BlockFluidEntry;

@FunctionalInterface
public interface BlockMaskCondition {
   boolean eval(int var1, int var2, BlockFluidEntry var3);
}
