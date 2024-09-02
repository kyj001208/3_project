package com.project.memmem.domain.dto.group;

import com.project.memmem.domain.entity.Category;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GroupDTO {
	
	private Long id;
    private String groupName;
    private String greeting;
    private String createdAt; // 포맷된 날짜를 저장
    private Category category;
    private Long creatorUserId;
    private String imgUrl;
}
