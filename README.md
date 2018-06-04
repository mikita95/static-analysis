# Static analysis for Java

## Usage

### From source
`mvn package`

### Using jar
`java -jar static-analysis-1.0.jar [DIRECTORY_PATH]`

### Jar
Download: [static-analysis-1.0.jar](https://github.com/mikita95/static-analysis/releases/download/v1.0/static-analysis-1.0.jar)

## Performing checks
1. CatchExceptionIgnore - check if catch block does not rethrow exception or does not log it.
2. ConstantBooleanCondition - check if any subcondition in if, while or do statement has a constant value
3. ConstantNaming - check if static final variable hasn't uppercase naming
4. EqualIfBranches - check if If branch and else branch are the same
5. UnusedMethodArgument - check if some method's argument is unused in its body

## Output example
```java -jar target/static-analysis-1.0.jar src/test/java```
```
Analyzer detected 2 INFOs
Analyzer detected 7 WARNINGs

In class Test1 analyzer detected 6 problems:
[WARNING] Class Test1 at position (line 24,col 21)-(line 24,col 25): condition is always false
[WARNING] Class Test1 at position (line 15,col 5)-(line 17,col 5): argument 'x' in method 'method2' is unused.
[WARNING] Class Test1 at position (line 8,col 9)-(line 12,col 9): 'if' statement has an equal 'then' and 'else' branches.
[WARNING] Class Test1 at position (line 24,col 13)-(line 24,col 16): condition is always true
[WARNING] Class Test1 at position (line 8,col 19)-(line 8,col 19): condition 'f' is always false
[INFO] Class Test1 at position (line 3,col 33)-(line 3,col 44): constant field 'lowCase' should use uppercase letters

In class Test2 analyzer detected 3 problems:
[WARNING] Class Test2 at position (line 11,col 18)-(line 11,col 28): ignoring exception 'e'
[WARNING] Class Test2 at position (line 32,col 16)-(line 32,col 19): condition is always true
[INFO] Class Test2 at position (line 6,col 33)-(line 6,col 77): constant field 'logger' should use uppercase letters
```
