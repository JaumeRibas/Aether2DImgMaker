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

import cellularautomata.model3d.Model3D;

public class Model4DWXDiagonalCrossSection<Source_Type extends Model4D> implements Model3D {

	protected Source_Type source;
	protected int slope;
	protected int xOffsetFromW;
	protected int crossSectionMinW;
	protected int crossSectionMaxW;
	protected int crossSectionMinY;
	protected int crossSectionMaxY;
	protected int crossSectionMinZ;
	protected int crossSectionMaxZ;
	
	public Model4DWXDiagonalCrossSection(Source_Type source, boolean positiveSlope, int xOffsetFromW) {		
		this.source = source;
		this.slope = positiveSlope ? 1 : -1;
		this.xOffsetFromW = xOffsetFromW;
		if (!getBounds()) {
			throw new IllegalArgumentException("Cross section is out of bounds.");
		}
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
		int crossSectionX = slope*w + xOffsetFromW;
		while (w <= maxW && (crossSectionX < source.getMinXAtW(w) || crossSectionX > source.getMaxXAtW(w))) {
			w++;
			crossSectionX += slope;
		}
		if (w <= maxW) {
			crossSectionMinW = w;
			crossSectionMaxW = w;
			crossSectionMaxY = source.getMaxYAtWX(w, crossSectionX);
			crossSectionMinY = source.getMinYAtWX(w, crossSectionX);
			crossSectionMaxZ = source.getMaxZAtWX(w, crossSectionX);
			crossSectionMinZ = source.getMinZAtWX(w, crossSectionX);
			w++;
			crossSectionX += slope;
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
				crossSectionX += slope;
			}
			return true;
		} else {
			return false;
		}
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
		for (int crossSectionW = crossSectionMinW, crossSectionX = slope*crossSectionW + xOffsetFromW; crossSectionW <= crossSectionMaxW; crossSectionW++, crossSectionX += slope) {
			int localMaxY = source.getMaxYAtWX(crossSectionW, crossSectionX);
			int localMinY = source.getMinYAtWX(crossSectionW, crossSectionX);
			if (y >= localMinY && y <= localMaxY) {
				return crossSectionW;
			}
		}
		throw new IllegalArgumentException("Y coordinate out of bounds.");
	}

	@Override
	public int getMaxXAtY(int y) { 
		for (int crossSectionW = crossSectionMaxW, crossSectionX = slope*crossSectionW + xOffsetFromW; crossSectionW >= crossSectionMinW; crossSectionW--, crossSectionX -= slope) {
			int localMaxY = source.getMaxYAtWX(crossSectionW, crossSectionX); 
			int localMinY = source.getMinYAtWX(crossSectionW, crossSectionX);
			if (y >= localMinY && y <= localMaxY) {
				return crossSectionW;
			}
		}
		throw new IllegalArgumentException("Y coordinate out of bounds.");
	}

	@Override
	public int getMinXAtZ(int z) { 
		for (int crossSectionW = crossSectionMinW, crossSectionX = slope*crossSectionW + xOffsetFromW; crossSectionW <= crossSectionMaxW; crossSectionW++, crossSectionX += slope) {
			int localMaxZ = source.getMaxZAtWX(crossSectionW, crossSectionX); 
			int localMinZ = source.getMinZAtWX(crossSectionW, crossSectionX);
			if (z >= localMinZ && z <= localMaxZ) {
				return crossSectionW;
			}
		}
		throw new IllegalArgumentException("Z coordinate out of bounds.");
	}

	@Override
	public int getMaxXAtZ(int z) {
		for (int crossSectionW = crossSectionMaxW, crossSectionX = slope*crossSectionW + xOffsetFromW; crossSectionW >= crossSectionMinW; crossSectionW--, crossSectionX -= slope) {
			int localMaxZ = source.getMaxZAtWX(crossSectionW, crossSectionX);
			int localMinZ = source.getMinZAtWX(crossSectionW, crossSectionX);
			if (z >= localMinZ && z <= localMaxZ) {
				return crossSectionW;
			}
		}
		throw new IllegalArgumentException("Z coordinate out of bounds.");
	}

	@Override
	public int getMinX(int y, int z) { 
		for (int crossSectionW = crossSectionMinW, crossSectionX = slope*crossSectionW + xOffsetFromW; crossSectionW <= crossSectionMaxW; crossSectionW++, crossSectionX += slope) {
			if (y >= source.getMinYAtWX(crossSectionW, crossSectionX) 
					&& y <= source.getMaxYAtWX(crossSectionW, crossSectionX) 
					&& z >= source.getMinZ(crossSectionW, crossSectionX, y) 
					&& z <= source.getMaxZ(crossSectionW, crossSectionX, y)) {
				return crossSectionW;
			}
		}
		throw new IllegalArgumentException("Coordinates out of bounds.");
	}

	@Override
	public int getMaxX(int y, int z) { 
		for (int crossSectionW = crossSectionMaxW, crossSectionX = slope*crossSectionW + xOffsetFromW; crossSectionW >= crossSectionMinW; crossSectionW--, crossSectionX -= slope) {
			if (y >= source.getMinYAtWX(crossSectionW, crossSectionX) 
					&& y <= source.getMaxYAtWX(crossSectionW, crossSectionX) 
					&& z >= source.getMinZ(crossSectionW, crossSectionX, y) 
					&& z <= source.getMaxZ(crossSectionW, crossSectionX, y)) {
				return crossSectionW;
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
		return source.getMinYAtWX(x, slope*x + xOffsetFromW);
	}

	@Override
	public int getMaxYAtX(int x) { 
		return source.getMaxYAtWX(x, slope*x + xOffsetFromW);
	}

	@Override
	public int getMinYAtZ(int z) {
		int crossSectionW = crossSectionMinW;
		int crossSectionX = slope*crossSectionW + xOffsetFromW;
		int crossSectionMinYAtZ = source.getMinY(crossSectionW, crossSectionX, z);
		int localMinY;
		for (crossSectionW++, crossSectionX += slope; 
				crossSectionW <= crossSectionMaxW && (localMinY = source.getMinY(crossSectionW, crossSectionX, z)) <= crossSectionMinYAtZ; 
				crossSectionW++, crossSectionX += slope) {
			crossSectionMinYAtZ = localMinY;
		}
		return crossSectionMinYAtZ;
	}

	@Override
	public int getMaxYAtZ(int z) {
		int crossSectionW = crossSectionMinW;
		int crossSectionX = slope*crossSectionW + xOffsetFromW;
		int crossSectionMaxYAtZ = source.getMaxY(crossSectionW, crossSectionX, z);
		int localMaxY;
		for (crossSectionW++, crossSectionX += slope; 
				crossSectionW <= crossSectionMaxW && (localMaxY = source.getMaxY(crossSectionW, crossSectionX, z)) >= crossSectionMaxYAtZ; 
				crossSectionW++, crossSectionX += slope) {
			crossSectionMaxYAtZ = localMaxY;
		}
		return crossSectionMaxYAtZ;
	}

	@Override
	public int getMinY(int x, int z) { 
		return source.getMinY(x, slope*x + xOffsetFromW, z);
	}

	@Override
	public int getMaxY(int x, int z) {
		return source.getMaxY(x, slope*x + xOffsetFromW, z);
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
		return source.getMinZAtWX(x, slope*x + xOffsetFromW);
	}

	@Override
	public int getMaxZAtX(int x) {
		return source.getMaxZAtWX(x, slope*x + xOffsetFromW);
	}

	@Override
	public int getMinZAtY(int y) { 
		int crossSectionW = crossSectionMinW;
		int crossSectionX = slope*crossSectionW + xOffsetFromW;
		int crossSectionMinZAtY = source.getMinZ(crossSectionW, crossSectionX, y);
		int localMinZ;
		for (crossSectionW++, crossSectionX += slope; 
				crossSectionW <= crossSectionMaxW && (localMinZ = source.getMinZ(crossSectionW, crossSectionX, y)) <= crossSectionMinZAtY; 
				crossSectionW++, crossSectionX += slope) {
			crossSectionMinZAtY = localMinZ;
		}
		return crossSectionMinZAtY;
	}

	@Override
	public int getMaxZAtY(int y) {
		int crossSectionW = crossSectionMinW;
		int crossSectionX = slope*crossSectionW + xOffsetFromW;
		int crossSectionMaxZAtY = source.getMaxZ(crossSectionW, crossSectionX, y);
		int localMaxZ;
		for (crossSectionW++, crossSectionX += slope;
				crossSectionW <= crossSectionMaxW && (localMaxZ = source.getMaxZ(crossSectionW, crossSectionX, y)) >= crossSectionMaxZAtY; 
				crossSectionW++, crossSectionX += slope) {
			crossSectionMaxZAtY = localMaxZ;
		}
		return crossSectionMaxZAtY;
	}

	@Override
	public int getMinZ(int x, int y) {
		return source.getMinZ(x, slope*x + xOffsetFromW, y);
	}

	@Override
	public int getMaxZ(int x, int y) {
		return source.getMaxZ(x, slope*x + xOffsetFromW, y);
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
		StringBuilder path = new StringBuilder();
		path.append(source.getSubfolderPath()).append("/").append(source.getXLabel()).append("=");
		if (slope == -1) {
			path.append("-");
		}
		path.append(source.getWLabel());
		if (xOffsetFromW < 0) {
			path.append(xOffsetFromW);
		} else if (xOffsetFromW > 0) {
			path.append("+").append(xOffsetFromW);
		}
		return path.toString();
	}

	@Override
	public void backUp(String backupPath, String backupName) throws FileNotFoundException, IOException {
		source.backUp(backupPath, backupName);
	}

}
