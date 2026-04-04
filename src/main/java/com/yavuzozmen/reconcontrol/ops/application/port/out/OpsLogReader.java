package com.yavuzozmen.reconcontrol.ops.application.port.out;

import com.yavuzozmen.reconcontrol.ops.application.OpsLogEntry;
import java.util.List;

public interface OpsLogReader {

    List<OpsLogEntry> findRecentLogs(int lookbackMinutes, int limit);

    List<OpsLogEntry> findByCorrelationId(String correlationId, int limit);
}
