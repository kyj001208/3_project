package com.project.memmem.domain.dto.bot;

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

	public AnswerDTO nnpNo(int nnpNo) {
		this.nnpNo = nnpNo;
		return this;
	}

}
