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
package cellularautomata.model3d;

public class SubModel3DWithXBounds<G extends Model3D> extends SubModel3D<G> {
	
	public SubModel3DWithXBounds(G source, int minX, int maxX) {
		if (minX > maxX) {
			throw new IllegalArgumentException("Min x cannot be bigger than max x.");
		}
		int sourceMinX = source.getMinX();
		int sourceMaxX = source.getMaxX();
		if (minX > sourceMaxX || maxX < sourceMinX) {
			throw new IllegalArgumentException("Subsection is out of bounds.");
		}
		this.source = source;
		this.minX = Math.max(minX, sourceMinX);
		this.maxX = Math.min(maxX, sourceMaxX);
		minY = source.getMinYAtX(this.minX);
		maxY = source.getMaxYAtX(this.minX);
		minZ = source.getMinZAtX(this.minX);
		maxZ = source.getMaxZAtX(this.minX);
		for (int x = this.minX + 1; x <= this.maxX; x++) {
			int localMinY = source.getMinYAtX(x);
			if (localMinY < minY) {
				minY = localMinY;
			}
			int localMaxY = source.getMaxYAtX(x);
			if (localMaxY > maxY) {
				maxY = localMaxY;
			}
			int localMinZ = source.getMinZAtX(x);
			if (localMinZ < minZ) {
				minZ = localMinZ;
			}
			int localMaxZ = source.getMaxZAtX(x);
			if (localMaxZ > maxZ) {
				maxZ = localMaxZ;
			}
		}
	}
}
