# Proximus

Proximus is an experiment stemming from the idea that tries could be used to look represent points
in a 2-d space in a way that would allow for fast querying of "neighbouring" points.

In order to achieve this, we first have to turn both the lattitudes and longitudes into base 4
numbers such that the more geographic proximity between to points, the more characters their
stringified versions share, and this, going from left to right.

For example, if city 1 is "aaa", city 2 is "aab" and city 3 is "a81", we can tell that city 1 is
closer to city 2 than city 3 and city 3 is closer to city 1 than city 3.
