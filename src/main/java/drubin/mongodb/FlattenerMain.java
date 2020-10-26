package drubin.mongodb;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.stream.Collectors;

public class FlattenerMain {

    /**
     * Main to print out flattened Json from command line. Input can be passed 2 ways
     * 1) Piped to Stdin.
     * 2) Arguments list of String. In this case, each argument is considered a separate json
     * @param args Jsons to flatten
     * @throws IOException
     */
    public static void main(String[] args) throws IOException {
        JsonFlattener flattener = JsonFlattener.getDefaultFlattener();
        // Consume and print flattened stdin if it exists
        String stdin = consumeStdInAsString();
        if (stdin != null) {
            System.out.println(flattener.flattenAsJsonString(stdin));
        }

        // Flatten all the args if they exist
        for (int i = 0; i < args.length; i++) {
            System.out.println(flattener.flattenAsJsonString(args[i]));
        }
    }

    /**
     * @return Reads all available on stdin and returns the result as a String. Returns null if nothing to read
     * @throws IOException
     */
    public static String consumeStdInAsString() throws IOException {
        if (System.in.available() > 0) {
            try (InputStreamReader is = new InputStreamReader(System.in); BufferedReader br = new BufferedReader(is)) {
                return br.lines().collect(Collectors.joining("\n"));
            }
        }
        return null;
    }
}
