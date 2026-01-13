package com.google.common.flogger.backend.system;

import com.google.common.flogger.backend.LoggerBackend;
import com.google.common.flogger.backend.NoOpContextDataProvider;
import com.google.common.flogger.backend.Platform;
import com.google.common.flogger.context.ContextDataProvider;
import com.google.common.flogger.util.StaticMethodCaller;

public class DefaultPlatform extends Platform {
   private static final String BACKEND_FACTORY = "flogger.backend_factory";
   private static final String LOGGING_CONTEXT = "flogger.logging_context";
   private static final String CLOCK = "flogger.clock";
   private final BackendFactory backendFactory;
   private final ContextDataProvider context;
   private final Clock clock;
   private final Platform.LogCallerFinder callerFinder;

   public DefaultPlatform() {
      BackendFactory factory = StaticMethodCaller.callGetterFromSystemProperty("flogger.backend_factory", BackendFactory.class);
      this.backendFactory = factory != null ? factory : SimpleBackendFactory.getInstance();
      ContextDataProvider context = StaticMethodCaller.callGetterFromSystemProperty("flogger.logging_context", ContextDataProvider.class);
      this.context = context != null ? context : NoOpContextDataProvider.getInstance();
      Clock clock = StaticMethodCaller.callGetterFromSystemProperty("flogger.clock", Clock.class);
      this.clock = (Clock)(clock != null ? clock : SystemClock.getInstance());
      this.callerFinder = StackBasedCallerFinder.getInstance();
   }

   DefaultPlatform(BackendFactory factory, ContextDataProvider context, Clock clock, Platform.LogCallerFinder callerFinder) {
      this.backendFactory = factory;
      this.context = context;
      this.clock = clock;
      this.callerFinder = callerFinder;
   }

   @Override
   protected Platform.LogCallerFinder getCallerFinderImpl() {
      return this.callerFinder;
   }

   @Override
   protected LoggerBackend getBackendImpl(String className) {
      return this.backendFactory.create(className);
   }

   @Override
   protected ContextDataProvider getContextDataProviderImpl() {
      return this.context;
   }

   @Override
   protected long getCurrentTimeNanosImpl() {
      return this.clock.getCurrentTimeNanos();
   }

   @Override
   protected String getConfigInfoImpl() {
      return "Platform: "
         + this.getClass().getName()
         + "\nBackendFactory: "
         + this.backendFactory
         + "\nClock: "
         + this.clock
         + "\nLoggingContext: "
         + this.context
         + "\nLogCallerFinder: "
         + this.callerFinder
         + "\n";
   }
}
