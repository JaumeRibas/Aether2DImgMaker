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

public class Model5DWZDiagonalCrossSection<Source_Type extends Model5D> implements Model4D {

	protected Source_Type source;
	protected int slope;
	protected int zOffsetFromW;
	protected int crossSectionMinV;
	protected int crossSectionMaxV;
	protected int crossSectionMinW;
	protected int crossSectionMaxW;
	protected int crossSectionMinX;
	protected int crossSectionMaxX;
	protected int crossSectionMinY;
	protected int crossSectionMaxY;

	public Model5DWZDiagonalCrossSection(Source_Type source, boolean positiveSlope, int zOffsetFromW) {
	    this.source = source;
	    this.slope = positiveSlope ? 1 : -1;
	    this.zOffsetFromW = zOffsetFromW;
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
	    return source.getXLabel();
	}

	@Override
	public String getZLabel() {
	    return source.getYLabel();
	}

	protected boolean getBounds() {
	    int w = source.getMinW();
	    int maxW = source.getMaxW();
	    int crossSectionZ = slope*w + zOffsetFromW;
	    while (w <= maxW && (crossSectionZ < source.getMinZAtW(w) || crossSectionZ > source.getMaxZAtW(w))) {
	        w++;
	        crossSectionZ += slope;
	    }
	    if (w <= maxW) {
	        crossSectionMinW = w;
	        crossSectionMaxW = w;
	        crossSectionMinV = source.getMinVAtWZ(w, crossSectionZ);
	        crossSectionMaxV = source.getMaxVAtWZ(w, crossSectionZ);
	        crossSectionMinX = source.getMinXAtWZ(w, crossSectionZ);
	        crossSectionMaxX = source.getMaxXAtWZ(w, crossSectionZ);
	        crossSectionMinY = source.getMinYAtWZ(w, crossSectionZ);
	        crossSectionMaxY = source.getMaxYAtWZ(w, crossSectionZ);
	        w++;
	        crossSectionZ += slope;
	        while (w <= maxW && crossSectionZ >= source.getMinZAtW(w) && crossSectionZ <= source.getMaxZAtW(w)) {
	            crossSectionMaxW = w;
	            int localMinV = source.getMinVAtWZ(w, crossSectionZ);
	            if (localMinV < crossSectionMinV) {
	                crossSectionMinV = localMinV;
	            }
	            int localMaxV = source.getMaxVAtWZ(w, crossSectionZ);
	            if (localMaxV > crossSectionMaxV) {
	                crossSectionMaxV = localMaxV;
	            }
	            int localMinX = source.getMinXAtWZ(w, crossSectionZ);
	            if (localMinX < crossSectionMinX) {
	                crossSectionMinX = localMinX;
	            }
	            int localMaxX = source.getMaxXAtWZ(w, crossSectionZ);
	            if (localMaxX > crossSectionMaxX) {
	                crossSectionMaxX = localMaxX;
	            }
	            int localMinY = source.getMinYAtWZ(w, crossSectionZ);
	            if (localMinY < crossSectionMinY) {
	                crossSectionMinY = localMinY;
	            }
	            int localMaxY = source.getMaxYAtWZ(w, crossSectionZ);
	            if (localMaxY > crossSectionMaxY) {
	                crossSectionMaxY = localMaxY;
	            }
	            w++;
	            crossSectionZ += slope;
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
	    return source.getMinVAtWZ(x, slope*x + zOffsetFromW);
	}

	@Override
	public int getMaxWAtX(int x) {
	    return source.getMaxVAtWZ(x, slope*x + zOffsetFromW);
	}

	@Override
	public int getMinWAtY(int y) {
	    int crossSectionW = crossSectionMinW;
	    int crossSectionZ = slope*crossSectionW + zOffsetFromW;
	    int result = source.getMinVAtWXZ(crossSectionW, y, crossSectionZ);
	    int localMinV;
	    for (crossSectionW++, crossSectionZ += slope;
	            crossSectionW <= crossSectionMaxW && (localMinV = source.getMinVAtWXZ(crossSectionW, y, crossSectionZ)) <= result;
	            crossSectionW++, crossSectionZ += slope) {
	        result = localMinV;
	    }
	    return result;
	}

	@Override
	public int getMaxWAtY(int y) {
	    int crossSectionW = crossSectionMinW;
	    int crossSectionZ = slope*crossSectionW + zOffsetFromW;
	    int result = source.getMaxVAtWXZ(crossSectionW, y, crossSectionZ);
	    int localMaxV;
	    for (crossSectionW++, crossSectionZ += slope;
	            crossSectionW <= crossSectionMaxW && (localMaxV = source.getMaxVAtWXZ(crossSectionW, y, crossSectionZ)) >= result;
	            crossSectionW++, crossSectionZ += slope) {
	        result = localMaxV;
	    }
	    return result;
	}

	@Override
	public int getMinWAtZ(int z) {
	    int crossSectionW = crossSectionMinW;
	    int crossSectionZ = slope*crossSectionW + zOffsetFromW;
	    int result = source.getMinVAtWYZ(crossSectionW, z, crossSectionZ);
	    int localMinV;
	    for (crossSectionW++, crossSectionZ += slope;
	            crossSectionW <= crossSectionMaxW && (localMinV = source.getMinVAtWYZ(crossSectionW, z, crossSectionZ)) <= result;
	            crossSectionW++, crossSectionZ += slope) {
	        result = localMinV;
	    }
	    return result;
	}

	@Override
	public int getMaxWAtZ(int z) {
	    int crossSectionW = crossSectionMinW;
	    int crossSectionZ = slope*crossSectionW + zOffsetFromW;
	    int result = source.getMaxVAtWYZ(crossSectionW, z, crossSectionZ);
	    int localMaxV;
	    for (crossSectionW++, crossSectionZ += slope;
	            crossSectionW <= crossSectionMaxW && (localMaxV = source.getMaxVAtWYZ(crossSectionW, z, crossSectionZ)) >= result;
	            crossSectionW++, crossSectionZ += slope) {
	        result = localMaxV;
	    }
	    return result;
	}

	@Override
	public int getMinWAtXY(int x, int y) {
	    return source.getMinVAtWXZ(x, y, slope*x + zOffsetFromW);
	}

	@Override
	public int getMaxWAtXY(int x, int y) {
	    return source.getMaxVAtWXZ(x, y, slope*x + zOffsetFromW);
	}

	@Override
	public int getMinWAtXZ(int x, int z) {
	    return source.getMinVAtWYZ(x, z, slope*x + zOffsetFromW);
	}

	@Override
	public int getMaxWAtXZ(int x, int z) {
	    return source.getMaxVAtWYZ(x, z, slope*x + zOffsetFromW);
	}

	@Override
	public int getMinWAtYZ(int y, int z) {
	    int crossSectionW = crossSectionMinW;
	    int crossSectionZ = slope*crossSectionW + zOffsetFromW;
	    int result = source.getMinV(crossSectionW, y, z, crossSectionZ);
	    int localMinV;
	    for (crossSectionW++, crossSectionZ += slope;
	            crossSectionW <= crossSectionMaxW && (localMinV = source.getMinV(crossSectionW, y, z, crossSectionZ)) <= result;
	            crossSectionW++, crossSectionZ += slope) {
	        result = localMinV;
	    }
	    return result;
	}

	@Override
	public int getMaxWAtYZ(int y, int z) {
	    int crossSectionW = crossSectionMinW;
	    int crossSectionZ = slope*crossSectionW + zOffsetFromW;
	    int result = source.getMaxV(crossSectionW, y, z, crossSectionZ);
	    int localMaxV;
	    for (crossSectionW++, crossSectionZ += slope;
	            crossSectionW <= crossSectionMaxW && (localMaxV = source.getMaxV(crossSectionW, y, z, crossSectionZ)) >= result;
	            crossSectionW++, crossSectionZ += slope) {
	        result = localMaxV;
	    }
	    return result;
	}

	@Override
	public int getMinW(int x, int y, int z) {
	    return source.getMinV(x, y, z, slope*x + zOffsetFromW);
	}

	@Override
	public int getMaxW(int x, int y, int z) {
	    return source.getMaxV(x, y, z, slope*x + zOffsetFromW);
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
	    for (int crossSectionW = crossSectionMinW, crossSectionZ = slope*crossSectionW + zOffsetFromW; crossSectionW <= crossSectionMaxW; crossSectionW++, crossSectionZ += slope) {
	        if (w >= source.getMinVAtWZ(crossSectionW, crossSectionZ)
	                && w <= source.getMaxVAtWZ(crossSectionW, crossSectionZ)) {
	            return crossSectionW;
	        }
	    }
	    throw new IllegalArgumentException("The coordinate is out of bounds.");
	}

	@Override
	public int getMaxXAtW(int w) {
	    for (int crossSectionW = crossSectionMaxW, crossSectionZ = slope*crossSectionW + zOffsetFromW; crossSectionW >= crossSectionMinW; crossSectionW--, crossSectionZ -= slope) {
	        if (w >= source.getMinVAtWZ(crossSectionW, crossSectionZ)
	                && w <= source.getMaxVAtWZ(crossSectionW, crossSectionZ)) {
	            return crossSectionW;
	        }
	    }
	    throw new IllegalArgumentException("The coordinate is out of bounds.");
	}

	@Override
	public int getMinXAtY(int y) {
	    for (int crossSectionW = crossSectionMinW, crossSectionZ = slope*crossSectionW + zOffsetFromW; crossSectionW <= crossSectionMaxW; crossSectionW++, crossSectionZ += slope) {
	        if (y >= source.getMinXAtWZ(crossSectionW, crossSectionZ)
	                && y <= source.getMaxXAtWZ(crossSectionW, crossSectionZ)) {
	            return crossSectionW;
	        }
	    }
	    throw new IllegalArgumentException("The coordinate is out of bounds.");
	}

	@Override
	public int getMaxXAtY(int y) {
	    for (int crossSectionW = crossSectionMaxW, crossSectionZ = slope*crossSectionW + zOffsetFromW; crossSectionW >= crossSectionMinW; crossSectionW--, crossSectionZ -= slope) {
	        if (y >= source.getMinXAtWZ(crossSectionW, crossSectionZ)
	                && y <= source.getMaxXAtWZ(crossSectionW, crossSectionZ)) {
	            return crossSectionW;
	        }
	    }
	    throw new IllegalArgumentException("The coordinate is out of bounds.");
	}

	@Override
	public int getMinXAtZ(int z) {
	    for (int crossSectionW = crossSectionMinW, crossSectionZ = slope*crossSectionW + zOffsetFromW; crossSectionW <= crossSectionMaxW; crossSectionW++, crossSectionZ += slope) {
	        if (z >= source.getMinYAtWZ(crossSectionW, crossSectionZ)
	                && z <= source.getMaxYAtWZ(crossSectionW, crossSectionZ)) {
	            return crossSectionW;
	        }
	    }
	    throw new IllegalArgumentException("The coordinate is out of bounds.");
	}

	@Override
	public int getMaxXAtZ(int z) {
	    for (int crossSectionW = crossSectionMaxW, crossSectionZ = slope*crossSectionW + zOffsetFromW; crossSectionW >= crossSectionMinW; crossSectionW--, crossSectionZ -= slope) {
	        if (z >= source.getMinYAtWZ(crossSectionW, crossSectionZ)
	                && z <= source.getMaxYAtWZ(crossSectionW, crossSectionZ)) {
	            return crossSectionW;
	        }
	    }
	    throw new IllegalArgumentException("The coordinate is out of bounds.");
	}

	@Override
	public int getMinXAtWY(int w, int y) {
	    for (int crossSectionW = crossSectionMinW, crossSectionZ = slope*crossSectionW + zOffsetFromW; crossSectionW <= crossSectionMaxW; crossSectionW++, crossSectionZ += slope) {
	        if (w >= source.getMinVAtWZ(crossSectionW, crossSectionZ)
	                && w <= source.getMaxVAtWZ(crossSectionW, crossSectionZ)
	                && y >= source.getMinXAtVWZ(w, crossSectionW, crossSectionZ)
	                && y <= source.getMaxXAtVWZ(w, crossSectionW, crossSectionZ)) {
	            return crossSectionW;
	        }
	    }
	    throw new IllegalArgumentException("The coordinates are out of bounds.");
	}

	@Override
	public int getMaxXAtWY(int w, int y) {
	    for (int crossSectionW = crossSectionMaxW, crossSectionZ = slope*crossSectionW + zOffsetFromW; crossSectionW >= crossSectionMinW; crossSectionW--, crossSectionZ -= slope) {
	        if (w >= source.getMinVAtWZ(crossSectionW, crossSectionZ)
	                && w <= source.getMaxVAtWZ(crossSectionW, crossSectionZ)
	                && y >= source.getMinXAtVWZ(w, crossSectionW, crossSectionZ)
	                && y <= source.getMaxXAtVWZ(w, crossSectionW, crossSectionZ)) {
	            return crossSectionW;
	        }
	    }
	    throw new IllegalArgumentException("The coordinates are out of bounds.");
	}

	@Override
	public int getMinXAtWZ(int w, int z) {
	    for (int crossSectionW = crossSectionMinW, crossSectionZ = slope*crossSectionW + zOffsetFromW; crossSectionW <= crossSectionMaxW; crossSectionW++, crossSectionZ += slope) {
	        if (w >= source.getMinVAtWZ(crossSectionW, crossSectionZ)
	                && w <= source.getMaxVAtWZ(crossSectionW, crossSectionZ)
	                && z >= source.getMinYAtVWZ(w, crossSectionW, crossSectionZ)
	                && z <= source.getMaxYAtVWZ(w, crossSectionW, crossSectionZ)) {
	            return crossSectionW;
	        }
	    }
	    throw new IllegalArgumentException("The coordinates are out of bounds.");
	}

	@Override
	public int getMaxXAtWZ(int w, int z) {
	    for (int crossSectionW = crossSectionMaxW, crossSectionZ = slope*crossSectionW + zOffsetFromW; crossSectionW >= crossSectionMinW; crossSectionW--, crossSectionZ -= slope) {
	        if (w >= source.getMinVAtWZ(crossSectionW, crossSectionZ)
	                && w <= source.getMaxVAtWZ(crossSectionW, crossSectionZ)
	                && z >= source.getMinYAtVWZ(w, crossSectionW, crossSectionZ)
	                && z <= source.getMaxYAtVWZ(w, crossSectionW, crossSectionZ)) {
	            return crossSectionW;
	        }
	    }
	    throw new IllegalArgumentException("The coordinates are out of bounds.");
	}

	@Override
	public int getMinXAtYZ(int y, int z) {
	    for (int crossSectionW = crossSectionMinW, crossSectionZ = slope*crossSectionW + zOffsetFromW; crossSectionW <= crossSectionMaxW; crossSectionW++, crossSectionZ += slope) {
	        if (y >= source.getMinXAtWZ(crossSectionW, crossSectionZ)
	                && y <= source.getMaxXAtWZ(crossSectionW, crossSectionZ)
	                && z >= source.getMinYAtWXZ(crossSectionW, y, crossSectionZ)
	                && z <= source.getMaxYAtWXZ(crossSectionW, y, crossSectionZ)) {
	            return crossSectionW;
	        }
	    }
	    throw new IllegalArgumentException("The coordinates are out of bounds.");
	}

	@Override
	public int getMaxXAtYZ(int y, int z) {
	    for (int crossSectionW = crossSectionMaxW, crossSectionZ = slope*crossSectionW + zOffsetFromW; crossSectionW >= crossSectionMinW; crossSectionW--, crossSectionZ -= slope) {
	        if (y >= source.getMinXAtWZ(crossSectionW, crossSectionZ)
	                && y <= source.getMaxXAtWZ(crossSectionW, crossSectionZ)
	                && z >= source.getMinYAtWXZ(crossSectionW, y, crossSectionZ)
	                && z <= source.getMaxYAtWXZ(crossSectionW, y, crossSectionZ)) {
	            return crossSectionW;
	        }
	    }
	    throw new IllegalArgumentException("The coordinates are out of bounds.");
	}

	@Override
	public int getMinX(int w, int y, int z) {
	    for (int crossSectionW = crossSectionMinW, crossSectionZ = slope*crossSectionW + zOffsetFromW; crossSectionW <= crossSectionMaxW; crossSectionW++, crossSectionZ += slope) {
	        if (w >= source.getMinVAtWZ(crossSectionW, crossSectionZ)
	                && w <= source.getMaxVAtWZ(crossSectionW, crossSectionZ)
	                && y >= source.getMinXAtVWZ(w, crossSectionW, crossSectionZ)
	                && y <= source.getMaxXAtVWZ(w, crossSectionW, crossSectionZ)
	                && z >= source.getMinY(w, crossSectionW, y, crossSectionZ)
	                && z <= source.getMaxY(w, crossSectionW, y, crossSectionZ)) {
	            return crossSectionW;
	        }
	    }
	    throw new IllegalArgumentException("The coordinates are out of bounds.");
	}

	@Override
	public int getMaxX(int w, int y, int z) {
	    for (int crossSectionW = crossSectionMaxW, crossSectionZ = slope*crossSectionW + zOffsetFromW; crossSectionW >= crossSectionMinW; crossSectionW--, crossSectionZ -= slope) {
	        if (w >= source.getMinVAtWZ(crossSectionW, crossSectionZ)
	                && w <= source.getMaxVAtWZ(crossSectionW, crossSectionZ)
	                && y >= source.getMinXAtVWZ(w, crossSectionW, crossSectionZ)
	                && y <= source.getMaxXAtVWZ(w, crossSectionW, crossSectionZ)
	                && z >= source.getMinY(w, crossSectionW, y, crossSectionZ)
	                && z <= source.getMaxY(w, crossSectionW, y, crossSectionZ)) {
	            return crossSectionW;
	        }
	    }
	    throw new IllegalArgumentException("The coordinates are out of bounds.");
	}

	@Override
	public int getMinY() {
	    return crossSectionMinX;
	}

	@Override
	public int getMaxY() {
	    return crossSectionMaxX;
	}

	@Override
	public int getMinYAtW(int w) {
	    int crossSectionW = crossSectionMinW;
	    int crossSectionZ = slope*crossSectionW + zOffsetFromW;
	    int result = source.getMinXAtVWZ(w, crossSectionW, crossSectionZ);
	    int localMinX;
	    for (crossSectionW++, crossSectionZ += slope;
	            crossSectionW <= crossSectionMaxW && (localMinX = source.getMinXAtVWZ(w, crossSectionW, crossSectionZ)) <= result;
	            crossSectionW++, crossSectionZ += slope) {
	        result = localMinX;
	    }
	    return result;
	}

	@Override
	public int getMaxYAtW(int w) {
	    int crossSectionW = crossSectionMinW;
	    int crossSectionZ = slope*crossSectionW + zOffsetFromW;
	    int result = source.getMaxXAtVWZ(w, crossSectionW, crossSectionZ);
	    int localMaxX;
	    for (crossSectionW++, crossSectionZ += slope;
	            crossSectionW <= crossSectionMaxW && (localMaxX = source.getMaxXAtVWZ(w, crossSectionW, crossSectionZ)) >= result;
	            crossSectionW++, crossSectionZ += slope) {
	        result = localMaxX;
	    }
	    return result;
	}

	@Override
	public int getMinYAtX(int x) {
	    return source.getMinXAtWZ(x, slope*x + zOffsetFromW);
	}

	@Override
	public int getMaxYAtX(int x) {
	    return source.getMaxXAtWZ(x, slope*x + zOffsetFromW);
	}

	@Override
	public int getMinYAtZ(int z) {
	    int crossSectionW = crossSectionMinW;
	    int crossSectionZ = slope*crossSectionW + zOffsetFromW;
	    int result = source.getMinXAtWYZ(crossSectionW, z, crossSectionZ);
	    int localMinX;
	    for (crossSectionW++, crossSectionZ += slope;
	            crossSectionW <= crossSectionMaxW && (localMinX = source.getMinXAtWYZ(crossSectionW, z, crossSectionZ)) <= result;
	            crossSectionW++, crossSectionZ += slope) {
	        result = localMinX;
	    }
	    return result;
	}

	@Override
	public int getMaxYAtZ(int z) {
	    int crossSectionW = crossSectionMinW;
	    int crossSectionZ = slope*crossSectionW + zOffsetFromW;
	    int result = source.getMaxXAtWYZ(crossSectionW, z, crossSectionZ);
	    int localMaxX;
	    for (crossSectionW++, crossSectionZ += slope;
	            crossSectionW <= crossSectionMaxW && (localMaxX = source.getMaxXAtWYZ(crossSectionW, z, crossSectionZ)) >= result;
	            crossSectionW++, crossSectionZ += slope) {
	        result = localMaxX;
	    }
	    return result;
	}

	@Override
	public int getMinYAtWX(int w, int x) {
	    return source.getMinXAtVWZ(w, x, slope*x + zOffsetFromW);
	}

	@Override
	public int getMaxYAtWX(int w, int x) {
	    return source.getMaxXAtVWZ(w, x, slope*x + zOffsetFromW);
	}

	@Override
	public int getMinYAtWZ(int w, int z) {
	    int crossSectionW = crossSectionMinW;
	    int crossSectionZ = slope*crossSectionW + zOffsetFromW;
	    int result = source.getMinX(w, crossSectionW, z, crossSectionZ);
	    int localMinX;
	    for (crossSectionW++, crossSectionZ += slope;
	            crossSectionW <= crossSectionMaxW && (localMinX = source.getMinX(w, crossSectionW, z, crossSectionZ)) <= result;
	            crossSectionW++, crossSectionZ += slope) {
	        result = localMinX;
	    }
	    return result;
	}

	@Override
	public int getMaxYAtWZ(int w, int z) {
	    int crossSectionW = crossSectionMinW;
	    int crossSectionZ = slope*crossSectionW + zOffsetFromW;
	    int result = source.getMaxX(w, crossSectionW, z, crossSectionZ);
	    int localMaxX;
	    for (crossSectionW++, crossSectionZ += slope;
	            crossSectionW <= crossSectionMaxW && (localMaxX = source.getMaxX(w, crossSectionW, z, crossSectionZ)) >= result;
	            crossSectionW++, crossSectionZ += slope) {
	        result = localMaxX;
	    }
	    return result;
	}

	@Override
	public int getMinYAtXZ(int x, int z) {
	    return source.getMinXAtWYZ(x, z, slope*x + zOffsetFromW);
	}

	@Override
	public int getMaxYAtXZ(int x, int z) {
	    return source.getMaxXAtWYZ(x, z, slope*x + zOffsetFromW);
	}

	@Override
	public int getMinY(int w, int x, int z) {
	    return source.getMinX(w, x, z, slope*x + zOffsetFromW);
	}

	@Override
	public int getMaxY(int w, int x, int z) {
	    return source.getMaxX(w, x, z, slope*x + zOffsetFromW);
	}

	@Override
	public int getMinZ() {
	    return crossSectionMinY;
	}

	@Override
	public int getMaxZ() {
	    return crossSectionMaxY;
	}

	@Override
	public int getMinZAtW(int w) {
	    int crossSectionW = crossSectionMinW;
	    int crossSectionZ = slope*crossSectionW + zOffsetFromW;
	    int result = source.getMinYAtVWZ(w, crossSectionW, crossSectionZ);
	    int localMinY;
	    for (crossSectionW++, crossSectionZ += slope;
	            crossSectionW <= crossSectionMaxW && (localMinY = source.getMinYAtVWZ(w, crossSectionW, crossSectionZ)) <= result;
	            crossSectionW++, crossSectionZ += slope) {
	        result = localMinY;
	    }
	    return result;
	}

	@Override
	public int getMaxZAtW(int w) {
	    int crossSectionW = crossSectionMinW;
	    int crossSectionZ = slope*crossSectionW + zOffsetFromW;
	    int result = source.getMaxYAtVWZ(w, crossSectionW, crossSectionZ);
	    int localMaxY;
	    for (crossSectionW++, crossSectionZ += slope;
	            crossSectionW <= crossSectionMaxW && (localMaxY = source.getMaxYAtVWZ(w, crossSectionW, crossSectionZ)) >= result;
	            crossSectionW++, crossSectionZ += slope) {
	        result = localMaxY;
	    }
	    return result;
	}

	@Override
	public int getMinZAtX(int x) {
	    return source.getMinYAtWZ(x, slope*x + zOffsetFromW);
	}

	@Override
	public int getMaxZAtX(int x) {
	    return source.getMaxYAtWZ(x, slope*x + zOffsetFromW);
	}

	@Override
	public int getMinZAtY(int y) {
	    int crossSectionW = crossSectionMinW;
	    int crossSectionZ = slope*crossSectionW + zOffsetFromW;
	    int result = source.getMinYAtWXZ(crossSectionW, y, crossSectionZ);
	    int localMinY;
	    for (crossSectionW++, crossSectionZ += slope;
	            crossSectionW <= crossSectionMaxW && (localMinY = source.getMinYAtWXZ(crossSectionW, y, crossSectionZ)) <= result;
	            crossSectionW++, crossSectionZ += slope) {
	        result = localMinY;
	    }
	    return result;
	}

	@Override
	public int getMaxZAtY(int y) {
	    int crossSectionW = crossSectionMinW;
	    int crossSectionZ = slope*crossSectionW + zOffsetFromW;
	    int result = source.getMaxYAtWXZ(crossSectionW, y, crossSectionZ);
	    int localMaxY;
	    for (crossSectionW++, crossSectionZ += slope;
	            crossSectionW <= crossSectionMaxW && (localMaxY = source.getMaxYAtWXZ(crossSectionW, y, crossSectionZ)) >= result;
	            crossSectionW++, crossSectionZ += slope) {
	        result = localMaxY;
	    }
	    return result;
	}

	@Override
	public int getMinZAtWX(int w, int x) {
	    return source.getMinYAtVWZ(w, x, slope*x + zOffsetFromW);
	}

	@Override
	public int getMaxZAtWX(int w, int x) {
	    return source.getMaxYAtVWZ(w, x, slope*x + zOffsetFromW);
	}

	@Override
	public int getMinZAtWY(int w, int y) {
	    int crossSectionW = crossSectionMinW;
	    int crossSectionZ = slope*crossSectionW + zOffsetFromW;
	    int result = source.getMinY(w, crossSectionW, y, crossSectionZ);
	    int localMinY;
	    for (crossSectionW++, crossSectionZ += slope;
	            crossSectionW <= crossSectionMaxW && (localMinY = source.getMinY(w, crossSectionW, y, crossSectionZ)) <= result;
	            crossSectionW++, crossSectionZ += slope) {
	        result = localMinY;
	    }
	    return result;
	}

	@Override
	public int getMaxZAtWY(int w, int y) {
	    int crossSectionW = crossSectionMinW;
	    int crossSectionZ = slope*crossSectionW + zOffsetFromW;
	    int result = source.getMaxY(w, crossSectionW, y, crossSectionZ);
	    int localMaxY;
	    for (crossSectionW++, crossSectionZ += slope;
	            crossSectionW <= crossSectionMaxW && (localMaxY = source.getMaxY(w, crossSectionW, y, crossSectionZ)) >= result;
	            crossSectionW++, crossSectionZ += slope) {
	        result = localMaxY;
	    }
	    return result;
	}

	@Override
	public int getMinZAtXY(int x, int y) {
	    return source.getMinYAtWXZ(x, y, slope*x + zOffsetFromW);
	}

	@Override
	public int getMaxZAtXY(int x, int y) {
	    return source.getMaxYAtWXZ(x, y, slope*x + zOffsetFromW);
	}

	@Override
	public int getMinZ(int w, int x, int y) {
	    return source.getMinY(w, x, y, slope*x + zOffsetFromW);
	}

	@Override
	public int getMaxZ(int w, int x, int y) {
	    return source.getMaxY(w, x, y, slope*x + zOffsetFromW);
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
	    StringBuilder path = new StringBuilder(source.getSubfolderPath()).append("/").append(source.getZLabel()).append("=");
	    if (slope == -1) {
	        path.append("-");
	    }
	    path.append(source.getWLabel());
	    if (zOffsetFromW < 0) {
	        path.append(zOffsetFromW);
	    } else if (zOffsetFromW > 0) {
	        path.append("+").append(zOffsetFromW);
	    }
	    return path.toString();
	}

	@Override
	public void backUp(String backupPath, String backupName) throws FileNotFoundException, IOException {
	    source.backUp(backupPath, backupName);
	}

}
