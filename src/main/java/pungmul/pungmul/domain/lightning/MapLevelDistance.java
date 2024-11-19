package pungmul.pungmul.domain.lightning;

import lombok.Getter;

@Getter
public enum MapLevelDistance {
    LEVEL_1(20),
    LEVEL_2(30),
    LEVEL_3(50),
    LEVEL_4(100),
    LEVEL_5(250),
    LEVEL_6(500),
    LEVEL_7(1000),
    LEVEL_8(2000),
    LEVEL_9(4000);

    private final int distance; // 단위 거리 (미터)

    MapLevelDistance(int distance) {
        this.distance = distance;
    }

    public static int getDistanceByLevel(int level) {
        for (MapLevelDistance mapLevel : values()) {
            if (mapLevel.ordinal() + 1 == level) {
                return mapLevel.getDistance();
            }
        }
        throw new IllegalArgumentException("Invalid map level: " + level);
    }
}