package kz.edu.se2517.delivery.planning;

import kz.edu.se2517.delivery.domain.Route;
import java.util.List;
import java.util.Map;

public record RouteDecision(Route selectedRoute, String strategyName, String explanation,
                            List<String> reasons, Map<String, String> rejectedRoutes) {
    public RouteDecision {
        if (selectedRoute == null || strategyName == null || explanation == null)
            throw new IllegalArgumentException("Decision fields are required");
        reasons = List.copyOf(reasons);
        rejectedRoutes = Map.copyOf(rejectedRoutes);
    }
}
