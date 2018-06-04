package analyzer;

import analyzer.event.AnalysisEventListener;
import analyzer.event.AnalysisEventPrinter;
import analyzer.walker.AnalysisWalker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.Banner;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ApplicationContext;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Collection;

@SpringBootApplication
public class Analyser implements CommandLineRunner {

    @Autowired
    ApplicationContext context;

    @Autowired
    AnalysisWalker walker;

    @Autowired
    AnalysisEventPrinter printer;

    @Autowired
    Collection<AnalysisEventListener> listeners;

    public static void main(String... args) {
        System.err.close();
        System.setErr(System.out);
        new SpringApplicationBuilder()
                .sources(Analyser.class)
                .bannerMode(Banner.Mode.OFF)
                .run(args);
    }

    @Override
    public void run(String... args) throws Exception {

        if (args.length != 1) {
            System.out.println("Usage: java -jar analyser.jar [directory_path]");
            return;
        }

        final String path = args[0];

        try {
            Files.walkFileTree(Paths.get(path), walker);
        } catch (Exception e) {
            System.out.println("Error during analysing:\n" + e.getMessage());
            return;
        }

        System.out.println(printer.print(listeners));
    }


}
