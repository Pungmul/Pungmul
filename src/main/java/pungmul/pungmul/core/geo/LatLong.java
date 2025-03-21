package pungmul.pungmul.core.geo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import pungmul.pungmul.dto.Mappable;

import java.util.HashMap;
import java.util.Map;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class LatLong implements Mappable {
    private Double latitude;
    private Double longitude;

    public Map<String, Object> toMap() {
        Map<String, Object> map = new HashMap<>();
        map.put("latitude", latitude);
        map.put("longitude", longitude);
        return map;
    }
}
