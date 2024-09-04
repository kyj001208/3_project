package com.project.memmem.service;

public interface BlockService {

	void blockUser(Long blockerId, Long blockedId);

	Object getBlockedUsers(Long userId);

}
