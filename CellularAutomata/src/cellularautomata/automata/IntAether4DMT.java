package cellularautomata.automata;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class IntAether4DMT extends SymmetricIntCellularAutomaton4D {

	/** A 4D array representing the grid */
	private int[][][][] grid;
	
	private int initialValue;
	private int backgroundValue;
	private long currentStep;
	
	private int maxX;
	private int maxY;
	private int maxZ;

	/** Whether or not the values reached the bounds of the array */
	private boolean boundsReached;
	
	private final int threadCount;
	
	private ExecutorService executor;
	
	/**
	 * Creates an instance with the given initial value
	 * 
	 * @param initialValue the value at the origin at step 0
	 * @param backgroundValue the value padding all the grid but the origin at step 0
	 * @param threadCount the number of threads to use
	 */
	public IntAether4DMT(int initialValue, int backgroundValue, int threadCount) {
		if (backgroundValue > initialValue) {
			BigInteger maxValue = BigInteger.valueOf(initialValue).add(BigInteger.valueOf(backgroundValue)
					.subtract(BigInteger.valueOf(initialValue)).divide(BigInteger.valueOf(2)).multiply(BigInteger.valueOf(8)));
			if (maxValue.compareTo(BigInteger.valueOf(Integer.MAX_VALUE)) > 0) {
				throw new IllegalArgumentException("Resulting max value " + maxValue 
						+ " exceeds implementation's limit (" + Integer.MAX_VALUE 
						+ "). Consider using a different implementation or a smaller backgroundValue/initialValue ratio.");
			}
		}
		this.threadCount = threadCount;
		this.initialValue = initialValue;
		this.backgroundValue = backgroundValue;
		grid = new int[3][][][];
		grid[0] = buildGridBlock(0, backgroundValue);
		grid[1] = buildGridBlock(1, backgroundValue);
		grid[2] = buildGridBlock(2, backgroundValue);
		grid[0][0][0][0] = this.initialValue;
		maxX = 0;
		maxY = 0;
		maxZ = 0;
		boundsReached = false;
		currentStep = 0;
		executor = Executors.newCachedThreadPool();
	}
	
	/**
	 * Creates an instance using the passed data
	 * 
	 * @param data an instance of {@link CustomSymmetricIntCA4DData}
	 * @param threadCount the number of threads to uses
	 */
	public IntAether4DMT(CustomSymmetricIntCA4DData data, int threadCount) {
		this.threadCount = threadCount;
		initialValue = data.getInitialValue();
		backgroundValue = data.getBackgroundValue();
		grid = data.getGrid();
		maxX = data.getMaxX();
		maxY = data.getMaxY();
		maxZ = data.getMaxZ();
		boundsReached = data.isBoundsReached();
		currentStep = data.getStep();
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
		int[][][][] newGrid;
		if (boundsReached) {
			boundsReached = false;
			newGrid = new int[grid.length + 1][][][];
		} else {
			newGrid = new int[grid.length][][][];
		}
		List<Callable<ComputingResult>> calls = new ArrayList<Callable<ComputingResult>>();
		boolean split = false;
		int[][] sectionBounds = new int[threadCount][2];
		if (threadCount > 1) {
			int maxW = grid.length - 1;
			int sqrMaxW = maxW*maxW;
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
				newGrid[sectionBounds[i][0]] = buildGridBlock(sectionBounds[i][0], 0);
				newGrid[sectionBounds[i][1]] = buildGridBlock(sectionBounds[i][1], 0);
				calls.add(new Aether4DComputer(grid, newGrid, sectionBounds[i][0], sectionBounds[i][1]));
			}
		} else {
			newGrid[0] = buildGridBlock(0, 0);
			if (grid.length > 1) {
				newGrid[grid.length - 1] = buildGridBlock(grid.length - 1, 0);
			}
			calls.add(new Aether4DComputer(grid, newGrid, 0, grid.length - 1));
		}
		List<Future<ComputingResult>> futureResults = executor.invokeAll(calls);
		boolean changed = false;
		for (Future<ComputingResult> futureResult : futureResults) {
			ComputingResult result = futureResult.get();
			maxX = Math.max(maxX, result.getMaxX());
			maxY = Math.max(maxY, result.getMaxY());
			maxZ = Math.max(maxZ, result.getMaxZ());
			changed = changed || result.isChanged();
			boundsReached = boundsReached || result.isWBoundReached();
		}
		grid = newGrid;
		currentStep++;
		return changed;
	}
	
	private int[][][] buildGridBlock(int w, int value) {
		int[][][] newGridBlock = new int[w + 1][][];
		for (int x = 0; x < newGridBlock.length; x++) {
			newGridBlock[x] = new int[x + 1][];
			for (int y = 0; y < newGridBlock[x].length; y++) {
				newGridBlock[x][y] = new int[y + 1];
				if (value != 0) {
					for (int z = 0; z < newGridBlock[x][y].length; z++) {
						newGridBlock[x][y][z] = value;
					}
				}
			}
		}
		return newGridBlock;
	}
	
	public int getValueAt(int w, int x, int y, int z){	
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
			return backgroundValue;
		}
	}
	
	public int getNonSymmetricValueAt(int w, int x, int y, int z){	
		if (w < grid.length 
				&& x < grid[w].length 
				&& y < grid[w][x].length 
				&& z < grid[w][x][y].length) {
			return grid[w][x][y][z];
		} else {
			return backgroundValue;
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
	
	public int getNonSymmetricMinW() {
		return 0;
	}
	
	public int getNonSymmetricMaxW() {
		return grid.length - 1;
	}
	
	public int getNonSymmetricMinX() {
		return 0;
	}

	public int getNonSymmetricMaxX() {
		return maxX;
	}
	
	public int getNonSymmetricMinY() {
		return 0;
	}
	
	public int getNonSymmetricMaxY() {
		return maxY;
	}
	
	public int getNonSymmetricMinZ() {
		return 0;
	}
	
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
	public int getIntialValue() {
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
	
	class Aether4DComputer implements Callable<ComputingResult> {
		
		private static final byte W_POSITIVE = 0;
		private static final byte W_NEGATIVE = 1;
		private static final byte X_POSITIVE = 2;
		private static final byte X_NEGATIVE = 3;
		private static final byte Y_POSITIVE = 4;
		private static final byte Y_NEGATIVE = 5;
		private static final byte Z_POSITIVE = 6;
		private static final byte Z_NEGATIVE = 7;
		
		private int[][][][] grid;
		private int[][][][] newGrid;
		private int start;
		private int finish;
		private int maxX = 0;
		private int maxY = 0;
		private int maxZ = 0;
		private boolean changed;
		private int maxWMinusOne;
		private boolean boundsReached;
		
		public Aether4DComputer(int[][][][] grid, int[][][][] newGrid, int start, int finish) {
			this.grid = grid;
			this.newGrid = newGrid;
			this.start = start;
			this.finish = finish;
			maxWMinusOne = newGrid.length - 2;
		}
		
		private void computeBlock(int w) {
			int[] neighborValues = new int[8];
			byte[] neighborDirections = new byte[8];
			for (int x = 0; x <= w; x++) {
				for (int y = 0; y <= x; y++) {
					for (int z = 0; z <= y; z++) {
						int value = grid[w][x][y][z];
						if (value != 0) {
							int relevantNeighborCount = 0;
							int neighborValue;
							neighborValue = getValueAt(w + 1, x, y, z);
							if (neighborValue < value) {
								neighborValues[relevantNeighborCount] = neighborValue;
								neighborDirections[relevantNeighborCount] = W_POSITIVE;
								relevantNeighborCount++;
							}
							neighborValue = getValueAt(w - 1, x, y, z);
							if (neighborValue < value) {
								neighborValues[relevantNeighborCount] = neighborValue;
								neighborDirections[relevantNeighborCount] = W_NEGATIVE;
								relevantNeighborCount++;
							}
							neighborValue = getValueAt(w, x + 1, y, z);
							if (neighborValue < value) {
								neighborValues[relevantNeighborCount] = neighborValue;
								neighborDirections[relevantNeighborCount] = X_POSITIVE;
								relevantNeighborCount++;
							}
							neighborValue = getValueAt(w, x - 1, y, z);
							if (neighborValue < value) {
								neighborValues[relevantNeighborCount] = neighborValue;
								neighborDirections[relevantNeighborCount] = X_NEGATIVE;
								relevantNeighborCount++;
							}
							neighborValue = getValueAt(w, x, y + 1, z);
							if (neighborValue < value) {
								neighborValues[relevantNeighborCount] = neighborValue;
								neighborDirections[relevantNeighborCount] = Y_POSITIVE;
								relevantNeighborCount++;
							}
							neighborValue = getValueAt(w, x, y - 1, z);
							if (neighborValue < value) {
								neighborValues[relevantNeighborCount] = neighborValue;
								neighborDirections[relevantNeighborCount] = Y_NEGATIVE;
								relevantNeighborCount++;
							}
							neighborValue = getValueAt(w, x, y, z + 1);
							if (neighborValue < value) {
								neighborValues[relevantNeighborCount] = neighborValue;
								neighborDirections[relevantNeighborCount] = Z_POSITIVE;
								relevantNeighborCount++;
							}
							neighborValue = getValueAt(w, x, y, z - 1);
							if (neighborValue < value) {
								neighborValues[relevantNeighborCount] = neighborValue;
								neighborDirections[relevantNeighborCount] = Z_NEGATIVE;
								relevantNeighborCount++;
							}
							
							if (relevantNeighborCount > 0) {
								//sort
								boolean sorted = false;
								while (!sorted) {
									sorted = true;
									for (int i = relevantNeighborCount - 2; i >= 0; i--) {
										if (neighborValues[i] < neighborValues[i+1]) {
											sorted = false;
											int valSwap = neighborValues[i];
											neighborValues[i] = neighborValues[i+1];
											neighborValues[i+1] = valSwap;
											byte dirSwap = neighborDirections[i];
											neighborDirections[i] = neighborDirections[i+1];
											neighborDirections[i+1] = dirSwap;
										}
									}
								}
								//divide
								boolean isFirst = true;
								int previousNeighborValue = 0;
								for (int i = 0; i < relevantNeighborCount; i++,isFirst = false) {
									neighborValue = neighborValues[i];
									if (neighborValue != previousNeighborValue || isFirst) {
										int shareCount = relevantNeighborCount - i + 1;
										int toShare = value - neighborValue;
										int share = toShare/shareCount;
										if (share != 0) {
											changed = true;
											value = value - toShare + toShare%shareCount + share;
											for (int j = i; j < relevantNeighborCount; j++) {
												addToNeighbor(newGrid, w, x, y, z, neighborDirections[j], share);
											}
										}
										previousNeighborValue = neighborValue;
									}
								}	
							}					
							newGrid[w][x][y][z] += value;
						}
					}
				}
			}
		}
		
		private void addToNeighbor(int grid[][][][], int w, int x, int y, int z, byte direction, int value) {
			switch(direction) {
			case W_POSITIVE:
				addToWPositive(grid, w, x, y, z, value);
				break;
			case W_NEGATIVE:
				addToWNegative(grid, w, x, y, z, value);
				break;
			case X_POSITIVE:
				addToXPositive(grid, w, x, y, z, value);
				break;
			case X_NEGATIVE:
				addToXNegative(grid, w, x, y, z, value);
				break;
			case Y_POSITIVE:
				addToYPositive(grid, w, x, y, z, value);
				break;
			case Y_NEGATIVE:
				addToYNegative(grid, w, x, y, z, value);
				break;
			case Z_POSITIVE:
				addToZPositive(grid, w, x, y, z, value);
				break;
			case Z_NEGATIVE:
				addToZNegative(grid, w, x, y, z, value);
				break;
			}
		}
		
		private void addToWPositive(int[][][][] grid, int w, int x, int y, int z, int value) {
			grid[w+1][x][y][z] += value;
			if (w >= maxWMinusOne) {
				boundsReached = true;
			}	
		}
					
		private void addToWNegative(int[][][][] grid, int w, int x, int y, int z, int value) {
			if (w > x) {
				int valueToAdd = value;
				if (w == x + 1) {
					valueToAdd += value;
					if (x == y) {
						valueToAdd += value;
						if (y == z) {
							valueToAdd += value;
							if (w == 1) {
								valueToAdd += 4*value;
							}
						}
					}
				}
				grid[w-1][x][y][z] += valueToAdd;
			}
			if (w >= maxWMinusOne) {
				boundsReached = true;
			}
		}

		private void addToXPositive(int[][][][] grid, int w, int x, int y, int z, int value) {
			if (x < w) {
				int valueToAdd = value;
				if (x == w - 1) {
					valueToAdd += value;
				}
				int xx = x+1;
				grid[w][xx][y][z] += valueToAdd;
				if (xx > maxX)
					maxX = xx;
			}
		}

		private void addToXNegative(int[][][][] grid, int w, int x, int y, int z, int value) {
			if (x > y) {
				int valueToAdd = value;									
				if (y == x - 1) {
					valueToAdd += value;
					if (y == z) {
						valueToAdd += value;
						if (y == 0) {
							valueToAdd += 3*value;
						}
					}
				}
				grid[w][x-1][y][z] += valueToAdd;
			}
		}

		private void addToYPositive(int[][][][] grid, int w, int x, int y, int z, int value) {
			if (y < x) {
				int valueToAdd = value;									
				if (y == x - 1) {
					valueToAdd += value;
					if (w == x) {
						valueToAdd += value;
					}
				}
				int yy = y+1;
				grid[w][x][yy][z] += valueToAdd;
				if (yy > maxY)
					maxY = yy;
			}
		}

		private void addToYNegative(int[][][][] grid, int w, int x, int y, int z, int value) {
			if (y > z) {	
				int valueToAdd = value;
				if (z == y - 1) {
					valueToAdd += value;
					if (y == 1) {
						valueToAdd += 2*value;
					}
				}
				grid[w][x][y-1][z] += valueToAdd;
			}
		}

		private void addToZPositive(int[][][][] grid, int w, int x, int y, int z, int value) {
			if (z < y) {
				int valueToAdd = value;
				if (z == y - 1) {
					valueToAdd += value;
					if (x == y) {
						valueToAdd += value;
						if (w == x) {
							valueToAdd += value;
						}
					}
				}
				int zz = z+1;
				grid[w][x][y][zz] += valueToAdd;
				if (zz > maxZ)
					maxZ = zz;
			}
		}

		private void addToZNegative(int[][][][] grid, int w, int x, int y, int z, int value) {
			if (z > 0) {
				int valueToAdd = value;
				if (z == 1) {
					valueToAdd += value;
				}
				grid[w][x][y][z-1] += valueToAdd;
			}
		}
		
		@Override
		public ComputingResult call() {
			if (finish - start + 1 > 1) {
				int w = start;
				int nextW = w + 1;
				if (nextW < finish) {
					newGrid[nextW] = buildGridBlock(nextW, 0);
				}
				boolean isBeginning = start == 0;
				if (!isBeginning) {
					synchronized (newGrid[w - 1]) {
						computeBlock(w);
					}
				} else {
					computeBlock(w);
				}
				w++;
				if (w < finish && !isBeginning) {
					nextW = w + 1;
					if (nextW < finish) {
						newGrid[nextW] = buildGridBlock(nextW, 0);
					}
					synchronized (newGrid[w - 2]) {
						computeBlock(w);
					}
					w++;
				}
				for (; w < finish; w++) {
					nextW = w + 1;
					if (nextW < finish) {
						newGrid[nextW] = buildGridBlock(nextW, 0);
						computeBlock(w);
					} else {
						synchronized (newGrid[finish]) {
							computeBlock(w);
						}
					}					
					grid[w-1] = null;
				}
				if (finish < grid.length - 1) {
					synchronized (newGrid[finish]) {
						computeBlock(finish);
					}
				} else {
					if (newGrid.length > grid.length) {
						newGrid[finish + 1] = buildGridBlock(finish + 1, backgroundValue);
					}
					computeBlock(w);
				}
				grid[finish-1] = null;
			} else {
				int x = start;
				if (x == grid.length - 1 && newGrid.length > grid.length) {
					newGrid[x + 1] = buildGridBlock(x + 1, backgroundValue);
				}
				computeBlock(x);
			}
			return new ComputingResult(maxX, maxY, maxZ, changed, boundsReached);
		}
		
	}

	@Override
	public CustomSymmetricIntCA4DData getData() {
		return new CustomSymmetricIntCA4DData(grid, initialValue, backgroundValue, currentStep, boundsReached, maxX, maxY, maxZ);
	}

	@Override
	public String getName() {
		return "Aether4D";
	}

	@Override
	public String getSubFolderPath() {
		return getName() + "/" + initialValue + "/" + backgroundValue;
	}

	@Override
	public int getBackgroundValue() {
		return backgroundValue;
	}
	
}
