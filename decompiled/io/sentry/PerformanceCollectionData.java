package io.sentry;

import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.ApiStatus.Internal;

@Internal
public final class PerformanceCollectionData {
   @Nullable
   private Double cpuUsagePercentage = null;
   @Nullable
   private Long usedHeapMemory = null;
   @Nullable
   private Long usedNativeMemory = null;
   private final long nanoTimestamp;

   public PerformanceCollectionData(long nanoTimestamp) {
      this.nanoTimestamp = nanoTimestamp;
   }

   public void setCpuUsagePercentage(@Nullable Double cpuUsagePercentage) {
      this.cpuUsagePercentage = cpuUsagePercentage;
   }

   @Nullable
   public Double getCpuUsagePercentage() {
      return this.cpuUsagePercentage;
   }

   public void setUsedHeapMemory(@Nullable Long usedHeapMemory) {
      this.usedHeapMemory = usedHeapMemory;
   }

   @Nullable
   public Long getUsedHeapMemory() {
      return this.usedHeapMemory;
   }

   public void setUsedNativeMemory(@Nullable Long usedNativeMemory) {
      this.usedNativeMemory = usedNativeMemory;
   }

   @Nullable
   public Long getUsedNativeMemory() {
      return this.usedNativeMemory;
   }

   public long getNanoTimestamp() {
      return this.nanoTimestamp;
   }
}
