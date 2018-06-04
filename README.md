# Static analysis for Java

## Usage

### From source
`mvn package`

### Using jar
`java -jar static-analysis.jar [DIRECTORY_PATH]`

### Jar
Download: [static-analysis.jar](https://github.com/mikita95/static-analysis/releases/download/v1.0/static-analysis.jar)

## Performing checks
1. CatchExceptionIgnore - check if catch block does not rethrow exception or does not log it.
2. ConstantBooleanCondition - check if any subcondition in if, while or do statement has a constant value
3. ConstantNaming - check if static final variable hasn't uppercase naming
4. EqualIfBranches - check if If branch and else branch are the same
5. UnusedMethodArgument - check if some method's argument is unused in its body

## Output example
