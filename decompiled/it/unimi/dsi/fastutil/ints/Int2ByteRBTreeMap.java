package it.unimi.dsi.fastutil.ints;

import it.unimi.dsi.fastutil.bytes.AbstractByteCollection;
import it.unimi.dsi.fastutil.bytes.ByteCollection;
import it.unimi.dsi.fastutil.bytes.ByteIterator;
import it.unimi.dsi.fastutil.bytes.ByteListIterator;
import it.unimi.dsi.fastutil.objects.AbstractObjectSortedSet;
import it.unimi.dsi.fastutil.objects.ObjectBidirectionalIterator;
import it.unimi.dsi.fastutil.objects.ObjectListIterator;
import it.unimi.dsi.fastutil.objects.ObjectSortedSet;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.SortedMap;

public class Int2ByteRBTreeMap extends AbstractInt2ByteSortedMap implements Serializable, Cloneable {
   protected transient Int2ByteRBTreeMap.Entry tree;
   protected int count;
   protected transient Int2ByteRBTreeMap.Entry firstEntry;
   protected transient Int2ByteRBTreeMap.Entry lastEntry;
   protected transient ObjectSortedSet<Int2ByteMap.Entry> entries;
   protected transient IntSortedSet keys;
   protected transient ByteCollection values;
   protected transient boolean modified;
   protected Comparator<? super Integer> storedComparator;
   protected transient IntComparator actualComparator;
   private static final long serialVersionUID = -7046029254386353129L;
   private transient boolean[] dirPath;
   private transient Int2ByteRBTreeMap.Entry[] nodePath;

   public Int2ByteRBTreeMap() {
      this.allocatePaths();
      this.tree = null;
      this.count = 0;
   }

   private void setActualComparator() {
      this.actualComparator = IntComparators.asIntComparator(this.storedComparator);
   }

   public Int2ByteRBTreeMap(Comparator<? super Integer> c) {
      this();
      this.storedComparator = c;
      this.setActualComparator();
   }

   public Int2ByteRBTreeMap(Map<? extends Integer, ? extends Byte> m) {
      this();
      this.putAll(m);
   }

   public Int2ByteRBTreeMap(SortedMap<Integer, Byte> m) {
      this(m.comparator());
      this.putAll(m);
   }

   public Int2ByteRBTreeMap(Int2ByteMap m) {
      this();
      this.putAll(m);
   }

   public Int2ByteRBTreeMap(Int2ByteSortedMap m) {
      this(m.comparator());
      this.putAll(m);
   }

   public Int2ByteRBTreeMap(int[] k, byte[] v, Comparator<? super Integer> c) {
      this(c);
      if (k.length != v.length) {
         throw new IllegalArgumentException("The key array and the value array have different lengths (" + k.length + " and " + v.length + ")");
      } else {
         for (int i = 0; i < k.length; i++) {
            this.put(k[i], v[i]);
         }
      }
   }

   public Int2ByteRBTreeMap(int[] k, byte[] v) {
      this(k, v, null);
   }

   final int compare(int k1, int k2) {
      return this.actualComparator == null ? Integer.compare(k1, k2) : this.actualComparator.compare(k1, k2);
   }

   final Int2ByteRBTreeMap.Entry findKey(int k) {
      Int2ByteRBTreeMap.Entry e = this.tree;

      int cmp;
      while (e != null && (cmp = this.compare(k, e.key)) != 0) {
         e = cmp < 0 ? e.left() : e.right();
      }

      return e;
   }

   final Int2ByteRBTreeMap.Entry locateKey(int k) {
      Int2ByteRBTreeMap.Entry e = this.tree;
      Int2ByteRBTreeMap.Entry last = this.tree;

      int cmp;
      for (cmp = 0; e != null && (cmp = this.compare(k, e.key)) != 0; e = cmp < 0 ? e.left() : e.right()) {
         last = e;
      }

      return cmp == 0 ? e : last;
   }

   private void allocatePaths() {
      this.dirPath = new boolean[64];
      this.nodePath = new Int2ByteRBTreeMap.Entry[64];
   }

   public byte addTo(int k, byte incr) {
      Int2ByteRBTreeMap.Entry e = this.add(k);
      byte oldValue = e.value;
      e.value += incr;
      return oldValue;
   }

   @Override
   public byte put(int k, byte v) {
      Int2ByteRBTreeMap.Entry e = this.add(k);
      byte oldValue = e.value;
      e.value = v;
      return oldValue;
   }

   private Int2ByteRBTreeMap.Entry add(int k) {
      this.modified = false;
      int maxDepth = 0;
      Int2ByteRBTreeMap.Entry e;
      if (this.tree == null) {
         this.count++;
         e = this.tree = this.lastEntry = this.firstEntry = new Int2ByteRBTreeMap.Entry(k, this.defRetValue);
      } else {
         Int2ByteRBTreeMap.Entry p = this.tree;
         int i = 0;

         label123:
         while (true) {
            int cmp;
            if ((cmp = this.compare(k, p.key)) == 0) {
               while (i-- != 0) {
                  this.nodePath[i] = null;
               }

               return p;
            }

            this.nodePath[i] = p;
            if (this.dirPath[i++] = cmp > 0) {
               if (!p.succ()) {
                  p = p.right;
                  continue;
               }

               this.count++;
               e = new Int2ByteRBTreeMap.Entry(k, this.defRetValue);
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
               e = new Int2ByteRBTreeMap.Entry(k, this.defRetValue);
               if (p.left == null) {
                  this.firstEntry = e;
               }

               e.right = p;
               e.left = p.left;
               p.left(e);
            }

            this.modified = true;
            maxDepth = i--;

            while (true) {
               if (i <= 0 || this.nodePath[i].black()) {
                  break label123;
               }

               if (!this.dirPath[i - 1]) {
                  Int2ByteRBTreeMap.Entry y = this.nodePath[i - 1].right;
                  if (this.nodePath[i - 1].succ() || y.black()) {
                     if (!this.dirPath[i]) {
                        y = this.nodePath[i];
                     } else {
                        Int2ByteRBTreeMap.Entry x = this.nodePath[i];
                        y = x.right;
                        x.right = y.left;
                        y.left = x;
                        this.nodePath[i - 1].left = y;
                        if (y.pred()) {
                           y.pred(false);
                           x.succ(y);
                        }
                     }

                     Int2ByteRBTreeMap.Entry xx = this.nodePath[i - 1];
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
                  Int2ByteRBTreeMap.Entry y = this.nodePath[i - 1].left;
                  if (this.nodePath[i - 1].pred() || y.black()) {
                     if (this.dirPath[i]) {
                        y = this.nodePath[i];
                     } else {
                        Int2ByteRBTreeMap.Entry x = this.nodePath[i];
                        y = x.left;
                        x.left = y.right;
                        y.right = x;
                        this.nodePath[i - 1].right = y;
                        if (y.succ()) {
                           y.succ(false);
                           x.pred(y);
                        }
                     }

                     Int2ByteRBTreeMap.Entry xxx = this.nodePath[i - 1];
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

      return e;
   }

   @Override
   public byte remove(int k) {
      this.modified = false;
      if (this.tree == null) {
         return this.defRetValue;
      } else {
         Int2ByteRBTreeMap.Entry p = this.tree;
         int i = 0;
         int kk = k;

         int cmp;
         while ((cmp = this.compare(kk, p.key)) != 0) {
            this.dirPath[i] = cmp > 0;
            this.nodePath[i] = p;
            if (this.dirPath[i++]) {
               if ((p = p.right()) == null) {
                  while (i-- != 0) {
                     this.nodePath[i] = null;
                  }

                  return this.defRetValue;
               }
            } else if ((p = p.left()) == null) {
               while (i-- != 0) {
                  this.nodePath[i] = null;
               }

               return this.defRetValue;
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
            Int2ByteRBTreeMap.Entry r = p.right;
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
                  Int2ByteRBTreeMap.Entry s = r.left;
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
                  Int2ByteRBTreeMap.Entry x = this.dirPath[i - 1] ? this.nodePath[i - 1].right : this.nodePath[i - 1].left;
                  if (!x.black()) {
                     x.black(true);
                     break;
                  }
               }

               if (!this.dirPath[i - 1]) {
                  Int2ByteRBTreeMap.Entry w = this.nodePath[i - 1].right;
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
                        Int2ByteRBTreeMap.Entry y = w.left;
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
                  Int2ByteRBTreeMap.Entry wx = this.nodePath[i - 1].left;
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
                        Int2ByteRBTreeMap.Entry y = wx.right;
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

         this.modified = true;
         this.count--;

         while (maxDepth-- != 0) {
            this.nodePath[maxDepth] = null;
         }

         return p.value;
      }
   }

   @Override
   public boolean containsValue(byte v) {
      Int2ByteRBTreeMap.ValueIterator i = new Int2ByteRBTreeMap.ValueIterator();
      int j = this.count;

      while (j-- != 0) {
         byte ev = i.nextByte();
         if (ev == v) {
            return true;
         }
      }

      return false;
   }

   @Override
   public void clear() {
      this.count = 0;
      this.tree = null;
      this.entries = null;
      this.values = null;
      this.keys = null;
      this.firstEntry = this.lastEntry = null;
   }

   @Override
   public boolean containsKey(int k) {
      return this.findKey(k) != null;
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
   public byte get(int k) {
      Int2ByteRBTreeMap.Entry e = this.findKey(k);
      return e == null ? this.defRetValue : e.value;
   }

   @Override
   public int firstIntKey() {
      if (this.tree == null) {
         throw new NoSuchElementException();
      } else {
         return this.firstEntry.key;
      }
   }

   @Override
   public int lastIntKey() {
      if (this.tree == null) {
         throw new NoSuchElementException();
      } else {
         return this.lastEntry.key;
      }
   }

   @Override
   public ObjectSortedSet<Int2ByteMap.Entry> int2ByteEntrySet() {
      if (this.entries == null) {
         this.entries = new AbstractObjectSortedSet<Int2ByteMap.Entry>() {
            final Comparator<? super Int2ByteMap.Entry> comparator = Int2ByteRBTreeMap.this.actualComparator == null
               ? (x, y) -> Integer.compare(x.getIntKey(), y.getIntKey())
               : (x, y) -> Int2ByteRBTreeMap.this.actualComparator.compare(x.getIntKey(), y.getIntKey());

            @Override
            public Comparator<? super Int2ByteMap.Entry> comparator() {
               return this.comparator;
            }

            @Override
            public ObjectBidirectionalIterator<Int2ByteMap.Entry> iterator() {
               return Int2ByteRBTreeMap.this.new EntryIterator();
            }

            public ObjectBidirectionalIterator<Int2ByteMap.Entry> iterator(Int2ByteMap.Entry from) {
               return Int2ByteRBTreeMap.this.new EntryIterator(from.getIntKey());
            }

            @Override
            public boolean contains(Object o) {
               if (o != null && o instanceof java.util.Map.Entry) {
                  java.util.Map.Entry<?, ?> e = (java.util.Map.Entry<?, ?>)o;
                  if (e.getKey() == null) {
                     return false;
                  } else if (!(e.getKey() instanceof Integer)) {
                     return false;
                  } else if (e.getValue() != null && e.getValue() instanceof Byte) {
                     Int2ByteRBTreeMap.Entry f = Int2ByteRBTreeMap.this.findKey((Integer)e.getKey());
                     return e.equals(f);
                  } else {
                     return false;
                  }
               } else {
                  return false;
               }
            }

            @Override
            public boolean remove(Object o) {
               if (!(o instanceof java.util.Map.Entry)) {
                  return false;
               } else {
                  java.util.Map.Entry<?, ?> e = (java.util.Map.Entry<?, ?>)o;
                  if (e.getKey() == null) {
                     return false;
                  } else if (!(e.getKey() instanceof Integer)) {
                     return false;
                  } else if (e.getValue() != null && e.getValue() instanceof Byte) {
                     Int2ByteRBTreeMap.Entry f = Int2ByteRBTreeMap.this.findKey((Integer)e.getKey());
                     if (f != null && f.getByteValue() == (Byte)e.getValue()) {
                        Int2ByteRBTreeMap.this.remove(f.key);
                        return true;
                     } else {
                        return false;
                     }
                  } else {
                     return false;
                  }
               }
            }

            @Override
            public int size() {
               return Int2ByteRBTreeMap.this.count;
            }

            @Override
            public void clear() {
               Int2ByteRBTreeMap.this.clear();
            }

            public Int2ByteMap.Entry first() {
               return Int2ByteRBTreeMap.this.firstEntry;
            }

            public Int2ByteMap.Entry last() {
               return Int2ByteRBTreeMap.this.lastEntry;
            }

            public ObjectSortedSet<Int2ByteMap.Entry> subSet(Int2ByteMap.Entry from, Int2ByteMap.Entry to) {
               return Int2ByteRBTreeMap.this.subMap(from.getIntKey(), to.getIntKey()).int2ByteEntrySet();
            }

            public ObjectSortedSet<Int2ByteMap.Entry> headSet(Int2ByteMap.Entry to) {
               return Int2ByteRBTreeMap.this.headMap(to.getIntKey()).int2ByteEntrySet();
            }

            public ObjectSortedSet<Int2ByteMap.Entry> tailSet(Int2ByteMap.Entry from) {
               return Int2ByteRBTreeMap.this.tailMap(from.getIntKey()).int2ByteEntrySet();
            }
         };
      }

      return this.entries;
   }

   @Override
   public IntSortedSet keySet() {
      if (this.keys == null) {
         this.keys = new Int2ByteRBTreeMap.KeySet();
      }

      return this.keys;
   }

   @Override
   public ByteCollection values() {
      if (this.values == null) {
         this.values = new AbstractByteCollection() {
            @Override
            public ByteIterator iterator() {
               return Int2ByteRBTreeMap.this.new ValueIterator();
            }

            @Override
            public boolean contains(byte k) {
               return Int2ByteRBTreeMap.this.containsValue(k);
            }

            @Override
            public int size() {
               return Int2ByteRBTreeMap.this.count;
            }

            @Override
            public void clear() {
               Int2ByteRBTreeMap.this.clear();
            }
         };
      }

      return this.values;
   }

   @Override
   public IntComparator comparator() {
      return this.actualComparator;
   }

   @Override
   public Int2ByteSortedMap headMap(int to) {
      return new Int2ByteRBTreeMap.Submap(0, true, to, false);
   }

   @Override
   public Int2ByteSortedMap tailMap(int from) {
      return new Int2ByteRBTreeMap.Submap(from, false, 0, true);
   }

   @Override
   public Int2ByteSortedMap subMap(int from, int to) {
      return new Int2ByteRBTreeMap.Submap(from, false, to, false);
   }

   public Int2ByteRBTreeMap clone() {
      Int2ByteRBTreeMap c;
      try {
         c = (Int2ByteRBTreeMap)super.clone();
      } catch (CloneNotSupportedException var7) {
         throw new InternalError();
      }

      c.keys = null;
      c.values = null;
      c.entries = null;
      c.allocatePaths();
      if (this.count == 0) {
         return c;
      } else {
         Int2ByteRBTreeMap.Entry rp = new Int2ByteRBTreeMap.Entry();
         Int2ByteRBTreeMap.Entry rq = new Int2ByteRBTreeMap.Entry();
         Int2ByteRBTreeMap.Entry p = rp;
         rp.left(this.tree);
         Int2ByteRBTreeMap.Entry q = rq;
         rq.pred(null);

         while (true) {
            if (!p.pred()) {
               Int2ByteRBTreeMap.Entry e = p.left.clone();
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
               Int2ByteRBTreeMap.Entry e = p.right.clone();
               e.succ(q.right);
               e.pred(q);
               q.right(e);
            }
         }
      }
   }

   private void writeObject(ObjectOutputStream s) throws IOException {
      int n = this.count;
      Int2ByteRBTreeMap.EntryIterator i = new Int2ByteRBTreeMap.EntryIterator();
      s.defaultWriteObject();

      while (n-- != 0) {
         Int2ByteRBTreeMap.Entry e = i.nextEntry();
         s.writeInt(e.key);
         s.writeByte(e.value);
      }
   }

   private Int2ByteRBTreeMap.Entry readTree(ObjectInputStream s, int n, Int2ByteRBTreeMap.Entry pred, Int2ByteRBTreeMap.Entry succ) throws IOException, ClassNotFoundException {
      if (n == 1) {
         Int2ByteRBTreeMap.Entry top = new Int2ByteRBTreeMap.Entry(s.readInt(), s.readByte());
         top.pred(pred);
         top.succ(succ);
         top.black(true);
         return top;
      } else if (n == 2) {
         Int2ByteRBTreeMap.Entry top = new Int2ByteRBTreeMap.Entry(s.readInt(), s.readByte());
         top.black(true);
         top.right(new Int2ByteRBTreeMap.Entry(s.readInt(), s.readByte()));
         top.right.pred(top);
         top.pred(pred);
         top.right.succ(succ);
         return top;
      } else {
         int rightN = n / 2;
         int leftN = n - rightN - 1;
         Int2ByteRBTreeMap.Entry top = new Int2ByteRBTreeMap.Entry();
         top.left(this.readTree(s, leftN, pred, top));
         top.key = s.readInt();
         top.value = s.readByte();
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
         Int2ByteRBTreeMap.Entry e = this.tree;

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

   private static final class Entry extends AbstractInt2ByteMap.BasicEntry implements Cloneable {
      private static final int BLACK_MASK = 1;
      private static final int SUCC_MASK = Integer.MIN_VALUE;
      private static final int PRED_MASK = 1073741824;
      Int2ByteRBTreeMap.Entry left;
      Int2ByteRBTreeMap.Entry right;
      int info;

      Entry() {
         super(0, (byte)0);
      }

      Entry(int k, byte v) {
         super(k, v);
         this.info = -1073741824;
      }

      Int2ByteRBTreeMap.Entry left() {
         return (this.info & 1073741824) != 0 ? null : this.left;
      }

      Int2ByteRBTreeMap.Entry right() {
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

      void pred(Int2ByteRBTreeMap.Entry pred) {
         this.info |= 1073741824;
         this.left = pred;
      }

      void succ(Int2ByteRBTreeMap.Entry succ) {
         this.info |= Integer.MIN_VALUE;
         this.right = succ;
      }

      void left(Int2ByteRBTreeMap.Entry left) {
         this.info &= -1073741825;
         this.left = left;
      }

      void right(Int2ByteRBTreeMap.Entry right) {
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

      Int2ByteRBTreeMap.Entry next() {
         Int2ByteRBTreeMap.Entry next = this.right;
         if ((this.info & -2147483648) == 0) {
            while ((next.info & 1073741824) == 0) {
               next = next.left;
            }
         }

         return next;
      }

      Int2ByteRBTreeMap.Entry prev() {
         Int2ByteRBTreeMap.Entry prev = this.left;
         if ((this.info & 1073741824) == 0) {
            while ((prev.info & -2147483648) == 0) {
               prev = prev.right;
            }
         }

         return prev;
      }

      @Override
      public byte setValue(byte value) {
         byte oldValue = this.value;
         this.value = value;
         return oldValue;
      }

      public Int2ByteRBTreeMap.Entry clone() {
         Int2ByteRBTreeMap.Entry c;
         try {
            c = (Int2ByteRBTreeMap.Entry)super.clone();
         } catch (CloneNotSupportedException var3) {
            throw new InternalError();
         }

         c.key = this.key;
         c.value = this.value;
         c.info = this.info;
         return c;
      }

      @Override
      public boolean equals(Object o) {
         if (!(o instanceof java.util.Map.Entry)) {
            return false;
         } else {
            java.util.Map.Entry<Integer, Byte> e = (java.util.Map.Entry<Integer, Byte>)o;
            return this.key == e.getKey() && this.value == e.getValue();
         }
      }

      @Override
      public int hashCode() {
         return this.key ^ this.value;
      }

      @Override
      public String toString() {
         return this.key + "=>" + this.value;
      }
   }

   private class EntryIterator extends Int2ByteRBTreeMap.TreeIterator implements ObjectListIterator<Int2ByteMap.Entry> {
      EntryIterator() {
      }

      EntryIterator(int k) {
         super(k);
      }

      public Int2ByteMap.Entry next() {
         return this.nextEntry();
      }

      public Int2ByteMap.Entry previous() {
         return this.previousEntry();
      }
   }

   private final class KeyIterator extends Int2ByteRBTreeMap.TreeIterator implements IntListIterator {
      public KeyIterator() {
      }

      public KeyIterator(int k) {
         super(k);
      }

      @Override
      public int nextInt() {
         return this.nextEntry().key;
      }

      @Override
      public int previousInt() {
         return this.previousEntry().key;
      }
   }

   private class KeySet extends AbstractInt2ByteSortedMap.KeySet {
      private KeySet() {
      }

      @Override
      public IntBidirectionalIterator iterator() {
         return Int2ByteRBTreeMap.this.new KeyIterator();
      }

      @Override
      public IntBidirectionalIterator iterator(int from) {
         return Int2ByteRBTreeMap.this.new KeyIterator(from);
      }
   }

   private final class Submap extends AbstractInt2ByteSortedMap implements Serializable {
      private static final long serialVersionUID = -7046029254386353129L;
      int from;
      int to;
      boolean bottom;
      boolean top;
      protected transient ObjectSortedSet<Int2ByteMap.Entry> entries;
      protected transient IntSortedSet keys;
      protected transient ByteCollection values;

      public Submap(int from, boolean bottom, int to, boolean top) {
         if (!bottom && !top && Int2ByteRBTreeMap.this.compare(from, to) > 0) {
            throw new IllegalArgumentException("Start key (" + from + ") is larger than end key (" + to + ")");
         } else {
            this.from = from;
            this.bottom = bottom;
            this.to = to;
            this.top = top;
            this.defRetValue = Int2ByteRBTreeMap.this.defRetValue;
         }
      }

      @Override
      public void clear() {
         Int2ByteRBTreeMap.Submap.SubmapIterator i = new Int2ByteRBTreeMap.Submap.SubmapIterator();

         while (i.hasNext()) {
            i.nextEntry();
            i.remove();
         }
      }

      final boolean in(int k) {
         return (this.bottom || Int2ByteRBTreeMap.this.compare(k, this.from) >= 0) && (this.top || Int2ByteRBTreeMap.this.compare(k, this.to) < 0);
      }

      @Override
      public ObjectSortedSet<Int2ByteMap.Entry> int2ByteEntrySet() {
         if (this.entries == null) {
            this.entries = new AbstractObjectSortedSet<Int2ByteMap.Entry>() {
               @Override
               public ObjectBidirectionalIterator<Int2ByteMap.Entry> iterator() {
                  return Submap.this.new SubmapEntryIterator();
               }

               public ObjectBidirectionalIterator<Int2ByteMap.Entry> iterator(Int2ByteMap.Entry from) {
                  return Submap.this.new SubmapEntryIterator(from.getIntKey());
               }

               @Override
               public Comparator<? super Int2ByteMap.Entry> comparator() {
                  return Int2ByteRBTreeMap.this.int2ByteEntrySet().comparator();
               }

               @Override
               public boolean contains(Object o) {
                  if (!(o instanceof java.util.Map.Entry)) {
                     return false;
                  } else {
                     java.util.Map.Entry<?, ?> e = (java.util.Map.Entry<?, ?>)o;
                     if (e.getKey() == null || !(e.getKey() instanceof Integer)) {
                        return false;
                     } else if (e.getValue() != null && e.getValue() instanceof Byte) {
                        Int2ByteRBTreeMap.Entry f = Int2ByteRBTreeMap.this.findKey((Integer)e.getKey());
                        return f != null && Submap.this.in(f.key) && e.equals(f);
                     } else {
                        return false;
                     }
                  }
               }

               @Override
               public boolean remove(Object o) {
                  if (!(o instanceof java.util.Map.Entry)) {
                     return false;
                  } else {
                     java.util.Map.Entry<?, ?> e = (java.util.Map.Entry<?, ?>)o;
                     if (e.getKey() == null || !(e.getKey() instanceof Integer)) {
                        return false;
                     } else if (e.getValue() != null && e.getValue() instanceof Byte) {
                        Int2ByteRBTreeMap.Entry f = Int2ByteRBTreeMap.this.findKey((Integer)e.getKey());
                        if (f != null && Submap.this.in(f.key)) {
                           Submap.this.remove(f.key);
                        }

                        return f != null;
                     } else {
                        return false;
                     }
                  }
               }

               @Override
               public int size() {
                  int c = 0;
                  Iterator<?> i = this.iterator();

                  while (i.hasNext()) {
                     c++;
                     i.next();
                  }

                  return c;
               }

               @Override
               public boolean isEmpty() {
                  return !Submap.this.new SubmapIterator().hasNext();
               }

               @Override
               public void clear() {
                  Submap.this.clear();
               }

               public Int2ByteMap.Entry first() {
                  return Submap.this.firstEntry();
               }

               public Int2ByteMap.Entry last() {
                  return Submap.this.lastEntry();
               }

               public ObjectSortedSet<Int2ByteMap.Entry> subSet(Int2ByteMap.Entry from, Int2ByteMap.Entry to) {
                  return Submap.this.subMap(from.getIntKey(), to.getIntKey()).int2ByteEntrySet();
               }

               public ObjectSortedSet<Int2ByteMap.Entry> headSet(Int2ByteMap.Entry to) {
                  return Submap.this.headMap(to.getIntKey()).int2ByteEntrySet();
               }

               public ObjectSortedSet<Int2ByteMap.Entry> tailSet(Int2ByteMap.Entry from) {
                  return Submap.this.tailMap(from.getIntKey()).int2ByteEntrySet();
               }
            };
         }

         return this.entries;
      }

      @Override
      public IntSortedSet keySet() {
         if (this.keys == null) {
            this.keys = new Int2ByteRBTreeMap.Submap.KeySet();
         }

         return this.keys;
      }

      @Override
      public ByteCollection values() {
         if (this.values == null) {
            this.values = new AbstractByteCollection() {
               @Override
               public ByteIterator iterator() {
                  return Submap.this.new SubmapValueIterator();
               }

               @Override
               public boolean contains(byte k) {
                  return Submap.this.containsValue(k);
               }

               @Override
               public int size() {
                  return Submap.this.size();
               }

               @Override
               public void clear() {
                  Submap.this.clear();
               }
            };
         }

         return this.values;
      }

      @Override
      public boolean containsKey(int k) {
         return this.in(k) && Int2ByteRBTreeMap.this.containsKey(k);
      }

      @Override
      public boolean containsValue(byte v) {
         Int2ByteRBTreeMap.Submap.SubmapIterator i = new Int2ByteRBTreeMap.Submap.SubmapIterator();

         while (i.hasNext()) {
            byte ev = i.nextEntry().value;
            if (ev == v) {
               return true;
            }
         }

         return false;
      }

      @Override
      public byte get(int k) {
         Int2ByteRBTreeMap.Entry e;
         return this.in(k) && (e = Int2ByteRBTreeMap.this.findKey(k)) != null ? e.value : this.defRetValue;
      }

      @Override
      public byte put(int k, byte v) {
         Int2ByteRBTreeMap.this.modified = false;
         if (!this.in(k)) {
            throw new IllegalArgumentException(
               "Key (" + k + ") out of range [" + (this.bottom ? "-" : String.valueOf(this.from)) + ", " + (this.top ? "-" : String.valueOf(this.to)) + ")"
            );
         } else {
            byte oldValue = Int2ByteRBTreeMap.this.put(k, v);
            return Int2ByteRBTreeMap.this.modified ? this.defRetValue : oldValue;
         }
      }

      @Override
      public byte remove(int k) {
         Int2ByteRBTreeMap.this.modified = false;
         if (!this.in(k)) {
            return this.defRetValue;
         } else {
            byte oldValue = Int2ByteRBTreeMap.this.remove(k);
            return Int2ByteRBTreeMap.this.modified ? oldValue : this.defRetValue;
         }
      }

      @Override
      public int size() {
         Int2ByteRBTreeMap.Submap.SubmapIterator i = new Int2ByteRBTreeMap.Submap.SubmapIterator();
         int n = 0;

         while (i.hasNext()) {
            n++;
            i.nextEntry();
         }

         return n;
      }

      @Override
      public boolean isEmpty() {
         return !new Int2ByteRBTreeMap.Submap.SubmapIterator().hasNext();
      }

      @Override
      public IntComparator comparator() {
         return Int2ByteRBTreeMap.this.actualComparator;
      }

      @Override
      public Int2ByteSortedMap headMap(int to) {
         if (this.top) {
            return Int2ByteRBTreeMap.this.new Submap(this.from, this.bottom, to, false);
         } else {
            return Int2ByteRBTreeMap.this.compare(to, this.to) < 0 ? Int2ByteRBTreeMap.this.new Submap(this.from, this.bottom, to, false) : this;
         }
      }

      @Override
      public Int2ByteSortedMap tailMap(int from) {
         if (this.bottom) {
            return Int2ByteRBTreeMap.this.new Submap(from, false, this.to, this.top);
         } else {
            return Int2ByteRBTreeMap.this.compare(from, this.from) > 0 ? Int2ByteRBTreeMap.this.new Submap(from, false, this.to, this.top) : this;
         }
      }

      @Override
      public Int2ByteSortedMap subMap(int from, int to) {
         if (this.top && this.bottom) {
            return Int2ByteRBTreeMap.this.new Submap(from, false, to, false);
         } else {
            if (!this.top) {
               to = Int2ByteRBTreeMap.this.compare(to, this.to) < 0 ? to : this.to;
            }

            if (!this.bottom) {
               from = Int2ByteRBTreeMap.this.compare(from, this.from) > 0 ? from : this.from;
            }

            return !this.top && !this.bottom && from == this.from && to == this.to ? this : Int2ByteRBTreeMap.this.new Submap(from, false, to, false);
         }
      }

      public Int2ByteRBTreeMap.Entry firstEntry() {
         if (Int2ByteRBTreeMap.this.tree == null) {
            return null;
         } else {
            Int2ByteRBTreeMap.Entry e;
            if (this.bottom) {
               e = Int2ByteRBTreeMap.this.firstEntry;
            } else {
               e = Int2ByteRBTreeMap.this.locateKey(this.from);
               if (Int2ByteRBTreeMap.this.compare(e.key, this.from) < 0) {
                  e = e.next();
               }
            }

            return e != null && (this.top || Int2ByteRBTreeMap.this.compare(e.key, this.to) < 0) ? e : null;
         }
      }

      public Int2ByteRBTreeMap.Entry lastEntry() {
         if (Int2ByteRBTreeMap.this.tree == null) {
            return null;
         } else {
            Int2ByteRBTreeMap.Entry e;
            if (this.top) {
               e = Int2ByteRBTreeMap.this.lastEntry;
            } else {
               e = Int2ByteRBTreeMap.this.locateKey(this.to);
               if (Int2ByteRBTreeMap.this.compare(e.key, this.to) >= 0) {
                  e = e.prev();
               }
            }

            return e != null && (this.bottom || Int2ByteRBTreeMap.this.compare(e.key, this.from) >= 0) ? e : null;
         }
      }

      @Override
      public int firstIntKey() {
         Int2ByteRBTreeMap.Entry e = this.firstEntry();
         if (e == null) {
            throw new NoSuchElementException();
         } else {
            return e.key;
         }
      }

      @Override
      public int lastIntKey() {
         Int2ByteRBTreeMap.Entry e = this.lastEntry();
         if (e == null) {
            throw new NoSuchElementException();
         } else {
            return e.key;
         }
      }

      private class KeySet extends AbstractInt2ByteSortedMap.KeySet {
         private KeySet() {
         }

         @Override
         public IntBidirectionalIterator iterator() {
            return Submap.this.new SubmapKeyIterator();
         }

         @Override
         public IntBidirectionalIterator iterator(int from) {
            return Submap.this.new SubmapKeyIterator(from);
         }
      }

      private class SubmapEntryIterator extends Int2ByteRBTreeMap.Submap.SubmapIterator implements ObjectListIterator<Int2ByteMap.Entry> {
         SubmapEntryIterator() {
         }

         SubmapEntryIterator(int k) {
            super(k);
         }

         public Int2ByteMap.Entry next() {
            return this.nextEntry();
         }

         public Int2ByteMap.Entry previous() {
            return this.previousEntry();
         }
      }

      private class SubmapIterator extends Int2ByteRBTreeMap.TreeIterator {
         SubmapIterator() {
            this.next = Submap.this.firstEntry();
         }

         SubmapIterator(int k) {
            this();
            if (this.next != null) {
               if (!Submap.this.bottom && Int2ByteRBTreeMap.this.compare(k, this.next.key) < 0) {
                  this.prev = null;
               } else if (!Submap.this.top && Int2ByteRBTreeMap.this.compare(k, (this.prev = Submap.this.lastEntry()).key) >= 0) {
                  this.next = null;
               } else {
                  this.next = Int2ByteRBTreeMap.this.locateKey(k);
                  if (Int2ByteRBTreeMap.this.compare(this.next.key, k) <= 0) {
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
            if (!Submap.this.bottom && this.prev != null && Int2ByteRBTreeMap.this.compare(this.prev.key, Submap.this.from) < 0) {
               this.prev = null;
            }
         }

         @Override
         void updateNext() {
            this.next = this.next.next();
            if (!Submap.this.top && this.next != null && Int2ByteRBTreeMap.this.compare(this.next.key, Submap.this.to) >= 0) {
               this.next = null;
            }
         }
      }

      private final class SubmapKeyIterator extends Int2ByteRBTreeMap.Submap.SubmapIterator implements IntListIterator {
         public SubmapKeyIterator() {
         }

         public SubmapKeyIterator(int from) {
            super(from);
         }

         @Override
         public int nextInt() {
            return this.nextEntry().key;
         }

         @Override
         public int previousInt() {
            return this.previousEntry().key;
         }
      }

      private final class SubmapValueIterator extends Int2ByteRBTreeMap.Submap.SubmapIterator implements ByteListIterator {
         private SubmapValueIterator() {
         }

         @Override
         public byte nextByte() {
            return this.nextEntry().value;
         }

         @Override
         public byte previousByte() {
            return this.previousEntry().value;
         }
      }
   }

   private class TreeIterator {
      Int2ByteRBTreeMap.Entry prev;
      Int2ByteRBTreeMap.Entry next;
      Int2ByteRBTreeMap.Entry curr;
      int index = 0;

      TreeIterator() {
         this.next = Int2ByteRBTreeMap.this.firstEntry;
      }

      TreeIterator(int k) {
         if ((this.next = Int2ByteRBTreeMap.this.locateKey(k)) != null) {
            if (Int2ByteRBTreeMap.this.compare(this.next.key, k) <= 0) {
               this.prev = this.next;
               this.next = this.next.next();
            } else {
               this.prev = this.next.prev();
            }
         }
      }

      public boolean hasNext() {
         return this.next != null;
      }

      public boolean hasPrevious() {
         return this.prev != null;
      }

      void updateNext() {
         this.next = this.next.next();
      }

      Int2ByteRBTreeMap.Entry nextEntry() {
         if (!this.hasNext()) {
            throw new NoSuchElementException();
         } else {
            this.curr = this.prev = this.next;
            this.index++;
            this.updateNext();
            return this.curr;
         }
      }

      void updatePrevious() {
         this.prev = this.prev.prev();
      }

      Int2ByteRBTreeMap.Entry previousEntry() {
         if (!this.hasPrevious()) {
            throw new NoSuchElementException();
         } else {
            this.curr = this.next = this.prev;
            this.index--;
            this.updatePrevious();
            return this.curr;
         }
      }

      public int nextIndex() {
         return this.index;
      }

      public int previousIndex() {
         return this.index - 1;
      }

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
            Int2ByteRBTreeMap.this.remove(this.curr.key);
            this.curr = null;
         }
      }

      public int skip(int n) {
         int i = n;

         while (i-- != 0 && this.hasNext()) {
            this.nextEntry();
         }

         return n - i - 1;
      }

      public int back(int n) {
         int i = n;

         while (i-- != 0 && this.hasPrevious()) {
            this.previousEntry();
         }

         return n - i - 1;
      }
   }

   private final class ValueIterator extends Int2ByteRBTreeMap.TreeIterator implements ByteListIterator {
      private ValueIterator() {
      }

      @Override
      public byte nextByte() {
         return this.nextEntry().value;
      }

      @Override
      public byte previousByte() {
         return this.previousEntry().value;
      }
   }
}
