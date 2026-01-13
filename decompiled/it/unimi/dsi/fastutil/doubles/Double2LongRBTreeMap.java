package it.unimi.dsi.fastutil.doubles;

import it.unimi.dsi.fastutil.HashCommon;
import it.unimi.dsi.fastutil.longs.AbstractLongCollection;
import it.unimi.dsi.fastutil.longs.LongCollection;
import it.unimi.dsi.fastutil.longs.LongIterator;
import it.unimi.dsi.fastutil.longs.LongListIterator;
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

public class Double2LongRBTreeMap extends AbstractDouble2LongSortedMap implements Serializable, Cloneable {
   protected transient Double2LongRBTreeMap.Entry tree;
   protected int count;
   protected transient Double2LongRBTreeMap.Entry firstEntry;
   protected transient Double2LongRBTreeMap.Entry lastEntry;
   protected transient ObjectSortedSet<Double2LongMap.Entry> entries;
   protected transient DoubleSortedSet keys;
   protected transient LongCollection values;
   protected transient boolean modified;
   protected Comparator<? super Double> storedComparator;
   protected transient DoubleComparator actualComparator;
   private static final long serialVersionUID = -7046029254386353129L;
   private transient boolean[] dirPath;
   private transient Double2LongRBTreeMap.Entry[] nodePath;

   public Double2LongRBTreeMap() {
      this.allocatePaths();
      this.tree = null;
      this.count = 0;
   }

   private void setActualComparator() {
      this.actualComparator = DoubleComparators.asDoubleComparator(this.storedComparator);
   }

   public Double2LongRBTreeMap(Comparator<? super Double> c) {
      this();
      this.storedComparator = c;
      this.setActualComparator();
   }

   public Double2LongRBTreeMap(Map<? extends Double, ? extends Long> m) {
      this();
      this.putAll(m);
   }

   public Double2LongRBTreeMap(SortedMap<Double, Long> m) {
      this(m.comparator());
      this.putAll(m);
   }

   public Double2LongRBTreeMap(Double2LongMap m) {
      this();
      this.putAll(m);
   }

   public Double2LongRBTreeMap(Double2LongSortedMap m) {
      this(m.comparator());
      this.putAll(m);
   }

   public Double2LongRBTreeMap(double[] k, long[] v, Comparator<? super Double> c) {
      this(c);
      if (k.length != v.length) {
         throw new IllegalArgumentException("The key array and the value array have different lengths (" + k.length + " and " + v.length + ")");
      } else {
         for (int i = 0; i < k.length; i++) {
            this.put(k[i], v[i]);
         }
      }
   }

   public Double2LongRBTreeMap(double[] k, long[] v) {
      this(k, v, null);
   }

   final int compare(double k1, double k2) {
      return this.actualComparator == null ? Double.compare(k1, k2) : this.actualComparator.compare(k1, k2);
   }

   final Double2LongRBTreeMap.Entry findKey(double k) {
      Double2LongRBTreeMap.Entry e = this.tree;

      int cmp;
      while (e != null && (cmp = this.compare(k, e.key)) != 0) {
         e = cmp < 0 ? e.left() : e.right();
      }

      return e;
   }

   final Double2LongRBTreeMap.Entry locateKey(double k) {
      Double2LongRBTreeMap.Entry e = this.tree;
      Double2LongRBTreeMap.Entry last = this.tree;

      int cmp;
      for (cmp = 0; e != null && (cmp = this.compare(k, e.key)) != 0; e = cmp < 0 ? e.left() : e.right()) {
         last = e;
      }

      return cmp == 0 ? e : last;
   }

   private void allocatePaths() {
      this.dirPath = new boolean[64];
      this.nodePath = new Double2LongRBTreeMap.Entry[64];
   }

   public long addTo(double k, long incr) {
      Double2LongRBTreeMap.Entry e = this.add(k);
      long oldValue = e.value;
      e.value += incr;
      return oldValue;
   }

   @Override
   public long put(double k, long v) {
      Double2LongRBTreeMap.Entry e = this.add(k);
      long oldValue = e.value;
      e.value = v;
      return oldValue;
   }

   private Double2LongRBTreeMap.Entry add(double k) {
      this.modified = false;
      int maxDepth = 0;
      Double2LongRBTreeMap.Entry e;
      if (this.tree == null) {
         this.count++;
         e = this.tree = this.lastEntry = this.firstEntry = new Double2LongRBTreeMap.Entry(k, this.defRetValue);
      } else {
         Double2LongRBTreeMap.Entry p = this.tree;
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
               e = new Double2LongRBTreeMap.Entry(k, this.defRetValue);
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
               e = new Double2LongRBTreeMap.Entry(k, this.defRetValue);
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
                  Double2LongRBTreeMap.Entry y = this.nodePath[i - 1].right;
                  if (this.nodePath[i - 1].succ() || y.black()) {
                     if (!this.dirPath[i]) {
                        y = this.nodePath[i];
                     } else {
                        Double2LongRBTreeMap.Entry x = this.nodePath[i];
                        y = x.right;
                        x.right = y.left;
                        y.left = x;
                        this.nodePath[i - 1].left = y;
                        if (y.pred()) {
                           y.pred(false);
                           x.succ(y);
                        }
                     }

                     Double2LongRBTreeMap.Entry xx = this.nodePath[i - 1];
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
                  Double2LongRBTreeMap.Entry y = this.nodePath[i - 1].left;
                  if (this.nodePath[i - 1].pred() || y.black()) {
                     if (this.dirPath[i]) {
                        y = this.nodePath[i];
                     } else {
                        Double2LongRBTreeMap.Entry x = this.nodePath[i];
                        y = x.left;
                        x.left = y.right;
                        y.right = x;
                        this.nodePath[i - 1].right = y;
                        if (y.succ()) {
                           y.succ(false);
                           x.pred(y);
                        }
                     }

                     Double2LongRBTreeMap.Entry xxx = this.nodePath[i - 1];
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
   public long remove(double k) {
      this.modified = false;
      if (this.tree == null) {
         return this.defRetValue;
      } else {
         Double2LongRBTreeMap.Entry p = this.tree;
         int i = 0;
         double kk = k;

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
            Double2LongRBTreeMap.Entry r = p.right;
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
                  Double2LongRBTreeMap.Entry s = r.left;
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
                  Double2LongRBTreeMap.Entry x = this.dirPath[i - 1] ? this.nodePath[i - 1].right : this.nodePath[i - 1].left;
                  if (!x.black()) {
                     x.black(true);
                     break;
                  }
               }

               if (!this.dirPath[i - 1]) {
                  Double2LongRBTreeMap.Entry w = this.nodePath[i - 1].right;
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
                        Double2LongRBTreeMap.Entry y = w.left;
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
                  Double2LongRBTreeMap.Entry wx = this.nodePath[i - 1].left;
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
                        Double2LongRBTreeMap.Entry y = wx.right;
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
   public boolean containsValue(long v) {
      Double2LongRBTreeMap.ValueIterator i = new Double2LongRBTreeMap.ValueIterator();
      int j = this.count;

      while (j-- != 0) {
         long ev = i.nextLong();
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
   public boolean containsKey(double k) {
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
   public long get(double k) {
      Double2LongRBTreeMap.Entry e = this.findKey(k);
      return e == null ? this.defRetValue : e.value;
   }

   @Override
   public double firstDoubleKey() {
      if (this.tree == null) {
         throw new NoSuchElementException();
      } else {
         return this.firstEntry.key;
      }
   }

   @Override
   public double lastDoubleKey() {
      if (this.tree == null) {
         throw new NoSuchElementException();
      } else {
         return this.lastEntry.key;
      }
   }

   @Override
   public ObjectSortedSet<Double2LongMap.Entry> double2LongEntrySet() {
      if (this.entries == null) {
         this.entries = new AbstractObjectSortedSet<Double2LongMap.Entry>() {
            final Comparator<? super Double2LongMap.Entry> comparator = Double2LongRBTreeMap.this.actualComparator == null
               ? (x, y) -> Double.compare(x.getDoubleKey(), y.getDoubleKey())
               : (x, y) -> Double2LongRBTreeMap.this.actualComparator.compare(x.getDoubleKey(), y.getDoubleKey());

            @Override
            public Comparator<? super Double2LongMap.Entry> comparator() {
               return this.comparator;
            }

            @Override
            public ObjectBidirectionalIterator<Double2LongMap.Entry> iterator() {
               return Double2LongRBTreeMap.this.new EntryIterator();
            }

            public ObjectBidirectionalIterator<Double2LongMap.Entry> iterator(Double2LongMap.Entry from) {
               return Double2LongRBTreeMap.this.new EntryIterator(from.getDoubleKey());
            }

            @Override
            public boolean contains(Object o) {
               if (o != null && o instanceof java.util.Map.Entry) {
                  java.util.Map.Entry<?, ?> e = (java.util.Map.Entry<?, ?>)o;
                  if (e.getKey() == null) {
                     return false;
                  } else if (!(e.getKey() instanceof Double)) {
                     return false;
                  } else if (e.getValue() != null && e.getValue() instanceof Long) {
                     Double2LongRBTreeMap.Entry f = Double2LongRBTreeMap.this.findKey((Double)e.getKey());
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
                  } else if (!(e.getKey() instanceof Double)) {
                     return false;
                  } else if (e.getValue() != null && e.getValue() instanceof Long) {
                     Double2LongRBTreeMap.Entry f = Double2LongRBTreeMap.this.findKey((Double)e.getKey());
                     if (f != null && f.getLongValue() == (Long)e.getValue()) {
                        Double2LongRBTreeMap.this.remove(f.key);
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
               return Double2LongRBTreeMap.this.count;
            }

            @Override
            public void clear() {
               Double2LongRBTreeMap.this.clear();
            }

            public Double2LongMap.Entry first() {
               return Double2LongRBTreeMap.this.firstEntry;
            }

            public Double2LongMap.Entry last() {
               return Double2LongRBTreeMap.this.lastEntry;
            }

            public ObjectSortedSet<Double2LongMap.Entry> subSet(Double2LongMap.Entry from, Double2LongMap.Entry to) {
               return Double2LongRBTreeMap.this.subMap(from.getDoubleKey(), to.getDoubleKey()).double2LongEntrySet();
            }

            public ObjectSortedSet<Double2LongMap.Entry> headSet(Double2LongMap.Entry to) {
               return Double2LongRBTreeMap.this.headMap(to.getDoubleKey()).double2LongEntrySet();
            }

            public ObjectSortedSet<Double2LongMap.Entry> tailSet(Double2LongMap.Entry from) {
               return Double2LongRBTreeMap.this.tailMap(from.getDoubleKey()).double2LongEntrySet();
            }
         };
      }

      return this.entries;
   }

   @Override
   public DoubleSortedSet keySet() {
      if (this.keys == null) {
         this.keys = new Double2LongRBTreeMap.KeySet();
      }

      return this.keys;
   }

   @Override
   public LongCollection values() {
      if (this.values == null) {
         this.values = new AbstractLongCollection() {
            @Override
            public LongIterator iterator() {
               return Double2LongRBTreeMap.this.new ValueIterator();
            }

            @Override
            public boolean contains(long k) {
               return Double2LongRBTreeMap.this.containsValue(k);
            }

            @Override
            public int size() {
               return Double2LongRBTreeMap.this.count;
            }

            @Override
            public void clear() {
               Double2LongRBTreeMap.this.clear();
            }
         };
      }

      return this.values;
   }

   @Override
   public DoubleComparator comparator() {
      return this.actualComparator;
   }

   @Override
   public Double2LongSortedMap headMap(double to) {
      return new Double2LongRBTreeMap.Submap(0.0, true, to, false);
   }

   @Override
   public Double2LongSortedMap tailMap(double from) {
      return new Double2LongRBTreeMap.Submap(from, false, 0.0, true);
   }

   @Override
   public Double2LongSortedMap subMap(double from, double to) {
      return new Double2LongRBTreeMap.Submap(from, false, to, false);
   }

   public Double2LongRBTreeMap clone() {
      Double2LongRBTreeMap c;
      try {
         c = (Double2LongRBTreeMap)super.clone();
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
         Double2LongRBTreeMap.Entry rp = new Double2LongRBTreeMap.Entry();
         Double2LongRBTreeMap.Entry rq = new Double2LongRBTreeMap.Entry();
         Double2LongRBTreeMap.Entry p = rp;
         rp.left(this.tree);
         Double2LongRBTreeMap.Entry q = rq;
         rq.pred(null);

         while (true) {
            if (!p.pred()) {
               Double2LongRBTreeMap.Entry e = p.left.clone();
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
               Double2LongRBTreeMap.Entry e = p.right.clone();
               e.succ(q.right);
               e.pred(q);
               q.right(e);
            }
         }
      }
   }

   private void writeObject(ObjectOutputStream s) throws IOException {
      int n = this.count;
      Double2LongRBTreeMap.EntryIterator i = new Double2LongRBTreeMap.EntryIterator();
      s.defaultWriteObject();

      while (n-- != 0) {
         Double2LongRBTreeMap.Entry e = i.nextEntry();
         s.writeDouble(e.key);
         s.writeLong(e.value);
      }
   }

   private Double2LongRBTreeMap.Entry readTree(ObjectInputStream s, int n, Double2LongRBTreeMap.Entry pred, Double2LongRBTreeMap.Entry succ) throws IOException, ClassNotFoundException {
      if (n == 1) {
         Double2LongRBTreeMap.Entry top = new Double2LongRBTreeMap.Entry(s.readDouble(), s.readLong());
         top.pred(pred);
         top.succ(succ);
         top.black(true);
         return top;
      } else if (n == 2) {
         Double2LongRBTreeMap.Entry top = new Double2LongRBTreeMap.Entry(s.readDouble(), s.readLong());
         top.black(true);
         top.right(new Double2LongRBTreeMap.Entry(s.readDouble(), s.readLong()));
         top.right.pred(top);
         top.pred(pred);
         top.right.succ(succ);
         return top;
      } else {
         int rightN = n / 2;
         int leftN = n - rightN - 1;
         Double2LongRBTreeMap.Entry top = new Double2LongRBTreeMap.Entry();
         top.left(this.readTree(s, leftN, pred, top));
         top.key = s.readDouble();
         top.value = s.readLong();
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
         Double2LongRBTreeMap.Entry e = this.tree;

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

   private static final class Entry extends AbstractDouble2LongMap.BasicEntry implements Cloneable {
      private static final int BLACK_MASK = 1;
      private static final int SUCC_MASK = Integer.MIN_VALUE;
      private static final int PRED_MASK = 1073741824;
      Double2LongRBTreeMap.Entry left;
      Double2LongRBTreeMap.Entry right;
      int info;

      Entry() {
         super(0.0, 0L);
      }

      Entry(double k, long v) {
         super(k, v);
         this.info = -1073741824;
      }

      Double2LongRBTreeMap.Entry left() {
         return (this.info & 1073741824) != 0 ? null : this.left;
      }

      Double2LongRBTreeMap.Entry right() {
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

      void pred(Double2LongRBTreeMap.Entry pred) {
         this.info |= 1073741824;
         this.left = pred;
      }

      void succ(Double2LongRBTreeMap.Entry succ) {
         this.info |= Integer.MIN_VALUE;
         this.right = succ;
      }

      void left(Double2LongRBTreeMap.Entry left) {
         this.info &= -1073741825;
         this.left = left;
      }

      void right(Double2LongRBTreeMap.Entry right) {
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

      Double2LongRBTreeMap.Entry next() {
         Double2LongRBTreeMap.Entry next = this.right;
         if ((this.info & -2147483648) == 0) {
            while ((next.info & 1073741824) == 0) {
               next = next.left;
            }
         }

         return next;
      }

      Double2LongRBTreeMap.Entry prev() {
         Double2LongRBTreeMap.Entry prev = this.left;
         if ((this.info & 1073741824) == 0) {
            while ((prev.info & -2147483648) == 0) {
               prev = prev.right;
            }
         }

         return prev;
      }

      @Override
      public long setValue(long value) {
         long oldValue = this.value;
         this.value = value;
         return oldValue;
      }

      public Double2LongRBTreeMap.Entry clone() {
         Double2LongRBTreeMap.Entry c;
         try {
            c = (Double2LongRBTreeMap.Entry)super.clone();
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
            java.util.Map.Entry<Double, Long> e = (java.util.Map.Entry<Double, Long>)o;
            return Double.doubleToLongBits(this.key) == Double.doubleToLongBits(e.getKey()) && this.value == e.getValue();
         }
      }

      @Override
      public int hashCode() {
         return HashCommon.double2int(this.key) ^ HashCommon.long2int(this.value);
      }

      @Override
      public String toString() {
         return this.key + "=>" + this.value;
      }
   }

   private class EntryIterator extends Double2LongRBTreeMap.TreeIterator implements ObjectListIterator<Double2LongMap.Entry> {
      EntryIterator() {
      }

      EntryIterator(double k) {
         super(k);
      }

      public Double2LongMap.Entry next() {
         return this.nextEntry();
      }

      public Double2LongMap.Entry previous() {
         return this.previousEntry();
      }
   }

   private final class KeyIterator extends Double2LongRBTreeMap.TreeIterator implements DoubleListIterator {
      public KeyIterator() {
      }

      public KeyIterator(double k) {
         super(k);
      }

      @Override
      public double nextDouble() {
         return this.nextEntry().key;
      }

      @Override
      public double previousDouble() {
         return this.previousEntry().key;
      }
   }

   private class KeySet extends AbstractDouble2LongSortedMap.KeySet {
      private KeySet() {
      }

      @Override
      public DoubleBidirectionalIterator iterator() {
         return Double2LongRBTreeMap.this.new KeyIterator();
      }

      @Override
      public DoubleBidirectionalIterator iterator(double from) {
         return Double2LongRBTreeMap.this.new KeyIterator(from);
      }
   }

   private final class Submap extends AbstractDouble2LongSortedMap implements Serializable {
      private static final long serialVersionUID = -7046029254386353129L;
      double from;
      double to;
      boolean bottom;
      boolean top;
      protected transient ObjectSortedSet<Double2LongMap.Entry> entries;
      protected transient DoubleSortedSet keys;
      protected transient LongCollection values;

      public Submap(double from, boolean bottom, double to, boolean top) {
         if (!bottom && !top && Double2LongRBTreeMap.this.compare(from, to) > 0) {
            throw new IllegalArgumentException("Start key (" + from + ") is larger than end key (" + to + ")");
         } else {
            this.from = from;
            this.bottom = bottom;
            this.to = to;
            this.top = top;
            this.defRetValue = Double2LongRBTreeMap.this.defRetValue;
         }
      }

      @Override
      public void clear() {
         Double2LongRBTreeMap.Submap.SubmapIterator i = new Double2LongRBTreeMap.Submap.SubmapIterator();

         while (i.hasNext()) {
            i.nextEntry();
            i.remove();
         }
      }

      final boolean in(double k) {
         return (this.bottom || Double2LongRBTreeMap.this.compare(k, this.from) >= 0) && (this.top || Double2LongRBTreeMap.this.compare(k, this.to) < 0);
      }

      @Override
      public ObjectSortedSet<Double2LongMap.Entry> double2LongEntrySet() {
         if (this.entries == null) {
            this.entries = new AbstractObjectSortedSet<Double2LongMap.Entry>() {
               @Override
               public ObjectBidirectionalIterator<Double2LongMap.Entry> iterator() {
                  return Submap.this.new SubmapEntryIterator();
               }

               public ObjectBidirectionalIterator<Double2LongMap.Entry> iterator(Double2LongMap.Entry from) {
                  return Submap.this.new SubmapEntryIterator(from.getDoubleKey());
               }

               @Override
               public Comparator<? super Double2LongMap.Entry> comparator() {
                  return Double2LongRBTreeMap.this.double2LongEntrySet().comparator();
               }

               @Override
               public boolean contains(Object o) {
                  if (!(o instanceof java.util.Map.Entry)) {
                     return false;
                  } else {
                     java.util.Map.Entry<?, ?> e = (java.util.Map.Entry<?, ?>)o;
                     if (e.getKey() == null || !(e.getKey() instanceof Double)) {
                        return false;
                     } else if (e.getValue() != null && e.getValue() instanceof Long) {
                        Double2LongRBTreeMap.Entry f = Double2LongRBTreeMap.this.findKey((Double)e.getKey());
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
                     if (e.getKey() == null || !(e.getKey() instanceof Double)) {
                        return false;
                     } else if (e.getValue() != null && e.getValue() instanceof Long) {
                        Double2LongRBTreeMap.Entry f = Double2LongRBTreeMap.this.findKey((Double)e.getKey());
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

               public Double2LongMap.Entry first() {
                  return Submap.this.firstEntry();
               }

               public Double2LongMap.Entry last() {
                  return Submap.this.lastEntry();
               }

               public ObjectSortedSet<Double2LongMap.Entry> subSet(Double2LongMap.Entry from, Double2LongMap.Entry to) {
                  return Submap.this.subMap(from.getDoubleKey(), to.getDoubleKey()).double2LongEntrySet();
               }

               public ObjectSortedSet<Double2LongMap.Entry> headSet(Double2LongMap.Entry to) {
                  return Submap.this.headMap(to.getDoubleKey()).double2LongEntrySet();
               }

               public ObjectSortedSet<Double2LongMap.Entry> tailSet(Double2LongMap.Entry from) {
                  return Submap.this.tailMap(from.getDoubleKey()).double2LongEntrySet();
               }
            };
         }

         return this.entries;
      }

      @Override
      public DoubleSortedSet keySet() {
         if (this.keys == null) {
            this.keys = new Double2LongRBTreeMap.Submap.KeySet();
         }

         return this.keys;
      }

      @Override
      public LongCollection values() {
         if (this.values == null) {
            this.values = new AbstractLongCollection() {
               @Override
               public LongIterator iterator() {
                  return Submap.this.new SubmapValueIterator();
               }

               @Override
               public boolean contains(long k) {
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
      public boolean containsKey(double k) {
         return this.in(k) && Double2LongRBTreeMap.this.containsKey(k);
      }

      @Override
      public boolean containsValue(long v) {
         Double2LongRBTreeMap.Submap.SubmapIterator i = new Double2LongRBTreeMap.Submap.SubmapIterator();

         while (i.hasNext()) {
            long ev = i.nextEntry().value;
            if (ev == v) {
               return true;
            }
         }

         return false;
      }

      @Override
      public long get(double k) {
         Double2LongRBTreeMap.Entry e;
         return this.in(k) && (e = Double2LongRBTreeMap.this.findKey(k)) != null ? e.value : this.defRetValue;
      }

      @Override
      public long put(double k, long v) {
         Double2LongRBTreeMap.this.modified = false;
         if (!this.in(k)) {
            throw new IllegalArgumentException(
               "Key (" + k + ") out of range [" + (this.bottom ? "-" : String.valueOf(this.from)) + ", " + (this.top ? "-" : String.valueOf(this.to)) + ")"
            );
         } else {
            long oldValue = Double2LongRBTreeMap.this.put(k, v);
            return Double2LongRBTreeMap.this.modified ? this.defRetValue : oldValue;
         }
      }

      @Override
      public long remove(double k) {
         Double2LongRBTreeMap.this.modified = false;
         if (!this.in(k)) {
            return this.defRetValue;
         } else {
            long oldValue = Double2LongRBTreeMap.this.remove(k);
            return Double2LongRBTreeMap.this.modified ? oldValue : this.defRetValue;
         }
      }

      @Override
      public int size() {
         Double2LongRBTreeMap.Submap.SubmapIterator i = new Double2LongRBTreeMap.Submap.SubmapIterator();
         int n = 0;

         while (i.hasNext()) {
            n++;
            i.nextEntry();
         }

         return n;
      }

      @Override
      public boolean isEmpty() {
         return !new Double2LongRBTreeMap.Submap.SubmapIterator().hasNext();
      }

      @Override
      public DoubleComparator comparator() {
         return Double2LongRBTreeMap.this.actualComparator;
      }

      @Override
      public Double2LongSortedMap headMap(double to) {
         if (this.top) {
            return Double2LongRBTreeMap.this.new Submap(this.from, this.bottom, to, false);
         } else {
            return Double2LongRBTreeMap.this.compare(to, this.to) < 0 ? Double2LongRBTreeMap.this.new Submap(this.from, this.bottom, to, false) : this;
         }
      }

      @Override
      public Double2LongSortedMap tailMap(double from) {
         if (this.bottom) {
            return Double2LongRBTreeMap.this.new Submap(from, false, this.to, this.top);
         } else {
            return Double2LongRBTreeMap.this.compare(from, this.from) > 0 ? Double2LongRBTreeMap.this.new Submap(from, false, this.to, this.top) : this;
         }
      }

      @Override
      public Double2LongSortedMap subMap(double from, double to) {
         if (this.top && this.bottom) {
            return Double2LongRBTreeMap.this.new Submap(from, false, to, false);
         } else {
            if (!this.top) {
               to = Double2LongRBTreeMap.this.compare(to, this.to) < 0 ? to : this.to;
            }

            if (!this.bottom) {
               from = Double2LongRBTreeMap.this.compare(from, this.from) > 0 ? from : this.from;
            }

            return !this.top && !this.bottom && from == this.from && to == this.to ? this : Double2LongRBTreeMap.this.new Submap(from, false, to, false);
         }
      }

      public Double2LongRBTreeMap.Entry firstEntry() {
         if (Double2LongRBTreeMap.this.tree == null) {
            return null;
         } else {
            Double2LongRBTreeMap.Entry e;
            if (this.bottom) {
               e = Double2LongRBTreeMap.this.firstEntry;
            } else {
               e = Double2LongRBTreeMap.this.locateKey(this.from);
               if (Double2LongRBTreeMap.this.compare(e.key, this.from) < 0) {
                  e = e.next();
               }
            }

            return e != null && (this.top || Double2LongRBTreeMap.this.compare(e.key, this.to) < 0) ? e : null;
         }
      }

      public Double2LongRBTreeMap.Entry lastEntry() {
         if (Double2LongRBTreeMap.this.tree == null) {
            return null;
         } else {
            Double2LongRBTreeMap.Entry e;
            if (this.top) {
               e = Double2LongRBTreeMap.this.lastEntry;
            } else {
               e = Double2LongRBTreeMap.this.locateKey(this.to);
               if (Double2LongRBTreeMap.this.compare(e.key, this.to) >= 0) {
                  e = e.prev();
               }
            }

            return e != null && (this.bottom || Double2LongRBTreeMap.this.compare(e.key, this.from) >= 0) ? e : null;
         }
      }

      @Override
      public double firstDoubleKey() {
         Double2LongRBTreeMap.Entry e = this.firstEntry();
         if (e == null) {
            throw new NoSuchElementException();
         } else {
            return e.key;
         }
      }

      @Override
      public double lastDoubleKey() {
         Double2LongRBTreeMap.Entry e = this.lastEntry();
         if (e == null) {
            throw new NoSuchElementException();
         } else {
            return e.key;
         }
      }

      private class KeySet extends AbstractDouble2LongSortedMap.KeySet {
         private KeySet() {
         }

         @Override
         public DoubleBidirectionalIterator iterator() {
            return Submap.this.new SubmapKeyIterator();
         }

         @Override
         public DoubleBidirectionalIterator iterator(double from) {
            return Submap.this.new SubmapKeyIterator(from);
         }
      }

      private class SubmapEntryIterator extends Double2LongRBTreeMap.Submap.SubmapIterator implements ObjectListIterator<Double2LongMap.Entry> {
         SubmapEntryIterator() {
         }

         SubmapEntryIterator(double k) {
            super(k);
         }

         public Double2LongMap.Entry next() {
            return this.nextEntry();
         }

         public Double2LongMap.Entry previous() {
            return this.previousEntry();
         }
      }

      private class SubmapIterator extends Double2LongRBTreeMap.TreeIterator {
         SubmapIterator() {
            this.next = Submap.this.firstEntry();
         }

         SubmapIterator(double k) {
            this();
            if (this.next != null) {
               if (!Submap.this.bottom && Double2LongRBTreeMap.this.compare(k, this.next.key) < 0) {
                  this.prev = null;
               } else if (!Submap.this.top && Double2LongRBTreeMap.this.compare(k, (this.prev = Submap.this.lastEntry()).key) >= 0) {
                  this.next = null;
               } else {
                  this.next = Double2LongRBTreeMap.this.locateKey(k);
                  if (Double2LongRBTreeMap.this.compare(this.next.key, k) <= 0) {
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
            if (!Submap.this.bottom && this.prev != null && Double2LongRBTreeMap.this.compare(this.prev.key, Submap.this.from) < 0) {
               this.prev = null;
            }
         }

         @Override
         void updateNext() {
            this.next = this.next.next();
            if (!Submap.this.top && this.next != null && Double2LongRBTreeMap.this.compare(this.next.key, Submap.this.to) >= 0) {
               this.next = null;
            }
         }
      }

      private final class SubmapKeyIterator extends Double2LongRBTreeMap.Submap.SubmapIterator implements DoubleListIterator {
         public SubmapKeyIterator() {
         }

         public SubmapKeyIterator(double from) {
            super(from);
         }

         @Override
         public double nextDouble() {
            return this.nextEntry().key;
         }

         @Override
         public double previousDouble() {
            return this.previousEntry().key;
         }
      }

      private final class SubmapValueIterator extends Double2LongRBTreeMap.Submap.SubmapIterator implements LongListIterator {
         private SubmapValueIterator() {
         }

         @Override
         public long nextLong() {
            return this.nextEntry().value;
         }

         @Override
         public long previousLong() {
            return this.previousEntry().value;
         }
      }
   }

   private class TreeIterator {
      Double2LongRBTreeMap.Entry prev;
      Double2LongRBTreeMap.Entry next;
      Double2LongRBTreeMap.Entry curr;
      int index = 0;

      TreeIterator() {
         this.next = Double2LongRBTreeMap.this.firstEntry;
      }

      TreeIterator(double k) {
         if ((this.next = Double2LongRBTreeMap.this.locateKey(k)) != null) {
            if (Double2LongRBTreeMap.this.compare(this.next.key, k) <= 0) {
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

      Double2LongRBTreeMap.Entry nextEntry() {
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

      Double2LongRBTreeMap.Entry previousEntry() {
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
            Double2LongRBTreeMap.this.remove(this.curr.key);
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

   private final class ValueIterator extends Double2LongRBTreeMap.TreeIterator implements LongListIterator {
      private ValueIterator() {
      }

      @Override
      public long nextLong() {
         return this.nextEntry().value;
      }

      @Override
      public long previousLong() {
         return this.previousEntry().value;
      }
   }
}
