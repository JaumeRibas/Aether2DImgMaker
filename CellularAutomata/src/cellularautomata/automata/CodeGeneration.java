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


public class CodeGeneration {
	
	public static void main(String[] args) {
		printAetherTopplingMethods(3);
	}
	
	public static void printAetherTopplingMethods(int dimension) {
		List<AnysotropicNeighborhoodType> neighborhoodTypes = new ArrayList<AnysotropicNeighborhoodType>();
		int[] coordinates = new int[dimension];
		int sizeMinusOne = dimension * 5;//totally made up
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
		for (int i = 0, num = 1; i < neighborhoodTypes.size(); i = num, num++) {
			AnysotropicNeighborhoodType type = neighborhoodTypes.get(i);
			printAetherTopplingMethod(type, num);
			System.out.println();
		}
	}
	
	private static String getNL(int indentation) {
		String result = System.lineSeparator();
		for (int i = 0; i < indentation; i++) {
			result += "    ";
		}
		return result;
	}
	
	private static void printAetherTopplingMethod(AnysotropicNeighborhoodType type, int number) {
		//TODO finish
		int ind0 = 0;
		int ind1 = ind0 + 1;
		int ind2 = ind1 + 1;
		int dimension = type.coordinates.length;
		StringBuilder method = new StringBuilder(); 
		method.append("private static boolean topplePositionType").append(number).append("(");
		//add coordinates parameters if needed
		for (int i = 1; i < dimension; i++) {
			if (type.coordinates[i] == null) {
				method.append("int ").append(getAxisLetterFromIndex(dimension, i)).append(", ");
			}
		}
		//add current value parameter
		method.append("long currentValue, ");
		//add neighbors parameters 
		int neighborCount = type.neighbors.size();
		for (int i = 0; i < neighborCount; i++) {
			NeighborType neighbor = type.neighbors.get(i);
			char axisLetter = getUpperCaseAxisLetterFromIndex(dimension, neighbor.axisIndex);
			String dir = neighbor.isPositiveDirection? "g" : "s";
			String varPrefix = dir + axisLetter;
			method.append("long ").append(varPrefix).append("Value, ");
			if (neighbor.symmetryCount == null) {
				method.append("int ").append(varPrefix).append("SymmetryCount, ");
			}
			if (neighbor.multiplier == null) {
				method.append("int ").append(varPrefix).append("ShareMultiplier, ");
			}
		}
		StringBuilder arrayBrackets = new StringBuilder();
		for (int i = 1; i < dimension; i++) {
			arrayBrackets.append("[]");
		}
		char firstAxisLetter = getUpperCaseAxisLetterFromIndex(dimension, 0);
		if (neighborCount > 2) {
			//add arrays to reuse
			method.append("long[] relevantAsymmetricNeighborValues, int[][] relevantAsymmetricNeighborCoords, ");
			if (type.hasMultipliers) {
				method.append("int[] relevantAsymmetricNeighborShareMultipliers, ");
			}
			if (type.hasSymmetries) {
				method.append("int[] relevantAsymmetricNeighborSymmetryCounts, ");
			}
			//add new grid slices parameter
			method.append("long").append(arrayBrackets).append("[] new").append(firstAxisLetter).append("Slices) {").append(getNL(ind1));
			String regularCountVariable;
			if (type.hasSymmetries) {
				regularCountVariable = "relevantAsymmetricNeighborCount";
				method.append("int ").append(regularCountVariable).append(" = 0;").append(getNL(ind1));
			} else {
				regularCountVariable = "relevantNeighborCount";
			}
			method.append("int relevantNeighborCount = 0;").append(getNL(ind1));
			for (int i = 0; i < neighborCount; i++) {
				NeighborType neighbor = type.neighbors.get(i);
				char axisLetter = getUpperCaseAxisLetterFromIndex(dimension, neighbor.axisIndex);
				String dir = neighbor.isPositiveDirection? "g" : "s";
				String varPrefix = dir + axisLetter;
				String valueVarName = varPrefix + "Value";
				method.append("if (").append(valueVarName).append(" < currentValue) {").append(getNL(ind2))
				.append("relevantAsymmetricNeighborValues[").append(regularCountVariable).append("] = ").append(valueVarName).append(";").append(getNL(ind2))
				.append("int[] nc = relevantAsymmetricNeighborCoords[").append(regularCountVariable).append("];").append(getNL(ind2))
				.append("nc[0] = ");
				if (neighbor.axisIndex == 0) {
					if (neighbor.isPositiveDirection) {
						method.append("2");
					} else {
						method.append("0");
					}
					method.append(";").append(getNL(ind2));
					for (int axis = 1; axis < dimension; axis++) {
						method.append("nc[").append(axis).append("] = ");
						Integer coordinate = type.coordinates[axis];
						if (coordinate == null) {
							method.append(getAxisLetterFromIndex(dimension, axis));
						} else {
							method.append(coordinate);
						}
						method.append(";").append(getNL(ind2));
					}
				} else {
					method.append("1").append(";").append(getNL(ind2));
					int axis = 1;
					Integer coordinate;
					for (;axis < neighbor.axisIndex; axis++) {
						method.append("nc[").append(axis).append("] = ");
						coordinate = type.coordinates[axis];
						if (coordinate == null) {
							method.append(getAxisLetterFromIndex(dimension, axis));
						} else {
							method.append(coordinate);
						}
						method.append(";").append(getNL(ind2));
					}
					method.append("nc[").append(axis).append("] = ");
					coordinate = type.coordinates[axis];
					if (coordinate == null) {
						method.append(getAxisLetterFromIndex(dimension, axis));
						if (neighbor.isPositiveDirection) {
							method.append(" + 1");
						} else {
							method.append(" - 1");
						}
					} else {
						int neighborCoordinate = coordinate;
						if (neighbor.isPositiveDirection) {
							neighborCoordinate++;
						} else {
							neighborCoordinate--;
						}
						method.append(neighborCoordinate);
					}
					method.append(";").append(getNL(ind2));
					axis++;
					for (; axis < dimension; axis++) {
						method.append("nc[").append(axis).append("] = ");
						coordinate = type.coordinates[axis];
						if (coordinate == null) {
							method.append(getAxisLetterFromIndex(dimension, axis));
						} else {
							method.append(coordinate);
						}
						method.append(";").append(getNL(ind2));
					}
				}
				if (type.hasMultipliers) {
					method.append("relevantAsymmetricNeighborShareMultipliers[").append(regularCountVariable).append("] = ");
					if (neighbor.multiplier == null) {
						method.append(varPrefix).append("ShareMultiplier");
					} else {
						method.append(neighbor.multiplier);
					}
					method.append(";").append(getNL(ind2));
				}
				if (type.hasSymmetries) {
					method.append("relevantAsymmetricNeighborSymmetryCounts[").append(regularCountVariable).append("] = ");
					String symmetryCount;
					if (neighbor.symmetryCount == null) {
						symmetryCount = varPrefix + "SymmetryCount";
					} else {
						symmetryCount = neighbor.symmetryCount.toString();
					}
					method.append(symmetryCount).append(";").append(getNL(ind2))
					.append("relevantNeighborCount += ").append(symmetryCount).append(";").append(getNL(ind2));
				}
				method.append(regularCountVariable).append("++;").append(getNL(ind1)).append("}").append(getNL(ind1));
			}
			method.append("if (topplePosition(new").append(firstAxisLetter).append("Slices, currentValue, ");
			for (int i = 1; i < dimension; i++) {
				if (type.coordinates[i] == null) {
					method.append(getAxisLetterFromIndex(dimension, i));
				} else {
					method.append(type.coordinates[i]);
				}
				method.append(", ");
			}
			method.append("relevantAsymmetricNeighborValues, relevantAsymmetricNeighborCoords, ");
			if (type.hasMultipliers) {
				method.append("relevantAsymmetricNeighborShareMultipliers, ");
			}
			if (type.hasSymmetries) {
				method.append("relevantAsymmetricNeighborSymmetryCounts, relevantNeighborCount, ");
			}
			method.append(regularCountVariable).append(")) {").append(getNL(ind2))
			.append("return true;").append(getNL(ind1))
			.append("} else {").append(getNL(ind2))
			.append("return false;").append(getNL(ind1)).append("}").append(getNL(ind0));
		} else {
			//add new grid slices parameter
			method.append("long").append(arrayBrackets).append(" newCurrent").append(firstAxisLetter).append("Slice, long")
			.append(arrayBrackets).append(" newGreater").append(firstAxisLetter).append("Slice) {").append(getNL(ind0));
			
			//TODO finish
			
		}		
		method.append("}");
		System.out.println(method);
	}
	
	private static class NeighborType {
		int axisIndex;
		boolean isPositiveDirection;
		
		//not used in equals on purpose
		Integer symmetryCount = 1;
		Integer multiplier = 1;
		
		public NeighborType(int axisIndex, boolean isPositiveDirection) {
			this.axisIndex = axisIndex;
			this.isPositiveDirection = isPositiveDirection;
		}
		
		@Override
		public boolean equals(Object other) {
			if (this == other)
				return true;
			if (other == null || other.getClass() != other.getClass()) {
				return false;
			}
			NeighborType otherNeighbor = (NeighborType)other;
			return otherNeighbor.axisIndex == axisIndex && otherNeighbor.isPositiveDirection == isPositiveDirection;
		}
	}
	
	private static class AnysotropicNeighborhoodType {
		boolean hasSymmetries = false;
		boolean hasMultipliers = false;
		List<NeighborType> neighbors = new ArrayList<NeighborType>();
		
		//not used in equals on purpose
		Integer[] coordinates;
		
		public AnysotropicNeighborhoodType(int[] coordinates) {
			this.coordinates = new Integer[coordinates.length];
			for (int i = 0; i < coordinates.length; i++) {
				this.coordinates[i] = coordinates[i];
			}
		}
		
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
		AnysotropicNeighborhoodType type = new AnysotropicNeighborhoodType(coords);
		for (int axis = 0; axis < coords.length; axis++) {
			
			int[] greaterNeighborCoords = coords.clone();
			int[] smallerNeighborCoords = coords.clone();
			greaterNeighborCoords[axis]++;
			smallerNeighborCoords[axis]--;
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
				NeighborType neighbor = new NeighborType(axis, true);
				if (greaterNeighborWeight > 1) {
					type.hasMultipliers = true;
					neighbor.multiplier = greaterNeighborWeight;
				}
				if (greaterNeighborSymmetries > 1) {
					type.hasSymmetries = true;
					neighbor.symmetryCount = greaterNeighborSymmetries;
				}
				type.neighbors.add(neighbor);
			}
			if (smallerNeighborSymmetries > 0) {
				NeighborType neighbor = new NeighborType(axis, false);
				if (smallerNeighborWeight > 1) {
					type.hasMultipliers = true;
					neighbor.multiplier = smallerNeighborWeight;
				}
				if (smallerNeighborSymmetries > 1) {
					type.hasSymmetries = true;
					neighbor.symmetryCount = smallerNeighborSymmetries;
				}
				type.neighbors.add(neighbor);
			}
		}
		int indexOfType = neighborhoodTypes.indexOf(type);
		if (indexOfType < 0) {
			neighborhoodTypes.add(type);
		} else {
			AnysotropicNeighborhoodType generalType = neighborhoodTypes.get(indexOfType);
			for (int coord = 0; coord < coords.length; coord++) {
				if (generalType.coordinates[coord] != type.coordinates[coord]) {
					generalType.coordinates[coord] = null;//it doesn't repeat itself so set it to null
				}
			}
			if (type.hasMultipliers || type.hasSymmetries) {
				int neighborsSize = type.neighbors.size();
				for (int i = 0; i < neighborsSize; i++) {
					NeighborType neighbor = type.neighbors.get(i);
					NeighborType generalNeighbor = generalType.neighbors.get(i);					
					if (generalNeighbor.multiplier != neighbor.multiplier) {
//						System.out.println("It does enter here though");
						generalNeighbor.multiplier = null;//it doesn't repeat itself so set it to null
					}
					if (generalNeighbor.symmetryCount != neighbor.symmetryCount) {
						System.out.println("Does it never enter here?");
						generalNeighbor.symmetryCount = null;//it doesn't repeat itself so set it to null
					}
				}			
			}
		}
	}
	
	public static void printAnisotropicPositionsNeighbors(int dimension, int size) {
		StringBuilder header = new StringBuilder();
		StringBuilder underline = new StringBuilder();
		header.append(' ').append(getAxisLetterFromIndex(dimension, 0)).append(' ');
		underline.append("-----------------------------------");
		for (int coord = 1; coord < dimension; coord++) {
			header.append("| ").append(getAxisLetterFromIndex(dimension, coord)).append(' ');
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
	
	private static char getAxisLetterFromIndex(int dimension, int axisIndex) {
		if (dimension < 3) {
			return (char) (axisIndex + 120);//120 letter 'x'
		} else {
			return (char) (122 - dimension + axisIndex  + 1);//122 letter 'z'
		}
	}
	
	private static char getUpperCaseAxisLetterFromIndex(int dimension, int axisIndex) {
		return Character.toUpperCase(getAxisLetterFromIndex(dimension, axisIndex));
	}
	
	private static void printAnisotropicPositionNeighbors(int[] coords, List<String> neighborhoodTypesA, List<String> neighborhoodTypesB) {
		List<String> neighbors = new ArrayList<String>();
		List<String> plainNeighbors = new ArrayList<String>();
		String[] strCoords = new String[coords.length];
		boolean hasSymmetries = false;
		boolean hasMultipliers = false;
		for (int axis = 0; axis < coords.length; axis++) {
			char coordLetter = getAxisLetterFromIndex(coords.length, axis);
			strCoords[axis] = coords[axis] + "";
			
			int[] greaterNeighborCoords = coords.clone();
			int[] smallerNeighborCoords = coords.clone();
			greaterNeighborCoords[axis]++;
			smallerNeighborCoords[axis]--;
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
				String neighbor = "G" + Character.toUpperCase(coordLetter);
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
				String neighbor = "S" + Character.toUpperCase(coordLetter);
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
