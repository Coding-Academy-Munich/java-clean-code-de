package adventure.v4b;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class PawnV4bTest {
    @SuppressWarnings("FieldCanBeLocal")
    private List<Map<String, Object>> locationData;
    private Location room1;
    private Location room2;

    @BeforeEach
    void setUp() {
        locationData = List.of(
                Map.of("name", "Room 1", "description", "This is a room", "connections", Map.of("north", "Room 2")),
                Map.of("name", "Room 2", "description", "This is another room", "connections",
                        Map.of("south", "Room 1")));
        World world = WorldFactory.fromLocationData(locationData);
        room1 = world.getLocationByName("Room 1");
        room2 = world.getLocationByName("Room 2");
    }

    @Test
    void perform_skipTurn() {
        var unit = new Pawn("Test Pawn", room1);
        unit.perform(Action.SKIP_TURN, null);
        assertEquals(room1, unit.getLocation());
    }

    @Test
    void perform_validMove() {
        var unit = new Pawn("Test Pawn", room1);
        unit.perform(Action.MOVE, "north");
        assertEquals(room2, unit.getLocation());
    }

    @Test
    void perform_invalidMove() {
        var unit = new Pawn("Test Pawn", room1);
        assertThrows(IllegalArgumentException.class, () -> unit.perform(Action.MOVE, "east"));
    }

    @Test
    void performIfPossible_validMove() {
        var unit = new Pawn("Test Pawn", room1);
        unit.performIfPossible(Action.MOVE, "north");
        assertEquals(room2, unit.getLocation());
    }

    @Test
    void performIfPossible_invalidMove() {
        var unit = new Pawn("Test Pawn", room1);
        unit.performIfPossible(Action.MOVE, "east");
        assertEquals(room1, unit.getLocation());
    }
}
