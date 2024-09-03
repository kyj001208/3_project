package com.project.memmem.service.group;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.springframework.ui.Model;
import org.springframework.web.multipart.MultipartFile;

import com.project.memmem.domain.dto.group.GroupListDTO;
import com.project.memmem.domain.dto.group.GroupSaveDTO;
import com.project.memmem.domain.entity.GroupEntity;

public interface GroupService {

	void groupSaveProcess(long userId, GroupSaveDTO dto);

	Map<String, String> s3TempUpload(MultipartFile file) throws IOException;

	List<GroupListDTO> getGroupsByGroupId(Long groupId);

	void joinGroup(long userId, Long groupId);

	boolean isUserMemberOfGroup(long userId, Long groupId);

	boolean isUserCreatorOfGroup(long userId, Long groupId);

	void updateGroup(Long id, GroupSaveDTO groupSaveDTO);

	GroupEntity findGroupById(Long id);

}
