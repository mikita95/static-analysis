package analyzer.event;

import org.springframework.stereotype.Component;

import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static java.lang.String.format;
import static java.util.Comparator.comparing;
import static java.util.stream.Collectors.*;

@Component
public class PrettyAnalysisEventPrinter implements AnalysisEventPrinter {

    @Override
    public String print(final Iterable<AnalysisEvent> events) {
        final var list = StreamSupport.stream(events.spliterator(), false).collect(Collectors.toList());

        final var statistics = list.stream()
                .collect(groupingBy(AnalysisEvent::getType, counting()))
                .entrySet()
                .stream()
                .map(entry -> format("Analyzer detected %s %ss", entry.getValue(), entry.getKey().name()))
                .collect(joining("\n"));

        final var messages = list.stream()
                .collect(groupingBy(event -> event.getClassName().orElse("Unknown class")))
                .entrySet()
                .stream()
                .map(entry -> format(
                        "In class %s analyzer detected %d problems:\n%s",
                        entry.getKey(),
                        entry.getValue().size(),
                        entry.getValue().stream()
                                .sorted(comparing(AnalysisEvent::getType))
                                .map(AnalysisEvent::toString)
                                .collect(joining("\n"))))
                .collect(joining("\n\n"));

        return statistics + "\n\n" + messages;
    }
}
