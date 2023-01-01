/* Aether2DImgMaker -- console app to generate images of the Aether cellular automaton in 2D
    Copyright (C) 2017-2023 Jaume Ribas

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

public class Model5DYZDiagonalCrossSection<Source_Type extends Model5D> implements Model4D {

	protected Source_Type source;
	protected int slope;
	protected int zOffsetFromY;
	protected int crossSectionMinV;
	protected int crossSectionMaxV;
	protected int crossSectionMinW;
	protected int crossSectionMaxW;
	protected int crossSectionMinX;
	protected int crossSectionMaxX;
	protected int crossSectionMinY;
	protected int crossSectionMaxY;

	public Model5DYZDiagonalCrossSection(Source_Type source, boolean positiveSlope, int zOffsetFromY) {
	    this.source = source;
	    this.slope = positiveSlope ? 1 : -1;
	    this.zOffsetFromY = zOffsetFromY;
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
	    int y = source.getMinY();
	    int maxY = source.getMaxY();
	    int crossSectionZ = slope*y + zOffsetFromY;
	    while (y <= maxY && (crossSectionZ < source.getMinZAtY(y) || crossSectionZ > source.getMaxZAtY(y))) {
	        y++;
	        crossSectionZ += slope;
	    }
	    if (y <= maxY) {
	        crossSectionMinY = y;
	        crossSectionMaxY = y;
	        crossSectionMinV = source.getMinVAtYZ(y, crossSectionZ);
	        crossSectionMaxV = source.getMaxVAtYZ(y, crossSectionZ);
	        crossSectionMinW = source.getMinWAtYZ(y, crossSectionZ);
	        crossSectionMaxW = source.getMaxWAtYZ(y, crossSectionZ);
	        crossSectionMinX = source.getMinXAtYZ(y, crossSectionZ);
	        crossSectionMaxX = source.getMaxXAtYZ(y, crossSectionZ);
	        y++;
	        crossSectionZ += slope;
	        while (y <= maxY && crossSectionZ >= source.getMinZAtY(y) && crossSectionZ <= source.getMaxZAtY(y)) {
	            crossSectionMaxY = y;
	            int localMinV = source.getMinVAtYZ(y, crossSectionZ);
	            if (localMinV < crossSectionMinV) {
	                crossSectionMinV = localMinV;
	            }
	            int localMaxV = source.getMaxVAtYZ(y, crossSectionZ);
	            if (localMaxV > crossSectionMaxV) {
	                crossSectionMaxV = localMaxV;
	            }
	            int localMinW = source.getMinWAtYZ(y, crossSectionZ);
	            if (localMinW < crossSectionMinW) {
	                crossSectionMinW = localMinW;
	            }
	            int localMaxW = source.getMaxWAtYZ(y, crossSectionZ);
	            if (localMaxW > crossSectionMaxW) {
	                crossSectionMaxW = localMaxW;
	            }
	            int localMinX = source.getMinXAtYZ(y, crossSectionZ);
	            if (localMinX < crossSectionMinX) {
	                crossSectionMinX = localMinX;
	            }
	            int localMaxX = source.getMaxXAtYZ(y, crossSectionZ);
	            if (localMaxX > crossSectionMaxX) {
	                crossSectionMaxX = localMaxX;
	            }
	            y++;
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
	    int crossSectionY = crossSectionMinY;
	    int crossSectionZ = slope*crossSectionY + zOffsetFromY;
	    int result = source.getMinVAtWYZ(x, crossSectionY, crossSectionZ);
	    int localMinV;
	    for (crossSectionY++, crossSectionZ += slope;
	            crossSectionY <= crossSectionMaxY && (localMinV = source.getMinVAtWYZ(x, crossSectionY, crossSectionZ)) <= result;
	            crossSectionY++, crossSectionZ += slope) {
	        result = localMinV;
	    }
	    return result;
	}

	@Override
	public int getMaxWAtX(int x) {
	    int crossSectionY = crossSectionMinY;
	    int crossSectionZ = slope*crossSectionY + zOffsetFromY;
	    int result = source.getMaxVAtWYZ(x, crossSectionY, crossSectionZ);
	    int localMaxV;
	    for (crossSectionY++, crossSectionZ += slope;
	            crossSectionY <= crossSectionMaxY && (localMaxV = source.getMaxVAtWYZ(x, crossSectionY, crossSectionZ)) >= result;
	            crossSectionY++, crossSectionZ += slope) {
	        result = localMaxV;
	    }
	    return result;
	}

	@Override
	public int getMinWAtY(int y) {
	    int crossSectionY = crossSectionMinY;
	    int crossSectionZ = slope*crossSectionY + zOffsetFromY;
	    int result = source.getMinVAtXYZ(y, crossSectionY, crossSectionZ);
	    int localMinV;
	    for (crossSectionY++, crossSectionZ += slope;
	            crossSectionY <= crossSectionMaxY && (localMinV = source.getMinVAtXYZ(y, crossSectionY, crossSectionZ)) <= result;
	            crossSectionY++, crossSectionZ += slope) {
	        result = localMinV;
	    }
	    return result;
	}

	@Override
	public int getMaxWAtY(int y) {
	    int crossSectionY = crossSectionMinY;
	    int crossSectionZ = slope*crossSectionY + zOffsetFromY;
	    int result = source.getMaxVAtXYZ(y, crossSectionY, crossSectionZ);
	    int localMaxV;
	    for (crossSectionY++, crossSectionZ += slope;
	            crossSectionY <= crossSectionMaxY && (localMaxV = source.getMaxVAtXYZ(y, crossSectionY, crossSectionZ)) >= result;
	            crossSectionY++, crossSectionZ += slope) {
	        result = localMaxV;
	    }
	    return result;
	}

	@Override
	public int getMinWAtZ(int z) {
	    return source.getMinVAtYZ(z, slope*z + zOffsetFromY);
	}

	@Override
	public int getMaxWAtZ(int z) {
	    return source.getMaxVAtYZ(z, slope*z + zOffsetFromY);
	}

	@Override
	public int getMinWAtXY(int x, int y) {
	    int crossSectionY = crossSectionMinY;
	    int crossSectionZ = slope*crossSectionY + zOffsetFromY;
	    int result = source.getMinV(x, y, crossSectionY, crossSectionZ);
	    int localMinV;
	    for (crossSectionY++, crossSectionZ += slope;
	            crossSectionY <= crossSectionMaxY && (localMinV = source.getMinV(x, y, crossSectionY, crossSectionZ)) <= result;
	            crossSectionY++, crossSectionZ += slope) {
	        result = localMinV;
	    }
	    return result;
	}

	@Override
	public int getMaxWAtXY(int x, int y) {
	    int crossSectionY = crossSectionMinY;
	    int crossSectionZ = slope*crossSectionY + zOffsetFromY;
	    int result = source.getMaxV(x, y, crossSectionY, crossSectionZ);
	    int localMaxV;
	    for (crossSectionY++, crossSectionZ += slope;
	            crossSectionY <= crossSectionMaxY && (localMaxV = source.getMaxV(x, y, crossSectionY, crossSectionZ)) >= result;
	            crossSectionY++, crossSectionZ += slope) {
	        result = localMaxV;
	    }
	    return result;
	}

	@Override
	public int getMinWAtXZ(int x, int z) {
	    return source.getMinVAtWYZ(x, z, slope*z + zOffsetFromY);
	}

	@Override
	public int getMaxWAtXZ(int x, int z) {
	    return source.getMaxVAtWYZ(x, z, slope*z + zOffsetFromY);
	}

	@Override
	public int getMinWAtYZ(int y, int z) {
	    return source.getMinVAtXYZ(y, z, slope*z + zOffsetFromY);
	}

	@Override
	public int getMaxWAtYZ(int y, int z) {
	    return source.getMaxVAtXYZ(y, z, slope*z + zOffsetFromY);
	}

	@Override
	public int getMinW(int x, int y, int z) {
	    return source.getMinV(x, y, z, slope*z + zOffsetFromY);
	}

	@Override
	public int getMaxW(int x, int y, int z) {
	    return source.getMaxV(x, y, z, slope*z + zOffsetFromY);
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
	    int crossSectionY = crossSectionMinY;
	    int crossSectionZ = slope*crossSectionY + zOffsetFromY;
	    int result = source.getMinWAtVYZ(w, crossSectionY, crossSectionZ);
	    int localMinW;
	    for (crossSectionY++, crossSectionZ += slope;
	            crossSectionY <= crossSectionMaxY && (localMinW = source.getMinWAtVYZ(w, crossSectionY, crossSectionZ)) <= result;
	            crossSectionY++, crossSectionZ += slope) {
	        result = localMinW;
	    }
	    return result;
	}

	@Override
	public int getMaxXAtW(int w) {
	    int crossSectionY = crossSectionMinY;
	    int crossSectionZ = slope*crossSectionY + zOffsetFromY;
	    int result = source.getMaxWAtVYZ(w, crossSectionY, crossSectionZ);
	    int localMaxW;
	    for (crossSectionY++, crossSectionZ += slope;
	            crossSectionY <= crossSectionMaxY && (localMaxW = source.getMaxWAtVYZ(w, crossSectionY, crossSectionZ)) >= result;
	            crossSectionY++, crossSectionZ += slope) {
	        result = localMaxW;
	    }
	    return result;
	}

	@Override
	public int getMinXAtY(int y) {
	    int crossSectionY = crossSectionMinY;
	    int crossSectionZ = slope*crossSectionY + zOffsetFromY;
	    int result = source.getMinWAtXYZ(y, crossSectionY, crossSectionZ);
	    int localMinW;
	    for (crossSectionY++, crossSectionZ += slope;
	            crossSectionY <= crossSectionMaxY && (localMinW = source.getMinWAtXYZ(y, crossSectionY, crossSectionZ)) <= result;
	            crossSectionY++, crossSectionZ += slope) {
	        result = localMinW;
	    }
	    return result;
	}

	@Override
	public int getMaxXAtY(int y) {
	    int crossSectionY = crossSectionMinY;
	    int crossSectionZ = slope*crossSectionY + zOffsetFromY;
	    int result = source.getMaxWAtXYZ(y, crossSectionY, crossSectionZ);
	    int localMaxW;
	    for (crossSectionY++, crossSectionZ += slope;
	            crossSectionY <= crossSectionMaxY && (localMaxW = source.getMaxWAtXYZ(y, crossSectionY, crossSectionZ)) >= result;
	            crossSectionY++, crossSectionZ += slope) {
	        result = localMaxW;
	    }
	    return result;
	}

	@Override
	public int getMinXAtZ(int z) {
	    return source.getMinWAtYZ(z, slope*z + zOffsetFromY);
	}

	@Override
	public int getMaxXAtZ(int z) {
	    return source.getMaxWAtYZ(z, slope*z + zOffsetFromY);
	}

	@Override
	public int getMinXAtWY(int w, int y) {
	    int crossSectionY = crossSectionMinY;
	    int crossSectionZ = slope*crossSectionY + zOffsetFromY;
	    int result = source.getMinW(w, y, crossSectionY, crossSectionZ);
	    int localMinW;
	    for (crossSectionY++, crossSectionZ += slope;
	            crossSectionY <= crossSectionMaxY && (localMinW = source.getMinW(w, y, crossSectionY, crossSectionZ)) <= result;
	            crossSectionY++, crossSectionZ += slope) {
	        result = localMinW;
	    }
	    return result;
	}

	@Override
	public int getMaxXAtWY(int w, int y) {
	    int crossSectionY = crossSectionMinY;
	    int crossSectionZ = slope*crossSectionY + zOffsetFromY;
	    int result = source.getMaxW(w, y, crossSectionY, crossSectionZ);
	    int localMaxW;
	    for (crossSectionY++, crossSectionZ += slope;
	            crossSectionY <= crossSectionMaxY && (localMaxW = source.getMaxW(w, y, crossSectionY, crossSectionZ)) >= result;
	            crossSectionY++, crossSectionZ += slope) {
	        result = localMaxW;
	    }
	    return result;
	}

	@Override
	public int getMinXAtWZ(int w, int z) {
	    return source.getMinWAtVYZ(w, z, slope*z + zOffsetFromY);
	}

	@Override
	public int getMaxXAtWZ(int w, int z) {
	    return source.getMaxWAtVYZ(w, z, slope*z + zOffsetFromY);
	}

	@Override
	public int getMinXAtYZ(int y, int z) {
	    return source.getMinWAtXYZ(y, z, slope*z + zOffsetFromY);
	}

	@Override
	public int getMaxXAtYZ(int y, int z) {
	    return source.getMaxWAtXYZ(y, z, slope*z + zOffsetFromY);
	}

	@Override
	public int getMinX(int w, int y, int z) {
	    return source.getMinW(w, y, z, slope*z + zOffsetFromY);
	}

	@Override
	public int getMaxX(int w, int y, int z) {
	    return source.getMaxW(w, y, z, slope*z + zOffsetFromY);
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
	    int crossSectionY = crossSectionMinY;
	    int crossSectionZ = slope*crossSectionY + zOffsetFromY;
	    int result = source.getMinXAtVYZ(w, crossSectionY, crossSectionZ);
	    int localMinX;
	    for (crossSectionY++, crossSectionZ += slope;
	            crossSectionY <= crossSectionMaxY && (localMinX = source.getMinXAtVYZ(w, crossSectionY, crossSectionZ)) <= result;
	            crossSectionY++, crossSectionZ += slope) {
	        result = localMinX;
	    }
	    return result;
	}

	@Override
	public int getMaxYAtW(int w) {
	    int crossSectionY = crossSectionMinY;
	    int crossSectionZ = slope*crossSectionY + zOffsetFromY;
	    int result = source.getMaxXAtVYZ(w, crossSectionY, crossSectionZ);
	    int localMaxX;
	    for (crossSectionY++, crossSectionZ += slope;
	            crossSectionY <= crossSectionMaxY && (localMaxX = source.getMaxXAtVYZ(w, crossSectionY, crossSectionZ)) >= result;
	            crossSectionY++, crossSectionZ += slope) {
	        result = localMaxX;
	    }
	    return result;
	}

	@Override
	public int getMinYAtX(int x) {
	    int crossSectionY = crossSectionMinY;
	    int crossSectionZ = slope*crossSectionY + zOffsetFromY;
	    int result = source.getMinXAtWYZ(x, crossSectionY, crossSectionZ);
	    int localMinX;
	    for (crossSectionY++, crossSectionZ += slope;
	            crossSectionY <= crossSectionMaxY && (localMinX = source.getMinXAtWYZ(x, crossSectionY, crossSectionZ)) <= result;
	            crossSectionY++, crossSectionZ += slope) {
	        result = localMinX;
	    }
	    return result;
	}

	@Override
	public int getMaxYAtX(int x) {
	    int crossSectionY = crossSectionMinY;
	    int crossSectionZ = slope*crossSectionY + zOffsetFromY;
	    int result = source.getMaxXAtWYZ(x, crossSectionY, crossSectionZ);
	    int localMaxX;
	    for (crossSectionY++, crossSectionZ += slope;
	            crossSectionY <= crossSectionMaxY && (localMaxX = source.getMaxXAtWYZ(x, crossSectionY, crossSectionZ)) >= result;
	            crossSectionY++, crossSectionZ += slope) {
	        result = localMaxX;
	    }
	    return result;
	}

	@Override
	public int getMinYAtZ(int z) {
	    return source.getMinXAtYZ(z, slope*z + zOffsetFromY);
	}

	@Override
	public int getMaxYAtZ(int z) {
	    return source.getMaxXAtYZ(z, slope*z + zOffsetFromY);
	}

	@Override
	public int getMinYAtWX(int w, int x) {
	    int crossSectionY = crossSectionMinY;
	    int crossSectionZ = slope*crossSectionY + zOffsetFromY;
	    int result = source.getMinX(w, x, crossSectionY, crossSectionZ);
	    int localMinX;
	    for (crossSectionY++, crossSectionZ += slope;
	            crossSectionY <= crossSectionMaxY && (localMinX = source.getMinX(w, x, crossSectionY, crossSectionZ)) <= result;
	            crossSectionY++, crossSectionZ += slope) {
	        result = localMinX;
	    }
	    return result;
	}

	@Override
	public int getMaxYAtWX(int w, int x) {
	    int crossSectionY = crossSectionMinY;
	    int crossSectionZ = slope*crossSectionY + zOffsetFromY;
	    int result = source.getMaxX(w, x, crossSectionY, crossSectionZ);
	    int localMaxX;
	    for (crossSectionY++, crossSectionZ += slope;
	            crossSectionY <= crossSectionMaxY && (localMaxX = source.getMaxX(w, x, crossSectionY, crossSectionZ)) >= result;
	            crossSectionY++, crossSectionZ += slope) {
	        result = localMaxX;
	    }
	    return result;
	}

	@Override
	public int getMinYAtWZ(int w, int z) {
	    return source.getMinXAtVYZ(w, z, slope*z + zOffsetFromY);
	}

	@Override
	public int getMaxYAtWZ(int w, int z) {
	    return source.getMaxXAtVYZ(w, z, slope*z + zOffsetFromY);
	}

	@Override
	public int getMinYAtXZ(int x, int z) {
	    return source.getMinXAtWYZ(x, z, slope*z + zOffsetFromY);
	}

	@Override
	public int getMaxYAtXZ(int x, int z) {
	    return source.getMaxXAtWYZ(x, z, slope*z + zOffsetFromY);
	}

	@Override
	public int getMinY(int w, int x, int z) {
	    return source.getMinX(w, x, z, slope*z + zOffsetFromY);
	}

	@Override
	public int getMaxY(int w, int x, int z) {
	    return source.getMaxX(w, x, z, slope*z + zOffsetFromY);
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
	    for (int crossSectionY = crossSectionMinY, crossSectionZ = slope*crossSectionY + zOffsetFromY; crossSectionY <= crossSectionMaxY; crossSectionY++, crossSectionZ += slope) {
	        if (w >= source.getMinVAtYZ(crossSectionY, crossSectionZ)
	                && w <= source.getMaxVAtYZ(crossSectionY, crossSectionZ)) {
	            return crossSectionY;
	        }
	    }
	    throw new IllegalArgumentException("The coordinate is out of bounds.");
	}

	@Override
	public int getMaxZAtW(int w) {
	    for (int crossSectionY = crossSectionMaxY, crossSectionZ = slope*crossSectionY + zOffsetFromY; crossSectionY >= crossSectionMinY; crossSectionY--, crossSectionZ -= slope) {
	        if (w >= source.getMinVAtYZ(crossSectionY, crossSectionZ)
	                && w <= source.getMaxVAtYZ(crossSectionY, crossSectionZ)) {
	            return crossSectionY;
	        }
	    }
	    throw new IllegalArgumentException("The coordinate is out of bounds.");
	}

	@Override
	public int getMinZAtX(int x) {
	    for (int crossSectionY = crossSectionMinY, crossSectionZ = slope*crossSectionY + zOffsetFromY; crossSectionY <= crossSectionMaxY; crossSectionY++, crossSectionZ += slope) {
	        if (x >= source.getMinWAtYZ(crossSectionY, crossSectionZ)
	                && x <= source.getMaxWAtYZ(crossSectionY, crossSectionZ)) {
	            return crossSectionY;
	        }
	    }
	    throw new IllegalArgumentException("The coordinate is out of bounds.");
	}

	@Override
	public int getMaxZAtX(int x) {
	    for (int crossSectionY = crossSectionMaxY, crossSectionZ = slope*crossSectionY + zOffsetFromY; crossSectionY >= crossSectionMinY; crossSectionY--, crossSectionZ -= slope) {
	        if (x >= source.getMinWAtYZ(crossSectionY, crossSectionZ)
	                && x <= source.getMaxWAtYZ(crossSectionY, crossSectionZ)) {
	            return crossSectionY;
	        }
	    }
	    throw new IllegalArgumentException("The coordinate is out of bounds.");
	}

	@Override
	public int getMinZAtY(int y) {
	    for (int crossSectionY = crossSectionMinY, crossSectionZ = slope*crossSectionY + zOffsetFromY; crossSectionY <= crossSectionMaxY; crossSectionY++, crossSectionZ += slope) {
	        if (y >= source.getMinXAtYZ(crossSectionY, crossSectionZ)
	                && y <= source.getMaxXAtYZ(crossSectionY, crossSectionZ)) {
	            return crossSectionY;
	        }
	    }
	    throw new IllegalArgumentException("The coordinate is out of bounds.");
	}

	@Override
	public int getMaxZAtY(int y) {
	    for (int crossSectionY = crossSectionMaxY, crossSectionZ = slope*crossSectionY + zOffsetFromY; crossSectionY >= crossSectionMinY; crossSectionY--, crossSectionZ -= slope) {
	        if (y >= source.getMinXAtYZ(crossSectionY, crossSectionZ)
	                && y <= source.getMaxXAtYZ(crossSectionY, crossSectionZ)) {
	            return crossSectionY;
	        }
	    }
	    throw new IllegalArgumentException("The coordinate is out of bounds.");
	}

	@Override
	public int getMinZAtWX(int w, int x) {
	    for (int crossSectionY = crossSectionMinY, crossSectionZ = slope*crossSectionY + zOffsetFromY; crossSectionY <= crossSectionMaxY; crossSectionY++, crossSectionZ += slope) {
	        if (w >= source.getMinVAtYZ(crossSectionY, crossSectionZ)
	                && w <= source.getMaxVAtYZ(crossSectionY, crossSectionZ)
	                && x >= source.getMinWAtVYZ(w, crossSectionY, crossSectionZ)
	                && x <= source.getMaxWAtVYZ(w, crossSectionY, crossSectionZ)) {
	            return crossSectionY;
	        }
	    }
	    throw new IllegalArgumentException("The coordinates are out of bounds.");
	}

	@Override
	public int getMaxZAtWX(int w, int x) {
	    for (int crossSectionY = crossSectionMaxY, crossSectionZ = slope*crossSectionY + zOffsetFromY; crossSectionY >= crossSectionMinY; crossSectionY--, crossSectionZ -= slope) {
	        if (w >= source.getMinVAtYZ(crossSectionY, crossSectionZ)
	                && w <= source.getMaxVAtYZ(crossSectionY, crossSectionZ)
	                && x >= source.getMinWAtVYZ(w, crossSectionY, crossSectionZ)
	                && x <= source.getMaxWAtVYZ(w, crossSectionY, crossSectionZ)) {
	            return crossSectionY;
	        }
	    }
	    throw new IllegalArgumentException("The coordinates are out of bounds.");
	}

	@Override
	public int getMinZAtWY(int w, int y) {
	    for (int crossSectionY = crossSectionMinY, crossSectionZ = slope*crossSectionY + zOffsetFromY; crossSectionY <= crossSectionMaxY; crossSectionY++, crossSectionZ += slope) {
	        if (w >= source.getMinVAtYZ(crossSectionY, crossSectionZ)
	                && w <= source.getMaxVAtYZ(crossSectionY, crossSectionZ)
	                && y >= source.getMinXAtVYZ(w, crossSectionY, crossSectionZ)
	                && y <= source.getMaxXAtVYZ(w, crossSectionY, crossSectionZ)) {
	            return crossSectionY;
	        }
	    }
	    throw new IllegalArgumentException("The coordinates are out of bounds.");
	}

	@Override
	public int getMaxZAtWY(int w, int y) {
	    for (int crossSectionY = crossSectionMaxY, crossSectionZ = slope*crossSectionY + zOffsetFromY; crossSectionY >= crossSectionMinY; crossSectionY--, crossSectionZ -= slope) {
	        if (w >= source.getMinVAtYZ(crossSectionY, crossSectionZ)
	                && w <= source.getMaxVAtYZ(crossSectionY, crossSectionZ)
	                && y >= source.getMinXAtVYZ(w, crossSectionY, crossSectionZ)
	                && y <= source.getMaxXAtVYZ(w, crossSectionY, crossSectionZ)) {
	            return crossSectionY;
	        }
	    }
	    throw new IllegalArgumentException("The coordinates are out of bounds.");
	}

	@Override
	public int getMinZAtXY(int x, int y) {
	    for (int crossSectionY = crossSectionMinY, crossSectionZ = slope*crossSectionY + zOffsetFromY; crossSectionY <= crossSectionMaxY; crossSectionY++, crossSectionZ += slope) {
	        if (x >= source.getMinWAtYZ(crossSectionY, crossSectionZ)
	                && x <= source.getMaxWAtYZ(crossSectionY, crossSectionZ)
	                && y >= source.getMinXAtWYZ(x, crossSectionY, crossSectionZ)
	                && y <= source.getMaxXAtWYZ(x, crossSectionY, crossSectionZ)) {
	            return crossSectionY;
	        }
	    }
	    throw new IllegalArgumentException("The coordinates are out of bounds.");
	}

	@Override
	public int getMaxZAtXY(int x, int y) {
	    for (int crossSectionY = crossSectionMaxY, crossSectionZ = slope*crossSectionY + zOffsetFromY; crossSectionY >= crossSectionMinY; crossSectionY--, crossSectionZ -= slope) {
	        if (x >= source.getMinWAtYZ(crossSectionY, crossSectionZ)
	                && x <= source.getMaxWAtYZ(crossSectionY, crossSectionZ)
	                && y >= source.getMinXAtWYZ(x, crossSectionY, crossSectionZ)
	                && y <= source.getMaxXAtWYZ(x, crossSectionY, crossSectionZ)) {
	            return crossSectionY;
	        }
	    }
	    throw new IllegalArgumentException("The coordinates are out of bounds.");
	}

	@Override
	public int getMinZ(int w, int x, int y) {
	    for (int crossSectionY = crossSectionMinY, crossSectionZ = slope*crossSectionY + zOffsetFromY; crossSectionY <= crossSectionMaxY; crossSectionY++, crossSectionZ += slope) {
	        if (w >= source.getMinVAtYZ(crossSectionY, crossSectionZ)
	                && w <= source.getMaxVAtYZ(crossSectionY, crossSectionZ)
	                && x >= source.getMinWAtVYZ(w, crossSectionY, crossSectionZ)
	                && x <= source.getMaxWAtVYZ(w, crossSectionY, crossSectionZ)
	                && y >= source.getMinX(w, x, crossSectionY, crossSectionZ)
	                && y <= source.getMaxX(w, x, crossSectionY, crossSectionZ)) {
	            return crossSectionY;
	        }
	    }
	    throw new IllegalArgumentException("The coordinates are out of bounds.");
	}

	@Override
	public int getMaxZ(int w, int x, int y) {
	    for (int crossSectionY = crossSectionMaxY, crossSectionZ = slope*crossSectionY + zOffsetFromY; crossSectionY >= crossSectionMinY; crossSectionY--, crossSectionZ -= slope) {
	        if (w >= source.getMinVAtYZ(crossSectionY, crossSectionZ)
	                && w <= source.getMaxVAtYZ(crossSectionY, crossSectionZ)
	                && x >= source.getMinWAtVYZ(w, crossSectionY, crossSectionZ)
	                && x <= source.getMaxWAtVYZ(w, crossSectionY, crossSectionZ)
	                && y >= source.getMinX(w, x, crossSectionY, crossSectionZ)
	                && y <= source.getMaxX(w, x, crossSectionY, crossSectionZ)) {
	            return crossSectionY;
	        }
	    }
	    throw new IllegalArgumentException("The coordinates are out of bounds.");
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
	    path.append(source.getYLabel());
	    if (zOffsetFromY < 0) {
	        path.append(zOffsetFromY);
	    } else if (zOffsetFromY > 0) {
	        path.append("+").append(zOffsetFromY);
	    }
	    return path.toString();
	}

	@Override
	public void backUp(String backupPath, String backupName) throws FileNotFoundException, IOException {
	    source.backUp(backupPath, backupName);
	}

}
