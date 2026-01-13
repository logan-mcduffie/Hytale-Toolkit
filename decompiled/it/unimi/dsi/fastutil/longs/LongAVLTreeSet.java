package it.unimi.dsi.fastutil.longs;

import it.unimi.dsi.fastutil.HashCommon;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.SortedSet;

public class LongAVLTreeSet extends AbstractLongSortedSet implements Serializable, Cloneable, LongSortedSet {
   protected transient LongAVLTreeSet.Entry tree;
   protected int count;
   protected transient LongAVLTreeSet.Entry firstEntry;
   protected transient LongAVLTreeSet.Entry lastEntry;
   protected Comparator<? super Long> storedComparator;
   protected transient LongComparator actualComparator;
   private static final long serialVersionUID = -7046029254386353130L;
   private transient boolean[] dirPath;

   public LongAVLTreeSet() {
      this.allocatePaths();
      this.tree = null;
      this.count = 0;
   }

   private void setActualComparator() {
      this.actualComparator = LongComparators.asLongComparator(this.storedComparator);
   }

   public LongAVLTreeSet(Comparator<? super Long> c) {
      this();
      this.storedComparator = c;
      this.setActualComparator();
   }

   public LongAVLTreeSet(Collection<? extends Long> c) {
      this();
      this.addAll(c);
   }

   public LongAVLTreeSet(SortedSet<Long> s) {
      this(s.comparator());
      this.addAll(s);
   }

   public LongAVLTreeSet(LongCollection c) {
      this();
      this.addAll(c);
   }

   public LongAVLTreeSet(LongSortedSet s) {
      this(s.comparator());
      this.addAll(s);
   }

   public LongAVLTreeSet(LongIterator i) {
      this.allocatePaths();

      while (i.hasNext()) {
         this.add(i.nextLong());
      }
   }

   public LongAVLTreeSet(Iterator<?> i) {
      this(LongIterators.asLongIterator(i));
   }

   public LongAVLTreeSet(long[] a, int offset, int length, Comparator<? super Long> c) {
      this(c);
      LongArrays.ensureOffsetLength(a, offset, length);

      for (int i = 0; i < length; i++) {
         this.add(a[offset + i]);
      }
   }

   public LongAVLTreeSet(long[] a, int offset, int length) {
      this(a, offset, length, null);
   }

   public LongAVLTreeSet(long[] a) {
      this();
      int i = a.length;

      while (i-- != 0) {
         this.add(a[i]);
      }
   }

   public LongAVLTreeSet(long[] a, Comparator<? super Long> c) {
      this(c);
      int i = a.length;

      while (i-- != 0) {
         this.add(a[i]);
      }
   }

   final int compare(long k1, long k2) {
      return this.actualComparator == null ? Long.compare(k1, k2) : this.actualComparator.compare(k1, k2);
   }

   private LongAVLTreeSet.Entry findKey(long k) {
      LongAVLTreeSet.Entry e = this.tree;

      int cmp;
      while (e != null && (cmp = this.compare(k, e.key)) != 0) {
         e = cmp < 0 ? e.left() : e.right();
      }

      return e;
   }

   final LongAVLTreeSet.Entry locateKey(long k) {
      LongAVLTreeSet.Entry e = this.tree;
      LongAVLTreeSet.Entry last = this.tree;

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
   public boolean add(long k) {
      if (this.tree == null) {
         this.count++;
         this.tree = this.lastEntry = this.firstEntry = new LongAVLTreeSet.Entry(k);
      } else {
         LongAVLTreeSet.Entry p = this.tree;
         LongAVLTreeSet.Entry q = null;
         LongAVLTreeSet.Entry y = this.tree;
         LongAVLTreeSet.Entry z = null;
         LongAVLTreeSet.Entry e = null;
         LongAVLTreeSet.Entry w = null;
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
                  e = new LongAVLTreeSet.Entry(k);
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
                  e = new LongAVLTreeSet.Entry(k);
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

         for (int var15 = 0; p != e; p = this.dirPath[var15++] ? p.right : p.left) {
            if (this.dirPath[var15]) {
               p.incBalance();
            } else {
               p.decBalance();
            }
         }

         if (y.balance() == -2) {
            LongAVLTreeSet.Entry x = y.left;
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

            LongAVLTreeSet.Entry x = y.right;
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

   private LongAVLTreeSet.Entry parent(LongAVLTreeSet.Entry e) {
      if (e == this.tree) {
         return null;
      } else {
         LongAVLTreeSet.Entry y = e;

         LongAVLTreeSet.Entry x;
         for (x = e; !y.succ(); y = y.right) {
            if (x.pred()) {
               LongAVLTreeSet.Entry p = x.left;
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

         LongAVLTreeSet.Entry p = y.right;
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
   public boolean remove(long k) {
      if (this.tree == null) {
         return false;
      } else {
         LongAVLTreeSet.Entry p = this.tree;
         LongAVLTreeSet.Entry q = null;
         boolean dir = false;
         long kk = k;

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
            LongAVLTreeSet.Entry r = p.right;
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
                  LongAVLTreeSet.Entry s = r.left;
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
            LongAVLTreeSet.Entry y = q;
            q = this.parent(q);
            if (!dir) {
               dir = q != null && q.left != y;
               y.incBalance();
               if (y.balance() == 1) {
                  break;
               }

               if (y.balance() == 2) {
                  LongAVLTreeSet.Entry x = y.right;

                  assert x != null;

                  if (x.balance() == -1) {
                     assert x.balance() == -1;

                     LongAVLTreeSet.Entry w = x.left;
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
                  LongAVLTreeSet.Entry xx = y.left;

                  assert xx != null;

                  if (xx.balance() == 1) {
                     assert xx.balance() == 1;

                     LongAVLTreeSet.Entry wx = xx.right;
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
   public boolean contains(long k) {
      return this.findKey(k) != null;
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
   public long firstLong() {
      if (this.tree == null) {
         throw new NoSuchElementException();
      } else {
         return this.firstEntry.key;
      }
   }

   @Override
   public long lastLong() {
      if (this.tree == null) {
         throw new NoSuchElementException();
      } else {
         return this.lastEntry.key;
      }
   }

   @Override
   public LongBidirectionalIterator iterator() {
      return new LongAVLTreeSet.SetIterator();
   }

   @Override
   public LongBidirectionalIterator iterator(long from) {
      return new LongAVLTreeSet.SetIterator(from);
   }

   @Override
   public LongComparator comparator() {
      return this.actualComparator;
   }

   @Override
   public LongSortedSet headSet(long to) {
      return new LongAVLTreeSet.Subset(0L, true, to, false);
   }

   @Override
   public LongSortedSet tailSet(long from) {
      return new LongAVLTreeSet.Subset(from, false, 0L, true);
   }

   @Override
   public LongSortedSet subSet(long from, long to) {
      return new LongAVLTreeSet.Subset(from, false, to, false);
   }

   @Override
   public Object clone() {
      LongAVLTreeSet c;
      try {
         c = (LongAVLTreeSet)super.clone();
      } catch (CloneNotSupportedException var7) {
         throw new InternalError();
      }

      c.allocatePaths();
      if (this.count == 0) {
         return c;
      } else {
         LongAVLTreeSet.Entry rp = new LongAVLTreeSet.Entry();
         LongAVLTreeSet.Entry rq = new LongAVLTreeSet.Entry();
         LongAVLTreeSet.Entry p = rp;
         rp.left(this.tree);
         LongAVLTreeSet.Entry q = rq;
         rq.pred(null);

         while (true) {
            if (!p.pred()) {
               LongAVLTreeSet.Entry e = p.left.clone();
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
               LongAVLTreeSet.Entry e = p.right.clone();
               e.succ(q.right);
               e.pred(q);
               q.right(e);
            }
         }
      }
   }

   private void writeObject(ObjectOutputStream s) throws IOException {
      int n = this.count;
      LongAVLTreeSet.SetIterator i = new LongAVLTreeSet.SetIterator();
      s.defaultWriteObject();

      while (n-- != 0) {
         s.writeLong(i.nextLong());
      }
   }

   private LongAVLTreeSet.Entry readTree(ObjectInputStream s, int n, LongAVLTreeSet.Entry pred, LongAVLTreeSet.Entry succ) throws IOException, ClassNotFoundException {
      if (n == 1) {
         LongAVLTreeSet.Entry top = new LongAVLTreeSet.Entry(s.readLong());
         top.pred(pred);
         top.succ(succ);
         return top;
      } else if (n == 2) {
         LongAVLTreeSet.Entry top = new LongAVLTreeSet.Entry(s.readLong());
         top.right(new LongAVLTreeSet.Entry(s.readLong()));
         top.right.pred(top);
         top.balance(1);
         top.pred(pred);
         top.right.succ(succ);
         return top;
      } else {
         int rightN = n / 2;
         int leftN = n - rightN - 1;
         LongAVLTreeSet.Entry top = new LongAVLTreeSet.Entry();
         top.left(this.readTree(s, leftN, pred, top));
         top.key = s.readLong();
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
         LongAVLTreeSet.Entry e = this.tree;

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

   private static final class Entry implements Cloneable {
      private static final int SUCC_MASK = Integer.MIN_VALUE;
      private static final int PRED_MASK = 1073741824;
      private static final int BALANCE_MASK = 255;
      long key;
      LongAVLTreeSet.Entry left;
      LongAVLTreeSet.Entry right;
      int info;

      Entry() {
      }

      Entry(long k) {
         this.key = k;
         this.info = -1073741824;
      }

      LongAVLTreeSet.Entry left() {
         return (this.info & 1073741824) != 0 ? null : this.left;
      }

      LongAVLTreeSet.Entry right() {
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

      void pred(LongAVLTreeSet.Entry pred) {
         this.info |= 1073741824;
         this.left = pred;
      }

      void succ(LongAVLTreeSet.Entry succ) {
         this.info |= Integer.MIN_VALUE;
         this.right = succ;
      }

      void left(LongAVLTreeSet.Entry left) {
         this.info &= -1073741825;
         this.left = left;
      }

      void right(LongAVLTreeSet.Entry right) {
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

      LongAVLTreeSet.Entry next() {
         LongAVLTreeSet.Entry next = this.right;
         if ((this.info & -2147483648) == 0) {
            while ((next.info & 1073741824) == 0) {
               next = next.left;
            }
         }

         return next;
      }

      LongAVLTreeSet.Entry prev() {
         LongAVLTreeSet.Entry prev = this.left;
         if ((this.info & 1073741824) == 0) {
            while ((prev.info & -2147483648) == 0) {
               prev = prev.right;
            }
         }

         return prev;
      }

      public LongAVLTreeSet.Entry clone() {
         LongAVLTreeSet.Entry c;
         try {
            c = (LongAVLTreeSet.Entry)super.clone();
         } catch (CloneNotSupportedException var3) {
            throw new InternalError();
         }

         c.key = this.key;
         c.info = this.info;
         return c;
      }

      @Override
      public boolean equals(Object o) {
         if (!(o instanceof LongAVLTreeSet.Entry)) {
            return false;
         } else {
            LongAVLTreeSet.Entry e = (LongAVLTreeSet.Entry)o;
            return this.key == e.key;
         }
      }

      @Override
      public int hashCode() {
         return HashCommon.long2int(this.key);
      }

      @Override
      public String toString() {
         return String.valueOf(this.key);
      }
   }

   private class SetIterator implements LongListIterator {
      LongAVLTreeSet.Entry prev;
      LongAVLTreeSet.Entry next;
      LongAVLTreeSet.Entry curr;
      int index = 0;

      SetIterator() {
         this.next = LongAVLTreeSet.this.firstEntry;
      }

      SetIterator(long k) {
         if ((this.next = LongAVLTreeSet.this.locateKey(k)) != null) {
            if (LongAVLTreeSet.this.compare(this.next.key, k) <= 0) {
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

      LongAVLTreeSet.Entry nextEntry() {
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
      public long nextLong() {
         return this.nextEntry().key;
      }

      @Override
      public long previousLong() {
         return this.previousEntry().key;
      }

      void updatePrevious() {
         this.prev = this.prev.prev();
      }

      LongAVLTreeSet.Entry previousEntry() {
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
            LongAVLTreeSet.this.remove(this.curr.key);
            this.curr = null;
         }
      }
   }

   private final class Subset extends AbstractLongSortedSet implements Serializable, LongSortedSet {
      private static final long serialVersionUID = -7046029254386353129L;
      long from;
      long to;
      boolean bottom;
      boolean top;

      public Subset(long from, boolean bottom, long to, boolean top) {
         if (!bottom && !top && LongAVLTreeSet.this.compare(from, to) > 0) {
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
         LongAVLTreeSet.Subset.SubsetIterator i = new LongAVLTreeSet.Subset.SubsetIterator();

         while (i.hasNext()) {
            i.nextLong();
            i.remove();
         }
      }

      final boolean in(long k) {
         return (this.bottom || LongAVLTreeSet.this.compare(k, this.from) >= 0) && (this.top || LongAVLTreeSet.this.compare(k, this.to) < 0);
      }

      @Override
      public boolean contains(long k) {
         return this.in(k) && LongAVLTreeSet.this.contains(k);
      }

      @Override
      public boolean add(long k) {
         if (!this.in(k)) {
            throw new IllegalArgumentException(
               "Element (" + k + ") out of range [" + (this.bottom ? "-" : String.valueOf(this.from)) + ", " + (this.top ? "-" : String.valueOf(this.to)) + ")"
            );
         } else {
            return LongAVLTreeSet.this.add(k);
         }
      }

      @Override
      public boolean remove(long k) {
         return !this.in(k) ? false : LongAVLTreeSet.this.remove(k);
      }

      @Override
      public int size() {
         LongAVLTreeSet.Subset.SubsetIterator i = new LongAVLTreeSet.Subset.SubsetIterator();
         int n = 0;

         while (i.hasNext()) {
            n++;
            i.nextLong();
         }

         return n;
      }

      @Override
      public boolean isEmpty() {
         return !new LongAVLTreeSet.Subset.SubsetIterator().hasNext();
      }

      @Override
      public LongComparator comparator() {
         return LongAVLTreeSet.this.actualComparator;
      }

      @Override
      public LongBidirectionalIterator iterator() {
         return new LongAVLTreeSet.Subset.SubsetIterator();
      }

      @Override
      public LongBidirectionalIterator iterator(long from) {
         return new LongAVLTreeSet.Subset.SubsetIterator(from);
      }

      @Override
      public LongSortedSet headSet(long to) {
         if (this.top) {
            return LongAVLTreeSet.this.new Subset(this.from, this.bottom, to, false);
         } else {
            return LongAVLTreeSet.this.compare(to, this.to) < 0 ? LongAVLTreeSet.this.new Subset(this.from, this.bottom, to, false) : this;
         }
      }

      @Override
      public LongSortedSet tailSet(long from) {
         if (this.bottom) {
            return LongAVLTreeSet.this.new Subset(from, false, this.to, this.top);
         } else {
            return LongAVLTreeSet.this.compare(from, this.from) > 0 ? LongAVLTreeSet.this.new Subset(from, false, this.to, this.top) : this;
         }
      }

      @Override
      public LongSortedSet subSet(long from, long to) {
         if (this.top && this.bottom) {
            return LongAVLTreeSet.this.new Subset(from, false, to, false);
         } else {
            if (!this.top) {
               to = LongAVLTreeSet.this.compare(to, this.to) < 0 ? to : this.to;
            }

            if (!this.bottom) {
               from = LongAVLTreeSet.this.compare(from, this.from) > 0 ? from : this.from;
            }

            return !this.top && !this.bottom && from == this.from && to == this.to ? this : LongAVLTreeSet.this.new Subset(from, false, to, false);
         }
      }

      public LongAVLTreeSet.Entry firstEntry() {
         if (LongAVLTreeSet.this.tree == null) {
            return null;
         } else {
            LongAVLTreeSet.Entry e;
            if (this.bottom) {
               e = LongAVLTreeSet.this.firstEntry;
            } else {
               e = LongAVLTreeSet.this.locateKey(this.from);
               if (LongAVLTreeSet.this.compare(e.key, this.from) < 0) {
                  e = e.next();
               }
            }

            return e != null && (this.top || LongAVLTreeSet.this.compare(e.key, this.to) < 0) ? e : null;
         }
      }

      public LongAVLTreeSet.Entry lastEntry() {
         if (LongAVLTreeSet.this.tree == null) {
            return null;
         } else {
            LongAVLTreeSet.Entry e;
            if (this.top) {
               e = LongAVLTreeSet.this.lastEntry;
            } else {
               e = LongAVLTreeSet.this.locateKey(this.to);
               if (LongAVLTreeSet.this.compare(e.key, this.to) >= 0) {
                  e = e.prev();
               }
            }

            return e != null && (this.bottom || LongAVLTreeSet.this.compare(e.key, this.from) >= 0) ? e : null;
         }
      }

      @Override
      public long firstLong() {
         LongAVLTreeSet.Entry e = this.firstEntry();
         if (e == null) {
            throw new NoSuchElementException();
         } else {
            return e.key;
         }
      }

      @Override
      public long lastLong() {
         LongAVLTreeSet.Entry e = this.lastEntry();
         if (e == null) {
            throw new NoSuchElementException();
         } else {
            return e.key;
         }
      }

      private final class SubsetIterator extends LongAVLTreeSet.SetIterator {
         SubsetIterator() {
            this.next = Subset.this.firstEntry();
         }

         SubsetIterator(long k) {
            this();
            if (this.next != null) {
               if (!Subset.this.bottom && LongAVLTreeSet.this.compare(k, this.next.key) < 0) {
                  this.prev = null;
               } else if (!Subset.this.top && LongAVLTreeSet.this.compare(k, (this.prev = Subset.this.lastEntry()).key) >= 0) {
                  this.next = null;
               } else {
                  this.next = LongAVLTreeSet.this.locateKey(k);
                  if (LongAVLTreeSet.this.compare(this.next.key, k) <= 0) {
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
            if (!Subset.this.bottom && this.prev != null && LongAVLTreeSet.this.compare(this.prev.key, Subset.this.from) < 0) {
               this.prev = null;
            }
         }

         @Override
         void updateNext() {
            this.next = this.next.next();
            if (!Subset.this.top && this.next != null && LongAVLTreeSet.this.compare(this.next.key, Subset.this.to) >= 0) {
               this.next = null;
            }
         }
      }
   }
}
