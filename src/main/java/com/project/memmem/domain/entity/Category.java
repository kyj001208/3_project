package com.project.memmem.domain.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum Category{
	
    CULTURE(1, "문화"),              
    ACTIVITY(2, "액티비티"),         
    FOOD(3, "푸드"),                  
    TRAVEL(4, "여행"),                
    SELF_DEVELOPMENT(5, "자기계발"),  
    PARTY(6, "파티"),                 
    DATING(7, "연애"),               
    GAMING(8, "게임");                

    private final int number;
    private final String koName;
}