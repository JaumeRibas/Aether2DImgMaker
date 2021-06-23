/* Aether2DImgMaker -- console app to generate images of the Aether cellular automaton in 2D
    Copyright (C) 2017-2021 Jaume Ribas

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>
 */
package cellularautomata.automata;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Miscellaneous methods to aid in writing code. 
 * AETHER HOLY
 * 
 * @author Jaume
 *
 */
public class CodeGeneration {

	public static void main(String[] args) {
		printAetherTopplingMethods(3);
	}
	
	public static void printAetherTopplingMethods(int dimension) {
		List<AnysotropicNeighborhoodType> neighborhoodTypes = new ArrayList<AnysotropicNeighborhoodType>();
		int[] coordinates = new int[dimension];
		int sizeMinusOne = dimension * 10;//totally made up
		int dimensionMinusOne = dimension - 1;
		int currentAxis = dimensionMinusOne;
		while (currentAxis > -1) {
			if (currentAxis == dimensionMinusOne) {
				int previousCoordinate = coordinates[currentAxis - 1];//TODO breaks here for 1D
				for (int currentCoordinate = 0; currentCoordinate <= previousCoordinate; currentCoordinate++) {
					coordinates[currentAxis] = currentCoordinate;
					getAnisotropicNeighborhoodTypes(coordinates, neighborhoodTypes);
				}
				currentAxis--;
			} else {
				int currentCoordinate = coordinates[currentAxis];
				int max;
				if (currentAxis == 0) {
					max = sizeMinusOne;
				} else {
					max = coordinates[currentAxis - 1];
				}
				if (currentCoordinate < max) {
					currentCoordinate++;
					coordinates[currentAxis] = currentCoordinate;
					currentAxis = dimensionMinusOne;
				} else {
					coordinates[currentAxis] = 0;
					currentAxis--;
				}
			}
		}
		for (int i = 0; i < neighborhoodTypes.size(); i++) {
			AnysotropicNeighborhoodType type = neighborhoodTypes.get(i);
			printAetherTopplingMethod(type, i);
			System.out.println();
		}
	}
	
	private static void printAetherTopplingMethod(AnysotropicNeighborhoodType type, int number) {
		//TODO finish
		//String method = "private static boolean topplePositionType14(int y, int z, long currentValue, long greaterXNeighborValue, long smallerYNeighborValue, long greaterZNeighborValue, long smallerZNeighborValue, long[] relevantAsymmetricNeighborValues, int[][] relevantAsymmetricNeighborCoords, int[] relevantAsymmetricNeighborSymmetryCounts, long[][][] newXSlices) {";
	}
	
	private static class NeighborType {
		int coordinateIndex;
		boolean isGreater;
		
		public NeighborType(int coordinateIndex, boolean isGreater) {
			this.coordinateIndex = coordinateIndex;
			this.isGreater = isGreater;
		}
		
		@Override
		public boolean equals(Object other) {
			if (this == other)
				return true;
			if (other == null || other.getClass() != other.getClass()) {
				return false;
			}
			NeighborType otherNeighbor = (NeighborType)other;
			return otherNeighbor.coordinateIndex == coordinateIndex && otherNeighbor.isGreater == isGreater;
		}
	}
	
	private static class AnysotropicNeighborhoodType {
		boolean hasSymmetries = false;
		boolean hasMultipliers = false;
		List<NeighborType> neighbors = new ArrayList<NeighborType>();
		
		@Override
		public boolean equals(Object other) {
			if (this == other)
				return true;
			if (other == null || other.getClass() != other.getClass()) {
				return false;
			}
			AnysotropicNeighborhoodType otherType = (AnysotropicNeighborhoodType)other;
			return otherType.hasMultipliers == hasMultipliers && otherType.hasSymmetries == hasSymmetries && otherType.neighbors.equals(neighbors);
		}
	}
	
	private static void getAnisotropicNeighborhoodTypes(int[] coords, List<AnysotropicNeighborhoodType> neighborhoodTypes) {
		AnysotropicNeighborhoodType type = new AnysotropicNeighborhoodType();
		for (int coord = 0; coord < coords.length; coord++) {
			
			int[] greaterNeighborCoords = coords.clone();
			int[] smallerNeighborCoords = coords.clone();
			greaterNeighborCoords[coord]++;
			smallerNeighborCoords[coord]--;
			int greaterNeighborSymmetries = 1;
			int smallerNeighborSymmetries = 1;
			int greaterNeighborWeight = 1;
			int smallerNeighborWeight = 1;
			
			int[] nc = greaterNeighborCoords;
			if (isOutsideAsymmetricSection(nc)) {
				greaterNeighborSymmetries = 0;
			} else {
				greaterNeighborWeight += getSymmetricNeighborsCount(nc, coords);
				greaterNeighborSymmetries += getSymmetricNeighborsCount(coords, nc);
			}
			nc = smallerNeighborCoords;
			if (isOutsideAsymmetricSection(nc)) {
				smallerNeighborSymmetries = 0;
			} else {
				smallerNeighborWeight += getSymmetricNeighborsCount(nc, coords);
				smallerNeighborSymmetries += getSymmetricNeighborsCount(coords, nc);
			}
			
			if (greaterNeighborSymmetries > 0) {
				NeighborType neighbor = new NeighborType(coord, true);
				if (greaterNeighborWeight > 1) {
					type.hasMultipliers = true;
				}
				if (greaterNeighborSymmetries > 1) {
					type.hasSymmetries = true;
				}
				type.neighbors.add(neighbor);
			}
			if (smallerNeighborSymmetries > 0) {
				NeighborType neighbor = new NeighborType(coord, false);
				if (smallerNeighborWeight > 1) {
					type.hasMultipliers = true;
				}
				if (smallerNeighborSymmetries > 1) {
					type.hasSymmetries = true;
				}
				type.neighbors.add(neighbor);
			}
		}
		if (!neighborhoodTypes.contains(type)) {
			neighborhoodTypes.add(type);
		}
	}
	
	public static void printAnisotropicPositionsNeighbors(int dimension, int size) {
		StringBuilder header = new StringBuilder();
		StringBuilder underline = new StringBuilder();
		header.append(' ').append(getCoordLetterFromIndex(dimension, 0)).append(' ');
		underline.append("-----------------------------------");
		for (int coord = 1; coord < dimension; coord++) {
			header.append("| ").append(getCoordLetterFromIndex(dimension, coord)).append(' ');
			underline.append("----");
		}
		header.append("| Neighborhood | Type A | Type B");
		header.append(System.lineSeparator()).append(underline);
		System.out.println(header);
		List<String> neighborhoodTypesA = new ArrayList<String>();
		List<String> neighborhoodTypesB = new ArrayList<String>();
		int[] coordinates = new int[dimension];
		int sizeMinusOne = size - 1;
		int dimensionMinusOne = dimension - 1;
		int currentAxis = dimensionMinusOne;
		while (currentAxis > -1) {
			if (currentAxis == dimensionMinusOne) {
				int previousCoordinate = coordinates[currentAxis - 1];//TODO breaks here for 1D
				for (int currentCoordinate = 0; currentCoordinate <= previousCoordinate; currentCoordinate++) {
					coordinates[currentAxis] = currentCoordinate;
					printAnisotropicPositionNeighbors(coordinates, neighborhoodTypesA, neighborhoodTypesB);
				}
				System.out.println();
				currentAxis--;
			} else {
				int currentCoordinate = coordinates[currentAxis];
				int max;
				if (currentAxis == 0) {
					max = sizeMinusOne;
				} else {
					max = coordinates[currentAxis - 1];
				}
				if (currentCoordinate < max) {
					currentCoordinate++;
					coordinates[currentAxis] = currentCoordinate;
					currentAxis = dimensionMinusOne;
				} else {
					coordinates[currentAxis] = 0;
					System.out.println();
					currentAxis--;
				}
			}
		}
		System.out.println("Type A count: " + neighborhoodTypesA.size());
		System.out.println("Type B count: " + neighborhoodTypesB.size());
	}	
	
	private static char getCoordLetterFromIndex(int dimension, int coordIndex) {
		if (dimension < 3) {
			return (char) (coordIndex + 120);//120 letter 'x'
		} else {
			return (char) (122 - dimension + coordIndex  + 1);//122 letter 'z'
		}
	}
	
	private static void printAnisotropicPositionNeighbors(int[] coords, List<String> neighborhoodTypesA, List<String> neighborhoodTypesB) {
		List<String> neighbors = new ArrayList<String>();
		List<String> plainNeighbors = new ArrayList<String>();
		String[] strCoords = new String[coords.length];
		boolean hasSymmetries = false;
		boolean hasMultipliers = false;
		for (int coord = 0; coord < coords.length; coord++) {
			char coordLetter = getCoordLetterFromIndex(coords.length, coord);
			strCoords[coord] = coords[coord] + "";
			
			int[] greaterNeighborCoords = coords.clone();
			int[] smallerNeighborCoords = coords.clone();
			greaterNeighborCoords[coord]++;
			smallerNeighborCoords[coord]--;
			int greaterNeighborSymmetries = 1;
			int smallerNeighborSymmetries = 1;
			int greaterNeighborWeight = 1;
			int smallerNeighborWeight = 1;
			
			int[] nc = greaterNeighborCoords;
			if (isOutsideAsymmetricSection(nc)) {
				greaterNeighborSymmetries = 0;
			} else {
				greaterNeighborWeight += getSymmetricNeighborsCount(nc, coords);
				greaterNeighborSymmetries += getSymmetricNeighborsCount(coords, nc);
			}
			nc = smallerNeighborCoords;
			if (isOutsideAsymmetricSection(nc)) {
				smallerNeighborSymmetries = 0;
			} else {
				smallerNeighborWeight += getSymmetricNeighborsCount(nc, coords);
				smallerNeighborSymmetries += getSymmetricNeighborsCount(coords, nc);
			}
			
			if (greaterNeighborSymmetries > 0) {
				String neighbor = ("G" + coordLetter).toUpperCase();
				String plainNeighbor = neighbor;
				if (greaterNeighborWeight > 1) {
					hasMultipliers = true;
					neighbor = neighbor + "(" + greaterNeighborWeight + ")";
				}
				if (greaterNeighborSymmetries > 1) {
					hasSymmetries = true;
					neighbor = greaterNeighborSymmetries + "*" + neighbor;
				}
				neighbors.add(neighbor);
				plainNeighbors.add(plainNeighbor);
			}
			if (smallerNeighborSymmetries > 0) {
				String neighbor = ("S" + coordLetter).toUpperCase();
				String plainNeighbor = neighbor;
				if (smallerNeighborWeight > 1) {
					hasMultipliers = true;
					neighbor = neighbor + "(" + smallerNeighborWeight + ")";
				}
				if (smallerNeighborSymmetries > 1) {
					hasSymmetries = true;
					neighbor = smallerNeighborSymmetries + "*" + neighbor;
				}
				neighbors.add(neighbor);
				plainNeighbors.add(plainNeighbor);
			}
		}
		String neighborhood = String.join(", ", neighbors);		
		int typeA = neighborhoodTypesA.indexOf(neighborhood);
		if (typeA < 0) {
			typeA = neighborhoodTypesA.size();
			neighborhoodTypesA.add(neighborhood);
		}
		typeA++;
		
		String plainNeighborhood = String.join(", ", plainNeighbors);
		plainNeighborhood += " " + hasSymmetries + hasMultipliers;
		int typeB = neighborhoodTypesB.indexOf(plainNeighborhood);
		if (typeB < 0) {
			typeB = neighborhoodTypesB.size();
			neighborhoodTypesB.add(plainNeighborhood);
		}
		typeB++;
		
		System.out.println(" " + String.join(" | ", strCoords) + " | " + neighborhood + " | " + typeA + " | " + typeB);
	}
	
	private static boolean isOutsideAsymmetricSection(int[] coords) {
		for (int i = 0; i < coords.length; i++) {
			if (coords[i] < 0) {
				return true;
			}
		}
		int lengthMinusOne = coords.length - 1;
		for (int i = 0; i < lengthMinusOne; i++) {
			if (coords[i] < coords[i + 1]) {
				return true;
			}
		}
		return false;
	}
	
	private static int getSymmetricNeighborsCount(int[] coords, int[] compareCoords) {
		int count = 0;
		for (int coord = 0; coord < coords.length; coord++) {
			int[] greaterNeighborCoords = coords.clone();
			int[] smallerNeighborCoords = coords.clone();
			greaterNeighborCoords[coord]++;
			smallerNeighborCoords[coord]--;
			if (!Arrays.equals(greaterNeighborCoords, compareCoords)) {
				if (Arrays.equals(getAsymmetricCoords(greaterNeighborCoords), compareCoords)) {
					count++;
				}
			}
			if (!Arrays.equals(smallerNeighborCoords, compareCoords)) {
				if (Arrays.equals(getAsymmetricCoords(smallerNeighborCoords), compareCoords)) {
					count++;
				}
			}
		}
		return count;
	}
	
	private static int[] getAsymmetricCoords(int[] coords){	
		int[] asymmetricCoords = coords.clone();
		for (int i = 0; i < asymmetricCoords.length; i++) {
			int coord = asymmetricCoords[i];
			if (coord < 0) asymmetricCoords[i] = -coord;
		}
		Arrays.sort(asymmetricCoords);
		//reverse order
		int halfLength = asymmetricCoords.length/2;
		for (int i = 0, j = asymmetricCoords.length - 1; i < halfLength; i++, j--) {
			int swp = asymmetricCoords[i];
			asymmetricCoords[i] = asymmetricCoords[j];
			asymmetricCoords[j] = swp;
		}
		return asymmetricCoords;
	}
	
}
