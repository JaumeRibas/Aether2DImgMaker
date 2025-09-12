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


public class RegularIntGrid5D implements IntModel5D {
	
	protected final int[][][][][] values;
	protected final int minV;
	protected final int maxV;
	protected final int minW;
	protected final int maxW;
	protected final int minX;
	protected final int maxX;
	protected final int minY;
	protected final int maxY;
	protected final int minZ;
	protected final int maxZ;

	
	public RegularIntGrid5D(int[][][][][] values, int minV, int minW, int minX, int minY, int minZ) {
		if (values.length == 0 || values[0].length == 0 || values[0][0].length == 0 || values[0][0][0].length == 0 || values[0][0][0][0].length == 0) {
			throw new IllegalArgumentException("Array lengths must be greater than zero.");
		}
		int vLength = values.length;
		int wLength = values[0].length;
		int xLength = values[0][0].length;
		int yLength = values[0][0][0].length;
		int zLength = values[0][0][0][0].length;
		for (int i = 0; i < wLength; i++) {
			if (values[i].length != xLength) {
				throw new IllegalArgumentException("Array lengths must be regular. Expected " + wLength + " but found " + values[i].length + " at index [" + i + "].");
			}
			for (int j = 0; j < wLength; j++) {
				if (values[i][j].length != xLength) {
					throw new IllegalArgumentException("Array lengths must be regular. Expected " + xLength + " but found " + values[i][j].length + " at index [" + i + "][" + j + "].");
				}
				for (int k = 0; k < xLength; k++) {
					if (values[i][j][k].length != yLength) {
						throw new IllegalArgumentException("Array lengths must be regular. Expected " + yLength + " but found " + values[i][j][k].length + " at index [" + i + "][" + j + "][" + k + "].");
					}
					for (int l = 0; l < yLength; l++) {
						if (values[i][j][k][l].length != zLength) {
							throw new IllegalArgumentException("Array lengths must be regular. Expected " + zLength + " but found " + values[i][j][k][l].length + " at index [" + i + "][" + j + "][" + k + "][" + l + "].");
						}
					}
				}
			}
		}
		this.values = values;
		this.minV = minV;
		this.minW = minW;
		this.minX = minX;
		this.minY = minY;
		this.minZ = minZ;
		this.maxV = minV + vLength - 1;
		this.maxW = minW + wLength - 1;
		this.maxX = minX + xLength - 1;
		this.maxY = minY + yLength - 1;
		this.maxZ = minZ + zLength - 1;
	}

	@Override
	public int getFromPosition(int v, int w, int x, int y, int z) {	
		int arrayV = v - minV;
		int arrayW = w - minW;
		int arrayX = x - minX;
		int arrayY = y - minY;
		int arrayZ = z - minZ;
		return values[arrayV][arrayW][arrayX][arrayY][arrayZ];
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
