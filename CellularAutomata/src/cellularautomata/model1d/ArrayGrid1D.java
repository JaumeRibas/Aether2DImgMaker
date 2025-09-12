/* Aether2DImgMaker -- console app to generate images of the Aether cellular automaton in 2D
    Copyright (C) 2017-2025 Jaume Ribas

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
package cellularautomata.model1d;

/**
 * Represents a finite region of a 1D grid backed by a 1D array
 * 
 * @author Jaume
 *
 */
public abstract class ArrayGrid1D implements Model1D {

	protected final int minX;
	protected int maxX;
	
	/**
	 * Constructs an {@code ArrayGrid1D} with the specified bounds
	 * 
	 * @param minX the smallest x-coordinate within the region
	 */
	public ArrayGrid1D(int minX) {
		this.minX = minX;
	}
	
	protected void setMaxX(int arrayLength) {
		long longMaxX = (long)arrayLength + minX - 1;
		if (longMaxX > Integer.MAX_VALUE) {
			throw new IllegalArgumentException(
					"Resulting max x (" + longMaxX + ") is greater than the supported max (" + Integer.MAX_VALUE + ").");
		}
		maxX = (int)longMaxX;
	}
	
	@Override
	public int getMinX() {
		return minX;
	}

	@Override
	public int getMaxX() {
		return maxX;
	}

	@Override
	public Boolean nextStep() throws Exception {
		throw new UnsupportedOperationException();
	}

	@Override
	public Boolean isChanged() {
		throw new UnsupportedOperationException();
	}

	@Override
	public long getStep() {
		throw new UnsupportedOperationException();
	}

	@Override
	public String getName() {
		throw new UnsupportedOperationException();
	}

	@Override
	public String getSubfolderPath() {
		throw new UnsupportedOperationException();
	}

	@Override
	public void backUp(String backupPath, String backupName) throws Exception {
		throw new UnsupportedOperationException();
	}
	
}
