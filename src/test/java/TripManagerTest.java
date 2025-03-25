import trip.TripManager;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

class TripManagerTest {
    private TripManager tripManager;

    @BeforeEach
    void setUp() {
        tripManager = new TripManager();
    }

    @Test
    void testAddTrip() {
        assertDoesNotThrow(() -> {
            tripManager.addTrip("Japan Trip", "Skiing in Hokkaido", "Japan");
        });
        tripManager.viewTrips(); // Check console output
    }

    @Test
    void testDeleteTrip() {
        assertDoesNotThrow(() -> {
            tripManager.addTrip("Japan Trip", "Skiing in Hokkaido", "Japan");
        });
        tripManager.deleteTrip(1);
        tripManager.viewTrips(); // Should show no trips
    }

    @Test
    void testSelectTrip() {
        assertDoesNotThrow(() -> {
            tripManager.addTrip("Japan Trip", "Skiing in Hokkaido", "Japan");
        });
        assertDoesNotThrow(() -> {
            tripManager.selectTrip(0);
        });
    }
}
