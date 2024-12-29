/* Aether2DImgMaker -- console app to generate images of the Aether cellular automaton in 2D
    Copyright (C) 2017-2024 Jaume Ribas

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
package cellularautomata;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;


final class CodeGeneration {
	
	private CodeGeneration() {}
	
	private static final String nl = System.lineSeparator();
	private static final String indentation = "    ";
	
	public static void main(String[] args) {
		printAnisotropicRegionVonNeumannNeighborhoodsWithOutgoingSymmetries(2, 10, false, false);		
	}
	
	static final class Aether {
		
		private Aether() {}
		
		static void printTopplingMethods(int dimension) {
			if (dimension < 2) {
				throw new IllegalArgumentException("The dimension must be greater than one.");
			}
			List<TypeOfAnysotropicRegionVonNeumannNeighborhoodWithBidirectionalSymmetries> neighborhoodTypes = new ArrayList<TypeOfAnysotropicRegionVonNeumannNeighborhoodWithBidirectionalSymmetries>();
			int[] coordinates = new int[dimension];
			int size = (dimension - 1)*2 + 3;//it seems to work to get all neighborhood types
			size++;//plus one so the last type can be generalized
			int sizeMinusOne = size - 1;
			int dimensionMinusOne = dimension - 1;
			int currentAxis = dimensionMinusOne;
			while (currentAxis > -1) {
				if (currentAxis == dimensionMinusOne) {
					TypeOfAnysotropicRegionVonNeumannNeighborhoodWithBidirectionalSymmetries type = getTypeOfAnisotropicRegionVonNeumannNeighborhoodWithBidirectionalSymmetries(coordinates);
					int indexOfType = getIndexOfSame(neighborhoodTypes, type);
					if (indexOfType == -1) {
						neighborhoodTypes.add(type);
					} else {
						TypeOfAnysotropicRegionVonNeumannNeighborhoodWithBidirectionalSymmetries generalType = neighborhoodTypes.get(indexOfType);
						generalize(generalType, type);
					}
				}
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
			for (int i = 0, num = 1; i < neighborhoodTypes.size(); i = num, num++) {
				TypeOfAnysotropicRegionVonNeumannNeighborhoodWithBidirectionalSymmetries type = neighborhoodTypes.get(i);
				printTopplingMethod(type, num);
				System.out.println();
			}
		}
		
		static void printTopplingMethod(TypeOfAnysotropicRegionVonNeumannNeighborhoodWithBidirectionalSymmetries type, int number) {
			//TODO finish
			String nl0 = nl;
			String nl1 = nl + indentation;
			String nl2 = nl1 + indentation;
			int dimension = type.coordinates.length;
			StringBuilder method = new StringBuilder(); 
			method.append("private static boolean topplePositionOfType").append(number).append('(');
			//add coordinates parameters if needed
			for (int i = 1; i < dimension; i++) {
				if (type.coordinates[i] == null) {
					method.append("int ").append(Utils.getAxisLabel(dimension, i)).append(", ");
				}
			}
			//add current value parameter
			method.append("long currentValue, ");
			//add neighbors parameters 
			int neighborCount = type.neighbors.size();
			for (int i = 0; i < neighborCount; i++) {
				TypeOfAnysotropicRegionVonNeumannNeighborWithBidirectionalSymmetries neighbor = type.neighbors.get(i);
				String axisLabel = Utils.getUpperCaseAxisLabel(dimension, neighbor.axisIndex);
				char dir = neighbor.isPositiveDirection? 'g' : 's';
				String varPrefix = dir + axisLabel;
				method.append("long ").append(varPrefix).append("Value, ");
				if (neighbor.incomingSymmetryCount == null) {
					method.append("int ").append(varPrefix).append("SymmetryCount, ");
				}
				if (neighbor.outgoingSymmetryCount == null) {
					method.append("int ").append(varPrefix).append("ShareMultiplier, ");
				}
			}
			StringBuilder arrayBrackets = new StringBuilder();
			for (int i = 1; i < dimension; i++) {
				arrayBrackets.append("[]");
			}
			String firstAxisLabel = Utils.getUpperCaseAxisLabel(dimension, 0);
			if (neighborCount > 2) {
				//add arrays to reuse
				method.append("long[] relevantAsymmetricNeighborValues, int[] sortedNeighborsIndexes, int[][] relevantAsymmetricNeighborCoords, ");
				if (type.hasOutgoingSymmetries) {
					method.append("int[] relevantAsymmetricNeighborShareMultipliers, ");
				}
				if (type.hasIncomingSymmetries) {
					method.append("int[] relevantAsymmetricNeighborSymmetryCounts, ");
				}
				//add new grid slices parameter
				method.append("long").append(arrayBrackets).append("[] new").append(firstAxisLabel).append("Slices) {").append(nl1);
				String relevantNeighborCountVariable;
				if (type.hasIncomingSymmetries) {
					relevantNeighborCountVariable = "relevantAsymmetricNeighborCount";
					method.append("int ").append(relevantNeighborCountVariable).append(" = 0;").append(nl1);
				} else {
					relevantNeighborCountVariable = "relevantNeighborCount";
				}
				method.append("int relevantNeighborCount = 0;").append(nl1);
				for (int i = 0; i < neighborCount; i++) {
					TypeOfAnysotropicRegionVonNeumannNeighborWithBidirectionalSymmetries neighbor = type.neighbors.get(i);
					String axisLabel = Utils.getUpperCaseAxisLabel(dimension, neighbor.axisIndex);
					char dir = neighbor.isPositiveDirection? 'g' : 's';
					String varPrefix = dir + axisLabel;
					String valueVarName = varPrefix + "Value";
					method.append("if (").append(valueVarName).append(" < currentValue) {").append(nl2)
					.append("relevantAsymmetricNeighborValues[").append(relevantNeighborCountVariable).append("] = ").append(valueVarName).append(';').append(nl2)
					.append("int[] nc = relevantAsymmetricNeighborCoords[").append(relevantNeighborCountVariable).append("];").append(nl2)
					.append("nc[0] = ");
					if (neighbor.axisIndex == 0) {
						if (neighbor.isPositiveDirection) {
							method.append('2');
						} else {
							method.append('0');
						}
						method.append(';').append(nl2);
						for (int axis = 1; axis < dimension; axis++) {
							method.append("nc[").append(axis).append("] = ");
							Integer coordinate = type.coordinates[axis];
							if (coordinate == null) {
								method.append(Utils.getAxisLabel(dimension, axis));
							} else {
								method.append(coordinate);
							}
							method.append(';').append(nl2);
						}
					} else {
						method.append("1;").append(nl2);
						int axis = 1;
						Integer coordinate;
						for (;axis < neighbor.axisIndex; axis++) {
							method.append("nc[").append(axis).append("] = ");
							coordinate = type.coordinates[axis];
							if (coordinate == null) {
								method.append(Utils.getAxisLabel(dimension, axis));
							} else {
								method.append(coordinate);
							}
							method.append(';').append(nl2);
						}
						method.append("nc[").append(axis).append("] = ");
						coordinate = type.coordinates[axis];
						if (coordinate == null) {
							method.append(Utils.getAxisLabel(dimension, axis));
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
						method.append(';').append(nl2);
						axis++;
						for (; axis < dimension; axis++) {
							method.append("nc[").append(axis).append("] = ");
							coordinate = type.coordinates[axis];
							if (coordinate == null) {
								method.append(Utils.getAxisLabel(dimension, axis));
							} else {
								method.append(coordinate);
							}
							method.append(';').append(nl2);
						}
					}
					if (type.hasOutgoingSymmetries) {
						method.append("relevantAsymmetricNeighborShareMultipliers[").append(relevantNeighborCountVariable).append("] = ");
						if (neighbor.outgoingSymmetryCount == null) {
							method.append(varPrefix).append("ShareMultiplier");
						} else {
							method.append(neighbor.outgoingSymmetryCount);
						}
						method.append(';').append(nl2);
					}
					if (type.hasIncomingSymmetries) {
						method.append("relevantAsymmetricNeighborSymmetryCounts[").append(relevantNeighborCountVariable).append("] = ");
						String symmetryCount;
						if (neighbor.incomingSymmetryCount == null) {
							symmetryCount = varPrefix + "SymmetryCount";
						} else {
							symmetryCount = neighbor.incomingSymmetryCount.toString();
						}
						method.append(symmetryCount).append(';').append(nl2)
						.append("relevantNeighborCount += ").append(symmetryCount).append(';').append(nl2);
					}
					method.append(relevantNeighborCountVariable).append("++;").append(nl1).append('}').append(nl1);
				}
				method.append("return topplePosition(new").append(firstAxisLabel).append("Slices, currentValue, ");
				for (int i = 1; i < dimension; i++) {
					if (type.coordinates[i] == null) {
						method.append(Utils.getAxisLabel(dimension, i));
					} else {
						method.append(type.coordinates[i]);
					}
					method.append(", ");
				}
				method.append("relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, ");
				if (type.hasOutgoingSymmetries) {
					method.append("relevantAsymmetricNeighborShareMultipliers, ");
				}
				if (type.hasIncomingSymmetries) {
					method.append("relevantAsymmetricNeighborSymmetryCounts, relevantNeighborCount, ");
				}
				method.append(relevantNeighborCountVariable).append(");");
			} else {
				//add new grid slices parameter
				method.append("long").append(arrayBrackets).append(" newCurrent").append(firstAxisLabel).append("Slice, long")
				.append(arrayBrackets).append(" newGreater").append(firstAxisLabel).append("Slice) {");
				
				//TODO finish
				
			}		
			method.append(nl0).append('}');
			System.out.println(method);
		}
		
		static void printFileBackedTopplingMethods(int dimension) {
			if (dimension < 2) {
				throw new IllegalArgumentException("The dimension must be greater than one.");
			}
			List<TypeOfAnysotropicRegionVonNeumannNeighborhoodWithBidirectionalSymmetries> neighborhoodTypes = new ArrayList<TypeOfAnysotropicRegionVonNeumannNeighborhoodWithBidirectionalSymmetries>();
			int[] coordinates = new int[dimension];
			int size = (dimension - 1)*2 + 3;//it seems to work to get all neighborhood types
			size++;//plus one so the last type can be generalized
			int sizeMinusOne = size - 1;
			int dimensionMinusOne = dimension - 1;
			int currentAxis = dimensionMinusOne;
			while (currentAxis > -1) {
				if (currentAxis == dimensionMinusOne) {
					TypeOfAnysotropicRegionVonNeumannNeighborhoodWithBidirectionalSymmetries type = getTypeOfAnisotropicRegionVonNeumannNeighborhoodWithBidirectionalSymmetries(coordinates);
					int indexOfType = getIndexOfSame(neighborhoodTypes, type);
					if (indexOfType == -1) {
						neighborhoodTypes.add(type);
					} else {
						TypeOfAnysotropicRegionVonNeumannNeighborhoodWithBidirectionalSymmetries generalType = neighborhoodTypes.get(indexOfType);
						generalize(generalType, type);
					}
				}
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
			for (int i = 0, num = 1; i < neighborhoodTypes.size(); i = num, num++) {
				TypeOfAnysotropicRegionVonNeumannNeighborhoodWithBidirectionalSymmetries type = neighborhoodTypes.get(i);
				printFileBackedTopplingMethod(type, num);
				System.out.println();
			}
		}
		
		static void printFileBackedTopplingMethod(TypeOfAnysotropicRegionVonNeumannNeighborhoodWithBidirectionalSymmetries type, int number) {
			//TODO finish
			String nl0 = nl;
			String nl1 = nl + indentation;
			String nl2 = nl1 + indentation;
			int dimension = type.coordinates.length;
			StringBuilder method = new StringBuilder(); 
			method.append("private static boolean topplePositionOfType").append(number).append('(');
			//add coordinates parameters if needed
			for (int i = 0; i < dimension; i++) {
				if (type.coordinates[i] == null) {
					method.append("int ").append(Utils.getAxisLabel(dimension, i)).append(", ");
				}
			}
			//add current value parameter
			method.append("long currentValue, ");
			//add neighbors parameters 
			int neighborCount = type.neighbors.size();
			for (int i = 0; i < neighborCount; i++) {
				TypeOfAnysotropicRegionVonNeumannNeighborWithBidirectionalSymmetries neighbor = type.neighbors.get(i);
				String axisLabel = Utils.getUpperCaseAxisLabel(dimension, neighbor.axisIndex);
				char dir = neighbor.isPositiveDirection? 'g' : 's';
				String varPrefix = dir + axisLabel;
				method.append("long ").append(varPrefix).append("Value, ");
				if (neighbor.incomingSymmetryCount == null) {
					method.append("int ").append(varPrefix).append("SymmetryCount, ");
				}
				if (neighbor.outgoingSymmetryCount == null) {
					method.append("int ").append(varPrefix).append("ShareMultiplier, ");
				}
			}
			if (neighborCount > 2) {
				//add arrays to reuse
				method.append("long[] relevantAsymmetricNeighborValues, int[] sortedNeighborsIndexes, int[][] relevantAsymmetricNeighborCoords, ");
				if (type.hasOutgoingSymmetries) {
					method.append("int[] relevantAsymmetricNeighborShareMultipliers, ");
				}
				if (type.hasIncomingSymmetries) {
					method.append("int[] relevantAsymmetricNeighborSymmetryCounts, ");
				}
				//add new grid parameter
				method.append("RandomAccessFile newGrid) throws IOException {").append(nl1);
				String relevantNeighborCountVariable;
				if (type.hasIncomingSymmetries) {
					relevantNeighborCountVariable = "relevantAsymmetricNeighborCount";
					method.append("int ").append(relevantNeighborCountVariable).append(" = 0;").append(nl1);
				} else {
					relevantNeighborCountVariable = "relevantNeighborCount";
				}
				method.append("int relevantNeighborCount = 0;").append(nl1);
				for (int i = 0; i < neighborCount; i++) {
					TypeOfAnysotropicRegionVonNeumannNeighborWithBidirectionalSymmetries neighbor = type.neighbors.get(i);
					String axisLabel = Utils.getUpperCaseAxisLabel(dimension, neighbor.axisIndex);
					char dir = neighbor.isPositiveDirection? 'g' : 's';
					String varPrefix = dir + axisLabel;
					String valueVarName = varPrefix + "Value";
					method.append("if (").append(valueVarName).append(" < currentValue) {").append(nl2)
					.append("relevantAsymmetricNeighborValues[").append(relevantNeighborCountVariable).append("] = ").append(valueVarName).append(';').append(nl2)
					.append("int[] nc = relevantAsymmetricNeighborCoords[").append(relevantNeighborCountVariable).append("];").append(nl2);
					int axis = 0;
					Integer coordinate;
					for (; axis < neighbor.axisIndex; axis++) {
						method.append("nc[").append(axis).append("] = ");
						coordinate = type.coordinates[axis];
						if (coordinate == null) {
							method.append(Utils.getAxisLabel(dimension, axis));
						} else {
							method.append(coordinate);
						}
						method.append(';').append(nl2);
					}
					method.append("nc[").append(axis).append("] = ");
					coordinate = type.coordinates[axis];
					if (coordinate == null) {
						method.append(Utils.getAxisLabel(dimension, axis));
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
					method.append(';').append(nl2);
					axis++;
					for (; axis < dimension; axis++) {
						method.append("nc[").append(axis).append("] = ");
						coordinate = type.coordinates[axis];
						if (coordinate == null) {
							method.append(Utils.getAxisLabel(dimension, axis));
						} else {
							method.append(coordinate);
						}
						method.append(';').append(nl2);
					}
					if (type.hasOutgoingSymmetries) {
						method.append("relevantAsymmetricNeighborShareMultipliers[").append(relevantNeighborCountVariable).append("] = ");
						if (neighbor.outgoingSymmetryCount == null) {
							method.append(varPrefix).append("ShareMultiplier");
						} else {
							method.append(neighbor.outgoingSymmetryCount);
						}
						method.append(';').append(nl2);
					}
					if (type.hasIncomingSymmetries) {
						method.append("relevantAsymmetricNeighborSymmetryCounts[").append(relevantNeighborCountVariable).append("] = ");
						String symmetryCount;
						if (neighbor.incomingSymmetryCount == null) {
							symmetryCount = varPrefix + "SymmetryCount";
						} else {
							symmetryCount = neighbor.incomingSymmetryCount.toString();
						}
						method.append(symmetryCount).append(';').append(nl2)
						.append("relevantNeighborCount += ").append(symmetryCount).append(';').append(nl2);
					}
					method.append(relevantNeighborCountVariable).append("++;").append(nl1).append('}').append(nl1);
				}
				method.append("return topplePosition(newGrid, currentValue, ");
				for (int i = 0; i < dimension; i++) {
					if (type.coordinates[i] == null) {
						method.append(Utils.getAxisLabel(dimension, i));
					} else {
						method.append(type.coordinates[i]);
					}
					method.append(", ");
				}
				method.append("relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, ");
				if (type.hasOutgoingSymmetries) {
					method.append("relevantAsymmetricNeighborShareMultipliers, ");
				}
				if (type.hasIncomingSymmetries) {
					method.append("relevantAsymmetricNeighborSymmetryCounts, relevantNeighborCount, ");
				}
				method.append(relevantNeighborCountVariable).append(");");
			} else {
				//add new grid slices parameter
				method.append("RandomAccessFile newGrid) throws IOException {");
				
				//TODO finish
				
			}		
			method.append(nl0).append('}');
			System.out.println(method);
		}
		
	}
	
	static final class Sunflower {
			
		private Sunflower() {}
		
		static void printTopplingMethods(int dimension) {
			if (dimension < 2) {
				throw new IllegalArgumentException("The dimension must be greater than one.");
			}
			List<TypeOfAnysotropicRegionVonNeumannNeighborhoodWithSymmetries> neighborhoodTypes = new ArrayList<TypeOfAnysotropicRegionVonNeumannNeighborhoodWithSymmetries>();
			int[] coordinates = new int[dimension];
			int size = (dimension - 1)*2 + 3;//it seems to work to get all neighborhood types
			size++;//plus one so the last type can be generalized
			int sizeMinusOne = size - 1;
			int dimensionMinusOne = dimension - 1;
			int currentAxis = dimensionMinusOne;
			while (currentAxis > -1) {
				if (currentAxis == dimensionMinusOne) {
					TypeOfAnysotropicRegionVonNeumannNeighborhoodWithSymmetries type = getTypeOfAnisotropicRegionVonNeumannNeighborhoodWithOutgoingSymmetries(coordinates);
					int indexOfType = getIndexOfSame(neighborhoodTypes, type);
					if (indexOfType == -1) {
						neighborhoodTypes.add(type);
					} else {
						TypeOfAnysotropicRegionVonNeumannNeighborhoodWithSymmetries generalType = neighborhoodTypes.get(indexOfType);
						generalize(generalType, type);
					}
				}
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
			for (int i = 0, num = 1; i < neighborhoodTypes.size(); i = num, num++) {
				TypeOfAnysotropicRegionVonNeumannNeighborhoodWithSymmetries type = neighborhoodTypes.get(i);
				printTopplingMethod(type, num);
				System.out.println();
			}
		}
		
		static void printTopplingMethod(TypeOfAnysotropicRegionVonNeumannNeighborhoodWithSymmetries type, int number) {
			String nl0 = nl;
			String nl1 = nl + indentation;
			String nl2 = nl1 + indentation;
			int dimension = type.coordinates.length;
			int neighborCount = type.neighbors.size();
			StringBuilder method = new StringBuilder(); 
			method.append("private static boolean topplePositionOfType").append(number).append('(');
			StringBuilder strCurrentCoords = new StringBuilder();
			//add coordinates parameters if needed
			for (int i = 1; i < dimension; i++) {
				strCurrentCoords.append('[');
				Integer coord = type.coordinates[i];
				if (coord == null) {
					String axisLabel = Utils.getAxisLabel(dimension, i);
					method.append("int ").append(axisLabel).append(", ");
					strCurrentCoords.append(axisLabel);
				} else {
					strCurrentCoords.append(coord);
				}
				strCurrentCoords.append(']');
			}
			StringBuilder arrayBrackets = new StringBuilder();
			for (int i = 1; i < dimension; i++) {
				arrayBrackets.append("[]");
			}
			String firstAxisLabel = Utils.getUpperCaseAxisLabel(dimension, 0);
			//add current grid slice parameter
			method.append("long").append(arrayBrackets).append(" current").append(firstAxisLabel).append("Slice, long")
			//add new current grid slice parameter
			.append(arrayBrackets).append(" newCurrent").append(firstAxisLabel).append("Slice");
			TypeOfAnysotropicRegionVonNeumannNeighborWithSymmetries firstNeighbor = type.neighbors.get(0);
			if (firstNeighbor.axisIndex == 0) {
				if (firstNeighbor.isPositiveDirection) {
					//add new greater grid slice parameter
					method.append(", long").append(arrayBrackets).append(" newGreater").append(firstAxisLabel).append("Slice");
					if (neighborCount != 1) {
						TypeOfAnysotropicRegionVonNeumannNeighborWithSymmetries secondNeighbor = type.neighbors.get(1);
						if (secondNeighbor.axisIndex == 0) {
							//add new smaller grid slice parameter
							method.append(", long").append(arrayBrackets).append(" newSmaller").append(firstAxisLabel).append("Slice");
						}
					}
				} else {
					//add new smaller grid slice parameter
					method.append(", long").append(arrayBrackets).append(" newSmaller").append(firstAxisLabel).append("Slice");
				}
			}
			if (type.hasSymmetries) {
				//add neighbor multipliers parameters
				for (int i = 0; i < neighborCount; i++) {
					TypeOfAnysotropicRegionVonNeumannNeighborWithSymmetries neighbor = type.neighbors.get(i);
					if (neighbor.symmetryCount == null) {
						String axisLabel = Utils.getUpperCaseAxisLabel(dimension, neighbor.axisIndex);
						char dir = neighbor.isPositiveDirection? 'g' : 's';
						method.append(", int ").append(dir).append(axisLabel).append("ShareMultiplier");
					}
				}
			}
			long dimensionTimesTwo = (long)dimension*2;
			method.append(") {").append(nl1).append("boolean toppled = false;").append(nl1)
			.append("long currentValue = current").append(firstAxisLabel).append("Slice").append(strCurrentCoords).append(';').append(nl1)
			.append("long share = currentValue/").append(dimensionTimesTwo + 1).append(';').append(nl1)
			.append("if (share != 0) {").append(nl2).append("toppled = true;").append(nl2).append("newCurrent").append(firstAxisLabel).append("Slice").append(strCurrentCoords)
			.append(" += currentValue - ").append(dimensionTimesTwo).append("*share;");
			int neighborIndex = 0;
			if (firstNeighbor.axisIndex == 0) {
				method.append(nl2);
				if (firstNeighbor.isPositiveDirection) {
					method.append("newGreater").append(firstAxisLabel).append("Slice").append(strCurrentCoords).append(" += ");
					if (firstNeighbor.symmetryCount == null) {
						method.append('g').append(firstAxisLabel).append("ShareMultiplier*");
					} else if (firstNeighbor.symmetryCount != 1) {
						method.append(firstNeighbor.symmetryCount).append('*');
					}
					method.append("share;");
					if (neighborCount != 1) {
						TypeOfAnysotropicRegionVonNeumannNeighborWithSymmetries secondNeighbor = type.neighbors.get(1);
						if (secondNeighbor.axisIndex == 0) {
							method.append(nl2).append("newSmaller").append(firstAxisLabel).append("Slice").append(strCurrentCoords).append(" += ");
							if (secondNeighbor.symmetryCount == null) {
								method.append('s').append(firstAxisLabel).append("ShareMultiplier*");
							} else if (secondNeighbor.symmetryCount != 1) {
								method.append(secondNeighbor.symmetryCount).append('*');
							}
							method.append("share;");
							neighborIndex = 2;
						} else {
							neighborIndex = 1;
						}
					} else {
						neighborIndex = 1;
					}
				} else {
					method.append("newSmaller").append(firstAxisLabel).append("Slice").append(strCurrentCoords).append(" += ");
					if (firstNeighbor.symmetryCount == null) {
						method.append('s').append(firstAxisLabel).append("ShareMultiplier*");
					} else if (firstNeighbor.symmetryCount != 1) {
						method.append(firstNeighbor.symmetryCount).append('*');
					}
					method.append("share;");
					neighborIndex = 1;
				}
			}
			for (; neighborIndex < neighborCount; neighborIndex++) {
				TypeOfAnysotropicRegionVonNeumannNeighborWithSymmetries neighbor = type.neighbors.get(neighborIndex);
				method.append(nl2).append("newCurrent").append(firstAxisLabel).append("Slice");
				int i = 1;
				for (; i != neighbor.axisIndex; i++) {
					method.append('[');
					Integer coord = type.coordinates[i];
					if (coord == null) {
						String axisLabel = Utils.getAxisLabel(dimension, i);
						method.append(axisLabel);
					} else {
						method.append(coord);
					}
					method.append(']');
				}
				method.append('[');
				Integer coord = type.coordinates[neighbor.axisIndex];
				if (coord == null) {
					String axisLabel = Utils.getAxisLabel(dimension, neighbor.axisIndex);
					method.append(axisLabel).append(neighbor.isPositiveDirection ? '+' : '-').append('1');
				} else {
					method.append(coord + (neighbor.isPositiveDirection ? 1 : -1));
				}
				method.append(']');
				for (i++; i != dimension; i++) {
					method.append('[');
					coord = type.coordinates[i];
					if (coord == null) {
						String axisLabel = Utils.getAxisLabel(dimension, i);
						method.append(axisLabel);
					} else {
						method.append(coord);
					}
					method.append(']');
				}
				method.append(" += ");
				if (neighbor.symmetryCount == null) {
					method.append(neighbor.isPositiveDirection ? 'g' : 's').append(Utils.getUpperCaseAxisLabel(dimension, neighbor.axisIndex)).append("ShareMultiplier*");
				} else if (neighbor.symmetryCount != 1) {
					method.append(neighbor.symmetryCount).append('*');
				}
				method.append("share;");
			}
			method.append(nl1).append("} else {").append(nl2).append("newCurrent").append(firstAxisLabel).append("Slice").append(strCurrentCoords)
			.append(" += currentValue;").append(nl1).append('}').append(nl1).append("return toppled;").append(nl0).append('}');			
			System.out.println(method);
		}
			
	}
	
	static void printBoundsMethodsForAnisotropicGrid(int dimension) {
		String[] axisLabels = new String[dimension];
		String[] axisUpperCaseLabels = new String[dimension];
		for (int axis = 0; axis < dimension; axis++) {
			axisLabels[axis] = Utils.getAxisLabel(dimension, axis);
			axisUpperCaseLabels[axis] = Utils.getUpperCaseAxisLabel(dimension, axis);
		}
		int dimensionMinusOne = dimension - 1;
		for (int currentAxis = 0; currentAxis < dimension; currentAxis++) {
			String currentAxisUpperCaseLabel = axisUpperCaseLabels[currentAxis];
			System.out.println("@Override" + nl + "default int getMin" + currentAxisUpperCaseLabel 
					+ "() { return 0; }" + nl);
			int[] otherAxes = new int[dimensionMinusOne];
			int i = 0;
			for (; i < currentAxis; i++) {
				otherAxes[i] = i;
			}
			for (int j = currentAxis + 1; i < dimensionMinusOne; i = j, j++) {
				otherAxes[i] = j;
			}
			int otherAxesCountMinusOne = otherAxes.length - 1;
			for (int indexCount = 1, indexCountMinusOne = 0; indexCount < otherAxes.length; indexCountMinusOne = indexCount, indexCount++) {
				int[] indexes = new int[indexCount];
				for (i = 1; i < indexes.length; i++) {
					indexes[i] = i;
				}
				i = indexCountMinusOne;
				while (i > -1) {
					if (i == indexCountMinusOne) {
						boolean isThereAGreaterAxis = false;
						boolean isThereASmallerAxis = false;
						int smallestGreaterAxis = 0, greatestSmallerAxis = 0;
						int otherAxis = otherAxes[indexes[0]];
						if (otherAxis > currentAxis) {
							isThereAGreaterAxis = true;
							smallestGreaterAxis = otherAxis;
						} else {
							isThereASmallerAxis = true;
							greatestSmallerAxis = otherAxis;
						}
						StringBuilder otherAxesInMethodName = new StringBuilder();
						StringBuilder otherAxesParams = new StringBuilder();
						otherAxesInMethodName.append(axisUpperCaseLabels[otherAxis]);
						otherAxesParams.append("int ").append(axisLabels[otherAxis]);
						for (int j = 1; j < indexCount; j++) {
							otherAxis = otherAxes[indexes[j]];
							if (otherAxis > currentAxis) {
								if (!isThereAGreaterAxis) {
									isThereAGreaterAxis = true;
									smallestGreaterAxis = otherAxis;
								}
							} else {
								isThereASmallerAxis = true;
								greatestSmallerAxis = otherAxis;
							}
							otherAxesInMethodName.append(axisUpperCaseLabels[otherAxis]);
							otherAxesParams.append(", int ").append(axisLabels[otherAxis]);
						}
						if (isThereAGreaterAxis) {
							System.out.println("@Override" + nl 
							+ "default int getMin" + currentAxisUpperCaseLabel + "At" + otherAxesInMethodName + '(' + otherAxesParams 
							+ ") { return " + axisLabels[smallestGreaterAxis] + "; }" + nl);
						} else {
							System.out.println("@Override" + nl 
							+ "default int getMin" + currentAxisUpperCaseLabel + "At" + otherAxesInMethodName + '(' + otherAxesParams 
							+ ") { return 0; }" + nl);
						}
						if (isThereASmallerAxis) {
							System.out.println("@Override" + nl 
							+ "default int getMax" + currentAxisUpperCaseLabel + "At" + otherAxesInMethodName + '(' + otherAxesParams 
							+ ") { return Math.min(" + axisLabels[greatestSmallerAxis] + ", getMax" + currentAxisUpperCaseLabel + "()); }" + nl);
						} else {
							System.out.println("@Override" + nl 
							+ "default int getMax" + currentAxisUpperCaseLabel + "At" + otherAxesInMethodName + '(' + otherAxesParams 
							+ ") { return getMax" + currentAxisUpperCaseLabel + "(); }" + nl);
						}
					}
					int index = indexes[i];
					int max = otherAxesCountMinusOne - indexCountMinusOne + i;
					if (index < max) {
						index++;
						indexes[i] = index;
						i = indexCountMinusOne;
					} else {
						if (i > 0) {
							int newIndex = indexes[i - 1] + 2;
							if (newIndex < max) {
								indexes[i] = newIndex;
							}
						}
						i--;
					}
				}
			}
			if (otherAxes.length > 0) {
				StringBuilder otherAxesParams = new StringBuilder();
				otherAxesParams.append("int ").append(axisLabels[otherAxes[0]]);
				for (i = 1; i < otherAxes.length; i++) {
					otherAxesParams.append(", int ").append(axisLabels[otherAxes[i]]);
				}
				if (currentAxis == dimensionMinusOne) {
					System.out.println("@Override" + nl 
					+ "default int getMin" + currentAxisUpperCaseLabel + '(' + otherAxesParams 
					+ ") { return 0; }" + nl);
				} else {
					System.out.println("@Override" + nl 
					+ "default int getMin" + currentAxisUpperCaseLabel + '(' + otherAxesParams 
					+ ") { return " + axisLabels[currentAxis + 1] + "; }" + nl);
				}
				if (currentAxis == 0) {
					System.out.println("@Override" + nl 
					+ "default int getMax" + currentAxisUpperCaseLabel + '(' + otherAxesParams 
					+ ") { return getMax" + currentAxisUpperCaseLabel + "(); }" + nl);
				} else {
					System.out.println("@Override" + nl 
					+ "default int getMax" + currentAxisUpperCaseLabel + '(' + otherAxesParams 
					+ ") { return Math.min(" + axisLabels[currentAxis - 1] + ", getMax" + currentAxisUpperCaseLabel + "()); }" + nl);
				}	
			}
		}
	}
	
	static void printBoundsMethodsForIsotropicHypercubicGrid(int dimension) {
		if (dimension > 0) {
			String[] axisLabels = new String[dimension];
			String[] axisUpperCaseLabels = new String[dimension];
			for (int axis = 0; axis < dimension; axis++) {
				axisLabels[axis] = Utils.getAxisLabel(dimension, axis);
				axisUpperCaseLabels[axis] = Utils.getUpperCaseAxisLabel(dimension, axis);
			}
			int dimensionMinusOne = dimension - 1;
			String firstAxisUpperCaseLabel = axisUpperCaseLabels[0];
			for (int currentAxis = 0; currentAxis < dimension; currentAxis++) {
				String currentAxisUpperCaseLabel = axisUpperCaseLabels[currentAxis];
				System.out.println("@Override" + nl + "default int getMin" + currentAxisUpperCaseLabel 
						+ "() { return -getAsymmetricMax" + firstAxisUpperCaseLabel + "(); }" + nl);
				System.out.println("@Override" + nl + "default int getMax" + currentAxisUpperCaseLabel 
						+ "() { return getAsymmetricMax" + firstAxisUpperCaseLabel + "(); }" + nl);
				System.out.println("@Override" + nl + "default int getAsymmetricMin" + currentAxisUpperCaseLabel 
						+ "() { return 0; }" + nl);
				if (currentAxis != 0) {
					System.out.println("@Override" + nl + "default int getAsymmetricMax" + currentAxisUpperCaseLabel 
							+ "() { return getAsymmetricMax" + firstAxisUpperCaseLabel + "(); }" + nl);
				}
				int[] otherAxes = new int[dimensionMinusOne];
				int i = 0;
				for (; i < currentAxis; i++) {
					otherAxes[i] = i;
				}
				for (int j = currentAxis + 1; i < dimensionMinusOne; i = j, j++) {
					otherAxes[i] = j;
				}
				int otherAxesCountMinusOne = otherAxes.length - 1;
				for (int indexCount = 1, indexCountMinusOne = 0; indexCount < otherAxes.length; indexCountMinusOne = indexCount, indexCount++) {
					int[] indexes = new int[indexCount];
					for (i = 1; i < indexes.length; i++) {
						indexes[i] = i;
					}
					i = indexCountMinusOne;
					while (i > -1) {
						if (i == indexCountMinusOne) {
							boolean isThereAGreaterAxis = false;
							boolean isThereASmallerAxis = false;
							int smallestGreaterAxis = 0, greatestSmallerAxis = 0;
							int otherAxis = otherAxes[indexes[0]];
							if (otherAxis > currentAxis) {
								isThereAGreaterAxis = true;
								smallestGreaterAxis = otherAxis;
							} else {
								isThereASmallerAxis = true;
								greatestSmallerAxis = otherAxis;
							}
							StringBuilder otherAxesInMethodName = new StringBuilder();
							StringBuilder otherAxesParams = new StringBuilder();
							otherAxesInMethodName.append(axisUpperCaseLabels[otherAxis]);
							otherAxesParams.append("int ").append(axisLabels[otherAxis]);
							for (int j = 1; j < indexCount; j++) {
								otherAxis = otherAxes[indexes[j]];
								if (otherAxis > currentAxis) {
									if (!isThereAGreaterAxis) {
										isThereAGreaterAxis = true;
										smallestGreaterAxis = otherAxis;
									}
								} else {
									isThereASmallerAxis = true;
									greatestSmallerAxis = otherAxis;
								}
								otherAxesInMethodName.append(axisUpperCaseLabels[otherAxis]);
								otherAxesParams.append(", int ").append(axisLabels[otherAxis]);
							}
							if (isThereAGreaterAxis) {
								System.out.println("@Override" + nl 
								+ "default int getAsymmetricMin" + currentAxisUpperCaseLabel + "At" + otherAxesInMethodName + '(' + otherAxesParams 
								+ ") { return " + axisLabels[smallestGreaterAxis] + "; }" + nl);
							} else {
								System.out.println("@Override" + nl 
								+ "default int getAsymmetricMin" + currentAxisUpperCaseLabel + "At" + otherAxesInMethodName + '(' + otherAxesParams 
								+ ") { return 0; }" + nl);
							}
							if (isThereASmallerAxis) {
								System.out.println("@Override" + nl 
								+ "default int getAsymmetricMax" + currentAxisUpperCaseLabel + "At" + otherAxesInMethodName + '(' + otherAxesParams 
								+ ") { return " + axisLabels[greatestSmallerAxis] + "; }" + nl);
							} else {
								System.out.println("@Override" + nl 
								+ "default int getAsymmetricMax" + currentAxisUpperCaseLabel + "At" + otherAxesInMethodName + '(' + otherAxesParams 
								+ ") { return getAsymmetricMax" + firstAxisUpperCaseLabel + "(); }" + nl);
							}
						}
						int index = indexes[i];
						int max = otherAxesCountMinusOne - indexCountMinusOne + i;
						if (index < max) {
							index++;
							indexes[i] = index;
							i = indexCountMinusOne;
						} else {
							if (i > 0) {
								int newIndex = indexes[i - 1] + 2;
								if (newIndex < max) {
									indexes[i] = newIndex;
								}
							}
							i--;
						}
					}
				}
				if (otherAxes.length > 0) {
					StringBuilder otherAxesParams = new StringBuilder();
					otherAxesParams.append("int ").append(axisLabels[otherAxes[0]]);
					for (i = 1; i < otherAxes.length; i++) {
						otherAxesParams.append(", int ").append(axisLabels[otherAxes[i]]);
					}
					if (currentAxis == dimensionMinusOne) {
						System.out.println("@Override" + nl 
						+ "default int getAsymmetricMin" + currentAxisUpperCaseLabel + '(' + otherAxesParams 
						+ ") { return 0; }" + nl);
					} else {
						System.out.println("@Override" + nl 
						+ "default int getAsymmetricMin" + currentAxisUpperCaseLabel + '(' + otherAxesParams 
						+ ") { return " + axisLabels[currentAxis + 1] + "; }" + nl);
					}
					if (currentAxis == 0) {
						System.out.println("@Override" + nl 
						+ "default int getAsymmetricMax" + currentAxisUpperCaseLabel + '(' + otherAxesParams 
						+ ") { return getAsymmetricMax" + currentAxisUpperCaseLabel + "(); }" + nl);
					} else {
						System.out.println("@Override" + nl 
						+ "default int getAsymmetricMax" + currentAxisUpperCaseLabel + '(' + otherAxesParams 
						+ ") { return " + axisLabels[currentAxis - 1] + "; }" + nl);
					}	
				}
			}
		}
	}
	
	static void printBoundsMethodsForAsymmetricSection(int dimension) {
		String[] axisLabels = new String[dimension];
		String[] axisUpperCaseLabels = new String[dimension];
		for (int i = 0; i < dimension; i++) {
			axisLabels[i] = Utils.getAxisLabel(dimension, i);
			axisUpperCaseLabels[i] = Utils.getUpperCaseAxisLabel(dimension, i);
		}
		int dimensionMinusOne = dimension - 1;
		for (int i = 0; i < dimension; i++) {
			String currentAxisUpperCaseLabel = axisUpperCaseLabels[i];
			System.out.println("@Override" + nl + "public int getMin" + currentAxisUpperCaseLabel 
					+ "() { return source.getAsymmetricMin" + currentAxisUpperCaseLabel + "(); }" + nl);
			System.out.println("@Override" + nl + "public int getMax" + currentAxisUpperCaseLabel 
					+ "() { return source.getAsymmetricMax" + currentAxisUpperCaseLabel + "(); }" + nl);
			int[] otherAxes = new int[dimensionMinusOne];
			int j = 0;
			for (; j < i; j++) {
				otherAxes[j] = j;
			}
			for (int k = i + 1; j < dimensionMinusOne; j = k, k++) {
				otherAxes[j] = k;
			}
			int otherAxesCountMinusOne = otherAxes.length - 1;
			for (int indexCount = 1, indexCountMinusOne = 0; indexCount < otherAxes.length; indexCountMinusOne = indexCount, indexCount++) {
				int[] indexes = new int[indexCount];
				for (j = 1; j < indexes.length; j++) {
					indexes[j] = j;
				}
				j = indexCountMinusOne;
				while (j > -1) {
					if (j == indexCountMinusOne) {
						StringBuilder otherAxesInMethodName = new StringBuilder();
						StringBuilder otherAxesParams = new StringBuilder();
						StringBuilder otherAxesCsv = new StringBuilder();
						otherAxesInMethodName.append(axisUpperCaseLabels[otherAxes[indexes[0]]]);
						otherAxesParams.append("int ").append(axisLabels[otherAxes[indexes[0]]]);
						otherAxesCsv.append(axisLabels[otherAxes[indexes[0]]]);
						for (int k = 1; k < indexCount; k++) {
							otherAxesInMethodName.append(axisUpperCaseLabels[otherAxes[indexes[k]]]);
							otherAxesParams.append(", int ").append(axisLabels[otherAxes[indexes[k]]]);
							otherAxesCsv.append(", ").append(axisLabels[otherAxes[indexes[k]]]);
						}
						System.out.println("@Override" + nl 
						+ "public int getMin" + currentAxisUpperCaseLabel + "At" + otherAxesInMethodName + '(' + otherAxesParams 
						+ ") { return source.getAsymmetricMin" + currentAxisUpperCaseLabel + "At" + otherAxesInMethodName + '(' + otherAxesCsv + "); }" + nl);
						System.out.println("@Override" + nl 
						+ "public int getMax" + currentAxisUpperCaseLabel + "At" + otherAxesInMethodName + '(' + otherAxesParams 
						+ ") { return source.getAsymmetricMax" + currentAxisUpperCaseLabel + "At" + otherAxesInMethodName + '(' + otherAxesCsv + "); }" + nl);
					}
					int index = indexes[j];
					int max = otherAxesCountMinusOne - indexCountMinusOne + j;
					if (index < max) {
						index++;
						indexes[j] = index;
						j = indexCountMinusOne;
					} else {
						if (j > 0) {
							int newIndex = indexes[j - 1] + 2;
							if (newIndex < max) {
								indexes[j] = newIndex;
							}
						}
						j--;
					}
				}
			}
			if (otherAxes.length > 0) {
				StringBuilder otherAxesParams = new StringBuilder();
				StringBuilder otherAxesCsv = new StringBuilder();
				otherAxesParams.append("int ").append(axisLabels[otherAxes[0]]);
				otherAxesCsv.append(axisLabels[otherAxes[0]]);
				for (j = 1; j < otherAxes.length; j++) {
					otherAxesParams.append(", int ").append(axisLabels[otherAxes[j]]);
					otherAxesCsv.append(", ").append(axisLabels[otherAxes[j]]);
				}
				System.out.println("@Override" + nl 
				+ "public int getMin" + currentAxisUpperCaseLabel + '(' + otherAxesParams 
				+ ") { return source.getAsymmetricMin" + currentAxisUpperCaseLabel + '(' + otherAxesCsv + "); }" + nl);
				System.out.println("@Override" + nl 
				+ "public int getMax" + currentAxisUpperCaseLabel + '(' + otherAxesParams 
				+ ") { return source.getAsymmetricMax" + currentAxisUpperCaseLabel + '(' + otherAxesCsv + "); }" + nl);	
			}
		}
	}
	
	static void printBoundsMethodsForDecorator(int dimension) {
		String[] axisLabels = new String[dimension];
		String[] axisUpperCaseLabels = new String[dimension];
		for (int i = 0; i < dimension; i++) {
			axisLabels[i] = Utils.getAxisLabel(dimension, i);
			axisUpperCaseLabels[i] = Utils.getUpperCaseAxisLabel(dimension, i);
		}
		int dimensionMinusOne = dimension - 1;
		for (int i = 0; i < dimension; i++) {
			String currentAxisUpperCaseLabel = axisUpperCaseLabels[i];
			System.out.println("@Override" + nl + "public int getMin" + currentAxisUpperCaseLabel 
					+ "() { return source.getMin" + currentAxisUpperCaseLabel + "(); }" + nl);
			System.out.println("@Override" + nl + "public int getMax" + currentAxisUpperCaseLabel 
					+ "() { return source.getMax" + currentAxisUpperCaseLabel + "(); }" + nl);
			int[] otherAxes = new int[dimensionMinusOne];
			int j = 0;
			for (; j < i; j++) {
				otherAxes[j] = j;
			}
			for (int k = i + 1; j < dimensionMinusOne; j = k, k++) {
				otherAxes[j] = k;
			}
			int otherAxesCountMinusOne = otherAxes.length - 1;
			for (int indexCount = 1, indexCountMinusOne = 0; indexCount < otherAxes.length; indexCountMinusOne = indexCount, indexCount++) {
				int[] indexes = new int[indexCount];
				for (j = 1; j < indexes.length; j++) {
					indexes[j] = j;
				}
				j = indexCountMinusOne;
				while (j > -1) {
					if (j == indexCountMinusOne) {
						StringBuilder otherAxesInMethodName = new StringBuilder();
						StringBuilder otherAxesParams = new StringBuilder();
						StringBuilder otherAxesCsv = new StringBuilder();
						otherAxesInMethodName.append(axisUpperCaseLabels[otherAxes[indexes[0]]]);
						otherAxesParams.append("int ").append(axisLabels[otherAxes[indexes[0]]]);
						otherAxesCsv.append(axisLabels[otherAxes[indexes[0]]]);
						for (int k = 1; k < indexCount; k++) {
							otherAxesInMethodName.append(axisUpperCaseLabels[otherAxes[indexes[k]]]);
							otherAxesParams.append(", int ").append(axisLabels[otherAxes[indexes[k]]]);
							otherAxesCsv.append(", ").append(axisLabels[otherAxes[indexes[k]]]);
						}
						System.out.println("@Override" + nl 
						+ "public int getMin" + currentAxisUpperCaseLabel + "At" + otherAxesInMethodName + '(' + otherAxesParams 
						+ ") { return source.getMin" + currentAxisUpperCaseLabel + "At" + otherAxesInMethodName + '(' + otherAxesCsv + "); }" + nl);
						System.out.println("@Override" + nl 
						+ "public int getMax" + currentAxisUpperCaseLabel + "At" + otherAxesInMethodName + '(' + otherAxesParams 
						+ ") { return source.getMax" + currentAxisUpperCaseLabel + "At" + otherAxesInMethodName + '(' + otherAxesCsv + "); }" + nl);
					}
					int index = indexes[j];
					int max = otherAxesCountMinusOne - indexCountMinusOne + j;
					if (index < max) {
						index++;
						indexes[j] = index;
						j = indexCountMinusOne;
					} else {
						if (j > 0) {
							int newIndex = indexes[j - 1] + 2;
							if (newIndex < max) {
								indexes[j] = newIndex;
							}
						}
						j--;
					}
				}
			}
			if (otherAxes.length > 0) {
				StringBuilder otherAxesParams = new StringBuilder();
				StringBuilder otherAxesCsv = new StringBuilder();
				otherAxesParams.append("int ").append(axisLabels[otherAxes[0]]);
				otherAxesCsv.append(axisLabels[otherAxes[0]]);
				for (j = 1; j < otherAxes.length; j++) {
					otherAxesParams.append(", int ").append(axisLabels[otherAxes[j]]);
					otherAxesCsv.append(", ").append(axisLabels[otherAxes[j]]);
				}
				System.out.println("@Override" + nl 
				+ "public int getMin" + currentAxisUpperCaseLabel + '(' + otherAxesParams 
				+ ") { return source.getMin" + currentAxisUpperCaseLabel + '(' + otherAxesCsv + "); }" + nl);
				System.out.println("@Override" + nl 
				+ "public int getMax" + currentAxisUpperCaseLabel + '(' + otherAxesParams 
				+ ") { return source.getMax" + currentAxisUpperCaseLabel + '(' + otherAxesCsv + "); }" + nl);	
			}
		}
	}
	
	static void printBoundsMethodsToOverride(int dimension) {
		String[] axisLabels = new String[dimension];
		String[] axisUpperCaseLabels = new String[dimension];
		for (int i = 0; i < dimension; i++) {
			axisLabels[i] = Utils.getAxisLabel(dimension, i);
			axisUpperCaseLabels[i] = Utils.getUpperCaseAxisLabel(dimension, i);
		}
		int dimensionMinusOne = dimension - 1;
		for (int i = 0; i < dimension; i++) {
			String currentAxisUpperCaseLabel = axisUpperCaseLabels[i];
			System.out.println("@Override" + nl + "public int getMin" + currentAxisUpperCaseLabel 
					+ "() {" + nl + nl + '}' + nl);
			System.out.println("@Override" + nl + "public int getMax" + currentAxisUpperCaseLabel 
					+ "() {" + nl + nl + '}' + nl);
			int[] otherAxes = new int[dimensionMinusOne];
			int j = 0;
			for (; j < i; j++) {
				otherAxes[j] = j;
			}
			for (int k = i + 1; j < dimensionMinusOne; j = k, k++) {
				otherAxes[j] = k;
			}
			int otherAxesCountMinusOne = otherAxes.length - 1;
			for (int indexCount = 1, indexCountMinusOne = 0; indexCount < otherAxes.length; indexCountMinusOne = indexCount, indexCount++) {
				int[] indexes = new int[indexCount];
				for (j = 1; j < indexes.length; j++) {
					indexes[j] = j;
				}
				j = indexCountMinusOne;
				while (j > -1) {
					if (j == indexCountMinusOne) {
						StringBuilder otherAxesInMethodName = new StringBuilder();
						StringBuilder otherAxesParams = new StringBuilder();
						otherAxesInMethodName.append(axisUpperCaseLabels[otherAxes[indexes[0]]]);
						otherAxesParams.append("int ").append(axisLabels[otherAxes[indexes[0]]]);
						for (int k = 1; k < indexCount; k++) {
							otherAxesInMethodName.append(axisUpperCaseLabels[otherAxes[indexes[k]]]);
							otherAxesParams.append(", int ").append(axisLabels[otherAxes[indexes[k]]]);
						}
						System.out.println("@Override" + nl 
						+ "public int getMin" + currentAxisUpperCaseLabel + "At" + otherAxesInMethodName + '(' + otherAxesParams 
						+ ") {" + nl + nl + '}' + nl);
						System.out.println("@Override" + nl 
						+ "public int getMax" + currentAxisUpperCaseLabel + "At" + otherAxesInMethodName + '(' + otherAxesParams 
						+ ") {" + nl + nl + '}' + nl);
					}
					int index = indexes[j];
					int max = otherAxesCountMinusOne - indexCountMinusOne + j;
					if (index < max) {
						index++;
						indexes[j] = index;
						j = indexCountMinusOne;
					} else {
						if (j > 0) {
							int newIndex = indexes[j - 1] + 2;
							if (newIndex < max) {
								indexes[j] = newIndex;
							}
						}
						j--;
					}
				}
			}
			if (otherAxes.length > 0) {
				StringBuilder otherAxesParams = new StringBuilder();
				otherAxesParams.append("int ").append(axisLabels[otherAxes[0]]);
				for (j = 1; j < otherAxes.length; j++) {
					otherAxesParams.append(", int ").append(axisLabels[otherAxes[j]]);
				}
				System.out.println("@Override" + nl 
				+ "public int getMin" + currentAxisUpperCaseLabel + '(' + otherAxesParams 
				+ ") {" + nl + nl + '}' + nl);
				System.out.println("@Override" + nl 
				+ "public int getMax" + currentAxisUpperCaseLabel + '(' + otherAxesParams 
				+ ") {" + nl + nl + '}' + nl);	
			}
		}
	}
	
	static void printBoundsMethodsForSubsection(int dimension) {
		String[] axisLabels = new String[dimension];
		String[] axisUpperCaseLabels = new String[dimension];
		for (int i = 0; i < dimension; i++) {
			axisLabels[i] = Utils.getAxisLabel(dimension, i);
			axisUpperCaseLabels[i] = Utils.getUpperCaseAxisLabel(dimension, i);
		}
		int dimensionMinusOne = dimension - 1;
		for (int i = 0; i < dimension; i++) {
			String currentAxisUpperCaseLabel = axisUpperCaseLabels[i];
			System.out.println("@Override" + nl + "public int getMin" + currentAxisUpperCaseLabel 
					+ "() { return min" + currentAxisUpperCaseLabel + "; }" + nl);
			System.out.println("@Override" + nl + "public int getMax" + currentAxisUpperCaseLabel 
					+ "() { return max" + currentAxisUpperCaseLabel + "; }" + nl);
			int[] otherAxes = new int[dimensionMinusOne];
			int j = 0;
			for (; j < i; j++) {
				otherAxes[j] = j;
			}
			for (int k = i + 1; j < dimensionMinusOne; j = k, k++) {
				otherAxes[j] = k;
			}
			int otherAxesCountMinusOne = otherAxes.length - 1;
			for (int indexCount = 1, indexCountMinusOne = 0; indexCount < otherAxes.length; indexCountMinusOne = indexCount, indexCount++) {
				int[] indexes = new int[indexCount];
				for (j = 1; j < indexes.length; j++) {
					indexes[j] = j;
				}
				j = indexCountMinusOne;
				while (j > -1) {
					if (j == indexCountMinusOne) {
						StringBuilder otherAxesInMethodName = new StringBuilder();
						StringBuilder otherAxesParams = new StringBuilder();
						StringBuilder otherAxesCsv = new StringBuilder();
						otherAxesInMethodName.append(axisUpperCaseLabels[otherAxes[indexes[0]]]);
						otherAxesParams.append("int ").append(axisLabels[otherAxes[indexes[0]]]);
						otherAxesCsv.append(axisLabels[otherAxes[indexes[0]]]);
						for (int k = 1; k < indexCount; k++) {
							otherAxesInMethodName.append(axisUpperCaseLabels[otherAxes[indexes[k]]]);
							otherAxesParams.append(", int ").append(axisLabels[otherAxes[indexes[k]]]);
							otherAxesCsv.append(", ").append(axisLabels[otherAxes[indexes[k]]]);
						}
						System.out.println("@Override" + nl 
						+ "public int getMin" + currentAxisUpperCaseLabel + "At" + otherAxesInMethodName + '(' + otherAxesParams + ") { return Math.max(min" + currentAxisUpperCaseLabel 
						+ ", source.getMin" + currentAxisUpperCaseLabel + "At" + otherAxesInMethodName + '(' + otherAxesCsv + ")); }" + nl);
						System.out.println("@Override" + nl 
						+ "public int getMax" + currentAxisUpperCaseLabel + "At" + otherAxesInMethodName + '(' + otherAxesParams + ") { return Math.min(max" + currentAxisUpperCaseLabel 
						+ ", source.getMax" + currentAxisUpperCaseLabel + "At" + otherAxesInMethodName + '(' + otherAxesCsv + ")); }" + nl);
					}
					int index = indexes[j];
					int max = otherAxesCountMinusOne - indexCountMinusOne + j;
					if (index < max) {
						index++;
						indexes[j] = index;
						j = indexCountMinusOne;
					} else {
						if (j > 0) {
							int newIndex = indexes[j - 1] + 2;
							if (newIndex < max) {
								indexes[j] = newIndex;
							}
						}
						j--;
					}
				}
			}
			if (otherAxes.length > 0) {
				StringBuilder otherAxesParams = new StringBuilder();
				StringBuilder otherAxesCsv = new StringBuilder();
				otherAxesParams.append("int ").append(axisLabels[otherAxes[0]]);
				otherAxesCsv.append(axisLabels[otherAxes[0]]);
				for (j = 1; j < otherAxes.length; j++) {
					otherAxesParams.append(", int ").append(axisLabels[otherAxes[j]]);
					otherAxesCsv.append(", ").append(axisLabels[otherAxes[j]]);
				}
				System.out.println("@Override" + nl 
				+ "public int getMin" + currentAxisUpperCaseLabel + '(' + otherAxesParams + ") { return Math.max(min" + currentAxisUpperCaseLabel 
				+ ", source.getMin" + currentAxisUpperCaseLabel + '(' + otherAxesCsv + ")); }" + nl);
				System.out.println("@Override" + nl 
				+ "public int getMax" + currentAxisUpperCaseLabel + '(' + otherAxesParams + ") { return Math.min(max" + currentAxisUpperCaseLabel 
				+ ", source.getMax" + currentAxisUpperCaseLabel + '(' + otherAxesCsv + ")); }" + nl);		
			}
		}
	}
	
	static void printBoundsMethodsForModelAsND(int dimension) {
		String[] axisLabels = new String[dimension];
		String[] axisUpperCaseLabels = new String[dimension];
		String[] partialCoordinatesTemplate = new String[dimension];
		for (int i = 0; i < dimension; i++) {
			axisLabels[i] = Utils.getAxisLabel(dimension, i);
			axisUpperCaseLabels[i] = Utils.getUpperCaseAxisLabel(dimension, i);
			partialCoordinatesTemplate[i] = "null";
		}
		int dimensionMinusOne = dimension - 1;
		for (int i = 0; i < dimension; i++) {
			String currentAxisUpperCaseLabel = axisUpperCaseLabels[i];
			System.out.println("@Override" + nl + "public int getMin" + currentAxisUpperCaseLabel 
					+ "() {" + nl + indentation + "return source.getMinCoordinate(" + i + ");" + nl + '}' + nl);
			System.out.println("@Override" + nl + "public int getMax" + currentAxisUpperCaseLabel 
					+ "() {" + nl + indentation + "return source.getMaxCoordinate(" + i + ");" + nl + '}' + nl);
			int[] otherAxes = new int[dimensionMinusOne];
			int j = 0;
			for (; j < i; j++) {
				otherAxes[j] = j;
			}
			for (int k = i + 1; j < dimensionMinusOne; j = k, k++) {
				otherAxes[j] = k;
			}
			int otherAxesCountMinusOne = otherAxes.length - 1;
			for (int indexCount = 1, indexCountMinusOne = 0; indexCount < otherAxes.length; indexCountMinusOne = indexCount, indexCount++) {
				int[] indexes = new int[indexCount];
				for (j = 1; j < indexes.length; j++) {
					indexes[j] = j;
				}
				j = indexCountMinusOne;
				while (j > -1) {
					if (j == indexCountMinusOne) {
						StringBuilder otherAxesInMethodName = new StringBuilder();
						StringBuilder otherAxesParams = new StringBuilder();
						String[] partialCoordinates = partialCoordinatesTemplate.clone();
						int otherAxis = otherAxes[indexes[0]];
						otherAxesInMethodName.append(axisUpperCaseLabels[otherAxis]);
						String otherAxisLabel = axisLabels[otherAxis];
						otherAxesParams.append("int ").append(otherAxisLabel);
						partialCoordinates[otherAxis] = otherAxisLabel;
						for (int k = 1; k < indexCount; k++) {
							otherAxis = otherAxes[indexes[k]];
							otherAxesInMethodName.append(axisUpperCaseLabels[otherAxis]);
							otherAxisLabel = axisLabels[otherAxis];
							otherAxesParams.append(", int ").append(otherAxisLabel);
							partialCoordinates[otherAxis] = otherAxisLabel;
						}
						String partialCoordinatesCsv = String.join(", ", partialCoordinates);
						System.out.println("@Override" + nl 
						+ "public int getMin" + currentAxisUpperCaseLabel + "At" + otherAxesInMethodName + '(' + otherAxesParams 
						+ ") {" + nl + indentation + "return source.getMinCoordinate(" + i + ", new PartialCoordinates(" + partialCoordinatesCsv + "));" 
						+ nl + '}' + nl);
						System.out.println("@Override" + nl 
						+ "public int getMax" + currentAxisUpperCaseLabel + "At" + otherAxesInMethodName + '(' + otherAxesParams 
						+ ") {" + nl + indentation + "return source.getMaxCoordinate(" + i + ", new PartialCoordinates(" + partialCoordinatesCsv + "));" 
						+ nl + '}' + nl);
					}
					int index = indexes[j];
					int max = otherAxesCountMinusOne - indexCountMinusOne + j;
					if (index < max) {
						index++;
						indexes[j] = index;
						j = indexCountMinusOne;
					} else {
						if (j > 0) {
							int newIndex = indexes[j - 1] + 2;
							if (newIndex < max) {
								indexes[j] = newIndex;
							}
						}
						j--;
					}
				}
			}
			if (otherAxes.length > 0) {
				StringBuilder otherAxesParams = new StringBuilder();
				String[] partialCoordinates = partialCoordinatesTemplate.clone();
				int otherAxis = otherAxes[0];
				String otherAxisLabel = axisLabels[otherAxis];
				otherAxesParams.append("int ").append(otherAxisLabel);
				partialCoordinates[otherAxis] = otherAxisLabel;
				for (j = 1; j < otherAxes.length; j++) {
					otherAxis = otherAxes[j];
					otherAxisLabel = axisLabels[otherAxis];
					otherAxesParams.append(", int ").append(otherAxisLabel);
					partialCoordinates[otherAxis] = otherAxisLabel;
				}
				String partialCoordinatesCsv = String.join(", ", partialCoordinates);
				System.out.println("@Override" + nl 
				+ "public int getMin" + currentAxisUpperCaseLabel + '(' + otherAxesParams 
				+ ") {" + nl + indentation + "return source.getMinCoordinate(" + i + ", new PartialCoordinates(" + partialCoordinatesCsv + "));" 
				+ nl + '}' + nl);
				System.out.println("@Override" + nl 
				+ "public int getMax" + currentAxisUpperCaseLabel + '(' + otherAxesParams 
				+ ") {" + nl + indentation + "return source.getMaxCoordinate(" + i + ", new PartialCoordinates(" + partialCoordinatesCsv + "));" 
				+ nl + '}' + nl);	
			}
		}
	}
	
	static void printAsymmetricBoundsMethodsForInterface(int dimension) {
		String[] axisLabels = new String[dimension];
		String[] axisUpperCaseLabels = new String[dimension];
		for (int i = 0; i < dimension; i++) {
			axisLabels[i] = Utils.getAxisLabel(dimension, i);
			axisUpperCaseLabels[i] = Utils.getUpperCaseAxisLabel(dimension, i);
		}
		int dimensionMinusOne = dimension - 1;
		for (int i = 0; i < dimension; i++) {
			String currentAxisUpperCaseLabel = axisUpperCaseLabels[i];
			System.out.println("int getAsymmetricMin" + currentAxisUpperCaseLabel + "();" + nl);
			System.out.println("int getAsymmetricMax" + currentAxisUpperCaseLabel + "();" + nl);
			int[] otherAxes = new int[dimensionMinusOne];
			int j = 0;
			for (; j < i; j++) {
				otherAxes[j] = j;
			}
			for (int k = i + 1; j < dimensionMinusOne; j = k, k++) {
				otherAxes[j] = k;
			}
			int otherAxesCountMinusOne = otherAxes.length - 1;
			for (int indexCount = 1, indexCountMinusOne = 0; indexCount < otherAxes.length; indexCountMinusOne = indexCount, indexCount++) {
				int[] indexes = new int[indexCount];
				for (j = 1; j < indexes.length; j++) {
					indexes[j] = j;
				}
				j = indexCountMinusOne;
				while (j > -1) {
					if (j == indexCountMinusOne) {
						StringBuilder otherAxesInMethodName = new StringBuilder();
						StringBuilder otherAxesParams = new StringBuilder();
						otherAxesInMethodName.append(axisUpperCaseLabels[otherAxes[indexes[0]]]);
						otherAxesParams.append("int ").append(axisLabels[otherAxes[indexes[0]]]);
						for (int k = 1; k < indexCount; k++) {
							otherAxesInMethodName.append(axisUpperCaseLabels[otherAxes[indexes[k]]]);
							otherAxesParams.append(", int ").append(axisLabels[otherAxes[indexes[k]]]);
						}
						System.out.println("int getAsymmetricMin" + currentAxisUpperCaseLabel + "At" + otherAxesInMethodName + '(' + otherAxesParams + ");" + nl);
						System.out.println("int getAsymmetricMax" + currentAxisUpperCaseLabel + "At" + otherAxesInMethodName + '(' + otherAxesParams + ");" + nl);
					}
					int index = indexes[j];
					int max = otherAxesCountMinusOne - indexCountMinusOne + j;
					if (index < max) {
						index++;
						indexes[j] = index;
						j = indexCountMinusOne;
					} else {
						if (j > 0) {
							int newIndex = indexes[j - 1] + 2;
							if (newIndex < max) {
								indexes[j] = newIndex;
							}
						}
						j--;
					}
				}
			}
			if (otherAxes.length > 0) {
				StringBuilder otherAxesParams = new StringBuilder();
				otherAxesParams.append("int ").append(axisLabels[otherAxes[0]]);
				for (j = 1; j < otherAxes.length; j++) {
					otherAxesParams.append(", int ").append(axisLabels[otherAxes[j]]);
				}
				System.out.println("int getAsymmetricMin" + currentAxisUpperCaseLabel + '(' + otherAxesParams + ");" + nl);
				System.out.println("int getAsymmetricMax" + currentAxisUpperCaseLabel + '(' + otherAxesParams + ");" + nl);
			}
		}
	}
	
	static void printBoundsMethodsForInterface(int dimension) {
		String[] axisLabels = new String[dimension];
		String[] axisUpperCaseLabels = new String[dimension];
		for (int i = 0; i < dimension; i++) {
			axisLabels[i] = Utils.getAxisLabel(dimension, i);
			axisUpperCaseLabels[i] = Utils.getUpperCaseAxisLabel(dimension, i);
		}
		int dimensionMinusOne = dimension - 1;
		for (int i = 0; i < dimension; i++) {
			String currentAxisUpperCaseLabel = axisUpperCaseLabels[i];
			System.out.println("int getMin" + currentAxisUpperCaseLabel + "();" + nl);
			System.out.println("int getMax" + currentAxisUpperCaseLabel + "();" + nl);
			int[] otherAxes = new int[dimensionMinusOne];
			int j = 0;
			for (; j < i; j++) {
				otherAxes[j] = j;
			}
			for (int k = i + 1; j < dimensionMinusOne; j = k, k++) {
				otherAxes[j] = k;
			}
			int otherAxesCountMinusOne = otherAxes.length - 1;
			for (int indexCount = 1, indexCountMinusOne = 0; indexCount < otherAxes.length; indexCountMinusOne = indexCount, indexCount++) {
				int[] indexes = new int[indexCount];
				for (j = 1; j < indexes.length; j++) {
					indexes[j] = j;
				}
				j = indexCountMinusOne;
				while (j > -1) {
					if (j == indexCountMinusOne) {
						StringBuilder otherAxesInMethodName = new StringBuilder();
						StringBuilder otherAxesParams = new StringBuilder();
						otherAxesInMethodName.append(axisUpperCaseLabels[otherAxes[indexes[0]]]);
						otherAxesParams.append("int ").append(axisLabels[otherAxes[indexes[0]]]);
						for (int k = 1; k < indexCount; k++) {
							otherAxesInMethodName.append(axisUpperCaseLabels[otherAxes[indexes[k]]]);
							otherAxesParams.append(", int ").append(axisLabels[otherAxes[indexes[k]]]);
						}
						System.out.println("int getMin" + currentAxisUpperCaseLabel + "At" + otherAxesInMethodName + '(' + otherAxesParams + ");" + nl);
						System.out.println("int getMax" + currentAxisUpperCaseLabel + "At" + otherAxesInMethodName + '(' + otherAxesParams + ");" + nl);
					}
					int index = indexes[j];
					int max = otherAxesCountMinusOne - indexCountMinusOne + j;
					if (index < max) {
						index++;
						indexes[j] = index;
						j = indexCountMinusOne;
					} else {
						if (j > 0) {
							int newIndex = indexes[j - 1] + 2;
							if (newIndex < max) {
								indexes[j] = newIndex;
							}
						}
						j--;
					}
				}
			}
			if (otherAxes.length > 0) {
				StringBuilder otherAxesParams = new StringBuilder();
				otherAxesParams.append("int ").append(axisLabels[otherAxes[0]]);
				for (j = 1; j < otherAxes.length; j++) {
					otherAxesParams.append(", int ").append(axisLabels[otherAxes[j]]);
				}
				System.out.println("int getMin" + currentAxisUpperCaseLabel + '(' + otherAxesParams + ");" + nl);
				System.out.println("int getMax" + currentAxisUpperCaseLabel + '(' + otherAxesParams + ");" + nl);				
			}
		}
	}
	
	static void printBoundsMethodsForInterfaceWithDefault(int dimension) {
		String[] axisLabels = new String[dimension];
		String[] axisUpperCaseLabels = new String[dimension];
		for (int i = 0; i < dimension; i++) {
			axisLabels[i] = Utils.getAxisLabel(dimension, i);
			axisUpperCaseLabels[i] = Utils.getUpperCaseAxisLabel(dimension, i);
		}
		int dimensionMinusOne = dimension - 1;
		for (int i = 0; i < dimension; i++) {
			String currentAxisUpperCaseLabel = axisUpperCaseLabels[i];
			System.out.println("int getMin" + currentAxisUpperCaseLabel + "();" + nl);
			System.out.println("int getMax" + currentAxisUpperCaseLabel + "();" + nl);
			int[] otherAxes = new int[dimensionMinusOne];
			int j = 0;
			for (; j < i; j++) {
				otherAxes[j] = j;
			}
			for (int k = i + 1; j < dimensionMinusOne; j = k, k++) {
				otherAxes[j] = k;
			}
			int otherAxesCountMinusOne = otherAxes.length - 1;
			for (int indexCount = 1, indexCountMinusOne = 0; indexCount < otherAxes.length; indexCountMinusOne = indexCount, indexCount++) {
				int[] indexes = new int[indexCount];
				for (j = 1; j < indexes.length; j++) {
					indexes[j] = j;
				}
				j = indexCountMinusOne;
				while (j > -1) {
					if (j == indexCountMinusOne) {
						StringBuilder otherAxesInMethodName = new StringBuilder();
						StringBuilder otherAxesParams = new StringBuilder();
						otherAxesInMethodName.append(axisUpperCaseLabels[otherAxes[indexes[0]]]);
						otherAxesParams.append("int ").append(axisLabels[otherAxes[indexes[0]]]);
						for (int k = 1; k < indexCount; k++) {
							otherAxesInMethodName.append(axisUpperCaseLabels[otherAxes[indexes[k]]]);
							otherAxesParams.append(", int ").append(axisLabels[otherAxes[indexes[k]]]);
						}
						System.out.println("default int getMin" + currentAxisUpperCaseLabel + "At" + otherAxesInMethodName + '(' + otherAxesParams + ") { return getMin" + currentAxisUpperCaseLabel + "(); }" + nl);
						System.out.println("default int getMax" + currentAxisUpperCaseLabel + "At" + otherAxesInMethodName + '(' + otherAxesParams + ") { return getMax" + currentAxisUpperCaseLabel + "(); }" + nl);
					}
					int index = indexes[j];
					int max = otherAxesCountMinusOne - indexCountMinusOne + j;
					if (index < max) {
						index++;
						indexes[j] = index;
						j = indexCountMinusOne;
					} else {
						if (j > 0) {
							int newIndex = indexes[j - 1] + 2;
							if (newIndex < max) {
								indexes[j] = newIndex;
							}
						}
						j--;
					}
				}
			}
			if (otherAxes.length > 0) {
				StringBuilder otherAxesParams = new StringBuilder();
				otherAxesParams.append("int ").append(axisLabels[otherAxes[0]]);
				for (j = 1; j < otherAxes.length; j++) {
					otherAxesParams.append(", int ").append(axisLabels[otherAxes[j]]);
				}
				System.out.println("default int getMin" + currentAxisUpperCaseLabel + '(' + otherAxesParams + ") { return getMin" + currentAxisUpperCaseLabel + "(); }" + nl);
				System.out.println("default int getMax" + currentAxisUpperCaseLabel + '(' + otherAxesParams + ") { return getMax" + currentAxisUpperCaseLabel + "(); }" + nl);
			}
		}
	}
	
	static void printLabelMethodsForCrossSection(int dimension, int crossSectionAxis) {
		int crossSectionDimension = dimension - 1;
		int axis = 0;
		for (; axis != crossSectionAxis; axis++) {
			String sourceAxisLabel = Utils.getUpperCaseAxisLabel(dimension, axis);
			String crossSectionAxisLabel = Utils.getUpperCaseAxisLabel(crossSectionDimension, axis);
			System.out.println("@Override" + nl + "public String get" + crossSectionAxisLabel + "Label() {" + nl 
			+ "    return source.get" + sourceAxisLabel + "Label();" + nl + '}' + nl);
		}
		for (int sourceAxis = axis + 1; axis != crossSectionDimension; axis = sourceAxis, sourceAxis++) {
			String sourceAxisLabel = Utils.getUpperCaseAxisLabel(dimension, sourceAxis);
			String crossSectionAxisLabel = Utils.getUpperCaseAxisLabel(crossSectionDimension, axis);
			System.out.println("@Override" + nl + "public String get" + crossSectionAxisLabel + "Label() {" + nl 
			+ "    return source.get" + sourceAxisLabel + "Label();" + nl + '}' + nl);
		}
	}
	
	static void printBoundsMethodsForCrossSection(int dimension, int crossSectionAxis) {
		int crossSectionDimension = dimension - 1;
		String crossSectionAxisLabel = Utils.getAxisLabel(dimension, crossSectionAxis);
		String[] axisLabels = new String[crossSectionDimension];
		String[] sourceAxisUpperCaseLabels = new String[dimension];
		String[] axisUpperCaseLabels = new String[crossSectionDimension];
		for (int i = 0; i < crossSectionDimension; i++) {
			axisLabels[i] = Utils.getAxisLabel(crossSectionDimension, i);
			axisUpperCaseLabels[i] = Utils.getUpperCaseAxisLabel(crossSectionDimension, i);
			sourceAxisUpperCaseLabels[i] = Utils.getUpperCaseAxisLabel(dimension, i);
		}
		sourceAxisUpperCaseLabels[crossSectionDimension] = Utils.getUpperCaseAxisLabel(dimension, crossSectionDimension);
		int dimensionMinusOne = crossSectionDimension - 1;
		int[] emptyArray = new int[0];
		for (int i = 0; i < crossSectionDimension; i++) {
			String currentAxisUpperCaseLabel = axisUpperCaseLabels[i];
			System.out.println("@Override" + nl + "public int getMin" + currentAxisUpperCaseLabel 
					+ "() {" + nl + "    " 
					+ getBoundMethodContentForCrossSection(dimension, crossSectionAxis, i, emptyArray, true, 
							crossSectionAxisLabel, sourceAxisUpperCaseLabels, axisLabels) + nl + '}' + nl);
			System.out.println("@Override" + nl + "public int getMax" + currentAxisUpperCaseLabel 
					+ "() {" + nl + "    " 
					+ getBoundMethodContentForCrossSection(dimension, crossSectionAxis, i, emptyArray, false, 
							crossSectionAxisLabel, sourceAxisUpperCaseLabels, axisLabels) + nl + '}' + nl);
			int[] otherAxes = new int[dimensionMinusOne];
			int j = 0;
			for (; j < i; j++) {
				otherAxes[j] = j;
			}
			for (int k = i + 1; j < dimensionMinusOne; j = k, k++) {
				otherAxes[j] = k;
			}
			int otherAxesCountMinusOne = otherAxes.length - 1;
			for (int indexCount = 1, indexCountMinusOne = 0; indexCount < otherAxes.length; indexCountMinusOne = indexCount, indexCount++) {
				int[] indexes = new int[indexCount];
				for (j = 1; j < indexes.length; j++) {
					indexes[j] = j;
				}
				j = indexCountMinusOne;
				while (j > -1) {
					if (j == indexCountMinusOne) {
						int[] otherAxesUsed = new int[indexCount];
						StringBuilder otherAxesInMethodName = new StringBuilder();
						StringBuilder otherAxesParams = new StringBuilder();
						StringBuilder otherAxesCsv = new StringBuilder();
						int otherAxis = otherAxes[indexes[0]];
						otherAxesUsed[0] = otherAxis;
						otherAxesInMethodName.append(axisUpperCaseLabels[otherAxis]);
						otherAxesParams.append("int ").append(axisLabels[otherAxis]);
						otherAxesCsv.append(axisLabels[otherAxis]);
						for (int k = 1; k < indexCount; k++) {
							otherAxis = otherAxes[indexes[k]];
							otherAxesUsed[k] = otherAxis;
							otherAxesInMethodName.append(axisUpperCaseLabels[otherAxis]);
							otherAxesParams.append(", int ").append(axisLabels[otherAxis]);
							otherAxesCsv.append(", ").append(axisLabels[otherAxis]);
						}
						System.out.println("@Override" + nl 
						+ "public int getMin" + currentAxisUpperCaseLabel + "At" + otherAxesInMethodName + '(' + otherAxesParams 
						+ ") {" + nl + "    " 
						+ getBoundMethodContentForCrossSection(dimension, crossSectionAxis, i, otherAxesUsed, true, 
								crossSectionAxisLabel, sourceAxisUpperCaseLabels, axisLabels) + nl + '}' + nl);
						System.out.println("@Override" + nl 
						+ "public int getMax" + currentAxisUpperCaseLabel + "At" + otherAxesInMethodName + '(' + otherAxesParams 
						+ ") {" + nl + "    " 
						+ getBoundMethodContentForCrossSection(dimension, crossSectionAxis, i, otherAxesUsed, false, 
								crossSectionAxisLabel, sourceAxisUpperCaseLabels, axisLabels) + nl + '}' + nl);
					}
					int index = indexes[j];
					int max = otherAxesCountMinusOne - indexCountMinusOne + j;
					if (index < max) {
						index++;
						indexes[j] = index;
						j = indexCountMinusOne;
					} else {
						if (j > 0) {
							int newIndex = indexes[j - 1] + 2;
							if (newIndex < max) {
								indexes[j] = newIndex;
							}
						}
						j--;
					}
				}
			}
			if (otherAxes.length > 0) {
				StringBuilder otherAxesParams = new StringBuilder();
				StringBuilder otherAxesCsv = new StringBuilder();
				otherAxesParams.append("int ").append(axisLabels[otherAxes[0]]);
				otherAxesCsv.append(axisLabels[otherAxes[0]]);
				for (j = 1; j < otherAxes.length; j++) {
					otherAxesParams.append(", int ").append(axisLabels[otherAxes[j]]);
					otherAxesCsv.append(", ").append(axisLabels[otherAxes[j]]);
				}
				System.out.println("@Override" + nl 
				+ "public int getMin" + currentAxisUpperCaseLabel + '(' + otherAxesParams 
				+ ") {" + nl + "    " 
				+ getBoundMethodContentForCrossSection(dimension, crossSectionAxis, i, otherAxes, true, 
						crossSectionAxisLabel, sourceAxisUpperCaseLabels, axisLabels) + nl + '}' + nl);
				System.out.println("@Override" + nl 
				+ "public int getMax" + currentAxisUpperCaseLabel + '(' + otherAxesParams 
				+ ") {" + nl + "    " 
				+ getBoundMethodContentForCrossSection(dimension, crossSectionAxis, i, otherAxes, false, 
						crossSectionAxisLabel, sourceAxisUpperCaseLabels, axisLabels) + nl + '}' + nl);
			}
		}
	}
	
	static String getBoundMethodContentForCrossSection(int dimension, int crossSectionAxis, int boundAxis, int[] otherAxes, boolean minCoord, 
			String crossSectionAxisLabel, String[] sourceAxisUpperCaseLabels, String[] crossSectionAxisLabels) {
		int sourceBoundAxis = boundAxis < crossSectionAxis ? boundAxis : boundAxis + 1;
		int otherSourceAxesCount = otherAxes.length + 1;
		String[] otherAxesParams = new String[otherSourceAxesCount];
		if (otherAxes.length == dimension - 2) {
			int i = 0;
			for (; i != otherAxes.length && otherAxes[i] < crossSectionAxis; i++) {
				otherAxesParams[i] = crossSectionAxisLabels[otherAxes[i]];
			}
			otherAxesParams[i] = "this." + crossSectionAxisLabel;
			int j = i;
			i++;
			for (; i != otherSourceAxesCount; j = i, i++) {
				otherAxesParams[i] = crossSectionAxisLabels[otherAxes[j]];
			}
			return "return source.getM" + (minCoord? "in" : "ax") + sourceAxisUpperCaseLabels[sourceBoundAxis] + '(' + String.join(", ", otherAxesParams) + ");";
		} else {
			StringBuilder otherSourceAxesInMethodName = new StringBuilder();
			int i = 0;
			for (; i != otherAxes.length && otherAxes[i] < crossSectionAxis; i++) {
				otherSourceAxesInMethodName.append(sourceAxisUpperCaseLabels[otherAxes[i]]);
				otherAxesParams[i] = crossSectionAxisLabels[otherAxes[i]];
			}
			otherSourceAxesInMethodName.append(sourceAxisUpperCaseLabels[crossSectionAxis]);
			otherAxesParams[i] = "this." + crossSectionAxisLabel;
			int j = i;
			i++;
			for (; i != otherSourceAxesCount; j = i, i++) {
				otherSourceAxesInMethodName.append(sourceAxisUpperCaseLabels[otherAxes[j] + 1]);
				otherAxesParams[i] = crossSectionAxisLabels[otherAxes[j]];
			}
			return "return source.getM" + (minCoord? "in" : "ax") + sourceAxisUpperCaseLabels[sourceBoundAxis] + "At" + otherSourceAxesInMethodName + '(' + String.join(", ", otherAxesParams) + ");";
		}
	}
	
	static void validateDiagonalAxes(int firstAxis, int secondAxis) {
		if (firstAxis < 0) {
			throw new IllegalArgumentException("The axes cannot be smaller than 0");
		}
		if (firstAxis >= secondAxis) {
			throw new IllegalArgumentException("The second axis must be smaller than the first.");
		}
	}
	
	static void printDiagonalCrossSectionClassContent(int dimension, int firstAxis, int secondAxis) {
		validateDiagonalAxes(firstAxis, secondAxis);
		String offsetName =  Utils.getAxisLabel(dimension, secondAxis) + "OffsetFrom" + Utils.getUpperCaseAxisLabel(dimension, firstAxis);
		StringBuilder propsAndConstructor = new StringBuilder("protected Source_Type source;").append(nl).append("protected int slope;").append(nl).append("protected int ")
				.append(offsetName).append(';').append(nl);
		int axis = 0;
		for (; axis != secondAxis; axis++) {
			String axisLabel = Utils.getUpperCaseAxisLabel(dimension, axis);
			propsAndConstructor.append("protected int crossSectionMin").append(axisLabel).append(';').append(nl).append("protected int crossSectionMax").append(axisLabel)
			.append(';').append(nl);
		}
		for (axis++; axis != dimension; axis++) {
			String axisLabel = Utils.getUpperCaseAxisLabel(dimension, axis);
			propsAndConstructor.append("protected int crossSectionMin").append(axisLabel).append(';').append(nl).append("protected int crossSectionMax").append(axisLabel)
			.append(';').append(nl);
		}
		propsAndConstructor.append(nl).append("public Model").append(dimension).append('D').append(Utils.getUpperCaseAxisLabel(dimension, firstAxis))
			.append(Utils.getUpperCaseAxisLabel(dimension, secondAxis)).append("DiagonalCrossSection(Source_Type source, boolean positiveSlope, int ").append(offsetName)
			.append(") {").append(nl).append("    this.source = source;").append(nl).append("    this.slope = positiveSlope ? 1 : -1;").append(nl).append("    this.")
			.append(offsetName).append(" = ").append(offsetName).append(';').append(nl).append("    if (!getBounds()) {").append(nl)
			.append("        throw new IllegalArgumentException(\"The cross section is out of bounds.\");").append(nl).append("    }").append(nl).append('}').append(nl);
		System.out.println(propsAndConstructor);
		printLabelMethodsForDiagonalCrossSection(dimension, firstAxis, secondAxis);
		printGetBoundsMethodForDiagonalCrossSection(dimension, firstAxis, secondAxis);
		printBoundsMethodsForDiagonalCrossSection(dimension, firstAxis, secondAxis);
		System.out.println("@Override" + nl + "public Boolean nextStep() throws Exception {" + nl + "    Boolean changed = source.nextStep();" + nl + "    if (!getBounds()) {" 
				+ nl + "        throw new UnsupportedOperationException(\"The cross section is out of bounds.\");" + nl + "    }" + nl + "    return changed;" + nl + '}' + nl 
				+ nl + "@Override" + nl + "public Boolean isChanged() {" + nl + "    return source.isChanged();" + nl + '}' + nl + nl + "@Override" + nl + "public long getStep() {" 
				+ nl + "    return source.getStep();" + nl + '}' + nl + nl + "@Override" + nl + "public String getName() {" + nl + "    return source.getName();" + nl + '}' 
				+ nl + nl + "@Override" + nl + "public String getSubfolderPath() {" + nl + "    StringBuilder path = new StringBuilder(source.getSubfolderPath()).append(\"/\").append(source.get" 
				+ Utils.getUpperCaseAxisLabel(dimension, secondAxis) + "Label()).append(\"=\");" + nl + "    if (slope == -1) {" + nl + "        path.append(\"-\");" + nl 
				+ "    }" + nl + "    path.append(source.get" + Utils.getUpperCaseAxisLabel(dimension, firstAxis) + "Label());" + nl + "    if (" + offsetName + " < 0) {" + nl 
				+ "        path.append(" + offsetName + ");" + nl + "    } else if (" + offsetName + " > 0) {" + nl + "        path.append(\"+\").append(" + offsetName 
				+ ");" + nl + "    }" + nl + "    return path.toString();" + nl + '}' + nl + nl + "@Override" + nl 
				+ "public void backUp(String backupPath, String backupName) throws Exception {" + nl + "    source.backUp(backupPath, backupName);" 
				+ nl + '}' + nl);
	}
	
	static void printLabelMethodsForDiagonalCrossSection(int dimension, int firstAxis, int secondAxis) {
		validateDiagonalAxes(firstAxis, secondAxis);
		printLabelMethodsForCrossSection(dimension, secondAxis);
	}
	
	static void printGetBoundsMethodForDiagonalCrossSection(int dimension, int firstAxis, int secondAxis) {
		validateDiagonalAxes(firstAxis, secondAxis);
		String firstAxisLabel = Utils.getAxisLabel(dimension, firstAxis);
		String[] axisLabels = new String[dimension];
		for (int axis = 0; axis < dimension; axis++) {
			axisLabels[axis] = Utils.getUpperCaseAxisLabel(dimension, axis);
		}
		String maxVar = "max" + axisLabels[firstAxis];
		String secondAxisCoordVar = "crossSection" + axisLabels[secondAxis];
		String condition = firstAxisLabel + " <= " + maxVar; 
		String secondPartOfSecondAxisBoundMethod = axisLabels[secondAxis] + (dimension == 2 ? "" : "At" + axisLabels[firstAxis]) + '(' + firstAxisLabel + ')';
		String secondPartOfOtherBoundMethod = dimension == 2 ? null : 
			(dimension == 3 ? "" : "At" + axisLabels[firstAxis] + axisLabels[secondAxis]) + '(' + firstAxisLabel + ", " + secondAxisCoordVar + ");" + nl;
		StringBuilder result = new StringBuilder("protected boolean getBounds() {").append(nl).append("    int ").append(firstAxisLabel)
				.append(" = source.getMin").append(axisLabels[firstAxis]).append("();").append(nl).append("    int ").append(maxVar)
				.append(" = source.getMax").append(axisLabels[firstAxis]).append("();").append(nl).append("    int ").append(secondAxisCoordVar).append(" = slope*")
				.append(firstAxisLabel).append(" + ").append(Utils.getAxisLabel(dimension, secondAxis)).append("OffsetFrom").append(axisLabels[firstAxis])
				.append(';').append(nl).append("    while (").append(condition).append(" && (").append(secondAxisCoordVar).append(" < source.getMin")
				.append(secondPartOfSecondAxisBoundMethod).append(" || ").append(secondAxisCoordVar).append(" > source.getMax").append(secondPartOfSecondAxisBoundMethod)
				.append(")) {").append(nl).append("        ").append(firstAxisLabel).append("++;").append(nl).append("        ").append(secondAxisCoordVar)
				.append(" += slope;").append(nl).append("    }").append(nl).append("    if (").append(condition).append(") {").append(nl).append("        crossSectionMin")
				.append(axisLabels[firstAxis]).append(" = ").append(firstAxisLabel).append(';').append(nl).append("        crossSectionMax").append(axisLabels[firstAxis])
				.append(" = ").append(firstAxisLabel).append(';').append(nl);
		int axis = 0;
		for (; axis < firstAxis; axis++) {
			String axisLabel = axisLabels[axis];
			result.append("        crossSectionMin").append(axisLabel).append(" = source.getMin").append(axisLabel).append(secondPartOfOtherBoundMethod)
				.append("        crossSectionMax").append(axisLabel).append(" = source.getMax").append(axisLabel).append(secondPartOfOtherBoundMethod);
		}
		for (axis++; axis < secondAxis; axis++) {
			String axisLabel = axisLabels[axis];
			result.append("        crossSectionMin").append(axisLabel).append(" = source.getMin").append(axisLabel).append(secondPartOfOtherBoundMethod)
				.append("        crossSectionMax").append(axisLabel).append(" = source.getMax").append(axisLabel).append(secondPartOfOtherBoundMethod);
		}
		for (axis++; axis < dimension; axis++) {
			String axisLabel = axisLabels[axis];
			result.append("        crossSectionMin").append(axisLabel).append(" = source.getMin").append(axisLabel).append(secondPartOfOtherBoundMethod)
				.append("        crossSectionMax").append(axisLabel).append(" = source.getMax").append(axisLabel).append(secondPartOfOtherBoundMethod);
		}
		result.append("        ").append(firstAxisLabel).append("++;").append(nl).append("        ").append(secondAxisCoordVar).append(" += slope;").append(nl)
			.append("        while (").append(condition).append(" && ").append(secondAxisCoordVar).append(" >= source.getMin").append(secondPartOfSecondAxisBoundMethod)
			.append(" && ").append(secondAxisCoordVar).append(" <= source.getMax").append(secondPartOfSecondAxisBoundMethod).append(") {").append(nl)
			.append("            crossSectionMax").append(axisLabels[firstAxis]).append(" = ").append(firstAxisLabel).append(';').append(nl);
		axis = 0;
		for (; axis < firstAxis; axis++) {
			String axisLabel = axisLabels[axis];
			result.append("            int localMin").append(axisLabel).append(" = source.getMin").append(axisLabel).append(secondPartOfOtherBoundMethod)
				.append("            if (localMin").append(axisLabel).append(" < crossSectionMin").append(axisLabel).append(") {").append(nl)
				.append("                crossSectionMin").append(axisLabel).append(" = localMin").append(axisLabel).append(';').append(nl)
				.append("            }").append(nl)
				.append("            int localMax").append(axisLabel).append(" = source.getMax").append(axisLabel).append(secondPartOfOtherBoundMethod)
				.append("            if (localMax").append(axisLabel).append(" > crossSectionMax").append(axisLabel).append(") {").append(nl)
				.append("                crossSectionMax").append(axisLabel).append(" = localMax").append(axisLabel).append(';').append(nl)
				.append("            }").append(nl);
		}
		for (axis++; axis < secondAxis; axis++) {
			String axisLabel = axisLabels[axis];
			result.append("            int localMin").append(axisLabel).append(" = source.getMin").append(axisLabel).append(secondPartOfOtherBoundMethod)
				.append("            if (localMin").append(axisLabel).append(" < crossSectionMin").append(axisLabel).append(") {").append(nl)
				.append("                crossSectionMin").append(axisLabel).append(" = localMin").append(axisLabel).append(';').append(nl)
				.append("            }").append(nl)
				.append("            int localMax").append(axisLabel).append(" = source.getMax").append(axisLabel).append(secondPartOfOtherBoundMethod)
				.append("            if (localMax").append(axisLabel).append(" > crossSectionMax").append(axisLabel).append(") {").append(nl)
				.append("                crossSectionMax").append(axisLabel).append(" = localMax").append(axisLabel).append(';').append(nl)
				.append("            }").append(nl);
		}
		for (axis++; axis < dimension; axis++) {
			String axisLabel = axisLabels[axis];
			result.append("            int localMin").append(axisLabel).append(" = source.getMin").append(axisLabel).append(secondPartOfOtherBoundMethod)
				.append("            if (localMin").append(axisLabel).append(" < crossSectionMin").append(axisLabel).append(") {").append(nl)
				.append("                crossSectionMin").append(axisLabel).append(" = localMin").append(axisLabel).append(';').append(nl)
				.append("            }").append(nl)
				.append("            int localMax").append(axisLabel).append(" = source.getMax").append(axisLabel).append(secondPartOfOtherBoundMethod)
				.append("            if (localMax").append(axisLabel).append(" > crossSectionMax").append(axisLabel).append(") {").append(nl)
				.append("                crossSectionMax").append(axisLabel).append(" = localMax").append(axisLabel).append(';').append(nl)
				.append("            }").append(nl);
		}
		result.append("            ").append(firstAxisLabel).append("++;").append(nl).append("            ").append(secondAxisCoordVar).append(" += slope;")
		.append(nl).append("        }").append(nl).append("        return true;").append(nl).append("    } else {").append(nl).append("        return false;")
		.append(nl).append("    }").append(nl).append('}').append(nl);
		System.out.println(result);
	}

	static void printBoundsMethodsForDiagonalCrossSection(int dimension, int firstAxis, int secondAxis) {
		validateDiagonalAxes(firstAxis, secondAxis);
		String offsetLabel = Utils.getAxisLabel(dimension, secondAxis) + "OffsetFrom" + Utils.getUpperCaseAxisLabel(dimension, firstAxis);
		int crossSectionDimension = dimension - 1;
		String[] axisLabels = new String[crossSectionDimension];
		String[] sourceAxisUpperCaseLabels = new String[dimension];
		String[] axisUpperCaseLabels = new String[crossSectionDimension];
		for (int i = 0; i < crossSectionDimension; i++) {
			axisLabels[i] = Utils.getAxisLabel(crossSectionDimension, i);
			axisUpperCaseLabels[i] = Utils.getUpperCaseAxisLabel(crossSectionDimension, i);
			sourceAxisUpperCaseLabels[i] = Utils.getUpperCaseAxisLabel(dimension, i);
		}
		sourceAxisUpperCaseLabels[crossSectionDimension] = Utils.getUpperCaseAxisLabel(dimension, crossSectionDimension);
		int dimensionMinusOne = crossSectionDimension - 1;
		int[] emptyArray = new int[0];
		for (int i = 0; i < crossSectionDimension; i++) {
			String currentAxisUpperCaseLabel = axisUpperCaseLabels[i];
			System.out.println("@Override" + nl + "public int getMin" + currentAxisUpperCaseLabel 
					+ "() {" + nl
					+ getBoundMethodContentForDiagonalCrossSection(dimension, firstAxis, secondAxis, i, emptyArray, true, 
							offsetLabel, sourceAxisUpperCaseLabels, axisLabels) + nl + '}' + nl);
			System.out.println("@Override" + nl + "public int getMax" + currentAxisUpperCaseLabel 
					+ "() {" + nl
					+ getBoundMethodContentForDiagonalCrossSection(dimension, firstAxis, secondAxis, i, emptyArray, false, 
							offsetLabel, sourceAxisUpperCaseLabels, axisLabels) + nl + '}' + nl);
			int[] otherAxes = new int[dimensionMinusOne];
			int j = 0;
			for (; j < i; j++) {
				otherAxes[j] = j;
			}
			for (int k = i + 1; j < dimensionMinusOne; j = k, k++) {
				otherAxes[j] = k;
			}
			int otherAxesCountMinusOne = otherAxes.length - 1;
			for (int indexCount = 1, indexCountMinusOne = 0; indexCount < otherAxes.length; indexCountMinusOne = indexCount, indexCount++) {
				int[] indexes = new int[indexCount];
				for (j = 1; j < indexes.length; j++) {
					indexes[j] = j;
				}
				j = indexCountMinusOne;
				while (j > -1) {
					if (j == indexCountMinusOne) {
						int[] otherAxesUsed = new int[indexCount];
						StringBuilder otherAxesInMethodName = new StringBuilder();
						StringBuilder otherAxesParams = new StringBuilder();
						StringBuilder otherAxesCsv = new StringBuilder();
						int otherAxis = otherAxes[indexes[0]];
						otherAxesUsed[0] = otherAxis;
						otherAxesInMethodName.append(axisUpperCaseLabels[otherAxis]);
						otherAxesParams.append("int ").append(axisLabels[otherAxis]);
						otherAxesCsv.append(axisLabels[otherAxis]);
						for (int k = 1; k < indexCount; k++) {
							otherAxis = otherAxes[indexes[k]];
							otherAxesUsed[k] = otherAxis;
							otherAxesInMethodName.append(axisUpperCaseLabels[otherAxis]);
							otherAxesParams.append(", int ").append(axisLabels[otherAxis]);
							otherAxesCsv.append(", ").append(axisLabels[otherAxis]);
						}
						System.out.println("@Override" + nl 
						+ "public int getMin" + currentAxisUpperCaseLabel + "At" + otherAxesInMethodName + '(' + otherAxesParams 
						+ ") {" + nl
						+ getBoundMethodContentForDiagonalCrossSection(dimension, firstAxis, secondAxis, i, otherAxesUsed, true, 
								offsetLabel, sourceAxisUpperCaseLabels, axisLabels) + nl + '}' + nl);
						System.out.println("@Override" + nl 
						+ "public int getMax" + currentAxisUpperCaseLabel + "At" + otherAxesInMethodName + '(' + otherAxesParams 
						+ ") {" + nl
						+ getBoundMethodContentForDiagonalCrossSection(dimension, firstAxis, secondAxis, i, otherAxesUsed, false, 
								offsetLabel, sourceAxisUpperCaseLabels, axisLabels) + nl + '}' + nl);
					}
					int index = indexes[j];
					int max = otherAxesCountMinusOne - indexCountMinusOne + j;
					if (index < max) {
						index++;
						indexes[j] = index;
						j = indexCountMinusOne;
					} else {
						if (j > 0) {
							int newIndex = indexes[j - 1] + 2;
							if (newIndex < max) {
								indexes[j] = newIndex;
							}
						}
						j--;
					}
				}
			}
			if (otherAxes.length > 0) {
				StringBuilder otherAxesParams = new StringBuilder();
				StringBuilder otherAxesCsv = new StringBuilder();
				otherAxesParams.append("int ").append(axisLabels[otherAxes[0]]);
				otherAxesCsv.append(axisLabels[otherAxes[0]]);
				for (j = 1; j < otherAxes.length; j++) {
					otherAxesParams.append(", int ").append(axisLabels[otherAxes[j]]);
					otherAxesCsv.append(", ").append(axisLabels[otherAxes[j]]);
				}
				System.out.println("@Override" + nl 
				+ "public int getMin" + currentAxisUpperCaseLabel + '(' + otherAxesParams 
				+ ") {" + nl
				+ getBoundMethodContentForDiagonalCrossSection(dimension, firstAxis, secondAxis, i, otherAxes, true, 
						offsetLabel, sourceAxisUpperCaseLabels, axisLabels) + nl + '}' + nl);
				System.out.println("@Override" + nl 
				+ "public int getMax" + currentAxisUpperCaseLabel + '(' + otherAxesParams 
				+ ") {" + nl
				+ getBoundMethodContentForDiagonalCrossSection(dimension, firstAxis, secondAxis, i, otherAxes, false, 
						offsetLabel, sourceAxisUpperCaseLabels, axisLabels) + nl + '}' + nl);
			}
		}
	}
	
	static String getBoundMethodContentForDiagonalCrossSection(int dimension, int firstAxis, int secondAxis, int boundAxis, int[] otherAxes, boolean minCoord, 
			String offsetLabel, String[] sourceAxisUpperCaseLabels, String[] crossSectionAxisLabels) {
		int sourceBoundAxis = boundAxis < secondAxis ? boundAxis : boundAxis + 1;
		if (otherAxes.length == 0) {
			return "    return crossSectionM" + (minCoord? "in" : "ax") + sourceAxisUpperCaseLabels[sourceBoundAxis] + ';';
		} else if (Utils.contains(otherAxes, firstAxis)) {
			int otherSourceAxesCount = otherAxes.length + 1;
			String[] otherAxesParams = new String[otherSourceAxesCount];
			if (otherAxes.length == dimension - 2) {
				int i = 0;
				for (; i != otherAxes.length && otherAxes[i] < secondAxis; i++) {
					otherAxesParams[i] = crossSectionAxisLabels[otherAxes[i]];
				}
				otherAxesParams[i] = "slope*" + crossSectionAxisLabels[firstAxis] + " + " + offsetLabel;
				int j = i;
				i++;
				for (; i != otherSourceAxesCount; j = i, i++) {
					otherAxesParams[i] = crossSectionAxisLabels[otherAxes[j]];
				}
				return "    return source.getM" + (minCoord? "in" : "ax") + sourceAxisUpperCaseLabels[sourceBoundAxis] + '(' + String.join(", ", otherAxesParams) + ");";
			} else {
				StringBuilder otherSourceAxesInMethodName = new StringBuilder();
				int i = 0;
				for (; i != otherAxes.length && otherAxes[i] < secondAxis; i++) {
					otherSourceAxesInMethodName.append(sourceAxisUpperCaseLabels[otherAxes[i]]);
					otherAxesParams[i] = crossSectionAxisLabels[otherAxes[i]];
				}
				otherSourceAxesInMethodName.append(sourceAxisUpperCaseLabels[secondAxis]);
				otherAxesParams[i] = "slope*" + crossSectionAxisLabels[firstAxis] + " + " + offsetLabel;
				int j = i;
				i++;
				for (; i != otherSourceAxesCount; j = i, i++) {
					otherSourceAxesInMethodName.append(sourceAxisUpperCaseLabels[otherAxes[j] + 1]);
					otherAxesParams[i] = crossSectionAxisLabels[otherAxes[j]];
				}
				return "    return source.getM" + (minCoord? "in" : "ax") + sourceAxisUpperCaseLabels[sourceBoundAxis] + "At" + otherSourceAxesInMethodName + '(' + String.join(", ", otherAxesParams) + ");";
			}
		} else {
			String firstAxisLabel = sourceAxisUpperCaseLabels[firstAxis];
			String secondAxisLabel = sourceAxisUpperCaseLabels[secondAxis];
			if (boundAxis == firstAxis) {
				StringBuilder result = new StringBuilder("    for (int crossSection").append(firstAxisLabel).append(" = crossSectionM").append(minCoord? "in" : "ax").append(firstAxisLabel).append(", crossSection")
						.append(secondAxisLabel).append(" = slope*crossSection").append(firstAxisLabel).append(" + ").append(offsetLabel).append("; crossSection").append(firstAxisLabel).append(minCoord? " <= " : " >= ")
						.append("crossSectionM").append(minCoord? "ax" : "in").append(firstAxisLabel).append("; crossSection").append(firstAxisLabel).append(minCoord? "++" : "--").append(", crossSection")
						.append(secondAxisLabel).append(minCoord? " +" : " -").append("= slope) {").append(nl).append("        if (");
				int otherAxesLengthMinusOne = otherAxes.length - 1;
				SortedMap<Integer, String> sourceOtherAxesInMethodName = new TreeMap<Integer, String>();
				sourceOtherAxesInMethodName.put(firstAxis, firstAxisLabel);
				sourceOtherAxesInMethodName.put(secondAxis, secondAxisLabel);
				SortedMap<Integer, String>  axesParams = new TreeMap<Integer, String>();
				axesParams.put(firstAxis, "crossSection" + firstAxisLabel);
				axesParams.put(secondAxis, "crossSection" + secondAxisLabel);
				String separator = nl + "                && ";
				for (int i = 0; i < otherAxesLengthMinusOne; i++) {
					int otherAxis = otherAxes[i];
					int sourceOtherAxis = otherAxis < secondAxis ? otherAxis : otherAxis + 1;
					String otherAxisLabel = crossSectionAxisLabels[otherAxis];
					String sourceOtherAxisLabel = sourceAxisUpperCaseLabels[sourceOtherAxis];
					String secondPartOfMethodCall = sourceOtherAxisLabel + "At" + String.join("", sourceOtherAxesInMethodName.values()) + '(' + String.join(", ", axesParams.values()) + ')';
					result.append(otherAxisLabel).append(" >= source.getMin").append(secondPartOfMethodCall).append(separator).append(otherAxisLabel).append(" <= source.getMax").append(secondPartOfMethodCall)
					.append(separator);
					sourceOtherAxesInMethodName.put(sourceOtherAxis, sourceOtherAxisLabel);
					axesParams.put(sourceOtherAxis, otherAxisLabel);
				}
				int otherAxis = otherAxes[otherAxesLengthMinusOne];
				int sourceOtherAxis = otherAxis < secondAxis ? otherAxis : otherAxis + 1;
				String sourceOtherAxisLabel = sourceAxisUpperCaseLabels[sourceOtherAxis];
				StringBuilder secondPartOfMethodCall = new StringBuilder(sourceOtherAxisLabel);
				if (otherAxes.length != dimension - 2) {
					secondPartOfMethodCall.append("At").append(String.join("", sourceOtherAxesInMethodName.values()));		
				}
				secondPartOfMethodCall.append('(').append(String.join(", ", axesParams.values())).append(')');
				String otherAxisLabel = crossSectionAxisLabels[otherAxis];
				result.append(otherAxisLabel).append(" >= source.getMin").append(secondPartOfMethodCall).append(separator).append(otherAxisLabel)
				.append(" <= source.getMax").append(secondPartOfMethodCall).append(") {").append(nl).append("            return crossSection").append(firstAxisLabel).append(';')
				.append(nl).append("        }").append(nl).append("    }").append(nl)
				.append("    throw new IllegalArgumentException(\"The coordinate").append(otherAxes.length == 1? " is" : "s are").append(" out of bounds.\");");
				return result.toString();
			} else {
				int otherSourceAxesCount = otherAxes.length + 2;
				String[] otherAxesParams = new String[otherSourceAxesCount];
				String sourceBoundMethodCall;
				if (otherAxes.length == dimension - 3) {
					int i = 0;
					for (; i != otherAxes.length && otherAxes[i] < firstAxis; i++) {
						otherAxesParams[i] = crossSectionAxisLabels[otherAxes[i]];
					}
					otherAxesParams[i] = "crossSection" + firstAxisLabel;
					int j = i + 1;
					for (; i != otherAxes.length && otherAxes[i] < secondAxis; i = j, j++) {
						otherAxesParams[j] = crossSectionAxisLabels[otherAxes[i]];
					}
					otherAxesParams[j] = "crossSection" + secondAxisLabel;
					j++;
					for (; i != otherAxes.length; i++, j++) {
						otherAxesParams[j] = crossSectionAxisLabels[otherAxes[i]];
					}
					sourceBoundMethodCall = "source.getM" + (minCoord? "in" : "ax") + sourceAxisUpperCaseLabels[sourceBoundAxis] + '(' + String.join(", ", otherAxesParams) + ')';
				} else {
					StringBuilder otherSourceAxesInMethodName = new StringBuilder();					
					int i = 0;
					for (; i != otherAxes.length && otherAxes[i] < firstAxis; i++) {
						otherSourceAxesInMethodName.append(sourceAxisUpperCaseLabels[otherAxes[i]]);
						otherAxesParams[i] = crossSectionAxisLabels[otherAxes[i]];
					}
					otherSourceAxesInMethodName.append(firstAxisLabel);
					otherAxesParams[i] = "crossSection" + firstAxisLabel;
					int j = i + 1;
					for (; i != otherAxes.length && otherAxes[i] < secondAxis; i = j, j++) {
						otherSourceAxesInMethodName.append(sourceAxisUpperCaseLabels[otherAxes[i]]);
						otherAxesParams[j] = crossSectionAxisLabels[otherAxes[i]];
					}
					otherSourceAxesInMethodName.append(secondAxisLabel);
					otherAxesParams[j] = "crossSection" + secondAxisLabel;
					j++;
					for (; i != otherAxes.length; i++, j++) {
						otherSourceAxesInMethodName.append(sourceAxisUpperCaseLabels[otherAxes[i] + 1]);
						otherAxesParams[j] = crossSectionAxisLabels[otherAxes[i]];
					}					
					sourceBoundMethodCall = "source.getM" + (minCoord? "in" : "ax") + sourceAxisUpperCaseLabels[sourceBoundAxis] + "At" + otherSourceAxesInMethodName + '(' + String.join(", ", otherAxesParams) + ')';
				}
				String localBoundVar = "localM" + (minCoord? "in" : "ax") + sourceAxisUpperCaseLabels[sourceBoundAxis];
				String increments = "crossSection" + firstAxisLabel + "++, crossSection" + secondAxisLabel + " += slope";
				StringBuilder result = new StringBuilder("    int crossSection").append(firstAxisLabel).append(" = crossSectionMin").append(firstAxisLabel).append(';').append(nl)
						.append("    int crossSection").append(secondAxisLabel).append(" = slope*crossSection").append(firstAxisLabel).append(" + ").append(offsetLabel).append(';')
						.append(nl).append("    int result = ").append(sourceBoundMethodCall).append(';').append(nl).append("    int ").append(localBoundVar).append(';').append(nl)
						.append("    for (").append(increments).append(';').append(nl).append("            crossSection").append(firstAxisLabel).append(" <= crossSectionMax")
						.append(firstAxisLabel).append(" && (").append(localBoundVar).append(" = ").append(sourceBoundMethodCall).append(") ").append(minCoord? '<' : '>')
						.append("= result;").append(nl).append("            ").append(increments).append(") {").append(nl).append("        result = ").append(localBoundVar)
						.append(';').append(nl).append("    }").append(nl).append("    return result;");
				return result.toString();						 
			}
		}
	}
	
	static class TypeOfAnysotropicRegionVonNeumannNeighborWithSymmetries implements Equatable {
		int axisIndex;
		boolean isPositiveDirection;
		
		//not used in isSame on purpose
		Integer symmetryCount = 1;
		
		public TypeOfAnysotropicRegionVonNeumannNeighborWithSymmetries(int axisIndex, boolean isPositiveDirection) {
			this.axisIndex = axisIndex;
			this.isPositiveDirection = isPositiveDirection;
		}
		
		@Override
		public boolean isSame(Object other) {
			if (other == null || !(other instanceof TypeOfAnysotropicRegionVonNeumannNeighborWithSymmetries)) return false;
			TypeOfAnysotropicRegionVonNeumannNeighborWithSymmetries otherNeighbor = (TypeOfAnysotropicRegionVonNeumannNeighborWithSymmetries)other;
			return otherNeighbor.axisIndex == axisIndex && otherNeighbor.isPositiveDirection == isPositiveDirection;
		}
	}
	
	static class TypeOfAnysotropicRegionVonNeumannNeighborhoodWithSymmetries implements Equatable {
		boolean hasSymmetries = false;
		/** A list of neighbors ordered by axis and direction **/ 
		List<TypeOfAnysotropicRegionVonNeumannNeighborWithSymmetries> neighbors = new ArrayList<TypeOfAnysotropicRegionVonNeumannNeighborWithSymmetries>();
		
		//not used in isSame on purpose
		Integer[] coordinates;
		
		public TypeOfAnysotropicRegionVonNeumannNeighborhoodWithSymmetries(int[] coordinates) {
			this.coordinates = new Integer[coordinates.length];
			for (int i = 0; i < coordinates.length; i++) {
				this.coordinates[i] = coordinates[i];
			}
		}
		
		@Override
		public boolean isSame(Object other) {
			if (other == null || !(other instanceof TypeOfAnysotropicRegionVonNeumannNeighborhoodWithSymmetries)) return false;
			TypeOfAnysotropicRegionVonNeumannNeighborhoodWithSymmetries otherNeighborhood = (TypeOfAnysotropicRegionVonNeumannNeighborhoodWithSymmetries)other;
			int neighborCount = otherNeighborhood.neighbors.size();
			if (neighborCount != neighbors.size()) {
				return false;
			}
			for (int i = 0; i != neighborCount; i++) {
				if (!otherNeighborhood.neighbors.get(i).isSame(neighbors.get(i))) {
					return false;
				}
			}
			return otherNeighborhood.hasSymmetries == hasSymmetries;
		}
	}
	
	static void printAnisotropicRegionVonNeumannNeighborhoodsWithOutgoingSymmetries(int dimension, int size, boolean hideNeighborhood, boolean hideTypeB) {
		StringBuilder header = new StringBuilder();
		StringBuilder underline = new StringBuilder();
		header.append("  ").append(Utils.getAxisLabel(dimension, 0)).append(' ');
		underline.append("------------");
		if (!hideNeighborhood) {
			underline.append("---------------");
		}
		if (!hideTypeB) {
			underline.append("---------");
		}
		for (int coord = 1; coord < dimension; coord++) {
			header.append("|  ").append(Utils.getAxisLabel(dimension, coord)).append(' ');
			underline.append("-----");
		}
		if (!hideNeighborhood) {
			header.append("| Neighborhood ");
		}
		header.append("| Type A");
		if (!hideTypeB) {
			header.append(" | Type B");
		}
		header.append(nl).append(underline);
		System.out.println(header);
		List<String> neighborhoodTypesA = new ArrayList<String>();
		List<String> neighborhoodTypesB = new ArrayList<String>();
		int[] coordinates = new int[dimension];
		int sizeMinusOne = size - 1;
		int dimensionMinusOne = dimension - 1;
		int currentAxis = dimensionMinusOne;
		while (currentAxis > -1) {
			if (currentAxis == dimensionMinusOne) {
				printAnisotropicRegionVonNeumannNeighborhoodWithOutgoingSymmetries(coordinates, neighborhoodTypesA, neighborhoodTypesB, hideNeighborhood, hideTypeB);
			}
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
		System.out.println("Type A count: " + neighborhoodTypesA.size());
		if (!hideTypeB)
			System.out.println("Type B count: " + neighborhoodTypesB.size());
	}
	
	static TypeOfAnysotropicRegionVonNeumannNeighborhoodWithSymmetries getTypeOfAnisotropicRegionVonNeumannNeighborhoodWithOutgoingSymmetries(int[] coords) {
		TypeOfAnysotropicRegionVonNeumannNeighborhoodWithSymmetries type = new TypeOfAnysotropicRegionVonNeumannNeighborhoodWithSymmetries(coords);
		for (int axis = 0; axis < coords.length; axis++) {
			
			int[] greaterNeighborCoords = coords.clone();
			int[] smallerNeighborCoords = coords.clone();
			greaterNeighborCoords[axis]++;
			smallerNeighborCoords[axis]--;
			int greaterNeighborOutgoingSymmetries = 1;
			int smallerNeighborOutgoingSymmetries = 1;
			
			if (!isOutsideAnisotropicSection(greaterNeighborCoords)) {
				greaterNeighborOutgoingSymmetries += getSymmetricNeighborsCount(greaterNeighborCoords, coords);
				TypeOfAnysotropicRegionVonNeumannNeighborWithSymmetries neighbor = new TypeOfAnysotropicRegionVonNeumannNeighborWithSymmetries(axis, true);
				if (greaterNeighborOutgoingSymmetries > 1) {
					type.hasSymmetries = true;
					neighbor.symmetryCount = greaterNeighborOutgoingSymmetries;
				}
				type.neighbors.add(neighbor);
			}
			if (!isOutsideAnisotropicSection(smallerNeighborCoords)) {
				smallerNeighborOutgoingSymmetries += getSymmetricNeighborsCount(smallerNeighborCoords, coords);
				TypeOfAnysotropicRegionVonNeumannNeighborWithSymmetries neighbor = new TypeOfAnysotropicRegionVonNeumannNeighborWithSymmetries(axis, false);
				if (smallerNeighborOutgoingSymmetries > 1) {
					type.hasSymmetries = true;
					neighbor.symmetryCount = smallerNeighborOutgoingSymmetries;
				}
				type.neighbors.add(neighbor);
			}
		}
		return type;
	}
	
	static void generalize(TypeOfAnysotropicRegionVonNeumannNeighborhoodWithSymmetries generalType, TypeOfAnysotropicRegionVonNeumannNeighborhoodWithSymmetries type) {
		for (int coord = 0; coord < type.coordinates.length; coord++) {
			if (generalType.coordinates[coord] != type.coordinates[coord]) {
				generalType.coordinates[coord] = null;//it doesn't repeat itself so set it to null
			}
		}
		if (type.hasSymmetries) {
			int neighborsSize = type.neighbors.size();
			for (int i = 0; i < neighborsSize; i++) {
				TypeOfAnysotropicRegionVonNeumannNeighborWithSymmetries neighbor = type.neighbors.get(i);
				TypeOfAnysotropicRegionVonNeumannNeighborWithSymmetries generalNeighbor = generalType.neighbors.get(i);
				if (generalNeighbor.symmetryCount != neighbor.symmetryCount) {
					generalNeighbor.symmetryCount = null;//it doesn't repeat itself so set it to null
				}
			}			
		}
	}
	
	static void printAnisotropicRegionVonNeumannNeighborhoodWithOutgoingSymmetries(int[] coords, List<String> neighborhoodTypesA, List<String> neighborhoodTypesB, 
			boolean hideNeighborhood, boolean hideTypeB) {
		List<String> neighbors = new ArrayList<String>();
		List<String> plainNeighbors = new ArrayList<String>();
		String[] strCoords = new String[coords.length];
		boolean hasOutgoingSymmetries = false;
		for (int axis = 0; axis < coords.length; axis++) {
			String coordLabel = Utils.getAxisLabel(coords.length, axis);
			strCoords[axis] = coords[axis] > 9 ? Integer.toString(coords[axis]) : "0" + coords[axis];
			
			int[] greaterNeighborCoords = coords.clone();
			int[] smallerNeighborCoords = coords.clone();
			greaterNeighborCoords[axis]++;
			smallerNeighborCoords[axis]--;
			int greaterNeighborOutgoingSymmetries = 1;
			int smallerNeighborOutgoingSymmetries = 1;
			
			int[] nc = greaterNeighborCoords;
			if (!isOutsideAnisotropicSection(nc)) {
				greaterNeighborOutgoingSymmetries += getSymmetricNeighborsCount(nc, coords);
				String neighbor = 'G' + coordLabel.toUpperCase();
				String plainNeighbor = neighbor;
				if (greaterNeighborOutgoingSymmetries > 1) {
					hasOutgoingSymmetries = true;
					neighbor = neighbor + '(' + greaterNeighborOutgoingSymmetries + ')';
				}
				neighbors.add(neighbor);
				plainNeighbors.add(plainNeighbor);
			}
			nc = smallerNeighborCoords;
			if (!isOutsideAnisotropicSection(nc)) {
				smallerNeighborOutgoingSymmetries += getSymmetricNeighborsCount(nc, coords);
				String neighbor = 'S' + coordLabel.toUpperCase();
				String plainNeighbor = neighbor;
				if (smallerNeighborOutgoingSymmetries > 1) {
					hasOutgoingSymmetries = true;
					neighbor = neighbor + '(' + smallerNeighborOutgoingSymmetries + ')';
				}
				neighbors.add(neighbor);
				plainNeighbors.add(plainNeighbor);
			}
		}
		String neighborhood = String.join(", ", neighbors);		
		int typeA = neighborhoodTypesA.indexOf(neighborhood);
		if (typeA == -1) {
			typeA = neighborhoodTypesA.size();
			neighborhoodTypesA.add(neighborhood);
		}
		typeA++;
		
		String plainNeighborhood = String.join(", ", plainNeighbors);
		plainNeighborhood += " " + hasOutgoingSymmetries;
		int typeB = neighborhoodTypesB.indexOf(plainNeighborhood);
		if (typeB == -1) {
			typeB = neighborhoodTypesB.size();
			neighborhoodTypesB.add(plainNeighborhood);
		}
		typeB++;
		if (hideNeighborhood) {
			neighborhood = "";
		} else {
			neighborhood = " | " + neighborhood;
		}
		String strTypeB = " | " + (typeB > 9 ? typeB : "0" + typeB);
		if (hideTypeB) {
			strTypeB = "";
		}
		System.out.println(' ' + String.join(" | ", strCoords) + neighborhood + " | " + (typeA > 9 ? typeA : "0" + typeA) + strTypeB);
	}
	
	static class TypeOfAnysotropicRegionVonNeumannNeighborWithBidirectionalSymmetries implements Equatable {
		int axisIndex;
		boolean isPositiveDirection;
		
		//not used in isSame on purpose
		Integer incomingSymmetryCount = 1;
		Integer outgoingSymmetryCount = 1;
		
		public TypeOfAnysotropicRegionVonNeumannNeighborWithBidirectionalSymmetries(int axisIndex, boolean isPositiveDirection) {
			this.axisIndex = axisIndex;
			this.isPositiveDirection = isPositiveDirection;
		}
		
		@Override
		public boolean isSame(Object other) {
			if (other == null || !(other instanceof TypeOfAnysotropicRegionVonNeumannNeighborWithBidirectionalSymmetries)) return false;
			TypeOfAnysotropicRegionVonNeumannNeighborWithBidirectionalSymmetries otherNeighbor = (TypeOfAnysotropicRegionVonNeumannNeighborWithBidirectionalSymmetries)other;
			return otherNeighbor.axisIndex == axisIndex && otherNeighbor.isPositiveDirection == isPositiveDirection;
		}
	}
	
	static class TypeOfAnysotropicRegionVonNeumannNeighborhoodWithBidirectionalSymmetries implements Equatable {
		boolean hasIncomingSymmetries = false;
		boolean hasOutgoingSymmetries = false;
		List<TypeOfAnysotropicRegionVonNeumannNeighborWithBidirectionalSymmetries> neighbors = new ArrayList<TypeOfAnysotropicRegionVonNeumannNeighborWithBidirectionalSymmetries>();
		
		//not used in isSame on purpose
		Integer[] coordinates;
		
		public TypeOfAnysotropicRegionVonNeumannNeighborhoodWithBidirectionalSymmetries(int[] coordinates) {
			this.coordinates = new Integer[coordinates.length];
			for (int i = 0; i < coordinates.length; i++) {
				this.coordinates[i] = coordinates[i];
			}
		}
		
		@Override
		public boolean isSame(Object other) {
			if (other == null || !(other instanceof TypeOfAnysotropicRegionVonNeumannNeighborhoodWithBidirectionalSymmetries)) return false;
			TypeOfAnysotropicRegionVonNeumannNeighborhoodWithBidirectionalSymmetries otherNeighborhood = (TypeOfAnysotropicRegionVonNeumannNeighborhoodWithBidirectionalSymmetries)other;
			int neighborCount = otherNeighborhood.neighbors.size();
			if (neighborCount != neighbors.size()) {
				return false;
			}
			for (int i = 0; i != neighborCount; i++) {
				if (!otherNeighborhood.neighbors.get(i).isSame(neighbors.get(i))) {
					return false;
				}
			}
			return otherNeighborhood.hasOutgoingSymmetries == hasOutgoingSymmetries && otherNeighborhood.hasIncomingSymmetries == hasIncomingSymmetries;
		}
	}
	
	static TypeOfAnysotropicRegionVonNeumannNeighborhoodWithBidirectionalSymmetries getTypeOfAnisotropicRegionVonNeumannNeighborhoodWithBidirectionalSymmetries(int[] coords) {
		TypeOfAnysotropicRegionVonNeumannNeighborhoodWithBidirectionalSymmetries type = new TypeOfAnysotropicRegionVonNeumannNeighborhoodWithBidirectionalSymmetries(coords);
		for (int axis = 0; axis < coords.length; axis++) {
			
			int[] greaterNeighborCoords = coords.clone();
			int[] smallerNeighborCoords = coords.clone();
			greaterNeighborCoords[axis]++;
			smallerNeighborCoords[axis]--;
			int greaterNeighborIncomingSymmetries = 1;
			int smallerNeighborIncomingSymmetries = 1;
			int greaterNeighborOutgoingSymmetries = 1;
			int smallerNeighborOutgoingSymmetries = 1;
			
			if (!isOutsideAnisotropicSection(greaterNeighborCoords)) {
				greaterNeighborOutgoingSymmetries += getSymmetricNeighborsCount(greaterNeighborCoords, coords);
				greaterNeighborIncomingSymmetries += getSymmetricNeighborsCount(coords, greaterNeighborCoords);
				TypeOfAnysotropicRegionVonNeumannNeighborWithBidirectionalSymmetries neighbor = new TypeOfAnysotropicRegionVonNeumannNeighborWithBidirectionalSymmetries(axis, true);
				if (greaterNeighborOutgoingSymmetries > 1) {
					type.hasOutgoingSymmetries = true;
					neighbor.outgoingSymmetryCount = greaterNeighborOutgoingSymmetries;
				}
				if (greaterNeighborIncomingSymmetries > 1) {
					type.hasIncomingSymmetries = true;
					neighbor.incomingSymmetryCount = greaterNeighborIncomingSymmetries;
				}
				type.neighbors.add(neighbor);
			}
			if (!isOutsideAnisotropicSection(smallerNeighborCoords)) {
				smallerNeighborOutgoingSymmetries += getSymmetricNeighborsCount(smallerNeighborCoords, coords);
				smallerNeighborIncomingSymmetries += getSymmetricNeighborsCount(coords, smallerNeighborCoords);
				TypeOfAnysotropicRegionVonNeumannNeighborWithBidirectionalSymmetries neighbor = new TypeOfAnysotropicRegionVonNeumannNeighborWithBidirectionalSymmetries(axis, false);
				if (smallerNeighborOutgoingSymmetries > 1) {
					type.hasOutgoingSymmetries = true;
					neighbor.outgoingSymmetryCount = smallerNeighborOutgoingSymmetries;
				}
				if (smallerNeighborIncomingSymmetries > 1) {
					type.hasIncomingSymmetries = true;
					neighbor.incomingSymmetryCount = smallerNeighborIncomingSymmetries;
				}
				type.neighbors.add(neighbor);
			}
		}
		return type;
	}
	
	static void generalize(TypeOfAnysotropicRegionVonNeumannNeighborhoodWithBidirectionalSymmetries generalType, TypeOfAnysotropicRegionVonNeumannNeighborhoodWithBidirectionalSymmetries type) {
		for (int coord = 0; coord < type.coordinates.length; coord++) {
			if (generalType.coordinates[coord] != type.coordinates[coord]) {
				generalType.coordinates[coord] = null;//it doesn't repeat itself so set it to null
			}
		}
		if (type.hasOutgoingSymmetries || type.hasIncomingSymmetries) {
			int neighborsSize = type.neighbors.size();
			for (int i = 0; i < neighborsSize; i++) {
				TypeOfAnysotropicRegionVonNeumannNeighborWithBidirectionalSymmetries neighbor = type.neighbors.get(i);
				TypeOfAnysotropicRegionVonNeumannNeighborWithBidirectionalSymmetries generalNeighbor = generalType.neighbors.get(i);					
				if (generalNeighbor.outgoingSymmetryCount != neighbor.outgoingSymmetryCount) {
//					System.out.println("It does enter here though");
					generalNeighbor.outgoingSymmetryCount = null;//it doesn't repeat itself so set it to null
				}
				if (generalNeighbor.incomingSymmetryCount != neighbor.incomingSymmetryCount) {
					System.out.println("Does it never enter here?");
					generalNeighbor.incomingSymmetryCount = null;//it doesn't repeat itself so set it to null
				}
			}			
		}
	}
	
	static void printAnisotropicRegionVonNeumannNeighborhoodWithBidirectionalSymmetries(int[] coords, boolean hideNeighborhood, boolean hideTypeB) {
		int dimension = coords.length;
		int maxCoord = 0;
		for (int axis = 0; axis < coords.length; axis++) {
			int coord = coords[axis]; 
			if (coord > maxCoord) {
				maxCoord = coord;
			}
		}
		int size = maxCoord + 1;
		StringBuilder header = new StringBuilder();
		StringBuilder underline = new StringBuilder();
		header.append("  ").append(Utils.getAxisLabel(dimension, 0)).append(' ');
		underline.append("------------");
		if (!hideNeighborhood) {
			underline.append("---------------");
		}
		if (!hideTypeB) {
			underline.append("---------");
		}
		for (int coord = 1; coord < dimension; coord++) {
			header.append("|  ").append(Utils.getAxisLabel(dimension, coord)).append(' ');
			underline.append("-----");
		}
		if (!hideNeighborhood) {
			header.append("| Neighborhood ");
		}
		header.append("| Type A");
		if (!hideTypeB) {
			header.append(" | Type B");
		}
		header.append(nl).append(underline);
		List<String> neighborhoodTypesA = new ArrayList<String>();
		List<String> neighborhoodTypesB = new ArrayList<String>();
		int[] coordinates = new int[dimension];
		int sizeMinusOne = size - 1;
		int dimensionMinusOne = dimension - 1;
		int currentAxis = dimensionMinusOne;
		boolean printed = false;
		while (currentAxis > -1 && !printed) {
			if (currentAxis == dimensionMinusOne) {
				if (Arrays.equals(coordinates, coords)) {
					System.out.println(header);
					printAnisotropicRegionVonNeumannNeighborhoodWithBidirectionalSymmetries(coordinates, neighborhoodTypesA, neighborhoodTypesB, hideNeighborhood, hideTypeB);
					printed = true;
				} else {
					getTypesOfAnisotropicRegionVonNeumannNeighborhoodWithBidirectionalSymmetries(coordinates, neighborhoodTypesA, neighborhoodTypesB);
				}
			}
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
	
	static void getTypesOfAnisotropicRegionVonNeumannNeighborhoodWithBidirectionalSymmetries(int[] coords, List<String> neighborhoodTypesA, List<String> neighborhoodTypesB) {
		List<String> neighbors = new ArrayList<String>();
		List<String> plainNeighbors = new ArrayList<String>();
		boolean hasIncomingSymmetries = false;
		boolean hasOutgoingSymmetries = false;
		for (int axis = 0; axis < coords.length; axis++) {
			String coordLabel = Utils.getAxisLabel(coords.length, axis);			
			int[] greaterNeighborCoords = coords.clone();
			int[] smallerNeighborCoords = coords.clone();
			greaterNeighborCoords[axis]++;
			smallerNeighborCoords[axis]--;
			int greaterNeighborIncomingSymmetries = 1;
			int smallerNeighborIncomingSymmetries = 1;
			int greaterNeighborOutgoingSymmetries = 1;
			int smallerNeighborOutgoingSymmetries = 1;
			
			int[] nc = greaterNeighborCoords;
			if (isOutsideAnisotropicSection(nc)) {
				greaterNeighborIncomingSymmetries = 0;
			} else {
				greaterNeighborOutgoingSymmetries += getSymmetricNeighborsCount(nc, coords);
				greaterNeighborIncomingSymmetries += getSymmetricNeighborsCount(coords, nc);
			}
			nc = smallerNeighborCoords;
			if (isOutsideAnisotropicSection(nc)) {
				smallerNeighborIncomingSymmetries = 0;
			} else {
				smallerNeighborOutgoingSymmetries += getSymmetricNeighborsCount(nc, coords);
				smallerNeighborIncomingSymmetries += getSymmetricNeighborsCount(coords, nc);
			}
			
			if (greaterNeighborIncomingSymmetries > 0) {
				String neighbor = 'G' + coordLabel.toUpperCase();
				String plainNeighbor = neighbor;
				if (greaterNeighborOutgoingSymmetries > 1) {
					hasOutgoingSymmetries = true;
					neighbor = neighbor + '(' + greaterNeighborOutgoingSymmetries + ')';
				}
				if (greaterNeighborIncomingSymmetries > 1) {
					hasIncomingSymmetries = true;
					neighbor = greaterNeighborIncomingSymmetries + '*' + neighbor;
				}
				neighbors.add(neighbor);
				plainNeighbors.add(plainNeighbor);
			}
			if (smallerNeighborIncomingSymmetries > 0) {
				String neighbor = 'S' + coordLabel.toUpperCase();
				String plainNeighbor = neighbor;
				if (smallerNeighborOutgoingSymmetries > 1) {
					hasOutgoingSymmetries = true;
					neighbor = neighbor + '(' + smallerNeighborOutgoingSymmetries + ')';
				}
				if (smallerNeighborIncomingSymmetries > 1) {
					hasIncomingSymmetries = true;
					neighbor = smallerNeighborIncomingSymmetries + '*' + neighbor;
				}
				neighbors.add(neighbor);
				plainNeighbors.add(plainNeighbor);
			}
		}
		String neighborhood = String.join(", ", neighbors);		
		int typeA = neighborhoodTypesA.indexOf(neighborhood);
		if (typeA == -1) {
			typeA = neighborhoodTypesA.size();
			neighborhoodTypesA.add(neighborhood);
		}		
		String plainNeighborhood = String.join(", ", plainNeighbors);
		plainNeighborhood += " " + hasIncomingSymmetries + hasOutgoingSymmetries;
		int typeB = neighborhoodTypesB.indexOf(plainNeighborhood);
		if (typeB == -1) {
			typeB = neighborhoodTypesB.size();
			neighborhoodTypesB.add(plainNeighborhood);
		}
	}
	
	static void printAnisotropicRegionVonNeumannNeighborhoodsWithBidirectionalSymmetries(int dimension, int size, boolean hideNeighborhood, boolean hideTypeB) {
		StringBuilder header = new StringBuilder();
		StringBuilder underline = new StringBuilder();
		header.append("  ").append(Utils.getAxisLabel(dimension, 0)).append(' ');
		underline.append("------------");
		if (!hideNeighborhood) {
			underline.append("---------------");
		}
		if (!hideTypeB) {
			underline.append("---------");
		}
		for (int coord = 1; coord < dimension; coord++) {
			header.append("|  ").append(Utils.getAxisLabel(dimension, coord)).append(' ');
			underline.append("-----");
		}
		if (!hideNeighborhood) {
			header.append("| Neighborhood ");
		}
		header.append("| Type A");
		if (!hideTypeB) {
			header.append(" | Type B");
		}
		header.append(nl).append(underline);
		System.out.println(header);
		List<String> neighborhoodTypesA = new ArrayList<String>();
		List<String> neighborhoodTypesB = new ArrayList<String>();
		int[] coordinates = new int[dimension];
		int sizeMinusOne = size - 1;
		int dimensionMinusOne = dimension - 1;
		int currentAxis = dimensionMinusOne;
		while (currentAxis > -1) {
			if (currentAxis == dimensionMinusOne) {
				printAnisotropicRegionVonNeumannNeighborhoodWithBidirectionalSymmetries(coordinates, neighborhoodTypesA, neighborhoodTypesB, hideNeighborhood, hideTypeB);
			}
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
		System.out.println("Type A count: " + neighborhoodTypesA.size());
		if (!hideTypeB)
			System.out.println("Type B count: " + neighborhoodTypesB.size());
	}
	
	static void printAnisotropicRegionVonNeumannNeighborhoodWithBidirectionalSymmetries(int[] coords, List<String> neighborhoodTypesA, List<String> neighborhoodTypesB, 
			boolean hideNeighborhood, boolean hideTypeB) {
		List<String> neighbors = new ArrayList<String>();
		List<String> plainNeighbors = new ArrayList<String>();
		String[] strCoords = new String[coords.length];
		boolean hasIncomingSymmetries = false;
		boolean hasOutgoingSymmetries = false;
		for (int axis = 0; axis < coords.length; axis++) {
			String coordLabel = Utils.getAxisLabel(coords.length, axis);
			strCoords[axis] = coords[axis] > 9 ? Integer.toString(coords[axis]) : "0" + coords[axis];
			
			int[] greaterNeighborCoords = coords.clone();
			int[] smallerNeighborCoords = coords.clone();
			greaterNeighborCoords[axis]++;
			smallerNeighborCoords[axis]--;
			int greaterNeighborIncomingSymmetries = 1;
			int smallerNeighborIncomingSymmetries = 1;
			int greaterNeighborOutgoingSymmetries = 1;
			int smallerNeighborOutgoingSymmetries = 1;
			
			int[] nc = greaterNeighborCoords;
			if (isOutsideAnisotropicSection(nc)) {
				greaterNeighborIncomingSymmetries = 0;
			} else {
				greaterNeighborOutgoingSymmetries += getSymmetricNeighborsCount(nc, coords);
				greaterNeighborIncomingSymmetries += getSymmetricNeighborsCount(coords, nc);
			}
			nc = smallerNeighborCoords;
			if (isOutsideAnisotropicSection(nc)) {
				smallerNeighborIncomingSymmetries = 0;
			} else {
				smallerNeighborOutgoingSymmetries += getSymmetricNeighborsCount(nc, coords);
				smallerNeighborIncomingSymmetries += getSymmetricNeighborsCount(coords, nc);
			}
			
			if (greaterNeighborIncomingSymmetries > 0) {
				String neighbor = 'G' + coordLabel.toUpperCase();
				String plainNeighbor = neighbor;
				if (greaterNeighborOutgoingSymmetries > 1) {
					hasOutgoingSymmetries = true;
					neighbor = neighbor + '(' + greaterNeighborOutgoingSymmetries + ')';
				}
				if (greaterNeighborIncomingSymmetries > 1) {
					hasIncomingSymmetries = true;
					neighbor = greaterNeighborIncomingSymmetries + '*' + neighbor;
				}
				neighbors.add(neighbor);
				plainNeighbors.add(plainNeighbor);
			}
			if (smallerNeighborIncomingSymmetries > 0) {
				String neighbor = 'S' + coordLabel.toUpperCase();
				String plainNeighbor = neighbor;
				if (smallerNeighborOutgoingSymmetries > 1) {
					hasOutgoingSymmetries = true;
					neighbor = neighbor + '(' + smallerNeighborOutgoingSymmetries + ')';
				}
				if (smallerNeighborIncomingSymmetries > 1) {
					hasIncomingSymmetries = true;
					neighbor = smallerNeighborIncomingSymmetries + '*' + neighbor;
				}
				neighbors.add(neighbor);
				plainNeighbors.add(plainNeighbor);
			}
		}
		String neighborhood = String.join(", ", neighbors);		
		int typeA = neighborhoodTypesA.indexOf(neighborhood);
		if (typeA == -1) {
			typeA = neighborhoodTypesA.size();
			neighborhoodTypesA.add(neighborhood);
		}
		typeA++;
		
		String plainNeighborhood = String.join(", ", plainNeighbors);
		plainNeighborhood += " " + hasIncomingSymmetries + hasOutgoingSymmetries;
		int typeB = neighborhoodTypesB.indexOf(plainNeighborhood);
		if (typeB == -1) {
			typeB = neighborhoodTypesB.size();
			neighborhoodTypesB.add(plainNeighborhood);
		}
		typeB++;
		if (hideNeighborhood) {
			neighborhood = "";
		} else {
			neighborhood = " | " + neighborhood;
		}
		String strTypeB = " | " + (typeB > 9 ? typeB : "0" + typeB);
		if (hideTypeB) {
			strTypeB = "";
		}
		System.out.println(' ' + String.join(" | ", strCoords) + neighborhood + " | " + (typeA > 9 ? typeA : "0" + typeA) + strTypeB);
	}
	
	static void printTypesMapOfAnisotropicRegionVonNeumannNeighborhoodsWithBidirectionalSymmetries(int dimension, int size) {
		StringBuilder header = new StringBuilder();
		StringBuilder underline = new StringBuilder();
		header.append("Type A | Type B");
		underline.append("---------------");
		header.append(nl).append(underline);
		System.out.println(header);
		List<String> neighborhoodTypesA = new ArrayList<String>();
		List<String> neighborhoodTypesB = new ArrayList<String>();
		Map<Integer, Integer> typesMap = new HashMap<Integer, Integer>();
		int[] coordinates = new int[dimension];
		int sizeMinusOne = size - 1;
		int dimensionMinusOne = dimension - 1;
		int currentAxis = dimensionMinusOne;
		while (currentAxis > -1) {
			if (currentAxis == dimensionMinusOne) {
				getTypeMapOfAnisotropicRegionVonNeumannNeighborhoodWithBidirectionalSymmetries(coordinates, neighborhoodTypesA, neighborhoodTypesB, typesMap);
			}
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
		for (Integer typeA : typesMap.keySet()) {
			Integer typeB = typesMap.get(typeA);
			System.out.println((typeA > 9 ? typeA : "0" + typeA) + " | " + (typeB > 9 ? typeB : "0" + typeB));
		}
	}
	
	static void getTypeMapOfAnisotropicRegionVonNeumannNeighborhoodWithBidirectionalSymmetries(int[] coords, List<String> neighborhoodTypesA, List<String> neighborhoodTypesB, Map<Integer, Integer> typesMap) {
		List<String> neighbors = new ArrayList<String>();
		List<String> plainNeighbors = new ArrayList<String>();
		boolean hasIncomingSymmetries = false;
		boolean hasOutgoingSymmetries = false;
		for (int axis = 0; axis < coords.length; axis++) {
			String coordLabel = Utils.getAxisLabel(coords.length, axis);			
			int[] greaterNeighborCoords = coords.clone();
			int[] smallerNeighborCoords = coords.clone();
			greaterNeighborCoords[axis]++;
			smallerNeighborCoords[axis]--;
			int greaterNeighborIncomingSymmetries = 1;
			int smallerNeighborIncomingSymmetries = 1;
			int greaterNeighborOutgoingSymmetries = 1;
			int smallerNeighborOutgoingSymmetries = 1;
			
			int[] nc = greaterNeighborCoords;
			if (isOutsideAnisotropicSection(nc)) {
				greaterNeighborIncomingSymmetries = 0;
			} else {
				greaterNeighborOutgoingSymmetries += getSymmetricNeighborsCount(nc, coords);
				greaterNeighborIncomingSymmetries += getSymmetricNeighborsCount(coords, nc);
			}
			nc = smallerNeighborCoords;
			if (isOutsideAnisotropicSection(nc)) {
				smallerNeighborIncomingSymmetries = 0;
			} else {
				smallerNeighborOutgoingSymmetries += getSymmetricNeighborsCount(nc, coords);
				smallerNeighborIncomingSymmetries += getSymmetricNeighborsCount(coords, nc);
			}
			
			if (greaterNeighborIncomingSymmetries > 0) {
				String neighbor = 'G' + coordLabel.toUpperCase();
				String plainNeighbor = neighbor;
				if (greaterNeighborOutgoingSymmetries > 1) {
					hasOutgoingSymmetries = true;
					neighbor = neighbor + '(' + greaterNeighborOutgoingSymmetries + ')';
				}
				if (greaterNeighborIncomingSymmetries > 1) {
					hasIncomingSymmetries = true;
					neighbor = greaterNeighborIncomingSymmetries + '*' + neighbor;
				}
				neighbors.add(neighbor);
				plainNeighbors.add(plainNeighbor);
			}
			if (smallerNeighborIncomingSymmetries > 0) {
				String neighbor = 'S' + coordLabel.toUpperCase();
				String plainNeighbor = neighbor;
				if (smallerNeighborOutgoingSymmetries > 1) {
					hasOutgoingSymmetries = true;
					neighbor = neighbor + '(' + smallerNeighborOutgoingSymmetries + ')';
				}
				if (smallerNeighborIncomingSymmetries > 1) {
					hasIncomingSymmetries = true;
					neighbor = smallerNeighborIncomingSymmetries + '*' + neighbor;
				}
				neighbors.add(neighbor);
				plainNeighbors.add(plainNeighbor);
			}
		}
		String neighborhood = String.join(", ", neighbors);
		if (!neighborhoodTypesA.contains(neighborhood)) {
			neighborhoodTypesA.add(neighborhood);
			int typeA = neighborhoodTypesA.size();
			
			String plainNeighborhood = String.join(", ", plainNeighbors);
			plainNeighborhood += " " + hasIncomingSymmetries + hasOutgoingSymmetries;
			int typeB = neighborhoodTypesB.indexOf(plainNeighborhood);
			if (typeB == -1) {
				typeB = neighborhoodTypesB.size();
				neighborhoodTypesB.add(plainNeighborhood);
			}
			typeB++;
			
			typesMap.put(typeA, typeB);			
		}		
	}
	
	static interface Equatable {
		boolean isSame(Object other);		
	}
	
	static <Equatable_Type extends Equatable> int getIndexOfSame(List<Equatable_Type> list, Equatable type) {
		for (int i = list.size() - 1; i != -1; i--) {
			if (list.get(i).isSame(type)) {
				return i;
			}
		}
		return -1;
	}
	
	static void printAnisotropicPositions(int dimension, int size) {
		int[] coordinates = new int[dimension];
		int sizeMinusOne = size - 1;
		int dimensionMinusOne = dimension - 1;
		int currentAxis = dimensionMinusOne;
		while (currentAxis > -1) {
			if (currentAxis == dimensionMinusOne) {
				printAnisotropicPosition(coordinates);
			}
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
//				System.out.println();
				currentAxis--;
			}
		}
	}

	static void printAnisotropicPosition(int[] coords) {
		String[] strCoords = new String[coords.length];
		for (int axis = 0; axis < coords.length; axis++) {
			String coordLabel = Utils.getAxisLabel(coords.length, axis);
			strCoords[axis] = coordLabel + " = " + coords[axis];
		}		
		System.out.println(String.join(", ", strCoords));
	}
	
	static boolean isOutsideAnisotropicSection(int[] coords) {
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
	
	static int getSymmetricNeighborsCount(int[] coords, int[] compareCoords) {
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
	
	static int[] getAsymmetricCoords(int[] coords) {	
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
