package edu.uob;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.io.File;
import java.nio.file.Paths;
import java.io.IOException;
import java.util.Locale;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

// PLEASE READ:
// The tests in this file will fail by default for a template skeleton, your job is to pass them
// and maybe write some more, read up on how to write tests at
// https://junit.org/junit5/docs/current/user-guide/#writing-tests
final class ExtendedTests {

    private GameServer server;

    // Make a new server for every @Test (i.e. this method runs before every @Test test case)
    @BeforeEach
    void setup() {
        File entitiesFile = Paths.get("config/extended-entities.dot").toAbsolutePath().toFile();
        File actionsFile = Paths.get("config/extended-actions.xml").toAbsolutePath().toFile();
        server = new GameServer(entitiesFile, actionsFile);
    }

    // Test to spawn a new server and send a simple "look" command
    @Test
    void testFlexibleCommandAction() {
        String response1 = server.handleCommand("player k: look").toLowerCase();
        System.out.println(response1);
        assertTrue(response1.contains("cabin"), "Did not see description of room in response to look");
        assertTrue(response1.contains("magic potion"), "Did not see description of artifacts in response to look");
        assertTrue(response1.contains("razor sharp axe"), "Did not see description of artifacts in response to look");
        assertTrue(response1.contains("silver coin"), "Did not see description of artifacts in response to look");
        assertTrue(response1.contains("wooden trapdoor"), "Did not see description of furniture in response to look");

        String response2 = server.handleCommand("player k: get axe").toLowerCase();
        System.out.println(response2);
        assertTrue(response2.contains("picked up"), "Did not see 'picked up'");

        String response3 = server.handleCommand("player k: goto forest").toLowerCase();
        System.out.println(response3);
        assertTrue(response3.contains("deep dark forest"), "Did not see description of room in response to look");
        assertTrue(response3.contains("rusty old key"), "Did not see description of artifacts in response to look");
        assertTrue(response3.contains("tall pine tree"), "Did not see description of furniture in response to look");

        String response4 = server.handleCommand("player k: cutdown cut tree").toLowerCase();
        System.out.println(response4);
        assertTrue(response4.contains("valid"), "Did not see 'valid'");
        assertTrue(response4.contains("you cut down the tree with the axe"), "Did not see 'you cut down the tree with the axe'");

        String response5 = server.handleCommand("player k: chop down the tree using the axe").toLowerCase();
        System.out.println(response5);
        assertTrue(response5.contains("error"), "Did not see 'error'");

    }

    @Test
    void testProduceLumberjack() {
        String response1 = server.handleCommand("player k: goto forest").toLowerCase();
        System.out.println(response1);

        String response2 = server.handleCommand("player k: goto riverbank").toLowerCase();
        System.out.println(response2);
        assertTrue(response2.contains("fast flowing river"), "Did not see description of room in response to look");
        assertTrue(response2.contains("old brass horn"), "Did not see description of artifacts in response to look");

        String response3 = server.handleCommand("player k: blow horn").toLowerCase();
        System.out.println(response3);
        assertTrue(response3.contains("you blow the horn and as if by magic, a lumberjack appears !"),
                "Did not see 'lumberjack'");

        String response4 = server.handleCommand("player k:look").toLowerCase();
        System.out.println(response4);
        assertTrue(response4.contains("lumberjack"),
                "Did not see 'lumberjack'");

    }

    @Test
    void testProduceLogClearingHoleGold() {
        String response1 = server.handleCommand("player k: goto forest").toLowerCase();
        System.out.println(response1);

        String response2 = server.handleCommand("player k: chop tree").toLowerCase();
        System.out.println(response2);

        String response3 = server.handleCommand("player k: goto cabin").toLowerCase();
        System.out.println(response3);
        assertTrue(response3.contains("cabin"), "Did not see description of room in response to look");
        assertTrue(response3.contains("magic potion"), "Did not see description of artifacts in response to look");
        assertTrue(response3.contains("razor sharp axe"), "Did not see description of artifacts in response to look");
        assertTrue(response3.contains("silver coin"), "Did not see description of artifacts in response to look");
        assertTrue(response3.contains("wooden trapdoor"), "Did not see description of furniture in response to look");

        String response4 = server.handleCommand("player k: get axe coin").toLowerCase();
        System.out.println(response4);
        assertTrue(response4.contains("your basic command is too long"), "Did not see 'error'");

        String response5 = server.handleCommand("player k: get axe").toLowerCase();
        System.out.println(response5);
        assertTrue(response5.contains("picked up"), "Did not see 'picked up'");

        String response6 = server.handleCommand("player k: get coin").toLowerCase();
        System.out.println(response6);
        assertTrue(response6.contains("picked up"), "Did not see 'picked up'");

        String response7 = server.handleCommand("player k: goto forest").toLowerCase();
        System.out.println(response7);
        assertTrue(response7.contains("deep dark forest"), "Did not see description of room in response to look");
        assertTrue(response7.contains("rusty old key"), "Did not see description of artifacts in response to look");
        assertTrue(response7.contains("tall pine tree"), "Did not see description of furniture in response to look");

        String response8 = server.handleCommand("player k: cutdown axe").toLowerCase();
        System.out.println(response8);
        assertTrue(response8.contains("you cut down the tree with the axe"),
                "Did not see 'you cut down the tree with the axe'");

        String response9 = server.handleCommand("player k: look").toLowerCase();
        System.out.println(response9);
        assertTrue(response9.contains("heavy wooden log"), "Did not see 'log'");
        assertFalse(response9.contains("tree"), "See 'tree'");

        String response10 = server.handleCommand("player k: get log").toLowerCase();
        System.out.println(response10);
        assertTrue(response10.contains("picked up"), "Did not see 'picked up'");

        String response11 = server.handleCommand("player k: bridge log").toLowerCase();
        System.out.println(response11);
        assertTrue(response11.contains("error"), "Did not see 'error'");

        String response12 = server.handleCommand("player k: goto riverbank").toLowerCase();
        System.out.println(response12);
        assertTrue(response12.contains("fast flowing river"), "Did not see description of room in response to look");
        assertTrue(response12.contains("old brass horn"), "Did not see description of artifacts in response to look");

        String response13 = server.handleCommand("player k: goto clearing").toLowerCase();
        System.out.println(response13);
        assertTrue(response13.contains("the entity(new location) cannot be found"),
                "Did not see 'the entity(new location) cannot be found'");

        String response14 = server.handleCommand("player k: bridge river").toLowerCase();
        System.out.println(response14);
        assertTrue(response14.contains("you bridge the river with the log and can now reach the other side"),
                "Did not see 'you bridge the river with the log and can now reach the other side'");

        String response15 = server.handleCommand("player k: goto clearing").toLowerCase();
        System.out.println(response15);
        assertTrue(response15.contains("ground"),
                "Did not see 'ground'");
        assertTrue(response15.contains("it looks like the soil has been recently disturbed"),
                "Did not see 'Did not see description of room in response to look'");

        String response16 = server.handleCommand("player k: goto riverbank").toLowerCase();
        System.out.println(response16);
        assertTrue(response16.contains("fast flowing river"), "Did not see description of room in response to look");
        assertTrue(response16.contains("old brass horn"), "Did not see description of artifacts in response to look");
        assertTrue(response16.contains("clearing"), "Did not see 'clearing' to access");

        String response17 = server.handleCommand("player k: goto forest").toLowerCase();
        System.out.println(response17);

        String response18 = server.handleCommand("player k: pick up key").toLowerCase();
        System.out.println(response18);
        assertTrue(response18.contains("your action cannot be found"),
                "Did not see 'your action cannot be found'");


        String response19 = server.handleCommand("player k: get key").toLowerCase();
        System.out.println(response19);
        assertTrue(response19.contains("picked up"), "Did not see 'picked up'");

        String response20 = server.handleCommand("player k: goto cabin").toLowerCase();
        System.out.println(response20);

        String response21 = server.handleCommand("player k: open key").toLowerCase();
        System.out.println(response21);
        assertTrue(response21.contains("you unlock the door and see steps leading down into a cellar"),
                "Did not see 'you unlock the door and see steps leading down into a cellar'");

        String response22 = server.handleCommand("player k: goto cellar").toLowerCase();
        System.out.println(response22);
        assertTrue(response22.contains("an angry looking elf"),
                "Did not see 'an angry looking elf'");

        String response23 = server.handleCommand("player k: pay elf").toLowerCase();
        System.out.println(response23);
        assertTrue(response23.contains("you pay the elf your silver coin and he produces a shovel"),
                "Did not see 'you pay the elf your silver coin and he produces a shovel'");

        String response24 = server.handleCommand("player k: get shovel").toLowerCase();
        System.out.println(response24);
        assertTrue(response24.contains("picked up"),
                "Did not see 'picked up'");

        String response25 = server.handleCommand("player k: goto cabin").toLowerCase();
        System.out.println(response25);

        String response26 = server.handleCommand("player k: goto forest").toLowerCase();
        System.out.println(response26);

        String response27 = server.handleCommand("player k: goto riverbank").toLowerCase();
        System.out.println(response27);

        String response28 = server.handleCommand("player k: goto clearing").toLowerCase();
        System.out.println(response28);

        String response29 = server.handleCommand("player k: dig").toLowerCase();
        System.out.println(response29);
        assertTrue(response29.contains("your command does not contain a subject"),
                "Did not see 'your command does not contain a subject'");

        String response30 = server.handleCommand("player k: dig shovel").toLowerCase();
        System.out.println(response30);
        assertTrue(response30.contains("you dig into the soft ground and unearth a pot of gold !!!"),
                "Did not see 'you dig into the soft ground and unearth a pot of gold !!!'");

        String response31 = server.handleCommand("player k: look").toLowerCase();
        System.out.println(response31);
        assertTrue(response31.contains("a big pot of gold"),
                "Did not see 'a big pot of gold'");
        assertTrue(response31.contains("hole"),
                "Did not see 'hole'");
        assertFalse(response31.contains("It looks like the soil has been recently disturbed"),
                "See 'It looks like the soil has been recently disturbed'");

        String response32 = server.handleCommand("player k: get gold").toLowerCase();
        System.out.println(response32);
        assertTrue(response32.contains("picked up"),
                "Did not see 'picked up'");

        String response33 = server.handleCommand("player k: inv").toLowerCase();
        System.out.println(response33);
        assertTrue(response33.contains("gold"),
                "Did not see 'gold'");
        assertTrue(response33.contains("axe"),
                "Did not see 'axe'");
        assertTrue(response33.contains("shovel"),
                "Did not see 'shovel'");
    }

    void testDifferentTriggerWord() {

    }


}