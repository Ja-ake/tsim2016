package com.jakespringer.trump.test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import com.jakespringer.trump.platfinder.Instruction;
import com.jakespringer.trump.platfinder.Node;
import com.jakespringer.trump.platfinder.NodeConnector;
import com.jakespringer.trump.platfinder.PlatfinderGraph;
import com.jakespringer.trump.platfinder.Precalculator;

public class Tester {
    public static void main(String[] args) {
        Scanner scan = new Scanner(System.in);
        // System.out.print("Input file: ");
        String iFileLoc = "map.txt";// scan.nextLine();
        // System.out.print("Output file: ");
        String oFileLoc = "omap.txt";// scan.nextLine();

        List<String> mapString = null;
        try {
            mapString = Files.readAllLines(Paths.get(iFileLoc), StandardCharsets.UTF_8);
        } catch (IOException e) {
            e.printStackTrace();
        }

        boolean[][] map = new boolean[mapString.get(0).length()][mapString.size()];
        for (int i = mapString.size() - 1; i >= 0; i--) {
            for (int j = 0; j < mapString.get(i).length(); j++) {
                map[j][i] = mapString.get((mapString.size() - 1) - i).charAt(j) == 'X';
            }
        }

        Precalculator precalculator = new Precalculator();
        precalculator.setBlockArray(map);
        precalculator.setGravitySpeed(1);
        precalculator.setJumpSpeed(2);
        precalculator.setMoveSpeed(2);
        precalculator.precalculate();
        PlatfinderGraph pfGraph = precalculator.graph;

        String[] outputMap = new String[map[0].length];
        for (int i = 0; i < outputMap.length; i++) {
            outputMap[i] = "";
        }

        for (int i = 0; i < map[0].length; i++) {
            for (int j = 0; j < map.length; j++) {
                outputMap[i] = outputMap[i].concat(map[j][i] == true ? "X" : " ");
            }
        }

        List<Node> nodeList = pfGraph.getNodeList();
        for (int i = 0; i < nodeList.size(); i++) {
            StringBuilder currentString = new StringBuilder(outputMap[nodeList.get(i).y]);
            currentString.setCharAt(nodeList.get(i).x, Character.toChars(65 + i)[0]);
            outputMap[nodeList.get(i).y] = currentString.toString();
        }

        List<String> finaloFile = new ArrayList<String>();
        for (int j = outputMap.length - 1; j >= 0; j--) {
            finaloFile.add(outputMap[j]);
        }

        try {
            Files.write(Paths.get(oFileLoc), finaloFile, StandardCharsets.UTF_8);
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        List<NodeConnector> connectionInstructions = pfGraph.getShortestPath(nodeList.get(65 - 65),
                nodeList.get(77 - 65));
        List<String> instructions = new ArrayList<String>();
        
        if (!connectionInstructions.isEmpty()) {
            for (NodeConnector n : connectionInstructions) {
                for (Instruction i : n.instructions) {
                    if (i.type == Instruction.Type.MOVE_RIGHT) {
                        instructions.add("Move right " + i.amount);
                    } else if (i.type == Instruction.Type.MOVE_LEFT) {
                        instructions.add("Move left " + i.amount);
                    } else if (i.type == Instruction.Type.JUMP) {
                        instructions.add("Jump " + i.delay);
                    } else if (i.type == Instruction.Type.FALL) {
                        instructions.add("Fall " + i.delay);
                    }
                }
            }
        } else {
            System.out.println("Impossible path");
        }

        for (String print : instructions) {
            System.out.println(print);
        }
    }
}