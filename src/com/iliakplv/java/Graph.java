package com.iliakplv.java;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class Graph {

	private Map<String, Set<String>> graph = new HashMap<>();


	public static void main(String[] args) {
		Graph g = new Graph();
		g.addEdge("a", "b");
		g.addEdge("b", "c");

		System.out.println("\nCycle found: " + g.containsCycle());
	}


	private void addEdge(String fromVertex, String toVertex) {
		final Set<String> adjacentVertices;
		if (graph.containsKey(fromVertex)) {
			adjacentVertices = graph.get(fromVertex);
		} else {
			adjacentVertices = new HashSet<>();
			graph.put(fromVertex, adjacentVertices);
		}

		adjacentVertices.add(toVertex);

		if (!graph.containsKey(toVertex)) {
			graph.put(toVertex, new HashSet<>());
		}
	}


	private boolean containsCycle() {
		final Set<String> visitedVertices = new HashSet<>();

		for (String vertex : graph.keySet()) {
			System.out.println("Depth search from: " + vertex);
			if (depthSearchForCycle(visitedVertices, new HashSet<>(), vertex)) {
				return true;
			}
		}

		return false;
	}

	private boolean depthSearchForCycle(Set<String> visitedVertices, Set<String> reachableVertices, String currentVertex) {

		// search in unvisited vertices
		if (!visitedVertices.contains(currentVertex)) {

			System.out.println(currentVertex);

			// if marked as reachable from current search then cycle found
			if (reachableVertices.contains(currentVertex)) {
				return true;
			}

			// mark as reachable from current search
			reachableVertices.add(currentVertex);

			// continue search in vertices adjacent to current
			for (String adjacentVertex : graph.get(currentVertex)) {
				if (depthSearchForCycle(visitedVertices, reachableVertices, adjacentVertex)) {
					return true;
				}
			}

			// mark as visited
			visitedVertices.add(currentVertex);

		}

		return false;
	}
}
