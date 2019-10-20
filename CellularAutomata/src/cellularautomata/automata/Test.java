/* Aether2DImgMaker -- console app to generate images of the Aether cellular automaton in 2D
    Copyright (C) 2017-2019 Jaume Ribas

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

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import cellularautomata.grid.SymmetricGridProcessor;
import cellularautomata.grid1d.LongGrid1D;
import cellularautomata.grid2d.IntGrid2D;
import cellularautomata.grid2d.LongGrid2D;
import cellularautomata.grid2d.ShortGrid2D;
import cellularautomata.grid3d.IntGrid3D;
import cellularautomata.grid3d.LongGrid3D;

public class Test {
	
	public static void main(String[] args) {
		long initialValue = 1000000;
		Aether2D ae1 = new Aether2D(initialValue);
		AetherSimple2D ae2 = new AetherSimple2D(initialValue);
		compare(ae1, ae2);
	}
	
	public static void takeSamples() {
		try {
			System.out.println("Loading backup");
			IntAether3DSwap ca = new IntAether3DSwap("D:/data/Aether3D/-1073741823/backups/IntAether3DSwap_9997", "D:/data/test");
			System.out.println("Taking samples");
			System.out.println("Sample 1 (170,59):" + ca.getValueAtPosition(170, 59, 0));
			System.out.println("Sample 2 (181,66):" + ca.getValueAtPosition(181, 66, 0));
			System.out.println("Sample 3 (385,134):" + ca.getValueAtPosition(385, 134, 0));
			System.out.println("Sample 4 (421,174):" + ca.getValueAtPosition(421, 174, 0));
			System.out.println("Sample 5 (638,225):" + ca.getValueAtPosition(638, 225, 0));
			System.out.println("Sample 6 (658,245):" + ca.getValueAtPosition(658, 245, 0));
			System.out.println("Sample 7 (779,370):" + ca.getValueAtPosition(779, 370, 0));
			System.out.println("Sample 8 (807,396):" + ca.getValueAtPosition(807, 396, 0));
			System.out.println("Sample 9 (3565,90):" + ca.getValueAtPosition(3565, 90, 0));
			System.out.println("Sample 10 (3577,102):" + ca.getValueAtPosition(3577, 102, 0));
			System.out.println("Sample 11 (3653,314):" + ca.getValueAtPosition(3653, 314, 0));
			System.out.println("Sample 12 (3657,374):" + ca.getValueAtPosition(3657, 374, 0));
			System.out.println("Finished");
		} catch (ClassNotFoundException | IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void generateBackup() {
		SymmetricIntCellularAutomaton3D ca;
		String path = "D:/data/test";
		int initialValue = -1000;
		try {
			ca = new IntAether3DSwap(initialValue, 1024 * 100, path);
			for (int i = 0; i < 100; i++) {
				ca.nextStep();
				System.out.println("step " + ca.getStep());
			}
			String backupPath = path + "/" + ca.getSubFolderPath() + "/backup";
			System.out.println("Backing up at " + backupPath);
			ca.backUp(backupPath, ca.getName() + "_" + ca.getStep());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void testAdvance() {
		SymmetricIntCellularAutomaton3D ca;
		String path = "D:/data/test";
		int initialValue = -1000;
		try {
			ca = new IntAether3DSwap(initialValue, 1024 * 500, path);//1MiB
			while (ca.nextStep()) {
				System.out.println("step " + ca.getStep());
			}
			System.out.println("Finished");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void compare(SymmetricLongCellularAutomaton1D ca1, SymmetricLongCellularAutomaton1D ca2) {
		try {
			System.out.println("Comparing...");
			boolean finished1 = false;
			boolean finished2 = false;
			boolean equal = true;
			while (!finished1 && !finished2) {
				//System.out.println("step " + ca1.getStep());
				for (int x = ca2.getMinX(); x <= ca2.getMaxX(); x++) {
					if (ca1.getValueAtPosition(x) != ca2.getValueAtPosition(x)) {
						equal = false;
						System.out.println("Different value at step " + ca1.getStep() + " (" + x + "): " 
								+ ca2.getClass().getSimpleName() + ":" + ca2.getValueAtPosition(x) 
								+ " != " + ca1.getClass().getSimpleName() + ":" + ca1.getValueAtPosition(x));
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
				System.out.println("Equal");
		} catch (Exception e) {
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
						if (ca1.getValueAtPosition(x, y) != ca2.getValueAtPosition(x, y)) {
							equal = false;
							System.out.println("Different value at step " + ca1.getStep() + " (" + x + ", " + y + "): " 
									+ ca2.getClass().getSimpleName() + ":" + ca2.getValueAtPosition(x, y) 
									+ " != " + ca1.getClass().getSimpleName() + ":" + ca1.getValueAtPosition(x, y));
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
				System.out.println("Equal");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void compare(SymmetricLongCellularAutomaton2D ca1, LongCellularAutomaton2D ca2) {
		try {
			System.out.println("Comparing...");
			boolean finished1 = false;
			boolean finished2 = false;
			boolean equal = true;
			while (!finished1 && !finished2) {
				for (int y = ca1.getMinY(); y <= ca1.getMaxY(); y++) {
					for (int x = ca2.getMinX(); x <= ca2.getMaxX(); x++) {
						if (ca1.getValueAtPosition(x, y) != ca2.getValueAtPosition(x, y)) {
							equal = false;
							System.out.println("Different value at step " + ca1.getStep() + " (" + x + ", " + y + "): " 
									+ ca2.getClass().getSimpleName() + ":" + ca2.getValueAtPosition(x, y) 
									+ " != " + ca1.getClass().getSimpleName() + ":" + ca1.getValueAtPosition(x, y));
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
						if (Math.abs(ca1.getValueAtPosition(x, y)) != Math.abs(ca2.getValueAtPosition(x, y))) {
							equal = false;
							System.out.println("Different absolute value at step " + ca1.getStep() + " (" + x + ", " + y + "): " 
									+ ca2.getClass().getSimpleName() + ":" + ca2.getValueAtPosition(x, y) 
									+ " != " + ca1.getClass().getSimpleName() + ":" + ca1.getValueAtPosition(x, y));
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
							if (ca1.getValueAtPosition(x, y, z) != ca2.getValueAtPosition(x, y, z)) {
								equal = false;
								System.out.println("Different value at step " + ca1.getStep() + " (" + x + ", " + y + ", " + z + "): " 
										+ ca2.getClass().getSimpleName() + ":" + ca2.getValueAtPosition(x, y, z) 
										+ " != " + ca1.getClass().getSimpleName() + ":" + ca1.getValueAtPosition(x, y, z));
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
				System.out.println("Equal");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void compare(SymmetricIntCellularAutomaton3D ca1, SymmetricLongCellularAutomaton3D ca2) {
		try {
			System.out.println("Comparing...");
			boolean finished1 = false;
			boolean finished2 = false;
			boolean equal = true;
			while (!finished1 && !finished2) {
				for (int z = ca1.getMinZ(); z <= ca1.getMaxZ(); z++) {
					for (int y = ca1.getMinY(); y <= ca1.getMaxY(); y++) {
						for (int x = ca2.getMinX(); x <= ca2.getMaxX(); x++) {
							if (ca1.getValueAtPosition(x, y, z) != ca2.getValueAtPosition(x, y, z)) {
								equal = false;
								System.out.println("Different value at step " + ca1.getStep() + " (" + x + ", " + y + ", " + z + "): " 
										+ ca2.getClass().getSimpleName() + ":" + ca2.getValueAtPosition(x, y, z) 
										+ " != " + ca1.getClass().getSimpleName() + ":" + ca1.getValueAtPosition(x, y, z));
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
				System.out.println("Equal");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void compare(SymmetricIntCellularAutomaton3D ca1, SymmetricIntCellularAutomaton3D ca2) {
		try {
			System.out.println("Comparing...");
			boolean finished1 = false;
			boolean finished2 = false;
			boolean equal = true;
			while (!finished1 && !finished2) {
				for (int z = ca1.getMinZ(); z <= ca1.getMaxZ(); z++) {
					for (int y = ca1.getMinY(); y <= ca1.getMaxY(); y++) {
						for (int x = ca2.getMinX(); x <= ca2.getMaxX(); x++) {
							if (ca1.getValueAtPosition(x, y, z) != ca2.getValueAtPosition(x, y, z)) {
								equal = false;
								System.out.println("Different value at step " + ca1.getStep() + " (" + x + ", " + y + ", " + z + "): " 
										+ ca2.getClass().getSimpleName() + ":" + ca2.getValueAtPosition(x, y, z) 
										+ " != " + ca1.getClass().getSimpleName() + ":" + ca1.getValueAtPosition(x, y, z));
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
//				System.out.println("Step " + ca1.getStep());
				for (int x = ca1.getMinX(); x <= ca1.getMaxX(); x++) {
					for (int z = ca1.getMinZAtX(x); z <= ca1.getMaxZAtX(x); z++) {
						for (int y = ca1.getMinY(x, z); y <= ca1.getMaxY(x, z); y++) {
							if (ca1.getValueAtPosition(x, y, z) != ca2.getValueAtPosition(x, y, z)) {
								equal = false;
								System.out.println("Different value at step " + ca1.getStep() + " (" + x + ", " + y + ", " + z + "): "
										+ ca2.getClass().getSimpleName() + ":" + ca2.getValueAtPosition(x, y, z) 
										+ " != " + ca1.getClass().getSimpleName() + ":" + ca1.getValueAtPosition(x, y, z));
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
				System.out.println("Equal");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void compare(SymmetricIntActionableCellularAutomaton3D ca1, SymmetricLongCellularAutomaton3D ca2) {
		try {
			System.out.println("Comparing...");
			boolean finished1 = false;
			boolean finished2 = false;
			SymmetricGridProcessor<IntGrid3D> comparator = new SymmetricGridProcessor<IntGrid3D>() {
				
				@Override
				public void processGridBlock(IntGrid3D gridBlock) throws Exception {
					for (int x = gridBlock.getMinX(); x <= gridBlock.getMaxX(); x++) {
						for (int z = gridBlock.getMinZAtX(x); z <= gridBlock.getMaxZAtX(x); z++) {
							for (int y = gridBlock.getMinY(x, z); y <= gridBlock.getMaxY(x, z); y++) {
								if (gridBlock.getValueAtPosition(x, y, z) != ca2.getValueAtPosition(x, y, z)) {
									System.out.println("Different value at step " + ca1.getStep() + " (" + x + ", " + y + ", " + z + "): " 
											+ ca2.getClass().getSimpleName() + ":" + ca2.getValueAtPosition(x, y, z) 
											+ " != " + ca1.getClass().getSimpleName() + ":" + ca1.getValueAtPosition(x, y, z));
								}	
							}	
						}
					}						
				}
				
				@Override
				public void beforeProcessing() throws Exception {
					// do nothing					
				}
				
				@Override
				public void afterProcessing() throws Exception {
					// do nothing					
				}
			};
			
			while (!finished1 && !finished2) {
				System.out.println("Step " + ca1.getStep());
				ca1.addProcessor(comparator);
				ca1.processGrid();
				ca1.removeProcessor(comparator);
				finished1 = !ca1.nextStep();
				finished2 = !ca2.nextStep();
				if (finished1 != finished2) {
					String finishedCA = finished1? ca1.getClass().getSimpleName() : ca2.getClass().getSimpleName();
					System.out.println("Different final step. " + finishedCA + " finished earlier (step " + ca1.getStep() + ")");
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void compare(SymmetricLongActionableCellularAutomaton3D ca1, SymmetricLongCellularAutomaton3D ca2) {
		try {
			System.out.println("Comparing...");
			boolean finished1 = false;
			boolean finished2 = false;
			SymmetricGridProcessor<LongGrid3D> comparator = new SymmetricGridProcessor<LongGrid3D>() {
				
				@Override
				public void processGridBlock(LongGrid3D gridBlock) throws Exception {
					for (int x = gridBlock.getMinX(); x <= gridBlock.getMaxX(); x++) {
						for (int z = gridBlock.getMinZAtX(x); z <= gridBlock.getMaxZAtX(x); z++) {
							for (int y = gridBlock.getMinY(x, z); y <= gridBlock.getMaxY(x, z); y++) {
								if (gridBlock.getValueAtPosition(x, y, z) != ca2.getValueAtPosition(x, y, z)) {
									System.out.println("Different value at step " + ca1.getStep() + " (" + x + ", " + y + ", " + z + "): " 
											+ ca2.getClass().getSimpleName() + ":" + ca2.getValueAtPosition(x, y, z) 
											+ " != " + ca1.getClass().getSimpleName() + ":" + ca1.getValueAtPosition(x, y, z));
								}	
							}	
						}
					}						
				}
				
				@Override
				public void beforeProcessing() throws Exception {
					// do nothing					
				}
				
				@Override
				public void afterProcessing() throws Exception {
					// do nothing			
				}
			};
			
			while (!finished1 && !finished2) {
				System.out.println("Step " + ca1.getStep());
				ca1.addProcessor(comparator);
				ca1.processGrid();
				ca1.removeProcessor(comparator);
				finished1 = !ca1.nextStep();
				finished2 = !ca2.nextStep();
				if (finished1 != finished2) {
					String finishedCA = finished1? ca1.getClass().getSimpleName() : ca2.getClass().getSimpleName();
					System.out.println("Different final step. " + finishedCA + " finished earlier (step " + ca1.getStep() + ")");
				}
			}
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
								if (ca1.getValueAtPosition(w, x, y, z) != ca2.getValueAtPosition(w, x, y, z)) {
									equal = false;
									System.out.println("Different value at step " + ca1.getStep() + " (" + w + ", " + x + ", " + y + ", " + z + "): " 
											+ ca2.getClass().getSimpleName() + ":" + ca2.getValueAtPosition(w, x, y, z) 
											+ " != " + ca1.getClass().getSimpleName() + ":" + ca1.getValueAtPosition(w, x, y, z));
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
								if (ca1.getValueAtPosition(w, x, y, z) != ca2.getValueAtPosition(w, x, y, z)) {
									equal = false;
									System.out.println("Different value at step " + ca1.getStep() + " (" + w + ", " + x + ", " + y + ", " + z + "): " 
											+ ca2.getClass().getSimpleName() + ":" + ca2.getValueAtPosition(w, x, y, z) 
											+ " != " + ca1.getClass().getSimpleName() + ":" + ca1.getValueAtPosition(w, x, y, z));
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
				System.out.println("Equal");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void print3DEdgeXSectionAsGridSequence(SymmetricShortCellularAutomaton4D ca) {
		try {
			do {
				System.out.println("Step " + ca.getStep());
				short[] minAndMaxValue = ca.getMinAndMaxValue();
				System.out.println("Min value " + minAndMaxValue[0] + " Max value " + minAndMaxValue[1]);
				printAsGrid(ca.projected3DEdgeMaxW().crossSectionAtZ(0), (short)0);
			} while (ca.nextStep());
		} catch (Exception e) {
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
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void checkTotalValueConservation(SymmetricLongCellularAutomaton2D ca) {
		System.out.println("Checking total value conservation...");
		try {
			long value = ca.getTotalValue(), newValue = value;
			boolean finished = false;
			while (value == newValue && !finished) {
				finished = !ca.nextStep();
				newValue = ca.getTotalValue();
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
	
	public static void checkTotalValueConservation(SymmetricLongCellularAutomaton3D ca) {
		System.out.println("Checking total value conservation...");
		try {
			long value = ca.getTotalValue(), newValue = value;
			boolean finished = false;
			while (value == newValue && !finished) {
				finished = !ca.nextStep();
				newValue = ca.getTotalValue();
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
	
	public static void checkTotalValueConservation(SymmetricIntCellularAutomaton3D ca) {
		System.out.println("Checking total value conservation...");
		try {
			long value = ca.getTotalValue(), newValue = value;
			boolean finished = false;
			while (value == newValue && !finished) {
				finished = !ca.nextStep();
				newValue = ca.getTotalValue();
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
	
	public static void stepByStep(SymmetricLongCellularAutomaton2D ca) {
		try {
			Scanner s = new Scanner(System.in);
			do {
				System.out.println("step " + ca.getStep());
				printAsGrid(ca, 0);
				System.out.println("totalValue " + ca.getTotalValue());
				s.nextLine();
			} while (ca.nextStep());
			s.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void stepByStep(SymmetricLongCellularAutomaton1D ca) {
		try {
			Scanner s = new Scanner(System.in);
			do {
				System.out.println("step " + ca.getStep());
				printAsGrid(ca, 0);
				System.out.println("totalValue " + ca.getTotalValue());
				s.nextLine();
			} while (ca.nextStep());
			s.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void stepByStep(SymmetricIntCellularAutomaton3D ca) {
		try {
			Scanner s = new Scanner(System.in);
			do {
				System.out.println("step " + ca.getStep());
				printAsGrid(ca.crossSectionAtZ(0), 0);
				System.out.println("totalValue " + ca.getTotalValue());
				s.nextLine();
			} while (ca.nextStep());
			s.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void stepByStep3DEdgeSurface(SymmetricIntCellularAutomaton4D ca) {
		try {
			Scanner s = new Scanner(System.in);
			do {
				System.out.println("step " + ca.getStep());
				printAsGrid(ca.projected3DEdgeMaxW().projectedSurfaceMaxX(), 0);
				System.out.println("totalValue " + ca.getTotalValue());
				s.nextLine();
			} while (ca.nextStep());
			s.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void stepByStep(SymmetricLongCellularAutomaton3D ca) {
		stepByStep(new SymmetricLongCellularAutomaton3D[] {ca});
	}
	
	public static void stepByStep(SymmetricLongCellularAutomaton3D[] cas) {
		try {
			Scanner s = new Scanner(System.in);
			boolean anyChanged = false;
			do {
				for (SymmetricLongCellularAutomaton3D ca : cas) {
					System.out.println("step " + ca.getStep());
					printAsGrid(ca.crossSectionAtZ(0), 0);
					System.out.println("totalValue " + ca.getTotalValue());
					boolean caChanged = ca.nextStep();
					anyChanged = anyChanged || caChanged;
				}
				s.nextLine();
			} while (anyChanged);
			s.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void stepByStep(SymmetricLongCellularAutomaton4D ca) {
		try {
			Scanner s = new Scanner(System.in);
			do {
				System.out.println("step " + ca.getStep());
				printAsGrid(ca.crossSectionAtYZ(0,0), 0);
				System.out.println("totalValue " + ca.getTotalValue());
				s.nextLine();
			} while (ca.nextStep());
			s.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void stepByStep(LongCellularAutomaton2D ca) {
		try {
			Scanner s = new Scanner(System.in);
			do {
				System.out.println("step " + ca.getStep());
				printAsGrid(ca, 0);
				System.out.println("totalValue " + ca.getTotalValue());
				s.nextLine();
			} while (ca.nextStep());
			s.close();
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
	
	public static void printAsGrid(LongGrid2D m, long backgroundValue) throws Exception {
		int maxX = m.getMaxX(), maxY = m.getMaxY(), minX = m.getMinX(), minY = m.getMinY();
		printAsGrid(m, minX, maxX, minY, maxY, backgroundValue);
	}
	
	public static void printAsGrid(LongGrid1D m, long backgroundValue) {
		int maxX = m.getMaxX(), minX = m.getMinX();
		printAsGrid(m, minX, maxX, backgroundValue);
	}
	
	public static void printAsGrid(IntGrid2D m, int backgroundValue) throws Exception {
		int maxX = m.getMaxX(), maxY = m.getMaxY(), minX = m.getMinX(), minY = m.getMinY();
		printAsGrid(m, minX, maxX, minY, maxY, backgroundValue);
	}
	
	public static void printAsGrid(ShortGrid2D m, short backgroundValue) throws Exception {
		int maxX = m.getMaxX(), maxY = m.getMaxY(), minX = m.getMinX(), minY = m.getMinY();
		printAsGrid(m, minX, maxX, minY, maxY, backgroundValue);
	}
	
	public static void printAsGrid(LongGrid2D m, int minX, int maxX, int minY, int maxY, long backgroundValue) throws Exception {
		int maxDigits = 3;
		for (int y = maxY; y >= minY; y--) {
			for (int x = minX; x <= maxX; x++) {
				int digits = Long.toString(m.getValueAtPosition(x, y)).length();
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
				long val = m.getValueAtPosition(x, y);
				if (val != backgroundValue) {
					strVal = val + "";
				}
				System.out.print("|" + padLeft(strVal, ' ', maxDigits));
			}
			System.out.println("|");
		}
		System.out.println(headFoot);
	}
	
	public static void printAsGrid(LongGrid1D m, int minX, int maxX, long backgroundValue) {
		int maxDigits = 3;
		for (int x = minX; x <= maxX; x++) {
			int digits = Long.toString(m.getValueAtPosition(x)).length();
			if (digits > maxDigits)
				maxDigits = digits;
		}
		String headFootGap = "";
		for (int i = 0; i < maxDigits; i++) {
			headFootGap += "-";
		}
		String headFoot = "+";
		for (int i = minX; i <= maxX; i++) {
			headFoot += headFootGap + "+";
		}
		System.out.println(headFoot);
		for (int x = minX; x <= maxX; x++) {
			String strVal = " ";
			long val = m.getValueAtPosition(x);
			if (val != backgroundValue) {
				strVal = val + "";
			}
			System.out.print("|" + padLeft(strVal, ' ', maxDigits));
		}
		System.out.println("|");
		System.out.println(headFoot);
	}
	
	public static void printAsGrid(IntGrid2D m, int minX, int maxX, int minY, int maxY, int backgroundValue) throws Exception {
		int maxDigits = 3;
		for (int y = maxY; y >= minY; y--) {
			for (int x = minX; x <= maxX; x++) {
				int digits = Long.toString(m.getValueAtPosition(x, y)).length();
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
				int val = m.getValueAtPosition(x, y);
				if (val != backgroundValue) {
					strVal = val + "";
				}
				System.out.print("|" + padLeft(strVal, ' ', maxDigits));
			}
			System.out.println("|");
		}
		System.out.println(headFoot);
	}
	
	public static void printAsGrid(ShortGrid2D m, int minX, int maxX, int minY, int maxY, short backgroundValue) throws Exception {
		int maxDigits = 3;
		for (int y = maxY; y >= minY; y--) {
			for (int x = minX; x <= maxX; x++) {
				int digits = Long.toString(m.getValueAtPosition(x, y)).length();
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
				long val = m.getValueAtPosition(x, y);
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

}
