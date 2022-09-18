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
			if (x >= source.getMinXAtVW(crossSectionV, crossSectionW) 
					&& x <= source.getMaxXAtVW(crossSectionV, crossSectionW) 
					&& y >= source.getMinYAtVWX(crossSectionV, crossSectionW, x) 
					&& y <= source.getMaxYAtVWX(crossSectionV, crossSectionW, x)) {
				return crossSectionV;
			}
		}
		throw new IllegalArgumentException("Coordinates out of bounds.");
	}

	@Override
	public int getMaxWAtXY(int x, int y) {
		for (int crossSectionV = crossSectionMaxV, crossSectionW = slope*crossSectionV + wOffsetFromV; crossSectionV >= crossSectionMinV; crossSectionV--, crossSectionW -= slope) {
			if (x >= source.getMinXAtVW(crossSectionV, crossSectionW) 
					&& x <= source.getMaxXAtVW(crossSectionV, crossSectionW) 
					&& y >= source.getMinYAtVWX(crossSectionV, crossSectionW, x) 
					&& y <= source.getMaxYAtVWX(crossSectionV, crossSectionW, x)) {
				return crossSectionV;
			}
		}
		throw new IllegalArgumentException("Coordinates out of bounds.");
	}

	@Override
	public int getMinWAtXZ(int x, int z) {
		for (int crossSectionV = crossSectionMinV, crossSectionW = slope*crossSectionV + wOffsetFromV; crossSectionV <= crossSectionMaxV; crossSectionV++, crossSectionW += slope) {
			if (x >= source.getMinXAtVW(crossSectionV, crossSectionW) 
					&& x <= source.getMaxXAtVW(crossSectionV, crossSectionW) 
					&& z >= source.getMinZAtVWX(crossSectionV, crossSectionW, x) 
					&& z <= source.getMaxZAtVWX(crossSectionV, crossSectionW, x)) {
				return crossSectionV;
			}
		}
		throw new IllegalArgumentException("Coordinates out of bounds.");
	}

	@Override
	public int getMaxWAtXZ(int x, int z) {
		for (int crossSectionV = crossSectionMaxV, crossSectionW = slope*crossSectionV + wOffsetFromV; crossSectionV >= crossSectionMinV; crossSectionV--, crossSectionW -= slope) {
			if (x >= source.getMinXAtVW(crossSectionV, crossSectionW) 
					&& x <= source.getMaxXAtVW(crossSectionV, crossSectionW) 
					&& z >= source.getMinZAtVWX(crossSectionV, crossSectionW, x) 
					&& z <= source.getMaxZAtVWX(crossSectionV, crossSectionW, x)) {
				return crossSectionV;
			}
		}
		throw new IllegalArgumentException("Coordinates out of bounds.");
	}

	@Override
	public int getMinWAtYZ(int y, int z) {
		for (int crossSectionV = crossSectionMinV, crossSectionW = slope*crossSectionV + wOffsetFromV; crossSectionV <= crossSectionMaxV; crossSectionV++, crossSectionW += slope) {
			if (y >= source.getMinYAtVW(crossSectionV, crossSectionW) 
					&& y <= source.getMaxYAtVW(crossSectionV, crossSectionW) 
					&& z >= source.getMinZAtVWY(crossSectionV, crossSectionW, y) 
					&& z <= source.getMaxZAtVWY(crossSectionV, crossSectionW, y)) {
				return crossSectionV;
			}
		}
		throw new IllegalArgumentException("Coordinates out of bounds.");
	}

	@Override
	public int getMaxWAtYZ(int y, int z) {
		for (int crossSectionV = crossSectionMaxV, crossSectionW = slope*crossSectionV + wOffsetFromV; crossSectionV >= crossSectionMinV; crossSectionV--, crossSectionW -= slope) {
			if (y >= source.getMinYAtVW(crossSectionV, crossSectionW) 
					&& y <= source.getMaxYAtVW(crossSectionV, crossSectionW) 
					&& z >= source.getMinZAtVWY(crossSectionV, crossSectionW, y) 
					&& z <= source.getMaxZAtVWY(crossSectionV, crossSectionW, y)) {
				return crossSectionV;
			}
		}
		throw new IllegalArgumentException("Coordinates out of bounds.");
	}

	@Override
	public int getMinW(int x, int y, int z) {
		for (int crossSectionV = crossSectionMinV, crossSectionW = slope*crossSectionV + wOffsetFromV; crossSectionV <= crossSectionMaxV; crossSectionV++, crossSectionW += slope) {
			if (x >= source.getMinXAtVW(crossSectionV, crossSectionW) 
					&& x <= source.getMaxXAtVW(crossSectionV, crossSectionW) 
					&& y >= source.getMinYAtVWX(crossSectionV, crossSectionW, x) 
					&& y <= source.getMaxYAtVWX(crossSectionV, crossSectionW, x) 
					&& z >= source.getMinZ(crossSectionV, crossSectionW, x, y)
					&& z <= source.getMaxZ(crossSectionV, crossSectionW, x, y)) {
				return crossSectionV;
			}
		}
		throw new IllegalArgumentException("Coordinates out of bounds.");
	}

	@Override
	public int getMaxW(int x, int y, int z) {
		for (int crossSectionV = crossSectionMaxV, crossSectionW = slope*crossSectionV + wOffsetFromV; crossSectionV >= crossSectionMinV; crossSectionV--, crossSectionW -= slope) {
			if (x >= source.getMinXAtVW(crossSectionV, crossSectionW) 
					&& x <= source.getMaxXAtVW(crossSectionV, crossSectionW) 
					&& y >= source.getMinYAtVWX(crossSectionV, crossSectionW, x) 
					&& y <= source.getMaxYAtVWX(crossSectionV, crossSectionW, x) 
					&& z >= source.getMinZ(crossSectionV, crossSectionW, x, y)
					&& z <= source.getMaxZ(crossSectionV, crossSectionW, x, y)) {
				return crossSectionV;
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
		int crossSectionV = crossSectionMinV;
		int crossSectionW = slope*crossSectionV + wOffsetFromV;
		int crossSectionMinXAtY = source.getMinXAtVWY(crossSectionV, crossSectionW, y);
		int localMinX;
		for (crossSectionV++, crossSectionW += slope;
				crossSectionV <= crossSectionMaxV && (localMinX = source.getMinXAtVWY(crossSectionV, crossSectionW, y)) <= crossSectionMinXAtY; 
				crossSectionV++, crossSectionW += slope) {
			crossSectionMinXAtY = localMinX;
		}
		return crossSectionMinXAtY;
	}

	@Override
	public int getMaxXAtY(int y) {
		int crossSectionV = crossSectionMinV;
		int crossSectionW = slope*crossSectionV + wOffsetFromV;
		int crossSectionMaxXAtY = source.getMaxXAtVWY(crossSectionV, crossSectionW, y);
		int localMaxX;
		for (crossSectionV++, crossSectionW += slope;
				crossSectionV <= crossSectionMaxV && (localMaxX = source.getMaxXAtVWY(crossSectionV, crossSectionW, y)) >= crossSectionMaxXAtY; 
				crossSectionV++, crossSectionW += slope) {
			crossSectionMaxXAtY = localMaxX;
		}
		return crossSectionMaxXAtY;
	}

	@Override
	public int getMinXAtZ(int z) {
		int crossSectionV = crossSectionMinV;
		int crossSectionW = slope*crossSectionV + wOffsetFromV;
		int crossSectionMinXAtZ = source.getMinXAtVWZ(crossSectionV, crossSectionW, z);
		int localMinX;
		for (crossSectionV++, crossSectionW += slope;
				crossSectionV <= crossSectionMaxV && (localMinX = source.getMinXAtVWZ(crossSectionV, crossSectionW, z)) <= crossSectionMinXAtZ; 
				crossSectionV++, crossSectionW += slope) {
			crossSectionMinXAtZ = localMinX;
		}
		return crossSectionMinXAtZ;
	}

	@Override
	public int getMaxXAtZ(int z) {
		int crossSectionV = crossSectionMinV;
		int crossSectionW = slope*crossSectionV + wOffsetFromV;
		int crossSectionMaxXAtZ = source.getMaxXAtVWZ(crossSectionV, crossSectionW, z);
		int localMaxX;
		for (crossSectionV++, crossSectionW += slope;
				crossSectionV <= crossSectionMaxV && (localMaxX = source.getMaxXAtVWZ(crossSectionV, crossSectionW, z)) >= crossSectionMaxXAtZ; 
				crossSectionV++, crossSectionW += slope) {
			crossSectionMaxXAtZ = localMaxX;
		}
		return crossSectionMaxXAtZ;
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
		int crossSectionV = crossSectionMinV;
		int crossSectionW = slope*crossSectionV + wOffsetFromV;
		int crossSectionMinXAtYZ = source.getMinX(crossSectionV, crossSectionW, y, z);
		int localMinX;
		for (crossSectionV++, crossSectionW += slope;
				crossSectionV <= crossSectionMaxV && (localMinX = source.getMinX(crossSectionV, crossSectionW, y, z)) <= crossSectionMinXAtYZ; 
				crossSectionV++, crossSectionW += slope) {
			crossSectionMinXAtYZ = localMinX;
		}
		return crossSectionMinXAtYZ;
	}

	@Override
	public int getMaxXAtYZ(int y, int z) {
		int crossSectionV = crossSectionMinV;
		int crossSectionW = slope*crossSectionV + wOffsetFromV;
		int crossSectionMaxXAtYZ = source.getMaxX(crossSectionV, crossSectionW, y, z);
		int localMaxX;
		for (crossSectionV++, crossSectionW += slope;
				crossSectionV <= crossSectionMaxV && (localMaxX = source.getMaxX(crossSectionV, crossSectionW, y, z)) >= crossSectionMaxXAtYZ; 
				crossSectionV++, crossSectionW += slope) {
			crossSectionMaxXAtYZ = localMaxX;
		}
		return crossSectionMaxXAtYZ;
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
		int crossSectionV = crossSectionMinV;
		int crossSectionW = slope*crossSectionV + wOffsetFromV;
		int crossSectionMinYAtX = source.getMinYAtVWX(crossSectionV, crossSectionW, x);
		int localMinY;
		for (crossSectionV++, crossSectionW += slope;
				crossSectionV <= crossSectionMaxV && (localMinY = source.getMinYAtVWX(crossSectionV, crossSectionW, x)) <= crossSectionMinYAtX; 
				crossSectionV++, crossSectionW += slope) {
			crossSectionMinYAtX = localMinY;
		}
		return crossSectionMinYAtX;
	}

	@Override
	public int getMaxYAtX(int x) {
		int crossSectionV = crossSectionMinV;
		int crossSectionW = slope*crossSectionV + wOffsetFromV;
		int crossSectionMaxYAtX = source.getMaxYAtVWX(crossSectionV, crossSectionW, x);
		int localMaxY;
		for (crossSectionV++, crossSectionW += slope;
				crossSectionV <= crossSectionMaxV && (localMaxY = source.getMaxYAtVWX(crossSectionV, crossSectionW, x)) >= crossSectionMaxYAtX; 
				crossSectionV++, crossSectionW += slope) {
			crossSectionMaxYAtX = localMaxY;
		}
		return crossSectionMaxYAtX;
	}

	@Override
	public int getMinYAtZ(int z) {
		int crossSectionV = crossSectionMinV;
		int crossSectionW = slope*crossSectionV + wOffsetFromV;
		int crossSectionMinYAtZ = source.getMinYAtVWZ(crossSectionV, crossSectionW, z);
		int localMinY;
		for (crossSectionV++, crossSectionW += slope;
				crossSectionV <= crossSectionMaxV && (localMinY = source.getMinYAtVWZ(crossSectionV, crossSectionW, z)) <= crossSectionMinYAtZ; 
				crossSectionV++, crossSectionW += slope) {
			crossSectionMinYAtZ = localMinY;
		}
		return crossSectionMinYAtZ;
	}

	@Override
	public int getMaxYAtZ(int z) {
		int crossSectionV = crossSectionMinV;
		int crossSectionW = slope*crossSectionV + wOffsetFromV;
		int crossSectionMaxYAtZ = source.getMaxYAtVWZ(crossSectionV, crossSectionW, z);
		int localMaxY;
		for (crossSectionV++, crossSectionW += slope;
				crossSectionV <= crossSectionMaxV && (localMaxY = source.getMaxYAtVWZ(crossSectionV, crossSectionW, z)) >= crossSectionMaxYAtZ; 
				crossSectionV++, crossSectionW += slope) {
			crossSectionMaxYAtZ = localMaxY;
		}
		return crossSectionMaxYAtZ;
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
		int crossSectionV = crossSectionMinV;
		int crossSectionW = slope*crossSectionV + wOffsetFromV;
		int crossSectionMinYAtXZ = source.getMinY(crossSectionV, crossSectionW, x, z);
		int localMinY;
		for (crossSectionV++, crossSectionW += slope;
				crossSectionV <= crossSectionMaxV && (localMinY = source.getMinY(crossSectionV, crossSectionW, x, z)) <= crossSectionMinYAtXZ; 
				crossSectionV++, crossSectionW += slope) {
			crossSectionMinYAtXZ = localMinY;
		}
		return crossSectionMinYAtXZ;
	}

	@Override
	public int getMaxYAtXZ(int x, int z) {
		int crossSectionV = crossSectionMinV;
		int crossSectionW = slope*crossSectionV + wOffsetFromV;
		int crossSectionMaxYAtXZ = source.getMaxY(crossSectionV, crossSectionW, x, z);
		int localMaxY;
		for (crossSectionV++, crossSectionW += slope;
				crossSectionV <= crossSectionMaxV && (localMaxY = source.getMaxY(crossSectionV, crossSectionW, x, z)) >= crossSectionMaxYAtXZ; 
				crossSectionV++, crossSectionW += slope) {
			crossSectionMaxYAtXZ = localMaxY;
		}
		return crossSectionMaxYAtXZ;
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
		int crossSectionV = crossSectionMinV;
		int crossSectionW = slope*crossSectionV + wOffsetFromV;
		int crossSectionMinZAtX = source.getMinZAtVWX(crossSectionV, crossSectionW, x);
		int localMinZ;
		for (crossSectionV++, crossSectionW += slope;
				crossSectionV <= crossSectionMaxV && (localMinZ = source.getMinZAtVWX(crossSectionV, crossSectionW, x)) <= crossSectionMinZAtX; 
				crossSectionV++, crossSectionW += slope) {
			crossSectionMinZAtX = localMinZ;
		}
		return crossSectionMinZAtX;
	}

	@Override
	public int getMaxZAtX(int x) {
		int crossSectionV = crossSectionMinV;
		int crossSectionW = slope*crossSectionV + wOffsetFromV;
		int crossSectionMaxZAtX = source.getMaxZAtVWX(crossSectionV, crossSectionW, x);
		int localMaxZ;
		for (crossSectionV++, crossSectionW += slope;
				crossSectionV <= crossSectionMaxV && (localMaxZ = source.getMaxZAtVWX(crossSectionV, crossSectionW, x)) >= crossSectionMaxZAtX; 
				crossSectionV++, crossSectionW += slope) {
			crossSectionMaxZAtX = localMaxZ;
		}
		return crossSectionMaxZAtX;
	}

	@Override
	public int getMinZAtY(int y) {
		int crossSectionV = crossSectionMinV;
		int crossSectionW = slope*crossSectionV + wOffsetFromV;
		int crossSectionMinZAtY = source.getMinZAtVWY(crossSectionV, crossSectionW, y);
		int localMinZ;
		for (crossSectionV++, crossSectionW += slope;
				crossSectionV <= crossSectionMaxV && (localMinZ = source.getMinZAtVWY(crossSectionV, crossSectionW, y)) <= crossSectionMinZAtY; 
				crossSectionV++, crossSectionW += slope) {
			crossSectionMinZAtY = localMinZ;
		}
		return crossSectionMinZAtY;
	}

	@Override
	public int getMaxZAtY(int y) {
		int crossSectionV = crossSectionMinV;
		int crossSectionW = slope*crossSectionV + wOffsetFromV;
		int crossSectionMaxZAtY = source.getMaxZAtVWY(crossSectionV, crossSectionW, y);
		int localMaxZ;
		for (crossSectionV++, crossSectionW += slope;
				crossSectionV <= crossSectionMaxV && (localMaxZ = source.getMaxZAtVWY(crossSectionV, crossSectionW, y)) >= crossSectionMaxZAtY; 
				crossSectionV++, crossSectionW += slope) {
			crossSectionMaxZAtY = localMaxZ;
		}
		return crossSectionMaxZAtY;
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
		int crossSectionV = crossSectionMinV;
		int crossSectionW = slope*crossSectionV + wOffsetFromV;
		int crossSectionMinZAtXY = source.getMinZ(crossSectionV, crossSectionW, x, y);
		int localMinZ;
		for (crossSectionV++, crossSectionW += slope;
				crossSectionV <= crossSectionMaxV && (localMinZ = source.getMinZ(crossSectionV, crossSectionW, x, y)) <= crossSectionMinZAtXY; 
				crossSectionV++, crossSectionW += slope) {
			crossSectionMinZAtXY = localMinZ;
		}
		return crossSectionMinZAtXY;
	}

	@Override
	public int getMaxZAtXY(int x, int y) {
		int crossSectionV = crossSectionMinV;
		int crossSectionW = slope*crossSectionV + wOffsetFromV;
		int crossSectionMaxZAtXY = source.getMaxZ(crossSectionV, crossSectionW, x, y);
		int localMaxZ;
		for (crossSectionV++, crossSectionW += slope;
				crossSectionV <= crossSectionMaxV && (localMaxZ = source.getMaxZ(crossSectionV, crossSectionW, x, y)) >= crossSectionMaxZAtXY; 
				crossSectionV++, crossSectionW += slope) {
			crossSectionMaxZAtXY = localMaxZ;
		}
		return crossSectionMaxZAtXY;
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
