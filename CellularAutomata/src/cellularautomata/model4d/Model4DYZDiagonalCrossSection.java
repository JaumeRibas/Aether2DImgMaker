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
package cellularautomata.model4d;

import cellularautomata.model3d.Model3D;

public class Model4DYZDiagonalCrossSection<Source_Type extends Model4D> implements Model3D {

	protected Source_Type source;
	protected int slope;
	protected int zOffsetFromY;
	protected int crossSectionMinW;
	protected int crossSectionMaxW;
	protected int crossSectionMinX;
	protected int crossSectionMaxX;
	protected int crossSectionMinY;
	protected int crossSectionMaxY;

	public Model4DYZDiagonalCrossSection(Source_Type source, boolean positiveSlope, int zOffsetFromY) {
	    this.source = source;
	    this.slope = positiveSlope ? 1 : -1;
	    this.zOffsetFromY = zOffsetFromY;
	    if (!getBounds()) {
	        throw new IllegalArgumentException("The cross section is out of bounds.");
	    }
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
	        crossSectionMinW = source.getMinWAtYZ(y, crossSectionZ);
	        crossSectionMaxW = source.getMaxWAtYZ(y, crossSectionZ);
	        crossSectionMinX = source.getMinXAtYZ(y, crossSectionZ);
	        crossSectionMaxX = source.getMaxXAtYZ(y, crossSectionZ);
	        y++;
	        crossSectionZ += slope;
	        while (y <= maxY && crossSectionZ >= source.getMinZAtY(y) && crossSectionZ <= source.getMaxZAtY(y)) {
	            crossSectionMaxY = y;
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
	public int getMinX() {
	    return crossSectionMinW;
	}

	@Override
	public int getMaxX() {
	    return crossSectionMaxW;
	}

	@Override
	public int getMinXAtY(int y) {
	    int crossSectionY = crossSectionMinY;
	    int crossSectionZ = slope*crossSectionY + zOffsetFromY;
	    int result = source.getMinW(y, crossSectionY, crossSectionZ);
	    int localMinW;
	    for (crossSectionY++, crossSectionZ += slope;
	            crossSectionY <= crossSectionMaxY && (localMinW = source.getMinW(y, crossSectionY, crossSectionZ)) <= result;
	            crossSectionY++, crossSectionZ += slope) {
	        result = localMinW;
	    }
	    return result;
	}

	@Override
	public int getMaxXAtY(int y) {
	    int crossSectionY = crossSectionMinY;
	    int crossSectionZ = slope*crossSectionY + zOffsetFromY;
	    int result = source.getMaxW(y, crossSectionY, crossSectionZ);
	    int localMaxW;
	    for (crossSectionY++, crossSectionZ += slope;
	            crossSectionY <= crossSectionMaxY && (localMaxW = source.getMaxW(y, crossSectionY, crossSectionZ)) >= result;
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
	public int getMinX(int y, int z) {
	    return source.getMinW(y, z, slope*z + zOffsetFromY);
	}

	@Override
	public int getMaxX(int y, int z) {
	    return source.getMaxW(y, z, slope*z + zOffsetFromY);
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
	public int getMinYAtX(int x) {
	    int crossSectionY = crossSectionMinY;
	    int crossSectionZ = slope*crossSectionY + zOffsetFromY;
	    int result = source.getMinX(x, crossSectionY, crossSectionZ);
	    int localMinX;
	    for (crossSectionY++, crossSectionZ += slope;
	            crossSectionY <= crossSectionMaxY && (localMinX = source.getMinX(x, crossSectionY, crossSectionZ)) <= result;
	            crossSectionY++, crossSectionZ += slope) {
	        result = localMinX;
	    }
	    return result;
	}

	@Override
	public int getMaxYAtX(int x) {
	    int crossSectionY = crossSectionMinY;
	    int crossSectionZ = slope*crossSectionY + zOffsetFromY;
	    int result = source.getMaxX(x, crossSectionY, crossSectionZ);
	    int localMaxX;
	    for (crossSectionY++, crossSectionZ += slope;
	            crossSectionY <= crossSectionMaxY && (localMaxX = source.getMaxX(x, crossSectionY, crossSectionZ)) >= result;
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
	public int getMinY(int x, int z) {
	    return source.getMinX(x, z, slope*z + zOffsetFromY);
	}

	@Override
	public int getMaxY(int x, int z) {
	    return source.getMaxX(x, z, slope*z + zOffsetFromY);
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
	public int getMinZ(int x, int y) {
	    for (int crossSectionY = crossSectionMinY, crossSectionZ = slope*crossSectionY + zOffsetFromY; crossSectionY <= crossSectionMaxY; crossSectionY++, crossSectionZ += slope) {
	        if (x >= source.getMinWAtYZ(crossSectionY, crossSectionZ)
	                && x <= source.getMaxWAtYZ(crossSectionY, crossSectionZ)
	                && y >= source.getMinX(x, crossSectionY, crossSectionZ)
	                && y <= source.getMaxX(x, crossSectionY, crossSectionZ)) {
	            return crossSectionY;
	        }
	    }
	    throw new IllegalArgumentException("The coordinates are out of bounds.");
	}

	@Override
	public int getMaxZ(int x, int y) {
	    for (int crossSectionY = crossSectionMaxY, crossSectionZ = slope*crossSectionY + zOffsetFromY; crossSectionY >= crossSectionMinY; crossSectionY--, crossSectionZ -= slope) {
	        if (x >= source.getMinWAtYZ(crossSectionY, crossSectionZ)
	                && x <= source.getMaxWAtYZ(crossSectionY, crossSectionZ)
	                && y >= source.getMinX(x, crossSectionY, crossSectionZ)
	                && y <= source.getMaxX(x, crossSectionY, crossSectionZ)) {
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
	public void backUp(String backupPath, String backupName) throws Exception {
	    source.backUp(backupPath, backupName);
	}

}
