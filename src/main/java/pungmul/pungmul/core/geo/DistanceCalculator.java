package pungmul.pungmul.core.geo;

import org.geolatte.geom.G2D;
import org.geolatte.geom.Geometries;
import org.geolatte.geom.Point;
import org.geolatte.geom.builder.DSL;
import org.geolatte.geom.crs.CoordinateReferenceSystems;
import pungmul.pungmul.domain.lightning.MapLevelDistance;

public class DistanceCalculator {

    private static final double EARTH_RADIUS = 6371 * 1000; // 지구 반지름 (미터)

    /**
     * 두 지점 간의 거리 계산 (미터 단위)
     * @param latitude1 첫 번째 지점의 위도
     * @param longitude1 첫 번째 지점의 경도
     * @param latitude2 두 번째 지점의 위도
     * @param longitude2 두 번째 지점의 경도
     * @return 두 지점 간의 거리 (미터)
     */
    public static double calculateDistance(double latitude1, double longitude1, double latitude2, double longitude2) {
        double latDistance = Math.toRadians(latitude2 - latitude1);
        double lonDistance = Math.toRadians(longitude2 - longitude1);

        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2) +
                Math.cos(Math.toRadians(latitude1)) * Math.cos(Math.toRadians(latitude2)) *
                        Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return EARTH_RADIUS * c; // 결과는 미터 단위
    }
}
