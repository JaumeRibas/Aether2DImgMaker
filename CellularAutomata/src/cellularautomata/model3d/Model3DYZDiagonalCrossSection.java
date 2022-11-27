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
package cellularautomata.model3d;

import java.io.FileNotFoundException;
import java.io.IOException;

import cellularautomata.model2d.Model2D;

public class Model3DYZDiagonalCrossSection<Source_Type extends Model3D> implements Model2D {

	protected Source_Type source;
	protected int slope;
	protected int zOffsetFromY;
	protected int crossSectionMinX;
	protected int crossSectionMaxX;
	protected int crossSectionMinY;
	protected int crossSectionMaxY;

	public Model3DYZDiagonalCrossSection(Source_Type source, boolean positiveSlope, int zOffsetFromY) {
	    this.source = source;
	    this.slope = positiveSlope ? 1 : -1;
	    this.zOffsetFromY = zOffsetFromY;
	    if (!getBounds()) {
	        throw new IllegalArgumentException("The cross section is out of bounds.");
	    }
	}

	@Override
	public String getXLabel() {
	    return source.getXLabel();
	}

	@Override
	public String getYLabel() {
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
	        crossSectionMinX = source.getMinX(y, crossSectionZ);
	        crossSectionMaxX = source.getMaxX(y, crossSectionZ);
	        y++;
	        crossSectionZ += slope;
	        while (y <= maxY && crossSectionZ >= source.getMinZAtY(y) && crossSectionZ <= source.getMaxZAtY(y)) {
	            crossSectionMaxY = y;
	            int localMinX = source.getMinX(y, crossSectionZ);
	            if (localMinX < crossSectionMinX) {
	                crossSectionMinX = localMinX;
	            }
	            int localMaxX = source.getMaxX(y, crossSectionZ);
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
	    return crossSectionMinX;
	}

	@Override
	public int getMaxX() {
	    return crossSectionMaxX;
	}

	@Override
	public int getMinX(int y) {
	    return source.getMinX(y, slope*y + zOffsetFromY);
	}

	@Override
	public int getMaxX(int y) {
	    return source.getMaxX(y, slope*y + zOffsetFromY);
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
	public int getMinY(int x) {
	    for (int crossSectionY = crossSectionMinY, crossSectionZ = slope*crossSectionY + zOffsetFromY; crossSectionY <= crossSectionMaxY; crossSectionY++, crossSectionZ += slope) {
	        if (x >= source.getMinX(crossSectionY, crossSectionZ)
	                && x <= source.getMaxX(crossSectionY, crossSectionZ)) {
	            return crossSectionY;
	        }
	    }
	    throw new IllegalArgumentException("The coordinate is out of bounds.");
	}

	@Override
	public int getMaxY(int x) {
	    for (int crossSectionY = crossSectionMaxY, crossSectionZ = slope*crossSectionY + zOffsetFromY; crossSectionY >= crossSectionMinY; crossSectionY--, crossSectionZ -= slope) {
	        if (x >= source.getMinX(crossSectionY, crossSectionZ)
	                && x <= source.getMaxX(crossSectionY, crossSectionZ)) {
	            return crossSectionY;
	        }
	    }
	    throw new IllegalArgumentException("The coordinate is out of bounds.");
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
