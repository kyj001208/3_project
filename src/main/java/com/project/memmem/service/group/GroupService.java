package com.project.memmem.service.group;

import java.io.IOException;
import java.util.Map;

import org.springframework.web.multipart.MultipartFile;

import com.project.memmem.domain.dto.group.GroupSaveDTO;

public interface GroupService {

	void groupSaveProcess(GroupSaveDTO dto);

	Map<String, String> s3TempUpload(MultipartFile file) throws IOException;

}
