package cellularautomata.grid;

public class SymmetricShortGrid3DProjectedSurface extends ShortGrid2D {

	private SymmetricShortGrid3D source;
	private short backgroundValue;
	
	public SymmetricShortGrid3DProjectedSurface(SymmetricShortGrid3D source, short backgroundValue) {
		this.source = source;
		this.backgroundValue = backgroundValue;
	}

	@Override
	public int getMinX() {
		return source.getNonSymmetricMinZ();
	}

	@Override
	public int getMaxX() {
		return source.getNonSymmetricMaxZ();
	}

	@Override
	public int getMinY() {
		return source.getNonSymmetricMinY();
	}

	@Override
	public int getMaxY() {
		return source.getNonSymmetricMaxY();
	}

	@Override
	public short getValueAt(int x, int y) {
		int sourceX, sourceY, sourceZ, minX;
		sourceY = y;
		sourceZ = x;
		sourceX = source.getMaxX();
		minX = source.getMinX();
		short value = source.getValueAt(sourceX, sourceY, sourceZ);
		while (value == backgroundValue && sourceX > minX) {
			sourceX--;
			value = source.getValueAt(sourceX, sourceY, sourceZ);
		}
		return value;
	}
}
