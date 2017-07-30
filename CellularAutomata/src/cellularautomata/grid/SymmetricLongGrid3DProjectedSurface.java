package cellularautomata.grid;

public class SymmetricLongGrid3DProjectedSurface extends LongGrid2D {

	private SymmetricLongGrid3D source;
	private long backgroundValue;
	
	public SymmetricLongGrid3DProjectedSurface(SymmetricLongGrid3D source, long backgroundValue) {
		this.source = source;
		this.backgroundValue = backgroundValue;
	}

	@Override
	public int getMinX() {
		return source.getNonSymmetricMinX();
	}

	@Override
	public int getMaxX() {
		return source.getNonSymmetricMaxX();
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
	public long getValueAt(int x, int y) {
		int sourceX, sourceY, sourceZ;
		sourceY = y;
		sourceZ = x;
		sourceX = source.getMaxX();
		long value = source.getValueAt(sourceX, sourceY, sourceZ);
		while (value == backgroundValue && sourceX > source.getMinX()) {
			sourceX--;
			value = source.getValueAt(sourceX, sourceY, sourceZ);
		}
		return value;
	}
}
