package edu.uob;

import com.alexmerz.graphviz.Parser;
import com.alexmerz.graphviz.ParseException;
import com.alexmerz.graphviz.objects.Edge;
import com.alexmerz.graphviz.objects.Graph;
import com.alexmerz.graphviz.objects.Node;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.*;


public class entityParser {
    final private ArrayList<EntityLocation> locationList = new ArrayList<>();
    final private HashMap<GameEntity, String> entitiesList= new HashMap<>();
    final private HashMap<String, HashSet<String>> pathList = new HashMap<>();

    public entityParser(File entitiesFile) {
        try {
            Parser parser = new Parser();
            FileReader reader = new FileReader(entitiesFile);
            parser.parse(reader);

            Graph wholeDocument = parser.getGraphs().get(0);
            ArrayList<Graph> firstSubGraphs = wholeDocument.getSubgraphs();
            Graph locationsInFirstGraph = firstSubGraphs.get(0);
            ArrayList<Graph> secondSubGraph = locationsInFirstGraph.getSubgraphs();
            for (Graph graphInSecondGraph:secondSubGraph) {
                // exclude nodes in graphInSecondGraph's sub-graphs
                ArrayList<Node> nodeLocation = graphInSecondGraph.getNodes(false);
                Node newLocation = nodeLocation.get(0);
                locationList.add(new EntityLocation(newLocation.getId().getId(), newLocation.getAttribute("description")));
                ArrayList<Graph> thirdSubGraph = graphInSecondGraph.getSubgraphs();
                for (Graph graphInThirdGraph:thirdSubGraph) {
                    ArrayList<Node> nodeOtherEntities = graphInThirdGraph.getNodes(false);
                    for (Node newOtherEntity:nodeOtherEntities) {
                        if (graphInThirdGraph.getId().getId().equals("artefacts")) {
                            entitiesList.put(new EntityArtefacts(newOtherEntity.getId().getId(),
                                    newOtherEntity.getAttribute("description")), newLocation.getId().getId());
                        } else if (graphInThirdGraph.getId().getId().equals("furniture")) {
                            entitiesList.put(new EntityFurniture(newOtherEntity.getId().getId(),
                                        newOtherEntity.getAttribute("description")), newLocation.getId().getId());
                        } else if (graphInThirdGraph.getId().getId().equals("characters")) {
                            entitiesList.put(new EntityArtefacts(newOtherEntity.getId().getId(),
                                        newOtherEntity.getAttribute("description")), newLocation.getId().getId());
                        }
                    }
                }
            }

            Graph pathsInSecondGraph = firstSubGraphs.get(1);
            ArrayList<Edge> edgesBetweenEntities = pathsInSecondGraph.getEdges();
            for(int i =0; i<locationList.size(); i++) {
                HashSet<String> targetedLocations= new HashSet<>();
                for (Edge edge:edgesBetweenEntities) {
                    if (edge.getSource().getNode().getId().getId().equals(locationList.get(i).getName())) {
                        targetedLocations.add(edge.getTarget().getNode().getId().getId());
                    }
                }
                pathList.put(locationList.get(i).getName(), targetedLocations);
            }

    } catch (FileNotFoundException | ParseException exception) {
            System.out.println(exception);
        }
    }

    public ArrayList<EntityLocation> getLocationList() {
        return locationList;
    }

    public HashMap<GameEntity, String> getEntitiesList() {
        return entitiesList;
    }

    public HashMap<String, HashSet<String>> getPathList() {
        return pathList;
    }

}
