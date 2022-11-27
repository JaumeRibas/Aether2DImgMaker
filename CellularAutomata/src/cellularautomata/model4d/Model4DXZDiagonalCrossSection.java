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
package cellularautomata.model4d;

import java.io.FileNotFoundException;
import java.io.IOException;

import cellularautomata.model3d.Model3D;

public class Model4DXZDiagonalCrossSection<Source_Type extends Model4D> implements Model3D {

	protected Source_Type source;
	protected int slope;
	protected int zOffsetFromX;
	protected int crossSectionMinW;
	protected int crossSectionMaxW;
	protected int crossSectionMinX;
	protected int crossSectionMaxX;
	protected int crossSectionMinY;
	protected int crossSectionMaxY;

	public Model4DXZDiagonalCrossSection(Source_Type source, boolean positiveSlope, int zOffsetFromX) {
	    this.source = source;
	    this.slope = positiveSlope ? 1 : -1;
	    this.zOffsetFromX = zOffsetFromX;
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
	        crossSectionMinW = source.getMinWAtXZ(x, crossSectionZ);
	        crossSectionMaxW = source.getMaxWAtXZ(x, crossSectionZ);
	        crossSectionMinY = source.getMinYAtXZ(x, crossSectionZ);
	        crossSectionMaxY = source.getMaxYAtXZ(x, crossSectionZ);
	        x++;
	        crossSectionZ += slope;
	        while (x <= maxX && crossSectionZ >= source.getMinZAtX(x) && crossSectionZ <= source.getMaxZAtX(x)) {
	            crossSectionMaxX = x;
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
	public int getMinX() {
	    return crossSectionMinW;
	}

	@Override
	public int getMaxX() {
	    return crossSectionMaxW;
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
	    int result = source.getMinW(crossSectionX, z, crossSectionZ);
	    int localMinW;
	    for (crossSectionX++, crossSectionZ += slope;
	            crossSectionX <= crossSectionMaxX && (localMinW = source.getMinW(crossSectionX, z, crossSectionZ)) <= result;
	            crossSectionX++, crossSectionZ += slope) {
	        result = localMinW;
	    }
	    return result;
	}

	@Override
	public int getMaxXAtZ(int z) {
	    int crossSectionX = crossSectionMinX;
	    int crossSectionZ = slope*crossSectionX + zOffsetFromX;
	    int result = source.getMaxW(crossSectionX, z, crossSectionZ);
	    int localMaxW;
	    for (crossSectionX++, crossSectionZ += slope;
	            crossSectionX <= crossSectionMaxX && (localMaxW = source.getMaxW(crossSectionX, z, crossSectionZ)) >= result;
	            crossSectionX++, crossSectionZ += slope) {
	        result = localMaxW;
	    }
	    return result;
	}

	@Override
	public int getMinX(int y, int z) {
	    return source.getMinW(y, z, slope*y + zOffsetFromX);
	}

	@Override
	public int getMaxX(int y, int z) {
	    return source.getMaxW(y, z, slope*y + zOffsetFromX);
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
	public int getMinY(int x, int z) {
	    for (int crossSectionX = crossSectionMinX, crossSectionZ = slope*crossSectionX + zOffsetFromX; crossSectionX <= crossSectionMaxX; crossSectionX++, crossSectionZ += slope) {
	        if (x >= source.getMinWAtXZ(crossSectionX, crossSectionZ)
	                && x <= source.getMaxWAtXZ(crossSectionX, crossSectionZ)
	                && z >= source.getMinY(x, crossSectionX, crossSectionZ)
	                && z <= source.getMaxY(x, crossSectionX, crossSectionZ)) {
	            return crossSectionX;
	        }
	    }
	    throw new IllegalArgumentException("The coordinates are out of bounds.");
	}

	@Override
	public int getMaxY(int x, int z) {
	    for (int crossSectionX = crossSectionMaxX, crossSectionZ = slope*crossSectionX + zOffsetFromX; crossSectionX >= crossSectionMinX; crossSectionX--, crossSectionZ -= slope) {
	        if (x >= source.getMinWAtXZ(crossSectionX, crossSectionZ)
	                && x <= source.getMaxWAtXZ(crossSectionX, crossSectionZ)
	                && z >= source.getMinY(x, crossSectionX, crossSectionZ)
	                && z <= source.getMaxY(x, crossSectionX, crossSectionZ)) {
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
	public int getMinZAtX(int x) {
	    int crossSectionX = crossSectionMinX;
	    int crossSectionZ = slope*crossSectionX + zOffsetFromX;
	    int result = source.getMinY(x, crossSectionX, crossSectionZ);
	    int localMinY;
	    for (crossSectionX++, crossSectionZ += slope;
	            crossSectionX <= crossSectionMaxX && (localMinY = source.getMinY(x, crossSectionX, crossSectionZ)) <= result;
	            crossSectionX++, crossSectionZ += slope) {
	        result = localMinY;
	    }
	    return result;
	}

	@Override
	public int getMaxZAtX(int x) {
	    int crossSectionX = crossSectionMinX;
	    int crossSectionZ = slope*crossSectionX + zOffsetFromX;
	    int result = source.getMaxY(x, crossSectionX, crossSectionZ);
	    int localMaxY;
	    for (crossSectionX++, crossSectionZ += slope;
	            crossSectionX <= crossSectionMaxX && (localMaxY = source.getMaxY(x, crossSectionX, crossSectionZ)) >= result;
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
	public int getMinZ(int x, int y) {
	    return source.getMinY(x, y, slope*y + zOffsetFromX);
	}

	@Override
	public int getMaxZ(int x, int y) {
	    return source.getMaxY(x, y, slope*y + zOffsetFromX);
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
	public void backUp(String backupPath, String backupName) throws FileNotFoundException, IOException {
	    source.backUp(backupPath, backupName);
	}

}
