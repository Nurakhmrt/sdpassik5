package kz.edu.se2517.delivery.strategy;

import kz.edu.se2517.delivery.domain.*;
import kz.edu.se2517.delivery.planning.RouteDecision;
import java.util.List;
import java.util.Locale;

public final class EcoFriendlyRouteStrategy extends AbstractRouteStrategy {
    private final double emissionWeight;
    private final double distanceWeight;
    public EcoFriendlyRouteStrategy() { this(0.8, 0.2); }
    public EcoFriendlyRouteStrategy(double emissionWeight, double distanceWeight) {
        if (!Double.isFinite(emissionWeight) || !Double.isFinite(distanceWeight) || emissionWeight < 0 || distanceWeight < 0 || emissionWeight + distanceWeight == 0)
            throw new IllegalArgumentException("Eco weights must be finite, non-negative, and not both zero");
        this.emissionWeight = emissionWeight; this.distanceWeight = distanceWeight;
    }
    private double score(Route r) { return r.carbonEmissionKg() * emissionWeight + r.distanceKm() * distanceWeight; }
    @Override public RouteDecision selectRoute(List<Route> routes, DeliveryRequest request) {
        return decide(routes, request, this::score, r -> String.format(Locale.ROOT,
                "Eco score = %.2f × %.2f kg CO2 + %.2f × %.1f km = %.3f",
                emissionWeight, r.carbonEmissionKg(), distanceWeight, r.distanceKm(), score(r)));
    }
    @Override public String getStrategyName() { return "Eco friendly"; }
}
