package analyzer.event;

import org.springframework.stereotype.Component;

import java.util.Comparator;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Component
public class PrettyAnalysisEventPrinter implements AnalysisEventPrinter {

    @Override
    public String print(final Iterable<AnalysisEvent> events) {
        final var list = StreamSupport.stream(events.spliterator(), false).collect(Collectors.toList());

        final var statistics = list.stream()
                .collect(Collectors.groupingBy(AnalysisEvent::getType, Collectors.counting()))
                .entrySet()
                .stream()
                .map(entry -> String.format("Analyzer detected %s %ss", entry.getValue(), entry.getKey().name()))
                .collect(Collectors.joining("\n"));

        final var messages = list.stream()
                .collect(Collectors.groupingBy(event -> event.getClassName().orElse("Unknown class")))
                .entrySet()
                .stream()
                .map(entry -> String.format(
                        "In class %s analyzer detected %d problems:\n%s",
                        entry.getKey(),
                        entry.getValue().size(),
                        entry.getValue().stream()
                                .sorted(Comparator.comparing(AnalysisEvent::getType))
                                .map(AnalysisEvent::toString)
                                .collect(Collectors.joining("\n"))))
                .collect(Collectors.joining("\n\n"));

        return statistics + "\n\n" + messages;
    }
}
