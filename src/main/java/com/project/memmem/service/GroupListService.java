package com.project.memmem.service;

import java.util.List;
import java.util.Map;

public interface GroupListService {

	List<Map<String, Object>> getGroups(int page, int size);
}
