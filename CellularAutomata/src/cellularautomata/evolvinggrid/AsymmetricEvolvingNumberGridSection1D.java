package cellularautomata.evolvinggrid;

import java.io.FileNotFoundException;
import java.io.IOException;

import org.apache.commons.math3.FieldElement;

import cellularautomata.grid1d.AsymmetricNumberGridSection1D;

public class AsymmetricEvolvingNumberGridSection1D<T extends FieldElement<T> & Comparable<T>> 
	extends AsymmetricNumberGridSection1D<T, SymmetricEvolvingNumberGrid1D<T>> implements EvolvingNumberGrid1D<T> {

	public AsymmetricEvolvingNumberGridSection1D(SymmetricEvolvingNumberGrid1D<T> source) {
		super(source);
	}

	@Override
	public boolean nextStep() throws Exception {
		return source.nextStep();
	}

	@Override
	public long getStep() {
		return source.getStep();
	}

	@Override
	public String getName() {
		return source.getName() + "_asymmetric_section";
	}

	@Override
	public String getSubFolderPath() {
		return source.getSubFolderPath() + "/asymmetric_section";
	}

	@Override
	public void backUp(String backupPath, String backupName) throws FileNotFoundException, IOException {
		source.backUp(backupPath, backupName);
	}

}
