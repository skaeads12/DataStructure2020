import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

public class TableType
{
	private String name;
	
	private String[] fieldName;
	private int[] fieldType;
	private int fieldSize;
	
	private RecordType head;
	
	int recordSize;
	int degree;
	int currentIndex;
	int currentOrderedIndex;
	
	BPTreeType tree;
	
	public TableType(String name, String[] fieldName, int[] fieldType, int fieldSize, int degree)
	{
		this.name = name;
		this.recordSize = 0;
		this.head = null;
		
		this.fieldName = fieldName;
		this.fieldType = fieldType;
		this.fieldSize = fieldSize;
		
		File file = new File("");
		String path = file.getAbsolutePath();
		file = new File(path + "/" + name + "_" + System.currentTimeMillis());
		
		if(!file.exists())
			file.mkdir();
		
		this.tree = new BPTreeType(degree, fieldType[0], file.getAbsolutePath() + "/");
		this.currentIndex = 0;
		this.currentOrderedIndex = 0;
		
		this.degree = degree;
	}
	
	public String getName()
	{
		return this.name;
	}
	
	public void setFieldName(String[] fieldName)
	{
		this.fieldName = fieldName;
	}
	
	public String[] getFieldName()
	{
		return this.fieldName;
	}
	
	public void setFieldType(int[] fieldType)
	{
		this.fieldType = fieldType;
	}
	
	public int[] getFieldType()
	{
		return this.fieldType;
	}
	
	public void setFieldSize(int fieldSize)
	{
		this.fieldSize = fieldSize;
	}
	
	public int getFieldSize()
	{
		return this.fieldSize;
	}
	
	public int getRecordSize()
	{
		return this.recordSize;
	}
	
	public boolean isEmpty()
	{
		return this.recordSize == 0;
	}
	
	public void swapRecord(int recordIndex1, int recordIndex2)
	{
		Object[] temp = this.getRecord(recordIndex1).getData().clone();
		
		this.getRecord(recordIndex1).setData(this.getRecord(recordIndex2).getData());
		this.getRecord(recordIndex2).setData(temp);
	}
	
	public Object[][] getDataFromObject()
	{
		if(this.recordSize == 0)
		{
			return null;
		}
		
		RecordType temp = this.head;
		Object[][] result = new Object[this.recordSize][this.fieldSize];
		
		int index = 0;
		while(temp != null)
		{
			result[index] = temp.getData();
			temp = temp.next;
			index++;
		}
		return result;
	}
	
	public ArrayList<Object[]> getDataFromArrayList()
	{
		if(this.recordSize == 0)
		{
			return null;
		}
		
		Object[][] data = this.getDataFromObject();
		ArrayList<Object[]> result = new ArrayList<Object[]>();
		
		for(int i = 0; i < data.length; i++)
		{
			result.add(data[i]);
		}
		
		return result;
	}
	
	public RecordType getRecord(int index)
	{
		if(index > this.recordSize)
		{
			return null;
		}
		
		RecordType temp = this.head;
		
		for(int i = 0; i < index; i++)
		{
			temp = temp.next;
		}
		
		return temp;
	}
	
	public void insertData(Object[] record)
	{	
		RecordType newData = new RecordType(record, this.fieldType, this.fieldSize);
		
		this.insertRecord(newData);
	}
	
	public void insertRecord(RecordType newRecord)
	{
		this.tree.insertNode(newRecord.getDataWithIndex(0), newRecord);
		
		this.recordSize++;
	}
	
	public void addData(Object[] record)
	{
		RecordType newData = new RecordType(record, this.fieldType, this.fieldSize);
		
		this.tree.addData(record[0], newData);
		this.recordSize++;
	}
	
	public void removeNode(Object key)
	{
		this.tree.removeData(key);
		this.recordSize--;
	}
	
	public Object[] searchData(Object key, int fieldIndex)
	{
		if(this.recordSize == 0)
		{
			return null;
		}
		
		return this.tree.searchData(key);
	}
	
	public void serializeRecords()
	{
		try
		{
			this.tree.serializeRecords();
		}
		catch (ClassNotFoundException e)
		{
			e.printStackTrace();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}
	
	public void saveRecords(String path)
	{
		try
		{
			this.tree.saveRecords(path + "_records.def");
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
	}
	
	public void loadRecords(String path)
	{
		try
		{
			FileInputStream fis = new FileInputStream(path + "_records.def");
			ObjectInputStream ois = new ObjectInputStream(fis);
			
			Object temp = ois.readObject();
			ArrayList<RecordType> records = (ArrayList<RecordType>) temp;
			this.recordSize = records.size();
			
			tree.loadRecords(records);
		}
		catch(FileNotFoundException e)
		{
			e.printStackTrace();
		}
		catch(IOException e)
		{
			e.printStackTrace();
		}
		catch (ClassNotFoundException e)
		{
			e.printStackTrace();
		}
	}
	
	public RecordType deserializeRecords(int line)
	{
		try
		{
			FileInputStream fis = new FileInputStream(this.name + "_records.def");
			ObjectInputStream ois = new ObjectInputStream(fis);
			
			for(int i = 0; i < line; i++)
			{
				ois.readObject();
			}
			
			RecordType resultRecord = (RecordType) ois.readObject();
			
			ois.close();
			fis.close();
			
			return resultRecord;
		}
		catch(IOException e)
		{
			e.printStackTrace();
		}
		catch (ClassNotFoundException e)
		{
			e.printStackTrace();
		}
		
		return null;
	}
	
	public void saveTable(String path) throws IOException
	{
		FileOutputStream fos = new FileOutputStream(path + ".dat");
		ObjectOutputStream oos = new ObjectOutputStream(fos);
		
		oos.writeObject(this.name);
		oos.writeObject(this.fieldName);
		oos.writeObject(this.fieldType);
		oos.writeObject(this.degree);
		oos.writeObject(this.tree.getRoot());
		
		oos.flush();
		fos.flush();
		oos.close();
		fos.close();
		
		this.saveRecords(path);
	}
	
	public Object[][] getOrderedData(int index)
	{
		BPTreeType tree = this.tree;
		
		ArrayList<RecordType> data = null;
		
		try
		{
			data = tree.deserializeRecords(index);
		}
		catch (ClassNotFoundException e)
		{
			e.printStackTrace();
		}
		catch (IOException e)
		{
			return null;
		}
		
		if(data == null)
			return null;
		
		Object[][] result = new Object[data.size()][];
		
		for(int i = 0; i < data.size(); i++)
		{
			result[i] = data.get(i).getData();
		}
		
		return result;
	}
	
	public int getNodeSize()
	{
		return this.tree.getLeafNodeSize();
	}
	
	public void modifyRecord(Object key, int fieldIndex, Object newValue)
	{
		this.tree.modifyRecord(key, fieldIndex, newValue);
	}
}