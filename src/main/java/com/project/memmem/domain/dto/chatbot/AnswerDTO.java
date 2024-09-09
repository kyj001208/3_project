package com.project.memmem.domain.dto.chatbot;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AnswerDTO {
    private String answer;
    private int nnpNo;
    private List<String> options;
    private boolean endScenario;
    private String categoryUrl;

}
