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

public class Model5DXZDiagonalCrossSection<Source_Type extends Model5D> implements Model4D {

	protected Source_Type source;
	protected int slope;
	protected int zOffsetFromX;
	protected int crossSectionMinV;
	protected int crossSectionMaxV;
	protected int crossSectionMinW;
	protected int crossSectionMaxW;
	protected int crossSectionMinX;
	protected int crossSectionMaxX;
	protected int crossSectionMinY;
	protected int crossSectionMaxY;

	public Model5DXZDiagonalCrossSection(Source_Type source, boolean positiveSlope, int zOffsetFromX) {
	    this.source = source;
	    this.slope = positiveSlope ? 1 : -1;
	    this.zOffsetFromX = zOffsetFromX;
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
	    int x = source.getMinX();
	    int maxX = source.getMaxX();
	    int crossSectionZ = slope*x + zOffsetFromX;
	    while (x <= maxX && (crossSectionZ < source.getMinZAtX(x) || crossSectionZ > source.getMaxZAtX(x))) {
	        x++;
	        crossSectionZ += slope;
	    }
	    if (x <= maxX) {
	        crossSectionMinX = x;
	        crossSectionMaxX = x;
	        crossSectionMinV = source.getMinVAtXZ(x, crossSectionZ);
	        crossSectionMaxV = source.getMaxVAtXZ(x, crossSectionZ);
	        crossSectionMinW = source.getMinWAtXZ(x, crossSectionZ);
	        crossSectionMaxW = source.getMaxWAtXZ(x, crossSectionZ);
	        crossSectionMinY = source.getMinYAtXZ(x, crossSectionZ);
	        crossSectionMaxY = source.getMaxYAtXZ(x, crossSectionZ);
	        x++;
	        crossSectionZ += slope;
	        while (x <= maxX && crossSectionZ >= source.getMinZAtX(x) && crossSectionZ <= source.getMaxZAtX(x)) {
	            crossSectionMaxX = x;
	            int localMinV = source.getMinVAtXZ(x, crossSectionZ);
	            if (localMinV < crossSectionMinV) {
	                crossSectionMinV = localMinV;
	            }
	            int localMaxV = source.getMaxVAtXZ(x, crossSectionZ);
	            if (localMaxV > crossSectionMaxV) {
	                crossSectionMaxV = localMaxV;
	            }
	            int localMinW = source.getMinWAtXZ(x, crossSectionZ);
	            if (localMinW < crossSectionMinW) {
	                crossSectionMinW = localMinW;
	            }
	            int localMaxW = source.getMaxWAtXZ(x, crossSectionZ);
	            if (localMaxW > crossSectionMaxW) {
	                crossSectionMaxW = localMaxW;
	            }
	            int localMinY = source.getMinYAtXZ(x, crossSectionZ);
	            if (localMinY < crossSectionMinY) {
	                crossSectionMinY = localMinY;
	            }
	            int localMaxY = source.getMaxYAtXZ(x, crossSectionZ);
	            if (localMaxY > crossSectionMaxY) {
	                crossSectionMaxY = localMaxY;
	            }
	            x++;
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
	    int crossSectionX = crossSectionMinX;
	    int crossSectionZ = slope*crossSectionX + zOffsetFromX;
	    int result = source.getMinVAtWXZ(x, crossSectionX, crossSectionZ);
	    int localMinV;
	    for (crossSectionX++, crossSectionZ += slope;
	            crossSectionX <= crossSectionMaxX && (localMinV = source.getMinVAtWXZ(x, crossSectionX, crossSectionZ)) <= result;
	            crossSectionX++, crossSectionZ += slope) {
	        result = localMinV;
	    }
	    return result;
	}

	@Override
	public int getMaxWAtX(int x) {
	    int crossSectionX = crossSectionMinX;
	    int crossSectionZ = slope*crossSectionX + zOffsetFromX;
	    int result = source.getMaxVAtWXZ(x, crossSectionX, crossSectionZ);
	    int localMaxV;
	    for (crossSectionX++, crossSectionZ += slope;
	            crossSectionX <= crossSectionMaxX && (localMaxV = source.getMaxVAtWXZ(x, crossSectionX, crossSectionZ)) >= result;
	            crossSectionX++, crossSectionZ += slope) {
	        result = localMaxV;
	    }
	    return result;
	}

	@Override
	public int getMinWAtY(int y) {
	    return source.getMinVAtXZ(y, slope*y + zOffsetFromX);
	}

	@Override
	public int getMaxWAtY(int y) {
	    return source.getMaxVAtXZ(y, slope*y + zOffsetFromX);
	}

	@Override
	public int getMinWAtZ(int z) {
	    int crossSectionX = crossSectionMinX;
	    int crossSectionZ = slope*crossSectionX + zOffsetFromX;
	    int result = source.getMinVAtXYZ(crossSectionX, z, crossSectionZ);
	    int localMinV;
	    for (crossSectionX++, crossSectionZ += slope;
	            crossSectionX <= crossSectionMaxX && (localMinV = source.getMinVAtXYZ(crossSectionX, z, crossSectionZ)) <= result;
	            crossSectionX++, crossSectionZ += slope) {
	        result = localMinV;
	    }
	    return result;
	}

	@Override
	public int getMaxWAtZ(int z) {
	    int crossSectionX = crossSectionMinX;
	    int crossSectionZ = slope*crossSectionX + zOffsetFromX;
	    int result = source.getMaxVAtXYZ(crossSectionX, z, crossSectionZ);
	    int localMaxV;
	    for (crossSectionX++, crossSectionZ += slope;
	            crossSectionX <= crossSectionMaxX && (localMaxV = source.getMaxVAtXYZ(crossSectionX, z, crossSectionZ)) >= result;
	            crossSectionX++, crossSectionZ += slope) {
	        result = localMaxV;
	    }
	    return result;
	}

	@Override
	public int getMinWAtXY(int x, int y) {
	    return source.getMinVAtWXZ(x, y, slope*y + zOffsetFromX);
	}

	@Override
	public int getMaxWAtXY(int x, int y) {
	    return source.getMaxVAtWXZ(x, y, slope*y + zOffsetFromX);
	}

	@Override
	public int getMinWAtXZ(int x, int z) {
	    int crossSectionX = crossSectionMinX;
	    int crossSectionZ = slope*crossSectionX + zOffsetFromX;
	    int result = source.getMinV(x, crossSectionX, z, crossSectionZ);
	    int localMinV;
	    for (crossSectionX++, crossSectionZ += slope;
	            crossSectionX <= crossSectionMaxX && (localMinV = source.getMinV(x, crossSectionX, z, crossSectionZ)) <= result;
	            crossSectionX++, crossSectionZ += slope) {
	        result = localMinV;
	    }
	    return result;
	}

	@Override
	public int getMaxWAtXZ(int x, int z) {
	    int crossSectionX = crossSectionMinX;
	    int crossSectionZ = slope*crossSectionX + zOffsetFromX;
	    int result = source.getMaxV(x, crossSectionX, z, crossSectionZ);
	    int localMaxV;
	    for (crossSectionX++, crossSectionZ += slope;
	            crossSectionX <= crossSectionMaxX && (localMaxV = source.getMaxV(x, crossSectionX, z, crossSectionZ)) >= result;
	            crossSectionX++, crossSectionZ += slope) {
	        result = localMaxV;
	    }
	    return result;
	}

	@Override
	public int getMinWAtYZ(int y, int z) {
	    return source.getMinVAtXYZ(y, z, slope*y + zOffsetFromX);
	}

	@Override
	public int getMaxWAtYZ(int y, int z) {
	    return source.getMaxVAtXYZ(y, z, slope*y + zOffsetFromX);
	}

	@Override
	public int getMinW(int x, int y, int z) {
	    return source.getMinV(x, y, z, slope*y + zOffsetFromX);
	}

	@Override
	public int getMaxW(int x, int y, int z) {
	    return source.getMaxV(x, y, z, slope*y + zOffsetFromX);
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
	    int crossSectionX = crossSectionMinX;
	    int crossSectionZ = slope*crossSectionX + zOffsetFromX;
	    int result = source.getMinWAtVXZ(w, crossSectionX, crossSectionZ);
	    int localMinW;
	    for (crossSectionX++, crossSectionZ += slope;
	            crossSectionX <= crossSectionMaxX && (localMinW = source.getMinWAtVXZ(w, crossSectionX, crossSectionZ)) <= result;
	            crossSectionX++, crossSectionZ += slope) {
	        result = localMinW;
	    }
	    return result;
	}

	@Override
	public int getMaxXAtW(int w) {
	    int crossSectionX = crossSectionMinX;
	    int crossSectionZ = slope*crossSectionX + zOffsetFromX;
	    int result = source.getMaxWAtVXZ(w, crossSectionX, crossSectionZ);
	    int localMaxW;
	    for (crossSectionX++, crossSectionZ += slope;
	            crossSectionX <= crossSectionMaxX && (localMaxW = source.getMaxWAtVXZ(w, crossSectionX, crossSectionZ)) >= result;
	            crossSectionX++, crossSectionZ += slope) {
	        result = localMaxW;
	    }
	    return result;
	}

	@Override
	public int getMinXAtY(int y) {
	    return source.getMinWAtXZ(y, slope*y + zOffsetFromX);
	}

	@Override
	public int getMaxXAtY(int y) {
	    return source.getMaxWAtXZ(y, slope*y + zOffsetFromX);
	}

	@Override
	public int getMinXAtZ(int z) {
	    int crossSectionX = crossSectionMinX;
	    int crossSectionZ = slope*crossSectionX + zOffsetFromX;
	    int result = source.getMinWAtXYZ(crossSectionX, z, crossSectionZ);
	    int localMinW;
	    for (crossSectionX++, crossSectionZ += slope;
	            crossSectionX <= crossSectionMaxX && (localMinW = source.getMinWAtXYZ(crossSectionX, z, crossSectionZ)) <= result;
	            crossSectionX++, crossSectionZ += slope) {
	        result = localMinW;
	    }
	    return result;
	}

	@Override
	public int getMaxXAtZ(int z) {
	    int crossSectionX = crossSectionMinX;
	    int crossSectionZ = slope*crossSectionX + zOffsetFromX;
	    int result = source.getMaxWAtXYZ(crossSectionX, z, crossSectionZ);
	    int localMaxW;
	    for (crossSectionX++, crossSectionZ += slope;
	            crossSectionX <= crossSectionMaxX && (localMaxW = source.getMaxWAtXYZ(crossSectionX, z, crossSectionZ)) >= result;
	            crossSectionX++, crossSectionZ += slope) {
	        result = localMaxW;
	    }
	    return result;
	}

	@Override
	public int getMinXAtWY(int w, int y) {
	    return source.getMinWAtVXZ(w, y, slope*y + zOffsetFromX);
	}

	@Override
	public int getMaxXAtWY(int w, int y) {
	    return source.getMaxWAtVXZ(w, y, slope*y + zOffsetFromX);
	}

	@Override
	public int getMinXAtWZ(int w, int z) {
	    int crossSectionX = crossSectionMinX;
	    int crossSectionZ = slope*crossSectionX + zOffsetFromX;
	    int result = source.getMinW(w, crossSectionX, z, crossSectionZ);
	    int localMinW;
	    for (crossSectionX++, crossSectionZ += slope;
	            crossSectionX <= crossSectionMaxX && (localMinW = source.getMinW(w, crossSectionX, z, crossSectionZ)) <= result;
	            crossSectionX++, crossSectionZ += slope) {
	        result = localMinW;
	    }
	    return result;
	}

	@Override
	public int getMaxXAtWZ(int w, int z) {
	    int crossSectionX = crossSectionMinX;
	    int crossSectionZ = slope*crossSectionX + zOffsetFromX;
	    int result = source.getMaxW(w, crossSectionX, z, crossSectionZ);
	    int localMaxW;
	    for (crossSectionX++, crossSectionZ += slope;
	            crossSectionX <= crossSectionMaxX && (localMaxW = source.getMaxW(w, crossSectionX, z, crossSectionZ)) >= result;
	            crossSectionX++, crossSectionZ += slope) {
	        result = localMaxW;
	    }
	    return result;
	}

	@Override
	public int getMinXAtYZ(int y, int z) {
	    return source.getMinWAtXYZ(y, z, slope*y + zOffsetFromX);
	}

	@Override
	public int getMaxXAtYZ(int y, int z) {
	    return source.getMaxWAtXYZ(y, z, slope*y + zOffsetFromX);
	}

	@Override
	public int getMinX(int w, int y, int z) {
	    return source.getMinW(w, y, z, slope*y + zOffsetFromX);
	}

	@Override
	public int getMaxX(int w, int y, int z) {
	    return source.getMaxW(w, y, z, slope*y + zOffsetFromX);
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
	    for (int crossSectionX = crossSectionMinX, crossSectionZ = slope*crossSectionX + zOffsetFromX; crossSectionX <= crossSectionMaxX; crossSectionX++, crossSectionZ += slope) {
	        if (w >= source.getMinVAtXZ(crossSectionX, crossSectionZ)
	                && w <= source.getMaxVAtXZ(crossSectionX, crossSectionZ)) {
	            return crossSectionX;
	        }
	    }
	    throw new IllegalArgumentException("The coordinate is out of bounds.");
	}

	@Override
	public int getMaxYAtW(int w) {
	    for (int crossSectionX = crossSectionMaxX, crossSectionZ = slope*crossSectionX + zOffsetFromX; crossSectionX >= crossSectionMinX; crossSectionX--, crossSectionZ -= slope) {
	        if (w >= source.getMinVAtXZ(crossSectionX, crossSectionZ)
	                && w <= source.getMaxVAtXZ(crossSectionX, crossSectionZ)) {
	            return crossSectionX;
	        }
	    }
	    throw new IllegalArgumentException("The coordinate is out of bounds.");
	}

	@Override
	public int getMinYAtX(int x) {
	    for (int crossSectionX = crossSectionMinX, crossSectionZ = slope*crossSectionX + zOffsetFromX; crossSectionX <= crossSectionMaxX; crossSectionX++, crossSectionZ += slope) {
	        if (x >= source.getMinWAtXZ(crossSectionX, crossSectionZ)
	                && x <= source.getMaxWAtXZ(crossSectionX, crossSectionZ)) {
	            return crossSectionX;
	        }
	    }
	    throw new IllegalArgumentException("The coordinate is out of bounds.");
	}

	@Override
	public int getMaxYAtX(int x) {
	    for (int crossSectionX = crossSectionMaxX, crossSectionZ = slope*crossSectionX + zOffsetFromX; crossSectionX >= crossSectionMinX; crossSectionX--, crossSectionZ -= slope) {
	        if (x >= source.getMinWAtXZ(crossSectionX, crossSectionZ)
	                && x <= source.getMaxWAtXZ(crossSectionX, crossSectionZ)) {
	            return crossSectionX;
	        }
	    }
	    throw new IllegalArgumentException("The coordinate is out of bounds.");
	}

	@Override
	public int getMinYAtZ(int z) {
	    for (int crossSectionX = crossSectionMinX, crossSectionZ = slope*crossSectionX + zOffsetFromX; crossSectionX <= crossSectionMaxX; crossSectionX++, crossSectionZ += slope) {
	        if (z >= source.getMinYAtXZ(crossSectionX, crossSectionZ)
	                && z <= source.getMaxYAtXZ(crossSectionX, crossSectionZ)) {
	            return crossSectionX;
	        }
	    }
	    throw new IllegalArgumentException("The coordinate is out of bounds.");
	}

	@Override
	public int getMaxYAtZ(int z) {
	    for (int crossSectionX = crossSectionMaxX, crossSectionZ = slope*crossSectionX + zOffsetFromX; crossSectionX >= crossSectionMinX; crossSectionX--, crossSectionZ -= slope) {
	        if (z >= source.getMinYAtXZ(crossSectionX, crossSectionZ)
	                && z <= source.getMaxYAtXZ(crossSectionX, crossSectionZ)) {
	            return crossSectionX;
	        }
	    }
	    throw new IllegalArgumentException("The coordinate is out of bounds.");
	}

	@Override
	public int getMinYAtWX(int w, int x) {
	    for (int crossSectionX = crossSectionMinX, crossSectionZ = slope*crossSectionX + zOffsetFromX; crossSectionX <= crossSectionMaxX; crossSectionX++, crossSectionZ += slope) {
	        if (w >= source.getMinVAtXZ(crossSectionX, crossSectionZ)
	                && w <= source.getMaxVAtXZ(crossSectionX, crossSectionZ)
	                && x >= source.getMinWAtVXZ(w, crossSectionX, crossSectionZ)
	                && x <= source.getMaxWAtVXZ(w, crossSectionX, crossSectionZ)) {
	            return crossSectionX;
	        }
	    }
	    throw new IllegalArgumentException("The coordinates are out of bounds.");
	}

	@Override
	public int getMaxYAtWX(int w, int x) {
	    for (int crossSectionX = crossSectionMaxX, crossSectionZ = slope*crossSectionX + zOffsetFromX; crossSectionX >= crossSectionMinX; crossSectionX--, crossSectionZ -= slope) {
	        if (w >= source.getMinVAtXZ(crossSectionX, crossSectionZ)
	                && w <= source.getMaxVAtXZ(crossSectionX, crossSectionZ)
	                && x >= source.getMinWAtVXZ(w, crossSectionX, crossSectionZ)
	                && x <= source.getMaxWAtVXZ(w, crossSectionX, crossSectionZ)) {
	            return crossSectionX;
	        }
	    }
	    throw new IllegalArgumentException("The coordinates are out of bounds.");
	}

	@Override
	public int getMinYAtWZ(int w, int z) {
	    for (int crossSectionX = crossSectionMinX, crossSectionZ = slope*crossSectionX + zOffsetFromX; crossSectionX <= crossSectionMaxX; crossSectionX++, crossSectionZ += slope) {
	        if (w >= source.getMinVAtXZ(crossSectionX, crossSectionZ)
	                && w <= source.getMaxVAtXZ(crossSectionX, crossSectionZ)
	                && z >= source.getMinYAtVXZ(w, crossSectionX, crossSectionZ)
	                && z <= source.getMaxYAtVXZ(w, crossSectionX, crossSectionZ)) {
	            return crossSectionX;
	        }
	    }
	    throw new IllegalArgumentException("The coordinates are out of bounds.");
	}

	@Override
	public int getMaxYAtWZ(int w, int z) {
	    for (int crossSectionX = crossSectionMaxX, crossSectionZ = slope*crossSectionX + zOffsetFromX; crossSectionX >= crossSectionMinX; crossSectionX--, crossSectionZ -= slope) {
	        if (w >= source.getMinVAtXZ(crossSectionX, crossSectionZ)
	                && w <= source.getMaxVAtXZ(crossSectionX, crossSectionZ)
	                && z >= source.getMinYAtVXZ(w, crossSectionX, crossSectionZ)
	                && z <= source.getMaxYAtVXZ(w, crossSectionX, crossSectionZ)) {
	            return crossSectionX;
	        }
	    }
	    throw new IllegalArgumentException("The coordinates are out of bounds.");
	}

	@Override
	public int getMinYAtXZ(int x, int z) {
	    for (int crossSectionX = crossSectionMinX, crossSectionZ = slope*crossSectionX + zOffsetFromX; crossSectionX <= crossSectionMaxX; crossSectionX++, crossSectionZ += slope) {
	        if (x >= source.getMinWAtXZ(crossSectionX, crossSectionZ)
	                && x <= source.getMaxWAtXZ(crossSectionX, crossSectionZ)
	                && z >= source.getMinYAtWXZ(x, crossSectionX, crossSectionZ)
	                && z <= source.getMaxYAtWXZ(x, crossSectionX, crossSectionZ)) {
	            return crossSectionX;
	        }
	    }
	    throw new IllegalArgumentException("The coordinates are out of bounds.");
	}

	@Override
	public int getMaxYAtXZ(int x, int z) {
	    for (int crossSectionX = crossSectionMaxX, crossSectionZ = slope*crossSectionX + zOffsetFromX; crossSectionX >= crossSectionMinX; crossSectionX--, crossSectionZ -= slope) {
	        if (x >= source.getMinWAtXZ(crossSectionX, crossSectionZ)
	                && x <= source.getMaxWAtXZ(crossSectionX, crossSectionZ)
	                && z >= source.getMinYAtWXZ(x, crossSectionX, crossSectionZ)
	                && z <= source.getMaxYAtWXZ(x, crossSectionX, crossSectionZ)) {
	            return crossSectionX;
	        }
	    }
	    throw new IllegalArgumentException("The coordinates are out of bounds.");
	}

	@Override
	public int getMinY(int w, int x, int z) {
	    for (int crossSectionX = crossSectionMinX, crossSectionZ = slope*crossSectionX + zOffsetFromX; crossSectionX <= crossSectionMaxX; crossSectionX++, crossSectionZ += slope) {
	        if (w >= source.getMinVAtXZ(crossSectionX, crossSectionZ)
	                && w <= source.getMaxVAtXZ(crossSectionX, crossSectionZ)
	                && x >= source.getMinWAtVXZ(w, crossSectionX, crossSectionZ)
	                && x <= source.getMaxWAtVXZ(w, crossSectionX, crossSectionZ)
	                && z >= source.getMinY(w, x, crossSectionX, crossSectionZ)
	                && z <= source.getMaxY(w, x, crossSectionX, crossSectionZ)) {
	            return crossSectionX;
	        }
	    }
	    throw new IllegalArgumentException("The coordinates are out of bounds.");
	}

	@Override
	public int getMaxY(int w, int x, int z) {
	    for (int crossSectionX = crossSectionMaxX, crossSectionZ = slope*crossSectionX + zOffsetFromX; crossSectionX >= crossSectionMinX; crossSectionX--, crossSectionZ -= slope) {
	        if (w >= source.getMinVAtXZ(crossSectionX, crossSectionZ)
	                && w <= source.getMaxVAtXZ(crossSectionX, crossSectionZ)
	                && x >= source.getMinWAtVXZ(w, crossSectionX, crossSectionZ)
	                && x <= source.getMaxWAtVXZ(w, crossSectionX, crossSectionZ)
	                && z >= source.getMinY(w, x, crossSectionX, crossSectionZ)
	                && z <= source.getMaxY(w, x, crossSectionX, crossSectionZ)) {
	            return crossSectionX;
	        }
	    }
	    throw new IllegalArgumentException("The coordinates are out of bounds.");
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
	    int crossSectionX = crossSectionMinX;
	    int crossSectionZ = slope*crossSectionX + zOffsetFromX;
	    int result = source.getMinYAtVXZ(w, crossSectionX, crossSectionZ);
	    int localMinY;
	    for (crossSectionX++, crossSectionZ += slope;
	            crossSectionX <= crossSectionMaxX && (localMinY = source.getMinYAtVXZ(w, crossSectionX, crossSectionZ)) <= result;
	            crossSectionX++, crossSectionZ += slope) {
	        result = localMinY;
	    }
	    return result;
	}

	@Override
	public int getMaxZAtW(int w) {
	    int crossSectionX = crossSectionMinX;
	    int crossSectionZ = slope*crossSectionX + zOffsetFromX;
	    int result = source.getMaxYAtVXZ(w, crossSectionX, crossSectionZ);
	    int localMaxY;
	    for (crossSectionX++, crossSectionZ += slope;
	            crossSectionX <= crossSectionMaxX && (localMaxY = source.getMaxYAtVXZ(w, crossSectionX, crossSectionZ)) >= result;
	            crossSectionX++, crossSectionZ += slope) {
	        result = localMaxY;
	    }
	    return result;
	}

	@Override
	public int getMinZAtX(int x) {
	    int crossSectionX = crossSectionMinX;
	    int crossSectionZ = slope*crossSectionX + zOffsetFromX;
	    int result = source.getMinYAtWXZ(x, crossSectionX, crossSectionZ);
	    int localMinY;
	    for (crossSectionX++, crossSectionZ += slope;
	            crossSectionX <= crossSectionMaxX && (localMinY = source.getMinYAtWXZ(x, crossSectionX, crossSectionZ)) <= result;
	            crossSectionX++, crossSectionZ += slope) {
	        result = localMinY;
	    }
	    return result;
	}

	@Override
	public int getMaxZAtX(int x) {
	    int crossSectionX = crossSectionMinX;
	    int crossSectionZ = slope*crossSectionX + zOffsetFromX;
	    int result = source.getMaxYAtWXZ(x, crossSectionX, crossSectionZ);
	    int localMaxY;
	    for (crossSectionX++, crossSectionZ += slope;
	            crossSectionX <= crossSectionMaxX && (localMaxY = source.getMaxYAtWXZ(x, crossSectionX, crossSectionZ)) >= result;
	            crossSectionX++, crossSectionZ += slope) {
	        result = localMaxY;
	    }
	    return result;
	}

	@Override
	public int getMinZAtY(int y) {
	    return source.getMinYAtXZ(y, slope*y + zOffsetFromX);
	}

	@Override
	public int getMaxZAtY(int y) {
	    return source.getMaxYAtXZ(y, slope*y + zOffsetFromX);
	}

	@Override
	public int getMinZAtWX(int w, int x) {
	    int crossSectionX = crossSectionMinX;
	    int crossSectionZ = slope*crossSectionX + zOffsetFromX;
	    int result = source.getMinY(w, x, crossSectionX, crossSectionZ);
	    int localMinY;
	    for (crossSectionX++, crossSectionZ += slope;
	            crossSectionX <= crossSectionMaxX && (localMinY = source.getMinY(w, x, crossSectionX, crossSectionZ)) <= result;
	            crossSectionX++, crossSectionZ += slope) {
	        result = localMinY;
	    }
	    return result;
	}

	@Override
	public int getMaxZAtWX(int w, int x) {
	    int crossSectionX = crossSectionMinX;
	    int crossSectionZ = slope*crossSectionX + zOffsetFromX;
	    int result = source.getMaxY(w, x, crossSectionX, crossSectionZ);
	    int localMaxY;
	    for (crossSectionX++, crossSectionZ += slope;
	            crossSectionX <= crossSectionMaxX && (localMaxY = source.getMaxY(w, x, crossSectionX, crossSectionZ)) >= result;
	            crossSectionX++, crossSectionZ += slope) {
	        result = localMaxY;
	    }
	    return result;
	}

	@Override
	public int getMinZAtWY(int w, int y) {
	    return source.getMinYAtVXZ(w, y, slope*y + zOffsetFromX);
	}

	@Override
	public int getMaxZAtWY(int w, int y) {
	    return source.getMaxYAtVXZ(w, y, slope*y + zOffsetFromX);
	}

	@Override
	public int getMinZAtXY(int x, int y) {
	    return source.getMinYAtWXZ(x, y, slope*y + zOffsetFromX);
	}

	@Override
	public int getMaxZAtXY(int x, int y) {
	    return source.getMaxYAtWXZ(x, y, slope*y + zOffsetFromX);
	}

	@Override
	public int getMinZ(int w, int x, int y) {
	    return source.getMinY(w, x, y, slope*y + zOffsetFromX);
	}

	@Override
	public int getMaxZ(int w, int x, int y) {
	    return source.getMaxY(w, x, y, slope*y + zOffsetFromX);
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
	    path.append(source.getXLabel());
	    if (zOffsetFromX < 0) {
	        path.append(zOffsetFromX);
	    } else if (zOffsetFromX > 0) {
	        path.append("+").append(zOffsetFromX);
	    }
	    return path.toString();
	}

	@Override
	public void backUp(String backupPath, String backupName) throws Exception {
	    source.backUp(backupPath, backupName);
	}

}
