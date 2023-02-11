# Contributing

All contributions must adhere to the following guidelines. Any contributions not following will not accepted.

All contributions are to be made via GitHub pull request where it will be reviewed by a FirePowered developer.

## Code Style

All code must use the [Google Java Style Guide](https://google.github.io/styleguide/javaguide.html). It is recommended to configure your IDE to do this automatically (for example, using 4 spaces instead of tab characters). The rest of this guide will not mention anything in there unless it is different.

Exceptions to the above:

- **4.2 Block Indentation**: Use 4 spaces for all indentation (instead of 2)
- **4.8.4.2 Fall-through**: Add a `//$FALL-THROUGH$` to `case`s that fall to the next case without a `break` (instead of `// fall through`). Add this comment in other places where a fall-through happens if necessary (such as falling out of many `if` statements).
- **7.1.3 Block tags**: Add `@since x.y` (replacing `x.y` with the `m.n` version in `pom.xml`) when adding new classes. Add `@author` with your name and email to a new class.
- **7.3 Where Javadoc is used**: Javadoc must be present on all public, protected, and package private classes and class members, with the exception of overridden methods. This includes so-called "self-explanatory members" such as getters and setters. It is optional on private members, but recommended. It is recommended to configure your IDE to give warnings markers on any missing Javadoc comments.

### Miscellaneous Style Guidelines
* Do not name fields with Hungarian notation.
* Loops use `i` as a variable. Nested loops should start at `i` and continue in the alphabet.
* Prefer Stream APIs over loops, and prefer `Collection`s over arrays.

### Javadoc

Besides what is mentioned above, adhere to the following Javadoc guidelines:

- Use `{@link}` when referencing another class or class member. Do not use fully qualified package names. If not otherwise imported, such that a FQN is needed, add the shorter form after, like `{@link com.example.MyClass#myMethod(String, int) MyClass.myMethod(String, int)}` so the FQN doesn't appear on hover or in generated docs. Do not use `{@link}` on subsequent references in the same Javadoc block.
- Use `{@code}` when referencing anything that could appear directly in the code. Examples include `null`, `true` and `false`, and parameter names in method Javadoc comments.
- Use `{@see}` (sometimes in combination with `{@link}`) when the reader should read a different Javadoc to better understand the current one.

## Code Guidelines

It is recommended to consult something like [Effective Java (Joshua Bloch)](https://www.google.com/books/edition/Effective_Java/1cVqswEACAAJ) for guidelines on writing good code, as good code style doesn't mean good code.

This repository adheres to the following principles:

- KISS ("keep it simple, stupid"), Object-oriented programming (OOP)
  - Each class should be designed to do exactly one thing
    - Use interfaces/abstract classes as needed
    - If many methods in a class are deviating from its main purpose, extract them to a new class.
  - Each method in that class should be designed to do exactly one thing
    - Use overloads as needed, being sure to explain in the Javadoc what that overload does differently as mentioned in [Javadoc](#javadoc).
  - Code statements should be simplified so it is immediately obvious what is going on, even if this includes writing more code. For example, instead of writing `if ((!foo && !bar) || baz)`, consider breaking it into multiple `if` statements, or assign complex boolean logic to a local variable.
    - Boolean logic can often be simplified by using DeMorgan's law, which states `!(a && b) = !a || !b` and `!(a || b) = !a && !b`. Choose whichever form makes the most sense in English if it were to be read out loud.
- DRY ("don't repeat yourself")
  - Code should never be written more than once. Extract it to a method instead.
- Installing an IDE extension such as [SpotBugs](https://spotbugs.github.io/) is recommended.

## Testing

All contributions must contain a (passing) unit test to demonstrate the feature or fix, with the exception that the fix or feature is already covered by an existing test.

Tests must be placed in `src/test/java` under the corresponding package name and be the same as the class name with "Test" appended. For example, if you make a class `Foo` in the package `org.bar`, the test class's fully-qualified-name is `org.bar.FooTest`.

Test resources (configuration files, test data, etc.) should be placed in `src/test/resources`.

Each test class should only test a single implementation class. Each test method should only test a single method of the unit under test (UUT).

As a result of all tests, each UUT should show at least 80% code coverage. Any missing coverages should be noted.
