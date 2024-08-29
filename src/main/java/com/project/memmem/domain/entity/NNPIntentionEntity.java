package com.project.memmem.domain.entity;

import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "nnp_intention")
public class NNPIntentionEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "nnp_no")
	private int nnpNo;

	@Column(nullable = false)
	private String nnpIntent; // 의도의 이름

	@OneToMany(mappedBy = "nnpIntention")
	private List<KeywordEntity> keywords; // 키워드 리스트

}
