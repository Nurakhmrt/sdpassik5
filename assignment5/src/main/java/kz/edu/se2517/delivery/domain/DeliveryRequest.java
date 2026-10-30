package kz.edu.se2517.delivery.domain;

public record DeliveryRequest(String id, DeliveryPriority priority, double maxBudget,
                              int maxDeliveryMinutes, boolean ecoPreferred,
                              boolean hazardousCargo, double maxEmissionKg) {
    public DeliveryRequest {
        if (id == null || id.isBlank()) throw new IllegalArgumentException("Request id is required");
        if (priority == null) throw new IllegalArgumentException("Priority is required");
        if (!Double.isFinite(maxBudget) || maxBudget < 0 || maxDeliveryMinutes < 0 ||
                !Double.isFinite(maxEmissionKg) || maxEmissionKg < 0)
            throw new IllegalArgumentException("Request limits must be non-negative and finite");
    }
}
