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
package cellularautomata.evolvinggrid5d;

import java.io.FileNotFoundException;
import java.io.IOException;

import cellularautomata.evolvinggrid3d.ActionableEvolvingGrid3D;
import cellularautomata.grid.GridProcessor;
import cellularautomata.grid3d.Grid3D;
import cellularautomata.grid5d.Grid5D;

public class ActionableEvolvingGrid5DYZCrossSection<G1 extends Grid5D, G2 extends Grid3D> 
	extends ActionableEvolvingGrid3D<G2> {

	protected int y;
	protected int z;	
	protected ActionableEvolvingGrid5D<G1> source;

	public ActionableEvolvingGrid5DYZCrossSection(ActionableEvolvingGrid5D<G1> grid, int y, int z) {
		if (y > grid.getMaxY() || y < grid.getMinY()) {
			throw new IllegalArgumentException("Y coordinate outside of grid bounds.");
		}
		if (z > grid.getMaxZAtY(y) || z < grid.getMinZAtY(y)) {
			throw new IllegalArgumentException("Z coordinate outside of grid bounds.");
		}
		this.source = grid;
		this.z = z;
		this.source.addProcessor(new InternalProcessor());
	}
	
	@Override
	public void processGrid() throws Exception {
		source.processGrid();
	}
	
	public int getY() {
		return y;
	}
	
	public int getZ() {
		return z;
	}
	
	// v -> x, w -> y, x -> z

	@Override
	public int getMinX() {
		return source.getMinVAtYZ(y, z);
	}
	
	@Override
	public int getMinXAtY(int y) {
		return source.getMinVAtWYZ(y, this.y, z);
	}
	
	@Override
	public int getMinXAtZ(int z) {
		return source.getMinVAtXYZ(z, y, this.z);
	}
	
	@Override
	public int getMinX(int y, int z) {
		return source.getMinV(y, z, this.y, this.z);
	}

	@Override
	public int getMaxX() {
		return source.getMaxVAtYZ(y, z);
	}
	
	@Override
	public int getMaxXAtY(int y) {
		return source.getMaxVAtWYZ(y, this.y, z);
	}
	
	@Override
	public int getMaxXAtZ(int z) {
		return source.getMaxVAtXYZ(z, y, this.z);
	}
	
	@Override
	public int getMaxX(int y, int z) {
		return source.getMaxV(y, z, this.y, this.z);
	}

	@Override
	public int getMinY() {
		return source.getMinWAtYZ(y, z);
	}
	
	@Override
	public int getMinYAtX(int x) {
		return source.getMinWAtVYZ(x, y, z);
	}
	
	@Override
	public int getMinYAtZ(int z) {
		return source.getMinWAtXYZ(z, y, this.z);
	}
	
	@Override
	public int getMinY(int x, int z) {
		return source.getMinW(x, z, y, this.z);
	}
	
	@Override
	public int getMaxY() {
		return source.getMaxWAtYZ(y, z);
	}
	
	@Override
	public int getMaxYAtX(int x) {
		return source.getMaxWAtVYZ(x, y, z);
	}
	
	@Override
	public int getMaxYAtZ(int z) {
		return source.getMaxWAtXYZ(z, y, this.z);
	}
	
	@Override
	public int getMaxY(int x, int z) {
		return source.getMaxW(x, z, y, this.z);
	}
	
	@Override
	public int getMinZ() {
		return source.getMinXAtYZ(y, z);
	}
	
	@Override
	public int getMinZAtX(int x) {
		return source.getMinXAtVYZ(x, y, z);
	}
	
	@Override
	public int getMinZAtY(int y) {
		return source.getMinXAtWYZ(y, this.y, z);
	}
	
	@Override
	public int getMinZ(int x, int y) {
		return source.getMinX(x, y, this.y, z);
	}
	
	@Override
	public int getMaxZ() {
		return source.getMaxXAtYZ(y, z);
	}
	
	@Override
	public int getMaxZAtX(int x) {
		return source.getMaxXAtVYZ(x, y, z);
	}
	
	@Override
	public int getMaxZAtY(int y) {
		return source.getMaxXAtWYZ(y, this.y, z);
	}
	
	@Override
	public int getMaxZ(int x, int y) {
		return source.getMaxX(x, y, this.y, z);
	}

	@Override
	public boolean nextStep() throws Exception {
		boolean changed = source.nextStep();
		if (y > source.getMaxY() || y < source.getMinY()) {
			throw new UnsupportedOperationException("Y coordinate outside of grid bounds.");
		}
		if (z > source.getMaxZAtY(y) || z < source.getMinZAtY(y)) {
			throw new UnsupportedOperationException("Z coordinate outside of grid bounds.");
		}
		return changed;
	}

	@Override
	public long getStep() {
		return source.getStep();
	}

	@Override
	public String getName() {
		return source.getName() + "_y=" + y + "_z=" + z;
	}

	@Override
	public String getSubFolderPath() {
		return source.getSubFolderPath() + "/y=" + y + "_z=" + z;
	}

	@Override
	public void backUp(String backupPath, String backupName) throws FileNotFoundException, IOException {
		source.backUp(backupPath, backupName);
	}
	
	private class InternalProcessor implements GridProcessor<G1> {

		@Override
		public void beforeProcessing() throws Exception {
			triggerBeforeProcessing();		
		}

		@SuppressWarnings("unchecked")
		@Override
		public void processGridBlock(G1 gridBlock) throws Exception {
			if (y >= gridBlock.getMinY() && y <= gridBlock.getMaxY()
					&& z >= gridBlock.getMinZAtY(y) && z <= gridBlock.getMaxZAtY(y)) {
				triggerProcessGridBlock((G2) gridBlock.crossSectionAtYZ(y, z));
			}
		}

		@Override
		public void afterProcessing() throws Exception {
			triggerAfterProcessing();		
		}
		
	}
}
