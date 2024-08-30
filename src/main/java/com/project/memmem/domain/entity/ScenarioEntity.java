package com.project.memmem.domain.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "scenario")
public class ScenarioEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, length = 255)
    private String content;

    @Column(nullable = false)
    private Integer dept;

    @Column(length = 50)
    private String category;

    @ManyToOne
    @JoinColumn(name = "parent_id")
    private ScenarioEntity parent;

    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL)
    private List<ScenarioEntity> children = new ArrayList<>();
}
