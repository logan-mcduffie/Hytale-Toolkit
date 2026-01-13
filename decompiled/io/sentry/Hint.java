package io.sentry;

import io.sentry.util.AutoClosableReentrantLock;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.ApiStatus.Internal;

public final class Hint {
   @NotNull
   private static final Map<String, Class<?>> PRIMITIVE_MAPPINGS = new HashMap<>();
   @NotNull
   private final Map<String, Object> internalStorage = new HashMap<>();
   @NotNull
   private final List<Attachment> attachments = new ArrayList<>();
   @NotNull
   private final AutoClosableReentrantLock lock = new AutoClosableReentrantLock();
   @Nullable
   private Attachment screenshot = null;
   @Nullable
   private Attachment viewHierarchy = null;
   @Nullable
   private Attachment threadDump = null;
   @Nullable
   private ReplayRecording replayRecording = null;

   @NotNull
   public static Hint withAttachment(@Nullable Attachment attachment) {
      Hint hint = new Hint();
      hint.addAttachment(attachment);
      return hint;
   }

   @NotNull
   public static Hint withAttachments(@Nullable List<Attachment> attachments) {
      Hint hint = new Hint();
      hint.addAttachments(attachments);
      return hint;
   }

   public void set(@NotNull String name, @Nullable Object hint) {
      ISentryLifecycleToken ignored = this.lock.acquire();

      try {
         this.internalStorage.put(name, hint);
      } catch (Throwable var7) {
         if (ignored != null) {
            try {
               ignored.close();
            } catch (Throwable var6) {
               var7.addSuppressed(var6);
            }
         }

         throw var7;
      }

      if (ignored != null) {
         ignored.close();
      }
   }

   @Nullable
   public Object get(@NotNull String name) {
      ISentryLifecycleToken ignored = this.lock.acquire();

      Object var3;
      try {
         var3 = this.internalStorage.get(name);
      } catch (Throwable var6) {
         if (ignored != null) {
            try {
               ignored.close();
            } catch (Throwable var5) {
               var6.addSuppressed(var5);
            }
         }

         throw var6;
      }

      if (ignored != null) {
         ignored.close();
      }

      return var3;
   }

   @Nullable
   public <T> T getAs(@NotNull String name, @NotNull Class<T> clazz) {
      ISentryLifecycleToken ignored = this.lock.acquire();

      Object var9;
      label55: {
         label56: {
            try {
               Object hintValue = this.internalStorage.get(name);
               if (clazz.isInstance(hintValue)) {
                  var9 = hintValue;
                  break label55;
               }

               if (this.isCastablePrimitive(hintValue, clazz)) {
                  var9 = hintValue;
                  break label56;
               }

               var9 = null;
            } catch (Throwable var7) {
               if (ignored != null) {
                  try {
                     ignored.close();
                  } catch (Throwable var6) {
                     var7.addSuppressed(var6);
                  }
               }

               throw var7;
            }

            if (ignored != null) {
               ignored.close();
            }

            return (T)var9;
         }

         if (ignored != null) {
            ignored.close();
         }

         return (T)var9;
      }

      if (ignored != null) {
         ignored.close();
      }

      return (T)var9;
   }

   public void remove(@NotNull String name) {
      ISentryLifecycleToken ignored = this.lock.acquire();

      try {
         this.internalStorage.remove(name);
      } catch (Throwable var6) {
         if (ignored != null) {
            try {
               ignored.close();
            } catch (Throwable var5) {
               var6.addSuppressed(var5);
            }
         }

         throw var6;
      }

      if (ignored != null) {
         ignored.close();
      }
   }

   public void addAttachment(@Nullable Attachment attachment) {
      if (attachment != null) {
         this.attachments.add(attachment);
      }
   }

   public void addAttachments(@Nullable List<Attachment> attachments) {
      if (attachments != null) {
         this.attachments.addAll(attachments);
      }
   }

   @NotNull
   public List<Attachment> getAttachments() {
      return new ArrayList<>(this.attachments);
   }

   public void replaceAttachments(@Nullable List<Attachment> attachments) {
      this.clearAttachments();
      this.addAttachments(attachments);
   }

   public void clearAttachments() {
      this.attachments.clear();
   }

   @Internal
   public void clear() {
      ISentryLifecycleToken ignored = this.lock.acquire();

      try {
         Iterator<Entry<String, Object>> iterator = this.internalStorage.entrySet().iterator();

         while (iterator.hasNext()) {
            Entry<String, Object> entry = iterator.next();
            if (entry.getKey() == null || !entry.getKey().startsWith("sentry:")) {
               iterator.remove();
            }
         }
      } catch (Throwable var5) {
         if (ignored != null) {
            try {
               ignored.close();
            } catch (Throwable var4) {
               var5.addSuppressed(var4);
            }
         }

         throw var5;
      }

      if (ignored != null) {
         ignored.close();
      }
   }

   public void setScreenshot(@Nullable Attachment screenshot) {
      this.screenshot = screenshot;
   }

   @Nullable
   public Attachment getScreenshot() {
      return this.screenshot;
   }

   public void setViewHierarchy(@Nullable Attachment viewHierarchy) {
      this.viewHierarchy = viewHierarchy;
   }

   @Nullable
   public Attachment getViewHierarchy() {
      return this.viewHierarchy;
   }

   public void setThreadDump(@Nullable Attachment threadDump) {
      this.threadDump = threadDump;
   }

   @Nullable
   public Attachment getThreadDump() {
      return this.threadDump;
   }

   @Nullable
   public ReplayRecording getReplayRecording() {
      return this.replayRecording;
   }

   public void setReplayRecording(@Nullable ReplayRecording replayRecording) {
      this.replayRecording = replayRecording;
   }

   private boolean isCastablePrimitive(@Nullable Object hintValue, @NotNull Class<?> clazz) {
      Class<?> nonPrimitiveClass = PRIMITIVE_MAPPINGS.get(clazz.getCanonicalName());
      return hintValue != null && clazz.isPrimitive() && nonPrimitiveClass != null && nonPrimitiveClass.isInstance(hintValue);
   }

   static {
      PRIMITIVE_MAPPINGS.put("boolean", Boolean.class);
      PRIMITIVE_MAPPINGS.put("char", Character.class);
      PRIMITIVE_MAPPINGS.put("byte", Byte.class);
      PRIMITIVE_MAPPINGS.put("short", Short.class);
      PRIMITIVE_MAPPINGS.put("int", Integer.class);
      PRIMITIVE_MAPPINGS.put("long", Long.class);
      PRIMITIVE_MAPPINGS.put("float", Float.class);
      PRIMITIVE_MAPPINGS.put("double", Double.class);
   }
}
