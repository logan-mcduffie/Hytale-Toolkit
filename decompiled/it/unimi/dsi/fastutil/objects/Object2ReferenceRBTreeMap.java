package it.unimi.dsi.fastutil.objects;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.SortedMap;

public class Object2ReferenceRBTreeMap<K, V> extends AbstractObject2ReferenceSortedMap<K, V> implements Serializable, Cloneable {
   protected transient Object2ReferenceRBTreeMap.Entry<K, V> tree;
   protected int count;
   protected transient Object2ReferenceRBTreeMap.Entry<K, V> firstEntry;
   protected transient Object2ReferenceRBTreeMap.Entry<K, V> lastEntry;
   protected transient ObjectSortedSet<Object2ReferenceMap.Entry<K, V>> entries;
   protected transient ObjectSortedSet<K> keys;
   protected transient ReferenceCollection<V> values;
   protected transient boolean modified;
   protected Comparator<? super K> storedComparator;
   protected transient Comparator<? super K> actualComparator;
   private static final long serialVersionUID = -7046029254386353129L;
   private transient boolean[] dirPath;
   private transient Object2ReferenceRBTreeMap.Entry<K, V>[] nodePath;

   public Object2ReferenceRBTreeMap() {
      this.allocatePaths();
      this.tree = null;
      this.count = 0;
   }

   private void setActualComparator() {
      this.actualComparator = this.storedComparator;
   }

   public Object2ReferenceRBTreeMap(Comparator<? super K> c) {
      this();
      this.storedComparator = c;
      this.setActualComparator();
   }

   public Object2ReferenceRBTreeMap(Map<? extends K, ? extends V> m) {
      this();
      this.putAll(m);
   }

   public Object2ReferenceRBTreeMap(SortedMap<K, V> m) {
      this(m.comparator());
      this.putAll(m);
   }

   public Object2ReferenceRBTreeMap(Object2ReferenceMap<? extends K, ? extends V> m) {
      this();
      this.putAll(m);
   }

   public Object2ReferenceRBTreeMap(Object2ReferenceSortedMap<K, V> m) {
      this(m.comparator());
      this.putAll(m);
   }

   public Object2ReferenceRBTreeMap(K[] k, V[] v, Comparator<? super K> c) {
      this(c);
      if (k.length != v.length) {
         throw new IllegalArgumentException("The key array and the value array have different lengths (" + k.length + " and " + v.length + ")");
      } else {
         for (int i = 0; i < k.length; i++) {
            this.put(k[i], v[i]);
         }
      }
   }

   public Object2ReferenceRBTreeMap(K[] k, V[] v) {
      this(k, v, null);
   }

   final int compare(K k1, K k2) {
      return this.actualComparator == null ? ((Comparable)k1).compareTo(k2) : this.actualComparator.compare(k1, k2);
   }

   final Object2ReferenceRBTreeMap.Entry<K, V> findKey(K k) {
      Object2ReferenceRBTreeMap.Entry<K, V> e = this.tree;

      int cmp;
      while (e != null && (cmp = this.compare(k, e.key)) != 0) {
         e = cmp < 0 ? e.left() : e.right();
      }

      return e;
   }

   final Object2ReferenceRBTreeMap.Entry<K, V> locateKey(K k) {
      Object2ReferenceRBTreeMap.Entry<K, V> e = this.tree;
      Object2ReferenceRBTreeMap.Entry<K, V> last = this.tree;

      int cmp;
      for (cmp = 0; e != null && (cmp = this.compare(k, e.key)) != 0; e = cmp < 0 ? e.left() : e.right()) {
         last = e;
      }

      return cmp == 0 ? e : last;
   }

   private void allocatePaths() {
      this.dirPath = new boolean[64];
      this.nodePath = new Object2ReferenceRBTreeMap.Entry[64];
   }

   @Override
   public V put(K k, V v) {
      Object2ReferenceRBTreeMap.Entry<K, V> e = this.add(k);
      V oldValue = e.value;
      e.value = v;
      return oldValue;
   }

   private Object2ReferenceRBTreeMap.Entry<K, V> add(K k) {
      Objects.requireNonNull(k);
      this.modified = false;
      int maxDepth = 0;
      Object2ReferenceRBTreeMap.Entry<K, V> e;
      if (this.tree == null) {
         this.count++;
         e = this.tree = this.lastEntry = this.firstEntry = new Object2ReferenceRBTreeMap.Entry<>(k, this.defRetValue);
      } else {
         Object2ReferenceRBTreeMap.Entry<K, V> p = this.tree;
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
               e = new Object2ReferenceRBTreeMap.Entry<>(k, this.defRetValue);
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
               e = new Object2ReferenceRBTreeMap.Entry<>(k, this.defRetValue);
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
                  Object2ReferenceRBTreeMap.Entry<K, V> y = this.nodePath[i - 1].right;
                  if (this.nodePath[i - 1].succ() || y.black()) {
                     if (!this.dirPath[i]) {
                        y = this.nodePath[i];
                     } else {
                        Object2ReferenceRBTreeMap.Entry<K, V> x = this.nodePath[i];
                        y = x.right;
                        x.right = y.left;
                        y.left = x;
                        this.nodePath[i - 1].left = y;
                        if (y.pred()) {
                           y.pred(false);
                           x.succ(y);
                        }
                     }

                     Object2ReferenceRBTreeMap.Entry<K, V> xx = this.nodePath[i - 1];
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
                  Object2ReferenceRBTreeMap.Entry<K, V> y = this.nodePath[i - 1].left;
                  if (this.nodePath[i - 1].pred() || y.black()) {
                     if (this.dirPath[i]) {
                        y = this.nodePath[i];
                     } else {
                        Object2ReferenceRBTreeMap.Entry<K, V> x = this.nodePath[i];
                        y = x.left;
                        x.left = y.right;
                        y.right = x;
                        this.nodePath[i - 1].right = y;
                        if (y.succ()) {
                           y.succ(false);
                           x.pred(y);
                        }
                     }

                     Object2ReferenceRBTreeMap.Entry<K, V> xxx = this.nodePath[i - 1];
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
   public V remove(Object k) {
      this.modified = false;
      if (this.tree == null) {
         return this.defRetValue;
      } else {
         Object2ReferenceRBTreeMap.Entry<K, V> p = this.tree;
         int i = 0;
         K kk = (K)k;

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
            Object2ReferenceRBTreeMap.Entry<K, V> r = p.right;
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
                  Object2ReferenceRBTreeMap.Entry<K, V> s = r.left;
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
                  Object2ReferenceRBTreeMap.Entry<K, V> x = this.dirPath[i - 1] ? this.nodePath[i - 1].right : this.nodePath[i - 1].left;
                  if (!x.black()) {
                     x.black(true);
                     break;
                  }
               }

               if (!this.dirPath[i - 1]) {
                  Object2ReferenceRBTreeMap.Entry<K, V> w = this.nodePath[i - 1].right;
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
                        Object2ReferenceRBTreeMap.Entry<K, V> y = w.left;
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
                  Object2ReferenceRBTreeMap.Entry<K, V> wx = this.nodePath[i - 1].left;
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
                        Object2ReferenceRBTreeMap.Entry<K, V> y = wx.right;
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
   public boolean containsValue(Object v) {
      Object2ReferenceRBTreeMap<K, V>.ValueIterator i = new Object2ReferenceRBTreeMap.ValueIterator();
      int j = this.count;

      while (j-- != 0) {
         Object ev = i.next();
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
   public boolean containsKey(Object k) {
      return k == null ? false : this.findKey((K)k) != null;
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
   public V get(Object k) {
      Object2ReferenceRBTreeMap.Entry<K, V> e = this.findKey((K)k);
      return e == null ? this.defRetValue : e.value;
   }

   @Override
   public K firstKey() {
      if (this.tree == null) {
         throw new NoSuchElementException();
      } else {
         return this.firstEntry.key;
      }
   }

   @Override
   public K lastKey() {
      if (this.tree == null) {
         throw new NoSuchElementException();
      } else {
         return this.lastEntry.key;
      }
   }

   @Override
   public ObjectSortedSet<Object2ReferenceMap.Entry<K, V>> object2ReferenceEntrySet() {
      if (this.entries == null) {
         this.entries = new AbstractObjectSortedSet<Object2ReferenceMap.Entry<K, V>>() {
            final Comparator<? super Object2ReferenceMap.Entry<K, V>> comparator = Object2ReferenceRBTreeMap.this.actualComparator == null
               ? (x, y) -> ((Comparable)x.getKey()).compareTo(y.getKey())
               : (x, y) -> Object2ReferenceRBTreeMap.this.actualComparator.compare(x.getKey(), y.getKey());

            @Override
            public Comparator<? super Object2ReferenceMap.Entry<K, V>> comparator() {
               return this.comparator;
            }

            @Override
            public ObjectBidirectionalIterator<Object2ReferenceMap.Entry<K, V>> iterator() {
               return Object2ReferenceRBTreeMap.this.new EntryIterator();
            }

            public ObjectBidirectionalIterator<Object2ReferenceMap.Entry<K, V>> iterator(Object2ReferenceMap.Entry<K, V> from) {
               return Object2ReferenceRBTreeMap.this.new EntryIterator(from.getKey());
            }

            @Override
            public boolean contains(Object o) {
               if (o != null && o instanceof java.util.Map.Entry) {
                  java.util.Map.Entry<?, ?> e = (java.util.Map.Entry<?, ?>)o;
                  if (e.getKey() == null) {
                     return false;
                  } else {
                     Object2ReferenceRBTreeMap.Entry<K, V> f = (Object2ReferenceRBTreeMap.Entry<K, V>)Object2ReferenceRBTreeMap.this.findKey(e.getKey());
                     return e.equals(f);
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
                  } else {
                     Object2ReferenceRBTreeMap.Entry<K, V> f = (Object2ReferenceRBTreeMap.Entry<K, V>)Object2ReferenceRBTreeMap.this.findKey(e.getKey());
                     if (f != null && f.getValue() == e.getValue()) {
                        Object2ReferenceRBTreeMap.this.remove(f.key);
                        return true;
                     } else {
                        return false;
                     }
                  }
               }
            }

            @Override
            public int size() {
               return Object2ReferenceRBTreeMap.this.count;
            }

            @Override
            public void clear() {
               Object2ReferenceRBTreeMap.this.clear();
            }

            public Object2ReferenceMap.Entry<K, V> first() {
               return Object2ReferenceRBTreeMap.this.firstEntry;
            }

            public Object2ReferenceMap.Entry<K, V> last() {
               return Object2ReferenceRBTreeMap.this.lastEntry;
            }

            public ObjectSortedSet<Object2ReferenceMap.Entry<K, V>> subSet(Object2ReferenceMap.Entry<K, V> from, Object2ReferenceMap.Entry<K, V> to) {
               return Object2ReferenceRBTreeMap.this.subMap(from.getKey(), to.getKey()).object2ReferenceEntrySet();
            }

            public ObjectSortedSet<Object2ReferenceMap.Entry<K, V>> headSet(Object2ReferenceMap.Entry<K, V> to) {
               return Object2ReferenceRBTreeMap.this.headMap(to.getKey()).object2ReferenceEntrySet();
            }

            public ObjectSortedSet<Object2ReferenceMap.Entry<K, V>> tailSet(Object2ReferenceMap.Entry<K, V> from) {
               return Object2ReferenceRBTreeMap.this.tailMap(from.getKey()).object2ReferenceEntrySet();
            }
         };
      }

      return this.entries;
   }

   @Override
   public ObjectSortedSet<K> keySet() {
      if (this.keys == null) {
         this.keys = new Object2ReferenceRBTreeMap.KeySet();
      }

      return this.keys;
   }

   @Override
   public ReferenceCollection<V> values() {
      if (this.values == null) {
         this.values = new AbstractReferenceCollection<V>() {
            @Override
            public ObjectIterator<V> iterator() {
               return Object2ReferenceRBTreeMap.this.new ValueIterator();
            }

            @Override
            public boolean contains(Object k) {
               return Object2ReferenceRBTreeMap.this.containsValue(k);
            }

            @Override
            public int size() {
               return Object2ReferenceRBTreeMap.this.count;
            }

            @Override
            public void clear() {
               Object2ReferenceRBTreeMap.this.clear();
            }
         };
      }

      return this.values;
   }

   @Override
   public Comparator<? super K> comparator() {
      return this.actualComparator;
   }

   @Override
   public Object2ReferenceSortedMap<K, V> headMap(K to) {
      return new Object2ReferenceRBTreeMap.Submap(null, true, to, false);
   }

   @Override
   public Object2ReferenceSortedMap<K, V> tailMap(K from) {
      return new Object2ReferenceRBTreeMap.Submap(from, false, null, true);
   }

   @Override
   public Object2ReferenceSortedMap<K, V> subMap(K from, K to) {
      return new Object2ReferenceRBTreeMap.Submap(from, false, to, false);
   }

   public Object2ReferenceRBTreeMap<K, V> clone() {
      Object2ReferenceRBTreeMap<K, V> c;
      try {
         c = (Object2ReferenceRBTreeMap<K, V>)super.clone();
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
         Object2ReferenceRBTreeMap.Entry<K, V> rp = new Object2ReferenceRBTreeMap.Entry<>();
         Object2ReferenceRBTreeMap.Entry<K, V> rq = new Object2ReferenceRBTreeMap.Entry<>();
         Object2ReferenceRBTreeMap.Entry<K, V> p = rp;
         rp.left(this.tree);
         Object2ReferenceRBTreeMap.Entry<K, V> q = rq;
         rq.pred(null);

         while (true) {
            if (!p.pred()) {
               Object2ReferenceRBTreeMap.Entry<K, V> e = p.left.clone();
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
               Object2ReferenceRBTreeMap.Entry<K, V> e = p.right.clone();
               e.succ(q.right);
               e.pred(q);
               q.right(e);
            }
         }
      }
   }

   private void writeObject(ObjectOutputStream s) throws IOException {
      int n = this.count;
      Object2ReferenceRBTreeMap<K, V>.EntryIterator i = new Object2ReferenceRBTreeMap.EntryIterator();
      s.defaultWriteObject();

      while (n-- != 0) {
         Object2ReferenceRBTreeMap.Entry<K, V> e = i.nextEntry();
         s.writeObject(e.key);
         s.writeObject(e.value);
      }
   }

   private Object2ReferenceRBTreeMap.Entry<K, V> readTree(
      ObjectInputStream s, int n, Object2ReferenceRBTreeMap.Entry<K, V> pred, Object2ReferenceRBTreeMap.Entry<K, V> succ
   ) throws IOException, ClassNotFoundException {
      if (n == 1) {
         Object2ReferenceRBTreeMap.Entry<K, V> top = new Object2ReferenceRBTreeMap.Entry<>((K)s.readObject(), (V)s.readObject());
         top.pred(pred);
         top.succ(succ);
         top.black(true);
         return top;
      } else if (n == 2) {
         Object2ReferenceRBTreeMap.Entry<K, V> top = new Object2ReferenceRBTreeMap.Entry<>((K)s.readObject(), (V)s.readObject());
         top.black(true);
         top.right(new Object2ReferenceRBTreeMap.Entry<>((K)s.readObject(), (V)s.readObject()));
         top.right.pred(top);
         top.pred(pred);
         top.right.succ(succ);
         return top;
      } else {
         int rightN = n / 2;
         int leftN = n - rightN - 1;
         Object2ReferenceRBTreeMap.Entry<K, V> top = new Object2ReferenceRBTreeMap.Entry<>();
         top.left(this.readTree(s, leftN, pred, top));
         top.key = (K)s.readObject();
         top.value = (V)s.readObject();
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
         Object2ReferenceRBTreeMap.Entry<K, V> e = this.tree;

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

   private static final class Entry<K, V> extends AbstractObject2ReferenceMap.BasicEntry<K, V> implements Cloneable {
      private static final int BLACK_MASK = 1;
      private static final int SUCC_MASK = Integer.MIN_VALUE;
      private static final int PRED_MASK = 1073741824;
      Object2ReferenceRBTreeMap.Entry<K, V> left;
      Object2ReferenceRBTreeMap.Entry<K, V> right;
      int info;

      Entry() {
         super(null, null);
      }

      Entry(K k, V v) {
         super(k, v);
         this.info = -1073741824;
      }

      Object2ReferenceRBTreeMap.Entry<K, V> left() {
         return (this.info & 1073741824) != 0 ? null : this.left;
      }

      Object2ReferenceRBTreeMap.Entry<K, V> right() {
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

      void pred(Object2ReferenceRBTreeMap.Entry<K, V> pred) {
         this.info |= 1073741824;
         this.left = pred;
      }

      void succ(Object2ReferenceRBTreeMap.Entry<K, V> succ) {
         this.info |= Integer.MIN_VALUE;
         this.right = succ;
      }

      void left(Object2ReferenceRBTreeMap.Entry<K, V> left) {
         this.info &= -1073741825;
         this.left = left;
      }

      void right(Object2ReferenceRBTreeMap.Entry<K, V> right) {
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

      Object2ReferenceRBTreeMap.Entry<K, V> next() {
         Object2ReferenceRBTreeMap.Entry<K, V> next = this.right;
         if ((this.info & -2147483648) == 0) {
            while ((next.info & 1073741824) == 0) {
               next = next.left;
            }
         }

         return next;
      }

      Object2ReferenceRBTreeMap.Entry<K, V> prev() {
         Object2ReferenceRBTreeMap.Entry<K, V> prev = this.left;
         if ((this.info & 1073741824) == 0) {
            while ((prev.info & -2147483648) == 0) {
               prev = prev.right;
            }
         }

         return prev;
      }

      @Override
      public V setValue(V value) {
         V oldValue = this.value;
         this.value = value;
         return oldValue;
      }

      public Object2ReferenceRBTreeMap.Entry<K, V> clone() {
         Object2ReferenceRBTreeMap.Entry<K, V> c;
         try {
            c = (Object2ReferenceRBTreeMap.Entry<K, V>)super.clone();
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
            java.util.Map.Entry<K, V> e = (java.util.Map.Entry<K, V>)o;
            return Objects.equals(this.key, e.getKey()) && this.value == e.getValue();
         }
      }

      @Override
      public int hashCode() {
         return this.key.hashCode() ^ (this.value == null ? 0 : System.identityHashCode(this.value));
      }

      @Override
      public String toString() {
         return this.key + "=>" + this.value;
      }
   }

   private class EntryIterator extends Object2ReferenceRBTreeMap<K, V>.TreeIterator implements ObjectListIterator<Object2ReferenceMap.Entry<K, V>> {
      EntryIterator() {
      }

      EntryIterator(K k) {
         super(k);
      }

      public Object2ReferenceMap.Entry<K, V> next() {
         return this.nextEntry();
      }

      public Object2ReferenceMap.Entry<K, V> previous() {
         return this.previousEntry();
      }
   }

   private final class KeyIterator extends Object2ReferenceRBTreeMap<K, V>.TreeIterator implements ObjectListIterator<K> {
      public KeyIterator() {
      }

      public KeyIterator(K k) {
         super(k);
      }

      @Override
      public K next() {
         return this.nextEntry().key;
      }

      @Override
      public K previous() {
         return this.previousEntry().key;
      }
   }

   private class KeySet extends AbstractObject2ReferenceSortedMap<K, V>.KeySet {
      private KeySet() {
      }

      @Override
      public ObjectBidirectionalIterator<K> iterator() {
         return Object2ReferenceRBTreeMap.this.new KeyIterator();
      }

      @Override
      public ObjectBidirectionalIterator<K> iterator(K from) {
         return Object2ReferenceRBTreeMap.this.new KeyIterator(from);
      }
   }

   private final class Submap extends AbstractObject2ReferenceSortedMap<K, V> implements Serializable {
      private static final long serialVersionUID = -7046029254386353129L;
      K from;
      K to;
      boolean bottom;
      boolean top;
      protected transient ObjectSortedSet<Object2ReferenceMap.Entry<K, V>> entries;
      protected transient ObjectSortedSet<K> keys;
      protected transient ReferenceCollection<V> values;

      public Submap(K from, boolean bottom, K to, boolean top) {
         if (!bottom && !top && Object2ReferenceRBTreeMap.this.compare(from, to) > 0) {
            throw new IllegalArgumentException("Start key (" + from + ") is larger than end key (" + to + ")");
         } else {
            this.from = from;
            this.bottom = bottom;
            this.to = to;
            this.top = top;
            this.defRetValue = Object2ReferenceRBTreeMap.this.defRetValue;
         }
      }

      @Override
      public void clear() {
         Object2ReferenceRBTreeMap<K, V>.Submap.SubmapIterator i = new Object2ReferenceRBTreeMap.Submap.SubmapIterator();

         while (i.hasNext()) {
            i.nextEntry();
            i.remove();
         }
      }

      final boolean in(K k) {
         return (this.bottom || Object2ReferenceRBTreeMap.this.compare(k, this.from) >= 0)
            && (this.top || Object2ReferenceRBTreeMap.this.compare(k, this.to) < 0);
      }

      @Override
      public ObjectSortedSet<Object2ReferenceMap.Entry<K, V>> object2ReferenceEntrySet() {
         if (this.entries == null) {
            this.entries = new AbstractObjectSortedSet<Object2ReferenceMap.Entry<K, V>>() {
               @Override
               public ObjectBidirectionalIterator<Object2ReferenceMap.Entry<K, V>> iterator() {
                  return Submap.this.new SubmapEntryIterator();
               }

               public ObjectBidirectionalIterator<Object2ReferenceMap.Entry<K, V>> iterator(Object2ReferenceMap.Entry<K, V> from) {
                  return Submap.this.new SubmapEntryIterator(from.getKey());
               }

               @Override
               public Comparator<? super Object2ReferenceMap.Entry<K, V>> comparator() {
                  return Object2ReferenceRBTreeMap.this.object2ReferenceEntrySet().comparator();
               }

               @Override
               public boolean contains(Object o) {
                  if (!(o instanceof java.util.Map.Entry)) {
                     return false;
                  } else {
                     java.util.Map.Entry<?, ?> e = (java.util.Map.Entry<?, ?>)o;
                     Object2ReferenceRBTreeMap.Entry<K, V> f = Object2ReferenceRBTreeMap.this.findKey((K)e.getKey());
                     return f != null && Submap.this.in(f.key) && e.equals(f);
                  }
               }

               @Override
               public boolean remove(Object o) {
                  if (!(o instanceof java.util.Map.Entry)) {
                     return false;
                  } else {
                     java.util.Map.Entry<?, ?> e = (java.util.Map.Entry<?, ?>)o;
                     Object2ReferenceRBTreeMap.Entry<K, V> f = Object2ReferenceRBTreeMap.this.findKey((K)e.getKey());
                     if (f != null && Submap.this.in(f.key)) {
                        Submap.this.remove(f.key);
                     }

                     return f != null;
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

               public Object2ReferenceMap.Entry<K, V> first() {
                  return Submap.this.firstEntry();
               }

               public Object2ReferenceMap.Entry<K, V> last() {
                  return Submap.this.lastEntry();
               }

               public ObjectSortedSet<Object2ReferenceMap.Entry<K, V>> subSet(Object2ReferenceMap.Entry<K, V> from, Object2ReferenceMap.Entry<K, V> to) {
                  return Submap.this.subMap(from.getKey(), to.getKey()).object2ReferenceEntrySet();
               }

               public ObjectSortedSet<Object2ReferenceMap.Entry<K, V>> headSet(Object2ReferenceMap.Entry<K, V> to) {
                  return Submap.this.headMap(to.getKey()).object2ReferenceEntrySet();
               }

               public ObjectSortedSet<Object2ReferenceMap.Entry<K, V>> tailSet(Object2ReferenceMap.Entry<K, V> from) {
                  return Submap.this.tailMap(from.getKey()).object2ReferenceEntrySet();
               }
            };
         }

         return this.entries;
      }

      @Override
      public ObjectSortedSet<K> keySet() {
         if (this.keys == null) {
            this.keys = new Object2ReferenceRBTreeMap.Submap.KeySet();
         }

         return this.keys;
      }

      @Override
      public ReferenceCollection<V> values() {
         if (this.values == null) {
            this.values = new AbstractReferenceCollection<V>() {
               @Override
               public ObjectIterator<V> iterator() {
                  return Submap.this.new SubmapValueIterator();
               }

               @Override
               public boolean contains(Object k) {
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
      public boolean containsKey(Object k) {
         return k == null ? false : this.in((K)k) && Object2ReferenceRBTreeMap.this.containsKey(k);
      }

      @Override
      public boolean containsValue(Object v) {
         Object2ReferenceRBTreeMap<K, V>.Submap.SubmapIterator i = new Object2ReferenceRBTreeMap.Submap.SubmapIterator();

         while (i.hasNext()) {
            Object ev = i.nextEntry().value;
            if (ev == v) {
               return true;
            }
         }

         return false;
      }

      @Override
      public V get(Object k) {
         Object2ReferenceRBTreeMap.Entry<K, V> e;
         return this.in((K)k) && (e = Object2ReferenceRBTreeMap.this.findKey((K)k)) != null ? e.value : this.defRetValue;
      }

      @Override
      public V put(K k, V v) {
         Object2ReferenceRBTreeMap.this.modified = false;
         if (!this.in(k)) {
            throw new IllegalArgumentException(
               "Key (" + k + ") out of range [" + (this.bottom ? "-" : String.valueOf(this.from)) + ", " + (this.top ? "-" : String.valueOf(this.to)) + ")"
            );
         } else {
            V oldValue = Object2ReferenceRBTreeMap.this.put(k, v);
            return Object2ReferenceRBTreeMap.this.modified ? this.defRetValue : oldValue;
         }
      }

      @Override
      public V remove(Object k) {
         Object2ReferenceRBTreeMap.this.modified = false;
         if (!this.in((K)k)) {
            return this.defRetValue;
         } else {
            V oldValue = Object2ReferenceRBTreeMap.this.remove(k);
            return Object2ReferenceRBTreeMap.this.modified ? oldValue : this.defRetValue;
         }
      }

      @Override
      public int size() {
         Object2ReferenceRBTreeMap<K, V>.Submap.SubmapIterator i = new Object2ReferenceRBTreeMap.Submap.SubmapIterator();
         int n = 0;

         while (i.hasNext()) {
            n++;
            i.nextEntry();
         }

         return n;
      }

      @Override
      public boolean isEmpty() {
         return !new Object2ReferenceRBTreeMap.Submap.SubmapIterator().hasNext();
      }

      @Override
      public Comparator<? super K> comparator() {
         return Object2ReferenceRBTreeMap.this.actualComparator;
      }

      @Override
      public Object2ReferenceSortedMap<K, V> headMap(K to) {
         if (this.top) {
            return Object2ReferenceRBTreeMap.this.new Submap(this.from, this.bottom, to, false);
         } else {
            return Object2ReferenceRBTreeMap.this.compare(to, this.to) < 0
               ? Object2ReferenceRBTreeMap.this.new Submap(this.from, this.bottom, to, false)
               : this;
         }
      }

      @Override
      public Object2ReferenceSortedMap<K, V> tailMap(K from) {
         if (this.bottom) {
            return Object2ReferenceRBTreeMap.this.new Submap(from, false, this.to, this.top);
         } else {
            return Object2ReferenceRBTreeMap.this.compare(from, this.from) > 0
               ? Object2ReferenceRBTreeMap.this.new Submap(from, false, this.to, this.top)
               : this;
         }
      }

      @Override
      public Object2ReferenceSortedMap<K, V> subMap(K from, K to) {
         if (this.top && this.bottom) {
            return Object2ReferenceRBTreeMap.this.new Submap(from, false, to, false);
         } else {
            if (!this.top) {
               to = Object2ReferenceRBTreeMap.this.compare(to, this.to) < 0 ? to : this.to;
            }

            if (!this.bottom) {
               from = Object2ReferenceRBTreeMap.this.compare(from, this.from) > 0 ? from : this.from;
            }

            return !this.top && !this.bottom && from == this.from && to == this.to ? this : Object2ReferenceRBTreeMap.this.new Submap(from, false, to, false);
         }
      }

      public Object2ReferenceRBTreeMap.Entry<K, V> firstEntry() {
         if (Object2ReferenceRBTreeMap.this.tree == null) {
            return null;
         } else {
            Object2ReferenceRBTreeMap.Entry<K, V> e;
            if (this.bottom) {
               e = Object2ReferenceRBTreeMap.this.firstEntry;
            } else {
               e = Object2ReferenceRBTreeMap.this.locateKey(this.from);
               if (Object2ReferenceRBTreeMap.this.compare(e.key, this.from) < 0) {
                  e = e.next();
               }
            }

            return e != null && (this.top || Object2ReferenceRBTreeMap.this.compare(e.key, this.to) < 0) ? e : null;
         }
      }

      public Object2ReferenceRBTreeMap.Entry<K, V> lastEntry() {
         if (Object2ReferenceRBTreeMap.this.tree == null) {
            return null;
         } else {
            Object2ReferenceRBTreeMap.Entry<K, V> e;
            if (this.top) {
               e = Object2ReferenceRBTreeMap.this.lastEntry;
            } else {
               e = Object2ReferenceRBTreeMap.this.locateKey(this.to);
               if (Object2ReferenceRBTreeMap.this.compare(e.key, this.to) >= 0) {
                  e = e.prev();
               }
            }

            return e != null && (this.bottom || Object2ReferenceRBTreeMap.this.compare(e.key, this.from) >= 0) ? e : null;
         }
      }

      @Override
      public K firstKey() {
         Object2ReferenceRBTreeMap.Entry<K, V> e = this.firstEntry();
         if (e == null) {
            throw new NoSuchElementException();
         } else {
            return e.key;
         }
      }

      @Override
      public K lastKey() {
         Object2ReferenceRBTreeMap.Entry<K, V> e = this.lastEntry();
         if (e == null) {
            throw new NoSuchElementException();
         } else {
            return e.key;
         }
      }

      private class KeySet extends AbstractObject2ReferenceSortedMap<K, V>.KeySet {
         private KeySet() {
         }

         @Override
         public ObjectBidirectionalIterator<K> iterator() {
            return Submap.this.new SubmapKeyIterator();
         }

         @Override
         public ObjectBidirectionalIterator<K> iterator(K from) {
            return Submap.this.new SubmapKeyIterator(from);
         }
      }

      private class SubmapEntryIterator
         extends Object2ReferenceRBTreeMap<K, V>.Submap.SubmapIterator
         implements ObjectListIterator<Object2ReferenceMap.Entry<K, V>> {
         SubmapEntryIterator() {
         }

         SubmapEntryIterator(K k) {
            super(k);
         }

         public Object2ReferenceMap.Entry<K, V> next() {
            return this.nextEntry();
         }

         public Object2ReferenceMap.Entry<K, V> previous() {
            return this.previousEntry();
         }
      }

      private class SubmapIterator extends Object2ReferenceRBTreeMap<K, V>.TreeIterator {
         SubmapIterator() {
            this.next = Submap.this.firstEntry();
         }

         SubmapIterator(K k) {
            this();
            if (this.next != null) {
               if (!Submap.this.bottom && Object2ReferenceRBTreeMap.this.compare(k, this.next.key) < 0) {
                  this.prev = null;
               } else if (!Submap.this.top && Object2ReferenceRBTreeMap.this.compare(k, (this.prev = Submap.this.lastEntry()).key) >= 0) {
                  this.next = null;
               } else {
                  this.next = Object2ReferenceRBTreeMap.this.locateKey(k);
                  if (Object2ReferenceRBTreeMap.this.compare(this.next.key, k) <= 0) {
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
            if (!Submap.this.bottom && this.prev != null && Object2ReferenceRBTreeMap.this.compare(this.prev.key, Submap.this.from) < 0) {
               this.prev = null;
            }
         }

         @Override
         void updateNext() {
            this.next = this.next.next();
            if (!Submap.this.top && this.next != null && Object2ReferenceRBTreeMap.this.compare(this.next.key, Submap.this.to) >= 0) {
               this.next = null;
            }
         }
      }

      private final class SubmapKeyIterator extends Object2ReferenceRBTreeMap<K, V>.Submap.SubmapIterator implements ObjectListIterator<K> {
         public SubmapKeyIterator() {
         }

         public SubmapKeyIterator(K from) {
            super(from);
         }

         @Override
         public K next() {
            return this.nextEntry().key;
         }

         @Override
         public K previous() {
            return this.previousEntry().key;
         }
      }

      private final class SubmapValueIterator extends Object2ReferenceRBTreeMap<K, V>.Submap.SubmapIterator implements ObjectListIterator<V> {
         private SubmapValueIterator() {
         }

         @Override
         public V next() {
            return this.nextEntry().value;
         }

         @Override
         public V previous() {
            return this.previousEntry().value;
         }
      }
   }

   private class TreeIterator {
      Object2ReferenceRBTreeMap.Entry<K, V> prev;
      Object2ReferenceRBTreeMap.Entry<K, V> next;
      Object2ReferenceRBTreeMap.Entry<K, V> curr;
      int index = 0;

      TreeIterator() {
         this.next = Object2ReferenceRBTreeMap.this.firstEntry;
      }

      TreeIterator(K k) {
         if ((this.next = Object2ReferenceRBTreeMap.this.locateKey(k)) != null) {
            if (Object2ReferenceRBTreeMap.this.compare(this.next.key, k) <= 0) {
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

      Object2ReferenceRBTreeMap.Entry<K, V> nextEntry() {
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

      Object2ReferenceRBTreeMap.Entry<K, V> previousEntry() {
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
            Object2ReferenceRBTreeMap.this.remove(this.curr.key);
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

   private final class ValueIterator extends Object2ReferenceRBTreeMap<K, V>.TreeIterator implements ObjectListIterator<V> {
      private ValueIterator() {
      }

      @Override
      public V next() {
         return this.nextEntry().value;
      }

      @Override
      public V previous() {
         return this.previousEntry().value;
      }
   }
}
