import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Test2 {

    private static final Logger logger = LoggerFactory.getLogger(Test2.class);

    public static void main(String... args) {
        try {
            foo();
        } catch (Exception e) {
            //
        }
    }

    private static void foo() {
        try {
            int a = 1;
        } catch (Exception loggedException) {
            logger.error("some message", loggedException);
        }

        try {
            int b = 1;
        } catch (Exception rethrownException) {
            int c = 1;
            throw rethrownException;
        }
    }
}
