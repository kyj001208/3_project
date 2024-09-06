package com.project.memmem.domain.dto.group;

import java.util.List;

import com.project.memmem.domain.entity.Category;
import com.project.memmem.domain.entity.UserEntity;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class GroupListDTO {
	private Long id;
	private String groupName;
	private String greeting;
	private String description;
	private Category category;
	private String creatorNickname;
	private List<String> memberNicknames;
	private String mainImageUrl;
	
}
