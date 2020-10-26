
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.Enumeration;

import javax.swing.*;
import javax.swing.border.BevelBorder;
import javax.swing.event.CellEditorListener;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.TableColumnModelEvent;
import javax.swing.event.TableColumnModelListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;

public class Main extends JFrame
{
	Database database;
	JFrame mainFrame;
	JTable tablePanel;
	JTree treePanel;
	JLabel stateBar;
	
	public Main()
	{
		JFrame mainFrame = this.initFrame("TEMP PROJECT");
		this.mainFrame = mainFrame;
		mainFrame.setLayout(new BorderLayout());
		
		int frameHeight = mainFrame.getHeight();
		int frameWidth = mainFrame.getWidth();
		
		Database database = new Database(this);
		this.database = database;
		
		JMenuBar mainMenuBar = initMenu();
		mainFrame.add(mainMenuBar, BorderLayout.NORTH);
		
		JScrollPane treePanel = initTreePanel();
		treePanel.setPreferredSize(new Dimension(frameWidth / 6, frameHeight));
		mainFrame.add(treePanel, BorderLayout.WEST);

		String columnNames[] = {};
		String rowData[][] = {};

		JPanel stateBar = initStateBar("");
		mainFrame.add(stateBar, BorderLayout.SOUTH);
		
		JScrollPane tablePanel = initTablePanel(rowData, columnNames);
		mainFrame.add(tablePanel, BorderLayout.CENTER);
		
		mainFrame.setVisible(true);
	}
	
	public JFrame initFrame(String title)
	{
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		
		// Frame Size는 화면의 2/3 크기
		int frameHeight = (int) screenSize.getHeight() * 2 / 3;
		int frameWidth = (int) screenSize.getWidth() * 2 / 3;
		
		// 위치는 정가운데
		int x = (int) (screenSize.getWidth() - frameWidth) / 2;
		int y = (int) (screenSize.getHeight() - frameHeight) / 2;
		
		JFrame mainFrame = new JFrame();
		mainFrame.setSize(new Dimension(frameWidth, frameHeight));
		
		mainFrame.setLocation(new Point(x, y));
		
		mainFrame.setTitle(title);
		mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		return mainFrame;
	}
	
	public JMenuBar initMenu()
	{
		JMenuBar menuBar = new JMenuBar();
		Database database = this.database;
		
		JMenu mainMenu = new JMenu("Main");
		JMenuItem defineItem = new JMenuItem("Define");
		JMenuItem enterItem = new JMenuItem("Enter");
		JMenuItem browseItem = new JMenuItem("Browse");
		JMenuItem searchItem = new JMenuItem("Search");
		JMenuItem modifyItem = new JMenuItem("Modify");
		JMenuItem deleteItem = new JMenuItem("Delete");
		JMenuItem sortItem = new JMenuItem("Sort");
		
		defineItem.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				database.define();
			}
		});
		
		enterItem.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				database.enter();
			}
		});
		
		browseItem.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				database.browse(tablePanel);
			}
		});
		
		searchItem.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				database.search();
			}
		});
		
		modifyItem.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				int selectedRow = tablePanel.getSelectedRow();
			
				if(selectedRow == -1)
				{
					JOptionPane.showMessageDialog(null, "수정할 데이터를 선택하고 진행해주세요.");
					return;
				}
				else
				{
					database.modify(selectedRow);
				}
			}
		});
		
		deleteItem.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				int[] selectedRows = tablePanel.getSelectedRows();
				
				if(selectedRows.length == 0)
				{
					JOptionPane.showMessageDialog(null, "데이터를 먼저 선택하고 진행해주세요!");
					return;
				}
				else
				{
					database.delete(selectedRows);
				}
			}
		});
		
		sortItem.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				LinkedListType table = database.sort();
				
				if(table != null)
				{
					showTablePanel(tablePanel, table);
				}
			}
		});
		
		mainMenu.add(defineItem);
		mainMenu.add(enterItem);
		mainMenu.addSeparator();
		mainMenu.add(browseItem);
		mainMenu.add(searchItem);
		mainMenu.add(modifyItem);
		mainMenu.add(deleteItem);
		mainMenu.addSeparator();
		mainMenu.add(sortItem);
		
		JMenu fileMenu = new JMenu("File");
		JMenuItem saveItem = new JMenuItem("Save");
		JMenuItem loadItem = new JMenuItem("Load");
		
		saveItem.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				database.save();
			}
		});
		
		loadItem.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				database.load();
			}
		});
		
		fileMenu.add(saveItem);
		fileMenu.add(loadItem);
		
		menuBar.add(mainMenu);
		menuBar.add(fileMenu);
		
		return menuBar;
	}
	
	public JScrollPane initTreePanel()
	{
		DefaultMutableTreeNode root = new DefaultMutableTreeNode("Database");
		DefaultTreeModel model = new DefaultTreeModel(root);
		JTree tree = new JTree(model);
		this.treePanel = tree;
		
		JScrollPane treeScroll = new JScrollPane(tree);
		
		return treeScroll;
	}
	
	public void addTableIntoTreePanel(JTree tree, LinkedListType table)
	{
		if(tree == null)
		{
			tree = this.treePanel;
		}
		
		DefaultMutableTreeNode tableRoot = new DefaultMutableTreeNode(table.getName());
		
		String[] fieldNames = table.getFieldName();
		int fieldSize = table.getFieldSize();
		int[] fieldTypes = table.getFieldType();
		
		for(int i = 0; i < fieldSize; i++)
		{
			String fieldName = fieldNames[i];
			int fieldTypeIndex = fieldTypes[i];
			String fieldType = this.database.TYPE[fieldTypeIndex];
			
			DefaultMutableTreeNode fieldNode = new DefaultMutableTreeNode(fieldName + " (" + fieldType + ")");
			
			tableRoot.add(fieldNode);
		}
		
		DefaultTreeModel model = (DefaultTreeModel) tree.getModel();
		DefaultMutableTreeNode root = (DefaultMutableTreeNode) model.getRoot();
		root.add(tableRoot);
		
		tree.setModel(model);
		
		tree.updateUI();
	}
	
	public JScrollPane initTablePanel(Object rowData[][], String columnNames[])
	{
		DefaultTableModel model = new DefaultTableModel(rowData, columnNames);
		
		JTable table = new JTable(model);
		this.tablePanel = table;
		
		JScrollPane tableScroll = new JScrollPane(table);
		
		this.setStateBar(" " + 0 + " 개의 레코드가 발견되었습니다.");
		
		return tableScroll;
	}
	
	public void showTablePanel(JTable jTable, LinkedListType table)
	{
		if(jTable == null)
		{
			jTable = this.tablePanel;
		}
		
		Object[] columnNames = table.getFieldName();
		Object[][] rowData = table.getDataFromObject();
		
		DefaultTableModel model = new DefaultTableModel(rowData, columnNames);
		jTable.setModel(model);
		jTable.updateUI();
		
		database.currentTable = table;
		this.setStateBar(" " + table.recordSize + " 개의 레코드가 발견되었습니다.");
	}
	
	public JPanel initStateBar(String status)
	{
		JPanel statusPanel = new JPanel();

		statusPanel.setLayout(new BoxLayout(statusPanel, BoxLayout.X_AXIS));
		
		JLabel statusLabel = new JLabel("  " + status);
		statusPanel.add(statusLabel);
		
		this.stateBar = statusLabel;
		
		statusPanel.setBorder(new BevelBorder(BevelBorder.LOWERED));
		
		return statusPanel;
	}
	
	public void setStateBar(String state)
	{
		this.stateBar.setText(state);
	}
	
	public static void main(String[] args)
	{
		Main main = new Main();
	}
}
