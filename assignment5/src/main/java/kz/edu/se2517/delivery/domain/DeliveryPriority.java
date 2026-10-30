package kz.edu.se2517.delivery.domain;

public enum DeliveryPriority {
    URGENT(1500), STANDARD(0), ECONOMY(-200);
    private final double surcharge;
    DeliveryPriority(double surcharge) { this.surcharge = surcharge; }
    public double surcharge() { return surcharge; }
}
