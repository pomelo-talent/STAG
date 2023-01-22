package edu.uob;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.TreeMap;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import org.xml.sax.SAXException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public class actionParser {
    final private TreeMap<String,HashSet<GameAction>> actionsMap = new TreeMap<>();
    final private HashMap<String, HashSet<String>> synonymousTriggersMap = new HashMap<>();

    public actionParser(File actionsFile) {
        try {


            DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            Document document = builder.parse(actionsFile);
            Element root = document.getDocumentElement();
            NodeList actions = root.getChildNodes();

            int k =0;

            for (int i=1; i<actions.getLength(); i=i+2) {
                Element action = (Element) actions.item(i);
                Element triggers = (Element) action.getElementsByTagName("triggers").item(0);
                HashSet<String> triggersSet = new HashSet<>();
                for(int j=0; j<triggers.getElementsByTagName("keyword").getLength();j++) {
                    String triggerPhrase = triggers.getElementsByTagName("keyword").item(j).getTextContent();

                    HashSet<GameAction> GameActionsHashset = new HashSet<>();
                    Element subjects = (Element) action.getElementsByTagName("subjects").item(0);
                    for (int s=0; s<subjects.getElementsByTagName("entity").getLength(); s++) {
                        String subjectPhrase = subjects.getElementsByTagName("entity").item(s).getTextContent();
                        GameAction subjectAction = new GameAction(subjectPhrase, "subjects");
                        GameActionsHashset.add(subjectAction);
                    }

                    Element consumed = (Element) action.getElementsByTagName("consumed").item(0);
                    for (int c=0; c<consumed.getElementsByTagName("entity").getLength(); c++) {
                        String consumedPhrase = consumed.getElementsByTagName("entity").item(c).getTextContent();
                        GameAction consumedAction = new GameAction(consumedPhrase, "consumed");
                        GameActionsHashset.add(consumedAction);
                    }

                    Element produced = (Element) action.getElementsByTagName("produced").item(0);
                    for (int p=0; p<produced.getElementsByTagName("entity").getLength(); p++) {
                        String producedPhrase = produced.getElementsByTagName("entity").item(p).getTextContent();
                        GameAction producedAction = new GameAction(producedPhrase, "produced");
                        GameActionsHashset.add(producedAction);
                    }

                    String narrationPhrase = action.getElementsByTagName("narration").item(0).getTextContent();
                    GameAction narrationAction = new GameAction(narrationPhrase, "narration");
                    GameActionsHashset.add(narrationAction);

                    actionsMap.put(triggerPhrase, GameActionsHashset);

                    triggersSet.add(triggerPhrase);
                    //System.out.println(triggerPhrase);
                }
                synonymousTriggersMap.put(String.valueOf(k),
                        triggersSet);
                //System.out.println(triggersSet);
                k++;
                //System.out.println(synonymousTriggersMap.keySet());
            }
        } catch (ParserConfigurationException pce) {
            System.out.println(pce);
        } catch (IOException ioe) {
            System.out.println(ioe);
        } catch (SAXException saxe) {
            System.out.println(saxe);
        }
    }

    public TreeMap<String, HashSet<GameAction>> getActionList() {
        return actionsMap;
    }

    public HashMap<String, HashSet<String>> getSynonymousTriggersMap() {
        return synonymousTriggersMap;
    }
}
