package it.unimi.dsi.fastutil.bytes;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.SortedSet;

public class ByteAVLTreeSet extends AbstractByteSortedSet implements Serializable, Cloneable, ByteSortedSet {
   protected transient ByteAVLTreeSet.Entry tree;
   protected int count;
   protected transient ByteAVLTreeSet.Entry firstEntry;
   protected transient ByteAVLTreeSet.Entry lastEntry;
   protected Comparator<? super Byte> storedComparator;
   protected transient ByteComparator actualComparator;
   private static final long serialVersionUID = -7046029254386353130L;
   private transient boolean[] dirPath;

   public ByteAVLTreeSet() {
      this.allocatePaths();
      this.tree = null;
      this.count = 0;
   }

   private void setActualComparator() {
      this.actualComparator = ByteComparators.asByteComparator(this.storedComparator);
   }

   public ByteAVLTreeSet(Comparator<? super Byte> c) {
      this();
      this.storedComparator = c;
      this.setActualComparator();
   }

   public ByteAVLTreeSet(Collection<? extends Byte> c) {
      this();
      this.addAll(c);
   }

   public ByteAVLTreeSet(SortedSet<Byte> s) {
      this(s.comparator());
      this.addAll(s);
   }

   public ByteAVLTreeSet(ByteCollection c) {
      this();
      this.addAll(c);
   }

   public ByteAVLTreeSet(ByteSortedSet s) {
      this(s.comparator());
      this.addAll(s);
   }

   public ByteAVLTreeSet(ByteIterator i) {
      this.allocatePaths();

      while (i.hasNext()) {
         this.add(i.nextByte());
      }
   }

   public ByteAVLTreeSet(Iterator<?> i) {
      this(ByteIterators.asByteIterator(i));
   }

   public ByteAVLTreeSet(byte[] a, int offset, int length, Comparator<? super Byte> c) {
      this(c);
      ByteArrays.ensureOffsetLength(a, offset, length);

      for (int i = 0; i < length; i++) {
         this.add(a[offset + i]);
      }
   }

   public ByteAVLTreeSet(byte[] a, int offset, int length) {
      this(a, offset, length, null);
   }

   public ByteAVLTreeSet(byte[] a) {
      this();
      int i = a.length;

      while (i-- != 0) {
         this.add(a[i]);
      }
   }

   public ByteAVLTreeSet(byte[] a, Comparator<? super Byte> c) {
      this(c);
      int i = a.length;

      while (i-- != 0) {
         this.add(a[i]);
      }
   }

   final int compare(byte k1, byte k2) {
      return this.actualComparator == null ? Byte.compare(k1, k2) : this.actualComparator.compare(k1, k2);
   }

   private ByteAVLTreeSet.Entry findKey(byte k) {
      ByteAVLTreeSet.Entry e = this.tree;

      int cmp;
      while (e != null && (cmp = this.compare(k, e.key)) != 0) {
         e = cmp < 0 ? e.left() : e.right();
      }

      return e;
   }

   final ByteAVLTreeSet.Entry locateKey(byte k) {
      ByteAVLTreeSet.Entry e = this.tree;
      ByteAVLTreeSet.Entry last = this.tree;

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
   public boolean add(byte k) {
      if (this.tree == null) {
         this.count++;
         this.tree = this.lastEntry = this.firstEntry = new ByteAVLTreeSet.Entry(k);
      } else {
         ByteAVLTreeSet.Entry p = this.tree;
         ByteAVLTreeSet.Entry q = null;
         ByteAVLTreeSet.Entry y = this.tree;
         ByteAVLTreeSet.Entry z = null;
         ByteAVLTreeSet.Entry e = null;
         ByteAVLTreeSet.Entry w = null;
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
                  e = new ByteAVLTreeSet.Entry(k);
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
                  e = new ByteAVLTreeSet.Entry(k);
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
            ByteAVLTreeSet.Entry x = y.left;
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

            ByteAVLTreeSet.Entry x = y.right;
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

   private ByteAVLTreeSet.Entry parent(ByteAVLTreeSet.Entry e) {
      if (e == this.tree) {
         return null;
      } else {
         ByteAVLTreeSet.Entry y = e;

         ByteAVLTreeSet.Entry x;
         for (x = e; !y.succ(); y = y.right) {
            if (x.pred()) {
               ByteAVLTreeSet.Entry p = x.left;
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

         ByteAVLTreeSet.Entry p = y.right;
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
   public boolean remove(byte k) {
      if (this.tree == null) {
         return false;
      } else {
         ByteAVLTreeSet.Entry p = this.tree;
         ByteAVLTreeSet.Entry q = null;
         boolean dir = false;
         byte kk = k;

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
            ByteAVLTreeSet.Entry r = p.right;
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
                  ByteAVLTreeSet.Entry s = r.left;
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
            ByteAVLTreeSet.Entry y = q;
            q = this.parent(q);
            if (!dir) {
               dir = q != null && q.left != y;
               y.incBalance();
               if (y.balance() == 1) {
                  break;
               }

               if (y.balance() == 2) {
                  ByteAVLTreeSet.Entry x = y.right;

                  assert x != null;

                  if (x.balance() == -1) {
                     assert x.balance() == -1;

                     ByteAVLTreeSet.Entry w = x.left;
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
                  ByteAVLTreeSet.Entry xx = y.left;

                  assert xx != null;

                  if (xx.balance() == 1) {
                     assert xx.balance() == 1;

                     ByteAVLTreeSet.Entry wx = xx.right;
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
   public boolean contains(byte k) {
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
   public byte firstByte() {
      if (this.tree == null) {
         throw new NoSuchElementException();
      } else {
         return this.firstEntry.key;
      }
   }

   @Override
   public byte lastByte() {
      if (this.tree == null) {
         throw new NoSuchElementException();
      } else {
         return this.lastEntry.key;
      }
   }

   @Override
   public ByteBidirectionalIterator iterator() {
      return new ByteAVLTreeSet.SetIterator();
   }

   @Override
   public ByteBidirectionalIterator iterator(byte from) {
      return new ByteAVLTreeSet.SetIterator(from);
   }

   @Override
   public ByteComparator comparator() {
      return this.actualComparator;
   }

   @Override
   public ByteSortedSet headSet(byte to) {
      return new ByteAVLTreeSet.Subset((byte)0, true, to, false);
   }

   @Override
   public ByteSortedSet tailSet(byte from) {
      return new ByteAVLTreeSet.Subset(from, false, (byte)0, true);
   }

   @Override
   public ByteSortedSet subSet(byte from, byte to) {
      return new ByteAVLTreeSet.Subset(from, false, to, false);
   }

   @Override
   public Object clone() {
      ByteAVLTreeSet c;
      try {
         c = (ByteAVLTreeSet)super.clone();
      } catch (CloneNotSupportedException var7) {
         throw new InternalError();
      }

      c.allocatePaths();
      if (this.count == 0) {
         return c;
      } else {
         ByteAVLTreeSet.Entry rp = new ByteAVLTreeSet.Entry();
         ByteAVLTreeSet.Entry rq = new ByteAVLTreeSet.Entry();
         ByteAVLTreeSet.Entry p = rp;
         rp.left(this.tree);
         ByteAVLTreeSet.Entry q = rq;
         rq.pred(null);

         while (true) {
            if (!p.pred()) {
               ByteAVLTreeSet.Entry e = p.left.clone();
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
               ByteAVLTreeSet.Entry e = p.right.clone();
               e.succ(q.right);
               e.pred(q);
               q.right(e);
            }
         }
      }
   }

   private void writeObject(ObjectOutputStream s) throws IOException {
      int n = this.count;
      ByteAVLTreeSet.SetIterator i = new ByteAVLTreeSet.SetIterator();
      s.defaultWriteObject();

      while (n-- != 0) {
         s.writeByte(i.nextByte());
      }
   }

   private ByteAVLTreeSet.Entry readTree(ObjectInputStream s, int n, ByteAVLTreeSet.Entry pred, ByteAVLTreeSet.Entry succ) throws IOException, ClassNotFoundException {
      if (n == 1) {
         ByteAVLTreeSet.Entry top = new ByteAVLTreeSet.Entry(s.readByte());
         top.pred(pred);
         top.succ(succ);
         return top;
      } else if (n == 2) {
         ByteAVLTreeSet.Entry top = new ByteAVLTreeSet.Entry(s.readByte());
         top.right(new ByteAVLTreeSet.Entry(s.readByte()));
         top.right.pred(top);
         top.balance(1);
         top.pred(pred);
         top.right.succ(succ);
         return top;
      } else {
         int rightN = n / 2;
         int leftN = n - rightN - 1;
         ByteAVLTreeSet.Entry top = new ByteAVLTreeSet.Entry();
         top.left(this.readTree(s, leftN, pred, top));
         top.key = s.readByte();
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
         ByteAVLTreeSet.Entry e = this.tree;

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
      byte key;
      ByteAVLTreeSet.Entry left;
      ByteAVLTreeSet.Entry right;
      int info;

      Entry() {
      }

      Entry(byte k) {
         this.key = k;
         this.info = -1073741824;
      }

      ByteAVLTreeSet.Entry left() {
         return (this.info & 1073741824) != 0 ? null : this.left;
      }

      ByteAVLTreeSet.Entry right() {
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

      void pred(ByteAVLTreeSet.Entry pred) {
         this.info |= 1073741824;
         this.left = pred;
      }

      void succ(ByteAVLTreeSet.Entry succ) {
         this.info |= Integer.MIN_VALUE;
         this.right = succ;
      }

      void left(ByteAVLTreeSet.Entry left) {
         this.info &= -1073741825;
         this.left = left;
      }

      void right(ByteAVLTreeSet.Entry right) {
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

      ByteAVLTreeSet.Entry next() {
         ByteAVLTreeSet.Entry next = this.right;
         if ((this.info & -2147483648) == 0) {
            while ((next.info & 1073741824) == 0) {
               next = next.left;
            }
         }

         return next;
      }

      ByteAVLTreeSet.Entry prev() {
         ByteAVLTreeSet.Entry prev = this.left;
         if ((this.info & 1073741824) == 0) {
            while ((prev.info & -2147483648) == 0) {
               prev = prev.right;
            }
         }

         return prev;
      }

      public ByteAVLTreeSet.Entry clone() {
         ByteAVLTreeSet.Entry c;
         try {
            c = (ByteAVLTreeSet.Entry)super.clone();
         } catch (CloneNotSupportedException var3) {
            throw new InternalError();
         }

         c.key = this.key;
         c.info = this.info;
         return c;
      }

      @Override
      public boolean equals(Object o) {
         if (!(o instanceof ByteAVLTreeSet.Entry)) {
            return false;
         } else {
            ByteAVLTreeSet.Entry e = (ByteAVLTreeSet.Entry)o;
            return this.key == e.key;
         }
      }

      @Override
      public int hashCode() {
         return this.key;
      }

      @Override
      public String toString() {
         return String.valueOf(this.key);
      }
   }

   private class SetIterator implements ByteListIterator {
      ByteAVLTreeSet.Entry prev;
      ByteAVLTreeSet.Entry next;
      ByteAVLTreeSet.Entry curr;
      int index = 0;

      SetIterator() {
         this.next = ByteAVLTreeSet.this.firstEntry;
      }

      SetIterator(byte k) {
         if ((this.next = ByteAVLTreeSet.this.locateKey(k)) != null) {
            if (ByteAVLTreeSet.this.compare(this.next.key, k) <= 0) {
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

      ByteAVLTreeSet.Entry nextEntry() {
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
      public byte nextByte() {
         return this.nextEntry().key;
      }

      @Override
      public byte previousByte() {
         return this.previousEntry().key;
      }

      void updatePrevious() {
         this.prev = this.prev.prev();
      }

      ByteAVLTreeSet.Entry previousEntry() {
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
            ByteAVLTreeSet.this.remove(this.curr.key);
            this.curr = null;
         }
      }
   }

   private final class Subset extends AbstractByteSortedSet implements Serializable, ByteSortedSet {
      private static final long serialVersionUID = -7046029254386353129L;
      byte from;
      byte to;
      boolean bottom;
      boolean top;

      public Subset(byte from, boolean bottom, byte to, boolean top) {
         if (!bottom && !top && ByteAVLTreeSet.this.compare(from, to) > 0) {
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
         ByteAVLTreeSet.Subset.SubsetIterator i = new ByteAVLTreeSet.Subset.SubsetIterator();

         while (i.hasNext()) {
            i.nextByte();
            i.remove();
         }
      }

      final boolean in(byte k) {
         return (this.bottom || ByteAVLTreeSet.this.compare(k, this.from) >= 0) && (this.top || ByteAVLTreeSet.this.compare(k, this.to) < 0);
      }

      @Override
      public boolean contains(byte k) {
         return this.in(k) && ByteAVLTreeSet.this.contains(k);
      }

      @Override
      public boolean add(byte k) {
         if (!this.in(k)) {
            throw new IllegalArgumentException(
               "Element (" + k + ") out of range [" + (this.bottom ? "-" : String.valueOf(this.from)) + ", " + (this.top ? "-" : String.valueOf(this.to)) + ")"
            );
         } else {
            return ByteAVLTreeSet.this.add(k);
         }
      }

      @Override
      public boolean remove(byte k) {
         return !this.in(k) ? false : ByteAVLTreeSet.this.remove(k);
      }

      @Override
      public int size() {
         ByteAVLTreeSet.Subset.SubsetIterator i = new ByteAVLTreeSet.Subset.SubsetIterator();
         int n = 0;

         while (i.hasNext()) {
            n++;
            i.nextByte();
         }

         return n;
      }

      @Override
      public boolean isEmpty() {
         return !new ByteAVLTreeSet.Subset.SubsetIterator().hasNext();
      }

      @Override
      public ByteComparator comparator() {
         return ByteAVLTreeSet.this.actualComparator;
      }

      @Override
      public ByteBidirectionalIterator iterator() {
         return new ByteAVLTreeSet.Subset.SubsetIterator();
      }

      @Override
      public ByteBidirectionalIterator iterator(byte from) {
         return new ByteAVLTreeSet.Subset.SubsetIterator(from);
      }

      @Override
      public ByteSortedSet headSet(byte to) {
         if (this.top) {
            return ByteAVLTreeSet.this.new Subset(this.from, this.bottom, to, false);
         } else {
            return ByteAVLTreeSet.this.compare(to, this.to) < 0 ? ByteAVLTreeSet.this.new Subset(this.from, this.bottom, to, false) : this;
         }
      }

      @Override
      public ByteSortedSet tailSet(byte from) {
         if (this.bottom) {
            return ByteAVLTreeSet.this.new Subset(from, false, this.to, this.top);
         } else {
            return ByteAVLTreeSet.this.compare(from, this.from) > 0 ? ByteAVLTreeSet.this.new Subset(from, false, this.to, this.top) : this;
         }
      }

      @Override
      public ByteSortedSet subSet(byte from, byte to) {
         if (this.top && this.bottom) {
            return ByteAVLTreeSet.this.new Subset(from, false, to, false);
         } else {
            if (!this.top) {
               to = ByteAVLTreeSet.this.compare(to, this.to) < 0 ? to : this.to;
            }

            if (!this.bottom) {
               from = ByteAVLTreeSet.this.compare(from, this.from) > 0 ? from : this.from;
            }

            return !this.top && !this.bottom && from == this.from && to == this.to ? this : ByteAVLTreeSet.this.new Subset(from, false, to, false);
         }
      }

      public ByteAVLTreeSet.Entry firstEntry() {
         if (ByteAVLTreeSet.this.tree == null) {
            return null;
         } else {
            ByteAVLTreeSet.Entry e;
            if (this.bottom) {
               e = ByteAVLTreeSet.this.firstEntry;
            } else {
               e = ByteAVLTreeSet.this.locateKey(this.from);
               if (ByteAVLTreeSet.this.compare(e.key, this.from) < 0) {
                  e = e.next();
               }
            }

            return e != null && (this.top || ByteAVLTreeSet.this.compare(e.key, this.to) < 0) ? e : null;
         }
      }

      public ByteAVLTreeSet.Entry lastEntry() {
         if (ByteAVLTreeSet.this.tree == null) {
            return null;
         } else {
            ByteAVLTreeSet.Entry e;
            if (this.top) {
               e = ByteAVLTreeSet.this.lastEntry;
            } else {
               e = ByteAVLTreeSet.this.locateKey(this.to);
               if (ByteAVLTreeSet.this.compare(e.key, this.to) >= 0) {
                  e = e.prev();
               }
            }

            return e != null && (this.bottom || ByteAVLTreeSet.this.compare(e.key, this.from) >= 0) ? e : null;
         }
      }

      @Override
      public byte firstByte() {
         ByteAVLTreeSet.Entry e = this.firstEntry();
         if (e == null) {
            throw new NoSuchElementException();
         } else {
            return e.key;
         }
      }

      @Override
      public byte lastByte() {
         ByteAVLTreeSet.Entry e = this.lastEntry();
         if (e == null) {
            throw new NoSuchElementException();
         } else {
            return e.key;
         }
      }

      private final class SubsetIterator extends ByteAVLTreeSet.SetIterator {
         SubsetIterator() {
            this.next = Subset.this.firstEntry();
         }

         SubsetIterator(byte k) {
            this();
            if (this.next != null) {
               if (!Subset.this.bottom && ByteAVLTreeSet.this.compare(k, this.next.key) < 0) {
                  this.prev = null;
               } else if (!Subset.this.top && ByteAVLTreeSet.this.compare(k, (this.prev = Subset.this.lastEntry()).key) >= 0) {
                  this.next = null;
               } else {
                  this.next = ByteAVLTreeSet.this.locateKey(k);
                  if (ByteAVLTreeSet.this.compare(this.next.key, k) <= 0) {
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
            if (!Subset.this.bottom && this.prev != null && ByteAVLTreeSet.this.compare(this.prev.key, Subset.this.from) < 0) {
               this.prev = null;
            }
         }

         @Override
         void updateNext() {
            this.next = this.next.next();
            if (!Subset.this.top && this.next != null && ByteAVLTreeSet.this.compare(this.next.key, Subset.this.to) >= 0) {
               this.next = null;
            }
         }
      }
   }
}
