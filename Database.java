import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.util.ArrayList;

import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextPane;
import javax.swing.ScrollPaneConstants;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableModel;

public class Database
{
	public final static String[] TYPE = {"STRING", "DOUBLE", "INTEGER", "CHAR"};
	
	public final static int STRING_TYPE = 0;
	public final static int DOUBLE_TYPE = 1;
	public final static int INTEGER_TYPE = 2;
	public final static int CHAR_TYPE = 3;
	
	ArrayList<TableType> database;
	TableType currentTable;
	Main main;
	
	public Database(Main main)
	{
		database = new ArrayList<TableType>();
		currentTable = null;
		this.main = main;
	}
	
	public void define()
	{
		String tableName = JOptionPane.showInputDialog(null, "새로 정의할 테이블 이름을 입력하세요.");
		
		if(tableName == null || tableName.equals(""))
		{
			JOptionPane.showMessageDialog(null, "테이블 정의가 취소되었습니다.");
			return;
		}
		
		if(this.hasTableName(tableName))
		{
			JOptionPane.showMessageDialog(null, "이미 존재하는 테이블 이름입니다.");
			return;
		}
		
		int fieldNumber = 0;
		
		boolean success = false;
		
		// fieldNumber 입력
		while(!success)
		{
			String input = JOptionPane.showInputDialog(null, "필드의 수를 양의 정수로 입력하세요.");
			
			try
			{
				fieldNumber = Integer.parseInt(input);
				
				if(fieldNumber < 1)
				{
					throw new NumberFormatException();
				}
				else
				{
					success = true;
				}
			}
			catch(NumberFormatException e)
			{
				int confirm = JOptionPane.showConfirmDialog(null, "필드의 수는 양의 정수로 입력해야 합니다!\n(NO 선택시 입력 취소)", "ERROR", JOptionPane.YES_NO_OPTION);
				
				if(confirm == JOptionPane.YES_OPTION)
				{
					success = false;
					continue;
				}
				else
				{
					return;
				}
			}
		}
		
		success = false;
		
		String[] fieldName = new String[fieldNumber];
		int[] fieldType = new int[fieldNumber];
		
		// fieldName 입력
		for(int i = 0; i < fieldNumber; i++)
		{
			String input = JOptionPane.showInputDialog((i + 1) + "번째 필드의 이름을 입력하세요.");
			
			if(input == null || input.equals(""))
			{
				int confirm = JOptionPane.showConfirmDialog(null, "필드의 이름을 다시 입력해주세요.\n(NO 선택시 입력 취소)", "ERROR", JOptionPane.YES_NO_OPTION);
				
				if(confirm == JOptionPane.YES_OPTION)
				{
					i--;
					continue;
				}
				else
				{
					return;
				}
			}
			else
			{
				fieldName[i] = input;
			}
		}

		JPanel panel = new JPanel();
		JLabel label1 = new JLabel();
		JLabel label2 = new JLabel("(CANCLE 선택시 입력 취소)");
		JComboBox<String> comboBox = new JComboBox<String>(this.TYPE);
		
		panel.setLayout(new BorderLayout());
		panel.add(label1, BorderLayout.NORTH);
		panel.add(label2, BorderLayout.CENTER);
		panel.add(comboBox, BorderLayout.SOUTH);
		
		// fieldType 입력
		for(int i = 0; i < fieldNumber; i++)
		{
			label1.setText((i + 1) + "번째 필드의 데이터 타입을 선택해주세요.");
			
			int confirm = JOptionPane.showConfirmDialog(null, panel, "Input", JOptionPane.OK_CANCEL_OPTION);
			
			if(confirm == JOptionPane.OK_OPTION)
			{
				fieldType[i] = comboBox.getSelectedIndex();
			}
			else
			{
				int cancleConfirm = JOptionPane.showConfirmDialog(null, "데이터 타입 선택을 취소하시겠습니까?", "Confirm", JOptionPane.OK_CANCEL_OPTION);
				
				if(cancleConfirm == JOptionPane.OK_OPTION)
				{
					return;
				}
				else
				{
					i--;
					continue;
				}
			}
		}
		
		int degree = -1;
		
		while(true)
		{
			String input = JOptionPane.showInputDialog("B+ 트리의 차수를 입력하세요.");
			
			try
			{
				degree = Integer.parseInt(input);
				break;
			}
			catch(NumberFormatException e)
			{
				int confirm = JOptionPane.showConfirmDialog(null, "잘못된 입력입니다! 테이블 생성을 중단하시겠습니까?");
				
				if(confirm == JOptionPane.YES_OPTION)
				{
					return;
				}
			}
		}
		
		TableType newTable = new TableType(tableName, fieldName, fieldType, fieldNumber, degree);
		
		this.database.add(newTable);
		
		main.addTableIntoTreePanel(null, newTable);
		main.showTablePanel(null, newTable, newTable.recordSize, newTable.getNodeSize());
	}
	
	public void enter()
	{
		if(this.currentTable == null)
			return;
		
		TableType table = this.currentTable;
		enter(table);
	}
	
	public void enter(TableType table)
	{
		int fieldSize = table.getFieldSize();
		int[] fieldType = table.getFieldType();
		String[] fieldName = table.getFieldName();
		
		Object[] record = new Object[fieldSize];
		
		for(int i = 0; i < fieldSize; i++)
		{
			String input = JOptionPane.showInputDialog(null, "[" + (i + 1) + "] \"" + fieldName[i] + "\" 필드에 들어갈 데이터를 입력해주세요.");
			
			if(input == null || input.equals(""))
			{
				int confirm = JOptionPane.showConfirmDialog(null, "데이터 입력을 취소하시겠습니까?", "Confirm", JOptionPane.YES_NO_OPTION);
				
				if(confirm == JOptionPane.YES_OPTION)
				{
					return;
				}
				else
				{
					i--;
					continue;
				}
			}
			
			if(fieldType[i] == this.STRING_TYPE)
			{
				record[i] = input;
			}
			else if(fieldType[i] == this.DOUBLE_TYPE)
			{
				try
				{
					record[i] = Double.parseDouble(input);
				}
				catch(NumberFormatException e)
				{
					JOptionPane.showMessageDialog(null, "데이터 타입에 맞춰 입력해주세요!\n(입력되어야 할 데이터타입: " + TYPE[fieldType[i]] + ")");
					i--;
				}
			}
			else if(fieldType[i] == this.INTEGER_TYPE)
			{
				try
				{
					record[i] = Integer.parseInt(input);
				}
				catch(NumberFormatException e)
				{
					JOptionPane.showMessageDialog(null, "데이터 타입에 맞춰 입력해주세요!\n(입력되어야 할 데이터타입: " + TYPE[fieldType[i]] + ")");
					i--;
				}
			}
			else if(fieldType[i] == this.CHAR_TYPE)
			{
				if(input.length() > 1)
				{
					JOptionPane.showMessageDialog(null, "데이터 타입에 맞춰 입력해주세요!\n(입력되어야 할 데이터타입: " + TYPE[fieldType[i]] + ")");
					i--;
				}
				else
				{
					record[i] = input.charAt(0);
				}
			}
		}
		
		table.insertData(record);
		main.showTablePanel(null, table.getOrderedData(table.currentIndex), table.getFieldName(), table.getRecordSize(), table.currentIndex, table.getNodeSize());
	}
	
	public void save()
	{
		if(this.database.size() < 1)
		{
			JOptionPane.showMessageDialog(null, "생성된 테이블이 없습니다!");
			
			return;
		}
		
		JFileChooser chooser = new JFileChooser();
		
		FileNameExtensionFilter filter = new FileNameExtensionFilter("Dat Files", "dat");
		chooser.setFileFilter(filter);
		
		int ret = chooser.showSaveDialog(null);
		String savePath = "";
		
		if(ret != JFileChooser.APPROVE_OPTION)
		{
			return;
		}
		else
		{
			savePath = chooser.getSelectedFile().getPath();
		}
		
		JPanel saveTablePanel = new JPanel();
		saveTablePanel.setLayout(new BorderLayout());
		JLabel label = new JLabel("저장할 테이블을 선택해주세요.");
		int size = this.database.size();
		JPanel checkBoxPanel = new JPanel();
		
		ArrayList<TableType> database = this.database;
		
		for(int i = 0; i < size; i++)
		{
			JCheckBox checkBox = new JCheckBox(database.get(i).getName());
			checkBox.setSelected(false);
			checkBoxPanel.add(checkBox);
		}
		
		saveTablePanel.add(label, BorderLayout.NORTH);
		saveTablePanel.add(checkBoxPanel, BorderLayout.CENTER);
		
		ArrayList<TableType> selectedTable = new ArrayList<TableType>();
		int confirm = JOptionPane.CANCEL_OPTION;
		
		while(confirm != JOptionPane.OK_OPTION)
		{
			confirm = JOptionPane.showConfirmDialog(null, saveTablePanel, "Confirm", JOptionPane.OK_CANCEL_OPTION);
			
			if(confirm != JOptionPane.OK_OPTION)
			{
				int cancelConfirm = JOptionPane.showConfirmDialog(null, "저장을 취소하시겠습니까?", "Confirm", JOptionPane.OK_CANCEL_OPTION);
				
				if(cancelConfirm == JOptionPane.OK_OPTION)
				{
					return;
				}
			}
			
			Component[] checkBoxes = checkBoxPanel.getComponents();
			
			int index = 0;
			
			for(Component component : checkBoxes)
			{
				JCheckBox checkBox = (JCheckBox) component;
				
				if(checkBox.isSelected())
				{
					selectedTable.add(database.get(index));
				}
			}
			
			if(selectedTable.size() < 1)
			{
				JOptionPane.showMessageDialog(null, "선택된 테이블이 없습니다! 다시 진행해주세요!");
				confirm = JOptionPane.CANCEL_OPTION;
				continue;
			}
		}
		
		for(TableType table : selectedTable)
		{
			this.saveTable(savePath, table);
		}
	}
	
	public void saveTable(String path, TableType table)
	{
		try
		{
			table.saveTable(path);
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}
	
	public void load()
	{
		JFileChooser chooser = new JFileChooser();
		
		FileNameExtensionFilter csvFilter = new FileNameExtensionFilter("CSV & TSV Files", "csv", "tsv");
		chooser.setFileFilter(csvFilter);
		
		FileNameExtensionFilter datFilter = new FileNameExtensionFilter("Dat Files", "dat");
		chooser.setFileFilter(datFilter);
		
		int ret = chooser.showOpenDialog(null);
		
		if(ret != JFileChooser.APPROVE_OPTION)
		{
			return;
		}
		
		File file = chooser.getSelectedFile();
		
		TableType table = null;
		
		if(file.getName().contains("tsv"))
		{
			String tableName = JOptionPane.showInputDialog(null, "테이블 이름을 입력하세요.");
			
			if(tableName == null || tableName.equals(""))
			{
				JOptionPane.showMessageDialog(null, "데이터 로드를 취소했습니다.");
				return;
			}
			
			int degree = -1;
			
			while(true)
			{
				String degreeInput = JOptionPane.showInputDialog(null, "차수를 입력하세요.");
				
				try
				{
					degree = Integer.parseInt(degreeInput);
					break;
				}
				catch(NumberFormatException e)
				{
					JOptionPane.showMessageDialog(null, "잘못된 입력입니다! 다시 입력하세요!");
				}
			}
			
			table = this.loadTsv(file, tableName, degree);
		}
		else if(file.getName().contains("dat"))
		{
			table = this.loadDat(file);
		}
		
		if(table != null)
		{
			this.database.add(table);
			main.addTableIntoTreePanel(null, table);
			main.showTablePanel(null, table, table.getRecordSize(), table.getNodeSize());
			this.currentTable = table;
		}
	}
	
	public TableType loadDat(File file)
	{
		String path = file.getPath();
		path = path.substring(0, path.indexOf(".dat"));
		
		try
		{
			FileInputStream fis = new FileInputStream(file);
			ObjectInputStream ois = new ObjectInputStream(fis);
			
			Object objectTableName = ois.readObject();
			Object objectFieldNames = ois.readObject();
			Object objectFieldTypes = ois.readObject();
			Object objectDegree = ois.readObject();
			Object objectRoot = ois.readObject();
			
			String tableName = (String) objectTableName;
			String[] fieldNames = (String[]) objectFieldNames;
			int[] fieldTypes = (int[]) objectFieldTypes;
			int degree = Integer.parseInt(objectDegree.toString());
			BPNodeType root = (BPNodeType) objectRoot;
			
			TableType table = new TableType(tableName, fieldNames, fieldTypes, fieldTypes.length, degree);
			table.tree.setRoot(root);
			
			table.loadRecords(path);
			
			ois.close();
			fis.close();
			
			return table;
		}
		catch (FileNotFoundException e)
		{
			e.printStackTrace();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		catch (ClassNotFoundException e)
		{
			e.printStackTrace();
		}
		
		return null;
	}
	
	public TableType loadTsv(File file, String tableName, int degree)
	{
		String[] columns;
		
		try
		{
			BufferedReader br = new BufferedReader(new FileReader(file));
			columns = br.readLine().split("\t");
			columns[0] = "index";
			int length = columns.length;
			
			int[] dataType = new int[length];
			
			for(int i = 0; i < length; i++)
			{
				dataType[i] = STRING_TYPE;
			}
			
			dataType[0] = INTEGER_TYPE;
			dataType[4] = INTEGER_TYPE;
			dataType[5] = DOUBLE_TYPE;
			
			TableType table = new TableType(tableName, columns, dataType, length, degree);
			
			String line = "";
			
			while((line = br.readLine()) != null)
			{
				String[] fields = line.split("\t");
				
				if(fields[5].equals(""))
				{
					fields[5] = String.valueOf(0.0);
				}
				
				table.addData(fields);
			}
			
			br.close();
			
			table.serializeRecords();
			
			return table;
		}
		catch (FileNotFoundException e)
		{
			e.printStackTrace();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		
		return null;
	}
	
	public void browse(JTable tablePanel)
	{
		int index = this.selectTable(); 
		
		if(index == -1)
		{
			return;
		}
		
		TableType table = database.get(index);
		main.showTablePanel(tablePanel, table, table.getRecordSize(), table.getNodeSize());
	}
	
	public void search()
	{
		TableType table = this.currentTable;
		
		if(table == null)
		{
			return;
		}
		
		ArrayList<Object[]> result = this.search(table);
		
		if(result == null)
		{
			return;
		}
		
		String[] fieldName = table.getFieldName();
		
		this.printTableFromData(result, fieldName);
	}
	
	public ArrayList<Object[]> search(TableType table)
	{
		String[] fieldName = table.getFieldName();
		int[] fieldType = table.getFieldType();
		
//		int select = this.selectField(table, "검색 기준 필드를 선택하세요.");
		int select = 0;
		
		if (select != -1)
		{
			int index = select;
			String input = null;
			
			while(true)
			{
				String message = fieldName[index] + " [" + TYPE[fieldType[index]] + "] 검색할 키 값을 입력해주세요.";
				input = JOptionPane.showInputDialog(null, message);
				
				if(input == null || input.equals(""))
				{
					int confirm = JOptionPane.showConfirmDialog(null, "데이터 검색을 취소하시겠습니까?", "Confirm", JOptionPane.OK_CANCEL_OPTION);
					
					if(confirm == JOptionPane.OK_OPTION)
					{
						return null;
					}
					else
					{
						continue;
					}
				}
				
				try
				{
					if(this.typeCheck(input, fieldType[index]))
					{
						break;
					}
					
				}
				catch(NumberFormatException e)
				{
					JOptionPane.showMessageDialog(null, "데이터 타입이 일치하지 않습니다!");
					continue;
				}
			}
			
			ArrayList<Object[]> result = this.search(table, input, index);
			
			return result;
		}
		else
		{
			return null;
		}
	}
	
	public ArrayList<Object[]> search(TableType table, String key, int index)
	{
		Object[] data = table.searchData(key, index);
		
		if(data != null)
		{
			ArrayList<Object[]> result = new ArrayList<Object[]>();
			result.add(data);
			
			return result;
		}
		
		return null;
	}
	
	public void modify(Object key)
	{
		TableType table = this.currentTable;
		
		int fieldIndex = this.selectField(table, "수정할 필드를 선택해주세요.");
		
		if(fieldIndex == 0)
		{
			JOptionPane.showMessageDialog(null, "PK는 수정이 불가능합니다.");
			return;
		}
		
		boolean failure = true;
		
		while(failure)
		{
			String newData = JOptionPane.showInputDialog(null, "새로운 데이터를 입력해주세요.");
			
			if(newData == null || newData.equals(""))
			{
				int confirm = JOptionPane.showConfirmDialog(null, "취소하시겠습니까?", "Confirm",JOptionPane.OK_CANCEL_OPTION);
				
				if(confirm == JOptionPane.OK_OPTION)
				{
					return;
				}
				else
				{
					continue;
				}
			}
			
			try
			{
				int[] fieldType = table.getFieldType();
				
				switch(fieldType[fieldIndex])
				{
				case STRING_TYPE:
					table.modifyRecord(key, fieldIndex, newData);
					failure = false;
					break;
				case DOUBLE_TYPE:
					double tempDouble = Double.parseDouble(newData);
					table.modifyRecord(key, fieldIndex, tempDouble);
					failure = false;
					break;
				case INTEGER_TYPE:
					int tempInt = Integer.parseInt(newData);
					table.modifyRecord(key, fieldIndex, tempInt);
					failure = false;
					break;
				case CHAR_TYPE:
					if(newData.toCharArray().length > 1) throw new NumberFormatException();
					char tempChar = newData.charAt(0);
					table.modifyRecord(key, fieldIndex, tempChar);
					failure = false;
					break;
				}
			}
			catch(NumberFormatException e)
			{
				JOptionPane.showMessageDialog(null, "데이터 타입이 일치하지 않습니다!");
			}
		}
		
		main.showTablePanel(null, table, table.getRecordSize(), table.getNodeSize());
	}
	
	public void printTableFromData(ArrayList<Object[]> data, String columnNames[])
	{
		if(data == null)
		{
			JOptionPane.showMessageDialog(null, "데이터를 찾을 수 없습니다!");
			return;
		}
		
		JTextPane textPane = new JTextPane();
		
		StringBuilder sb = new StringBuilder("");
		
		for(int i = 0; i < data.size(); i++)
		{
			for(int j = 0; j < data.get(i).length; j++)
			{
				sb.append("[ " + columnNames[j] + " ]:\t");
				
				String temp = data.get(i)[j].toString();
				sb.append(temp);
				sb.append("\n");
			}
		}
		
		textPane.setText(sb.toString());
		textPane.setEditable(false);
		textPane.setPreferredSize(new Dimension(main.mainFrame.getWidth() / 2, main.mainFrame.getHeight() / 2));
		textPane.setFont(new Font("맑은 고딕", Font.PLAIN, 20));
		
		JScrollPane panel = new JScrollPane(textPane);
		panel.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
		panel.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		
		JOptionPane.showMessageDialog(null, panel);
	}
	
	public void delete(Object[] deleteKeys)
	{
		TableType table = this.currentTable;
		int length = deleteKeys.length;
		
		int confirm = JOptionPane.showConfirmDialog(null, length + "개의 데이터를 정말로 삭제하시겠습니까?", "Confirm", JOptionPane.OK_CANCEL_OPTION);
		
		if(confirm != JOptionPane.OK_OPTION)
		{
			JOptionPane.showMessageDialog(null, "데이터 삭제가 취소되었습니다.");
			return;
		}
		
		for(int i = 0; i < length; i++)
		{
			table.removeNode(deleteKeys[i]);
		}
		
		main.showTablePanel(null, table.getOrderedData(table.currentIndex), table.getFieldName(), table.getRecordSize(), table.currentIndex, table.getNodeSize());
	}
	
	public int selectField(TableType table, String message)
	{
		JPanel panel = new JPanel();
		JLabel label = new JLabel(message);
		String[] fieldName = table.getFieldName();
		
		JComboBox<String> comboBox = new JComboBox<String>(fieldName);
		
		panel.setLayout(new BorderLayout());
		panel.add(label, BorderLayout.NORTH);
		panel.add(comboBox, BorderLayout.CENTER);
		
		int select = JOptionPane.CANCEL_OPTION;
		
		while(select != JOptionPane.OK_OPTION)
		{
			select = JOptionPane.showConfirmDialog(null, panel, "Input", JOptionPane.OK_CANCEL_OPTION);
			
			if(select != JOptionPane.OK_OPTION)
			{
				int confirm = JOptionPane.showConfirmDialog(null, "필드 선택을 취소하시겠습니까?", "Confirm", JOptionPane.OK_CANCEL_OPTION);
				
				if(confirm == JOptionPane.OK_OPTION)
				{
					return -1;
				}
				else
				{
					continue;
				}
			}
		}
		
		return comboBox.getSelectedIndex();
	}
	
	public int selectTable()
	{
		JPanel panel = new JPanel();
		JLabel label = new JLabel("테이블을 선택해주세요.");
		String[] tableNames = new String[database.size()];
		
		if(database.size() == 0)
		{
			JOptionPane.showMessageDialog(null, "생성된 테이블이 없습니다!");
			return -1;
		}
		
		for(int i = 0; i < database.size(); i++)
		{
			tableNames[i] = database.get(i).getName();
		}
		
		JComboBox<String> comboBox = new JComboBox<String>(tableNames);
		
		panel.setLayout(new BorderLayout());
		panel.add(label, BorderLayout.NORTH);
		panel.add(comboBox, BorderLayout.CENTER);
		
		int select = JOptionPane.CANCEL_OPTION;
		
		while(select != JOptionPane.OK_OPTION)
		{
			select = JOptionPane.showConfirmDialog(null, panel, "Input", JOptionPane.OK_CANCEL_OPTION);
			
			if(select != JOptionPane.OK_OPTION)
			{
				int confirm = JOptionPane.showConfirmDialog(null, "지금 동작을 취소하시겠습니까?", "Confirm", JOptionPane.OK_CANCEL_OPTION);
				
				if(confirm == JOptionPane.OK_OPTION)
				{
					return -1;
				}
			}
		}
		
		return comboBox.getSelectedIndex();
	}
	
	public boolean typeCheck(String key, int type) throws NumberFormatException
	{
		switch(type)
		{
			case STRING_TYPE:
				return true;
			case DOUBLE_TYPE:
				Double doubleKey = Double.parseDouble(key);
				return true;
			case INTEGER_TYPE:
				Integer intKey = Integer.parseInt(key);
				return true;
			case CHAR_TYPE:
				if(key.length() > 1)
				{
					throw new NumberFormatException();
				}
				else
					return true;
			default:
				throw new NumberFormatException();
		}
	}
	
	public void sort()
	{
		int fieldIndex = -1;
		
		while(fieldIndex == -1)
		{
			if(currentTable == null)
			{
				JOptionPane.showMessageDialog(null, "먼저 테이블을 생성해주세요!");
				return;
			}
			
			fieldIndex = this.selectField(currentTable, "기준이 될 필드를 선택해주세요.");
			
			if(fieldIndex == -1)
			{
				return;
			}
		}
		
		Object[][] rowData = this.currentTable.getOrderedData(0);
		this.currentTable.currentIndex = 0;
		this.currentTable.currentOrderedIndex = fieldIndex;
		Object[] columns = this.currentTable.getFieldName();
		int recordSize = this.currentTable.recordSize;
		
		main.showTablePanel(null, rowData, columns, recordSize, this.currentTable.currentIndex, this.currentTable.getNodeSize());
	}
	
	public void next()
	{
		if(this.currentTable == null)
		{
			JOptionPane.showMessageDialog(null, "테이블을 우선 정의해주세요!");
			return;
		}
		
		TableType table = this.currentTable;
		
		if(table.currentIndex < table.tree.getLeafNodeSize())
			table.currentIndex++;
		
		Object[][] rowData = table.getOrderedData(table.currentIndex);
		Object[] columns = table.getFieldName();
		int recordSize = table.recordSize;
		
		main.showTablePanel(null, rowData, columns, recordSize, table.currentIndex, table.getNodeSize());
	}
	
	public void prev()
	{
		if(this.currentTable == null)
		{
			JOptionPane.showMessageDialog(null, "테이블을 우선 정의해주세요!");
			return;
		}
		
		TableType table = this.currentTable;
		
		if(table.currentIndex > 0)
			table.currentIndex--;
		
		Object[][] rowData = table.getOrderedData(table.currentIndex);
		Object[] columns = table.getFieldName();
		
		main.showTablePanel(null, rowData, columns, table.recordSize, table.currentIndex, table.getNodeSize());
	}
	
	public void move()
	{
		if(this.currentTable == null)
		{
			JOptionPane.showMessageDialog(null, "테이블을 우선 정의해주세요.");
			return;
		}
		
		TableType table = this.currentTable;
		int movePage;
		
		while(true)
		{
			String input = JOptionPane.showInputDialog(null, "이동하실 페이지를 입력하세요.");
			
			try
			{
				movePage = Integer.parseInt(input);
				
				break;
			}
			catch(NumberFormatException e)
			{
				JOptionPane.showMessageDialog(null, "숫자가 올바르지 않습니다! 다시 시도해주세요!");
				return;
			}
		}

		int length = table.getNodeSize();
		
		if(movePage > length)
			movePage = length;
		else if(movePage < 0)
			movePage = 0;
		
		Object[][] rowData = table.getOrderedData(movePage);
		Object[] columns = table.getFieldName();
		
		main.showTablePanel(null, rowData, columns, table.recordSize, movePage, table.getNodeSize());
		table.currentIndex = movePage;
	}
	
	public boolean hasTableName(String tableName)
	{
		ArrayList<TableType> database = this.database;
		
		for(TableType table: database)
		{
			if(tableName.equals(table.getName()))
			{
				return true;
			}
		}
		
		return false;
	}
}
