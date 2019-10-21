package cellularautomata.grid4d;

public class NonsymmetricIntGridSection4D extends NonsymmetricGridSection4D implements IntGrid4D {
	
	public NonsymmetricIntGridSection4D(SymmetricIntGrid4D grid) {
		super(grid);
	}

	@Override
	public int getValueAtPosition(int w, int x, int y, int z) throws Exception {
		return ((SymmetricIntGrid4D) grid).getValueAtNonsymmetricPosition(w, x, y, z);
	}

}
