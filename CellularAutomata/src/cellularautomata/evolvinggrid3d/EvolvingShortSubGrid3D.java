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
package cellularautomata.evolvinggrid3d;

import java.io.FileNotFoundException;
import java.io.IOException;

import cellularautomata.grid3d.ShortSubGrid3D;

public class EvolvingShortSubGrid3D extends ShortSubGrid3D<EvolvingShortGrid3D> implements EvolvingShortGrid3D {

	protected int absoluteMinX;
	protected int absoluteMaxX;
	protected int absoluteMinY;
	protected int absoluteMaxY;
	protected int absoluteMinZ;
	protected int absoluteMaxZ;
	
	public EvolvingShortSubGrid3D(EvolvingShortGrid3D source, int minX, int maxX, int minY, int maxY, int minZ, int maxZ) {
		super(source, minX, maxX, minY, maxY, minZ, maxZ);
		this.absoluteMaxX = maxX;
		this.absoluteMinX = minX;
		this.absoluteMaxY = maxY;
		this.absoluteMinY = minY;
		this.absoluteMaxZ = maxZ;
		this.absoluteMinZ = minZ;
	}

	@Override
	public boolean nextStep() throws Exception {
		boolean changed = source.nextStep();
		if (!getActualBounds(absoluteMinX, absoluteMaxX, absoluteMinY, absoluteMaxY, absoluteMinZ, absoluteMaxZ)) {
			throw new UnsupportedOperationException("Sub-grid bounds outside of grid bounds.");
		}
		return changed;
	}

	@Override
	public long getStep() {
		return source.getStep();
	}

	@Override
	public String getName() {
		return source.getName() + "_minX=" + absoluteMinX + "_maxX=" + absoluteMaxX 
				+ "_minY=" + absoluteMinY + "_maxY=" + absoluteMaxY 
				+ "_minZ=" + absoluteMinZ + "_maxZ=" + absoluteMaxZ;
	}

	@Override
	public String getSubFolderPath() {
		return source.getSubFolderPath() + "/minX=" + absoluteMinX + "_maxX=" + absoluteMaxX 
				+ "_minY=" + absoluteMinY + "_maxY=" + absoluteMaxY 
				+ "_minZ=" + absoluteMinZ + "_maxZ=" + absoluteMaxZ;
	}

	@Override
	public void backUp(String backupPath, String backupName) throws FileNotFoundException, IOException {
		source.backUp(backupPath, backupName);
	}

}
