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

LAPL currently has 9 primitive data types as follows:

* `Number` - Describes a real number like 1, 0, 3.14
* `Character` - Describes a character like 'A', '文'
* `Name` - Describe a reference to variables
* `Pointer` - Describe a reference to procedures
* `Handler` - Describe a reference to system resources like file handler
* `Array` - Describe a collection of data
* `Null` - Just null
* `Type` - Describe a type
* `Bool` - Describe a Boolean value

Though LAPL does have types, it's controlled by LAPL runtime and cannot be 
manually assigned. When you creating variables, LAPL would automatically 
determine the type of certain variable.

`Pointer` and `Name` can be implicitly converted to `Number` when expected.

## Primitive Operations

Every operation would return a Data.

### Arithmetic operations

| Operator | Function                 | Parameters     | Returns |
|:-------- |:------------------------ |:-------------- |:------- |
| ln       | Natural logarithm        | Number         | Number  |
| +1       | Increase                 | Number         | Number  |
| -1       | Decrease                 | Number         | Number  |
| +        | Add                      | Number, Number | Number  |
| -        | Subtract                 | Number, Number | Number  |
| *        | Multiply                 | Number, Number | Number  |
| /        | Divide                   | Number, Number | Number  |
| ^        | Power                    | Number, Number | Number  |
| &lt;     | Lesser than              | Number, Number | Bool    |
| &gt;     | Greater than             | Number, Number | Bool    |
| =        | Equal                    | Number, Number | Bool    |
| ==       | Strictly equal[^equ]     | Number, Number | Bool    |
| !=       | Not equal                | Number, Number | Bool    |
| !==      | Strictly not equal       | Number, Number | Bool    |
| &lt;=    | Not greater than         | Number, Number | Bool    |
| &gt;=    | Not lesser than          | Number, Number | Bool    |

### I/O

| Operator       | Function                       | Parameters       | Returns |
|:-------------- |:------------------------------ |:---------------- |:------- |
| input!         | Read a name from stdin         | VarName          | Array   |
| file-delete!   | Delete a file                  | FilePath         | Null    |
| file-eof       | Get the end position of a file | VarName          | Number  |
| file-open      | Opens a file                   | FilePath         | Handler |
| file-new       | Create a file                  | FilePath         | Handler |
| file-seek      | Seek in a file (fseek)         | VarName, Pos     | Null    |
| file-read-all! | Read all the content of a file | VarName, FileVar | Null    |
| print          | Print some names and values    | Names or Numbers | Null    |

### Flow control

| Operator | Function             | Parameters               | Returns    |
|:-------- |:-------------------- |:------------------------ |:---------- |
| while    | \_(:3\| L)\_[^omit]  | ConditionPtr, BodyPtr    | (Body)     |
| if       | \_(:3\| L)\_         | CondPtr, ConsPtr, AltPtr | (Cons/Alt) |

### Variable control

| Operator | Function                  | Parameters               | Returns |
|:-------- |:------------------------- |:------------------------ |:------- |
| drop!    | Erases a global variable  | VarName                  | Null    |
| set?     | Test if a variable is set | VarName                  | Bool    |
| set!     | Sets a global variable    | VarName, Any             | Null    |
| let      | Sets a local variable     | VarName, Number, BodyPtr | Null    |

### Array manipulation

| Operator | Function                                  | Parameters  | Returns |
|:-------- |:----------------------------------------- |:----------- |:------- |
| len      | Get the length of a NodeGroup or an Array | VarName     | Number  |
| read-at  | Get the specific element of an Array      | VarName, Num| Any     |
| append   | Append an element to an Array             | VarName, Any| Null    |
| erase-at | Erases the specific element of an Array   | VarName, Num| Null    |
| write-at | Set the specific element of an Array      | Name,Num,Any| Null    |
| arr      | Creates an Array                          | Any...      | Array   |

### Misc

| Operator | Function          | Parameters      | Returns |
|:-------- |:----------------- |:--------------- |:------- |
| halt     | Halt immediately  | /               | /       |
| ret      | Evaluate the node | Node            | Number  |
| eval     | Evaluate a file   | FilePath        | Null    |
| def      | Define a function | SigPtr, BodyPtr | Null    |

[^equ]: 'Strictly equal' means two numbers are equal in byte level.
Sometimes computers add funny because there is rounding error.
(e.g. 0.1 + 0.2 = 0.30000000000000004, sadly).
Since all arithmetic operations in LAPL would be done with 
doubles, it is necessary to introduce 'equal (=)', which has 
a tolerance of 1e-10.

[^omit]: I thought it is trivial to explain what does it do.
