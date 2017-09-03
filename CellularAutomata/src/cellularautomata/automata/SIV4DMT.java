package cellularautomata.automata;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class SIV4DMT extends SymmetricLongCellularAutomaton4D {

	/** A 4D array representing the grid */
	private long[][][][] grid;
	
	private long initialValue;
	private long currentStep;
	
	private int maxX;
	private int maxY;
	private int maxZ;

	/** Whether or not the values reached the bounds of the array */
	private boolean wBoundReached;
	
	private final int threadCount;
	
	private ExecutorService executor;
	
	/**
	 * Creates an instance with the given initial value
	 * 
	 * @param initialValue the value at the origin at step 0
	 */
	public SIV4DMT(long initialValue, int threadCount) {
		this.threadCount = threadCount;
		this.initialValue = initialValue;
		grid = new long[2][][][];
		grid[0] = buildGridBlock(0);
		grid[1] = buildGridBlock(1);
		grid[0][0][0][0] = this.initialValue;
		maxX = 0;
		maxY = 0;
		maxZ = 0;
		wBoundReached = false;
		currentStep = 0;
		executor = Executors.newCachedThreadPool();
	}
	
	/**
	 * Computes the next step of the algorithm and returns whether
	 * or not the state of the grid changed. 
	 *  
	 * @return true if the state of the grid changed or false otherwise
	 * @throws ExecutionException 
	 * @throws InterruptedException 
	 */
	public boolean nextStep() throws InterruptedException, ExecutionException{
		long[][][][] newGrid;
		if (wBoundReached) {
			wBoundReached = false;
			newGrid = new long[grid.length + 1][][][];
		} else {
			newGrid = new long[grid.length][][][];
		}
		List<Callable<ComputingResult>> calls = new ArrayList<Callable<ComputingResult>>();
		boolean split = false;
		int[][] sectionBounds = new int[threadCount][2];
		if (threadCount > 1) {
			int maxW = grid.length - 1;
			long sqrMaxW = maxW*maxW;
			double totalVolume = (double)(maxW*sqrMaxW)/6; //TODO: use 4D formula
			//System.out.println("totalVolume " + totalVolume);
			double sectionVolume = totalVolume/threadCount;
			sectionBounds[0][0] = 0;
			sectionBounds[0][1] = (int) ((6*sectionVolume)/sqrMaxW);
			//System.out.println("section (" + sectionBounds[0][0] + "-" + sectionBounds[0][1] + ")");
			boolean rangesBiggerThanTwo = sectionBounds[0][1] - sectionBounds[0][0] + 1 > 2;
			int threadCountMinusOne = threadCount - 1;
			for (int i = 1; i < threadCountMinusOne; i++) {
				sectionBounds[i][0] = sectionBounds[i - 1][1] + 1;
				sectionBounds[i][1] = (int) (6*(sectionVolume + (double)(sectionBounds[i][0]*sqrMaxW)/6)/sqrMaxW);
				//System.out.println("section (" + sectionBounds[i][0] + "-" + sectionBounds[i][1] + ")");
				rangesBiggerThanTwo = rangesBiggerThanTwo && sectionBounds[i][1] - sectionBounds[i][0] + 1 > 2;
			}
			sectionBounds[threadCountMinusOne][0] = sectionBounds[threadCountMinusOne - 1][1] + 1;
			sectionBounds[threadCountMinusOne][1] = grid.length - 1;
			rangesBiggerThanTwo = rangesBiggerThanTwo && sectionBounds[threadCountMinusOne][1] - sectionBounds[threadCountMinusOne][0] + 1 > 2;
			//System.out.println("section (" + sectionBounds[threadCountMinusOne][0] + "-" + sectionBounds[threadCountMinusOne][1] + ")");
			split = rangesBiggerThanTwo;
		}	
		if (split) {
			for (int i = 0; i < threadCount; i++) {
				newGrid[sectionBounds[i][0]] = buildGridBlock(sectionBounds[i][0]);
				newGrid[sectionBounds[i][1]] = buildGridBlock(sectionBounds[i][1]);
				calls.add(new SIV4DComputer(grid, newGrid, sectionBounds[i][0], sectionBounds[i][1]));
			}
		} else {
			newGrid[0] = buildGridBlock(0);
			if (grid.length > 1) {
				newGrid[grid.length - 1] = buildGridBlock(grid.length - 1);
			}
			calls.add(new SIV4DComputer(grid, newGrid, 0, grid.length - 1));
		}
		List<Future<ComputingResult>> futureResults = executor.invokeAll(calls);
		boolean changed = false;
		for (Future<ComputingResult> futureResult : futureResults) {
			ComputingResult result = futureResult.get();
			maxX = Math.max(maxX, result.getMaxX());
			maxY = Math.max(maxY, result.getMaxY());
			maxZ = Math.max(maxZ, result.getMaxZ());
			changed = changed || result.isChanged();
			wBoundReached = wBoundReached || result.isWBoundReached();
		}
		grid = newGrid;
		currentStep++;
		return changed;
	}
	
	private long[][][] buildGridBlock(int w) {
		long[][][] newGridBlock = new long[w + 1][][];
		for (int x = 0; x < newGridBlock.length; x++) {
			newGridBlock[x] = new long[x + 1][];
			for (int y = 0; y < newGridBlock[x].length; y++) {
				newGridBlock[x][y] = new long[y + 1];
			}
		}
		return newGridBlock;
	}
	
	public long getValueAt(int w, int x, int y, int z){	
		if (x < 0) x = -x;
		if (y < 0) y = -y;
		if (z < 0) z = -z;
		if (w < 0) w = -w;
		//sort coordinates
		boolean sorted;
		do {
			sorted = true;
			if (z > y) {
				sorted = false;
				int swp = z;
				z = y;
				y = swp;
			}
			if (y > x) {
				sorted = false;
				int swp = y;
				y = x;
				x = swp;
			}
			if (x > w) {
				sorted = false;
				int swp = x;
				x = w;
				w = swp;
			}
		} while (!sorted);
		if (w < grid.length 
				&& x < grid[w].length 
				&& y < grid[w][x].length 
				&& z < grid[w][x][y].length) {
			return grid[w][x][y][z];
		} else {
			return 0;
		}
	}
	
	public long getNonSymmetricValueAt(int w, int x, int y, int z){	
		if (w < grid.length 
				&& x < grid[w].length 
				&& y < grid[w][x].length 
				&& z < grid[w][x][y].length) {
			return grid[w][x][y][z];
		} else {
			return 0;
		}
	}
	
	@Override
	public int getMinW() {
		return -getNonSymmetricMaxW();
	}

	@Override
	public int getMaxW() {
		return getNonSymmetricMaxW();
	}

	@Override
	public int getMinX() {
		return -getNonSymmetricMaxW();
	}

	@Override
	public int getMaxX() {
		return getNonSymmetricMaxW();
	}

	@Override
	public int getMinY() {
		return -getNonSymmetricMaxW();
	}

	@Override
	public int getMaxY() {
		return getNonSymmetricMaxW();
	}

	@Override
	public int getMinZ() {
		return -getNonSymmetricMaxW();
	}

	@Override
	public int getMaxZ() {
		return getNonSymmetricMaxW();
	}
	
	@Override
	public int getNonSymmetricMinW() {
		return 0;
	}
	
	@Override
	public int getNonSymmetricMaxW() {
		return grid.length - 1;
	}
	
	@Override
	public int getNonSymmetricMinX() {
		return 0;
	}

	@Override
	public int getNonSymmetricMaxX() {
		return maxX;
	}
	
	@Override
	public int getNonSymmetricMinY() {
		return 0;
	}
	
	@Override
	public int getNonSymmetricMaxY() {
		return maxY;
	}
	
	@Override
	public int getNonSymmetricMinZ() {
		return 0;
	}
	
	@Override
	public int getNonSymmetricMaxZ() {
		return maxZ;
	}
	
	/**
	 * Returns the current step
	 * 
	 * @return the current step
	 */
	public long getCurrentStep() {
		return currentStep;
	}
	
	/**
	 * Returns the initial value
	 * 
	 * @return the value at the origin at step 0
	 */
	public long getIntialValue() {
		return initialValue;
	}
	
	public void shutdownNow() {
		executor.shutdownNow();
	}
	
	class ComputingResult {
		private boolean changed;
		private int maxX;
		private int maxY;
		private int maxZ;
		private boolean wBoundReached;
		public ComputingResult(int maxX, int maxY, int maxZ, boolean changed, boolean wBoundReached) {
			this.changed = changed;
			this.maxX = maxX;
			this.maxY = maxY;
			this.maxZ = maxZ;
			this.wBoundReached = wBoundReached;
		}
		public boolean isChanged() {
			return changed;
		}
		public boolean isWBoundReached() {
			return wBoundReached;
		}
		public int getMaxX() {
			return maxX;
		}
		public int getMaxY() {
			return maxY;
		}
		public int getMaxZ() {
			return maxZ;
		}
	}
	
	class SIV4DComputer implements Callable<ComputingResult> {
		
		private long[][][][] grid;
		private long[][][][] newGrid;
		private int start;
		private int finish;
		private int maxX = 0;
		private int maxY = 0;
		private int maxZ = 0;
		private boolean changed;
		private int maxWMinusOne;
		private boolean wBoundReached;
		
		public SIV4DComputer(long[][][][] grid, long[][][][] newGrid, int start, int finish) {
			this.grid = grid;
			this.newGrid = newGrid;
			this.start = start;
			this.finish = finish;
			maxWMinusOne = grid.length - 2;
		}
		
		private void computeBlock(int w) {
			for (int x = 0; x <= w; x++) {
				for (int y = 0; y <= x; y++) {
					for (int z = 0; z <= y; z++) {
						long value = grid[w][x][y][z];
						if (value != 0) {
							//Divide its value by 9 (using integer division)
							long quotient = value/9;
							if (quotient != 0) {
								//I assume that if any quotient is not zero the state changes
								changed = true;
								//Add the quotient to the neighboring positions
								//w+
								newGrid[w+1][x][y][z] += quotient;
								if (w == maxWMinusOne) {
									wBoundReached = true;
								}						
								//w-
								if (w > x) {
									long valueToAdd = quotient;
									if (w == x + 1) {
										valueToAdd += quotient;
										if (x == y) {
											valueToAdd += quotient;
											if (y == z) {
												valueToAdd += quotient;
												if (w == 1) {
													valueToAdd += 4*quotient;
												}
											}
										}
									}
									newGrid[w-1][x][y][z] += valueToAdd;
								}
								//x+
								if (x < w) {
									long valueToAdd = quotient;
									if (x == w - 1) {
										valueToAdd += quotient;
									}
									int xx = x+1;
									newGrid[w][xx][y][z] += valueToAdd;
									if (xx > maxX)
										maxX = xx;
								}
								//x-
								if (x > y) {
									long valueToAdd = quotient;									
									if (y == x - 1) {
										valueToAdd += quotient;
										if (y == z) {
											valueToAdd += quotient;
											if (y == 0) {
												valueToAdd += 3*quotient;
											}
										}
									}
									newGrid[w][x-1][y][z] += valueToAdd;
								}
								//y+
								if (y < x) {
									long valueToAdd = quotient;									
									if (y == x - 1) {
										valueToAdd += quotient;
										if (w == x) {
											valueToAdd += quotient;
										}
									}
									int yy = y+1;
									newGrid[w][x][yy][z] += valueToAdd;
									if (yy > maxY)
										maxY = yy;
								}
								//y-
								if (y > z) {	
									long valueToAdd = quotient;
									if (z == y - 1) {
										valueToAdd += quotient;
										if (y == 1) {
											valueToAdd += 2*quotient;
										}
									}
									newGrid[w][x][y-1][z] += valueToAdd;
								}
								//z+
								if (z < y) {
									long valueToAdd = quotient;
									if (z == y - 1) {
										valueToAdd += quotient;
										if (x == y) {
											valueToAdd += quotient;
											if (w == x) {
												valueToAdd += quotient;
											}
										}
									}
									int zz = z+1;
									newGrid[w][x][y][zz] += valueToAdd;
									if (zz > maxZ)
										maxZ = zz;
								}
								//z-
								if (z > 0) {
									long valueToAdd = quotient;
									if (z == 1) {
										valueToAdd += quotient;
									}
									newGrid[w][x][y][z-1] += valueToAdd;
								}								
							}
							newGrid[w][x][y][z] += value - 8*quotient;
						}
					}
					grid[w][x][y] = null;
				}
				grid[w][x] = null;
			}
		}
		
		@Override
		public ComputingResult call() {
			if (finish - start + 1 > 1) {
				int w = start;
				int nextW = w + 1;
				if (nextW < finish) {
					newGrid[nextW] = buildGridBlock(nextW);
				}
				boolean isBeginning = start == 0;
				if (!isBeginning) {
					synchronized (newGrid[w - 1]) {
						computeBlock(w);
					}
				} else {
					computeBlock(w);
				}
				grid[w] = null;
				w++;
				if (w < finish && !isBeginning) {
					nextW = w + 1;
					if (nextW < finish) {
						newGrid[nextW] = buildGridBlock(nextW);
					}
					synchronized (newGrid[w - 2]) {
						computeBlock(w);
					}
					grid[w] = null;
					w++;
				}
				for (; w < finish; w++) {
					nextW = w + 1;
					if (nextW < finish) {
						newGrid[nextW] = buildGridBlock(nextW);
						computeBlock(w);
					} else {
						synchronized (newGrid[finish]) {
							computeBlock(w);
						}
					}					
					grid[w] = null;
				}
				if (finish < grid.length - 1) {
					synchronized (newGrid[finish]) {
						computeBlock(finish);
					}
				} else {
					if (newGrid.length > grid.length) {
						newGrid[finish + 1] = buildGridBlock(finish + 1);
					}
					computeBlock(w);
				}
				grid[finish] = null;
			} else {
				int x = start;
				if (x == grid.length - 1 && newGrid.length > grid.length) {
					newGrid[x + 1] = buildGridBlock(x + 1);
				}
				computeBlock(x);
				grid[x] = null;
			}
			return new ComputingResult(maxX, maxY, maxZ, changed, wBoundReached);
		}
		
	}

	@Override
	public CustomSymmetricLongCA4DData getData() {
		return new CustomSymmetricLongCA4DData(grid, initialValue, 0, currentStep, wBoundReached, maxX, maxY, maxZ);
	}

	@Override
	public String getName() {
		return "SpreadIntegerValue4D";
	}

	@Override
	public String getSubFolderPath() {
		return getName() + "/" + initialValue;
	}

	@Override
	public long getBackgroundValue() {
		// TODO Auto-generated method stub
		return 0;
	}
	
}
