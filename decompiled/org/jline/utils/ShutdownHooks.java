package org.jline.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public final class ShutdownHooks {
   private static final List<ShutdownHooks.Task> tasks = new ArrayList<>();
   private static Thread hook;

   public static synchronized <T extends ShutdownHooks.Task> T add(T task) {
      Objects.requireNonNull(task);
      if (hook == null) {
         hook = addHook(new Thread("JLine Shutdown Hook") {
            @Override
            public void run() {
               ShutdownHooks.runTasks();
            }
         });
      }

      Log.debug("Adding shutdown-hook task: ", task);
      tasks.add(task);
      return task;
   }

   private static synchronized void runTasks() {
      Log.debug("Running all shutdown-hook tasks");

      for (ShutdownHooks.Task task : tasks.toArray(new ShutdownHooks.Task[tasks.size()])) {
         Log.debug("Running task: ", task);

         try {
            task.run();
         } catch (Throwable var5) {
            Log.warn("Task failed", var5);
         }
      }

      tasks.clear();
   }

   private static Thread addHook(Thread thread) {
      Log.debug("Registering shutdown-hook: ", thread);
      Runtime.getRuntime().addShutdownHook(thread);
      return thread;
   }

   public static synchronized void remove(ShutdownHooks.Task task) {
      Objects.requireNonNull(task);
      if (hook != null) {
         tasks.remove(task);
         if (tasks.isEmpty()) {
            removeHook(hook);
            hook = null;
         }
      }
   }

   private static void removeHook(Thread thread) {
      Log.debug("Removing shutdown-hook: ", thread);

      try {
         Runtime.getRuntime().removeShutdownHook(thread);
      } catch (IllegalStateException var2) {
      }
   }

   public interface Task {
      void run() throws Exception;
   }
}
