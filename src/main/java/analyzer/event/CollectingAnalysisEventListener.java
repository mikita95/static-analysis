package analyzer.event;

import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Component
public class CollectingAnalysisEventListener implements AnalysisEventListener {

    private final List<AnalysisEvent> events = new ArrayList<>();

    @Override
    public void onApplicationEvent(AnalysisEvent analysisEvent) {
        events.add(analysisEvent);
    }

    @Override
    public Collection<AnalysisEvent> getAnalysisEvents() {
        return events;
    }
}
