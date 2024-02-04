/* Aether2DImgMaker -- console app to generate images of the Aether cellular automaton in 2D
    Copyright (C) 2017-2024 Jaume Ribas

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
	        throw new IllegalArgumentException("The cross section is out of bounds.");
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
	        crossSectionMinX = source.getMinXAtVW(v, crossSectionW);
	        crossSectionMaxX = source.getMaxXAtVW(v, crossSectionW);
	        crossSectionMinY = source.getMinYAtVW(v, crossSectionW);
	        crossSectionMaxY = source.getMaxYAtVW(v, crossSectionW);
	        crossSectionMinZ = source.getMinZAtVW(v, crossSectionW);
	        crossSectionMaxZ = source.getMaxZAtVW(v, crossSectionW);
	        v++;
	        crossSectionW += slope;
	        while (v <= maxV && crossSectionW >= source.getMinWAtV(v) && crossSectionW <= source.getMaxWAtV(v)) {
	            crossSectionMaxV = v;
	            int localMinX = source.getMinXAtVW(v, crossSectionW);
	            if (localMinX < crossSectionMinX) {
	                crossSectionMinX = localMinX;
	            }
	            int localMaxX = source.getMaxXAtVW(v, crossSectionW);
	            if (localMaxX > crossSectionMaxX) {
	                crossSectionMaxX = localMaxX;
	            }
	            int localMinY = source.getMinYAtVW(v, crossSectionW);
	            if (localMinY < crossSectionMinY) {
	                crossSectionMinY = localMinY;
	            }
	            int localMaxY = source.getMaxYAtVW(v, crossSectionW);
	            if (localMaxY > crossSectionMaxY) {
	                crossSectionMaxY = localMaxY;
	            }
	            int localMinZ = source.getMinZAtVW(v, crossSectionW);
	            if (localMinZ < crossSectionMinZ) {
	                crossSectionMinZ = localMinZ;
	            }
	            int localMaxZ = source.getMaxZAtVW(v, crossSectionW);
	            if (localMaxZ > crossSectionMaxZ) {
	                crossSectionMaxZ = localMaxZ;
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
	        if (x >= source.getMinXAtVW(crossSectionV, crossSectionW)
	                && x <= source.getMaxXAtVW(crossSectionV, crossSectionW)) {
	            return crossSectionV;
	        }
	    }
	    throw new IllegalArgumentException("The coordinate is out of bounds.");
	}

	@Override
	public int getMaxWAtX(int x) {
	    for (int crossSectionV = crossSectionMaxV, crossSectionW = slope*crossSectionV + wOffsetFromV; crossSectionV >= crossSectionMinV; crossSectionV--, crossSectionW -= slope) {
	        if (x >= source.getMinXAtVW(crossSectionV, crossSectionW)
	                && x <= source.getMaxXAtVW(crossSectionV, crossSectionW)) {
	            return crossSectionV;
	        }
	    }
	    throw new IllegalArgumentException("The coordinate is out of bounds.");
	}

	@Override
	public int getMinWAtY(int y) {
	    for (int crossSectionV = crossSectionMinV, crossSectionW = slope*crossSectionV + wOffsetFromV; crossSectionV <= crossSectionMaxV; crossSectionV++, crossSectionW += slope) {
	        if (y >= source.getMinYAtVW(crossSectionV, crossSectionW)
	                && y <= source.getMaxYAtVW(crossSectionV, crossSectionW)) {
	            return crossSectionV;
	        }
	    }
	    throw new IllegalArgumentException("The coordinate is out of bounds.");
	}

	@Override
	public int getMaxWAtY(int y) {
	    for (int crossSectionV = crossSectionMaxV, crossSectionW = slope*crossSectionV + wOffsetFromV; crossSectionV >= crossSectionMinV; crossSectionV--, crossSectionW -= slope) {
	        if (y >= source.getMinYAtVW(crossSectionV, crossSectionW)
	                && y <= source.getMaxYAtVW(crossSectionV, crossSectionW)) {
	            return crossSectionV;
	        }
	    }
	    throw new IllegalArgumentException("The coordinate is out of bounds.");
	}

	@Override
	public int getMinWAtZ(int z) {
	    for (int crossSectionV = crossSectionMinV, crossSectionW = slope*crossSectionV + wOffsetFromV; crossSectionV <= crossSectionMaxV; crossSectionV++, crossSectionW += slope) {
	        if (z >= source.getMinZAtVW(crossSectionV, crossSectionW)
	                && z <= source.getMaxZAtVW(crossSectionV, crossSectionW)) {
	            return crossSectionV;
	        }
	    }
	    throw new IllegalArgumentException("The coordinate is out of bounds.");
	}

	@Override
	public int getMaxWAtZ(int z) {
	    for (int crossSectionV = crossSectionMaxV, crossSectionW = slope*crossSectionV + wOffsetFromV; crossSectionV >= crossSectionMinV; crossSectionV--, crossSectionW -= slope) {
	        if (z >= source.getMinZAtVW(crossSectionV, crossSectionW)
	                && z <= source.getMaxZAtVW(crossSectionV, crossSectionW)) {
	            return crossSectionV;
	        }
	    }
	    throw new IllegalArgumentException("The coordinate is out of bounds.");
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
	    throw new IllegalArgumentException("The coordinates are out of bounds.");
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
	    throw new IllegalArgumentException("The coordinates are out of bounds.");
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
	    throw new IllegalArgumentException("The coordinates are out of bounds.");
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
	    throw new IllegalArgumentException("The coordinates are out of bounds.");
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
	    throw new IllegalArgumentException("The coordinates are out of bounds.");
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
	    throw new IllegalArgumentException("The coordinates are out of bounds.");
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
	    throw new IllegalArgumentException("The coordinates are out of bounds.");
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
	    throw new IllegalArgumentException("The coordinates are out of bounds.");
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
	    int result = source.getMinXAtVWY(crossSectionV, crossSectionW, y);
	    int localMinX;
	    for (crossSectionV++, crossSectionW += slope;
	            crossSectionV <= crossSectionMaxV && (localMinX = source.getMinXAtVWY(crossSectionV, crossSectionW, y)) <= result;
	            crossSectionV++, crossSectionW += slope) {
	        result = localMinX;
	    }
	    return result;
	}

	@Override
	public int getMaxXAtY(int y) {
	    int crossSectionV = crossSectionMinV;
	    int crossSectionW = slope*crossSectionV + wOffsetFromV;
	    int result = source.getMaxXAtVWY(crossSectionV, crossSectionW, y);
	    int localMaxX;
	    for (crossSectionV++, crossSectionW += slope;
	            crossSectionV <= crossSectionMaxV && (localMaxX = source.getMaxXAtVWY(crossSectionV, crossSectionW, y)) >= result;
	            crossSectionV++, crossSectionW += slope) {
	        result = localMaxX;
	    }
	    return result;
	}

	@Override
	public int getMinXAtZ(int z) {
	    int crossSectionV = crossSectionMinV;
	    int crossSectionW = slope*crossSectionV + wOffsetFromV;
	    int result = source.getMinXAtVWZ(crossSectionV, crossSectionW, z);
	    int localMinX;
	    for (crossSectionV++, crossSectionW += slope;
	            crossSectionV <= crossSectionMaxV && (localMinX = source.getMinXAtVWZ(crossSectionV, crossSectionW, z)) <= result;
	            crossSectionV++, crossSectionW += slope) {
	        result = localMinX;
	    }
	    return result;
	}

	@Override
	public int getMaxXAtZ(int z) {
	    int crossSectionV = crossSectionMinV;
	    int crossSectionW = slope*crossSectionV + wOffsetFromV;
	    int result = source.getMaxXAtVWZ(crossSectionV, crossSectionW, z);
	    int localMaxX;
	    for (crossSectionV++, crossSectionW += slope;
	            crossSectionV <= crossSectionMaxV && (localMaxX = source.getMaxXAtVWZ(crossSectionV, crossSectionW, z)) >= result;
	            crossSectionV++, crossSectionW += slope) {
	        result = localMaxX;
	    }
	    return result;
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
	    int result = source.getMinX(crossSectionV, crossSectionW, y, z);
	    int localMinX;
	    for (crossSectionV++, crossSectionW += slope;
	            crossSectionV <= crossSectionMaxV && (localMinX = source.getMinX(crossSectionV, crossSectionW, y, z)) <= result;
	            crossSectionV++, crossSectionW += slope) {
	        result = localMinX;
	    }
	    return result;
	}

	@Override
	public int getMaxXAtYZ(int y, int z) {
	    int crossSectionV = crossSectionMinV;
	    int crossSectionW = slope*crossSectionV + wOffsetFromV;
	    int result = source.getMaxX(crossSectionV, crossSectionW, y, z);
	    int localMaxX;
	    for (crossSectionV++, crossSectionW += slope;
	            crossSectionV <= crossSectionMaxV && (localMaxX = source.getMaxX(crossSectionV, crossSectionW, y, z)) >= result;
	            crossSectionV++, crossSectionW += slope) {
	        result = localMaxX;
	    }
	    return result;
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
	    int result = source.getMinYAtVWX(crossSectionV, crossSectionW, x);
	    int localMinY;
	    for (crossSectionV++, crossSectionW += slope;
	            crossSectionV <= crossSectionMaxV && (localMinY = source.getMinYAtVWX(crossSectionV, crossSectionW, x)) <= result;
	            crossSectionV++, crossSectionW += slope) {
	        result = localMinY;
	    }
	    return result;
	}

	@Override
	public int getMaxYAtX(int x) {
	    int crossSectionV = crossSectionMinV;
	    int crossSectionW = slope*crossSectionV + wOffsetFromV;
	    int result = source.getMaxYAtVWX(crossSectionV, crossSectionW, x);
	    int localMaxY;
	    for (crossSectionV++, crossSectionW += slope;
	            crossSectionV <= crossSectionMaxV && (localMaxY = source.getMaxYAtVWX(crossSectionV, crossSectionW, x)) >= result;
	            crossSectionV++, crossSectionW += slope) {
	        result = localMaxY;
	    }
	    return result;
	}

	@Override
	public int getMinYAtZ(int z) {
	    int crossSectionV = crossSectionMinV;
	    int crossSectionW = slope*crossSectionV + wOffsetFromV;
	    int result = source.getMinYAtVWZ(crossSectionV, crossSectionW, z);
	    int localMinY;
	    for (crossSectionV++, crossSectionW += slope;
	            crossSectionV <= crossSectionMaxV && (localMinY = source.getMinYAtVWZ(crossSectionV, crossSectionW, z)) <= result;
	            crossSectionV++, crossSectionW += slope) {
	        result = localMinY;
	    }
	    return result;
	}

	@Override
	public int getMaxYAtZ(int z) {
	    int crossSectionV = crossSectionMinV;
	    int crossSectionW = slope*crossSectionV + wOffsetFromV;
	    int result = source.getMaxYAtVWZ(crossSectionV, crossSectionW, z);
	    int localMaxY;
	    for (crossSectionV++, crossSectionW += slope;
	            crossSectionV <= crossSectionMaxV && (localMaxY = source.getMaxYAtVWZ(crossSectionV, crossSectionW, z)) >= result;
	            crossSectionV++, crossSectionW += slope) {
	        result = localMaxY;
	    }
	    return result;
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
	    int result = source.getMinY(crossSectionV, crossSectionW, x, z);
	    int localMinY;
	    for (crossSectionV++, crossSectionW += slope;
	            crossSectionV <= crossSectionMaxV && (localMinY = source.getMinY(crossSectionV, crossSectionW, x, z)) <= result;
	            crossSectionV++, crossSectionW += slope) {
	        result = localMinY;
	    }
	    return result;
	}

	@Override
	public int getMaxYAtXZ(int x, int z) {
	    int crossSectionV = crossSectionMinV;
	    int crossSectionW = slope*crossSectionV + wOffsetFromV;
	    int result = source.getMaxY(crossSectionV, crossSectionW, x, z);
	    int localMaxY;
	    for (crossSectionV++, crossSectionW += slope;
	            crossSectionV <= crossSectionMaxV && (localMaxY = source.getMaxY(crossSectionV, crossSectionW, x, z)) >= result;
	            crossSectionV++, crossSectionW += slope) {
	        result = localMaxY;
	    }
	    return result;
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
	    int result = source.getMinZAtVWX(crossSectionV, crossSectionW, x);
	    int localMinZ;
	    for (crossSectionV++, crossSectionW += slope;
	            crossSectionV <= crossSectionMaxV && (localMinZ = source.getMinZAtVWX(crossSectionV, crossSectionW, x)) <= result;
	            crossSectionV++, crossSectionW += slope) {
	        result = localMinZ;
	    }
	    return result;
	}

	@Override
	public int getMaxZAtX(int x) {
	    int crossSectionV = crossSectionMinV;
	    int crossSectionW = slope*crossSectionV + wOffsetFromV;
	    int result = source.getMaxZAtVWX(crossSectionV, crossSectionW, x);
	    int localMaxZ;
	    for (crossSectionV++, crossSectionW += slope;
	            crossSectionV <= crossSectionMaxV && (localMaxZ = source.getMaxZAtVWX(crossSectionV, crossSectionW, x)) >= result;
	            crossSectionV++, crossSectionW += slope) {
	        result = localMaxZ;
	    }
	    return result;
	}

	@Override
	public int getMinZAtY(int y) {
	    int crossSectionV = crossSectionMinV;
	    int crossSectionW = slope*crossSectionV + wOffsetFromV;
	    int result = source.getMinZAtVWY(crossSectionV, crossSectionW, y);
	    int localMinZ;
	    for (crossSectionV++, crossSectionW += slope;
	            crossSectionV <= crossSectionMaxV && (localMinZ = source.getMinZAtVWY(crossSectionV, crossSectionW, y)) <= result;
	            crossSectionV++, crossSectionW += slope) {
	        result = localMinZ;
	    }
	    return result;
	}

	@Override
	public int getMaxZAtY(int y) {
	    int crossSectionV = crossSectionMinV;
	    int crossSectionW = slope*crossSectionV + wOffsetFromV;
	    int result = source.getMaxZAtVWY(crossSectionV, crossSectionW, y);
	    int localMaxZ;
	    for (crossSectionV++, crossSectionW += slope;
	            crossSectionV <= crossSectionMaxV && (localMaxZ = source.getMaxZAtVWY(crossSectionV, crossSectionW, y)) >= result;
	            crossSectionV++, crossSectionW += slope) {
	        result = localMaxZ;
	    }
	    return result;
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
	    int result = source.getMinZ(crossSectionV, crossSectionW, x, y);
	    int localMinZ;
	    for (crossSectionV++, crossSectionW += slope;
	            crossSectionV <= crossSectionMaxV && (localMinZ = source.getMinZ(crossSectionV, crossSectionW, x, y)) <= result;
	            crossSectionV++, crossSectionW += slope) {
	        result = localMinZ;
	    }
	    return result;
	}

	@Override
	public int getMaxZAtXY(int x, int y) {
	    int crossSectionV = crossSectionMinV;
	    int crossSectionW = slope*crossSectionV + wOffsetFromV;
	    int result = source.getMaxZ(crossSectionV, crossSectionW, x, y);
	    int localMaxZ;
	    for (crossSectionV++, crossSectionW += slope;
	            crossSectionV <= crossSectionMaxV && (localMaxZ = source.getMaxZ(crossSectionV, crossSectionW, x, y)) >= result;
	            crossSectionV++, crossSectionW += slope) {
	        result = localMaxZ;
	    }
	    return result;
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
	public Boolean nextStep() throws Exception {
	    Boolean changed = source.nextStep();
	    if (!getBounds()) {
	        throw new UnsupportedOperationException("The cross section is out of bounds.");
	    }
	    return changed;
	}

	@Override
	public Boolean isChanged() {
	    return source.isChanged();
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
	    StringBuilder path = new StringBuilder(source.getSubfolderPath()).append("/").append(source.getWLabel()).append("=");
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
	public void backUp(String backupPath, String backupName) throws Exception {
	    source.backUp(backupPath, backupName);
	}

}
