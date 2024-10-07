package adventure.v5c;

import adventure.v5c.actions.HealAction;
import adventure.v5c.actions.InvestigateAction;
import adventure.v5c.actions.MoveAction;
import adventure.v5c.actions.SkipTurnAction;
import adventure.v5c.strategies.SelectAggressiveActionStrategy;
import adventure.v5c.strategies.SelectFirstActionStrategy;
import adventure.v5c.strategies.SelectHelpfulActionStrategy;
import adventure.v5c.strategies.SelectRandomActionStrategy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.params.provider.Arguments.arguments;

class PlayerV5cTest {
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
        var unit = new Player("Test Player", room1);
        unit.perform(new SkipTurnAction());
        assertEquals(room1, unit.getLocation());
    }

    @Test
    void perform_validMove() {
        var unit = new Player("Test Player", room1);
        unit.perform(new MoveAction("north"));
        assertEquals(room2, unit.getLocation());
    }

    @Test
    void perform_invalidMove() {
        var unit = new Player("Test Player", room1);
        assertThrows(IllegalArgumentException.class, () -> unit.perform(new MoveAction("nowhere")));
    }

    @Test
    void performIfPossible_validMove() {
        var unit = new Player("Test Player", room1);
        unit.performIfPossible(new MoveAction("north"));
        assertEquals(room2, unit.getLocation());
    }

    @Test
    void performIfPossible_invalidMove() {
        var unit = new Player("Test Player", room1);
        unit.performIfPossible(new MoveAction("nowhere"));
        assertEquals(room1, unit.getLocation());
    }

    @ParameterizedTest
    @MethodSource("selectActionValueProvider")
    void selectAction_actionsAvailable(SelectActionStrategy strategy, Class<?> expectedClass) {
        List<Action> actions = List.of(
                new MoveAction("north"), new InvestigateAction(), new HealAction(), new SkipTurnAction());
        var unit = new Player("Test Player", room1);
        unit.setStrategy(strategy);

        Action action = unit.selectAction(actions);

        assertEquals(expectedClass, action.getClass());
    }

    static Stream<Arguments> selectActionValueProvider() {
        return Stream.of( //
                arguments(new SelectFirstActionStrategy(), MoveAction.class),
                arguments(new SelectAggressiveActionStrategy(), InvestigateAction.class),
                arguments(new SelectHelpfulActionStrategy(), HealAction.class));
    }

    @Test
    void selectAction_randomStrategy() {
        List<Action> actions = List.of(
                new MoveAction("north"), new InvestigateAction(), new HealAction(), new SkipTurnAction());
        var unit = new Player("Test Player", room1);
        unit.setStrategy(new SelectRandomActionStrategy());

        Action action = unit.selectAction(actions);

        assertTrue(actions.contains(action));
    }

    @Test
    void selectAction_noActionsAvailable() {
        var unit = new Player("Test Player", room1);
        Action action = unit.selectAction(List.of());
        assertEquals(SkipTurnAction.class, action.getClass());
    }

    @Test
    void takeTurn_singleTurn() {
        var unit = new Player("Test Player", room1);
        unit.takeTurn();
        assertEquals(room2, unit.getLocation());
    }
}
