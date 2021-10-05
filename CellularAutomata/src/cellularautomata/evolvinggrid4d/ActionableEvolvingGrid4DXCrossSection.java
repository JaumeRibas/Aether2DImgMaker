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
package cellularautomata.evolvinggrid4d;

import java.io.FileNotFoundException;
import java.io.IOException;

import cellularautomata.evolvinggrid3d.ActionableEvolvingGrid3D;
import cellularautomata.grid.GridProcessor;
import cellularautomata.grid3d.Grid3D;
import cellularautomata.grid4d.Grid4D;

public class ActionableEvolvingGrid4DXCrossSection<G1 extends Grid4D, G2 extends Grid3D> 
	extends ActionableEvolvingGrid3D<G2> {

	protected int x;	
	protected ActionableEvolvingGrid4D<G1> source;

	public ActionableEvolvingGrid4DXCrossSection(ActionableEvolvingGrid4D<G1> grid, int x) {
		if (x > grid.getMaxX() || x < grid.getMinX()) {
			throw new IllegalArgumentException("X coordinate outside of grid bounds.");
		}
		this.source = grid;
		this.x = x;
		this.source.addProcessor(new InternalProcessor());
	}
	
	@Override
	public void processGrid() throws Exception {
		source.processGrid();
	}
	
	public int getX() {
		return x;
	}

	/*
	 * x -> w
	 * y -> y
	 * z -> z
	 */
	
	@Override
	public int getMinX() {
		return source.getMinWAtX(x);
	}
	
	@Override
	public int getMinXAtY(int y) {
		return source.getMinWAtXY(this.x, y);
	}
	
	@Override
	public int getMinXAtZ(int z) {
		return source.getMinWAtXZ(this.x, z);
	}
	
	@Override
	public int getMinX(int y, int z) {
		return source.getMinW(this.x, y, z);
	}

	@Override
	public int getMaxX() {
		return source.getMaxWAtX(this.x);
	}
	
	@Override
	public int getMaxXAtY(int y) {
		return source.getMaxWAtXY(this.x, y);
	}
	
	@Override
	public int getMaxXAtZ(int z) {
		return source.getMaxWAtXZ(this.x, z);
	}
	
	@Override
	public int getMaxX(int y, int z) {
		return source.getMaxW(this.x, y, z);
	}

	@Override
	public int getMinY() {
		return source.getMinYAtX(this.x);
	}
	
	@Override
	public int getMinYAtX(int x) {
		return source.getMinYAtWX(x, this.x);
	}
	
	@Override
	public int getMinYAtZ(int z) {
		return source.getMinYAtXZ(this.x, z);
	}
	
	@Override
	public int getMinY(int x, int z) {
		return source.getMinY(x, this.x, z);
	}
	
	@Override
	public int getMaxY() {
		return source.getMaxYAtX(this.x);
	}
	
	@Override
	public int getMaxYAtX(int x) {
		return source.getMaxYAtWX(x, this.x);
	}
	
	@Override
	public int getMaxYAtZ(int z) {
		return source.getMaxYAtXZ(this.x, z);
	}
	
	@Override
	public int getMaxY(int x, int z) {
		return source.getMaxY(x, this.x, z);
	}
	
	@Override
	public int getMinZ() {
		return source.getMinZAtX(this.x);
	}
	
	@Override
	public int getMinZAtX(int x) {
		return source.getMinZAtWX(x, this.x);
	}
	
	@Override
	public int getMinZAtY(int y) {
		return source.getMinZAtXY(this.x, y);
	}
	
	@Override
	public int getMinZ(int x, int y) {
		return source.getMinZ(x, this.x, y);
	}
	
	@Override
	public int getMaxZ() {
		return source.getMaxZAtX(this.x);
	}
	
	@Override
	public int getMaxZAtX(int x) {
		return source.getMaxZAtWX(x, this.x);
	}
	
	@Override
	public int getMaxZAtY(int y) {
		return source.getMaxZAtXY(this.x, y);
	}
	
	@Override
	public int getMaxZ(int x, int y) {
		return source.getMaxZ(x, this.x, y);
	}

	@Override
	public boolean nextStep() throws Exception {
		boolean changed = source.nextStep();
		if (x > source.getMaxX() || x < source.getMinX()) {
			throw new UnsupportedOperationException("X coordinate outside of grid bounds.");
		}
		return changed;
	}

	@Override
	public long getStep() {
		return source.getStep();
	}

	@Override
	public String getName() {
		return source.getName() + "_x=" + x;
	}

	@Override
	public String getSubFolderPath() {
		return source.getSubFolderPath() + "/x=" + x;
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
			if (x >= gridBlock.getMinX() && x <= gridBlock.getMaxX()) {
				triggerProcessGridBlock((G2) gridBlock.crossSectionAtX(x));
			}
		}

		@Override
		public void afterProcessing() throws Exception {
			triggerAfterProcessing();		
		}
		
	}
}
