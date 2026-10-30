package kz.edu.se2517.delivery.demo;

import kz.edu.se2517.delivery.domain.*;
import java.util.List;

public final class DemoData {
    private DemoData() {}
    public static final Location HUB = new Location("Almaty Hub", 43.2389, 76.8897);
    public static final Location DESTINATION = new Location("Central Depot", 43.2567, 76.9286);
    public static List<Route> routes() {
        return List.of(
            new Route("R1",HUB,DESTINATION,12,24,700,90,2.4,6,true,true),
            new Route("R2",HUB,DESTINATION,18,20,500,70,3.8,2,true,true),
            new Route("R3",HUB,DESTINATION,10,35,400,110,1.2,3,true,true),
            new Route("R4",HUB,DESTINATION,22,42,350,65,2.0,0,true,true),
            new Route("R5",HUB,DESTINATION,15,31,450,80,1.8,4,true,false),
            new Route("R6",HUB,DESTINATION,9,27,650,95,2.9,1,true,true),
            new Route("R7",HUB,DESTINATION,30,50,300,55,4.5,0,false,true),
            new Route("R8",HUB,DESTINATION,16,29,600,75,1.5,2,true,true));
    }
    public static DeliveryRequest standardRequest() { return new DeliveryRequest("105",DeliveryPriority.STANDARD,5000,60,false,false,10); }
}
