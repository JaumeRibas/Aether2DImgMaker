/* Aether2DImgMaker -- console app to generate images of the Aether cellular automaton in 2D
    Copyright (C) 2017-2018 Jaume Ribas

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
package cellularautomata.grid3D;

public class NonSymmetricIntSubGrid3D implements IntGrid3D {
	
	private IntGrid3D source;
	private int minX;
	private int maxX;
	private int minY;
	private int maxY;
	private int minZ;
	private int maxZ;
	
	public NonSymmetricIntSubGrid3D(IntGrid3D source, int minX, int maxX, int minY, 
			int maxY, int minZ, int maxZ) {
		this.source = source;
		this.minX = minX;
		this.maxX = maxX;
		this.minY = minY;
		this.maxY = maxY;
		this.minZ = minZ;
		this.maxZ = maxZ;
	}

	@Override
	public int getValueAtPosition(int x, int y, int z) throws Exception {
		return source.getValueAtPosition(x, y, z);
	}

	@Override
	public int getMinX() {
		return Math.max(minX, 0);
	}

	@Override
	public int getMaxX() {
		return maxX;
	}
	
	@Override
	public int getMinY() {
		return Math.max(minY, getMinZ());
	}
	
	@Override
	public int getMaxY() {
		return Math.min(maxY, maxX);
	}
	
	@Override
	public int getMinZ() {
		return Math.max(minZ, 0);
	}
	
	@Override
	public int getMaxZ() {
		return Math.min(maxZ, getMaxY());
	}
	
	@Override
	public int getMinXAtY(int y) {
		return Math.max(getMinX(), y);
	}

	@Override
	public int getMinXAtZ(int z) {
		return Math.max(getMinX(), z);
	}

	@Override
	public int getMinX(int y, int z) {
		return Math.max(getMinX(), Math.max(y, z));
	}

	@Override
	public int getMaxXAtY(int y) {
		return maxX;
	}

	@Override
	public int getMaxXAtZ(int z) {
		return maxX;
	}

	@Override
	public int getMaxX(int y, int z) {
		return maxX;
	}

	@Override
	public int getMinYAtX(int x) {
		return getMinZ();
	}

	@Override
	public int getMinYAtZ(int z) {
		return Math.max(getMinY(), z);
	}

	@Override
	public int getMinY(int x, int z) {
		return Math.max(getMinY(), z);
	}

	@Override
	public int getMaxYAtX(int x) {
		return Math.min(getMaxY(), x);
	}

	@Override
	public int getMaxYAtZ(int z) {
		return getMaxY();
	}

	@Override
	public int getMaxY(int x, int z) {
		return Math.min(getMaxY(), x);
	}

	@Override
	public int getMinZAtX(int x) {
		return getMinZ();
	}

	@Override
	public int getMinZAtY(int y) {
		return getMinZ();
	}

	@Override
	public int getMinZ(int x, int y) {
		return getMinZ();
	}

	@Override
	public int getMaxZAtX(int x) {
		return Math.min(getMaxZ(), x);
	}

	@Override
	public int getMaxZAtY(int y) {
		return Math.min(getMaxZ(), y);
	}

	@Override
	public int getMaxZ(int x, int y) {
		return Math.min(getMaxZ(), y);
	}

}
