package pl.edu.pw.mini.zpoif.projekt.publicdataviewer.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

import tech.tablesaw.api.Table;
import tech.tablesaw.columns.Column;

public class DisplayPanel extends JPanel {

	private static final long serialVersionUID = -7673681118032612744L;
	
	private JTable dataTable;
	private DefaultTableModel tableModel;
	
	public DisplayPanel() {
		setPreferredSize(new Dimension(600, 600));
		createTableModel();
		dataTable = new JTable(tableModel);
		JScrollPane scrollPane = new JScrollPane(dataTable);
		add(scrollPane, BorderLayout.CENTER);
	}
	
	private void createTableModel() {
		tableModel = new DefaultTableModel();
	}
	
	//Po wcisnieciu przycisku Read wypelnia tabele danymi
	public void fillWithData(Table t) {
		tableModel.setRowCount(0);
		tableModel.setColumnCount(0);
		
		for (Column<?> column : t.columns()) {
			tableModel.addColumn(column.name(), column.asObjectArray());
		}
	}
	
	
}
