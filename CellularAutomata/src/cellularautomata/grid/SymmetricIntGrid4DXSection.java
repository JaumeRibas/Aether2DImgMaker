package cellularautomata.grid;

public class SymmetricIntGrid4DXSection extends SymmetricIntGrid2D {

	private SymmetricIntGrid4D source;
	private int y;
	private int z;
	
	public SymmetricIntGrid4DXSection(SymmetricIntGrid4D source, int y, int z) {
		this.source = source;
		this.y = y;
		this.z = z;
	}

	@Override
	public int getMinX() {
		return source.getMinW();
	}

	@Override
	public int getMaxX() {
		return source.getMaxW();
	}

	@Override
	public int getMinY() {
		return source.getMinX();
	}

	@Override
	public int getMaxY() {
		return source.getMaxX();
	}

	@Override
	public int getValueAt(int x, int y) {
		return source.getValueAt(x, y, this.y, z);
	}

	@Override
	public int getNonSymmetricMinX() {
		return source.getNonSymmetricMinW();
	}

	@Override
	public int getNonSymmetricMaxX() {
		return source.getNonSymmetricMaxW();
	}

	@Override
	public int getNonSymmetricMinY() {
		return source.getNonSymmetricMinX();
	}

	@Override
	public int getNonSymmetricMaxY() {
		return source.getNonSymmetricMaxX();
	}

	@Override
	public int getNonSymmetricValueAt(int x, int y) {
		return source.getNonSymmetricValueAt(x, y, this.y, z);
	}

}
