package caimgmaker.args;

import com.beust.jcommander.IParameterValidator;
import com.beust.jcommander.ParameterException;

public class GridValidator implements IParameterValidator {
	public void validate(String name, String value)
			throws ParameterException {
		if (!value.matches("^\\d+d_(\\d+|infinite)$")) {
			throw new ParameterException("The value of " + name + " has an incorrect format.");
		}
	}
}