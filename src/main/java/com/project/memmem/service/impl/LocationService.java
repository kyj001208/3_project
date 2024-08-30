package com.project.memmem.service.impl;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.project.memmem.domain.entity.LocationCoordinate;

@Service
public class LocationService {
    private final Map<String, LocationCoordinate> locationCoordinates;
    private final Map<String, String> locationAliases;

    public LocationService() {
        locationCoordinates = new HashMap<>();
        locationAliases = new HashMap<>();

        // 주요 도시의 좌표 설정
        locationCoordinates.put("서울", new LocationCoordinate(60, 127));
        locationCoordinates.put("부산", new LocationCoordinate(98, 76));
        locationCoordinates.put("인천", new LocationCoordinate(55, 124));

        // 별칭 설정
        locationAliases.put("서울특별시", "서울");
        locationAliases.put("부산광역시", "부산");
        locationAliases.put("인천광역시", "인천");
    }

    public boolean isValidLocation(String location) {
        return locationCoordinates.containsKey(normalizeLocation(location));
    }

    public LocationCoordinate getCoordinateForLocation(String location) {
        return locationCoordinates.getOrDefault(normalizeLocation(location), new LocationCoordinate(60, 127));
    }

    public String normalizeLocation(String location) {
        return locationAliases.getOrDefault(location, location);
    }

    public Optional<String> findMatchingLocation(String input) {
        String normalizedInput = input.toLowerCase();
        return locationCoordinates.keySet().stream()
                .filter(loc -> loc.toLowerCase().contains(normalizedInput) || normalizedInput.contains(loc.toLowerCase()))
                .findFirst();
    }
}