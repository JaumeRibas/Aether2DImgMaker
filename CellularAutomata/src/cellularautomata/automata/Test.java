/* Aether2DImgMaker -- console app to generate images of the Aether cellular automaton in 2D
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

import java.util.Scanner;
import java.util.concurrent.ExecutionException;

import cellularautomata.grid.IntGrid2D;
import cellularautomata.grid.LongGrid2D;
import cellularautomata.grid.ShortGrid2D;

public class Test {
	
	public static void main(String[] args) {
		long initialValue = 100000;
		Aether2D ae1 = new Aether2D(initialValue);
		AetherSimple2D ae2 = new AetherSimple2D(initialValue);
		compare(ae1, ae2);
	}
	
	public static void print3DEdgeCSectionAsGridSequence(SymmetricShortCellularAutomaton4D ca) {
		try {
			do {
				System.out.println("Step " + ca.getCurrentStep());
				short[] minAndMaxValue = ca.getMinAndMaxValue();
				System.out.println("Min value " + minAndMaxValue[0] + " Max value " + minAndMaxValue[1]);
				printAsGrid(ca.projected3DEdge().crossSection(0), ca.getBackgroundValue());
			} while (ca.nextStep());
		} catch (InterruptedException | ExecutionException e) {
			e.printStackTrace();
		}
		System.out.println("Finished.");
	}
	
	public static void printMinAndMaxValues(SymmetricLongCellularAutomaton4D ca) {
		try {
			do {
				long[] minAndMax = ca.getMinAndMaxValue();
				System.out.println("min: " + minAndMax[0] + "\t\tmax: " + minAndMax[1]);
			} while(ca.nextStep());
		} catch (InterruptedException | ExecutionException e) {
			e.printStackTrace();
		}
	}
	
	public static void checkValueConservation(SymmetricLongCellularAutomaton2D ca) {
		try {
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
		} catch (InterruptedException | ExecutionException e) {
			e.printStackTrace();
		}
	}
	
	public static void stepByStep(SymmetricLongCellularAutomaton2D ca) {
		try {
			Scanner s = new Scanner(System.in);
			do {
				System.out.println("step " + ca.getCurrentStep());
				printAsGrid(ca, 0);
				System.out.println("totalValue " + ca.getTotalValue());
				s.nextLine();
			} while (ca.nextStep());
			s.close();
		} catch (InterruptedException | ExecutionException e) {
			e.printStackTrace();
		}
	}
	
	public static void stepByStep3DEdgeSurface(SymmetricIntCellularAutomaton4D ca) {
		try {
			Scanner s = new Scanner(System.in);
			do {
				System.out.println("step " + ca.getCurrentStep());
				printAsGrid(ca.projected3DEdge().projectedSurfaceMaxX(ca.getBackgroundValue()), ca.getBackgroundValue());
				System.out.println("totalValue " + ca.getTotalValue());
				s.nextLine();
			} while (ca.nextStep());
			s.close();
		} catch (InterruptedException | ExecutionException e) {
			e.printStackTrace();
		}
	}
	
	public static void stepByStep(SymmetricLongCellularAutomaton3D ca) {
		try {
			Scanner s = new Scanner(System.in);
			do {
				System.out.println("step " + ca.getCurrentStep());
				printAsGrid(ca.crossSection(0), ca.getBackgroundValue());
				System.out.println("totalValue " + ca.getTotalValue());
				s.nextLine();
			} while (ca.nextStep());
			s.close();
		} catch (InterruptedException | ExecutionException e) {
			e.printStackTrace();
		}
	}
	
	public static void stepByStep(SymmetricLongCellularAutomaton4D ca) {
		try {
			Scanner s = new Scanner(System.in);
			do {
				System.out.println("step " + ca.getCurrentStep());
				printAsGrid(ca.crossSection(0,0), ca.getBackgroundValue());
				System.out.println("totalValue " + ca.getTotalValue());
				s.nextLine();
			} while (ca.nextStep());
			s.close();
		} catch (InterruptedException | ExecutionException e) {
			e.printStackTrace();
		}
	}
	
	public static void stepByStep(LongCellularAutomaton2D ca) {
		try {
			Scanner s = new Scanner(System.in);
			do {
				System.out.println("step " + ca.getCurrentStep());
				printAsGrid(ca, ca.getBackgroundValue());
				System.out.println("totalValue " + ca.getTotalValue());
				s.nextLine();
			} while (ca.nextStep());
			s.close();
		} catch (InterruptedException | ExecutionException e) {
			e.printStackTrace();
		}
	}
	
	public static void compare(SymmetricLongCellularAutomaton2D ca1, SymmetricLongCellularAutomaton2D ca2) {
		try {
			System.out.println("Comparing...");
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
	
	public static void compareAbs(SymmetricLongCellularAutomaton2D ca1, SymmetricLongCellularAutomaton2D ca2) {
		try {
			System.out.println("Comparing...");
			boolean finished1 = false;
			boolean finished2 = false;
			boolean equal = true;
			while (!finished1 && !finished2) {
				for (int y = ca1.getMinY(); y <= ca1.getMaxY(); y++) {
					for (int x = ca2.getMinX(); x <= ca2.getMaxX(); x++) {
						if (Math.abs(ca1.getValueAt(x, y)) != Math.abs(ca2.getValueAt(x, y))) {
							equal = false;
							System.out.println("Different absolute value at step " + ca1.getCurrentStep() + " (" + x + ", " + y + "): " 
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
	
	public static void compare(SymmetricLongCellularAutomaton3D ca1, SymmetricLongCellularAutomaton3D ca2) {
		try {
			System.out.println("Comparing...");
			boolean finished1 = false;
			boolean finished2 = false;
			boolean equal = true;
			while (!finished1 && !finished2) {
				for (int z = ca1.getMinZ(); z <= ca1.getMaxZ(); z++) {
					for (int y = ca1.getMinY(); y <= ca1.getMaxY(); y++) {
						for (int x = ca2.getMinX(); x <= ca2.getMaxX(); x++) {
							if (ca1.getValueAt(x, y, z) != ca2.getValueAt(x, y, z)) {
								equal = false;
								System.out.println("Different value at step " + ca1.getCurrentStep() + " (" + x + ", " + y + "): " 
										+ ca2.getClass().getSimpleName() + ":" + ca2.getValueAt(x, y, z) 
										+ " != " + ca1.getClass().getSimpleName() + ":" + ca1.getValueAt(x, y, z));
							}
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
	
	public static void compare(SymmetricLongCellularAutomaton3D ca1, SymmetricIntCellularAutomaton3D ca2) {
		try {
			System.out.println("Comparing...");
			boolean finished1 = false;
			boolean finished2 = false;
			boolean equal = true;
			while (!finished1 && !finished2) {
				for (int z = ca1.getMinZ(); z <= ca1.getMaxZ(); z++) {
					for (int y = ca1.getMinY(); y <= ca1.getMaxY(); y++) {
						for (int x = ca2.getMinX(); x <= ca2.getMaxX(); x++) {
							if (ca1.getValueAt(x, y, z) != ca2.getValueAt(x, y, z)) {
								equal = false;
								System.out.println("Different value at step " + ca1.getCurrentStep() + " (" + x + ", " + y + "): " 
										+ ca2.getClass().getSimpleName() + ":" + ca2.getValueAt(x, y, z) 
										+ " != " + ca1.getClass().getSimpleName() + ":" + ca1.getValueAt(x, y, z));
							}
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
	
	public static void compare(SymmetricLongCellularAutomaton4D ca1, SymmetricLongCellularAutomaton4D ca2) {
		try {
			System.out.println("Comparing...");
			boolean finished1 = false;
			boolean finished2 = false;
			boolean equal = true;
			while (!finished1 && !finished2) {
				for (int z = ca1.getMinZ(); z <= ca1.getMaxZ(); z++) {
					for (int y = ca1.getMinY(); y <= ca1.getMaxY(); y++) {
						for (int x = ca2.getMinX(); x <= ca2.getMaxX(); x++) {
							for (int w = ca2.getMinW(); w <= ca2.getMaxW(); w++) {
								if (ca1.getValueAt(w, x, y, z) != ca2.getValueAt(w, x, y, z)) {
									equal = false;
									System.out.println("Different value at step " + ca1.getCurrentStep() + " (" + w + ", " + x + ", " + y + "): " 
											+ ca2.getClass().getSimpleName() + ":" + ca2.getValueAt(w, x, y, z) 
											+ " != " + ca1.getClass().getSimpleName() + ":" + ca1.getValueAt(w, x, y, z));
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
					System.out.println("Different final step. " + finishedCA + " finished earlier (step " + ca1.getCurrentStep() + ")");
				}
			}
			if (equal)
				System.out.println("Equal");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void compare(SymmetricShortCellularAutomaton4D ca1, SymmetricLongCellularAutomaton4D ca2) {
		try {
			System.out.println("Comparing...");
			boolean finished1 = false;
			boolean finished2 = false;
			boolean equal = true;
			while (!finished1 && !finished2) {
				for (int z = ca1.getMinZ(); z <= ca1.getMaxZ(); z++) {
					for (int y = ca1.getMinY(); y <= ca1.getMaxY(); y++) {
						for (int x = ca2.getMinX(); x <= ca2.getMaxX(); x++) {
							for (int w = ca2.getMinW(); w <= ca2.getMaxW(); w++) {
								if (ca1.getValueAt(w, x, y, z) != ca2.getValueAt(w, x, y, z)) {
									equal = false;
									System.out.println("Different value at step " + ca1.getCurrentStep() + " (" + w + ", " + x + ", " + y + "): " 
											+ ca2.getClass().getSimpleName() + ":" + ca2.getValueAt(w, x, y, z) 
											+ " != " + ca1.getClass().getSimpleName() + ":" + ca1.getValueAt(w, x, y, z));
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
					System.out.println("Different final step. " + finishedCA + " finished earlier (step " + ca1.getCurrentStep() + ")");
				}
			}
			if (equal)
				System.out.println("Equal");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void race(CellularAutomaton[] cas) {
		try {
			long millis;
			for (CellularAutomaton ca : cas) {
				millis = System.currentTimeMillis();
				while (ca.nextStep());
				System.out.println(ca.getClass().getSimpleName() + ": " + (System.currentTimeMillis() - millis));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void printAsGrid(LongGrid2D m, long backgroundValue) {
		int maxX = m.getMaxX(), maxY = m.getMaxY(), minX = m.getMinX(), minY = m.getMinY();
		printAsGrid(m, minX, maxX, minY, maxY, backgroundValue);
	}
	
	public static void printAsGrid(IntGrid2D m, int backgroundValue) {
		int maxX = m.getMaxX(), maxY = m.getMaxY(), minX = m.getMinX(), minY = m.getMinY();
		printAsGrid(m, minX, maxX, minY, maxY, backgroundValue);
	}
	
	public static void printAsGrid(ShortGrid2D m, short backgroundValue) {
		int maxX = m.getMaxX(), maxY = m.getMaxY(), minX = m.getMinX(), minY = m.getMinY();
		printAsGrid(m, minX, maxX, minY, maxY, backgroundValue);
	}
	
	public static void printAsGrid(LongGrid2D m, int minX, int maxX, int minY, int maxY, long backgroundValue) {
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
				if (val != backgroundValue) {
					strVal = val + "";
				}
				System.out.print("|" + padLeft(strVal, ' ', maxDigits));
			}
			System.out.println("|");
		}
		System.out.println(headFoot);
	}
	
	public static void printAsGrid(IntGrid2D m, int minX, int maxX, int minY, int maxY, int backgroundValue) {
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
				int val = m.getValueAt(x, y);
				if (val != backgroundValue) {
					strVal = val + "";
				}
				System.out.print("|" + padLeft(strVal, ' ', maxDigits));
			}
			System.out.println("|");
		}
		System.out.println(headFoot);
	}
	
	public static void printAsGrid(ShortGrid2D m, int minX, int maxX, int minY, int maxY, short backgroundValue) {
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
				if (val != backgroundValue) {
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
