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
package cellularautomata.model5d;

import java.io.FileNotFoundException;
import java.io.IOException;

import cellularautomata.model4d.Model4D;

public class Model5DVWDiagonalCrossSection<Source_Type extends Model5D> implements Model4D {

	protected Source_Type source;
	protected int slope;
	protected int wOffsetFromV;
	protected int crossSectionMinV;
	protected int crossSectionMaxV;
	protected int crossSectionMinX;
	protected int crossSectionMaxX;
	protected int crossSectionMinY;
	protected int crossSectionMaxY;
	protected int crossSectionMinZ;
	protected int crossSectionMaxZ;
	
	public Model5DVWDiagonalCrossSection(Source_Type source, boolean positiveSlope, int wOffsetFromV) {		
		this.source = source;
		this.slope = positiveSlope ? 1 : -1;
		this.wOffsetFromV = wOffsetFromV;
		if (!getBounds()) {
			throw new IllegalArgumentException("Cross section is out of bounds.");
		}
	}
	
	@Override
	public String getWLabel() {
		return source.getVLabel();
	}
	
	@Override
	public String getXLabel() {
		return source.getXLabel();
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
		int v = source.getMinV();
		int maxV = source.getMaxV();
		int crossSectionW = slope*v + wOffsetFromV;
		while (v <= maxV && (crossSectionW < source.getMinWAtV(v) || crossSectionW > source.getMaxWAtV(v))) {
			v++;
			crossSectionW += slope;
		}
		if (v <= maxV) {
			crossSectionMinV = v;
			crossSectionMaxV = v;
			crossSectionMaxX = source.getMaxXAtVW(v, crossSectionW);
			crossSectionMinX = source.getMinXAtVW(v, crossSectionW);
			crossSectionMaxY = source.getMaxYAtVW(v, crossSectionW);
			crossSectionMinY = source.getMinYAtVW(v, crossSectionW);
			crossSectionMaxZ = source.getMaxZAtVW(v, crossSectionW);
			crossSectionMinZ = source.getMinZAtVW(v, crossSectionW);
			v++;
			crossSectionW += slope;
			while (v <= maxV && crossSectionW >= source.getMinWAtV(v) && crossSectionW <= source.getMaxWAtV(v)) {
				crossSectionMaxV = v;
				int localMaxX = source.getMaxXAtVW(v, crossSectionW), localMinX = source.getMinXAtVW(v, crossSectionW);
				if (localMaxX > crossSectionMaxX) {
					crossSectionMaxX = localMaxX;
				}
				if (localMinX < crossSectionMinX) {
					crossSectionMinX = localMinX;
				}
				int localMaxY = source.getMaxYAtVW(v, crossSectionW), localMinY = source.getMinYAtVW(v, crossSectionW);
				if (localMaxY > crossSectionMaxY) {
					crossSectionMaxY = localMaxY;
				}
				if (localMinY < crossSectionMinY) {
					crossSectionMinY = localMinY;
				}
				int localMaxZ = source.getMaxZAtVW(v, crossSectionW), localMinZ = source.getMinZAtVW(v, crossSectionW);
				if (localMaxZ > crossSectionMaxZ) {
					crossSectionMaxZ = localMaxZ;
				}
				if (localMinZ < crossSectionMinZ) {
					crossSectionMinZ = localMinZ;
				}
				v++;
				crossSectionW += slope;
			}
			return true;
		} else {
			return false;
		}
	}
	
	@Override
	public int getMinW() { 
		return crossSectionMinV; 
	}

	@Override
	public int getMaxW() {
		return crossSectionMaxV; 
	}

	@Override
	public int getMinWAtX(int x) {
		for (int crossSectionV = crossSectionMinV, crossSectionW = slope*crossSectionV + wOffsetFromV; crossSectionV <= crossSectionMaxV; crossSectionV++, crossSectionW += slope) {
			int localMaxX = source.getMaxXAtVW(crossSectionV, crossSectionW), localMinX = source.getMinXAtVW(crossSectionV, crossSectionW);
			if (x >= localMinX && x <= localMaxX) {
				return crossSectionV;
			}
		}
		throw new IllegalArgumentException("X coordinate out of bounds.");
	}

	@Override
	public int getMaxWAtX(int x) {
		for (int crossSectionV = crossSectionMaxV, crossSectionW = slope*crossSectionV + wOffsetFromV; crossSectionV >= crossSectionMinV; crossSectionV--, crossSectionW -= slope) {
			int localMaxX = source.getMaxXAtVW(crossSectionV, crossSectionW), localMinX = source.getMinXAtVW(crossSectionV, crossSectionW);
			if (x >= localMinX && x <= localMaxX) {
				return crossSectionV;
			}
		}
		throw new IllegalArgumentException("X coordinate out of bounds.");
	}

	@Override
	public int getMinWAtY(int y) {
		for (int crossSectionV = crossSectionMinV, crossSectionW = slope*crossSectionV + wOffsetFromV; crossSectionV <= crossSectionMaxV; crossSectionV++, crossSectionW += slope) {
			int localMaxY = source.getMaxYAtVW(crossSectionV, crossSectionW), localMinY = source.getMinYAtVW(crossSectionV, crossSectionW);
			if (y >= localMinY && y <= localMaxY) {
				return crossSectionV;
			}
		}
		throw new IllegalArgumentException("Y coordinate out of bounds.");
	}

	@Override
	public int getMaxWAtY(int y) {
		for (int crossSectionV = crossSectionMaxV, crossSectionW = slope*crossSectionV + wOffsetFromV; crossSectionV >= crossSectionMinV; crossSectionV--, crossSectionW -= slope) {
			int localMaxY = source.getMaxYAtVW(crossSectionV, crossSectionW), localMinY = source.getMinYAtVW(crossSectionV, crossSectionW);
			if (y >= localMinY && y <= localMaxY) {
				return crossSectionV;
			}
		}
		throw new IllegalArgumentException("Y coordinate out of bounds.");
	}

	@Override
	public int getMinWAtZ(int z) {
		for (int crossSectionV = crossSectionMinV, crossSectionW = slope*crossSectionV + wOffsetFromV; crossSectionV <= crossSectionMaxV; crossSectionV++, crossSectionW += slope) {
			int localMaxZ = source.getMaxZAtVW(crossSectionV, crossSectionW), localMinZ = source.getMinZAtVW(crossSectionV, crossSectionW);
			if (z >= localMinZ && z <= localMaxZ) {
				return crossSectionV;
			}
		}
		throw new IllegalArgumentException("Z coordinate out of bounds.");
	}

	@Override
	public int getMaxWAtZ(int z) {
		for (int crossSectionV = crossSectionMaxV, crossSectionW = slope*crossSectionV + wOffsetFromV; crossSectionV >= crossSectionMinV; crossSectionV--, crossSectionW -= slope) {
			int localMaxZ = source.getMaxZAtVW(crossSectionV, crossSectionW), localMinZ = source.getMinZAtVW(crossSectionV, crossSectionW);
			if (z >= localMinZ && z <= localMaxZ) {
				return crossSectionV;
			}
		}
		throw new IllegalArgumentException("Z coordinate out of bounds.");
	}

	@Override
	public int getMinWAtXY(int x, int y) {
		for (int crossSectionV = crossSectionMinV, crossSectionW = slope*crossSectionV + wOffsetFromV; crossSectionV <= crossSectionMaxV; crossSectionV++, crossSectionW += slope) {
			int localMaxX = source.getMaxXAtVW(crossSectionV, crossSectionW), localMinX = source.getMinXAtVW(crossSectionV, crossSectionW);
			if (x >= localMinX && x <= localMaxX) {
				int localMaxY = source.getMaxYAtVWX(crossSectionV, crossSectionW, x), localMinY = source.getMinYAtVWX(crossSectionV, crossSectionW, x);
				if (y >= localMinY && y <= localMaxY) {
					return crossSectionV;
				}
			}
		}
		throw new IllegalArgumentException("Coordinates out of bounds.");
	}

	@Override
	public int getMaxWAtXY(int x, int y) {
		for (int crossSectionV = crossSectionMaxV, crossSectionW = slope*crossSectionV + wOffsetFromV; crossSectionV >= crossSectionMinV; crossSectionV--, crossSectionW -= slope) {
			int localMaxX = source.getMaxXAtVW(crossSectionV, crossSectionW), localMinX = source.getMinXAtVW(crossSectionV, crossSectionW);
			if (x >= localMinX && x <= localMaxX) {
				int localMaxY = source.getMaxYAtVWX(crossSectionV, crossSectionW, x), localMinY = source.getMinYAtVWX(crossSectionV, crossSectionW, x);
				if (y >= localMinY && y <= localMaxY) {
					return crossSectionV;
				}
			}
		}
		throw new IllegalArgumentException("Coordinates out of bounds.");
	}

	@Override
	public int getMinWAtXZ(int x, int z) {
		for (int crossSectionV = crossSectionMinV, crossSectionW = slope*crossSectionV + wOffsetFromV; crossSectionV <= crossSectionMaxV; crossSectionV++, crossSectionW += slope) {
			int localMaxX = source.getMaxXAtVW(crossSectionV, crossSectionW), localMinX = source.getMinXAtVW(crossSectionV, crossSectionW);
			if (x >= localMinX && x <= localMaxX) {
				int localMaxZ = source.getMaxZAtVWX(crossSectionV, crossSectionW, x), localMinZ = source.getMinZAtVWX(crossSectionV, crossSectionW, x);
				if (z >= localMinZ && z <= localMaxZ) {
					return crossSectionV;
				}
			}
		}
		throw new IllegalArgumentException("Coordinates out of bounds.");
	}

	@Override
	public int getMaxWAtXZ(int x, int z) {
		for (int crossSectionV = crossSectionMaxV, crossSectionW = slope*crossSectionV + wOffsetFromV; crossSectionV >= crossSectionMinV; crossSectionV--, crossSectionW -= slope) {
			int localMaxX = source.getMaxXAtVW(crossSectionV, crossSectionW), localMinX = source.getMinXAtVW(crossSectionV, crossSectionW);
			if (x >= localMinX && x <= localMaxX) {
				int localMaxZ = source.getMaxZAtVWX(crossSectionV, crossSectionW, x), localMinZ = source.getMinZAtVWX(crossSectionV, crossSectionW, x);
				if (z >= localMinZ && z <= localMaxZ) {
					return crossSectionV;
				}
			}
		}
		throw new IllegalArgumentException("Coordinates out of bounds.");
	}

	@Override
	public int getMinWAtYZ(int y, int z) {
		for (int crossSectionV = crossSectionMinV, crossSectionW = slope*crossSectionV + wOffsetFromV; crossSectionV <= crossSectionMaxV; crossSectionV++, crossSectionW += slope) {
			int localMaxY = source.getMaxYAtVW(crossSectionV, crossSectionW), localMinY = source.getMinYAtVW(crossSectionV, crossSectionW);
			if (y >= localMinY && y <= localMaxY) {
				int localMaxZ = source.getMaxZAtVWY(crossSectionV, crossSectionW, y), localMinZ = source.getMinZAtVWY(crossSectionV, crossSectionW, y);
				if (z >= localMinZ && z <= localMaxZ) {
					return crossSectionV;
				}
			}
		}
		throw new IllegalArgumentException("Coordinates out of bounds.");
	}

	@Override
	public int getMaxWAtYZ(int y, int z) {
		for (int crossSectionV = crossSectionMaxV, crossSectionW = slope*crossSectionV + wOffsetFromV; crossSectionV >= crossSectionMinV; crossSectionV--, crossSectionW -= slope) {
			int localMaxY = source.getMaxYAtVW(crossSectionV, crossSectionW), localMinY = source.getMinYAtVW(crossSectionV, crossSectionW);
			if (y >= localMinY && y <= localMaxY) {
				int localMaxZ = source.getMaxZAtVWY(crossSectionV, crossSectionW, y), localMinZ = source.getMinZAtVWY(crossSectionV, crossSectionW, y);
				if (z >= localMinZ && z <= localMaxZ) {
					return crossSectionV;
				}
			}
		}
		throw new IllegalArgumentException("Coordinates out of bounds.");
	}

	@Override
	public int getMinW(int x, int y, int z) {
		for (int crossSectionV = crossSectionMinV, crossSectionW = slope*crossSectionV + wOffsetFromV; crossSectionV <= crossSectionMaxV; crossSectionV++, crossSectionW += slope) {
			int localMaxX = source.getMaxXAtVW(crossSectionV, crossSectionW), localMinX = source.getMinXAtVW(crossSectionV, crossSectionW);
			if (x >= localMinX && x <= localMaxX) {
				int localMaxY = source.getMaxYAtVWX(crossSectionV, crossSectionW, x), localMinY = source.getMinYAtVWX(crossSectionV, crossSectionW, x);
				if (y >= localMinY && y <= localMaxY) {
					int localMaxZ = source.getMaxZ(crossSectionV, crossSectionW, x, y), localMinZ = source.getMinZ(crossSectionV, crossSectionW, x, y);
					if (z >= localMinZ && z <= localMaxZ) {
						return crossSectionV;
					}
				}
			}
		}
		throw new IllegalArgumentException("Coordinates out of bounds.");
	}

	@Override
	public int getMaxW(int x, int y, int z) {
		for (int crossSectionV = crossSectionMaxV, crossSectionW = slope*crossSectionV + wOffsetFromV; crossSectionV >= crossSectionMinV; crossSectionV--, crossSectionW -= slope) {
			int localMaxX = source.getMaxXAtVW(crossSectionV, crossSectionW), localMinX = source.getMinXAtVW(crossSectionV, crossSectionW);
			if (x >= localMinX && x <= localMaxX) {
				int localMaxY = source.getMaxYAtVWX(crossSectionV, crossSectionW, x), localMinY = source.getMinYAtVWX(crossSectionV, crossSectionW, x);
				if (y >= localMinY && y <= localMaxY) {
					int localMaxZ = source.getMaxZ(crossSectionV, crossSectionW, x, y), localMinZ = source.getMinZ(crossSectionV, crossSectionW, x, y);
					if (z >= localMinZ && z <= localMaxZ) {
						return crossSectionV;
					}
				}
			}
		}
		throw new IllegalArgumentException("Coordinates out of bounds.");
	}

	@Override
	public int getMinX() {
		return crossSectionMinX;
	}

	@Override
	public int getMaxX() {
		return crossSectionMaxX;
	}

	@Override
	public int getMinXAtW(int w) {
		return source.getMinXAtVW(w, slope*w + wOffsetFromV);
	}

	@Override
	public int getMaxXAtW(int w) {
		return source.getMaxXAtVW(w, slope*w + wOffsetFromV);
	}

	@Override
	public int getMinXAtY(int y) {
		return source.getMinXAtY(y);
	}

	@Override
	public int getMaxXAtY(int y) {
		return source.getMaxXAtY(y);
	}

	@Override
	public int getMinXAtZ(int z) {
		return source.getMinXAtZ(z);
	}

	@Override
	public int getMaxXAtZ(int z) {
		return source.getMaxXAtZ(z);
	}

	@Override
	public int getMinXAtWY(int w, int y) {
		return source.getMinXAtVWY(w, slope*w + wOffsetFromV, y);
	}

	@Override
	public int getMaxXAtWY(int w, int y) {
		return source.getMaxXAtVWY(w, slope*w + wOffsetFromV, y);
	}

	@Override
	public int getMinXAtWZ(int w, int z) {
		return source.getMinXAtVWZ(w, slope*w + wOffsetFromV, z);
	}

	@Override
	public int getMaxXAtWZ(int w, int z) {
		return source.getMaxXAtVWZ(w, slope*w + wOffsetFromV, z);
	}

	@Override
	public int getMinXAtYZ(int y, int z) {
		return source.getMinXAtYZ(y, z);
	}

	@Override
	public int getMaxXAtYZ(int y, int z) {
		return source.getMaxXAtYZ(y, z);
	}

	@Override
	public int getMinX(int w, int y, int z) {
		return source.getMinX(w, slope*w + wOffsetFromV, y, z);
	}

	@Override
	public int getMaxX(int w, int y, int z) {
		return source.getMaxX(w, slope*w + wOffsetFromV, y, z);
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
	public int getMinYAtW(int w) {
		return source.getMinYAtVW(w, slope*w + wOffsetFromV);
	}

	@Override
	public int getMaxYAtW(int w) {
		return source.getMaxYAtVW(w, slope*w + wOffsetFromV);
	}

	@Override
	public int getMinYAtX(int x) {
		return source.getMinYAtX(x);
	}

	@Override
	public int getMaxYAtX(int x) {
		return source.getMaxYAtX(x);
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
	public int getMinYAtWX(int w, int x) {
		return source.getMinYAtVWX(w, slope*w + wOffsetFromV, x);
	}

	@Override
	public int getMaxYAtWX(int w, int x) {
		return source.getMaxYAtVWX(w, slope*w + wOffsetFromV, x);
	}

	@Override
	public int getMinYAtWZ(int w, int z) {
		return source.getMinYAtVWZ(w, slope*w + wOffsetFromV, z);
	}

	@Override
	public int getMaxYAtWZ(int w, int z) {
		return source.getMaxYAtVWZ(w, slope*w + wOffsetFromV, z);
	}

	@Override
	public int getMinYAtXZ(int x, int z) {
		return source.getMinYAtXZ(x, z);
	}

	@Override
	public int getMaxYAtXZ(int x, int z) {
		return source.getMaxYAtXZ(x, z);
	}

	@Override
	public int getMinY(int w, int x, int z) {
		return source.getMinY(w, slope*w + wOffsetFromV, x, z);
	}

	@Override
	public int getMaxY(int w, int x, int z) {
		return source.getMaxY(w, slope*w + wOffsetFromV, x, z);
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
	public int getMinZAtW(int w) {
		return source.getMinZAtVW(w, slope*w + wOffsetFromV);
	}

	@Override
	public int getMaxZAtW(int w) {
		return source.getMaxZAtVW(w, slope*w + wOffsetFromV);
	}

	@Override
	public int getMinZAtX(int x) {
		return source.getMinZAtX(x);
	}

	@Override
	public int getMaxZAtX(int x) {
		return source.getMaxZAtX(x);
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
	public int getMinZAtWX(int w, int x) {
		return source.getMinZAtVWX(w, slope*w + wOffsetFromV, x);
	}

	@Override
	public int getMaxZAtWX(int w, int x) {
		return source.getMaxZAtVWX(w, slope*w + wOffsetFromV, x);
	}

	@Override
	public int getMinZAtWY(int w, int y) {
		return source.getMinZAtVWY(w, slope*w + wOffsetFromV, y);
	}

	@Override
	public int getMaxZAtWY(int w, int y) {
		return source.getMaxZAtVWY(w, slope*w + wOffsetFromV, y);
	}

	@Override
	public int getMinZAtXY(int x, int y) {
		return source.getMinZAtXY(x, y);
	}

	@Override
	public int getMaxZAtXY(int x, int y) {
		return source.getMaxZAtXY(x, y);
	}

	@Override
	public int getMinZ(int w, int x, int y) {
		return source.getMinZ(w, slope*w + wOffsetFromV, x, y);
	}

	@Override
	public int getMaxZ(int w, int x, int y) {
		return source.getMaxZ(w, slope*w + wOffsetFromV, x, y);
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
		path.append(source.getSubfolderPath()).append("/").append(source.getWLabel()).append("=");
		if (slope == -1) {
			path.append("-");
		}
		path.append(source.getVLabel());
		if (wOffsetFromV < 0) {
			path.append(wOffsetFromV);
		} else if (wOffsetFromV > 0) {
			path.append("+").append(wOffsetFromV);
		}
		return path.toString();
	}

	@Override
	public void backUp(String backupPath, String backupName) throws FileNotFoundException, IOException {
		source.backUp(backupPath, backupName);
	}

}
