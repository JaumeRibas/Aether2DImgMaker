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

public class Model5DXYDiagonalCrossSection<Source_Type extends Model5D> implements Model4D {

	protected Source_Type source;
	protected int slope;
	protected int yOffsetFromX;
	protected int crossSectionMinV;
	protected int crossSectionMaxV;
	protected int crossSectionMinW;
	protected int crossSectionMaxW;
	protected int crossSectionMinX;
	protected int crossSectionMaxX;
	protected int crossSectionMinZ;
	protected int crossSectionMaxZ;

	public Model5DXYDiagonalCrossSection(Source_Type source, boolean positiveSlope, int yOffsetFromX) {
	    this.source = source;
	    this.slope = positiveSlope ? 1 : -1;
	    this.yOffsetFromX = yOffsetFromX;
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
	    int x = source.getMinX();
	    int maxX = source.getMaxX();
	    int crossSectionY = slope*x + yOffsetFromX;
	    while (x <= maxX && (crossSectionY < source.getMinYAtX(x) || crossSectionY > source.getMaxYAtX(x))) {
	        x++;
	        crossSectionY += slope;
	    }
	    if (x <= maxX) {
	        crossSectionMinX = x;
	        crossSectionMaxX = x;
	        crossSectionMinV = source.getMinVAtXY(x, crossSectionY);
	        crossSectionMaxV = source.getMaxVAtXY(x, crossSectionY);
	        crossSectionMinW = source.getMinWAtXY(x, crossSectionY);
	        crossSectionMaxW = source.getMaxWAtXY(x, crossSectionY);
	        crossSectionMinZ = source.getMinZAtXY(x, crossSectionY);
	        crossSectionMaxZ = source.getMaxZAtXY(x, crossSectionY);
	        x++;
	        crossSectionY += slope;
	        while (x <= maxX && crossSectionY >= source.getMinYAtX(x) && crossSectionY <= source.getMaxYAtX(x)) {
	            crossSectionMaxX = x;
	            int localMinV = source.getMinVAtXY(x, crossSectionY);
	            if (localMinV < crossSectionMinV) {
	                crossSectionMinV = localMinV;
	            }
	            int localMaxV = source.getMaxVAtXY(x, crossSectionY);
	            if (localMaxV > crossSectionMaxV) {
	                crossSectionMaxV = localMaxV;
	            }
	            int localMinW = source.getMinWAtXY(x, crossSectionY);
	            if (localMinW < crossSectionMinW) {
	                crossSectionMinW = localMinW;
	            }
	            int localMaxW = source.getMaxWAtXY(x, crossSectionY);
	            if (localMaxW > crossSectionMaxW) {
	                crossSectionMaxW = localMaxW;
	            }
	            int localMinZ = source.getMinZAtXY(x, crossSectionY);
	            if (localMinZ < crossSectionMinZ) {
	                crossSectionMinZ = localMinZ;
	            }
	            int localMaxZ = source.getMaxZAtXY(x, crossSectionY);
	            if (localMaxZ > crossSectionMaxZ) {
	                crossSectionMaxZ = localMaxZ;
	            }
	            x++;
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
	    int crossSectionX = crossSectionMinX;
	    int crossSectionY = slope*crossSectionX + yOffsetFromX;
	    int result = source.getMinVAtWXY(x, crossSectionX, crossSectionY);
	    int localMinV;
	    for (crossSectionX++, crossSectionY += slope;
	            crossSectionX <= crossSectionMaxX && (localMinV = source.getMinVAtWXY(x, crossSectionX, crossSectionY)) <= result;
	            crossSectionX++, crossSectionY += slope) {
	        result = localMinV;
	    }
	    return result;
	}

	@Override
	public int getMaxWAtX(int x) {
	    int crossSectionX = crossSectionMinX;
	    int crossSectionY = slope*crossSectionX + yOffsetFromX;
	    int result = source.getMaxVAtWXY(x, crossSectionX, crossSectionY);
	    int localMaxV;
	    for (crossSectionX++, crossSectionY += slope;
	            crossSectionX <= crossSectionMaxX && (localMaxV = source.getMaxVAtWXY(x, crossSectionX, crossSectionY)) >= result;
	            crossSectionX++, crossSectionY += slope) {
	        result = localMaxV;
	    }
	    return result;
	}

	@Override
	public int getMinWAtY(int y) {
	    return source.getMinVAtXY(y, slope*y + yOffsetFromX);
	}

	@Override
	public int getMaxWAtY(int y) {
	    return source.getMaxVAtXY(y, slope*y + yOffsetFromX);
	}

	@Override
	public int getMinWAtZ(int z) {
	    int crossSectionX = crossSectionMinX;
	    int crossSectionY = slope*crossSectionX + yOffsetFromX;
	    int result = source.getMinVAtXYZ(crossSectionX, crossSectionY, z);
	    int localMinV;
	    for (crossSectionX++, crossSectionY += slope;
	            crossSectionX <= crossSectionMaxX && (localMinV = source.getMinVAtXYZ(crossSectionX, crossSectionY, z)) <= result;
	            crossSectionX++, crossSectionY += slope) {
	        result = localMinV;
	    }
	    return result;
	}

	@Override
	public int getMaxWAtZ(int z) {
	    int crossSectionX = crossSectionMinX;
	    int crossSectionY = slope*crossSectionX + yOffsetFromX;
	    int result = source.getMaxVAtXYZ(crossSectionX, crossSectionY, z);
	    int localMaxV;
	    for (crossSectionX++, crossSectionY += slope;
	            crossSectionX <= crossSectionMaxX && (localMaxV = source.getMaxVAtXYZ(crossSectionX, crossSectionY, z)) >= result;
	            crossSectionX++, crossSectionY += slope) {
	        result = localMaxV;
	    }
	    return result;
	}

	@Override
	public int getMinWAtXY(int x, int y) {
	    return source.getMinVAtWXY(x, y, slope*y + yOffsetFromX);
	}

	@Override
	public int getMaxWAtXY(int x, int y) {
	    return source.getMaxVAtWXY(x, y, slope*y + yOffsetFromX);
	}

	@Override
	public int getMinWAtXZ(int x, int z) {
	    int crossSectionX = crossSectionMinX;
	    int crossSectionY = slope*crossSectionX + yOffsetFromX;
	    int result = source.getMinV(x, crossSectionX, crossSectionY, z);
	    int localMinV;
	    for (crossSectionX++, crossSectionY += slope;
	            crossSectionX <= crossSectionMaxX && (localMinV = source.getMinV(x, crossSectionX, crossSectionY, z)) <= result;
	            crossSectionX++, crossSectionY += slope) {
	        result = localMinV;
	    }
	    return result;
	}

	@Override
	public int getMaxWAtXZ(int x, int z) {
	    int crossSectionX = crossSectionMinX;
	    int crossSectionY = slope*crossSectionX + yOffsetFromX;
	    int result = source.getMaxV(x, crossSectionX, crossSectionY, z);
	    int localMaxV;
	    for (crossSectionX++, crossSectionY += slope;
	            crossSectionX <= crossSectionMaxX && (localMaxV = source.getMaxV(x, crossSectionX, crossSectionY, z)) >= result;
	            crossSectionX++, crossSectionY += slope) {
	        result = localMaxV;
	    }
	    return result;
	}

	@Override
	public int getMinWAtYZ(int y, int z) {
	    return source.getMinVAtXYZ(y, slope*y + yOffsetFromX, z);
	}

	@Override
	public int getMaxWAtYZ(int y, int z) {
	    return source.getMaxVAtXYZ(y, slope*y + yOffsetFromX, z);
	}

	@Override
	public int getMinW(int x, int y, int z) {
	    return source.getMinV(x, y, slope*y + yOffsetFromX, z);
	}

	@Override
	public int getMaxW(int x, int y, int z) {
	    return source.getMaxV(x, y, slope*y + yOffsetFromX, z);
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
	    int crossSectionY = slope*crossSectionX + yOffsetFromX;
	    int result = source.getMinWAtVXY(w, crossSectionX, crossSectionY);
	    int localMinW;
	    for (crossSectionX++, crossSectionY += slope;
	            crossSectionX <= crossSectionMaxX && (localMinW = source.getMinWAtVXY(w, crossSectionX, crossSectionY)) <= result;
	            crossSectionX++, crossSectionY += slope) {
	        result = localMinW;
	    }
	    return result;
	}

	@Override
	public int getMaxXAtW(int w) {
	    int crossSectionX = crossSectionMinX;
	    int crossSectionY = slope*crossSectionX + yOffsetFromX;
	    int result = source.getMaxWAtVXY(w, crossSectionX, crossSectionY);
	    int localMaxW;
	    for (crossSectionX++, crossSectionY += slope;
	            crossSectionX <= crossSectionMaxX && (localMaxW = source.getMaxWAtVXY(w, crossSectionX, crossSectionY)) >= result;
	            crossSectionX++, crossSectionY += slope) {
	        result = localMaxW;
	    }
	    return result;
	}

	@Override
	public int getMinXAtY(int y) {
	    return source.getMinWAtXY(y, slope*y + yOffsetFromX);
	}

	@Override
	public int getMaxXAtY(int y) {
	    return source.getMaxWAtXY(y, slope*y + yOffsetFromX);
	}

	@Override
	public int getMinXAtZ(int z) {
	    int crossSectionX = crossSectionMinX;
	    int crossSectionY = slope*crossSectionX + yOffsetFromX;
	    int result = source.getMinWAtXYZ(crossSectionX, crossSectionY, z);
	    int localMinW;
	    for (crossSectionX++, crossSectionY += slope;
	            crossSectionX <= crossSectionMaxX && (localMinW = source.getMinWAtXYZ(crossSectionX, crossSectionY, z)) <= result;
	            crossSectionX++, crossSectionY += slope) {
	        result = localMinW;
	    }
	    return result;
	}

	@Override
	public int getMaxXAtZ(int z) {
	    int crossSectionX = crossSectionMinX;
	    int crossSectionY = slope*crossSectionX + yOffsetFromX;
	    int result = source.getMaxWAtXYZ(crossSectionX, crossSectionY, z);
	    int localMaxW;
	    for (crossSectionX++, crossSectionY += slope;
	            crossSectionX <= crossSectionMaxX && (localMaxW = source.getMaxWAtXYZ(crossSectionX, crossSectionY, z)) >= result;
	            crossSectionX++, crossSectionY += slope) {
	        result = localMaxW;
	    }
	    return result;
	}

	@Override
	public int getMinXAtWY(int w, int y) {
	    return source.getMinWAtVXY(w, y, slope*y + yOffsetFromX);
	}

	@Override
	public int getMaxXAtWY(int w, int y) {
	    return source.getMaxWAtVXY(w, y, slope*y + yOffsetFromX);
	}

	@Override
	public int getMinXAtWZ(int w, int z) {
	    int crossSectionX = crossSectionMinX;
	    int crossSectionY = slope*crossSectionX + yOffsetFromX;
	    int result = source.getMinW(w, crossSectionX, crossSectionY, z);
	    int localMinW;
	    for (crossSectionX++, crossSectionY += slope;
	            crossSectionX <= crossSectionMaxX && (localMinW = source.getMinW(w, crossSectionX, crossSectionY, z)) <= result;
	            crossSectionX++, crossSectionY += slope) {
	        result = localMinW;
	    }
	    return result;
	}

	@Override
	public int getMaxXAtWZ(int w, int z) {
	    int crossSectionX = crossSectionMinX;
	    int crossSectionY = slope*crossSectionX + yOffsetFromX;
	    int result = source.getMaxW(w, crossSectionX, crossSectionY, z);
	    int localMaxW;
	    for (crossSectionX++, crossSectionY += slope;
	            crossSectionX <= crossSectionMaxX && (localMaxW = source.getMaxW(w, crossSectionX, crossSectionY, z)) >= result;
	            crossSectionX++, crossSectionY += slope) {
	        result = localMaxW;
	    }
	    return result;
	}

	@Override
	public int getMinXAtYZ(int y, int z) {
	    return source.getMinWAtXYZ(y, slope*y + yOffsetFromX, z);
	}

	@Override
	public int getMaxXAtYZ(int y, int z) {
	    return source.getMaxWAtXYZ(y, slope*y + yOffsetFromX, z);
	}

	@Override
	public int getMinX(int w, int y, int z) {
	    return source.getMinW(w, y, slope*y + yOffsetFromX, z);
	}

	@Override
	public int getMaxX(int w, int y, int z) {
	    return source.getMaxW(w, y, slope*y + yOffsetFromX, z);
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
	    for (int crossSectionX = crossSectionMinX, crossSectionY = slope*crossSectionX + yOffsetFromX; crossSectionX <= crossSectionMaxX; crossSectionX++, crossSectionY += slope) {
	        if (w >= source.getMinVAtXY(crossSectionX, crossSectionY)
	                && w <= source.getMaxVAtXY(crossSectionX, crossSectionY)) {
	            return crossSectionX;
	        }
	    }
	    throw new IllegalArgumentException("The coordinate is out of bounds.");
	}

	@Override
	public int getMaxYAtW(int w) {
	    for (int crossSectionX = crossSectionMaxX, crossSectionY = slope*crossSectionX + yOffsetFromX; crossSectionX >= crossSectionMinX; crossSectionX--, crossSectionY -= slope) {
	        if (w >= source.getMinVAtXY(crossSectionX, crossSectionY)
	                && w <= source.getMaxVAtXY(crossSectionX, crossSectionY)) {
	            return crossSectionX;
	        }
	    }
	    throw new IllegalArgumentException("The coordinate is out of bounds.");
	}

	@Override
	public int getMinYAtX(int x) {
	    for (int crossSectionX = crossSectionMinX, crossSectionY = slope*crossSectionX + yOffsetFromX; crossSectionX <= crossSectionMaxX; crossSectionX++, crossSectionY += slope) {
	        if (x >= source.getMinWAtXY(crossSectionX, crossSectionY)
	                && x <= source.getMaxWAtXY(crossSectionX, crossSectionY)) {
	            return crossSectionX;
	        }
	    }
	    throw new IllegalArgumentException("The coordinate is out of bounds.");
	}

	@Override
	public int getMaxYAtX(int x) {
	    for (int crossSectionX = crossSectionMaxX, crossSectionY = slope*crossSectionX + yOffsetFromX; crossSectionX >= crossSectionMinX; crossSectionX--, crossSectionY -= slope) {
	        if (x >= source.getMinWAtXY(crossSectionX, crossSectionY)
	                && x <= source.getMaxWAtXY(crossSectionX, crossSectionY)) {
	            return crossSectionX;
	        }
	    }
	    throw new IllegalArgumentException("The coordinate is out of bounds.");
	}

	@Override
	public int getMinYAtZ(int z) {
	    for (int crossSectionX = crossSectionMinX, crossSectionY = slope*crossSectionX + yOffsetFromX; crossSectionX <= crossSectionMaxX; crossSectionX++, crossSectionY += slope) {
	        if (z >= source.getMinZAtXY(crossSectionX, crossSectionY)
	                && z <= source.getMaxZAtXY(crossSectionX, crossSectionY)) {
	            return crossSectionX;
	        }
	    }
	    throw new IllegalArgumentException("The coordinate is out of bounds.");
	}

	@Override
	public int getMaxYAtZ(int z) {
	    for (int crossSectionX = crossSectionMaxX, crossSectionY = slope*crossSectionX + yOffsetFromX; crossSectionX >= crossSectionMinX; crossSectionX--, crossSectionY -= slope) {
	        if (z >= source.getMinZAtXY(crossSectionX, crossSectionY)
	                && z <= source.getMaxZAtXY(crossSectionX, crossSectionY)) {
	            return crossSectionX;
	        }
	    }
	    throw new IllegalArgumentException("The coordinate is out of bounds.");
	}

	@Override
	public int getMinYAtWX(int w, int x) {
	    for (int crossSectionX = crossSectionMinX, crossSectionY = slope*crossSectionX + yOffsetFromX; crossSectionX <= crossSectionMaxX; crossSectionX++, crossSectionY += slope) {
	        if (w >= source.getMinVAtXY(crossSectionX, crossSectionY)
	                && w <= source.getMaxVAtXY(crossSectionX, crossSectionY)
	                && x >= source.getMinWAtVXY(w, crossSectionX, crossSectionY)
	                && x <= source.getMaxWAtVXY(w, crossSectionX, crossSectionY)) {
	            return crossSectionX;
	        }
	    }
	    throw new IllegalArgumentException("The coordinates are out of bounds.");
	}

	@Override
	public int getMaxYAtWX(int w, int x) {
	    for (int crossSectionX = crossSectionMaxX, crossSectionY = slope*crossSectionX + yOffsetFromX; crossSectionX >= crossSectionMinX; crossSectionX--, crossSectionY -= slope) {
	        if (w >= source.getMinVAtXY(crossSectionX, crossSectionY)
	                && w <= source.getMaxVAtXY(crossSectionX, crossSectionY)
	                && x >= source.getMinWAtVXY(w, crossSectionX, crossSectionY)
	                && x <= source.getMaxWAtVXY(w, crossSectionX, crossSectionY)) {
	            return crossSectionX;
	        }
	    }
	    throw new IllegalArgumentException("The coordinates are out of bounds.");
	}

	@Override
	public int getMinYAtWZ(int w, int z) {
	    for (int crossSectionX = crossSectionMinX, crossSectionY = slope*crossSectionX + yOffsetFromX; crossSectionX <= crossSectionMaxX; crossSectionX++, crossSectionY += slope) {
	        if (w >= source.getMinVAtXY(crossSectionX, crossSectionY)
	                && w <= source.getMaxVAtXY(crossSectionX, crossSectionY)
	                && z >= source.getMinZAtVXY(w, crossSectionX, crossSectionY)
	                && z <= source.getMaxZAtVXY(w, crossSectionX, crossSectionY)) {
	            return crossSectionX;
	        }
	    }
	    throw new IllegalArgumentException("The coordinates are out of bounds.");
	}

	@Override
	public int getMaxYAtWZ(int w, int z) {
	    for (int crossSectionX = crossSectionMaxX, crossSectionY = slope*crossSectionX + yOffsetFromX; crossSectionX >= crossSectionMinX; crossSectionX--, crossSectionY -= slope) {
	        if (w >= source.getMinVAtXY(crossSectionX, crossSectionY)
	                && w <= source.getMaxVAtXY(crossSectionX, crossSectionY)
	                && z >= source.getMinZAtVXY(w, crossSectionX, crossSectionY)
	                && z <= source.getMaxZAtVXY(w, crossSectionX, crossSectionY)) {
	            return crossSectionX;
	        }
	    }
	    throw new IllegalArgumentException("The coordinates are out of bounds.");
	}

	@Override
	public int getMinYAtXZ(int x, int z) {
	    for (int crossSectionX = crossSectionMinX, crossSectionY = slope*crossSectionX + yOffsetFromX; crossSectionX <= crossSectionMaxX; crossSectionX++, crossSectionY += slope) {
	        if (x >= source.getMinWAtXY(crossSectionX, crossSectionY)
	                && x <= source.getMaxWAtXY(crossSectionX, crossSectionY)
	                && z >= source.getMinZAtWXY(x, crossSectionX, crossSectionY)
	                && z <= source.getMaxZAtWXY(x, crossSectionX, crossSectionY)) {
	            return crossSectionX;
	        }
	    }
	    throw new IllegalArgumentException("The coordinates are out of bounds.");
	}

	@Override
	public int getMaxYAtXZ(int x, int z) {
	    for (int crossSectionX = crossSectionMaxX, crossSectionY = slope*crossSectionX + yOffsetFromX; crossSectionX >= crossSectionMinX; crossSectionX--, crossSectionY -= slope) {
	        if (x >= source.getMinWAtXY(crossSectionX, crossSectionY)
	                && x <= source.getMaxWAtXY(crossSectionX, crossSectionY)
	                && z >= source.getMinZAtWXY(x, crossSectionX, crossSectionY)
	                && z <= source.getMaxZAtWXY(x, crossSectionX, crossSectionY)) {
	            return crossSectionX;
	        }
	    }
	    throw new IllegalArgumentException("The coordinates are out of bounds.");
	}

	@Override
	public int getMinY(int w, int x, int z) {
	    for (int crossSectionX = crossSectionMinX, crossSectionY = slope*crossSectionX + yOffsetFromX; crossSectionX <= crossSectionMaxX; crossSectionX++, crossSectionY += slope) {
	        if (w >= source.getMinVAtXY(crossSectionX, crossSectionY)
	                && w <= source.getMaxVAtXY(crossSectionX, crossSectionY)
	                && x >= source.getMinWAtVXY(w, crossSectionX, crossSectionY)
	                && x <= source.getMaxWAtVXY(w, crossSectionX, crossSectionY)
	                && z >= source.getMinZ(w, x, crossSectionX, crossSectionY)
	                && z <= source.getMaxZ(w, x, crossSectionX, crossSectionY)) {
	            return crossSectionX;
	        }
	    }
	    throw new IllegalArgumentException("The coordinates are out of bounds.");
	}

	@Override
	public int getMaxY(int w, int x, int z) {
	    for (int crossSectionX = crossSectionMaxX, crossSectionY = slope*crossSectionX + yOffsetFromX; crossSectionX >= crossSectionMinX; crossSectionX--, crossSectionY -= slope) {
	        if (w >= source.getMinVAtXY(crossSectionX, crossSectionY)
	                && w <= source.getMaxVAtXY(crossSectionX, crossSectionY)
	                && x >= source.getMinWAtVXY(w, crossSectionX, crossSectionY)
	                && x <= source.getMaxWAtVXY(w, crossSectionX, crossSectionY)
	                && z >= source.getMinZ(w, x, crossSectionX, crossSectionY)
	                && z <= source.getMaxZ(w, x, crossSectionX, crossSectionY)) {
	            return crossSectionX;
	        }
	    }
	    throw new IllegalArgumentException("The coordinates are out of bounds.");
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
	    int crossSectionX = crossSectionMinX;
	    int crossSectionY = slope*crossSectionX + yOffsetFromX;
	    int result = source.getMinZAtVXY(w, crossSectionX, crossSectionY);
	    int localMinZ;
	    for (crossSectionX++, crossSectionY += slope;
	            crossSectionX <= crossSectionMaxX && (localMinZ = source.getMinZAtVXY(w, crossSectionX, crossSectionY)) <= result;
	            crossSectionX++, crossSectionY += slope) {
	        result = localMinZ;
	    }
	    return result;
	}

	@Override
	public int getMaxZAtW(int w) {
	    int crossSectionX = crossSectionMinX;
	    int crossSectionY = slope*crossSectionX + yOffsetFromX;
	    int result = source.getMaxZAtVXY(w, crossSectionX, crossSectionY);
	    int localMaxZ;
	    for (crossSectionX++, crossSectionY += slope;
	            crossSectionX <= crossSectionMaxX && (localMaxZ = source.getMaxZAtVXY(w, crossSectionX, crossSectionY)) >= result;
	            crossSectionX++, crossSectionY += slope) {
	        result = localMaxZ;
	    }
	    return result;
	}

	@Override
	public int getMinZAtX(int x) {
	    int crossSectionX = crossSectionMinX;
	    int crossSectionY = slope*crossSectionX + yOffsetFromX;
	    int result = source.getMinZAtWXY(x, crossSectionX, crossSectionY);
	    int localMinZ;
	    for (crossSectionX++, crossSectionY += slope;
	            crossSectionX <= crossSectionMaxX && (localMinZ = source.getMinZAtWXY(x, crossSectionX, crossSectionY)) <= result;
	            crossSectionX++, crossSectionY += slope) {
	        result = localMinZ;
	    }
	    return result;
	}

	@Override
	public int getMaxZAtX(int x) {
	    int crossSectionX = crossSectionMinX;
	    int crossSectionY = slope*crossSectionX + yOffsetFromX;
	    int result = source.getMaxZAtWXY(x, crossSectionX, crossSectionY);
	    int localMaxZ;
	    for (crossSectionX++, crossSectionY += slope;
	            crossSectionX <= crossSectionMaxX && (localMaxZ = source.getMaxZAtWXY(x, crossSectionX, crossSectionY)) >= result;
	            crossSectionX++, crossSectionY += slope) {
	        result = localMaxZ;
	    }
	    return result;
	}

	@Override
	public int getMinZAtY(int y) {
	    return source.getMinZAtXY(y, slope*y + yOffsetFromX);
	}

	@Override
	public int getMaxZAtY(int y) {
	    return source.getMaxZAtXY(y, slope*y + yOffsetFromX);
	}

	@Override
	public int getMinZAtWX(int w, int x) {
	    int crossSectionX = crossSectionMinX;
	    int crossSectionY = slope*crossSectionX + yOffsetFromX;
	    int result = source.getMinZ(w, x, crossSectionX, crossSectionY);
	    int localMinZ;
	    for (crossSectionX++, crossSectionY += slope;
	            crossSectionX <= crossSectionMaxX && (localMinZ = source.getMinZ(w, x, crossSectionX, crossSectionY)) <= result;
	            crossSectionX++, crossSectionY += slope) {
	        result = localMinZ;
	    }
	    return result;
	}

	@Override
	public int getMaxZAtWX(int w, int x) {
	    int crossSectionX = crossSectionMinX;
	    int crossSectionY = slope*crossSectionX + yOffsetFromX;
	    int result = source.getMaxZ(w, x, crossSectionX, crossSectionY);
	    int localMaxZ;
	    for (crossSectionX++, crossSectionY += slope;
	            crossSectionX <= crossSectionMaxX && (localMaxZ = source.getMaxZ(w, x, crossSectionX, crossSectionY)) >= result;
	            crossSectionX++, crossSectionY += slope) {
	        result = localMaxZ;
	    }
	    return result;
	}

	@Override
	public int getMinZAtWY(int w, int y) {
	    return source.getMinZAtVXY(w, y, slope*y + yOffsetFromX);
	}

	@Override
	public int getMaxZAtWY(int w, int y) {
	    return source.getMaxZAtVXY(w, y, slope*y + yOffsetFromX);
	}

	@Override
	public int getMinZAtXY(int x, int y) {
	    return source.getMinZAtWXY(x, y, slope*y + yOffsetFromX);
	}

	@Override
	public int getMaxZAtXY(int x, int y) {
	    return source.getMaxZAtWXY(x, y, slope*y + yOffsetFromX);
	}

	@Override
	public int getMinZ(int w, int x, int y) {
	    return source.getMinZ(w, x, y, slope*y + yOffsetFromX);
	}

	@Override
	public int getMaxZ(int w, int x, int y) {
	    return source.getMaxZ(w, x, y, slope*y + yOffsetFromX);
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
	    path.append(source.getXLabel());
	    if (yOffsetFromX < 0) {
	        path.append(yOffsetFromX);
	    } else if (yOffsetFromX > 0) {
	        path.append("+").append(yOffsetFromX);
	    }
	    return path.toString();
	}

	@Override
	public void backUp(String backupPath, String backupName) throws Exception {
	    source.backUp(backupPath, backupName);
	}

}
