package analyzer.event;

import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class AnalysisEventListener implements ApplicationListener<AnalysisEvent> {

    private final List<AnalysisEvent> events = new ArrayList<>();

    @Override
    public void onApplicationEvent(AnalysisEvent analysisEvent) {
        events.add(analysisEvent);
    }

    public List<AnalysisEvent> getAnalysisEvents() {
        return events;
    }
}
