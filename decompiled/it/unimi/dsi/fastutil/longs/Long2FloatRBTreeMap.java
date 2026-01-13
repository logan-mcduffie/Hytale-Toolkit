package it.unimi.dsi.fastutil.longs;

import it.unimi.dsi.fastutil.HashCommon;
import it.unimi.dsi.fastutil.floats.AbstractFloatCollection;
import it.unimi.dsi.fastutil.floats.FloatCollection;
import it.unimi.dsi.fastutil.floats.FloatIterator;
import it.unimi.dsi.fastutil.floats.FloatListIterator;
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

public class Long2FloatRBTreeMap extends AbstractLong2FloatSortedMap implements Serializable, Cloneable {
   protected transient Long2FloatRBTreeMap.Entry tree;
   protected int count;
   protected transient Long2FloatRBTreeMap.Entry firstEntry;
   protected transient Long2FloatRBTreeMap.Entry lastEntry;
   protected transient ObjectSortedSet<Long2FloatMap.Entry> entries;
   protected transient LongSortedSet keys;
   protected transient FloatCollection values;
   protected transient boolean modified;
   protected Comparator<? super Long> storedComparator;
   protected transient LongComparator actualComparator;
   private static final long serialVersionUID = -7046029254386353129L;
   private transient boolean[] dirPath;
   private transient Long2FloatRBTreeMap.Entry[] nodePath;

   public Long2FloatRBTreeMap() {
      this.allocatePaths();
      this.tree = null;
      this.count = 0;
   }

   private void setActualComparator() {
      this.actualComparator = LongComparators.asLongComparator(this.storedComparator);
   }

   public Long2FloatRBTreeMap(Comparator<? super Long> c) {
      this();
      this.storedComparator = c;
      this.setActualComparator();
   }

   public Long2FloatRBTreeMap(Map<? extends Long, ? extends Float> m) {
      this();
      this.putAll(m);
   }

   public Long2FloatRBTreeMap(SortedMap<Long, Float> m) {
      this(m.comparator());
      this.putAll(m);
   }

   public Long2FloatRBTreeMap(Long2FloatMap m) {
      this();
      this.putAll(m);
   }

   public Long2FloatRBTreeMap(Long2FloatSortedMap m) {
      this(m.comparator());
      this.putAll(m);
   }

   public Long2FloatRBTreeMap(long[] k, float[] v, Comparator<? super Long> c) {
      this(c);
      if (k.length != v.length) {
         throw new IllegalArgumentException("The key array and the value array have different lengths (" + k.length + " and " + v.length + ")");
      } else {
         for (int i = 0; i < k.length; i++) {
            this.put(k[i], v[i]);
         }
      }
   }

   public Long2FloatRBTreeMap(long[] k, float[] v) {
      this(k, v, null);
   }

   final int compare(long k1, long k2) {
      return this.actualComparator == null ? Long.compare(k1, k2) : this.actualComparator.compare(k1, k2);
   }

   final Long2FloatRBTreeMap.Entry findKey(long k) {
      Long2FloatRBTreeMap.Entry e = this.tree;

      int cmp;
      while (e != null && (cmp = this.compare(k, e.key)) != 0) {
         e = cmp < 0 ? e.left() : e.right();
      }

      return e;
   }

   final Long2FloatRBTreeMap.Entry locateKey(long k) {
      Long2FloatRBTreeMap.Entry e = this.tree;
      Long2FloatRBTreeMap.Entry last = this.tree;

      int cmp;
      for (cmp = 0; e != null && (cmp = this.compare(k, e.key)) != 0; e = cmp < 0 ? e.left() : e.right()) {
         last = e;
      }

      return cmp == 0 ? e : last;
   }

   private void allocatePaths() {
      this.dirPath = new boolean[64];
      this.nodePath = new Long2FloatRBTreeMap.Entry[64];
   }

   public float addTo(long k, float incr) {
      Long2FloatRBTreeMap.Entry e = this.add(k);
      float oldValue = e.value;
      e.value += incr;
      return oldValue;
   }

   @Override
   public float put(long k, float v) {
      Long2FloatRBTreeMap.Entry e = this.add(k);
      float oldValue = e.value;
      e.value = v;
      return oldValue;
   }

   private Long2FloatRBTreeMap.Entry add(long k) {
      this.modified = false;
      int maxDepth = 0;
      Long2FloatRBTreeMap.Entry e;
      if (this.tree == null) {
         this.count++;
         e = this.tree = this.lastEntry = this.firstEntry = new Long2FloatRBTreeMap.Entry(k, this.defRetValue);
      } else {
         Long2FloatRBTreeMap.Entry p = this.tree;
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
               e = new Long2FloatRBTreeMap.Entry(k, this.defRetValue);
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
               e = new Long2FloatRBTreeMap.Entry(k, this.defRetValue);
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
                  Long2FloatRBTreeMap.Entry y = this.nodePath[i - 1].right;
                  if (this.nodePath[i - 1].succ() || y.black()) {
                     if (!this.dirPath[i]) {
                        y = this.nodePath[i];
                     } else {
                        Long2FloatRBTreeMap.Entry x = this.nodePath[i];
                        y = x.right;
                        x.right = y.left;
                        y.left = x;
                        this.nodePath[i - 1].left = y;
                        if (y.pred()) {
                           y.pred(false);
                           x.succ(y);
                        }
                     }

                     Long2FloatRBTreeMap.Entry xx = this.nodePath[i - 1];
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
                  Long2FloatRBTreeMap.Entry y = this.nodePath[i - 1].left;
                  if (this.nodePath[i - 1].pred() || y.black()) {
                     if (this.dirPath[i]) {
                        y = this.nodePath[i];
                     } else {
                        Long2FloatRBTreeMap.Entry x = this.nodePath[i];
                        y = x.left;
                        x.left = y.right;
                        y.right = x;
                        this.nodePath[i - 1].right = y;
                        if (y.succ()) {
                           y.succ(false);
                           x.pred(y);
                        }
                     }

                     Long2FloatRBTreeMap.Entry xxx = this.nodePath[i - 1];
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
   public float remove(long k) {
      this.modified = false;
      if (this.tree == null) {
         return this.defRetValue;
      } else {
         Long2FloatRBTreeMap.Entry p = this.tree;
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
            Long2FloatRBTreeMap.Entry r = p.right;
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
                  Long2FloatRBTreeMap.Entry s = r.left;
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
                  Long2FloatRBTreeMap.Entry x = this.dirPath[i - 1] ? this.nodePath[i - 1].right : this.nodePath[i - 1].left;
                  if (!x.black()) {
                     x.black(true);
                     break;
                  }
               }

               if (!this.dirPath[i - 1]) {
                  Long2FloatRBTreeMap.Entry w = this.nodePath[i - 1].right;
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
                        Long2FloatRBTreeMap.Entry y = w.left;
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
                  Long2FloatRBTreeMap.Entry wx = this.nodePath[i - 1].left;
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
                        Long2FloatRBTreeMap.Entry y = wx.right;
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
   public boolean containsValue(float v) {
      Long2FloatRBTreeMap.ValueIterator i = new Long2FloatRBTreeMap.ValueIterator();
      int j = this.count;

      while (j-- != 0) {
         float ev = i.nextFloat();
         if (Float.floatToIntBits(ev) == Float.floatToIntBits(v)) {
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
   public boolean containsKey(long k) {
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
   public float get(long k) {
      Long2FloatRBTreeMap.Entry e = this.findKey(k);
      return e == null ? this.defRetValue : e.value;
   }

   @Override
   public long firstLongKey() {
      if (this.tree == null) {
         throw new NoSuchElementException();
      } else {
         return this.firstEntry.key;
      }
   }

   @Override
   public long lastLongKey() {
      if (this.tree == null) {
         throw new NoSuchElementException();
      } else {
         return this.lastEntry.key;
      }
   }

   @Override
   public ObjectSortedSet<Long2FloatMap.Entry> long2FloatEntrySet() {
      if (this.entries == null) {
         this.entries = new AbstractObjectSortedSet<Long2FloatMap.Entry>() {
            final Comparator<? super Long2FloatMap.Entry> comparator = Long2FloatRBTreeMap.this.actualComparator == null
               ? (x, y) -> Long.compare(x.getLongKey(), y.getLongKey())
               : (x, y) -> Long2FloatRBTreeMap.this.actualComparator.compare(x.getLongKey(), y.getLongKey());

            @Override
            public Comparator<? super Long2FloatMap.Entry> comparator() {
               return this.comparator;
            }

            @Override
            public ObjectBidirectionalIterator<Long2FloatMap.Entry> iterator() {
               return Long2FloatRBTreeMap.this.new EntryIterator();
            }

            public ObjectBidirectionalIterator<Long2FloatMap.Entry> iterator(Long2FloatMap.Entry from) {
               return Long2FloatRBTreeMap.this.new EntryIterator(from.getLongKey());
            }

            @Override
            public boolean contains(Object o) {
               if (o != null && o instanceof java.util.Map.Entry) {
                  java.util.Map.Entry<?, ?> e = (java.util.Map.Entry<?, ?>)o;
                  if (e.getKey() == null) {
                     return false;
                  } else if (!(e.getKey() instanceof Long)) {
                     return false;
                  } else if (e.getValue() != null && e.getValue() instanceof Float) {
                     Long2FloatRBTreeMap.Entry f = Long2FloatRBTreeMap.this.findKey((Long)e.getKey());
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
                  } else if (!(e.getKey() instanceof Long)) {
                     return false;
                  } else if (e.getValue() != null && e.getValue() instanceof Float) {
                     Long2FloatRBTreeMap.Entry f = Long2FloatRBTreeMap.this.findKey((Long)e.getKey());
                     if (f != null && Float.floatToIntBits(f.getFloatValue()) == Float.floatToIntBits((Float)e.getValue())) {
                        Long2FloatRBTreeMap.this.remove(f.key);
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
               return Long2FloatRBTreeMap.this.count;
            }

            @Override
            public void clear() {
               Long2FloatRBTreeMap.this.clear();
            }

            public Long2FloatMap.Entry first() {
               return Long2FloatRBTreeMap.this.firstEntry;
            }

            public Long2FloatMap.Entry last() {
               return Long2FloatRBTreeMap.this.lastEntry;
            }

            public ObjectSortedSet<Long2FloatMap.Entry> subSet(Long2FloatMap.Entry from, Long2FloatMap.Entry to) {
               return Long2FloatRBTreeMap.this.subMap(from.getLongKey(), to.getLongKey()).long2FloatEntrySet();
            }

            public ObjectSortedSet<Long2FloatMap.Entry> headSet(Long2FloatMap.Entry to) {
               return Long2FloatRBTreeMap.this.headMap(to.getLongKey()).long2FloatEntrySet();
            }

            public ObjectSortedSet<Long2FloatMap.Entry> tailSet(Long2FloatMap.Entry from) {
               return Long2FloatRBTreeMap.this.tailMap(from.getLongKey()).long2FloatEntrySet();
            }
         };
      }

      return this.entries;
   }

   @Override
   public LongSortedSet keySet() {
      if (this.keys == null) {
         this.keys = new Long2FloatRBTreeMap.KeySet();
      }

      return this.keys;
   }

   @Override
   public FloatCollection values() {
      if (this.values == null) {
         this.values = new AbstractFloatCollection() {
            @Override
            public FloatIterator iterator() {
               return Long2FloatRBTreeMap.this.new ValueIterator();
            }

            @Override
            public boolean contains(float k) {
               return Long2FloatRBTreeMap.this.containsValue(k);
            }

            @Override
            public int size() {
               return Long2FloatRBTreeMap.this.count;
            }

            @Override
            public void clear() {
               Long2FloatRBTreeMap.this.clear();
            }
         };
      }

      return this.values;
   }

   @Override
   public LongComparator comparator() {
      return this.actualComparator;
   }

   @Override
   public Long2FloatSortedMap headMap(long to) {
      return new Long2FloatRBTreeMap.Submap(0L, true, to, false);
   }

   @Override
   public Long2FloatSortedMap tailMap(long from) {
      return new Long2FloatRBTreeMap.Submap(from, false, 0L, true);
   }

   @Override
   public Long2FloatSortedMap subMap(long from, long to) {
      return new Long2FloatRBTreeMap.Submap(from, false, to, false);
   }

   public Long2FloatRBTreeMap clone() {
      Long2FloatRBTreeMap c;
      try {
         c = (Long2FloatRBTreeMap)super.clone();
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
         Long2FloatRBTreeMap.Entry rp = new Long2FloatRBTreeMap.Entry();
         Long2FloatRBTreeMap.Entry rq = new Long2FloatRBTreeMap.Entry();
         Long2FloatRBTreeMap.Entry p = rp;
         rp.left(this.tree);
         Long2FloatRBTreeMap.Entry q = rq;
         rq.pred(null);

         while (true) {
            if (!p.pred()) {
               Long2FloatRBTreeMap.Entry e = p.left.clone();
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
               Long2FloatRBTreeMap.Entry e = p.right.clone();
               e.succ(q.right);
               e.pred(q);
               q.right(e);
            }
         }
      }
   }

   private void writeObject(ObjectOutputStream s) throws IOException {
      int n = this.count;
      Long2FloatRBTreeMap.EntryIterator i = new Long2FloatRBTreeMap.EntryIterator();
      s.defaultWriteObject();

      while (n-- != 0) {
         Long2FloatRBTreeMap.Entry e = i.nextEntry();
         s.writeLong(e.key);
         s.writeFloat(e.value);
      }
   }

   private Long2FloatRBTreeMap.Entry readTree(ObjectInputStream s, int n, Long2FloatRBTreeMap.Entry pred, Long2FloatRBTreeMap.Entry succ) throws IOException, ClassNotFoundException {
      if (n == 1) {
         Long2FloatRBTreeMap.Entry top = new Long2FloatRBTreeMap.Entry(s.readLong(), s.readFloat());
         top.pred(pred);
         top.succ(succ);
         top.black(true);
         return top;
      } else if (n == 2) {
         Long2FloatRBTreeMap.Entry top = new Long2FloatRBTreeMap.Entry(s.readLong(), s.readFloat());
         top.black(true);
         top.right(new Long2FloatRBTreeMap.Entry(s.readLong(), s.readFloat()));
         top.right.pred(top);
         top.pred(pred);
         top.right.succ(succ);
         return top;
      } else {
         int rightN = n / 2;
         int leftN = n - rightN - 1;
         Long2FloatRBTreeMap.Entry top = new Long2FloatRBTreeMap.Entry();
         top.left(this.readTree(s, leftN, pred, top));
         top.key = s.readLong();
         top.value = s.readFloat();
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
         Long2FloatRBTreeMap.Entry e = this.tree;

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

   private static final class Entry extends AbstractLong2FloatMap.BasicEntry implements Cloneable {
      private static final int BLACK_MASK = 1;
      private static final int SUCC_MASK = Integer.MIN_VALUE;
      private static final int PRED_MASK = 1073741824;
      Long2FloatRBTreeMap.Entry left;
      Long2FloatRBTreeMap.Entry right;
      int info;

      Entry() {
         super(0L, 0.0F);
      }

      Entry(long k, float v) {
         super(k, v);
         this.info = -1073741824;
      }

      Long2FloatRBTreeMap.Entry left() {
         return (this.info & 1073741824) != 0 ? null : this.left;
      }

      Long2FloatRBTreeMap.Entry right() {
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

      void pred(Long2FloatRBTreeMap.Entry pred) {
         this.info |= 1073741824;
         this.left = pred;
      }

      void succ(Long2FloatRBTreeMap.Entry succ) {
         this.info |= Integer.MIN_VALUE;
         this.right = succ;
      }

      void left(Long2FloatRBTreeMap.Entry left) {
         this.info &= -1073741825;
         this.left = left;
      }

      void right(Long2FloatRBTreeMap.Entry right) {
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

      Long2FloatRBTreeMap.Entry next() {
         Long2FloatRBTreeMap.Entry next = this.right;
         if ((this.info & -2147483648) == 0) {
            while ((next.info & 1073741824) == 0) {
               next = next.left;
            }
         }

         return next;
      }

      Long2FloatRBTreeMap.Entry prev() {
         Long2FloatRBTreeMap.Entry prev = this.left;
         if ((this.info & 1073741824) == 0) {
            while ((prev.info & -2147483648) == 0) {
               prev = prev.right;
            }
         }

         return prev;
      }

      @Override
      public float setValue(float value) {
         float oldValue = this.value;
         this.value = value;
         return oldValue;
      }

      public Long2FloatRBTreeMap.Entry clone() {
         Long2FloatRBTreeMap.Entry c;
         try {
            c = (Long2FloatRBTreeMap.Entry)super.clone();
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
            java.util.Map.Entry<Long, Float> e = (java.util.Map.Entry<Long, Float>)o;
            return this.key == e.getKey() && Float.floatToIntBits(this.value) == Float.floatToIntBits(e.getValue());
         }
      }

      @Override
      public int hashCode() {
         return HashCommon.long2int(this.key) ^ HashCommon.float2int(this.value);
      }

      @Override
      public String toString() {
         return this.key + "=>" + this.value;
      }
   }

   private class EntryIterator extends Long2FloatRBTreeMap.TreeIterator implements ObjectListIterator<Long2FloatMap.Entry> {
      EntryIterator() {
      }

      EntryIterator(long k) {
         super(k);
      }

      public Long2FloatMap.Entry next() {
         return this.nextEntry();
      }

      public Long2FloatMap.Entry previous() {
         return this.previousEntry();
      }
   }

   private final class KeyIterator extends Long2FloatRBTreeMap.TreeIterator implements LongListIterator {
      public KeyIterator() {
      }

      public KeyIterator(long k) {
         super(k);
      }

      @Override
      public long nextLong() {
         return this.nextEntry().key;
      }

      @Override
      public long previousLong() {
         return this.previousEntry().key;
      }
   }

   private class KeySet extends AbstractLong2FloatSortedMap.KeySet {
      private KeySet() {
      }

      @Override
      public LongBidirectionalIterator iterator() {
         return Long2FloatRBTreeMap.this.new KeyIterator();
      }

      @Override
      public LongBidirectionalIterator iterator(long from) {
         return Long2FloatRBTreeMap.this.new KeyIterator(from);
      }
   }

   private final class Submap extends AbstractLong2FloatSortedMap implements Serializable {
      private static final long serialVersionUID = -7046029254386353129L;
      long from;
      long to;
      boolean bottom;
      boolean top;
      protected transient ObjectSortedSet<Long2FloatMap.Entry> entries;
      protected transient LongSortedSet keys;
      protected transient FloatCollection values;

      public Submap(long from, boolean bottom, long to, boolean top) {
         if (!bottom && !top && Long2FloatRBTreeMap.this.compare(from, to) > 0) {
            throw new IllegalArgumentException("Start key (" + from + ") is larger than end key (" + to + ")");
         } else {
            this.from = from;
            this.bottom = bottom;
            this.to = to;
            this.top = top;
            this.defRetValue = Long2FloatRBTreeMap.this.defRetValue;
         }
      }

      @Override
      public void clear() {
         Long2FloatRBTreeMap.Submap.SubmapIterator i = new Long2FloatRBTreeMap.Submap.SubmapIterator();

         while (i.hasNext()) {
            i.nextEntry();
            i.remove();
         }
      }

      final boolean in(long k) {
         return (this.bottom || Long2FloatRBTreeMap.this.compare(k, this.from) >= 0) && (this.top || Long2FloatRBTreeMap.this.compare(k, this.to) < 0);
      }

      @Override
      public ObjectSortedSet<Long2FloatMap.Entry> long2FloatEntrySet() {
         if (this.entries == null) {
            this.entries = new AbstractObjectSortedSet<Long2FloatMap.Entry>() {
               @Override
               public ObjectBidirectionalIterator<Long2FloatMap.Entry> iterator() {
                  return Submap.this.new SubmapEntryIterator();
               }

               public ObjectBidirectionalIterator<Long2FloatMap.Entry> iterator(Long2FloatMap.Entry from) {
                  return Submap.this.new SubmapEntryIterator(from.getLongKey());
               }

               @Override
               public Comparator<? super Long2FloatMap.Entry> comparator() {
                  return Long2FloatRBTreeMap.this.long2FloatEntrySet().comparator();
               }

               @Override
               public boolean contains(Object o) {
                  if (!(o instanceof java.util.Map.Entry)) {
                     return false;
                  } else {
                     java.util.Map.Entry<?, ?> e = (java.util.Map.Entry<?, ?>)o;
                     if (e.getKey() == null || !(e.getKey() instanceof Long)) {
                        return false;
                     } else if (e.getValue() != null && e.getValue() instanceof Float) {
                        Long2FloatRBTreeMap.Entry f = Long2FloatRBTreeMap.this.findKey((Long)e.getKey());
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
                     if (e.getKey() == null || !(e.getKey() instanceof Long)) {
                        return false;
                     } else if (e.getValue() != null && e.getValue() instanceof Float) {
                        Long2FloatRBTreeMap.Entry f = Long2FloatRBTreeMap.this.findKey((Long)e.getKey());
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

               public Long2FloatMap.Entry first() {
                  return Submap.this.firstEntry();
               }

               public Long2FloatMap.Entry last() {
                  return Submap.this.lastEntry();
               }

               public ObjectSortedSet<Long2FloatMap.Entry> subSet(Long2FloatMap.Entry from, Long2FloatMap.Entry to) {
                  return Submap.this.subMap(from.getLongKey(), to.getLongKey()).long2FloatEntrySet();
               }

               public ObjectSortedSet<Long2FloatMap.Entry> headSet(Long2FloatMap.Entry to) {
                  return Submap.this.headMap(to.getLongKey()).long2FloatEntrySet();
               }

               public ObjectSortedSet<Long2FloatMap.Entry> tailSet(Long2FloatMap.Entry from) {
                  return Submap.this.tailMap(from.getLongKey()).long2FloatEntrySet();
               }
            };
         }

         return this.entries;
      }

      @Override
      public LongSortedSet keySet() {
         if (this.keys == null) {
            this.keys = new Long2FloatRBTreeMap.Submap.KeySet();
         }

         return this.keys;
      }

      @Override
      public FloatCollection values() {
         if (this.values == null) {
            this.values = new AbstractFloatCollection() {
               @Override
               public FloatIterator iterator() {
                  return Submap.this.new SubmapValueIterator();
               }

               @Override
               public boolean contains(float k) {
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
      public boolean containsKey(long k) {
         return this.in(k) && Long2FloatRBTreeMap.this.containsKey(k);
      }

      @Override
      public boolean containsValue(float v) {
         Long2FloatRBTreeMap.Submap.SubmapIterator i = new Long2FloatRBTreeMap.Submap.SubmapIterator();

         while (i.hasNext()) {
            float ev = i.nextEntry().value;
            if (Float.floatToIntBits(ev) == Float.floatToIntBits(v)) {
               return true;
            }
         }

         return false;
      }

      @Override
      public float get(long k) {
         Long2FloatRBTreeMap.Entry e;
         return this.in(k) && (e = Long2FloatRBTreeMap.this.findKey(k)) != null ? e.value : this.defRetValue;
      }

      @Override
      public float put(long k, float v) {
         Long2FloatRBTreeMap.this.modified = false;
         if (!this.in(k)) {
            throw new IllegalArgumentException(
               "Key (" + k + ") out of range [" + (this.bottom ? "-" : String.valueOf(this.from)) + ", " + (this.top ? "-" : String.valueOf(this.to)) + ")"
            );
         } else {
            float oldValue = Long2FloatRBTreeMap.this.put(k, v);
            return Long2FloatRBTreeMap.this.modified ? this.defRetValue : oldValue;
         }
      }

      @Override
      public float remove(long k) {
         Long2FloatRBTreeMap.this.modified = false;
         if (!this.in(k)) {
            return this.defRetValue;
         } else {
            float oldValue = Long2FloatRBTreeMap.this.remove(k);
            return Long2FloatRBTreeMap.this.modified ? oldValue : this.defRetValue;
         }
      }

      @Override
      public int size() {
         Long2FloatRBTreeMap.Submap.SubmapIterator i = new Long2FloatRBTreeMap.Submap.SubmapIterator();
         int n = 0;

         while (i.hasNext()) {
            n++;
            i.nextEntry();
         }

         return n;
      }

      @Override
      public boolean isEmpty() {
         return !new Long2FloatRBTreeMap.Submap.SubmapIterator().hasNext();
      }

      @Override
      public LongComparator comparator() {
         return Long2FloatRBTreeMap.this.actualComparator;
      }

      @Override
      public Long2FloatSortedMap headMap(long to) {
         if (this.top) {
            return Long2FloatRBTreeMap.this.new Submap(this.from, this.bottom, to, false);
         } else {
            return Long2FloatRBTreeMap.this.compare(to, this.to) < 0 ? Long2FloatRBTreeMap.this.new Submap(this.from, this.bottom, to, false) : this;
         }
      }

      @Override
      public Long2FloatSortedMap tailMap(long from) {
         if (this.bottom) {
            return Long2FloatRBTreeMap.this.new Submap(from, false, this.to, this.top);
         } else {
            return Long2FloatRBTreeMap.this.compare(from, this.from) > 0 ? Long2FloatRBTreeMap.this.new Submap(from, false, this.to, this.top) : this;
         }
      }

      @Override
      public Long2FloatSortedMap subMap(long from, long to) {
         if (this.top && this.bottom) {
            return Long2FloatRBTreeMap.this.new Submap(from, false, to, false);
         } else {
            if (!this.top) {
               to = Long2FloatRBTreeMap.this.compare(to, this.to) < 0 ? to : this.to;
            }

            if (!this.bottom) {
               from = Long2FloatRBTreeMap.this.compare(from, this.from) > 0 ? from : this.from;
            }

            return !this.top && !this.bottom && from == this.from && to == this.to ? this : Long2FloatRBTreeMap.this.new Submap(from, false, to, false);
         }
      }

      public Long2FloatRBTreeMap.Entry firstEntry() {
         if (Long2FloatRBTreeMap.this.tree == null) {
            return null;
         } else {
            Long2FloatRBTreeMap.Entry e;
            if (this.bottom) {
               e = Long2FloatRBTreeMap.this.firstEntry;
            } else {
               e = Long2FloatRBTreeMap.this.locateKey(this.from);
               if (Long2FloatRBTreeMap.this.compare(e.key, this.from) < 0) {
                  e = e.next();
               }
            }

            return e != null && (this.top || Long2FloatRBTreeMap.this.compare(e.key, this.to) < 0) ? e : null;
         }
      }

      public Long2FloatRBTreeMap.Entry lastEntry() {
         if (Long2FloatRBTreeMap.this.tree == null) {
            return null;
         } else {
            Long2FloatRBTreeMap.Entry e;
            if (this.top) {
               e = Long2FloatRBTreeMap.this.lastEntry;
            } else {
               e = Long2FloatRBTreeMap.this.locateKey(this.to);
               if (Long2FloatRBTreeMap.this.compare(e.key, this.to) >= 0) {
                  e = e.prev();
               }
            }

            return e != null && (this.bottom || Long2FloatRBTreeMap.this.compare(e.key, this.from) >= 0) ? e : null;
         }
      }

      @Override
      public long firstLongKey() {
         Long2FloatRBTreeMap.Entry e = this.firstEntry();
         if (e == null) {
            throw new NoSuchElementException();
         } else {
            return e.key;
         }
      }

      @Override
      public long lastLongKey() {
         Long2FloatRBTreeMap.Entry e = this.lastEntry();
         if (e == null) {
            throw new NoSuchElementException();
         } else {
            return e.key;
         }
      }

      private class KeySet extends AbstractLong2FloatSortedMap.KeySet {
         private KeySet() {
         }

         @Override
         public LongBidirectionalIterator iterator() {
            return Submap.this.new SubmapKeyIterator();
         }

         @Override
         public LongBidirectionalIterator iterator(long from) {
            return Submap.this.new SubmapKeyIterator(from);
         }
      }

      private class SubmapEntryIterator extends Long2FloatRBTreeMap.Submap.SubmapIterator implements ObjectListIterator<Long2FloatMap.Entry> {
         SubmapEntryIterator() {
         }

         SubmapEntryIterator(long k) {
            super(k);
         }

         public Long2FloatMap.Entry next() {
            return this.nextEntry();
         }

         public Long2FloatMap.Entry previous() {
            return this.previousEntry();
         }
      }

      private class SubmapIterator extends Long2FloatRBTreeMap.TreeIterator {
         SubmapIterator() {
            this.next = Submap.this.firstEntry();
         }

         SubmapIterator(long k) {
            this();
            if (this.next != null) {
               if (!Submap.this.bottom && Long2FloatRBTreeMap.this.compare(k, this.next.key) < 0) {
                  this.prev = null;
               } else if (!Submap.this.top && Long2FloatRBTreeMap.this.compare(k, (this.prev = Submap.this.lastEntry()).key) >= 0) {
                  this.next = null;
               } else {
                  this.next = Long2FloatRBTreeMap.this.locateKey(k);
                  if (Long2FloatRBTreeMap.this.compare(this.next.key, k) <= 0) {
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
            if (!Submap.this.bottom && this.prev != null && Long2FloatRBTreeMap.this.compare(this.prev.key, Submap.this.from) < 0) {
               this.prev = null;
            }
         }

         @Override
         void updateNext() {
            this.next = this.next.next();
            if (!Submap.this.top && this.next != null && Long2FloatRBTreeMap.this.compare(this.next.key, Submap.this.to) >= 0) {
               this.next = null;
            }
         }
      }

      private final class SubmapKeyIterator extends Long2FloatRBTreeMap.Submap.SubmapIterator implements LongListIterator {
         public SubmapKeyIterator() {
         }

         public SubmapKeyIterator(long from) {
            super(from);
         }

         @Override
         public long nextLong() {
            return this.nextEntry().key;
         }

         @Override
         public long previousLong() {
            return this.previousEntry().key;
         }
      }

      private final class SubmapValueIterator extends Long2FloatRBTreeMap.Submap.SubmapIterator implements FloatListIterator {
         private SubmapValueIterator() {
         }

         @Override
         public float nextFloat() {
            return this.nextEntry().value;
         }

         @Override
         public float previousFloat() {
            return this.previousEntry().value;
         }
      }
   }

   private class TreeIterator {
      Long2FloatRBTreeMap.Entry prev;
      Long2FloatRBTreeMap.Entry next;
      Long2FloatRBTreeMap.Entry curr;
      int index = 0;

      TreeIterator() {
         this.next = Long2FloatRBTreeMap.this.firstEntry;
      }

      TreeIterator(long k) {
         if ((this.next = Long2FloatRBTreeMap.this.locateKey(k)) != null) {
            if (Long2FloatRBTreeMap.this.compare(this.next.key, k) <= 0) {
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

      Long2FloatRBTreeMap.Entry nextEntry() {
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

      Long2FloatRBTreeMap.Entry previousEntry() {
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
            Long2FloatRBTreeMap.this.remove(this.curr.key);
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

   private final class ValueIterator extends Long2FloatRBTreeMap.TreeIterator implements FloatListIterator {
      private ValueIterator() {
      }

      @Override
      public float nextFloat() {
         return this.nextEntry().value;
      }

      @Override
      public float previousFloat() {
         return this.previousEntry().value;
      }
   }
}
