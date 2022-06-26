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

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Consumer;

import org.apache.commons.math3.FieldElement;
import cellularautomata.automata.aether.Aether2D;
import cellularautomata.automata.aether.Aether3DEnclosed;
import cellularautomata.automata.aether.Aether3DEnclosed2;
import cellularautomata.automata.aether.IntAether3DRandomConfiguration;
import cellularautomata.automata.aether.Aether4D;
import cellularautomata.automata.aether.AetherSimple2D;
import cellularautomata.automata.aether.AetherSimple5D;
import cellularautomata.automata.aether.IntAether3D;
import cellularautomata.automata.aether.IntAether4D;
import cellularautomata.automata.siv.SpreadIntegerValue2D;
import cellularautomata.automata.siv.SpreadIntegerValueSimple2D;
import cellularautomata.model.Model;
import cellularautomata.model.IntModel;
import cellularautomata.model.LongModel;
import cellularautomata.model.NumericModel;
import cellularautomata.model1d.LongModel1D;
import cellularautomata.model1d.NumericModel1D;
import cellularautomata.model2d.ArrayIntGrid2D;
import cellularautomata.model2d.ArrayLongGrid2D;
import cellularautomata.model2d.ArrayNumberGrid2D;
import cellularautomata.model2d.Model2D;
import cellularautomata.model2d.IntModel2D;
import cellularautomata.model2d.LongModel2D;
import cellularautomata.model2d.NumericModel2D;
import cellularautomata.model3d.Model3D;
import cellularautomata.model3d.IntModel3D;
import cellularautomata.model3d.LongModel3D;
import cellularautomata.model3d.NumericModel3D;
import cellularautomata.model3d.RegularIntGrid3D;
import cellularautomata.model4d.IntModel4D;
import cellularautomata.model4d.LongModel4D;
import cellularautomata.model4d.NumericModel4D;
import cellularautomata.model4d.RegularIntGrid4D;
import cellularautomata.model5d.IntModel5D;
import cellularautomata.model5d.LongModel5D;
import cellularautomata.model5d.RegularIntGrid5D;
import cellularautomata.numbers.BigInt;

public class Test {
	
	public static void main(String[] args) throws Exception {
		long initialValue = -3000;
		Aether2D ae1 = new Aether2D(initialValue);
		AetherSimple2D ae2 = new AetherSimple2D(initialValue);
		compareAllSteps(ae1, ae2);
	}
	
	public static void test2DModelMinAndMaxValues() throws Exception {
		int side = 20;
		ThreadLocalRandom random = ThreadLocalRandom.current();
		String[] names = new String[] { "Global", "Even", "Odd", "Even X", "Odd X", "Even Y", "Odd Y" };
		//int 
		System.out.println("Testing IntModel2D");
		int[][] intValues = new int[side][side];
		ArrayIntGrid2D intGrid = new ArrayIntGrid2D(0, new int[side], intValues);
		int[][] intMinAndMaxToCompare = new int[7][2];
		for (int i = 0; i < intMinAndMaxToCompare.length; i++) {
			intMinAndMaxToCompare[i][0] = Integer.MAX_VALUE;
			intMinAndMaxToCompare[i][1] = Integer.MIN_VALUE;
		}
		for (int x = 0; x < side; x++) {
			for (int y = 0; y < side; y++) {
				boolean isEvenPosition = (x+y)%2 == 0;
				boolean isEvenX = x%2 == 0;
				boolean isEvenY = y%2 == 0;
				int value = random.nextInt();
				intValues[x][y] = value;
				int i = 0; //all positions
				if (value < intMinAndMaxToCompare[i][0])
					intMinAndMaxToCompare[i][0] = value;
				if (value > intMinAndMaxToCompare[i][1])
					intMinAndMaxToCompare[i][1] = value;
				if (isEvenPosition) {
					i = 1;//even
					if (value < intMinAndMaxToCompare[i][0])
						intMinAndMaxToCompare[i][0] = value;
					if (value > intMinAndMaxToCompare[i][1])
						intMinAndMaxToCompare[i][1] = value;
				} else {
					i = 2;//odd
					if (value < intMinAndMaxToCompare[i][0])
						intMinAndMaxToCompare[i][0] = value;
					if (value > intMinAndMaxToCompare[i][1])
						intMinAndMaxToCompare[i][1] = value;
				}
				if (isEvenX) {
					i = 3;//even x
					if (value < intMinAndMaxToCompare[i][0])
						intMinAndMaxToCompare[i][0] = value;
					if (value > intMinAndMaxToCompare[i][1])
						intMinAndMaxToCompare[i][1] = value;
				} else {
					i = 4;//odd x
					if (value < intMinAndMaxToCompare[i][0])
						intMinAndMaxToCompare[i][0] = value;
					if (value > intMinAndMaxToCompare[i][1])
						intMinAndMaxToCompare[i][1] = value;
				}
				if (isEvenY) {
					i = 5;//even y
					if (value < intMinAndMaxToCompare[i][0])
						intMinAndMaxToCompare[i][0] = value;
					if (value > intMinAndMaxToCompare[i][1])
						intMinAndMaxToCompare[i][1] = value;
				} else {
					i = 6;//odd y
					if (value < intMinAndMaxToCompare[i][0])
						intMinAndMaxToCompare[i][0] = value;
					if (value > intMinAndMaxToCompare[i][1])
						intMinAndMaxToCompare[i][1] = value;
				}
			}
		}
		int[][] intMinAndMax = new int[7][2];
		intMinAndMax[0] = intGrid.getMinAndMax();
		intMinAndMax[1] = intGrid.getEvenOddPositionsMinAndMax(true);
		intMinAndMax[2] = intGrid.getEvenOddPositionsMinAndMax(false);
		intMinAndMax[3] = intGrid.getMinAndMaxAtEvenOddX(true);
		intMinAndMax[4] = intGrid.getMinAndMaxAtEvenOddX(false);
		intMinAndMax[5] = intGrid.getMinAndMaxAtEvenOddY(true);
		intMinAndMax[6] = intGrid.getMinAndMaxAtEvenOddY(false);
		for (int i = 0; i < intMinAndMaxToCompare.length; i++) {
			if (intMinAndMax[i][0] != intMinAndMaxToCompare[i][0] || intMinAndMax[i][1] != intMinAndMaxToCompare[i][1]) {
				System.out.println(names[i] + " min and max don't match [" + intMinAndMax[i][0] + ", " + intMinAndMax[i][1] + "] != [" + intMinAndMaxToCompare[i][0] + ", " + intMinAndMaxToCompare[i][1] + "]");
				return;
			}
		}
		
		//long
		System.out.println("Testing LongModel2D");
		long[][] longValues = new long[side][side];
		ArrayLongGrid2D longGrid = new ArrayLongGrid2D(0, new int[side], longValues);
		long[][] longMinAndMaxToCompare = new long[7][2];
		for (int i = 0; i < longMinAndMaxToCompare.length; i++) {
			longMinAndMaxToCompare[i][0] = Long.MAX_VALUE;
			longMinAndMaxToCompare[i][1] = Long.MIN_VALUE;
		}
		for (int x = 0; x < side; x++) {
			for (int y = 0; y < side; y++) {
				boolean isEvenPosition = (x+y)%2 == 0;
				boolean isEvenX = x%2 == 0;
				boolean isEvenY = y%2 == 0;
				long value = random.nextLong();
				longValues[x][y] = value;
				int i = 0; //all positions
				if (value < longMinAndMaxToCompare[i][0])
					longMinAndMaxToCompare[i][0] = value;
				if (value > longMinAndMaxToCompare[i][1])
					longMinAndMaxToCompare[i][1] = value;
				if (isEvenPosition) {
					i = 1;//even
					if (value < longMinAndMaxToCompare[i][0])
						longMinAndMaxToCompare[i][0] = value;
					if (value > longMinAndMaxToCompare[i][1])
						longMinAndMaxToCompare[i][1] = value;
				} else {
					i = 2;//odd
					if (value < longMinAndMaxToCompare[i][0])
						longMinAndMaxToCompare[i][0] = value;
					if (value > longMinAndMaxToCompare[i][1])
						longMinAndMaxToCompare[i][1] = value;
				}
				if (isEvenX) {
					i = 3;//even x
					if (value < longMinAndMaxToCompare[i][0])
						longMinAndMaxToCompare[i][0] = value;
					if (value > longMinAndMaxToCompare[i][1])
						longMinAndMaxToCompare[i][1] = value;
				} else {
					i = 4;//odd x
					if (value < longMinAndMaxToCompare[i][0])
						longMinAndMaxToCompare[i][0] = value;
					if (value > longMinAndMaxToCompare[i][1])
						longMinAndMaxToCompare[i][1] = value;
				}
				if (isEvenY) {
					i = 5;//even y
					if (value < longMinAndMaxToCompare[i][0])
						longMinAndMaxToCompare[i][0] = value;
					if (value > longMinAndMaxToCompare[i][1])
						longMinAndMaxToCompare[i][1] = value;
				} else {
					i = 6;//odd y
					if (value < longMinAndMaxToCompare[i][0])
						longMinAndMaxToCompare[i][0] = value;
					if (value > longMinAndMaxToCompare[i][1])
						longMinAndMaxToCompare[i][1] = value;
				}
			}
		}
		long[][] longMinAndMax = new long[7][2];
		longMinAndMax[0] = longGrid.getMinAndMax();
		longMinAndMax[1] = longGrid.getEvenOddPositionsMinAndMax(true);
		longMinAndMax[2] = longGrid.getEvenOddPositionsMinAndMax(false);
		longMinAndMax[3] = longGrid.getMinAndMaxAtEvenOddX(true);
		longMinAndMax[4] = longGrid.getMinAndMaxAtEvenOddX(false);
		longMinAndMax[5] = longGrid.getMinAndMaxAtEvenOddY(true);
		longMinAndMax[6] = longGrid.getMinAndMaxAtEvenOddY(false);
		for (int i = 0; i < longMinAndMaxToCompare.length; i++) {
			if (longMinAndMax[i][0] != longMinAndMaxToCompare[i][0] || longMinAndMax[i][1] != longMinAndMaxToCompare[i][1]) {
				System.out.println(names[i] + " min and max don't match [" + longMinAndMax[i][0] + ", " + longMinAndMax[i][1] + "] != [" + longMinAndMaxToCompare[i][0] + ", " + longMinAndMaxToCompare[i][1] + "]");
				return;
			}
		}

		//Numeric
		System.out.println("Testing NumericModel2D");
		BigInt[][] bigIntValues = new BigInt[side][side];
		ArrayNumberGrid2D<BigInt> bigIntGrid = new ArrayNumberGrid2D<BigInt>(0, new int[side], bigIntValues);
		BigInt[][] bigIntMinAndMaxToCompare = new BigInt[7][2];
		for (int i = 0; i < bigIntMinAndMaxToCompare.length; i++) {
			bigIntMinAndMaxToCompare[i][0] = BigInt.valueOf(Long.MAX_VALUE);
			bigIntMinAndMaxToCompare[i][1] = BigInt.valueOf(Long.MIN_VALUE);
		}
		for (int x = 0; x < side; x++) {
			for (int y = 0; y < side; y++) {
				boolean isEvenPosition = (x+y)%2 == 0;
				boolean isEvenX = x%2 == 0;
				boolean isEvenY = y%2 == 0;
				BigInt value = BigInt.valueOf(random.nextLong());
				bigIntValues[x][y] = value;
				int i = 0; //all positions
				if (value.compareTo(bigIntMinAndMaxToCompare[i][0]) < 0)
					bigIntMinAndMaxToCompare[i][0] = value;
				if (value.compareTo(bigIntMinAndMaxToCompare[i][1]) > 0)
					bigIntMinAndMaxToCompare[i][1] = value;
				if (isEvenPosition) {
					i = 1;//even
					if (value.compareTo(bigIntMinAndMaxToCompare[i][0]) < 0)
						bigIntMinAndMaxToCompare[i][0] = value;
					if (value.compareTo(bigIntMinAndMaxToCompare[i][1]) > 0)
						bigIntMinAndMaxToCompare[i][1] = value;
				} else {
					i = 2;//odd
					if (value.compareTo(bigIntMinAndMaxToCompare[i][0]) < 0)
						bigIntMinAndMaxToCompare[i][0] = value;
					if (value.compareTo(bigIntMinAndMaxToCompare[i][1]) > 0)
						bigIntMinAndMaxToCompare[i][1] = value;
				}
				if (isEvenX) {
					i = 3;//even x
					if (value.compareTo(bigIntMinAndMaxToCompare[i][0]) < 0)
						bigIntMinAndMaxToCompare[i][0] = value;
					if (value.compareTo(bigIntMinAndMaxToCompare[i][1]) > 0)
						bigIntMinAndMaxToCompare[i][1] = value;
				} else {
					i = 4;//odd x
					if (value.compareTo(bigIntMinAndMaxToCompare[i][0]) < 0)
						bigIntMinAndMaxToCompare[i][0] = value;
					if (value.compareTo(bigIntMinAndMaxToCompare[i][1]) > 0)
						bigIntMinAndMaxToCompare[i][1] = value;
				}
				if (isEvenY) {
					i = 5;//even y
					if (value.compareTo(bigIntMinAndMaxToCompare[i][0]) < 0)
						bigIntMinAndMaxToCompare[i][0] = value;
					if (value.compareTo(bigIntMinAndMaxToCompare[i][1]) > 0)
						bigIntMinAndMaxToCompare[i][1] = value;
				} else {
					i = 6;//odd y
					if (value.compareTo(bigIntMinAndMaxToCompare[i][0]) < 0)
						bigIntMinAndMaxToCompare[i][0] = value;
					if (value.compareTo(bigIntMinAndMaxToCompare[i][1]) > 0)
						bigIntMinAndMaxToCompare[i][1] = value;
				}
			}
		}
		@SuppressWarnings("unchecked")
		MinAndMax<BigInt>[] bigIntMinAndMax = (MinAndMax<BigInt>[]) Array.newInstance(new MinAndMax<BigInt>(BigInt.ZERO, BigInt.ONE).getClass(), 7);
		bigIntMinAndMax[0] = bigIntGrid.getMinAndMax();
		bigIntMinAndMax[1] = bigIntGrid.getEvenOddPositionsMinAndMax(true);
		bigIntMinAndMax[2] = bigIntGrid.getEvenOddPositionsMinAndMax(false);
		bigIntMinAndMax[3] = bigIntGrid.getMinAndMaxAtEvenOddX(true);
		bigIntMinAndMax[4] = bigIntGrid.getMinAndMaxAtEvenOddX(false);
		bigIntMinAndMax[5] = bigIntGrid.getMinAndMaxAtEvenOddY(true);
		bigIntMinAndMax[6] = bigIntGrid.getMinAndMaxAtEvenOddY(false);
		for (int i = 0; i < bigIntMinAndMaxToCompare.length; i++) {
			if (!bigIntMinAndMax[i].getMin().equals(bigIntMinAndMaxToCompare[i][0]) || !bigIntMinAndMax[i].getMax().equals(bigIntMinAndMaxToCompare[i][1])) {
				System.out.println(names[i] + " min and max don't match [" + bigIntMinAndMax[i].getMin() + ", " + bigIntMinAndMax[i].getMax() + "] != [" + bigIntMinAndMaxToCompare[i][0] + ", " + bigIntMinAndMaxToCompare[i][1] + "]");
				return;
			}
		}
		System.out.println("Min and max values match!");
	}
	
	public static void testBoundsConsistency(Model2D grid) {
		List<String> edgePositions = new ArrayList<String>();
		//xy
		int x = grid.getMinX();
		int maxX = grid.getMaxX();
		int localMaxY = grid.getMaxY(x);
		for (int y = grid.getMinY(x); y <= localMaxY; y++) {
			edgePositions.add(x + "," + y);
		}
		for (x++; x < maxX; x++) {
			int localMinY = grid.getMinY(x);
			localMaxY = grid.getMaxY(x);
			edgePositions.add(x + "," + localMinY);
			if (localMinY != localMaxY) {
				edgePositions.add(x + "," + localMaxY);
			}
		}
		if (x == maxX) {
			localMaxY = grid.getMaxY(x);
			for (int y = grid.getMinY(x); y <= localMaxY; y++) {
				edgePositions.add(x + "," + y);
			}
		}
		//yx
		String errorMessage = "Inconsistency found in bounds in yx traversal";
		int positionCount = 0;
		int y = grid.getMinY();
		int maxY = grid.getMaxY();
		int localMaxX = grid.getMaxX(y);
		for (x = grid.getMinX(y); x <= localMaxX; x++) {
			positionCount++;
			if (!edgePositions.contains(x + "," + y)) {
				System.out.println(errorMessage);
			}
		}
		for (y++; y < maxY; y++) {
			int localMinX = grid.getMinX(y);
			localMaxX = grid.getMaxX(y);
			positionCount++;
			if (!edgePositions.contains(localMinX + "," + y)) {
				System.out.println(errorMessage);
			}
			if (localMinX != localMaxX) {
				positionCount++;
				if (!edgePositions.contains(localMaxX + "," + y)) {
					System.out.println(errorMessage);
				}
			}
		}
		if (y == maxY) {
			localMaxX = grid.getMaxX(y);
			for (x = grid.getMinX(y); x <= localMaxX; x++) {
				positionCount++;
				if (!edgePositions.contains(x + "," + y)) {
					System.out.println(errorMessage);
				}
			}
		}
		if (edgePositions.size() != positionCount) {
			System.out.println(errorMessage);
		}
	}
	
	public static void testBoundsConsistency(Model3D grid) {
		List<String> edgePositions = new ArrayList<String>();
		//xyz
		int x = grid.getMinX();
		int maxX = grid.getMaxX();
		int localMaxY = grid.getMaxYAtX(x);
		for (int y = grid.getMinYAtX(x); y <= localMaxY; y++) {
			int localMaxZ = grid.getMaxZ(x, y);
			for (int z = grid.getMinZ(x, y); z <= localMaxZ; z++) {
				edgePositions.add(x + "," + y + "," + z);
			}
		}
		for (x++; x < maxX; x++) {
			int y = grid.getMinYAtX(x);
			int maxY = grid.getMaxYAtX(x);
			int localMaxZ = grid.getMaxZ(x, y);
			for (int z = grid.getMinZ(x, y); z <= localMaxZ; z++) {
				edgePositions.add(x + "," + y + "," + z);
			}
			for (y++; y < maxY; y++) {
				int localMinZ = grid.getMinZ(x, y);
				localMaxZ = grid.getMaxZ(x, y);
				edgePositions.add(x + "," + y + "," + localMinZ);
				if (localMinZ != localMaxZ) {
					edgePositions.add(x + "," + y + "," + localMaxZ);
				}
			}
			if (y == maxY) {
				localMaxZ = grid.getMaxZ(x, y);
				for (int z = grid.getMinZ(x, y); z <= localMaxZ; z++) {
					edgePositions.add(x + "," + y + "," + z);
				}
			}
		}
		if (x == maxX) {
			localMaxY = grid.getMaxYAtX(x);
			for (int y = grid.getMinYAtX(x); y <= localMaxY; y++) {
				int localMaxZ = grid.getMaxZ(x, y);
				for (int z = grid.getMinZ(x, y); z <= localMaxZ; z++) {
					edgePositions.add(x + "," + y + "," + z);
				}
			}
		}
		//xzy
		String errorMessage = "Inconsistency found in bounds in xzy traversal";
		int positionCount = 0;
		x = grid.getMinX();
		maxX = grid.getMaxX();
		int localMaxZ = grid.getMaxZAtX(x);
		for (int z = grid.getMinZAtX(x); z <= localMaxZ; z++) {
			localMaxY = grid.getMaxY(x, z);
			for (int y = grid.getMinY(x, z); y <= localMaxY; y++) {
				positionCount++;
				if (!edgePositions.contains(x + "," + y + "," + z)) {
					System.out.println(errorMessage);
				}
			}
		}
		for (x++; x < maxX; x++) {
			int z = grid.getMinZAtX(x);
			int maxZ = grid.getMaxZAtX(x);
			localMaxY = grid.getMaxY(x, z);
			for (int y = grid.getMinY(x, z); y <= localMaxY; y++) {
				positionCount++;
				if (!edgePositions.contains(x + "," + y + "," + z)) {
					System.out.println(errorMessage);
				}
			}
			for (z++; z < maxZ; z++) {
				int localMinY = grid.getMinY(x, z);
				localMaxY = grid.getMaxY(x, z);
				positionCount++;
				if (!edgePositions.contains(x + "," + localMinY + "," + z)) {
					System.out.println(errorMessage);
				}
				if (localMinY != localMaxY) {
					positionCount++;
					if (!edgePositions.contains(x + "," + localMaxY + "," + z)) {
						System.out.println(errorMessage);
					}
				}
			}
			if (z == maxZ) {
				localMaxY = grid.getMaxY(x, z);
				for (int y = grid.getMinY(x, z); y <= localMaxY; y++) {
					positionCount++;
					if (!edgePositions.contains(x + "," + y + "," + z)) {
						System.out.println(errorMessage);
					}
				}
			}
		}
		if (x == maxX) {
			localMaxZ = grid.getMaxZAtX(x);
			for (int z = grid.getMinZAtX(x); z <= localMaxZ; z++) {
				localMaxY = grid.getMaxY(x, z);
				for (int y = grid.getMinY(x, z); y <= localMaxY; y++) {
					positionCount++;
					if (!edgePositions.contains(x + "," + y + "," + z)) {
						System.out.println(errorMessage);
					}
				}
			}
		}
		if (edgePositions.size() != positionCount) {
			System.out.println(errorMessage);
		}
		//yxz
		errorMessage = "Inconsistency found in bounds in yxz traversal";
		positionCount = 0;
		int y = grid.getMinY();
		int maxY = grid.getMaxY();
		int localMaxX = grid.getMaxXAtY(y);
		for (x = grid.getMinXAtY(y); x <= localMaxX; x++) {
			localMaxZ = grid.getMaxZ(x, y);
			for (int z = grid.getMinZ(x, y); z <= localMaxZ; z++) {
				positionCount++;
				if (!edgePositions.contains(x + "," + y + "," + z)) {
					System.out.println(errorMessage);
				}
			}
		}
		for (y++; y < maxY; y++) {
			x = grid.getMinXAtY(y);
			maxX = grid.getMaxXAtY(y);
			localMaxZ = grid.getMaxZ(x, y);
			for (int z = grid.getMinZ(x, y); z <= localMaxZ; z++) {
				positionCount++;
				if (!edgePositions.contains(x + "," + y + "," + z)) {
					System.out.println(errorMessage);
				}
			}
			for (x++; x < maxX; x++) {
				int localMinZ = grid.getMinZ(x, y);
				localMaxZ = grid.getMaxZ(x, y);
				positionCount++;
				if (!edgePositions.contains(x + "," + y + "," + localMinZ)) {
					System.out.println(errorMessage);
				}
				if (localMinZ != localMaxZ) {
					positionCount++;
					if (!edgePositions.contains(x + "," + y + "," + localMaxZ)) {
						System.out.println(errorMessage);
					}
				}
			}
			if (x == maxX) {
				localMaxZ = grid.getMaxZ(x, y);
				for (int z = grid.getMinZ(x, y); z <= localMaxZ; z++) {
					positionCount++;
					if (!edgePositions.contains(x + "," + y + "," + z)) {
						System.out.println(errorMessage);
					}
				}
			}
		}
		if (y == maxY) {
			localMaxX = grid.getMaxXAtY(y);
			for (x = grid.getMinXAtY(y); x <= localMaxX; x++) {
				localMaxZ = grid.getMaxZ(x, y);
				for (int z = grid.getMinZ(x, y); z <= localMaxZ; z++) {
					positionCount++;
					if (!edgePositions.contains(x + "," + y + "," + z)) {
						System.out.println(errorMessage);
					}
				}
			}
		}
		if (edgePositions.size() != positionCount) {
			System.out.println(errorMessage);
		}
		//yzx
		errorMessage = "Inconsistency found in bounds in yzx traversal";
		positionCount = 0;
		y = grid.getMinY();
		maxY = grid.getMaxY();
		localMaxZ = grid.getMaxZAtY(y);
		for (int z = grid.getMinZAtY(y); z <= localMaxZ; z++) {
			localMaxX = grid.getMaxX(y, z);
			for (x = grid.getMinX(y, z); x <= localMaxX; x++) {
				positionCount++;
				if (!edgePositions.contains(x + "," + y + "," + z)) {
					System.out.println(errorMessage);
				}
			}
		}
		for (y++; y < maxY; y++) {
			int z = grid.getMinZAtY(y);
			int maxZ = grid.getMaxZAtY(y);
			localMaxX = grid.getMaxX(y, z);
			for (x = grid.getMinX(y, z); x <= localMaxX; x++) {
				positionCount++;
				if (!edgePositions.contains(x + "," + y + "," + z)) {
					System.out.println(errorMessage);
				}
			}
			for (z++; z < maxZ; z++) {
				int localMinX = grid.getMinX(y, z);
				localMaxX = grid.getMaxX(y, z);
				positionCount++;
				if (!edgePositions.contains(localMinX + "," + y + "," + z)) {
					System.out.println(errorMessage);
				}
				if (localMinX != localMaxX) {
					positionCount++;
					if (!edgePositions.contains(localMaxX + "," + y + "," + z)) {
						System.out.println(errorMessage);
					}
				}
			}
			if (z == maxZ) {
				localMaxX = grid.getMaxX(y, z);
				for (x = grid.getMinX(y, z); x <= localMaxX; x++) {
					positionCount++;
					if (!edgePositions.contains(x + "," + y + "," + z)) {
						System.out.println(errorMessage);
					}
				}
			}
		}
		if (y == maxY) {
			localMaxZ = grid.getMaxZAtY(y);
			for (int z = grid.getMinZAtY(y); z <= localMaxZ; z++) {
				localMaxX = grid.getMaxX(y, z);
				for (x = grid.getMinX(y, z); x <= localMaxX; x++) {
					positionCount++;
					if (!edgePositions.contains(x + "," + y + "," + z)) {
						System.out.println(errorMessage);
					}
				}
			}
		}
		if (edgePositions.size() != positionCount) {
			System.out.println(errorMessage);
		}
		//zxy
		errorMessage = "Inconsistency found in bounds in zxy traversal";
		positionCount = 0;
		int z = grid.getMinZ();
		int maxZ = grid.getMaxZ();
		localMaxX = grid.getMaxXAtZ(z);
		for (x = grid.getMinXAtZ(z); x <= localMaxX; x++) {
			localMaxY = grid.getMaxY(x, z);
			for (y = grid.getMinY(x, z); y <= localMaxY; y++) {
				positionCount++;
				if (!edgePositions.contains(x + "," + y + "," + z)) {
					System.out.println(errorMessage);
				}
			}
		}
		for (z++; z < maxZ; z++) {
			x = grid.getMinXAtZ(z);
			maxX = grid.getMaxXAtZ(z);
			localMaxY = grid.getMaxY(x, z);
			for (y = grid.getMinY(x, z); y <= localMaxY; y++) {
				positionCount++;
				if (!edgePositions.contains(x + "," + y + "," + z)) {
					System.out.println(errorMessage);
				}
			}
			for (x++; x < maxX; x++) {
				int localMinY = grid.getMinY(x, z);
				localMaxY = grid.getMaxY(x, z);
				positionCount++;
				if (!edgePositions.contains(x + "," + localMinY + "," + z)) {
					System.out.println(errorMessage);
				}
				if (localMinY != localMaxY) {
					positionCount++;
					if (!edgePositions.contains(x + "," + localMaxY + "," + z)) {
						System.out.println(errorMessage);
					}
				}
			}
			if (x == maxX) {
				localMaxY = grid.getMaxY(x, z);
				for (y = grid.getMinY(x, z); y <= localMaxY; y++) {
					positionCount++;
					if (!edgePositions.contains(x + "," + y + "," + z)) {
						System.out.println(errorMessage);
					}
				}
			}
		}
		if (z == maxZ) {
			localMaxX = grid.getMaxXAtZ(z);
			for (x = grid.getMinXAtZ(z); x <= localMaxX; x++) {
				localMaxY = grid.getMaxY(x, z);
				for (y = grid.getMinY(x, z); y <= localMaxY; y++) {
					positionCount++;
					if (!edgePositions.contains(x + "," + y + "," + z)) {
						System.out.println(errorMessage);
					}
				}
			}
		}
		if (edgePositions.size() != positionCount) {
			System.out.println(errorMessage);
		}
		//zyx
		errorMessage = "Inconsistency found in bounds in zyx traversal";
		positionCount = 0;
		z = grid.getMinZ();
		maxZ = grid.getMaxZ();
		localMaxY = grid.getMaxYAtZ(z);
		for (y = grid.getMinYAtZ(z); y <= localMaxY; y++) {
			localMaxX = grid.getMaxX(y, z);
			for (x = grid.getMinX(y, z); x <= localMaxX; x++) {
				positionCount++;
				if (!edgePositions.contains(x + "," + y + "," + z)) {
					System.out.println(errorMessage);
				}
			}
		}
		for (z++; z < maxZ; z++) {
			y = grid.getMinYAtZ(z);
			maxY = grid.getMaxYAtZ(z);
			localMaxX = grid.getMaxX(y, z);
			for (x = grid.getMinX(y, z); x <= localMaxX; x++) {
				positionCount++;
				if (!edgePositions.contains(x + "," + y + "," + z)) {
					System.out.println(errorMessage);
				}
			}
			for (y++; y < maxY; y++) {
				int localMinX = grid.getMinX(y, z);
				localMaxX = grid.getMaxX(y, z);
				positionCount++;
				if (!edgePositions.contains(localMinX + "," + y + "," + z)) {
					System.out.println(errorMessage);
				}
				if (localMinX != localMaxX) {
					positionCount++;
					if (!edgePositions.contains(localMaxX + "," + y + "," + z)) {
						System.out.println(errorMessage);
					}
				}
			}
			if (y == maxY) {
				localMaxX = grid.getMaxX(y, z);
				for (x = grid.getMinX(y, z); x <= localMaxX; x++) {
					positionCount++;
					if (!edgePositions.contains(x + "," + y + "," + z)) {
						System.out.println(errorMessage);
					}
				}
			}
		}
		if (z == maxZ) {
			localMaxY = grid.getMaxYAtZ(z);
			for (y = grid.getMinYAtZ(z); y <= localMaxY; y++) {
				localMaxX = grid.getMaxX(y, z);
				for (x = grid.getMinX(y, z); x <= localMaxX; x++) {
					positionCount++;
					if (!edgePositions.contains(x + "," + y + "," + z)) {
						System.out.println(errorMessage);
					}
				}
			}
		}
		if (edgePositions.size() != positionCount) {
			System.out.println(errorMessage);
		}
	}
	
	public static void testArrayGrid() {
		System.out.println("Test ArrayGrid2D");
		ArrayIntGrid2D grid;
		System.out.println("nulls");
		try {
			grid = new ArrayIntGrid2D(0, null, null);
		} catch(Exception ex) { 
			ex.printStackTrace(System.out);}
		try {
			grid = new ArrayIntGrid2D(0, new int[] {0, 0}, null);
		} catch(Exception ex) { 
			ex.printStackTrace(System.out);}
		try {
			grid = new ArrayIntGrid2D(0, null, new int[][] {{0, 0}, {0, 0}});
		} catch(Exception ex) { 
			ex.printStackTrace(System.out);}
		System.out.println("empty array");
		try {
			grid = new ArrayIntGrid2D(0, new int[0], new int[0][0]);
		} catch(Exception ex) { 
			ex.printStackTrace(System.out);}		
		System.out.println("different length");
		try {
			grid = new ArrayIntGrid2D(0, new int[] {0, 0, 0}, new int[][] {{0, 0}, {0, 0}});
		} catch(Exception ex) { 
			ex.printStackTrace(System.out);}
		System.out.println("minX = Ineger.MAX_VALUE, values.length = large");
		try {
			grid = new ArrayIntGrid2D(Integer.MAX_VALUE, new int[99999], new int[99999][1]);
		} catch(Exception ex) { 
			ex.printStackTrace(System.out);}
		System.out.println("not convex");
		try {
			grid = new ArrayIntGrid2D(0, new int[] {0, 0, -2, 0, 0}, new int[][] { {1}, {1}, {1, 1, 1, 1, 1}, {1}, {1} });
		} catch(Exception ex) { 
			ex.printStackTrace(System.out);}
		try {
			grid = new ArrayIntGrid2D(0, new int[] {0, 0, 0, 0, 0}, new int[][] { {1}, {1}, {1, 1, 1, 1, 1}, {1}, {1} });
		} catch(Exception ex) { 
			ex.printStackTrace(System.out);}
		try {
			grid = new ArrayIntGrid2D(0, new int[] {-3, -3, -2, -3, -3 }, 
					new int[][] { {1, 1, 1, 1, 1}, {1, 1, 1, 1, 1}, {1, 1, 1}, {1, 1, 1, 1, 1}, {1, 1, 1, 1, 1} });
		} catch(Exception ex) { 
			ex.printStackTrace(System.out);}
		try {
			System.out.println("single dot");
			grid = new ArrayIntGrid2D(Integer.MIN_VALUE, new int[1], new int[][] {{1}});
			Utils.printAsGrid(grid);
			System.out.println("single line, horiz, vert, diagonal");
			grid = new ArrayIntGrid2D(Integer.MIN_VALUE, new int[5], new int[][] { {1}, {1}, {1}, {1}, {1} });
			Utils.printAsGrid(grid);
			grid = new ArrayIntGrid2D(Integer.MIN_VALUE, new int[1], new int[][] { {1, 1, 1, 1, 1 } });
			Utils.printAsGrid(grid);
			grid = new ArrayIntGrid2D(Integer.MIN_VALUE, new int[] {-2, -1, 0, 1, 2 }, new int[][] { {1}, {1}, {1}, {1}, {1} });
			Utils.printAsGrid(grid);
			grid = new ArrayIntGrid2D(Integer.MIN_VALUE, new int[] {2, 1, 0, -1, -2 }, new int[][] { {1}, {1}, {1}, {1}, {1} });
			Utils.printAsGrid(grid);
			System.out.println("tilted square");
			grid = new ArrayIntGrid2D(Integer.MIN_VALUE, new int[] {0, -1, -2, -3, -2, -1, 0 }, 
					new int[][] { {1}, {1, 1, 1}, {1, 1, 1, 1, 1}, {1, 1, 1, 1, 1, 1, 1}, {1, 1, 1, 1, 1}, {1, 1, 1}, {1} });
			Utils.printAsGrid(grid);
			System.out.println("aligned square");
			grid = new ArrayIntGrid2D(Integer.MIN_VALUE, new int[] {-3, -3, -3, -3, -3 }, 
					new int[][] { {1, 1, 1, 1, 1}, {1, 1, 1, 1, 1}, {1, 1, 1, 1, 1}, {1, 1, 1, 1, 1}, {1, 1, 1, 1, 1} });
			Utils.printAsGrid(grid);
			System.out.println("object grid");
			BigInt[][] array = testGenericArrayBySample(BigInt.ZERO, 1, 1);
			array[0][0] = BigInt.ONE;
			ArrayNumberGrid2D<BigInt> nubergrid = new ArrayNumberGrid2D<BigInt>(0, new int[1], array);
			Utils.printAsGrid(nubergrid);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@SuppressWarnings("unchecked")
	public static <Object_Type> Object_Type[][] testGenericArrayBySample(Object_Type sample, int width, int height) {
		Class<Object_Type> clazz = (Class<Object_Type>) sample.getClass();
		Class<Object_Type[]> arrayClass = (Class<Object_Type[]>) Array.newInstance(clazz, 0).getClass();
		Object_Type[][] arr = (Object_Type[][]) Array.newInstance(arrayClass, width);
		for (int i = 0; i < arr.length; i++) {
			arr[i] = (Object_Type[]) Array.newInstance(clazz, height);
		}
		return arr;
	}
	
	public static void test2DDiagonals() throws Exception {
		int[][][] sourceValues;
		int[][] resultValues;
		RegularIntGrid3D grid;
		IntModel2D diagonal;
		int side = 21;
		int originIndex = side/2;
		int offset = 5;
		ThreadLocalRandom random = ThreadLocalRandom.current();
		//xy 
		for (int slope = 1; slope != -3; slope -= 2) {
			for (int off = 0; off <= offset; off += offset) {
				sourceValues = new int[side][side][side];
				resultValues = new int[side][side];
				for (int x = -originIndex; x < side-originIndex; x++) {
					for (int z = -originIndex; z < side-originIndex; z++) {
						int value = random.nextInt();
						int y = slope*x+off;
						if (y < side-originIndex && y >= -originIndex) {
							sourceValues[x+originIndex][y+originIndex][z+originIndex] = value;
							resultValues[z+originIndex][x+originIndex] = value;	
						}
					}
				}			
				grid = new RegularIntGrid3D(sourceValues, -originIndex, -originIndex, -originIndex);
				diagonal = grid.diagonalCrossSectionOnXY(slope == 1, off);
				compare(diagonal, resultValues, originIndex, originIndex);
				testBoundsConsistency(diagonal);
			}
		}
		//xz
		for (int slope = 1; slope != -3; slope -= 2) {
			for (int off = 0; off <= offset; off += offset) {
				sourceValues = new int[side][side][side];
				resultValues = new int[side][side];
				for (int x = -originIndex; x < side-originIndex; x++) {
					for (int y = -originIndex; y < side-originIndex; y++) {
						int value = random.nextInt();
						int z = slope*x+off;
						if (z < side-originIndex && z >= -originIndex) {
							sourceValues[x+originIndex][y+originIndex][z+originIndex] = value;
							resultValues[x+originIndex][y+originIndex] = value;
						}
					}
				}			
				grid = new RegularIntGrid3D(sourceValues, -originIndex, -originIndex, -originIndex);
				diagonal = grid.diagonalCrossSectionOnXZ(slope == 1, off);
				compare(diagonal, resultValues, originIndex, originIndex);
				testBoundsConsistency(diagonal);
			}
		}
		//yz
		for (int slope = 1; slope != -3; slope -= 2) {
			for (int off = 0; off <= offset; off += offset) {
				sourceValues = new int[side][side][side];
				resultValues = new int[side][side];
				for (int x = -originIndex; x < side-originIndex; x++) {
					for (int y = -originIndex; y < side-originIndex; y++) {
						int value = random.nextInt();
						int z = slope*y+off;
						if (z < side-originIndex && z >= -originIndex) {
							sourceValues[x+originIndex][y+originIndex][z+originIndex] = value;
							resultValues[x+originIndex][y+originIndex] = value;
						}
					}
				}			
				grid = new RegularIntGrid3D(sourceValues, -originIndex, -originIndex, -originIndex);
				diagonal = grid.diagonalCrossSectionOnYZ(slope == 1, off);
				compare(diagonal, resultValues, originIndex, originIndex);
				testBoundsConsistency(diagonal);
			}
		}
	}
	
	public static void printRegionBounds(Model region) {
		int dimension = region.getGridDimension();
		for (int axis = 0; axis < dimension; axis++) {
			String axisLabel = Utils.getAxisLabel(dimension, axis);
			System.out.printf("%d <= %c <= %d %n", region.getMinCoordinate(axis), axisLabel, region.getMaxCoordinate(axis));
		}
	}
	
	public static void test3DDiagonals() throws Exception {
		int[][][][] sourceValues;
		int[][][] resultValues;
		RegularIntGrid4D grid;
		IntModel3D diagonal;
		int side = 21;
		int originIndex = side/2;
		int offset = 5;
		ThreadLocalRandom random = ThreadLocalRandom.current();
		//wx 
		for (int slope = 1; slope != -3; slope -= 2) {
			for (int off = 0; off <= offset; off += offset) {
				sourceValues = new int[side][side][side][side];
				resultValues = new int[side][side][side];
				for (int w = -originIndex; w < side-originIndex; w++) {
					for (int y = -originIndex; y < side-originIndex; y++) {
						for (int z = -originIndex; z < side-originIndex; z++) {
							int value = random.nextInt();
							int x = slope*w+off;
							if (x < side-originIndex && x >= -originIndex) {
								sourceValues[w+originIndex][x+originIndex][y+originIndex][z+originIndex] = value;
								resultValues[w+originIndex][y+originIndex][z+originIndex] = value;	
							}
						}
					}
				}			
				grid = new RegularIntGrid4D(sourceValues, -originIndex, -originIndex, -originIndex, -originIndex);
				diagonal = grid.diagonalCrossSectionOnWX(slope == 1, off);
				testBoundsConsistency(diagonal);
				compare(diagonal, new RegularIntGrid3D(resultValues, -originIndex, -originIndex, -originIndex));
			}
		}
		//yz 
		for (int slope = 1; slope != -3; slope -= 2) {
			for (int off = 0; off <= offset; off += offset) {
				sourceValues = new int[side][side][side][side];
				resultValues = new int[side][side][side];
				for (int w = -originIndex; w < side-originIndex; w++) {
					for (int y = -originIndex; y < side-originIndex; y++) {
						for (int x = -originIndex; x < side-originIndex; x++) {
							int value = random.nextInt();
							int z = slope*y+off;
							if (z < side-originIndex && z >= -originIndex) {
								sourceValues[w+originIndex][x+originIndex][y+originIndex][z+originIndex] = value;
								resultValues[w+originIndex][x+originIndex][y+originIndex] = value;	
							}
						}
					}
				}			
				grid = new RegularIntGrid4D(sourceValues, -originIndex, -originIndex, -originIndex, -originIndex);
				diagonal = grid.diagonalCrossSectionOnYZ(slope == 1, off);
				testBoundsConsistency(diagonal);
				compare(diagonal, new RegularIntGrid3D(resultValues, -originIndex, -originIndex, -originIndex));
			}
		}
	}
	
	public static void compare(IntModel2D grid, int[][] array, int xOffset, int yOffset) throws Exception {
		int maxX = grid.getMaxX();
		for (int x = grid.getMinX(); x <= maxX; x++) {
			int localMaxY = grid.getMaxY(x);
			for (int y = grid.getMinY(x); y <= localMaxY; y++) {
				if (grid.getFromPosition(x, y) != array[x + xOffset][y + yOffset]) {
					System.out.println("Different value at (" + x + ", " + y + "): " + grid.getFromPosition(x, y) + " != " + array[x + xOffset][y + yOffset]);
					return;
				}
			}
		}
		System.out.println("Equal!");
	}
	
	public static void compare(IntModel3D grid1, IntModel3D grid2) {
		try {
			boolean equal = true;
			for (int z = grid1.getMinZ(); z <= grid1.getMaxZ(); z++) {
				for (int y = grid1.getMinYAtZ(z); y <= grid1.getMaxYAtZ(z); y++) {
					for (int x = grid1.getMinX(y,z); x <= grid1.getMaxX(y,z); x++) {
						if (grid1.getFromPosition(x, y, z) != grid2.getFromPosition(x, y, z)) {
							equal = false;
							System.out.println("Different value at (" + x + ", " + y + ", " + z + "): " 
									+ grid1.getClass().getSimpleName() + ":" + grid1.getFromPosition(x, y, z) 
									+ " != " + grid2.getClass().getSimpleName() + ":" + grid2.getFromPosition(x, y, z));
						}
					}	
				}	
			}
			if (equal)
				System.out.println("Equal!");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void test4DDiagonals() throws Exception {
		int[][][][][] sourceValues;
		int[][][][] resultValues;
		RegularIntGrid5D grid;
		IntModel4D diagonal;
		int side = 20;
		int offset = 5;
		ThreadLocalRandom random = ThreadLocalRandom.current();
		//vw
		for (int off = 0; off <= offset; off += offset) {
			sourceValues = new int[side][side][side][side][side];
			resultValues = new int[side][side][side][side];
			for (int v = 0; v < side; v++) {
				for (int x = 0; x < side; x++) {
					for (int y = 0; y < side; y++) {
						for (int z = 0; z < side; z++) {
							int value = random.nextInt();
							int w = v+off;
							if (w < side) {
								sourceValues[v][w][x][y][z] = value;
								resultValues[v][x][y][z] = value;	
							}
						}
					}
				}
			}			
			grid = new RegularIntGrid5D(sourceValues, 0, 0, 0, 0, 0);
			diagonal = grid.diagonalCrossSectionOnVW(off);
			compare(diagonal, new RegularIntGrid4D(resultValues, 0, 0, 0, 0));
		}
	}
	
	public static void compare(IntModel4D grid1, IntModel4D grid2) {
		try {
			boolean equal = true;
			for (int z = grid1.getMinZ(); z <= grid1.getMaxZ(); z++) {
				for (int y = grid1.getMinYAtZ(z); y <= grid1.getMaxYAtZ(z); y++) {
					for (int x = grid1.getMinXAtYZ(y,z); x <= grid1.getMaxXAtYZ(y,z); x++) {
						for (int w = grid1.getMinW(x,y,z); w <= grid1.getMaxW(x,y,z); w++) {
							if (grid1.getFromPosition(w, x, y, z) != grid2.getFromPosition(w, x, y, z)) {
								equal = false;
								System.out.println("Different value at (" + w + ", " + x + ", " + y + ", " + z + "): " 
										+ grid1.getClass().getSimpleName() + ":" + grid1.getFromPosition(w, x, y, z) 
										+ " != " + grid2.getClass().getSimpleName() + ":" + grid2.getFromPosition(w, x, y, z));
							}
						}
					}	
				}	
			}
			if (equal)
				System.out.println("Equal!");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void testBigIntValueOfPerformance() {
		int count = 100000000;
		BigInt total, bigIntIncrement;
		int increment;
		long millis;
		millis = System.currentTimeMillis();
		total = BigInt.ZERO;
		increment = 0;
		for (int i = 0; i < count; i++) {
			increment++;
			total = total.add(BigInt.valueOf(increment));
		}
		System.out.println(System.currentTimeMillis() - millis);
		System.out.println(total);
		millis = System.currentTimeMillis();
		total = BigInt.ZERO;
		bigIntIncrement = BigInt.ZERO;
		for (int i = 0; i < count; i++) {
			bigIntIncrement = bigIntIncrement.add(BigInt.ONE);
			total = total.add(bigIntIncrement);
		}
		System.out.println(System.currentTimeMillis() - millis);
		System.out.println(total);
	}
	
	public static void queryAether5DNeighborhood(String[] args) {
		long initialValue = Long.parseLong(args[0]);
		int step = Integer.parseInt(args[1]);
		int v = Integer.parseInt(args[2]);
		int w = Integer.parseInt(args[3]);
		int x = Integer.parseInt(args[4]);
		int y = Integer.parseInt(args[5]);
		int z = Integer.parseInt(args[6]);
		AetherSimple5D ae = new AetherSimple5D(initialValue);
		System.out.println("Initial value: " + initialValue);
		for (int i = 0; i < step; i++) {
			System.out.print("Step: " + i + "\r");
			ae.nextStep();
		}
		System.out.println("Step: " + step);
		printVonNeumannNeighborhood(ae, v, w, x, y, z);
	}
	
	public static void queryAether4DNeighborhood(String[] args) {
		long initialValue = Long.parseLong(args[0]);
		int step = Integer.parseInt(args[1]);
		int w = Integer.parseInt(args[2]);
		int x = Integer.parseInt(args[3]);
		int y = Integer.parseInt(args[4]);
		int z = Integer.parseInt(args[5]);
		Aether4D ae = new Aether4D(initialValue);
		System.out.println("Initial value: " + initialValue);
		for (int i = 0; i < step; i++) {
			System.out.print("Step: " + i + "\r");
			ae.nextStep();
		}
		System.out.println("Step: " + step);
		printVonNeumannNeighborhood(ae, w, x, y, z);
	}
	
	public static void timeIntAether3D(int singleSource) {
		IntAether3D ae1 = new IntAether3D(singleSource);
		long millis = System.currentTimeMillis();
		while(ae1.nextStep());
		System.out.println(System.currentTimeMillis() - millis);
	}
	
	public static void printVonNeumannNeighborhood(LongModel3D grid, int x, int y, int z) {
		try {
			//center
			System.out.println("(" + x + ", " + y + ", " + z + "): " + grid.getFromPosition(x, y, z));
			//gx
			System.out.println("("+ (x + 1) + ", " + y + ", " + z + "): " + grid.getFromPosition(x + 1, y, z));
			//sx
			System.out.println("("+ (x - 1) + ", " + y + ", " + z + "): " + grid.getFromPosition(x - 1, y, z));
			//gy
			System.out.println("("+ x + ", " + (y + 1) + ", " + z + "): " + grid.getFromPosition(x, y + 1, z));
			//sy
			System.out.println("("+ x + ", " + (y - 1) + ", " + z + "): " + grid.getFromPosition(x, y - 1, z));
			//gz
			System.out.println("("+ x + ", " + y + ", " + (z + 1) + "): " + grid.getFromPosition(x, y, z + 1));
			//sz
			System.out.println("("+ x + ", " + y + ", " + (z - 1) + "): " + grid.getFromPosition(x, y, z - 1));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}	
	
	public static void printVonNeumannNeighborhood(LongModel4D grid, int w, int x, int y, int z) {
		try {
			//center
			System.out.println("(" + w + ", " + x + ", " + y + ", " + z + "): " + grid.getFromPosition(w, x, y, z));
			//gw
			System.out.println("(" + (w + 1) + ", " + x + ", " + y + ", " + z + "): " + grid.getFromPosition(w + 1, x, y, z));
			//sw
			System.out.println("(" + (w - 1) + ", " + x + ", " + y + ", " + z + "): " + grid.getFromPosition(w - 1, x, y, z));
			//gx
			System.out.println("(" + w + ", " + (x + 1) + ", " + y + ", " + z + "): " + grid.getFromPosition(w, x + 1, y, z));
			//sx
			System.out.println("(" + w + ", " + (x - 1) + ", " + y + ", " + z + "): " + grid.getFromPosition(w, x - 1, y, z));
			//gy
			System.out.println("(" + w + ", " + x + ", " + (y + 1) + ", " + z + "): " + grid.getFromPosition(w, x, y + 1, z));
			//sy
			System.out.println("(" + w + ", " + x + ", " + (y - 1) + ", " + z + "): " + grid.getFromPosition(w, x, y - 1, z));
			//gz
			System.out.println("(" + w + ", " + x + ", " + y + ", " + (z + 1) + "): " + grid.getFromPosition(w, x, y, z + 1));
			//sz
			System.out.println("(" + w + ", " + x + ", " + y + ", " + (z - 1) + "): " + grid.getFromPosition(w, x, y, z - 1));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void printVonNeumannNeighborhood(LongModel5D grid, int v, int w, int x, int y, int z) {
		try {
			//center
			System.out.println("(" + v + ", " + w + ", " + x + ", " + y + ", " + z + "): " + grid.getFromPosition(v, w, x, y, z));
			//gv
			System.out.println("(" + (v + 1) + ", " + w + ", " + x + ", " + y + ", " + z + "): " + grid.getFromPosition(v + 1, w, x, y, z));
			//sv
			System.out.println("(" + (v - 1) + ", " + w + ", " + x + ", " + y + ", " + z + "): " + grid.getFromPosition(v - 1, w, x, y, z));
			//gw
			System.out.println("(" + v + ", " + (w + 1) + ", " + x + ", " + y + ", " + z + "): " + grid.getFromPosition(v, w + 1, x, y, z));
			//sw
			System.out.println("(" + v + ", " + (w - 1) + ", " + x + ", " + y + ", " + z + "): " + grid.getFromPosition(v, w - 1, x, y, z));
			//gx
			System.out.println("(" + v + ", " + w + ", " + (x + 1) + ", " + y + ", " + z + "): " + grid.getFromPosition(v, w, x + 1, y, z));
			//sx
			System.out.println("(" + v + ", " + w + ", " + (x - 1) + ", " + y + ", " + z + "): " + grid.getFromPosition(v, w, x - 1, y, z));
			//gy
			System.out.println("(" + v + ", " + w + ", " + x + ", " + (y + 1) + ", " + z + "): " + grid.getFromPosition(v, w, x, y + 1, z));
			//sy
			System.out.println("(" + v + ", " + w + ", " + x + ", " + (y - 1) + ", " + z + "): " + grid.getFromPosition(v, w, x, y - 1, z));
			//gz
			System.out.println("(" + v + ", " + w + ", " + x + ", " + y + ", " + (z + 1) + "): " + grid.getFromPosition(v, w, x, y, z + 1));
			//sz
			System.out.println("(" + v + ", " + w + ", " + x + ", " + y + ", " + (z - 1) + "): " + grid.getFromPosition(v, w, x, y, z - 1));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void testAether3DEnclosed2() {
		int side = 31;
		int initialValue = -10000;
		Aether3DEnclosed2 ae1 = new Aether3DEnclosed2(side, side, side, initialValue, side/2, side/2, side/2);
		Aether3DEnclosed ae2 = new Aether3DEnclosed(initialValue, side);
//		checkTotalValueConservation(ae1);
//		stepByStep(ae2);
		compareWithOffset(ae1, ae2, side/2 + 1, side/2 + 1, side/2 + 1);
	}
	
	public static void compareWithOffset(LongModel3D ca1, LongModel3D ca2, int xOffset, int yOffset, int zOffset) {
		try {
			System.out.println("Comparing...");
			boolean finished1 = false;
			boolean finished2 = false;
			boolean equal = true;
			while (!finished1 && !finished2) {
//				System.out.println("Comparing step " + ca1.getStep());
				for (int z = ca1.getMinZ(), z2 = z + zOffset; z <= ca1.getMaxZ(); z++, z2++) {
					for (int y = ca1.getMinYAtZ(z), y2 = y + yOffset; y <= ca1.getMaxYAtZ(z); y++, y2++) {
						for (int x = ca1.getMinX(y,z), x2 = x + xOffset; x <= ca1.getMaxX(y,z); x++, x2++) {
							if (ca1.getFromPosition(x, y, z) != ca2.getFromPosition(x2, y2, z2)) {
								equal = false;
								System.out.println("Different value at step " + ca1.getStep() + " (" + x + ", " + y + ", " + z + "): " 
										+ ca1.getClass().getSimpleName() + ":" + ca1.getFromPosition(x, y, z) 
										+ " != " + ca2.getClass().getSimpleName() + ":" + ca2.getFromPosition(x2, y2, z2));
								return;
							}
						}	
					}	
				}
				finished1 = !ca1.nextStep();
				finished2 = !ca2.nextStep();
				if (finished1 != finished2) {
					equal = false;
					String finishedCA = finished1? ca1.getClass().getSimpleName() : ca2.getClass().getSimpleName();
					System.out.println("Different final step. " + finishedCA + " finished earlier (step " + ca1.getStep() + ")");
				}
			}
			if (equal)
				System.out.println("Equal!");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void testAsymmetricSection() {
		IntAether4D ae = new IntAether4D(-10000);
		IntModel4D ns = ae.asymmetricSection();
		try {
			do {
				System.out.println("Comparing step " + ae.getStep());
				int maxW = ae.getAsymmetricMaxW();
				for (int w = 0; w <= maxW; w++) {
					for (int x = 0; x <= w; x++) {
						for (int y = 0; y <= x; y++) {
							for (int z = 0; z <= y; z++) {
//								System.out.println("Comparing value at (" + w + ", " + x + ", " + y + ", " + z + ")");
								if (ae.getFromAsymmetricPosition(w, x, y, z) != ns.getFromPosition(w, x, y, z)) {
									System.out.println("Different value");
									return;
								}
							}
						}
					}
				}
			} while(ae.nextStep());
			System.out.println("Equal!");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void test4DZCrossSection() {
		IntAether4D ae = new IntAether4D(-200);
		
		int z = 0;
		IntModel3D cs = ae.asymmetricSection().crossSectionAtZ(z);
		try {
			do {
				System.out.println("Comparing step " + ae.getStep());
				int maxW = ae.getAsymmetricMaxWAtZ(z);
				if (maxW != cs.getMaxX()) {
					System.out.println("Different max w!");
					return;
				}
				int minW = ae.getAsymmetricMinWAtZ(z);
				if (minW != cs.getMinX()) {
					System.out.println("Different min w!");
					return;
				}
				for (int w = minW; w <= maxW; w++) {
					int maxX = ae.getAsymmetricMaxXAtWZ(w, z);
					if (maxX != cs.getMaxYAtX(w)) {
						System.out.println("Different max x!");
						return;
					}
					int minX = ae.getAsymmetricMinXAtWZ(w, z);
					if (minX != cs.getMinYAtX(w)) {
						System.out.println("Different min x!");
						return;
					}
					for (int x = minX; x <= maxX; x++) {
						int maxY = ae.getAsymmetricMaxY(w, x, z);
						if (maxY != cs.getMaxZ(w, x)) {
							System.out.println("Different max y!");
							return;
						}
						int minY = ae.getAsymmetricMinY(w, x, z);
						if (minY != cs.getMinZ(w, x)) {
							System.out.println("Different min y!");
							return;
						}
						for (int y = minY; y <= maxY; y++) {
							if (ae.getFromAsymmetricPosition(w, x, y, z) != cs.getFromPosition(w, x, y)) {
								System.out.println("Different value");
								return;
							}
						}
					}
				}
			} while(ae.nextStep());
			System.out.println("Equal!");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void sivTesting() {
		long initialValue = 100000;
		SpreadIntegerValue2D ae1 = new SpreadIntegerValue2D(initialValue, 0);
		SpreadIntegerValueSimple2D ae2 = new SpreadIntegerValueSimple2D(initialValue, 0);
		compareAllSteps(ae1, ae2);
	}
	
	public static void testAetherRandomConfig() {
		IntAether3DRandomConfiguration ae1 = new IntAether3DRandomConfiguration(3, -10, 10);
		stepByStep(ae1.crossSectionAtZ(0));
	}
	
	public static int findAetherMaxSafeIntValue(int dimension, int minValue) {	
		long maxValue = (long)Integer.MAX_VALUE + 1;
		long maxResultingValue;
		do {
			maxValue--;
			maxResultingValue = minValue + (((maxValue-minValue)/2)*dimension*2);
		} while(maxResultingValue > Integer.MAX_VALUE);
		return (int)maxValue;
	}
	
	public static int findAetherMinSafeIntValue(int dimension, int maxValue) {	
		long minValue = (long)Integer.MAX_VALUE -1;
		long maxResultingValue;
		do {
			minValue++;
			maxResultingValue = minValue + (((maxValue-minValue)/2)*dimension*2);
		} while(maxResultingValue > Integer.MAX_VALUE);
		return (int)minValue;
	}
	
	public static void compareAllSteps(LongModel1D ca1, LongModel1D ca2) {
		try {
			System.out.println("Comparing...");
			boolean finished1 = false;
			boolean finished2 = false;
			boolean equal = true;
			while (!finished1 && !finished2) {
				//System.out.println("step " + ca1.getStep());
				for (int x = ca2.getMinX(); x <= ca2.getMaxX(); x++) {
//					System.out.println(x);
					long a = ca1.getFromPosition(x);
					long b = ca2.getFromPosition(x);
					if (a != b) {
						equal = false;
						System.out.println("Different value at step " + ca1.getStep() + " (" + x + "): " 
								+ ca1.getClass().getSimpleName() + ":" + a 
								+ " != " + ca2.getClass().getSimpleName() + ":" + b);
						//return;
					}
				}
				finished1 = !ca1.nextStep();
				finished2 = !ca2.nextStep();
				if (finished1 != finished2) {
					equal = false;
					String finishedCA = finished1? ca1.getClass().getSimpleName() : ca2.getClass().getSimpleName();
					System.out.println("Different final step. " + finishedCA + " finished earlier (step " + ca1.getStep() + ")");
				}
			}
			if (equal)
				System.out.println("Equal!");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void compareAllSteps(IntModel2D ca1, LongModel2D ca2) {
		try {
			System.out.println("Comparing...");
			boolean finished1 = false;
			boolean finished2 = false;
			boolean equal = true;
			while (!finished1 && !finished2) {
				for (int y = ca1.getMinY(); y <= ca1.getMaxY(); y++) {
					for (int x = ca2.getMinX(y); x <= ca2.getMaxX(y); x++) {
						if (ca1.getFromPosition(x, y) != ca2.getFromPosition(x, y)) {
							equal = false;
							System.out.println("Different value at step " + ca1.getStep() + " (" + x + ", " + y + "): " 
									+ ca1.getClass().getSimpleName() + ":" + ca1.getFromPosition(x, y) 
									+ " != " + ca2.getClass().getSimpleName() + ":" + ca2.getFromPosition(x, y));
						}
					}	
				}
				finished1 = !ca1.nextStep();
				finished2 = !ca2.nextStep();
				if (finished1 != finished2) {
					equal = false;
					String finishedCA = finished1? ca1.getClass().getSimpleName() : ca2.getClass().getSimpleName();
					System.out.println("Different final step. " + finishedCA + " finished earlier (step " + ca1.getStep() + ")");
				}
			}
			if (equal)
				System.out.println("Equal!");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void compareAllSteps(IntModel2D ca1, IntModel2D ca2) {
		try {
			System.out.println("Comparing...");
			boolean finished1 = false;
			boolean finished2 = false;
			boolean equal = true;
			while (!finished1 && !finished2) {
				for (int y = ca1.getMinY(); y <= ca1.getMaxY(); y++) {
					for (int x = ca2.getMinX(y); x <= ca2.getMaxX(y); x++) {
						if (ca1.getFromPosition(x, y) != ca2.getFromPosition(x, y)) {
							equal = false;
							System.out.println("Different value at step " + ca1.getStep() + " (" + x + ", " + y + "): " 
									+ ca1.getClass().getSimpleName() + ":" + ca1.getFromPosition(x, y) 
									+ " != " + ca2.getClass().getSimpleName() + ":" + ca2.getFromPosition(x, y));
						}
					}	
				}
				finished1 = !ca1.nextStep();
				finished2 = !ca2.nextStep();
				if (finished1 != finished2) {
					equal = false;
					String finishedCA = finished1? ca1.getClass().getSimpleName() : ca2.getClass().getSimpleName();
					System.out.println("Different final step. " + finishedCA + " finished earlier (step " + ca1.getStep() + ")");
				}
			}
			if (equal)
				System.out.println("Equal!");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void compareAllSteps(LongModel2D ca1, LongModel2D ca2) {
		try {
			System.out.println("Comparing...");
			boolean finished1 = false;
			boolean finished2 = false;
			boolean equal = true;
			while (!finished1 && !finished2) {
//				System.out.println("Comparing step " + ca1.getStep());
				int maxY = ca1.getMaxY();
				for (int y = ca1.getMinY(); y <= maxY; y++) {
					for (int x = ca2.getMinX(y); x <= ca2.getMaxX(y); x++) {
						if (ca1.getFromPosition(x, y) != ca2.getFromPosition(x, y)) {
							equal = false;
							System.out.println("Different value at step " + ca1.getStep() + " (" + x + ", " + y + "): " 
									+ ca1.getClass().getSimpleName() + ":" + ca1.getFromPosition(x, y) 
									+ " != " + ca2.getClass().getSimpleName() + ":" + ca2.getFromPosition(x, y));
							//return;
						}
					}	
				}
				finished1 = !ca1.nextStep();
				finished2 = !ca2.nextStep();
				if (finished1 != finished2) {
					equal = false;
					String finishedCA = finished1? ca1.getClass().getSimpleName() : ca2.getClass().getSimpleName();
					System.out.println("Different final step. " + finishedCA + " finished earlier (step " + ca1.getStep() + ")");
				}
				if (!equal) {
					return;
				}
			}
			if (equal)
				System.out.println("Equal!");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static <Number_Type extends Number & FieldElement<Number_Type> & Comparable<Number_Type>> void compareAllSteps(LongModel2D ca1, NumericModel2D<Number_Type> ca2) {
		compareAllSteps(ca2, ca1);
	}
	
	public static <Number_Type extends Number & FieldElement<Number_Type> & Comparable<Number_Type>> void compareAllSteps(NumericModel2D<Number_Type> ca1, LongModel2D ca2) {
		try {
			System.out.println("Comparing...");
			boolean finished1 = false;
			boolean finished2 = false;
			boolean equal = true;
			while (!finished1 && !finished2) {
//				System.out.println("Comparing step " + ca1.getStep());
				for (int y = ca1.getMinY(); y <= ca1.getMaxY(); y++) {
					for (int x = ca2.getMinX(y); x <= ca2.getMaxX(y); x++) {
						if (ca1.getFromPosition(x, y).longValue() != ca2.getFromPosition(x, y)) {
							equal = false;
							System.out.println("Different value at step " + ca1.getStep() + " (" + x + ", " + y + "): " 
									+ ca2.getClass().getSimpleName() + ":" + ca2.getFromPosition(x, y) 
									+ " != " + ca1.getClass().getSimpleName() + ":" + ca1.getFromPosition(x, y));
						}
					}	
				}
				finished1 = !ca1.nextStep();
				finished2 = !ca2.nextStep();
				if (finished1 != finished2) {
					equal = false;
					String finishedCA = finished1? ca1.getClass().getSimpleName() : ca2.getClass().getSimpleName();
					System.out.println("Different final step. " + finishedCA + " finished earlier (step " + ca1.getStep() + ")");
				}
				if (!equal)
					return;
			}
			if (equal)
				System.out.println("Equal!");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static <Number_Type extends FieldElement<Number_Type> & Comparable<Number_Type>> void compareAllSteps(NumericModel2D<Number_Type> ca1, NumericModel2D<Number_Type> ca2) {
		try {
			System.out.println("Comparing...");
			boolean finished1 = false;
			boolean finished2 = false;
			boolean equal = true;
			while (!finished1 && !finished2) {
				System.out.println("Comparing step " + ca1.getStep());
				for (int y = ca1.getMinY(); y <= ca1.getMaxY(); y++) {
					for (int x = ca2.getMinX(y); x <= ca2.getMaxX(y); x++) {
						if (ca1.getFromPosition(x, y).compareTo(ca2.getFromPosition(x, y)) != 0) {
							equal = false;
							System.out.println("Different value at step " + ca1.getStep() + " (" + x + ", " + y + "): " 
									+ ca2.getClass().getSimpleName() + ":" + ca2.getFromPosition(x, y) 
									+ " != " + ca1.getClass().getSimpleName() + ":" + ca1.getFromPosition(x, y));
						}
					}	
				}
				finished1 = !ca1.nextStep();
				finished2 = !ca2.nextStep();
				if (finished1 != finished2) {
					equal = false;
					String finishedCA = finished1? ca1.getClass().getSimpleName() : ca2.getClass().getSimpleName();
					System.out.println("Different final step. " + finishedCA + " finished earlier (step " + ca1.getStep() + ")");
				}
				if (!equal)
					return;
			}
			if (equal)
				System.out.println("Equal!");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void compareAllSteps(IntModel3D ca1, IntModel3D ca2) {
		try {
			System.out.println("Comparing...");
			boolean finished1 = false;
			boolean finished2 = false;
			boolean equal = true;
			while (!finished1 && !finished2) {
				for (int z = ca1.getMinZ(); z <= ca1.getMaxZ(); z++) {
					for (int y = ca1.getMinYAtZ(z); y <= ca1.getMaxYAtZ(z); y++) {
						for (int x = ca2.getMinX(y,z); x <= ca2.getMaxX(y,z); x++) {
							if (ca1.getFromPosition(x, y, z) != ca2.getFromPosition(x, y, z)) {
								equal = false;
								System.out.println("Different value at step " + ca1.getStep() + " (" + x + ", " + y + ", " + z + "): " 
										+ ca1.getClass().getSimpleName() + ":" + ca1.getFromPosition(x, y, z) 
										+ " != " + ca2.getClass().getSimpleName() + ":" + ca2.getFromPosition(x, y, z));
							}
						}	
					}	
				}
				finished1 = !ca1.nextStep();
				finished2 = !ca2.nextStep();
				if (finished1 != finished2) {
					equal = false;
					String finishedCA = finished1? ca1.getClass().getSimpleName() : ca2.getClass().getSimpleName();
					System.out.println("Different final step. " + finishedCA + " finished earlier (step " + ca1.getStep() + ")");
				}
			}
			if (equal)
				System.out.println("Equal!");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void compareAllSteps(LongModel4D ca1, LongModel4D ca2) {
		try {
			System.out.println("Comparing...");
			boolean finished1 = false;
			boolean finished2 = false;
			boolean equal = true;
			while (!finished1 && !finished2) {
				System.out.println("step " + ca1.getStep());
				for (int z = ca1.getMinZ(); z <= ca1.getMaxZ(); z++) {
					for (int y = ca1.getMinYAtZ(z); y <= ca1.getMaxYAtZ(z); y++) {
						for (int x = ca2.getMinXAtYZ(y,z); x <= ca2.getMaxXAtYZ(y,z); x++) {
							for (int w = ca2.getMinW(x,y,z); w <= ca2.getMaxW(x,y,z); w++) {
								if (ca1.getFromPosition(w, x, y, z) != ca2.getFromPosition(w, x, y, z)) {
									equal = false;
									System.out.println("Different value at step " + ca1.getStep() + " (" + w + ", " + x + ", " + y + ", " + z + "): " 
											+ ca1.getClass().getSimpleName() + ":" + ca1.getFromPosition(w, x, y, z) 
											+ " != " + ca2.getClass().getSimpleName() + ":" + ca2.getFromPosition(w, x, y, z));
								}
							}
						}	
					}	
				}
				finished1 = !ca1.nextStep();
				finished2 = !ca2.nextStep();
				if (finished1 != finished2) {
					equal = false;
					String finishedCA = finished1? ca1.getClass().getSimpleName() : ca2.getClass().getSimpleName();
					System.out.println("Different final step. " + finishedCA + " finished earlier (step " + ca1.getStep() + ")");
				}
				if (!equal)
					break;
			}
			if (equal)
				System.out.println("Equal!");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void compareAllSteps(LongModel4D ca1, IntModel4D ca2) {
		compareAllSteps(ca2, ca1);
	}
	
	public static void compareAllSteps(IntModel4D ca1, LongModel4D ca2) {
		try {
			System.out.println("Comparing...");
			boolean finished1 = false;
			boolean finished2 = false;
			boolean equal = true;
			while (!finished1 && !finished2) {
				System.out.println("Comparing step " + ca1.getStep());
				for (int z = ca1.getMinZ(); z <= ca1.getMaxZ(); z++) {
					for (int y = ca1.getMinYAtZ(z); y <= ca1.getMaxYAtZ(z); y++) {
						for (int x = ca2.getMinXAtYZ(y,z); x <= ca2.getMaxXAtYZ(y,z); x++) {
							for (int w = ca2.getMinW(x,y,z); w <= ca2.getMaxW(x,y,z); w++) {
								if (ca1.getFromPosition(w, x, y, z) != ca2.getFromPosition(w, x, y, z)) {
									equal = false;
									System.out.println("Different value at step " + ca1.getStep() + " (" + w + ", " + x + ", " + y + ", " + z + "): " 
											+ ca1.getClass().getSimpleName() + ":" + ca1.getFromPosition(w, x, y, z) 
											+ " != " + ca2.getClass().getSimpleName() + ":" + ca2.getFromPosition(w, x, y, z));
									return;
								}
							}
						}	
					}	
				}
				finished1 = !ca1.nextStep();
				finished2 = !ca2.nextStep();
				if (finished1 != finished2) {
					equal = false;
					String finishedCA = finished1? ca1.getClass().getSimpleName() : ca2.getClass().getSimpleName();
					System.out.println("Different final step. " + finishedCA + " finished earlier (step " + ca1.getStep() + ")");
				}
			}
			if (equal)
				System.out.println("Equal!");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void compareAllSteps(IntModel4D ca1, IntModel4D ca2) {
		try {
			System.out.println("Comparing...");
			boolean finished1 = false;
			boolean finished2 = false;
			boolean equal = true;
			while (!finished1 && !finished2) {
				System.out.println("Comparing step " + ca1.getStep());
				for (int z = ca1.getMinZ(); z <= ca1.getMaxZ(); z++) {
					for (int y = ca1.getMinYAtZ(z); y <= ca1.getMaxYAtZ(z); y++) {
						for (int x = ca2.getMinXAtYZ(y,z); x <= ca2.getMaxXAtYZ(y,z); x++) {
							for (int w = ca2.getMinW(x,y,z); w <= ca2.getMaxW(x,y,z); w++) {
								if (ca1.getFromPosition(w, x, y, z) != ca2.getFromPosition(w, x, y, z)) {
									equal = false;
									System.out.println("Different value at step " + ca1.getStep() + " (" + w + ", " + x + ", " + y + ", " + z + "): " 
											+ ca1.getClass().getSimpleName() + ":" + ca1.getFromPosition(w, x, y, z) 
											+ " != " + ca2.getClass().getSimpleName() + ":" + ca2.getFromPosition(w, x, y, z));
									return;
								}
							}
						}	
					}	
				}
				finished1 = !ca1.nextStep();
				finished2 = !ca2.nextStep();
				if (finished1 != finished2) {
					equal = false;
					String finishedCA = finished1? ca1.getClass().getSimpleName() : ca2.getClass().getSimpleName();
					System.out.println("Different final step. " + finishedCA + " finished earlier (step " + ca1.getStep() + ")");
				}
			}
			if (equal)
				System.out.println("Equal!");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void compareAllSteps(LongModel3D ca1, IntModel3D ca2) {
		compareAllSteps(ca2, ca1);
	}
	
	public static void compareAllSteps(IntModel3D ca1, LongModel3D ca2) {
		try {
			System.out.println("Comparing...");
			boolean finished1 = false;
			boolean finished2 = false;
			boolean equal = true;
			while (!finished1 && !finished2) {
				System.out.println("Comparing step " + ca1.getStep());
				for (int z = ca1.getMinZ(); z <= ca1.getMaxZ(); z++) {
					for (int y = ca1.getMinYAtZ(z); y <= ca1.getMaxYAtZ(z); y++) {
						for (int x = ca2.getMinX(y,z); x <= ca2.getMaxX(y,z); x++) {
//							System.out.println("Comparing position (" + x + ", " + y + ", " + z + ")");
							if (ca1.getFromPosition(x, y, z) != ca2.getFromPosition(x, y, z)) {
								equal = false;
								System.out.println("Different value at step " + ca1.getStep() + " (" + x + ", " + y + ", " + z + "): " 
										+ ca2.getClass().getSimpleName() + ":" + ca2.getFromPosition(x, y, z) 
										+ " != " + ca1.getClass().getSimpleName() + ":" + ca1.getFromPosition(x, y, z));
								return;
							}
						}	
					}	
				}
				finished1 = !ca1.nextStep();
				finished2 = !ca2.nextStep();
				if (finished1 != finished2) {
					equal = false;
					String finishedCA = finished1? ca1.getClass().getSimpleName() : ca2.getClass().getSimpleName();
					System.out.println("Different final step. " + finishedCA + " finished earlier (step " + ca1.getStep() + ")");
				}
			}
			if (equal)
				System.out.println("Equal!");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void compareAllSteps(LongModel3D ca1, LongModel3D ca2) {
		try {
			System.out.println("Comparing...");
			boolean finished1 = false;
			boolean finished2 = false;
			boolean equal = true;
			while (!finished1 && !finished2) {
//				System.out.println("Comparing step " + ca1.getStep());
				for (int z = ca1.getMinZ(); z <= ca1.getMaxZ(); z++) {
					for (int y = ca1.getMinYAtZ(z); y <= ca1.getMaxYAtZ(z); y++) {
						for (int x = ca2.getMinX(y,z); x <= ca2.getMaxX(y,z); x++) {
							if (ca1.getFromPosition(x, y, z) != ca2.getFromPosition(x, y, z)) {
								equal = false;
								System.out.println("Different value at step " + ca1.getStep() + " (" + x + ", " + y + ", " + z + "): " 
										+ ca2.getClass().getSimpleName() + ":" + ca2.getFromPosition(x, y, z) 
										+ " != " + ca1.getClass().getSimpleName() + ":" + ca1.getFromPosition(x, y, z));
							}
						}	
					}	
				}
				finished1 = !ca1.nextStep();
				finished2 = !ca2.nextStep();
				if (finished1 != finished2) {
					equal = false;
					String finishedCA = finished1? ca1.getClass().getSimpleName() : ca2.getClass().getSimpleName();
					System.out.println("Different final step. " + finishedCA + " finished earlier (step " + ca1.getStep() + ")");
				}
				if (!equal)
					return;
			}
			if (equal)
				System.out.println("Equal!");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void compareInverted(LongModel3D ca1, LongModel3D ca2) {
		try {
			System.out.println("Comparing...");
			boolean finished1 = false;
			boolean finished2 = false;
			boolean equal = true;
			while (!finished1 && !finished2) {
//				System.out.println("Comparing step " + ca1.getStep());
				for (int z = ca1.getMinZ(); z <= ca1.getMaxZ(); z++) {
					for (int y = ca1.getMinYAtZ(z); y <= ca1.getMaxYAtZ(z); y++) {
						for (int x = ca2.getMinX(y,z); x <= ca2.getMaxX(y,z); x++) {
							if (ca1.getFromPosition(x, y, z) != -ca2.getFromPosition(x, y, z)) {
								equal = false;
								System.out.println("Different value at step " + ca1.getStep() + " (" + x + ", " + y + ", " + z + "): " 
										+ ca2.getClass().getSimpleName() + ":" + ca2.getFromPosition(x, y, z) 
										+ " != - " + ca1.getClass().getSimpleName() + ":" + ca1.getFromPosition(x, y, z));
							}
						}	
					}	
				}
				finished1 = !ca1.nextStep();
				finished2 = !ca2.nextStep();
				if (finished1 != finished2) {
					equal = false;
					String finishedCA = finished1? ca1.getClass().getSimpleName() : ca2.getClass().getSimpleName();
					System.out.println("Different final step. " + finishedCA + " finished earlier (step " + ca1.getStep() + ")");
				}
				if (!equal)
					return;
			}
			if (equal)
				System.out.println("Equal!");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static <Number_Type extends Number & FieldElement<Number_Type> & Comparable<Number_Type>> void compareAllSteps(NumericModel3D<Number_Type> ca1, LongModel3D ca2) {
		try {
			System.out.println("Comparing...");
			boolean finished1 = false;
			boolean finished2 = false;
			boolean equal = true;
			while (!finished1 && !finished2) {
//				System.out.println("Comparing step " + ca1.getStep());
				for (int z = ca1.getMinZ(); z <= ca1.getMaxZ(); z++) {
					for (int y = ca1.getMinYAtZ(z); y <= ca1.getMaxYAtZ(z); y++) {
						for (int x = ca2.getMinX(y,z); x <= ca2.getMaxX(y,z); x++) {
							if (ca1.getFromPosition(x, y, z).longValue() != ca2.getFromPosition(x, y, z)) {
								equal = false;
								System.out.println("Different value at step " + ca1.getStep() + " (" + x + ", " + y + ", " + z + "): " 
										+ ca2.getClass().getSimpleName() + ":" + ca2.getFromPosition(x, y, z) 
										+ " != " + ca1.getClass().getSimpleName() + ":" + ca1.getFromPosition(x, y, z));
							}
						}	
					}	
				}
				finished1 = !ca1.nextStep();
				finished2 = !ca2.nextStep();
				if (finished1 != finished2) {
					equal = false;
					String finishedCA = finished1? ca1.getClass().getSimpleName() : ca2.getClass().getSimpleName();
					System.out.println("Different final step. " + finishedCA + " finished earlier (step " + ca1.getStep() + ")");
				}
				if (!equal)
					return;
			}
			if (equal)
				System.out.println("Equal!");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static <Number_Type extends Number & FieldElement<Number_Type> & Comparable<Number_Type>> void compareAllSteps(NumericModel4D<Number_Type> ca1, LongModel4D ca2) {
		try {
			System.out.println("Comparing...");
			boolean finished1 = false;
			boolean finished2 = false;
			boolean equal = true;
			while (!finished1 && !finished2) {
//				System.out.println("Comparing step " + ca1.getStep());
				for (int z = ca1.getMinZ(); z <= ca1.getMaxZ(); z++) {
					for (int y = ca1.getMinYAtZ(z); y <= ca1.getMaxYAtZ(z); y++) {
						for (int x = ca2.getMinXAtYZ(y,z); x <= ca2.getMaxXAtYZ(y,z); x++) {
							for (int w = ca2.getMinW(x,y,z); w <= ca2.getMaxW(x,y,z); w++) {
								if (ca1.getFromPosition(w, x, y, z).longValue() != ca2.getFromPosition(w, x, y, z)) {
									equal = false;
									System.out.println("Different value at step " + ca1.getStep() + " (" + w + ", " + x + ", " + y + ", " + z + "): " 
											+ ca2.getClass().getSimpleName() + ":" + ca2.getFromPosition(w, x, y, z) 
											+ " != " + ca1.getClass().getSimpleName() + ":" + ca1.getFromPosition(w, x, y, z));
								}
							}
						}	
					}	
				}
				finished1 = !ca1.nextStep();
				finished2 = !ca2.nextStep();
				if (finished1 != finished2) {
					equal = false;
					String finishedCA = finished1? ca1.getClass().getSimpleName() : ca2.getClass().getSimpleName();
					System.out.println("Different final step. " + finishedCA + " finished earlier (step " + ca1.getStep() + ")");
				}
				if (!equal)
					return;
			}
			if (equal)
				System.out.println("Equal!");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static <Number_Type extends Number & FieldElement<Number_Type> & Comparable<Number_Type>> void compareAllSteps(NumericModel1D<Number_Type> ca1, LongModel1D ca2) {
		try {
			System.out.println("Comparing...");
			boolean finished1 = false;
			boolean finished2 = false;
			boolean equal = true;
			while (!finished1 && !finished2) {
//				System.out.println("Comparing step " + ca1.getStep());
				for (int x = ca2.getMinX(); x <= ca2.getMaxX(); x++) {
					if (ca1.getFromPosition(x).longValue() != ca2.getFromPosition(x)) {
						equal = false;
						System.out.println("Different value at step " + ca1.getStep() + " (" + x + "): " 
								+ ca2.getClass().getSimpleName() + ":" + ca2.getFromPosition(x) 
								+ " != " + ca1.getClass().getSimpleName() + ":" + ca1.getFromPosition(x));
					}
				}	
				finished1 = !ca1.nextStep();
				finished2 = !ca2.nextStep();
				if (finished1 != finished2) {
					equal = false;
					String finishedCA = finished1? ca1.getClass().getSimpleName() : ca2.getClass().getSimpleName();
					System.out.println("Different final step. " + finishedCA + " finished earlier (step " + ca1.getStep() + ")");
				}
				if (!equal)
					return;
			}
			if (equal)
				System.out.println("Equal!");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static <Number_Type extends Number & FieldElement<Number_Type> & Comparable<Number_Type>> void compareAllSteps(NumericModel3D<Number_Type> ca1, IntModel3D ca2) {
		try {
			System.out.println("Comparing...");
			boolean finished1 = false;
			boolean finished2 = false;
			boolean equal = true;
			while (!finished1 && !finished2) {
//				System.out.println("Comparing step " + ca1.getStep());
				for (int z = ca1.getMinZ(); z <= ca1.getMaxZ(); z++) {
					for (int y = ca1.getMinYAtZ(z); y <= ca1.getMaxYAtZ(z); y++) {
						for (int x = ca2.getMinX(y,z); x <= ca2.getMaxX(y,z); x++) {
							if (ca1.getFromPosition(x, y, z).intValue() != ca2.getFromPosition(x, y, z)) {
								equal = false;
								System.out.println("Different value at step " + ca1.getStep() + " (" + x + ", " + y + ", " + z + "): " 
										+ ca2.getClass().getSimpleName() + ":" + ca2.getFromPosition(x, y, z) 
										+ " != " + ca1.getClass().getSimpleName() + ":" + ca1.getFromPosition(x, y, z));
							}
						}	
					}	
				}
				finished1 = !ca1.nextStep();
				finished2 = !ca2.nextStep();
				if (finished1 != finished2) {
					equal = false;
					String finishedCA = finished1? ca1.getClass().getSimpleName() : ca2.getClass().getSimpleName();
					System.out.println("Different final step. " + finishedCA + " finished earlier (step " + ca1.getStep() + ")");
				}
				if (!equal)
					return;
			}
			if (equal)
				System.out.println("Equal!");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static <Number_Type extends FieldElement<Number_Type> & Comparable<Number_Type>> void compareAllSteps(NumericModel3D<Number_Type> ca1, NumericModel3D<Number_Type> ca2) {
		try {
			System.out.println("Comparing...");
			boolean finished1 = false;
			boolean finished2 = false;
			boolean equal = true;
			while (!finished1 && !finished2) {
				System.out.println("Comparing step " + ca1.getStep());
				for (int z = ca1.getMinZ(); z <= ca1.getMaxZ(); z++) {
					for (int y = ca1.getMinYAtZ(z); y <= ca1.getMaxYAtZ(z); y++) {
						for (int x = ca2.getMinX(y,z); x <= ca2.getMaxX(y,z); x++) {
							if (ca1.getFromPosition(x, y, z).compareTo(ca2.getFromPosition(x, y, z)) != 0) {
								equal = false;
								System.out.println("Different value at step " + ca1.getStep() + " (" + x + ", " + y + ", " + z + "): " 
										+ ca2.getClass().getSimpleName() + ":" + ca2.getFromPosition(x, y, z) 
										+ " != " + ca1.getClass().getSimpleName() + ":" + ca1.getFromPosition(x, y, z));
							}
						}	
					}	
				}
				finished1 = !ca1.nextStep();
				finished2 = !ca2.nextStep();
				if (finished1 != finished2) {
					equal = false;
					String finishedCA = finished1? ca1.getClass().getSimpleName() : ca2.getClass().getSimpleName();
					System.out.println("Different final step. " + finishedCA + " finished earlier (step " + ca1.getStep() + ")");
				}
				if (!equal)
					return;
			}
			if (equal)
				System.out.println("Equal!");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static <Number_Type extends FieldElement<Number_Type> & Comparable<Number_Type>> void compareAllSteps(NumericModel4D<Number_Type> ca1, NumericModel4D<Number_Type> ca2) {
		try {
			System.out.println("Comparing...");
			boolean finished1 = false;
			boolean finished2 = false;
			boolean equal = true;
			while (!finished1 && !finished2) {
				System.out.println("Comparing step " + ca1.getStep());
				for (int z = ca1.getMinZ(); z <= ca1.getMaxZ(); z++) {
					for (int y = ca1.getMinYAtZ(z); y <= ca1.getMaxYAtZ(z); y++) {
						for (int x = ca2.getMinXAtYZ(y,z); x <= ca2.getMaxXAtYZ(y,z); x++) {
							for (int w = ca2.getMinW(x,y,z); w <= ca2.getMaxW(w,y,z); w++) {
								if (ca1.getFromPosition(w, x, y, z).compareTo(ca2.getFromPosition(w, x, y, z)) != 0) {
									equal = false;
									System.out.println("Different value at step " + ca1.getStep() + " (" + w + ", " + x + ", " + y + ", " + z + "): " 
											+ ca2.getClass().getSimpleName() + ":" + ca2.getFromPosition(w, x, y, z) 
											+ " != " + ca1.getClass().getSimpleName() + ":" + ca1.getFromPosition(w, x, y, z));
								}
							}
						}	
					}	
				}
				finished1 = !ca1.nextStep();
				finished2 = !ca2.nextStep();
				if (finished1 != finished2) {
					equal = false;
					String finishedCA = finished1? ca1.getClass().getSimpleName() : ca2.getClass().getSimpleName();
					System.out.println("Different final step. " + finishedCA + " finished earlier (step " + ca1.getStep() + ")");
				}
				if (!equal)
					return;
			}
			if (equal)
				System.out.println("Equal!");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void compareAllSteps(LongModel5D ca1, LongModel5D ca2) {
		try {
			System.out.println("Comparing...");
			boolean finished1 = false;
			boolean finished2 = false;
			boolean equal = true;
			while (!finished1 && !finished2) {
				System.out.println("step " + ca1.getStep());
				for (int z = ca1.getMinZ(); z <= ca1.getMaxZ(); z++) {
					for (int y = ca1.getMinYAtZ(z); y <= ca1.getMaxYAtZ(z); y++) {
						for (int x = ca1.getMinXAtYZ(y,z); x <= ca1.getMaxXAtYZ(y,z); x++) {
							for (int w = ca1.getMinWAtXYZ(x,y,z); w <= ca1.getMaxWAtXYZ(x,y,z); w++) {
								for (int v = ca1.getMinV(w,x,y,z); v <= ca1.getMaxV(w,x,y,z); v++) {
									if (ca1.getFromPosition(v, w, x, y, z) != ca2.getFromPosition(v, w, x, y, z)) {
										equal = false;
										System.out.println("Different value at step " + ca1.getStep() + " (" + v + ", " + w + ", " + x + ", " + y + ", " + z + "): " 
												+ ca1.getClass().getSimpleName() + ":" + ca1.getFromPosition(v, w, x, y, z) 
												+ " != " + ca2.getClass().getSimpleName() + ":" + ca2.getFromPosition(v, w, x, y, z));
									}
								}
							}
						}	
					}	
				}
				finished1 = !ca1.nextStep();
				finished2 = !ca2.nextStep();
				if (finished1 != finished2) {
					equal = false;
					String finishedCA = finished1? ca1.getClass().getSimpleName() : ca2.getClass().getSimpleName();
					System.out.println("Different final step. " + finishedCA + " finished earlier (step " + ca1.getStep() + ")");
				}
				if (!equal)
					break;
			}
			if (equal)
				System.out.println("Equal!");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void compareAllSteps(IntModel5D ca1, LongModel5D ca2) {
		compareAllSteps(ca2, ca1);
	}
	
	public static void compareAllSteps(LongModel5D ca1, IntModel5D ca2) {
		try {
			System.out.println("Comparing...");
			boolean finished1 = false;
			boolean finished2 = false;
			boolean equal = true;
			while (!finished1 && !finished2) {
				System.out.println("step " + ca1.getStep());
				for (int z = ca1.getMinZ(); z <= ca1.getMaxZ(); z++) {
					for (int y = ca1.getMinYAtZ(z); y <= ca1.getMaxYAtZ(z); y++) {
						for (int x = ca1.getMinXAtYZ(y,z); x <= ca1.getMaxXAtYZ(y,z); x++) {
							for (int w = ca1.getMinWAtXYZ(x,y,z); w <= ca1.getMaxWAtXYZ(x,y,z); w++) {
								for (int v = ca1.getMinV(w,x,y,z); v <= ca1.getMaxV(w,x,y,z); v++) {
									if (ca1.getFromPosition(v, w, x, y, z) != ca2.getFromPosition(v, w, x, y, z)) {
										equal = false;
										System.out.println("Different value at step " + ca1.getStep() + " (" + v + ", " + w + ", " + x + ", " + y + ", " + z + "): " 
												+ ca1.getClass().getSimpleName() + ":" + ca1.getFromPosition(v, w, x, y, z) 
												+ " != " + ca2.getClass().getSimpleName() + ":" + ca2.getFromPosition(v, w, x, y, z));
									}
								}
							}
						}	
					}	
				}
				finished1 = !ca1.nextStep();
				finished2 = !ca2.nextStep();
				if (finished1 != finished2) {
					equal = false;
					String finishedCA = finished1? ca1.getClass().getSimpleName() : ca2.getClass().getSimpleName();
					System.out.println("Different final step. " + finishedCA + " finished earlier (step " + ca1.getStep() + ")");
				}
				if (!equal)
					break;
			}
			if (equal)
				System.out.println("Equal!");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void compareAllStepsWithIterators(LongModel1D ca1, LongModel1D ca2) {
		try {
			System.out.println("Comparing...");
			boolean finished1 = false;
			boolean finished2 = false;
			boolean equal = true;
			while (!finished1 && !finished2) {
				Iterator<Long> iterator1 = ca1.iterator();
				Iterator<Long> iterator2 = ca2.iterator();
//				System.out.println("Comparing step " + ca1.getStep());
				while (iterator1.hasNext() && iterator2.hasNext()) {
					long value1 = iterator1.next();
					long value2 = iterator2.next();
					if (value1 != value2) {
						equal = false;
						System.out.println("Different value at step " + ca1.getStep() + ": " 
								+ ca1.getClass().getSimpleName() + ":" + value1 
								+ " != " + ca2.getClass().getSimpleName() + ":" + value2);
						//return;
					}
				}
				finished1 = !ca1.nextStep();
				finished2 = !ca2.nextStep();
				if (finished1 != finished2) {
					equal = false;
					String finishedCA = finished1? ca1.getClass().getSimpleName() : ca2.getClass().getSimpleName();
					System.out.println("Different final step. " + finishedCA + " finished earlier (step " + ca1.getStep() + ")");
				}
				if (!equal) {
					return;
				}
			}
			if (equal)
				System.out.println("Equal!");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void printMinAndMaxValues(LongModel ca) {
		try {
			do {
				long[] minAndMax = ca.getMinAndMax();
				System.out.println("min: " + minAndMax[0] + "\t\tmax: " + minAndMax[1]);
			} while(ca.nextStep());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void checkTotalValueConservation(LongModel ca) {
		System.out.println("Checking total value conservation...");
		try {
			long value = ca.getTotal(), newValue = value;
			boolean finished = false;
			while (value == newValue && !finished) {
				finished = !ca.nextStep();
				newValue = ca.getTotal();
			}
			if (!finished) {
				System.out.println("Total value changed at step " + ca.getStep() + ". Previous value " + value + ", new value " + newValue);
			} else {
				System.out.println("The total value remained constant!");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static <Number_Type extends FieldElement<Number_Type> & Comparable<Number_Type>> void checkTotalValueConservation(NumericModel<Number_Type> ca) {
		System.out.println("Checking total value conservation...");
		try {
			Number_Type value = ca.getTotal(), newValue = value;
			boolean finished = false;
			while (value.equals(newValue) && !finished) {
				finished = !ca.nextStep();
				newValue = ca.getTotal();
			}
			if (!finished) {
				System.out.println("Total value changed at step " + ca.getStep() + ". Previous value " + value + ", new value " + newValue);
			} else {
				System.out.println("The total value remained constant!");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void printTotalValueEvolution(LongModel ca) {
		try {
			do {
				System.out.println("step " + ca.getStep() + ": " + ca.getTotal());
			}
			while (ca.nextStep());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void checkTotalValueConservation(IntModel ca) {
		System.out.println("Checking total value conservation...");
		try {
			int value = ca.getTotal(), newValue = value;
			boolean finished = false;
			while (value == newValue && !finished) {
				finished = !ca.nextStep();
				newValue = ca.getTotal();
			}
			if (!finished) {
				System.out.println("Total value changed at step " + ca.getStep() + ". Previous value " + value + ", new value " + newValue);
			} else {
				System.out.println("The total value remained constant!");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void stepByStep(IntModel2D ca) {
		try {
			Scanner s = new Scanner(System.in);
			do {
				System.out.println("step " + ca.getStep());
				Utils.printAsGrid(ca);
				System.out.println("total value " + ca.getTotal());
				s.nextLine();
			} while (ca.nextStep());
			s.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void stepByStep(LongModel1D ca) {
		try {
			Scanner s = new Scanner(System.in);
			do {
				System.out.println("step " + ca.getStep());
				Utils.printAsGrid(ca);
				System.out.println("total value " + ca.getTotal());
				s.nextLine();
			} while (ca.nextStep());
			s.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void stepByStep(LongModel2D ca) {
		try {
			Scanner s = new Scanner(System.in);
			do {
				System.out.println("step " + ca.getStep());
				Utils.printAsGrid(ca);
				System.out.println("total value " + ca.getTotal());
				s.nextLine();
			} while (ca.nextStep());
			s.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static <Number_Type extends FieldElement<Number_Type> & Comparable<Number_Type>> void stepByStep(NumericModel1D<Number_Type> ca) {
		try {
			Scanner s = new Scanner(System.in);
			do {
				System.out.println("step " + ca.getStep());
				Utils.printAsGrid(ca);
				System.out.println("total value " + ca.getTotal());
				s.nextLine();
			} while (ca.nextStep());
			s.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static <Number_Type extends Number & FieldElement<Number_Type> & Comparable<Number_Type>> void stepByStep(NumericModel2D<Number_Type> ca) {
		try {
			Scanner s = new Scanner(System.in);
			do {
				System.out.println("step " + ca.getStep());
				Utils.printAsGrid(ca);
				System.out.println("total value " + ca.getTotal());
				s.nextLine();
			} while (ca.nextStep());
			s.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void race(Model... cas) {
		try {
			long millis;
			for (Model ca : cas) {
				millis = System.currentTimeMillis();
				while (ca.nextStep());
				System.out.println(ca.getClass().getSimpleName() + ": " + (System.currentTimeMillis() - millis));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static long[][] parseCSVLong2DArray(String pathName) throws IOException {
		long[][] array = null;
		try(BufferedReader br = new BufferedReader(new FileReader(pathName))) {
		    String line = br.readLine();
		    if (line != null) {
		    	List<long[]> rows = new ArrayList<long[]>();
		    	int lineIndex = 1; 
		    	String[] elements = line.split(",");
		    	int colCount = elements.length;
		    	long[] row = new long[colCount];
		    	for (int i = 0; i < colCount; i++) {
		    		row[i] = Long.parseLong(elements[i]);
		    	}
		    	rows.add(row);
		    	line = br.readLine();
		    	while (line != null) {
		    		lineIndex++;
		    		elements = line.split(",");
		    		if (elements.length != colCount)
		    			throw new IllegalArgumentException("Wrong number of elements at line " + lineIndex);
		    		row = new long[colCount];
			    	for (int i = 0; i < colCount; i++) {
			    		row[i] = Long.parseLong(elements[i]);
			    	}
			    	rows.add(row);
			        line = br.readLine();
			    }
		    	int rowCount = rows.size();
		    	array = new long[colCount][rowCount];
		    	for (int y = 0; y < rowCount; y++) {
		    		row = rows.get(y);
	    			for (int x = 0; x < colCount; x++) {
	    				array[x][y] = row[x];
			    	}
		    	}
		    }
		}
		return array;
	}
	
	public static void compareAllSteps(IntModel ca1, IntModel ca2) {
		try {
			System.out.println("Comparing...");
			boolean finished1 = false;
			boolean finished2 = false;
//			boolean equal = true;
			while (!finished1 && !finished2) {
				System.out.println("Comparing step " + ca1.getStep());//debug
				ca1.forEachPosition(new Consumer<Coordinates>() {
					
					@Override
					public void accept(Coordinates coordinates) {
						try {
							if (ca1.getFromPosition(coordinates) != ca2.getFromPosition(coordinates)) {
//							equal = false;
								System.out.println("Different value at step " + ca1.getStep() + " " + coordinates + ": " 
										+ ca1.getClass().getSimpleName() + ":" + ca1.getFromPosition(coordinates) 
										+ " != " + ca2.getClass().getSimpleName() + ":" + ca2.getFromPosition(coordinates));
							}
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				});
				finished1 = !ca1.nextStep();
				finished2 = !ca2.nextStep();
				if (finished1 != finished2) {
//					equal = false;
					String finishedCA = finished1? ca1.getClass().getSimpleName() : ca2.getClass().getSimpleName();
					System.out.println("Different final step. " + finishedCA + " finished earlier (step " + ca1.getStep() + ")");
				}
			}
//			if (equal)
//				System.out.println("Equal");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void compareAllSteps(LongModel ca1, IntModel ca2) {
		try {
			System.out.println("Comparing...");
			boolean finished1 = false;
			boolean finished2 = false;
//			boolean equal = true;
			while (!finished1 && !finished2) {
				System.out.println("Comparing step " + ca1.getStep());//debug
				ca1.forEachPosition(new Consumer<Coordinates>() {
					
					@Override
					public void accept(Coordinates coordinates) {
						try {
							if (ca1.getFromPosition(coordinates) != ca2.getFromPosition(coordinates)) {
//							equal = false;
								System.out.println("Different value at step " + ca1.getStep() + " " + coordinates + ": " 
										+ ca1.getClass().getSimpleName() + ":" + ca1.getFromPosition(coordinates) 
										+ " != " + ca2.getClass().getSimpleName() + ":" + ca2.getFromPosition(coordinates));
							}
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				});
				finished1 = !ca1.nextStep();
				finished2 = !ca2.nextStep();
				if (finished1 != finished2) {
//					equal = false;
					String finishedCA = finished1? ca1.getClass().getSimpleName() : ca2.getClass().getSimpleName();
					System.out.println("Different final step. " + finishedCA + " finished earlier (step " + ca1.getStep() + ")");
				}
			}
//			if (equal)
//				System.out.println("Equal");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void stepByStep(IntModel ca) {
		try {
			Scanner s = new Scanner(System.in);
			Coordinates crossSectionCoordinates = new Coordinates(new int[ca.getGridDimension()]);
			do {
				System.out.println("step " + ca.getStep());
				System.out.println(ca.toString2DCrossSection(0, 1, crossSectionCoordinates));
				System.out.println("total value " + ca.getTotal());
				s.nextLine();
			} while (ca.nextStep());
			s.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
