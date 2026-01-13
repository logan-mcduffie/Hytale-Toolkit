package it.unimi.dsi.fastutil.shorts;

import it.unimi.dsi.fastutil.objects.AbstractObjectSortedSet;
import it.unimi.dsi.fastutil.objects.AbstractReferenceCollection;
import it.unimi.dsi.fastutil.objects.ObjectBidirectionalIterator;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import it.unimi.dsi.fastutil.objects.ObjectListIterator;
import it.unimi.dsi.fastutil.objects.ObjectSortedSet;
import it.unimi.dsi.fastutil.objects.ReferenceCollection;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.SortedMap;

public class Short2ReferenceAVLTreeMap<V> extends AbstractShort2ReferenceSortedMap<V> implements Serializable, Cloneable {
   protected transient Short2ReferenceAVLTreeMap.Entry<V> tree;
   protected int count;
   protected transient Short2ReferenceAVLTreeMap.Entry<V> firstEntry;
   protected transient Short2ReferenceAVLTreeMap.Entry<V> lastEntry;
   protected transient ObjectSortedSet<Short2ReferenceMap.Entry<V>> entries;
   protected transient ShortSortedSet keys;
   protected transient ReferenceCollection<V> values;
   protected transient boolean modified;
   protected Comparator<? super Short> storedComparator;
   protected transient ShortComparator actualComparator;
   private static final long serialVersionUID = -7046029254386353129L;
   private transient boolean[] dirPath;

   public Short2ReferenceAVLTreeMap() {
      this.allocatePaths();
      this.tree = null;
      this.count = 0;
   }

   private void setActualComparator() {
      this.actualComparator = ShortComparators.asShortComparator(this.storedComparator);
   }

   public Short2ReferenceAVLTreeMap(Comparator<? super Short> c) {
      this();
      this.storedComparator = c;
      this.setActualComparator();
   }

   public Short2ReferenceAVLTreeMap(Map<? extends Short, ? extends V> m) {
      this();
      this.putAll(m);
   }

   public Short2ReferenceAVLTreeMap(SortedMap<Short, V> m) {
      this(m.comparator());
      this.putAll(m);
   }

   public Short2ReferenceAVLTreeMap(Short2ReferenceMap<? extends V> m) {
      this();
      this.putAll(m);
   }

   public Short2ReferenceAVLTreeMap(Short2ReferenceSortedMap<V> m) {
      this(m.comparator());
      this.putAll(m);
   }

   public Short2ReferenceAVLTreeMap(short[] k, V[] v, Comparator<? super Short> c) {
      this(c);
      if (k.length != v.length) {
         throw new IllegalArgumentException("The key array and the value array have different lengths (" + k.length + " and " + v.length + ")");
      } else {
         for (int i = 0; i < k.length; i++) {
            this.put(k[i], v[i]);
         }
      }
   }

   public Short2ReferenceAVLTreeMap(short[] k, V[] v) {
      this(k, v, null);
   }

   final int compare(short k1, short k2) {
      return this.actualComparator == null ? Short.compare(k1, k2) : this.actualComparator.compare(k1, k2);
   }

   final Short2ReferenceAVLTreeMap.Entry<V> findKey(short k) {
      Short2ReferenceAVLTreeMap.Entry<V> e = this.tree;

      int cmp;
      while (e != null && (cmp = this.compare(k, e.key)) != 0) {
         e = cmp < 0 ? e.left() : e.right();
      }

      return e;
   }

   final Short2ReferenceAVLTreeMap.Entry<V> locateKey(short k) {
      Short2ReferenceAVLTreeMap.Entry<V> e = this.tree;
      Short2ReferenceAVLTreeMap.Entry<V> last = this.tree;

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
   public V put(short k, V v) {
      Short2ReferenceAVLTreeMap.Entry<V> e = this.add(k);
      V oldValue = e.value;
      e.value = v;
      return oldValue;
   }

   private Short2ReferenceAVLTreeMap.Entry<V> add(short k) {
      this.modified = false;
      Short2ReferenceAVLTreeMap.Entry<V> e = null;
      if (this.tree == null) {
         this.count++;
         e = this.tree = this.lastEntry = this.firstEntry = new Short2ReferenceAVLTreeMap.Entry<>(k, this.defRetValue);
         this.modified = true;
      } else {
         Short2ReferenceAVLTreeMap.Entry<V> p = this.tree;
         Short2ReferenceAVLTreeMap.Entry<V> q = null;
         Short2ReferenceAVLTreeMap.Entry<V> y = this.tree;
         Short2ReferenceAVLTreeMap.Entry<V> z = null;
         Short2ReferenceAVLTreeMap.Entry<V> w = null;
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
                  e = new Short2ReferenceAVLTreeMap.Entry<>(k, this.defRetValue);
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
                  e = new Short2ReferenceAVLTreeMap.Entry<>(k, this.defRetValue);
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

         for (int var14 = 0; p != e; p = this.dirPath[var14++] ? p.right : p.left) {
            if (this.dirPath[var14]) {
               p.incBalance();
            } else {
               p.decBalance();
            }
         }

         if (y.balance() == -2) {
            Short2ReferenceAVLTreeMap.Entry<V> x = y.left;
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

            Short2ReferenceAVLTreeMap.Entry<V> x = y.right;
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

   private Short2ReferenceAVLTreeMap.Entry<V> parent(Short2ReferenceAVLTreeMap.Entry<V> e) {
      if (e == this.tree) {
         return null;
      } else {
         Short2ReferenceAVLTreeMap.Entry<V> y = e;

         Short2ReferenceAVLTreeMap.Entry<V> x;
         for (x = e; !y.succ(); y = y.right) {
            if (x.pred()) {
               Short2ReferenceAVLTreeMap.Entry<V> p = x.left;
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

         Short2ReferenceAVLTreeMap.Entry<V> p = y.right;
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
   public V remove(short k) {
      this.modified = false;
      if (this.tree == null) {
         return this.defRetValue;
      } else {
         Short2ReferenceAVLTreeMap.Entry<V> p = this.tree;
         Short2ReferenceAVLTreeMap.Entry<V> q = null;
         boolean dir = false;
         short kk = k;

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
            Short2ReferenceAVLTreeMap.Entry<V> r = p.right;
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
                  Short2ReferenceAVLTreeMap.Entry<V> s = r.left;
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
            Short2ReferenceAVLTreeMap.Entry<V> y = q;
            q = this.parent(q);
            if (!dir) {
               dir = q != null && q.left != y;
               y.incBalance();
               if (y.balance() == 1) {
                  break;
               }

               if (y.balance() == 2) {
                  Short2ReferenceAVLTreeMap.Entry<V> x = y.right;

                  assert x != null;

                  if (x.balance() == -1) {
                     assert x.balance() == -1;

                     Short2ReferenceAVLTreeMap.Entry<V> w = x.left;
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
                  Short2ReferenceAVLTreeMap.Entry<V> xx = y.left;

                  assert xx != null;

                  if (xx.balance() == 1) {
                     assert xx.balance() == 1;

                     Short2ReferenceAVLTreeMap.Entry<V> wx = xx.right;
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
      Short2ReferenceAVLTreeMap<V>.ValueIterator i = new Short2ReferenceAVLTreeMap.ValueIterator();
      int j = this.count;

      while (j-- != 0) {
         V ev = i.next();
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
   public boolean containsKey(short k) {
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
   public V get(short k) {
      Short2ReferenceAVLTreeMap.Entry<V> e = this.findKey(k);
      return e == null ? this.defRetValue : e.value;
   }

   @Override
   public short firstShortKey() {
      if (this.tree == null) {
         throw new NoSuchElementException();
      } else {
         return this.firstEntry.key;
      }
   }

   @Override
   public short lastShortKey() {
      if (this.tree == null) {
         throw new NoSuchElementException();
      } else {
         return this.lastEntry.key;
      }
   }

   @Override
   public ObjectSortedSet<Short2ReferenceMap.Entry<V>> short2ReferenceEntrySet() {
      if (this.entries == null) {
         this.entries = new AbstractObjectSortedSet<Short2ReferenceMap.Entry<V>>() {
            final Comparator<? super Short2ReferenceMap.Entry<V>> comparator = Short2ReferenceAVLTreeMap.this.actualComparator == null
               ? (x, y) -> Short.compare(x.getShortKey(), y.getShortKey())
               : (x, y) -> Short2ReferenceAVLTreeMap.this.actualComparator.compare(x.getShortKey(), y.getShortKey());

            @Override
            public Comparator<? super Short2ReferenceMap.Entry<V>> comparator() {
               return this.comparator;
            }

            @Override
            public ObjectBidirectionalIterator<Short2ReferenceMap.Entry<V>> iterator() {
               return Short2ReferenceAVLTreeMap.this.new EntryIterator();
            }

            public ObjectBidirectionalIterator<Short2ReferenceMap.Entry<V>> iterator(Short2ReferenceMap.Entry<V> from) {
               return Short2ReferenceAVLTreeMap.this.new EntryIterator(from.getShortKey());
            }

            @Override
            public boolean contains(Object o) {
               if (o != null && o instanceof java.util.Map.Entry) {
                  java.util.Map.Entry<?, ?> e = (java.util.Map.Entry<?, ?>)o;
                  if (e.getKey() == null) {
                     return false;
                  } else if (!(e.getKey() instanceof Short)) {
                     return false;
                  } else {
                     Short2ReferenceAVLTreeMap.Entry<V> f = Short2ReferenceAVLTreeMap.this.findKey((Short)e.getKey());
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
                  } else if (e.getKey() != null && e.getKey() instanceof Short) {
                     Short2ReferenceAVLTreeMap.Entry<V> f = Short2ReferenceAVLTreeMap.this.findKey((Short)e.getKey());
                     if (f != null && f.getValue() == e.getValue()) {
                        Short2ReferenceAVLTreeMap.this.remove(f.key);
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
               return Short2ReferenceAVLTreeMap.this.count;
            }

            @Override
            public void clear() {
               Short2ReferenceAVLTreeMap.this.clear();
            }

            public Short2ReferenceMap.Entry<V> first() {
               return Short2ReferenceAVLTreeMap.this.firstEntry;
            }

            public Short2ReferenceMap.Entry<V> last() {
               return Short2ReferenceAVLTreeMap.this.lastEntry;
            }

            public ObjectSortedSet<Short2ReferenceMap.Entry<V>> subSet(Short2ReferenceMap.Entry<V> from, Short2ReferenceMap.Entry<V> to) {
               return Short2ReferenceAVLTreeMap.this.subMap(from.getShortKey(), to.getShortKey()).short2ReferenceEntrySet();
            }

            public ObjectSortedSet<Short2ReferenceMap.Entry<V>> headSet(Short2ReferenceMap.Entry<V> to) {
               return Short2ReferenceAVLTreeMap.this.headMap(to.getShortKey()).short2ReferenceEntrySet();
            }

            public ObjectSortedSet<Short2ReferenceMap.Entry<V>> tailSet(Short2ReferenceMap.Entry<V> from) {
               return Short2ReferenceAVLTreeMap.this.tailMap(from.getShortKey()).short2ReferenceEntrySet();
            }
         };
      }

      return this.entries;
   }

   @Override
   public ShortSortedSet keySet() {
      if (this.keys == null) {
         this.keys = new Short2ReferenceAVLTreeMap.KeySet();
      }

      return this.keys;
   }

   @Override
   public ReferenceCollection<V> values() {
      if (this.values == null) {
         this.values = new AbstractReferenceCollection<V>() {
            @Override
            public ObjectIterator<V> iterator() {
               return Short2ReferenceAVLTreeMap.this.new ValueIterator();
            }

            @Override
            public boolean contains(Object k) {
               return Short2ReferenceAVLTreeMap.this.containsValue(k);
            }

            @Override
            public int size() {
               return Short2ReferenceAVLTreeMap.this.count;
            }

            @Override
            public void clear() {
               Short2ReferenceAVLTreeMap.this.clear();
            }
         };
      }

      return this.values;
   }

   @Override
   public ShortComparator comparator() {
      return this.actualComparator;
   }

   @Override
   public Short2ReferenceSortedMap<V> headMap(short to) {
      return new Short2ReferenceAVLTreeMap.Submap((short)0, true, to, false);
   }

   @Override
   public Short2ReferenceSortedMap<V> tailMap(short from) {
      return new Short2ReferenceAVLTreeMap.Submap(from, false, (short)0, true);
   }

   @Override
   public Short2ReferenceSortedMap<V> subMap(short from, short to) {
      return new Short2ReferenceAVLTreeMap.Submap(from, false, to, false);
   }

   public Short2ReferenceAVLTreeMap<V> clone() {
      Short2ReferenceAVLTreeMap<V> c;
      try {
         c = (Short2ReferenceAVLTreeMap<V>)super.clone();
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
         Short2ReferenceAVLTreeMap.Entry<V> rp = new Short2ReferenceAVLTreeMap.Entry<>();
         Short2ReferenceAVLTreeMap.Entry<V> rq = new Short2ReferenceAVLTreeMap.Entry<>();
         Short2ReferenceAVLTreeMap.Entry<V> p = rp;
         rp.left(this.tree);
         Short2ReferenceAVLTreeMap.Entry<V> q = rq;
         rq.pred(null);

         while (true) {
            if (!p.pred()) {
               Short2ReferenceAVLTreeMap.Entry<V> e = p.left.clone();
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
               Short2ReferenceAVLTreeMap.Entry<V> e = p.right.clone();
               e.succ(q.right);
               e.pred(q);
               q.right(e);
            }
         }
      }
   }

   private void writeObject(ObjectOutputStream s) throws IOException {
      int n = this.count;
      Short2ReferenceAVLTreeMap<V>.EntryIterator i = new Short2ReferenceAVLTreeMap.EntryIterator();
      s.defaultWriteObject();

      while (n-- != 0) {
         Short2ReferenceAVLTreeMap.Entry<V> e = i.nextEntry();
         s.writeShort(e.key);
         s.writeObject(e.value);
      }
   }

   private Short2ReferenceAVLTreeMap.Entry<V> readTree(
      ObjectInputStream s, int n, Short2ReferenceAVLTreeMap.Entry<V> pred, Short2ReferenceAVLTreeMap.Entry<V> succ
   ) throws IOException, ClassNotFoundException {
      if (n == 1) {
         Short2ReferenceAVLTreeMap.Entry<V> top = new Short2ReferenceAVLTreeMap.Entry<>(s.readShort(), (V)s.readObject());
         top.pred(pred);
         top.succ(succ);
         return top;
      } else if (n == 2) {
         Short2ReferenceAVLTreeMap.Entry<V> top = new Short2ReferenceAVLTreeMap.Entry<>(s.readShort(), (V)s.readObject());
         top.right(new Short2ReferenceAVLTreeMap.Entry<>(s.readShort(), (V)s.readObject()));
         top.right.pred(top);
         top.balance(1);
         top.pred(pred);
         top.right.succ(succ);
         return top;
      } else {
         int rightN = n / 2;
         int leftN = n - rightN - 1;
         Short2ReferenceAVLTreeMap.Entry<V> top = new Short2ReferenceAVLTreeMap.Entry<>();
         top.left(this.readTree(s, leftN, pred, top));
         top.key = s.readShort();
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
         Short2ReferenceAVLTreeMap.Entry<V> e = this.tree;

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

   private static final class Entry<V> extends AbstractShort2ReferenceMap.BasicEntry<V> implements Cloneable {
      private static final int SUCC_MASK = Integer.MIN_VALUE;
      private static final int PRED_MASK = 1073741824;
      private static final int BALANCE_MASK = 255;
      Short2ReferenceAVLTreeMap.Entry<V> left;
      Short2ReferenceAVLTreeMap.Entry<V> right;
      int info;

      Entry() {
         super((short)0, null);
      }

      Entry(short k, V v) {
         super(k, v);
         this.info = -1073741824;
      }

      Short2ReferenceAVLTreeMap.Entry<V> left() {
         return (this.info & 1073741824) != 0 ? null : this.left;
      }

      Short2ReferenceAVLTreeMap.Entry<V> right() {
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

      void pred(Short2ReferenceAVLTreeMap.Entry<V> pred) {
         this.info |= 1073741824;
         this.left = pred;
      }

      void succ(Short2ReferenceAVLTreeMap.Entry<V> succ) {
         this.info |= Integer.MIN_VALUE;
         this.right = succ;
      }

      void left(Short2ReferenceAVLTreeMap.Entry<V> left) {
         this.info &= -1073741825;
         this.left = left;
      }

      void right(Short2ReferenceAVLTreeMap.Entry<V> right) {
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

      Short2ReferenceAVLTreeMap.Entry<V> next() {
         Short2ReferenceAVLTreeMap.Entry<V> next = this.right;
         if ((this.info & -2147483648) == 0) {
            while ((next.info & 1073741824) == 0) {
               next = next.left;
            }
         }

         return next;
      }

      Short2ReferenceAVLTreeMap.Entry<V> prev() {
         Short2ReferenceAVLTreeMap.Entry<V> prev = this.left;
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

      public Short2ReferenceAVLTreeMap.Entry<V> clone() {
         Short2ReferenceAVLTreeMap.Entry<V> c;
         try {
            c = (Short2ReferenceAVLTreeMap.Entry<V>)super.clone();
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
            java.util.Map.Entry<Short, V> e = (java.util.Map.Entry<Short, V>)o;
            return this.key == e.getKey() && this.value == e.getValue();
         }
      }

      @Override
      public int hashCode() {
         return this.key ^ (this.value == null ? 0 : System.identityHashCode(this.value));
      }

      @Override
      public String toString() {
         return this.key + "=>" + this.value;
      }
   }

   private class EntryIterator extends Short2ReferenceAVLTreeMap<V>.TreeIterator implements ObjectListIterator<Short2ReferenceMap.Entry<V>> {
      EntryIterator() {
      }

      EntryIterator(short k) {
         super(k);
      }

      public Short2ReferenceMap.Entry<V> next() {
         return this.nextEntry();
      }

      public Short2ReferenceMap.Entry<V> previous() {
         return this.previousEntry();
      }

      public void set(Short2ReferenceMap.Entry<V> ok) {
         throw new UnsupportedOperationException();
      }

      public void add(Short2ReferenceMap.Entry<V> ok) {
         throw new UnsupportedOperationException();
      }
   }

   private final class KeyIterator extends Short2ReferenceAVLTreeMap<V>.TreeIterator implements ShortListIterator {
      public KeyIterator() {
      }

      public KeyIterator(short k) {
         super(k);
      }

      @Override
      public short nextShort() {
         return this.nextEntry().key;
      }

      @Override
      public short previousShort() {
         return this.previousEntry().key;
      }
   }

   private class KeySet extends AbstractShort2ReferenceSortedMap<V>.KeySet {
      private KeySet() {
      }

      @Override
      public ShortBidirectionalIterator iterator() {
         return Short2ReferenceAVLTreeMap.this.new KeyIterator();
      }

      @Override
      public ShortBidirectionalIterator iterator(short from) {
         return Short2ReferenceAVLTreeMap.this.new KeyIterator(from);
      }
   }

   private final class Submap extends AbstractShort2ReferenceSortedMap<V> implements Serializable {
      private static final long serialVersionUID = -7046029254386353129L;
      short from;
      short to;
      boolean bottom;
      boolean top;
      protected transient ObjectSortedSet<Short2ReferenceMap.Entry<V>> entries;
      protected transient ShortSortedSet keys;
      protected transient ReferenceCollection<V> values;

      public Submap(short from, boolean bottom, short to, boolean top) {
         if (!bottom && !top && Short2ReferenceAVLTreeMap.this.compare(from, to) > 0) {
            throw new IllegalArgumentException("Start key (" + from + ") is larger than end key (" + to + ")");
         } else {
            this.from = from;
            this.bottom = bottom;
            this.to = to;
            this.top = top;
            this.defRetValue = Short2ReferenceAVLTreeMap.this.defRetValue;
         }
      }

      @Override
      public void clear() {
         Short2ReferenceAVLTreeMap<V>.Submap.SubmapIterator i = new Short2ReferenceAVLTreeMap.Submap.SubmapIterator();

         while (i.hasNext()) {
            i.nextEntry();
            i.remove();
         }
      }

      final boolean in(short k) {
         return (this.bottom || Short2ReferenceAVLTreeMap.this.compare(k, this.from) >= 0)
            && (this.top || Short2ReferenceAVLTreeMap.this.compare(k, this.to) < 0);
      }

      @Override
      public ObjectSortedSet<Short2ReferenceMap.Entry<V>> short2ReferenceEntrySet() {
         if (this.entries == null) {
            this.entries = new AbstractObjectSortedSet<Short2ReferenceMap.Entry<V>>() {
               @Override
               public ObjectBidirectionalIterator<Short2ReferenceMap.Entry<V>> iterator() {
                  return Submap.this.new SubmapEntryIterator();
               }

               public ObjectBidirectionalIterator<Short2ReferenceMap.Entry<V>> iterator(Short2ReferenceMap.Entry<V> from) {
                  return Submap.this.new SubmapEntryIterator(from.getShortKey());
               }

               @Override
               public Comparator<? super Short2ReferenceMap.Entry<V>> comparator() {
                  return Short2ReferenceAVLTreeMap.this.short2ReferenceEntrySet().comparator();
               }

               @Override
               public boolean contains(Object o) {
                  if (!(o instanceof java.util.Map.Entry)) {
                     return false;
                  } else {
                     java.util.Map.Entry<?, ?> e = (java.util.Map.Entry<?, ?>)o;
                     if (e.getKey() != null && e.getKey() instanceof Short) {
                        Short2ReferenceAVLTreeMap.Entry<V> f = Short2ReferenceAVLTreeMap.this.findKey((Short)e.getKey());
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
                     if (e.getKey() != null && e.getKey() instanceof Short) {
                        Short2ReferenceAVLTreeMap.Entry<V> f = Short2ReferenceAVLTreeMap.this.findKey((Short)e.getKey());
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

               public Short2ReferenceMap.Entry<V> first() {
                  return Submap.this.firstEntry();
               }

               public Short2ReferenceMap.Entry<V> last() {
                  return Submap.this.lastEntry();
               }

               public ObjectSortedSet<Short2ReferenceMap.Entry<V>> subSet(Short2ReferenceMap.Entry<V> from, Short2ReferenceMap.Entry<V> to) {
                  return Submap.this.subMap(from.getShortKey(), to.getShortKey()).short2ReferenceEntrySet();
               }

               public ObjectSortedSet<Short2ReferenceMap.Entry<V>> headSet(Short2ReferenceMap.Entry<V> to) {
                  return Submap.this.headMap(to.getShortKey()).short2ReferenceEntrySet();
               }

               public ObjectSortedSet<Short2ReferenceMap.Entry<V>> tailSet(Short2ReferenceMap.Entry<V> from) {
                  return Submap.this.tailMap(from.getShortKey()).short2ReferenceEntrySet();
               }
            };
         }

         return this.entries;
      }

      @Override
      public ShortSortedSet keySet() {
         if (this.keys == null) {
            this.keys = new Short2ReferenceAVLTreeMap.Submap.KeySet();
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
      public boolean containsKey(short k) {
         return this.in(k) && Short2ReferenceAVLTreeMap.this.containsKey(k);
      }

      @Override
      public boolean containsValue(Object v) {
         Short2ReferenceAVLTreeMap<V>.Submap.SubmapIterator i = new Short2ReferenceAVLTreeMap.Submap.SubmapIterator();

         while (i.hasNext()) {
            Object ev = i.nextEntry().value;
            if (ev == v) {
               return true;
            }
         }

         return false;
      }

      @Override
      public V get(short k) {
         Short2ReferenceAVLTreeMap.Entry<V> e;
         return this.in(k) && (e = Short2ReferenceAVLTreeMap.this.findKey(k)) != null ? e.value : this.defRetValue;
      }

      @Override
      public V put(short k, V v) {
         Short2ReferenceAVLTreeMap.this.modified = false;
         if (!this.in(k)) {
            throw new IllegalArgumentException(
               "Key (" + k + ") out of range [" + (this.bottom ? "-" : String.valueOf(this.from)) + ", " + (this.top ? "-" : String.valueOf(this.to)) + ")"
            );
         } else {
            V oldValue = Short2ReferenceAVLTreeMap.this.put(k, v);
            return Short2ReferenceAVLTreeMap.this.modified ? this.defRetValue : oldValue;
         }
      }

      @Override
      public V remove(short k) {
         Short2ReferenceAVLTreeMap.this.modified = false;
         if (!this.in(k)) {
            return this.defRetValue;
         } else {
            V oldValue = Short2ReferenceAVLTreeMap.this.remove(k);
            return Short2ReferenceAVLTreeMap.this.modified ? oldValue : this.defRetValue;
         }
      }

      @Override
      public int size() {
         Short2ReferenceAVLTreeMap<V>.Submap.SubmapIterator i = new Short2ReferenceAVLTreeMap.Submap.SubmapIterator();
         int n = 0;

         while (i.hasNext()) {
            n++;
            i.nextEntry();
         }

         return n;
      }

      @Override
      public boolean isEmpty() {
         return !new Short2ReferenceAVLTreeMap.Submap.SubmapIterator().hasNext();
      }

      @Override
      public ShortComparator comparator() {
         return Short2ReferenceAVLTreeMap.this.actualComparator;
      }

      @Override
      public Short2ReferenceSortedMap<V> headMap(short to) {
         if (this.top) {
            return Short2ReferenceAVLTreeMap.this.new Submap(this.from, this.bottom, to, false);
         } else {
            return Short2ReferenceAVLTreeMap.this.compare(to, this.to) < 0
               ? Short2ReferenceAVLTreeMap.this.new Submap(this.from, this.bottom, to, false)
               : this;
         }
      }

      @Override
      public Short2ReferenceSortedMap<V> tailMap(short from) {
         if (this.bottom) {
            return Short2ReferenceAVLTreeMap.this.new Submap(from, false, this.to, this.top);
         } else {
            return Short2ReferenceAVLTreeMap.this.compare(from, this.from) > 0
               ? Short2ReferenceAVLTreeMap.this.new Submap(from, false, this.to, this.top)
               : this;
         }
      }

      @Override
      public Short2ReferenceSortedMap<V> subMap(short from, short to) {
         if (this.top && this.bottom) {
            return Short2ReferenceAVLTreeMap.this.new Submap(from, false, to, false);
         } else {
            if (!this.top) {
               to = Short2ReferenceAVLTreeMap.this.compare(to, this.to) < 0 ? to : this.to;
            }

            if (!this.bottom) {
               from = Short2ReferenceAVLTreeMap.this.compare(from, this.from) > 0 ? from : this.from;
            }

            return !this.top && !this.bottom && from == this.from && to == this.to ? this : Short2ReferenceAVLTreeMap.this.new Submap(from, false, to, false);
         }
      }

      public Short2ReferenceAVLTreeMap.Entry<V> firstEntry() {
         if (Short2ReferenceAVLTreeMap.this.tree == null) {
            return null;
         } else {
            Short2ReferenceAVLTreeMap.Entry<V> e;
            if (this.bottom) {
               e = Short2ReferenceAVLTreeMap.this.firstEntry;
            } else {
               e = Short2ReferenceAVLTreeMap.this.locateKey(this.from);
               if (Short2ReferenceAVLTreeMap.this.compare(e.key, this.from) < 0) {
                  e = e.next();
               }
            }

            return e != null && (this.top || Short2ReferenceAVLTreeMap.this.compare(e.key, this.to) < 0) ? e : null;
         }
      }

      public Short2ReferenceAVLTreeMap.Entry<V> lastEntry() {
         if (Short2ReferenceAVLTreeMap.this.tree == null) {
            return null;
         } else {
            Short2ReferenceAVLTreeMap.Entry<V> e;
            if (this.top) {
               e = Short2ReferenceAVLTreeMap.this.lastEntry;
            } else {
               e = Short2ReferenceAVLTreeMap.this.locateKey(this.to);
               if (Short2ReferenceAVLTreeMap.this.compare(e.key, this.to) >= 0) {
                  e = e.prev();
               }
            }

            return e != null && (this.bottom || Short2ReferenceAVLTreeMap.this.compare(e.key, this.from) >= 0) ? e : null;
         }
      }

      @Override
      public short firstShortKey() {
         Short2ReferenceAVLTreeMap.Entry<V> e = this.firstEntry();
         if (e == null) {
            throw new NoSuchElementException();
         } else {
            return e.key;
         }
      }

      @Override
      public short lastShortKey() {
         Short2ReferenceAVLTreeMap.Entry<V> e = this.lastEntry();
         if (e == null) {
            throw new NoSuchElementException();
         } else {
            return e.key;
         }
      }

      private class KeySet extends AbstractShort2ReferenceSortedMap<V>.KeySet {
         private KeySet() {
         }

         @Override
         public ShortBidirectionalIterator iterator() {
            return Submap.this.new SubmapKeyIterator();
         }

         @Override
         public ShortBidirectionalIterator iterator(short from) {
            return Submap.this.new SubmapKeyIterator(from);
         }
      }

      private class SubmapEntryIterator extends Short2ReferenceAVLTreeMap<V>.Submap.SubmapIterator implements ObjectListIterator<Short2ReferenceMap.Entry<V>> {
         SubmapEntryIterator() {
         }

         SubmapEntryIterator(short k) {
            super(k);
         }

         public Short2ReferenceMap.Entry<V> next() {
            return this.nextEntry();
         }

         public Short2ReferenceMap.Entry<V> previous() {
            return this.previousEntry();
         }
      }

      private class SubmapIterator extends Short2ReferenceAVLTreeMap<V>.TreeIterator {
         SubmapIterator() {
            this.next = Submap.this.firstEntry();
         }

         SubmapIterator(short k) {
            this();
            if (this.next != null) {
               if (!Submap.this.bottom && Short2ReferenceAVLTreeMap.this.compare(k, this.next.key) < 0) {
                  this.prev = null;
               } else if (!Submap.this.top && Short2ReferenceAVLTreeMap.this.compare(k, (this.prev = Submap.this.lastEntry()).key) >= 0) {
                  this.next = null;
               } else {
                  this.next = Short2ReferenceAVLTreeMap.this.locateKey(k);
                  if (Short2ReferenceAVLTreeMap.this.compare(this.next.key, k) <= 0) {
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
            if (!Submap.this.bottom && this.prev != null && Short2ReferenceAVLTreeMap.this.compare(this.prev.key, Submap.this.from) < 0) {
               this.prev = null;
            }
         }

         @Override
         void updateNext() {
            this.next = this.next.next();
            if (!Submap.this.top && this.next != null && Short2ReferenceAVLTreeMap.this.compare(this.next.key, Submap.this.to) >= 0) {
               this.next = null;
            }
         }
      }

      private final class SubmapKeyIterator extends Short2ReferenceAVLTreeMap<V>.Submap.SubmapIterator implements ShortListIterator {
         public SubmapKeyIterator() {
         }

         public SubmapKeyIterator(short from) {
            super(from);
         }

         @Override
         public short nextShort() {
            return this.nextEntry().key;
         }

         @Override
         public short previousShort() {
            return this.previousEntry().key;
         }
      }

      private final class SubmapValueIterator extends Short2ReferenceAVLTreeMap<V>.Submap.SubmapIterator implements ObjectListIterator<V> {
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
      Short2ReferenceAVLTreeMap.Entry<V> prev;
      Short2ReferenceAVLTreeMap.Entry<V> next;
      Short2ReferenceAVLTreeMap.Entry<V> curr;
      int index = 0;

      TreeIterator() {
         this.next = Short2ReferenceAVLTreeMap.this.firstEntry;
      }

      TreeIterator(short k) {
         if ((this.next = Short2ReferenceAVLTreeMap.this.locateKey(k)) != null) {
            if (Short2ReferenceAVLTreeMap.this.compare(this.next.key, k) <= 0) {
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

      Short2ReferenceAVLTreeMap.Entry<V> nextEntry() {
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

      Short2ReferenceAVLTreeMap.Entry<V> previousEntry() {
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
            Short2ReferenceAVLTreeMap.this.remove(this.curr.key);
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

   private final class ValueIterator extends Short2ReferenceAVLTreeMap<V>.TreeIterator implements ObjectListIterator<V> {
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
