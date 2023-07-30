/* Aether2DImgMaker -- console app to generate images of the Aether cellular automaton in 2D
    Copyright (C) 2017-2023 Jaume Ribas

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
package cellularautomata.arrays;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.function.Consumer;

import cellularautomata.Coordinates;
import cellularautomata.Utils;

public final class Test {	
	
	private Test() {}

	public static void main(String[] args) {
		testHyperrectangularIntArray2();
	}
	
	public static void testHypercubicIntArray() {
		int side = 3;
		int dimension = 5;
		int positionCount = (int) Math.pow(side, dimension);
		HypercubicIntArray arr = new HypercubicIntArray(dimension, side);
		System.out.println("dimension: " + arr.getDimension());
		HashSet<Coordinates> indexSets = new HashSet<Coordinates>();
		ArrayList<Integer> list = new ArrayList<Integer>();
		System.out.println("Setting values.");
		arr.forEachIndex(new Consumer<Coordinates>() {
			
			@Override
			public void accept(Coordinates indexes) {
				if (indexSets.contains(indexes)) {
					System.err.println("Indexes " + indexes + " are repeated.");
				}
				indexSets.add(indexes);
				int value = Utils.getRandomInt(Integer.MIN_VALUE, Integer.MAX_VALUE - 1);
				list.add(value);
				arr.set(indexes, value);		
			}
		});
		System.out.println("Getting values.");
		Iterator<Integer> iterator = list.iterator();
		arr.forEachIndex(new Consumer<Coordinates>() {
			
			@Override
			public void accept(Coordinates indexes) {
				int retrievedValue = arr.get(indexes);
				int insertedValue = iterator.next();
				if (retrievedValue != insertedValue) {
					System.err.println("Retrieved value ("+retrievedValue+") is different from previouly inserted value ("+insertedValue+") at indexes " + indexes + ".");
				}	
			}
		});
		if (list.size() != positionCount) {
			System.err.println("Wrong position count.");
		}
		System.out.println("Testing even index traversal.");
		HashSet<Coordinates> evenIndexes = new HashSet<Coordinates>();
		arr.forEachEvenIndex(new Consumer<Coordinates>() {
			
			@Override
			public void accept(Coordinates indexes) {
				if (evenIndexes.contains(indexes)) {
					System.err.println("Indexes " + indexes + " are repeated.");
				}
				evenIndexes.add(indexes);
				if (!Utils.isEvenPosition(indexes)) {
					System.err.println(indexes + " is not even.");
				}	
			}
		});	
		if (Math.abs(evenIndexes.size() - positionCount/2) > 1) {
			System.err.println("Wrong even position count.");
		}
		System.out.println("Testing odd index traversal.");
		HashSet<Coordinates> oddIndexes = new HashSet<Coordinates>();
		arr.forEachOddIndex(new Consumer<Coordinates>() {
			
			@Override
			public void accept(Coordinates indexes) {
				if (oddIndexes.contains(indexes)) {
					System.err.println("Indexes " + indexes + " are repeated.");
				}
				oddIndexes.add(indexes);
				if (Utils.isEvenPosition(indexes)) {
					System.err.println(indexes + " is not odd.");
				}	
			}
		});	
		if (Math.abs(oddIndexes.size() - positionCount/2) > 1) {
			System.err.println("Wrong odd position count.");
		}
	}
	
	public static void testHyperrectangularIntArray2() {
		int dimension = 5;
		int[] sizes = new int[dimension];
		int positionCount = 1;
		for (int i = 0; i < dimension; i++) {
			sizes[i] = Utils.getRandomInt(2, 6);
			positionCount *= sizes[i];
		}
		System.out.println(Arrays.toString(sizes));
		HyperrectangularIntArray arr = new HyperrectangularIntArray(sizes);
		System.out.println("dimension: " + arr.getDimension());
		HashSet<Coordinates> indexSets = new HashSet<Coordinates>();
		ArrayList<Integer> list = new ArrayList<Integer>();
		System.out.println("Setting values.");
		arr.forEachIndex(new Consumer<Coordinates>() {
			
			@Override
			public void accept(Coordinates indexes) {
				if (indexSets.contains(indexes)) {
					System.err.println("Indexes " + indexes + " are repeated.");
				}
				indexSets.add(indexes);
				int value = Utils.getRandomInt(Integer.MIN_VALUE, Integer.MAX_VALUE - 1);
				list.add(value);
				arr.set(indexes, value);		
			}
		});
		System.out.println("Getting values.");
		Iterator<Integer> iterator = list.iterator();
		arr.forEachIndex(new Consumer<Coordinates>() {
			
			@Override
			public void accept(Coordinates indexes) {
				int retrievedValue = arr.get(indexes);
				int insertedValue = iterator.next();
				if (retrievedValue != insertedValue) {
					System.err.println("Retrieved value ("+retrievedValue+") is different from previouly inserted value ("+insertedValue+") at indexes " + indexes + ".");
				}
			}
		});		
		if (list.size() != positionCount) {
			System.err.println("Wrong position count.");
		}
		System.out.println("Testing even index traversal.");
		HashSet<Coordinates> evenIndexes = new HashSet<Coordinates>();
		arr.forEachEvenIndex(new Consumer<Coordinates>() {
			
			@Override
			public void accept(Coordinates indexes) {
				if (evenIndexes.contains(indexes)) {
					System.err.println("Indexes " + indexes + " are repeated.");
				}
				evenIndexes.add(indexes);
				if (!Utils.isEvenPosition(indexes)) {
					System.err.println(indexes + " is not even.");
				}	
			}
		});	
		if (Math.abs(evenIndexes.size() - positionCount/2) > 1) {
			System.err.println("Wrong even position count.");
		}
		System.out.println("Testing odd index traversal.");
		HashSet<Coordinates> oddIndexes = new HashSet<Coordinates>();
		arr.forEachOddIndex(new Consumer<Coordinates>() {
			
			@Override
			public void accept(Coordinates indexes) {
				if (oddIndexes.contains(indexes)) {
					System.err.println("Indexes " + indexes + " are repeated.");
				}
				oddIndexes.add(indexes);
				if (Utils.isEvenPosition(indexes)) {
					System.err.println(indexes + " is not odd.");
				}	
			}
		});	
		if (Math.abs(oddIndexes.size() - positionCount/2) > 1) {
			System.err.println("Wrong odd position count.");
		}
	}
	
	public static void testRectangularIntArray() {
		HyperrectangularIntArray arr = new HyperrectangularIntArray(new int[] { 50, 13, 12, 32 });
		System.out.println("dimension: " + arr.getDimension());
		int size0 = arr.getSize(0);
		int size1 = arr.getSize(1);
		int size2 = arr.getSize(2);
		int size3 = arr.getSize(3);
		ArrayList<Integer> list = new ArrayList<Integer>();
		System.out.println("Setting values.");
		for (int i = 0; i < size0; i++) {
			for (int j = 0; j < size1; j++) {
				for (int k = 0; k < size2; k++) {
					for (int l = 0; l < size3; l++) {
						int value = Utils.getRandomInt(Integer.MIN_VALUE, Integer.MAX_VALUE - 1);
						list.add(value);
						arr.set(new Coordinates(new int[] {i, j, k, l}), value);
					}
				}
			}
		}
		System.out.println(list.size());
		int listIndex = 0;
		System.out.println("Getting values.");
		for (int i = 0; i < size0; i++) {
			for (int j = 0; j < size1; j++) {
				for (int k = 0; k < size2; k++) {
					for (int l = 0; l < size3; l++) {
						Coordinates indexes = new Coordinates(new int[] {i, j, k, l});
						int value = arr.get(indexes);
						if (value != list.get(listIndex)) {
							System.err.println("Retrieved value is different from previouly inserted value at indexes " + indexes + ".");
						}
						listIndex++;
					}
				}
			}
		}
	}
	
	public static void testRectangularIntArray3() {
		HyperrectangularIntArray arr = new HyperrectangularIntArray(new int[] { 3, 3, 3 });
		System.out.println("dimension: " + arr.getDimension());
		arr.forEachIndex(new Consumer<Coordinates>() {
			
			@Override
			public void accept(Coordinates indexes) {
				System.out.println(indexes);
			}
		});
	}

}









