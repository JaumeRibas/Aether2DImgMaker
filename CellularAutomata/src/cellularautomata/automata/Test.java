/* CellularSharing2DImgMaker -- console app to generate images from the Cellular Sharing 2D cellular automaton
    Copyright (C) 2017 Jaume Ribas

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

import java.math.BigInteger;
import java.util.Scanner;
import java.util.concurrent.ExecutionException;

import cellularautomata.grid.BigIntegerGrid2D;
import cellularautomata.grid.LongGrid2D;

public class Test {
	
	public static void main(String[] args) {
		long initialValue = 10000;
		CellularSharing2D cs = new CellularSharing2D(initialValue);
		CellularSharingSimple2D csSimple = new CellularSharingSimple2D(initialValue);
		compare(csSimple, cs);
	}
	
	public static void checkValueConservation(SymmetricLongCellularAutomaton2D ca) throws InterruptedException, ExecutionException {
		long value = ca.getTotalValue(), newValue = value;
		boolean finished = false;
		while (value == newValue && !finished) {
			finished = !ca.nextStep();
			newValue = ca.getTotalValue();
		}
		if (!finished) {
			System.out.println("Value changed at step " + ca.getCurrentStep() + ". Original value " + value + " new value " + newValue);
		} else {
			System.out.println("The value remained constant!");
		}
	}
	
	public static void stepByStep(SymmetricLongCellularAutomaton2D ca) throws InterruptedException, ExecutionException {
		Scanner s = new Scanner(System.in);
		do {
			System.out.println("step " + ca.getCurrentStep());
			printAsGrid(ca);
			System.out.println("totalValue " + ca.getTotalValue());
			s.nextLine();
		} while (ca.nextStep());
		s.close();
	}
	
	public static void compare(LongCellularAutomaton2D ca1, SymmetricLongCellularAutomaton2D ca2) {
		try {
			boolean finished1 = false;
			boolean finished2 = false;
			boolean equal = true;
			while (!finished1 && !finished2) {
				for (int y = ca1.getMinY(); y <= ca1.getMaxY(); y++) {
					for (int x = ca2.getMinX(); x <= ca2.getMaxX(); x++) {
						if (ca1.getValueAt(x, y) != ca2.getValueAt(x, y)) {
							equal = false;
							System.out.println("Different value at step " + ca1.getCurrentStep() + " (" + x + ", " + y + "): " 
									+ ca2.getClass().getSimpleName() + ":" + ca2.getValueAt(x, y) 
									+ " != " + ca1.getClass().getSimpleName() + ":" + ca1.getValueAt(x, y));
						}
					}	
				}
				finished1 = !ca1.nextStep();
				finished2 = !ca2.nextStep();
				if (finished1 != finished2) {
					equal = false;
					String finishedCA = finished1? ca1.getClass().getSimpleName() : ca2.getClass().getSimpleName();
					System.out.println("Different final step. " + finishedCA + " finished earlier (step " + ca1.getCurrentStep() + ")");
				}
			}
			if (equal)
				System.out.println("Equal");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void printAsGrid(LongGrid2D m) {
		int maxX = m.getMaxX(), maxY = m.getMaxY(), minX = m.getMinX(), minY = m.getMinY();
		printAsGrid(m, minX, maxX, minY, maxY);
	}
	
	public static void printAsGrid(BigIntegerGrid2D m) {
		int maxX = m.getMaxX(), maxY = m.getMaxY(), minX = m.getMinX(), minY = m.getMinY();
		printAsGrid(m, minX, maxX, minY, maxY);
	}
	
	public static void printAsGrid(LongGrid2D m, int minX, int maxX, int minY, int maxY) {
		int maxDigits = 3;
		for (int y = maxY; y >= minY; y--) {
			for (int x = minX; x <= maxX; x++) {
				int digits = Long.toString(m.getValueAt(x, y)).length();
				if (digits > maxDigits)
					maxDigits = digits;
			}
		}
		String headFootGap = "";
		for (int i = 0; i < maxDigits; i++) {
			headFootGap += "-";
		}
		String headFoot = "+";
		for (int i = minX; i <= maxX; i++) {
			headFoot += headFootGap + "+";
		}
		for (int y = maxY; y >= minY; y--) {
			System.out.println(headFoot);
			for (int x = minX; x <= maxX; x++) {
				String strVal = " ";
				long val = m.getValueAt(x, y);
				if (val != 0) {
					strVal = val + "";
				}
				System.out.print("|" + padLeft(strVal, ' ', maxDigits));
			}
			System.out.println("|");
		}
		System.out.println(headFoot);
	}
	
	public static void printAsGrid(BigIntegerGrid2D m, int minX, int maxX, int minY, int maxY) {
		int maxDigits = 3;
		for (int y = maxY; y >= minY; y--) {
			for (int x = minX; x <= maxX; x++) {
				int digits = m.getValueAt(x, y).toString().length();
				if (digits > maxDigits)
					maxDigits = digits;
			}
		}
		String headFootGap = "";
		for (int i = 0; i < maxDigits; i++) {
			headFootGap += "-";
		}
		String headFoot = "+";
		for (int i = minX; i <= maxX; i++) {
			headFoot += headFootGap + "+";
		}
		for (int y = maxY; y >= minY; y--) {
			System.out.println(headFoot);
			for (int x = minX; x <= maxX; x++) {
				String strVal = " ";
				BigInteger val = m.getValueAt(x, y);
				if (!val.equals(BigInteger.ZERO)) {
					strVal = val + "";
				}
				System.out.print("|" + padLeft(strVal, ' ', maxDigits));
			}
			System.out.println("|");
		}
		System.out.println(headFoot);
	}	
	
	public static String padLeft(String source, char c, int totalLength) {
		int margin = totalLength - source.length();
		for (int i = 0; i < margin; i++) {
			source = c + source;
		}
		return source;
	}

}
