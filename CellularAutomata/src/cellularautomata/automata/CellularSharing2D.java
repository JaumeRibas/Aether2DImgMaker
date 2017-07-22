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

public class CellularSharing2D extends SymmetricLongCellularAutomaton2D {	
	
	/** A 2D array representing the grid */
	private long[][] grid;
	
	private long initialValue;
	private long currentStep;
	
	/** Whether or not the values reached the bounds of the array */
	private boolean boundsReached;

	private int maxY;

	private int maxXMinusOne;

	private boolean changed;
	
	/**
	 * Creates an instance with the given initial value
	 * 
	 * @param initialValue the value at the origin at step 0
	 */
	public CellularSharing2D(long initialValue) {
		this.initialValue = initialValue;
		grid = new long[2][];
		grid[0] = buildGridBlock(0);
		grid[1] = buildGridBlock(1);
		grid[0][0] = this.initialValue;
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
		newGrid[0] = buildGridBlock(0);
		boolean first = true;
		for (int x = 0, nextX = 1; x < grid.length; x++, nextX++, first = false) {
			if (nextX < newGrid.length) {
				newGrid[nextX] = buildGridBlock(nextX);
			}
			for (int y = 0; y <= x; y++) {
				long value = this.grid[x][y];
				if (value != 0) {
					long up = getValueAt(x, y + 1);
					long down = getValueAt(x, y - 1); 
					long left = getValueAt(x - 1, y);
					long right = getValueAt(x + 1, y);
					
					value = sharingLogicMethod1(newGrid, value, right, left, up, down, x, y);
					
					newGrid[x][y] += value;
				}
			}
			if (!first) {
				grid[x-1] = null;
			}
		}
		grid = newGrid;
		currentStep++;
		return changed;
	}
	
	protected long sharingLogicMethod1(long[][] newGrid, long value, long right, long left, long up, long down, int x, int y) {
		if (right < value) {
			value = sharingLogicMethod2(newGrid, value, right, left, up, down, x, y);
		} else {//right >= value
			value = sharingLogicMethod76(newGrid, value, right, left, up, down, x, y);
		}
		return value;
	}

	protected long sharingLogicMethod76(long[][] newGrid, long value, long right, long left, long up, long down, int x, int y) {
		if (left < value) {
			value = sharingLogicMethod77(newGrid, value, right, left, up, down, x, y);
		} else {//left >= value
			value = sharingLogicMethod89(newGrid, value, right, left, up, down, x, y);
		}
		return value;
	}

	protected long sharingLogicMethod89(long[][] newGrid, long value, long right, long left, long up, long down, int x, int y) {
		if (up < value) {
			value = sharingLogicMethod90(newGrid, value, right, left, up, down, x, y);
		} else {//up >= value
			value = sharingLogicMethod92(newGrid, value, right, left, up, down, x, y);
		}
		return value;
	}

	protected long sharingLogicMethod92(long[][] newGrid, long value, long right, long left, long up, long down, int x, int y) {
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

	protected long sharingLogicMethod90(long[][] newGrid, long value, long right, long left, long up, long down, int x, int y) {
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
			value = sharingLogicMethod91(newGrid, value, right, left, up, down, x, y);
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

	protected long sharingLogicMethod91(long[][] newGrid, long value, long right, long left, long up, long down, int x, int y) {
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

	protected long sharingLogicMethod77(long[][] newGrid, long value, long right, long left, long up, long down, int x, int y) {
		if (up < left) {
			value = sharingLogicMethod78(newGrid, value, right, left, up, down, x, y);
		} else if (up > left) {
			value = sharingLogicMethod81(newGrid, value, right, left, up, down, x, y);
		} else {//up == left
			value = sharingLogicMethod87(newGrid, value, right, left, up, down, x, y);
		}
		return value;
	}

	protected long sharingLogicMethod87(long[][] newGrid, long value, long right, long left, long up, long down, int x, int y) {
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
			value = sharingLogicMethod88(newGrid, value, right, left, up, down, x, y);
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

	protected long sharingLogicMethod88(long[][] newGrid, long value, long right, long left, long up, long down, int x, int y) {
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

	protected long sharingLogicMethod81(long[][] newGrid, long value, long right, long left, long up, long down, int x, int y) {
		if (up < value) {
			value = sharingLogicMethod82(newGrid, value, right, left, up, down, x, y);
		} else {//up >= value
			value = sharingLogicMethod85(newGrid, value, right, left, up, down, x, y);
		}
		return value;
	}

	protected long sharingLogicMethod85(long[][] newGrid, long value, long right, long left, long up, long down, int x, int y) {
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
			value = sharingLogicMethod86(newGrid, value, right, left, up, down, x, y);
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

	protected long sharingLogicMethod86(long[][] newGrid, long value, long right, long left, long up, long down, int x, int y) {
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

	protected long sharingLogicMethod82(long[][] newGrid, long value, long right, long left, long up, long down, int x, int y) {
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
			value = sharingLogicMethod83(newGrid, value, right, left, up, down, x, y);
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

	protected long sharingLogicMethod83(long[][] newGrid, long value, long right, long left, long up, long down, int x, int y) {
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
			value = sharingLogicMethod84(newGrid, value, right, left, up, down, x, y);
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

	protected long sharingLogicMethod84(long[][] newGrid, long value, long right, long left, long up, long down, int x, int y) {
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

	protected long sharingLogicMethod78(long[][] newGrid, long value, long right, long left, long up, long down, int x, int y) {
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
			value = sharingLogicMethod79(newGrid, value, right, left, up, down, x, y);
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

	protected long sharingLogicMethod79(long[][] newGrid, long value, long right, long left, long up, long down, int x, int y) {
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
			value = sharingLogicMethod80(newGrid, value, right, left, up, down, x, y);
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

	protected long sharingLogicMethod80(long[][] newGrid, long value, long right, long left, long up, long down, int x, int y) {
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

	protected long sharingLogicMethod2(long[][] newGrid, long value, long right, long left, long up, long down, int x, int y) {
		if (left < right) {
			value = sharingLogicMethod3(newGrid, value, right, left, up, down, x, y);
		} else if (left > right) {
			value = sharingLogicMethod27(newGrid, value, right, left, up, down, x, y);
		} else {//left == right
			value = sharingLogicMethod64(newGrid, value, right, left, up, down, x, y);
		}
		return value;
	}

	protected long sharingLogicMethod64(long[][] newGrid, long value, long right, long left, long up, long down, int x, int y) {
		if (up < right) {
			value = sharingLogicMethod65(newGrid, value, right, left, up, down, x, y);
		} else if (up > right) {
			value = sharingLogicMethod68(newGrid, value, right, left, up, down, x, y);
		} else {//up == right
			value = sharingLogicMethod74(newGrid, value, right, left, up, down, x, y);
		}
		return value;
	}

	protected long sharingLogicMethod74(long[][] newGrid, long value, long right, long left, long up, long down, int x, int y) {
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
			value = sharingLogicMethod75(newGrid, value, right, left, up, down, x, y);
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

	protected long sharingLogicMethod75(long[][] newGrid, long value, long right, long left, long up, long down, int x, int y) {
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

	protected long sharingLogicMethod68(long[][] newGrid, long value, long right, long left, long up, long down, int x, int y) {
		if (up < value) {
			value = sharingLogicMethod69(newGrid, value, right, left, up, down, x, y);
		} else {//up >= value
			value = sharingLogicMethod72(newGrid, value, right, left, up, down, x, y);
		}
		return value;
	}

	protected long sharingLogicMethod72(long[][] newGrid, long value, long right, long left, long up, long down, int x, int y) {
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
			value = sharingLogicMethod73(newGrid, value, right, left, up, down, x, y);
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

	protected long sharingLogicMethod73(long[][] newGrid, long value, long right, long left, long up, long down, int x, int y) {
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

	protected long sharingLogicMethod69(long[][] newGrid, long value, long right, long left, long up, long down, int x, int y) {
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
			value = sharingLogicMethod70(newGrid, value, right, left, up, down, x, y);
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

	protected long sharingLogicMethod70(long[][] newGrid, long value, long right, long left, long up, long down, int x, int y) {
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
			value = sharingLogicMethod71(newGrid, value, right, left, up, down, x, y);
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

	protected long sharingLogicMethod71(long[][] newGrid, long value, long right, long left, long up, long down, int x, int y) {
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

	protected long sharingLogicMethod65(long[][] newGrid, long value, long right, long left, long up, long down, int x, int y) {
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
			value = sharingLogicMethod66(newGrid, value, right, left, up, down, x, y);
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

	protected long sharingLogicMethod66(long[][] newGrid, long value, long right, long left, long up, long down, int x, int y) {
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
			value = sharingLogicMethod67(newGrid, value, right, left, up, down, x, y);
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

	protected long sharingLogicMethod67(long[][] newGrid, long value, long right, long left, long up, long down, int x, int y) {
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

	protected long sharingLogicMethod27(long[][] newGrid, long value, long right, long left, long up, long down, int x, int y) {
		if (left < value) {
			value = sharingLogicMethod28(newGrid, value, right, left, up, down, x, y);
		} else {//left >= value
			value = sharingLogicMethod52(newGrid, value, right, left, up, down, x, y);
		}
		return value;
	}

	protected long sharingLogicMethod52(long[][] newGrid, long value, long right, long left, long up, long down, int x, int y) {
		if (up < right) {
			value = sharingLogicMethod53(newGrid, value, right, left, up, down, x, y);
		} else if (up > right) {
			value = sharingLogicMethod56(newGrid, value, right, left, up, down, x, y);
		} else {//up == right
			value = sharingLogicMethod62(newGrid, value, right, left, up, down, x, y);
		}
		return value;
	}

	protected long sharingLogicMethod62(long[][] newGrid, long value, long right, long left, long up, long down, int x, int y) {
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
			value = sharingLogicMethod63(newGrid, value, right, left, up, down, x, y);
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

	protected long sharingLogicMethod63(long[][] newGrid, long value, long right, long left, long up, long down, int x, int y) {
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

	protected long sharingLogicMethod56(long[][] newGrid, long value, long right, long left, long up, long down, int x, int y) {
		if (up < value) {
			value = sharingLogicMethod57(newGrid, value, right, left, up, down, x, y);
		} else {//up >= value
			value = sharingLogicMethod60(newGrid, value, right, left, up, down, x, y);
		}
		return value;
	}

	protected long sharingLogicMethod60(long[][] newGrid, long value, long right, long left, long up, long down, int x, int y) {
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
			value = sharingLogicMethod61(newGrid, value, right, left, up, down, x, y);
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

	protected long sharingLogicMethod61(long[][] newGrid, long value, long right, long left, long up, long down, int x, int y) {
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

	protected long sharingLogicMethod57(long[][] newGrid, long value, long right, long left, long up, long down, int x, int y) {
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
			value = sharingLogicMethod58(newGrid, value, right, left, up, down, x, y);
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

	protected long sharingLogicMethod58(long[][] newGrid, long value, long right, long left, long up, long down, int x, int y) {
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
			value = sharingLogicMethod59(newGrid, value, right, left, up, down, x, y);
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

	protected long sharingLogicMethod59(long[][] newGrid, long value, long right, long left, long up, long down, int x, int y) {
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

	protected long sharingLogicMethod53(long[][] newGrid, long value, long right, long left, long up, long down, int x, int y) {
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
			value = sharingLogicMethod54(newGrid, value, right, left, up, down, x, y);
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

	protected long sharingLogicMethod54(long[][] newGrid, long value, long right, long left, long up, long down, int x, int y) {
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
			value = sharingLogicMethod55(newGrid, value, right, left, up, down, x, y);
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

	protected long sharingLogicMethod55(long[][] newGrid, long value, long right, long left, long up, long down, int x, int y) {
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

	protected long sharingLogicMethod28(long[][] newGrid, long value, long right, long left, long up, long down, int x, int y) {
		if (up < right) {
			value = sharingLogicMethod29(newGrid, value, right, left, up, down, x, y);
		} else if (up > right) {
			value = sharingLogicMethod33(newGrid, value, right, left, up, down, x, y);
		} else {//up == right
			value = sharingLogicMethod49(newGrid, value, right, left, up, down, x, y);
		}
		return value;
	}

	protected long sharingLogicMethod49(long[][] newGrid, long value, long right, long left, long up, long down, int x, int y) {
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
			value = sharingLogicMethod50(newGrid, value, right, left, up, down, x, y);
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

	protected long sharingLogicMethod50(long[][] newGrid, long value, long right, long left, long up, long down, int x, int y) {
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
			value = sharingLogicMethod51(newGrid, value, right, left, up, down, x, y);
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

	protected long sharingLogicMethod51(long[][] newGrid, long value, long right, long left, long up, long down, int x, int y) {
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

	protected long sharingLogicMethod33(long[][] newGrid, long value, long right, long left, long up, long down, int x, int y) {
		if (up < left) {
			value = sharingLogicMethod34(newGrid, value, right, left, up, down, x, y);
		} else if (up > left) {
			value = sharingLogicMethod38(newGrid, value, right, left, up, down, x, y);
		} else {//up == left
			value = sharingLogicMethod46(newGrid, value, right, left, up, down, x, y);
		}
		return value;
	}

	protected long sharingLogicMethod46(long[][] newGrid, long value, long right, long left, long up, long down, int x, int y) {
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
			value = sharingLogicMethod47(newGrid, value, right, left, up, down, x, y);
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

	protected long sharingLogicMethod47(long[][] newGrid, long value, long right, long left, long up, long down, int x, int y) {
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
			value = sharingLogicMethod48(newGrid, value, right, left, up, down, x, y);
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

	protected long sharingLogicMethod48(long[][] newGrid, long value, long right, long left, long up, long down, int x, int y) {
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

	protected long sharingLogicMethod38(long[][] newGrid, long value, long right, long left, long up, long down, int x, int y) {
		if (up < value) {
			value = sharingLogicMethod39(newGrid, value, right, left, up, down, x, y);
		} else {//up >= value
			value = sharingLogicMethod43(newGrid, value, right, left, up, down, x, y);
		}
		return value;
	}

	protected long sharingLogicMethod43(long[][] newGrid, long value, long right, long left, long up, long down, int x, int y) {
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
			value = sharingLogicMethod44(newGrid, value, right, left, up, down, x, y);
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

	protected long sharingLogicMethod44(long[][] newGrid, long value, long right, long left, long up, long down, int x, int y) {
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
			value = sharingLogicMethod45(newGrid, value, right, left, up, down, x, y);
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

	protected long sharingLogicMethod45(long[][] newGrid, long value, long right, long left, long up, long down, int x, int y) {
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

	protected long sharingLogicMethod39(long[][] newGrid, long value, long right, long left, long up, long down, int x, int y) {
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
			value = sharingLogicMethod40(newGrid, value, right, left, up, down, x, y);
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

	protected long sharingLogicMethod40(long[][] newGrid, long value, long right, long left, long up, long down, int x, int y) {
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
			value = sharingLogicMethod41(newGrid, value, right, left, up, down, x, y);
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

	protected long sharingLogicMethod41(long[][] newGrid, long value, long right, long left, long up, long down, int x, int y) {
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
			value = sharingLogicMethod42(newGrid, value, right, left, up, down, x, y);
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

	protected long sharingLogicMethod42(long[][] newGrid, long value, long right, long left, long up, long down, int x, int y) {
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

	protected long sharingLogicMethod34(long[][] newGrid, long value, long right, long left, long up, long down, int x, int y) {
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
			value = sharingLogicMethod35(newGrid, value, right, left, up, down, x, y);
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

	protected long sharingLogicMethod35(long[][] newGrid, long value, long right, long left, long up, long down, int x, int y) {
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
			value = sharingLogicMethod36(newGrid, value, right, left, up, down, x, y);
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

	protected long sharingLogicMethod36(long[][] newGrid, long value, long right, long left, long up, long down, int x, int y) {
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
			value = sharingLogicMethod37(newGrid, value, right, left, up, down, x, y);
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

	protected long sharingLogicMethod37(long[][] newGrid, long value, long right, long left, long up, long down, int x, int y) {
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

	protected long sharingLogicMethod29(long[][] newGrid, long value, long right, long left, long up, long down, int x, int y) {
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
			value = sharingLogicMethod30(newGrid, value, right, left, up, down, x, y);
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

	protected long sharingLogicMethod30(long[][] newGrid, long value, long right, long left, long up, long down, int x, int y) {
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
			value = sharingLogicMethod31(newGrid, value, right, left, up, down, x, y);
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

	protected long sharingLogicMethod31(long[][] newGrid, long value, long right, long left, long up, long down, int x, int y) {
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
			value = sharingLogicMethod32(newGrid, value, right, left, up, down, x, y);
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

	protected long sharingLogicMethod32(long[][] newGrid, long value, long right, long left, long up, long down, int x, int y) {
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

	protected long sharingLogicMethod3(long[][] newGrid, long value, long right, long left, long up, long down, int x, int y) {
		if (up < left) {
			value = sharingLogicMethod4(newGrid, value, right, left, up, down, x, y);
		} else if (up > left) {
			value = sharingLogicMethod8(newGrid, value, right, left, up, down, x, y);
		} else {//up == left
			value = sharingLogicMethod24(newGrid, value, right, left, up, down, x, y);
		}
		return value;
	}

	protected long sharingLogicMethod24(long[][] newGrid, long value, long right, long left, long up, long down, int x, int y) {
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
			value = sharingLogicMethod25(newGrid, value, right, left, up, down, x, y);
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

	protected long sharingLogicMethod25(long[][] newGrid, long value, long right, long left, long up, long down, int x, int y) {
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
			value = sharingLogicMethod26(newGrid, value, right, left, up, down, x, y);
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

	protected long sharingLogicMethod26(long[][] newGrid, long value, long right, long left, long up, long down, int x, int y) {
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

	protected long sharingLogicMethod8(long[][] newGrid, long value, long right, long left, long up, long down, int x, int y) {
		if (up < right) {
			value = sharingLogicMethod9(newGrid, value, right, left, up, down, x, y);
		} else if (up > right) {
			value = sharingLogicMethod13(newGrid, value, right, left, up, down, x, y);
		} else {//up == right
			value = sharingLogicMethod21(newGrid, value, right, left, up, down, x, y);
		}
		return value;
	}

	protected long sharingLogicMethod21(long[][] newGrid, long value, long right, long left, long up, long down, int x, int y) {
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
			value = sharingLogicMethod22(newGrid, value, right, left, up, down, x, y);
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

	protected long sharingLogicMethod22(long[][] newGrid, long value, long right, long left, long up, long down, int x, int y) {
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
			value = sharingLogicMethod23(newGrid, value, right, left, up, down, x, y);
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

	protected long sharingLogicMethod23(long[][] newGrid, long value, long right, long left, long up, long down, int x, int y) {
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

	protected long sharingLogicMethod13(long[][] newGrid, long value, long right, long left, long up, long down, int x, int y) {
		if (up < value) {
			value = sharingLogicMethod14(newGrid, value, right, left, up, down, x, y);
		} else {//up >= value
			value = sharingLogicMethod18(newGrid, value, right, left, up, down, x, y);
		}
		return value;
	}

	protected long sharingLogicMethod18(long[][] newGrid, long value, long right, long left, long up, long down, int x, int y) {
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
			value = sharingLogicMethod19(newGrid, value, right, left, up, down, x, y);
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

	protected long sharingLogicMethod19(long[][] newGrid, long value, long right, long left, long up, long down, int x, int y) {
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
			value = sharingLogicMethod20(newGrid, value, right, left, up, down, x, y);
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

	protected long sharingLogicMethod20(long[][] newGrid, long value, long right, long left, long up, long down, int x, int y) {
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

	protected long sharingLogicMethod14(long[][] newGrid, long value, long right, long left, long up, long down, int x, int y) {
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
			value = sharingLogicMethod15(newGrid, value, right, left, up, down, x, y);
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

	protected long sharingLogicMethod15(long[][] newGrid, long value, long right, long left, long up, long down, int x, int y) {
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
			value = sharingLogicMethod16(newGrid, value, right, left, up, down, x, y);
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

	protected long sharingLogicMethod16(long[][] newGrid, long value, long right, long left, long up, long down, int x, int y) {
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
			value = sharingLogicMethod17(newGrid, value, right, left, up, down, x, y);
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

	protected long sharingLogicMethod17(long[][] newGrid, long value, long right, long left, long up, long down, int x, int y) {
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

	protected long sharingLogicMethod9(long[][] newGrid, long value, long right, long left, long up, long down, int x, int y) {
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
			value = sharingLogicMethod10(newGrid, value, right, left, up, down, x, y);
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

	protected long sharingLogicMethod10(long[][] newGrid, long value, long right, long left, long up, long down, int x, int y) {
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
			value = sharingLogicMethod11(newGrid, value, right, left, up, down, x, y);
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

	protected long sharingLogicMethod11(long[][] newGrid, long value, long right, long left, long up, long down, int x, int y) {
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
			value = sharingLogicMethod12(newGrid, value, right, left, up, down, x, y);
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

	protected long sharingLogicMethod12(long[][] newGrid, long value, long right, long left, long up, long down, int x, int y) {
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

	protected long sharingLogicMethod4(long[][] newGrid, long value, long right, long left, long up, long down, int x, int y) {
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
			value = sharingLogicMethod5(newGrid, value, right, left, up, down, x, y);
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

	protected long sharingLogicMethod5(long[][] newGrid, long value, long right, long left, long up, long down, int x, int y) {
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
			value = sharingLogicMethod6(newGrid, value, right, left, up, down, x, y);
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

	protected long sharingLogicMethod6(long[][] newGrid, long value, long right, long left, long up, long down, int x, int y) {
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
			value = sharingLogicMethod7(newGrid, value, right, left, up, down, x, y);
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

	protected long sharingLogicMethod7(long[][] newGrid, long value, long right, long left, long up, long down, int x, int y) {
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
	
	private long[] buildGridBlock(int x) {
		long[] newGridBlock = new long[x + 1];
		return newGridBlock;
	}
	
	private void addRight(long[][] grid, int x, int y, long value) {
		grid[x+1][y] += value;
		if (x == maxXMinusOne) {
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
			return 0;
		}
	}
	
	public long getNonSymmetricValueAt(int x, int y){	
		if (x < grid.length 
				&& y < grid[x].length) {
			return grid[x][y];
		} else {
			return 0;
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
	public long getIntialValue() {
		return initialValue;
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
}
