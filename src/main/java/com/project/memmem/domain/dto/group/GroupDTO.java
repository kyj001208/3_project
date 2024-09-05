package com.project.memmem.domain.dto.group;

import com.project.memmem.domain.entity.Category;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GroupDTO {
	
	private Long id;
    private String groupName;
    private String greeting;
    private String createdAt; // 포맷된 날짜를 저장
    private String categoryKoName;
    private Long creatorUserId;
    private String mainImageUrl;
}
