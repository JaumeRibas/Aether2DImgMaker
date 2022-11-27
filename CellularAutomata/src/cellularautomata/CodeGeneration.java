/* Aether2DImgMaker -- console app to generate images of the Aether cellular automaton in 2D
    Copyright (C) 2017-2022 Jaume Ribas

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


public class CodeGeneration {
	
	private static final String NL = System.lineSeparator();
	
	public static void main(String[] args) {
		int dimension = 3;
		int firstAxis = 1;
		int secondAxis = 2;
		printDiagonalCrossSectionClassContent(dimension, firstAxis, secondAxis);
	}
	
	public static void printBoundsMethodsForAnisotropicGrid(int dimension) {
		String[] axisLabels = new String[dimension];
		String[] axisUpperCaseLabels = new String[dimension];
		for (int axis = 0; axis < dimension; axis++) {
			axisLabels[axis] = Utils.getAxisLabel(dimension, axis);
			axisUpperCaseLabels[axis] = Utils.getUpperCaseAxisLabel(dimension, axis);
		}
		int dimensionMinusOne = dimension - 1;
		for (int currentAxis = 0; currentAxis < dimension; currentAxis++) {
			String currentAxisUpperCaseLabel = axisUpperCaseLabels[currentAxis];
			System.out.println("@Override" + NL + "default int getMin" + currentAxisUpperCaseLabel 
					+ "() { return 0; }" + NL);
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
							System.out.println("@Override" + NL 
							+ "default int getMin" + currentAxisUpperCaseLabel + "At" + otherAxesInMethodName + "(" + otherAxesParams 
							+ ") { return " + axisLabels[smallestGreaterAxis] + "; }" + NL);
						} else {
							System.out.println("@Override" + NL 
							+ "default int getMin" + currentAxisUpperCaseLabel + "At" + otherAxesInMethodName + "(" + otherAxesParams 
							+ ") { return 0; }" + NL);
						}
						if (isThereASmallerAxis) {
							System.out.println("@Override" + NL 
							+ "default int getMax" + currentAxisUpperCaseLabel + "At" + otherAxesInMethodName + "(" + otherAxesParams 
							+ ") { return Math.min(" + axisLabels[greatestSmallerAxis] + ", getMax" + currentAxisUpperCaseLabel + "()); }" + NL);
						} else {
							System.out.println("@Override" + NL 
							+ "default int getMax" + currentAxisUpperCaseLabel + "At" + otherAxesInMethodName + "(" + otherAxesParams 
							+ ") { return getMax" + currentAxisUpperCaseLabel + "(); }" + NL);
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
					System.out.println("@Override" + NL 
					+ "default int getMin" + currentAxisUpperCaseLabel + "(" + otherAxesParams 
					+ ") { return 0; }" + NL);
				} else {
					System.out.println("@Override" + NL 
					+ "default int getMin" + currentAxisUpperCaseLabel + "(" + otherAxesParams 
					+ ") { return " + axisLabels[currentAxis + 1] + "; }" + NL);
				}
				if (currentAxis == 0) {
					System.out.println("@Override" + NL 
					+ "default int getMax" + currentAxisUpperCaseLabel + "(" + otherAxesParams 
					+ ") { return getMax" + currentAxisUpperCaseLabel + "(); }" + NL);
				} else {
					System.out.println("@Override" + NL 
					+ "default int getMax" + currentAxisUpperCaseLabel + "(" + otherAxesParams 
					+ ") { return Math.min(" + axisLabels[currentAxis - 1] + ", getMax" + currentAxisUpperCaseLabel + "()); }" + NL);
				}	
			}
		}
	}
	
	public static void printBoundsMethodsForIsotropicHypercubicGrid(int dimension) {
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
				System.out.println("@Override" + NL + "default int getMin" + currentAxisUpperCaseLabel 
						+ "() { return -getAsymmetricMax" + firstAxisUpperCaseLabel + "(); }" + NL);
				System.out.println("@Override" + NL + "default int getMax" + currentAxisUpperCaseLabel 
						+ "() { return getAsymmetricMax" + firstAxisUpperCaseLabel + "(); }" + NL);
				System.out.println("@Override" + NL + "default int getAsymmetricMin" + currentAxisUpperCaseLabel 
						+ "() { return 0; }" + NL);
				if (currentAxis != 0) {
					System.out.println("@Override" + NL + "default int getAsymmetricMax" + currentAxisUpperCaseLabel 
							+ "() { return getAsymmetricMax" + firstAxisUpperCaseLabel + "(); }" + NL);
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
								System.out.println("@Override" + NL 
								+ "default int getAsymmetricMin" + currentAxisUpperCaseLabel + "At" + otherAxesInMethodName + "(" + otherAxesParams 
								+ ") { return " + axisLabels[smallestGreaterAxis] + "; }" + NL);
							} else {
								System.out.println("@Override" + NL 
								+ "default int getAsymmetricMin" + currentAxisUpperCaseLabel + "At" + otherAxesInMethodName + "(" + otherAxesParams 
								+ ") { return 0; }" + NL);
							}
							if (isThereASmallerAxis) {
								System.out.println("@Override" + NL 
								+ "default int getAsymmetricMax" + currentAxisUpperCaseLabel + "At" + otherAxesInMethodName + "(" + otherAxesParams 
								+ ") { return " + axisLabels[greatestSmallerAxis] + "; }" + NL);
							} else {
								System.out.println("@Override" + NL 
								+ "default int getAsymmetricMax" + currentAxisUpperCaseLabel + "At" + otherAxesInMethodName + "(" + otherAxesParams 
								+ ") { return getAsymmetricMax" + firstAxisUpperCaseLabel + "(); }" + NL);
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
						System.out.println("@Override" + NL 
						+ "default int getAsymmetricMin" + currentAxisUpperCaseLabel + "(" + otherAxesParams 
						+ ") { return 0; }" + NL);
					} else {
						System.out.println("@Override" + NL 
						+ "default int getAsymmetricMin" + currentAxisUpperCaseLabel + "(" + otherAxesParams 
						+ ") { return " + axisLabels[currentAxis + 1] + "; }" + NL);
					}
					if (currentAxis == 0) {
						System.out.println("@Override" + NL 
						+ "default int getAsymmetricMax" + currentAxisUpperCaseLabel + "(" + otherAxesParams 
						+ ") { return getAsymmetricMax" + currentAxisUpperCaseLabel + "(); }" + NL);
					} else {
						System.out.println("@Override" + NL 
						+ "default int getAsymmetricMax" + currentAxisUpperCaseLabel + "(" + otherAxesParams 
						+ ") { return " + axisLabels[currentAxis - 1] + "; }" + NL);
					}	
				}
			}
		}
	}
	
	public static void printBoundsMethodsForAsymmetricSection(int dimension) {
		String[] axisLabels = new String[dimension];
		String[] axisUpperCaseLabels = new String[dimension];
		for (int i = 0; i < dimension; i++) {
			axisLabels[i] = Utils.getAxisLabel(dimension, i);
			axisUpperCaseLabels[i] = Utils.getUpperCaseAxisLabel(dimension, i);
		}
		int dimensionMinusOne = dimension - 1;
		for (int i = 0; i < dimension; i++) {
			String currentAxisUpperCaseLabel = axisUpperCaseLabels[i];
			System.out.println("@Override" + NL + "public int getMin" + currentAxisUpperCaseLabel 
					+ "() { return source.getAsymmetricMin" + currentAxisUpperCaseLabel + "(); }" + NL);
			System.out.println("@Override" + NL + "public int getMax" + currentAxisUpperCaseLabel 
					+ "() { return source.getAsymmetricMax" + currentAxisUpperCaseLabel + "(); }" + NL);
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
						System.out.println("@Override" + NL 
						+ "public int getMin" + currentAxisUpperCaseLabel + "At" + otherAxesInMethodName + "(" + otherAxesParams 
						+ ") { return source.getAsymmetricMin" + currentAxisUpperCaseLabel + "At" + otherAxesInMethodName + "(" + otherAxesCsv + "); }" + NL);
						System.out.println("@Override" + NL 
						+ "public int getMax" + currentAxisUpperCaseLabel + "At" + otherAxesInMethodName + "(" + otherAxesParams 
						+ ") { return source.getAsymmetricMax" + currentAxisUpperCaseLabel + "At" + otherAxesInMethodName + "(" + otherAxesCsv + "); }" + NL);
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
				System.out.println("@Override" + NL 
				+ "public int getMin" + currentAxisUpperCaseLabel + "(" + otherAxesParams 
				+ ") { return source.getAsymmetricMin" + currentAxisUpperCaseLabel + "(" + otherAxesCsv + "); }" + NL);
				System.out.println("@Override" + NL 
				+ "public int getMax" + currentAxisUpperCaseLabel + "(" + otherAxesParams 
				+ ") { return source.getAsymmetricMax" + currentAxisUpperCaseLabel + "(" + otherAxesCsv + "); }" + NL);	
			}
		}
	}
	
	public static void printBoundsMethodsForDecorator(int dimension) {
		String[] axisLabels = new String[dimension];
		String[] axisUpperCaseLabels = new String[dimension];
		for (int i = 0; i < dimension; i++) {
			axisLabels[i] = Utils.getAxisLabel(dimension, i);
			axisUpperCaseLabels[i] = Utils.getUpperCaseAxisLabel(dimension, i);
		}
		int dimensionMinusOne = dimension - 1;
		for (int i = 0; i < dimension; i++) {
			String currentAxisUpperCaseLabel = axisUpperCaseLabels[i];
			System.out.println("@Override" + NL + "public int getMin" + currentAxisUpperCaseLabel 
					+ "() { return source.getMin" + currentAxisUpperCaseLabel + "(); }" + NL);
			System.out.println("@Override" + NL + "public int getMax" + currentAxisUpperCaseLabel 
					+ "() { return source.getMax" + currentAxisUpperCaseLabel + "(); }" + NL);
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
						System.out.println("@Override" + NL 
						+ "public int getMin" + currentAxisUpperCaseLabel + "At" + otherAxesInMethodName + "(" + otherAxesParams 
						+ ") { return source.getMin" + currentAxisUpperCaseLabel + "At" + otherAxesInMethodName + "(" + otherAxesCsv + "); }" + NL);
						System.out.println("@Override" + NL 
						+ "public int getMax" + currentAxisUpperCaseLabel + "At" + otherAxesInMethodName + "(" + otherAxesParams 
						+ ") { return source.getMax" + currentAxisUpperCaseLabel + "At" + otherAxesInMethodName + "(" + otherAxesCsv + "); }" + NL);
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
				System.out.println("@Override" + NL 
				+ "public int getMin" + currentAxisUpperCaseLabel + "(" + otherAxesParams 
				+ ") { return source.getMin" + currentAxisUpperCaseLabel + "(" + otherAxesCsv + "); }" + NL);
				System.out.println("@Override" + NL 
				+ "public int getMax" + currentAxisUpperCaseLabel + "(" + otherAxesParams 
				+ ") { return source.getMax" + currentAxisUpperCaseLabel + "(" + otherAxesCsv + "); }" + NL);	
			}
		}
	}
	
	public static void printBoundsMethodsToOverride(int dimension) {
		String[] axisLabels = new String[dimension];
		String[] axisUpperCaseLabels = new String[dimension];
		for (int i = 0; i < dimension; i++) {
			axisLabels[i] = Utils.getAxisLabel(dimension, i);
			axisUpperCaseLabels[i] = Utils.getUpperCaseAxisLabel(dimension, i);
		}
		int dimensionMinusOne = dimension - 1;
		for (int i = 0; i < dimension; i++) {
			String currentAxisUpperCaseLabel = axisUpperCaseLabels[i];
			System.out.println("@Override" + NL + "public int getMin" + currentAxisUpperCaseLabel 
					+ "() {" + NL + NL + "}" + NL);
			System.out.println("@Override" + NL + "public int getMax" + currentAxisUpperCaseLabel 
					+ "() {" + NL + NL + "}" + NL);
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
						System.out.println("@Override" + NL 
						+ "public int getMin" + currentAxisUpperCaseLabel + "At" + otherAxesInMethodName + "(" + otherAxesParams 
						+ ") {" + NL + NL + "}" + NL);
						System.out.println("@Override" + NL 
						+ "public int getMax" + currentAxisUpperCaseLabel + "At" + otherAxesInMethodName + "(" + otherAxesParams 
						+ ") {" + NL + NL + "}" + NL);
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
				System.out.println("@Override" + NL 
				+ "public int getMin" + currentAxisUpperCaseLabel + "(" + otherAxesParams 
				+ ") {" + NL + NL + "}" + NL);
				System.out.println("@Override" + NL 
				+ "public int getMax" + currentAxisUpperCaseLabel + "(" + otherAxesParams 
				+ ") {" + NL + NL + "}" + NL);	
			}
		}
	}
	
	public static void printBoundsMethodsForSubsection(int dimension) {
		String[] axisLabels = new String[dimension];
		String[] axisUpperCaseLabels = new String[dimension];
		for (int i = 0; i < dimension; i++) {
			axisLabels[i] = Utils.getAxisLabel(dimension, i);
			axisUpperCaseLabels[i] = Utils.getUpperCaseAxisLabel(dimension, i);
		}
		int dimensionMinusOne = dimension - 1;
		for (int i = 0; i < dimension; i++) {
			String currentAxisUpperCaseLabel = axisUpperCaseLabels[i];
			System.out.println("@Override" + NL + "public int getMin" + currentAxisUpperCaseLabel 
					+ "() { return min" + currentAxisUpperCaseLabel + "; }" + NL);
			System.out.println("@Override" + NL + "public int getMax" + currentAxisUpperCaseLabel 
					+ "() { return max" + currentAxisUpperCaseLabel + "; }" + NL);
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
						System.out.println("@Override" + NL 
						+ "public int getMin" + currentAxisUpperCaseLabel + "At" + otherAxesInMethodName + "(" + otherAxesParams + ") { return Math.max(min" + currentAxisUpperCaseLabel 
						+ ", source.getMin" + currentAxisUpperCaseLabel + "At" + otherAxesInMethodName + "(" + otherAxesCsv + ")); }" + NL);
						System.out.println("@Override" + NL 
						+ "public int getMax" + currentAxisUpperCaseLabel + "At" + otherAxesInMethodName + "(" + otherAxesParams + ") { return Math.min(max" + currentAxisUpperCaseLabel 
						+ ", source.getMax" + currentAxisUpperCaseLabel + "At" + otherAxesInMethodName + "(" + otherAxesCsv + ")); }" + NL);
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
				System.out.println("@Override" + NL 
				+ "public int getMin" + currentAxisUpperCaseLabel + "(" + otherAxesParams + ") { return Math.max(min" + currentAxisUpperCaseLabel 
				+ ", source.getMin" + currentAxisUpperCaseLabel + "(" + otherAxesCsv + ")); }" + NL);
				System.out.println("@Override" + NL 
				+ "public int getMax" + currentAxisUpperCaseLabel + "(" + otherAxesParams + ") { return Math.min(max" + currentAxisUpperCaseLabel 
				+ ", source.getMax" + currentAxisUpperCaseLabel + "(" + otherAxesCsv + ")); }" + NL);		
			}
		}
	}
	
	public static void printBoundsMethodsForModelAsND(int dimension) {
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
			System.out.println("@Override" + NL + "public int getMin" + currentAxisUpperCaseLabel 
					+ "() {" + getNL(1) + "return source.getMinCoordinate(" + i + ");" + NL + "}" + NL);
			System.out.println("@Override" + NL + "public int getMax" + currentAxisUpperCaseLabel 
					+ "() {" + getNL(1) + "return source.getMaxCoordinate(" + i + ");" + NL + "}" + NL);
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
						System.out.println("@Override" + NL 
						+ "public int getMin" + currentAxisUpperCaseLabel + "At" + otherAxesInMethodName + "(" + otherAxesParams 
						+ ") {" + getNL(1) + "return source.getMinCoordinate(" + i + ", new PartialCoordinates(" + partialCoordinatesCsv + "));" 
						+ NL + "}" + NL);
						System.out.println("@Override" + NL 
						+ "public int getMax" + currentAxisUpperCaseLabel + "At" + otherAxesInMethodName + "(" + otherAxesParams 
						+ ") {" + getNL(1) + "return source.getMaxCoordinate(" + i + ", new PartialCoordinates(" + partialCoordinatesCsv + "));" 
						+ NL + "}" + NL);
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
				System.out.println("@Override" + NL 
				+ "public int getMin" + currentAxisUpperCaseLabel + "(" + otherAxesParams 
				+ ") {" + getNL(1) + "return source.getMinCoordinate(" + i + ", new PartialCoordinates(" + partialCoordinatesCsv + "));" 
				+ NL + "}" + NL);
				System.out.println("@Override" + NL 
				+ "public int getMax" + currentAxisUpperCaseLabel + "(" + otherAxesParams 
				+ ") {" + getNL(1) + "return source.getMaxCoordinate(" + i + ", new PartialCoordinates(" + partialCoordinatesCsv + "));" 
				+ NL + "}" + NL);	
			}
		}
	}
	
	public static void printAsymmetricBoundsMethodsForInterface(int dimension) {
		String[] axisLabels = new String[dimension];
		String[] axisUpperCaseLabels = new String[dimension];
		for (int i = 0; i < dimension; i++) {
			axisLabels[i] = Utils.getAxisLabel(dimension, i);
			axisUpperCaseLabels[i] = Utils.getUpperCaseAxisLabel(dimension, i);
		}
		int dimensionMinusOne = dimension - 1;
		for (int i = 0; i < dimension; i++) {
			String currentAxisUpperCaseLabel = axisUpperCaseLabels[i];
			System.out.println("int getAsymmetricMin" + currentAxisUpperCaseLabel + "();" + NL);
			System.out.println("int getAsymmetricMax" + currentAxisUpperCaseLabel + "();" + NL);
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
						System.out.println("int getAsymmetricMin" + currentAxisUpperCaseLabel + "At" + otherAxesInMethodName + "(" + otherAxesParams + ");" + NL);
						System.out.println("int getAsymmetricMax" + currentAxisUpperCaseLabel + "At" + otherAxesInMethodName + "(" + otherAxesParams + ");" + NL);
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
				System.out.println("int getAsymmetricMin" + currentAxisUpperCaseLabel + "(" + otherAxesParams + ");" + NL);
				System.out.println("int getAsymmetricMax" + currentAxisUpperCaseLabel + "(" + otherAxesParams + ");" + NL);
			}
		}
	}
	
	public static void printBoundsMethodsForInterface(int dimension) {
		String[] axisLabels = new String[dimension];
		String[] axisUpperCaseLabels = new String[dimension];
		for (int i = 0; i < dimension; i++) {
			axisLabels[i] = Utils.getAxisLabel(dimension, i);
			axisUpperCaseLabels[i] = Utils.getUpperCaseAxisLabel(dimension, i);
		}
		int dimensionMinusOne = dimension - 1;
		for (int i = 0; i < dimension; i++) {
			String currentAxisUpperCaseLabel = axisUpperCaseLabels[i];
			System.out.println("int getMin" + currentAxisUpperCaseLabel + "();" + NL);
			System.out.println("int getMax" + currentAxisUpperCaseLabel + "();" + NL);
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
						System.out.println("int getMin" + currentAxisUpperCaseLabel + "At" + otherAxesInMethodName + "(" + otherAxesParams + ");" + NL);
						System.out.println("int getMax" + currentAxisUpperCaseLabel + "At" + otherAxesInMethodName + "(" + otherAxesParams + ");" + NL);
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
				System.out.println("int getMin" + currentAxisUpperCaseLabel + "(" + otherAxesParams + ");" + NL);
				System.out.println("int getMax" + currentAxisUpperCaseLabel + "(" + otherAxesParams + ");" + NL);				
			}
		}
	}
	
	public static void printBoundsMethodsForInterfaceWithDefault(int dimension) {
		String[] axisLabels = new String[dimension];
		String[] axisUpperCaseLabels = new String[dimension];
		for (int i = 0; i < dimension; i++) {
			axisLabels[i] = Utils.getAxisLabel(dimension, i);
			axisUpperCaseLabels[i] = Utils.getUpperCaseAxisLabel(dimension, i);
		}
		int dimensionMinusOne = dimension - 1;
		for (int i = 0; i < dimension; i++) {
			String currentAxisUpperCaseLabel = axisUpperCaseLabels[i];
			System.out.println("int getMin" + currentAxisUpperCaseLabel + "();" + NL);
			System.out.println("int getMax" + currentAxisUpperCaseLabel + "();" + NL);
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
						System.out.println("default int getMin" + currentAxisUpperCaseLabel + "At" + otherAxesInMethodName + "(" + otherAxesParams + ") { return getMin" + currentAxisUpperCaseLabel + "(); }" + NL);
						System.out.println("default int getMax" + currentAxisUpperCaseLabel + "At" + otherAxesInMethodName + "(" + otherAxesParams + ") { return getMax" + currentAxisUpperCaseLabel + "(); }" + NL);
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
				System.out.println("default int getMin" + currentAxisUpperCaseLabel + "(" + otherAxesParams + ") { return getMin" + currentAxisUpperCaseLabel + "(); }" + NL);
				System.out.println("default int getMax" + currentAxisUpperCaseLabel + "(" + otherAxesParams + ") { return getMax" + currentAxisUpperCaseLabel + "(); }" + NL);
			}
		}
	}
	
	public static void printLabelMethodsForCrossSection(int dimension, int crossSectionAxis) {
		int crossSectionDimension = dimension - 1;
		int axis = 0;
		for (; axis != crossSectionAxis; axis++) {
			String sourceAxisLabel = Utils.getUpperCaseAxisLabel(dimension, axis);
			String crossSectionAxisLabel = Utils.getUpperCaseAxisLabel(crossSectionDimension, axis);
			System.out.println("@Override" + NL + "public String get" + crossSectionAxisLabel + "Label() {" + NL 
			+ "    return source.get" + sourceAxisLabel + "Label();" + NL + "}" + NL);
		}
		for (int sourceAxis = axis + 1; axis != crossSectionDimension; axis = sourceAxis, sourceAxis++) {
			String sourceAxisLabel = Utils.getUpperCaseAxisLabel(dimension, sourceAxis);
			String crossSectionAxisLabel = Utils.getUpperCaseAxisLabel(crossSectionDimension, axis);
			System.out.println("@Override" + NL + "public String get" + crossSectionAxisLabel + "Label() {" + NL 
			+ "    return source.get" + sourceAxisLabel + "Label();" + NL + "}" + NL);
		}
	}
	
	public static void printBoundsMethodsForCrossSection(int dimension, int crossSectionAxis) {
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
			System.out.println("@Override" + NL + "public int getMin" + currentAxisUpperCaseLabel 
					+ "() {" + NL + "    " 
					+ getBoundMethodContentForCrossSection(dimension, crossSectionAxis, i, emptyArray, true, 
							crossSectionAxisLabel, sourceAxisUpperCaseLabels, axisLabels) + NL + "}" + NL);
			System.out.println("@Override" + NL + "public int getMax" + currentAxisUpperCaseLabel 
					+ "() {" + NL + "    " 
					+ getBoundMethodContentForCrossSection(dimension, crossSectionAxis, i, emptyArray, false, 
							crossSectionAxisLabel, sourceAxisUpperCaseLabels, axisLabels) + NL + "}" + NL);
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
						System.out.println("@Override" + NL 
						+ "public int getMin" + currentAxisUpperCaseLabel + "At" + otherAxesInMethodName + "(" + otherAxesParams 
						+ ") {" + NL + "    " 
						+ getBoundMethodContentForCrossSection(dimension, crossSectionAxis, i, otherAxesUsed, true, 
								crossSectionAxisLabel, sourceAxisUpperCaseLabels, axisLabels) + NL + "}" + NL);
						System.out.println("@Override" + NL 
						+ "public int getMax" + currentAxisUpperCaseLabel + "At" + otherAxesInMethodName + "(" + otherAxesParams 
						+ ") {" + NL + "    " 
						+ getBoundMethodContentForCrossSection(dimension, crossSectionAxis, i, otherAxesUsed, false, 
								crossSectionAxisLabel, sourceAxisUpperCaseLabels, axisLabels) + NL + "}" + NL);
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
				System.out.println("@Override" + NL 
				+ "public int getMin" + currentAxisUpperCaseLabel + "(" + otherAxesParams 
				+ ") {" + NL + "    " 
				+ getBoundMethodContentForCrossSection(dimension, crossSectionAxis, i, otherAxes, true, 
						crossSectionAxisLabel, sourceAxisUpperCaseLabels, axisLabels) + NL + "}" + NL);
				System.out.println("@Override" + NL 
				+ "public int getMax" + currentAxisUpperCaseLabel + "(" + otherAxesParams 
				+ ") {" + NL + "    " 
				+ getBoundMethodContentForCrossSection(dimension, crossSectionAxis, i, otherAxes, false, 
						crossSectionAxisLabel, sourceAxisUpperCaseLabels, axisLabels) + NL + "}" + NL);
			}
		}
	}
	
	public static String getBoundMethodContentForCrossSection(int dimension, int crossSectionAxis, int boundAxis, int[] otherAxes, boolean minCoord, 
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
			return "return source.getM" + (minCoord? "in" : "ax") + sourceAxisUpperCaseLabels[sourceBoundAxis] + "(" + String.join(", ", otherAxesParams) + ");";
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
			return "return source.getM" + (minCoord? "in" : "ax") + sourceAxisUpperCaseLabels[sourceBoundAxis] + "At" + otherSourceAxesInMethodName + "(" + String.join(", ", otherAxesParams) + ");";
		}
	}
	
	private static void validateDiagonalAxes(int firstAxis, int secondAxis) {
		if (firstAxis < 0) {
			throw new IllegalArgumentException("The axes cannot be smaller than 0");
		}
		if (firstAxis >= secondAxis) {
			throw new IllegalArgumentException("The second axis must be smaller than the first.");
		}
	}
	
	public static void printDiagonalCrossSectionClassContent(int dimension, int firstAxis, int secondAxis) {
		validateDiagonalAxes(firstAxis, secondAxis);
		String offsetName =  Utils.getAxisLabel(dimension, secondAxis) + "OffsetFrom" + Utils.getUpperCaseAxisLabel(dimension, firstAxis);
		StringBuilder propsAndConstructor = new StringBuilder("protected Source_Type source;").append(NL).append("protected int slope;").append(NL).append("protected int ")
				.append(offsetName).append(";").append(NL);
		int axis = 0;
		for (; axis != secondAxis; axis++) {
			String axisLabel = Utils.getUpperCaseAxisLabel(dimension, axis);
			propsAndConstructor.append("protected int crossSectionMin").append(axisLabel).append(";").append(NL).append("protected int crossSectionMax").append(axisLabel)
			.append(";").append(NL);
		}
		for (axis++; axis != dimension; axis++) {
			String axisLabel = Utils.getUpperCaseAxisLabel(dimension, axis);
			propsAndConstructor.append("protected int crossSectionMin").append(axisLabel).append(";").append(NL).append("protected int crossSectionMax").append(axisLabel)
			.append(";").append(NL);
		}
		propsAndConstructor.append(NL).append("public Model").append(dimension).append("D").append(Utils.getUpperCaseAxisLabel(dimension, firstAxis))
			.append(Utils.getUpperCaseAxisLabel(dimension, secondAxis)).append("DiagonalCrossSection(Source_Type source, boolean positiveSlope, int ").append(offsetName)
			.append(") {").append(NL).append("    this.source = source;").append(NL).append("    this.slope = positiveSlope ? 1 : -1;").append(NL).append("    this.")
			.append(offsetName).append(" = ").append(offsetName).append(";").append(NL).append("    if (!getBounds()) {").append(NL)
			.append("        throw new IllegalArgumentException(\"The cross section is out of bounds.\");").append(NL).append("    }").append(NL).append("}").append(NL);
		System.out.println(propsAndConstructor);
		printLabelMethodsForDiagonalCrossSection(dimension, firstAxis, secondAxis);
		printGetBoundsMethodForDiagonalCrossSection(dimension, firstAxis, secondAxis);
		printBoundsMethodsForDiagonalCrossSection(dimension, firstAxis, secondAxis);
		System.out.println("@Override" + NL + "public Boolean nextStep() throws Exception {" + NL + "    Boolean changed = source.nextStep();" + NL + "    if (!getBounds()) {" 
				+ NL + "        throw new UnsupportedOperationException(\"The cross section is out of bounds.\");" + NL + "    }" + NL + "    return changed;" + NL + "}" + NL 
				+ NL + "@Override" + NL + "public Boolean isChanged() {" + NL + "    return source.isChanged();" + NL + "}" + NL + NL + "@Override" + NL + "public long getStep() {" 
				+ NL + "    return source.getStep();" + NL + "}" + NL + NL + "@Override" + NL + "public String getName() {" + NL + "    return source.getName();" + NL + "}" 
				+ NL + NL + "@Override" + NL + "public String getSubfolderPath() {" + NL + "    StringBuilder path = new StringBuilder(source.getSubfolderPath()).append(\"/\").append(source.get" 
				+ Utils.getUpperCaseAxisLabel(dimension, secondAxis) + "Label()).append(\"=\");" + NL + "    if (slope == -1) {" + NL + "        path.append(\"-\");" + NL 
				+ "    }" + NL + "    path.append(source.get" + Utils.getUpperCaseAxisLabel(dimension, firstAxis) + "Label());" + NL + "    if (" + offsetName + " < 0) {" + NL 
				+ "        path.append(" + offsetName + ");" + NL + "    } else if (" + offsetName + " > 0) {" + NL + "        path.append(\"+\").append(" + offsetName 
				+ ");" + NL + "    }" + NL + "    return path.toString();" + NL + "}" + NL + NL + "@Override" + NL 
				+ "public void backUp(String backupPath, String backupName) throws FileNotFoundException, IOException {" + NL + "    source.backUp(backupPath, backupName);" 
				+ NL + "}" + NL);
	}
	
	public static void printLabelMethodsForDiagonalCrossSection(int dimension, int firstAxis, int secondAxis) {
		validateDiagonalAxes(firstAxis, secondAxis);
		printLabelMethodsForCrossSection(dimension, secondAxis);
	}
	
	public static void printGetBoundsMethodForDiagonalCrossSection(int dimension, int firstAxis, int secondAxis) {
		validateDiagonalAxes(firstAxis, secondAxis);
		String firstAxisLabel = Utils.getAxisLabel(dimension, firstAxis);
		String[] axisLabels = new String[dimension];
		for (int axis = 0; axis < dimension; axis++) {
			axisLabels[axis] = Utils.getUpperCaseAxisLabel(dimension, axis);
		}
		String maxVar = "max" + axisLabels[firstAxis];
		String secondAxisCoordVar = "crossSection" + axisLabels[secondAxis];
		String condition = firstAxisLabel + " <= " + maxVar; 
		String secondPartOfSecondAxisBoundMethod = axisLabels[secondAxis] + (dimension == 2 ? "" : "At" + axisLabels[firstAxis]) + "(" + firstAxisLabel + ")";
		String secondPartOfOtherBoundMethod = dimension == 2 ? null : 
			(dimension == 3 ? "" : "At" + axisLabels[firstAxis] + axisLabels[secondAxis]) + "(" + firstAxisLabel + ", " + secondAxisCoordVar + ");" + NL;
		StringBuilder result = new StringBuilder("protected boolean getBounds() {").append(NL).append("    int ").append(firstAxisLabel)
				.append(" = source.getMin").append(axisLabels[firstAxis]).append("();").append(NL).append("    int ").append(maxVar)
				.append(" = source.getMax").append(axisLabels[firstAxis]).append("();").append(NL).append("    int ").append(secondAxisCoordVar).append(" = slope*")
				.append(firstAxisLabel).append(" + ").append(Utils.getAxisLabel(dimension, secondAxis)).append("OffsetFrom").append(axisLabels[firstAxis])
				.append(";").append(NL).append("    while (").append(condition).append(" && (").append(secondAxisCoordVar).append(" < source.getMin")
				.append(secondPartOfSecondAxisBoundMethod).append(" || ").append(secondAxisCoordVar).append(" > source.getMax").append(secondPartOfSecondAxisBoundMethod)
				.append(")) {").append(NL).append("        ").append(firstAxisLabel).append("++;").append(NL).append("        ").append(secondAxisCoordVar)
				.append(" += slope;").append(NL).append("    }").append(NL).append("    if (").append(condition).append(") {").append(NL).append("        crossSectionMin")
				.append(axisLabels[firstAxis]).append(" = ").append(firstAxisLabel).append(";").append(NL).append("        crossSectionMax").append(axisLabels[firstAxis])
				.append(" = ").append(firstAxisLabel).append(";").append(NL);
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
		result.append("        ").append(firstAxisLabel).append("++;").append(NL).append("        ").append(secondAxisCoordVar).append(" += slope;").append(NL)
			.append("        while (").append(condition).append(" && ").append(secondAxisCoordVar).append(" >= source.getMin").append(secondPartOfSecondAxisBoundMethod)
			.append(" && ").append(secondAxisCoordVar).append(" <= source.getMax").append(secondPartOfSecondAxisBoundMethod).append(") {").append(NL)
			.append("            crossSectionMax").append(axisLabels[firstAxis]).append(" = ").append(firstAxisLabel).append(";").append(NL);
		axis = 0;
		for (; axis < firstAxis; axis++) {
			String axisLabel = axisLabels[axis];
			result.append("            int localMin").append(axisLabel).append(" = source.getMin").append(axisLabel).append(secondPartOfOtherBoundMethod)
				.append("            if (localMin").append(axisLabel).append(" < crossSectionMin").append(axisLabel).append(") {").append(NL)
				.append("                crossSectionMin").append(axisLabel).append(" = localMin").append(axisLabel).append(";").append(NL)
				.append("            }").append(NL)
				.append("            int localMax").append(axisLabel).append(" = source.getMax").append(axisLabel).append(secondPartOfOtherBoundMethod)
				.append("            if (localMax").append(axisLabel).append(" > crossSectionMax").append(axisLabel).append(") {").append(NL)
				.append("                crossSectionMax").append(axisLabel).append(" = localMax").append(axisLabel).append(";").append(NL)
				.append("            }").append(NL);
		}
		for (axis++; axis < secondAxis; axis++) {
			String axisLabel = axisLabels[axis];
			result.append("            int localMin").append(axisLabel).append(" = source.getMin").append(axisLabel).append(secondPartOfOtherBoundMethod)
				.append("            if (localMin").append(axisLabel).append(" < crossSectionMin").append(axisLabel).append(") {").append(NL)
				.append("                crossSectionMin").append(axisLabel).append(" = localMin").append(axisLabel).append(";").append(NL)
				.append("            }").append(NL)
				.append("            int localMax").append(axisLabel).append(" = source.getMax").append(axisLabel).append(secondPartOfOtherBoundMethod)
				.append("            if (localMax").append(axisLabel).append(" > crossSectionMax").append(axisLabel).append(") {").append(NL)
				.append("                crossSectionMax").append(axisLabel).append(" = localMax").append(axisLabel).append(";").append(NL)
				.append("            }").append(NL);
		}
		for (axis++; axis < dimension; axis++) {
			String axisLabel = axisLabels[axis];
			result.append("            int localMin").append(axisLabel).append(" = source.getMin").append(axisLabel).append(secondPartOfOtherBoundMethod)
				.append("            if (localMin").append(axisLabel).append(" < crossSectionMin").append(axisLabel).append(") {").append(NL)
				.append("                crossSectionMin").append(axisLabel).append(" = localMin").append(axisLabel).append(";").append(NL)
				.append("            }").append(NL)
				.append("            int localMax").append(axisLabel).append(" = source.getMax").append(axisLabel).append(secondPartOfOtherBoundMethod)
				.append("            if (localMax").append(axisLabel).append(" > crossSectionMax").append(axisLabel).append(") {").append(NL)
				.append("                crossSectionMax").append(axisLabel).append(" = localMax").append(axisLabel).append(";").append(NL)
				.append("            }").append(NL);
		}
		result.append("            ").append(firstAxisLabel).append("++;").append(NL).append("            ").append(secondAxisCoordVar).append(" += slope;")
		.append(NL).append("        }").append(NL).append("        return true;").append(NL).append("    } else {").append(NL).append("        return false;")
		.append(NL).append("    }").append(NL).append("}").append(NL);
		System.out.println(result);
	}

	public static void printBoundsMethodsForDiagonalCrossSection(int dimension, int firstAxis, int secondAxis) {
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
			System.out.println("@Override" + NL + "public int getMin" + currentAxisUpperCaseLabel 
					+ "() {" + NL
					+ getBoundMethodContentForDiagonalCrossSection(dimension, firstAxis, secondAxis, i, emptyArray, true, 
							offsetLabel, sourceAxisUpperCaseLabels, axisLabels) + NL + "}" + NL);
			System.out.println("@Override" + NL + "public int getMax" + currentAxisUpperCaseLabel 
					+ "() {" + NL
					+ getBoundMethodContentForDiagonalCrossSection(dimension, firstAxis, secondAxis, i, emptyArray, false, 
							offsetLabel, sourceAxisUpperCaseLabels, axisLabels) + NL + "}" + NL);
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
						System.out.println("@Override" + NL 
						+ "public int getMin" + currentAxisUpperCaseLabel + "At" + otherAxesInMethodName + "(" + otherAxesParams 
						+ ") {" + NL
						+ getBoundMethodContentForDiagonalCrossSection(dimension, firstAxis, secondAxis, i, otherAxesUsed, true, 
								offsetLabel, sourceAxisUpperCaseLabels, axisLabels) + NL + "}" + NL);
						System.out.println("@Override" + NL 
						+ "public int getMax" + currentAxisUpperCaseLabel + "At" + otherAxesInMethodName + "(" + otherAxesParams 
						+ ") {" + NL
						+ getBoundMethodContentForDiagonalCrossSection(dimension, firstAxis, secondAxis, i, otherAxesUsed, false, 
								offsetLabel, sourceAxisUpperCaseLabels, axisLabels) + NL + "}" + NL);
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
				System.out.println("@Override" + NL 
				+ "public int getMin" + currentAxisUpperCaseLabel + "(" + otherAxesParams 
				+ ") {" + NL
				+ getBoundMethodContentForDiagonalCrossSection(dimension, firstAxis, secondAxis, i, otherAxes, true, 
						offsetLabel, sourceAxisUpperCaseLabels, axisLabels) + NL + "}" + NL);
				System.out.println("@Override" + NL 
				+ "public int getMax" + currentAxisUpperCaseLabel + "(" + otherAxesParams 
				+ ") {" + NL
				+ getBoundMethodContentForDiagonalCrossSection(dimension, firstAxis, secondAxis, i, otherAxes, false, 
						offsetLabel, sourceAxisUpperCaseLabels, axisLabels) + NL + "}" + NL);
			}
		}
	}
	
	public static String getBoundMethodContentForDiagonalCrossSection(int dimension, int firstAxis, int secondAxis, int boundAxis, int[] otherAxes, boolean minCoord, 
			String offsetLabel, String[] sourceAxisUpperCaseLabels, String[] crossSectionAxisLabels) {
		int sourceBoundAxis = boundAxis < secondAxis ? boundAxis : boundAxis + 1;
		if (otherAxes.length == 0) {
			return "    return crossSectionM" + (minCoord? "in" : "ax") + sourceAxisUpperCaseLabels[sourceBoundAxis] + ";";
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
				return "    return source.getM" + (minCoord? "in" : "ax") + sourceAxisUpperCaseLabels[sourceBoundAxis] + "(" + String.join(", ", otherAxesParams) + ");";
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
				return "    return source.getM" + (minCoord? "in" : "ax") + sourceAxisUpperCaseLabels[sourceBoundAxis] + "At" + otherSourceAxesInMethodName + "(" + String.join(", ", otherAxesParams) + ");";
			}
		} else {
			String firstAxisLabel = sourceAxisUpperCaseLabels[firstAxis];
			String secondAxisLabel = sourceAxisUpperCaseLabels[secondAxis];
			if (boundAxis == firstAxis) {
				StringBuilder result = new StringBuilder("    for (int crossSection").append(firstAxisLabel).append(" = crossSectionM").append(minCoord? "in" : "ax").append(firstAxisLabel).append(", crossSection")
						.append(secondAxisLabel).append(" = slope*crossSection").append(firstAxisLabel).append(" + ").append(offsetLabel).append("; crossSection").append(firstAxisLabel).append(minCoord? " <= " : " >= ")
						.append("crossSectionM").append(minCoord? "ax" : "in").append(firstAxisLabel).append("; crossSection").append(firstAxisLabel).append(minCoord? "++" : "--").append(", crossSection")
						.append(secondAxisLabel).append(minCoord? " +" : " -").append("= slope) {").append(NL).append("        if (");
				int otherAxesLengthMinusOne = otherAxes.length - 1;
				SortedMap<Integer, String> sourceOtherAxesInMethodName = new TreeMap<Integer, String>();
				sourceOtherAxesInMethodName.put(firstAxis, firstAxisLabel);
				sourceOtherAxesInMethodName.put(secondAxis, secondAxisLabel);
				SortedMap<Integer, String>  axesParams = new TreeMap<Integer, String>();
				axesParams.put(firstAxis, "crossSection" + firstAxisLabel);
				axesParams.put(secondAxis, "crossSection" + secondAxisLabel);
				String separator = NL + "                && ";
				for (int i = 0; i < otherAxesLengthMinusOne; i++) {
					int otherAxis = otherAxes[i];
					int sourceOtherAxis = otherAxis < secondAxis ? otherAxis : otherAxis + 1;
					String otherAxisLabel = crossSectionAxisLabels[otherAxis];
					String sourceOtherAxisLabel = sourceAxisUpperCaseLabels[sourceOtherAxis];
					String secondPartOfMethodCall = sourceOtherAxisLabel + "At" + String.join("", sourceOtherAxesInMethodName.values()) + "(" + String.join(", ", axesParams.values()) + ")";
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
				secondPartOfMethodCall.append("(").append(String.join(", ", axesParams.values())).append(")");
				String otherAxisLabel = crossSectionAxisLabels[otherAxis];
				result.append(otherAxisLabel).append(" >= source.getMin").append(secondPartOfMethodCall).append(separator).append(otherAxisLabel)
				.append(" <= source.getMax").append(secondPartOfMethodCall).append(") {").append(NL).append("            return crossSection").append(firstAxisLabel).append(";")
				.append(NL).append("        }").append(NL).append("    }").append(NL)
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
					sourceBoundMethodCall = "source.getM" + (minCoord? "in" : "ax") + sourceAxisUpperCaseLabels[sourceBoundAxis] + "(" + String.join(", ", otherAxesParams) + ")";
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
					sourceBoundMethodCall = "source.getM" + (minCoord? "in" : "ax") + sourceAxisUpperCaseLabels[sourceBoundAxis] + "At" + otherSourceAxesInMethodName + "(" + String.join(", ", otherAxesParams) + ")";
				}
				String localBoundVar = "localM" + (minCoord? "in" : "ax") + sourceAxisUpperCaseLabels[sourceBoundAxis];
				String increments = "crossSection" + firstAxisLabel + "++, crossSection" + secondAxisLabel + " += slope";
				StringBuilder result = new StringBuilder("    int crossSection").append(firstAxisLabel).append(" = crossSectionMin").append(firstAxisLabel).append(";").append(NL)
						.append("    int crossSection").append(secondAxisLabel).append(" = slope*crossSection").append(firstAxisLabel).append(" + ").append(offsetLabel).append(";")
						.append(NL).append("    int result = ").append(sourceBoundMethodCall).append(";").append(NL).append("    int ").append(localBoundVar).append(";").append(NL)
						.append("    for (").append(increments).append(";").append(NL).append("            crossSection").append(firstAxisLabel).append(" <= crossSectionMax")
						.append(firstAxisLabel).append(" && (").append(localBoundVar).append(" = ").append(sourceBoundMethodCall).append(") ").append(minCoord? "<" : ">")
						.append("= result;").append(NL).append("            ").append(increments).append(") {").append(NL).append("        result = ").append(localBoundVar)
						.append(";").append(NL).append("    }").append(NL).append("    return result;");
				return result.toString();						 
			}
		}
	}
	
	public static void printAetherTopplingMethods(int dimension) {
		List<AnysotropicVonNeumannNeighborhoodType> neighborhoodTypes = new ArrayList<AnysotropicVonNeumannNeighborhoodType>();
		int[] coordinates = new int[dimension];
		int size = (dimension - 1)*2 + 3;//it seems to work
		int sizeMinusOne = size - 1;
		int dimensionMinusOne = dimension - 1;
		int currentAxis = dimensionMinusOne;
		while (currentAxis > -1) {
			if (currentAxis == dimensionMinusOne) {
				getAnisotropicVonNeumannNeighborhoodTypes(coordinates, neighborhoodTypes);
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
			AnysotropicVonNeumannNeighborhoodType type = neighborhoodTypes.get(i);
			printAetherTopplingMethod(type, num);
			System.out.println();
		}
	}
	
	private static String getNL(int indentation) {
		String result = NL;
		for (int i = 0; i < indentation; i++) {
			result += "    ";
		}
		return result;
	}
	
	private static void printAetherTopplingMethod(AnysotropicVonNeumannNeighborhoodType type, int number) {
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
				method.append("int ").append(Utils.getAxisLabel(dimension, i)).append(", ");
			}
		}
		//add current value parameter
		method.append("long currentValue, ");
		//add neighbors parameters 
		int neighborCount = type.neighbors.size();
		for (int i = 0; i < neighborCount; i++) {
			NeighborType neighbor = type.neighbors.get(i);
			String axisLabel = Utils.getUpperCaseAxisLabel(dimension, neighbor.axisIndex);
			String dir = neighbor.isPositiveDirection? "g" : "s";
			String varPrefix = dir + axisLabel;
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
		String firstAxisLabel = Utils.getUpperCaseAxisLabel(dimension, 0);
		if (neighborCount > 2) {
			//add arrays to reuse
			method.append("long[] relevantAsymmetricNeighborValues, int[] sortedNeighborsIndexes, int[][] relevantAsymmetricNeighborCoords, ");
			if (type.hasMultipliers) {
				method.append("int[] relevantAsymmetricNeighborShareMultipliers, ");
			}
			if (type.hasSymmetries) {
				method.append("int[] relevantAsymmetricNeighborSymmetryCounts, ");
			}
			//add new grid slices parameter
			method.append("long").append(arrayBrackets).append("[] new").append(firstAxisLabel).append("Slices) {").append(getNL(ind1));
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
				String axisLabel = Utils.getUpperCaseAxisLabel(dimension, neighbor.axisIndex);
				String dir = neighbor.isPositiveDirection? "g" : "s";
				String varPrefix = dir + axisLabel;
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
							method.append(Utils.getAxisLabel(dimension, axis));
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
							method.append(Utils.getAxisLabel(dimension, axis));
						} else {
							method.append(coordinate);
						}
						method.append(";").append(getNL(ind2));
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
					method.append(";").append(getNL(ind2));
					axis++;
					for (; axis < dimension; axis++) {
						method.append("nc[").append(axis).append("] = ");
						coordinate = type.coordinates[axis];
						if (coordinate == null) {
							method.append(Utils.getAxisLabel(dimension, axis));
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
			if (type.hasMultipliers) {
				method.append("relevantAsymmetricNeighborShareMultipliers, ");
			}
			if (type.hasSymmetries) {
				method.append("relevantAsymmetricNeighborSymmetryCounts, relevantNeighborCount, ");
			}
			method.append(regularCountVariable).append(");").append(getNL(ind0));
		} else {
			//add new grid slices parameter
			method.append("long").append(arrayBrackets).append(" newCurrent").append(firstAxisLabel).append("Slice, long")
			.append(arrayBrackets).append(" newGreater").append(firstAxisLabel).append("Slice) {").append(getNL(ind0));
			
			//TODO finish
			
		}		
		method.append("}");
		System.out.println(method);
	}
	
	public static void printFileBackedAetherTopplingMethods(int dimension) {
		List<AnysotropicVonNeumannNeighborhoodType> neighborhoodTypes = new ArrayList<AnysotropicVonNeumannNeighborhoodType>();
		int[] coordinates = new int[dimension];
		int size = (dimension - 1)*2 + 3;//it seems to work
		int sizeMinusOne = size - 1;
		int dimensionMinusOne = dimension - 1;
		int currentAxis = dimensionMinusOne;
		while (currentAxis > -1) {
			if (currentAxis == dimensionMinusOne) {
				getAnisotropicVonNeumannNeighborhoodTypes(coordinates, neighborhoodTypes);
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
			AnysotropicVonNeumannNeighborhoodType type = neighborhoodTypes.get(i);
			printFileBackedAetherTopplingMethod(type, num);
			System.out.println();
		}
	}
	
	private static void printFileBackedAetherTopplingMethod(AnysotropicVonNeumannNeighborhoodType type, int number) {
		//TODO finish
		int ind0 = 0;
		int ind1 = ind0 + 1;
		int ind2 = ind1 + 1;
		int dimension = type.coordinates.length;
		StringBuilder method = new StringBuilder(); 
		method.append("private static boolean topplePositionType").append(number).append("(");
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
			NeighborType neighbor = type.neighbors.get(i);
			String axisLabel = Utils.getUpperCaseAxisLabel(dimension, neighbor.axisIndex);
			String dir = neighbor.isPositiveDirection? "g" : "s";
			String varPrefix = dir + axisLabel;
			method.append("long ").append(varPrefix).append("Value, ");
			if (neighbor.symmetryCount == null) {
				method.append("int ").append(varPrefix).append("SymmetryCount, ");
			}
			if (neighbor.multiplier == null) {
				method.append("int ").append(varPrefix).append("ShareMultiplier, ");
			}
		}
		if (neighborCount > 2) {
			//add arrays to reuse
			method.append("long[] relevantAsymmetricNeighborValues, int[] sortedNeighborsIndexes, int[][] relevantAsymmetricNeighborCoords, ");
			if (type.hasMultipliers) {
				method.append("int[] relevantAsymmetricNeighborShareMultipliers, ");
			}
			if (type.hasSymmetries) {
				method.append("int[] relevantAsymmetricNeighborSymmetryCounts, ");
			}
			//add new grid parameter
			method.append("RandomAccessFile newGrid) throws IOException {").append(getNL(ind1));
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
				String axisLabel = Utils.getUpperCaseAxisLabel(dimension, neighbor.axisIndex);
				String dir = neighbor.isPositiveDirection? "g" : "s";
				String varPrefix = dir + axisLabel;
				String valueVarName = varPrefix + "Value";
				method.append("if (").append(valueVarName).append(" < currentValue) {").append(getNL(ind2))
				.append("relevantAsymmetricNeighborValues[").append(regularCountVariable).append("] = ").append(valueVarName).append(";").append(getNL(ind2))
				.append("int[] nc = relevantAsymmetricNeighborCoords[").append(regularCountVariable).append("];").append(getNL(ind2));
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
					method.append(";").append(getNL(ind2));
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
				method.append(";").append(getNL(ind2));
				axis++;
				for (; axis < dimension; axis++) {
					method.append("nc[").append(axis).append("] = ");
					coordinate = type.coordinates[axis];
					if (coordinate == null) {
						method.append(Utils.getAxisLabel(dimension, axis));
					} else {
						method.append(coordinate);
					}
					method.append(";").append(getNL(ind2));
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
			if (type.hasMultipliers) {
				method.append("relevantAsymmetricNeighborShareMultipliers, ");
			}
			if (type.hasSymmetries) {
				method.append("relevantAsymmetricNeighborSymmetryCounts, relevantNeighborCount, ");
			}
			method.append(regularCountVariable).append(");").append(getNL(ind0));
		} else {
			//add new grid slices parameter
			method.append("RandomAccessFile newGrid) throws IOException {").append(getNL(ind0));
			
			//TODO finish
			
		}		
		method.append("}");
		System.out.println(method);
	}
	
	private static class NeighborType {
		int axisIndex;
		boolean isPositiveDirection;
		
		//not used in isSame on purpose
		Integer symmetryCount = 1;
		Integer multiplier = 1;
		
		public NeighborType(int axisIndex, boolean isPositiveDirection) {
			this.axisIndex = axisIndex;
			this.isPositiveDirection = isPositiveDirection;
		}
		
		public boolean isSame(NeighborType other) {
			return other.axisIndex == axisIndex && other.isPositiveDirection == isPositiveDirection;
		}
	}
	
	private static class AnysotropicVonNeumannNeighborhoodType {
		boolean hasSymmetries = false;
		boolean hasMultipliers = false;
		List<NeighborType> neighbors = new ArrayList<NeighborType>();
		
		//not used in isSame on purpose
		Integer[] coordinates;
		
		public AnysotropicVonNeumannNeighborhoodType(int[] coordinates) {
			this.coordinates = new Integer[coordinates.length];
			for (int i = 0; i < coordinates.length; i++) {
				this.coordinates[i] = coordinates[i];
			}
		}
		
		public boolean isSame(AnysotropicVonNeumannNeighborhoodType other) {
			int neighborCount = other.neighbors.size();
			if (neighborCount != neighbors.size()) {
				return false;
			}
			for (int i = 0; i != neighborCount; i++) {
				if (!other.neighbors.get(i).isSame(neighbors.get(i))) {
					return false;
				}
			}
			return other.hasMultipliers == hasMultipliers && other.hasSymmetries == hasSymmetries;
		}
	}
	
	private static int getIndexOfSameType(List<AnysotropicVonNeumannNeighborhoodType> list, AnysotropicVonNeumannNeighborhoodType type) {
		for (int i = list.size() - 1; i != -1; i--) {
			if (list.get(i).isSame(type)) {
				return i;
			}
		}
		return -1;
	}
	
	private static void getAnisotropicVonNeumannNeighborhoodTypes(int[] coords, List<AnysotropicVonNeumannNeighborhoodType> neighborhoodTypes) {
		AnysotropicVonNeumannNeighborhoodType type = new AnysotropicVonNeumannNeighborhoodType(coords);
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
		int indexOfType = getIndexOfSameType(neighborhoodTypes, type);
		if (indexOfType == -1) {
			neighborhoodTypes.add(type);
		} else {
			AnysotropicVonNeumannNeighborhoodType generalType = neighborhoodTypes.get(indexOfType);
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
	
	public static void printAnisotropicPositionVonNeumannNeighborhood(int[] coords, boolean hideNeighborhood, boolean hideTypeB) {
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
		header.append("  ").append(Utils.getAxisLabel(dimension, 0)).append(" ");
		underline.append("------------");
		if (!hideNeighborhood) {
			underline.append("---------------");
		}
		if (!hideTypeB) {
			underline.append("---------");
		}
		for (int coord = 1; coord < dimension; coord++) {
			header.append("|  ").append(Utils.getAxisLabel(dimension, coord)).append(" ");
			underline.append("-----");
		}
		if (!hideNeighborhood) {
			header.append("| Neighborhood ");
		}
		header.append("| Type A");
		if (!hideTypeB) {
			header.append(" | Type B");
		}
		header.append(NL).append(underline);
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
					printAnisotropicPositionVonNeumannNeighborhood(coordinates, neighborhoodTypesA, neighborhoodTypesB, hideNeighborhood, hideTypeB);
					printed = true;
				} else {
					getAnisotropicPositionVonNeumannNeighborhoodTypes(coordinates, neighborhoodTypesA, neighborhoodTypesB);
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
	
	public static void printAnisotropicPositionsVonNeumannNeighborhoods(int dimension, int size, boolean hideNeighborhood, boolean hideTypeB) {
		StringBuilder header = new StringBuilder();
		StringBuilder underline = new StringBuilder();
		header.append("  ").append(Utils.getAxisLabel(dimension, 0)).append(" ");
		underline.append("------------");
		if (!hideNeighborhood) {
			underline.append("---------------");
		}
		if (!hideTypeB) {
			underline.append("---------");
		}
		for (int coord = 1; coord < dimension; coord++) {
			header.append("|  ").append(Utils.getAxisLabel(dimension, coord)).append(" ");
			underline.append("-----");
		}
		if (!hideNeighborhood) {
			header.append("| Neighborhood ");
		}
		header.append("| Type A");
		if (!hideTypeB) {
			header.append(" | Type B");
		}
		header.append(NL).append(underline);
		System.out.println(header);
		List<String> neighborhoodTypesA = new ArrayList<String>();
		List<String> neighborhoodTypesB = new ArrayList<String>();
		int[] coordinates = new int[dimension];
		int sizeMinusOne = size - 1;
		int dimensionMinusOne = dimension - 1;
		int currentAxis = dimensionMinusOne;
		while (currentAxis > -1) {
			if (currentAxis == dimensionMinusOne) {
				printAnisotropicPositionVonNeumannNeighborhood(coordinates, neighborhoodTypesA, neighborhoodTypesB, hideNeighborhood, hideTypeB);
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
	
	public static void printAnisotropicVonNeumannNeighborhoodTypesMap(int dimension, int size) {
		StringBuilder header = new StringBuilder();
		StringBuilder underline = new StringBuilder();
		header.append("Type A | Type B");
		underline.append("---------------");
		header.append(NL).append(underline);
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
				getAnisotropicVonNeumannNeighborhoodTypeMap(coordinates, neighborhoodTypesA, neighborhoodTypesB, typesMap);
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
	
	public static void printAnisotropicPositions(int dimension, int size) {
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

	private static void printAnisotropicPosition(int[] coords) {
		String[] strCoords = new String[coords.length];
		for (int axis = 0; axis < coords.length; axis++) {
			String coordLabel = Utils.getAxisLabel(coords.length, axis);
			strCoords[axis] = coordLabel + " = " + coords[axis];
		}		
		System.out.println(String.join(", ", strCoords));
	}
	
	private static void printAnisotropicPositionVonNeumannNeighborhood(int[] coords, List<String> neighborhoodTypesA, List<String> neighborhoodTypesB, 
			boolean hideNeighborhood, boolean hideTypeB) {
		List<String> neighbors = new ArrayList<String>();
		List<String> plainNeighbors = new ArrayList<String>();
		String[] strCoords = new String[coords.length];
		boolean hasSymmetries = false;
		boolean hasMultipliers = false;
		for (int axis = 0; axis < coords.length; axis++) {
			String coordLabel = Utils.getAxisLabel(coords.length, axis);
			strCoords[axis] = coords[axis] > 9 ? coords[axis] + "" : "0" + coords[axis];
			
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
				String neighbor = "G" + coordLabel.toUpperCase();
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
				String neighbor = "S" + coordLabel.toUpperCase();
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
		if (typeA == -1) {
			typeA = neighborhoodTypesA.size();
			neighborhoodTypesA.add(neighborhood);
		}
		typeA++;
		
		String plainNeighborhood = String.join(", ", plainNeighbors);
		plainNeighborhood += " " + hasSymmetries + hasMultipliers;
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
		System.out.println(" " + String.join(" | ", strCoords) + neighborhood + " | " + (typeA > 9 ? typeA : "0" + typeA) + strTypeB);
	}
	
	private static void getAnisotropicPositionVonNeumannNeighborhoodTypes(int[] coords, List<String> neighborhoodTypesA, List<String> neighborhoodTypesB) {
		List<String> neighbors = new ArrayList<String>();
		List<String> plainNeighbors = new ArrayList<String>();
		boolean hasSymmetries = false;
		boolean hasMultipliers = false;
		for (int axis = 0; axis < coords.length; axis++) {
			String coordLabel = Utils.getAxisLabel(coords.length, axis);			
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
				String neighbor = "G" + coordLabel.toUpperCase();
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
				String neighbor = "S" + coordLabel.toUpperCase();
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
		if (typeA == -1) {
			typeA = neighborhoodTypesA.size();
			neighborhoodTypesA.add(neighborhood);
		}		
		String plainNeighborhood = String.join(", ", plainNeighbors);
		plainNeighborhood += " " + hasSymmetries + hasMultipliers;
		int typeB = neighborhoodTypesB.indexOf(plainNeighborhood);
		if (typeB == -1) {
			typeB = neighborhoodTypesB.size();
			neighborhoodTypesB.add(plainNeighborhood);
		}
	}
	
	private static void getAnisotropicVonNeumannNeighborhoodTypeMap(int[] coords, List<String> neighborhoodTypesA, List<String> neighborhoodTypesB, Map<Integer, Integer> typesMap) {
		List<String> neighbors = new ArrayList<String>();
		List<String> plainNeighbors = new ArrayList<String>();
		boolean hasSymmetries = false;
		boolean hasMultipliers = false;
		for (int axis = 0; axis < coords.length; axis++) {
			String coordLabel = Utils.getAxisLabel(coords.length, axis);			
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
				String neighbor = "G" + coordLabel.toUpperCase();
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
				String neighbor = "S" + coordLabel.toUpperCase();
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
		if (!neighborhoodTypesA.contains(neighborhood)) {
			neighborhoodTypesA.add(neighborhood);
			int typeA = neighborhoodTypesA.size();
			
			String plainNeighborhood = String.join(", ", plainNeighbors);
			plainNeighborhood += " " + hasSymmetries + hasMultipliers;
			int typeB = neighborhoodTypesB.indexOf(plainNeighborhood);
			if (typeB == -1) {
				typeB = neighborhoodTypesB.size();
				neighborhoodTypesB.add(plainNeighborhood);
			}
			typeB++;
			
			typesMap.put(typeA, typeB);			
		}		
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
	
	private static int[] getAsymmetricCoords(int[] coords) {	
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
