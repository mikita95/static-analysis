package analyzer.event;

import java.util.Collection;
import java.util.stream.Collectors;

public interface AnalysisEventPrinter {

    String print(final Iterable<AnalysisEvent> events);

    default String print(final Collection<AnalysisEventListener> listeners) {
        return print(
                listeners.stream()
                        .map(AnalysisEventListener::getAnalysisEvents)
                        .flatMap(Collection::stream)
                        .collect(Collectors.toList()));
    }
}
