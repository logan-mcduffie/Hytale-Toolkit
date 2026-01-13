package io.sentry;

import io.sentry.util.AutoClosableReentrantLock;
import java.util.Queue;

final class SynchronizedQueue<E> extends SynchronizedCollection<E> implements Queue<E> {
   private static final long serialVersionUID = 1L;

   static <E> SynchronizedQueue<E> synchronizedQueue(Queue<E> queue) {
      return new SynchronizedQueue<>(queue);
   }

   private SynchronizedQueue(Queue<E> queue) {
      super(queue);
   }

   protected SynchronizedQueue(Queue<E> queue, AutoClosableReentrantLock lock) {
      super(queue, lock);
   }

   protected Queue<E> decorated() {
      return (Queue<E>)super.decorated();
   }

   @Override
   public E element() {
      ISentryLifecycleToken ignored = this.lock.acquire();

      Object var2;
      try {
         var2 = this.decorated().element();
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

      return (E)var2;
   }

   @Override
   public boolean equals(Object object) {
      if (object == this) {
         return true;
      } else {
         ISentryLifecycleToken ignored = this.lock.acquire();

         boolean var3;
         try {
            var3 = this.decorated().equals(object);
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
   }

   @Override
   public int hashCode() {
      ISentryLifecycleToken ignored = this.lock.acquire();

      int var2;
      try {
         var2 = this.decorated().hashCode();
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

      return var2;
   }

   @Override
   public boolean offer(E e) {
      ISentryLifecycleToken ignored = this.lock.acquire();

      boolean var3;
      try {
         var3 = this.decorated().offer(e);
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

   @Override
   public E peek() {
      ISentryLifecycleToken ignored = this.lock.acquire();

      Object var2;
      try {
         var2 = this.decorated().peek();
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

      return (E)var2;
   }

   @Override
   public E poll() {
      ISentryLifecycleToken ignored = this.lock.acquire();

      Object var2;
      try {
         var2 = this.decorated().poll();
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

      return (E)var2;
   }

   @Override
   public E remove() {
      ISentryLifecycleToken ignored = this.lock.acquire();

      Object var2;
      try {
         var2 = this.decorated().remove();
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

      return (E)var2;
   }

   @Override
   public Object[] toArray() {
      ISentryLifecycleToken ignored = this.lock.acquire();

      Object[] var2;
      try {
         var2 = this.decorated().toArray();
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

      return var2;
   }

   @Override
   public <T> T[] toArray(T[] object) {
      ISentryLifecycleToken ignored = this.lock.acquire();

      Object[] var3;
      try {
         var3 = this.decorated().toArray(object);
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

      return (T[])var3;
   }
}
