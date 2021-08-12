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

public class SubGrid4DWithWBounds<G extends Grid4D> extends SubGrid4D<G> {
	
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
		minX = source.getMinXAtW(this.minW);
		maxX = source.getMaxXAtW(this.minW);
		minY = source.getMinYAtW(this.minW);
		maxY = source.getMaxYAtW(this.minW);
		minZ = source.getMinZAtW(this.minW);
		maxZ = source.getMaxZAtW(this.minW);
		for (int w = this.minW + 1; w <= this.maxW; w++) {
			int localMinX = source.getMinXAtW(w);
			if (localMinX < minX) {
				minX = localMinX;
			}
			int localMaxX = source.getMaxXAtW(w);
			if (localMaxX > maxX) {
				maxX = localMaxX;
			}
			int localMinY = source.getMinYAtW(w);
			if (localMinY < minY) {
				minY = localMinY;
			}
			int localMaxY = source.getMaxYAtW(w);
			if (localMaxY > maxY) {
				maxY = localMaxY;
			}
			int localMinZ = source.getMinZAtW(w);
			if (localMinZ < minZ) {
				minZ = localMinZ;
			}
			int localMaxZ = source.getMaxZAtW(w);
			if (localMaxZ > maxZ) {
				maxZ = localMaxZ;
			}
		}
	}

}
