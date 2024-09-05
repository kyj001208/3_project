package com.project.memmem.domain.dto.group;

import java.util.List;

import com.project.memmem.domain.entity.Category;
import com.project.memmem.domain.entity.GroupEntity;
import com.project.memmem.domain.entity.GroupMemberShipEntity;
import com.project.memmem.domain.entity.UserEntity;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GroupSaveDTO {

	private Long id;
	private String groupName;
	private String greeting;
	private String description;
	private Category category;
	private String mainImageUrl;
	
	// 이미지 관련 필드 추가
    private String mainImageBucketKey;
    private String mainImageOrgName;
    private List<String> additionalImageBucketKeys;
    private List<String> additionalImageOrgNames;
    
    public GroupEntity toGroupEntity(UserEntity creator) {
    	return GroupEntity.builder()
    			.id(id)
    			.groupName(groupName)
    			.greeting(greeting)
    			.description(description)
    			.category(category)
    			.creator(creator)
    			.build();
    }
	
	
}
