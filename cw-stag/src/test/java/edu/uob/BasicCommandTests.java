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
final class BasicCommandTests {

  private GameServer server;

  // Make a new server for every @Test (i.e. this method runs before every @Test test case)
  @BeforeEach
  void setup() {
      File entitiesFile = Paths.get("config/basic-entities.dot").toAbsolutePath().toFile();
      File actionsFile = Paths.get("config/basic-actions.xml").toAbsolutePath().toFile();
      server = new GameServer(entitiesFile, actionsFile);
  }

  // Test to spawn a new server and send a simple "look" command
  @Test
  void testLookingAroundStartLocation() {
    String response1 = server.handleCommand("player k: look").toLowerCase();
    System.out.println(response1);
    assertTrue(response1.contains("empty room"), "Did not see description of room in response to look");
    assertTrue(response1.contains("magic potion"), "Did not see description of artifacts in response to look");
    assertTrue(response1.contains("wooden trapdoor"), "Did not see description of furniture in response to look");
  }

  @Test
  void testNoFlexibilityInBasicCommands() {
    String response1 = server.handleCommand("player k: potion get").toLowerCase();
    System.out.println(response1);
    assertTrue(response1.contains("your command has some mistakes"), "your command has some mistakes");
  }

  // Add more unit tests or integration tests here.
  @Test
  void testInitializeTwoPlayers() {
    String response1 = server.handleCommand("player  '- '-k: health").toLowerCase();
    System.out.println(response1);
    assertTrue(response1.contains("valid"),
            "Did not see 'valid'");
    assertTrue(response1.contains("3"),
            "Did not see '3'");

    String response2 = server.handleCommand("player 1: health").toLowerCase();
    System.out.println(response2);
    assertTrue(response2.contains("error"),
           "Did not see 'error'");
    assertTrue(response2.contains(" check your playername again"),
            "Did not see ' check your playername again'");

    String response3 = server.handleCommand("player t: health").toLowerCase();
    System.out.println(response3);
    assertTrue(response3.contains("valid"),
            "Did not see 'valid'");
    assertTrue(response3.contains("3"),
            "Did not see '3'");

    String response4 =server.handleCommand("player  '- '-k: goto forest").toLowerCase();
    System.out.println(response4);
    assertTrue(response4.contains("a dark forest"), "Did not see description of forest in response to look");
    assertTrue(response4.contains("brass key"), "Did not see description of artifacts in response to look");

    String response5 = server.handleCommand("player t: look").toLowerCase();
    System.out.println(response5);
    assertTrue(response5.contains("empty room"), "Did not see description of room in response to look");
    assertTrue(response5.contains("magic potion"), "Did not see description of artifacts in response to look");
    assertTrue(response5.contains("wooden trapdoor"), "Did not see description of furniture in response to look");

    String response6 =server.handleCommand("player  '- '-k: get key").toLowerCase();
    System.out.println(response6);
    String response7 =server.handleCommand("player  '- '-k: inv").toLowerCase();
    System.out.println(response7);
    assertTrue(response7.contains("brass key"), "Did not see description of artifacts in response to inv");

    String response8 = server.handleCommand("player t: inventory").toLowerCase();
    System.out.println(response8);
    assertFalse(response8.contains("brass key"), "See 'key' in inv");
    assertTrue(response8.contains("empty"), "Did not see 'empty' in response to inv");

  }


  @Test
  void testGetAndDropAndInvCommand() {
    String response = server.handleCommand("player k: look").toLowerCase();
    System.out.println(response);
    assertTrue(response.contains("empty room"), "Did not see description of room in response to look");
    assertTrue(response.contains("magic potion"), "Did not see description of artifacts in response to look");
    assertTrue(response.contains("wooden trapdoor"), "Did not see description of furniture in response to look");

    String response2 = server.handleCommand("player k: inv").toLowerCase();
    System.out.println(response2);
    assertTrue(response2.contains("empty"), "Did not see 'empty'");

    String response3 = server.handleCommand("player k: get potion").toLowerCase();
    System.out.println(response3);
    assertTrue(response3.contains("potion"), "Did not see 'potion'");

    String response4 = server.handleCommand("player k: get ok").toLowerCase();
    System.out.println(response4);
    assertTrue(response4.contains("the entity cannot be found in the current location"),
            "Did not see 'the entity cannot be found in the current location'");

    String response5 = server.handleCommand("player k: get trapdoor").toLowerCase();
    System.out.println(response5);
    assertTrue(response5.contains("the entity cannot be picked up as it isn't artefact"),
            "Did not see 'the entity cannot be picked up as it isn't artefact'");

    String response6 = server.handleCommand("player k: look").toLowerCase();
    System.out.println(response6);
    assertTrue(response6.contains("trapdoor"),
            "Did not see 'trapdoor'");
    assertFalse(response6.contains("potion"), "See 'potion'");

    String response7 = server.handleCommand("player k: inv").toLowerCase();
    System.out.println(response7);
    assertTrue(response7.contains("potion"), "Did not see 'potion'");

    String response8 = server.handleCommand("player k: drop potion").toLowerCase();
    System.out.println(response8);
    assertTrue(response8.contains("dropped"), "Did not see 'dropped'");

    String response9 = server.handleCommand("player k: look").toLowerCase();
    System.out.println(response9);
    assertTrue(response9.contains("potion"), "Did not see 'potion'");

    String response10 = server.handleCommand("player k: inventory").toLowerCase();
    System.out.println(response10);
    assertTrue(response9.contains("empty"), "Did not see 'empty'");

  }

  @Test
  void testGotoCommand() {
    String response1 = server.handleCommand("player k: look").toLowerCase();
    System.out.println(response1);

    String response2 = server.handleCommand("player k: goto cabin").toLowerCase();
    System.out.println(response2);
    assertTrue(response2.contains("error"),
            "Did not see 'error'");
    assertTrue(response2.contains("you have already been in this location"),
            "Did not see 'you have already been in this location'");

    String response3 = server.handleCommand("player k: goto log").toLowerCase();
    System.out.println(response3);
    assertTrue(response3.contains("error"),
            "Did not see 'error'");
    assertTrue(response3.contains("the entity(new location) cannot be found"),
            "Did not see 'the entity(new location) cannot be found'");

  }

  @Test
  void testOneCommandATime() {
    String response1 = server.handleCommand("player k: look and goto forest").toLowerCase();
    System.out.println(response1);
    assertTrue(response1.contains("error"),
            "Did not see 'error'");
    assertTrue(response1.contains("your basic command is too long"),
            "Did not see 'your basic command is too long'");
  }

  @Test
  void testOneAction() {
    String response1 = server.handleCommand("player k: goto forest").toLowerCase();
    System.out.println(response1);

    String response2 = server.handleCommand("player k: get key").toLowerCase();
    System.out.println(response2);

    String response3 = server.handleCommand("player k: goto cabin").toLowerCase();
    System.out.println(response3);

    String response4 = server.handleCommand("player k: open drink trapdoor").toLowerCase();
    System.out.println(response4);
    assertTrue(response4.contains("one action at a time please"),
            "Did not see 'one action at a time please'");
  }

  @Test
  void testOpenAndFightActionAndHealthCommand() {
    String response = server.handleCommand("player k: look").toLowerCase();
    System.out.println(response);
    assertTrue(response.contains("empty room"), "Did not see description of room in response to look");
    assertTrue(response.contains("magic potion"), "Did not see description of artifacts in response to look");
    assertTrue(response.contains("wooden trapdoor"), "Did not see description of furniture in response to look");

    String response2 = server.handleCommand("player k: inv").toLowerCase();
    System.out.println(response2);
    assertTrue(response2.contains("empty"), "Did not see 'empty'");

    String response3 = server.handleCommand("player k: get potion").toLowerCase();
    System.out.println(response3);
    assertTrue(response3.contains("picked up"), "Did not see 'picked up'");

    String response4 = server.handleCommand("player k: get ok").toLowerCase();
    System.out.println(response4);
    assertTrue(response4.contains("the entity cannot be found in the current location"),
            "Did not see 'the entity cannot be found in the current location'");

    String response5 = server.handleCommand("player k: get trapdoor").toLowerCase();
    System.out.println(response5);
    assertTrue(response5.contains("the entity cannot be picked up as it isn't artefact"),
            "Did not see 'the entity cannot be picked up as it isn't artefact'");

    String response6 = server.handleCommand("player k: look").toLowerCase();
    System.out.println(response6);
    assertFalse(response6.contains("potion"),
            "See 'potion'");

    String response7 = server.handleCommand("player k: inv").toLowerCase();
    System.out.println(response7);
    assertTrue(response7.contains("potion"),
            "Did not see 'potion'");

    String response8 = server.handleCommand("player k: goto forest").toLowerCase();
    System.out.println(response8);

    String response9 = server.handleCommand("player k: get key").toLowerCase();
    System.out.println(response9);
    assertTrue(response9.contains("picked up"), "Did not see 'picked up'");

    String response10 = server.handleCommand("player k: inv").toLowerCase();
    System.out.println(response10);
    assertTrue(response10.contains("potion"), "Did not see 'potion'");
    assertTrue(response10.contains("key"), "Did not see 'key'");

    String response11 = server.handleCommand("player k: goto cabin").toLowerCase();
    System.out.println(response11);

    String response12 = server.handleCommand("player k: open trapdoor").toLowerCase();
    System.out.println(response12);
    assertTrue(response12.contains("you unlock the trapdoor and see steps leading down into a cellar"),
            "Did not see 'you unlock the trapdoor and see steps leading down into a cellar'");

    String response13 = server.handleCommand("player k: look").toLowerCase();
    System.out.println(response13);
    assertTrue(response13.contains("cellar"),
            "Did not see 'cellar'");

    String response14 = server.handleCommand("player k: inv").toLowerCase();
    System.out.println(response14);
    assertTrue(response14.contains("potion"), "Did not see 'potion'");
    assertFalse(response14.contains("key"), "See 'key'");

    String response15 = server.handleCommand("player k: health").toLowerCase();
    System.out.println(response15);
    assertTrue(response15.contains("3"), "Did not see '3'");

    String response16 = server.handleCommand("player k: goto cellar").toLowerCase();
    System.out.println(response16);
    assertTrue(response16.contains("angry elf"), "Did not see 'angry elf'");

    String response17 = server.handleCommand("player k: fight elf log").toLowerCase();
    System.out.println(response17);
    assertTrue(response17.contains("there are some useless subjects in this action"),
            "'there are some useless subjects in this action'");

    String response18 = server.handleCommand("player k: health").toLowerCase();
    System.out.println(response18);
    assertTrue(response18.contains("3"), "Did not see '3'");

    String response19 = server.handleCommand("player k: fight elf").toLowerCase();
    System.out.println(response19);
    assertTrue(response19.contains("you attack the elf, but he fights back and you lose some health"),
            "Did not see 'you attack the elf, but he fights back and you lose some health'");

    String response20 = server.handleCommand("player k: health").toLowerCase();
    System.out.println(response20);
    assertTrue(response20.contains("2"), "Did not see '2'");

    String response21 = server.handleCommand("player k: fight elf").toLowerCase();
    System.out.println(response21);
    assertTrue(response21.contains("you attack the elf, but he fights back and you lose some health"),
            "Did not see 'you attack the elf, but he fights back and you lose some health'");

    String response22 = server.handleCommand("player k: health").toLowerCase();
    System.out.println(response22);
    assertTrue(response22.contains("1"), "Did not see '1'");

    String response23 = server.handleCommand("player k: drink potion").toLowerCase();
    System.out.println(response23);
    assertTrue(response23.contains("you drink the potion and your health improves"),
            "Did not see 'you drink the potion and your health improves'");

    String response24 = server.handleCommand("player k: inv").toLowerCase();
    System.out.println(response24);
    assertTrue(response24.contains("empty"), "Did not see 'empty'");

    String response25 = server.handleCommand("player k: health").toLowerCase();
    System.out.println(response25);
    assertTrue(response25.contains("2"), "Did not see '2'");

    String response26 = server.handleCommand("player k: fight elf").toLowerCase();
    System.out.println(response26);
    assertTrue(response26.contains("you attack the elf, but he fights back and you lose some health"),
            "Did not see 'you attack the elf, but he fights back and you lose some health'");

    String response27 = server.handleCommand("player k: health").toLowerCase();
    System.out.println(response27);
    assertTrue(response27.contains("1"), "Did not see '1'");

    String response28 = server.handleCommand("player k: fight elf").toLowerCase();
    System.out.println(response28);
    assertTrue(response28.contains("you attack the elf, but he fights back and you lose some health"),
            "Did not see 'you attack the elf, but he fights back and you lose some health'");
    assertTrue(response28.contains("you died and lost all of your items, you must return to the start of the game"),
            "Did not see 'you died and lost all of your items, you must return to the start of the game'");

    String response29 = server.handleCommand("player k: health").toLowerCase();
    System.out.println(response29);
    assertTrue(response29.contains("3"), "Did not see '3'");

    String response30 = server.handleCommand("player k: look").toLowerCase();
    System.out.println(response30);
    assertTrue(response30.contains("empty room"), "Did not see description of room in response to look");
    assertTrue(response30.contains("wooden trapdoor"), "Did not see description of furniture in response to look");

  }

  @Test
  void testDeathReset() {
    String response1 = server.handleCommand("player k: get potion").toLowerCase();
    String response2 = server.handleCommand("player k: goto forest").toLowerCase();
    String response3 = server.handleCommand("player k: get key").toLowerCase();
    String response4 = server.handleCommand("player k: goto cabin").toLowerCase();
    String response5 = server.handleCommand("player k: open trapdoor").toLowerCase();
    String response6 = server.handleCommand("player k: goto cellar").toLowerCase();

    String response7 = server.handleCommand("player k: fight elf").toLowerCase();
    System.out.println(response7);
    assertTrue(response7.contains("you attack the elf, but he fights back and you lose some health"),
            "'you attack the elf, but he fights back and you lose some health'");

    String response8 = server.handleCommand("player k: health").toLowerCase();
    System.out.println(response8);
    assertTrue(response8.contains("2"), "Did not see '2'");

    String response9 = server.handleCommand("player k: fight elf").toLowerCase();
    System.out.println(response9);
    assertTrue(response9.contains("you attack the elf, but he fights back and you lose some health"),
           "Did not see 'you attack the elf, but he fights back and you lose some health'");

    String response10 = server.handleCommand("player k: health").toLowerCase();
    System.out.println(response10);
    assertTrue(response10.contains("1"), "Did not see '1'");

    String response11 = server.handleCommand("player k: inv").toLowerCase();
    System.out.println(response11);
    assertTrue(response11.contains("potion"), "Did not see 'potion'");

    String response12 = server.handleCommand("player k: fight elf").toLowerCase();
    System.out.println(response12);
    assertTrue(response12.contains("you attack the elf, but he fights back and you lose some health"),
            "Did not see 'you attack the elf, but he fights back and you lose some health'");
    assertTrue(response12.contains("you died and lost all of your items, you must return to the start of the game"),
            "Did not see 'you died and lost all of your items, you must return to the start of the game'");

    String response13 = server.handleCommand("player k: health").toLowerCase();
    System.out.println(response13);
    assertTrue(response13.contains("3"), "Did not see '3'");

    String response14 = server.handleCommand("player k: inv").toLowerCase();
    System.out.println(response14);
    assertTrue(response14.contains("empty"), "Did not see 'empty'");

    String response15 = server.handleCommand("player k: look").toLowerCase();
    System.out.println(response15);
    assertTrue(response15.contains("empty room"), "Did not see description of room in response to look");
    assertTrue(response15.contains("wooden trapdoor"), "Did not see description of furniture in response to look");

  }


}
