lintools-datatypes
==================
[![Build Status](https://travis-ci.org/lintool/tools.svg?branch=master)](https://travis-ci.org/lintool/tools)
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/tl.lin/lintools-datatypes/badge.svg)](https://maven-badges.herokuapp.com/maven-central/tl.lin/lintools-datatypes)
[![Javadoc](https://javadoc-badge.appspot.com/tl.lin/lintools-datatypes.svg?label=javadoc)](https://lintool.github.io/tools-javadoc/lintools-datatypes/1.1.1/apidocs/)
[![LICENSE](https://img.shields.io/badge/license-Apache-blue.svg?style=flat-square)](../LICENSE)

This artifact contains lots of goodies I've accumulated over the years while hacking Hadoop. Highlights include:

+ Hadoop Writable datatypes (including primitive collections)
+ Frequency distributions and conditional frequency distributions
+ Classes to track of top *k* items

Hadoop Writable datatypes
-------------------------

This artifact contains lots of Hadoop `Writable` datatypes for building arbitrarily complex structures to use as keys and values in your mappers and reducers. Of course, using these building blocks won't be as fast as implementing your own custom `Writable` objects, but they *will* be faster than serializing to string (stuffing inside a `Text` object) and then parsing the string to reconstruct the data.

It's perhaps easiest to illustrate by examples:

```
PairOfWritables<PairOfStringInt, IntArrayWritable> data =
    new PairOfWritables<PairOfStringInt, IntArrayWritable>(
        new PairOfStringInt("foo", 42),
        new IntArrayWritable(new int[] {1, 2, 3, 4, 5}));
```

This creates the following data structure:

```
((foo, 42), [1, 2, 3, 4, 5])
```

Try this:

```
ArrayListWritable<PairOfInts> data = new ArrayListWritable<PairOfInts>();
data.add(new PairOfInts(1, 2));
data.add(new PairOfInts(3, 4));
data.add(new PairOfInts(5, 6));
data.add(new PairOfInts(7, 8));
```

Which corresponds to:

```
[(1, 2), (3, 4), (5, 6), (7, 8)]
```

And:

```
HMapIVW<PairOfStrings> data = new HMapIVW<PairOfStrings>();
data.put(1, new PairOfStrings("a", "b"));
data.put(2, new PairOfStrings("foo", "bar"));
data.put(4, new PairOfStrings("alpha", "beta"));
data.put(42, new PairOfStrings("four", "two"));
```

Gives:

```
{1=(a, b), 2=(foo, bar), 4=(alpha, beta), 42=(four, two)}
```

Note that these are all `Writable` objects, so they are Hadoop-compatible "out of the box".

The following provides more detail on each of the datatype packages, but the general theme of the design is to create custom classes for each primitive type. This means avoiding things like `List<Integer>` and `Map<String,Integer>`, which incur memory overhead due to a proliferation of Java objects and latency overhead for boxing/unboxing.

### tl.lin.data.array

Summary of classes:

+ `{Double,Float,Int,Long,Short}ArrayWritable`: these classes are `Writable` objects that wrap primitive arrays. Use this if you have static fixed-length arrays. Otherwise, use the other classes below.
+ `ArrayListOf{Double,Float,Int,Long,Short}`: these are implementations of `ArrayList` customized for each primitive type. Like a normal `ArrayList`, the backing array grows dynamically.
+ `ArrayListOf{Double,Float,Int,Long,Short}Writable`: the above classes, except as `Writable` objects.
+ `ArrayListWritable` and `ArrayListWritableComparable`: as the names suggest, holds arbitrary `Writable` and `WritableComparable` objects.

### tl.lin.data.map

This package contains specialized primitive maps. Understanding the naming convention: `HMap` is the base hash map.

+ `D` for double
+ `F` for float
+ `I` for integer
+ `S` for short
+ `K` for generic key
+ `V` for generic value
+ `W` for a Writable

So, for example, `HMapIIW` is a `Writable` hash map that maps from integers to integers and `HMapKFW` is a `Writable` hash map that maps from arbitrary (`WritableComparable`) keys to float values.

### tl.lin.data.pair

This contains `Writable` objects representing pairs. Naming convention is `PairOfFooBar` with `Foo` and `Bar` as the respective types.

Frequency distributions
-----------------------

This package contains classes for frequency distributions and conditional frequency distributions. Basically, they're built on hash maps, but with convenience methods for incrementing, decrementing, enumerating top entries, etc.

Classes to track of top *k* items
---------------------------------

This package contains subclasses of Hadoop's `PriorityQueue` for keeping track of top *k* items in terms of score.

