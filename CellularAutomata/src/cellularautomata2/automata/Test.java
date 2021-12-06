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
package cellularautomata2.automata;

import java.util.Scanner;
import java.util.function.Consumer;

import cellularautomata.grid.Coordinates;
import cellularautomata2.grid.IntGridRegion;

public class Test {

	public static void main(String[] args) {
		testSpreadIntegerValue();
	}
	
	public static void testSpreadIntegerValue() {
		try {
			int dimension = 2;
			int initialValue = 5000;
			int backgroundValue = 0;
			SpreadIntegerValueSimple siv = new SpreadIntegerValueSimple(dimension, initialValue, backgroundValue);
			SpreadIntegerValue siv2 = new SpreadIntegerValue(dimension, initialValue, backgroundValue);
			compare(siv, siv, siv2, siv2);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void compare(CellularAutomaton ca1, IntGridRegion region1, CellularAutomaton ca2, IntGridRegion region2) {
		try {
			System.out.println("Comparing...");
			boolean finished1 = false;
			boolean finished2 = false;
//			boolean equal = true;
			while (!finished1 && !finished2) {
				region1.forEachPosition(new Consumer<Coordinates>() {
					
					@Override
					public void accept(Coordinates coordinates) {
						if (region1.getValue(coordinates) != region2.getValue(coordinates)) {
//							equsal = false;
							System.out.println("Different value at step " + ca1.getStep() + " " + coordinates + ": " 
									+ ca1.getClass().getSimpleName() + ":" + region1.getValue(coordinates) 
									+ " != " + ca2.getClass().getSimpleName() + ":" + region2.getValue(coordinates));
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
	
	public static void testSpreadIntegerValue1D() {
		int counter = 0;
		try {
			SpreadIntegerValueSimple siv = new SpreadIntegerValueSimple(1, 0, 9);
			do {
				counter++;
				siv.forEachPosition(new Consumer<Coordinates>() {
					
					@Override
					public void accept(Coordinates coordinates) {
						System.out.print(siv.getValue(coordinates) + " ");
					}
				});
				System.out.println();
				System.out.println("total: " + siv.getTotalValue());
				if (counter == 50) break;
			} while (siv.nextStep());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void testSpreadIntegerValue2AndUpD() {
		//int counter = 0;
		try {
			int dimension = 2;
			SpreadIntegerValueSimple siv = new SpreadIntegerValueSimple(dimension, 32, 0);
			int[] crossSectionCoordinatesArray = new int[dimension];
			//crossSectionCoordinatesArray[2] = 1;
			Coordinates crossSectionCoordinates = new Coordinates(crossSectionCoordinatesArray);
			do {
				//counter++;
				System.out.println(siv.toString2DCrossSection(0, 1, crossSectionCoordinates));
				System.out.println("total: " + siv.getTotalValue());
				System.out.println(System.lineSeparator());
				//if (counter == 50) break;
			} while (siv.nextStep());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void stepByStep(CellularAutomaton ca, IntGridRegion region) {
		try {
			Scanner s = new Scanner(System.in);
			do {
				System.out.println("step " + ca.getStep());
				System.out.println(region.toString2DCrossSection(0, 1, new Coordinates(new int[region.getGridDimension()])));
				System.out.println("total value " + region.getTotalValue());
				s.nextLine();
			} while (ca.nextStep());
			s.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
}
