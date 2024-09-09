package com.project.memmem.domain.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum Category{
	
	CULTURE(1, "문화", "/images/culture.png"),
    ACTIVITY(2, "액티비티", "/images/activity.png"),
    FOOD(3, "푸드", "/images/food.png"),
    TRAVEL(4, "여행", "/images/travel.png"),
    SELF_DEVELOPMENT(5, "자기계발", "/images/seldev.png"),
    PARTY(6, "파티", "/images/party.png"),
    DATING(7, "연애", "/images/date.png"),
    GAMING(8, "게임", "/images/game.png");               

    private final int number;
    private final String koName;
    private final String imageUrl;
}