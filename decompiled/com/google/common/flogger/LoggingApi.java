package com.google.common.flogger;

import com.google.common.flogger.util.Checks;
import com.google.errorprone.annotations.CheckReturnValue;
import java.util.concurrent.TimeUnit;
import org.checkerframework.checker.nullness.compatqual.NullableDecl;

@CheckReturnValue
public interface LoggingApi<API extends LoggingApi<API>> {
   API withCause(@NullableDecl Throwable var1);

   API every(int var1);

   API atMostEvery(int var1, TimeUnit var2);

   API withStackTrace(StackSize var1);

   <T> API with(MetadataKey<T> var1, @NullableDecl T var2);

   <T> API with(MetadataKey<Boolean> var1);

   API withInjectedLogSite(LogSite var1);

   API withInjectedLogSite(String var1, String var2, int var3, @NullableDecl String var4);

   boolean isEnabled();

   void logVarargs(String var1, @NullableDecl Object[] var2);

   void log();

   void log(String var1);

   void log(String var1, @NullableDecl Object var2);

   void log(String var1, @NullableDecl Object var2, @NullableDecl Object var3);

   void log(String var1, @NullableDecl Object var2, @NullableDecl Object var3, @NullableDecl Object var4);

   void log(String var1, @NullableDecl Object var2, @NullableDecl Object var3, @NullableDecl Object var4, @NullableDecl Object var5);

   void log(String var1, @NullableDecl Object var2, @NullableDecl Object var3, @NullableDecl Object var4, @NullableDecl Object var5, @NullableDecl Object var6);

   void log(
      String var1,
      @NullableDecl Object var2,
      @NullableDecl Object var3,
      @NullableDecl Object var4,
      @NullableDecl Object var5,
      @NullableDecl Object var6,
      @NullableDecl Object var7
   );

   void log(
      String var1,
      @NullableDecl Object var2,
      @NullableDecl Object var3,
      @NullableDecl Object var4,
      @NullableDecl Object var5,
      @NullableDecl Object var6,
      @NullableDecl Object var7,
      @NullableDecl Object var8
   );

   void log(
      String var1,
      @NullableDecl Object var2,
      @NullableDecl Object var3,
      @NullableDecl Object var4,
      @NullableDecl Object var5,
      @NullableDecl Object var6,
      @NullableDecl Object var7,
      @NullableDecl Object var8,
      @NullableDecl Object var9
   );

   void log(
      String var1,
      @NullableDecl Object var2,
      @NullableDecl Object var3,
      @NullableDecl Object var4,
      @NullableDecl Object var5,
      @NullableDecl Object var6,
      @NullableDecl Object var7,
      @NullableDecl Object var8,
      @NullableDecl Object var9,
      @NullableDecl Object var10
   );

   void log(
      String var1,
      @NullableDecl Object var2,
      @NullableDecl Object var3,
      @NullableDecl Object var4,
      @NullableDecl Object var5,
      @NullableDecl Object var6,
      @NullableDecl Object var7,
      @NullableDecl Object var8,
      @NullableDecl Object var9,
      @NullableDecl Object var10,
      @NullableDecl Object var11
   );

   void log(
      String var1,
      @NullableDecl Object var2,
      @NullableDecl Object var3,
      @NullableDecl Object var4,
      @NullableDecl Object var5,
      @NullableDecl Object var6,
      @NullableDecl Object var7,
      @NullableDecl Object var8,
      @NullableDecl Object var9,
      @NullableDecl Object var10,
      @NullableDecl Object var11,
      Object... var12
   );

   void log(String var1, char var2);

   void log(String var1, byte var2);

   void log(String var1, short var2);

   void log(String var1, int var2);

   void log(String var1, long var2);

   void log(String var1, @NullableDecl Object var2, boolean var3);

   void log(String var1, @NullableDecl Object var2, char var3);

   void log(String var1, @NullableDecl Object var2, byte var3);

   void log(String var1, @NullableDecl Object var2, short var3);

   void log(String var1, @NullableDecl Object var2, int var3);

   void log(String var1, @NullableDecl Object var2, long var3);

   void log(String var1, @NullableDecl Object var2, float var3);

   void log(String var1, @NullableDecl Object var2, double var3);

   void log(String var1, boolean var2, @NullableDecl Object var3);

   void log(String var1, char var2, @NullableDecl Object var3);

   void log(String var1, byte var2, @NullableDecl Object var3);

   void log(String var1, short var2, @NullableDecl Object var3);

   void log(String var1, int var2, @NullableDecl Object var3);

   void log(String var1, long var2, @NullableDecl Object var4);

   void log(String var1, float var2, @NullableDecl Object var3);

   void log(String var1, double var2, @NullableDecl Object var4);

   void log(String var1, boolean var2, boolean var3);

   void log(String var1, char var2, boolean var3);

   void log(String var1, byte var2, boolean var3);

   void log(String var1, short var2, boolean var3);

   void log(String var1, int var2, boolean var3);

   void log(String var1, long var2, boolean var4);

   void log(String var1, float var2, boolean var3);

   void log(String var1, double var2, boolean var4);

   void log(String var1, boolean var2, char var3);

   void log(String var1, char var2, char var3);

   void log(String var1, byte var2, char var3);

   void log(String var1, short var2, char var3);

   void log(String var1, int var2, char var3);

   void log(String var1, long var2, char var4);

   void log(String var1, float var2, char var3);

   void log(String var1, double var2, char var4);

   void log(String var1, boolean var2, byte var3);

   void log(String var1, char var2, byte var3);

   void log(String var1, byte var2, byte var3);

   void log(String var1, short var2, byte var3);

   void log(String var1, int var2, byte var3);

   void log(String var1, long var2, byte var4);

   void log(String var1, float var2, byte var3);

   void log(String var1, double var2, byte var4);

   void log(String var1, boolean var2, short var3);

   void log(String var1, char var2, short var3);

   void log(String var1, byte var2, short var3);

   void log(String var1, short var2, short var3);

   void log(String var1, int var2, short var3);

   void log(String var1, long var2, short var4);

   void log(String var1, float var2, short var3);

   void log(String var1, double var2, short var4);

   void log(String var1, boolean var2, int var3);

   void log(String var1, char var2, int var3);

   void log(String var1, byte var2, int var3);

   void log(String var1, short var2, int var3);

   void log(String var1, int var2, int var3);

   void log(String var1, long var2, int var4);

   void log(String var1, float var2, int var3);

   void log(String var1, double var2, int var4);

   void log(String var1, boolean var2, long var3);

   void log(String var1, char var2, long var3);

   void log(String var1, byte var2, long var3);

   void log(String var1, short var2, long var3);

   void log(String var1, int var2, long var3);

   void log(String var1, long var2, long var4);

   void log(String var1, float var2, long var3);

   void log(String var1, double var2, long var4);

   void log(String var1, boolean var2, float var3);

   void log(String var1, char var2, float var3);

   void log(String var1, byte var2, float var3);

   void log(String var1, short var2, float var3);

   void log(String var1, int var2, float var3);

   void log(String var1, long var2, float var4);

   void log(String var1, float var2, float var3);

   void log(String var1, double var2, float var4);

   void log(String var1, boolean var2, double var3);

   void log(String var1, char var2, double var3);

   void log(String var1, byte var2, double var3);

   void log(String var1, short var2, double var3);

   void log(String var1, int var2, double var3);

   void log(String var1, long var2, double var4);

   void log(String var1, float var2, double var3);

   void log(String var1, double var2, double var4);

   public static class NoOp<API extends LoggingApi<API>> implements LoggingApi<API> {
      protected final API noOp() {
         return (API)this;
      }

      @Override
      public API withInjectedLogSite(LogSite logSite) {
         return this.noOp();
      }

      @Override
      public API withInjectedLogSite(String internalClassName, String methodName, int encodedLineNumber, @NullableDecl String sourceFileName) {
         return this.noOp();
      }

      @Override
      public final boolean isEnabled() {
         return false;
      }

      @Override
      public final <T> API with(MetadataKey<T> key, @NullableDecl T value) {
         Checks.checkNotNull(key, "metadata key");
         return this.noOp();
      }

      @Override
      public final <T> API with(MetadataKey<Boolean> key) {
         Checks.checkNotNull(key, "metadata key");
         return this.noOp();
      }

      @Override
      public final API withCause(@NullableDecl Throwable cause) {
         return this.noOp();
      }

      @Override
      public final API every(int n) {
         return this.noOp();
      }

      @Override
      public final API atMostEvery(int n, TimeUnit unit) {
         Checks.checkNotNull(unit, "time unit");
         return this.noOp();
      }

      @Override
      public API withStackTrace(StackSize size) {
         Checks.checkNotNull(size, "stack size");
         return this.noOp();
      }

      @Override
      public final void logVarargs(String msg, Object[] params) {
      }

      @Override
      public final void log() {
      }

      @Override
      public final void log(String msg) {
      }

      @Override
      public final void log(String msg, @NullableDecl Object p1) {
      }

      @Override
      public final void log(String msg, @NullableDecl Object p1, @NullableDecl Object p2) {
      }

      @Override
      public final void log(String msg, @NullableDecl Object p1, @NullableDecl Object p2, @NullableDecl Object p3) {
      }

      @Override
      public final void log(String msg, @NullableDecl Object p1, @NullableDecl Object p2, @NullableDecl Object p3, @NullableDecl Object p4) {
      }

      @Override
      public final void log(
         String msg, @NullableDecl Object p1, @NullableDecl Object p2, @NullableDecl Object p3, @NullableDecl Object p4, @NullableDecl Object p5
      ) {
      }

      @Override
      public final void log(
         String msg,
         @NullableDecl Object p1,
         @NullableDecl Object p2,
         @NullableDecl Object p3,
         @NullableDecl Object p4,
         @NullableDecl Object p5,
         @NullableDecl Object p6
      ) {
      }

      @Override
      public final void log(
         String msg,
         @NullableDecl Object p1,
         @NullableDecl Object p2,
         @NullableDecl Object p3,
         @NullableDecl Object p4,
         @NullableDecl Object p5,
         @NullableDecl Object p6,
         @NullableDecl Object p7
      ) {
      }

      @Override
      public final void log(
         String msg,
         @NullableDecl Object p1,
         @NullableDecl Object p2,
         @NullableDecl Object p3,
         @NullableDecl Object p4,
         @NullableDecl Object p5,
         @NullableDecl Object p6,
         @NullableDecl Object p7,
         @NullableDecl Object p8
      ) {
      }

      @Override
      public final void log(
         String msg,
         @NullableDecl Object p1,
         @NullableDecl Object p2,
         @NullableDecl Object p3,
         @NullableDecl Object p4,
         @NullableDecl Object p5,
         @NullableDecl Object p6,
         @NullableDecl Object p7,
         @NullableDecl Object p8,
         @NullableDecl Object p9
      ) {
      }

      @Override
      public final void log(
         String msg,
         @NullableDecl Object p1,
         @NullableDecl Object p2,
         @NullableDecl Object p3,
         @NullableDecl Object p4,
         @NullableDecl Object p5,
         @NullableDecl Object p6,
         @NullableDecl Object p7,
         @NullableDecl Object p8,
         @NullableDecl Object p9,
         @NullableDecl Object p10
      ) {
      }

      @Override
      public final void log(
         String msg,
         @NullableDecl Object p1,
         @NullableDecl Object p2,
         @NullableDecl Object p3,
         @NullableDecl Object p4,
         @NullableDecl Object p5,
         @NullableDecl Object p6,
         @NullableDecl Object p7,
         @NullableDecl Object p8,
         @NullableDecl Object p9,
         @NullableDecl Object p10,
         Object... rest
      ) {
      }

      @Override
      public final void log(String msg, char p1) {
      }

      @Override
      public final void log(String msg, byte p1) {
      }

      @Override
      public final void log(String msg, short p1) {
      }

      @Override
      public final void log(String msg, int p1) {
      }

      @Override
      public final void log(String msg, long p1) {
      }

      @Override
      public final void log(String msg, @NullableDecl Object p1, boolean p2) {
      }

      @Override
      public final void log(String msg, @NullableDecl Object p1, char p2) {
      }

      @Override
      public final void log(String msg, @NullableDecl Object p1, byte p2) {
      }

      @Override
      public final void log(String msg, @NullableDecl Object p1, short p2) {
      }

      @Override
      public final void log(String msg, @NullableDecl Object p1, int p2) {
      }

      @Override
      public final void log(String msg, @NullableDecl Object p1, long p2) {
      }

      @Override
      public final void log(String msg, @NullableDecl Object p1, float p2) {
      }

      @Override
      public final void log(String msg, @NullableDecl Object p1, double p2) {
      }

      @Override
      public final void log(String msg, boolean p1, @NullableDecl Object p2) {
      }

      @Override
      public final void log(String msg, char p1, @NullableDecl Object p2) {
      }

      @Override
      public final void log(String msg, byte p1, @NullableDecl Object p2) {
      }

      @Override
      public final void log(String msg, short p1, @NullableDecl Object p2) {
      }

      @Override
      public final void log(String msg, int p1, @NullableDecl Object p2) {
      }

      @Override
      public final void log(String msg, long p1, @NullableDecl Object p2) {
      }

      @Override
      public final void log(String msg, float p1, @NullableDecl Object p2) {
      }

      @Override
      public final void log(String msg, double p1, @NullableDecl Object p2) {
      }

      @Override
      public final void log(String msg, boolean p1, boolean p2) {
      }

      @Override
      public final void log(String msg, char p1, boolean p2) {
      }

      @Override
      public final void log(String msg, byte p1, boolean p2) {
      }

      @Override
      public final void log(String msg, short p1, boolean p2) {
      }

      @Override
      public final void log(String msg, int p1, boolean p2) {
      }

      @Override
      public final void log(String msg, long p1, boolean p2) {
      }

      @Override
      public final void log(String msg, float p1, boolean p2) {
      }

      @Override
      public final void log(String msg, double p1, boolean p2) {
      }

      @Override
      public final void log(String msg, boolean p1, char p2) {
      }

      @Override
      public final void log(String msg, char p1, char p2) {
      }

      @Override
      public final void log(String msg, byte p1, char p2) {
      }

      @Override
      public final void log(String msg, short p1, char p2) {
      }

      @Override
      public final void log(String msg, int p1, char p2) {
      }

      @Override
      public final void log(String msg, long p1, char p2) {
      }

      @Override
      public final void log(String msg, float p1, char p2) {
      }

      @Override
      public final void log(String msg, double p1, char p2) {
      }

      @Override
      public final void log(String msg, boolean p1, byte p2) {
      }

      @Override
      public final void log(String msg, char p1, byte p2) {
      }

      @Override
      public final void log(String msg, byte p1, byte p2) {
      }

      @Override
      public final void log(String msg, short p1, byte p2) {
      }

      @Override
      public final void log(String msg, int p1, byte p2) {
      }

      @Override
      public final void log(String msg, long p1, byte p2) {
      }

      @Override
      public final void log(String msg, float p1, byte p2) {
      }

      @Override
      public final void log(String msg, double p1, byte p2) {
      }

      @Override
      public final void log(String msg, boolean p1, short p2) {
      }

      @Override
      public final void log(String msg, char p1, short p2) {
      }

      @Override
      public final void log(String msg, byte p1, short p2) {
      }

      @Override
      public final void log(String msg, short p1, short p2) {
      }

      @Override
      public final void log(String msg, int p1, short p2) {
      }

      @Override
      public final void log(String msg, long p1, short p2) {
      }

      @Override
      public final void log(String msg, float p1, short p2) {
      }

      @Override
      public final void log(String msg, double p1, short p2) {
      }

      @Override
      public final void log(String msg, boolean p1, int p2) {
      }

      @Override
      public final void log(String msg, char p1, int p2) {
      }

      @Override
      public final void log(String msg, byte p1, int p2) {
      }

      @Override
      public final void log(String msg, short p1, int p2) {
      }

      @Override
      public final void log(String msg, int p1, int p2) {
      }

      @Override
      public final void log(String msg, long p1, int p2) {
      }

      @Override
      public final void log(String msg, float p1, int p2) {
      }

      @Override
      public final void log(String msg, double p1, int p2) {
      }

      @Override
      public final void log(String msg, boolean p1, long p2) {
      }

      @Override
      public final void log(String msg, char p1, long p2) {
      }

      @Override
      public final void log(String msg, byte p1, long p2) {
      }

      @Override
      public final void log(String msg, short p1, long p2) {
      }

      @Override
      public final void log(String msg, int p1, long p2) {
      }

      @Override
      public final void log(String msg, long p1, long p2) {
      }

      @Override
      public final void log(String msg, float p1, long p2) {
      }

      @Override
      public final void log(String msg, double p1, long p2) {
      }

      @Override
      public final void log(String msg, boolean p1, float p2) {
      }

      @Override
      public final void log(String msg, char p1, float p2) {
      }

      @Override
      public final void log(String msg, byte p1, float p2) {
      }

      @Override
      public final void log(String msg, short p1, float p2) {
      }

      @Override
      public final void log(String msg, int p1, float p2) {
      }

      @Override
      public final void log(String msg, long p1, float p2) {
      }

      @Override
      public final void log(String msg, float p1, float p2) {
      }

      @Override
      public final void log(String msg, double p1, float p2) {
      }

      @Override
      public final void log(String msg, boolean p1, double p2) {
      }

      @Override
      public final void log(String msg, char p1, double p2) {
      }

      @Override
      public final void log(String msg, byte p1, double p2) {
      }

      @Override
      public final void log(String msg, short p1, double p2) {
      }

      @Override
      public final void log(String msg, int p1, double p2) {
      }

      @Override
      public final void log(String msg, long p1, double p2) {
      }

      @Override
      public final void log(String msg, float p1, double p2) {
      }

      @Override
      public final void log(String msg, double p1, double p2) {
      }
   }
}
