package pl.edu.pw.mini.zpoif.projekt.publicdataviewer.gui;

import javax.swing.*;
import javax.swing.JComboBox;

import pl.edu.pw.mini.zpoif.projekt.publicdataviewer.exceptions.ColumnNotNumericalException;
import pl.edu.pw.mini.zpoif.projekt.publicdataviewer.exceptions.FileCanNotBeReadException;
import pl.edu.pw.mini.zpoif.projekt.publicdataviewer.export.TableExporter;
import pl.edu.pw.mini.zpoif.projekt.publicdataviewer.parser.CsvReader;
import pl.edu.pw.mini.zpoif.projekt.publicdataviewer.parser.CsvParser;
import pl.edu.pw.mini.zpoif.projekt.publicdataviewer.plot.PlotGenerator;
import tech.tablesaw.api.ColumnType;
import tech.tablesaw.api.DateColumn;
import tech.tablesaw.api.Table;

import java.awt.*;
import java.net.MalformedURLException;
import java.time.LocalDate;
import java.util.stream.Collectors;

public class MyFrame extends JFrame {
    private static final long serialVersionUID = -5430580973844446733L;

    private DisplayPanel displayPanel;
    private OptionsPanel optionsPanel;
    private Table t;
	private boolean isRead = false;

	public MyFrame(){
        setTitle("Public CSV Data Viewer");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);
        getContentPane().setBackground(new Color(210, 255, 230));
        setSize(500, 800);
        // dodać logo aplikacj
        // ImageIcon image = new ImageIcon("logo.png")
        // this.setIconImage(image.getImage());

        displayPanel = new DisplayPanel();
        optionsPanel = new OptionsPanel(displayPanel);

		// dodanie menu
		add(createMenu(), BorderLayout.NORTH);
        add(createMainPanel());
        pack();
    }
	
    //tworzy glowny panel
    //tutaj wywolywane sa metody tworzace pozostale panele
    private JPanel createMainPanel() {
    	JPanel result = new JPanel();


    	result.setLayout(new BoxLayout(result, BoxLayout.Y_AXIS));

    	result.add(createUrlInputPanel());
    	result.add(createDisplayPanel(displayPanel));
    	result.add(createOptionsPanel(optionsPanel));

    	return result;
    }


    private JPanel createDisplayPanel(DisplayPanel displayPanel) {
    	JPanel result = new JPanel();

    	result.add(displayPanel);

    	return result;
    }

    //panel w ktory jest pole tekstowe i przycisk Read
    //uzytkownik wpisuje url do pola tekstowego i naciska przycisk, program zczytuje csv z linku

    private JPanel createUrlInputPanel() {
    	JPanel result = new JPanel();

    	JTextField textField = new JTextField("Please enter your url");
    	textField.setPreferredSize(new Dimension(500, 20));

    	JButton readButton = new JButton("Read");

    	readButton.addActionListener(e -> {
    		if (e.getSource() == readButton) {
    			String url = textField.getText();
    			try {
    				t = CsvReader.readCsvFromURL(url);
    			} catch (MalformedURLException ex) {
    				JOptionPane.showMessageDialog(this, "Invalid URL. Please try again.");
    			} catch (FileCanNotBeReadException ex) {
					JOptionPane.showMessageDialog(this, "Unidentified csv separator. File can not be read.");

				}

				displayPanel.fillWithData(t);
        		optionsPanel.updateJComboBox(t.columnNames());
        		optionsPanel.enableSorting(t);

    			isRead = true;
    		}
    	});

    	result.add(textField);
    	result.add(readButton);

    	return result;
    }

    //panel z przyciskami do sortowania filtrowania itd.
    private JPanel createOptionsPanel(OptionsPanel optionsPanel) {
    	JPanel result = new JPanel();

    	result.add(optionsPanel);

    	return result;
    }

	// tworzy menu
	private	JMenuBar createMenu(){

		// tworzy menu bar i dodaje opcje
		JMenuBar menuBar = new JMenuBar();

		JMenu menuExport = new JMenu("Export");
		JMenuItem menuItemExportToCsv = new JMenuItem("Export to CSV");
		JMenu menuBasicInfo = new JMenu("Basic Info");
		JMenuItem menuItemBasicInfo = new JMenuItem("Basic Info");
		JMenu menuPlot = new JMenu("Plot");
		JMenuItem menuItemPlot = new JMenuItem("Histogram");
		JMenuItem menuItemPlot2 = new JMenuItem("Scatter Plot");
		JMenu menuStatistics = new JMenu("Statistics");
		JMenuItem menuItemStatisticsMean = new JMenuItem("Mean");
		JMenuItem menuItemStatisticsMedian = new JMenuItem("Median");
		JMenuItem menuItemStatisticsStandardDeviation = new JMenuItem("Standard Deviation");
		JMenuItem menuItemStatisticsVariance = new JMenuItem("Variance");
		JMenu menuAggregate = new JMenu("Aggregate");
		JMenuItem menuItemAggregateMax = new JMenuItem("Max");
		JMenuItem menuItemAggregateMin = new JMenuItem("Min");
		JMenuItem menuItemAggregateSum = new JMenuItem("Sum");
		JMenu menuFilter = new JMenu("Filter");
		JMenuItem menuItemFilterInterval = new JMenuItem("Filter rows by interval");
		JMenuItem menuItemFilterDates = new JMenuItem("Filter rows by dates");
		JMenu menuFind = new JMenu("Find");
		JMenuItem menuItemFind = new JMenuItem("Find values in rows");


		menuExport.add(menuItemExportToCsv);

		menuBasicInfo.add(menuItemBasicInfo);

		menuPlot.add(menuItemPlot);
		menuPlot.add(menuItemPlot2);

		menuAggregate.add(menuItemAggregateMax);
		menuAggregate.add(menuItemAggregateMin);
		menuStatistics.add(menuItemStatisticsMean);
		menuAggregate.add(menuItemAggregateSum);
		menuStatistics.add(menuItemStatisticsMedian);
		menuStatistics.add(menuItemStatisticsStandardDeviation);
		menuStatistics.add(menuItemStatisticsVariance);

		menuFilter.add(menuItemFilterInterval);
		menuFilter.add(menuItemFilterDates);

		menuFind.add(menuItemFind);

		// dodaje w odpowiedniej kolejności przyciski
		menuBar.add(menuBasicInfo);
		menuBar.add(menuFind);
		menuBar.add(menuFilter);
		menuBar.add(menuPlot);
		menuBar.add(menuStatistics);
		menuBar.add(menuAggregate);
		menuBar.add(menuExport);

		menuItemFilterDates.addActionListener(e -> {
			if (isRead) {
				String[] columnNames = t.columnNames().toArray(new String[0]);
				ColumnType[] columnTypes = t.columnTypes();
				boolean isDateColumn = false;
				for (int i = 0; i < columnTypes.length; i++) {
					if (columnTypes[i].equals(ColumnType.LOCAL_DATE) || columnTypes[i].equals(ColumnType.LOCAL_DATE_TIME) || columnTypes[i].equals(ColumnType.LOCAL_TIME)) {
						isDateColumn = true;
						break;
					}
				}
				if (columnNames.length == 0 || !isDateColumn){
					JOptionPane.showMessageDialog(this, "No columns available for filtering");
				} else {
					// wybor kolumny i przedzialu
					JPanel panel = new JPanel(new GridLayout(4, 2));

					JComboBox<String> columnComboBox = new JComboBox<>(columnNames);
					JComboBox<LocalDate> minDateComboBox = new JComboBox<>(t.dateColumn(columnNames[0]).asList().toArray(new LocalDate[0]));
					JComboBox<LocalDate> maxDateComboBox = new JComboBox<>(t.dateColumn(columnNames[0]).asList().toArray(new LocalDate[0]));

					columnComboBox.addActionListener(e1 -> {
						String chosenColumn = (String) columnComboBox.getSelectedItem();
						if (chosenColumn != null) {
							DateColumn dateColumn = t.dateColumn(chosenColumn);
							minDateComboBox.setModel(new DefaultComboBoxModel<>(dateColumn.asList().stream().distinct().collect(Collectors.toList()).toArray(new LocalDate[0])));
							maxDateComboBox.setModel(new DefaultComboBoxModel<>(dateColumn.asList().stream().distinct().collect(Collectors.toList()).toArray(new LocalDate[0])));
						}
					});

					panel.add(new JLabel("Column:"));
					panel.add(columnComboBox);
					panel.add(new JLabel("Min bound:"));
					panel.add(minDateComboBox);
					panel.add(new JLabel("Max bound:"));
					panel.add(maxDateComboBox);

					int result = JOptionPane.showConfirmDialog(this, panel, "Select Axes", JOptionPane.OK_CANCEL_OPTION);

					if (result == JOptionPane.OK_OPTION) {
						String chosenColumn = (String) columnComboBox.getSelectedItem();
						LocalDate leftBound = (LocalDate) minDateComboBox.getSelectedItem();
						LocalDate rightBound = (LocalDate) maxDateComboBox.getSelectedItem();

						// Check if the user selected a column
						if (chosenColumn != null) {
							if (leftBound != null && rightBound != null && rightBound.isAfter(leftBound)) {
								displayPanel.fillWithData(CsvParser.filterByIntervalDate(t, chosenColumn, leftBound, rightBound));
							} else {
								JOptionPane.showMessageDialog(this, "Invalid date interval. Please select valid dates.");
							}
						} else {
							JOptionPane.showMessageDialog(this, "No columns selected for filtering");
						}
					}
				}
			} else {
				JOptionPane.showMessageDialog(this, "Please read data first");
			}
		});

		// akcje po kliknięciu find
		menuItemFind.addActionListener(e -> {
			if (isRead) {
				String[] columnNames = t.columnNames().toArray(new String[0]);
				if (columnNames.length == 0) {
					JOptionPane.showMessageDialog(this, "No columns available for filtering");
				} else {
					// wyswieetla okienko do wyboru kolumny
					String chosenColumn = (String) JOptionPane.showInputDialog(
							this,
							"Choose a column for filtering:",
							"Select Column",
							JOptionPane.QUESTION_MESSAGE,
							null,
							columnNames,
							columnNames[0]);

					// sprawdza czy uzytkownik wybral kolumne
					if (chosenColumn != null) {
						// wyswietla okienko do wpisania wartosci
						String value = JOptionPane.showInputDialog(this, "Enter value to find in column " + chosenColumn + ":");
						if (value != null) {
							displayPanel.fillWithData(CsvParser.filter(t, chosenColumn, value));
						} else {
							JOptionPane.showMessageDialog(this, "No value entered");
						}
					} else {
						JOptionPane.showMessageDialog(this, "No column selected for filtering");
					}
				}
			} else {
				JOptionPane.showMessageDialog(this, "Please read data first");
			}
		});

		// akcje po kliknięciu filter
		menuItemFilterInterval.addActionListener(e -> {
			if (isRead) {
				String[] columnNames = t.columnNames().toArray(new String[0]);
				if (columnNames.length == 0) {
					JOptionPane.showMessageDialog(this, "No columns available for filtering");
				} else {
					// wybor kolumny i przedzialu
					JPanel panel = new JPanel(new GridLayout(3, 2));

					JComboBox<String> xComboBox = new JComboBox<>(columnNames);
					JTextField leftBoundTextField = new JTextField("");
					JTextField rightBoundTextField = new JTextField("");

					panel.add(new JLabel("Column:"));
					panel.add(xComboBox);
					panel.add(new JLabel("Left bound:"));
					panel.add(leftBoundTextField);
					panel.add(new JLabel("Right bound:"));
					panel.add(rightBoundTextField);

					int result = JOptionPane.showConfirmDialog(this, panel, "Select Bounds", JOptionPane.OK_CANCEL_OPTION);

					if (result == JOptionPane.OK_OPTION) {
						String chosenColumn = (String) xComboBox.getSelectedItem();
						String leftBound = leftBoundTextField.getText();
						String rightBound = rightBoundTextField.getText();

						// sprawdza czy uzytkownik wybral kolumne
						if (chosenColumn != null) {
							if(CsvParser.isColumnNumerical(t.column(chosenColumn).type())) {
								try {
									double leftBoundDouble = Double.parseDouble(leftBound);
									double rightBoundDouble = Double.parseDouble(rightBound);
									displayPanel.fillWithData(CsvParser.filterByInterval(t, chosenColumn, leftBoundDouble, rightBoundDouble));
								} catch (NumberFormatException ex) {
									JOptionPane.showMessageDialog(this, "Invalid bounds. Please try again.");
								}
							}
							else
								JOptionPane.showMessageDialog(this, "Filtering is available only for numerical value columns.");
						} else {
							JOptionPane.showMessageDialog(this, "No columns selected for filtering");
						}
					}
				}
			} else {
				JOptionPane.showMessageDialog(this, "Please read data first");
			}
		});

		// akcje po kliknięciu eksport
		menuItemExportToCsv.addActionListener(e -> {
			if (isRead) {
				JFileChooser fileChooser = new JFileChooser();
				fileChooser.setDialogTitle("Specify location to save CSV file");
				fileChooser.showSaveDialog(null);
				String path = fileChooser.getSelectedFile().getAbsolutePath();
				TableExporter.exportCSV(t, path);
			}
			else {
				JOptionPane.showMessageDialog(this, "Please read data first");
			}

		});
		// akcje po kliknięciu basic info
		menuItemBasicInfo.addActionListener(e -> {
			if (isRead) {
				JOptionPane.showMessageDialog(this, CsvParser.getBasicTableInfoString(t), "Basic Info", JOptionPane.INFORMATION_MESSAGE);
			}
			else {
				JOptionPane.showMessageDialog(this, "Please read data first");
			}

		});
		// akcje po kliknięciu histogram
		menuItemPlot.addActionListener(e -> {
			if (isRead) {
				String[] columnNames = t.columnNames().toArray(new String[0]);
				if (columnNames.length == 0) {
					JOptionPane.showMessageDialog(this, "No columns available for plotting");
				} else {
					// wyswieetla okienko do wyboru kolumny
					String chosenColumn = (String) JOptionPane.showInputDialog(
							this,
							"Choose a column for plotting:",
							"Select Column",
							JOptionPane.QUESTION_MESSAGE,
							null,
							columnNames,
							columnNames[0]);

					// sprawdza czy uzytkownik wybral kolumne
					if (chosenColumn != null) {
						try {
							PlotGenerator.generateHistogram(t, chosenColumn);
						} catch (ColumnNotNumericalException e1) {
							JOptionPane.showMessageDialog(this, "Histograms are available only for numerical value columns.");
						}
							
					} else {
						JOptionPane.showMessageDialog(this, "No column selected for plotting");
					}
				}
			} else {
				JOptionPane.showMessageDialog(this, "Please read data first");
			}
		});
		// akcje po kliknięciu scatter plot
		menuItemPlot2.addActionListener(e -> {
			if (isRead) {
				String[] columnNames = t.columnNames().toArray(new String[0]);
				if (columnNames.length == 0) {
					JOptionPane.showMessageDialog(this, "No columns available for plotting");
				} else {
					// wybor osi
					JPanel panel = new JPanel(new GridLayout(2, 2));

					JComboBox<String> xComboBox = new JComboBox<>(columnNames);
					JComboBox<String> yComboBox = new JComboBox<>(columnNames);

					panel.add(new JLabel("X-Axis:"));
					panel.add(xComboBox);
					panel.add(new JLabel("Y-Axis:"));
					panel.add(yComboBox);

					int result = JOptionPane.showConfirmDialog(this, panel, "Select Axes", JOptionPane.OK_CANCEL_OPTION);

					if (result == JOptionPane.OK_OPTION) {
						String chosenColumn1 = (String) xComboBox.getSelectedItem();
						String chosenColumn2 = (String) yComboBox.getSelectedItem();

						// sprawdza czy uzytkownik wybral kolumne
						if (chosenColumn1 != null && chosenColumn2 != null) {
							if (CsvParser.isColumnNumerical(t.column(chosenColumn1).type()) && CsvParser.isColumnNumerical(t.column(chosenColumn2).type())) {
								PlotGenerator.generateScatter2D(t, chosenColumn1, chosenColumn2);
							} else {
								JOptionPane.showMessageDialog(this, "Scatter plots are available only for numerical value columns.");
							}
						} else {
							JOptionPane.showMessageDialog(this, "No columns selected for plotting");
						}
					}
				}
			} else {
				JOptionPane.showMessageDialog(this, "Please read data first");
			}
		});

		// akcje po kliknięciu max
		menuItemAggregateMax.addActionListener( e -> {
			if (isRead) {
				String[] columnNames = t.columnNames().toArray(new String[0]);
				if (columnNames.length == 0) {
					JOptionPane.showMessageDialog(this, "No columns available for statistics");
				} else {
					// wyswieetla okienko do wyboru kolumny
					String chosenColumn = (String) JOptionPane.showInputDialog(
							this,
							"Choose a column for statistics:",
							"Select Column",
							JOptionPane.QUESTION_MESSAGE,
							null,
							columnNames,
							columnNames[0]);

					// sprawdza czy uzytkownik wybral kolumne
					if (chosenColumn != null) {
						if(CsvParser.isColumnNumerical(t.column(chosenColumn).type()))
							JOptionPane.showMessageDialog(this, "Max value in column " + chosenColumn +
									" is " + CsvParser.getStatisticsForNumericalColumns(t, chosenColumn, CsvParser.StatisticsType.MAX), "Statistics - Max", JOptionPane.INFORMATION_MESSAGE);
						else
							JOptionPane.showMessageDialog(this, "Statistics are available only for numerical value columns.");
					} else {
						JOptionPane.showMessageDialog(this, "No column selected for statistics");
					}
				}
			} else {
				JOptionPane.showMessageDialog(this, "Please read data first");
			}
		});

		// akcje po kliknięciu min
		menuItemAggregateMin.addActionListener( e -> {
			if (isRead) {
				String[] columnNames = t.columnNames().toArray(new String[0]);
				if (columnNames.length == 0) {
					JOptionPane.showMessageDialog(this, "No columns available for statistics");
				} else {
					// wyswieetla okienko do wyboru kolumny
					String chosenColumn = (String) JOptionPane.showInputDialog(
							this,
							"Choose a column for statistics:",
							"Select Column",
							JOptionPane.QUESTION_MESSAGE,
							null,
							columnNames,
							columnNames[0]);

					// sprawdza czy uzytkownik wybral kolumne
					if (chosenColumn != null) {
						if(CsvParser.isColumnNumerical(t.column(chosenColumn).type()))
							JOptionPane.showMessageDialog(this, "Min value in column " + chosenColumn +
									" is " + CsvParser.getStatisticsForNumericalColumns(t, chosenColumn, CsvParser.StatisticsType.MIN), "Statistics - Min", JOptionPane.INFORMATION_MESSAGE);
						else
							JOptionPane.showMessageDialog(this, "Statistics are available only for numerical value columns.");
					} else {
						JOptionPane.showMessageDialog(this, "No column selected for statistics");
					}
				}
			} else {
				JOptionPane.showMessageDialog(this, "Please read data first");
			}
		});

		// akcje po kliknięciu mean
		menuItemStatisticsMean.addActionListener( e -> {
			if (isRead) {
				String[] columnNames = t.columnNames().toArray(new String[0]);
				if (columnNames.length == 0) {
					JOptionPane.showMessageDialog(this, "No columns available for statistics");
				} else {
					// wyswieetla okienko do wyboru kolumny
					String chosenColumn = (String) JOptionPane.showInputDialog(
							this,
							"Choose a column for statistics:",
							"Select Column",
							JOptionPane.QUESTION_MESSAGE,
							null,
							columnNames,
							columnNames[0]);

					// sprawdza czy uzytkownik wybral kolumne
					if (chosenColumn != null) {
						if(CsvParser.isColumnNumerical(t.column(chosenColumn).type()))
							JOptionPane.showMessageDialog(this, "Mean value in column " + chosenColumn +
									" is " + CsvParser.getStatisticsForNumericalColumns(t, chosenColumn, CsvParser.StatisticsType.MEAN), "Statistics - Mean", JOptionPane.INFORMATION_MESSAGE);
						else
							JOptionPane.showMessageDialog(this, "Statistics are available only for numerical value columns.");
					} else {
						JOptionPane.showMessageDialog(this, "No column selected for statistics");
					}
				}
			} else {
				JOptionPane.showMessageDialog(this, "Please read data first");
			}
		});
		// akcje po kliknięciu sum
		menuItemAggregateSum.addActionListener( e -> {
			if (isRead) {
				String[] columnNames = t.columnNames().toArray(new String[0]);
				if (columnNames.length == 0) {
					JOptionPane.showMessageDialog(this, "No columns available for statistics");
				} else {
					// wyswieetla okienko do wyboru kolumny
					String chosenColumn = (String) JOptionPane.showInputDialog(
							this,
							"Choose a column for statistics:",
							"Select Column",
							JOptionPane.QUESTION_MESSAGE,
							null,
							columnNames,
							columnNames[0]);

					// sprawdza czy uzytkownik wybral kolumne
					if (chosenColumn != null) {
						if(CsvParser.isColumnNumerical(t.column(chosenColumn).type()))
							JOptionPane.showMessageDialog(this, "Sum value in column " + chosenColumn +
									" is " + CsvParser.getStatisticsForNumericalColumns(t, chosenColumn, CsvParser.StatisticsType.SUM), "Statistics - Sum", JOptionPane.INFORMATION_MESSAGE);
						else
							JOptionPane.showMessageDialog(this, "Statistics are available only for numerical value columns.");
					} else {
						JOptionPane.showMessageDialog(this, "No column selected for statistics");
					}
				}
			} else {
				JOptionPane.showMessageDialog(this, "Please read data first");
			}
		});
		menuItemStatisticsMedian.addActionListener( e -> {
			if (isRead) {
				String[] columnNames = t.columnNames().toArray(new String[0]);
				if (columnNames.length == 0) {
					JOptionPane.showMessageDialog(this, "No columns available for statistics");
				} else {
					// wyswieetla okienko do wyboru kolumny
					String chosenColumn = (String) JOptionPane.showInputDialog(
							this,
							"Choose a column for statistics:",
							"Select Column",
							JOptionPane.QUESTION_MESSAGE,
							null,
							columnNames,
							columnNames[0]);

					// sprawdza czy uzytkownik wybral kolumne
					if (chosenColumn != null) {
						if(CsvParser.isColumnNumerical(t.column(chosenColumn).type()))
							JOptionPane.showMessageDialog(this, "Median value in column " + chosenColumn +
									" is " + CsvParser.getStatisticsForNumericalColumns(t, chosenColumn, CsvParser.StatisticsType.MEDIAN), "Statistics - Median", JOptionPane.INFORMATION_MESSAGE);
						else
							JOptionPane.showMessageDialog(this, "Statistics are available only for numerical value columns.");
					} else {
						JOptionPane.showMessageDialog(this, "No column selected for statistics");
					}
				}
			} else {
				JOptionPane.showMessageDialog(this, "Please read data first");
			}
		});

		// akcje po kliknięciu standard deviation
		menuItemStatisticsStandardDeviation.addActionListener( e -> {
			if (isRead) {
				String[] columnNames = t.columnNames().toArray(new String[0]);
				if (columnNames.length == 0) {
					JOptionPane.showMessageDialog(this, "No columns available for statistics");
				} else {
					// wyswieetla okienko do wyboru kolumny
					String chosenColumn = (String) JOptionPane.showInputDialog(
							this,
							"Choose a column for statistics:",
							"Select Column",
							JOptionPane.QUESTION_MESSAGE,
							null,
							columnNames,
							columnNames[0]);

					// sprawdza czy uzytkownik wybral kolumne
					if (chosenColumn != null) {
						if(CsvParser.isColumnNumerical(t.column(chosenColumn).type()))
							JOptionPane.showMessageDialog(this, "Standard deviation value in column " + chosenColumn +
									" is " + CsvParser.getStatisticsForNumericalColumns(t, chosenColumn, CsvParser.StatisticsType.STANDARD_DEVIATION), "Statistics - Standard Deviation", JOptionPane.INFORMATION_MESSAGE);
						else
							JOptionPane.showMessageDialog(this, "Statistics are available only for numerical value columns.");
					} else {
						JOptionPane.showMessageDialog(this, "No column selected for statistics");
					}
				}
			} else {
				JOptionPane.showMessageDialog(this, "Please read data first");
			}
		});

		// akcje po kliknięciu variance
		menuItemStatisticsVariance.addActionListener( e -> {
			if (isRead) {
				String[] columnNames = t.columnNames().toArray(new String[0]);
				if (columnNames.length == 0) {
					JOptionPane.showMessageDialog(this, "No columns available for statistics");
				} else {
					// wyswieetla okienko do wyboru kolumny
					String chosenColumn = (String) JOptionPane.showInputDialog(
							this,
							"Choose a column for statistics:",
							"Select Column",
							JOptionPane.QUESTION_MESSAGE,
							null,
							columnNames,
							columnNames[0]);

					// sprawdza czy uzytkownik wybral kolumne
					if (chosenColumn != null) {
						if(CsvParser.isColumnNumerical(t.column(chosenColumn).type()))
							JOptionPane.showMessageDialog(this, "Variance value in column " + chosenColumn +
									" is " + CsvParser.getStatisticsForNumericalColumns(t, chosenColumn, CsvParser.StatisticsType.VARIANCE), "Statistics - Variance", JOptionPane.INFORMATION_MESSAGE);
						else
							JOptionPane.showMessageDialog(this, "Statistics are available only for numerical value columns.");
					} else {
						JOptionPane.showMessageDialog(this, "No column selected for statistics");
					}
				}
			} else {
				JOptionPane.showMessageDialog(this, "Please read data first");
			}
		});

		return menuBar;

	}
}
