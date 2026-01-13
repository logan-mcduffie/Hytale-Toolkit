package com.google.common.flogger;

import org.checkerframework.checker.nullness.compatqual.NullableDecl;

public interface LazyArg<T> {
   @NullableDecl
   T evaluate();
}
