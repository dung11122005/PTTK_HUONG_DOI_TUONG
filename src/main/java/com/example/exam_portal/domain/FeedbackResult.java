package com.example.exam_portal.domain;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FeedbackResult {
    private String standardTranslation;
    private String errorAnalysis;
    private String score;
    private String suggestedFix;
    private String rawResponse;
    private String errorAnalysisHtml;
    private String suggestedFixHtml;
}
