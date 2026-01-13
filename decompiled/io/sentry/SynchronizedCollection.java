package io.sentry;

import io.sentry.util.AutoClosableReentrantLock;
import java.io.Serializable;
import java.util.Collection;
import java.util.Iterator;

class SynchronizedCollection<E> implements Collection<E>, Serializable {
   private static final long serialVersionUID = 2412805092710877986L;
   private final Collection<E> collection;
   final AutoClosableReentrantLock lock;

   public static <T> SynchronizedCollection<T> synchronizedCollection(Collection<T> coll) {
      return new SynchronizedCollection<>(coll);
   }

   SynchronizedCollection(Collection<E> collection) {
      if (collection == null) {
         throw new NullPointerException("Collection must not be null.");
      } else {
         this.collection = collection;
         this.lock = new AutoClosableReentrantLock();
      }
   }

   SynchronizedCollection(Collection<E> collection, AutoClosableReentrantLock lock) {
      if (collection == null) {
         throw new NullPointerException("Collection must not be null.");
      } else if (lock == null) {
         throw new NullPointerException("Lock must not be null.");
      } else {
         this.collection = collection;
         this.lock = lock;
      }
   }

   protected Collection<E> decorated() {
      return this.collection;
   }

   @Override
   public boolean add(E object) {
      ISentryLifecycleToken ignored = this.lock.acquire();

      boolean var3;
      try {
         var3 = this.decorated().add(object);
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
   public boolean addAll(Collection<? extends E> coll) {
      ISentryLifecycleToken ignored = this.lock.acquire();

      boolean var3;
      try {
         var3 = this.decorated().addAll(coll);
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
   public void clear() {
      ISentryLifecycleToken ignored = this.lock.acquire();

      try {
         this.decorated().clear();
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

   @Override
   public boolean contains(Object object) {
      ISentryLifecycleToken ignored = this.lock.acquire();

      boolean var3;
      try {
         var3 = this.decorated().contains(object);
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
   public boolean containsAll(Collection<?> coll) {
      ISentryLifecycleToken ignored = this.lock.acquire();

      boolean var3;
      try {
         var3 = this.decorated().containsAll(coll);
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
   public boolean isEmpty() {
      ISentryLifecycleToken ignored = this.lock.acquire();

      boolean var2;
      try {
         var2 = this.decorated().isEmpty();
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
   public Iterator<E> iterator() {
      return this.decorated().iterator();
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

   @Override
   public boolean remove(Object object) {
      ISentryLifecycleToken ignored = this.lock.acquire();

      boolean var3;
      try {
         var3 = this.decorated().remove(object);
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
   public boolean removeAll(Collection<?> coll) {
      ISentryLifecycleToken ignored = this.lock.acquire();

      boolean var3;
      try {
         var3 = this.decorated().removeAll(coll);
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
   public boolean retainAll(Collection<?> coll) {
      ISentryLifecycleToken ignored = this.lock.acquire();

      boolean var3;
      try {
         var3 = this.decorated().retainAll(coll);
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
   public int size() {
      ISentryLifecycleToken ignored = this.lock.acquire();

      int var2;
      try {
         var2 = this.decorated().size();
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
   public boolean equals(Object object) {
      ISentryLifecycleToken ignored = this.lock.acquire();

      boolean var7;
      label50: {
         try {
            if (object == this) {
               var7 = true;
               break label50;
            }

            var7 = object == this || this.decorated().equals(object);
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

         return var7;
      }

      if (ignored != null) {
         ignored.close();
      }

      return var7;
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
   public String toString() {
      ISentryLifecycleToken ignored = this.lock.acquire();

      String var2;
      try {
         var2 = this.decorated().toString();
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
}
