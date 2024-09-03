package com.project.memmem.domain.dto.user;

import com.project.memmem.domain.entity.GroupEntity;
import com.project.memmem.domain.entity.GroupMemberShipEntity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MyGroupListDTO {
    private Long groupId;
    private String groupName;
    private String description;
    private String category;
    private String mainImageUrl;
    private boolean isCreator;
    private String userRole; // "CREATOR" 또는 "MEMBER"
    
    // GroupMemberShipEntity로부터 MyGroupListDTO 생성
    public static MyGroupListDTO fromMembership(GroupMemberShipEntity membership, String baseUrl) {
        GroupEntity group = membership.getGroup();
        return MyGroupListDTO.builder()
                .groupId(group.getId())
                .groupName(group.getGroupName())
                .description(group.getDescription())
                .category(group.getCategory().toString())
                //.mainImageUrl(group.getMainImageUrl(baseUrl))
                .isCreator(false)
                .userRole("MEMBER")
                .build();
    }

    // GroupEntity로부터 MyGroupListDTO 생성 (생성자용)
    public static MyGroupListDTO fromGroup(GroupEntity group, String baseUrl) {
        return MyGroupListDTO.builder()
                .groupId(group.getId())
                .groupName(group.getGroupName())
                .description(group.getDescription())
                .category(group.getCategory().toString())
                //.mainImageUrl(group.getMainImageUrl(baseUrl))
                .isCreator(true)
                .userRole("CREATOR")
                .build();
    }

}
