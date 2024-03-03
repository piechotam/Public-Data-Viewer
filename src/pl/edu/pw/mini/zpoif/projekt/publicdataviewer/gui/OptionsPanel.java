package pl.edu.pw.mini.zpoif.projekt.publicdataviewer.gui;

import java.awt.event.ItemEvent;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JTextField;

import pl.edu.pw.mini.zpoif.projekt.publicdataviewer.parser.CsvParser;
import tech.tablesaw.api.Table;

public class OptionsPanel extends JPanel {
	
	private static final long serialVersionUID = 7288022260788851702L;
	
	private JComboBox<String> columnsComboBox = new JComboBox<String>();
	private DisplayPanel displayPanel;
	private boolean isDescending;
	
	public OptionsPanel(DisplayPanel displayPanel) {
    	this.displayPanel = displayPanel;
		JTextField sortText = new JTextField("Sort by: ");
    	sortText.setEditable(false);
    	
    	JCheckBox chooseDescending = new JCheckBox("Descending");
    	chooseDescending.setSelected(true);
    	chooseDescending.addItemListener(e -> {
    		if (e.getSource() == chooseDescending) {
    			isDescending = (e.getStateChange() == ItemEvent.DESELECTED);
    		}
    	});
    	
    	add(sortText);
    	add(columnsComboBox);
    	add(chooseDescending);
	}
	
	//po wcisnieciu Read w DisplayPanel odswieza liste nazw kolumn
	public void updateJComboBox(List<String> columnNames) {
		columnsComboBox.removeAllItems();
		for (String string : columnNames) {
			columnsComboBox.addItem(string);
		}
	}
	
	public void enableSorting(Table t) {
		columnsComboBox.addActionListener(e -> {
			if (e.getSource() == columnsComboBox) {
				String chosenColumn = (String) columnsComboBox.getSelectedItem();
	    		if (chosenColumn != null) {
	    			displayPanel.fillWithData(CsvParser.sortByColumn(t, chosenColumn, isDescending));	
	    		}
			}
    	});
	}
}
