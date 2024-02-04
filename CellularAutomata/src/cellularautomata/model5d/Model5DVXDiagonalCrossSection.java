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

public class Model5DVXDiagonalCrossSection<Source_Type extends Model5D> implements Model4D {

	protected Source_Type source;
	protected int slope;
	protected int xOffsetFromV;
	protected int crossSectionMinV;
	protected int crossSectionMaxV;
	protected int crossSectionMinW;
	protected int crossSectionMaxW;
	protected int crossSectionMinY;
	protected int crossSectionMaxY;
	protected int crossSectionMinZ;
	protected int crossSectionMaxZ;

	public Model5DVXDiagonalCrossSection(Source_Type source, boolean positiveSlope, int xOffsetFromV) {
	    this.source = source;
	    this.slope = positiveSlope ? 1 : -1;
	    this.xOffsetFromV = xOffsetFromV;
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
	    int v = source.getMinV();
	    int maxV = source.getMaxV();
	    int crossSectionX = slope*v + xOffsetFromV;
	    while (v <= maxV && (crossSectionX < source.getMinXAtV(v) || crossSectionX > source.getMaxXAtV(v))) {
	        v++;
	        crossSectionX += slope;
	    }
	    if (v <= maxV) {
	        crossSectionMinV = v;
	        crossSectionMaxV = v;
	        crossSectionMinW = source.getMinWAtVX(v, crossSectionX);
	        crossSectionMaxW = source.getMaxWAtVX(v, crossSectionX);
	        crossSectionMinY = source.getMinYAtVX(v, crossSectionX);
	        crossSectionMaxY = source.getMaxYAtVX(v, crossSectionX);
	        crossSectionMinZ = source.getMinZAtVX(v, crossSectionX);
	        crossSectionMaxZ = source.getMaxZAtVX(v, crossSectionX);
	        v++;
	        crossSectionX += slope;
	        while (v <= maxV && crossSectionX >= source.getMinXAtV(v) && crossSectionX <= source.getMaxXAtV(v)) {
	            crossSectionMaxV = v;
	            int localMinW = source.getMinWAtVX(v, crossSectionX);
	            if (localMinW < crossSectionMinW) {
	                crossSectionMinW = localMinW;
	            }
	            int localMaxW = source.getMaxWAtVX(v, crossSectionX);
	            if (localMaxW > crossSectionMaxW) {
	                crossSectionMaxW = localMaxW;
	            }
	            int localMinY = source.getMinYAtVX(v, crossSectionX);
	            if (localMinY < crossSectionMinY) {
	                crossSectionMinY = localMinY;
	            }
	            int localMaxY = source.getMaxYAtVX(v, crossSectionX);
	            if (localMaxY > crossSectionMaxY) {
	                crossSectionMaxY = localMaxY;
	            }
	            int localMinZ = source.getMinZAtVX(v, crossSectionX);
	            if (localMinZ < crossSectionMinZ) {
	                crossSectionMinZ = localMinZ;
	            }
	            int localMaxZ = source.getMaxZAtVX(v, crossSectionX);
	            if (localMaxZ > crossSectionMaxZ) {
	                crossSectionMaxZ = localMaxZ;
	            }
	            v++;
	            crossSectionX += slope;
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
	    for (int crossSectionV = crossSectionMinV, crossSectionX = slope*crossSectionV + xOffsetFromV; crossSectionV <= crossSectionMaxV; crossSectionV++, crossSectionX += slope) {
	        if (x >= source.getMinWAtVX(crossSectionV, crossSectionX)
	                && x <= source.getMaxWAtVX(crossSectionV, crossSectionX)) {
	            return crossSectionV;
	        }
	    }
	    throw new IllegalArgumentException("The coordinate is out of bounds.");
	}

	@Override
	public int getMaxWAtX(int x) {
	    for (int crossSectionV = crossSectionMaxV, crossSectionX = slope*crossSectionV + xOffsetFromV; crossSectionV >= crossSectionMinV; crossSectionV--, crossSectionX -= slope) {
	        if (x >= source.getMinWAtVX(crossSectionV, crossSectionX)
	                && x <= source.getMaxWAtVX(crossSectionV, crossSectionX)) {
	            return crossSectionV;
	        }
	    }
	    throw new IllegalArgumentException("The coordinate is out of bounds.");
	}

	@Override
	public int getMinWAtY(int y) {
	    for (int crossSectionV = crossSectionMinV, crossSectionX = slope*crossSectionV + xOffsetFromV; crossSectionV <= crossSectionMaxV; crossSectionV++, crossSectionX += slope) {
	        if (y >= source.getMinYAtVX(crossSectionV, crossSectionX)
	                && y <= source.getMaxYAtVX(crossSectionV, crossSectionX)) {
	            return crossSectionV;
	        }
	    }
	    throw new IllegalArgumentException("The coordinate is out of bounds.");
	}

	@Override
	public int getMaxWAtY(int y) {
	    for (int crossSectionV = crossSectionMaxV, crossSectionX = slope*crossSectionV + xOffsetFromV; crossSectionV >= crossSectionMinV; crossSectionV--, crossSectionX -= slope) {
	        if (y >= source.getMinYAtVX(crossSectionV, crossSectionX)
	                && y <= source.getMaxYAtVX(crossSectionV, crossSectionX)) {
	            return crossSectionV;
	        }
	    }
	    throw new IllegalArgumentException("The coordinate is out of bounds.");
	}

	@Override
	public int getMinWAtZ(int z) {
	    for (int crossSectionV = crossSectionMinV, crossSectionX = slope*crossSectionV + xOffsetFromV; crossSectionV <= crossSectionMaxV; crossSectionV++, crossSectionX += slope) {
	        if (z >= source.getMinZAtVX(crossSectionV, crossSectionX)
	                && z <= source.getMaxZAtVX(crossSectionV, crossSectionX)) {
	            return crossSectionV;
	        }
	    }
	    throw new IllegalArgumentException("The coordinate is out of bounds.");
	}

	@Override
	public int getMaxWAtZ(int z) {
	    for (int crossSectionV = crossSectionMaxV, crossSectionX = slope*crossSectionV + xOffsetFromV; crossSectionV >= crossSectionMinV; crossSectionV--, crossSectionX -= slope) {
	        if (z >= source.getMinZAtVX(crossSectionV, crossSectionX)
	                && z <= source.getMaxZAtVX(crossSectionV, crossSectionX)) {
	            return crossSectionV;
	        }
	    }
	    throw new IllegalArgumentException("The coordinate is out of bounds.");
	}

	@Override
	public int getMinWAtXY(int x, int y) {
	    for (int crossSectionV = crossSectionMinV, crossSectionX = slope*crossSectionV + xOffsetFromV; crossSectionV <= crossSectionMaxV; crossSectionV++, crossSectionX += slope) {
	        if (x >= source.getMinWAtVX(crossSectionV, crossSectionX)
	                && x <= source.getMaxWAtVX(crossSectionV, crossSectionX)
	                && y >= source.getMinYAtVWX(crossSectionV, x, crossSectionX)
	                && y <= source.getMaxYAtVWX(crossSectionV, x, crossSectionX)) {
	            return crossSectionV;
	        }
	    }
	    throw new IllegalArgumentException("The coordinates are out of bounds.");
	}

	@Override
	public int getMaxWAtXY(int x, int y) {
	    for (int crossSectionV = crossSectionMaxV, crossSectionX = slope*crossSectionV + xOffsetFromV; crossSectionV >= crossSectionMinV; crossSectionV--, crossSectionX -= slope) {
	        if (x >= source.getMinWAtVX(crossSectionV, crossSectionX)
	                && x <= source.getMaxWAtVX(crossSectionV, crossSectionX)
	                && y >= source.getMinYAtVWX(crossSectionV, x, crossSectionX)
	                && y <= source.getMaxYAtVWX(crossSectionV, x, crossSectionX)) {
	            return crossSectionV;
	        }
	    }
	    throw new IllegalArgumentException("The coordinates are out of bounds.");
	}

	@Override
	public int getMinWAtXZ(int x, int z) {
	    for (int crossSectionV = crossSectionMinV, crossSectionX = slope*crossSectionV + xOffsetFromV; crossSectionV <= crossSectionMaxV; crossSectionV++, crossSectionX += slope) {
	        if (x >= source.getMinWAtVX(crossSectionV, crossSectionX)
	                && x <= source.getMaxWAtVX(crossSectionV, crossSectionX)
	                && z >= source.getMinZAtVWX(crossSectionV, x, crossSectionX)
	                && z <= source.getMaxZAtVWX(crossSectionV, x, crossSectionX)) {
	            return crossSectionV;
	        }
	    }
	    throw new IllegalArgumentException("The coordinates are out of bounds.");
	}

	@Override
	public int getMaxWAtXZ(int x, int z) {
	    for (int crossSectionV = crossSectionMaxV, crossSectionX = slope*crossSectionV + xOffsetFromV; crossSectionV >= crossSectionMinV; crossSectionV--, crossSectionX -= slope) {
	        if (x >= source.getMinWAtVX(crossSectionV, crossSectionX)
	                && x <= source.getMaxWAtVX(crossSectionV, crossSectionX)
	                && z >= source.getMinZAtVWX(crossSectionV, x, crossSectionX)
	                && z <= source.getMaxZAtVWX(crossSectionV, x, crossSectionX)) {
	            return crossSectionV;
	        }
	    }
	    throw new IllegalArgumentException("The coordinates are out of bounds.");
	}

	@Override
	public int getMinWAtYZ(int y, int z) {
	    for (int crossSectionV = crossSectionMinV, crossSectionX = slope*crossSectionV + xOffsetFromV; crossSectionV <= crossSectionMaxV; crossSectionV++, crossSectionX += slope) {
	        if (y >= source.getMinYAtVX(crossSectionV, crossSectionX)
	                && y <= source.getMaxYAtVX(crossSectionV, crossSectionX)
	                && z >= source.getMinZAtVXY(crossSectionV, crossSectionX, y)
	                && z <= source.getMaxZAtVXY(crossSectionV, crossSectionX, y)) {
	            return crossSectionV;
	        }
	    }
	    throw new IllegalArgumentException("The coordinates are out of bounds.");
	}

	@Override
	public int getMaxWAtYZ(int y, int z) {
	    for (int crossSectionV = crossSectionMaxV, crossSectionX = slope*crossSectionV + xOffsetFromV; crossSectionV >= crossSectionMinV; crossSectionV--, crossSectionX -= slope) {
	        if (y >= source.getMinYAtVX(crossSectionV, crossSectionX)
	                && y <= source.getMaxYAtVX(crossSectionV, crossSectionX)
	                && z >= source.getMinZAtVXY(crossSectionV, crossSectionX, y)
	                && z <= source.getMaxZAtVXY(crossSectionV, crossSectionX, y)) {
	            return crossSectionV;
	        }
	    }
	    throw new IllegalArgumentException("The coordinates are out of bounds.");
	}

	@Override
	public int getMinW(int x, int y, int z) {
	    for (int crossSectionV = crossSectionMinV, crossSectionX = slope*crossSectionV + xOffsetFromV; crossSectionV <= crossSectionMaxV; crossSectionV++, crossSectionX += slope) {
	        if (x >= source.getMinWAtVX(crossSectionV, crossSectionX)
	                && x <= source.getMaxWAtVX(crossSectionV, crossSectionX)
	                && y >= source.getMinYAtVWX(crossSectionV, x, crossSectionX)
	                && y <= source.getMaxYAtVWX(crossSectionV, x, crossSectionX)
	                && z >= source.getMinZ(crossSectionV, x, crossSectionX, y)
	                && z <= source.getMaxZ(crossSectionV, x, crossSectionX, y)) {
	            return crossSectionV;
	        }
	    }
	    throw new IllegalArgumentException("The coordinates are out of bounds.");
	}

	@Override
	public int getMaxW(int x, int y, int z) {
	    for (int crossSectionV = crossSectionMaxV, crossSectionX = slope*crossSectionV + xOffsetFromV; crossSectionV >= crossSectionMinV; crossSectionV--, crossSectionX -= slope) {
	        if (x >= source.getMinWAtVX(crossSectionV, crossSectionX)
	                && x <= source.getMaxWAtVX(crossSectionV, crossSectionX)
	                && y >= source.getMinYAtVWX(crossSectionV, x, crossSectionX)
	                && y <= source.getMaxYAtVWX(crossSectionV, x, crossSectionX)
	                && z >= source.getMinZ(crossSectionV, x, crossSectionX, y)
	                && z <= source.getMaxZ(crossSectionV, x, crossSectionX, y)) {
	            return crossSectionV;
	        }
	    }
	    throw new IllegalArgumentException("The coordinates are out of bounds.");
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
	public int getMinXAtW(int w) {
	    return source.getMinWAtVX(w, slope*w + xOffsetFromV);
	}

	@Override
	public int getMaxXAtW(int w) {
	    return source.getMaxWAtVX(w, slope*w + xOffsetFromV);
	}

	@Override
	public int getMinXAtY(int y) {
	    int crossSectionV = crossSectionMinV;
	    int crossSectionX = slope*crossSectionV + xOffsetFromV;
	    int result = source.getMinWAtVXY(crossSectionV, crossSectionX, y);
	    int localMinW;
	    for (crossSectionV++, crossSectionX += slope;
	            crossSectionV <= crossSectionMaxV && (localMinW = source.getMinWAtVXY(crossSectionV, crossSectionX, y)) <= result;
	            crossSectionV++, crossSectionX += slope) {
	        result = localMinW;
	    }
	    return result;
	}

	@Override
	public int getMaxXAtY(int y) {
	    int crossSectionV = crossSectionMinV;
	    int crossSectionX = slope*crossSectionV + xOffsetFromV;
	    int result = source.getMaxWAtVXY(crossSectionV, crossSectionX, y);
	    int localMaxW;
	    for (crossSectionV++, crossSectionX += slope;
	            crossSectionV <= crossSectionMaxV && (localMaxW = source.getMaxWAtVXY(crossSectionV, crossSectionX, y)) >= result;
	            crossSectionV++, crossSectionX += slope) {
	        result = localMaxW;
	    }
	    return result;
	}

	@Override
	public int getMinXAtZ(int z) {
	    int crossSectionV = crossSectionMinV;
	    int crossSectionX = slope*crossSectionV + xOffsetFromV;
	    int result = source.getMinWAtVXZ(crossSectionV, crossSectionX, z);
	    int localMinW;
	    for (crossSectionV++, crossSectionX += slope;
	            crossSectionV <= crossSectionMaxV && (localMinW = source.getMinWAtVXZ(crossSectionV, crossSectionX, z)) <= result;
	            crossSectionV++, crossSectionX += slope) {
	        result = localMinW;
	    }
	    return result;
	}

	@Override
	public int getMaxXAtZ(int z) {
	    int crossSectionV = crossSectionMinV;
	    int crossSectionX = slope*crossSectionV + xOffsetFromV;
	    int result = source.getMaxWAtVXZ(crossSectionV, crossSectionX, z);
	    int localMaxW;
	    for (crossSectionV++, crossSectionX += slope;
	            crossSectionV <= crossSectionMaxV && (localMaxW = source.getMaxWAtVXZ(crossSectionV, crossSectionX, z)) >= result;
	            crossSectionV++, crossSectionX += slope) {
	        result = localMaxW;
	    }
	    return result;
	}

	@Override
	public int getMinXAtWY(int w, int y) {
	    return source.getMinWAtVXY(w, slope*w + xOffsetFromV, y);
	}

	@Override
	public int getMaxXAtWY(int w, int y) {
	    return source.getMaxWAtVXY(w, slope*w + xOffsetFromV, y);
	}

	@Override
	public int getMinXAtWZ(int w, int z) {
	    return source.getMinWAtVXZ(w, slope*w + xOffsetFromV, z);
	}

	@Override
	public int getMaxXAtWZ(int w, int z) {
	    return source.getMaxWAtVXZ(w, slope*w + xOffsetFromV, z);
	}

	@Override
	public int getMinXAtYZ(int y, int z) {
	    int crossSectionV = crossSectionMinV;
	    int crossSectionX = slope*crossSectionV + xOffsetFromV;
	    int result = source.getMinW(crossSectionV, crossSectionX, y, z);
	    int localMinW;
	    for (crossSectionV++, crossSectionX += slope;
	            crossSectionV <= crossSectionMaxV && (localMinW = source.getMinW(crossSectionV, crossSectionX, y, z)) <= result;
	            crossSectionV++, crossSectionX += slope) {
	        result = localMinW;
	    }
	    return result;
	}

	@Override
	public int getMaxXAtYZ(int y, int z) {
	    int crossSectionV = crossSectionMinV;
	    int crossSectionX = slope*crossSectionV + xOffsetFromV;
	    int result = source.getMaxW(crossSectionV, crossSectionX, y, z);
	    int localMaxW;
	    for (crossSectionV++, crossSectionX += slope;
	            crossSectionV <= crossSectionMaxV && (localMaxW = source.getMaxW(crossSectionV, crossSectionX, y, z)) >= result;
	            crossSectionV++, crossSectionX += slope) {
	        result = localMaxW;
	    }
	    return result;
	}

	@Override
	public int getMinX(int w, int y, int z) {
	    return source.getMinW(w, slope*w + xOffsetFromV, y, z);
	}

	@Override
	public int getMaxX(int w, int y, int z) {
	    return source.getMaxW(w, slope*w + xOffsetFromV, y, z);
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
	    return source.getMinYAtVX(w, slope*w + xOffsetFromV);
	}

	@Override
	public int getMaxYAtW(int w) {
	    return source.getMaxYAtVX(w, slope*w + xOffsetFromV);
	}

	@Override
	public int getMinYAtX(int x) {
	    int crossSectionV = crossSectionMinV;
	    int crossSectionX = slope*crossSectionV + xOffsetFromV;
	    int result = source.getMinYAtVWX(crossSectionV, x, crossSectionX);
	    int localMinY;
	    for (crossSectionV++, crossSectionX += slope;
	            crossSectionV <= crossSectionMaxV && (localMinY = source.getMinYAtVWX(crossSectionV, x, crossSectionX)) <= result;
	            crossSectionV++, crossSectionX += slope) {
	        result = localMinY;
	    }
	    return result;
	}

	@Override
	public int getMaxYAtX(int x) {
	    int crossSectionV = crossSectionMinV;
	    int crossSectionX = slope*crossSectionV + xOffsetFromV;
	    int result = source.getMaxYAtVWX(crossSectionV, x, crossSectionX);
	    int localMaxY;
	    for (crossSectionV++, crossSectionX += slope;
	            crossSectionV <= crossSectionMaxV && (localMaxY = source.getMaxYAtVWX(crossSectionV, x, crossSectionX)) >= result;
	            crossSectionV++, crossSectionX += slope) {
	        result = localMaxY;
	    }
	    return result;
	}

	@Override
	public int getMinYAtZ(int z) {
	    int crossSectionV = crossSectionMinV;
	    int crossSectionX = slope*crossSectionV + xOffsetFromV;
	    int result = source.getMinYAtVXZ(crossSectionV, crossSectionX, z);
	    int localMinY;
	    for (crossSectionV++, crossSectionX += slope;
	            crossSectionV <= crossSectionMaxV && (localMinY = source.getMinYAtVXZ(crossSectionV, crossSectionX, z)) <= result;
	            crossSectionV++, crossSectionX += slope) {
	        result = localMinY;
	    }
	    return result;
	}

	@Override
	public int getMaxYAtZ(int z) {
	    int crossSectionV = crossSectionMinV;
	    int crossSectionX = slope*crossSectionV + xOffsetFromV;
	    int result = source.getMaxYAtVXZ(crossSectionV, crossSectionX, z);
	    int localMaxY;
	    for (crossSectionV++, crossSectionX += slope;
	            crossSectionV <= crossSectionMaxV && (localMaxY = source.getMaxYAtVXZ(crossSectionV, crossSectionX, z)) >= result;
	            crossSectionV++, crossSectionX += slope) {
	        result = localMaxY;
	    }
	    return result;
	}

	@Override
	public int getMinYAtWX(int w, int x) {
	    return source.getMinYAtVWX(w, x, slope*w + xOffsetFromV);
	}

	@Override
	public int getMaxYAtWX(int w, int x) {
	    return source.getMaxYAtVWX(w, x, slope*w + xOffsetFromV);
	}

	@Override
	public int getMinYAtWZ(int w, int z) {
	    return source.getMinYAtVXZ(w, slope*w + xOffsetFromV, z);
	}

	@Override
	public int getMaxYAtWZ(int w, int z) {
	    return source.getMaxYAtVXZ(w, slope*w + xOffsetFromV, z);
	}

	@Override
	public int getMinYAtXZ(int x, int z) {
	    int crossSectionV = crossSectionMinV;
	    int crossSectionX = slope*crossSectionV + xOffsetFromV;
	    int result = source.getMinY(crossSectionV, x, crossSectionX, z);
	    int localMinY;
	    for (crossSectionV++, crossSectionX += slope;
	            crossSectionV <= crossSectionMaxV && (localMinY = source.getMinY(crossSectionV, x, crossSectionX, z)) <= result;
	            crossSectionV++, crossSectionX += slope) {
	        result = localMinY;
	    }
	    return result;
	}

	@Override
	public int getMaxYAtXZ(int x, int z) {
	    int crossSectionV = crossSectionMinV;
	    int crossSectionX = slope*crossSectionV + xOffsetFromV;
	    int result = source.getMaxY(crossSectionV, x, crossSectionX, z);
	    int localMaxY;
	    for (crossSectionV++, crossSectionX += slope;
	            crossSectionV <= crossSectionMaxV && (localMaxY = source.getMaxY(crossSectionV, x, crossSectionX, z)) >= result;
	            crossSectionV++, crossSectionX += slope) {
	        result = localMaxY;
	    }
	    return result;
	}

	@Override
	public int getMinY(int w, int x, int z) {
	    return source.getMinY(w, x, slope*w + xOffsetFromV, z);
	}

	@Override
	public int getMaxY(int w, int x, int z) {
	    return source.getMaxY(w, x, slope*w + xOffsetFromV, z);
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
	    return source.getMinZAtVX(w, slope*w + xOffsetFromV);
	}

	@Override
	public int getMaxZAtW(int w) {
	    return source.getMaxZAtVX(w, slope*w + xOffsetFromV);
	}

	@Override
	public int getMinZAtX(int x) {
	    int crossSectionV = crossSectionMinV;
	    int crossSectionX = slope*crossSectionV + xOffsetFromV;
	    int result = source.getMinZAtVWX(crossSectionV, x, crossSectionX);
	    int localMinZ;
	    for (crossSectionV++, crossSectionX += slope;
	            crossSectionV <= crossSectionMaxV && (localMinZ = source.getMinZAtVWX(crossSectionV, x, crossSectionX)) <= result;
	            crossSectionV++, crossSectionX += slope) {
	        result = localMinZ;
	    }
	    return result;
	}

	@Override
	public int getMaxZAtX(int x) {
	    int crossSectionV = crossSectionMinV;
	    int crossSectionX = slope*crossSectionV + xOffsetFromV;
	    int result = source.getMaxZAtVWX(crossSectionV, x, crossSectionX);
	    int localMaxZ;
	    for (crossSectionV++, crossSectionX += slope;
	            crossSectionV <= crossSectionMaxV && (localMaxZ = source.getMaxZAtVWX(crossSectionV, x, crossSectionX)) >= result;
	            crossSectionV++, crossSectionX += slope) {
	        result = localMaxZ;
	    }
	    return result;
	}

	@Override
	public int getMinZAtY(int y) {
	    int crossSectionV = crossSectionMinV;
	    int crossSectionX = slope*crossSectionV + xOffsetFromV;
	    int result = source.getMinZAtVXY(crossSectionV, crossSectionX, y);
	    int localMinZ;
	    for (crossSectionV++, crossSectionX += slope;
	            crossSectionV <= crossSectionMaxV && (localMinZ = source.getMinZAtVXY(crossSectionV, crossSectionX, y)) <= result;
	            crossSectionV++, crossSectionX += slope) {
	        result = localMinZ;
	    }
	    return result;
	}

	@Override
	public int getMaxZAtY(int y) {
	    int crossSectionV = crossSectionMinV;
	    int crossSectionX = slope*crossSectionV + xOffsetFromV;
	    int result = source.getMaxZAtVXY(crossSectionV, crossSectionX, y);
	    int localMaxZ;
	    for (crossSectionV++, crossSectionX += slope;
	            crossSectionV <= crossSectionMaxV && (localMaxZ = source.getMaxZAtVXY(crossSectionV, crossSectionX, y)) >= result;
	            crossSectionV++, crossSectionX += slope) {
	        result = localMaxZ;
	    }
	    return result;
	}

	@Override
	public int getMinZAtWX(int w, int x) {
	    return source.getMinZAtVWX(w, x, slope*w + xOffsetFromV);
	}

	@Override
	public int getMaxZAtWX(int w, int x) {
	    return source.getMaxZAtVWX(w, x, slope*w + xOffsetFromV);
	}

	@Override
	public int getMinZAtWY(int w, int y) {
	    return source.getMinZAtVXY(w, slope*w + xOffsetFromV, y);
	}

	@Override
	public int getMaxZAtWY(int w, int y) {
	    return source.getMaxZAtVXY(w, slope*w + xOffsetFromV, y);
	}

	@Override
	public int getMinZAtXY(int x, int y) {
	    int crossSectionV = crossSectionMinV;
	    int crossSectionX = slope*crossSectionV + xOffsetFromV;
	    int result = source.getMinZ(crossSectionV, x, crossSectionX, y);
	    int localMinZ;
	    for (crossSectionV++, crossSectionX += slope;
	            crossSectionV <= crossSectionMaxV && (localMinZ = source.getMinZ(crossSectionV, x, crossSectionX, y)) <= result;
	            crossSectionV++, crossSectionX += slope) {
	        result = localMinZ;
	    }
	    return result;
	}

	@Override
	public int getMaxZAtXY(int x, int y) {
	    int crossSectionV = crossSectionMinV;
	    int crossSectionX = slope*crossSectionV + xOffsetFromV;
	    int result = source.getMaxZ(crossSectionV, x, crossSectionX, y);
	    int localMaxZ;
	    for (crossSectionV++, crossSectionX += slope;
	            crossSectionV <= crossSectionMaxV && (localMaxZ = source.getMaxZ(crossSectionV, x, crossSectionX, y)) >= result;
	            crossSectionV++, crossSectionX += slope) {
	        result = localMaxZ;
	    }
	    return result;
	}

	@Override
	public int getMinZ(int w, int x, int y) {
	    return source.getMinZ(w, x, slope*w + xOffsetFromV, y);
	}

	@Override
	public int getMaxZ(int w, int x, int y) {
	    return source.getMaxZ(w, x, slope*w + xOffsetFromV, y);
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
	    StringBuilder path = new StringBuilder(source.getSubfolderPath()).append("/").append(source.getXLabel()).append("=");
	    if (slope == -1) {
	        path.append("-");
	    }
	    path.append(source.getVLabel());
	    if (xOffsetFromV < 0) {
	        path.append(xOffsetFromV);
	    } else if (xOffsetFromV > 0) {
	        path.append("+").append(xOffsetFromV);
	    }
	    return path.toString();
	}

	@Override
	public void backUp(String backupPath, String backupName) throws Exception {
	    source.backUp(backupPath, backupName);
	}

}
