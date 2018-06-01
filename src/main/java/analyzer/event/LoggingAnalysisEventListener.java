package analyzer.event;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Collections;

@Component
public class LoggingAnalysisEventListener implements AnalysisEventListener {

    private final Logger logger = LoggerFactory.getLogger(LoggingAnalysisEventListener.class);

    @Override
    public void onApplicationEvent(AnalysisEvent analysisEvent) {
        logger.info("Event detected: {}", analysisEvent.toString());
    }

    @Override
    public Collection<AnalysisEvent> getAnalysisEvents() {
        return Collections.emptyList();
    }
}
