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

import java.math.BigInteger;

public class Aether2D extends SymmetricLongCellularAutomaton2D {

	/** A 2D array representing the grid */
	private long[][] grid;
	
	private long backgroundValue;
	private long initialValue;
	private long currentStep;
	
	/** Whether or not the values reached the bounds of the array */
	private boolean boundsReached;

	private int maxY;

	private int maxXMinusOne;

	private boolean changed;
	
	/**
	 * Creates an instance with the given initial value.
	 * 
	 * @param initialValue the value at the origin at step 0
	 * @param backgroundValue the value padding all the grid but the origin at step 0
	 */
	public Aether2D(long initialValue, long backgroundValue) {
		if (backgroundValue > initialValue) {
			BigInteger maxValue = BigInteger.valueOf(initialValue).add(BigInteger.valueOf(backgroundValue)
					.subtract(BigInteger.valueOf(initialValue)).divide(BigInteger.valueOf(2)).multiply(BigInteger.valueOf(4)));
			if (maxValue.compareTo(BigInteger.valueOf(Long.MAX_VALUE)) > 0) {
				throw new IllegalArgumentException("Resulting max value " + maxValue 
						+ " exceeds implementation's limit (" + Long.MAX_VALUE 
						+ "). Consider using a different implementation or a smaller backgroundValue/initialValue ratio.");
			}
		}
		this.initialValue = initialValue;
		this.backgroundValue = backgroundValue;
		grid = new long[3][];
		grid[0] = buildGridBlock(0, backgroundValue);
		grid[1] = buildGridBlock(1, backgroundValue);
		grid[2] = buildGridBlock(2, backgroundValue);
		grid[0][0] = initialValue;
		maxY = 0;
		boundsReached = false;
		currentStep = 0;
	}
	
	public boolean nextStep(){
		long[][] newGrid = null;
		if (boundsReached) {
			boundsReached = false;
			newGrid = new long[grid.length + 1][];
		} else {
			newGrid = new long[grid.length][];
		}
		maxXMinusOne = newGrid.length - 2;
		changed = false;
		newGrid[0] = buildGridBlock(0, 0);
		boolean isFirst = true;
		for (int x = 0, nextX = 1; x < grid.length; x++, nextX++, isFirst = false) {
			if (nextX < grid.length) {
				newGrid[nextX] = buildGridBlock(nextX, 0);
			} else if (nextX < newGrid.length) {
				newGrid[nextX] = buildGridBlock(nextX, backgroundValue);
			}
			for (int y = 0; y <= x; y++) {
				long value = this.grid[x][y];
				if (value != 0) {
					long up = getValueAt(x, y + 1);
					long down = getValueAt(x, y - 1); 
					long left = getValueAt(x - 1, y);
					long right = getValueAt(x + 1, y);
					
					//the "aetherLogicMethod*" methods have been machine generated using the code in the AetherGen folder
					value = aetherLogicMethod1(newGrid, value, right, left, up, down, x, y);
					
					newGrid[x][y] += value;
				}
			}
			if (!isFirst) {
				grid[x-1] = null;
			}
		}
		grid = newGrid;
		currentStep++;
		return changed;
	}
	
	protected long aetherLogicMethod1(long[][] newGrid, long value, long right, long left, long up, long down, int x, int y) {
		if (right < value) {
			value = aetherLogicMethod2(newGrid, value, right, left, up, down, x, y);
		} else {//right >= value
			value = aetherLogicMethod76(newGrid, value, right, left, up, down, x, y);
		}
		return value;
	}

	protected long aetherLogicMethod76(long[][] newGrid, long value, long right, long left, long up, long down, int x, int y) {
		if (left < value) {
			value = aetherLogicMethod77(newGrid, value, right, left, up, down, x, y);
		} else {//left >= value
			value = aetherLogicMethod89(newGrid, value, right, left, up, down, x, y);
		}
		return value;
	}

	protected long aetherLogicMethod89(long[][] newGrid, long value, long right, long left, long up, long down, int x, int y) {
		if (up < value) {
			value = aetherLogicMethod90(newGrid, value, right, left, up, down, x, y);
		} else {//up >= value
			value = aetherLogicMethod92(newGrid, value, right, left, up, down, x, y);
		}
		return value;
	}

	protected long aetherLogicMethod92(long[][] newGrid, long value, long right, long left, long up, long down, int x, int y) {
		if (down < value) {
			// value > down
			long toShare, share;
			int shareCount;
			toShare = value - down;
			shareCount = 2;
			share = toShare/shareCount;
			if (share != 0) {
				changed = true;
				value = value - toShare + toShare%shareCount + share;
				addDown(newGrid, x, y, share);
			}
		}
		return value;
	}

	protected long aetherLogicMethod90(long[][] newGrid, long value, long right, long left, long up, long down, int x, int y) {
		if (down < up) {
			// value > up > down
			long toShare, share;
			int shareCount;
			toShare = value - up;
			shareCount = 3;
			share = toShare/shareCount;
			if (share != 0) {
				changed = true;
				value = value - toShare + toShare%shareCount + share;
				addUp(newGrid, x, y, share);
				addDown(newGrid, x, y, share);
			}
			toShare = value - down;
			shareCount = 2;
			share = toShare/shareCount;
			if (share != 0) {
				changed = true;
				value = value - toShare + toShare%shareCount + share;
				addDown(newGrid, x, y, share);
			}
		} else if (down > up) {
			value = aetherLogicMethod91(newGrid, value, right, left, up, down, x, y);
		} else {//down == up
			// value > up = down
			long toShare, share;
			int shareCount;
			toShare = value - up;
			shareCount = 3;
			share = toShare/shareCount;
			if (share != 0) {
				changed = true;
				value = value - toShare + toShare%shareCount + share;
				addUp(newGrid, x, y, share);
				addDown(newGrid, x, y, share);
			}
		}
		return value;
	}

	protected long aetherLogicMethod91(long[][] newGrid, long value, long right, long left, long up, long down, int x, int y) {
		if (down < value) {
			// value > down > up
			long toShare, share;
			int shareCount;
			toShare = value - down;
			shareCount = 3;
			share = toShare/shareCount;
			if (share != 0) {
				changed = true;
				value = value - toShare + toShare%shareCount + share;
				addDown(newGrid, x, y, share);
				addUp(newGrid, x, y, share);
			}
			toShare = value - up;
			shareCount = 2;
			share = toShare/shareCount;
			if (share != 0) {
				changed = true;
				value = value - toShare + toShare%shareCount + share;
				addUp(newGrid, x, y, share);
			}
		} else {//down >= value
			// value > up
			long toShare, share;
			int shareCount;
			toShare = value - up;
			shareCount = 2;
			share = toShare/shareCount;
			if (share != 0) {
				changed = true;
				value = value - toShare + toShare%shareCount + share;
				addUp(newGrid, x, y, share);
			}
		}
		return value;
	}

	protected long aetherLogicMethod77(long[][] newGrid, long value, long right, long left, long up, long down, int x, int y) {
		if (up < left) {
			value = aetherLogicMethod78(newGrid, value, right, left, up, down, x, y);
		} else if (up > left) {
			value = aetherLogicMethod81(newGrid, value, right, left, up, down, x, y);
		} else {//up == left
			value = aetherLogicMethod87(newGrid, value, right, left, up, down, x, y);
		}
		return value;
	}

	protected long aetherLogicMethod87(long[][] newGrid, long value, long right, long left, long up, long down, int x, int y) {
		if (down < left) {
			// value > left = up > down
			long toShare, share;
			int shareCount;
			toShare = value - left;
			shareCount = 4;
			share = toShare/shareCount;
			if (share != 0) {
				changed = true;
				value = value - toShare + toShare%shareCount + share;
				addLeft(newGrid, x, y, share);
				addUp(newGrid, x, y, share);
				addDown(newGrid, x, y, share);
			}
			toShare = value - down;
			shareCount = 2;
			share = toShare/shareCount;
			if (share != 0) {
				changed = true;
				value = value - toShare + toShare%shareCount + share;
				addDown(newGrid, x, y, share);
			}
		} else if (down > left) {
			value = aetherLogicMethod88(newGrid, value, right, left, up, down, x, y);
		} else {//down == left
			// value > left = up = down
			long toShare, share;
			int shareCount;
			toShare = value - left;
			shareCount = 4;
			share = toShare/shareCount;
			if (share != 0) {
				changed = true;
				value = value - toShare + toShare%shareCount + share;
				addLeft(newGrid, x, y, share);
				addUp(newGrid, x, y, share);
				addDown(newGrid, x, y, share);
			}
		}
		return value;
	}

	protected long aetherLogicMethod88(long[][] newGrid, long value, long right, long left, long up, long down, int x, int y) {
		if (down < value) {
			// value > down > left = up
			long toShare, share;
			int shareCount;
			toShare = value - down;
			shareCount = 4;
			share = toShare/shareCount;
			if (share != 0) {
				changed = true;
				value = value - toShare + toShare%shareCount + share;
				addDown(newGrid, x, y, share);
				addLeft(newGrid, x, y, share);
				addUp(newGrid, x, y, share);
			}
			toShare = value - left;
			shareCount = 3;
			share = toShare/shareCount;
			if (share != 0) {
				changed = true;
				value = value - toShare + toShare%shareCount + share;
				addLeft(newGrid, x, y, share);
				addUp(newGrid, x, y, share);
			}
		} else {//down >= value
			// value > left = up
			long toShare, share;
			int shareCount;
			toShare = value - left;
			shareCount = 3;
			share = toShare/shareCount;
			if (share != 0) {
				changed = true;
				value = value - toShare + toShare%shareCount + share;
				addLeft(newGrid, x, y, share);
				addUp(newGrid, x, y, share);
			}
		}
		return value;
	}

	protected long aetherLogicMethod81(long[][] newGrid, long value, long right, long left, long up, long down, int x, int y) {
		if (up < value) {
			value = aetherLogicMethod82(newGrid, value, right, left, up, down, x, y);
		} else {//up >= value
			value = aetherLogicMethod85(newGrid, value, right, left, up, down, x, y);
		}
		return value;
	}

	protected long aetherLogicMethod85(long[][] newGrid, long value, long right, long left, long up, long down, int x, int y) {
		if (down < left) {
			// value > left > down
			long toShare, share;
			int shareCount;
			toShare = value - left;
			shareCount = 3;
			share = toShare/shareCount;
			if (share != 0) {
				changed = true;
				value = value - toShare + toShare%shareCount + share;
				addLeft(newGrid, x, y, share);
				addDown(newGrid, x, y, share);
			}
			toShare = value - down;
			shareCount = 2;
			share = toShare/shareCount;
			if (share != 0) {
				changed = true;
				value = value - toShare + toShare%shareCount + share;
				addDown(newGrid, x, y, share);
			}
		} else if (down > left) {
			value = aetherLogicMethod86(newGrid, value, right, left, up, down, x, y);
		} else {//down == left
			// value > left = down
			long toShare, share;
			int shareCount;
			toShare = value - left;
			shareCount = 3;
			share = toShare/shareCount;
			if (share != 0) {
				changed = true;
				value = value - toShare + toShare%shareCount + share;
				addLeft(newGrid, x, y, share);
				addDown(newGrid, x, y, share);
			}
		}
		return value;
	}

	protected long aetherLogicMethod86(long[][] newGrid, long value, long right, long left, long up, long down, int x, int y) {
		if (down < value) {
			// value > down > left
			long toShare, share;
			int shareCount;
			toShare = value - down;
			shareCount = 3;
			share = toShare/shareCount;
			if (share != 0) {
				changed = true;
				value = value - toShare + toShare%shareCount + share;
				addDown(newGrid, x, y, share);
				addLeft(newGrid, x, y, share);
			}
			toShare = value - left;
			shareCount = 2;
			share = toShare/shareCount;
			if (share != 0) {
				changed = true;
				value = value - toShare + toShare%shareCount + share;
				addLeft(newGrid, x, y, share);
			}
		} else {//down >= value
			// value > left
			long toShare, share;
			int shareCount;
			toShare = value - left;
			shareCount = 2;
			share = toShare/shareCount;
			if (share != 0) {
				changed = true;
				value = value - toShare + toShare%shareCount + share;
				addLeft(newGrid, x, y, share);
			}
		}
		return value;
	}

	protected long aetherLogicMethod82(long[][] newGrid, long value, long right, long left, long up, long down, int x, int y) {
		if (down < left) {
			// value > up > left > down
			long toShare, share;
			int shareCount;
			toShare = value - up;
			shareCount = 4;
			share = toShare/shareCount;
			if (share != 0) {
				changed = true;
				value = value - toShare + toShare%shareCount + share;
				addUp(newGrid, x, y, share);
				addLeft(newGrid, x, y, share);
				addDown(newGrid, x, y, share);
			}
			toShare = value - left;
			shareCount = 3;
			share = toShare/shareCount;
			if (share != 0) {
				changed = true;
				value = value - toShare + toShare%shareCount + share;
				addLeft(newGrid, x, y, share);
				addDown(newGrid, x, y, share);
			}
			toShare = value - down;
			shareCount = 2;
			share = toShare/shareCount;
			if (share != 0) {
				changed = true;
				value = value - toShare + toShare%shareCount + share;
				addDown(newGrid, x, y, share);
			}
		} else if (down > left) {
			value = aetherLogicMethod83(newGrid, value, right, left, up, down, x, y);
		} else {//down == left
			// value > up > left = down
			long toShare, share;
			int shareCount;
			toShare = value - up;
			shareCount = 4;
			share = toShare/shareCount;
			if (share != 0) {
				changed = true;
				value = value - toShare + toShare%shareCount + share;
				addUp(newGrid, x, y, share);
				addLeft(newGrid, x, y, share);
				addDown(newGrid, x, y, share);
			}
			toShare = value - left;
			shareCount = 3;
			share = toShare/shareCount;
			if (share != 0) {
				changed = true;
				value = value - toShare + toShare%shareCount + share;
				addLeft(newGrid, x, y, share);
				addDown(newGrid, x, y, share);
			}
		}
		return value;
	}

	protected long aetherLogicMethod83(long[][] newGrid, long value, long right, long left, long up, long down, int x, int y) {
		if (down < up) {
			// value > up > down > left
			long toShare, share;
			int shareCount;
			toShare = value - up;
			shareCount = 4;
			share = toShare/shareCount;
			if (share != 0) {
				changed = true;
				value = value - toShare + toShare%shareCount + share;
				addUp(newGrid, x, y, share);
				addDown(newGrid, x, y, share);
				addLeft(newGrid, x, y, share);
			}
			toShare = value - down;
			shareCount = 3;
			share = toShare/shareCount;
			if (share != 0) {
				changed = true;
				value = value - toShare + toShare%shareCount + share;
				addDown(newGrid, x, y, share);
				addLeft(newGrid, x, y, share);
			}
			toShare = value - left;
			shareCount = 2;
			share = toShare/shareCount;
			if (share != 0) {
				changed = true;
				value = value - toShare + toShare%shareCount + share;
				addLeft(newGrid, x, y, share);
			}
		} else if (down > up) {
			value = aetherLogicMethod84(newGrid, value, right, left, up, down, x, y);
		} else {//down == up
			// value > up = down > left
			long toShare, share;
			int shareCount;
			toShare = value - up;
			shareCount = 4;
			share = toShare/shareCount;
			if (share != 0) {
				changed = true;
				value = value - toShare + toShare%shareCount + share;
				addUp(newGrid, x, y, share);
				addDown(newGrid, x, y, share);
				addLeft(newGrid, x, y, share);
			}
			toShare = value - left;
			shareCount = 2;
			share = toShare/shareCount;
			if (share != 0) {
				changed = true;
				value = value - toShare + toShare%shareCount + share;
				addLeft(newGrid, x, y, share);
			}
		}
		return value;
	}

	protected long aetherLogicMethod84(long[][] newGrid, long value, long right, long left, long up, long down, int x, int y) {
		if (down < value) {
			// value > down > up > left
			long toShare, share;
			int shareCount;
			toShare = value - down;
			shareCount = 4;
			share = toShare/shareCount;
			if (share != 0) {
				changed = true;
				value = value - toShare + toShare%shareCount + share;
				addDown(newGrid, x, y, share);
				addUp(newGrid, x, y, share);
				addLeft(newGrid, x, y, share);
			}
			toShare = value - up;
			shareCount = 3;
			share = toShare/shareCount;
			if (share != 0) {
				changed = true;
				value = value - toShare + toShare%shareCount + share;
				addUp(newGrid, x, y, share);
				addLeft(newGrid, x, y, share);
			}
			toShare = value - left;
			shareCount = 2;
			share = toShare/shareCount;
			if (share != 0) {
				changed = true;
				value = value - toShare + toShare%shareCount + share;
				addLeft(newGrid, x, y, share);
			}
		} else {//down >= value
			// value > up > left
			long toShare, share;
			int shareCount;
			toShare = value - up;
			shareCount = 3;
			share = toShare/shareCount;
			if (share != 0) {
				changed = true;
				value = value - toShare + toShare%shareCount + share;
				addUp(newGrid, x, y, share);
				addLeft(newGrid, x, y, share);
			}
			toShare = value - left;
			shareCount = 2;
			share = toShare/shareCount;
			if (share != 0) {
				changed = true;
				value = value - toShare + toShare%shareCount + share;
				addLeft(newGrid, x, y, share);
			}
		}
		return value;
	}

	protected long aetherLogicMethod78(long[][] newGrid, long value, long right, long left, long up, long down, int x, int y) {
		if (down < up) {
			// value > left > up > down
			long toShare, share;
			int shareCount;
			toShare = value - left;
			shareCount = 4;
			share = toShare/shareCount;
			if (share != 0) {
				changed = true;
				value = value - toShare + toShare%shareCount + share;
				addLeft(newGrid, x, y, share);
				addUp(newGrid, x, y, share);
				addDown(newGrid, x, y, share);
			}
			toShare = value - up;
			shareCount = 3;
			share = toShare/shareCount;
			if (share != 0) {
				changed = true;
				value = value - toShare + toShare%shareCount + share;
				addUp(newGrid, x, y, share);
				addDown(newGrid, x, y, share);
			}
			toShare = value - down;
			shareCount = 2;
			share = toShare/shareCount;
			if (share != 0) {
				changed = true;
				value = value - toShare + toShare%shareCount + share;
				addDown(newGrid, x, y, share);
			}
		} else if (down > up) {
			value = aetherLogicMethod79(newGrid, value, right, left, up, down, x, y);
		} else {//down == up
			// value > left > up = down
			long toShare, share;
			int shareCount;
			toShare = value - left;
			shareCount = 4;
			share = toShare/shareCount;
			if (share != 0) {
				changed = true;
				value = value - toShare + toShare%shareCount + share;
				addLeft(newGrid, x, y, share);
				addUp(newGrid, x, y, share);
				addDown(newGrid, x, y, share);
			}
			toShare = value - up;
			shareCount = 3;
			share = toShare/shareCount;
			if (share != 0) {
				changed = true;
				value = value - toShare + toShare%shareCount + share;
				addUp(newGrid, x, y, share);
				addDown(newGrid, x, y, share);
			}
		}
		return value;
	}

	protected long aetherLogicMethod79(long[][] newGrid, long value, long right, long left, long up, long down, int x, int y) {
		if (down < left) {
			// value > left > down > up
			long toShare, share;
			int shareCount;
			toShare = value - left;
			shareCount = 4;
			share = toShare/shareCount;
			if (share != 0) {
				changed = true;
				value = value - toShare + toShare%shareCount + share;
				addLeft(newGrid, x, y, share);
				addDown(newGrid, x, y, share);
				addUp(newGrid, x, y, share);
			}
			toShare = value - down;
			shareCount = 3;
			share = toShare/shareCount;
			if (share != 0) {
				changed = true;
				value = value - toShare + toShare%shareCount + share;
				addDown(newGrid, x, y, share);
				addUp(newGrid, x, y, share);
			}
			toShare = value - up;
			shareCount = 2;
			share = toShare/shareCount;
			if (share != 0) {
				changed = true;
				value = value - toShare + toShare%shareCount + share;
				addUp(newGrid, x, y, share);
			}
		} else if (down > left) {
			value = aetherLogicMethod80(newGrid, value, right, left, up, down, x, y);
		} else {//down == left
			// value > left = down > up
			long toShare, share;
			int shareCount;
			toShare = value - left;
			shareCount = 4;
			share = toShare/shareCount;
			if (share != 0) {
				changed = true;
				value = value - toShare + toShare%shareCount + share;
				addLeft(newGrid, x, y, share);
				addDown(newGrid, x, y, share);
				addUp(newGrid, x, y, share);
			}
			toShare = value - up;
			shareCount = 2;
			share = toShare/shareCount;
			if (share != 0) {
				changed = true;
				value = value - toShare + toShare%shareCount + share;
				addUp(newGrid, x, y, share);
			}
		}
		return value;
	}

	protected long aetherLogicMethod80(long[][] newGrid, long value, long right, long left, long up, long down, int x, int y) {
		if (down < value) {
			// value > down > left > up
			long toShare, share;
			int shareCount;
			toShare = value - down;
			shareCount = 4;
			share = toShare/shareCount;
			if (share != 0) {
				changed = true;
				value = value - toShare + toShare%shareCount + share;
				addDown(newGrid, x, y, share);
				addLeft(newGrid, x, y, share);
				addUp(newGrid, x, y, share);
			}
			toShare = value - left;
			shareCount = 3;
			share = toShare/shareCount;
			if (share != 0) {
				changed = true;
				value = value - toShare + toShare%shareCount + share;
				addLeft(newGrid, x, y, share);
				addUp(newGrid, x, y, share);
			}
			toShare = value - up;
			shareCount = 2;
			share = toShare/shareCount;
			if (share != 0) {
				changed = true;
				value = value - toShare + toShare%shareCount + share;
				addUp(newGrid, x, y, share);
			}
		} else {//down >= value
			// value > left > up
			long toShare, share;
			int shareCount;
			toShare = value - left;
			shareCount = 3;
			share = toShare/shareCount;
			if (share != 0) {
				changed = true;
				value = value - toShare + toShare%shareCount + share;
				addLeft(newGrid, x, y, share);
				addUp(newGrid, x, y, share);
			}
			toShare = value - up;
			shareCount = 2;
			share = toShare/shareCount;
			if (share != 0) {
				changed = true;
				value = value - toShare + toShare%shareCount + share;
				addUp(newGrid, x, y, share);
			}
		}
		return value;
	}

	protected long aetherLogicMethod2(long[][] newGrid, long value, long right, long left, long up, long down, int x, int y) {
		if (left < right) {
			value = aetherLogicMethod3(newGrid, value, right, left, up, down, x, y);
		} else if (left > right) {
			value = aetherLogicMethod27(newGrid, value, right, left, up, down, x, y);
		} else {//left == right
			value = aetherLogicMethod64(newGrid, value, right, left, up, down, x, y);
		}
		return value;
	}

	protected long aetherLogicMethod64(long[][] newGrid, long value, long right, long left, long up, long down, int x, int y) {
		if (up < right) {
			value = aetherLogicMethod65(newGrid, value, right, left, up, down, x, y);
		} else if (up > right) {
			value = aetherLogicMethod68(newGrid, value, right, left, up, down, x, y);
		} else {//up == right
			value = aetherLogicMethod74(newGrid, value, right, left, up, down, x, y);
		}
		return value;
	}

	protected long aetherLogicMethod74(long[][] newGrid, long value, long right, long left, long up, long down, int x, int y) {
		if (down < right) {
			// value > right = left = up > down
			long toShare, share;
			int shareCount;
			toShare = value - right;
			shareCount = 5;
			share = toShare/shareCount;
			if (share != 0) {
				changed = true;
				value = value - toShare + toShare%shareCount + share;
				addRight(newGrid, x, y, share);
				addLeft(newGrid, x, y, share);
				addUp(newGrid, x, y, share);
				addDown(newGrid, x, y, share);
			}
			toShare = value - down;
			shareCount = 2;
			share = toShare/shareCount;
			if (share != 0) {
				changed = true;
				value = value - toShare + toShare%shareCount + share;
				addDown(newGrid, x, y, share);
			}
		} else if (down > right) {
			value = aetherLogicMethod75(newGrid, value, right, left, up, down, x, y);
		} else {//down == right
			// value > right = left = up = down
			long toShare, share;
			int shareCount;
			toShare = value - right;
			shareCount = 5;
			share = toShare/shareCount;
			if (share != 0) {
				changed = true;
				value = value - toShare + toShare%shareCount + share;
				addRight(newGrid, x, y, share);
				addLeft(newGrid, x, y, share);
				addUp(newGrid, x, y, share);
				addDown(newGrid, x, y, share);
			}
		}
		return value;
	}

	protected long aetherLogicMethod75(long[][] newGrid, long value, long right, long left, long up, long down, int x, int y) {
		if (down < value) {
			// value > down > right = left = up
			long toShare, share;
			int shareCount;
			toShare = value - down;
			shareCount = 5;
			share = toShare/shareCount;
			if (share != 0) {
				changed = true;
				value = value - toShare + toShare%shareCount + share;
				addDown(newGrid, x, y, share);
				addRight(newGrid, x, y, share);
				addLeft(newGrid, x, y, share);
				addUp(newGrid, x, y, share);
			}
			toShare = value - right;
			shareCount = 4;
			share = toShare/shareCount;
			if (share != 0) {
				changed = true;
				value = value - toShare + toShare%shareCount + share;
				addRight(newGrid, x, y, share);
				addLeft(newGrid, x, y, share);
				addUp(newGrid, x, y, share);
			}
		} else {//down >= value
			// value > right = left = up
			long toShare, share;
			int shareCount;
			toShare = value - right;
			shareCount = 4;
			share = toShare/shareCount;
			if (share != 0) {
				changed = true;
				value = value - toShare + toShare%shareCount + share;
				addRight(newGrid, x, y, share);
				addLeft(newGrid, x, y, share);
				addUp(newGrid, x, y, share);
			}
		}
		return value;
	}

	protected long aetherLogicMethod68(long[][] newGrid, long value, long right, long left, long up, long down, int x, int y) {
		if (up < value) {
			value = aetherLogicMethod69(newGrid, value, right, left, up, down, x, y);
		} else {//up >= value
			value = aetherLogicMethod72(newGrid, value, right, left, up, down, x, y);
		}
		return value;
	}

	protected long aetherLogicMethod72(long[][] newGrid, long value, long right, long left, long up, long down, int x, int y) {
		if (down < right) {
			// value > right = left > down
			long toShare, share;
			int shareCount;
			toShare = value - right;
			shareCount = 4;
			share = toShare/shareCount;
			if (share != 0) {
				changed = true;
				value = value - toShare + toShare%shareCount + share;
				addRight(newGrid, x, y, share);
				addLeft(newGrid, x, y, share);
				addDown(newGrid, x, y, share);
			}
			toShare = value - down;
			shareCount = 2;
			share = toShare/shareCount;
			if (share != 0) {
				changed = true;
				value = value - toShare + toShare%shareCount + share;
				addDown(newGrid, x, y, share);
			}
		} else if (down > right) {
			value = aetherLogicMethod73(newGrid, value, right, left, up, down, x, y);
		} else {//down == right
			// value > right = left = down
			long toShare, share;
			int shareCount;
			toShare = value - right;
			shareCount = 4;
			share = toShare/shareCount;
			if (share != 0) {
				changed = true;
				value = value - toShare + toShare%shareCount + share;
				addRight(newGrid, x, y, share);
				addLeft(newGrid, x, y, share);
				addDown(newGrid, x, y, share);
			}
		}
		return value;
	}

	protected long aetherLogicMethod73(long[][] newGrid, long value, long right, long left, long up, long down, int x, int y) {
		if (down < value) {
			// value > down > right = left
			long toShare, share;
			int shareCount;
			toShare = value - down;
			shareCount = 4;
			share = toShare/shareCount;
			if (share != 0) {
				changed = true;
				value = value - toShare + toShare%shareCount + share;
				addDown(newGrid, x, y, share);
				addRight(newGrid, x, y, share);
				addLeft(newGrid, x, y, share);
			}
			toShare = value - right;
			shareCount = 3;
			share = toShare/shareCount;
			if (share != 0) {
				changed = true;
				value = value - toShare + toShare%shareCount + share;
				addRight(newGrid, x, y, share);
				addLeft(newGrid, x, y, share);
			}
		} else {//down >= value
			// value > right = left
			long toShare, share;
			int shareCount;
			toShare = value - right;
			shareCount = 3;
			share = toShare/shareCount;
			if (share != 0) {
				changed = true;
				value = value - toShare + toShare%shareCount + share;
				addRight(newGrid, x, y, share);
				addLeft(newGrid, x, y, share);
			}
		}
		return value;
	}

	protected long aetherLogicMethod69(long[][] newGrid, long value, long right, long left, long up, long down, int x, int y) {
		if (down < right) {
			// value > up > right = left > down
			long toShare, share;
			int shareCount;
			toShare = value - up;
			shareCount = 5;
			share = toShare/shareCount;
			if (share != 0) {
				changed = true;
				value = value - toShare + toShare%shareCount + share;
				addUp(newGrid, x, y, share);
				addRight(newGrid, x, y, share);
				addLeft(newGrid, x, y, share);
				addDown(newGrid, x, y, share);
			}
			toShare = value - right;
			shareCount = 4;
			share = toShare/shareCount;
			if (share != 0) {
				changed = true;
				value = value - toShare + toShare%shareCount + share;
				addRight(newGrid, x, y, share);
				addLeft(newGrid, x, y, share);
				addDown(newGrid, x, y, share);
			}
			toShare = value - down;
			shareCount = 2;
			share = toShare/shareCount;
			if (share != 0) {
				changed = true;
				value = value - toShare + toShare%shareCount + share;
				addDown(newGrid, x, y, share);
			}
		} else if (down > right) {
			value = aetherLogicMethod70(newGrid, value, right, left, up, down, x, y);
		} else {//down == right
			// value > up > right = left = down
			long toShare, share;
			int shareCount;
			toShare = value - up;
			shareCount = 5;
			share = toShare/shareCount;
			if (share != 0) {
				changed = true;
				value = value - toShare + toShare%shareCount + share;
				addUp(newGrid, x, y, share);
				addRight(newGrid, x, y, share);
				addLeft(newGrid, x, y, share);
				addDown(newGrid, x, y, share);
			}
			toShare = value - right;
			shareCount = 4;
			share = toShare/shareCount;
			if (share != 0) {
				changed = true;
				value = value - toShare + toShare%shareCount + share;
				addRight(newGrid, x, y, share);
				addLeft(newGrid, x, y, share);
				addDown(newGrid, x, y, share);
			}
		}
		return value;
	}

	protected long aetherLogicMethod70(long[][] newGrid, long value, long right, long left, long up, long down, int x, int y) {
		if (down < up) {
			// value > up > down > right = left
			long toShare, share;
			int shareCount;
			toShare = value - up;
			shareCount = 5;
			share = toShare/shareCount;
			if (share != 0) {
				changed = true;
				value = value - toShare + toShare%shareCount + share;
				addUp(newGrid, x, y, share);
				addDown(newGrid, x, y, share);
				addRight(newGrid, x, y, share);
				addLeft(newGrid, x, y, share);
			}
			toShare = value - down;
			shareCount = 4;
			share = toShare/shareCount;
			if (share != 0) {
				changed = true;
				value = value - toShare + toShare%shareCount + share;
				addDown(newGrid, x, y, share);
				addRight(newGrid, x, y, share);
				addLeft(newGrid, x, y, share);
			}
			toShare = value - right;
			shareCount = 3;
			share = toShare/shareCount;
			if (share != 0) {
				changed = true;
				value = value - toShare + toShare%shareCount + share;
				addRight(newGrid, x, y, share);
				addLeft(newGrid, x, y, share);
			}
		} else if (down > up) {
			value = aetherLogicMethod71(newGrid, value, right, left, up, down, x, y);
		} else {//down == up
			// value > up = down > right = left
			long toShare, share;
			int shareCount;
			toShare = value - up;
			shareCount = 5;
			share = toShare/shareCount;
			if (share != 0) {
				changed = true;
				value = value - toShare + toShare%shareCount + share;
				addUp(newGrid, x, y, share);
				addDown(newGrid, x, y, share);
				addRight(newGrid, x, y, share);
				addLeft(newGrid, x, y, share);
			}
			toShare = value - right;
			shareCount = 3;
			share = toShare/shareCount;
			if (share != 0) {
				changed = true;
				value = value - toShare + toShare%shareCount + share;
				addRight(newGrid, x, y, share);
				addLeft(newGrid, x, y, share);
			}
		}
		return value;
	}

	protected long aetherLogicMethod71(long[][] newGrid, long value, long right, long left, long up, long down, int x, int y) {
		if (down < value) {
			// value > down > up > right = left
			long toShare, share;
			int shareCount;
			toShare = value - down;
			shareCount = 5;
			share = toShare/shareCount;
			if (share != 0) {
				changed = true;
				value = value - toShare + toShare%shareCount + share;
				addDown(newGrid, x, y, share);
				addUp(newGrid, x, y, share);
				addRight(newGrid, x, y, share);
				addLeft(newGrid, x, y, share);
			}
			toShare = value - up;
			shareCount = 4;
			share = toShare/shareCount;
			if (share != 0) {
				changed = true;
				value = value - toShare + toShare%shareCount + share;
				addUp(newGrid, x, y, share);
				addRight(newGrid, x, y, share);
				addLeft(newGrid, x, y, share);
			}
			toShare = value - right;
			shareCount = 3;
			share = toShare/shareCount;
			if (share != 0) {
				changed = true;
				value = value - toShare + toShare%shareCount + share;
				addRight(newGrid, x, y, share);
				addLeft(newGrid, x, y, share);
			}
		} else {//down >= value
			// value > up > right = left
			long toShare, share;
			int shareCount;
			toShare = value - up;
			shareCount = 4;
			share = toShare/shareCount;
			if (share != 0) {
				changed = true;
				value = value - toShare + toShare%shareCount + share;
				addUp(newGrid, x, y, share);
				addRight(newGrid, x, y, share);
				addLeft(newGrid, x, y, share);
			}
			toShare = value - right;
			shareCount = 3;
			share = toShare/shareCount;
			if (share != 0) {
				changed = true;
				value = value - toShare + toShare%shareCount + share;
				addRight(newGrid, x, y, share);
				addLeft(newGrid, x, y, share);
			}
		}
		return value;
	}

	protected long aetherLogicMethod65(long[][] newGrid, long value, long right, long left, long up, long down, int x, int y) {
		if (down < up) {
			// value > right = left > up > down
			long toShare, share;
			int shareCount;
			toShare = value - right;
			shareCount = 5;
			share = toShare/shareCount;
			if (share != 0) {
				changed = true;
				value = value - toShare + toShare%shareCount + share;
				addRight(newGrid, x, y, share);
				addLeft(newGrid, x, y, share);
				addUp(newGrid, x, y, share);
				addDown(newGrid, x, y, share);
			}
			toShare = value - up;
			shareCount = 3;
			share = toShare/shareCount;
			if (share != 0) {
				changed = true;
				value = value - toShare + toShare%shareCount + share;
				addUp(newGrid, x, y, share);
				addDown(newGrid, x, y, share);
			}
			toShare = value - down;
			shareCount = 2;
			share = toShare/shareCount;
			if (share != 0) {
				changed = true;
				value = value - toShare + toShare%shareCount + share;
				addDown(newGrid, x, y, share);
			}
		} else if (down > up) {
			value = aetherLogicMethod66(newGrid, value, right, left, up, down, x, y);
		} else {//down == up
			// value > right = left > up = down
			long toShare, share;
			int shareCount;
			toShare = value - right;
			shareCount = 5;
			share = toShare/shareCount;
			if (share != 0) {
				changed = true;
				value = value - toShare + toShare%shareCount + share;
				addRight(newGrid, x, y, share);
				addLeft(newGrid, x, y, share);
				addUp(newGrid, x, y, share);
				addDown(newGrid, x, y, share);
			}
			toShare = value - up;
			shareCount = 3;
			share = toShare/shareCount;
			if (share != 0) {
				changed = true;
				value = value - toShare + toShare%shareCount + share;
				addUp(newGrid, x, y, share);
				addDown(newGrid, x, y, share);
			}
		}
		return value;
	}

	protected long aetherLogicMethod66(long[][] newGrid, long value, long right, long left, long up, long down, int x, int y) {
		if (down < right) {
			// value > right = left > down > up
			long toShare, share;
			int shareCount;
			toShare = value - right;
			shareCount = 5;
			share = toShare/shareCount;
			if (share != 0) {
				changed = true;
				value = value - toShare + toShare%shareCount + share;
				addRight(newGrid, x, y, share);
				addLeft(newGrid, x, y, share);
				addDown(newGrid, x, y, share);
				addUp(newGrid, x, y, share);
			}
			toShare = value - down;
			shareCount = 3;
			share = toShare/shareCount;
			if (share != 0) {
				changed = true;
				value = value - toShare + toShare%shareCount + share;
				addDown(newGrid, x, y, share);
				addUp(newGrid, x, y, share);
			}
			toShare = value - up;
			shareCount = 2;
			share = toShare/shareCount;
			if (share != 0) {
				changed = true;
				value = value - toShare + toShare%shareCount + share;
				addUp(newGrid, x, y, share);
			}
		} else if (down > right) {
			value = aetherLogicMethod67(newGrid, value, right, left, up, down, x, y);
		} else {//down == right
			// value > right = left = down > up
			long toShare, share;
			int shareCount;
			toShare = value - right;
			shareCount = 5;
			share = toShare/shareCount;
			if (share != 0) {
				changed = true;
				value = value - toShare + toShare%shareCount + share;
				addRight(newGrid, x, y, share);
				addLeft(newGrid, x, y, share);
				addDown(newGrid, x, y, share);
				addUp(newGrid, x, y, share);
			}
			toShare = value - up;
			shareCount = 2;
			share = toShare/shareCount;
			if (share != 0) {
				changed = true;
				value = value - toShare + toShare%shareCount + share;
				addUp(newGrid, x, y, share);
			}
		}
		return value;
	}

	protected long aetherLogicMethod67(long[][] newGrid, long value, long right, long left, long up, long down, int x, int y) {
		if (down < value) {
			// value > down > right = left > up
			long toShare, share;
			int shareCount;
			toShare = value - down;
			shareCount = 5;
			share = toShare/shareCount;
			if (share != 0) {
				changed = true;
				value = value - toShare + toShare%shareCount + share;
				addDown(newGrid, x, y, share);
				addRight(newGrid, x, y, share);
				addLeft(newGrid, x, y, share);
				addUp(newGrid, x, y, share);
			}
			toShare = value - right;
			shareCount = 4;
			share = toShare/shareCount;
			if (share != 0) {
				changed = true;
				value = value - toShare + toShare%shareCount + share;
				addRight(newGrid, x, y, share);
				addLeft(newGrid, x, y, share);
				addUp(newGrid, x, y, share);
			}
			toShare = value - up;
			shareCount = 2;
			share = toShare/shareCount;
			if (share != 0) {
				changed = true;
				value = value - toShare + toShare%shareCount + share;
				addUp(newGrid, x, y, share);
			}
		} else {//down >= value
			// value > right = left > up
			long toShare, share;
			int shareCount;
			toShare = value - right;
			shareCount = 4;
			share = toShare/shareCount;
			if (share != 0) {
				changed = true;
				value = value - toShare + toShare%shareCount + share;
				addRight(newGrid, x, y, share);
				addLeft(newGrid, x, y, share);
				addUp(newGrid, x, y, share);
			}
			toShare = value - up;
			shareCount = 2;
			share = toShare/shareCount;
			if (share != 0) {
				changed = true;
				value = value - toShare + toShare%shareCount + share;
				addUp(newGrid, x, y, share);
			}
		}
		return value;
	}

	protected long aetherLogicMethod27(long[][] newGrid, long value, long right, long left, long up, long down, int x, int y) {
		if (left < value) {
			value = aetherLogicMethod28(newGrid, value, right, left, up, down, x, y);
		} else {//left >= value
			value = aetherLogicMethod52(newGrid, value, right, left, up, down, x, y);
		}
		return value;
	}

	protected long aetherLogicMethod52(long[][] newGrid, long value, long right, long left, long up, long down, int x, int y) {
		if (up < right) {
			value = aetherLogicMethod53(newGrid, value, right, left, up, down, x, y);
		} else if (up > right) {
			value = aetherLogicMethod56(newGrid, value, right, left, up, down, x, y);
		} else {//up == right
			value = aetherLogicMethod62(newGrid, value, right, left, up, down, x, y);
		}
		return value;
	}

	protected long aetherLogicMethod62(long[][] newGrid, long value, long right, long left, long up, long down, int x, int y) {
		if (down < right) {
			// value > right = up > down
			long toShare, share;
			int shareCount;
			toShare = value - right;
			shareCount = 4;
			share = toShare/shareCount;
			if (share != 0) {
				changed = true;
				value = value - toShare + toShare%shareCount + share;
				addRight(newGrid, x, y, share);
				addUp(newGrid, x, y, share);
				addDown(newGrid, x, y, share);
			}
			toShare = value - down;
			shareCount = 2;
			share = toShare/shareCount;
			if (share != 0) {
				changed = true;
				value = value - toShare + toShare%shareCount + share;
				addDown(newGrid, x, y, share);
			}
		} else if (down > right) {
			value = aetherLogicMethod63(newGrid, value, right, left, up, down, x, y);
		} else {//down == right
			// value > right = up = down
			long toShare, share;
			int shareCount;
			toShare = value - right;
			shareCount = 4;
			share = toShare/shareCount;
			if (share != 0) {
				changed = true;
				value = value - toShare + toShare%shareCount + share;
				addRight(newGrid, x, y, share);
				addUp(newGrid, x, y, share);
				addDown(newGrid, x, y, share);
			}
		}
		return value;
	}

	protected long aetherLogicMethod63(long[][] newGrid, long value, long right, long left, long up, long down, int x, int y) {
		if (down < value) {
			// value > down > right = up
			long toShare, share;
			int shareCount;
			toShare = value - down;
			shareCount = 4;
			share = toShare/shareCount;
			if (share != 0) {
				changed = true;
				value = value - toShare + toShare%shareCount + share;
				addDown(newGrid, x, y, share);
				addRight(newGrid, x, y, share);
				addUp(newGrid, x, y, share);
			}
			toShare = value - right;
			shareCount = 3;
			share = toShare/shareCount;
			if (share != 0) {
				changed = true;
				value = value - toShare + toShare%shareCount + share;
				addRight(newGrid, x, y, share);
				addUp(newGrid, x, y, share);
			}
		} else {//down >= value
			// value > right = up
			long toShare, share;
			int shareCount;
			toShare = value - right;
			shareCount = 3;
			share = toShare/shareCount;
			if (share != 0) {
				changed = true;
				value = value - toShare + toShare%shareCount + share;
				addRight(newGrid, x, y, share);
				addUp(newGrid, x, y, share);
			}
		}
		return value;
	}

	protected long aetherLogicMethod56(long[][] newGrid, long value, long right, long left, long up, long down, int x, int y) {
		if (up < value) {
			value = aetherLogicMethod57(newGrid, value, right, left, up, down, x, y);
		} else {//up >= value
			value = aetherLogicMethod60(newGrid, value, right, left, up, down, x, y);
		}
		return value;
	}

	protected long aetherLogicMethod60(long[][] newGrid, long value, long right, long left, long up, long down, int x, int y) {
		if (down < right) {
			// value > right > down
			long toShare, share;
			int shareCount;
			toShare = value - right;
			shareCount = 3;
			share = toShare/shareCount;
			if (share != 0) {
				changed = true;
				value = value - toShare + toShare%shareCount + share;
				addRight(newGrid, x, y, share);
				addDown(newGrid, x, y, share);
			}
			toShare = value - down;
			shareCount = 2;
			share = toShare/shareCount;
			if (share != 0) {
				changed = true;
				value = value - toShare + toShare%shareCount + share;
				addDown(newGrid, x, y, share);
			}
		} else if (down > right) {
			value = aetherLogicMethod61(newGrid, value, right, left, up, down, x, y);
		} else {//down == right
			// value > right = down
			long toShare, share;
			int shareCount;
			toShare = value - right;
			shareCount = 3;
			share = toShare/shareCount;
			if (share != 0) {
				changed = true;
				value = value - toShare + toShare%shareCount + share;
				addRight(newGrid, x, y, share);
				addDown(newGrid, x, y, share);
			}
		}
		return value;
	}

	protected long aetherLogicMethod61(long[][] newGrid, long value, long right, long left, long up, long down, int x, int y) {
		if (down < value) {
			// value > down > right
			long toShare, share;
			int shareCount;
			toShare = value - down;
			shareCount = 3;
			share = toShare/shareCount;
			if (share != 0) {
				changed = true;
				value = value - toShare + toShare%shareCount + share;
				addDown(newGrid, x, y, share);
				addRight(newGrid, x, y, share);
			}
			toShare = value - right;
			shareCount = 2;
			share = toShare/shareCount;
			if (share != 0) {
				changed = true;
				value = value - toShare + toShare%shareCount + share;
				addRight(newGrid, x, y, share);
			}
		} else {//down >= value
			// value > right
			long toShare, share;
			int shareCount;
			toShare = value - right;
			shareCount = 2;
			share = toShare/shareCount;
			if (share != 0) {
				changed = true;
				value = value - toShare + toShare%shareCount + share;
				addRight(newGrid, x, y, share);
			}
		}
		return value;
	}

	protected long aetherLogicMethod57(long[][] newGrid, long value, long right, long left, long up, long down, int x, int y) {
		if (down < right) {
			// value > up > right > down
			long toShare, share;
			int shareCount;
			toShare = value - up;
			shareCount = 4;
			share = toShare/shareCount;
			if (share != 0) {
				changed = true;
				value = value - toShare + toShare%shareCount + share;
				addUp(newGrid, x, y, share);
				addRight(newGrid, x, y, share);
				addDown(newGrid, x, y, share);
			}
			toShare = value - right;
			shareCount = 3;
			share = toShare/shareCount;
			if (share != 0) {
				changed = true;
				value = value - toShare + toShare%shareCount + share;
				addRight(newGrid, x, y, share);
				addDown(newGrid, x, y, share);
			}
			toShare = value - down;
			shareCount = 2;
			share = toShare/shareCount;
			if (share != 0) {
				changed = true;
				value = value - toShare + toShare%shareCount + share;
				addDown(newGrid, x, y, share);
			}
		} else if (down > right) {
			value = aetherLogicMethod58(newGrid, value, right, left, up, down, x, y);
		} else {//down == right
			// value > up > right = down
			long toShare, share;
			int shareCount;
			toShare = value - up;
			shareCount = 4;
			share = toShare/shareCount;
			if (share != 0) {
				changed = true;
				value = value - toShare + toShare%shareCount + share;
				addUp(newGrid, x, y, share);
				addRight(newGrid, x, y, share);
				addDown(newGrid, x, y, share);
			}
			toShare = value - right;
			shareCount = 3;
			share = toShare/shareCount;
			if (share != 0) {
				changed = true;
				value = value - toShare + toShare%shareCount + share;
				addRight(newGrid, x, y, share);
				addDown(newGrid, x, y, share);
			}
		}
		return value;
	}

	protected long aetherLogicMethod58(long[][] newGrid, long value, long right, long left, long up, long down, int x, int y) {
		if (down < up) {
			// value > up > down > right
			long toShare, share;
			int shareCount;
			toShare = value - up;
			shareCount = 4;
			share = toShare/shareCount;
			if (share != 0) {
				changed = true;
				value = value - toShare + toShare%shareCount + share;
				addUp(newGrid, x, y, share);
				addDown(newGrid, x, y, share);
				addRight(newGrid, x, y, share);
			}
			toShare = value - down;
			shareCount = 3;
			share = toShare/shareCount;
			if (share != 0) {
				changed = true;
				value = value - toShare + toShare%shareCount + share;
				addDown(newGrid, x, y, share);
				addRight(newGrid, x, y, share);
			}
			toShare = value - right;
			shareCount = 2;
			share = toShare/shareCount;
			if (share != 0) {
				changed = true;
				value = value - toShare + toShare%shareCount + share;
				addRight(newGrid, x, y, share);
			}
		} else if (down > up) {
			value = aetherLogicMethod59(newGrid, value, right, left, up, down, x, y);
		} else {//down == up
			// value > up = down > right
			long toShare, share;
			int shareCount;
			toShare = value - up;
			shareCount = 4;
			share = toShare/shareCount;
			if (share != 0) {
				changed = true;
				value = value - toShare + toShare%shareCount + share;
				addUp(newGrid, x, y, share);
				addDown(newGrid, x, y, share);
				addRight(newGrid, x, y, share);
			}
			toShare = value - right;
			shareCount = 2;
			share = toShare/shareCount;
			if (share != 0) {
				changed = true;
				value = value - toShare + toShare%shareCount + share;
				addRight(newGrid, x, y, share);
			}
		}
		return value;
	}

	protected long aetherLogicMethod59(long[][] newGrid, long value, long right, long left, long up, long down, int x, int y) {
		if (down < value) {
			// value > down > up > right
			long toShare, share;
			int shareCount;
			toShare = value - down;
			shareCount = 4;
			share = toShare/shareCount;
			if (share != 0) {
				changed = true;
				value = value - toShare + toShare%shareCount + share;
				addDown(newGrid, x, y, share);
				addUp(newGrid, x, y, share);
				addRight(newGrid, x, y, share);
			}
			toShare = value - up;
			shareCount = 3;
			share = toShare/shareCount;
			if (share != 0) {
				changed = true;
				value = value - toShare + toShare%shareCount + share;
				addUp(newGrid, x, y, share);
				addRight(newGrid, x, y, share);
			}
			toShare = value - right;
			shareCount = 2;
			share = toShare/shareCount;
			if (share != 0) {
				changed = true;
				value = value - toShare + toShare%shareCount + share;
				addRight(newGrid, x, y, share);
			}
		} else {//down >= value
			// value > up > right
			long toShare, share;
			int shareCount;
			toShare = value - up;
			shareCount = 3;
			share = toShare/shareCount;
			if (share != 0) {
				changed = true;
				value = value - toShare + toShare%shareCount + share;
				addUp(newGrid, x, y, share);
				addRight(newGrid, x, y, share);
			}
			toShare = value - right;
			shareCount = 2;
			share = toShare/shareCount;
			if (share != 0) {
				changed = true;
				value = value - toShare + toShare%shareCount + share;
				addRight(newGrid, x, y, share);
			}
		}
		return value;
	}

	protected long aetherLogicMethod53(long[][] newGrid, long value, long right, long left, long up, long down, int x, int y) {
		if (down < up) {
			// value > right > up > down
			long toShare, share;
			int shareCount;
			toShare = value - right;
			shareCount = 4;
			share = toShare/shareCount;
			if (share != 0) {
				changed = true;
				value = value - toShare + toShare%shareCount + share;
				addRight(newGrid, x, y, share);
				addUp(newGrid, x, y, share);
				addDown(newGrid, x, y, share);
			}
			toShare = value - up;
			shareCount = 3;
			share = toShare/shareCount;
			if (share != 0) {
				changed = true;
				value = value - toShare + toShare%shareCount + share;
				addUp(newGrid, x, y, share);
				addDown(newGrid, x, y, share);
			}
			toShare = value - down;
			shareCount = 2;
			share = toShare/shareCount;
			if (share != 0) {
				changed = true;
				value = value - toShare + toShare%shareCount + share;
				addDown(newGrid, x, y, share);
			}
		} else if (down > up) {
			value = aetherLogicMethod54(newGrid, value, right, left, up, down, x, y);
		} else {//down == up
			// value > right > up = down
			long toShare, share;
			int shareCount;
			toShare = value - right;
			shareCount = 4;
			share = toShare/shareCount;
			if (share != 0) {
				changed = true;
				value = value - toShare + toShare%shareCount + share;
				addRight(newGrid, x, y, share);
				addUp(newGrid, x, y, share);
				addDown(newGrid, x, y, share);
			}
			toShare = value - up;
			shareCount = 3;
			share = toShare/shareCount;
			if (share != 0) {
				changed = true;
				value = value - toShare + toShare%shareCount + share;
				addUp(newGrid, x, y, share);
				addDown(newGrid, x, y, share);
			}
		}
		return value;
	}

	protected long aetherLogicMethod54(long[][] newGrid, long value, long right, long left, long up, long down, int x, int y) {
		if (down < right) {
			// value > right > down > up
			long toShare, share;
			int shareCount;
			toShare = value - right;
			shareCount = 4;
			share = toShare/shareCount;
			if (share != 0) {
				changed = true;
				value = value - toShare + toShare%shareCount + share;
				addRight(newGrid, x, y, share);
				addDown(newGrid, x, y, share);
				addUp(newGrid, x, y, share);
			}
			toShare = value - down;
			shareCount = 3;
			share = toShare/shareCount;
			if (share != 0) {
				changed = true;
				value = value - toShare + toShare%shareCount + share;
				addDown(newGrid, x, y, share);
				addUp(newGrid, x, y, share);
			}
			toShare = value - up;
			shareCount = 2;
			share = toShare/shareCount;
			if (share != 0) {
				changed = true;
				value = value - toShare + toShare%shareCount + share;
				addUp(newGrid, x, y, share);
			}
		} else if (down > right) {
			value = aetherLogicMethod55(newGrid, value, right, left, up, down, x, y);
		} else {//down == right
			// value > right = down > up
			long toShare, share;
			int shareCount;
			toShare = value - right;
			shareCount = 4;
			share = toShare/shareCount;
			if (share != 0) {
				changed = true;
				value = value - toShare + toShare%shareCount + share;
				addRight(newGrid, x, y, share);
				addDown(newGrid, x, y, share);
				addUp(newGrid, x, y, share);
			}
			toShare = value - up;
			shareCount = 2;
			share = toShare/shareCount;
			if (share != 0) {
				changed = true;
				value = value - toShare + toShare%shareCount + share;
				addUp(newGrid, x, y, share);
			}
		}
		return value;
	}

	protected long aetherLogicMethod55(long[][] newGrid, long value, long right, long left, long up, long down, int x, int y) {
		if (down < value) {
			// value > down > right > up
			long toShare, share;
			int shareCount;
			toShare = value - down;
			shareCount = 4;
			share = toShare/shareCount;
			if (share != 0) {
				changed = true;
				value = value - toShare + toShare%shareCount + share;
				addDown(newGrid, x, y, share);
				addRight(newGrid, x, y, share);
				addUp(newGrid, x, y, share);
			}
			toShare = value - right;
			shareCount = 3;
			share = toShare/shareCount;
			if (share != 0) {
				changed = true;
				value = value - toShare + toShare%shareCount + share;
				addRight(newGrid, x, y, share);
				addUp(newGrid, x, y, share);
			}
			toShare = value - up;
			shareCount = 2;
			share = toShare/shareCount;
			if (share != 0) {
				changed = true;
				value = value - toShare + toShare%shareCount + share;
				addUp(newGrid, x, y, share);
			}
		} else {//down >= value
			// value > right > up
			long toShare, share;
			int shareCount;
			toShare = value - right;
			shareCount = 3;
			share = toShare/shareCount;
			if (share != 0) {
				changed = true;
				value = value - toShare + toShare%shareCount + share;
				addRight(newGrid, x, y, share);
				addUp(newGrid, x, y, share);
			}
			toShare = value - up;
			shareCount = 2;
			share = toShare/shareCount;
			if (share != 0) {
				changed = true;
				value = value - toShare + toShare%shareCount + share;
				addUp(newGrid, x, y, share);
			}
		}
		return value;
	}

	protected long aetherLogicMethod28(long[][] newGrid, long value, long right, long left, long up, long down, int x, int y) {
		if (up < right) {
			value = aetherLogicMethod29(newGrid, value, right, left, up, down, x, y);
		} else if (up > right) {
			value = aetherLogicMethod33(newGrid, value, right, left, up, down, x, y);
		} else {//up == right
			value = aetherLogicMethod49(newGrid, value, right, left, up, down, x, y);
		}
		return value;
	}

	protected long aetherLogicMethod49(long[][] newGrid, long value, long right, long left, long up, long down, int x, int y) {
		if (down < right) {
			// value > left > right = up > down
			long toShare, share;
			int shareCount;
			toShare = value - left;
			shareCount = 5;
			share = toShare/shareCount;
			if (share != 0) {
				changed = true;
				value = value - toShare + toShare%shareCount + share;
				addLeft(newGrid, x, y, share);
				addRight(newGrid, x, y, share);
				addUp(newGrid, x, y, share);
				addDown(newGrid, x, y, share);
			}
			toShare = value - right;
			shareCount = 4;
			share = toShare/shareCount;
			if (share != 0) {
				changed = true;
				value = value - toShare + toShare%shareCount + share;
				addRight(newGrid, x, y, share);
				addUp(newGrid, x, y, share);
				addDown(newGrid, x, y, share);
			}
			toShare = value - down;
			shareCount = 2;
			share = toShare/shareCount;
			if (share != 0) {
				changed = true;
				value = value - toShare + toShare%shareCount + share;
				addDown(newGrid, x, y, share);
			}
		} else if (down > right) {
			value = aetherLogicMethod50(newGrid, value, right, left, up, down, x, y);
		} else {//down == right
			// value > left > right = up = down
			long toShare, share;
			int shareCount;
			toShare = value - left;
			shareCount = 5;
			share = toShare/shareCount;
			if (share != 0) {
				changed = true;
				value = value - toShare + toShare%shareCount + share;
				addLeft(newGrid, x, y, share);
				addRight(newGrid, x, y, share);
				addUp(newGrid, x, y, share);
				addDown(newGrid, x, y, share);
			}
			toShare = value - right;
			shareCount = 4;
			share = toShare/shareCount;
			if (share != 0) {
				changed = true;
				value = value - toShare + toShare%shareCount + share;
				addRight(newGrid, x, y, share);
				addUp(newGrid, x, y, share);
				addDown(newGrid, x, y, share);
			}
		}
		return value;
	}

	protected long aetherLogicMethod50(long[][] newGrid, long value, long right, long left, long up, long down, int x, int y) {
		if (down < left) {
			// value > left > down > right = up
			long toShare, share;
			int shareCount;
			toShare = value - left;
			shareCount = 5;
			share = toShare/shareCount;
			if (share != 0) {
				changed = true;
				value = value - toShare + toShare%shareCount + share;
				addLeft(newGrid, x, y, share);
				addDown(newGrid, x, y, share);
				addRight(newGrid, x, y, share);
				addUp(newGrid, x, y, share);
			}
			toShare = value - down;
			shareCount = 4;
			share = toShare/shareCount;
			if (share != 0) {
				changed = true;
				value = value - toShare + toShare%shareCount + share;
				addDown(newGrid, x, y, share);
				addRight(newGrid, x, y, share);
				addUp(newGrid, x, y, share);
			}
			toShare = value - right;
			shareCount = 3;
			share = toShare/shareCount;
			if (share != 0) {
				changed = true;
				value = value - toShare + toShare%shareCount + share;
				addRight(newGrid, x, y, share);
				addUp(newGrid, x, y, share);
			}
		} else if (down > left) {
			value = aetherLogicMethod51(newGrid, value, right, left, up, down, x, y);
		} else {//down == left
			// value > left = down > right = up
			long toShare, share;
			int shareCount;
			toShare = value - left;
			shareCount = 5;
			share = toShare/shareCount;
			if (share != 0) {
				changed = true;
				value = value - toShare + toShare%shareCount + share;
				addLeft(newGrid, x, y, share);
				addDown(newGrid, x, y, share);
				addRight(newGrid, x, y, share);
				addUp(newGrid, x, y, share);
			}
			toShare = value - right;
			shareCount = 3;
			share = toShare/shareCount;
			if (share != 0) {
				changed = true;
				value = value - toShare + toShare%shareCount + share;
				addRight(newGrid, x, y, share);
				addUp(newGrid, x, y, share);
			}
		}
		return value;
	}

	protected long aetherLogicMethod51(long[][] newGrid, long value, long right, long left, long up, long down, int x, int y) {
		if (down < value) {
			// value > down > left > right = up
			long toShare, share;
			int shareCount;
			toShare = value - down;
			shareCount = 5;
			share = toShare/shareCount;
			if (share != 0) {
				changed = true;
				value = value - toShare + toShare%shareCount + share;
				addDown(newGrid, x, y, share);
				addLeft(newGrid, x, y, share);
				addRight(newGrid, x, y, share);
				addUp(newGrid, x, y, share);
			}
			toShare = value - left;
			shareCount = 4;
			share = toShare/shareCount;
			if (share != 0) {
				changed = true;
				value = value - toShare + toShare%shareCount + share;
				addLeft(newGrid, x, y, share);
				addRight(newGrid, x, y, share);
				addUp(newGrid, x, y, share);
			}
			toShare = value - right;
			shareCount = 3;
			share = toShare/shareCount;
			if (share != 0) {
				changed = true;
				value = value - toShare + toShare%shareCount + share;
				addRight(newGrid, x, y, share);
				addUp(newGrid, x, y, share);
			}
		} else {//down >= value
			// value > left > right = up
			long toShare, share;
			int shareCount;
			toShare = value - left;
			shareCount = 4;
			share = toShare/shareCount;
			if (share != 0) {
				changed = true;
				value = value - toShare + toShare%shareCount + share;
				addLeft(newGrid, x, y, share);
				addRight(newGrid, x, y, share);
				addUp(newGrid, x, y, share);
			}
			toShare = value - right;
			shareCount = 3;
			share = toShare/shareCount;
			if (share != 0) {
				changed = true;
				value = value - toShare + toShare%shareCount + share;
				addRight(newGrid, x, y, share);
				addUp(newGrid, x, y, share);
			}
		}
		return value;
	}

	protected long aetherLogicMethod33(long[][] newGrid, long value, long right, long left, long up, long down, int x, int y) {
		if (up < left) {
			value = aetherLogicMethod34(newGrid, value, right, left, up, down, x, y);
		} else if (up > left) {
			value = aetherLogicMethod38(newGrid, value, right, left, up, down, x, y);
		} else {//up == left
			value = aetherLogicMethod46(newGrid, value, right, left, up, down, x, y);
		}
		return value;
	}

	protected long aetherLogicMethod46(long[][] newGrid, long value, long right, long left, long up, long down, int x, int y) {
		if (down < right) {
			// value > left = up > right > down
			long toShare, share;
			int shareCount;
			toShare = value - left;
			shareCount = 5;
			share = toShare/shareCount;
			if (share != 0) {
				changed = true;
				value = value - toShare + toShare%shareCount + share;
				addLeft(newGrid, x, y, share);
				addUp(newGrid, x, y, share);
				addRight(newGrid, x, y, share);
				addDown(newGrid, x, y, share);
			}
			toShare = value - right;
			shareCount = 3;
			share = toShare/shareCount;
			if (share != 0) {
				changed = true;
				value = value - toShare + toShare%shareCount + share;
				addRight(newGrid, x, y, share);
				addDown(newGrid, x, y, share);
			}
			toShare = value - down;
			shareCount = 2;
			share = toShare/shareCount;
			if (share != 0) {
				changed = true;
				value = value - toShare + toShare%shareCount + share;
				addDown(newGrid, x, y, share);
			}
		} else if (down > right) {
			value = aetherLogicMethod47(newGrid, value, right, left, up, down, x, y);
		} else {//down == right
			// value > left = up > right = down
			long toShare, share;
			int shareCount;
			toShare = value - left;
			shareCount = 5;
			share = toShare/shareCount;
			if (share != 0) {
				changed = true;
				value = value - toShare + toShare%shareCount + share;
				addLeft(newGrid, x, y, share);
				addUp(newGrid, x, y, share);
				addRight(newGrid, x, y, share);
				addDown(newGrid, x, y, share);
			}
			toShare = value - right;
			shareCount = 3;
			share = toShare/shareCount;
			if (share != 0) {
				changed = true;
				value = value - toShare + toShare%shareCount + share;
				addRight(newGrid, x, y, share);
				addDown(newGrid, x, y, share);
			}
		}
		return value;
	}

	protected long aetherLogicMethod47(long[][] newGrid, long value, long right, long left, long up, long down, int x, int y) {
		if (down < left) {
			// value > left = up > down > right
			long toShare, share;
			int shareCount;
			toShare = value - left;
			shareCount = 5;
			share = toShare/shareCount;
			if (share != 0) {
				changed = true;
				value = value - toShare + toShare%shareCount + share;
				addLeft(newGrid, x, y, share);
				addUp(newGrid, x, y, share);
				addDown(newGrid, x, y, share);
				addRight(newGrid, x, y, share);
			}
			toShare = value - down;
			shareCount = 3;
			share = toShare/shareCount;
			if (share != 0) {
				changed = true;
				value = value - toShare + toShare%shareCount + share;
				addDown(newGrid, x, y, share);
				addRight(newGrid, x, y, share);
			}
			toShare = value - right;
			shareCount = 2;
			share = toShare/shareCount;
			if (share != 0) {
				changed = true;
				value = value - toShare + toShare%shareCount + share;
				addRight(newGrid, x, y, share);
			}
		} else if (down > left) {
			value = aetherLogicMethod48(newGrid, value, right, left, up, down, x, y);
		} else {//down == left
			// value > left = up = down > right
			long toShare, share;
			int shareCount;
			toShare = value - left;
			shareCount = 5;
			share = toShare/shareCount;
			if (share != 0) {
				changed = true;
				value = value - toShare + toShare%shareCount + share;
				addLeft(newGrid, x, y, share);
				addUp(newGrid, x, y, share);
				addDown(newGrid, x, y, share);
				addRight(newGrid, x, y, share);
			}
			toShare = value - right;
			shareCount = 2;
			share = toShare/shareCount;
			if (share != 0) {
				changed = true;
				value = value - toShare + toShare%shareCount + share;
				addRight(newGrid, x, y, share);
			}
		}
		return value;
	}

	protected long aetherLogicMethod48(long[][] newGrid, long value, long right, long left, long up, long down, int x, int y) {
		if (down < value) {
			// value > down > left = up > right
			long toShare, share;
			int shareCount;
			toShare = value - down;
			shareCount = 5;
			share = toShare/shareCount;
			if (share != 0) {
				changed = true;
				value = value - toShare + toShare%shareCount + share;
				addDown(newGrid, x, y, share);
				addLeft(newGrid, x, y, share);
				addUp(newGrid, x, y, share);
				addRight(newGrid, x, y, share);
			}
			toShare = value - left;
			shareCount = 4;
			share = toShare/shareCount;
			if (share != 0) {
				changed = true;
				value = value - toShare + toShare%shareCount + share;
				addLeft(newGrid, x, y, share);
				addUp(newGrid, x, y, share);
				addRight(newGrid, x, y, share);
			}
			toShare = value - right;
			shareCount = 2;
			share = toShare/shareCount;
			if (share != 0) {
				changed = true;
				value = value - toShare + toShare%shareCount + share;
				addRight(newGrid, x, y, share);
			}
		} else {//down >= value
			// value > left = up > right
			long toShare, share;
			int shareCount;
			toShare = value - left;
			shareCount = 4;
			share = toShare/shareCount;
			if (share != 0) {
				changed = true;
				value = value - toShare + toShare%shareCount + share;
				addLeft(newGrid, x, y, share);
				addUp(newGrid, x, y, share);
				addRight(newGrid, x, y, share);
			}
			toShare = value - right;
			shareCount = 2;
			share = toShare/shareCount;
			if (share != 0) {
				changed = true;
				value = value - toShare + toShare%shareCount + share;
				addRight(newGrid, x, y, share);
			}
		}
		return value;
	}

	protected long aetherLogicMethod38(long[][] newGrid, long value, long right, long left, long up, long down, int x, int y) {
		if (up < value) {
			value = aetherLogicMethod39(newGrid, value, right, left, up, down, x, y);
		} else {//up >= value
			value = aetherLogicMethod43(newGrid, value, right, left, up, down, x, y);
		}
		return value;
	}

	protected long aetherLogicMethod43(long[][] newGrid, long value, long right, long left, long up, long down, int x, int y) {
		if (down < right) {
			// value > left > right > down
			long toShare, share;
			int shareCount;
			toShare = value - left;
			shareCount = 4;
			share = toShare/shareCount;
			if (share != 0) {
				changed = true;
				value = value - toShare + toShare%shareCount + share;
				addLeft(newGrid, x, y, share);
				addRight(newGrid, x, y, share);
				addDown(newGrid, x, y, share);
			}
			toShare = value - right;
			shareCount = 3;
			share = toShare/shareCount;
			if (share != 0) {
				changed = true;
				value = value - toShare + toShare%shareCount + share;
				addRight(newGrid, x, y, share);
				addDown(newGrid, x, y, share);
			}
			toShare = value - down;
			shareCount = 2;
			share = toShare/shareCount;
			if (share != 0) {
				changed = true;
				value = value - toShare + toShare%shareCount + share;
				addDown(newGrid, x, y, share);
			}
		} else if (down > right) {
			value = aetherLogicMethod44(newGrid, value, right, left, up, down, x, y);
		} else {//down == right
			// value > left > right = down
			long toShare, share;
			int shareCount;
			toShare = value - left;
			shareCount = 4;
			share = toShare/shareCount;
			if (share != 0) {
				changed = true;
				value = value - toShare + toShare%shareCount + share;
				addLeft(newGrid, x, y, share);
				addRight(newGrid, x, y, share);
				addDown(newGrid, x, y, share);
			}
			toShare = value - right;
			shareCount = 3;
			share = toShare/shareCount;
			if (share != 0) {
				changed = true;
				value = value - toShare + toShare%shareCount + share;
				addRight(newGrid, x, y, share);
				addDown(newGrid, x, y, share);
			}
		}
		return value;
	}

	protected long aetherLogicMethod44(long[][] newGrid, long value, long right, long left, long up, long down, int x, int y) {
		if (down < left) {
			// value > left > down > right
			long toShare, share;
			int shareCount;
			toShare = value - left;
			shareCount = 4;
			share = toShare/shareCount;
			if (share != 0) {
				changed = true;
				value = value - toShare + toShare%shareCount + share;
				addLeft(newGrid, x, y, share);
				addDown(newGrid, x, y, share);
				addRight(newGrid, x, y, share);
			}
			toShare = value - down;
			shareCount = 3;
			share = toShare/shareCount;
			if (share != 0) {
				changed = true;
				value = value - toShare + toShare%shareCount + share;
				addDown(newGrid, x, y, share);
				addRight(newGrid, x, y, share);
			}
			toShare = value - right;
			shareCount = 2;
			share = toShare/shareCount;
			if (share != 0) {
				changed = true;
				value = value - toShare + toShare%shareCount + share;
				addRight(newGrid, x, y, share);
			}
		} else if (down > left) {
			value = aetherLogicMethod45(newGrid, value, right, left, up, down, x, y);
		} else {//down == left
			// value > left = down > right
			long toShare, share;
			int shareCount;
			toShare = value - left;
			shareCount = 4;
			share = toShare/shareCount;
			if (share != 0) {
				changed = true;
				value = value - toShare + toShare%shareCount + share;
				addLeft(newGrid, x, y, share);
				addDown(newGrid, x, y, share);
				addRight(newGrid, x, y, share);
			}
			toShare = value - right;
			shareCount = 2;
			share = toShare/shareCount;
			if (share != 0) {
				changed = true;
				value = value - toShare + toShare%shareCount + share;
				addRight(newGrid, x, y, share);
			}
		}
		return value;
	}

	protected long aetherLogicMethod45(long[][] newGrid, long value, long right, long left, long up, long down, int x, int y) {
		if (down < value) {
			// value > down > left > right
			long toShare, share;
			int shareCount;
			toShare = value - down;
			shareCount = 4;
			share = toShare/shareCount;
			if (share != 0) {
				changed = true;
				value = value - toShare + toShare%shareCount + share;
				addDown(newGrid, x, y, share);
				addLeft(newGrid, x, y, share);
				addRight(newGrid, x, y, share);
			}
			toShare = value - left;
			shareCount = 3;
			share = toShare/shareCount;
			if (share != 0) {
				changed = true;
				value = value - toShare + toShare%shareCount + share;
				addLeft(newGrid, x, y, share);
				addRight(newGrid, x, y, share);
			}
			toShare = value - right;
			shareCount = 2;
			share = toShare/shareCount;
			if (share != 0) {
				changed = true;
				value = value - toShare + toShare%shareCount + share;
				addRight(newGrid, x, y, share);
			}
		} else {//down >= value
			// value > left > right
			long toShare, share;
			int shareCount;
			toShare = value - left;
			shareCount = 3;
			share = toShare/shareCount;
			if (share != 0) {
				changed = true;
				value = value - toShare + toShare%shareCount + share;
				addLeft(newGrid, x, y, share);
				addRight(newGrid, x, y, share);
			}
			toShare = value - right;
			shareCount = 2;
			share = toShare/shareCount;
			if (share != 0) {
				changed = true;
				value = value - toShare + toShare%shareCount + share;
				addRight(newGrid, x, y, share);
			}
		}
		return value;
	}

	protected long aetherLogicMethod39(long[][] newGrid, long value, long right, long left, long up, long down, int x, int y) {
		if (down < right) {
			// value > up > left > right > down
			long toShare, share;
			int shareCount;
			toShare = value - up;
			shareCount = 5;
			share = toShare/shareCount;
			if (share != 0) {
				changed = true;
				value = value - toShare + toShare%shareCount + share;
				addUp(newGrid, x, y, share);
				addLeft(newGrid, x, y, share);
				addRight(newGrid, x, y, share);
				addDown(newGrid, x, y, share);
			}
			toShare = value - left;
			shareCount = 4;
			share = toShare/shareCount;
			if (share != 0) {
				changed = true;
				value = value - toShare + toShare%shareCount + share;
				addLeft(newGrid, x, y, share);
				addRight(newGrid, x, y, share);
				addDown(newGrid, x, y, share);
			}
			toShare = value - right;
			shareCount = 3;
			share = toShare/shareCount;
			if (share != 0) {
				changed = true;
				value = value - toShare + toShare%shareCount + share;
				addRight(newGrid, x, y, share);
				addDown(newGrid, x, y, share);
			}
			toShare = value - down;
			shareCount = 2;
			share = toShare/shareCount;
			if (share != 0) {
				changed = true;
				value = value - toShare + toShare%shareCount + share;
				addDown(newGrid, x, y, share);
			}
		} else if (down > right) {
			value = aetherLogicMethod40(newGrid, value, right, left, up, down, x, y);
		} else {//down == right
			// value > up > left > right = down
			long toShare, share;
			int shareCount;
			toShare = value - up;
			shareCount = 5;
			share = toShare/shareCount;
			if (share != 0) {
				changed = true;
				value = value - toShare + toShare%shareCount + share;
				addUp(newGrid, x, y, share);
				addLeft(newGrid, x, y, share);
				addRight(newGrid, x, y, share);
				addDown(newGrid, x, y, share);
			}
			toShare = value - left;
			shareCount = 4;
			share = toShare/shareCount;
			if (share != 0) {
				changed = true;
				value = value - toShare + toShare%shareCount + share;
				addLeft(newGrid, x, y, share);
				addRight(newGrid, x, y, share);
				addDown(newGrid, x, y, share);
			}
			toShare = value - right;
			shareCount = 3;
			share = toShare/shareCount;
			if (share != 0) {
				changed = true;
				value = value - toShare + toShare%shareCount + share;
				addRight(newGrid, x, y, share);
				addDown(newGrid, x, y, share);
			}
		}
		return value;
	}

	protected long aetherLogicMethod40(long[][] newGrid, long value, long right, long left, long up, long down, int x, int y) {
		if (down < left) {
			// value > up > left > down > right
			long toShare, share;
			int shareCount;
			toShare = value - up;
			shareCount = 5;
			share = toShare/shareCount;
			if (share != 0) {
				changed = true;
				value = value - toShare + toShare%shareCount + share;
				addUp(newGrid, x, y, share);
				addLeft(newGrid, x, y, share);
				addDown(newGrid, x, y, share);
				addRight(newGrid, x, y, share);
			}
			toShare = value - left;
			shareCount = 4;
			share = toShare/shareCount;
			if (share != 0) {
				changed = true;
				value = value - toShare + toShare%shareCount + share;
				addLeft(newGrid, x, y, share);
				addDown(newGrid, x, y, share);
				addRight(newGrid, x, y, share);
			}
			toShare = value - down;
			shareCount = 3;
			share = toShare/shareCount;
			if (share != 0) {
				changed = true;
				value = value - toShare + toShare%shareCount + share;
				addDown(newGrid, x, y, share);
				addRight(newGrid, x, y, share);
			}
			toShare = value - right;
			shareCount = 2;
			share = toShare/shareCount;
			if (share != 0) {
				changed = true;
				value = value - toShare + toShare%shareCount + share;
				addRight(newGrid, x, y, share);
			}
		} else if (down > left) {
			value = aetherLogicMethod41(newGrid, value, right, left, up, down, x, y);
		} else {//down == left
			// value > up > left = down > right
			long toShare, share;
			int shareCount;
			toShare = value - up;
			shareCount = 5;
			share = toShare/shareCount;
			if (share != 0) {
				changed = true;
				value = value - toShare + toShare%shareCount + share;
				addUp(newGrid, x, y, share);
				addLeft(newGrid, x, y, share);
				addDown(newGrid, x, y, share);
				addRight(newGrid, x, y, share);
			}
			toShare = value - left;
			shareCount = 4;
			share = toShare/shareCount;
			if (share != 0) {
				changed = true;
				value = value - toShare + toShare%shareCount + share;
				addLeft(newGrid, x, y, share);
				addDown(newGrid, x, y, share);
				addRight(newGrid, x, y, share);
			}
			toShare = value - right;
			shareCount = 2;
			share = toShare/shareCount;
			if (share != 0) {
				changed = true;
				value = value - toShare + toShare%shareCount + share;
				addRight(newGrid, x, y, share);
			}
		}
		return value;
	}

	protected long aetherLogicMethod41(long[][] newGrid, long value, long right, long left, long up, long down, int x, int y) {
		if (down < up) {
			// value > up > down > left > right
			long toShare, share;
			int shareCount;
			toShare = value - up;
			shareCount = 5;
			share = toShare/shareCount;
			if (share != 0) {
				changed = true;
				value = value - toShare + toShare%shareCount + share;
				addUp(newGrid, x, y, share);
				addDown(newGrid, x, y, share);
				addLeft(newGrid, x, y, share);
				addRight(newGrid, x, y, share);
			}
			toShare = value - down;
			shareCount = 4;
			share = toShare/shareCount;
			if (share != 0) {
				changed = true;
				value = value - toShare + toShare%shareCount + share;
				addDown(newGrid, x, y, share);
				addLeft(newGrid, x, y, share);
				addRight(newGrid, x, y, share);
			}
			toShare = value - left;
			shareCount = 3;
			share = toShare/shareCount;
			if (share != 0) {
				changed = true;
				value = value - toShare + toShare%shareCount + share;
				addLeft(newGrid, x, y, share);
				addRight(newGrid, x, y, share);
			}
			toShare = value - right;
			shareCount = 2;
			share = toShare/shareCount;
			if (share != 0) {
				changed = true;
				value = value - toShare + toShare%shareCount + share;
				addRight(newGrid, x, y, share);
			}
		} else if (down > up) {
			value = aetherLogicMethod42(newGrid, value, right, left, up, down, x, y);
		} else {//down == up
			// value > up = down > left > right
			long toShare, share;
			int shareCount;
			toShare = value - up;
			shareCount = 5;
			share = toShare/shareCount;
			if (share != 0) {
				changed = true;
				value = value - toShare + toShare%shareCount + share;
				addUp(newGrid, x, y, share);
				addDown(newGrid, x, y, share);
				addLeft(newGrid, x, y, share);
				addRight(newGrid, x, y, share);
			}
			toShare = value - left;
			shareCount = 3;
			share = toShare/shareCount;
			if (share != 0) {
				changed = true;
				value = value - toShare + toShare%shareCount + share;
				addLeft(newGrid, x, y, share);
				addRight(newGrid, x, y, share);
			}
			toShare = value - right;
			shareCount = 2;
			share = toShare/shareCount;
			if (share != 0) {
				changed = true;
				value = value - toShare + toShare%shareCount + share;
				addRight(newGrid, x, y, share);
			}
		}
		return value;
	}

	protected long aetherLogicMethod42(long[][] newGrid, long value, long right, long left, long up, long down, int x, int y) {
		if (down < value) {
			// value > down > up > left > right
			long toShare, share;
			int shareCount;
			toShare = value - down;
			shareCount = 5;
			share = toShare/shareCount;
			if (share != 0) {
				changed = true;
				value = value - toShare + toShare%shareCount + share;
				addDown(newGrid, x, y, share);
				addUp(newGrid, x, y, share);
				addLeft(newGrid, x, y, share);
				addRight(newGrid, x, y, share);
			}
			toShare = value - up;
			shareCount = 4;
			share = toShare/shareCount;
			if (share != 0) {
				changed = true;
				value = value - toShare + toShare%shareCount + share;
				addUp(newGrid, x, y, share);
				addLeft(newGrid, x, y, share);
				addRight(newGrid, x, y, share);
			}
			toShare = value - left;
			shareCount = 3;
			share = toShare/shareCount;
			if (share != 0) {
				changed = true;
				value = value - toShare + toShare%shareCount + share;
				addLeft(newGrid, x, y, share);
				addRight(newGrid, x, y, share);
			}
			toShare = value - right;
			shareCount = 2;
			share = toShare/shareCount;
			if (share != 0) {
				changed = true;
				value = value - toShare + toShare%shareCount + share;
				addRight(newGrid, x, y, share);
			}
		} else {//down >= value
			// value > up > left > right
			long toShare, share;
			int shareCount;
			toShare = value - up;
			shareCount = 4;
			share = toShare/shareCount;
			if (share != 0) {
				changed = true;
				value = value - toShare + toShare%shareCount + share;
				addUp(newGrid, x, y, share);
				addLeft(newGrid, x, y, share);
				addRight(newGrid, x, y, share);
			}
			toShare = value - left;
			shareCount = 3;
			share = toShare/shareCount;
			if (share != 0) {
				changed = true;
				value = value - toShare + toShare%shareCount + share;
				addLeft(newGrid, x, y, share);
				addRight(newGrid, x, y, share);
			}
			toShare = value - right;
			shareCount = 2;
			share = toShare/shareCount;
			if (share != 0) {
				changed = true;
				value = value - toShare + toShare%shareCount + share;
				addRight(newGrid, x, y, share);
			}
		}
		return value;
	}

	protected long aetherLogicMethod34(long[][] newGrid, long value, long right, long left, long up, long down, int x, int y) {
		if (down < right) {
			// value > left > up > right > down
			long toShare, share;
			int shareCount;
			toShare = value - left;
			shareCount = 5;
			share = toShare/shareCount;
			if (share != 0) {
				changed = true;
				value = value - toShare + toShare%shareCount + share;
				addLeft(newGrid, x, y, share);
				addUp(newGrid, x, y, share);
				addRight(newGrid, x, y, share);
				addDown(newGrid, x, y, share);
			}
			toShare = value - up;
			shareCount = 4;
			share = toShare/shareCount;
			if (share != 0) {
				changed = true;
				value = value - toShare + toShare%shareCount + share;
				addUp(newGrid, x, y, share);
				addRight(newGrid, x, y, share);
				addDown(newGrid, x, y, share);
			}
			toShare = value - right;
			shareCount = 3;
			share = toShare/shareCount;
			if (share != 0) {
				changed = true;
				value = value - toShare + toShare%shareCount + share;
				addRight(newGrid, x, y, share);
				addDown(newGrid, x, y, share);
			}
			toShare = value - down;
			shareCount = 2;
			share = toShare/shareCount;
			if (share != 0) {
				changed = true;
				value = value - toShare + toShare%shareCount + share;
				addDown(newGrid, x, y, share);
			}
		} else if (down > right) {
			value = aetherLogicMethod35(newGrid, value, right, left, up, down, x, y);
		} else {//down == right
			// value > left > up > right = down
			long toShare, share;
			int shareCount;
			toShare = value - left;
			shareCount = 5;
			share = toShare/shareCount;
			if (share != 0) {
				changed = true;
				value = value - toShare + toShare%shareCount + share;
				addLeft(newGrid, x, y, share);
				addUp(newGrid, x, y, share);
				addRight(newGrid, x, y, share);
				addDown(newGrid, x, y, share);
			}
			toShare = value - up;
			shareCount = 4;
			share = toShare/shareCount;
			if (share != 0) {
				changed = true;
				value = value - toShare + toShare%shareCount + share;
				addUp(newGrid, x, y, share);
				addRight(newGrid, x, y, share);
				addDown(newGrid, x, y, share);
			}
			toShare = value - right;
			shareCount = 3;
			share = toShare/shareCount;
			if (share != 0) {
				changed = true;
				value = value - toShare + toShare%shareCount + share;
				addRight(newGrid, x, y, share);
				addDown(newGrid, x, y, share);
			}
		}
		return value;
	}

	protected long aetherLogicMethod35(long[][] newGrid, long value, long right, long left, long up, long down, int x, int y) {
		if (down < up) {
			// value > left > up > down > right
			long toShare, share;
			int shareCount;
			toShare = value - left;
			shareCount = 5;
			share = toShare/shareCount;
			if (share != 0) {
				changed = true;
				value = value - toShare + toShare%shareCount + share;
				addLeft(newGrid, x, y, share);
				addUp(newGrid, x, y, share);
				addDown(newGrid, x, y, share);
				addRight(newGrid, x, y, share);
			}
			toShare = value - up;
			shareCount = 4;
			share = toShare/shareCount;
			if (share != 0) {
				changed = true;
				value = value - toShare + toShare%shareCount + share;
				addUp(newGrid, x, y, share);
				addDown(newGrid, x, y, share);
				addRight(newGrid, x, y, share);
			}
			toShare = value - down;
			shareCount = 3;
			share = toShare/shareCount;
			if (share != 0) {
				changed = true;
				value = value - toShare + toShare%shareCount + share;
				addDown(newGrid, x, y, share);
				addRight(newGrid, x, y, share);
			}
			toShare = value - right;
			shareCount = 2;
			share = toShare/shareCount;
			if (share != 0) {
				changed = true;
				value = value - toShare + toShare%shareCount + share;
				addRight(newGrid, x, y, share);
			}
		} else if (down > up) {
			value = aetherLogicMethod36(newGrid, value, right, left, up, down, x, y);
		} else {//down == up
			// value > left > up = down > right
			long toShare, share;
			int shareCount;
			toShare = value - left;
			shareCount = 5;
			share = toShare/shareCount;
			if (share != 0) {
				changed = true;
				value = value - toShare + toShare%shareCount + share;
				addLeft(newGrid, x, y, share);
				addUp(newGrid, x, y, share);
				addDown(newGrid, x, y, share);
				addRight(newGrid, x, y, share);
			}
			toShare = value - up;
			shareCount = 4;
			share = toShare/shareCount;
			if (share != 0) {
				changed = true;
				value = value - toShare + toShare%shareCount + share;
				addUp(newGrid, x, y, share);
				addDown(newGrid, x, y, share);
				addRight(newGrid, x, y, share);
			}
			toShare = value - right;
			shareCount = 2;
			share = toShare/shareCount;
			if (share != 0) {
				changed = true;
				value = value - toShare + toShare%shareCount + share;
				addRight(newGrid, x, y, share);
			}
		}
		return value;
	}

	protected long aetherLogicMethod36(long[][] newGrid, long value, long right, long left, long up, long down, int x, int y) {
		if (down < left) {
			// value > left > down > up > right
			long toShare, share;
			int shareCount;
			toShare = value - left;
			shareCount = 5;
			share = toShare/shareCount;
			if (share != 0) {
				changed = true;
				value = value - toShare + toShare%shareCount + share;
				addLeft(newGrid, x, y, share);
				addDown(newGrid, x, y, share);
				addUp(newGrid, x, y, share);
				addRight(newGrid, x, y, share);
			}
			toShare = value - down;
			shareCount = 4;
			share = toShare/shareCount;
			if (share != 0) {
				changed = true;
				value = value - toShare + toShare%shareCount + share;
				addDown(newGrid, x, y, share);
				addUp(newGrid, x, y, share);
				addRight(newGrid, x, y, share);
			}
			toShare = value - up;
			shareCount = 3;
			share = toShare/shareCount;
			if (share != 0) {
				changed = true;
				value = value - toShare + toShare%shareCount + share;
				addUp(newGrid, x, y, share);
				addRight(newGrid, x, y, share);
			}
			toShare = value - right;
			shareCount = 2;
			share = toShare/shareCount;
			if (share != 0) {
				changed = true;
				value = value - toShare + toShare%shareCount + share;
				addRight(newGrid, x, y, share);
			}
		} else if (down > left) {
			value = aetherLogicMethod37(newGrid, value, right, left, up, down, x, y);
		} else {//down == left
			// value > left = down > up > right
			long toShare, share;
			int shareCount;
			toShare = value - left;
			shareCount = 5;
			share = toShare/shareCount;
			if (share != 0) {
				changed = true;
				value = value - toShare + toShare%shareCount + share;
				addLeft(newGrid, x, y, share);
				addDown(newGrid, x, y, share);
				addUp(newGrid, x, y, share);
				addRight(newGrid, x, y, share);
			}
			toShare = value - up;
			shareCount = 3;
			share = toShare/shareCount;
			if (share != 0) {
				changed = true;
				value = value - toShare + toShare%shareCount + share;
				addUp(newGrid, x, y, share);
				addRight(newGrid, x, y, share);
			}
			toShare = value - right;
			shareCount = 2;
			share = toShare/shareCount;
			if (share != 0) {
				changed = true;
				value = value - toShare + toShare%shareCount + share;
				addRight(newGrid, x, y, share);
			}
		}
		return value;
	}

	protected long aetherLogicMethod37(long[][] newGrid, long value, long right, long left, long up, long down, int x, int y) {
		if (down < value) {
			// value > down > left > up > right
			long toShare, share;
			int shareCount;
			toShare = value - down;
			shareCount = 5;
			share = toShare/shareCount;
			if (share != 0) {
				changed = true;
				value = value - toShare + toShare%shareCount + share;
				addDown(newGrid, x, y, share);
				addLeft(newGrid, x, y, share);
				addUp(newGrid, x, y, share);
				addRight(newGrid, x, y, share);
			}
			toShare = value - left;
			shareCount = 4;
			share = toShare/shareCount;
			if (share != 0) {
				changed = true;
				value = value - toShare + toShare%shareCount + share;
				addLeft(newGrid, x, y, share);
				addUp(newGrid, x, y, share);
				addRight(newGrid, x, y, share);
			}
			toShare = value - up;
			shareCount = 3;
			share = toShare/shareCount;
			if (share != 0) {
				changed = true;
				value = value - toShare + toShare%shareCount + share;
				addUp(newGrid, x, y, share);
				addRight(newGrid, x, y, share);
			}
			toShare = value - right;
			shareCount = 2;
			share = toShare/shareCount;
			if (share != 0) {
				changed = true;
				value = value - toShare + toShare%shareCount + share;
				addRight(newGrid, x, y, share);
			}
		} else {//down >= value
			// value > left > up > right
			long toShare, share;
			int shareCount;
			toShare = value - left;
			shareCount = 4;
			share = toShare/shareCount;
			if (share != 0) {
				changed = true;
				value = value - toShare + toShare%shareCount + share;
				addLeft(newGrid, x, y, share);
				addUp(newGrid, x, y, share);
				addRight(newGrid, x, y, share);
			}
			toShare = value - up;
			shareCount = 3;
			share = toShare/shareCount;
			if (share != 0) {
				changed = true;
				value = value - toShare + toShare%shareCount + share;
				addUp(newGrid, x, y, share);
				addRight(newGrid, x, y, share);
			}
			toShare = value - right;
			shareCount = 2;
			share = toShare/shareCount;
			if (share != 0) {
				changed = true;
				value = value - toShare + toShare%shareCount + share;
				addRight(newGrid, x, y, share);
			}
		}
		return value;
	}

	protected long aetherLogicMethod29(long[][] newGrid, long value, long right, long left, long up, long down, int x, int y) {
		if (down < up) {
			// value > left > right > up > down
			long toShare, share;
			int shareCount;
			toShare = value - left;
			shareCount = 5;
			share = toShare/shareCount;
			if (share != 0) {
				changed = true;
				value = value - toShare + toShare%shareCount + share;
				addLeft(newGrid, x, y, share);
				addRight(newGrid, x, y, share);
				addUp(newGrid, x, y, share);
				addDown(newGrid, x, y, share);
			}
			toShare = value - right;
			shareCount = 4;
			share = toShare/shareCount;
			if (share != 0) {
				changed = true;
				value = value - toShare + toShare%shareCount + share;
				addRight(newGrid, x, y, share);
				addUp(newGrid, x, y, share);
				addDown(newGrid, x, y, share);
			}
			toShare = value - up;
			shareCount = 3;
			share = toShare/shareCount;
			if (share != 0) {
				changed = true;
				value = value - toShare + toShare%shareCount + share;
				addUp(newGrid, x, y, share);
				addDown(newGrid, x, y, share);
			}
			toShare = value - down;
			shareCount = 2;
			share = toShare/shareCount;
			if (share != 0) {
				changed = true;
				value = value - toShare + toShare%shareCount + share;
				addDown(newGrid, x, y, share);
			}
		} else if (down > up) {
			value = aetherLogicMethod30(newGrid, value, right, left, up, down, x, y);
		} else {//down == up
			// value > left > right > up = down
			long toShare, share;
			int shareCount;
			toShare = value - left;
			shareCount = 5;
			share = toShare/shareCount;
			if (share != 0) {
				changed = true;
				value = value - toShare + toShare%shareCount + share;
				addLeft(newGrid, x, y, share);
				addRight(newGrid, x, y, share);
				addUp(newGrid, x, y, share);
				addDown(newGrid, x, y, share);
			}
			toShare = value - right;
			shareCount = 4;
			share = toShare/shareCount;
			if (share != 0) {
				changed = true;
				value = value - toShare + toShare%shareCount + share;
				addRight(newGrid, x, y, share);
				addUp(newGrid, x, y, share);
				addDown(newGrid, x, y, share);
			}
			toShare = value - up;
			shareCount = 3;
			share = toShare/shareCount;
			if (share != 0) {
				changed = true;
				value = value - toShare + toShare%shareCount + share;
				addUp(newGrid, x, y, share);
				addDown(newGrid, x, y, share);
			}
		}
		return value;
	}

	protected long aetherLogicMethod30(long[][] newGrid, long value, long right, long left, long up, long down, int x, int y) {
		if (down < right) {
			// value > left > right > down > up
			long toShare, share;
			int shareCount;
			toShare = value - left;
			shareCount = 5;
			share = toShare/shareCount;
			if (share != 0) {
				changed = true;
				value = value - toShare + toShare%shareCount + share;
				addLeft(newGrid, x, y, share);
				addRight(newGrid, x, y, share);
				addDown(newGrid, x, y, share);
				addUp(newGrid, x, y, share);
			}
			toShare = value - right;
			shareCount = 4;
			share = toShare/shareCount;
			if (share != 0) {
				changed = true;
				value = value - toShare + toShare%shareCount + share;
				addRight(newGrid, x, y, share);
				addDown(newGrid, x, y, share);
				addUp(newGrid, x, y, share);
			}
			toShare = value - down;
			shareCount = 3;
			share = toShare/shareCount;
			if (share != 0) {
				changed = true;
				value = value - toShare + toShare%shareCount + share;
				addDown(newGrid, x, y, share);
				addUp(newGrid, x, y, share);
			}
			toShare = value - up;
			shareCount = 2;
			share = toShare/shareCount;
			if (share != 0) {
				changed = true;
				value = value - toShare + toShare%shareCount + share;
				addUp(newGrid, x, y, share);
			}
		} else if (down > right) {
			value = aetherLogicMethod31(newGrid, value, right, left, up, down, x, y);
		} else {//down == right
			// value > left > right = down > up
			long toShare, share;
			int shareCount;
			toShare = value - left;
			shareCount = 5;
			share = toShare/shareCount;
			if (share != 0) {
				changed = true;
				value = value - toShare + toShare%shareCount + share;
				addLeft(newGrid, x, y, share);
				addRight(newGrid, x, y, share);
				addDown(newGrid, x, y, share);
				addUp(newGrid, x, y, share);
			}
			toShare = value - right;
			shareCount = 4;
			share = toShare/shareCount;
			if (share != 0) {
				changed = true;
				value = value - toShare + toShare%shareCount + share;
				addRight(newGrid, x, y, share);
				addDown(newGrid, x, y, share);
				addUp(newGrid, x, y, share);
			}
			toShare = value - up;
			shareCount = 2;
			share = toShare/shareCount;
			if (share != 0) {
				changed = true;
				value = value - toShare + toShare%shareCount + share;
				addUp(newGrid, x, y, share);
			}
		}
		return value;
	}

	protected long aetherLogicMethod31(long[][] newGrid, long value, long right, long left, long up, long down, int x, int y) {
		if (down < left) {
			// value > left > down > right > up
			long toShare, share;
			int shareCount;
			toShare = value - left;
			shareCount = 5;
			share = toShare/shareCount;
			if (share != 0) {
				changed = true;
				value = value - toShare + toShare%shareCount + share;
				addLeft(newGrid, x, y, share);
				addDown(newGrid, x, y, share);
				addRight(newGrid, x, y, share);
				addUp(newGrid, x, y, share);
			}
			toShare = value - down;
			shareCount = 4;
			share = toShare/shareCount;
			if (share != 0) {
				changed = true;
				value = value - toShare + toShare%shareCount + share;
				addDown(newGrid, x, y, share);
				addRight(newGrid, x, y, share);
				addUp(newGrid, x, y, share);
			}
			toShare = value - right;
			shareCount = 3;
			share = toShare/shareCount;
			if (share != 0) {
				changed = true;
				value = value - toShare + toShare%shareCount + share;
				addRight(newGrid, x, y, share);
				addUp(newGrid, x, y, share);
			}
			toShare = value - up;
			shareCount = 2;
			share = toShare/shareCount;
			if (share != 0) {
				changed = true;
				value = value - toShare + toShare%shareCount + share;
				addUp(newGrid, x, y, share);
			}
		} else if (down > left) {
			value = aetherLogicMethod32(newGrid, value, right, left, up, down, x, y);
		} else {//down == left
			// value > left = down > right > up
			long toShare, share;
			int shareCount;
			toShare = value - left;
			shareCount = 5;
			share = toShare/shareCount;
			if (share != 0) {
				changed = true;
				value = value - toShare + toShare%shareCount + share;
				addLeft(newGrid, x, y, share);
				addDown(newGrid, x, y, share);
				addRight(newGrid, x, y, share);
				addUp(newGrid, x, y, share);
			}
			toShare = value - right;
			shareCount = 3;
			share = toShare/shareCount;
			if (share != 0) {
				changed = true;
				value = value - toShare + toShare%shareCount + share;
				addRight(newGrid, x, y, share);
				addUp(newGrid, x, y, share);
			}
			toShare = value - up;
			shareCount = 2;
			share = toShare/shareCount;
			if (share != 0) {
				changed = true;
				value = value - toShare + toShare%shareCount + share;
				addUp(newGrid, x, y, share);
			}
		}
		return value;
	}

	protected long aetherLogicMethod32(long[][] newGrid, long value, long right, long left, long up, long down, int x, int y) {
		if (down < value) {
			// value > down > left > right > up
			long toShare, share;
			int shareCount;
			toShare = value - down;
			shareCount = 5;
			share = toShare/shareCount;
			if (share != 0) {
				changed = true;
				value = value - toShare + toShare%shareCount + share;
				addDown(newGrid, x, y, share);
				addLeft(newGrid, x, y, share);
				addRight(newGrid, x, y, share);
				addUp(newGrid, x, y, share);
			}
			toShare = value - left;
			shareCount = 4;
			share = toShare/shareCount;
			if (share != 0) {
				changed = true;
				value = value - toShare + toShare%shareCount + share;
				addLeft(newGrid, x, y, share);
				addRight(newGrid, x, y, share);
				addUp(newGrid, x, y, share);
			}
			toShare = value - right;
			shareCount = 3;
			share = toShare/shareCount;
			if (share != 0) {
				changed = true;
				value = value - toShare + toShare%shareCount + share;
				addRight(newGrid, x, y, share);
				addUp(newGrid, x, y, share);
			}
			toShare = value - up;
			shareCount = 2;
			share = toShare/shareCount;
			if (share != 0) {
				changed = true;
				value = value - toShare + toShare%shareCount + share;
				addUp(newGrid, x, y, share);
			}
		} else {//down >= value
			// value > left > right > up
			long toShare, share;
			int shareCount;
			toShare = value - left;
			shareCount = 4;
			share = toShare/shareCount;
			if (share != 0) {
				changed = true;
				value = value - toShare + toShare%shareCount + share;
				addLeft(newGrid, x, y, share);
				addRight(newGrid, x, y, share);
				addUp(newGrid, x, y, share);
			}
			toShare = value - right;
			shareCount = 3;
			share = toShare/shareCount;
			if (share != 0) {
				changed = true;
				value = value - toShare + toShare%shareCount + share;
				addRight(newGrid, x, y, share);
				addUp(newGrid, x, y, share);
			}
			toShare = value - up;
			shareCount = 2;
			share = toShare/shareCount;
			if (share != 0) {
				changed = true;
				value = value - toShare + toShare%shareCount + share;
				addUp(newGrid, x, y, share);
			}
		}
		return value;
	}

	protected long aetherLogicMethod3(long[][] newGrid, long value, long right, long left, long up, long down, int x, int y) {
		if (up < left) {
			value = aetherLogicMethod4(newGrid, value, right, left, up, down, x, y);
		} else if (up > left) {
			value = aetherLogicMethod8(newGrid, value, right, left, up, down, x, y);
		} else {//up == left
			value = aetherLogicMethod24(newGrid, value, right, left, up, down, x, y);
		}
		return value;
	}

	protected long aetherLogicMethod24(long[][] newGrid, long value, long right, long left, long up, long down, int x, int y) {
		if (down < left) {
			// value > right > left = up > down
			long toShare, share;
			int shareCount;
			toShare = value - right;
			shareCount = 5;
			share = toShare/shareCount;
			if (share != 0) {
				changed = true;
				value = value - toShare + toShare%shareCount + share;
				addRight(newGrid, x, y, share);
				addLeft(newGrid, x, y, share);
				addUp(newGrid, x, y, share);
				addDown(newGrid, x, y, share);
			}
			toShare = value - left;
			shareCount = 4;
			share = toShare/shareCount;
			if (share != 0) {
				changed = true;
				value = value - toShare + toShare%shareCount + share;
				addLeft(newGrid, x, y, share);
				addUp(newGrid, x, y, share);
				addDown(newGrid, x, y, share);
			}
			toShare = value - down;
			shareCount = 2;
			share = toShare/shareCount;
			if (share != 0) {
				changed = true;
				value = value - toShare + toShare%shareCount + share;
				addDown(newGrid, x, y, share);
			}
		} else if (down > left) {
			value = aetherLogicMethod25(newGrid, value, right, left, up, down, x, y);
		} else {//down == left
			// value > right > left = up = down
			long toShare, share;
			int shareCount;
			toShare = value - right;
			shareCount = 5;
			share = toShare/shareCount;
			if (share != 0) {
				changed = true;
				value = value - toShare + toShare%shareCount + share;
				addRight(newGrid, x, y, share);
				addLeft(newGrid, x, y, share);
				addUp(newGrid, x, y, share);
				addDown(newGrid, x, y, share);
			}
			toShare = value - left;
			shareCount = 4;
			share = toShare/shareCount;
			if (share != 0) {
				changed = true;
				value = value - toShare + toShare%shareCount + share;
				addLeft(newGrid, x, y, share);
				addUp(newGrid, x, y, share);
				addDown(newGrid, x, y, share);
			}
		}
		return value;
	}

	protected long aetherLogicMethod25(long[][] newGrid, long value, long right, long left, long up, long down, int x, int y) {
		if (down < right) {
			// value > right > down > left = up
			long toShare, share;
			int shareCount;
			toShare = value - right;
			shareCount = 5;
			share = toShare/shareCount;
			if (share != 0) {
				changed = true;
				value = value - toShare + toShare%shareCount + share;
				addRight(newGrid, x, y, share);
				addDown(newGrid, x, y, share);
				addLeft(newGrid, x, y, share);
				addUp(newGrid, x, y, share);
			}
			toShare = value - down;
			shareCount = 4;
			share = toShare/shareCount;
			if (share != 0) {
				changed = true;
				value = value - toShare + toShare%shareCount + share;
				addDown(newGrid, x, y, share);
				addLeft(newGrid, x, y, share);
				addUp(newGrid, x, y, share);
			}
			toShare = value - left;
			shareCount = 3;
			share = toShare/shareCount;
			if (share != 0) {
				changed = true;
				value = value - toShare + toShare%shareCount + share;
				addLeft(newGrid, x, y, share);
				addUp(newGrid, x, y, share);
			}
		} else if (down > right) {
			value = aetherLogicMethod26(newGrid, value, right, left, up, down, x, y);
		} else {//down == right
			// value > right = down > left = up
			long toShare, share;
			int shareCount;
			toShare = value - right;
			shareCount = 5;
			share = toShare/shareCount;
			if (share != 0) {
				changed = true;
				value = value - toShare + toShare%shareCount + share;
				addRight(newGrid, x, y, share);
				addDown(newGrid, x, y, share);
				addLeft(newGrid, x, y, share);
				addUp(newGrid, x, y, share);
			}
			toShare = value - left;
			shareCount = 3;
			share = toShare/shareCount;
			if (share != 0) {
				changed = true;
				value = value - toShare + toShare%shareCount + share;
				addLeft(newGrid, x, y, share);
				addUp(newGrid, x, y, share);
			}
		}
		return value;
	}

	protected long aetherLogicMethod26(long[][] newGrid, long value, long right, long left, long up, long down, int x, int y) {
		if (down < value) {
			// value > down > right > left = up
			long toShare, share;
			int shareCount;
			toShare = value - down;
			shareCount = 5;
			share = toShare/shareCount;
			if (share != 0) {
				changed = true;
				value = value - toShare + toShare%shareCount + share;
				addDown(newGrid, x, y, share);
				addRight(newGrid, x, y, share);
				addLeft(newGrid, x, y, share);
				addUp(newGrid, x, y, share);
			}
			toShare = value - right;
			shareCount = 4;
			share = toShare/shareCount;
			if (share != 0) {
				changed = true;
				value = value - toShare + toShare%shareCount + share;
				addRight(newGrid, x, y, share);
				addLeft(newGrid, x, y, share);
				addUp(newGrid, x, y, share);
			}
			toShare = value - left;
			shareCount = 3;
			share = toShare/shareCount;
			if (share != 0) {
				changed = true;
				value = value - toShare + toShare%shareCount + share;
				addLeft(newGrid, x, y, share);
				addUp(newGrid, x, y, share);
			}
		} else {//down >= value
			// value > right > left = up
			long toShare, share;
			int shareCount;
			toShare = value - right;
			shareCount = 4;
			share = toShare/shareCount;
			if (share != 0) {
				changed = true;
				value = value - toShare + toShare%shareCount + share;
				addRight(newGrid, x, y, share);
				addLeft(newGrid, x, y, share);
				addUp(newGrid, x, y, share);
			}
			toShare = value - left;
			shareCount = 3;
			share = toShare/shareCount;
			if (share != 0) {
				changed = true;
				value = value - toShare + toShare%shareCount + share;
				addLeft(newGrid, x, y, share);
				addUp(newGrid, x, y, share);
			}
		}
		return value;
	}

	protected long aetherLogicMethod8(long[][] newGrid, long value, long right, long left, long up, long down, int x, int y) {
		if (up < right) {
			value = aetherLogicMethod9(newGrid, value, right, left, up, down, x, y);
		} else if (up > right) {
			value = aetherLogicMethod13(newGrid, value, right, left, up, down, x, y);
		} else {//up == right
			value = aetherLogicMethod21(newGrid, value, right, left, up, down, x, y);
		}
		return value;
	}

	protected long aetherLogicMethod21(long[][] newGrid, long value, long right, long left, long up, long down, int x, int y) {
		if (down < left) {
			// value > right = up > left > down
			long toShare, share;
			int shareCount;
			toShare = value - right;
			shareCount = 5;
			share = toShare/shareCount;
			if (share != 0) {
				changed = true;
				value = value - toShare + toShare%shareCount + share;
				addRight(newGrid, x, y, share);
				addUp(newGrid, x, y, share);
				addLeft(newGrid, x, y, share);
				addDown(newGrid, x, y, share);
			}
			toShare = value - left;
			shareCount = 3;
			share = toShare/shareCount;
			if (share != 0) {
				changed = true;
				value = value - toShare + toShare%shareCount + share;
				addLeft(newGrid, x, y, share);
				addDown(newGrid, x, y, share);
			}
			toShare = value - down;
			shareCount = 2;
			share = toShare/shareCount;
			if (share != 0) {
				changed = true;
				value = value - toShare + toShare%shareCount + share;
				addDown(newGrid, x, y, share);
			}
		} else if (down > left) {
			value = aetherLogicMethod22(newGrid, value, right, left, up, down, x, y);
		} else {//down == left
			// value > right = up > left = down
			long toShare, share;
			int shareCount;
			toShare = value - right;
			shareCount = 5;
			share = toShare/shareCount;
			if (share != 0) {
				changed = true;
				value = value - toShare + toShare%shareCount + share;
				addRight(newGrid, x, y, share);
				addUp(newGrid, x, y, share);
				addLeft(newGrid, x, y, share);
				addDown(newGrid, x, y, share);
			}
			toShare = value - left;
			shareCount = 3;
			share = toShare/shareCount;
			if (share != 0) {
				changed = true;
				value = value - toShare + toShare%shareCount + share;
				addLeft(newGrid, x, y, share);
				addDown(newGrid, x, y, share);
			}
		}
		return value;
	}

	protected long aetherLogicMethod22(long[][] newGrid, long value, long right, long left, long up, long down, int x, int y) {
		if (down < right) {
			// value > right = up > down > left
			long toShare, share;
			int shareCount;
			toShare = value - right;
			shareCount = 5;
			share = toShare/shareCount;
			if (share != 0) {
				changed = true;
				value = value - toShare + toShare%shareCount + share;
				addRight(newGrid, x, y, share);
				addUp(newGrid, x, y, share);
				addDown(newGrid, x, y, share);
				addLeft(newGrid, x, y, share);
			}
			toShare = value - down;
			shareCount = 3;
			share = toShare/shareCount;
			if (share != 0) {
				changed = true;
				value = value - toShare + toShare%shareCount + share;
				addDown(newGrid, x, y, share);
				addLeft(newGrid, x, y, share);
			}
			toShare = value - left;
			shareCount = 2;
			share = toShare/shareCount;
			if (share != 0) {
				changed = true;
				value = value - toShare + toShare%shareCount + share;
				addLeft(newGrid, x, y, share);
			}
		} else if (down > right) {
			value = aetherLogicMethod23(newGrid, value, right, left, up, down, x, y);
		} else {//down == right
			// value > right = up = down > left
			long toShare, share;
			int shareCount;
			toShare = value - right;
			shareCount = 5;
			share = toShare/shareCount;
			if (share != 0) {
				changed = true;
				value = value - toShare + toShare%shareCount + share;
				addRight(newGrid, x, y, share);
				addUp(newGrid, x, y, share);
				addDown(newGrid, x, y, share);
				addLeft(newGrid, x, y, share);
			}
			toShare = value - left;
			shareCount = 2;
			share = toShare/shareCount;
			if (share != 0) {
				changed = true;
				value = value - toShare + toShare%shareCount + share;
				addLeft(newGrid, x, y, share);
			}
		}
		return value;
	}

	protected long aetherLogicMethod23(long[][] newGrid, long value, long right, long left, long up, long down, int x, int y) {
		if (down < value) {
			// value > down > right = up > left
			long toShare, share;
			int shareCount;
			toShare = value - down;
			shareCount = 5;
			share = toShare/shareCount;
			if (share != 0) {
				changed = true;
				value = value - toShare + toShare%shareCount + share;
				addDown(newGrid, x, y, share);
				addRight(newGrid, x, y, share);
				addUp(newGrid, x, y, share);
				addLeft(newGrid, x, y, share);
			}
			toShare = value - right;
			shareCount = 4;
			share = toShare/shareCount;
			if (share != 0) {
				changed = true;
				value = value - toShare + toShare%shareCount + share;
				addRight(newGrid, x, y, share);
				addUp(newGrid, x, y, share);
				addLeft(newGrid, x, y, share);
			}
			toShare = value - left;
			shareCount = 2;
			share = toShare/shareCount;
			if (share != 0) {
				changed = true;
				value = value - toShare + toShare%shareCount + share;
				addLeft(newGrid, x, y, share);
			}
		} else {//down >= value
			// value > right = up > left
			long toShare, share;
			int shareCount;
			toShare = value - right;
			shareCount = 4;
			share = toShare/shareCount;
			if (share != 0) {
				changed = true;
				value = value - toShare + toShare%shareCount + share;
				addRight(newGrid, x, y, share);
				addUp(newGrid, x, y, share);
				addLeft(newGrid, x, y, share);
			}
			toShare = value - left;
			shareCount = 2;
			share = toShare/shareCount;
			if (share != 0) {
				changed = true;
				value = value - toShare + toShare%shareCount + share;
				addLeft(newGrid, x, y, share);
			}
		}
		return value;
	}

	protected long aetherLogicMethod13(long[][] newGrid, long value, long right, long left, long up, long down, int x, int y) {
		if (up < value) {
			value = aetherLogicMethod14(newGrid, value, right, left, up, down, x, y);
		} else {//up >= value
			value = aetherLogicMethod18(newGrid, value, right, left, up, down, x, y);
		}
		return value;
	}

	protected long aetherLogicMethod18(long[][] newGrid, long value, long right, long left, long up, long down, int x, int y) {
		if (down < left) {
			// value > right > left > down
			long toShare, share;
			int shareCount;
			toShare = value - right;
			shareCount = 4;
			share = toShare/shareCount;
			if (share != 0) {
				changed = true;
				value = value - toShare + toShare%shareCount + share;
				addRight(newGrid, x, y, share);
				addLeft(newGrid, x, y, share);
				addDown(newGrid, x, y, share);
			}
			toShare = value - left;
			shareCount = 3;
			share = toShare/shareCount;
			if (share != 0) {
				changed = true;
				value = value - toShare + toShare%shareCount + share;
				addLeft(newGrid, x, y, share);
				addDown(newGrid, x, y, share);
			}
			toShare = value - down;
			shareCount = 2;
			share = toShare/shareCount;
			if (share != 0) {
				changed = true;
				value = value - toShare + toShare%shareCount + share;
				addDown(newGrid, x, y, share);
			}
		} else if (down > left) {
			value = aetherLogicMethod19(newGrid, value, right, left, up, down, x, y);
		} else {//down == left
			// value > right > left = down
			long toShare, share;
			int shareCount;
			toShare = value - right;
			shareCount = 4;
			share = toShare/shareCount;
			if (share != 0) {
				changed = true;
				value = value - toShare + toShare%shareCount + share;
				addRight(newGrid, x, y, share);
				addLeft(newGrid, x, y, share);
				addDown(newGrid, x, y, share);
			}
			toShare = value - left;
			shareCount = 3;
			share = toShare/shareCount;
			if (share != 0) {
				changed = true;
				value = value - toShare + toShare%shareCount + share;
				addLeft(newGrid, x, y, share);
				addDown(newGrid, x, y, share);
			}
		}
		return value;
	}

	protected long aetherLogicMethod19(long[][] newGrid, long value, long right, long left, long up, long down, int x, int y) {
		if (down < right) {
			// value > right > down > left
			long toShare, share;
			int shareCount;
			toShare = value - right;
			shareCount = 4;
			share = toShare/shareCount;
			if (share != 0) {
				changed = true;
				value = value - toShare + toShare%shareCount + share;
				addRight(newGrid, x, y, share);
				addDown(newGrid, x, y, share);
				addLeft(newGrid, x, y, share);
			}
			toShare = value - down;
			shareCount = 3;
			share = toShare/shareCount;
			if (share != 0) {
				changed = true;
				value = value - toShare + toShare%shareCount + share;
				addDown(newGrid, x, y, share);
				addLeft(newGrid, x, y, share);
			}
			toShare = value - left;
			shareCount = 2;
			share = toShare/shareCount;
			if (share != 0) {
				changed = true;
				value = value - toShare + toShare%shareCount + share;
				addLeft(newGrid, x, y, share);
			}
		} else if (down > right) {
			value = aetherLogicMethod20(newGrid, value, right, left, up, down, x, y);
		} else {//down == right
			// value > right = down > left
			long toShare, share;
			int shareCount;
			toShare = value - right;
			shareCount = 4;
			share = toShare/shareCount;
			if (share != 0) {
				changed = true;
				value = value - toShare + toShare%shareCount + share;
				addRight(newGrid, x, y, share);
				addDown(newGrid, x, y, share);
				addLeft(newGrid, x, y, share);
			}
			toShare = value - left;
			shareCount = 2;
			share = toShare/shareCount;
			if (share != 0) {
				changed = true;
				value = value - toShare + toShare%shareCount + share;
				addLeft(newGrid, x, y, share);
			}
		}
		return value;
	}

	protected long aetherLogicMethod20(long[][] newGrid, long value, long right, long left, long up, long down, int x, int y) {
		if (down < value) {
			// value > down > right > left
			long toShare, share;
			int shareCount;
			toShare = value - down;
			shareCount = 4;
			share = toShare/shareCount;
			if (share != 0) {
				changed = true;
				value = value - toShare + toShare%shareCount + share;
				addDown(newGrid, x, y, share);
				addRight(newGrid, x, y, share);
				addLeft(newGrid, x, y, share);
			}
			toShare = value - right;
			shareCount = 3;
			share = toShare/shareCount;
			if (share != 0) {
				changed = true;
				value = value - toShare + toShare%shareCount + share;
				addRight(newGrid, x, y, share);
				addLeft(newGrid, x, y, share);
			}
			toShare = value - left;
			shareCount = 2;
			share = toShare/shareCount;
			if (share != 0) {
				changed = true;
				value = value - toShare + toShare%shareCount + share;
				addLeft(newGrid, x, y, share);
			}
		} else {//down >= value
			// value > right > left
			long toShare, share;
			int shareCount;
			toShare = value - right;
			shareCount = 3;
			share = toShare/shareCount;
			if (share != 0) {
				changed = true;
				value = value - toShare + toShare%shareCount + share;
				addRight(newGrid, x, y, share);
				addLeft(newGrid, x, y, share);
			}
			toShare = value - left;
			shareCount = 2;
			share = toShare/shareCount;
			if (share != 0) {
				changed = true;
				value = value - toShare + toShare%shareCount + share;
				addLeft(newGrid, x, y, share);
			}
		}
		return value;
	}

	protected long aetherLogicMethod14(long[][] newGrid, long value, long right, long left, long up, long down, int x, int y) {
		if (down < left) {
			// value > up > right > left > down
			long toShare, share;
			int shareCount;
			toShare = value - up;
			shareCount = 5;
			share = toShare/shareCount;
			if (share != 0) {
				changed = true;
				value = value - toShare + toShare%shareCount + share;
				addUp(newGrid, x, y, share);
				addRight(newGrid, x, y, share);
				addLeft(newGrid, x, y, share);
				addDown(newGrid, x, y, share);
			}
			toShare = value - right;
			shareCount = 4;
			share = toShare/shareCount;
			if (share != 0) {
				changed = true;
				value = value - toShare + toShare%shareCount + share;
				addRight(newGrid, x, y, share);
				addLeft(newGrid, x, y, share);
				addDown(newGrid, x, y, share);
			}
			toShare = value - left;
			shareCount = 3;
			share = toShare/shareCount;
			if (share != 0) {
				changed = true;
				value = value - toShare + toShare%shareCount + share;
				addLeft(newGrid, x, y, share);
				addDown(newGrid, x, y, share);
			}
			toShare = value - down;
			shareCount = 2;
			share = toShare/shareCount;
			if (share != 0) {
				changed = true;
				value = value - toShare + toShare%shareCount + share;
				addDown(newGrid, x, y, share);
			}
		} else if (down > left) {
			value = aetherLogicMethod15(newGrid, value, right, left, up, down, x, y);
		} else {//down == left
			// value > up > right > left = down
			long toShare, share;
			int shareCount;
			toShare = value - up;
			shareCount = 5;
			share = toShare/shareCount;
			if (share != 0) {
				changed = true;
				value = value - toShare + toShare%shareCount + share;
				addUp(newGrid, x, y, share);
				addRight(newGrid, x, y, share);
				addLeft(newGrid, x, y, share);
				addDown(newGrid, x, y, share);
			}
			toShare = value - right;
			shareCount = 4;
			share = toShare/shareCount;
			if (share != 0) {
				changed = true;
				value = value - toShare + toShare%shareCount + share;
				addRight(newGrid, x, y, share);
				addLeft(newGrid, x, y, share);
				addDown(newGrid, x, y, share);
			}
			toShare = value - left;
			shareCount = 3;
			share = toShare/shareCount;
			if (share != 0) {
				changed = true;
				value = value - toShare + toShare%shareCount + share;
				addLeft(newGrid, x, y, share);
				addDown(newGrid, x, y, share);
			}
		}
		return value;
	}

	protected long aetherLogicMethod15(long[][] newGrid, long value, long right, long left, long up, long down, int x, int y) {
		if (down < right) {
			// value > up > right > down > left
			long toShare, share;
			int shareCount;
			toShare = value - up;
			shareCount = 5;
			share = toShare/shareCount;
			if (share != 0) {
				changed = true;
				value = value - toShare + toShare%shareCount + share;
				addUp(newGrid, x, y, share);
				addRight(newGrid, x, y, share);
				addDown(newGrid, x, y, share);
				addLeft(newGrid, x, y, share);
			}
			toShare = value - right;
			shareCount = 4;
			share = toShare/shareCount;
			if (share != 0) {
				changed = true;
				value = value - toShare + toShare%shareCount + share;
				addRight(newGrid, x, y, share);
				addDown(newGrid, x, y, share);
				addLeft(newGrid, x, y, share);
			}
			toShare = value - down;
			shareCount = 3;
			share = toShare/shareCount;
			if (share != 0) {
				changed = true;
				value = value - toShare + toShare%shareCount + share;
				addDown(newGrid, x, y, share);
				addLeft(newGrid, x, y, share);
			}
			toShare = value - left;
			shareCount = 2;
			share = toShare/shareCount;
			if (share != 0) {
				changed = true;
				value = value - toShare + toShare%shareCount + share;
				addLeft(newGrid, x, y, share);
			}
		} else if (down > right) {
			value = aetherLogicMethod16(newGrid, value, right, left, up, down, x, y);
		} else {//down == right
			// value > up > right = down > left
			long toShare, share;
			int shareCount;
			toShare = value - up;
			shareCount = 5;
			share = toShare/shareCount;
			if (share != 0) {
				changed = true;
				value = value - toShare + toShare%shareCount + share;
				addUp(newGrid, x, y, share);
				addRight(newGrid, x, y, share);
				addDown(newGrid, x, y, share);
				addLeft(newGrid, x, y, share);
			}
			toShare = value - right;
			shareCount = 4;
			share = toShare/shareCount;
			if (share != 0) {
				changed = true;
				value = value - toShare + toShare%shareCount + share;
				addRight(newGrid, x, y, share);
				addDown(newGrid, x, y, share);
				addLeft(newGrid, x, y, share);
			}
			toShare = value - left;
			shareCount = 2;
			share = toShare/shareCount;
			if (share != 0) {
				changed = true;
				value = value - toShare + toShare%shareCount + share;
				addLeft(newGrid, x, y, share);
			}
		}
		return value;
	}

	protected long aetherLogicMethod16(long[][] newGrid, long value, long right, long left, long up, long down, int x, int y) {
		if (down < up) {
			// value > up > down > right > left
			long toShare, share;
			int shareCount;
			toShare = value - up;
			shareCount = 5;
			share = toShare/shareCount;
			if (share != 0) {
				changed = true;
				value = value - toShare + toShare%shareCount + share;
				addUp(newGrid, x, y, share);
				addDown(newGrid, x, y, share);
				addRight(newGrid, x, y, share);
				addLeft(newGrid, x, y, share);
			}
			toShare = value - down;
			shareCount = 4;
			share = toShare/shareCount;
			if (share != 0) {
				changed = true;
				value = value - toShare + toShare%shareCount + share;
				addDown(newGrid, x, y, share);
				addRight(newGrid, x, y, share);
				addLeft(newGrid, x, y, share);
			}
			toShare = value - right;
			shareCount = 3;
			share = toShare/shareCount;
			if (share != 0) {
				changed = true;
				value = value - toShare + toShare%shareCount + share;
				addRight(newGrid, x, y, share);
				addLeft(newGrid, x, y, share);
			}
			toShare = value - left;
			shareCount = 2;
			share = toShare/shareCount;
			if (share != 0) {
				changed = true;
				value = value - toShare + toShare%shareCount + share;
				addLeft(newGrid, x, y, share);
			}
		} else if (down > up) {
			value = aetherLogicMethod17(newGrid, value, right, left, up, down, x, y);
		} else {//down == up
			// value > up = down > right > left
			long toShare, share;
			int shareCount;
			toShare = value - up;
			shareCount = 5;
			share = toShare/shareCount;
			if (share != 0) {
				changed = true;
				value = value - toShare + toShare%shareCount + share;
				addUp(newGrid, x, y, share);
				addDown(newGrid, x, y, share);
				addRight(newGrid, x, y, share);
				addLeft(newGrid, x, y, share);
			}
			toShare = value - right;
			shareCount = 3;
			share = toShare/shareCount;
			if (share != 0) {
				changed = true;
				value = value - toShare + toShare%shareCount + share;
				addRight(newGrid, x, y, share);
				addLeft(newGrid, x, y, share);
			}
			toShare = value - left;
			shareCount = 2;
			share = toShare/shareCount;
			if (share != 0) {
				changed = true;
				value = value - toShare + toShare%shareCount + share;
				addLeft(newGrid, x, y, share);
			}
		}
		return value;
	}

	protected long aetherLogicMethod17(long[][] newGrid, long value, long right, long left, long up, long down, int x, int y) {
		if (down < value) {
			// value > down > up > right > left
			long toShare, share;
			int shareCount;
			toShare = value - down;
			shareCount = 5;
			share = toShare/shareCount;
			if (share != 0) {
				changed = true;
				value = value - toShare + toShare%shareCount + share;
				addDown(newGrid, x, y, share);
				addUp(newGrid, x, y, share);
				addRight(newGrid, x, y, share);
				addLeft(newGrid, x, y, share);
			}
			toShare = value - up;
			shareCount = 4;
			share = toShare/shareCount;
			if (share != 0) {
				changed = true;
				value = value - toShare + toShare%shareCount + share;
				addUp(newGrid, x, y, share);
				addRight(newGrid, x, y, share);
				addLeft(newGrid, x, y, share);
			}
			toShare = value - right;
			shareCount = 3;
			share = toShare/shareCount;
			if (share != 0) {
				changed = true;
				value = value - toShare + toShare%shareCount + share;
				addRight(newGrid, x, y, share);
				addLeft(newGrid, x, y, share);
			}
			toShare = value - left;
			shareCount = 2;
			share = toShare/shareCount;
			if (share != 0) {
				changed = true;
				value = value - toShare + toShare%shareCount + share;
				addLeft(newGrid, x, y, share);
			}
		} else {//down >= value
			// value > up > right > left
			long toShare, share;
			int shareCount;
			toShare = value - up;
			shareCount = 4;
			share = toShare/shareCount;
			if (share != 0) {
				changed = true;
				value = value - toShare + toShare%shareCount + share;
				addUp(newGrid, x, y, share);
				addRight(newGrid, x, y, share);
				addLeft(newGrid, x, y, share);
			}
			toShare = value - right;
			shareCount = 3;
			share = toShare/shareCount;
			if (share != 0) {
				changed = true;
				value = value - toShare + toShare%shareCount + share;
				addRight(newGrid, x, y, share);
				addLeft(newGrid, x, y, share);
			}
			toShare = value - left;
			shareCount = 2;
			share = toShare/shareCount;
			if (share != 0) {
				changed = true;
				value = value - toShare + toShare%shareCount + share;
				addLeft(newGrid, x, y, share);
			}
		}
		return value;
	}

	protected long aetherLogicMethod9(long[][] newGrid, long value, long right, long left, long up, long down, int x, int y) {
		if (down < left) {
			// value > right > up > left > down
			long toShare, share;
			int shareCount;
			toShare = value - right;
			shareCount = 5;
			share = toShare/shareCount;
			if (share != 0) {
				changed = true;
				value = value - toShare + toShare%shareCount + share;
				addRight(newGrid, x, y, share);
				addUp(newGrid, x, y, share);
				addLeft(newGrid, x, y, share);
				addDown(newGrid, x, y, share);
			}
			toShare = value - up;
			shareCount = 4;
			share = toShare/shareCount;
			if (share != 0) {
				changed = true;
				value = value - toShare + toShare%shareCount + share;
				addUp(newGrid, x, y, share);
				addLeft(newGrid, x, y, share);
				addDown(newGrid, x, y, share);
			}
			toShare = value - left;
			shareCount = 3;
			share = toShare/shareCount;
			if (share != 0) {
				changed = true;
				value = value - toShare + toShare%shareCount + share;
				addLeft(newGrid, x, y, share);
				addDown(newGrid, x, y, share);
			}
			toShare = value - down;
			shareCount = 2;
			share = toShare/shareCount;
			if (share != 0) {
				changed = true;
				value = value - toShare + toShare%shareCount + share;
				addDown(newGrid, x, y, share);
			}
		} else if (down > left) {
			value = aetherLogicMethod10(newGrid, value, right, left, up, down, x, y);
		} else {//down == left
			// value > right > up > left = down
			long toShare, share;
			int shareCount;
			toShare = value - right;
			shareCount = 5;
			share = toShare/shareCount;
			if (share != 0) {
				changed = true;
				value = value - toShare + toShare%shareCount + share;
				addRight(newGrid, x, y, share);
				addUp(newGrid, x, y, share);
				addLeft(newGrid, x, y, share);
				addDown(newGrid, x, y, share);
			}
			toShare = value - up;
			shareCount = 4;
			share = toShare/shareCount;
			if (share != 0) {
				changed = true;
				value = value - toShare + toShare%shareCount + share;
				addUp(newGrid, x, y, share);
				addLeft(newGrid, x, y, share);
				addDown(newGrid, x, y, share);
			}
			toShare = value - left;
			shareCount = 3;
			share = toShare/shareCount;
			if (share != 0) {
				changed = true;
				value = value - toShare + toShare%shareCount + share;
				addLeft(newGrid, x, y, share);
				addDown(newGrid, x, y, share);
			}
		}
		return value;
	}

	protected long aetherLogicMethod10(long[][] newGrid, long value, long right, long left, long up, long down, int x, int y) {
		if (down < up) {
			// value > right > up > down > left
			long toShare, share;
			int shareCount;
			toShare = value - right;
			shareCount = 5;
			share = toShare/shareCount;
			if (share != 0) {
				changed = true;
				value = value - toShare + toShare%shareCount + share;
				addRight(newGrid, x, y, share);
				addUp(newGrid, x, y, share);
				addDown(newGrid, x, y, share);
				addLeft(newGrid, x, y, share);
			}
			toShare = value - up;
			shareCount = 4;
			share = toShare/shareCount;
			if (share != 0) {
				changed = true;
				value = value - toShare + toShare%shareCount + share;
				addUp(newGrid, x, y, share);
				addDown(newGrid, x, y, share);
				addLeft(newGrid, x, y, share);
			}
			toShare = value - down;
			shareCount = 3;
			share = toShare/shareCount;
			if (share != 0) {
				changed = true;
				value = value - toShare + toShare%shareCount + share;
				addDown(newGrid, x, y, share);
				addLeft(newGrid, x, y, share);
			}
			toShare = value - left;
			shareCount = 2;
			share = toShare/shareCount;
			if (share != 0) {
				changed = true;
				value = value - toShare + toShare%shareCount + share;
				addLeft(newGrid, x, y, share);
			}
		} else if (down > up) {
			value = aetherLogicMethod11(newGrid, value, right, left, up, down, x, y);
		} else {//down == up
			// value > right > up = down > left
			long toShare, share;
			int shareCount;
			toShare = value - right;
			shareCount = 5;
			share = toShare/shareCount;
			if (share != 0) {
				changed = true;
				value = value - toShare + toShare%shareCount + share;
				addRight(newGrid, x, y, share);
				addUp(newGrid, x, y, share);
				addDown(newGrid, x, y, share);
				addLeft(newGrid, x, y, share);
			}
			toShare = value - up;
			shareCount = 4;
			share = toShare/shareCount;
			if (share != 0) {
				changed = true;
				value = value - toShare + toShare%shareCount + share;
				addUp(newGrid, x, y, share);
				addDown(newGrid, x, y, share);
				addLeft(newGrid, x, y, share);
			}
			toShare = value - left;
			shareCount = 2;
			share = toShare/shareCount;
			if (share != 0) {
				changed = true;
				value = value - toShare + toShare%shareCount + share;
				addLeft(newGrid, x, y, share);
			}
		}
		return value;
	}

	protected long aetherLogicMethod11(long[][] newGrid, long value, long right, long left, long up, long down, int x, int y) {
		if (down < right) {
			// value > right > down > up > left
			long toShare, share;
			int shareCount;
			toShare = value - right;
			shareCount = 5;
			share = toShare/shareCount;
			if (share != 0) {
				changed = true;
				value = value - toShare + toShare%shareCount + share;
				addRight(newGrid, x, y, share);
				addDown(newGrid, x, y, share);
				addUp(newGrid, x, y, share);
				addLeft(newGrid, x, y, share);
			}
			toShare = value - down;
			shareCount = 4;
			share = toShare/shareCount;
			if (share != 0) {
				changed = true;
				value = value - toShare + toShare%shareCount + share;
				addDown(newGrid, x, y, share);
				addUp(newGrid, x, y, share);
				addLeft(newGrid, x, y, share);
			}
			toShare = value - up;
			shareCount = 3;
			share = toShare/shareCount;
			if (share != 0) {
				changed = true;
				value = value - toShare + toShare%shareCount + share;
				addUp(newGrid, x, y, share);
				addLeft(newGrid, x, y, share);
			}
			toShare = value - left;
			shareCount = 2;
			share = toShare/shareCount;
			if (share != 0) {
				changed = true;
				value = value - toShare + toShare%shareCount + share;
				addLeft(newGrid, x, y, share);
			}
		} else if (down > right) {
			value = aetherLogicMethod12(newGrid, value, right, left, up, down, x, y);
		} else {//down == right
			// value > right = down > up > left
			long toShare, share;
			int shareCount;
			toShare = value - right;
			shareCount = 5;
			share = toShare/shareCount;
			if (share != 0) {
				changed = true;
				value = value - toShare + toShare%shareCount + share;
				addRight(newGrid, x, y, share);
				addDown(newGrid, x, y, share);
				addUp(newGrid, x, y, share);
				addLeft(newGrid, x, y, share);
			}
			toShare = value - up;
			shareCount = 3;
			share = toShare/shareCount;
			if (share != 0) {
				changed = true;
				value = value - toShare + toShare%shareCount + share;
				addUp(newGrid, x, y, share);
				addLeft(newGrid, x, y, share);
			}
			toShare = value - left;
			shareCount = 2;
			share = toShare/shareCount;
			if (share != 0) {
				changed = true;
				value = value - toShare + toShare%shareCount + share;
				addLeft(newGrid, x, y, share);
			}
		}
		return value;
	}

	protected long aetherLogicMethod12(long[][] newGrid, long value, long right, long left, long up, long down, int x, int y) {
		if (down < value) {
			// value > down > right > up > left
			long toShare, share;
			int shareCount;
			toShare = value - down;
			shareCount = 5;
			share = toShare/shareCount;
			if (share != 0) {
				changed = true;
				value = value - toShare + toShare%shareCount + share;
				addDown(newGrid, x, y, share);
				addRight(newGrid, x, y, share);
				addUp(newGrid, x, y, share);
				addLeft(newGrid, x, y, share);
			}
			toShare = value - right;
			shareCount = 4;
			share = toShare/shareCount;
			if (share != 0) {
				changed = true;
				value = value - toShare + toShare%shareCount + share;
				addRight(newGrid, x, y, share);
				addUp(newGrid, x, y, share);
				addLeft(newGrid, x, y, share);
			}
			toShare = value - up;
			shareCount = 3;
			share = toShare/shareCount;
			if (share != 0) {
				changed = true;
				value = value - toShare + toShare%shareCount + share;
				addUp(newGrid, x, y, share);
				addLeft(newGrid, x, y, share);
			}
			toShare = value - left;
			shareCount = 2;
			share = toShare/shareCount;
			if (share != 0) {
				changed = true;
				value = value - toShare + toShare%shareCount + share;
				addLeft(newGrid, x, y, share);
			}
		} else {//down >= value
			// value > right > up > left
			long toShare, share;
			int shareCount;
			toShare = value - right;
			shareCount = 4;
			share = toShare/shareCount;
			if (share != 0) {
				changed = true;
				value = value - toShare + toShare%shareCount + share;
				addRight(newGrid, x, y, share);
				addUp(newGrid, x, y, share);
				addLeft(newGrid, x, y, share);
			}
			toShare = value - up;
			shareCount = 3;
			share = toShare/shareCount;
			if (share != 0) {
				changed = true;
				value = value - toShare + toShare%shareCount + share;
				addUp(newGrid, x, y, share);
				addLeft(newGrid, x, y, share);
			}
			toShare = value - left;
			shareCount = 2;
			share = toShare/shareCount;
			if (share != 0) {
				changed = true;
				value = value - toShare + toShare%shareCount + share;
				addLeft(newGrid, x, y, share);
			}
		}
		return value;
	}

	protected long aetherLogicMethod4(long[][] newGrid, long value, long right, long left, long up, long down, int x, int y) {
		if (down < up) {
			// value > right > left > up > down
			long toShare, share;
			int shareCount;
			toShare = value - right;
			shareCount = 5;
			share = toShare/shareCount;
			if (share != 0) {
				changed = true;
				value = value - toShare + toShare%shareCount + share;
				addRight(newGrid, x, y, share);
				addLeft(newGrid, x, y, share);
				addUp(newGrid, x, y, share);
				addDown(newGrid, x, y, share);
			}
			toShare = value - left;
			shareCount = 4;
			share = toShare/shareCount;
			if (share != 0) {
				changed = true;
				value = value - toShare + toShare%shareCount + share;
				addLeft(newGrid, x, y, share);
				addUp(newGrid, x, y, share);
				addDown(newGrid, x, y, share);
			}
			toShare = value - up;
			shareCount = 3;
			share = toShare/shareCount;
			if (share != 0) {
				changed = true;
				value = value - toShare + toShare%shareCount + share;
				addUp(newGrid, x, y, share);
				addDown(newGrid, x, y, share);
			}
			toShare = value - down;
			shareCount = 2;
			share = toShare/shareCount;
			if (share != 0) {
				changed = true;
				value = value - toShare + toShare%shareCount + share;
				addDown(newGrid, x, y, share);
			}
		} else if (down > up) {
			value = aetherLogicMethod5(newGrid, value, right, left, up, down, x, y);
		} else {//down == up
			// value > right > left > up = down
			long toShare, share;
			int shareCount;
			toShare = value - right;
			shareCount = 5;
			share = toShare/shareCount;
			if (share != 0) {
				changed = true;
				value = value - toShare + toShare%shareCount + share;
				addRight(newGrid, x, y, share);
				addLeft(newGrid, x, y, share);
				addUp(newGrid, x, y, share);
				addDown(newGrid, x, y, share);
			}
			toShare = value - left;
			shareCount = 4;
			share = toShare/shareCount;
			if (share != 0) {
				changed = true;
				value = value - toShare + toShare%shareCount + share;
				addLeft(newGrid, x, y, share);
				addUp(newGrid, x, y, share);
				addDown(newGrid, x, y, share);
			}
			toShare = value - up;
			shareCount = 3;
			share = toShare/shareCount;
			if (share != 0) {
				changed = true;
				value = value - toShare + toShare%shareCount + share;
				addUp(newGrid, x, y, share);
				addDown(newGrid, x, y, share);
			}
		}
		return value;
	}

	protected long aetherLogicMethod5(long[][] newGrid, long value, long right, long left, long up, long down, int x, int y) {
		if (down < left) {
			// value > right > left > down > up
			long toShare, share;
			int shareCount;
			toShare = value - right;
			shareCount = 5;
			share = toShare/shareCount;
			if (share != 0) {
				changed = true;
				value = value - toShare + toShare%shareCount + share;
				addRight(newGrid, x, y, share);
				addLeft(newGrid, x, y, share);
				addDown(newGrid, x, y, share);
				addUp(newGrid, x, y, share);
			}
			toShare = value - left;
			shareCount = 4;
			share = toShare/shareCount;
			if (share != 0) {
				changed = true;
				value = value - toShare + toShare%shareCount + share;
				addLeft(newGrid, x, y, share);
				addDown(newGrid, x, y, share);
				addUp(newGrid, x, y, share);
			}
			toShare = value - down;
			shareCount = 3;
			share = toShare/shareCount;
			if (share != 0) {
				changed = true;
				value = value - toShare + toShare%shareCount + share;
				addDown(newGrid, x, y, share);
				addUp(newGrid, x, y, share);
			}
			toShare = value - up;
			shareCount = 2;
			share = toShare/shareCount;
			if (share != 0) {
				changed = true;
				value = value - toShare + toShare%shareCount + share;
				addUp(newGrid, x, y, share);
			}
		} else if (down > left) {
			value = aetherLogicMethod6(newGrid, value, right, left, up, down, x, y);
		} else {//down == left
			// value > right > left = down > up
			long toShare, share;
			int shareCount;
			toShare = value - right;
			shareCount = 5;
			share = toShare/shareCount;
			if (share != 0) {
				changed = true;
				value = value - toShare + toShare%shareCount + share;
				addRight(newGrid, x, y, share);
				addLeft(newGrid, x, y, share);
				addDown(newGrid, x, y, share);
				addUp(newGrid, x, y, share);
			}
			toShare = value - left;
			shareCount = 4;
			share = toShare/shareCount;
			if (share != 0) {
				changed = true;
				value = value - toShare + toShare%shareCount + share;
				addLeft(newGrid, x, y, share);
				addDown(newGrid, x, y, share);
				addUp(newGrid, x, y, share);
			}
			toShare = value - up;
			shareCount = 2;
			share = toShare/shareCount;
			if (share != 0) {
				changed = true;
				value = value - toShare + toShare%shareCount + share;
				addUp(newGrid, x, y, share);
			}
		}
		return value;
	}

	protected long aetherLogicMethod6(long[][] newGrid, long value, long right, long left, long up, long down, int x, int y) {
		if (down < right) {
			// value > right > down > left > up
			long toShare, share;
			int shareCount;
			toShare = value - right;
			shareCount = 5;
			share = toShare/shareCount;
			if (share != 0) {
				changed = true;
				value = value - toShare + toShare%shareCount + share;
				addRight(newGrid, x, y, share);
				addDown(newGrid, x, y, share);
				addLeft(newGrid, x, y, share);
				addUp(newGrid, x, y, share);
			}
			toShare = value - down;
			shareCount = 4;
			share = toShare/shareCount;
			if (share != 0) {
				changed = true;
				value = value - toShare + toShare%shareCount + share;
				addDown(newGrid, x, y, share);
				addLeft(newGrid, x, y, share);
				addUp(newGrid, x, y, share);
			}
			toShare = value - left;
			shareCount = 3;
			share = toShare/shareCount;
			if (share != 0) {
				changed = true;
				value = value - toShare + toShare%shareCount + share;
				addLeft(newGrid, x, y, share);
				addUp(newGrid, x, y, share);
			}
			toShare = value - up;
			shareCount = 2;
			share = toShare/shareCount;
			if (share != 0) {
				changed = true;
				value = value - toShare + toShare%shareCount + share;
				addUp(newGrid, x, y, share);
			}
		} else if (down > right) {
			value = aetherLogicMethod7(newGrid, value, right, left, up, down, x, y);
		} else {//down == right
			// value > right = down > left > up
			long toShare, share;
			int shareCount;
			toShare = value - right;
			shareCount = 5;
			share = toShare/shareCount;
			if (share != 0) {
				changed = true;
				value = value - toShare + toShare%shareCount + share;
				addRight(newGrid, x, y, share);
				addDown(newGrid, x, y, share);
				addLeft(newGrid, x, y, share);
				addUp(newGrid, x, y, share);
			}
			toShare = value - left;
			shareCount = 3;
			share = toShare/shareCount;
			if (share != 0) {
				changed = true;
				value = value - toShare + toShare%shareCount + share;
				addLeft(newGrid, x, y, share);
				addUp(newGrid, x, y, share);
			}
			toShare = value - up;
			shareCount = 2;
			share = toShare/shareCount;
			if (share != 0) {
				changed = true;
				value = value - toShare + toShare%shareCount + share;
				addUp(newGrid, x, y, share);
			}
		}
		return value;
	}

	protected long aetherLogicMethod7(long[][] newGrid, long value, long right, long left, long up, long down, int x, int y) {
		if (down < value) {
			// value > down > right > left > up
			long toShare, share;
			int shareCount;
			toShare = value - down;
			shareCount = 5;
			share = toShare/shareCount;
			if (share != 0) {
				changed = true;
				value = value - toShare + toShare%shareCount + share;
				addDown(newGrid, x, y, share);
				addRight(newGrid, x, y, share);
				addLeft(newGrid, x, y, share);
				addUp(newGrid, x, y, share);
			}
			toShare = value - right;
			shareCount = 4;
			share = toShare/shareCount;
			if (share != 0) {
				changed = true;
				value = value - toShare + toShare%shareCount + share;
				addRight(newGrid, x, y, share);
				addLeft(newGrid, x, y, share);
				addUp(newGrid, x, y, share);
			}
			toShare = value - left;
			shareCount = 3;
			share = toShare/shareCount;
			if (share != 0) {
				changed = true;
				value = value - toShare + toShare%shareCount + share;
				addLeft(newGrid, x, y, share);
				addUp(newGrid, x, y, share);
			}
			toShare = value - up;
			shareCount = 2;
			share = toShare/shareCount;
			if (share != 0) {
				changed = true;
				value = value - toShare + toShare%shareCount + share;
				addUp(newGrid, x, y, share);
			}
		} else {//down >= value
			// value > right > left > up
			long toShare, share;
			int shareCount;
			toShare = value - right;
			shareCount = 4;
			share = toShare/shareCount;
			if (share != 0) {
				changed = true;
				value = value - toShare + toShare%shareCount + share;
				addRight(newGrid, x, y, share);
				addLeft(newGrid, x, y, share);
				addUp(newGrid, x, y, share);
			}
			toShare = value - left;
			shareCount = 3;
			share = toShare/shareCount;
			if (share != 0) {
				changed = true;
				value = value - toShare + toShare%shareCount + share;
				addLeft(newGrid, x, y, share);
				addUp(newGrid, x, y, share);
			}
			toShare = value - up;
			shareCount = 2;
			share = toShare/shareCount;
			if (share != 0) {
				changed = true;
				value = value - toShare + toShare%shareCount + share;
				addUp(newGrid, x, y, share);
			}
		}
		return value;
	}
	
	private long[] buildGridBlock(int x, long value) {
		long[] newGridBlock = new long[x + 1];
		if (value != 0) {
			for (int y = 0; y < newGridBlock.length; y++) {
				newGridBlock[y] = value;
			}
		}
		return newGridBlock;
	}
	
	private void addRight(long[][] grid, int x, int y, long value) {
		grid[x+1][y] += value;
		if (x >= maxXMinusOne) {
			boundsReached = true;
		}
	}
	
	private void addLeft(long[][] grid, int x, int y, long value) {
		if (x > y) {
			long valueToAdd = value;
			if (x == y + 1) {
				valueToAdd += value;
				if (x == 1) {
					valueToAdd += 2*value;							
				}
			}
			grid[x-1][y] += valueToAdd;
		}
		if (x >= maxXMinusOne) {
			boundsReached = true;
		}
	}
	
	private void addUp(long[][] grid, int x, int y, long value) {
		if (y < x) {
			long valueToAdd = value;
			if (y == x - 1) {
				valueToAdd += value;
			}
			int yy = y+1;
			grid[x][yy] += valueToAdd;
			if (yy > maxY)
				maxY = yy;
		}
	}
	
	private void addDown(long[][] grid, int x, int y, long value) {
		if (y > 0) {
			long valueToAdd = value;
			if (y == 1) {
				valueToAdd += value;
			}
			grid[x][y-1] += valueToAdd;
		}
	}
	
	public long getValueAt(int x, int y){	
		if (x < 0) x = -x;
		if (y < 0) y = -y;
		if (y > x) {
			int swp = y;
			y = x;
			x = swp;
		}
		if (x < grid.length 
				&& y < grid[x].length) {
			return grid[x][y];
		} else {
			return backgroundValue;
		}
	}
	
	public long getNonSymmetricValueAt(int x, int y){	
		if (x < grid.length 
				&& y < grid[x].length) {
			return grid[x][y];
		} else {
			return backgroundValue;
		}
	}
	
	public int getNonSymmetricMinX() {
		return 0;
	}

	public int getNonSymmetricMaxX() {
		return grid.length - 1;
	}
	
	public int getNonSymmetricMinY() {
		return 0;
	}
	
	public int getNonSymmetricMaxY() {
		return maxY;
	}
	
	/**
	 * Returns the initial value
	 * 
	 * @return the value at the origin at step 0
	 */
	public long getInitialValue() {
		return initialValue;
	}
	
	/**
	 * Returns the background value
	 * 
	 * @return the value padding all the grid but the origin at step 0
	 */
	public long getBackgroundValue() {
		return backgroundValue;
	}

	@Override
	public int getMinX() {
		return -getNonSymmetricMaxX();
	}

	@Override
	public int getMaxX() {
		return getNonSymmetricMaxX();
	}

	@Override
	public int getMinY() {
		return -getNonSymmetricMaxX();
	}

	@Override
	public int getMaxY() {
		return getNonSymmetricMaxX();
	}

	@Override
	public long getCurrentStep() {
		return currentStep;
	}

	@Override
	public String getName() {
		return "Aether2D";
	}

	@Override
	public String getSubFolderPath() {
		return getName() + "/" + initialValue + "/" + backgroundValue;
	}
}
