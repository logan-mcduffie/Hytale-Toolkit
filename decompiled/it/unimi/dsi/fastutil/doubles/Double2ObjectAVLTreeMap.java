package it.unimi.dsi.fastutil.doubles;

import it.unimi.dsi.fastutil.HashCommon;
import it.unimi.dsi.fastutil.objects.AbstractObjectCollection;
import it.unimi.dsi.fastutil.objects.AbstractObjectSortedSet;
import it.unimi.dsi.fastutil.objects.ObjectBidirectionalIterator;
import it.unimi.dsi.fastutil.objects.ObjectCollection;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
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
import java.util.Objects;
import java.util.SortedMap;

public class Double2ObjectAVLTreeMap<V> extends AbstractDouble2ObjectSortedMap<V> implements Serializable, Cloneable {
   protected transient Double2ObjectAVLTreeMap.Entry<V> tree;
   protected int count;
   protected transient Double2ObjectAVLTreeMap.Entry<V> firstEntry;
   protected transient Double2ObjectAVLTreeMap.Entry<V> lastEntry;
   protected transient ObjectSortedSet<Double2ObjectMap.Entry<V>> entries;
   protected transient DoubleSortedSet keys;
   protected transient ObjectCollection<V> values;
   protected transient boolean modified;
   protected Comparator<? super Double> storedComparator;
   protected transient DoubleComparator actualComparator;
   private static final long serialVersionUID = -7046029254386353129L;
   private transient boolean[] dirPath;

   public Double2ObjectAVLTreeMap() {
      this.allocatePaths();
      this.tree = null;
      this.count = 0;
   }

   private void setActualComparator() {
      this.actualComparator = DoubleComparators.asDoubleComparator(this.storedComparator);
   }

   public Double2ObjectAVLTreeMap(Comparator<? super Double> c) {
      this();
      this.storedComparator = c;
      this.setActualComparator();
   }

   public Double2ObjectAVLTreeMap(Map<? extends Double, ? extends V> m) {
      this();
      this.putAll(m);
   }

   public Double2ObjectAVLTreeMap(SortedMap<Double, V> m) {
      this(m.comparator());
      this.putAll(m);
   }

   public Double2ObjectAVLTreeMap(Double2ObjectMap<? extends V> m) {
      this();
      this.putAll(m);
   }

   public Double2ObjectAVLTreeMap(Double2ObjectSortedMap<V> m) {
      this(m.comparator());
      this.putAll(m);
   }

   public Double2ObjectAVLTreeMap(double[] k, V[] v, Comparator<? super Double> c) {
      this(c);
      if (k.length != v.length) {
         throw new IllegalArgumentException("The key array and the value array have different lengths (" + k.length + " and " + v.length + ")");
      } else {
         for (int i = 0; i < k.length; i++) {
            this.put(k[i], v[i]);
         }
      }
   }

   public Double2ObjectAVLTreeMap(double[] k, V[] v) {
      this(k, v, null);
   }

   final int compare(double k1, double k2) {
      return this.actualComparator == null ? Double.compare(k1, k2) : this.actualComparator.compare(k1, k2);
   }

   final Double2ObjectAVLTreeMap.Entry<V> findKey(double k) {
      Double2ObjectAVLTreeMap.Entry<V> e = this.tree;

      int cmp;
      while (e != null && (cmp = this.compare(k, e.key)) != 0) {
         e = cmp < 0 ? e.left() : e.right();
      }

      return e;
   }

   final Double2ObjectAVLTreeMap.Entry<V> locateKey(double k) {
      Double2ObjectAVLTreeMap.Entry<V> e = this.tree;
      Double2ObjectAVLTreeMap.Entry<V> last = this.tree;

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
   public V put(double k, V v) {
      Double2ObjectAVLTreeMap.Entry<V> e = this.add(k);
      V oldValue = e.value;
      e.value = v;
      return oldValue;
   }

   private Double2ObjectAVLTreeMap.Entry<V> add(double k) {
      this.modified = false;
      Double2ObjectAVLTreeMap.Entry<V> e = null;
      if (this.tree == null) {
         this.count++;
         e = this.tree = this.lastEntry = this.firstEntry = new Double2ObjectAVLTreeMap.Entry<>(k, this.defRetValue);
         this.modified = true;
      } else {
         Double2ObjectAVLTreeMap.Entry<V> p = this.tree;
         Double2ObjectAVLTreeMap.Entry<V> q = null;
         Double2ObjectAVLTreeMap.Entry<V> y = this.tree;
         Double2ObjectAVLTreeMap.Entry<V> z = null;
         Double2ObjectAVLTreeMap.Entry<V> w = null;
         int i = 0;

         while (true) {
            int cmp;
            if ((cmp = this.compare(k, p.key)) == 0) {
               return p;
            }

            if (p.balance() != 0) {
               i = 0;
               z = q;
               y = p;
            }

            if (this.dirPath[i++] = cmp > 0) {
               if (p.succ()) {
                  this.count++;
                  e = new Double2ObjectAVLTreeMap.Entry<>(k, this.defRetValue);
                  this.modified = true;
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
                  e = new Double2ObjectAVLTreeMap.Entry<>(k, this.defRetValue);
                  this.modified = true;
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
            Double2ObjectAVLTreeMap.Entry<V> x = y.left;
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
               return e;
            }

            Double2ObjectAVLTreeMap.Entry<V> x = y.right;
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

      return e;
   }

   private Double2ObjectAVLTreeMap.Entry<V> parent(Double2ObjectAVLTreeMap.Entry<V> e) {
      if (e == this.tree) {
         return null;
      } else {
         Double2ObjectAVLTreeMap.Entry<V> y = e;

         Double2ObjectAVLTreeMap.Entry<V> x;
         for (x = e; !y.succ(); y = y.right) {
            if (x.pred()) {
               Double2ObjectAVLTreeMap.Entry<V> p = x.left;
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

         Double2ObjectAVLTreeMap.Entry<V> p = y.right;
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
   public V remove(double k) {
      this.modified = false;
      if (this.tree == null) {
         return this.defRetValue;
      } else {
         Double2ObjectAVLTreeMap.Entry<V> p = this.tree;
         Double2ObjectAVLTreeMap.Entry<V> q = null;
         boolean dir = false;
         double kk = k;

         int cmp;
         while ((cmp = this.compare(kk, p.key)) != 0) {
            if (dir = cmp > 0) {
               q = p;
               if ((p = p.right()) == null) {
                  return this.defRetValue;
               }
            } else {
               q = p;
               if ((p = p.left()) == null) {
                  return this.defRetValue;
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
            Double2ObjectAVLTreeMap.Entry<V> r = p.right;
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
                  Double2ObjectAVLTreeMap.Entry<V> s = r.left;
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
            Double2ObjectAVLTreeMap.Entry<V> y = q;
            q = this.parent(q);
            if (!dir) {
               dir = q != null && q.left != y;
               y.incBalance();
               if (y.balance() == 1) {
                  break;
               }

               if (y.balance() == 2) {
                  Double2ObjectAVLTreeMap.Entry<V> x = y.right;

                  assert x != null;

                  if (x.balance() == -1) {
                     assert x.balance() == -1;

                     Double2ObjectAVLTreeMap.Entry<V> w = x.left;
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
                  Double2ObjectAVLTreeMap.Entry<V> xx = y.left;

                  assert xx != null;

                  if (xx.balance() == 1) {
                     assert xx.balance() == 1;

                     Double2ObjectAVLTreeMap.Entry<V> wx = xx.right;
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

         this.modified = true;
         this.count--;
         return p.value;
      }
   }

   @Override
   public boolean containsValue(Object v) {
      Double2ObjectAVLTreeMap<V>.ValueIterator i = new Double2ObjectAVLTreeMap.ValueIterator();
      int j = this.count;

      while (j-- != 0) {
         V ev = i.next();
         if (Objects.equals(ev, v)) {
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
   public V get(double k) {
      Double2ObjectAVLTreeMap.Entry<V> e = this.findKey(k);
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
   public ObjectSortedSet<Double2ObjectMap.Entry<V>> double2ObjectEntrySet() {
      if (this.entries == null) {
         this.entries = new AbstractObjectSortedSet<Double2ObjectMap.Entry<V>>() {
            final Comparator<? super Double2ObjectMap.Entry<V>> comparator = Double2ObjectAVLTreeMap.this.actualComparator == null
               ? (x, y) -> Double.compare(x.getDoubleKey(), y.getDoubleKey())
               : (x, y) -> Double2ObjectAVLTreeMap.this.actualComparator.compare(x.getDoubleKey(), y.getDoubleKey());

            @Override
            public Comparator<? super Double2ObjectMap.Entry<V>> comparator() {
               return this.comparator;
            }

            @Override
            public ObjectBidirectionalIterator<Double2ObjectMap.Entry<V>> iterator() {
               return Double2ObjectAVLTreeMap.this.new EntryIterator();
            }

            public ObjectBidirectionalIterator<Double2ObjectMap.Entry<V>> iterator(Double2ObjectMap.Entry<V> from) {
               return Double2ObjectAVLTreeMap.this.new EntryIterator(from.getDoubleKey());
            }

            @Override
            public boolean contains(Object o) {
               if (o != null && o instanceof java.util.Map.Entry) {
                  java.util.Map.Entry<?, ?> e = (java.util.Map.Entry<?, ?>)o;
                  if (e.getKey() == null) {
                     return false;
                  } else if (!(e.getKey() instanceof Double)) {
                     return false;
                  } else {
                     Double2ObjectAVLTreeMap.Entry<V> f = Double2ObjectAVLTreeMap.this.findKey((Double)e.getKey());
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
                  } else if (e.getKey() != null && e.getKey() instanceof Double) {
                     Double2ObjectAVLTreeMap.Entry<V> f = Double2ObjectAVLTreeMap.this.findKey((Double)e.getKey());
                     if (f != null && Objects.equals(f.getValue(), e.getValue())) {
                        Double2ObjectAVLTreeMap.this.remove(f.key);
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
               return Double2ObjectAVLTreeMap.this.count;
            }

            @Override
            public void clear() {
               Double2ObjectAVLTreeMap.this.clear();
            }

            public Double2ObjectMap.Entry<V> first() {
               return Double2ObjectAVLTreeMap.this.firstEntry;
            }

            public Double2ObjectMap.Entry<V> last() {
               return Double2ObjectAVLTreeMap.this.lastEntry;
            }

            public ObjectSortedSet<Double2ObjectMap.Entry<V>> subSet(Double2ObjectMap.Entry<V> from, Double2ObjectMap.Entry<V> to) {
               return Double2ObjectAVLTreeMap.this.subMap(from.getDoubleKey(), to.getDoubleKey()).double2ObjectEntrySet();
            }

            public ObjectSortedSet<Double2ObjectMap.Entry<V>> headSet(Double2ObjectMap.Entry<V> to) {
               return Double2ObjectAVLTreeMap.this.headMap(to.getDoubleKey()).double2ObjectEntrySet();
            }

            public ObjectSortedSet<Double2ObjectMap.Entry<V>> tailSet(Double2ObjectMap.Entry<V> from) {
               return Double2ObjectAVLTreeMap.this.tailMap(from.getDoubleKey()).double2ObjectEntrySet();
            }
         };
      }

      return this.entries;
   }

   @Override
   public DoubleSortedSet keySet() {
      if (this.keys == null) {
         this.keys = new Double2ObjectAVLTreeMap.KeySet();
      }

      return this.keys;
   }

   @Override
   public ObjectCollection<V> values() {
      if (this.values == null) {
         this.values = new AbstractObjectCollection<V>() {
            @Override
            public ObjectIterator<V> iterator() {
               return Double2ObjectAVLTreeMap.this.new ValueIterator();
            }

            @Override
            public boolean contains(Object k) {
               return Double2ObjectAVLTreeMap.this.containsValue(k);
            }

            @Override
            public int size() {
               return Double2ObjectAVLTreeMap.this.count;
            }

            @Override
            public void clear() {
               Double2ObjectAVLTreeMap.this.clear();
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
   public Double2ObjectSortedMap<V> headMap(double to) {
      return new Double2ObjectAVLTreeMap.Submap(0.0, true, to, false);
   }

   @Override
   public Double2ObjectSortedMap<V> tailMap(double from) {
      return new Double2ObjectAVLTreeMap.Submap(from, false, 0.0, true);
   }

   @Override
   public Double2ObjectSortedMap<V> subMap(double from, double to) {
      return new Double2ObjectAVLTreeMap.Submap(from, false, to, false);
   }

   public Double2ObjectAVLTreeMap<V> clone() {
      Double2ObjectAVLTreeMap<V> c;
      try {
         c = (Double2ObjectAVLTreeMap<V>)super.clone();
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
         Double2ObjectAVLTreeMap.Entry<V> rp = new Double2ObjectAVLTreeMap.Entry<>();
         Double2ObjectAVLTreeMap.Entry<V> rq = new Double2ObjectAVLTreeMap.Entry<>();
         Double2ObjectAVLTreeMap.Entry<V> p = rp;
         rp.left(this.tree);
         Double2ObjectAVLTreeMap.Entry<V> q = rq;
         rq.pred(null);

         while (true) {
            if (!p.pred()) {
               Double2ObjectAVLTreeMap.Entry<V> e = p.left.clone();
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
               Double2ObjectAVLTreeMap.Entry<V> e = p.right.clone();
               e.succ(q.right);
               e.pred(q);
               q.right(e);
            }
         }
      }
   }

   private void writeObject(ObjectOutputStream s) throws IOException {
      int n = this.count;
      Double2ObjectAVLTreeMap<V>.EntryIterator i = new Double2ObjectAVLTreeMap.EntryIterator();
      s.defaultWriteObject();

      while (n-- != 0) {
         Double2ObjectAVLTreeMap.Entry<V> e = i.nextEntry();
         s.writeDouble(e.key);
         s.writeObject(e.value);
      }
   }

   private Double2ObjectAVLTreeMap.Entry<V> readTree(ObjectInputStream s, int n, Double2ObjectAVLTreeMap.Entry<V> pred, Double2ObjectAVLTreeMap.Entry<V> succ) throws IOException, ClassNotFoundException {
      if (n == 1) {
         Double2ObjectAVLTreeMap.Entry<V> top = new Double2ObjectAVLTreeMap.Entry<>(s.readDouble(), (V)s.readObject());
         top.pred(pred);
         top.succ(succ);
         return top;
      } else if (n == 2) {
         Double2ObjectAVLTreeMap.Entry<V> top = new Double2ObjectAVLTreeMap.Entry<>(s.readDouble(), (V)s.readObject());
         top.right(new Double2ObjectAVLTreeMap.Entry<>(s.readDouble(), (V)s.readObject()));
         top.right.pred(top);
         top.balance(1);
         top.pred(pred);
         top.right.succ(succ);
         return top;
      } else {
         int rightN = n / 2;
         int leftN = n - rightN - 1;
         Double2ObjectAVLTreeMap.Entry<V> top = new Double2ObjectAVLTreeMap.Entry<>();
         top.left(this.readTree(s, leftN, pred, top));
         top.key = s.readDouble();
         top.value = (V)s.readObject();
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
         Double2ObjectAVLTreeMap.Entry<V> e = this.tree;

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

   private static final class Entry<V> extends AbstractDouble2ObjectMap.BasicEntry<V> implements Cloneable {
      private static final int SUCC_MASK = Integer.MIN_VALUE;
      private static final int PRED_MASK = 1073741824;
      private static final int BALANCE_MASK = 255;
      Double2ObjectAVLTreeMap.Entry<V> left;
      Double2ObjectAVLTreeMap.Entry<V> right;
      int info;

      Entry() {
         super(0.0, null);
      }

      Entry(double k, V v) {
         super(k, v);
         this.info = -1073741824;
      }

      Double2ObjectAVLTreeMap.Entry<V> left() {
         return (this.info & 1073741824) != 0 ? null : this.left;
      }

      Double2ObjectAVLTreeMap.Entry<V> right() {
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

      void pred(Double2ObjectAVLTreeMap.Entry<V> pred) {
         this.info |= 1073741824;
         this.left = pred;
      }

      void succ(Double2ObjectAVLTreeMap.Entry<V> succ) {
         this.info |= Integer.MIN_VALUE;
         this.right = succ;
      }

      void left(Double2ObjectAVLTreeMap.Entry<V> left) {
         this.info &= -1073741825;
         this.left = left;
      }

      void right(Double2ObjectAVLTreeMap.Entry<V> right) {
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

      Double2ObjectAVLTreeMap.Entry<V> next() {
         Double2ObjectAVLTreeMap.Entry<V> next = this.right;
         if ((this.info & -2147483648) == 0) {
            while ((next.info & 1073741824) == 0) {
               next = next.left;
            }
         }

         return next;
      }

      Double2ObjectAVLTreeMap.Entry<V> prev() {
         Double2ObjectAVLTreeMap.Entry<V> prev = this.left;
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

      public Double2ObjectAVLTreeMap.Entry<V> clone() {
         Double2ObjectAVLTreeMap.Entry<V> c;
         try {
            c = (Double2ObjectAVLTreeMap.Entry<V>)super.clone();
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
            java.util.Map.Entry<Double, V> e = (java.util.Map.Entry<Double, V>)o;
            return Double.doubleToLongBits(this.key) == Double.doubleToLongBits(e.getKey()) && Objects.equals(this.value, e.getValue());
         }
      }

      @Override
      public int hashCode() {
         return HashCommon.double2int(this.key) ^ (this.value == null ? 0 : this.value.hashCode());
      }

      @Override
      public String toString() {
         return this.key + "=>" + this.value;
      }
   }

   private class EntryIterator extends Double2ObjectAVLTreeMap<V>.TreeIterator implements ObjectListIterator<Double2ObjectMap.Entry<V>> {
      EntryIterator() {
      }

      EntryIterator(double k) {
         super(k);
      }

      public Double2ObjectMap.Entry<V> next() {
         return this.nextEntry();
      }

      public Double2ObjectMap.Entry<V> previous() {
         return this.previousEntry();
      }

      public void set(Double2ObjectMap.Entry<V> ok) {
         throw new UnsupportedOperationException();
      }

      public void add(Double2ObjectMap.Entry<V> ok) {
         throw new UnsupportedOperationException();
      }
   }

   private final class KeyIterator extends Double2ObjectAVLTreeMap<V>.TreeIterator implements DoubleListIterator {
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

   private class KeySet extends AbstractDouble2ObjectSortedMap<V>.KeySet {
      private KeySet() {
      }

      @Override
      public DoubleBidirectionalIterator iterator() {
         return Double2ObjectAVLTreeMap.this.new KeyIterator();
      }

      @Override
      public DoubleBidirectionalIterator iterator(double from) {
         return Double2ObjectAVLTreeMap.this.new KeyIterator(from);
      }
   }

   private final class Submap extends AbstractDouble2ObjectSortedMap<V> implements Serializable {
      private static final long serialVersionUID = -7046029254386353129L;
      double from;
      double to;
      boolean bottom;
      boolean top;
      protected transient ObjectSortedSet<Double2ObjectMap.Entry<V>> entries;
      protected transient DoubleSortedSet keys;
      protected transient ObjectCollection<V> values;

      public Submap(double from, boolean bottom, double to, boolean top) {
         if (!bottom && !top && Double2ObjectAVLTreeMap.this.compare(from, to) > 0) {
            throw new IllegalArgumentException("Start key (" + from + ") is larger than end key (" + to + ")");
         } else {
            this.from = from;
            this.bottom = bottom;
            this.to = to;
            this.top = top;
            this.defRetValue = Double2ObjectAVLTreeMap.this.defRetValue;
         }
      }

      @Override
      public void clear() {
         Double2ObjectAVLTreeMap<V>.Submap.SubmapIterator i = new Double2ObjectAVLTreeMap.Submap.SubmapIterator();

         while (i.hasNext()) {
            i.nextEntry();
            i.remove();
         }
      }

      final boolean in(double k) {
         return (this.bottom || Double2ObjectAVLTreeMap.this.compare(k, this.from) >= 0) && (this.top || Double2ObjectAVLTreeMap.this.compare(k, this.to) < 0);
      }

      @Override
      public ObjectSortedSet<Double2ObjectMap.Entry<V>> double2ObjectEntrySet() {
         if (this.entries == null) {
            this.entries = new AbstractObjectSortedSet<Double2ObjectMap.Entry<V>>() {
               @Override
               public ObjectBidirectionalIterator<Double2ObjectMap.Entry<V>> iterator() {
                  return Submap.this.new SubmapEntryIterator();
               }

               public ObjectBidirectionalIterator<Double2ObjectMap.Entry<V>> iterator(Double2ObjectMap.Entry<V> from) {
                  return Submap.this.new SubmapEntryIterator(from.getDoubleKey());
               }

               @Override
               public Comparator<? super Double2ObjectMap.Entry<V>> comparator() {
                  return Double2ObjectAVLTreeMap.this.double2ObjectEntrySet().comparator();
               }

               @Override
               public boolean contains(Object o) {
                  if (!(o instanceof java.util.Map.Entry)) {
                     return false;
                  } else {
                     java.util.Map.Entry<?, ?> e = (java.util.Map.Entry<?, ?>)o;
                     if (e.getKey() != null && e.getKey() instanceof Double) {
                        Double2ObjectAVLTreeMap.Entry<V> f = Double2ObjectAVLTreeMap.this.findKey((Double)e.getKey());
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
                     if (e.getKey() != null && e.getKey() instanceof Double) {
                        Double2ObjectAVLTreeMap.Entry<V> f = Double2ObjectAVLTreeMap.this.findKey((Double)e.getKey());
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

               public Double2ObjectMap.Entry<V> first() {
                  return Submap.this.firstEntry();
               }

               public Double2ObjectMap.Entry<V> last() {
                  return Submap.this.lastEntry();
               }

               public ObjectSortedSet<Double2ObjectMap.Entry<V>> subSet(Double2ObjectMap.Entry<V> from, Double2ObjectMap.Entry<V> to) {
                  return Submap.this.subMap(from.getDoubleKey(), to.getDoubleKey()).double2ObjectEntrySet();
               }

               public ObjectSortedSet<Double2ObjectMap.Entry<V>> headSet(Double2ObjectMap.Entry<V> to) {
                  return Submap.this.headMap(to.getDoubleKey()).double2ObjectEntrySet();
               }

               public ObjectSortedSet<Double2ObjectMap.Entry<V>> tailSet(Double2ObjectMap.Entry<V> from) {
                  return Submap.this.tailMap(from.getDoubleKey()).double2ObjectEntrySet();
               }
            };
         }

         return this.entries;
      }

      @Override
      public DoubleSortedSet keySet() {
         if (this.keys == null) {
            this.keys = new Double2ObjectAVLTreeMap.Submap.KeySet();
         }

         return this.keys;
      }

      @Override
      public ObjectCollection<V> values() {
         if (this.values == null) {
            this.values = new AbstractObjectCollection<V>() {
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
      public boolean containsKey(double k) {
         return this.in(k) && Double2ObjectAVLTreeMap.this.containsKey(k);
      }

      @Override
      public boolean containsValue(Object v) {
         Double2ObjectAVLTreeMap<V>.Submap.SubmapIterator i = new Double2ObjectAVLTreeMap.Submap.SubmapIterator();

         while (i.hasNext()) {
            Object ev = i.nextEntry().value;
            if (Objects.equals(ev, v)) {
               return true;
            }
         }

         return false;
      }

      @Override
      public V get(double k) {
         Double2ObjectAVLTreeMap.Entry<V> e;
         return this.in(k) && (e = Double2ObjectAVLTreeMap.this.findKey(k)) != null ? e.value : this.defRetValue;
      }

      @Override
      public V put(double k, V v) {
         Double2ObjectAVLTreeMap.this.modified = false;
         if (!this.in(k)) {
            throw new IllegalArgumentException(
               "Key (" + k + ") out of range [" + (this.bottom ? "-" : String.valueOf(this.from)) + ", " + (this.top ? "-" : String.valueOf(this.to)) + ")"
            );
         } else {
            V oldValue = Double2ObjectAVLTreeMap.this.put(k, v);
            return Double2ObjectAVLTreeMap.this.modified ? this.defRetValue : oldValue;
         }
      }

      @Override
      public V remove(double k) {
         Double2ObjectAVLTreeMap.this.modified = false;
         if (!this.in(k)) {
            return this.defRetValue;
         } else {
            V oldValue = Double2ObjectAVLTreeMap.this.remove(k);
            return Double2ObjectAVLTreeMap.this.modified ? oldValue : this.defRetValue;
         }
      }

      @Override
      public int size() {
         Double2ObjectAVLTreeMap<V>.Submap.SubmapIterator i = new Double2ObjectAVLTreeMap.Submap.SubmapIterator();
         int n = 0;

         while (i.hasNext()) {
            n++;
            i.nextEntry();
         }

         return n;
      }

      @Override
      public boolean isEmpty() {
         return !new Double2ObjectAVLTreeMap.Submap.SubmapIterator().hasNext();
      }

      @Override
      public DoubleComparator comparator() {
         return Double2ObjectAVLTreeMap.this.actualComparator;
      }

      @Override
      public Double2ObjectSortedMap<V> headMap(double to) {
         if (this.top) {
            return Double2ObjectAVLTreeMap.this.new Submap(this.from, this.bottom, to, false);
         } else {
            return Double2ObjectAVLTreeMap.this.compare(to, this.to) < 0 ? Double2ObjectAVLTreeMap.this.new Submap(this.from, this.bottom, to, false) : this;
         }
      }

      @Override
      public Double2ObjectSortedMap<V> tailMap(double from) {
         if (this.bottom) {
            return Double2ObjectAVLTreeMap.this.new Submap(from, false, this.to, this.top);
         } else {
            return Double2ObjectAVLTreeMap.this.compare(from, this.from) > 0 ? Double2ObjectAVLTreeMap.this.new Submap(from, false, this.to, this.top) : this;
         }
      }

      @Override
      public Double2ObjectSortedMap<V> subMap(double from, double to) {
         if (this.top && this.bottom) {
            return Double2ObjectAVLTreeMap.this.new Submap(from, false, to, false);
         } else {
            if (!this.top) {
               to = Double2ObjectAVLTreeMap.this.compare(to, this.to) < 0 ? to : this.to;
            }

            if (!this.bottom) {
               from = Double2ObjectAVLTreeMap.this.compare(from, this.from) > 0 ? from : this.from;
            }

            return !this.top && !this.bottom && from == this.from && to == this.to ? this : Double2ObjectAVLTreeMap.this.new Submap(from, false, to, false);
         }
      }

      public Double2ObjectAVLTreeMap.Entry<V> firstEntry() {
         if (Double2ObjectAVLTreeMap.this.tree == null) {
            return null;
         } else {
            Double2ObjectAVLTreeMap.Entry<V> e;
            if (this.bottom) {
               e = Double2ObjectAVLTreeMap.this.firstEntry;
            } else {
               e = Double2ObjectAVLTreeMap.this.locateKey(this.from);
               if (Double2ObjectAVLTreeMap.this.compare(e.key, this.from) < 0) {
                  e = e.next();
               }
            }

            return e != null && (this.top || Double2ObjectAVLTreeMap.this.compare(e.key, this.to) < 0) ? e : null;
         }
      }

      public Double2ObjectAVLTreeMap.Entry<V> lastEntry() {
         if (Double2ObjectAVLTreeMap.this.tree == null) {
            return null;
         } else {
            Double2ObjectAVLTreeMap.Entry<V> e;
            if (this.top) {
               e = Double2ObjectAVLTreeMap.this.lastEntry;
            } else {
               e = Double2ObjectAVLTreeMap.this.locateKey(this.to);
               if (Double2ObjectAVLTreeMap.this.compare(e.key, this.to) >= 0) {
                  e = e.prev();
               }
            }

            return e != null && (this.bottom || Double2ObjectAVLTreeMap.this.compare(e.key, this.from) >= 0) ? e : null;
         }
      }

      @Override
      public double firstDoubleKey() {
         Double2ObjectAVLTreeMap.Entry<V> e = this.firstEntry();
         if (e == null) {
            throw new NoSuchElementException();
         } else {
            return e.key;
         }
      }

      @Override
      public double lastDoubleKey() {
         Double2ObjectAVLTreeMap.Entry<V> e = this.lastEntry();
         if (e == null) {
            throw new NoSuchElementException();
         } else {
            return e.key;
         }
      }

      private class KeySet extends AbstractDouble2ObjectSortedMap<V>.KeySet {
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

      private class SubmapEntryIterator extends Double2ObjectAVLTreeMap<V>.Submap.SubmapIterator implements ObjectListIterator<Double2ObjectMap.Entry<V>> {
         SubmapEntryIterator() {
         }

         SubmapEntryIterator(double k) {
            super(k);
         }

         public Double2ObjectMap.Entry<V> next() {
            return this.nextEntry();
         }

         public Double2ObjectMap.Entry<V> previous() {
            return this.previousEntry();
         }
      }

      private class SubmapIterator extends Double2ObjectAVLTreeMap<V>.TreeIterator {
         SubmapIterator() {
            this.next = Submap.this.firstEntry();
         }

         SubmapIterator(double k) {
            this();
            if (this.next != null) {
               if (!Submap.this.bottom && Double2ObjectAVLTreeMap.this.compare(k, this.next.key) < 0) {
                  this.prev = null;
               } else if (!Submap.this.top && Double2ObjectAVLTreeMap.this.compare(k, (this.prev = Submap.this.lastEntry()).key) >= 0) {
                  this.next = null;
               } else {
                  this.next = Double2ObjectAVLTreeMap.this.locateKey(k);
                  if (Double2ObjectAVLTreeMap.this.compare(this.next.key, k) <= 0) {
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
            if (!Submap.this.bottom && this.prev != null && Double2ObjectAVLTreeMap.this.compare(this.prev.key, Submap.this.from) < 0) {
               this.prev = null;
            }
         }

         @Override
         void updateNext() {
            this.next = this.next.next();
            if (!Submap.this.top && this.next != null && Double2ObjectAVLTreeMap.this.compare(this.next.key, Submap.this.to) >= 0) {
               this.next = null;
            }
         }
      }

      private final class SubmapKeyIterator extends Double2ObjectAVLTreeMap<V>.Submap.SubmapIterator implements DoubleListIterator {
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

      private final class SubmapValueIterator extends Double2ObjectAVLTreeMap<V>.Submap.SubmapIterator implements ObjectListIterator<V> {
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
      Double2ObjectAVLTreeMap.Entry<V> prev;
      Double2ObjectAVLTreeMap.Entry<V> next;
      Double2ObjectAVLTreeMap.Entry<V> curr;
      int index = 0;

      TreeIterator() {
         this.next = Double2ObjectAVLTreeMap.this.firstEntry;
      }

      TreeIterator(double k) {
         if ((this.next = Double2ObjectAVLTreeMap.this.locateKey(k)) != null) {
            if (Double2ObjectAVLTreeMap.this.compare(this.next.key, k) <= 0) {
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

      Double2ObjectAVLTreeMap.Entry<V> nextEntry() {
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

      Double2ObjectAVLTreeMap.Entry<V> previousEntry() {
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
            Double2ObjectAVLTreeMap.this.remove(this.curr.key);
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

   private final class ValueIterator extends Double2ObjectAVLTreeMap<V>.TreeIterator implements ObjectListIterator<V> {
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
