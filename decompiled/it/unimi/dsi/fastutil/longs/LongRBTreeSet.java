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

public class LongRBTreeSet extends AbstractLongSortedSet implements Serializable, Cloneable, LongSortedSet {
   protected transient LongRBTreeSet.Entry tree;
   protected int count;
   protected transient LongRBTreeSet.Entry firstEntry;
   protected transient LongRBTreeSet.Entry lastEntry;
   protected Comparator<? super Long> storedComparator;
   protected transient LongComparator actualComparator;
   private static final long serialVersionUID = -7046029254386353130L;
   private transient boolean[] dirPath;
   private transient LongRBTreeSet.Entry[] nodePath;

   public LongRBTreeSet() {
      this.allocatePaths();
      this.tree = null;
      this.count = 0;
   }

   private void setActualComparator() {
      this.actualComparator = LongComparators.asLongComparator(this.storedComparator);
   }

   public LongRBTreeSet(Comparator<? super Long> c) {
      this();
      this.storedComparator = c;
      this.setActualComparator();
   }

   public LongRBTreeSet(Collection<? extends Long> c) {
      this();
      this.addAll(c);
   }

   public LongRBTreeSet(SortedSet<Long> s) {
      this(s.comparator());
      this.addAll(s);
   }

   public LongRBTreeSet(LongCollection c) {
      this();
      this.addAll(c);
   }

   public LongRBTreeSet(LongSortedSet s) {
      this(s.comparator());
      this.addAll(s);
   }

   public LongRBTreeSet(LongIterator i) {
      this.allocatePaths();

      while (i.hasNext()) {
         this.add(i.nextLong());
      }
   }

   public LongRBTreeSet(Iterator<?> i) {
      this(LongIterators.asLongIterator(i));
   }

   public LongRBTreeSet(long[] a, int offset, int length, Comparator<? super Long> c) {
      this(c);
      LongArrays.ensureOffsetLength(a, offset, length);

      for (int i = 0; i < length; i++) {
         this.add(a[offset + i]);
      }
   }

   public LongRBTreeSet(long[] a, int offset, int length) {
      this(a, offset, length, null);
   }

   public LongRBTreeSet(long[] a) {
      this();
      int i = a.length;

      while (i-- != 0) {
         this.add(a[i]);
      }
   }

   public LongRBTreeSet(long[] a, Comparator<? super Long> c) {
      this(c);
      int i = a.length;

      while (i-- != 0) {
         this.add(a[i]);
      }
   }

   final int compare(long k1, long k2) {
      return this.actualComparator == null ? Long.compare(k1, k2) : this.actualComparator.compare(k1, k2);
   }

   private LongRBTreeSet.Entry findKey(long k) {
      LongRBTreeSet.Entry e = this.tree;

      int cmp;
      while (e != null && (cmp = this.compare(k, e.key)) != 0) {
         e = cmp < 0 ? e.left() : e.right();
      }

      return e;
   }

   final LongRBTreeSet.Entry locateKey(long k) {
      LongRBTreeSet.Entry e = this.tree;
      LongRBTreeSet.Entry last = this.tree;

      int cmp;
      for (cmp = 0; e != null && (cmp = this.compare(k, e.key)) != 0; e = cmp < 0 ? e.left() : e.right()) {
         last = e;
      }

      return cmp == 0 ? e : last;
   }

   private void allocatePaths() {
      this.dirPath = new boolean[64];
      this.nodePath = new LongRBTreeSet.Entry[64];
   }

   @Override
   public boolean add(long k) {
      int maxDepth = 0;
      if (this.tree == null) {
         this.count++;
         this.tree = this.lastEntry = this.firstEntry = new LongRBTreeSet.Entry(k);
      } else {
         LongRBTreeSet.Entry p = this.tree;
         int i = 0;

         label123:
         while (true) {
            int cmp;
            if ((cmp = this.compare(k, p.key)) == 0) {
               while (i-- != 0) {
                  this.nodePath[i] = null;
               }

               return false;
            }

            this.nodePath[i] = p;
            if (this.dirPath[i++] = cmp > 0) {
               if (!p.succ()) {
                  p = p.right;
                  continue;
               }

               this.count++;
               LongRBTreeSet.Entry e = new LongRBTreeSet.Entry(k);
               if (p.right == null) {
                  this.lastEntry = e;
               }

               e.left = p;
               e.right = p.right;
               p.right(e);
            } else {
               if (!p.pred()) {
                  p = p.left;
                  continue;
               }

               this.count++;
               LongRBTreeSet.Entry e = new LongRBTreeSet.Entry(k);
               if (p.left == null) {
                  this.firstEntry = e;
               }

               e.right = p;
               e.left = p.left;
               p.left(e);
            }

            maxDepth = i--;

            while (true) {
               if (i <= 0 || this.nodePath[i].black()) {
                  break label123;
               }

               if (!this.dirPath[i - 1]) {
                  LongRBTreeSet.Entry y = this.nodePath[i - 1].right;
                  if (this.nodePath[i - 1].succ() || y.black()) {
                     if (!this.dirPath[i]) {
                        y = this.nodePath[i];
                     } else {
                        LongRBTreeSet.Entry x = this.nodePath[i];
                        y = x.right;
                        x.right = y.left;
                        y.left = x;
                        this.nodePath[i - 1].left = y;
                        if (y.pred()) {
                           y.pred(false);
                           x.succ(y);
                        }
                     }

                     LongRBTreeSet.Entry xx = this.nodePath[i - 1];
                     xx.black(false);
                     y.black(true);
                     xx.left = y.right;
                     y.right = xx;
                     if (i < 2) {
                        this.tree = y;
                     } else if (this.dirPath[i - 2]) {
                        this.nodePath[i - 2].right = y;
                     } else {
                        this.nodePath[i - 2].left = y;
                     }

                     if (y.succ()) {
                        y.succ(false);
                        xx.pred(y);
                     }
                     break label123;
                  }

                  this.nodePath[i].black(true);
                  y.black(true);
                  this.nodePath[i - 1].black(false);
                  i -= 2;
               } else {
                  LongRBTreeSet.Entry y = this.nodePath[i - 1].left;
                  if (this.nodePath[i - 1].pred() || y.black()) {
                     if (this.dirPath[i]) {
                        y = this.nodePath[i];
                     } else {
                        LongRBTreeSet.Entry x = this.nodePath[i];
                        y = x.left;
                        x.left = y.right;
                        y.right = x;
                        this.nodePath[i - 1].right = y;
                        if (y.succ()) {
                           y.succ(false);
                           x.pred(y);
                        }
                     }

                     LongRBTreeSet.Entry xxx = this.nodePath[i - 1];
                     xxx.black(false);
                     y.black(true);
                     xxx.right = y.left;
                     y.left = xxx;
                     if (i < 2) {
                        this.tree = y;
                     } else if (this.dirPath[i - 2]) {
                        this.nodePath[i - 2].right = y;
                     } else {
                        this.nodePath[i - 2].left = y;
                     }

                     if (y.pred()) {
                        y.pred(false);
                        xxx.succ(y);
                     }
                     break label123;
                  }

                  this.nodePath[i].black(true);
                  y.black(true);
                  this.nodePath[i - 1].black(false);
                  i -= 2;
               }
            }
         }
      }

      this.tree.black(true);

      while (maxDepth-- != 0) {
         this.nodePath[maxDepth] = null;
      }

      return true;
   }

   @Override
   public boolean remove(long k) {
      if (this.tree == null) {
         return false;
      } else {
         LongRBTreeSet.Entry p = this.tree;
         int i = 0;
         long kk = k;

         int cmp;
         while ((cmp = this.compare(kk, p.key)) != 0) {
            this.dirPath[i] = cmp > 0;
            this.nodePath[i] = p;
            if (this.dirPath[i++]) {
               if ((p = p.right()) == null) {
                  while (i-- != 0) {
                     this.nodePath[i] = null;
                  }

                  return false;
               }
            } else if ((p = p.left()) == null) {
               while (i-- != 0) {
                  this.nodePath[i] = null;
               }

               return false;
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
               if (i == 0) {
                  this.tree = p.left;
               } else if (this.dirPath[i - 1]) {
                  this.nodePath[i - 1].succ(p.right);
               } else {
                  this.nodePath[i - 1].pred(p.left);
               }
            } else {
               p.prev().right = p.right;
               if (i == 0) {
                  this.tree = p.left;
               } else if (this.dirPath[i - 1]) {
                  this.nodePath[i - 1].right = p.left;
               } else {
                  this.nodePath[i - 1].left = p.left;
               }
            }
         } else {
            LongRBTreeSet.Entry r = p.right;
            if (r.pred()) {
               r.left = p.left;
               r.pred(p.pred());
               if (!r.pred()) {
                  r.prev().right = r;
               }

               if (i == 0) {
                  this.tree = r;
               } else if (this.dirPath[i - 1]) {
                  this.nodePath[i - 1].right = r;
               } else {
                  this.nodePath[i - 1].left = r;
               }

               boolean color = r.black();
               r.black(p.black());
               p.black(color);
               this.dirPath[i] = true;
               this.nodePath[i++] = r;
            } else {
               int j = i++;

               while (true) {
                  this.dirPath[i] = false;
                  this.nodePath[i++] = r;
                  LongRBTreeSet.Entry s = r.left;
                  if (s.pred()) {
                     this.dirPath[j] = true;
                     this.nodePath[j] = s;
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

                     s.right(p.right);
                     boolean color = s.black();
                     s.black(p.black());
                     p.black(color);
                     if (j == 0) {
                        this.tree = s;
                     } else if (this.dirPath[j - 1]) {
                        this.nodePath[j - 1].right = s;
                     } else {
                        this.nodePath[j - 1].left = s;
                     }
                     break;
                  }

                  r = s;
               }
            }
         }

         int maxDepth = i;
         if (p.black()) {
            for (; i > 0; i--) {
               if (this.dirPath[i - 1] && !this.nodePath[i - 1].succ() || !this.dirPath[i - 1] && !this.nodePath[i - 1].pred()) {
                  LongRBTreeSet.Entry x = this.dirPath[i - 1] ? this.nodePath[i - 1].right : this.nodePath[i - 1].left;
                  if (!x.black()) {
                     x.black(true);
                     break;
                  }
               }

               if (!this.dirPath[i - 1]) {
                  LongRBTreeSet.Entry w = this.nodePath[i - 1].right;
                  if (!w.black()) {
                     w.black(true);
                     this.nodePath[i - 1].black(false);
                     this.nodePath[i - 1].right = w.left;
                     w.left = this.nodePath[i - 1];
                     if (i < 2) {
                        this.tree = w;
                     } else if (this.dirPath[i - 2]) {
                        this.nodePath[i - 2].right = w;
                     } else {
                        this.nodePath[i - 2].left = w;
                     }

                     this.nodePath[i] = this.nodePath[i - 1];
                     this.dirPath[i] = false;
                     this.nodePath[i - 1] = w;
                     if (maxDepth == i++) {
                        maxDepth++;
                     }

                     w = this.nodePath[i - 1].right;
                  }

                  if (!w.pred() && !w.left.black() || !w.succ() && !w.right.black()) {
                     if (w.succ() || w.right.black()) {
                        LongRBTreeSet.Entry y = w.left;
                        y.black(true);
                        w.black(false);
                        w.left = y.right;
                        y.right = w;
                        w = this.nodePath[i - 1].right = y;
                        if (w.succ()) {
                           w.succ(false);
                           w.right.pred(w);
                        }
                     }

                     w.black(this.nodePath[i - 1].black());
                     this.nodePath[i - 1].black(true);
                     w.right.black(true);
                     this.nodePath[i - 1].right = w.left;
                     w.left = this.nodePath[i - 1];
                     if (i < 2) {
                        this.tree = w;
                     } else if (this.dirPath[i - 2]) {
                        this.nodePath[i - 2].right = w;
                     } else {
                        this.nodePath[i - 2].left = w;
                     }

                     if (w.pred()) {
                        w.pred(false);
                        this.nodePath[i - 1].succ(w);
                     }
                     break;
                  }

                  w.black(false);
               } else {
                  LongRBTreeSet.Entry wx = this.nodePath[i - 1].left;
                  if (!wx.black()) {
                     wx.black(true);
                     this.nodePath[i - 1].black(false);
                     this.nodePath[i - 1].left = wx.right;
                     wx.right = this.nodePath[i - 1];
                     if (i < 2) {
                        this.tree = wx;
                     } else if (this.dirPath[i - 2]) {
                        this.nodePath[i - 2].right = wx;
                     } else {
                        this.nodePath[i - 2].left = wx;
                     }

                     this.nodePath[i] = this.nodePath[i - 1];
                     this.dirPath[i] = true;
                     this.nodePath[i - 1] = wx;
                     if (maxDepth == i++) {
                        maxDepth++;
                     }

                     wx = this.nodePath[i - 1].left;
                  }

                  if (!wx.pred() && !wx.left.black() || !wx.succ() && !wx.right.black()) {
                     if (wx.pred() || wx.left.black()) {
                        LongRBTreeSet.Entry y = wx.right;
                        y.black(true);
                        wx.black(false);
                        wx.right = y.left;
                        y.left = wx;
                        wx = this.nodePath[i - 1].left = y;
                        if (wx.pred()) {
                           wx.pred(false);
                           wx.left.succ(wx);
                        }
                     }

                     wx.black(this.nodePath[i - 1].black());
                     this.nodePath[i - 1].black(true);
                     wx.left.black(true);
                     this.nodePath[i - 1].left = wx.right;
                     wx.right = this.nodePath[i - 1];
                     if (i < 2) {
                        this.tree = wx;
                     } else if (this.dirPath[i - 2]) {
                        this.nodePath[i - 2].right = wx;
                     } else {
                        this.nodePath[i - 2].left = wx;
                     }

                     if (wx.succ()) {
                        wx.succ(false);
                        this.nodePath[i - 1].pred(wx);
                     }
                     break;
                  }

                  wx.black(false);
               }
            }

            if (this.tree != null) {
               this.tree.black(true);
            }
         }

         this.count--;

         while (maxDepth-- != 0) {
            this.nodePath[maxDepth] = null;
         }

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
      return new LongRBTreeSet.SetIterator();
   }

   @Override
   public LongBidirectionalIterator iterator(long from) {
      return new LongRBTreeSet.SetIterator(from);
   }

   @Override
   public LongComparator comparator() {
      return this.actualComparator;
   }

   @Override
   public LongSortedSet headSet(long to) {
      return new LongRBTreeSet.Subset(0L, true, to, false);
   }

   @Override
   public LongSortedSet tailSet(long from) {
      return new LongRBTreeSet.Subset(from, false, 0L, true);
   }

   @Override
   public LongSortedSet subSet(long from, long to) {
      return new LongRBTreeSet.Subset(from, false, to, false);
   }

   @Override
   public Object clone() {
      LongRBTreeSet c;
      try {
         c = (LongRBTreeSet)super.clone();
      } catch (CloneNotSupportedException var7) {
         throw new InternalError();
      }

      c.allocatePaths();
      if (this.count == 0) {
         return c;
      } else {
         LongRBTreeSet.Entry rp = new LongRBTreeSet.Entry();
         LongRBTreeSet.Entry rq = new LongRBTreeSet.Entry();
         LongRBTreeSet.Entry p = rp;
         rp.left(this.tree);
         LongRBTreeSet.Entry q = rq;
         rq.pred(null);

         while (true) {
            if (!p.pred()) {
               LongRBTreeSet.Entry e = p.left.clone();
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
               LongRBTreeSet.Entry e = p.right.clone();
               e.succ(q.right);
               e.pred(q);
               q.right(e);
            }
         }
      }
   }

   private void writeObject(ObjectOutputStream s) throws IOException {
      int n = this.count;
      LongRBTreeSet.SetIterator i = new LongRBTreeSet.SetIterator();
      s.defaultWriteObject();

      while (n-- != 0) {
         s.writeLong(i.nextLong());
      }
   }

   private LongRBTreeSet.Entry readTree(ObjectInputStream s, int n, LongRBTreeSet.Entry pred, LongRBTreeSet.Entry succ) throws IOException, ClassNotFoundException {
      if (n == 1) {
         LongRBTreeSet.Entry top = new LongRBTreeSet.Entry(s.readLong());
         top.pred(pred);
         top.succ(succ);
         top.black(true);
         return top;
      } else if (n == 2) {
         LongRBTreeSet.Entry top = new LongRBTreeSet.Entry(s.readLong());
         top.black(true);
         top.right(new LongRBTreeSet.Entry(s.readLong()));
         top.right.pred(top);
         top.pred(pred);
         top.right.succ(succ);
         return top;
      } else {
         int rightN = n / 2;
         int leftN = n - rightN - 1;
         LongRBTreeSet.Entry top = new LongRBTreeSet.Entry();
         top.left(this.readTree(s, leftN, pred, top));
         top.key = s.readLong();
         top.black(true);
         top.right(this.readTree(s, rightN, top, succ));
         if (n + 2 == (n + 2 & -(n + 2))) {
            top.right.black(false);
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
         LongRBTreeSet.Entry e = this.tree;

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
      private static final int BLACK_MASK = 1;
      private static final int SUCC_MASK = Integer.MIN_VALUE;
      private static final int PRED_MASK = 1073741824;
      long key;
      LongRBTreeSet.Entry left;
      LongRBTreeSet.Entry right;
      int info;

      Entry() {
      }

      Entry(long k) {
         this.key = k;
         this.info = -1073741824;
      }

      LongRBTreeSet.Entry left() {
         return (this.info & 1073741824) != 0 ? null : this.left;
      }

      LongRBTreeSet.Entry right() {
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

      void pred(LongRBTreeSet.Entry pred) {
         this.info |= 1073741824;
         this.left = pred;
      }

      void succ(LongRBTreeSet.Entry succ) {
         this.info |= Integer.MIN_VALUE;
         this.right = succ;
      }

      void left(LongRBTreeSet.Entry left) {
         this.info &= -1073741825;
         this.left = left;
      }

      void right(LongRBTreeSet.Entry right) {
         this.info &= Integer.MAX_VALUE;
         this.right = right;
      }

      boolean black() {
         return (this.info & 1) != 0;
      }

      void black(boolean black) {
         if (black) {
            this.info |= 1;
         } else {
            this.info &= -2;
         }
      }

      LongRBTreeSet.Entry next() {
         LongRBTreeSet.Entry next = this.right;
         if ((this.info & -2147483648) == 0) {
            while ((next.info & 1073741824) == 0) {
               next = next.left;
            }
         }

         return next;
      }

      LongRBTreeSet.Entry prev() {
         LongRBTreeSet.Entry prev = this.left;
         if ((this.info & 1073741824) == 0) {
            while ((prev.info & -2147483648) == 0) {
               prev = prev.right;
            }
         }

         return prev;
      }

      public LongRBTreeSet.Entry clone() {
         LongRBTreeSet.Entry c;
         try {
            c = (LongRBTreeSet.Entry)super.clone();
         } catch (CloneNotSupportedException var3) {
            throw new InternalError();
         }

         c.key = this.key;
         c.info = this.info;
         return c;
      }

      @Override
      public boolean equals(Object o) {
         if (!(o instanceof LongRBTreeSet.Entry)) {
            return false;
         } else {
            LongRBTreeSet.Entry e = (LongRBTreeSet.Entry)o;
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
      LongRBTreeSet.Entry prev;
      LongRBTreeSet.Entry next;
      LongRBTreeSet.Entry curr;
      int index = 0;

      SetIterator() {
         this.next = LongRBTreeSet.this.firstEntry;
      }

      SetIterator(long k) {
         if ((this.next = LongRBTreeSet.this.locateKey(k)) != null) {
            if (LongRBTreeSet.this.compare(this.next.key, k) <= 0) {
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

      void updatePrevious() {
         this.prev = this.prev.prev();
      }

      @Override
      public long nextLong() {
         return this.nextEntry().key;
      }

      @Override
      public long previousLong() {
         return this.previousEntry().key;
      }

      LongRBTreeSet.Entry nextEntry() {
         if (!this.hasNext()) {
            throw new NoSuchElementException();
         } else {
            this.curr = this.prev = this.next;
            this.index++;
            this.updateNext();
            return this.curr;
         }
      }

      LongRBTreeSet.Entry previousEntry() {
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
            LongRBTreeSet.this.remove(this.curr.key);
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
         if (!bottom && !top && LongRBTreeSet.this.compare(from, to) > 0) {
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
         LongRBTreeSet.Subset.SubsetIterator i = new LongRBTreeSet.Subset.SubsetIterator();

         while (i.hasNext()) {
            i.nextLong();
            i.remove();
         }
      }

      final boolean in(long k) {
         return (this.bottom || LongRBTreeSet.this.compare(k, this.from) >= 0) && (this.top || LongRBTreeSet.this.compare(k, this.to) < 0);
      }

      @Override
      public boolean contains(long k) {
         return this.in(k) && LongRBTreeSet.this.contains(k);
      }

      @Override
      public boolean add(long k) {
         if (!this.in(k)) {
            throw new IllegalArgumentException(
               "Element (" + k + ") out of range [" + (this.bottom ? "-" : String.valueOf(this.from)) + ", " + (this.top ? "-" : String.valueOf(this.to)) + ")"
            );
         } else {
            return LongRBTreeSet.this.add(k);
         }
      }

      @Override
      public boolean remove(long k) {
         return !this.in(k) ? false : LongRBTreeSet.this.remove(k);
      }

      @Override
      public int size() {
         LongRBTreeSet.Subset.SubsetIterator i = new LongRBTreeSet.Subset.SubsetIterator();
         int n = 0;

         while (i.hasNext()) {
            n++;
            i.nextLong();
         }

         return n;
      }

      @Override
      public boolean isEmpty() {
         return !new LongRBTreeSet.Subset.SubsetIterator().hasNext();
      }

      @Override
      public LongComparator comparator() {
         return LongRBTreeSet.this.actualComparator;
      }

      @Override
      public LongBidirectionalIterator iterator() {
         return new LongRBTreeSet.Subset.SubsetIterator();
      }

      @Override
      public LongBidirectionalIterator iterator(long from) {
         return new LongRBTreeSet.Subset.SubsetIterator(from);
      }

      @Override
      public LongSortedSet headSet(long to) {
         if (this.top) {
            return LongRBTreeSet.this.new Subset(this.from, this.bottom, to, false);
         } else {
            return LongRBTreeSet.this.compare(to, this.to) < 0 ? LongRBTreeSet.this.new Subset(this.from, this.bottom, to, false) : this;
         }
      }

      @Override
      public LongSortedSet tailSet(long from) {
         if (this.bottom) {
            return LongRBTreeSet.this.new Subset(from, false, this.to, this.top);
         } else {
            return LongRBTreeSet.this.compare(from, this.from) > 0 ? LongRBTreeSet.this.new Subset(from, false, this.to, this.top) : this;
         }
      }

      @Override
      public LongSortedSet subSet(long from, long to) {
         if (this.top && this.bottom) {
            return LongRBTreeSet.this.new Subset(from, false, to, false);
         } else {
            if (!this.top) {
               to = LongRBTreeSet.this.compare(to, this.to) < 0 ? to : this.to;
            }

            if (!this.bottom) {
               from = LongRBTreeSet.this.compare(from, this.from) > 0 ? from : this.from;
            }

            return !this.top && !this.bottom && from == this.from && to == this.to ? this : LongRBTreeSet.this.new Subset(from, false, to, false);
         }
      }

      public LongRBTreeSet.Entry firstEntry() {
         if (LongRBTreeSet.this.tree == null) {
            return null;
         } else {
            LongRBTreeSet.Entry e;
            if (this.bottom) {
               e = LongRBTreeSet.this.firstEntry;
            } else {
               e = LongRBTreeSet.this.locateKey(this.from);
               if (LongRBTreeSet.this.compare(e.key, this.from) < 0) {
                  e = e.next();
               }
            }

            return e != null && (this.top || LongRBTreeSet.this.compare(e.key, this.to) < 0) ? e : null;
         }
      }

      public LongRBTreeSet.Entry lastEntry() {
         if (LongRBTreeSet.this.tree == null) {
            return null;
         } else {
            LongRBTreeSet.Entry e;
            if (this.top) {
               e = LongRBTreeSet.this.lastEntry;
            } else {
               e = LongRBTreeSet.this.locateKey(this.to);
               if (LongRBTreeSet.this.compare(e.key, this.to) >= 0) {
                  e = e.prev();
               }
            }

            return e != null && (this.bottom || LongRBTreeSet.this.compare(e.key, this.from) >= 0) ? e : null;
         }
      }

      @Override
      public long firstLong() {
         LongRBTreeSet.Entry e = this.firstEntry();
         if (e == null) {
            throw new NoSuchElementException();
         } else {
            return e.key;
         }
      }

      @Override
      public long lastLong() {
         LongRBTreeSet.Entry e = this.lastEntry();
         if (e == null) {
            throw new NoSuchElementException();
         } else {
            return e.key;
         }
      }

      private final class SubsetIterator extends LongRBTreeSet.SetIterator {
         SubsetIterator() {
            this.next = Subset.this.firstEntry();
         }

         SubsetIterator(long k) {
            this();
            if (this.next != null) {
               if (!Subset.this.bottom && LongRBTreeSet.this.compare(k, this.next.key) < 0) {
                  this.prev = null;
               } else if (!Subset.this.top && LongRBTreeSet.this.compare(k, (this.prev = Subset.this.lastEntry()).key) >= 0) {
                  this.next = null;
               } else {
                  this.next = LongRBTreeSet.this.locateKey(k);
                  if (LongRBTreeSet.this.compare(this.next.key, k) <= 0) {
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
            if (!Subset.this.bottom && this.prev != null && LongRBTreeSet.this.compare(this.prev.key, Subset.this.from) < 0) {
               this.prev = null;
            }
         }

         @Override
         void updateNext() {
            this.next = this.next.next();
            if (!Subset.this.top && this.next != null && LongRBTreeSet.this.compare(this.next.key, Subset.this.to) >= 0) {
               this.next = null;
            }
         }
      }
   }
}
