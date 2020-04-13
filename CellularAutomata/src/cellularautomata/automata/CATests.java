/* Aether2DImgMaker -- console app to generate images of the Aether cellular automaton in 2D
    Copyright (C) 2017-2020 Jaume Ribas

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

import cellularautomata.evolvinggrid.EvolvingIntGrid;
import cellularautomata.evolvinggrid.EvolvingIntGrid2D;
import cellularautomata.evolvinggrid.EvolvingIntGrid3D;
import cellularautomata.evolvinggrid.EvolvingIntGrid4D;
import cellularautomata.evolvinggrid.EvolvingLongGrid;
import cellularautomata.evolvinggrid.EvolvingLongGrid1D;
import cellularautomata.evolvinggrid.EvolvingLongGrid2D;
import cellularautomata.evolvinggrid.EvolvingLongGrid3D;
import cellularautomata.evolvinggrid.EvolvingLongGrid4D;
import cellularautomata.evolvinggrid.EvolvingModel;
import cellularautomata.evolvinggrid.EvolvingShortGrid;
import cellularautomata.evolvinggrid.EvolvingShortGrid3D;
import cellularautomata.evolvinggrid.SymmetricActionableEvolvingIntGrid3D;
import cellularautomata.evolvinggrid.SymmetricActionableEvolvingLongGrid3D;
import cellularautomata.evolvinggrid.SymmetricEvolvingShortGrid4D;
import cellularautomata.grid.SymmetricGridProcessor;
import cellularautomata.grid1d.LongGrid1D;
import cellularautomata.grid2d.IntGrid2D;
import cellularautomata.grid2d.LongGrid2D;
import cellularautomata.grid2d.ShortGrid2D;
import cellularautomata.grid3d.IntGrid3D;
import cellularautomata.grid3d.LongGrid3D;
import cellularautomata.grid4d.ShortGrid4D;

public class CATests {
	
	public static void main(String[] args) {
		long initialValue = 1000000;
		Aether2D ae1 = new Aether2D(initialValue);
		AetherSimple2D ae2 = new AetherSimple2D(initialValue);
		compare(ae1, ae2);
	}
	
	public static void testAsymmetricSection() {
		ShortAether4D ae = new ShortAether4D((short) -10000);
		ShortGrid4D ns = ae.asymmetricSection();
		try {
			do {
				System.out.println("Comparing step " + ae.getStep());
				int maxW = ae.getAsymmetricMaxW();
				for (int w = 0; w <= maxW; w++) {
					for (int x = 0; x <= w; x++) {
						for (int y = 0; y <= x; y++) {
							for (int z = 0; z <= y; z++) {
//								System.out.println("Comparing value at (" + w + ", " + x + ", " + y + ", " + z + ")");
								if (ae.getValueAtAsymmetricPosition(w, x, y, z) != ns.getValueAtPosition(w, x, y, z)) {
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
		IntGrid3D cs = ae.asymmetricSection().crossSectionAtZ(z);
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
							if (ae.getValueAtAsymmetricPosition(w, x, y, z) != cs.getValueAtPosition(w, x, y)) {
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
		compare(ae1, ae2);
	}
	
	public static void testAetherRandomConfig() {
		Aether3DRandomConfiguration ae1 = new Aether3DRandomConfiguration(3, -10, 10);
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
	
	public static void raceSandpileImplementations() {
		long initialValue = 2000;
		SpreadIntegerValue1D siv1 = new SpreadIntegerValue1D(initialValue, 0);
		SingleSourceLongSandpile1D siv2 = new SingleSourceLongSandpile1D(new SpreadIntegerValueRules(), initialValue);
		race(new EvolvingModel[] { siv1, siv2 });
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
		IntAether3DSwap ca;
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
		IntAether3DSwap ca;
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
	
	public static void compare(EvolvingLongGrid1D ca1, EvolvingLongGrid1D ca2) {
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
								+ ca1.getClass().getSimpleName() + ":" + ca1.getValueAtPosition(x) 
								+ " != " + ca2.getClass().getSimpleName() + ":" + ca2.getValueAtPosition(x));
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
	
	public static void compare(EvolvingIntGrid2D ca1, EvolvingLongGrid2D ca2) {
		try {
			System.out.println("Comparing...");
			boolean finished1 = false;
			boolean finished2 = false;
			boolean equal = true;
			while (!finished1 && !finished2) {
				for (int y = ca1.getMinY(); y <= ca1.getMaxY(); y++) {
					for (int x = ca2.getMinX(y); x <= ca2.getMaxX(y); x++) {
						if (ca1.getValueAtPosition(x, y) != ca2.getValueAtPosition(x, y)) {
							equal = false;
							System.out.println("Different value at step " + ca1.getStep() + " (" + x + ", " + y + "): " 
									+ ca1.getClass().getSimpleName() + ":" + ca1.getValueAtPosition(x, y) 
									+ " != " + ca2.getClass().getSimpleName() + ":" + ca2.getValueAtPosition(x, y));
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
	
	public static void compare(EvolvingIntGrid2D ca1, EvolvingIntGrid2D ca2) {
		try {
			System.out.println("Comparing...");
			boolean finished1 = false;
			boolean finished2 = false;
			boolean equal = true;
			while (!finished1 && !finished2) {
				for (int y = ca1.getMinY(); y <= ca1.getMaxY(); y++) {
					for (int x = ca2.getMinX(y); x <= ca2.getMaxX(y); x++) {
						if (ca1.getValueAtPosition(x, y) != ca2.getValueAtPosition(x, y)) {
							equal = false;
							System.out.println("Different value at step " + ca1.getStep() + " (" + x + ", " + y + "): " 
									+ ca1.getClass().getSimpleName() + ":" + ca1.getValueAtPosition(x, y) 
									+ " != " + ca2.getClass().getSimpleName() + ":" + ca2.getValueAtPosition(x, y));
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
	
	public static void compare(EvolvingLongGrid2D ca1, EvolvingLongGrid2D ca2) {
		try {
			System.out.println("Comparing...");
			boolean finished1 = false;
			boolean finished2 = false;
			boolean equal = true;
			while (!finished1 && !finished2) {
				System.out.println("Comparing step " + ca1.getStep());
				int maxY = ca1.getMaxY();
				for (int y = ca1.getMinY(); y <= maxY; y++) {
					for (int x = ca2.getMinX(y); x <= ca2.getMaxX(y); x++) {
						if (ca1.getValueAtPosition(x, y) != ca2.getValueAtPosition(x, y)) {
							equal = false;
							System.out.println("Different value at step " + ca1.getStep() + " (" + x + ", " + y + "): " 
									+ ca1.getClass().getSimpleName() + ":" + ca1.getValueAtPosition(x, y) 
									+ " != " + ca2.getClass().getSimpleName() + ":" + ca2.getValueAtPosition(x, y));
							return;
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
	
	public static void compare(EvolvingIntGrid3D ca1, EvolvingIntGrid3D ca2) {
		try {
			System.out.println("Comparing...");
			boolean finished1 = false;
			boolean finished2 = false;
			boolean equal = true;
			while (!finished1 && !finished2) {
				for (int z = ca1.getMinZ(); z <= ca1.getMaxZ(); z++) {
					for (int y = ca1.getMinYAtZ(z); y <= ca1.getMaxYAtZ(z); y++) {
						for (int x = ca2.getMinX(y,z); x <= ca2.getMaxX(y,z); x++) {
							if (ca1.getValueAtPosition(x, y, z) != ca2.getValueAtPosition(x, y, z)) {
								equal = false;
								System.out.println("Different value at step " + ca1.getStep() + " (" + x + ", " + y + ", " + z + "): " 
										+ ca1.getClass().getSimpleName() + ":" + ca1.getValueAtPosition(x, y, z) 
										+ " != " + ca2.getClass().getSimpleName() + ":" + ca2.getValueAtPosition(x, y, z));
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
	
	public static void compare(SymmetricActionableEvolvingIntGrid3D ca1, EvolvingLongGrid3D ca2) {
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
											+ ca1.getClass().getSimpleName() + ":" + gridBlock.getValueAtPosition(x, y, z) 
											+ " != " + ca2.getClass().getSimpleName() + ":" + ca2.getValueAtPosition(x, y, z));
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
	
	public static void compare(SymmetricActionableEvolvingLongGrid3D ca1, EvolvingLongGrid3D ca2) {
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
											+ ca1.getClass().getSimpleName() + ":" + ca1.getValueAtPosition(x, y, z) 
											+ " != " + ca2.getClass().getSimpleName() + ":" + ca2.getValueAtPosition(x, y, z));
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
	
	public static void compare(EvolvingLongGrid4D ca1, EvolvingLongGrid4D ca2) {
		try {
			System.out.println("Comparing...");
			boolean finished1 = false;
			boolean finished2 = false;
			boolean equal = true;
			while (!finished1 && !finished2) {
				for (int z = ca1.getMinZ(); z <= ca1.getMaxZ(); z++) {
					for (int y = ca1.getMinYAtZ(z); y <= ca1.getMaxYAtZ(z); y++) {
						for (int x = ca2.getMinXAtYZ(y,z); x <= ca2.getMaxXAtYZ(y,z); x++) {
							for (int w = ca2.getMinW(x,y,z); w <= ca2.getMaxW(x,y,z); w++) {
								if (ca1.getValueAtPosition(w, x, y, z) != ca2.getValueAtPosition(w, x, y, z)) {
									equal = false;
									System.out.println("Different value at step " + ca1.getStep() + " (" + w + ", " + x + ", " + y + ", " + z + "): " 
											+ ca1.getClass().getSimpleName() + ":" + ca1.getValueAtPosition(w, x, y, z) 
											+ " != " + ca2.getClass().getSimpleName() + ":" + ca2.getValueAtPosition(w, x, y, z));
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
				System.out.println("Equal");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void compare(SymmetricEvolvingShortGrid4D ca1, SymmetricEvolvingShortGrid4D ca2) {
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
								if (ca1.getValueAtPosition(w, x, y, z) != ca2.getValueAtPosition(w, x, y, z)) {
									equal = false;
									System.out.println("Different value at step " + ca1.getStep() + " (" + w + ", " + x + ", " + y + ", " + z + "): " 
											+ ca1.getClass().getSimpleName() + ":" + ca1.getValueAtPosition(w, x, y, z) 
											+ " != " + ca2.getClass().getSimpleName() + ":" + ca2.getValueAtPosition(w, x, y, z));
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
				System.out.println("Equal");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void compare(EvolvingLongGrid4D ca1, EvolvingIntGrid4D ca2) {
		compare(ca2, ca1);
	}
	
	public static void compare(EvolvingIntGrid4D ca1, EvolvingLongGrid4D ca2) {
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
								if (ca1.getValueAtPosition(w, x, y, z) != ca2.getValueAtPosition(w, x, y, z)) {
									equal = false;
									System.out.println("Different value at step " + ca1.getStep() + " (" + w + ", " + x + ", " + y + ", " + z + "): " 
											+ ca1.getClass().getSimpleName() + ":" + ca1.getValueAtPosition(w, x, y, z) 
											+ " != " + ca2.getClass().getSimpleName() + ":" + ca2.getValueAtPosition(w, x, y, z));
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
				System.out.println("Equal");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void compare(EvolvingShortGrid3D ca1,  EvolvingIntGrid3D ca2) {
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
							if (ca1.getValueAtPosition(x, y, z) != ca2.getValueAtPosition(x, y, z)) {
								equal = false;
								System.out.println("Different value at step " + ca1.getStep() + " (" + x + ", " + y + ", " + z + "): " 
										+ ca2.getClass().getSimpleName() + ":" + ca2.getValueAtPosition(x, y, z) 
										+ " != " + ca1.getClass().getSimpleName() + ":" + ca1.getValueAtPosition(x, y, z));
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
				System.out.println("Equal");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void compare(EvolvingLongGrid3D ca1, EvolvingIntGrid3D ca2) {
		compare(ca2, ca1);
	}
	
	public static void compare(EvolvingIntGrid3D ca1, EvolvingLongGrid3D ca2) {
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
							if (ca1.getValueAtPosition(x, y, z) != ca2.getValueAtPosition(x, y, z)) {
								equal = false;
								System.out.println("Different value at step " + ca1.getStep() + " (" + x + ", " + y + ", " + z + "): " 
										+ ca2.getClass().getSimpleName() + ":" + ca2.getValueAtPosition(x, y, z) 
										+ " != " + ca1.getClass().getSimpleName() + ":" + ca1.getValueAtPosition(x, y, z));
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
				System.out.println("Equal");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void compare(EvolvingShortGrid3D ca1, EvolvingLongGrid3D ca2) {
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
							if (ca1.getValueAtPosition(x, y, z) != ca2.getValueAtPosition(x, y, z)) {
								equal = false;
								System.out.println("Different value at step " + ca1.getStep() + " (" + x + ", " + y + ", " + z + "): " 
										+ ca2.getClass().getSimpleName() + ":" + ca2.getValueAtPosition(x, y, z) 
										+ " != " + ca1.getClass().getSimpleName() + ":" + ca1.getValueAtPosition(x, y, z));
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
				System.out.println("Equal");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void compare(EvolvingLongGrid3D ca1, EvolvingLongGrid3D ca2) {
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
							if (ca1.getValueAtPosition(x, y, z) != ca2.getValueAtPosition(x, y, z)) {
								equal = false;
								System.out.println("Different value at step " + ca1.getStep() + " (" + x + ", " + y + ", " + z + "): " 
										+ ca2.getClass().getSimpleName() + ":" + ca2.getValueAtPosition(x, y, z) 
										+ " != " + ca1.getClass().getSimpleName() + ":" + ca1.getValueAtPosition(x, y, z));
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
				System.out.println("Equal");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void printMinAndMaxValues(EvolvingShortGrid ca) {
		try {
			do {
				short[] minAndMax = ca.getMinAndMaxValue();
				System.out.println("min: " + minAndMax[0] + "\t\tmax: " + minAndMax[1]);
			} while(ca.nextStep());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void printMinAndMaxValues(EvolvingLongGrid ca) {
		try {
			do {
				long[] minAndMax = ca.getMinAndMaxValue();
				System.out.println("min: " + minAndMax[0] + "\t\tmax: " + minAndMax[1]);
			} while(ca.nextStep());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void checkTotalValueConservation(EvolvingLongGrid ca) {
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
	
	public static void checkTotalValueConservation(EvolvingIntGrid ca) {
		System.out.println("Checking total value conservation...");
		try {
			int value = ca.getTotalValue(), newValue = value;
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
	
	public static void stepByStep(EvolvingIntGrid2D ca) {
		try {
			Scanner s = new Scanner(System.in);
			do {
				System.out.println("step " + ca.getStep());
				printAsGrid(ca, 0);
				System.out.println("total value " + ca.getTotalValue());
				s.nextLine();
			} while (ca.nextStep());
			s.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void stepByStep(EvolvingLongGrid1D ca) {
		try {
			Scanner s = new Scanner(System.in);
			do {
				System.out.println("step " + ca.getStep());
				printAsGrid(ca, 0);
				System.out.println("total value " + ca.getTotalValue());
				s.nextLine();
			} while (ca.nextStep());
			s.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void stepByStep(EvolvingLongGrid2D ca) {
		try {
			Scanner s = new Scanner(System.in);
			do {
				System.out.println("step " + ca.getStep());
				printAsGrid(ca, 0);
				System.out.println("total value " + ca.getTotalValue());
				s.nextLine();
			} while (ca.nextStep());
			s.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}	
	
	public static void race(EvolvingModel[] cas) {
		try {
			long millis;
			for (EvolvingModel ca : cas) {
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
