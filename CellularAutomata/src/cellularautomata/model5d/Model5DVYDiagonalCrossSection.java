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

public class Model5DVYDiagonalCrossSection<Source_Type extends Model5D> implements Model4D {

	protected Source_Type source;
	protected int slope;
	protected int yOffsetFromV;
	protected int crossSectionMinV;
	protected int crossSectionMaxV;
	protected int crossSectionMinW;
	protected int crossSectionMaxW;
	protected int crossSectionMinX;
	protected int crossSectionMaxX;
	protected int crossSectionMinZ;
	protected int crossSectionMaxZ;

	public Model5DVYDiagonalCrossSection(Source_Type source, boolean positiveSlope, int yOffsetFromV) {
	    this.source = source;
	    this.slope = positiveSlope ? 1 : -1;
	    this.yOffsetFromV = yOffsetFromV;
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
	    int v = source.getMinV();
	    int maxV = source.getMaxV();
	    int crossSectionY = slope*v + yOffsetFromV;
	    while (v <= maxV && (crossSectionY < source.getMinYAtV(v) || crossSectionY > source.getMaxYAtV(v))) {
	        v++;
	        crossSectionY += slope;
	    }
	    if (v <= maxV) {
	        crossSectionMinV = v;
	        crossSectionMaxV = v;
	        crossSectionMinW = source.getMinWAtVY(v, crossSectionY);
	        crossSectionMaxW = source.getMaxWAtVY(v, crossSectionY);
	        crossSectionMinX = source.getMinXAtVY(v, crossSectionY);
	        crossSectionMaxX = source.getMaxXAtVY(v, crossSectionY);
	        crossSectionMinZ = source.getMinZAtVY(v, crossSectionY);
	        crossSectionMaxZ = source.getMaxZAtVY(v, crossSectionY);
	        v++;
	        crossSectionY += slope;
	        while (v <= maxV && crossSectionY >= source.getMinYAtV(v) && crossSectionY <= source.getMaxYAtV(v)) {
	            crossSectionMaxV = v;
	            int localMinW = source.getMinWAtVY(v, crossSectionY);
	            if (localMinW < crossSectionMinW) {
	                crossSectionMinW = localMinW;
	            }
	            int localMaxW = source.getMaxWAtVY(v, crossSectionY);
	            if (localMaxW > crossSectionMaxW) {
	                crossSectionMaxW = localMaxW;
	            }
	            int localMinX = source.getMinXAtVY(v, crossSectionY);
	            if (localMinX < crossSectionMinX) {
	                crossSectionMinX = localMinX;
	            }
	            int localMaxX = source.getMaxXAtVY(v, crossSectionY);
	            if (localMaxX > crossSectionMaxX) {
	                crossSectionMaxX = localMaxX;
	            }
	            int localMinZ = source.getMinZAtVY(v, crossSectionY);
	            if (localMinZ < crossSectionMinZ) {
	                crossSectionMinZ = localMinZ;
	            }
	            int localMaxZ = source.getMaxZAtVY(v, crossSectionY);
	            if (localMaxZ > crossSectionMaxZ) {
	                crossSectionMaxZ = localMaxZ;
	            }
	            v++;
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
	    for (int crossSectionV = crossSectionMinV, crossSectionY = slope*crossSectionV + yOffsetFromV; crossSectionV <= crossSectionMaxV; crossSectionV++, crossSectionY += slope) {
	        if (x >= source.getMinWAtVY(crossSectionV, crossSectionY)
	                && x <= source.getMaxWAtVY(crossSectionV, crossSectionY)) {
	            return crossSectionV;
	        }
	    }
	    throw new IllegalArgumentException("The coordinate is out of bounds.");
	}

	@Override
	public int getMaxWAtX(int x) {
	    for (int crossSectionV = crossSectionMaxV, crossSectionY = slope*crossSectionV + yOffsetFromV; crossSectionV >= crossSectionMinV; crossSectionV--, crossSectionY -= slope) {
	        if (x >= source.getMinWAtVY(crossSectionV, crossSectionY)
	                && x <= source.getMaxWAtVY(crossSectionV, crossSectionY)) {
	            return crossSectionV;
	        }
	    }
	    throw new IllegalArgumentException("The coordinate is out of bounds.");
	}

	@Override
	public int getMinWAtY(int y) {
	    for (int crossSectionV = crossSectionMinV, crossSectionY = slope*crossSectionV + yOffsetFromV; crossSectionV <= crossSectionMaxV; crossSectionV++, crossSectionY += slope) {
	        if (y >= source.getMinXAtVY(crossSectionV, crossSectionY)
	                && y <= source.getMaxXAtVY(crossSectionV, crossSectionY)) {
	            return crossSectionV;
	        }
	    }
	    throw new IllegalArgumentException("The coordinate is out of bounds.");
	}

	@Override
	public int getMaxWAtY(int y) {
	    for (int crossSectionV = crossSectionMaxV, crossSectionY = slope*crossSectionV + yOffsetFromV; crossSectionV >= crossSectionMinV; crossSectionV--, crossSectionY -= slope) {
	        if (y >= source.getMinXAtVY(crossSectionV, crossSectionY)
	                && y <= source.getMaxXAtVY(crossSectionV, crossSectionY)) {
	            return crossSectionV;
	        }
	    }
	    throw new IllegalArgumentException("The coordinate is out of bounds.");
	}

	@Override
	public int getMinWAtZ(int z) {
	    for (int crossSectionV = crossSectionMinV, crossSectionY = slope*crossSectionV + yOffsetFromV; crossSectionV <= crossSectionMaxV; crossSectionV++, crossSectionY += slope) {
	        if (z >= source.getMinZAtVY(crossSectionV, crossSectionY)
	                && z <= source.getMaxZAtVY(crossSectionV, crossSectionY)) {
	            return crossSectionV;
	        }
	    }
	    throw new IllegalArgumentException("The coordinate is out of bounds.");
	}

	@Override
	public int getMaxWAtZ(int z) {
	    for (int crossSectionV = crossSectionMaxV, crossSectionY = slope*crossSectionV + yOffsetFromV; crossSectionV >= crossSectionMinV; crossSectionV--, crossSectionY -= slope) {
	        if (z >= source.getMinZAtVY(crossSectionV, crossSectionY)
	                && z <= source.getMaxZAtVY(crossSectionV, crossSectionY)) {
	            return crossSectionV;
	        }
	    }
	    throw new IllegalArgumentException("The coordinate is out of bounds.");
	}

	@Override
	public int getMinWAtXY(int x, int y) {
	    for (int crossSectionV = crossSectionMinV, crossSectionY = slope*crossSectionV + yOffsetFromV; crossSectionV <= crossSectionMaxV; crossSectionV++, crossSectionY += slope) {
	        if (x >= source.getMinWAtVY(crossSectionV, crossSectionY)
	                && x <= source.getMaxWAtVY(crossSectionV, crossSectionY)
	                && y >= source.getMinXAtVWY(crossSectionV, x, crossSectionY)
	                && y <= source.getMaxXAtVWY(crossSectionV, x, crossSectionY)) {
	            return crossSectionV;
	        }
	    }
	    throw new IllegalArgumentException("The coordinates are out of bounds.");
	}

	@Override
	public int getMaxWAtXY(int x, int y) {
	    for (int crossSectionV = crossSectionMaxV, crossSectionY = slope*crossSectionV + yOffsetFromV; crossSectionV >= crossSectionMinV; crossSectionV--, crossSectionY -= slope) {
	        if (x >= source.getMinWAtVY(crossSectionV, crossSectionY)
	                && x <= source.getMaxWAtVY(crossSectionV, crossSectionY)
	                && y >= source.getMinXAtVWY(crossSectionV, x, crossSectionY)
	                && y <= source.getMaxXAtVWY(crossSectionV, x, crossSectionY)) {
	            return crossSectionV;
	        }
	    }
	    throw new IllegalArgumentException("The coordinates are out of bounds.");
	}

	@Override
	public int getMinWAtXZ(int x, int z) {
	    for (int crossSectionV = crossSectionMinV, crossSectionY = slope*crossSectionV + yOffsetFromV; crossSectionV <= crossSectionMaxV; crossSectionV++, crossSectionY += slope) {
	        if (x >= source.getMinWAtVY(crossSectionV, crossSectionY)
	                && x <= source.getMaxWAtVY(crossSectionV, crossSectionY)
	                && z >= source.getMinZAtVWY(crossSectionV, x, crossSectionY)
	                && z <= source.getMaxZAtVWY(crossSectionV, x, crossSectionY)) {
	            return crossSectionV;
	        }
	    }
	    throw new IllegalArgumentException("The coordinates are out of bounds.");
	}

	@Override
	public int getMaxWAtXZ(int x, int z) {
	    for (int crossSectionV = crossSectionMaxV, crossSectionY = slope*crossSectionV + yOffsetFromV; crossSectionV >= crossSectionMinV; crossSectionV--, crossSectionY -= slope) {
	        if (x >= source.getMinWAtVY(crossSectionV, crossSectionY)
	                && x <= source.getMaxWAtVY(crossSectionV, crossSectionY)
	                && z >= source.getMinZAtVWY(crossSectionV, x, crossSectionY)
	                && z <= source.getMaxZAtVWY(crossSectionV, x, crossSectionY)) {
	            return crossSectionV;
	        }
	    }
	    throw new IllegalArgumentException("The coordinates are out of bounds.");
	}

	@Override
	public int getMinWAtYZ(int y, int z) {
	    for (int crossSectionV = crossSectionMinV, crossSectionY = slope*crossSectionV + yOffsetFromV; crossSectionV <= crossSectionMaxV; crossSectionV++, crossSectionY += slope) {
	        if (y >= source.getMinXAtVY(crossSectionV, crossSectionY)
	                && y <= source.getMaxXAtVY(crossSectionV, crossSectionY)
	                && z >= source.getMinZAtVXY(crossSectionV, y, crossSectionY)
	                && z <= source.getMaxZAtVXY(crossSectionV, y, crossSectionY)) {
	            return crossSectionV;
	        }
	    }
	    throw new IllegalArgumentException("The coordinates are out of bounds.");
	}

	@Override
	public int getMaxWAtYZ(int y, int z) {
	    for (int crossSectionV = crossSectionMaxV, crossSectionY = slope*crossSectionV + yOffsetFromV; crossSectionV >= crossSectionMinV; crossSectionV--, crossSectionY -= slope) {
	        if (y >= source.getMinXAtVY(crossSectionV, crossSectionY)
	                && y <= source.getMaxXAtVY(crossSectionV, crossSectionY)
	                && z >= source.getMinZAtVXY(crossSectionV, y, crossSectionY)
	                && z <= source.getMaxZAtVXY(crossSectionV, y, crossSectionY)) {
	            return crossSectionV;
	        }
	    }
	    throw new IllegalArgumentException("The coordinates are out of bounds.");
	}

	@Override
	public int getMinW(int x, int y, int z) {
	    for (int crossSectionV = crossSectionMinV, crossSectionY = slope*crossSectionV + yOffsetFromV; crossSectionV <= crossSectionMaxV; crossSectionV++, crossSectionY += slope) {
	        if (x >= source.getMinWAtVY(crossSectionV, crossSectionY)
	                && x <= source.getMaxWAtVY(crossSectionV, crossSectionY)
	                && y >= source.getMinXAtVWY(crossSectionV, x, crossSectionY)
	                && y <= source.getMaxXAtVWY(crossSectionV, x, crossSectionY)
	                && z >= source.getMinZ(crossSectionV, x, y, crossSectionY)
	                && z <= source.getMaxZ(crossSectionV, x, y, crossSectionY)) {
	            return crossSectionV;
	        }
	    }
	    throw new IllegalArgumentException("The coordinates are out of bounds.");
	}

	@Override
	public int getMaxW(int x, int y, int z) {
	    for (int crossSectionV = crossSectionMaxV, crossSectionY = slope*crossSectionV + yOffsetFromV; crossSectionV >= crossSectionMinV; crossSectionV--, crossSectionY -= slope) {
	        if (x >= source.getMinWAtVY(crossSectionV, crossSectionY)
	                && x <= source.getMaxWAtVY(crossSectionV, crossSectionY)
	                && y >= source.getMinXAtVWY(crossSectionV, x, crossSectionY)
	                && y <= source.getMaxXAtVWY(crossSectionV, x, crossSectionY)
	                && z >= source.getMinZ(crossSectionV, x, y, crossSectionY)
	                && z <= source.getMaxZ(crossSectionV, x, y, crossSectionY)) {
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
	    return source.getMinWAtVY(w, slope*w + yOffsetFromV);
	}

	@Override
	public int getMaxXAtW(int w) {
	    return source.getMaxWAtVY(w, slope*w + yOffsetFromV);
	}

	@Override
	public int getMinXAtY(int y) {
	    int crossSectionV = crossSectionMinV;
	    int crossSectionY = slope*crossSectionV + yOffsetFromV;
	    int result = source.getMinWAtVXY(crossSectionV, y, crossSectionY);
	    int localMinW;
	    for (crossSectionV++, crossSectionY += slope;
	            crossSectionV <= crossSectionMaxV && (localMinW = source.getMinWAtVXY(crossSectionV, y, crossSectionY)) <= result;
	            crossSectionV++, crossSectionY += slope) {
	        result = localMinW;
	    }
	    return result;
	}

	@Override
	public int getMaxXAtY(int y) {
	    int crossSectionV = crossSectionMinV;
	    int crossSectionY = slope*crossSectionV + yOffsetFromV;
	    int result = source.getMaxWAtVXY(crossSectionV, y, crossSectionY);
	    int localMaxW;
	    for (crossSectionV++, crossSectionY += slope;
	            crossSectionV <= crossSectionMaxV && (localMaxW = source.getMaxWAtVXY(crossSectionV, y, crossSectionY)) >= result;
	            crossSectionV++, crossSectionY += slope) {
	        result = localMaxW;
	    }
	    return result;
	}

	@Override
	public int getMinXAtZ(int z) {
	    int crossSectionV = crossSectionMinV;
	    int crossSectionY = slope*crossSectionV + yOffsetFromV;
	    int result = source.getMinWAtVYZ(crossSectionV, crossSectionY, z);
	    int localMinW;
	    for (crossSectionV++, crossSectionY += slope;
	            crossSectionV <= crossSectionMaxV && (localMinW = source.getMinWAtVYZ(crossSectionV, crossSectionY, z)) <= result;
	            crossSectionV++, crossSectionY += slope) {
	        result = localMinW;
	    }
	    return result;
	}

	@Override
	public int getMaxXAtZ(int z) {
	    int crossSectionV = crossSectionMinV;
	    int crossSectionY = slope*crossSectionV + yOffsetFromV;
	    int result = source.getMaxWAtVYZ(crossSectionV, crossSectionY, z);
	    int localMaxW;
	    for (crossSectionV++, crossSectionY += slope;
	            crossSectionV <= crossSectionMaxV && (localMaxW = source.getMaxWAtVYZ(crossSectionV, crossSectionY, z)) >= result;
	            crossSectionV++, crossSectionY += slope) {
	        result = localMaxW;
	    }
	    return result;
	}

	@Override
	public int getMinXAtWY(int w, int y) {
	    return source.getMinWAtVXY(w, y, slope*w + yOffsetFromV);
	}

	@Override
	public int getMaxXAtWY(int w, int y) {
	    return source.getMaxWAtVXY(w, y, slope*w + yOffsetFromV);
	}

	@Override
	public int getMinXAtWZ(int w, int z) {
	    return source.getMinWAtVYZ(w, slope*w + yOffsetFromV, z);
	}

	@Override
	public int getMaxXAtWZ(int w, int z) {
	    return source.getMaxWAtVYZ(w, slope*w + yOffsetFromV, z);
	}

	@Override
	public int getMinXAtYZ(int y, int z) {
	    int crossSectionV = crossSectionMinV;
	    int crossSectionY = slope*crossSectionV + yOffsetFromV;
	    int result = source.getMinW(crossSectionV, y, crossSectionY, z);
	    int localMinW;
	    for (crossSectionV++, crossSectionY += slope;
	            crossSectionV <= crossSectionMaxV && (localMinW = source.getMinW(crossSectionV, y, crossSectionY, z)) <= result;
	            crossSectionV++, crossSectionY += slope) {
	        result = localMinW;
	    }
	    return result;
	}

	@Override
	public int getMaxXAtYZ(int y, int z) {
	    int crossSectionV = crossSectionMinV;
	    int crossSectionY = slope*crossSectionV + yOffsetFromV;
	    int result = source.getMaxW(crossSectionV, y, crossSectionY, z);
	    int localMaxW;
	    for (crossSectionV++, crossSectionY += slope;
	            crossSectionV <= crossSectionMaxV && (localMaxW = source.getMaxW(crossSectionV, y, crossSectionY, z)) >= result;
	            crossSectionV++, crossSectionY += slope) {
	        result = localMaxW;
	    }
	    return result;
	}

	@Override
	public int getMinX(int w, int y, int z) {
	    return source.getMinW(w, y, slope*w + yOffsetFromV, z);
	}

	@Override
	public int getMaxX(int w, int y, int z) {
	    return source.getMaxW(w, y, slope*w + yOffsetFromV, z);
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
	    return source.getMinXAtVY(w, slope*w + yOffsetFromV);
	}

	@Override
	public int getMaxYAtW(int w) {
	    return source.getMaxXAtVY(w, slope*w + yOffsetFromV);
	}

	@Override
	public int getMinYAtX(int x) {
	    int crossSectionV = crossSectionMinV;
	    int crossSectionY = slope*crossSectionV + yOffsetFromV;
	    int result = source.getMinXAtVWY(crossSectionV, x, crossSectionY);
	    int localMinX;
	    for (crossSectionV++, crossSectionY += slope;
	            crossSectionV <= crossSectionMaxV && (localMinX = source.getMinXAtVWY(crossSectionV, x, crossSectionY)) <= result;
	            crossSectionV++, crossSectionY += slope) {
	        result = localMinX;
	    }
	    return result;
	}

	@Override
	public int getMaxYAtX(int x) {
	    int crossSectionV = crossSectionMinV;
	    int crossSectionY = slope*crossSectionV + yOffsetFromV;
	    int result = source.getMaxXAtVWY(crossSectionV, x, crossSectionY);
	    int localMaxX;
	    for (crossSectionV++, crossSectionY += slope;
	            crossSectionV <= crossSectionMaxV && (localMaxX = source.getMaxXAtVWY(crossSectionV, x, crossSectionY)) >= result;
	            crossSectionV++, crossSectionY += slope) {
	        result = localMaxX;
	    }
	    return result;
	}

	@Override
	public int getMinYAtZ(int z) {
	    int crossSectionV = crossSectionMinV;
	    int crossSectionY = slope*crossSectionV + yOffsetFromV;
	    int result = source.getMinXAtVYZ(crossSectionV, crossSectionY, z);
	    int localMinX;
	    for (crossSectionV++, crossSectionY += slope;
	            crossSectionV <= crossSectionMaxV && (localMinX = source.getMinXAtVYZ(crossSectionV, crossSectionY, z)) <= result;
	            crossSectionV++, crossSectionY += slope) {
	        result = localMinX;
	    }
	    return result;
	}

	@Override
	public int getMaxYAtZ(int z) {
	    int crossSectionV = crossSectionMinV;
	    int crossSectionY = slope*crossSectionV + yOffsetFromV;
	    int result = source.getMaxXAtVYZ(crossSectionV, crossSectionY, z);
	    int localMaxX;
	    for (crossSectionV++, crossSectionY += slope;
	            crossSectionV <= crossSectionMaxV && (localMaxX = source.getMaxXAtVYZ(crossSectionV, crossSectionY, z)) >= result;
	            crossSectionV++, crossSectionY += slope) {
	        result = localMaxX;
	    }
	    return result;
	}

	@Override
	public int getMinYAtWX(int w, int x) {
	    return source.getMinXAtVWY(w, x, slope*w + yOffsetFromV);
	}

	@Override
	public int getMaxYAtWX(int w, int x) {
	    return source.getMaxXAtVWY(w, x, slope*w + yOffsetFromV);
	}

	@Override
	public int getMinYAtWZ(int w, int z) {
	    return source.getMinXAtVYZ(w, slope*w + yOffsetFromV, z);
	}

	@Override
	public int getMaxYAtWZ(int w, int z) {
	    return source.getMaxXAtVYZ(w, slope*w + yOffsetFromV, z);
	}

	@Override
	public int getMinYAtXZ(int x, int z) {
	    int crossSectionV = crossSectionMinV;
	    int crossSectionY = slope*crossSectionV + yOffsetFromV;
	    int result = source.getMinX(crossSectionV, x, crossSectionY, z);
	    int localMinX;
	    for (crossSectionV++, crossSectionY += slope;
	            crossSectionV <= crossSectionMaxV && (localMinX = source.getMinX(crossSectionV, x, crossSectionY, z)) <= result;
	            crossSectionV++, crossSectionY += slope) {
	        result = localMinX;
	    }
	    return result;
	}

	@Override
	public int getMaxYAtXZ(int x, int z) {
	    int crossSectionV = crossSectionMinV;
	    int crossSectionY = slope*crossSectionV + yOffsetFromV;
	    int result = source.getMaxX(crossSectionV, x, crossSectionY, z);
	    int localMaxX;
	    for (crossSectionV++, crossSectionY += slope;
	            crossSectionV <= crossSectionMaxV && (localMaxX = source.getMaxX(crossSectionV, x, crossSectionY, z)) >= result;
	            crossSectionV++, crossSectionY += slope) {
	        result = localMaxX;
	    }
	    return result;
	}

	@Override
	public int getMinY(int w, int x, int z) {
	    return source.getMinX(w, x, slope*w + yOffsetFromV, z);
	}

	@Override
	public int getMaxY(int w, int x, int z) {
	    return source.getMaxX(w, x, slope*w + yOffsetFromV, z);
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
	    return source.getMinZAtVY(w, slope*w + yOffsetFromV);
	}

	@Override
	public int getMaxZAtW(int w) {
	    return source.getMaxZAtVY(w, slope*w + yOffsetFromV);
	}

	@Override
	public int getMinZAtX(int x) {
	    int crossSectionV = crossSectionMinV;
	    int crossSectionY = slope*crossSectionV + yOffsetFromV;
	    int result = source.getMinZAtVWY(crossSectionV, x, crossSectionY);
	    int localMinZ;
	    for (crossSectionV++, crossSectionY += slope;
	            crossSectionV <= crossSectionMaxV && (localMinZ = source.getMinZAtVWY(crossSectionV, x, crossSectionY)) <= result;
	            crossSectionV++, crossSectionY += slope) {
	        result = localMinZ;
	    }
	    return result;
	}

	@Override
	public int getMaxZAtX(int x) {
	    int crossSectionV = crossSectionMinV;
	    int crossSectionY = slope*crossSectionV + yOffsetFromV;
	    int result = source.getMaxZAtVWY(crossSectionV, x, crossSectionY);
	    int localMaxZ;
	    for (crossSectionV++, crossSectionY += slope;
	            crossSectionV <= crossSectionMaxV && (localMaxZ = source.getMaxZAtVWY(crossSectionV, x, crossSectionY)) >= result;
	            crossSectionV++, crossSectionY += slope) {
	        result = localMaxZ;
	    }
	    return result;
	}

	@Override
	public int getMinZAtY(int y) {
	    int crossSectionV = crossSectionMinV;
	    int crossSectionY = slope*crossSectionV + yOffsetFromV;
	    int result = source.getMinZAtVXY(crossSectionV, y, crossSectionY);
	    int localMinZ;
	    for (crossSectionV++, crossSectionY += slope;
	            crossSectionV <= crossSectionMaxV && (localMinZ = source.getMinZAtVXY(crossSectionV, y, crossSectionY)) <= result;
	            crossSectionV++, crossSectionY += slope) {
	        result = localMinZ;
	    }
	    return result;
	}

	@Override
	public int getMaxZAtY(int y) {
	    int crossSectionV = crossSectionMinV;
	    int crossSectionY = slope*crossSectionV + yOffsetFromV;
	    int result = source.getMaxZAtVXY(crossSectionV, y, crossSectionY);
	    int localMaxZ;
	    for (crossSectionV++, crossSectionY += slope;
	            crossSectionV <= crossSectionMaxV && (localMaxZ = source.getMaxZAtVXY(crossSectionV, y, crossSectionY)) >= result;
	            crossSectionV++, crossSectionY += slope) {
	        result = localMaxZ;
	    }
	    return result;
	}

	@Override
	public int getMinZAtWX(int w, int x) {
	    return source.getMinZAtVWY(w, x, slope*w + yOffsetFromV);
	}

	@Override
	public int getMaxZAtWX(int w, int x) {
	    return source.getMaxZAtVWY(w, x, slope*w + yOffsetFromV);
	}

	@Override
	public int getMinZAtWY(int w, int y) {
	    return source.getMinZAtVXY(w, y, slope*w + yOffsetFromV);
	}

	@Override
	public int getMaxZAtWY(int w, int y) {
	    return source.getMaxZAtVXY(w, y, slope*w + yOffsetFromV);
	}

	@Override
	public int getMinZAtXY(int x, int y) {
	    int crossSectionV = crossSectionMinV;
	    int crossSectionY = slope*crossSectionV + yOffsetFromV;
	    int result = source.getMinZ(crossSectionV, x, y, crossSectionY);
	    int localMinZ;
	    for (crossSectionV++, crossSectionY += slope;
	            crossSectionV <= crossSectionMaxV && (localMinZ = source.getMinZ(crossSectionV, x, y, crossSectionY)) <= result;
	            crossSectionV++, crossSectionY += slope) {
	        result = localMinZ;
	    }
	    return result;
	}

	@Override
	public int getMaxZAtXY(int x, int y) {
	    int crossSectionV = crossSectionMinV;
	    int crossSectionY = slope*crossSectionV + yOffsetFromV;
	    int result = source.getMaxZ(crossSectionV, x, y, crossSectionY);
	    int localMaxZ;
	    for (crossSectionV++, crossSectionY += slope;
	            crossSectionV <= crossSectionMaxV && (localMaxZ = source.getMaxZ(crossSectionV, x, y, crossSectionY)) >= result;
	            crossSectionV++, crossSectionY += slope) {
	        result = localMaxZ;
	    }
	    return result;
	}

	@Override
	public int getMinZ(int w, int x, int y) {
	    return source.getMinZ(w, x, y, slope*w + yOffsetFromV);
	}

	@Override
	public int getMaxZ(int w, int x, int y) {
	    return source.getMaxZ(w, x, y, slope*w + yOffsetFromV);
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
	    path.append(source.getVLabel());
	    if (yOffsetFromV < 0) {
	        path.append(yOffsetFromV);
	    } else if (yOffsetFromV > 0) {
	        path.append("+").append(yOffsetFromV);
	    }
	    return path.toString();
	}

	@Override
	public void backUp(String backupPath, String backupName) throws Exception {
	    source.backUp(backupPath, backupName);
	}

}
