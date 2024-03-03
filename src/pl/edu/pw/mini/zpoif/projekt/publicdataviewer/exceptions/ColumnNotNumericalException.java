package pl.edu.pw.mini.zpoif.projekt.publicdataviewer.exceptions;

public class ColumnNotNumericalException extends Exception {

	private static final long serialVersionUID = 7754426360233205051L;
	
	public ColumnNotNumericalException() {
		super("This operation is allowed for numerical columns only.");
	}
}
