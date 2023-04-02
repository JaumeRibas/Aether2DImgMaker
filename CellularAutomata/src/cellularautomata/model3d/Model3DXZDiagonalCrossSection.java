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
package cellularautomata.model3d;

import cellularautomata.model2d.Model2D;

public class Model3DXZDiagonalCrossSection<Source_Type extends Model3D> implements Model2D {

	protected Source_Type source;
	protected int slope;
	protected int zOffsetFromX;
	protected int crossSectionMinX;
	protected int crossSectionMaxX;
	protected int crossSectionMinY;
	protected int crossSectionMaxY;

	public Model3DXZDiagonalCrossSection(Source_Type source, boolean positiveSlope, int zOffsetFromX) {
	    this.source = source;
	    this.slope = positiveSlope ? 1 : -1;
	    this.zOffsetFromX = zOffsetFromX;
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
	        crossSectionMinY = source.getMinY(x, crossSectionZ);
	        crossSectionMaxY = source.getMaxY(x, crossSectionZ);
	        x++;
	        crossSectionZ += slope;
	        while (x <= maxX && crossSectionZ >= source.getMinZAtX(x) && crossSectionZ <= source.getMaxZAtX(x)) {
	            crossSectionMaxX = x;
	            int localMinY = source.getMinY(x, crossSectionZ);
	            if (localMinY < crossSectionMinY) {
	                crossSectionMinY = localMinY;
	            }
	            int localMaxY = source.getMaxY(x, crossSectionZ);
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
	    return crossSectionMinX;
	}

	@Override
	public int getMaxX() {
	    return crossSectionMaxX;
	}

	@Override
	public int getMinX(int y) {
	    for (int crossSectionX = crossSectionMinX, crossSectionZ = slope*crossSectionX + zOffsetFromX; crossSectionX <= crossSectionMaxX; crossSectionX++, crossSectionZ += slope) {
	        if (y >= source.getMinY(crossSectionX, crossSectionZ)
	                && y <= source.getMaxY(crossSectionX, crossSectionZ)) {
	            return crossSectionX;
	        }
	    }
	    throw new IllegalArgumentException("The coordinate is out of bounds.");
	}

	@Override
	public int getMaxX(int y) {
	    for (int crossSectionX = crossSectionMaxX, crossSectionZ = slope*crossSectionX + zOffsetFromX; crossSectionX >= crossSectionMinX; crossSectionX--, crossSectionZ -= slope) {
	        if (y >= source.getMinY(crossSectionX, crossSectionZ)
	                && y <= source.getMaxY(crossSectionX, crossSectionZ)) {
	            return crossSectionX;
	        }
	    }
	    throw new IllegalArgumentException("The coordinate is out of bounds.");
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
	    return source.getMinY(x, slope*x + zOffsetFromX);
	}

	@Override
	public int getMaxY(int x) {
	    return source.getMaxY(x, slope*x + zOffsetFromX);
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
