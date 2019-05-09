# Damysos

Damysos is an experiment stemming from the idea that tries could be used to store points in a
n-dimensional space in a way that would allow for fast querying of "neighboring" points.

In order to achieve this, we first have to turn each coordinate or every point into a base 4
number such that the more spacial proximity between two points, the more characters their
transformed coordinates share, and this, going from left to right.

For example, if point A's encoded x-axis coordinate is "aaa", point B's is "aab" and point C's is
"a81", we can tell that point A is closer to point B than point C and point A is closer to point C
than point B (this along the aformentionned x-axis).

This way, in order to get the neighboring points of a GPS coordinate, we only have to compute the
"trie path" for those coordinates, and descend the trie at the desired depth (the level of
precision, or "zoom"). Then, we take all the leaves below that point.

What's interesting in this approach, in my opinion, resides in the fact that nowhere in the code we
are actually commparing GPS coordinates, calculating distances etc. The data structure itself, in
this case a Trie, _is_ the logic.

From early testing, on a _Intel Core i7-7700HQ CPU @ 2.80GHz_ and on a dataset of 128769 points, it
finds all the neighboring points of a given GPS coordinate in under 400μs.
