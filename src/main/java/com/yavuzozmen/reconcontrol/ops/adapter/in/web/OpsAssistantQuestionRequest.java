package com.yavuzozmen.reconcontrol.ops.adapter.in.web;

import jakarta.validation.constraints.NotBlank;

public record OpsAssistantQuestionRequest(
    @NotBlank(message = "question must not be blank") String question,
    Integer lookbackMinutes
) {}
