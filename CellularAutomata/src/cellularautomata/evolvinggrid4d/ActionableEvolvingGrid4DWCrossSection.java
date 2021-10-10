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

public class ActionableEvolvingGrid4DWCrossSection<G1 extends Grid4D, G2 extends Grid3D> 
	extends ActionableEvolvingGrid3D<G2> {

	protected int w;	
	protected ActionableEvolvingGrid4D<G1> source;

	public ActionableEvolvingGrid4DWCrossSection(ActionableEvolvingGrid4D<G1> grid, int w) {
		if (w > grid.getMaxW() || w < grid.getMinW()) {
			throw new IllegalArgumentException("W coordinate outside of grid bounds.");
		}
		this.source = grid;
		this.w = w;
		this.source.addProcessor(new InternalProcessor());
	}
	
	@Override
	public void processGrid() throws Exception {
		source.processGrid();
	}
	
	public int getW() {
		return w;
	}

	@Override
	public int getMinX() {
		return source.getMinXAtW(w);
	}
	
	@Override
	public int getMinXAtY(int y) {
		return source.getMinXAtWY(w, y);
	}
	
	@Override
	public int getMinXAtZ(int z) {
		return source.getMinXAtWZ(w, z);
	}
	
	@Override
	public int getMinX(int y, int z) {
		return source.getMinX(w, y, z);
	}

	@Override
	public int getMaxX() {
		return source.getMaxXAtW(w);
	}
	
	@Override
	public int getMaxXAtY(int y) {
		return source.getMaxXAtWY(w, y);
	}
	
	@Override
	public int getMaxXAtZ(int z) {
		return source.getMaxXAtWZ(w, z);
	}
	
	@Override
	public int getMaxX(int y, int z) {
		return source.getMaxX(w, y, z);
	}

	@Override
	public int getMinY() {
		return source.getMinYAtW(w);
	}
	
	@Override
	public int getMinYAtX(int x) {
		return source.getMinYAtWX(w, x);
	}
	
	@Override
	public int getMinYAtZ(int z) {
		return source.getMinYAtWZ(w, z);
	}
	
	@Override
	public int getMinY(int x, int z) {
		return source.getMinY(w, x, z);
	}
	
	@Override
	public int getMaxY() {
		return source.getMaxYAtW(w);
	}
	
	@Override
	public int getMaxYAtX(int x) {
		return source.getMaxYAtWX(w, x);
	}
	
	@Override
	public int getMaxYAtZ(int z) {
		return source.getMaxYAtWZ(w, z);
	}
	
	@Override
	public int getMaxY(int x, int z) {
		return source.getMaxY(w, x, z);
	}
	
	@Override
	public int getMinZ() {
		return source.getMinZAtW(w);
	}
	
	@Override
	public int getMinZAtX(int x) {
		return source.getMinZAtWX(w, x);
	}
	
	@Override
	public int getMinZAtY(int y) {
		return source.getMinZAtWY(w, y);
	}
	
	@Override
	public int getMinZ(int x, int y) {
		return source.getMinZ(w, x, y);
	}
	
	@Override
	public int getMaxZ() {
		return source.getMaxZAtW(w);
	}
	
	@Override
	public int getMaxZAtX(int x) {
		return source.getMaxZAtWX(w, x);
	}
	
	@Override
	public int getMaxZAtY(int y) {
		return source.getMaxZAtWY(w, y);
	}
	
	@Override
	public int getMaxZ(int x, int y) {
		return source.getMaxZ(w, x, y);
	}

	@Override
	public boolean nextStep() throws Exception {
		boolean changed = source.nextStep();
		if (w > source.getMaxW() || w < source.getMinW()) {
			throw new UnsupportedOperationException("W coordinate outside of grid bounds.");
		}
		return changed;
	}

	@Override
	public long getStep() {
		return source.getStep();
	}

	@Override
	public String getName() {
		return source.getName() + "_w=" + w;
	}

	@Override
	public String getSubFolderPath() {
		return source.getSubFolderPath() + "/w=" + w;
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
			if (w >= gridBlock.getMinW() && w <= gridBlock.getMaxW()) {
				triggerProcessGridBlock((G2) gridBlock.crossSectionAtW(w));
			}
		}

		@Override
		public void afterProcessing() throws Exception {
			triggerAfterProcessing();		
		}
		
	}
}