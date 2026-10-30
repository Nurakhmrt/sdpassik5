package kz.edu.se2517.delivery.domain;

public record Route(String id, Location origin, Location destination, double distanceKm,
                    int estimatedMinutes, double baseCost, double costPerKm,
                    double carbonEmissionKg, int trafficDelayMinutes,
                    boolean available, boolean hazardousCargoAllowed) {
    public Route {
        if (id == null || id.isBlank()) throw new IllegalArgumentException("Route id is required");
        if (origin == null || destination == null) throw new IllegalArgumentException("Route endpoints are required");
        if (!Double.isFinite(distanceKm) || distanceKm < 0 || estimatedMinutes < 0 ||
                !Double.isFinite(baseCost) || baseCost < 0 || !Double.isFinite(costPerKm) || costPerKm < 0 ||
                !Double.isFinite(carbonEmissionKg) || carbonEmissionKg < 0 || trafficDelayMinutes < 0)
            throw new IllegalArgumentException("Route metrics cannot be negative or non-finite");
    }
    public double totalCost(DeliveryRequest request) {
        return baseCost + distanceKm * costPerKm + request.priority().surcharge();
    }
    public int effectiveMinutes() { return estimatedMinutes + trafficDelayMinutes; }
}
