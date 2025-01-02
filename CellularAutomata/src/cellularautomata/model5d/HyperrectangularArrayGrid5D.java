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
package cellularautomata.model5d;

/**
 * Represents a hyperrectangular region of a 5D grid backed by a 5D array.
 * 
 * @author Jaume
 *
 */
public abstract class HyperrectangularArrayGrid5D implements Model5D {

	protected int minV;
	protected int maxV;
	protected int minW;
	protected int maxW;
	protected int minX;
	protected int maxX;
	protected int minY;
	protected int maxY;
	protected int minZ;
	protected int maxZ;
	
	/**
	 * Constructs a {@code HyperrectangularArrayGrid5D}
	 * 
	 * @param minV the smallest v-coordinate within the region
	 * @param minW the smallest w-coordinate within the region
	 * @param minX the smallest x-coordinate within the region
	 * @param minY the smallest y-coordinate within the region
	 * @param minZ the smallest z-coordinate within the region
	 */
	public HyperrectangularArrayGrid5D(int minV, int minW, int minX, int minY, int minZ) {
		this.minV = minV;
		this.minW = minW;
		this.minX = minX;
		this.minY = minY;
		this.minZ = minZ;
	}
	
	protected void setMaxCoordinates(int arrayVSide, int arrayWSide, int arrayXSide, int arrayYSide, int arrayZSide) {
		long maxCoord = (long)arrayVSide + minV - 1;
		if (maxCoord > Integer.MAX_VALUE) {
			throw new IllegalArgumentException(
					"Resulting max v (" + maxCoord + ") is greater than the supported max (" + Integer.MAX_VALUE + ").");
		}
		maxV = (int)maxCoord;
		maxCoord = (long)arrayWSide + minW - 1;
		if (maxCoord > Integer.MAX_VALUE) {
			throw new IllegalArgumentException(
					"Resulting max w (" + maxCoord + ") is greater than the supported max (" + Integer.MAX_VALUE + ").");
		}
		maxW = (int)maxCoord;
		maxCoord = (long)arrayXSide + minX - 1;
		if (maxCoord > Integer.MAX_VALUE) {
			throw new IllegalArgumentException(
					"Resulting max x (" + maxCoord + ") is greater than the supported max (" + Integer.MAX_VALUE + ").");
		}
		maxX = (int)maxCoord;
		maxCoord = (long)arrayYSide + minY - 1;
		if (maxCoord > Integer.MAX_VALUE) {
			throw new IllegalArgumentException(
					"Resulting max y (" + maxCoord + ") is greater than the supported max (" + Integer.MAX_VALUE + ").");
		}
		maxY = (int)maxCoord;
		maxCoord = (long)arrayZSide + minZ - 1;
		if (maxCoord > Integer.MAX_VALUE) {
			throw new IllegalArgumentException(
					"Resulting max z (" + maxCoord + ") is greater than the supported max (" + Integer.MAX_VALUE + ").");
		}
		maxZ = (int)maxCoord;
	}
	
	@Override
	public int getMinV() {
		return minV;
	}

	@Override
	public int getMaxV() {
		return maxV;
	}
	
	@Override
	public int getMinW() {
		return minW;
	}

	@Override
	public int getMaxW() {
		return maxW;
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
	public int getMinY() {
		return minY;
	}

	@Override
	public int getMaxY() {
		return maxY;
	}

	@Override
	public int getMinZ() {
		return minZ;
	}

	@Override
	public int getMaxZ() {
		return maxZ;
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
