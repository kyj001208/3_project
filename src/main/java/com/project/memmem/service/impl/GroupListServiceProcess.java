package com.project.memmem.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.project.memmem.service.GroupListService;

@Service
public class GroupListServiceProcess implements GroupListService {
	
	@Override
    public List<Map<String, Object>> getGroups(int page, int size) {
        List<Map<String, Object>> groups = new ArrayList<>();

        // 예제 데이터 생성 (데이터베이스 쿼리나 다른 데이터 소스에서 가져오도록 변경 가능)
        for (int i = 0; i < size; i++) {
            int groupId = (page - 1) * size + i + 1; // 그룹 ID 계산
            Map<String, Object> group = new HashMap<>();
            group.put("id", groupId);
            group.put("title", "그룹 " + groupId);
            group.put("location", "위치 " + groupId);
            groups.add(group);
        }

        return groups;
    }

}
