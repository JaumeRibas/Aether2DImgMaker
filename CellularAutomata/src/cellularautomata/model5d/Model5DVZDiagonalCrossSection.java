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

public class Model5DVZDiagonalCrossSection<Source_Type extends Model5D> implements Model4D {

	protected Source_Type source;
	protected int slope;
	protected int zOffsetFromV;
	protected int crossSectionMinV;
	protected int crossSectionMaxV;
	protected int crossSectionMinW;
	protected int crossSectionMaxW;
	protected int crossSectionMinX;
	protected int crossSectionMaxX;
	protected int crossSectionMinY;
	protected int crossSectionMaxY;

	public Model5DVZDiagonalCrossSection(Source_Type source, boolean positiveSlope, int zOffsetFromV) {
	    this.source = source;
	    this.slope = positiveSlope ? 1 : -1;
	    this.zOffsetFromV = zOffsetFromV;
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
	    int v = source.getMinV();
	    int maxV = source.getMaxV();
	    int crossSectionZ = slope*v + zOffsetFromV;
	    while (v <= maxV && (crossSectionZ < source.getMinZAtV(v) || crossSectionZ > source.getMaxZAtV(v))) {
	        v++;
	        crossSectionZ += slope;
	    }
	    if (v <= maxV) {
	        crossSectionMinV = v;
	        crossSectionMaxV = v;
	        crossSectionMinW = source.getMinWAtVZ(v, crossSectionZ);
	        crossSectionMaxW = source.getMaxWAtVZ(v, crossSectionZ);
	        crossSectionMinX = source.getMinXAtVZ(v, crossSectionZ);
	        crossSectionMaxX = source.getMaxXAtVZ(v, crossSectionZ);
	        crossSectionMinY = source.getMinYAtVZ(v, crossSectionZ);
	        crossSectionMaxY = source.getMaxYAtVZ(v, crossSectionZ);
	        v++;
	        crossSectionZ += slope;
	        while (v <= maxV && crossSectionZ >= source.getMinZAtV(v) && crossSectionZ <= source.getMaxZAtV(v)) {
	            crossSectionMaxV = v;
	            int localMinW = source.getMinWAtVZ(v, crossSectionZ);
	            if (localMinW < crossSectionMinW) {
	                crossSectionMinW = localMinW;
	            }
	            int localMaxW = source.getMaxWAtVZ(v, crossSectionZ);
	            if (localMaxW > crossSectionMaxW) {
	                crossSectionMaxW = localMaxW;
	            }
	            int localMinX = source.getMinXAtVZ(v, crossSectionZ);
	            if (localMinX < crossSectionMinX) {
	                crossSectionMinX = localMinX;
	            }
	            int localMaxX = source.getMaxXAtVZ(v, crossSectionZ);
	            if (localMaxX > crossSectionMaxX) {
	                crossSectionMaxX = localMaxX;
	            }
	            int localMinY = source.getMinYAtVZ(v, crossSectionZ);
	            if (localMinY < crossSectionMinY) {
	                crossSectionMinY = localMinY;
	            }
	            int localMaxY = source.getMaxYAtVZ(v, crossSectionZ);
	            if (localMaxY > crossSectionMaxY) {
	                crossSectionMaxY = localMaxY;
	            }
	            v++;
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
	    for (int crossSectionV = crossSectionMinV, crossSectionZ = slope*crossSectionV + zOffsetFromV; crossSectionV <= crossSectionMaxV; crossSectionV++, crossSectionZ += slope) {
	        if (x >= source.getMinWAtVZ(crossSectionV, crossSectionZ)
	                && x <= source.getMaxWAtVZ(crossSectionV, crossSectionZ)) {
	            return crossSectionV;
	        }
	    }
	    throw new IllegalArgumentException("The coordinate is out of bounds.");
	}

	@Override
	public int getMaxWAtX(int x) {
	    for (int crossSectionV = crossSectionMaxV, crossSectionZ = slope*crossSectionV + zOffsetFromV; crossSectionV >= crossSectionMinV; crossSectionV--, crossSectionZ -= slope) {
	        if (x >= source.getMinWAtVZ(crossSectionV, crossSectionZ)
	                && x <= source.getMaxWAtVZ(crossSectionV, crossSectionZ)) {
	            return crossSectionV;
	        }
	    }
	    throw new IllegalArgumentException("The coordinate is out of bounds.");
	}

	@Override
	public int getMinWAtY(int y) {
	    for (int crossSectionV = crossSectionMinV, crossSectionZ = slope*crossSectionV + zOffsetFromV; crossSectionV <= crossSectionMaxV; crossSectionV++, crossSectionZ += slope) {
	        if (y >= source.getMinXAtVZ(crossSectionV, crossSectionZ)
	                && y <= source.getMaxXAtVZ(crossSectionV, crossSectionZ)) {
	            return crossSectionV;
	        }
	    }
	    throw new IllegalArgumentException("The coordinate is out of bounds.");
	}

	@Override
	public int getMaxWAtY(int y) {
	    for (int crossSectionV = crossSectionMaxV, crossSectionZ = slope*crossSectionV + zOffsetFromV; crossSectionV >= crossSectionMinV; crossSectionV--, crossSectionZ -= slope) {
	        if (y >= source.getMinXAtVZ(crossSectionV, crossSectionZ)
	                && y <= source.getMaxXAtVZ(crossSectionV, crossSectionZ)) {
	            return crossSectionV;
	        }
	    }
	    throw new IllegalArgumentException("The coordinate is out of bounds.");
	}

	@Override
	public int getMinWAtZ(int z) {
	    for (int crossSectionV = crossSectionMinV, crossSectionZ = slope*crossSectionV + zOffsetFromV; crossSectionV <= crossSectionMaxV; crossSectionV++, crossSectionZ += slope) {
	        if (z >= source.getMinYAtVZ(crossSectionV, crossSectionZ)
	                && z <= source.getMaxYAtVZ(crossSectionV, crossSectionZ)) {
	            return crossSectionV;
	        }
	    }
	    throw new IllegalArgumentException("The coordinate is out of bounds.");
	}

	@Override
	public int getMaxWAtZ(int z) {
	    for (int crossSectionV = crossSectionMaxV, crossSectionZ = slope*crossSectionV + zOffsetFromV; crossSectionV >= crossSectionMinV; crossSectionV--, crossSectionZ -= slope) {
	        if (z >= source.getMinYAtVZ(crossSectionV, crossSectionZ)
	                && z <= source.getMaxYAtVZ(crossSectionV, crossSectionZ)) {
	            return crossSectionV;
	        }
	    }
	    throw new IllegalArgumentException("The coordinate is out of bounds.");
	}

	@Override
	public int getMinWAtXY(int x, int y) {
	    for (int crossSectionV = crossSectionMinV, crossSectionZ = slope*crossSectionV + zOffsetFromV; crossSectionV <= crossSectionMaxV; crossSectionV++, crossSectionZ += slope) {
	        if (x >= source.getMinWAtVZ(crossSectionV, crossSectionZ)
	                && x <= source.getMaxWAtVZ(crossSectionV, crossSectionZ)
	                && y >= source.getMinXAtVWZ(crossSectionV, x, crossSectionZ)
	                && y <= source.getMaxXAtVWZ(crossSectionV, x, crossSectionZ)) {
	            return crossSectionV;
	        }
	    }
	    throw new IllegalArgumentException("The coordinates are out of bounds.");
	}

	@Override
	public int getMaxWAtXY(int x, int y) {
	    for (int crossSectionV = crossSectionMaxV, crossSectionZ = slope*crossSectionV + zOffsetFromV; crossSectionV >= crossSectionMinV; crossSectionV--, crossSectionZ -= slope) {
	        if (x >= source.getMinWAtVZ(crossSectionV, crossSectionZ)
	                && x <= source.getMaxWAtVZ(crossSectionV, crossSectionZ)
	                && y >= source.getMinXAtVWZ(crossSectionV, x, crossSectionZ)
	                && y <= source.getMaxXAtVWZ(crossSectionV, x, crossSectionZ)) {
	            return crossSectionV;
	        }
	    }
	    throw new IllegalArgumentException("The coordinates are out of bounds.");
	}

	@Override
	public int getMinWAtXZ(int x, int z) {
	    for (int crossSectionV = crossSectionMinV, crossSectionZ = slope*crossSectionV + zOffsetFromV; crossSectionV <= crossSectionMaxV; crossSectionV++, crossSectionZ += slope) {
	        if (x >= source.getMinWAtVZ(crossSectionV, crossSectionZ)
	                && x <= source.getMaxWAtVZ(crossSectionV, crossSectionZ)
	                && z >= source.getMinYAtVWZ(crossSectionV, x, crossSectionZ)
	                && z <= source.getMaxYAtVWZ(crossSectionV, x, crossSectionZ)) {
	            return crossSectionV;
	        }
	    }
	    throw new IllegalArgumentException("The coordinates are out of bounds.");
	}

	@Override
	public int getMaxWAtXZ(int x, int z) {
	    for (int crossSectionV = crossSectionMaxV, crossSectionZ = slope*crossSectionV + zOffsetFromV; crossSectionV >= crossSectionMinV; crossSectionV--, crossSectionZ -= slope) {
	        if (x >= source.getMinWAtVZ(crossSectionV, crossSectionZ)
	                && x <= source.getMaxWAtVZ(crossSectionV, crossSectionZ)
	                && z >= source.getMinYAtVWZ(crossSectionV, x, crossSectionZ)
	                && z <= source.getMaxYAtVWZ(crossSectionV, x, crossSectionZ)) {
	            return crossSectionV;
	        }
	    }
	    throw new IllegalArgumentException("The coordinates are out of bounds.");
	}

	@Override
	public int getMinWAtYZ(int y, int z) {
	    for (int crossSectionV = crossSectionMinV, crossSectionZ = slope*crossSectionV + zOffsetFromV; crossSectionV <= crossSectionMaxV; crossSectionV++, crossSectionZ += slope) {
	        if (y >= source.getMinXAtVZ(crossSectionV, crossSectionZ)
	                && y <= source.getMaxXAtVZ(crossSectionV, crossSectionZ)
	                && z >= source.getMinYAtVXZ(crossSectionV, y, crossSectionZ)
	                && z <= source.getMaxYAtVXZ(crossSectionV, y, crossSectionZ)) {
	            return crossSectionV;
	        }
	    }
	    throw new IllegalArgumentException("The coordinates are out of bounds.");
	}

	@Override
	public int getMaxWAtYZ(int y, int z) {
	    for (int crossSectionV = crossSectionMaxV, crossSectionZ = slope*crossSectionV + zOffsetFromV; crossSectionV >= crossSectionMinV; crossSectionV--, crossSectionZ -= slope) {
	        if (y >= source.getMinXAtVZ(crossSectionV, crossSectionZ)
	                && y <= source.getMaxXAtVZ(crossSectionV, crossSectionZ)
	                && z >= source.getMinYAtVXZ(crossSectionV, y, crossSectionZ)
	                && z <= source.getMaxYAtVXZ(crossSectionV, y, crossSectionZ)) {
	            return crossSectionV;
	        }
	    }
	    throw new IllegalArgumentException("The coordinates are out of bounds.");
	}

	@Override
	public int getMinW(int x, int y, int z) {
	    for (int crossSectionV = crossSectionMinV, crossSectionZ = slope*crossSectionV + zOffsetFromV; crossSectionV <= crossSectionMaxV; crossSectionV++, crossSectionZ += slope) {
	        if (x >= source.getMinWAtVZ(crossSectionV, crossSectionZ)
	                && x <= source.getMaxWAtVZ(crossSectionV, crossSectionZ)
	                && y >= source.getMinXAtVWZ(crossSectionV, x, crossSectionZ)
	                && y <= source.getMaxXAtVWZ(crossSectionV, x, crossSectionZ)
	                && z >= source.getMinY(crossSectionV, x, y, crossSectionZ)
	                && z <= source.getMaxY(crossSectionV, x, y, crossSectionZ)) {
	            return crossSectionV;
	        }
	    }
	    throw new IllegalArgumentException("The coordinates are out of bounds.");
	}

	@Override
	public int getMaxW(int x, int y, int z) {
	    for (int crossSectionV = crossSectionMaxV, crossSectionZ = slope*crossSectionV + zOffsetFromV; crossSectionV >= crossSectionMinV; crossSectionV--, crossSectionZ -= slope) {
	        if (x >= source.getMinWAtVZ(crossSectionV, crossSectionZ)
	                && x <= source.getMaxWAtVZ(crossSectionV, crossSectionZ)
	                && y >= source.getMinXAtVWZ(crossSectionV, x, crossSectionZ)
	                && y <= source.getMaxXAtVWZ(crossSectionV, x, crossSectionZ)
	                && z >= source.getMinY(crossSectionV, x, y, crossSectionZ)
	                && z <= source.getMaxY(crossSectionV, x, y, crossSectionZ)) {
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
	    return source.getMinWAtVZ(w, slope*w + zOffsetFromV);
	}

	@Override
	public int getMaxXAtW(int w) {
	    return source.getMaxWAtVZ(w, slope*w + zOffsetFromV);
	}

	@Override
	public int getMinXAtY(int y) {
	    int crossSectionV = crossSectionMinV;
	    int crossSectionZ = slope*crossSectionV + zOffsetFromV;
	    int result = source.getMinWAtVXZ(crossSectionV, y, crossSectionZ);
	    int localMinW;
	    for (crossSectionV++, crossSectionZ += slope;
	            crossSectionV <= crossSectionMaxV && (localMinW = source.getMinWAtVXZ(crossSectionV, y, crossSectionZ)) <= result;
	            crossSectionV++, crossSectionZ += slope) {
	        result = localMinW;
	    }
	    return result;
	}

	@Override
	public int getMaxXAtY(int y) {
	    int crossSectionV = crossSectionMinV;
	    int crossSectionZ = slope*crossSectionV + zOffsetFromV;
	    int result = source.getMaxWAtVXZ(crossSectionV, y, crossSectionZ);
	    int localMaxW;
	    for (crossSectionV++, crossSectionZ += slope;
	            crossSectionV <= crossSectionMaxV && (localMaxW = source.getMaxWAtVXZ(crossSectionV, y, crossSectionZ)) >= result;
	            crossSectionV++, crossSectionZ += slope) {
	        result = localMaxW;
	    }
	    return result;
	}

	@Override
	public int getMinXAtZ(int z) {
	    int crossSectionV = crossSectionMinV;
	    int crossSectionZ = slope*crossSectionV + zOffsetFromV;
	    int result = source.getMinWAtVYZ(crossSectionV, z, crossSectionZ);
	    int localMinW;
	    for (crossSectionV++, crossSectionZ += slope;
	            crossSectionV <= crossSectionMaxV && (localMinW = source.getMinWAtVYZ(crossSectionV, z, crossSectionZ)) <= result;
	            crossSectionV++, crossSectionZ += slope) {
	        result = localMinW;
	    }
	    return result;
	}

	@Override
	public int getMaxXAtZ(int z) {
	    int crossSectionV = crossSectionMinV;
	    int crossSectionZ = slope*crossSectionV + zOffsetFromV;
	    int result = source.getMaxWAtVYZ(crossSectionV, z, crossSectionZ);
	    int localMaxW;
	    for (crossSectionV++, crossSectionZ += slope;
	            crossSectionV <= crossSectionMaxV && (localMaxW = source.getMaxWAtVYZ(crossSectionV, z, crossSectionZ)) >= result;
	            crossSectionV++, crossSectionZ += slope) {
	        result = localMaxW;
	    }
	    return result;
	}

	@Override
	public int getMinXAtWY(int w, int y) {
	    return source.getMinWAtVXZ(w, y, slope*w + zOffsetFromV);
	}

	@Override
	public int getMaxXAtWY(int w, int y) {
	    return source.getMaxWAtVXZ(w, y, slope*w + zOffsetFromV);
	}

	@Override
	public int getMinXAtWZ(int w, int z) {
	    return source.getMinWAtVYZ(w, z, slope*w + zOffsetFromV);
	}

	@Override
	public int getMaxXAtWZ(int w, int z) {
	    return source.getMaxWAtVYZ(w, z, slope*w + zOffsetFromV);
	}

	@Override
	public int getMinXAtYZ(int y, int z) {
	    int crossSectionV = crossSectionMinV;
	    int crossSectionZ = slope*crossSectionV + zOffsetFromV;
	    int result = source.getMinW(crossSectionV, y, z, crossSectionZ);
	    int localMinW;
	    for (crossSectionV++, crossSectionZ += slope;
	            crossSectionV <= crossSectionMaxV && (localMinW = source.getMinW(crossSectionV, y, z, crossSectionZ)) <= result;
	            crossSectionV++, crossSectionZ += slope) {
	        result = localMinW;
	    }
	    return result;
	}

	@Override
	public int getMaxXAtYZ(int y, int z) {
	    int crossSectionV = crossSectionMinV;
	    int crossSectionZ = slope*crossSectionV + zOffsetFromV;
	    int result = source.getMaxW(crossSectionV, y, z, crossSectionZ);
	    int localMaxW;
	    for (crossSectionV++, crossSectionZ += slope;
	            crossSectionV <= crossSectionMaxV && (localMaxW = source.getMaxW(crossSectionV, y, z, crossSectionZ)) >= result;
	            crossSectionV++, crossSectionZ += slope) {
	        result = localMaxW;
	    }
	    return result;
	}

	@Override
	public int getMinX(int w, int y, int z) {
	    return source.getMinW(w, y, z, slope*w + zOffsetFromV);
	}

	@Override
	public int getMaxX(int w, int y, int z) {
	    return source.getMaxW(w, y, z, slope*w + zOffsetFromV);
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
	    return source.getMinXAtVZ(w, slope*w + zOffsetFromV);
	}

	@Override
	public int getMaxYAtW(int w) {
	    return source.getMaxXAtVZ(w, slope*w + zOffsetFromV);
	}

	@Override
	public int getMinYAtX(int x) {
	    int crossSectionV = crossSectionMinV;
	    int crossSectionZ = slope*crossSectionV + zOffsetFromV;
	    int result = source.getMinXAtVWZ(crossSectionV, x, crossSectionZ);
	    int localMinX;
	    for (crossSectionV++, crossSectionZ += slope;
	            crossSectionV <= crossSectionMaxV && (localMinX = source.getMinXAtVWZ(crossSectionV, x, crossSectionZ)) <= result;
	            crossSectionV++, crossSectionZ += slope) {
	        result = localMinX;
	    }
	    return result;
	}

	@Override
	public int getMaxYAtX(int x) {
	    int crossSectionV = crossSectionMinV;
	    int crossSectionZ = slope*crossSectionV + zOffsetFromV;
	    int result = source.getMaxXAtVWZ(crossSectionV, x, crossSectionZ);
	    int localMaxX;
	    for (crossSectionV++, crossSectionZ += slope;
	            crossSectionV <= crossSectionMaxV && (localMaxX = source.getMaxXAtVWZ(crossSectionV, x, crossSectionZ)) >= result;
	            crossSectionV++, crossSectionZ += slope) {
	        result = localMaxX;
	    }
	    return result;
	}

	@Override
	public int getMinYAtZ(int z) {
	    int crossSectionV = crossSectionMinV;
	    int crossSectionZ = slope*crossSectionV + zOffsetFromV;
	    int result = source.getMinXAtVYZ(crossSectionV, z, crossSectionZ);
	    int localMinX;
	    for (crossSectionV++, crossSectionZ += slope;
	            crossSectionV <= crossSectionMaxV && (localMinX = source.getMinXAtVYZ(crossSectionV, z, crossSectionZ)) <= result;
	            crossSectionV++, crossSectionZ += slope) {
	        result = localMinX;
	    }
	    return result;
	}

	@Override
	public int getMaxYAtZ(int z) {
	    int crossSectionV = crossSectionMinV;
	    int crossSectionZ = slope*crossSectionV + zOffsetFromV;
	    int result = source.getMaxXAtVYZ(crossSectionV, z, crossSectionZ);
	    int localMaxX;
	    for (crossSectionV++, crossSectionZ += slope;
	            crossSectionV <= crossSectionMaxV && (localMaxX = source.getMaxXAtVYZ(crossSectionV, z, crossSectionZ)) >= result;
	            crossSectionV++, crossSectionZ += slope) {
	        result = localMaxX;
	    }
	    return result;
	}

	@Override
	public int getMinYAtWX(int w, int x) {
	    return source.getMinXAtVWZ(w, x, slope*w + zOffsetFromV);
	}

	@Override
	public int getMaxYAtWX(int w, int x) {
	    return source.getMaxXAtVWZ(w, x, slope*w + zOffsetFromV);
	}

	@Override
	public int getMinYAtWZ(int w, int z) {
	    return source.getMinXAtVYZ(w, z, slope*w + zOffsetFromV);
	}

	@Override
	public int getMaxYAtWZ(int w, int z) {
	    return source.getMaxXAtVYZ(w, z, slope*w + zOffsetFromV);
	}

	@Override
	public int getMinYAtXZ(int x, int z) {
	    int crossSectionV = crossSectionMinV;
	    int crossSectionZ = slope*crossSectionV + zOffsetFromV;
	    int result = source.getMinX(crossSectionV, x, z, crossSectionZ);
	    int localMinX;
	    for (crossSectionV++, crossSectionZ += slope;
	            crossSectionV <= crossSectionMaxV && (localMinX = source.getMinX(crossSectionV, x, z, crossSectionZ)) <= result;
	            crossSectionV++, crossSectionZ += slope) {
	        result = localMinX;
	    }
	    return result;
	}

	@Override
	public int getMaxYAtXZ(int x, int z) {
	    int crossSectionV = crossSectionMinV;
	    int crossSectionZ = slope*crossSectionV + zOffsetFromV;
	    int result = source.getMaxX(crossSectionV, x, z, crossSectionZ);
	    int localMaxX;
	    for (crossSectionV++, crossSectionZ += slope;
	            crossSectionV <= crossSectionMaxV && (localMaxX = source.getMaxX(crossSectionV, x, z, crossSectionZ)) >= result;
	            crossSectionV++, crossSectionZ += slope) {
	        result = localMaxX;
	    }
	    return result;
	}

	@Override
	public int getMinY(int w, int x, int z) {
	    return source.getMinX(w, x, z, slope*w + zOffsetFromV);
	}

	@Override
	public int getMaxY(int w, int x, int z) {
	    return source.getMaxX(w, x, z, slope*w + zOffsetFromV);
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
	    return source.getMinYAtVZ(w, slope*w + zOffsetFromV);
	}

	@Override
	public int getMaxZAtW(int w) {
	    return source.getMaxYAtVZ(w, slope*w + zOffsetFromV);
	}

	@Override
	public int getMinZAtX(int x) {
	    int crossSectionV = crossSectionMinV;
	    int crossSectionZ = slope*crossSectionV + zOffsetFromV;
	    int result = source.getMinYAtVWZ(crossSectionV, x, crossSectionZ);
	    int localMinY;
	    for (crossSectionV++, crossSectionZ += slope;
	            crossSectionV <= crossSectionMaxV && (localMinY = source.getMinYAtVWZ(crossSectionV, x, crossSectionZ)) <= result;
	            crossSectionV++, crossSectionZ += slope) {
	        result = localMinY;
	    }
	    return result;
	}

	@Override
	public int getMaxZAtX(int x) {
	    int crossSectionV = crossSectionMinV;
	    int crossSectionZ = slope*crossSectionV + zOffsetFromV;
	    int result = source.getMaxYAtVWZ(crossSectionV, x, crossSectionZ);
	    int localMaxY;
	    for (crossSectionV++, crossSectionZ += slope;
	            crossSectionV <= crossSectionMaxV && (localMaxY = source.getMaxYAtVWZ(crossSectionV, x, crossSectionZ)) >= result;
	            crossSectionV++, crossSectionZ += slope) {
	        result = localMaxY;
	    }
	    return result;
	}

	@Override
	public int getMinZAtY(int y) {
	    int crossSectionV = crossSectionMinV;
	    int crossSectionZ = slope*crossSectionV + zOffsetFromV;
	    int result = source.getMinYAtVXZ(crossSectionV, y, crossSectionZ);
	    int localMinY;
	    for (crossSectionV++, crossSectionZ += slope;
	            crossSectionV <= crossSectionMaxV && (localMinY = source.getMinYAtVXZ(crossSectionV, y, crossSectionZ)) <= result;
	            crossSectionV++, crossSectionZ += slope) {
	        result = localMinY;
	    }
	    return result;
	}

	@Override
	public int getMaxZAtY(int y) {
	    int crossSectionV = crossSectionMinV;
	    int crossSectionZ = slope*crossSectionV + zOffsetFromV;
	    int result = source.getMaxYAtVXZ(crossSectionV, y, crossSectionZ);
	    int localMaxY;
	    for (crossSectionV++, crossSectionZ += slope;
	            crossSectionV <= crossSectionMaxV && (localMaxY = source.getMaxYAtVXZ(crossSectionV, y, crossSectionZ)) >= result;
	            crossSectionV++, crossSectionZ += slope) {
	        result = localMaxY;
	    }
	    return result;
	}

	@Override
	public int getMinZAtWX(int w, int x) {
	    return source.getMinYAtVWZ(w, x, slope*w + zOffsetFromV);
	}

	@Override
	public int getMaxZAtWX(int w, int x) {
	    return source.getMaxYAtVWZ(w, x, slope*w + zOffsetFromV);
	}

	@Override
	public int getMinZAtWY(int w, int y) {
	    return source.getMinYAtVXZ(w, y, slope*w + zOffsetFromV);
	}

	@Override
	public int getMaxZAtWY(int w, int y) {
	    return source.getMaxYAtVXZ(w, y, slope*w + zOffsetFromV);
	}

	@Override
	public int getMinZAtXY(int x, int y) {
	    int crossSectionV = crossSectionMinV;
	    int crossSectionZ = slope*crossSectionV + zOffsetFromV;
	    int result = source.getMinY(crossSectionV, x, y, crossSectionZ);
	    int localMinY;
	    for (crossSectionV++, crossSectionZ += slope;
	            crossSectionV <= crossSectionMaxV && (localMinY = source.getMinY(crossSectionV, x, y, crossSectionZ)) <= result;
	            crossSectionV++, crossSectionZ += slope) {
	        result = localMinY;
	    }
	    return result;
	}

	@Override
	public int getMaxZAtXY(int x, int y) {
	    int crossSectionV = crossSectionMinV;
	    int crossSectionZ = slope*crossSectionV + zOffsetFromV;
	    int result = source.getMaxY(crossSectionV, x, y, crossSectionZ);
	    int localMaxY;
	    for (crossSectionV++, crossSectionZ += slope;
	            crossSectionV <= crossSectionMaxV && (localMaxY = source.getMaxY(crossSectionV, x, y, crossSectionZ)) >= result;
	            crossSectionV++, crossSectionZ += slope) {
	        result = localMaxY;
	    }
	    return result;
	}

	@Override
	public int getMinZ(int w, int x, int y) {
	    return source.getMinY(w, x, y, slope*w + zOffsetFromV);
	}

	@Override
	public int getMaxZ(int w, int x, int y) {
	    return source.getMaxY(w, x, y, slope*w + zOffsetFromV);
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
	    path.append(source.getVLabel());
	    if (zOffsetFromV < 0) {
	        path.append(zOffsetFromV);
	    } else if (zOffsetFromV > 0) {
	        path.append("+").append(zOffsetFromV);
	    }
	    return path.toString();
	}

	@Override
	public void backUp(String backupPath, String backupName) throws Exception {
	    source.backUp(backupPath, backupName);
	}

}
