/* Aether2DImgMaker -- console app to generate images of the Aether cellular automaton in 2D
    Copyright (C) 2017-2025 Jaume Ribas

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

public class Model5DWXDiagonalCrossSection<Source_Type extends Model5D> implements Model4D {

	protected final Source_Type source;
	protected final int slope;
	protected final int xOffsetFromW;
	protected int crossSectionMinV;
	protected int crossSectionMaxV;
	protected int crossSectionMinW;
	protected int crossSectionMaxW;
	protected int crossSectionMinY;
	protected int crossSectionMaxY;
	protected int crossSectionMinZ;
	protected int crossSectionMaxZ;

	public Model5DWXDiagonalCrossSection(Source_Type source, boolean positiveSlope, int xOffsetFromW) {
	    this.source = source;
	    this.slope = positiveSlope ? 1 : -1;
	    this.xOffsetFromW = xOffsetFromW;
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
	        crossSectionMinV = source.getMinVAtWX(w, crossSectionX);
	        crossSectionMaxV = source.getMaxVAtWX(w, crossSectionX);
	        crossSectionMinY = source.getMinYAtWX(w, crossSectionX);
	        crossSectionMaxY = source.getMaxYAtWX(w, crossSectionX);
	        crossSectionMinZ = source.getMinZAtWX(w, crossSectionX);
	        crossSectionMaxZ = source.getMaxZAtWX(w, crossSectionX);
	        w++;
	        crossSectionX += slope;
	        while (w <= maxW && crossSectionX >= source.getMinXAtW(w) && crossSectionX <= source.getMaxXAtW(w)) {
	            crossSectionMaxW = w;
	            int localMinV = source.getMinVAtWX(w, crossSectionX);
	            if (localMinV < crossSectionMinV) {
	                crossSectionMinV = localMinV;
	            }
	            int localMaxV = source.getMaxVAtWX(w, crossSectionX);
	            if (localMaxV > crossSectionMaxV) {
	                crossSectionMaxV = localMaxV;
	            }
	            int localMinY = source.getMinYAtWX(w, crossSectionX);
	            if (localMinY < crossSectionMinY) {
	                crossSectionMinY = localMinY;
	            }
	            int localMaxY = source.getMaxYAtWX(w, crossSectionX);
	            if (localMaxY > crossSectionMaxY) {
	                crossSectionMaxY = localMaxY;
	            }
	            int localMinZ = source.getMinZAtWX(w, crossSectionX);
	            if (localMinZ < crossSectionMinZ) {
	                crossSectionMinZ = localMinZ;
	            }
	            int localMaxZ = source.getMaxZAtWX(w, crossSectionX);
	            if (localMaxZ > crossSectionMaxZ) {
	                crossSectionMaxZ = localMaxZ;
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
	public int getMinW() {
	    return crossSectionMinV;
	}

	@Override
	public int getMaxW() {
	    return crossSectionMaxV;
	}

	@Override
	public int getMinWAtX(int x) {
	    return source.getMinVAtWX(x, slope*x + xOffsetFromW);
	}

	@Override
	public int getMaxWAtX(int x) {
	    return source.getMaxVAtWX(x, slope*x + xOffsetFromW);
	}

	@Override
	public int getMinWAtY(int y) {
	    int crossSectionW = crossSectionMinW;
	    int crossSectionX = slope*crossSectionW + xOffsetFromW;
	    int result = source.getMinVAtWXY(crossSectionW, crossSectionX, y);
	    int localMinV;
	    for (crossSectionW++, crossSectionX += slope;
	            crossSectionW <= crossSectionMaxW && (localMinV = source.getMinVAtWXY(crossSectionW, crossSectionX, y)) <= result;
	            crossSectionW++, crossSectionX += slope) {
	        result = localMinV;
	    }
	    return result;
	}

	@Override
	public int getMaxWAtY(int y) {
	    int crossSectionW = crossSectionMinW;
	    int crossSectionX = slope*crossSectionW + xOffsetFromW;
	    int result = source.getMaxVAtWXY(crossSectionW, crossSectionX, y);
	    int localMaxV;
	    for (crossSectionW++, crossSectionX += slope;
	            crossSectionW <= crossSectionMaxW && (localMaxV = source.getMaxVAtWXY(crossSectionW, crossSectionX, y)) >= result;
	            crossSectionW++, crossSectionX += slope) {
	        result = localMaxV;
	    }
	    return result;
	}

	@Override
	public int getMinWAtZ(int z) {
	    int crossSectionW = crossSectionMinW;
	    int crossSectionX = slope*crossSectionW + xOffsetFromW;
	    int result = source.getMinVAtWXZ(crossSectionW, crossSectionX, z);
	    int localMinV;
	    for (crossSectionW++, crossSectionX += slope;
	            crossSectionW <= crossSectionMaxW && (localMinV = source.getMinVAtWXZ(crossSectionW, crossSectionX, z)) <= result;
	            crossSectionW++, crossSectionX += slope) {
	        result = localMinV;
	    }
	    return result;
	}

	@Override
	public int getMaxWAtZ(int z) {
	    int crossSectionW = crossSectionMinW;
	    int crossSectionX = slope*crossSectionW + xOffsetFromW;
	    int result = source.getMaxVAtWXZ(crossSectionW, crossSectionX, z);
	    int localMaxV;
	    for (crossSectionW++, crossSectionX += slope;
	            crossSectionW <= crossSectionMaxW && (localMaxV = source.getMaxVAtWXZ(crossSectionW, crossSectionX, z)) >= result;
	            crossSectionW++, crossSectionX += slope) {
	        result = localMaxV;
	    }
	    return result;
	}

	@Override
	public int getMinWAtXY(int x, int y) {
	    return source.getMinVAtWXY(x, slope*x + xOffsetFromW, y);
	}

	@Override
	public int getMaxWAtXY(int x, int y) {
	    return source.getMaxVAtWXY(x, slope*x + xOffsetFromW, y);
	}

	@Override
	public int getMinWAtXZ(int x, int z) {
	    return source.getMinVAtWXZ(x, slope*x + xOffsetFromW, z);
	}

	@Override
	public int getMaxWAtXZ(int x, int z) {
	    return source.getMaxVAtWXZ(x, slope*x + xOffsetFromW, z);
	}

	@Override
	public int getMinWAtYZ(int y, int z) {
	    int crossSectionW = crossSectionMinW;
	    int crossSectionX = slope*crossSectionW + xOffsetFromW;
	    int result = source.getMinV(crossSectionW, crossSectionX, y, z);
	    int localMinV;
	    for (crossSectionW++, crossSectionX += slope;
	            crossSectionW <= crossSectionMaxW && (localMinV = source.getMinV(crossSectionW, crossSectionX, y, z)) <= result;
	            crossSectionW++, crossSectionX += slope) {
	        result = localMinV;
	    }
	    return result;
	}

	@Override
	public int getMaxWAtYZ(int y, int z) {
	    int crossSectionW = crossSectionMinW;
	    int crossSectionX = slope*crossSectionW + xOffsetFromW;
	    int result = source.getMaxV(crossSectionW, crossSectionX, y, z);
	    int localMaxV;
	    for (crossSectionW++, crossSectionX += slope;
	            crossSectionW <= crossSectionMaxW && (localMaxV = source.getMaxV(crossSectionW, crossSectionX, y, z)) >= result;
	            crossSectionW++, crossSectionX += slope) {
	        result = localMaxV;
	    }
	    return result;
	}

	@Override
	public int getMinW(int x, int y, int z) {
	    return source.getMinV(x, slope*x + xOffsetFromW, y, z);
	}

	@Override
	public int getMaxW(int x, int y, int z) {
	    return source.getMaxV(x, slope*x + xOffsetFromW, y, z);
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
	    for (int crossSectionW = crossSectionMinW, crossSectionX = slope*crossSectionW + xOffsetFromW; crossSectionW <= crossSectionMaxW; crossSectionW++, crossSectionX += slope) {
	        if (w >= source.getMinVAtWX(crossSectionW, crossSectionX)
	                && w <= source.getMaxVAtWX(crossSectionW, crossSectionX)) {
	            return crossSectionW;
	        }
	    }
	    throw new IllegalArgumentException("The coordinate is out of bounds.");
	}

	@Override
	public int getMaxXAtW(int w) {
	    for (int crossSectionW = crossSectionMaxW, crossSectionX = slope*crossSectionW + xOffsetFromW; crossSectionW >= crossSectionMinW; crossSectionW--, crossSectionX -= slope) {
	        if (w >= source.getMinVAtWX(crossSectionW, crossSectionX)
	                && w <= source.getMaxVAtWX(crossSectionW, crossSectionX)) {
	            return crossSectionW;
	        }
	    }
	    throw new IllegalArgumentException("The coordinate is out of bounds.");
	}

	@Override
	public int getMinXAtY(int y) {
	    for (int crossSectionW = crossSectionMinW, crossSectionX = slope*crossSectionW + xOffsetFromW; crossSectionW <= crossSectionMaxW; crossSectionW++, crossSectionX += slope) {
	        if (y >= source.getMinYAtWX(crossSectionW, crossSectionX)
	                && y <= source.getMaxYAtWX(crossSectionW, crossSectionX)) {
	            return crossSectionW;
	        }
	    }
	    throw new IllegalArgumentException("The coordinate is out of bounds.");
	}

	@Override
	public int getMaxXAtY(int y) {
	    for (int crossSectionW = crossSectionMaxW, crossSectionX = slope*crossSectionW + xOffsetFromW; crossSectionW >= crossSectionMinW; crossSectionW--, crossSectionX -= slope) {
	        if (y >= source.getMinYAtWX(crossSectionW, crossSectionX)
	                && y <= source.getMaxYAtWX(crossSectionW, crossSectionX)) {
	            return crossSectionW;
	        }
	    }
	    throw new IllegalArgumentException("The coordinate is out of bounds.");
	}

	@Override
	public int getMinXAtZ(int z) {
	    for (int crossSectionW = crossSectionMinW, crossSectionX = slope*crossSectionW + xOffsetFromW; crossSectionW <= crossSectionMaxW; crossSectionW++, crossSectionX += slope) {
	        if (z >= source.getMinZAtWX(crossSectionW, crossSectionX)
	                && z <= source.getMaxZAtWX(crossSectionW, crossSectionX)) {
	            return crossSectionW;
	        }
	    }
	    throw new IllegalArgumentException("The coordinate is out of bounds.");
	}

	@Override
	public int getMaxXAtZ(int z) {
	    for (int crossSectionW = crossSectionMaxW, crossSectionX = slope*crossSectionW + xOffsetFromW; crossSectionW >= crossSectionMinW; crossSectionW--, crossSectionX -= slope) {
	        if (z >= source.getMinZAtWX(crossSectionW, crossSectionX)
	                && z <= source.getMaxZAtWX(crossSectionW, crossSectionX)) {
	            return crossSectionW;
	        }
	    }
	    throw new IllegalArgumentException("The coordinate is out of bounds.");
	}

	@Override
	public int getMinXAtWY(int w, int y) {
	    for (int crossSectionW = crossSectionMinW, crossSectionX = slope*crossSectionW + xOffsetFromW; crossSectionW <= crossSectionMaxW; crossSectionW++, crossSectionX += slope) {
	        if (w >= source.getMinVAtWX(crossSectionW, crossSectionX)
	                && w <= source.getMaxVAtWX(crossSectionW, crossSectionX)
	                && y >= source.getMinYAtVWX(w, crossSectionW, crossSectionX)
	                && y <= source.getMaxYAtVWX(w, crossSectionW, crossSectionX)) {
	            return crossSectionW;
	        }
	    }
	    throw new IllegalArgumentException("The coordinates are out of bounds.");
	}

	@Override
	public int getMaxXAtWY(int w, int y) {
	    for (int crossSectionW = crossSectionMaxW, crossSectionX = slope*crossSectionW + xOffsetFromW; crossSectionW >= crossSectionMinW; crossSectionW--, crossSectionX -= slope) {
	        if (w >= source.getMinVAtWX(crossSectionW, crossSectionX)
	                && w <= source.getMaxVAtWX(crossSectionW, crossSectionX)
	                && y >= source.getMinYAtVWX(w, crossSectionW, crossSectionX)
	                && y <= source.getMaxYAtVWX(w, crossSectionW, crossSectionX)) {
	            return crossSectionW;
	        }
	    }
	    throw new IllegalArgumentException("The coordinates are out of bounds.");
	}

	@Override
	public int getMinXAtWZ(int w, int z) {
	    for (int crossSectionW = crossSectionMinW, crossSectionX = slope*crossSectionW + xOffsetFromW; crossSectionW <= crossSectionMaxW; crossSectionW++, crossSectionX += slope) {
	        if (w >= source.getMinVAtWX(crossSectionW, crossSectionX)
	                && w <= source.getMaxVAtWX(crossSectionW, crossSectionX)
	                && z >= source.getMinZAtVWX(w, crossSectionW, crossSectionX)
	                && z <= source.getMaxZAtVWX(w, crossSectionW, crossSectionX)) {
	            return crossSectionW;
	        }
	    }
	    throw new IllegalArgumentException("The coordinates are out of bounds.");
	}

	@Override
	public int getMaxXAtWZ(int w, int z) {
	    for (int crossSectionW = crossSectionMaxW, crossSectionX = slope*crossSectionW + xOffsetFromW; crossSectionW >= crossSectionMinW; crossSectionW--, crossSectionX -= slope) {
	        if (w >= source.getMinVAtWX(crossSectionW, crossSectionX)
	                && w <= source.getMaxVAtWX(crossSectionW, crossSectionX)
	                && z >= source.getMinZAtVWX(w, crossSectionW, crossSectionX)
	                && z <= source.getMaxZAtVWX(w, crossSectionW, crossSectionX)) {
	            return crossSectionW;
	        }
	    }
	    throw new IllegalArgumentException("The coordinates are out of bounds.");
	}

	@Override
	public int getMinXAtYZ(int y, int z) {
	    for (int crossSectionW = crossSectionMinW, crossSectionX = slope*crossSectionW + xOffsetFromW; crossSectionW <= crossSectionMaxW; crossSectionW++, crossSectionX += slope) {
	        if (y >= source.getMinYAtWX(crossSectionW, crossSectionX)
	                && y <= source.getMaxYAtWX(crossSectionW, crossSectionX)
	                && z >= source.getMinZAtWXY(crossSectionW, crossSectionX, y)
	                && z <= source.getMaxZAtWXY(crossSectionW, crossSectionX, y)) {
	            return crossSectionW;
	        }
	    }
	    throw new IllegalArgumentException("The coordinates are out of bounds.");
	}

	@Override
	public int getMaxXAtYZ(int y, int z) {
	    for (int crossSectionW = crossSectionMaxW, crossSectionX = slope*crossSectionW + xOffsetFromW; crossSectionW >= crossSectionMinW; crossSectionW--, crossSectionX -= slope) {
	        if (y >= source.getMinYAtWX(crossSectionW, crossSectionX)
	                && y <= source.getMaxYAtWX(crossSectionW, crossSectionX)
	                && z >= source.getMinZAtWXY(crossSectionW, crossSectionX, y)
	                && z <= source.getMaxZAtWXY(crossSectionW, crossSectionX, y)) {
	            return crossSectionW;
	        }
	    }
	    throw new IllegalArgumentException("The coordinates are out of bounds.");
	}

	@Override
	public int getMinX(int w, int y, int z) {
	    for (int crossSectionW = crossSectionMinW, crossSectionX = slope*crossSectionW + xOffsetFromW; crossSectionW <= crossSectionMaxW; crossSectionW++, crossSectionX += slope) {
	        if (w >= source.getMinVAtWX(crossSectionW, crossSectionX)
	                && w <= source.getMaxVAtWX(crossSectionW, crossSectionX)
	                && y >= source.getMinYAtVWX(w, crossSectionW, crossSectionX)
	                && y <= source.getMaxYAtVWX(w, crossSectionW, crossSectionX)
	                && z >= source.getMinZ(w, crossSectionW, crossSectionX, y)
	                && z <= source.getMaxZ(w, crossSectionW, crossSectionX, y)) {
	            return crossSectionW;
	        }
	    }
	    throw new IllegalArgumentException("The coordinates are out of bounds.");
	}

	@Override
	public int getMaxX(int w, int y, int z) {
	    for (int crossSectionW = crossSectionMaxW, crossSectionX = slope*crossSectionW + xOffsetFromW; crossSectionW >= crossSectionMinW; crossSectionW--, crossSectionX -= slope) {
	        if (w >= source.getMinVAtWX(crossSectionW, crossSectionX)
	                && w <= source.getMaxVAtWX(crossSectionW, crossSectionX)
	                && y >= source.getMinYAtVWX(w, crossSectionW, crossSectionX)
	                && y <= source.getMaxYAtVWX(w, crossSectionW, crossSectionX)
	                && z >= source.getMinZ(w, crossSectionW, crossSectionX, y)
	                && z <= source.getMaxZ(w, crossSectionW, crossSectionX, y)) {
	            return crossSectionW;
	        }
	    }
	    throw new IllegalArgumentException("The coordinates are out of bounds.");
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
	    int crossSectionW = crossSectionMinW;
	    int crossSectionX = slope*crossSectionW + xOffsetFromW;
	    int result = source.getMinYAtVWX(w, crossSectionW, crossSectionX);
	    int localMinY;
	    for (crossSectionW++, crossSectionX += slope;
	            crossSectionW <= crossSectionMaxW && (localMinY = source.getMinYAtVWX(w, crossSectionW, crossSectionX)) <= result;
	            crossSectionW++, crossSectionX += slope) {
	        result = localMinY;
	    }
	    return result;
	}

	@Override
	public int getMaxYAtW(int w) {
	    int crossSectionW = crossSectionMinW;
	    int crossSectionX = slope*crossSectionW + xOffsetFromW;
	    int result = source.getMaxYAtVWX(w, crossSectionW, crossSectionX);
	    int localMaxY;
	    for (crossSectionW++, crossSectionX += slope;
	            crossSectionW <= crossSectionMaxW && (localMaxY = source.getMaxYAtVWX(w, crossSectionW, crossSectionX)) >= result;
	            crossSectionW++, crossSectionX += slope) {
	        result = localMaxY;
	    }
	    return result;
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
	    int result = source.getMinYAtWXZ(crossSectionW, crossSectionX, z);
	    int localMinY;
	    for (crossSectionW++, crossSectionX += slope;
	            crossSectionW <= crossSectionMaxW && (localMinY = source.getMinYAtWXZ(crossSectionW, crossSectionX, z)) <= result;
	            crossSectionW++, crossSectionX += slope) {
	        result = localMinY;
	    }
	    return result;
	}

	@Override
	public int getMaxYAtZ(int z) {
	    int crossSectionW = crossSectionMinW;
	    int crossSectionX = slope*crossSectionW + xOffsetFromW;
	    int result = source.getMaxYAtWXZ(crossSectionW, crossSectionX, z);
	    int localMaxY;
	    for (crossSectionW++, crossSectionX += slope;
	            crossSectionW <= crossSectionMaxW && (localMaxY = source.getMaxYAtWXZ(crossSectionW, crossSectionX, z)) >= result;
	            crossSectionW++, crossSectionX += slope) {
	        result = localMaxY;
	    }
	    return result;
	}

	@Override
	public int getMinYAtWX(int w, int x) {
	    return source.getMinYAtVWX(w, x, slope*x + xOffsetFromW);
	}

	@Override
	public int getMaxYAtWX(int w, int x) {
	    return source.getMaxYAtVWX(w, x, slope*x + xOffsetFromW);
	}

	@Override
	public int getMinYAtWZ(int w, int z) {
	    int crossSectionW = crossSectionMinW;
	    int crossSectionX = slope*crossSectionW + xOffsetFromW;
	    int result = source.getMinY(w, crossSectionW, crossSectionX, z);
	    int localMinY;
	    for (crossSectionW++, crossSectionX += slope;
	            crossSectionW <= crossSectionMaxW && (localMinY = source.getMinY(w, crossSectionW, crossSectionX, z)) <= result;
	            crossSectionW++, crossSectionX += slope) {
	        result = localMinY;
	    }
	    return result;
	}

	@Override
	public int getMaxYAtWZ(int w, int z) {
	    int crossSectionW = crossSectionMinW;
	    int crossSectionX = slope*crossSectionW + xOffsetFromW;
	    int result = source.getMaxY(w, crossSectionW, crossSectionX, z);
	    int localMaxY;
	    for (crossSectionW++, crossSectionX += slope;
	            crossSectionW <= crossSectionMaxW && (localMaxY = source.getMaxY(w, crossSectionW, crossSectionX, z)) >= result;
	            crossSectionW++, crossSectionX += slope) {
	        result = localMaxY;
	    }
	    return result;
	}

	@Override
	public int getMinYAtXZ(int x, int z) {
	    return source.getMinYAtWXZ(x, slope*x + xOffsetFromW, z);
	}

	@Override
	public int getMaxYAtXZ(int x, int z) {
	    return source.getMaxYAtWXZ(x, slope*x + xOffsetFromW, z);
	}

	@Override
	public int getMinY(int w, int x, int z) {
	    return source.getMinY(w, x, slope*x + xOffsetFromW, z);
	}

	@Override
	public int getMaxY(int w, int x, int z) {
	    return source.getMaxY(w, x, slope*x + xOffsetFromW, z);
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
	    int crossSectionW = crossSectionMinW;
	    int crossSectionX = slope*crossSectionW + xOffsetFromW;
	    int result = source.getMinZAtVWX(w, crossSectionW, crossSectionX);
	    int localMinZ;
	    for (crossSectionW++, crossSectionX += slope;
	            crossSectionW <= crossSectionMaxW && (localMinZ = source.getMinZAtVWX(w, crossSectionW, crossSectionX)) <= result;
	            crossSectionW++, crossSectionX += slope) {
	        result = localMinZ;
	    }
	    return result;
	}

	@Override
	public int getMaxZAtW(int w) {
	    int crossSectionW = crossSectionMinW;
	    int crossSectionX = slope*crossSectionW + xOffsetFromW;
	    int result = source.getMaxZAtVWX(w, crossSectionW, crossSectionX);
	    int localMaxZ;
	    for (crossSectionW++, crossSectionX += slope;
	            crossSectionW <= crossSectionMaxW && (localMaxZ = source.getMaxZAtVWX(w, crossSectionW, crossSectionX)) >= result;
	            crossSectionW++, crossSectionX += slope) {
	        result = localMaxZ;
	    }
	    return result;
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
	    int result = source.getMinZAtWXY(crossSectionW, crossSectionX, y);
	    int localMinZ;
	    for (crossSectionW++, crossSectionX += slope;
	            crossSectionW <= crossSectionMaxW && (localMinZ = source.getMinZAtWXY(crossSectionW, crossSectionX, y)) <= result;
	            crossSectionW++, crossSectionX += slope) {
	        result = localMinZ;
	    }
	    return result;
	}

	@Override
	public int getMaxZAtY(int y) {
	    int crossSectionW = crossSectionMinW;
	    int crossSectionX = slope*crossSectionW + xOffsetFromW;
	    int result = source.getMaxZAtWXY(crossSectionW, crossSectionX, y);
	    int localMaxZ;
	    for (crossSectionW++, crossSectionX += slope;
	            crossSectionW <= crossSectionMaxW && (localMaxZ = source.getMaxZAtWXY(crossSectionW, crossSectionX, y)) >= result;
	            crossSectionW++, crossSectionX += slope) {
	        result = localMaxZ;
	    }
	    return result;
	}

	@Override
	public int getMinZAtWX(int w, int x) {
	    return source.getMinZAtVWX(w, x, slope*x + xOffsetFromW);
	}

	@Override
	public int getMaxZAtWX(int w, int x) {
	    return source.getMaxZAtVWX(w, x, slope*x + xOffsetFromW);
	}

	@Override
	public int getMinZAtWY(int w, int y) {
	    int crossSectionW = crossSectionMinW;
	    int crossSectionX = slope*crossSectionW + xOffsetFromW;
	    int result = source.getMinZ(w, crossSectionW, crossSectionX, y);
	    int localMinZ;
	    for (crossSectionW++, crossSectionX += slope;
	            crossSectionW <= crossSectionMaxW && (localMinZ = source.getMinZ(w, crossSectionW, crossSectionX, y)) <= result;
	            crossSectionW++, crossSectionX += slope) {
	        result = localMinZ;
	    }
	    return result;
	}

	@Override
	public int getMaxZAtWY(int w, int y) {
	    int crossSectionW = crossSectionMinW;
	    int crossSectionX = slope*crossSectionW + xOffsetFromW;
	    int result = source.getMaxZ(w, crossSectionW, crossSectionX, y);
	    int localMaxZ;
	    for (crossSectionW++, crossSectionX += slope;
	            crossSectionW <= crossSectionMaxW && (localMaxZ = source.getMaxZ(w, crossSectionW, crossSectionX, y)) >= result;
	            crossSectionW++, crossSectionX += slope) {
	        result = localMaxZ;
	    }
	    return result;
	}

	@Override
	public int getMinZAtXY(int x, int y) {
	    return source.getMinZAtWXY(x, slope*x + xOffsetFromW, y);
	}

	@Override
	public int getMaxZAtXY(int x, int y) {
	    return source.getMaxZAtWXY(x, slope*x + xOffsetFromW, y);
	}

	@Override
	public int getMinZ(int w, int x, int y) {
	    return source.getMinZ(w, x, slope*x + xOffsetFromW, y);
	}

	@Override
	public int getMaxZ(int w, int x, int y) {
	    return source.getMaxZ(w, x, slope*x + xOffsetFromW, y);
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
	    path.append(source.getWLabel());
	    if (xOffsetFromW < 0) {
	        path.append(xOffsetFromW);
	    } else if (xOffsetFromW > 0) {
	        path.append("+").append(xOffsetFromW);
	    }
	    return path.toString();
	}

	@Override
	public void backUp(String backupPath, String backupName) throws Exception {
	    source.backUp(backupPath, backupName);
	}

}
