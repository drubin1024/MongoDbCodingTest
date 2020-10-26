Hi, and welcome to my solution.

I tried to include many comments and discuss various design tradeoffs. I do comment my code
a lot typically, but I will say that these are especially verbose since I wanted to give
you some insight into my thought processes.


## Running the code
I included a copy of the compiled code along with all of its dependencies in a jar (eg: a fat jar).
You can run the code using jar, but you need to include it in the classpath first using the -cp option.
I also separately included all the dependencies in the lib/ folder. These are not actually used and are mainly for display purposes.
You can run the code in 2 modes:
1) Pipe in input using stdin:
`cat test.txt | java -cp build/libs/MongoDB-all-1.0-fat.jar drubin.mongodb.FlattenerMain`
2) Pass command line arguments
`java -cp build/libs/MongoDB-all-1.0-fat.jar drubin.mongodb.FlattenerMain "{\"a\"={\"b\"=1}}"`

## Compiling the fatjar
If for some reason you want to re-compile the fat jar you can do so from this directory using
`./gradlew clean fatjar`

## Running Tests
You can run tests via
`./gradlew clean test`


Thanks!
-David
