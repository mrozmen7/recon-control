package com.yavuzozmen.reconcontrol.ops.application.port.out;

import com.yavuzozmen.reconcontrol.ops.application.OpsMetric;
import java.util.List;

public interface OpsMetricsReader {

    List<OpsMetric> readOperationalSnapshot();
}
