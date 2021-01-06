/* Aether2DImgMaker -- console app to generate images of the Aether cellular automaton in 2D
    Copyright (C) 2017-2021 Jaume Ribas

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
package cellularautomata.grid3d;

public class SubGrid3DWithXBounds<G extends Grid3D> implements Grid3D {
	
	protected G source;
	private int minX;
	private int maxX;
	
	public SubGrid3DWithXBounds(G source, int minX, int maxX) {
		if (minX > maxX) {
			throw new IllegalArgumentException("Min x cannot be bigger than max x.");
		}
		int sourceMinX = source.getMinX();
		int sourceMaxX = source.getMaxX();
		if (minX > sourceMaxX || maxX < sourceMinX) {
			throw new IllegalArgumentException("Sub-grid bounds outside of grid bounds.");
		}
		this.source = source;
		this.minX = Math.max(minX, sourceMinX);
		this.maxX = Math.min(maxX, sourceMaxX);
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
		return source.getMinY();
	}
	
	@Override
	public int getMaxY() {
		return source.getMaxY();
	}
	
	@Override
	public int getMinZ() {
		return source.getMinZ();
	}
	
	@Override
	public int getMaxZ() {
		return source.getMaxZ();
	}
	
	@Override
	public int getMinXAtY(int y) {
		return Math.max(source.getMinXAtY(y), minX);
	}

	@Override
	public int getMinXAtZ(int z) {
		return Math.max(source.getMinXAtZ(z), minX);
	}

	@Override
	public int getMinX(int y, int z) {
		return Math.max(source.getMinX(y, z), minX);
	}

	@Override
	public int getMaxXAtY(int y) {
		return Math.min(source.getMaxXAtY(y), maxX);
	}

	@Override
	public int getMaxXAtZ(int z) {
		return Math.min(source.getMaxXAtZ(z), maxX);
	}

	@Override
	public int getMaxX(int y, int z) {
		return Math.min(source.getMaxX(y, z), maxX);
	}

	@Override
	public int getMinYAtX(int x) {
		return source.getMinYAtX(x);
	}

	@Override
	public int getMinYAtZ(int z) {
		return source.getMinYAtZ(z);
	}

	@Override
	public int getMinY(int x, int z) {
		return source.getMinY(x, z);
	}

	@Override
	public int getMaxYAtX(int x) {
		return source.getMaxYAtX(x);
	}

	@Override
	public int getMaxYAtZ(int z) {
		return source.getMaxYAtZ(z);
	}

	@Override
	public int getMaxY(int x, int z) {
		return source.getMaxY(x, z);
	}

	@Override
	public int getMinZAtX(int x) {
		return source.getMinZAtX(x);
	}

	@Override
	public int getMinZAtY(int y) {
		return source.getMinZAtY(y);
	}

	@Override
	public int getMinZ(int x, int y) {
		return source.getMinZ(x, y);
	}

	@Override
	public int getMaxZAtX(int x) {
		return source.getMaxZAtX(x);
	}

	@Override
	public int getMaxZAtY(int y) {
		return source.getMaxZAtY(y);
	}

	@Override
	public int getMaxZ(int x, int y) {
		return source.getMaxZ(x, y);
	}

}
