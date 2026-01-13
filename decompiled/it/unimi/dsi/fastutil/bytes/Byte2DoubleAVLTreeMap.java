package it.unimi.dsi.fastutil.bytes;

import it.unimi.dsi.fastutil.HashCommon;
import it.unimi.dsi.fastutil.doubles.AbstractDoubleCollection;
import it.unimi.dsi.fastutil.doubles.DoubleCollection;
import it.unimi.dsi.fastutil.doubles.DoubleIterator;
import it.unimi.dsi.fastutil.doubles.DoubleListIterator;
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

public class Byte2DoubleAVLTreeMap extends AbstractByte2DoubleSortedMap implements Serializable, Cloneable {
   protected transient Byte2DoubleAVLTreeMap.Entry tree;
   protected int count;
   protected transient Byte2DoubleAVLTreeMap.Entry firstEntry;
   protected transient Byte2DoubleAVLTreeMap.Entry lastEntry;
   protected transient ObjectSortedSet<Byte2DoubleMap.Entry> entries;
   protected transient ByteSortedSet keys;
   protected transient DoubleCollection values;
   protected transient boolean modified;
   protected Comparator<? super Byte> storedComparator;
   protected transient ByteComparator actualComparator;
   private static final long serialVersionUID = -7046029254386353129L;
   private transient boolean[] dirPath;

   public Byte2DoubleAVLTreeMap() {
      this.allocatePaths();
      this.tree = null;
      this.count = 0;
   }

   private void setActualComparator() {
      this.actualComparator = ByteComparators.asByteComparator(this.storedComparator);
   }

   public Byte2DoubleAVLTreeMap(Comparator<? super Byte> c) {
      this();
      this.storedComparator = c;
      this.setActualComparator();
   }

   public Byte2DoubleAVLTreeMap(Map<? extends Byte, ? extends Double> m) {
      this();
      this.putAll(m);
   }

   public Byte2DoubleAVLTreeMap(SortedMap<Byte, Double> m) {
      this(m.comparator());
      this.putAll(m);
   }

   public Byte2DoubleAVLTreeMap(Byte2DoubleMap m) {
      this();
      this.putAll(m);
   }

   public Byte2DoubleAVLTreeMap(Byte2DoubleSortedMap m) {
      this(m.comparator());
      this.putAll(m);
   }

   public Byte2DoubleAVLTreeMap(byte[] k, double[] v, Comparator<? super Byte> c) {
      this(c);
      if (k.length != v.length) {
         throw new IllegalArgumentException("The key array and the value array have different lengths (" + k.length + " and " + v.length + ")");
      } else {
         for (int i = 0; i < k.length; i++) {
            this.put(k[i], v[i]);
         }
      }
   }

   public Byte2DoubleAVLTreeMap(byte[] k, double[] v) {
      this(k, v, null);
   }

   final int compare(byte k1, byte k2) {
      return this.actualComparator == null ? Byte.compare(k1, k2) : this.actualComparator.compare(k1, k2);
   }

   final Byte2DoubleAVLTreeMap.Entry findKey(byte k) {
      Byte2DoubleAVLTreeMap.Entry e = this.tree;

      int cmp;
      while (e != null && (cmp = this.compare(k, e.key)) != 0) {
         e = cmp < 0 ? e.left() : e.right();
      }

      return e;
   }

   final Byte2DoubleAVLTreeMap.Entry locateKey(byte k) {
      Byte2DoubleAVLTreeMap.Entry e = this.tree;
      Byte2DoubleAVLTreeMap.Entry last = this.tree;

      int cmp;
      for (cmp = 0; e != null && (cmp = this.compare(k, e.key)) != 0; e = cmp < 0 ? e.left() : e.right()) {
         last = e;
      }

      return cmp == 0 ? e : last;
   }

   private void allocatePaths() {
      this.dirPath = new boolean[48];
   }

   public double addTo(byte k, double incr) {
      Byte2DoubleAVLTreeMap.Entry e = this.add(k);
      double oldValue = e.value;
      e.value += incr;
      return oldValue;
   }

   @Override
   public double put(byte k, double v) {
      Byte2DoubleAVLTreeMap.Entry e = this.add(k);
      double oldValue = e.value;
      e.value = v;
      return oldValue;
   }

   private Byte2DoubleAVLTreeMap.Entry add(byte k) {
      this.modified = false;
      Byte2DoubleAVLTreeMap.Entry e = null;
      if (this.tree == null) {
         this.count++;
         e = this.tree = this.lastEntry = this.firstEntry = new Byte2DoubleAVLTreeMap.Entry(k, this.defRetValue);
         this.modified = true;
      } else {
         Byte2DoubleAVLTreeMap.Entry p = this.tree;
         Byte2DoubleAVLTreeMap.Entry q = null;
         Byte2DoubleAVLTreeMap.Entry y = this.tree;
         Byte2DoubleAVLTreeMap.Entry z = null;
         Byte2DoubleAVLTreeMap.Entry w = null;
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
                  e = new Byte2DoubleAVLTreeMap.Entry(k, this.defRetValue);
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
                  e = new Byte2DoubleAVLTreeMap.Entry(k, this.defRetValue);
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
            Byte2DoubleAVLTreeMap.Entry x = y.left;
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

            Byte2DoubleAVLTreeMap.Entry x = y.right;
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

   private Byte2DoubleAVLTreeMap.Entry parent(Byte2DoubleAVLTreeMap.Entry e) {
      if (e == this.tree) {
         return null;
      } else {
         Byte2DoubleAVLTreeMap.Entry y = e;

         Byte2DoubleAVLTreeMap.Entry x;
         for (x = e; !y.succ(); y = y.right) {
            if (x.pred()) {
               Byte2DoubleAVLTreeMap.Entry p = x.left;
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

         Byte2DoubleAVLTreeMap.Entry p = y.right;
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
   public double remove(byte k) {
      this.modified = false;
      if (this.tree == null) {
         return this.defRetValue;
      } else {
         Byte2DoubleAVLTreeMap.Entry p = this.tree;
         Byte2DoubleAVLTreeMap.Entry q = null;
         boolean dir = false;
         byte kk = k;

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
            Byte2DoubleAVLTreeMap.Entry r = p.right;
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
                  Byte2DoubleAVLTreeMap.Entry s = r.left;
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
            Byte2DoubleAVLTreeMap.Entry y = q;
            q = this.parent(q);
            if (!dir) {
               dir = q != null && q.left != y;
               y.incBalance();
               if (y.balance() == 1) {
                  break;
               }

               if (y.balance() == 2) {
                  Byte2DoubleAVLTreeMap.Entry x = y.right;

                  assert x != null;

                  if (x.balance() == -1) {
                     assert x.balance() == -1;

                     Byte2DoubleAVLTreeMap.Entry w = x.left;
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
                  Byte2DoubleAVLTreeMap.Entry xx = y.left;

                  assert xx != null;

                  if (xx.balance() == 1) {
                     assert xx.balance() == 1;

                     Byte2DoubleAVLTreeMap.Entry wx = xx.right;
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
   public boolean containsValue(double v) {
      Byte2DoubleAVLTreeMap.ValueIterator i = new Byte2DoubleAVLTreeMap.ValueIterator();
      int j = this.count;

      while (j-- != 0) {
         double ev = i.nextDouble();
         if (Double.doubleToLongBits(ev) == Double.doubleToLongBits(v)) {
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
   public boolean containsKey(byte k) {
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
   public double get(byte k) {
      Byte2DoubleAVLTreeMap.Entry e = this.findKey(k);
      return e == null ? this.defRetValue : e.value;
   }

   @Override
   public byte firstByteKey() {
      if (this.tree == null) {
         throw new NoSuchElementException();
      } else {
         return this.firstEntry.key;
      }
   }

   @Override
   public byte lastByteKey() {
      if (this.tree == null) {
         throw new NoSuchElementException();
      } else {
         return this.lastEntry.key;
      }
   }

   @Override
   public ObjectSortedSet<Byte2DoubleMap.Entry> byte2DoubleEntrySet() {
      if (this.entries == null) {
         this.entries = new AbstractObjectSortedSet<Byte2DoubleMap.Entry>() {
            final Comparator<? super Byte2DoubleMap.Entry> comparator = Byte2DoubleAVLTreeMap.this.actualComparator == null
               ? (x, y) -> Byte.compare(x.getByteKey(), y.getByteKey())
               : (x, y) -> Byte2DoubleAVLTreeMap.this.actualComparator.compare(x.getByteKey(), y.getByteKey());

            @Override
            public Comparator<? super Byte2DoubleMap.Entry> comparator() {
               return this.comparator;
            }

            @Override
            public ObjectBidirectionalIterator<Byte2DoubleMap.Entry> iterator() {
               return Byte2DoubleAVLTreeMap.this.new EntryIterator();
            }

            public ObjectBidirectionalIterator<Byte2DoubleMap.Entry> iterator(Byte2DoubleMap.Entry from) {
               return Byte2DoubleAVLTreeMap.this.new EntryIterator(from.getByteKey());
            }

            @Override
            public boolean contains(Object o) {
               if (o != null && o instanceof java.util.Map.Entry) {
                  java.util.Map.Entry<?, ?> e = (java.util.Map.Entry<?, ?>)o;
                  if (e.getKey() == null) {
                     return false;
                  } else if (!(e.getKey() instanceof Byte)) {
                     return false;
                  } else if (e.getValue() != null && e.getValue() instanceof Double) {
                     Byte2DoubleAVLTreeMap.Entry f = Byte2DoubleAVLTreeMap.this.findKey((Byte)e.getKey());
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
                  } else if (e.getKey() == null || !(e.getKey() instanceof Byte)) {
                     return false;
                  } else if (!(e.getValue() instanceof Double)) {
                     return false;
                  } else {
                     Byte2DoubleAVLTreeMap.Entry f = Byte2DoubleAVLTreeMap.this.findKey((Byte)e.getKey());
                     if (f != null && Double.doubleToLongBits(f.getDoubleValue()) == Double.doubleToLongBits((Double)e.getValue())) {
                        Byte2DoubleAVLTreeMap.this.remove(f.key);
                        return true;
                     } else {
                        return false;
                     }
                  }
               }
            }

            @Override
            public int size() {
               return Byte2DoubleAVLTreeMap.this.count;
            }

            @Override
            public void clear() {
               Byte2DoubleAVLTreeMap.this.clear();
            }

            public Byte2DoubleMap.Entry first() {
               return Byte2DoubleAVLTreeMap.this.firstEntry;
            }

            public Byte2DoubleMap.Entry last() {
               return Byte2DoubleAVLTreeMap.this.lastEntry;
            }

            public ObjectSortedSet<Byte2DoubleMap.Entry> subSet(Byte2DoubleMap.Entry from, Byte2DoubleMap.Entry to) {
               return Byte2DoubleAVLTreeMap.this.subMap(from.getByteKey(), to.getByteKey()).byte2DoubleEntrySet();
            }

            public ObjectSortedSet<Byte2DoubleMap.Entry> headSet(Byte2DoubleMap.Entry to) {
               return Byte2DoubleAVLTreeMap.this.headMap(to.getByteKey()).byte2DoubleEntrySet();
            }

            public ObjectSortedSet<Byte2DoubleMap.Entry> tailSet(Byte2DoubleMap.Entry from) {
               return Byte2DoubleAVLTreeMap.this.tailMap(from.getByteKey()).byte2DoubleEntrySet();
            }
         };
      }

      return this.entries;
   }

   @Override
   public ByteSortedSet keySet() {
      if (this.keys == null) {
         this.keys = new Byte2DoubleAVLTreeMap.KeySet();
      }

      return this.keys;
   }

   @Override
   public DoubleCollection values() {
      if (this.values == null) {
         this.values = new AbstractDoubleCollection() {
            @Override
            public DoubleIterator iterator() {
               return Byte2DoubleAVLTreeMap.this.new ValueIterator();
            }

            @Override
            public boolean contains(double k) {
               return Byte2DoubleAVLTreeMap.this.containsValue(k);
            }

            @Override
            public int size() {
               return Byte2DoubleAVLTreeMap.this.count;
            }

            @Override
            public void clear() {
               Byte2DoubleAVLTreeMap.this.clear();
            }
         };
      }

      return this.values;
   }

   @Override
   public ByteComparator comparator() {
      return this.actualComparator;
   }

   @Override
   public Byte2DoubleSortedMap headMap(byte to) {
      return new Byte2DoubleAVLTreeMap.Submap((byte)0, true, to, false);
   }

   @Override
   public Byte2DoubleSortedMap tailMap(byte from) {
      return new Byte2DoubleAVLTreeMap.Submap(from, false, (byte)0, true);
   }

   @Override
   public Byte2DoubleSortedMap subMap(byte from, byte to) {
      return new Byte2DoubleAVLTreeMap.Submap(from, false, to, false);
   }

   public Byte2DoubleAVLTreeMap clone() {
      Byte2DoubleAVLTreeMap c;
      try {
         c = (Byte2DoubleAVLTreeMap)super.clone();
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
         Byte2DoubleAVLTreeMap.Entry rp = new Byte2DoubleAVLTreeMap.Entry();
         Byte2DoubleAVLTreeMap.Entry rq = new Byte2DoubleAVLTreeMap.Entry();
         Byte2DoubleAVLTreeMap.Entry p = rp;
         rp.left(this.tree);
         Byte2DoubleAVLTreeMap.Entry q = rq;
         rq.pred(null);

         while (true) {
            if (!p.pred()) {
               Byte2DoubleAVLTreeMap.Entry e = p.left.clone();
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
               Byte2DoubleAVLTreeMap.Entry e = p.right.clone();
               e.succ(q.right);
               e.pred(q);
               q.right(e);
            }
         }
      }
   }

   private void writeObject(ObjectOutputStream s) throws IOException {
      int n = this.count;
      Byte2DoubleAVLTreeMap.EntryIterator i = new Byte2DoubleAVLTreeMap.EntryIterator();
      s.defaultWriteObject();

      while (n-- != 0) {
         Byte2DoubleAVLTreeMap.Entry e = i.nextEntry();
         s.writeByte(e.key);
         s.writeDouble(e.value);
      }
   }

   private Byte2DoubleAVLTreeMap.Entry readTree(ObjectInputStream s, int n, Byte2DoubleAVLTreeMap.Entry pred, Byte2DoubleAVLTreeMap.Entry succ) throws IOException, ClassNotFoundException {
      if (n == 1) {
         Byte2DoubleAVLTreeMap.Entry top = new Byte2DoubleAVLTreeMap.Entry(s.readByte(), s.readDouble());
         top.pred(pred);
         top.succ(succ);
         return top;
      } else if (n == 2) {
         Byte2DoubleAVLTreeMap.Entry top = new Byte2DoubleAVLTreeMap.Entry(s.readByte(), s.readDouble());
         top.right(new Byte2DoubleAVLTreeMap.Entry(s.readByte(), s.readDouble()));
         top.right.pred(top);
         top.balance(1);
         top.pred(pred);
         top.right.succ(succ);
         return top;
      } else {
         int rightN = n / 2;
         int leftN = n - rightN - 1;
         Byte2DoubleAVLTreeMap.Entry top = new Byte2DoubleAVLTreeMap.Entry();
         top.left(this.readTree(s, leftN, pred, top));
         top.key = s.readByte();
         top.value = s.readDouble();
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
         Byte2DoubleAVLTreeMap.Entry e = this.tree;

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

   private static final class Entry extends AbstractByte2DoubleMap.BasicEntry implements Cloneable {
      private static final int SUCC_MASK = Integer.MIN_VALUE;
      private static final int PRED_MASK = 1073741824;
      private static final int BALANCE_MASK = 255;
      Byte2DoubleAVLTreeMap.Entry left;
      Byte2DoubleAVLTreeMap.Entry right;
      int info;

      Entry() {
         super((byte)0, 0.0);
      }

      Entry(byte k, double v) {
         super(k, v);
         this.info = -1073741824;
      }

      Byte2DoubleAVLTreeMap.Entry left() {
         return (this.info & 1073741824) != 0 ? null : this.left;
      }

      Byte2DoubleAVLTreeMap.Entry right() {
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

      void pred(Byte2DoubleAVLTreeMap.Entry pred) {
         this.info |= 1073741824;
         this.left = pred;
      }

      void succ(Byte2DoubleAVLTreeMap.Entry succ) {
         this.info |= Integer.MIN_VALUE;
         this.right = succ;
      }

      void left(Byte2DoubleAVLTreeMap.Entry left) {
         this.info &= -1073741825;
         this.left = left;
      }

      void right(Byte2DoubleAVLTreeMap.Entry right) {
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

      Byte2DoubleAVLTreeMap.Entry next() {
         Byte2DoubleAVLTreeMap.Entry next = this.right;
         if ((this.info & -2147483648) == 0) {
            while ((next.info & 1073741824) == 0) {
               next = next.left;
            }
         }

         return next;
      }

      Byte2DoubleAVLTreeMap.Entry prev() {
         Byte2DoubleAVLTreeMap.Entry prev = this.left;
         if ((this.info & 1073741824) == 0) {
            while ((prev.info & -2147483648) == 0) {
               prev = prev.right;
            }
         }

         return prev;
      }

      @Override
      public double setValue(double value) {
         double oldValue = this.value;
         this.value = value;
         return oldValue;
      }

      public Byte2DoubleAVLTreeMap.Entry clone() {
         Byte2DoubleAVLTreeMap.Entry c;
         try {
            c = (Byte2DoubleAVLTreeMap.Entry)super.clone();
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
            java.util.Map.Entry<Byte, Double> e = (java.util.Map.Entry<Byte, Double>)o;
            return this.key == e.getKey() && Double.doubleToLongBits(this.value) == Double.doubleToLongBits(e.getValue());
         }
      }

      @Override
      public int hashCode() {
         return this.key ^ HashCommon.double2int(this.value);
      }

      @Override
      public String toString() {
         return this.key + "=>" + this.value;
      }
   }

   private class EntryIterator extends Byte2DoubleAVLTreeMap.TreeIterator implements ObjectListIterator<Byte2DoubleMap.Entry> {
      EntryIterator() {
      }

      EntryIterator(byte k) {
         super(k);
      }

      public Byte2DoubleMap.Entry next() {
         return this.nextEntry();
      }

      public Byte2DoubleMap.Entry previous() {
         return this.previousEntry();
      }

      public void set(Byte2DoubleMap.Entry ok) {
         throw new UnsupportedOperationException();
      }

      public void add(Byte2DoubleMap.Entry ok) {
         throw new UnsupportedOperationException();
      }
   }

   private final class KeyIterator extends Byte2DoubleAVLTreeMap.TreeIterator implements ByteListIterator {
      public KeyIterator() {
      }

      public KeyIterator(byte k) {
         super(k);
      }

      @Override
      public byte nextByte() {
         return this.nextEntry().key;
      }

      @Override
      public byte previousByte() {
         return this.previousEntry().key;
      }
   }

   private class KeySet extends AbstractByte2DoubleSortedMap.KeySet {
      private KeySet() {
      }

      @Override
      public ByteBidirectionalIterator iterator() {
         return Byte2DoubleAVLTreeMap.this.new KeyIterator();
      }

      @Override
      public ByteBidirectionalIterator iterator(byte from) {
         return Byte2DoubleAVLTreeMap.this.new KeyIterator(from);
      }
   }

   private final class Submap extends AbstractByte2DoubleSortedMap implements Serializable {
      private static final long serialVersionUID = -7046029254386353129L;
      byte from;
      byte to;
      boolean bottom;
      boolean top;
      protected transient ObjectSortedSet<Byte2DoubleMap.Entry> entries;
      protected transient ByteSortedSet keys;
      protected transient DoubleCollection values;

      public Submap(byte from, boolean bottom, byte to, boolean top) {
         if (!bottom && !top && Byte2DoubleAVLTreeMap.this.compare(from, to) > 0) {
            throw new IllegalArgumentException("Start key (" + from + ") is larger than end key (" + to + ")");
         } else {
            this.from = from;
            this.bottom = bottom;
            this.to = to;
            this.top = top;
            this.defRetValue = Byte2DoubleAVLTreeMap.this.defRetValue;
         }
      }

      @Override
      public void clear() {
         Byte2DoubleAVLTreeMap.Submap.SubmapIterator i = new Byte2DoubleAVLTreeMap.Submap.SubmapIterator();

         while (i.hasNext()) {
            i.nextEntry();
            i.remove();
         }
      }

      final boolean in(byte k) {
         return (this.bottom || Byte2DoubleAVLTreeMap.this.compare(k, this.from) >= 0) && (this.top || Byte2DoubleAVLTreeMap.this.compare(k, this.to) < 0);
      }

      @Override
      public ObjectSortedSet<Byte2DoubleMap.Entry> byte2DoubleEntrySet() {
         if (this.entries == null) {
            this.entries = new AbstractObjectSortedSet<Byte2DoubleMap.Entry>() {
               @Override
               public ObjectBidirectionalIterator<Byte2DoubleMap.Entry> iterator() {
                  return Submap.this.new SubmapEntryIterator();
               }

               public ObjectBidirectionalIterator<Byte2DoubleMap.Entry> iterator(Byte2DoubleMap.Entry from) {
                  return Submap.this.new SubmapEntryIterator(from.getByteKey());
               }

               @Override
               public Comparator<? super Byte2DoubleMap.Entry> comparator() {
                  return Byte2DoubleAVLTreeMap.this.byte2DoubleEntrySet().comparator();
               }

               @Override
               public boolean contains(Object o) {
                  if (!(o instanceof java.util.Map.Entry)) {
                     return false;
                  } else {
                     java.util.Map.Entry<?, ?> e = (java.util.Map.Entry<?, ?>)o;
                     if (e.getKey() == null || !(e.getKey() instanceof Byte)) {
                        return false;
                     } else if (e.getValue() != null && e.getValue() instanceof Double) {
                        Byte2DoubleAVLTreeMap.Entry f = Byte2DoubleAVLTreeMap.this.findKey((Byte)e.getKey());
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
                     if (e.getKey() == null || !(e.getKey() instanceof Byte)) {
                        return false;
                     } else if (e.getValue() != null && e.getValue() instanceof Double) {
                        Byte2DoubleAVLTreeMap.Entry f = Byte2DoubleAVLTreeMap.this.findKey((Byte)e.getKey());
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

               public Byte2DoubleMap.Entry first() {
                  return Submap.this.firstEntry();
               }

               public Byte2DoubleMap.Entry last() {
                  return Submap.this.lastEntry();
               }

               public ObjectSortedSet<Byte2DoubleMap.Entry> subSet(Byte2DoubleMap.Entry from, Byte2DoubleMap.Entry to) {
                  return Submap.this.subMap(from.getByteKey(), to.getByteKey()).byte2DoubleEntrySet();
               }

               public ObjectSortedSet<Byte2DoubleMap.Entry> headSet(Byte2DoubleMap.Entry to) {
                  return Submap.this.headMap(to.getByteKey()).byte2DoubleEntrySet();
               }

               public ObjectSortedSet<Byte2DoubleMap.Entry> tailSet(Byte2DoubleMap.Entry from) {
                  return Submap.this.tailMap(from.getByteKey()).byte2DoubleEntrySet();
               }
            };
         }

         return this.entries;
      }

      @Override
      public ByteSortedSet keySet() {
         if (this.keys == null) {
            this.keys = new Byte2DoubleAVLTreeMap.Submap.KeySet();
         }

         return this.keys;
      }

      @Override
      public DoubleCollection values() {
         if (this.values == null) {
            this.values = new AbstractDoubleCollection() {
               @Override
               public DoubleIterator iterator() {
                  return Submap.this.new SubmapValueIterator();
               }

               @Override
               public boolean contains(double k) {
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
      public boolean containsKey(byte k) {
         return this.in(k) && Byte2DoubleAVLTreeMap.this.containsKey(k);
      }

      @Override
      public boolean containsValue(double v) {
         Byte2DoubleAVLTreeMap.Submap.SubmapIterator i = new Byte2DoubleAVLTreeMap.Submap.SubmapIterator();

         while (i.hasNext()) {
            double ev = i.nextEntry().value;
            if (Double.doubleToLongBits(ev) == Double.doubleToLongBits(v)) {
               return true;
            }
         }

         return false;
      }

      @Override
      public double get(byte k) {
         Byte2DoubleAVLTreeMap.Entry e;
         return this.in(k) && (e = Byte2DoubleAVLTreeMap.this.findKey(k)) != null ? e.value : this.defRetValue;
      }

      @Override
      public double put(byte k, double v) {
         Byte2DoubleAVLTreeMap.this.modified = false;
         if (!this.in(k)) {
            throw new IllegalArgumentException(
               "Key (" + k + ") out of range [" + (this.bottom ? "-" : String.valueOf(this.from)) + ", " + (this.top ? "-" : String.valueOf(this.to)) + ")"
            );
         } else {
            double oldValue = Byte2DoubleAVLTreeMap.this.put(k, v);
            return Byte2DoubleAVLTreeMap.this.modified ? this.defRetValue : oldValue;
         }
      }

      @Override
      public double remove(byte k) {
         Byte2DoubleAVLTreeMap.this.modified = false;
         if (!this.in(k)) {
            return this.defRetValue;
         } else {
            double oldValue = Byte2DoubleAVLTreeMap.this.remove(k);
            return Byte2DoubleAVLTreeMap.this.modified ? oldValue : this.defRetValue;
         }
      }

      @Override
      public int size() {
         Byte2DoubleAVLTreeMap.Submap.SubmapIterator i = new Byte2DoubleAVLTreeMap.Submap.SubmapIterator();
         int n = 0;

         while (i.hasNext()) {
            n++;
            i.nextEntry();
         }

         return n;
      }

      @Override
      public boolean isEmpty() {
         return !new Byte2DoubleAVLTreeMap.Submap.SubmapIterator().hasNext();
      }

      @Override
      public ByteComparator comparator() {
         return Byte2DoubleAVLTreeMap.this.actualComparator;
      }

      @Override
      public Byte2DoubleSortedMap headMap(byte to) {
         if (this.top) {
            return Byte2DoubleAVLTreeMap.this.new Submap(this.from, this.bottom, to, false);
         } else {
            return Byte2DoubleAVLTreeMap.this.compare(to, this.to) < 0 ? Byte2DoubleAVLTreeMap.this.new Submap(this.from, this.bottom, to, false) : this;
         }
      }

      @Override
      public Byte2DoubleSortedMap tailMap(byte from) {
         if (this.bottom) {
            return Byte2DoubleAVLTreeMap.this.new Submap(from, false, this.to, this.top);
         } else {
            return Byte2DoubleAVLTreeMap.this.compare(from, this.from) > 0 ? Byte2DoubleAVLTreeMap.this.new Submap(from, false, this.to, this.top) : this;
         }
      }

      @Override
      public Byte2DoubleSortedMap subMap(byte from, byte to) {
         if (this.top && this.bottom) {
            return Byte2DoubleAVLTreeMap.this.new Submap(from, false, to, false);
         } else {
            if (!this.top) {
               to = Byte2DoubleAVLTreeMap.this.compare(to, this.to) < 0 ? to : this.to;
            }

            if (!this.bottom) {
               from = Byte2DoubleAVLTreeMap.this.compare(from, this.from) > 0 ? from : this.from;
            }

            return !this.top && !this.bottom && from == this.from && to == this.to ? this : Byte2DoubleAVLTreeMap.this.new Submap(from, false, to, false);
         }
      }

      public Byte2DoubleAVLTreeMap.Entry firstEntry() {
         if (Byte2DoubleAVLTreeMap.this.tree == null) {
            return null;
         } else {
            Byte2DoubleAVLTreeMap.Entry e;
            if (this.bottom) {
               e = Byte2DoubleAVLTreeMap.this.firstEntry;
            } else {
               e = Byte2DoubleAVLTreeMap.this.locateKey(this.from);
               if (Byte2DoubleAVLTreeMap.this.compare(e.key, this.from) < 0) {
                  e = e.next();
               }
            }

            return e != null && (this.top || Byte2DoubleAVLTreeMap.this.compare(e.key, this.to) < 0) ? e : null;
         }
      }

      public Byte2DoubleAVLTreeMap.Entry lastEntry() {
         if (Byte2DoubleAVLTreeMap.this.tree == null) {
            return null;
         } else {
            Byte2DoubleAVLTreeMap.Entry e;
            if (this.top) {
               e = Byte2DoubleAVLTreeMap.this.lastEntry;
            } else {
               e = Byte2DoubleAVLTreeMap.this.locateKey(this.to);
               if (Byte2DoubleAVLTreeMap.this.compare(e.key, this.to) >= 0) {
                  e = e.prev();
               }
            }

            return e != null && (this.bottom || Byte2DoubleAVLTreeMap.this.compare(e.key, this.from) >= 0) ? e : null;
         }
      }

      @Override
      public byte firstByteKey() {
         Byte2DoubleAVLTreeMap.Entry e = this.firstEntry();
         if (e == null) {
            throw new NoSuchElementException();
         } else {
            return e.key;
         }
      }

      @Override
      public byte lastByteKey() {
         Byte2DoubleAVLTreeMap.Entry e = this.lastEntry();
         if (e == null) {
            throw new NoSuchElementException();
         } else {
            return e.key;
         }
      }

      private class KeySet extends AbstractByte2DoubleSortedMap.KeySet {
         private KeySet() {
         }

         @Override
         public ByteBidirectionalIterator iterator() {
            return Submap.this.new SubmapKeyIterator();
         }

         @Override
         public ByteBidirectionalIterator iterator(byte from) {
            return Submap.this.new SubmapKeyIterator(from);
         }
      }

      private class SubmapEntryIterator extends Byte2DoubleAVLTreeMap.Submap.SubmapIterator implements ObjectListIterator<Byte2DoubleMap.Entry> {
         SubmapEntryIterator() {
         }

         SubmapEntryIterator(byte k) {
            super(k);
         }

         public Byte2DoubleMap.Entry next() {
            return this.nextEntry();
         }

         public Byte2DoubleMap.Entry previous() {
            return this.previousEntry();
         }
      }

      private class SubmapIterator extends Byte2DoubleAVLTreeMap.TreeIterator {
         SubmapIterator() {
            this.next = Submap.this.firstEntry();
         }

         SubmapIterator(byte k) {
            this();
            if (this.next != null) {
               if (!Submap.this.bottom && Byte2DoubleAVLTreeMap.this.compare(k, this.next.key) < 0) {
                  this.prev = null;
               } else if (!Submap.this.top && Byte2DoubleAVLTreeMap.this.compare(k, (this.prev = Submap.this.lastEntry()).key) >= 0) {
                  this.next = null;
               } else {
                  this.next = Byte2DoubleAVLTreeMap.this.locateKey(k);
                  if (Byte2DoubleAVLTreeMap.this.compare(this.next.key, k) <= 0) {
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
            if (!Submap.this.bottom && this.prev != null && Byte2DoubleAVLTreeMap.this.compare(this.prev.key, Submap.this.from) < 0) {
               this.prev = null;
            }
         }

         @Override
         void updateNext() {
            this.next = this.next.next();
            if (!Submap.this.top && this.next != null && Byte2DoubleAVLTreeMap.this.compare(this.next.key, Submap.this.to) >= 0) {
               this.next = null;
            }
         }
      }

      private final class SubmapKeyIterator extends Byte2DoubleAVLTreeMap.Submap.SubmapIterator implements ByteListIterator {
         public SubmapKeyIterator() {
         }

         public SubmapKeyIterator(byte from) {
            super(from);
         }

         @Override
         public byte nextByte() {
            return this.nextEntry().key;
         }

         @Override
         public byte previousByte() {
            return this.previousEntry().key;
         }
      }

      private final class SubmapValueIterator extends Byte2DoubleAVLTreeMap.Submap.SubmapIterator implements DoubleListIterator {
         private SubmapValueIterator() {
         }

         @Override
         public double nextDouble() {
            return this.nextEntry().value;
         }

         @Override
         public double previousDouble() {
            return this.previousEntry().value;
         }
      }
   }

   private class TreeIterator {
      Byte2DoubleAVLTreeMap.Entry prev;
      Byte2DoubleAVLTreeMap.Entry next;
      Byte2DoubleAVLTreeMap.Entry curr;
      int index = 0;

      TreeIterator() {
         this.next = Byte2DoubleAVLTreeMap.this.firstEntry;
      }

      TreeIterator(byte k) {
         if ((this.next = Byte2DoubleAVLTreeMap.this.locateKey(k)) != null) {
            if (Byte2DoubleAVLTreeMap.this.compare(this.next.key, k) <= 0) {
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

      Byte2DoubleAVLTreeMap.Entry nextEntry() {
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

      Byte2DoubleAVLTreeMap.Entry previousEntry() {
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
            Byte2DoubleAVLTreeMap.this.remove(this.curr.key);
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

   private final class ValueIterator extends Byte2DoubleAVLTreeMap.TreeIterator implements DoubleListIterator {
      private ValueIterator() {
      }

      @Override
      public double nextDouble() {
         return this.nextEntry().value;
      }

      @Override
      public double previousDouble() {
         return this.previousEntry().value;
      }
   }
}
