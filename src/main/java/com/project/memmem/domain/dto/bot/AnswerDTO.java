package com.project.memmem.domain.dto.bot;

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

    public AnswerDTO nnpNo(int nnpNo) {
        this.nnpNo = nnpNo;
        return this;
    }

    public void setEndScenario(boolean endScenario) {
        this.endScenario = endScenario;
    }

    public void setCategoryUrl(String categoryUrl) {
        this.categoryUrl = categoryUrl;
    }
}
