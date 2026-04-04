package com.yavuzozmen.reconcontrol.ops.adapter.in.web;

import com.yavuzozmen.reconcontrol.ops.application.KnowledgeDocument;

public record KnowledgeDocumentResponse(
    String title,
    String path,
    String excerpt
) {
    public static KnowledgeDocumentResponse fromDomain(KnowledgeDocument document) {
        return new KnowledgeDocumentResponse(
            document.title(),
            document.path(),
            document.excerpt()
        );
    }
}
