package com.github.luben.zstd;

public class ZstdFrameProgression {
   private long ingested;
   private long consumed;
   private long produced;
   private long flushed;
   private int currentJobID;
   private int nbActiveWorkers;

   public ZstdFrameProgression(long var1, long var3, long var5, long var7, int var9, int var10) {
      this.ingested = var1;
      this.consumed = var3;
      this.produced = var5;
      this.flushed = var7;
      this.currentJobID = var9;
      this.nbActiveWorkers = var10;
   }

   public long getIngested() {
      return this.ingested;
   }

   public long getConsumed() {
      return this.consumed;
   }

   public long getProduced() {
      return this.produced;
   }

   public long getFlushed() {
      return this.flushed;
   }

   public int getCurrentJobID() {
      return this.currentJobID;
   }

   public int getNbActiveWorkers() {
      return this.nbActiveWorkers;
   }
}
