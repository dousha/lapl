# LAPL - Something like LISP, but in a dirty way

## Syntax (in an informal way)

To call a function:
```(function)```

... With some parameters:
```
(function parameters)
(function (function2 parameters for func2) parameters for func1)
```

To define a new function:
```
(def (a-new-function formal-param) (old-function-combinations formal-param))
```

## Primitives

### Arithmetic operations

| Operator | Function                 | Type    |
|:-------- |:------------------------ | -------:|
| ln       | Natural logarithm        | Monadic |
| +1       | Increase                 | Monadic |
| -1       | Decrease                 | Monadic |
| +        | Add                      | Dyadic  |
| -        | Subtract                 | Dyadic  |
| *        | Multiply                 | Dyadic  |
| /        | Divide                   | Dyadic  |
| ^        | Power                    | Dyadic  |
| &lt;     | Lesser than              | Dyadic  |
| &gt;     | Greater than             | Dyadic  |
| =        | Equal                    | Dyadic  |
| ==       | Strictly equal[^equ]     | Dyadic  |
| !=       | Not equal                | Dyadic  |
| !==      | Strictly not equal       | Dyadic  |
| &lt;=    | Not greater than         | Dyadic  |
| &gt;=    | Not lesser than          | Dyadic  |

### I/O

| Operator | Function                    | Type    |
|:-------- |:--------------------------- | -------:|
| input    | Read a name from stdin      | Monadic |
| print    | Print some names and values | Complex |
| display  | (Deprecated) Print a value  | Monadic |

### Flow control

| Operator | Function             | Type    |
|:-------- |:-------------------- | -------:|
| while    | \_(:3\| L)\_[^omit]  | Dyadic  |
| if       | \_(:3\| L)\_         | Triadic |

### Variable control

| Operator | Function                 | Type    |
|:-------- |:------------------------ | -------:|
| drop!    | Erases a global variable | Monadic |
| set!     | Sets a global variable   | Dyadic  |
| let      | Sets a local variable    | Triadic |

### Array manipulation

_TODO: This part is a stub of an incomplete feature._

| Operator | Function                                  | Type    |
|:-------- |:----------------------------------------- | -------:|
| len      | Get the length of a NodeGroup or an Array | Monadic |

### Misc

| Operator | Function          | Type    |
|:-------- |:----------------- | -------:|
| halt     | Halt immediately  | Simple  |
| ret      | Evaluate the node | Monadic |
| def      | Define a function | Dyadic  |

[^equ]: 'Strictly equal' means two numbers are equal in byte level.
Sometimes computers add funny because there is rounding error.
(e.g. 0.1 + 0.2 = 0.30000000000000004, sadly).
Since all arithmetic operations in LAPL would be done with 
doubles, it is necessary to introduce 'equal (=)', which has 
a tolerance of 1e-10.

[^omit]: I thought it is trivial to explain what does it do.
