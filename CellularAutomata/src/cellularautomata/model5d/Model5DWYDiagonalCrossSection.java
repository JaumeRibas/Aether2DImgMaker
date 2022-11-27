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

public class Model5DWYDiagonalCrossSection<Source_Type extends Model5D> implements Model4D {

	protected Source_Type source;
	protected int slope;
	protected int yOffsetFromW;
	protected int crossSectionMinV;
	protected int crossSectionMaxV;
	protected int crossSectionMinW;
	protected int crossSectionMaxW;
	protected int crossSectionMinX;
	protected int crossSectionMaxX;
	protected int crossSectionMinZ;
	protected int crossSectionMaxZ;

	public Model5DWYDiagonalCrossSection(Source_Type source, boolean positiveSlope, int yOffsetFromW) {
	    this.source = source;
	    this.slope = positiveSlope ? 1 : -1;
	    this.yOffsetFromW = yOffsetFromW;
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
	    return source.getZLabel();
	}

	protected boolean getBounds() {
	    int w = source.getMinW();
	    int maxW = source.getMaxW();
	    int crossSectionY = slope*w + yOffsetFromW;
	    while (w <= maxW && (crossSectionY < source.getMinYAtW(w) || crossSectionY > source.getMaxYAtW(w))) {
	        w++;
	        crossSectionY += slope;
	    }
	    if (w <= maxW) {
	        crossSectionMinW = w;
	        crossSectionMaxW = w;
	        crossSectionMinV = source.getMinVAtWY(w, crossSectionY);
	        crossSectionMaxV = source.getMaxVAtWY(w, crossSectionY);
	        crossSectionMinX = source.getMinXAtWY(w, crossSectionY);
	        crossSectionMaxX = source.getMaxXAtWY(w, crossSectionY);
	        crossSectionMinZ = source.getMinZAtWY(w, crossSectionY);
	        crossSectionMaxZ = source.getMaxZAtWY(w, crossSectionY);
	        w++;
	        crossSectionY += slope;
	        while (w <= maxW && crossSectionY >= source.getMinYAtW(w) && crossSectionY <= source.getMaxYAtW(w)) {
	            crossSectionMaxW = w;
	            int localMinV = source.getMinVAtWY(w, crossSectionY);
	            if (localMinV < crossSectionMinV) {
	                crossSectionMinV = localMinV;
	            }
	            int localMaxV = source.getMaxVAtWY(w, crossSectionY);
	            if (localMaxV > crossSectionMaxV) {
	                crossSectionMaxV = localMaxV;
	            }
	            int localMinX = source.getMinXAtWY(w, crossSectionY);
	            if (localMinX < crossSectionMinX) {
	                crossSectionMinX = localMinX;
	            }
	            int localMaxX = source.getMaxXAtWY(w, crossSectionY);
	            if (localMaxX > crossSectionMaxX) {
	                crossSectionMaxX = localMaxX;
	            }
	            int localMinZ = source.getMinZAtWY(w, crossSectionY);
	            if (localMinZ < crossSectionMinZ) {
	                crossSectionMinZ = localMinZ;
	            }
	            int localMaxZ = source.getMaxZAtWY(w, crossSectionY);
	            if (localMaxZ > crossSectionMaxZ) {
	                crossSectionMaxZ = localMaxZ;
	            }
	            w++;
	            crossSectionY += slope;
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
	    return source.getMinVAtWY(x, slope*x + yOffsetFromW);
	}

	@Override
	public int getMaxWAtX(int x) {
	    return source.getMaxVAtWY(x, slope*x + yOffsetFromW);
	}

	@Override
	public int getMinWAtY(int y) {
	    int crossSectionW = crossSectionMinW;
	    int crossSectionY = slope*crossSectionW + yOffsetFromW;
	    int result = source.getMinVAtWXY(crossSectionW, y, crossSectionY);
	    int localMinV;
	    for (crossSectionW++, crossSectionY += slope;
	            crossSectionW <= crossSectionMaxW && (localMinV = source.getMinVAtWXY(crossSectionW, y, crossSectionY)) <= result;
	            crossSectionW++, crossSectionY += slope) {
	        result = localMinV;
	    }
	    return result;
	}

	@Override
	public int getMaxWAtY(int y) {
	    int crossSectionW = crossSectionMinW;
	    int crossSectionY = slope*crossSectionW + yOffsetFromW;
	    int result = source.getMaxVAtWXY(crossSectionW, y, crossSectionY);
	    int localMaxV;
	    for (crossSectionW++, crossSectionY += slope;
	            crossSectionW <= crossSectionMaxW && (localMaxV = source.getMaxVAtWXY(crossSectionW, y, crossSectionY)) >= result;
	            crossSectionW++, crossSectionY += slope) {
	        result = localMaxV;
	    }
	    return result;
	}

	@Override
	public int getMinWAtZ(int z) {
	    int crossSectionW = crossSectionMinW;
	    int crossSectionY = slope*crossSectionW + yOffsetFromW;
	    int result = source.getMinVAtWYZ(crossSectionW, crossSectionY, z);
	    int localMinV;
	    for (crossSectionW++, crossSectionY += slope;
	            crossSectionW <= crossSectionMaxW && (localMinV = source.getMinVAtWYZ(crossSectionW, crossSectionY, z)) <= result;
	            crossSectionW++, crossSectionY += slope) {
	        result = localMinV;
	    }
	    return result;
	}

	@Override
	public int getMaxWAtZ(int z) {
	    int crossSectionW = crossSectionMinW;
	    int crossSectionY = slope*crossSectionW + yOffsetFromW;
	    int result = source.getMaxVAtWYZ(crossSectionW, crossSectionY, z);
	    int localMaxV;
	    for (crossSectionW++, crossSectionY += slope;
	            crossSectionW <= crossSectionMaxW && (localMaxV = source.getMaxVAtWYZ(crossSectionW, crossSectionY, z)) >= result;
	            crossSectionW++, crossSectionY += slope) {
	        result = localMaxV;
	    }
	    return result;
	}

	@Override
	public int getMinWAtXY(int x, int y) {
	    return source.getMinVAtWXY(x, y, slope*x + yOffsetFromW);
	}

	@Override
	public int getMaxWAtXY(int x, int y) {
	    return source.getMaxVAtWXY(x, y, slope*x + yOffsetFromW);
	}

	@Override
	public int getMinWAtXZ(int x, int z) {
	    return source.getMinVAtWYZ(x, slope*x + yOffsetFromW, z);
	}

	@Override
	public int getMaxWAtXZ(int x, int z) {
	    return source.getMaxVAtWYZ(x, slope*x + yOffsetFromW, z);
	}

	@Override
	public int getMinWAtYZ(int y, int z) {
	    int crossSectionW = crossSectionMinW;
	    int crossSectionY = slope*crossSectionW + yOffsetFromW;
	    int result = source.getMinV(crossSectionW, y, crossSectionY, z);
	    int localMinV;
	    for (crossSectionW++, crossSectionY += slope;
	            crossSectionW <= crossSectionMaxW && (localMinV = source.getMinV(crossSectionW, y, crossSectionY, z)) <= result;
	            crossSectionW++, crossSectionY += slope) {
	        result = localMinV;
	    }
	    return result;
	}

	@Override
	public int getMaxWAtYZ(int y, int z) {
	    int crossSectionW = crossSectionMinW;
	    int crossSectionY = slope*crossSectionW + yOffsetFromW;
	    int result = source.getMaxV(crossSectionW, y, crossSectionY, z);
	    int localMaxV;
	    for (crossSectionW++, crossSectionY += slope;
	            crossSectionW <= crossSectionMaxW && (localMaxV = source.getMaxV(crossSectionW, y, crossSectionY, z)) >= result;
	            crossSectionW++, crossSectionY += slope) {
	        result = localMaxV;
	    }
	    return result;
	}

	@Override
	public int getMinW(int x, int y, int z) {
	    return source.getMinV(x, y, slope*x + yOffsetFromW, z);
	}

	@Override
	public int getMaxW(int x, int y, int z) {
	    return source.getMaxV(x, y, slope*x + yOffsetFromW, z);
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
	    for (int crossSectionW = crossSectionMinW, crossSectionY = slope*crossSectionW + yOffsetFromW; crossSectionW <= crossSectionMaxW; crossSectionW++, crossSectionY += slope) {
	        if (w >= source.getMinVAtWY(crossSectionW, crossSectionY)
	                && w <= source.getMaxVAtWY(crossSectionW, crossSectionY)) {
	            return crossSectionW;
	        }
	    }
	    throw new IllegalArgumentException("The coordinate is out of bounds.");
	}

	@Override
	public int getMaxXAtW(int w) {
	    for (int crossSectionW = crossSectionMaxW, crossSectionY = slope*crossSectionW + yOffsetFromW; crossSectionW >= crossSectionMinW; crossSectionW--, crossSectionY -= slope) {
	        if (w >= source.getMinVAtWY(crossSectionW, crossSectionY)
	                && w <= source.getMaxVAtWY(crossSectionW, crossSectionY)) {
	            return crossSectionW;
	        }
	    }
	    throw new IllegalArgumentException("The coordinate is out of bounds.");
	}

	@Override
	public int getMinXAtY(int y) {
	    for (int crossSectionW = crossSectionMinW, crossSectionY = slope*crossSectionW + yOffsetFromW; crossSectionW <= crossSectionMaxW; crossSectionW++, crossSectionY += slope) {
	        if (y >= source.getMinXAtWY(crossSectionW, crossSectionY)
	                && y <= source.getMaxXAtWY(crossSectionW, crossSectionY)) {
	            return crossSectionW;
	        }
	    }
	    throw new IllegalArgumentException("The coordinate is out of bounds.");
	}

	@Override
	public int getMaxXAtY(int y) {
	    for (int crossSectionW = crossSectionMaxW, crossSectionY = slope*crossSectionW + yOffsetFromW; crossSectionW >= crossSectionMinW; crossSectionW--, crossSectionY -= slope) {
	        if (y >= source.getMinXAtWY(crossSectionW, crossSectionY)
	                && y <= source.getMaxXAtWY(crossSectionW, crossSectionY)) {
	            return crossSectionW;
	        }
	    }
	    throw new IllegalArgumentException("The coordinate is out of bounds.");
	}

	@Override
	public int getMinXAtZ(int z) {
	    for (int crossSectionW = crossSectionMinW, crossSectionY = slope*crossSectionW + yOffsetFromW; crossSectionW <= crossSectionMaxW; crossSectionW++, crossSectionY += slope) {
	        if (z >= source.getMinZAtWY(crossSectionW, crossSectionY)
	                && z <= source.getMaxZAtWY(crossSectionW, crossSectionY)) {
	            return crossSectionW;
	        }
	    }
	    throw new IllegalArgumentException("The coordinate is out of bounds.");
	}

	@Override
	public int getMaxXAtZ(int z) {
	    for (int crossSectionW = crossSectionMaxW, crossSectionY = slope*crossSectionW + yOffsetFromW; crossSectionW >= crossSectionMinW; crossSectionW--, crossSectionY -= slope) {
	        if (z >= source.getMinZAtWY(crossSectionW, crossSectionY)
	                && z <= source.getMaxZAtWY(crossSectionW, crossSectionY)) {
	            return crossSectionW;
	        }
	    }
	    throw new IllegalArgumentException("The coordinate is out of bounds.");
	}

	@Override
	public int getMinXAtWY(int w, int y) {
	    for (int crossSectionW = crossSectionMinW, crossSectionY = slope*crossSectionW + yOffsetFromW; crossSectionW <= crossSectionMaxW; crossSectionW++, crossSectionY += slope) {
	        if (w >= source.getMinVAtWY(crossSectionW, crossSectionY)
	                && w <= source.getMaxVAtWY(crossSectionW, crossSectionY)
	                && y >= source.getMinXAtVWY(w, crossSectionW, crossSectionY)
	                && y <= source.getMaxXAtVWY(w, crossSectionW, crossSectionY)) {
	            return crossSectionW;
	        }
	    }
	    throw new IllegalArgumentException("The coordinates are out of bounds.");
	}

	@Override
	public int getMaxXAtWY(int w, int y) {
	    for (int crossSectionW = crossSectionMaxW, crossSectionY = slope*crossSectionW + yOffsetFromW; crossSectionW >= crossSectionMinW; crossSectionW--, crossSectionY -= slope) {
	        if (w >= source.getMinVAtWY(crossSectionW, crossSectionY)
	                && w <= source.getMaxVAtWY(crossSectionW, crossSectionY)
	                && y >= source.getMinXAtVWY(w, crossSectionW, crossSectionY)
	                && y <= source.getMaxXAtVWY(w, crossSectionW, crossSectionY)) {
	            return crossSectionW;
	        }
	    }
	    throw new IllegalArgumentException("The coordinates are out of bounds.");
	}

	@Override
	public int getMinXAtWZ(int w, int z) {
	    for (int crossSectionW = crossSectionMinW, crossSectionY = slope*crossSectionW + yOffsetFromW; crossSectionW <= crossSectionMaxW; crossSectionW++, crossSectionY += slope) {
	        if (w >= source.getMinVAtWY(crossSectionW, crossSectionY)
	                && w <= source.getMaxVAtWY(crossSectionW, crossSectionY)
	                && z >= source.getMinZAtVWY(w, crossSectionW, crossSectionY)
	                && z <= source.getMaxZAtVWY(w, crossSectionW, crossSectionY)) {
	            return crossSectionW;
	        }
	    }
	    throw new IllegalArgumentException("The coordinates are out of bounds.");
	}

	@Override
	public int getMaxXAtWZ(int w, int z) {
	    for (int crossSectionW = crossSectionMaxW, crossSectionY = slope*crossSectionW + yOffsetFromW; crossSectionW >= crossSectionMinW; crossSectionW--, crossSectionY -= slope) {
	        if (w >= source.getMinVAtWY(crossSectionW, crossSectionY)
	                && w <= source.getMaxVAtWY(crossSectionW, crossSectionY)
	                && z >= source.getMinZAtVWY(w, crossSectionW, crossSectionY)
	                && z <= source.getMaxZAtVWY(w, crossSectionW, crossSectionY)) {
	            return crossSectionW;
	        }
	    }
	    throw new IllegalArgumentException("The coordinates are out of bounds.");
	}

	@Override
	public int getMinXAtYZ(int y, int z) {
	    for (int crossSectionW = crossSectionMinW, crossSectionY = slope*crossSectionW + yOffsetFromW; crossSectionW <= crossSectionMaxW; crossSectionW++, crossSectionY += slope) {
	        if (y >= source.getMinXAtWY(crossSectionW, crossSectionY)
	                && y <= source.getMaxXAtWY(crossSectionW, crossSectionY)
	                && z >= source.getMinZAtWXY(crossSectionW, y, crossSectionY)
	                && z <= source.getMaxZAtWXY(crossSectionW, y, crossSectionY)) {
	            return crossSectionW;
	        }
	    }
	    throw new IllegalArgumentException("The coordinates are out of bounds.");
	}

	@Override
	public int getMaxXAtYZ(int y, int z) {
	    for (int crossSectionW = crossSectionMaxW, crossSectionY = slope*crossSectionW + yOffsetFromW; crossSectionW >= crossSectionMinW; crossSectionW--, crossSectionY -= slope) {
	        if (y >= source.getMinXAtWY(crossSectionW, crossSectionY)
	                && y <= source.getMaxXAtWY(crossSectionW, crossSectionY)
	                && z >= source.getMinZAtWXY(crossSectionW, y, crossSectionY)
	                && z <= source.getMaxZAtWXY(crossSectionW, y, crossSectionY)) {
	            return crossSectionW;
	        }
	    }
	    throw new IllegalArgumentException("The coordinates are out of bounds.");
	}

	@Override
	public int getMinX(int w, int y, int z) {
	    for (int crossSectionW = crossSectionMinW, crossSectionY = slope*crossSectionW + yOffsetFromW; crossSectionW <= crossSectionMaxW; crossSectionW++, crossSectionY += slope) {
	        if (w >= source.getMinVAtWY(crossSectionW, crossSectionY)
	                && w <= source.getMaxVAtWY(crossSectionW, crossSectionY)
	                && y >= source.getMinXAtVWY(w, crossSectionW, crossSectionY)
	                && y <= source.getMaxXAtVWY(w, crossSectionW, crossSectionY)
	                && z >= source.getMinZ(w, crossSectionW, y, crossSectionY)
	                && z <= source.getMaxZ(w, crossSectionW, y, crossSectionY)) {
	            return crossSectionW;
	        }
	    }
	    throw new IllegalArgumentException("The coordinates are out of bounds.");
	}

	@Override
	public int getMaxX(int w, int y, int z) {
	    for (int crossSectionW = crossSectionMaxW, crossSectionY = slope*crossSectionW + yOffsetFromW; crossSectionW >= crossSectionMinW; crossSectionW--, crossSectionY -= slope) {
	        if (w >= source.getMinVAtWY(crossSectionW, crossSectionY)
	                && w <= source.getMaxVAtWY(crossSectionW, crossSectionY)
	                && y >= source.getMinXAtVWY(w, crossSectionW, crossSectionY)
	                && y <= source.getMaxXAtVWY(w, crossSectionW, crossSectionY)
	                && z >= source.getMinZ(w, crossSectionW, y, crossSectionY)
	                && z <= source.getMaxZ(w, crossSectionW, y, crossSectionY)) {
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
	    int crossSectionY = slope*crossSectionW + yOffsetFromW;
	    int result = source.getMinXAtVWY(w, crossSectionW, crossSectionY);
	    int localMinX;
	    for (crossSectionW++, crossSectionY += slope;
	            crossSectionW <= crossSectionMaxW && (localMinX = source.getMinXAtVWY(w, crossSectionW, crossSectionY)) <= result;
	            crossSectionW++, crossSectionY += slope) {
	        result = localMinX;
	    }
	    return result;
	}

	@Override
	public int getMaxYAtW(int w) {
	    int crossSectionW = crossSectionMinW;
	    int crossSectionY = slope*crossSectionW + yOffsetFromW;
	    int result = source.getMaxXAtVWY(w, crossSectionW, crossSectionY);
	    int localMaxX;
	    for (crossSectionW++, crossSectionY += slope;
	            crossSectionW <= crossSectionMaxW && (localMaxX = source.getMaxXAtVWY(w, crossSectionW, crossSectionY)) >= result;
	            crossSectionW++, crossSectionY += slope) {
	        result = localMaxX;
	    }
	    return result;
	}

	@Override
	public int getMinYAtX(int x) {
	    return source.getMinXAtWY(x, slope*x + yOffsetFromW);
	}

	@Override
	public int getMaxYAtX(int x) {
	    return source.getMaxXAtWY(x, slope*x + yOffsetFromW);
	}

	@Override
	public int getMinYAtZ(int z) {
	    int crossSectionW = crossSectionMinW;
	    int crossSectionY = slope*crossSectionW + yOffsetFromW;
	    int result = source.getMinXAtWYZ(crossSectionW, crossSectionY, z);
	    int localMinX;
	    for (crossSectionW++, crossSectionY += slope;
	            crossSectionW <= crossSectionMaxW && (localMinX = source.getMinXAtWYZ(crossSectionW, crossSectionY, z)) <= result;
	            crossSectionW++, crossSectionY += slope) {
	        result = localMinX;
	    }
	    return result;
	}

	@Override
	public int getMaxYAtZ(int z) {
	    int crossSectionW = crossSectionMinW;
	    int crossSectionY = slope*crossSectionW + yOffsetFromW;
	    int result = source.getMaxXAtWYZ(crossSectionW, crossSectionY, z);
	    int localMaxX;
	    for (crossSectionW++, crossSectionY += slope;
	            crossSectionW <= crossSectionMaxW && (localMaxX = source.getMaxXAtWYZ(crossSectionW, crossSectionY, z)) >= result;
	            crossSectionW++, crossSectionY += slope) {
	        result = localMaxX;
	    }
	    return result;
	}

	@Override
	public int getMinYAtWX(int w, int x) {
	    return source.getMinXAtVWY(w, x, slope*x + yOffsetFromW);
	}

	@Override
	public int getMaxYAtWX(int w, int x) {
	    return source.getMaxXAtVWY(w, x, slope*x + yOffsetFromW);
	}

	@Override
	public int getMinYAtWZ(int w, int z) {
	    int crossSectionW = crossSectionMinW;
	    int crossSectionY = slope*crossSectionW + yOffsetFromW;
	    int result = source.getMinX(w, crossSectionW, crossSectionY, z);
	    int localMinX;
	    for (crossSectionW++, crossSectionY += slope;
	            crossSectionW <= crossSectionMaxW && (localMinX = source.getMinX(w, crossSectionW, crossSectionY, z)) <= result;
	            crossSectionW++, crossSectionY += slope) {
	        result = localMinX;
	    }
	    return result;
	}

	@Override
	public int getMaxYAtWZ(int w, int z) {
	    int crossSectionW = crossSectionMinW;
	    int crossSectionY = slope*crossSectionW + yOffsetFromW;
	    int result = source.getMaxX(w, crossSectionW, crossSectionY, z);
	    int localMaxX;
	    for (crossSectionW++, crossSectionY += slope;
	            crossSectionW <= crossSectionMaxW && (localMaxX = source.getMaxX(w, crossSectionW, crossSectionY, z)) >= result;
	            crossSectionW++, crossSectionY += slope) {
	        result = localMaxX;
	    }
	    return result;
	}

	@Override
	public int getMinYAtXZ(int x, int z) {
	    return source.getMinXAtWYZ(x, slope*x + yOffsetFromW, z);
	}

	@Override
	public int getMaxYAtXZ(int x, int z) {
	    return source.getMaxXAtWYZ(x, slope*x + yOffsetFromW, z);
	}

	@Override
	public int getMinY(int w, int x, int z) {
	    return source.getMinX(w, x, slope*x + yOffsetFromW, z);
	}

	@Override
	public int getMaxY(int w, int x, int z) {
	    return source.getMaxX(w, x, slope*x + yOffsetFromW, z);
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
	    int crossSectionY = slope*crossSectionW + yOffsetFromW;
	    int result = source.getMinZAtVWY(w, crossSectionW, crossSectionY);
	    int localMinZ;
	    for (crossSectionW++, crossSectionY += slope;
	            crossSectionW <= crossSectionMaxW && (localMinZ = source.getMinZAtVWY(w, crossSectionW, crossSectionY)) <= result;
	            crossSectionW++, crossSectionY += slope) {
	        result = localMinZ;
	    }
	    return result;
	}

	@Override
	public int getMaxZAtW(int w) {
	    int crossSectionW = crossSectionMinW;
	    int crossSectionY = slope*crossSectionW + yOffsetFromW;
	    int result = source.getMaxZAtVWY(w, crossSectionW, crossSectionY);
	    int localMaxZ;
	    for (crossSectionW++, crossSectionY += slope;
	            crossSectionW <= crossSectionMaxW && (localMaxZ = source.getMaxZAtVWY(w, crossSectionW, crossSectionY)) >= result;
	            crossSectionW++, crossSectionY += slope) {
	        result = localMaxZ;
	    }
	    return result;
	}

	@Override
	public int getMinZAtX(int x) {
	    return source.getMinZAtWY(x, slope*x + yOffsetFromW);
	}

	@Override
	public int getMaxZAtX(int x) {
	    return source.getMaxZAtWY(x, slope*x + yOffsetFromW);
	}

	@Override
	public int getMinZAtY(int y) {
	    int crossSectionW = crossSectionMinW;
	    int crossSectionY = slope*crossSectionW + yOffsetFromW;
	    int result = source.getMinZAtWXY(crossSectionW, y, crossSectionY);
	    int localMinZ;
	    for (crossSectionW++, crossSectionY += slope;
	            crossSectionW <= crossSectionMaxW && (localMinZ = source.getMinZAtWXY(crossSectionW, y, crossSectionY)) <= result;
	            crossSectionW++, crossSectionY += slope) {
	        result = localMinZ;
	    }
	    return result;
	}

	@Override
	public int getMaxZAtY(int y) {
	    int crossSectionW = crossSectionMinW;
	    int crossSectionY = slope*crossSectionW + yOffsetFromW;
	    int result = source.getMaxZAtWXY(crossSectionW, y, crossSectionY);
	    int localMaxZ;
	    for (crossSectionW++, crossSectionY += slope;
	            crossSectionW <= crossSectionMaxW && (localMaxZ = source.getMaxZAtWXY(crossSectionW, y, crossSectionY)) >= result;
	            crossSectionW++, crossSectionY += slope) {
	        result = localMaxZ;
	    }
	    return result;
	}

	@Override
	public int getMinZAtWX(int w, int x) {
	    return source.getMinZAtVWY(w, x, slope*x + yOffsetFromW);
	}

	@Override
	public int getMaxZAtWX(int w, int x) {
	    return source.getMaxZAtVWY(w, x, slope*x + yOffsetFromW);
	}

	@Override
	public int getMinZAtWY(int w, int y) {
	    int crossSectionW = crossSectionMinW;
	    int crossSectionY = slope*crossSectionW + yOffsetFromW;
	    int result = source.getMinZ(w, crossSectionW, y, crossSectionY);
	    int localMinZ;
	    for (crossSectionW++, crossSectionY += slope;
	            crossSectionW <= crossSectionMaxW && (localMinZ = source.getMinZ(w, crossSectionW, y, crossSectionY)) <= result;
	            crossSectionW++, crossSectionY += slope) {
	        result = localMinZ;
	    }
	    return result;
	}

	@Override
	public int getMaxZAtWY(int w, int y) {
	    int crossSectionW = crossSectionMinW;
	    int crossSectionY = slope*crossSectionW + yOffsetFromW;
	    int result = source.getMaxZ(w, crossSectionW, y, crossSectionY);
	    int localMaxZ;
	    for (crossSectionW++, crossSectionY += slope;
	            crossSectionW <= crossSectionMaxW && (localMaxZ = source.getMaxZ(w, crossSectionW, y, crossSectionY)) >= result;
	            crossSectionW++, crossSectionY += slope) {
	        result = localMaxZ;
	    }
	    return result;
	}

	@Override
	public int getMinZAtXY(int x, int y) {
	    return source.getMinZAtWXY(x, y, slope*x + yOffsetFromW);
	}

	@Override
	public int getMaxZAtXY(int x, int y) {
	    return source.getMaxZAtWXY(x, y, slope*x + yOffsetFromW);
	}

	@Override
	public int getMinZ(int w, int x, int y) {
	    return source.getMinZ(w, x, y, slope*x + yOffsetFromW);
	}

	@Override
	public int getMaxZ(int w, int x, int y) {
	    return source.getMaxZ(w, x, y, slope*x + yOffsetFromW);
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
	    StringBuilder path = new StringBuilder(source.getSubfolderPath()).append("/").append(source.getYLabel()).append("=");
	    if (slope == -1) {
	        path.append("-");
	    }
	    path.append(source.getWLabel());
	    if (yOffsetFromW < 0) {
	        path.append(yOffsetFromW);
	    } else if (yOffsetFromW > 0) {
	        path.append("+").append(yOffsetFromW);
	    }
	    return path.toString();
	}

	@Override
	public void backUp(String backupPath, String backupName) throws FileNotFoundException, IOException {
	    source.backUp(backupPath, backupName);
	}

}
