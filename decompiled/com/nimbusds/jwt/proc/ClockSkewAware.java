package com.nimbusds.jwt.proc;

public interface ClockSkewAware {
   int getMaxClockSkew();

   void setMaxClockSkew(int var1);
}
