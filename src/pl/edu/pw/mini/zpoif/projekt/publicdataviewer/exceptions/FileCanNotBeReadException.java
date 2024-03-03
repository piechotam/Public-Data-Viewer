package pl.edu.pw.mini.zpoif.projekt.publicdataviewer.exceptions;

public class FileCanNotBeReadException extends Exception{
	
        private static final long serialVersionUID = -372635690582320885L;

		public FileCanNotBeReadException() {
            super("Unidentified csv separator. File can not be read.");
        }
}
