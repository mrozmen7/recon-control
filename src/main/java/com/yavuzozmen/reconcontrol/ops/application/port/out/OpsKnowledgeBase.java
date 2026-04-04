package com.yavuzozmen.reconcontrol.ops.application.port.out;

import com.yavuzozmen.reconcontrol.ops.application.KnowledgeDocument;
import java.util.List;

public interface OpsKnowledgeBase {

    List<KnowledgeDocument> search(String question, int limit);
}
