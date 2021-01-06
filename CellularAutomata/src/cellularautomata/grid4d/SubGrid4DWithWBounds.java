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
package cellularautomata.grid4d;

public class SubGrid4DWithWBounds<G extends Grid4D> implements Grid4D {
	
	protected G source;
	private int minW;
	private int maxW;
	
	public SubGrid4DWithWBounds(G source, int minW, int maxW) {
		if (minW > maxW) {
			throw new IllegalArgumentException("Min w cannot be bigger than max w.");
		}
		int sourceMinW = source.getMinW();
		int sourceMaxW = source.getMaxW();
		if (minW > sourceMaxW || maxW < sourceMinW) {
			throw new IllegalArgumentException("Sub-grid bounds outside of grid bounds.");
		}
		this.source = source;
		this.minW = Math.max(minW, sourceMinW);
		this.maxW = Math.min(maxW, sourceMaxW);
	}

	@Override
	public int getMinW() {
		return minW;
	}

	@Override
	public int getMinW(int x, int y, int z) {
		return Math.max(minW, source.getMinW(x, y, z));
	}

	@Override
	public int getMaxW(int x, int y, int z) {
		return Math.min(maxW, source.getMaxW(x, y, z));
	}

	@Override
	public int getMaxW() {
		return maxW;
	}

	@Override
	public int getMinX() {
		return source.getMinX();
	}

	@Override
	public int getMaxX() {
		return source.getMaxX();
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
	public int getMinWAtZ(int z) {
		return source.getMinWAtZ(z);
	}

	@Override
	public int getMinWAtXZ(int x, int z) {
		return source.getMinWAtXZ(x, z);
	}

	@Override
	public int getMinWAtYZ(int y, int z) {
		return source.getMinWAtYZ(y, z);
	}

	@Override
	public int getMaxWAtZ(int z) {
		return source.getMaxWAtZ(z);
	}

	@Override
	public int getMaxWAtXZ(int x, int z) {
		return source.getMaxWAtXZ(x, z);
	}

	@Override
	public int getMaxWAtYZ(int y, int z) {
		return source.getMaxWAtYZ(y, z);
	}

	@Override
	public int getMinXAtZ(int z) {
		return source.getMinXAtZ(z);
	}

	@Override
	public int getMinXAtWZ(int w, int z) {
		return source.getMinXAtWZ(w, z);
	}

	@Override
	public int getMinXAtYZ(int y, int z) {
		return source.getMinXAtYZ(y, z);
	}

	@Override
	public int getMinX(int w, int y, int z) {
		return source.getMinX(w, y, z);
	}

	@Override
	public int getMaxXAtZ(int z) {
		return source.getMaxXAtZ(z);
	}

	@Override
	public int getMaxXAtWZ(int w, int z) {
		return source.getMaxXAtWZ(w, z);
	}

	@Override
	public int getMaxXAtYZ(int y, int z) {
		return source.getMaxXAtYZ(y, z);
	}

	@Override
	public int getMaxX(int w, int y, int z) {
		return source.getMaxX(w, y, z);
	}

	@Override
	public int getMinYAtZ(int z) {
		return source.getMinYAtZ(z);
	}

	@Override
	public int getMaxYAtWZ(int w, int z) {
		return source.getMaxYAtWZ(w, z);
	}

	@Override
	public int getMinYAtXZ(int x, int z) {
		return source.getMinYAtXZ(x, z);
	}

	@Override
	public int getMinY(int w, int x, int z) {
		return source.getMinY(w, x, z);
	}

	@Override
	public int getMaxYAtZ(int z) {
		return source.getMaxYAtZ(z);
	}

	@Override
	public int getMinYAtWZ(int w, int z) {
		return source.getMinYAtWZ(w, z);
	}

	@Override
	public int getMaxYAtXZ(int x, int z) {
		return source.getMaxYAtXZ(x, z);
	}

	@Override
	public int getMaxY(int w, int x, int z) {
		return source.getMaxY(w, x, z);
	}

}
