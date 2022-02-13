/* Aether2DImgMaker -- console app to generate images of the Aether cellular automaton in 2D
    Copyright (C) 2017-2022 Jaume Ribas

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
package cellularautomata.model4d;

import java.io.FileNotFoundException;
import java.io.IOException;

import cellularautomata.model.ModelProcessor;
import cellularautomata.model3d.ActionableModel3D;
import cellularautomata.model3d.Model3D;

public class ActionableModel4DWXDiagonalCrossSection<G1 extends Model4D, G2 extends Model3D> 
	extends ActionableModel3D<G2> {

	protected ActionableModel4D<G1> source;
	protected int xOffsetFromW;	
	protected int crossSectionMinW;
	protected int crossSectionMaxW;
	protected int crossSectionMinY;
	protected int crossSectionMaxY;
	protected int crossSectionMinZ;
	protected int crossSectionMaxZ;
	
	public ActionableModel4DWXDiagonalCrossSection(ActionableModel4D<G1> grid, int xOffsetFromW) {
		this.source = grid;
		this.xOffsetFromW = xOffsetFromW;
		if (!getBounds()) {
			throw new IllegalArgumentException("Cross section is out of bounds.");
		}
		this.source.addProcessor(new InternalProcessor());
	}
	
	@Override
	public String getXLabel() {
		return source.getWLabel();
	}
	
	@Override
	public String getYLabel() {
		return source.getYLabel();
	}
	
	@Override
	public String getZLabel() {
		return source.getZLabel();
	}
	
	protected boolean getBounds() {
		int w = source.getMinW();
		int maxW = source.getMaxW();
		int crossSectionX = w + xOffsetFromW;
		while (w <= maxW && (crossSectionX < source.getMinXAtW(w) || crossSectionX > source.getMaxXAtW(w))) {
			w++;
			crossSectionX++;
		}
		if (w <= maxW) {
			crossSectionMinW = w;
			crossSectionMaxW = w;
			crossSectionMaxY = source.getMaxYAtWX(w, crossSectionX);
			crossSectionMinY = source.getMinYAtWX(w, crossSectionX);
			crossSectionMaxZ = source.getMaxZAtWX(w, crossSectionX);
			crossSectionMinZ = source.getMinZAtWX(w, crossSectionX);
			w++;
			crossSectionX++;
			while (w <= maxW && crossSectionX >= source.getMinXAtW(w) && crossSectionX <= source.getMaxXAtW(w)) {
				crossSectionMaxW = w;
				int localMaxY = source.getMaxYAtWX(w, crossSectionX), localMinY = source.getMinYAtWX(w, crossSectionX);
				if (localMaxY > crossSectionMaxY) {
					crossSectionMaxY = localMaxY;
				}
				if (localMinY < crossSectionMinY) {
					crossSectionMinY = localMinY;
				}
				int localMaxZ = source.getMaxZAtWX(w, crossSectionX), localMinZ = source.getMinZAtWX(w, crossSectionX);
				if (localMaxZ > crossSectionMaxZ) {
					crossSectionMaxZ = localMaxZ;
				}
				if (localMinZ < crossSectionMinZ) {
					crossSectionMinZ = localMinZ;
				}
				w++;
				crossSectionX++;
			}
			return true;
		} else {
			return false;
		}
	}
	
	@Override
	public void processModel() throws Exception {
		source.processModel();
	}

	@Override
	public int getMinX() { 
		return crossSectionMinW; 
	}

	@Override
	public int getMaxX() { 
		return crossSectionMaxW; 
	}

	@Override
	public int getMinXAtY(int y) { 
		for (int crossSectionW = crossSectionMinW, crossSectionX = crossSectionW + xOffsetFromW; crossSectionW <= crossSectionMaxW; crossSectionW++, crossSectionX++) {
			int localMaxY = source.getMaxYAtWX(crossSectionW, crossSectionX), localMinY = source.getMinYAtWX(crossSectionW, crossSectionX);
			if (y >= localMinY && y <= localMaxY) {
				return crossSectionW;
			}
		}
		throw new IllegalArgumentException("Y coordinate out of bounds.");
	}

	@Override
	public int getMaxXAtY(int y) { 
		for (int crossSectionW = crossSectionMaxW, crossSectionX = crossSectionW + xOffsetFromW; crossSectionW >= crossSectionMinW; crossSectionW--, crossSectionX--) {
			int localMaxY = source.getMaxYAtWX(crossSectionW, crossSectionX), localMinY = source.getMinYAtWX(crossSectionW, crossSectionX);
			if (y >= localMinY && y <= localMaxY) {
				return crossSectionW;
			}
		}
		throw new IllegalArgumentException("Y coordinate out of bounds.");
	}

	@Override
	public int getMinXAtZ(int z) { 
		for (int crossSectionW = crossSectionMinW, crossSectionX = crossSectionW + xOffsetFromW; crossSectionW <= crossSectionMaxW; crossSectionW++, crossSectionX++) {
			int localMaxZ = source.getMaxZAtWX(crossSectionW, crossSectionX), localMinZ = source.getMinZAtWX(crossSectionW, crossSectionX);
			if (z >= localMinZ && z <= localMaxZ) {
				return crossSectionW;
			}
		}
		throw new IllegalArgumentException("Z coordinate out of bounds.");
	}

	@Override
	public int getMaxXAtZ(int z) {
		for (int crossSectionW = crossSectionMaxW, crossSectionX = crossSectionW + xOffsetFromW; crossSectionW >= crossSectionMinW; crossSectionW--, crossSectionX--) {
			int localMaxZ = source.getMaxZAtWX(crossSectionW, crossSectionX), localMinZ = source.getMinZAtWX(crossSectionW, crossSectionX);
			if (z >= localMinZ && z <= localMaxZ) {
				return crossSectionW;
			}
		}
		throw new IllegalArgumentException("Z coordinate out of bounds.");
	}

	@Override
	public int getMinX(int y, int z) { 
		for (int crossSectionW = crossSectionMinW, crossSectionX = crossSectionW + xOffsetFromW; crossSectionW <= crossSectionMaxW; crossSectionW++, crossSectionX++) {
			int localMaxY = source.getMaxYAtWX(crossSectionW, crossSectionX), localMinY = source.getMinYAtWX(crossSectionW, crossSectionX);
			if (y >= localMinY && y <= localMaxY) {
				int localMaxZ = source.getMaxZ(crossSectionW, crossSectionX, y), localMinZ = source.getMinZ(crossSectionW, crossSectionX, y);
				if (z >= localMinZ && z <= localMaxZ) {
					return crossSectionW;
				}
			}
		}
		throw new IllegalArgumentException("Coordinates out of bounds.");
	}

	@Override
	public int getMaxX(int y, int z) { 
		for (int crossSectionW = crossSectionMaxW, crossSectionX = crossSectionW + xOffsetFromW; crossSectionW >= crossSectionMinW; crossSectionW--, crossSectionX--) {
			int localMaxY = source.getMaxYAtWX(crossSectionW, crossSectionX), localMinY = source.getMinYAtWX(crossSectionW, crossSectionX);
			if (y >= localMinY && y <= localMaxY) {
				int localMaxZ = source.getMaxZ(crossSectionW, crossSectionX, y), localMinZ = source.getMinZ(crossSectionW, crossSectionX, y);
				if (z >= localMinZ && z <= localMaxZ) {
					return crossSectionW;
				}
			}
		}
		throw new IllegalArgumentException("Coordinates out of bounds.");
	}

	@Override
	public int getMinY() { 
		return crossSectionMinY;
	}

	@Override
	public int getMaxY() { 
		return crossSectionMaxY;
	}

	@Override
	public int getMinYAtX(int x) { 
		return source.getMinYAtWX(x, x + xOffsetFromW);
	}

	@Override
	public int getMaxYAtX(int x) { 
		return source.getMaxYAtWX(x, x + xOffsetFromW);
	}

	@Override
	public int getMinYAtZ(int z) { 
		return source.getMinYAtZ(z);
	}

	@Override
	public int getMaxYAtZ(int z) { 
		return source.getMaxYAtZ(z);
	}

	@Override
	public int getMinY(int x, int z) { 
		return source.getMinY(x, x + xOffsetFromW, z);
	}

	@Override
	public int getMaxY(int x, int z) {
		return source.getMaxY(x, x + xOffsetFromW, z);
	}

	@Override
	public int getMinZ() {
		return crossSectionMinZ;
	}

	@Override
	public int getMaxZ() { 
		return crossSectionMaxZ;
	}

	@Override
	public int getMinZAtX(int x) {
		return source.getMinZAtWX(x, x + xOffsetFromW);
	}

	@Override
	public int getMaxZAtX(int x) {
		return source.getMaxZAtWX(x, x + xOffsetFromW);
	}

	@Override
	public int getMinZAtY(int y) { 
		return source.getMinZAtY(y); 
	}

	@Override
	public int getMaxZAtY(int y) {
		return source.getMaxZAtY(y); 
	}

	@Override
	public int getMinZ(int x, int y) {
		return source.getMinZ(x, x + xOffsetFromW, y);
	}

	@Override
	public int getMaxZ(int x, int y) {
		return source.getMaxZ(x, x + xOffsetFromW, y);
	}

	@Override
	public boolean nextStep() throws Exception {
		boolean changed = source.nextStep();
		if (!getBounds()) {
			throw new UnsupportedOperationException("Cross section is out of bounds.");
		}
		return changed;
	}

	@Override
	public long getStep() {
		return source.getStep();
	}

	@Override
	public String getName() {
		return source.getName();
	}

	@Override
	public String getSubfolderPath() {
		String path = source.getSubfolderPath() + "/" + source.getXLabel() + "=" + source.getWLabel();
		if (xOffsetFromW < 0) {
			path += xOffsetFromW;
		} else if (xOffsetFromW > 0) {
			path += "+" + xOffsetFromW;
		}
		return path;
	}

	@Override
	public void backUp(String backupPath, String backupName) throws FileNotFoundException, IOException {
		source.backUp(backupPath, backupName);
	}
	
	private class InternalProcessor implements ModelProcessor<G1> {

		@Override
		public void beforeProcessing() throws Exception {
			triggerBeforeProcessing();		
		}

		@SuppressWarnings("unchecked")
		@Override
		public void processModelBlock(G1 gridBlock) throws Exception {
			int w = gridBlock.getMinW();
			int maxW = gridBlock.getMaxW();
			int crossSectionX = w + xOffsetFromW;
			while (w <= maxW && (crossSectionX < gridBlock.getMinXAtW(w) || crossSectionX > gridBlock.getMaxXAtW(w))) {
				w++;
				crossSectionX++;
			}
			if (w <= maxW) {
				triggerProcessModelBlock((G2) gridBlock.diagonalCrossSectionOnWX(xOffsetFromW));
			}
		}

		@Override
		public void afterProcessing() throws Exception {
			triggerAfterProcessing();		
		}
		
	}
}
