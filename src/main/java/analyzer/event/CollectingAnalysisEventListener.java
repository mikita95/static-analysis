package analyzer.event;

import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

@Component
public class CollectingAnalysisEventListener implements AnalysisEventListener {

    private final Set<AnalysisEvent> events = new HashSet<>();

    @Override
    public void onApplicationEvent(AnalysisEvent analysisEvent) {
        events.add(analysisEvent);
    }

    @Override
    public Collection<AnalysisEvent> getAnalysisEvents() {
        return events;
    }
}
