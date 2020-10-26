import java.awt.BorderLayout;
import java.awt.Component;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableModel;

public class Database
{
	public final static String[] TYPE = {"STRING", "DOUBLE", "INTEGER", "CHAR"};
	
	public final static int STRING_TYPE = 0;
	public final static int DOUBLE_TYPE = 1;
	public final static int INTEGER_TYPE = 2;
	public final static int CHAR_TYPE = 3;
	
	ArrayList<LinkedListType> database;
	LinkedListType currentTable;
	Main main;
	
	public Database(Main main)
	{
		database = new ArrayList<LinkedListType>();
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
		
		LinkedListType newTable = new LinkedListType(tableName);
		
		int fieldNumber = 0;
		
		boolean success = false;
		
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
		
		newTable.setFieldSize(fieldNumber);
		newTable.setFieldName(fieldName);
		newTable.setFieldType(fieldType);
		
		this.database.add(newTable);
		
		main.addTableIntoTreePanel(null, newTable);
		main.showTablePanel(null, newTable);
	}
	
	public void enter()
	{
		int index = this.selectTable();
		
		if(index == -1)
		{
			return;
		}
		
		LinkedListType table = database.get(index);
		enter(table);
	}
	
	public void enter(LinkedListType table)
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
		main.showTablePanel(null, table);
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
		
		ArrayList<LinkedListType> database = this.database;
		
		for(int i = 0; i < size; i++)
		{
			JCheckBox checkBox = new JCheckBox(database.get(i).getName());
			checkBox.setSelected(false);
			checkBoxPanel.add(checkBox);
		}
		
		saveTablePanel.add(label, BorderLayout.NORTH);
		saveTablePanel.add(checkBoxPanel, BorderLayout.CENTER);
		
		ArrayList<LinkedListType> selectedTable = new ArrayList<LinkedListType>();
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
		
		for(LinkedListType table : selectedTable)
		{
			this.saveTable(savePath, table);
		}
	}
	
	public void saveTable(String path, LinkedListType table)
	{
		String[] fieldName = table.getFieldName();
		int[] fieldTypes = table.getFieldType();
		int fieldSize = table.getFieldSize();
		Object[][] data = table.getDataFromObject();
		int recordSize = table.getRecordSize();
		path += "_" + table.getName() + ".dat"; 
		
		String result = table.getName() + "\n";
		
		for(int i = 0; i < fieldSize; i++)
		{
			result += fieldTypes[i];
			
			if(i != fieldSize - 1)
			{
				result += "\t";
			}
		}
		
		result += "\n";
		
		for(int i = 0; i < fieldSize; i++)
		{
			result += fieldName[i];
			
			if(i != fieldSize - 1)
			{
				result += "\t";
			}
		}
		
		result += "\n";
		
		for(int i = 0; i < recordSize; i++)
		{
			Object[] record = data[i];
			int length = record.length;
			
			for(int j = 0; j < length; j++)
			{
				Object field = record[j];
				result += field.toString();
				
				if(j != length - 1)
				{
					result += "\t";
				}
			}
			result += "\n";
		}
		
		try
		{
			FileOutputStream output = new FileOutputStream(path);
			output.write(result.getBytes());
			
			output.close();
		}
		catch (FileNotFoundException e)
		{
			e.printStackTrace();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}
	
	public void load()
	{
		JFileChooser chooser = new JFileChooser();
		
		FileNameExtensionFilter filter = new FileNameExtensionFilter("Dat Files", "dat");
		chooser.setFileFilter(filter);
		
		int ret = chooser.showOpenDialog(null);
		
		if(ret != JFileChooser.APPROVE_OPTION)
		{
			return;
		}
		
		File file = chooser.getSelectedFile();
		LinkedListType table = this.loadTable(file);

		if(table != null)
		{
			this.database.add(table);
			main.addTableIntoTreePanel(null, table);
			main.showTablePanel(null, table);
			
			JOptionPane.showMessageDialog(null, "데이터 로드를 성공적으로 마쳤습니다.");
		}
	}
	
	public LinkedListType loadTable(File file)
	{
		String result = "";
		
		try
		{
			FileReader reader = new FileReader(file);
			int cur = 0;
		
			while((cur = reader.read()) != -1)
			{
				result += (char) cur;
			}
			
			reader.close();
		}
		catch(IOException e)
		{
			e.printStackTrace();
		}
		
		String[] record = result.split("\n");
		
		String tableName = record[0];
		if(this.hasTableName(tableName))
		{
			tableName += "_";
		}
		
		String[] fieldTypesString = record[1].split("\t");
		int fieldSize = fieldTypesString.length;
		int[] fieldTypes = new int[fieldSize];
		
		for(int i = 0; i < fieldSize; i++)
		{
			fieldTypes[i] = Integer.valueOf(fieldTypesString[i]);
		}
		
		String[] fieldNames = record[2].split("\t");
		int length = record.length;

		LinkedListType table = new LinkedListType(tableName);
		table.setFieldSize(fieldSize);
		table.setFieldName(fieldNames);
		table.setFieldType(fieldTypes);
		
		for(int i = 3; i < length; i++)
		{
			Object[] objectRecord = record[i].split("\t");
			table.insertData(objectRecord);
		}
		
		return table;
	}
	
	public void browse(JTable tablePanel)
	{
		int index = this.selectTable(); 
		
		if(index == -1)
		{
			return;
		}
		
		LinkedListType table = database.get(index);
		main.showTablePanel(tablePanel, table);
	}
	
	public void search()
	{
		int index = this.selectTable();
		
		if(index == -1)
		{
			return;
		}
		
		LinkedListType table = database.get(index);
		ArrayList<Object[]> result = this.search(table);
		if(result == null)
		{
			return;
		}
		
		String[] fieldName = table.getFieldName();
			
		this.printTableFromData(result, fieldName);
	}
	
	public ArrayList<Object[]> search(LinkedListType table)
	{
		String[] fieldName = table.getFieldName();
		int[] fieldType = table.getFieldType();
		
		int select = this.selectField(table, "검색 기준 필드를 선택하세요.");
		
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
	
	public ArrayList<Object[]> search(LinkedListType table, String key, int index)
	{
		Object[][] data = table.getDataFromObject();
		
		int size = table.getRecordSize();
		
		ArrayList<Object[]> result = new ArrayList<Object[]>();
		
		for(int i = 0; i < size; i++)
		{
			Object[] record = data[i];
			String object = String.valueOf(record[index]);
			
			if(object.contains(key))
			{
				result.add(record);
			}
			else
			{
				continue;
			}
		}
		
		return result;
	}
	
	public void modify(int selectedRow)
	{
		LinkedListType table = this.currentTable;
		
		Object[] record = table.getRecord(selectedRow).getData();
		
		if(record == null)
		{
			JOptionPane.showMessageDialog(null, "잘못된 접근입니다.");
			return;
		}
		
		int fieldIndex = this.selectField(table, "수정할 필드를 선택해주세요.");
			
		if(fieldIndex == -1)
		{
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
				switch(table.getFieldType()[fieldIndex])
				{
				case STRING_TYPE:
					record[fieldIndex] = newData;
					failure = false;
					break;
				case DOUBLE_TYPE:
					double tempData = Double.parseDouble(newData);
					record[fieldIndex] = String.valueOf(tempData);
					failure = false;
					break;
				case INTEGER_TYPE:
					Integer.parseInt(newData);
					record[fieldIndex] = newData;
					failure = false;
					break;
				case CHAR_TYPE:
					if(newData.length() > 1) throw new NumberFormatException();
					record[fieldIndex] = newData;
					failure = false;
					break;
				}
			}
			catch(NumberFormatException e)
			{
				JOptionPane.showMessageDialog(null, "데이터 타입이 일치하지 않습니다!");
			}
		}
		
		main.showTablePanel(null, table);
	}
	
	public int printTableFromData(ArrayList<Object[]> data, String columnNames[])
	{
		Object[][] rowData;
		
		try
		{
			rowData = new Object[data.size()][data.get(0).length];
		}
		catch(IndexOutOfBoundsException e)
		{
			JOptionPane.showMessageDialog(null, "데이터가 없습니다.");
			return -1;
		}
		catch(NullPointerException ne)
		{
			JOptionPane.showMessageDialog(null, "데이터 검색이 취소되었습니다.");
			return -1;
		}
		
		for(int i = 0; i < data.size(); i++)
		{
			rowData[i] = data.get(i);
		}
		
		DefaultTableModel model = new DefaultTableModel(rowData, columnNames);
		JTable table = new JTable(model);
		JScrollPane panel = new JScrollPane(table);
		
		JOptionPane.showMessageDialog(null, panel);
		
		return table.getSelectedRow();
	}
	
	public void delete(int[] selectedRows)
	{
		int rowsCount = selectedRows.length;
		LinkedListType table = this.currentTable;
		
		int confirm = JOptionPane.showConfirmDialog(null, rowsCount + "개의 데이터를 정말로 삭제하시겠습니까?", "Confirm", JOptionPane.OK_CANCEL_OPTION);
		
		if(confirm != JOptionPane.OK_OPTION)
		{
			JOptionPane.showMessageDialog(null, "데이터 삭제가 취소되었습니다.");
			return;
		}
		
		for(int i = 0; i < rowsCount; i++)
		{
			int index = selectedRows[i];
			table.removeNode(table.getRecord(index));
			
			rowsCount--;
			i--;
			
			if(rowsCount == 0)
			{
				break;
			}
		}
		
		main.showTablePanel(null, table);
	}
	
	public int selectField(LinkedListType table, String message)
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
	
	public LinkedListType sort()
	{
		int fieldIndex = -1;
		
		while(fieldIndex == -1)
		{
			if(currentTable == null)
			{
				JOptionPane.showMessageDialog(null, "먼저 테이블을 생성해주세요!");
				return null;
			}
			
			fieldIndex = this.selectField(currentTable, "기준이 될 필드를 선택해주세요.");
			
			if(fieldIndex == -1)
			{
				return null;
			}
		}
		
		JPanel panel = new JPanel();
		panel.setLayout(new BorderLayout());
		JLabel label = new JLabel("정렬 방향을 선택해주세요.");
		
		JRadioButton ascendButton = new JRadioButton("오름차순");
		JRadioButton descendButton = new JRadioButton("내림차순");
		
		ButtonGroup group = new ButtonGroup();
		
		group.add(ascendButton);
		group.add(descendButton);
		
		ascendButton.setSelected(true);
		descendButton.setSelected(false);
		
		panel.add(label, BorderLayout.NORTH);
		
		JPanel buttonPanel = new JPanel();
		buttonPanel.add(ascendButton);
		buttonPanel.add(descendButton);
		
		panel.add(buttonPanel);
		
		int confirm = JOptionPane.CANCEL_OPTION;
		
		boolean ascend = false;
		
		while(confirm != JOptionPane.OK_OPTION)
		{
			confirm = JOptionPane.showConfirmDialog(null, panel, "Confirm", JOptionPane.OK_CANCEL_OPTION);
			
			if(confirm == JOptionPane.OK_OPTION)
			{
				if(ascendButton.isSelected())
				{
					ascend = true;
				}
				else
				{
					ascend = false;
				}
			}
			else
			{
				int cancelConfirm = JOptionPane.showConfirmDialog(null, "정렬을 취소하시겠습니까?", "Confirm", JOptionPane.OK_CANCEL_OPTION);
				
				if(cancelConfirm == JOptionPane.OK_OPTION)
				{
					return null;
				}
				else
				{
					continue;
				}
			}
		}
		
		currentTable.sortNode(fieldIndex, ascend);
		
		return currentTable;
	}
	
	public boolean hasTableName(String tableName)
	{
		ArrayList<LinkedListType> database = this.database;
		
		for(LinkedListType table: database)
		{
			if(tableName.equals(table.getName()))
			{
				return true;
			}
		}
		
		return false;
	}
}
