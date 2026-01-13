package it.unimi.dsi.fastutil.objects;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.SortedSet;

public class ObjectAVLTreeSet<K> extends AbstractObjectSortedSet<K> implements Serializable, Cloneable, ObjectSortedSet<K> {
   protected transient ObjectAVLTreeSet.Entry<K> tree;
   protected int count;
   protected transient ObjectAVLTreeSet.Entry<K> firstEntry;
   protected transient ObjectAVLTreeSet.Entry<K> lastEntry;
   protected Comparator<? super K> storedComparator;
   protected transient Comparator<? super K> actualComparator;
   private static final long serialVersionUID = -7046029254386353130L;
   private transient boolean[] dirPath;

   public ObjectAVLTreeSet() {
      this.allocatePaths();
      this.tree = null;
      this.count = 0;
   }

   private void setActualComparator() {
      this.actualComparator = this.storedComparator;
   }

   public ObjectAVLTreeSet(Comparator<? super K> c) {
      this();
      this.storedComparator = c;
      this.setActualComparator();
   }

   public ObjectAVLTreeSet(Collection<? extends K> c) {
      this();
      this.addAll(c);
   }

   public ObjectAVLTreeSet(SortedSet<K> s) {
      this(s.comparator());
      this.addAll(s);
   }

   public ObjectAVLTreeSet(ObjectCollection<? extends K> c) {
      this();
      this.addAll(c);
   }

   public ObjectAVLTreeSet(ObjectSortedSet<K> s) {
      this(s.comparator());
      this.addAll(s);
   }

   public ObjectAVLTreeSet(Iterator<? extends K> i) {
      this.allocatePaths();

      while (i.hasNext()) {
         this.add((K)i.next());
      }
   }

   public ObjectAVLTreeSet(K[] a, int offset, int length, Comparator<? super K> c) {
      this(c);
      ObjectArrays.ensureOffsetLength(a, offset, length);

      for (int i = 0; i < length; i++) {
         this.add(a[offset + i]);
      }
   }

   public ObjectAVLTreeSet(K[] a, int offset, int length) {
      this(a, offset, length, null);
   }

   public ObjectAVLTreeSet(K[] a) {
      this();
      int i = a.length;

      while (i-- != 0) {
         this.add(a[i]);
      }
   }

   public ObjectAVLTreeSet(K[] a, Comparator<? super K> c) {
      this(c);
      int i = a.length;

      while (i-- != 0) {
         this.add(a[i]);
      }
   }

   final int compare(K k1, K k2) {
      return this.actualComparator == null ? ((Comparable)k1).compareTo(k2) : this.actualComparator.compare(k1, k2);
   }

   private ObjectAVLTreeSet.Entry<K> findKey(K k) {
      ObjectAVLTreeSet.Entry<K> e = this.tree;

      int cmp;
      while (e != null && (cmp = this.compare(k, e.key)) != 0) {
         e = cmp < 0 ? e.left() : e.right();
      }

      return e;
   }

   final ObjectAVLTreeSet.Entry<K> locateKey(K k) {
      ObjectAVLTreeSet.Entry<K> e = this.tree;
      ObjectAVLTreeSet.Entry<K> last = this.tree;

      int cmp;
      for (cmp = 0; e != null && (cmp = this.compare(k, e.key)) != 0; e = cmp < 0 ? e.left() : e.right()) {
         last = e;
      }

      return cmp == 0 ? e : last;
   }

   private void allocatePaths() {
      this.dirPath = new boolean[48];
   }

   @Override
   public boolean add(K k) {
      Objects.requireNonNull(k);
      if (this.tree == null) {
         this.count++;
         this.tree = this.lastEntry = this.firstEntry = new ObjectAVLTreeSet.Entry<>(k);
      } else {
         ObjectAVLTreeSet.Entry<K> p = this.tree;
         ObjectAVLTreeSet.Entry<K> q = null;
         ObjectAVLTreeSet.Entry<K> y = this.tree;
         ObjectAVLTreeSet.Entry<K> z = null;
         ObjectAVLTreeSet.Entry<K> e = null;
         ObjectAVLTreeSet.Entry<K> w = null;
         int i = 0;

         while (true) {
            int cmp;
            if ((cmp = this.compare(k, p.key)) == 0) {
               return false;
            }

            if (p.balance() != 0) {
               i = 0;
               z = q;
               y = p;
            }

            if (this.dirPath[i++] = cmp > 0) {
               if (p.succ()) {
                  this.count++;
                  e = new ObjectAVLTreeSet.Entry<>(k);
                  if (p.right == null) {
                     this.lastEntry = e;
                  }

                  e.left = p;
                  e.right = p.right;
                  p.right(e);
                  break;
               }

               q = p;
               p = p.right;
            } else {
               if (p.pred()) {
                  this.count++;
                  e = new ObjectAVLTreeSet.Entry<>(k);
                  if (p.left == null) {
                     this.firstEntry = e;
                  }

                  e.right = p;
                  e.left = p.left;
                  p.left(e);
                  break;
               }

               q = p;
               p = p.left;
            }
         }

         p = y;

         for (int var14 = 0; p != e; p = this.dirPath[var14++] ? p.right : p.left) {
            if (this.dirPath[var14]) {
               p.incBalance();
            } else {
               p.decBalance();
            }
         }

         if (y.balance() == -2) {
            ObjectAVLTreeSet.Entry<K> x = y.left;
            if (x.balance() == -1) {
               w = x;
               if (x.succ()) {
                  x.succ(false);
                  y.pred(x);
               } else {
                  y.left = x.right;
               }

               x.right = y;
               x.balance(0);
               y.balance(0);
            } else {
               assert x.balance() == 1;

               w = x.right;
               x.right = w.left;
               w.left = x;
               y.left = w.right;
               w.right = y;
               if (w.balance() == -1) {
                  x.balance(0);
                  y.balance(1);
               } else if (w.balance() == 0) {
                  x.balance(0);
                  y.balance(0);
               } else {
                  x.balance(-1);
                  y.balance(0);
               }

               w.balance(0);
               if (w.pred()) {
                  x.succ(w);
                  w.pred(false);
               }

               if (w.succ()) {
                  y.pred(w);
                  w.succ(false);
               }
            }
         } else {
            if (y.balance() != 2) {
               return true;
            }

            ObjectAVLTreeSet.Entry<K> x = y.right;
            if (x.balance() == 1) {
               w = x;
               if (x.pred()) {
                  x.pred(false);
                  y.succ(x);
               } else {
                  y.right = x.left;
               }

               x.left = y;
               x.balance(0);
               y.balance(0);
            } else {
               assert x.balance() == -1;

               w = x.left;
               x.left = w.right;
               w.right = x;
               y.right = w.left;
               w.left = y;
               if (w.balance() == 1) {
                  x.balance(0);
                  y.balance(-1);
               } else if (w.balance() == 0) {
                  x.balance(0);
                  y.balance(0);
               } else {
                  x.balance(1);
                  y.balance(0);
               }

               w.balance(0);
               if (w.pred()) {
                  y.succ(w);
                  w.pred(false);
               }

               if (w.succ()) {
                  x.pred(w);
                  w.succ(false);
               }
            }
         }

         if (z == null) {
            this.tree = w;
         } else if (z.left == y) {
            z.left = w;
         } else {
            z.right = w;
         }
      }

      return true;
   }

   private ObjectAVLTreeSet.Entry<K> parent(ObjectAVLTreeSet.Entry<K> e) {
      if (e == this.tree) {
         return null;
      } else {
         ObjectAVLTreeSet.Entry<K> y = e;

         ObjectAVLTreeSet.Entry<K> x;
         for (x = e; !y.succ(); y = y.right) {
            if (x.pred()) {
               ObjectAVLTreeSet.Entry<K> p = x.left;
               if (p == null || p.right != e) {
                  while (!y.succ()) {
                     y = y.right;
                  }

                  p = y.right;
               }

               return p;
            }

            x = x.left;
         }

         ObjectAVLTreeSet.Entry<K> p = y.right;
         if (p == null || p.left != e) {
            while (!x.pred()) {
               x = x.left;
            }

            p = x.left;
         }

         return p;
      }
   }

   @Override
   public boolean remove(Object k) {
      if (this.tree == null) {
         return false;
      } else {
         ObjectAVLTreeSet.Entry<K> p = this.tree;
         ObjectAVLTreeSet.Entry<K> q = null;
         boolean dir = false;
         K kk = (K)k;

         int cmp;
         while ((cmp = this.compare(kk, p.key)) != 0) {
            if (dir = cmp > 0) {
               q = p;
               if ((p = p.right()) == null) {
                  return false;
               }
            } else {
               q = p;
               if ((p = p.left()) == null) {
                  return false;
               }
            }
         }

         if (p.left == null) {
            this.firstEntry = p.next();
         }

         if (p.right == null) {
            this.lastEntry = p.prev();
         }

         if (p.succ()) {
            if (p.pred()) {
               if (q != null) {
                  if (dir) {
                     q.succ(p.right);
                  } else {
                     q.pred(p.left);
                  }
               } else {
                  this.tree = dir ? p.right : p.left;
               }
            } else {
               p.prev().right = p.right;
               if (q != null) {
                  if (dir) {
                     q.right = p.left;
                  } else {
                     q.left = p.left;
                  }
               } else {
                  this.tree = p.left;
               }
            }
         } else {
            ObjectAVLTreeSet.Entry<K> r = p.right;
            if (r.pred()) {
               r.left = p.left;
               r.pred(p.pred());
               if (!r.pred()) {
                  r.prev().right = r;
               }

               if (q != null) {
                  if (dir) {
                     q.right = r;
                  } else {
                     q.left = r;
                  }
               } else {
                  this.tree = r;
               }

               r.balance(p.balance());
               q = r;
               dir = true;
            } else {
               while (true) {
                  ObjectAVLTreeSet.Entry<K> s = r.left;
                  if (s.pred()) {
                     if (s.succ()) {
                        r.pred(s);
                     } else {
                        r.left = s.right;
                     }

                     s.left = p.left;
                     if (!p.pred()) {
                        p.prev().right = s;
                        s.pred(false);
                     }

                     s.right = p.right;
                     s.succ(false);
                     if (q != null) {
                        if (dir) {
                           q.right = s;
                        } else {
                           q.left = s;
                        }
                     } else {
                        this.tree = s;
                     }

                     s.balance(p.balance());
                     q = r;
                     dir = false;
                     break;
                  }

                  r = s;
               }
            }
         }

         while (q != null) {
            ObjectAVLTreeSet.Entry<K> y = q;
            q = this.parent(q);
            if (!dir) {
               dir = q != null && q.left != y;
               y.incBalance();
               if (y.balance() == 1) {
                  break;
               }

               if (y.balance() == 2) {
                  ObjectAVLTreeSet.Entry<K> x = y.right;

                  assert x != null;

                  if (x.balance() == -1) {
                     assert x.balance() == -1;

                     ObjectAVLTreeSet.Entry<K> w = x.left;
                     x.left = w.right;
                     w.right = x;
                     y.right = w.left;
                     w.left = y;
                     if (w.balance() == 1) {
                        x.balance(0);
                        y.balance(-1);
                     } else if (w.balance() == 0) {
                        x.balance(0);
                        y.balance(0);
                     } else {
                        assert w.balance() == -1;

                        x.balance(1);
                        y.balance(0);
                     }

                     w.balance(0);
                     if (w.pred()) {
                        y.succ(w);
                        w.pred(false);
                     }

                     if (w.succ()) {
                        x.pred(w);
                        w.succ(false);
                     }

                     if (q != null) {
                        if (dir) {
                           q.right = w;
                        } else {
                           q.left = w;
                        }
                     } else {
                        this.tree = w;
                     }
                  } else {
                     if (q != null) {
                        if (dir) {
                           q.right = x;
                        } else {
                           q.left = x;
                        }
                     } else {
                        this.tree = x;
                     }

                     if (x.balance() == 0) {
                        y.right = x.left;
                        x.left = y;
                        x.balance(-1);
                        y.balance(1);
                        break;
                     }

                     assert x.balance() == 1;

                     if (x.pred()) {
                        y.succ(true);
                        x.pred(false);
                     } else {
                        y.right = x.left;
                     }

                     x.left = y;
                     y.balance(0);
                     x.balance(0);
                  }
               }
            } else {
               dir = q != null && q.left != y;
               y.decBalance();
               if (y.balance() == -1) {
                  break;
               }

               if (y.balance() == -2) {
                  ObjectAVLTreeSet.Entry<K> xx = y.left;

                  assert xx != null;

                  if (xx.balance() == 1) {
                     assert xx.balance() == 1;

                     ObjectAVLTreeSet.Entry<K> wx = xx.right;
                     xx.right = wx.left;
                     wx.left = xx;
                     y.left = wx.right;
                     wx.right = y;
                     if (wx.balance() == -1) {
                        xx.balance(0);
                        y.balance(1);
                     } else if (wx.balance() == 0) {
                        xx.balance(0);
                        y.balance(0);
                     } else {
                        assert wx.balance() == 1;

                        xx.balance(-1);
                        y.balance(0);
                     }

                     wx.balance(0);
                     if (wx.pred()) {
                        xx.succ(wx);
                        wx.pred(false);
                     }

                     if (wx.succ()) {
                        y.pred(wx);
                        wx.succ(false);
                     }

                     if (q != null) {
                        if (dir) {
                           q.right = wx;
                        } else {
                           q.left = wx;
                        }
                     } else {
                        this.tree = wx;
                     }
                  } else {
                     if (q != null) {
                        if (dir) {
                           q.right = xx;
                        } else {
                           q.left = xx;
                        }
                     } else {
                        this.tree = xx;
                     }

                     if (xx.balance() == 0) {
                        y.left = xx.right;
                        xx.right = y;
                        xx.balance(1);
                        y.balance(-1);
                        break;
                     }

                     assert xx.balance() == -1;

                     if (xx.succ()) {
                        y.pred(true);
                        xx.succ(false);
                     } else {
                        y.left = xx.right;
                     }

                     xx.right = y;
                     y.balance(0);
                     xx.balance(0);
                  }
               }
            }
         }

         this.count--;
         return true;
      }
   }

   @Override
   public boolean contains(Object k) {
      return this.findKey((K)k) != null;
   }

   public K get(Object k) {
      ObjectAVLTreeSet.Entry<K> entry = this.findKey((K)k);
      return entry == null ? null : entry.key;
   }

   @Override
   public void clear() {
      this.count = 0;
      this.tree = null;
      this.firstEntry = this.lastEntry = null;
   }

   @Override
   public int size() {
      return this.count;
   }

   @Override
   public boolean isEmpty() {
      return this.count == 0;
   }

   @Override
   public K first() {
      if (this.tree == null) {
         throw new NoSuchElementException();
      } else {
         return this.firstEntry.key;
      }
   }

   @Override
   public K last() {
      if (this.tree == null) {
         throw new NoSuchElementException();
      } else {
         return this.lastEntry.key;
      }
   }

   @Override
   public ObjectBidirectionalIterator<K> iterator() {
      return new ObjectAVLTreeSet.SetIterator();
   }

   @Override
   public ObjectBidirectionalIterator<K> iterator(K from) {
      return new ObjectAVLTreeSet.SetIterator(from);
   }

   @Override
   public Comparator<? super K> comparator() {
      return this.actualComparator;
   }

   @Override
   public ObjectSortedSet<K> headSet(K to) {
      return new ObjectAVLTreeSet.Subset(null, true, to, false);
   }

   @Override
   public ObjectSortedSet<K> tailSet(K from) {
      return new ObjectAVLTreeSet.Subset(from, false, null, true);
   }

   @Override
   public ObjectSortedSet<K> subSet(K from, K to) {
      return new ObjectAVLTreeSet.Subset(from, false, to, false);
   }

   @Override
   public Object clone() {
      ObjectAVLTreeSet<K> c;
      try {
         c = (ObjectAVLTreeSet<K>)super.clone();
      } catch (CloneNotSupportedException var7) {
         throw new InternalError();
      }

      c.allocatePaths();
      if (this.count == 0) {
         return c;
      } else {
         ObjectAVLTreeSet.Entry<K> rp = new ObjectAVLTreeSet.Entry<>();
         ObjectAVLTreeSet.Entry<K> rq = new ObjectAVLTreeSet.Entry<>();
         ObjectAVLTreeSet.Entry<K> p = rp;
         rp.left(this.tree);
         ObjectAVLTreeSet.Entry<K> q = rq;
         rq.pred(null);

         while (true) {
            if (!p.pred()) {
               ObjectAVLTreeSet.Entry<K> e = p.left.clone();
               e.pred(q.left);
               e.succ(q);
               q.left(e);
               p = p.left;
               q = q.left;
            } else {
               while (p.succ()) {
                  p = p.right;
                  if (p == null) {
                     q.right = null;
                     c.tree = rq.left;
                     c.firstEntry = c.tree;

                     while (c.firstEntry.left != null) {
                        c.firstEntry = c.firstEntry.left;
                     }

                     c.lastEntry = c.tree;

                     while (c.lastEntry.right != null) {
                        c.lastEntry = c.lastEntry.right;
                     }

                     return c;
                  }

                  q = q.right;
               }

               p = p.right;
               q = q.right;
            }

            if (!p.succ()) {
               ObjectAVLTreeSet.Entry<K> e = p.right.clone();
               e.succ(q.right);
               e.pred(q);
               q.right(e);
            }
         }
      }
   }

   private void writeObject(ObjectOutputStream s) throws IOException {
      int n = this.count;
      ObjectAVLTreeSet<K>.SetIterator i = new ObjectAVLTreeSet.SetIterator();
      s.defaultWriteObject();

      while (n-- != 0) {
         s.writeObject(i.next());
      }
   }

   private ObjectAVLTreeSet.Entry<K> readTree(ObjectInputStream s, int n, ObjectAVLTreeSet.Entry<K> pred, ObjectAVLTreeSet.Entry<K> succ) throws IOException, ClassNotFoundException {
      if (n == 1) {
         ObjectAVLTreeSet.Entry<K> top = new ObjectAVLTreeSet.Entry<>((K)s.readObject());
         top.pred(pred);
         top.succ(succ);
         return top;
      } else if (n == 2) {
         ObjectAVLTreeSet.Entry<K> top = new ObjectAVLTreeSet.Entry<>((K)s.readObject());
         top.right(new ObjectAVLTreeSet.Entry<>((K)s.readObject()));
         top.right.pred(top);
         top.balance(1);
         top.pred(pred);
         top.right.succ(succ);
         return top;
      } else {
         int rightN = n / 2;
         int leftN = n - rightN - 1;
         ObjectAVLTreeSet.Entry<K> top = new ObjectAVLTreeSet.Entry<>();
         top.left(this.readTree(s, leftN, pred, top));
         top.key = (K)s.readObject();
         top.right(this.readTree(s, rightN, top, succ));
         if (n == (n & -n)) {
            top.balance(1);
         }

         return top;
      }
   }

   private void readObject(ObjectInputStream s) throws IOException, ClassNotFoundException {
      s.defaultReadObject();
      this.setActualComparator();
      this.allocatePaths();
      if (this.count != 0) {
         this.tree = this.readTree(s, this.count, null, null);
         ObjectAVLTreeSet.Entry<K> e = this.tree;

         while (e.left() != null) {
            e = e.left();
         }

         this.firstEntry = e;
         e = this.tree;

         while (e.right() != null) {
            e = e.right();
         }

         this.lastEntry = e;
      }
   }

   private static final class Entry<K> implements Cloneable {
      private static final int SUCC_MASK = Integer.MIN_VALUE;
      private static final int PRED_MASK = 1073741824;
      private static final int BALANCE_MASK = 255;
      K key;
      ObjectAVLTreeSet.Entry<K> left;
      ObjectAVLTreeSet.Entry<K> right;
      int info;

      Entry() {
      }

      Entry(K k) {
         this.key = k;
         this.info = -1073741824;
      }

      ObjectAVLTreeSet.Entry<K> left() {
         return (this.info & 1073741824) != 0 ? null : this.left;
      }

      ObjectAVLTreeSet.Entry<K> right() {
         return (this.info & -2147483648) != 0 ? null : this.right;
      }

      boolean pred() {
         return (this.info & 1073741824) != 0;
      }

      boolean succ() {
         return (this.info & -2147483648) != 0;
      }

      void pred(boolean pred) {
         if (pred) {
            this.info |= 1073741824;
         } else {
            this.info &= -1073741825;
         }
      }

      void succ(boolean succ) {
         if (succ) {
            this.info |= Integer.MIN_VALUE;
         } else {
            this.info &= Integer.MAX_VALUE;
         }
      }

      void pred(ObjectAVLTreeSet.Entry<K> pred) {
         this.info |= 1073741824;
         this.left = pred;
      }

      void succ(ObjectAVLTreeSet.Entry<K> succ) {
         this.info |= Integer.MIN_VALUE;
         this.right = succ;
      }

      void left(ObjectAVLTreeSet.Entry<K> left) {
         this.info &= -1073741825;
         this.left = left;
      }

      void right(ObjectAVLTreeSet.Entry<K> right) {
         this.info &= Integer.MAX_VALUE;
         this.right = right;
      }

      int balance() {
         return (byte)this.info;
      }

      void balance(int level) {
         this.info &= -256;
         this.info |= level & 0xFF;
      }

      void incBalance() {
         this.info = this.info & -256 | (byte)this.info + 1 & 0xFF;
      }

      protected void decBalance() {
         this.info = this.info & -256 | (byte)this.info - 1 & 0xFF;
      }

      ObjectAVLTreeSet.Entry<K> next() {
         ObjectAVLTreeSet.Entry<K> next = this.right;
         if ((this.info & -2147483648) == 0) {
            while ((next.info & 1073741824) == 0) {
               next = next.left;
            }
         }

         return next;
      }

      ObjectAVLTreeSet.Entry<K> prev() {
         ObjectAVLTreeSet.Entry<K> prev = this.left;
         if ((this.info & 1073741824) == 0) {
            while ((prev.info & -2147483648) == 0) {
               prev = prev.right;
            }
         }

         return prev;
      }

      public ObjectAVLTreeSet.Entry<K> clone() {
         ObjectAVLTreeSet.Entry<K> c;
         try {
            c = (ObjectAVLTreeSet.Entry<K>)super.clone();
         } catch (CloneNotSupportedException var3) {
            throw new InternalError();
         }

         c.key = this.key;
         c.info = this.info;
         return c;
      }

      @Override
      public boolean equals(Object o) {
         if (!(o instanceof ObjectAVLTreeSet.Entry)) {
            return false;
         } else {
            ObjectAVLTreeSet.Entry<?> e = (ObjectAVLTreeSet.Entry<?>)o;
            return Objects.equals(this.key, e.key);
         }
      }

      @Override
      public int hashCode() {
         return this.key.hashCode();
      }

      @Override
      public String toString() {
         return String.valueOf(this.key);
      }
   }

   private class SetIterator implements ObjectListIterator<K> {
      ObjectAVLTreeSet.Entry<K> prev;
      ObjectAVLTreeSet.Entry<K> next;
      ObjectAVLTreeSet.Entry<K> curr;
      int index = 0;

      SetIterator() {
         this.next = ObjectAVLTreeSet.this.firstEntry;
      }

      SetIterator(K k) {
         if ((this.next = ObjectAVLTreeSet.this.locateKey(k)) != null) {
            if (ObjectAVLTreeSet.this.compare(this.next.key, k) <= 0) {
               this.prev = this.next;
               this.next = this.next.next();
            } else {
               this.prev = this.next.prev();
            }
         }
      }

      @Override
      public boolean hasNext() {
         return this.next != null;
      }

      @Override
      public boolean hasPrevious() {
         return this.prev != null;
      }

      void updateNext() {
         this.next = this.next.next();
      }

      ObjectAVLTreeSet.Entry<K> nextEntry() {
         if (!this.hasNext()) {
            throw new NoSuchElementException();
         } else {
            this.curr = this.prev = this.next;
            this.index++;
            this.updateNext();
            return this.curr;
         }
      }

      @Override
      public K next() {
         return this.nextEntry().key;
      }

      @Override
      public K previous() {
         return this.previousEntry().key;
      }

      void updatePrevious() {
         this.prev = this.prev.prev();
      }

      ObjectAVLTreeSet.Entry<K> previousEntry() {
         if (!this.hasPrevious()) {
            throw new NoSuchElementException();
         } else {
            this.curr = this.next = this.prev;
            this.index--;
            this.updatePrevious();
            return this.curr;
         }
      }

      @Override
      public int nextIndex() {
         return this.index;
      }

      @Override
      public int previousIndex() {
         return this.index - 1;
      }

      @Override
      public void remove() {
         if (this.curr == null) {
            throw new IllegalStateException();
         } else {
            if (this.curr == this.prev) {
               this.index--;
            }

            this.next = this.prev = this.curr;
            this.updatePrevious();
            this.updateNext();
            ObjectAVLTreeSet.this.remove(this.curr.key);
            this.curr = null;
         }
      }
   }

   private final class Subset extends AbstractObjectSortedSet<K> implements Serializable, ObjectSortedSet<K> {
      private static final long serialVersionUID = -7046029254386353129L;
      K from;
      K to;
      boolean bottom;
      boolean top;

      public Subset(K from, boolean bottom, K to, boolean top) {
         if (!bottom && !top && ObjectAVLTreeSet.this.compare(from, to) > 0) {
            throw new IllegalArgumentException("Start element (" + from + ") is larger than end element (" + to + ")");
         } else {
            this.from = from;
            this.bottom = bottom;
            this.to = to;
            this.top = top;
         }
      }

      @Override
      public void clear() {
         ObjectAVLTreeSet<K>.Subset.SubsetIterator i = new ObjectAVLTreeSet.Subset.SubsetIterator();

         while (i.hasNext()) {
            i.next();
            i.remove();
         }
      }

      final boolean in(K k) {
         return (this.bottom || ObjectAVLTreeSet.this.compare(k, this.from) >= 0) && (this.top || ObjectAVLTreeSet.this.compare(k, this.to) < 0);
      }

      @Override
      public boolean contains(Object k) {
         return this.in((K)k) && ObjectAVLTreeSet.this.contains(k);
      }

      @Override
      public boolean add(K k) {
         if (!this.in(k)) {
            throw new IllegalArgumentException(
               "Element (" + k + ") out of range [" + (this.bottom ? "-" : String.valueOf(this.from)) + ", " + (this.top ? "-" : String.valueOf(this.to)) + ")"
            );
         } else {
            return ObjectAVLTreeSet.this.add(k);
         }
      }

      @Override
      public boolean remove(Object k) {
         return !this.in((K)k) ? false : ObjectAVLTreeSet.this.remove(k);
      }

      @Override
      public int size() {
         ObjectAVLTreeSet<K>.Subset.SubsetIterator i = new ObjectAVLTreeSet.Subset.SubsetIterator();
         int n = 0;

         while (i.hasNext()) {
            n++;
            i.next();
         }

         return n;
      }

      @Override
      public boolean isEmpty() {
         return !new ObjectAVLTreeSet.Subset.SubsetIterator().hasNext();
      }

      @Override
      public Comparator<? super K> comparator() {
         return ObjectAVLTreeSet.this.actualComparator;
      }

      @Override
      public ObjectBidirectionalIterator<K> iterator() {
         return new ObjectAVLTreeSet.Subset.SubsetIterator();
      }

      @Override
      public ObjectBidirectionalIterator<K> iterator(K from) {
         return new ObjectAVLTreeSet.Subset.SubsetIterator(from);
      }

      @Override
      public ObjectSortedSet<K> headSet(K to) {
         if (this.top) {
            return ObjectAVLTreeSet.this.new Subset(this.from, this.bottom, to, false);
         } else {
            return ObjectAVLTreeSet.this.compare(to, this.to) < 0 ? ObjectAVLTreeSet.this.new Subset(this.from, this.bottom, to, false) : this;
         }
      }

      @Override
      public ObjectSortedSet<K> tailSet(K from) {
         if (this.bottom) {
            return ObjectAVLTreeSet.this.new Subset(from, false, this.to, this.top);
         } else {
            return ObjectAVLTreeSet.this.compare(from, this.from) > 0 ? ObjectAVLTreeSet.this.new Subset(from, false, this.to, this.top) : this;
         }
      }

      @Override
      public ObjectSortedSet<K> subSet(K from, K to) {
         if (this.top && this.bottom) {
            return ObjectAVLTreeSet.this.new Subset(from, false, to, false);
         } else {
            if (!this.top) {
               to = ObjectAVLTreeSet.this.compare(to, this.to) < 0 ? to : this.to;
            }

            if (!this.bottom) {
               from = ObjectAVLTreeSet.this.compare(from, this.from) > 0 ? from : this.from;
            }

            return !this.top && !this.bottom && from == this.from && to == this.to ? this : ObjectAVLTreeSet.this.new Subset(from, false, to, false);
         }
      }

      public ObjectAVLTreeSet.Entry<K> firstEntry() {
         if (ObjectAVLTreeSet.this.tree == null) {
            return null;
         } else {
            ObjectAVLTreeSet.Entry<K> e;
            if (this.bottom) {
               e = ObjectAVLTreeSet.this.firstEntry;
            } else {
               e = ObjectAVLTreeSet.this.locateKey(this.from);
               if (ObjectAVLTreeSet.this.compare(e.key, this.from) < 0) {
                  e = e.next();
               }
            }

            return e != null && (this.top || ObjectAVLTreeSet.this.compare(e.key, this.to) < 0) ? e : null;
         }
      }

      public ObjectAVLTreeSet.Entry<K> lastEntry() {
         if (ObjectAVLTreeSet.this.tree == null) {
            return null;
         } else {
            ObjectAVLTreeSet.Entry<K> e;
            if (this.top) {
               e = ObjectAVLTreeSet.this.lastEntry;
            } else {
               e = ObjectAVLTreeSet.this.locateKey(this.to);
               if (ObjectAVLTreeSet.this.compare(e.key, this.to) >= 0) {
                  e = e.prev();
               }
            }

            return e != null && (this.bottom || ObjectAVLTreeSet.this.compare(e.key, this.from) >= 0) ? e : null;
         }
      }

      @Override
      public K first() {
         ObjectAVLTreeSet.Entry<K> e = this.firstEntry();
         if (e == null) {
            throw new NoSuchElementException();
         } else {
            return e.key;
         }
      }

      @Override
      public K last() {
         ObjectAVLTreeSet.Entry<K> e = this.lastEntry();
         if (e == null) {
            throw new NoSuchElementException();
         } else {
            return e.key;
         }
      }

      private final class SubsetIterator extends ObjectAVLTreeSet<K>.SetIterator {
         SubsetIterator() {
            this.next = Subset.this.firstEntry();
         }

         SubsetIterator(K k) {
            this();
            if (this.next != null) {
               if (!Subset.this.bottom && ObjectAVLTreeSet.this.compare(k, this.next.key) < 0) {
                  this.prev = null;
               } else if (!Subset.this.top && ObjectAVLTreeSet.this.compare(k, (this.prev = Subset.this.lastEntry()).key) >= 0) {
                  this.next = null;
               } else {
                  this.next = ObjectAVLTreeSet.this.locateKey(k);
                  if (ObjectAVLTreeSet.this.compare(this.next.key, k) <= 0) {
                     this.prev = this.next;
                     this.next = this.next.next();
                  } else {
                     this.prev = this.next.prev();
                  }
               }
            }
         }

         @Override
         void updatePrevious() {
            this.prev = this.prev.prev();
            if (!Subset.this.bottom && this.prev != null && ObjectAVLTreeSet.this.compare(this.prev.key, Subset.this.from) < 0) {
               this.prev = null;
            }
         }

         @Override
         void updateNext() {
            this.next = this.next.next();
            if (!Subset.this.top && this.next != null && ObjectAVLTreeSet.this.compare(this.next.key, Subset.this.to) >= 0) {
               this.next = null;
            }
         }
      }
   }
}
