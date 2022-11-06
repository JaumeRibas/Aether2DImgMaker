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


public class CodeGeneration {
	
	public static void main(String[] args) {
		printLabelMethodsForCrossSection(5, 1);
		printBoundsMethodsForCrossSection(5, 1);
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
			System.out.println("@Override" + System.lineSeparator() + "default int getMin" + currentAxisUpperCaseLabel 
					+ "() { return 0; }" + System.lineSeparator());
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
							System.out.println("@Override" + System.lineSeparator() 
							+ "default int getMin" + currentAxisUpperCaseLabel + "At" + otherAxesInMethodName + "(" + otherAxesParams 
							+ ") { return " + axisLabels[smallestGreaterAxis] + "; }" + System.lineSeparator());
						} else {
							System.out.println("@Override" + System.lineSeparator() 
							+ "default int getMin" + currentAxisUpperCaseLabel + "At" + otherAxesInMethodName + "(" + otherAxesParams 
							+ ") { return 0; }" + System.lineSeparator());
						}
						if (isThereASmallerAxis) {
							System.out.println("@Override" + System.lineSeparator() 
							+ "default int getMax" + currentAxisUpperCaseLabel + "At" + otherAxesInMethodName + "(" + otherAxesParams 
							+ ") { return Math.min(" + axisLabels[greatestSmallerAxis] + ", getMax" + currentAxisUpperCaseLabel + "()); }" + System.lineSeparator());
						} else {
							System.out.println("@Override" + System.lineSeparator() 
							+ "default int getMax" + currentAxisUpperCaseLabel + "At" + otherAxesInMethodName + "(" + otherAxesParams 
							+ ") { return getMax" + currentAxisUpperCaseLabel + "(); }" + System.lineSeparator());
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
					System.out.println("@Override" + System.lineSeparator() 
					+ "default int getMin" + currentAxisUpperCaseLabel + "(" + otherAxesParams 
					+ ") { return 0; }" + System.lineSeparator());
				} else {
					System.out.println("@Override" + System.lineSeparator() 
					+ "default int getMin" + currentAxisUpperCaseLabel + "(" + otherAxesParams 
					+ ") { return " + axisLabels[currentAxis + 1] + "; }" + System.lineSeparator());
				}
				if (currentAxis == 0) {
					System.out.println("@Override" + System.lineSeparator() 
					+ "default int getMax" + currentAxisUpperCaseLabel + "(" + otherAxesParams 
					+ ") { return getMax" + currentAxisUpperCaseLabel + "(); }" + System.lineSeparator());
				} else {
					System.out.println("@Override" + System.lineSeparator() 
					+ "default int getMax" + currentAxisUpperCaseLabel + "(" + otherAxesParams 
					+ ") { return Math.min(" + axisLabels[currentAxis - 1] + ", getMax" + currentAxisUpperCaseLabel + "()); }" + System.lineSeparator());
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
				System.out.println("@Override" + System.lineSeparator() + "default int getMin" + currentAxisUpperCaseLabel 
						+ "() { return -getAsymmetricMax" + firstAxisUpperCaseLabel + "(); }" + System.lineSeparator());
				System.out.println("@Override" + System.lineSeparator() + "default int getMax" + currentAxisUpperCaseLabel 
						+ "() { return getAsymmetricMax" + firstAxisUpperCaseLabel + "(); }" + System.lineSeparator());
				System.out.println("@Override" + System.lineSeparator() + "default int getAsymmetricMin" + currentAxisUpperCaseLabel 
						+ "() { return 0; }" + System.lineSeparator());
				if (currentAxis != 0) {
					System.out.println("@Override" + System.lineSeparator() + "default int getAsymmetricMax" + currentAxisUpperCaseLabel 
							+ "() { return getAsymmetricMax" + firstAxisUpperCaseLabel + "(); }" + System.lineSeparator());
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
								System.out.println("@Override" + System.lineSeparator() 
								+ "default int getAsymmetricMin" + currentAxisUpperCaseLabel + "At" + otherAxesInMethodName + "(" + otherAxesParams 
								+ ") { return " + axisLabels[smallestGreaterAxis] + "; }" + System.lineSeparator());
							} else {
								System.out.println("@Override" + System.lineSeparator() 
								+ "default int getAsymmetricMin" + currentAxisUpperCaseLabel + "At" + otherAxesInMethodName + "(" + otherAxesParams 
								+ ") { return 0; }" + System.lineSeparator());
							}
							if (isThereASmallerAxis) {
								System.out.println("@Override" + System.lineSeparator() 
								+ "default int getAsymmetricMax" + currentAxisUpperCaseLabel + "At" + otherAxesInMethodName + "(" + otherAxesParams 
								+ ") { return " + axisLabels[greatestSmallerAxis] + "; }" + System.lineSeparator());
							} else {
								System.out.println("@Override" + System.lineSeparator() 
								+ "default int getAsymmetricMax" + currentAxisUpperCaseLabel + "At" + otherAxesInMethodName + "(" + otherAxesParams 
								+ ") { return getAsymmetricMax" + firstAxisUpperCaseLabel + "(); }" + System.lineSeparator());
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
						System.out.println("@Override" + System.lineSeparator() 
						+ "default int getAsymmetricMin" + currentAxisUpperCaseLabel + "(" + otherAxesParams 
						+ ") { return 0; }" + System.lineSeparator());
					} else {
						System.out.println("@Override" + System.lineSeparator() 
						+ "default int getAsymmetricMin" + currentAxisUpperCaseLabel + "(" + otherAxesParams 
						+ ") { return " + axisLabels[currentAxis + 1] + "; }" + System.lineSeparator());
					}
					if (currentAxis == 0) {
						System.out.println("@Override" + System.lineSeparator() 
						+ "default int getAsymmetricMax" + currentAxisUpperCaseLabel + "(" + otherAxesParams 
						+ ") { return getAsymmetricMax" + currentAxisUpperCaseLabel + "(); }" + System.lineSeparator());
					} else {
						System.out.println("@Override" + System.lineSeparator() 
						+ "default int getAsymmetricMax" + currentAxisUpperCaseLabel + "(" + otherAxesParams 
						+ ") { return " + axisLabels[currentAxis - 1] + "; }" + System.lineSeparator());
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
			System.out.println("@Override" + System.lineSeparator() + "public int getMin" + currentAxisUpperCaseLabel 
					+ "() { return source.getAsymmetricMin" + currentAxisUpperCaseLabel + "(); }" + System.lineSeparator());
			System.out.println("@Override" + System.lineSeparator() + "public int getMax" + currentAxisUpperCaseLabel 
					+ "() { return source.getAsymmetricMax" + currentAxisUpperCaseLabel + "(); }" + System.lineSeparator());
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
						System.out.println("@Override" + System.lineSeparator() 
						+ "public int getMin" + currentAxisUpperCaseLabel + "At" + otherAxesInMethodName + "(" + otherAxesParams 
						+ ") { return source.getAsymmetricMin" + currentAxisUpperCaseLabel + "At" + otherAxesInMethodName + "(" + otherAxesCsv + "); }" + System.lineSeparator());
						System.out.println("@Override" + System.lineSeparator() 
						+ "public int getMax" + currentAxisUpperCaseLabel + "At" + otherAxesInMethodName + "(" + otherAxesParams 
						+ ") { return source.getAsymmetricMax" + currentAxisUpperCaseLabel + "At" + otherAxesInMethodName + "(" + otherAxesCsv + "); }" + System.lineSeparator());
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
				System.out.println("@Override" + System.lineSeparator() 
				+ "public int getMin" + currentAxisUpperCaseLabel + "(" + otherAxesParams 
				+ ") { return source.getAsymmetricMin" + currentAxisUpperCaseLabel + "(" + otherAxesCsv + "); }" + System.lineSeparator());
				System.out.println("@Override" + System.lineSeparator() 
				+ "public int getMax" + currentAxisUpperCaseLabel + "(" + otherAxesParams 
				+ ") { return source.getAsymmetricMax" + currentAxisUpperCaseLabel + "(" + otherAxesCsv + "); }" + System.lineSeparator());	
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
			System.out.println("@Override" + System.lineSeparator() + "public int getMin" + currentAxisUpperCaseLabel 
					+ "() { return source.getMin" + currentAxisUpperCaseLabel + "(); }" + System.lineSeparator());
			System.out.println("@Override" + System.lineSeparator() + "public int getMax" + currentAxisUpperCaseLabel 
					+ "() { return source.getMax" + currentAxisUpperCaseLabel + "(); }" + System.lineSeparator());
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
						System.out.println("@Override" + System.lineSeparator() 
						+ "public int getMin" + currentAxisUpperCaseLabel + "At" + otherAxesInMethodName + "(" + otherAxesParams 
						+ ") { return source.getMin" + currentAxisUpperCaseLabel + "At" + otherAxesInMethodName + "(" + otherAxesCsv + "); }" + System.lineSeparator());
						System.out.println("@Override" + System.lineSeparator() 
						+ "public int getMax" + currentAxisUpperCaseLabel + "At" + otherAxesInMethodName + "(" + otherAxesParams 
						+ ") { return source.getMax" + currentAxisUpperCaseLabel + "At" + otherAxesInMethodName + "(" + otherAxesCsv + "); }" + System.lineSeparator());
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
				System.out.println("@Override" + System.lineSeparator() 
				+ "public int getMin" + currentAxisUpperCaseLabel + "(" + otherAxesParams 
				+ ") { return source.getMin" + currentAxisUpperCaseLabel + "(" + otherAxesCsv + "); }" + System.lineSeparator());
				System.out.println("@Override" + System.lineSeparator() 
				+ "public int getMax" + currentAxisUpperCaseLabel + "(" + otherAxesParams 
				+ ") { return source.getMax" + currentAxisUpperCaseLabel + "(" + otherAxesCsv + "); }" + System.lineSeparator());	
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
			System.out.println("@Override" + System.lineSeparator() + "public int getMin" + currentAxisUpperCaseLabel 
					+ "() {" + System.lineSeparator() + System.lineSeparator() + "}" + System.lineSeparator());
			System.out.println("@Override" + System.lineSeparator() + "public int getMax" + currentAxisUpperCaseLabel 
					+ "() {" + System.lineSeparator() + System.lineSeparator() + "}" + System.lineSeparator());
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
						System.out.println("@Override" + System.lineSeparator() 
						+ "public int getMin" + currentAxisUpperCaseLabel + "At" + otherAxesInMethodName + "(" + otherAxesParams 
						+ ") {" + System.lineSeparator() + System.lineSeparator() + "}" + System.lineSeparator());
						System.out.println("@Override" + System.lineSeparator() 
						+ "public int getMax" + currentAxisUpperCaseLabel + "At" + otherAxesInMethodName + "(" + otherAxesParams 
						+ ") {" + System.lineSeparator() + System.lineSeparator() + "}" + System.lineSeparator());
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
				System.out.println("@Override" + System.lineSeparator() 
				+ "public int getMin" + currentAxisUpperCaseLabel + "(" + otherAxesParams 
				+ ") {" + System.lineSeparator() + System.lineSeparator() + "}" + System.lineSeparator());
				System.out.println("@Override" + System.lineSeparator() 
				+ "public int getMax" + currentAxisUpperCaseLabel + "(" + otherAxesParams 
				+ ") {" + System.lineSeparator() + System.lineSeparator() + "}" + System.lineSeparator());	
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
			System.out.println("@Override" + System.lineSeparator() + "public int getMin" + currentAxisUpperCaseLabel 
					+ "() { return min" + currentAxisUpperCaseLabel + "; }" + System.lineSeparator());
			System.out.println("@Override" + System.lineSeparator() + "public int getMax" + currentAxisUpperCaseLabel 
					+ "() { return max" + currentAxisUpperCaseLabel + "; }" + System.lineSeparator());
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
						System.out.println("@Override" + System.lineSeparator() 
						+ "public int getMin" + currentAxisUpperCaseLabel + "At" + otherAxesInMethodName + "(" + otherAxesParams + ") { return Math.max(min" + currentAxisUpperCaseLabel 
						+ ", source.getMin" + currentAxisUpperCaseLabel + "At" + otherAxesInMethodName + "(" + otherAxesCsv + ")); }" + System.lineSeparator());
						System.out.println("@Override" + System.lineSeparator() 
						+ "public int getMax" + currentAxisUpperCaseLabel + "At" + otherAxesInMethodName + "(" + otherAxesParams + ") { return Math.min(max" + currentAxisUpperCaseLabel 
						+ ", source.getMax" + currentAxisUpperCaseLabel + "At" + otherAxesInMethodName + "(" + otherAxesCsv + ")); }" + System.lineSeparator());
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
				System.out.println("@Override" + System.lineSeparator() 
				+ "public int getMin" + currentAxisUpperCaseLabel + "(" + otherAxesParams + ") { return Math.max(min" + currentAxisUpperCaseLabel 
				+ ", source.getMin" + currentAxisUpperCaseLabel + "(" + otherAxesCsv + ")); }" + System.lineSeparator());
				System.out.println("@Override" + System.lineSeparator() 
				+ "public int getMax" + currentAxisUpperCaseLabel + "(" + otherAxesParams + ") { return Math.min(max" + currentAxisUpperCaseLabel 
				+ ", source.getMax" + currentAxisUpperCaseLabel + "(" + otherAxesCsv + ")); }" + System.lineSeparator());		
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
			System.out.println("@Override" + System.lineSeparator() + "public int getMin" + currentAxisUpperCaseLabel 
					+ "() {" + getNL(1) + "return source.getMinCoordinate(" + i + ");" + System.lineSeparator() + "}" + System.lineSeparator());
			System.out.println("@Override" + System.lineSeparator() + "public int getMax" + currentAxisUpperCaseLabel 
					+ "() {" + getNL(1) + "return source.getMaxCoordinate(" + i + ");" + System.lineSeparator() + "}" + System.lineSeparator());
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
						System.out.println("@Override" + System.lineSeparator() 
						+ "public int getMin" + currentAxisUpperCaseLabel + "At" + otherAxesInMethodName + "(" + otherAxesParams 
						+ ") {" + getNL(1) + "return source.getMinCoordinate(" + i + ", new PartialCoordinates(" + partialCoordinatesCsv + "));" 
						+ System.lineSeparator() + "}" + System.lineSeparator());
						System.out.println("@Override" + System.lineSeparator() 
						+ "public int getMax" + currentAxisUpperCaseLabel + "At" + otherAxesInMethodName + "(" + otherAxesParams 
						+ ") {" + getNL(1) + "return source.getMaxCoordinate(" + i + ", new PartialCoordinates(" + partialCoordinatesCsv + "));" 
						+ System.lineSeparator() + "}" + System.lineSeparator());
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
				System.out.println("@Override" + System.lineSeparator() 
				+ "public int getMin" + currentAxisUpperCaseLabel + "(" + otherAxesParams 
				+ ") {" + getNL(1) + "return source.getMinCoordinate(" + i + ", new PartialCoordinates(" + partialCoordinatesCsv + "));" 
				+ System.lineSeparator() + "}" + System.lineSeparator());
				System.out.println("@Override" + System.lineSeparator() 
				+ "public int getMax" + currentAxisUpperCaseLabel + "(" + otherAxesParams 
				+ ") {" + getNL(1) + "return source.getMaxCoordinate(" + i + ", new PartialCoordinates(" + partialCoordinatesCsv + "));" 
				+ System.lineSeparator() + "}" + System.lineSeparator());	
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
			System.out.println("int getAsymmetricMin" + currentAxisUpperCaseLabel + "();" + System.lineSeparator());
			System.out.println("int getAsymmetricMax" + currentAxisUpperCaseLabel + "();" + System.lineSeparator());
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
						System.out.println("int getAsymmetricMin" + currentAxisUpperCaseLabel + "At" + otherAxesInMethodName + "(" + otherAxesParams + ");" + System.lineSeparator());
						System.out.println("int getAsymmetricMax" + currentAxisUpperCaseLabel + "At" + otherAxesInMethodName + "(" + otherAxesParams + ");" + System.lineSeparator());
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
				System.out.println("int getAsymmetricMin" + currentAxisUpperCaseLabel + "(" + otherAxesParams + ");" + System.lineSeparator());
				System.out.println("int getAsymmetricMax" + currentAxisUpperCaseLabel + "(" + otherAxesParams + ");" + System.lineSeparator());
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
			System.out.println("int getMin" + currentAxisUpperCaseLabel + "();" + System.lineSeparator());
			System.out.println("int getMax" + currentAxisUpperCaseLabel + "();" + System.lineSeparator());
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
						System.out.println("int getMin" + currentAxisUpperCaseLabel + "At" + otherAxesInMethodName + "(" + otherAxesParams + ");" + System.lineSeparator());
						System.out.println("int getMax" + currentAxisUpperCaseLabel + "At" + otherAxesInMethodName + "(" + otherAxesParams + ");" + System.lineSeparator());
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
				System.out.println("int getMin" + currentAxisUpperCaseLabel + "(" + otherAxesParams + ");" + System.lineSeparator());
				System.out.println("int getMax" + currentAxisUpperCaseLabel + "(" + otherAxesParams + ");" + System.lineSeparator());				
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
			System.out.println("int getMin" + currentAxisUpperCaseLabel + "();" + System.lineSeparator());
			System.out.println("int getMax" + currentAxisUpperCaseLabel + "();" + System.lineSeparator());
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
						System.out.println("default int getMin" + currentAxisUpperCaseLabel + "At" + otherAxesInMethodName + "(" + otherAxesParams + ") { return getMin" + currentAxisUpperCaseLabel + "(); }" + System.lineSeparator());
						System.out.println("default int getMax" + currentAxisUpperCaseLabel + "At" + otherAxesInMethodName + "(" + otherAxesParams + ") { return getMax" + currentAxisUpperCaseLabel + "(); }" + System.lineSeparator());
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
				System.out.println("default int getMin" + currentAxisUpperCaseLabel + "(" + otherAxesParams + ") { return getMin" + currentAxisUpperCaseLabel + "(); }" + System.lineSeparator());
				System.out.println("default int getMax" + currentAxisUpperCaseLabel + "(" + otherAxesParams + ") { return getMax" + currentAxisUpperCaseLabel + "(); }" + System.lineSeparator());
			}
		}
	}
	
	public static void printLabelMethodsForCrossSection(int dimension, int crossSectionAxis) {
		int crossSectionDimension = dimension - 1;
		int axis = 0;
		for (; axis != crossSectionAxis; axis++) {
			String sourceAxisLabel = Utils.getUpperCaseAxisLabel(dimension, axis);
			String crossSectionAxisLabel = Utils.getUpperCaseAxisLabel(crossSectionDimension, axis);
			System.out.println("@Override" + System.lineSeparator() + "public String get" + crossSectionAxisLabel + "Label() {" + System.lineSeparator() 
			+ "    return source.get" + sourceAxisLabel + "Label();" + System.lineSeparator() + "}" + System.lineSeparator());
		}
		for (int sourceAxis = axis + 1; axis != crossSectionDimension; axis = sourceAxis, sourceAxis++) {
			String sourceAxisLabel = Utils.getUpperCaseAxisLabel(dimension, sourceAxis);
			String crossSectionAxisLabel = Utils.getUpperCaseAxisLabel(crossSectionDimension, axis);
			System.out.println("@Override" + System.lineSeparator() + "public String get" + crossSectionAxisLabel + "Label() {" + System.lineSeparator() 
			+ "    return source.get" + sourceAxisLabel + "Label();" + System.lineSeparator() + "}" + System.lineSeparator());
		}
	}
	
	public static void printBoundsMethodsForCrossSection(int dimension, int crossSectionAxis) {//TODO
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
			System.out.println("@Override" + System.lineSeparator() + "public int getMin" + currentAxisUpperCaseLabel 
					+ "() {" + System.lineSeparator() + "    " 
					+ getBoundMethodContentForCrossSection(dimension, crossSectionAxis, i, emptyArray, true, 
							crossSectionAxisLabel, sourceAxisUpperCaseLabels, axisLabels) + System.lineSeparator() + "}" + System.lineSeparator());
			System.out.println("@Override" + System.lineSeparator() + "public int getMax" + currentAxisUpperCaseLabel 
					+ "() {" + System.lineSeparator() + "    " 
					+ getBoundMethodContentForCrossSection(dimension, crossSectionAxis, i, emptyArray, false, 
							crossSectionAxisLabel, sourceAxisUpperCaseLabels, axisLabels) + System.lineSeparator() + "}" + System.lineSeparator());
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
						System.out.println("@Override" + System.lineSeparator() 
						+ "public int getMin" + currentAxisUpperCaseLabel + "At" + otherAxesInMethodName + "(" + otherAxesParams 
						+ ") {" + System.lineSeparator() + "    " 
						+ getBoundMethodContentForCrossSection(dimension, crossSectionAxis, i, otherAxesUsed, true, 
								crossSectionAxisLabel, sourceAxisUpperCaseLabels, axisLabels) + System.lineSeparator() + "}" + System.lineSeparator());
						System.out.println("@Override" + System.lineSeparator() 
						+ "public int getMax" + currentAxisUpperCaseLabel + "At" + otherAxesInMethodName + "(" + otherAxesParams 
						+ ") {" + System.lineSeparator() + "    " 
						+ getBoundMethodContentForCrossSection(dimension, crossSectionAxis, i, otherAxesUsed, false, 
								crossSectionAxisLabel, sourceAxisUpperCaseLabels, axisLabels) + System.lineSeparator() + "}" + System.lineSeparator());
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
				System.out.println("@Override" + System.lineSeparator() 
				+ "public int getMin" + currentAxisUpperCaseLabel + "(" + otherAxesParams 
				+ ") {" + System.lineSeparator() + "    " 
				+ getBoundMethodContentForCrossSection(dimension, crossSectionAxis, i, otherAxes, true, 
						crossSectionAxisLabel, sourceAxisUpperCaseLabels, axisLabels) + System.lineSeparator() + "}" + System.lineSeparator());
				System.out.println("@Override" + System.lineSeparator() 
				+ "public int getMax" + currentAxisUpperCaseLabel + "(" + otherAxesParams 
				+ ") {" + System.lineSeparator() + "    " 
				+ getBoundMethodContentForCrossSection(dimension, crossSectionAxis, i, otherAxes, false, 
						crossSectionAxisLabel, sourceAxisUpperCaseLabels, axisLabels) + System.lineSeparator() + "}" + System.lineSeparator());
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
		String result = System.lineSeparator();
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
		header.append(System.lineSeparator()).append(underline);
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
		header.append(System.lineSeparator()).append(underline);
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
