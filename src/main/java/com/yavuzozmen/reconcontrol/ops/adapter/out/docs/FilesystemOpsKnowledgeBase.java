package com.yavuzozmen.reconcontrol.ops.adapter.out.docs;

import com.yavuzozmen.reconcontrol.ops.application.KnowledgeDocument;
import com.yavuzozmen.reconcontrol.ops.application.OpsAssistantProperties;
import com.yavuzozmen.reconcontrol.ops.application.port.out.OpsKnowledgeBase;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.stream.Stream;

public class FilesystemOpsKnowledgeBase implements OpsKnowledgeBase {

    private final OpsAssistantProperties properties;

    public FilesystemOpsKnowledgeBase(OpsAssistantProperties properties) {
        this.properties = Objects.requireNonNull(properties, "properties must not be null");
    }

    @Override
    public List<KnowledgeDocument> search(String question, int limit) {
        Path docsRoot = Path.of(properties.getDocsRoot());
        if (!Files.exists(docsRoot)) {
            return List.of();
        }

        List<String> keywords = tokenize(question);
        List<ScoredDocument> candidates = new ArrayList<>();

        try (Stream<Path> paths = Files.walk(docsRoot)) {
            paths.filter(Files::isRegularFile)
                .filter(path -> path.toString().endsWith(".md"))
                .forEach(path -> score(path, keywords).ifPresent(candidates::add));
        } catch (IOException ignored) {
            return List.of();
        }

        return candidates.stream()
            .sorted(Comparator.comparingInt(ScoredDocument::score).reversed())
            .limit(limit)
            .map(ScoredDocument::document)
            .toList();
    }

    private java.util.Optional<ScoredDocument> score(Path path, List<String> keywords) {
        try {
            String content = Files.readString(path, StandardCharsets.UTF_8);
            String haystack = (path.getFileName() + " " + content).toLowerCase(Locale.ROOT);
            int score = 0;
            for (String keyword : keywords) {
                if (haystack.contains(keyword)) {
                    score++;
                }
            }

            if (score == 0 && !keywords.isEmpty()) {
                return java.util.Optional.empty();
            }

            return java.util.Optional.of(
                new ScoredDocument(
                    new KnowledgeDocument(
                        path.getFileName().toString(),
                        path.toString(),
                        buildExcerpt(content, keywords)
                    ),
                    score == 0 ? 1 : score
                )
            );
        } catch (IOException exception) {
            return java.util.Optional.empty();
        }
    }

    private String buildExcerpt(String content, List<String> keywords) {
        String[] lines = content.split("\\R");
        for (String line : lines) {
            String normalized = line.toLowerCase(Locale.ROOT);
            for (String keyword : keywords) {
                if (normalized.contains(keyword)) {
                    return trim(line);
                }
            }
        }

        return lines.length == 0 ? "" : trim(lines[0]);
    }

    private String trim(String line) {
        String trimmed = line.strip();
        return trimmed.length() <= 180 ? trimmed : trimmed.substring(0, 180) + "...";
    }

    private List<String> tokenize(String question) {
        return java.util.Arrays.stream(question.toLowerCase(Locale.ROOT).split("[^a-z0-9]+"))
            .filter(token -> token.length() >= 3)
            .distinct()
            .toList();
    }

    private record ScoredDocument(KnowledgeDocument document, int score) {}
}
