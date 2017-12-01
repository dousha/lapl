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

## Data Types

LAPL currently has 6 primitive data types as follows:

* `Number` - Describes a real number like 1, 0, 3.14
* `Character` - Describes a character like 'A', '文'
* `Name` - Describe a reference to variables
* `Pointer` - Describe a reference to procedures
* `Handler` - Describe a reference to system resources like file handler
* `Array` - Describe a collection of above types

Though LAPL does have types, it's controlled by LAPL runtime and cannot be 
manually assigned. When you creating variables, LAPL would automatically 
determine the type of certain variable.

`Pointer` and `Name` can be implicitly converted to `Number` when expected.

## Primitive Operations

Every operation would return a Number.

### Arithmetic operations

| Operator | Function                 | Type    | Parameters (if any)      |
|:-------- |:------------------------ | -------:|:------------------------ |
| ln       | Natural logarithm        | Monadic | Number                   |
| +1       | Increase                 | Monadic | Number                   |
| -1       | Decrease                 | Monadic | Number                   |
| +        | Add                      | Dyadic  | Number, Number           |
| -        | Subtract                 | Dyadic  | Number, Number           |
| *        | Multiply                 | Dyadic  | Number, Number           |
| /        | Divide                   | Dyadic  | Number, Number except 0. |
| ^        | Power                    | Dyadic  | Number, Number           |
| &lt;     | Lesser than              | Dyadic  | Number, Number           |
| &gt;     | Greater than             | Dyadic  | Number, Number           |
| =        | Equal                    | Dyadic  | Number, Number           |
| ==       | Strictly equal[^equ]     | Dyadic  | Number, Number           |
| !=       | Not equal                | Dyadic  | Number, Number           |
| !==      | Strictly not equal       | Dyadic  | Number, Number           |
| &lt;=    | Not greater than         | Dyadic  | Number, Number           |
| &gt;=    | Not lesser than          | Dyadic  | Number, Number           |

### I/O

_TODO: This part is a stub of an incomplete feature._

| Operator       | Function                       | Type    | Parameters       |
|:-------------- |:------------------------------ | -------:| ---------------- |
| input!         | Read a name from stdin         | Monadic | VarName          |
| file-open!     | Opens a file                   | Dyadic  | VarName, FilePath|
| file-read-all! | Read all the content of a file | Dyadic  | VarName, FileVar |
| print          | Print some names and values    | Complex | Names or Numbers |
| display        | (Deprecated) Print a value     | Monadic | Number           |

### Flow control

| Operator | Function             | Type    | Parameters               |
|:-------- |:-------------------- | -------:| ------------------------ |
| while    | \_(:3\| L)\_[^omit]  | Dyadic  | ConditionPtr, BodyPtr    |
| if       | \_(:3\| L)\_         | Triadic | CondPtr, ConsPtr, AltPtr |

### Variable control

| Operator | Function                 | Type    | Parameters               |
|:-------- |:------------------------ | -------:| ------------------------ |
| drop!    | Erases a global variable | Monadic | VarName                  |
| set!     | Sets a global variable   | Dyadic  | VarName, Any             |
| let      | Sets a local variable    | Triadic | VarName, Number, BodyPtr |

### Array manipulation

| Operator | Function                                  | Type    | Parameters  |
|:-------- |:----------------------------------------- | -------:| ----------- |
| len      | Get the length of a NodeGroup or an Array | Monadic | VarName     |
| read-at  | Get the specific element of an Array      | Dyadic  | VarName, Num|
| append   | Append an element to an Array             | Dyadic  | VarName, Any|
| erase-at | Erases the specific element of an Array   | Dyadic  | VarName, Num|
| write-at | Set the specific element of an Array      | Triadic | Name,Num,Any|
| arr!     | Creates an Array                          | Complex | Name, Anys  |

### Misc

| Operator | Function          | Type    | Parameters      |
|:-------- |:----------------- | -------:| --------------- |
| halt     | Halt immediately  | Simple  | /               |
| ret      | Evaluate the node | Monadic | Number          |
| eval     | Evaluate a file   | Monadic | FilePath        |
| def      | Define a function | Dyadic  | SigPtr, BodyPtr |

[^equ]: 'Strictly equal' means two numbers are equal in byte level.
Sometimes computers add funny because there is rounding error.
(e.g. 0.1 + 0.2 = 0.30000000000000004, sadly).
Since all arithmetic operations in LAPL would be done with 
doubles, it is necessary to introduce 'equal (=)', which has 
a tolerance of 1e-10.

[^omit]: I thought it is trivial to explain what does it do.
