package com.project.memmem.domain.dto.chatbot;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class QuestionDTO {
	private Long key;
    private String content;
    private boolean inScenario;
    
    @Builder.Default
    private int weatherStep = 0;
    
    @Builder.Default
    private String selectedLocation = "";
    
}
