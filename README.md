# BST Android

An Android application for working with and visualizing a **Binary Search Tree (BST)**. The project demonstrates implementation of a generic binary search tree with support for multiple data types, indexed access, deletion, balancing, serialization, and graphical visualization.

## Features

* Binary Search Tree implementation from scratch
* Support for multiple data types:

  * `Integer`
  * `Date`
  * `Point2D`
* Insert elements into the tree
* Get an element by its in-order index
* Remove an element by its in-order index
* Balance the binary search tree
* Display the tree graphically
* Display tree elements as an ordered list
* Save tree data to a local file
* Load tree data from a local file
* Preserve tree state during Android configuration changes

## How It Works

The application stores data in a Binary Search Tree.

Each tree node contains:

* the stored value;
* references to the left and right children;
* a reference to its parent;
* the size of its subtree.

The subtree size allows the application to efficiently locate an element by its position in the sorted, in-order traversal of the tree.

For example, for the following tree:

```text
        10
       /  \
      5    20
     / \
    2   7
```

the in-order traversal is:

```text
2, 5, 7, 10, 20
```

Therefore:

```text
get(0) → 2
get(1) → 5
get(2) → 7
get(3) → 10
get(4) → 20
```

## Supported Data Types

### Integer

Values are parsed using Java's `Integer.parseInt()` and compared numerically.

Example:

```text
42
```

### Date

Dates are represented using `java.time.LocalDate`.

Input format:

```text
YYYY-MM-DD
```

Example:

```text
2026-09-26
```

### Point2D

A two-dimensional point is represented by an `x` and `y` coordinate.

The following formats are supported:

```text
3,4
```

or:

```text
3 4
```

Points are ordered by their Euclidean distance from the origin `(0,0)`.

For example:

```text
(3,4)
```

has a distance of `5` from the origin.

## User Interface

The main screen contains:

1. **Data type selector** — selects the type of values stored in the tree.
2. **Value field** — accepts a new value.
3. **Index field** — specifies an element index for `Get` and `Remove`.
4. **Add** — inserts a new value.
5. **Remove** — removes an element by its in-order index.
6. **Get** — retrieves an element by its in-order index.
7. **Balance** — rebuilds the tree into a more balanced structure.
8. **Save** — saves the current values to local storage.
9. **Load** — loads values from local storage.
10. **Tree visualization** — displays the BST graphically.
11. **List** — displays the values in sorted in-order order.

## Project Structure

```text
BST_Android/
├── app/
│   └── src/
│       ├── main/
│       │   ├── java/com/example/kitpo_l1/
│       │   │   ├── BinaryTree.java
│       │   │   ├── BinaryTreeView.java
│       │   │   ├── Comparator.java
│       │   │   ├── DateType.java
│       │   │   ├── DoWith.java
│       │   │   ├── IntType.java
│       │   │   ├── MainActivity.java
│       │   │   ├── Node.java
│       │   │   ├── Point2D.java
│       │   │   ├── PointType.java
│       │   │   ├── TestIt.java
│       │   │   ├── TreeViewModel.java
│       │   │   ├── UserFactory.java
│       │   │   └── UserType.java
│       │   │
│       │   ├── res/
│       │   │   └── layout/
│       │   │       └── activity_main.xml
│       │   │
│       │   └── AndroidManifest.xml
│       │
│       ├── test/
│       └── androidTest/
│
├── gradle/
├── build.gradle.kts
├── settings.gradle.kts
├── gradle.properties
└── gradlew
```

## Core Classes

### `BinaryTree`

Contains the main BST implementation.

Provides operations including:

```java
add(Object value)
insert(Object value)
get(int index)
remove(int index)
balance()
size()
toList()
firstThat(TestIt test)
saveTo(Writer writer)
loadFrom(BufferedReader reader, UserType type)
```

The tree also implements `Iterable<Object>` and provides an in-order iterator.

### `Node`

Represents an individual tree node.

Each node stores:

```text
value
left
right
parent
size
```

The `size` field contains the number of nodes in the corresponding subtree.

### `BinaryTreeView`

Custom Android `View` responsible for drawing the tree on a `Canvas`.

It recursively draws:

* tree edges;
* nodes;
* node values.

The visualization automatically scales the tree vertically when necessary.

### `UserType`

Interface defining how a supported data type interacts with the BST.

It provides methods for:

```java
typeName()
create()
clone(Object obj)
parseValue(String s)
getTypeComparator()
```

This allows different object types to use the same `BinaryTree` implementation.

### `UserFactory`

Registers the available data types and provides the appropriate `UserType` implementation based on the selected type.

Currently registered types are:

```text
Point2D
Date
Integer
```

### `TreeViewModel`

Stores the current tree and selected data type.

Using Android's `ViewModel` allows the tree state to survive configuration changes such as screen rotation.

### `MainActivity`

Connects the Android UI with the BST implementation.

It handles:

* user input;
* type selection;
* insertion;
* deletion;
* indexed retrieval;
* balancing;
* saving;
* loading;
* refreshing the list;
* updating the graphical tree.

## Balancing

The `balance()` operation first obtains the values using an in-order traversal.

The resulting sorted list is then recursively converted into a balanced tree by selecting the middle element as the root and recursively constructing the left and right subtrees.

Conceptually:

```text
Sorted list:

1 2 3 4 5 6 7

        4
      /   \
     2     6
    / \   / \
   1   3 5   7
```

This reduces the height of a highly unbalanced BST.

## Saving and Loading

The application stores tree values in the application's internal storage using:

```text
tree_data.txt
```

Values are written one per line.

For example:

```text
1
5
7
10
20
```

When loading, the currently selected `UserType` is used to parse the stored values.

Because the file contains the values but not an explicit type identifier, the appropriate data type should be selected before loading.

## Technologies

* **Java**
* **Android SDK**
* **AndroidX**
* **Gradle**
* **Kotlin DSL**
* **Jetpack ViewModel**
* **XML layouts**
* **Android Canvas API**
* **JUnit**
* **Espresso**

The project targets Android SDK 36 and has a minimum SDK level of 24.

Java/Kotlin compilation is configured for JVM 11.

## Building the Project

Clone the repository:

```bash
git clone https://github.com/Chupacabra0000/BST_Visualizer_Android_app.git
```

Open the project in **Android Studio**.

Allow Gradle to synchronize the project and then build the application.

Alternatively, on systems with the Gradle wrapper available:

### Linux/macOS

```bash
./gradlew build
```

### Windows

```bash
gradlew.bat build
```

To install the debug application on a connected Android device or emulator:

```bash
./gradlew installDebug
```

## Example Workflow

1. Launch the application.
2. Select a data type such as `Integer`.
3. Enter a value, for example:

```text
50
```

4. Press **Add**.
5. Add several more values:

```text
30
70
20
40
60
80
```

6. The application displays the resulting BST.
7. Use **Get** with an index to retrieve an element from the sorted order.
8. Use **Remove** to delete an element by index.
9. Press **Balance** to rebuild the tree into a more balanced structure.
10. Press **Save** to store the values locally.
11. Press **Load** to restore the saved values.

