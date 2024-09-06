package com.project.memmem.domain.dto.group;

import com.project.memmem.domain.entity.GroupEntity;
import com.project.memmem.domain.entity.NoticeEntity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NoticeSaveDTO {

	private String notice;

	public NoticeEntity  toEntity() {
		return NoticeEntity .builder()
				.notice(notice)
				.build();
	}

	public NoticeEntity toEntity(GroupEntity group) {
		return NoticeEntity .builder()
				.notice(notice)
				.build();
	}
}
