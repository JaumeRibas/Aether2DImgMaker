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
package cellularautomata.grid5d;

public class SubGrid5DWithVBounds<G extends Grid5D> extends SubGrid5D<G> {
	
	public SubGrid5DWithVBounds(G source, int minV, int maxV) {
		if (minV > maxV) {
			throw new IllegalArgumentException("Min v cannot be bigger than max v.");
		}
		int sourceMinV = source.getMinV();
		int sourceMaxV = source.getMaxV();
		if (minV > sourceMaxV || maxV < sourceMinV) {
			throw new IllegalArgumentException("Sub-grid bounds outside of grid bounds.");
		}
		this.source = source;
		this.minV = Math.max(minV, sourceMinV);
		this.maxV = Math.min(maxV, sourceMaxV);
		minW = source.getMinWAtV(this.minV);
		maxW = source.getMaxWAtV(this.minV);
		minX = source.getMinXAtV(this.minV);
		maxX = source.getMaxXAtV(this.minV);
		minY = source.getMinYAtV(this.minV);
		maxY = source.getMaxYAtV(this.minV);
		minZ = source.getMinZAtV(this.minV);
		maxZ = source.getMaxZAtV(this.minV);
		for (int v = this.minV + 1; v <= this.maxV; v++) {
			int localMinW = source.getMinWAtV(v);
			if (localMinW < minW) {
				minW = localMinW;
			}
			int localMaxW = source.getMaxWAtV(v);
			if (localMaxW > maxW) {
				maxW = localMaxW;
			}
			int localMinX = source.getMinXAtV(v);
			if (localMinX < minX) {
				minX = localMinX;
			}
			int localMaxX = source.getMaxXAtV(v);
			if (localMaxX > maxX) {
				maxX = localMaxX;
			}
			int localMinY = source.getMinYAtV(v);
			if (localMinY < minY) {
				minY = localMinY;
			}
			int localMaxY = source.getMaxYAtV(v);
			if (localMaxY > maxY) {
				maxY = localMaxY;
			}
			int localMinZ = source.getMinZAtV(v);
			if (localMinZ < minZ) {
				minZ = localMinZ;
			}
			int localMaxZ = source.getMaxZAtV(v);
			if (localMaxZ > maxZ) {
				maxZ = localMaxZ;
			}
		}
	}

}
