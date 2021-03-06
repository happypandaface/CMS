
/**
 * This started as:
 * A simple example showing how to use {@link FileDrop}
 * @author Robert Harder, rob@iharder.net
 * but I've added like... a lot of liness...
 * scottgriffy@gmail.com
 * but credit goes to that guy for the filedrop code.
 */
 
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ImageIcon;
import javax.swing.JTextArea;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.JPasswordField;
import javax.swing.JButton;
import javax.swing.JList;
import javax.swing.BorderFactory;
import javax.swing.border.Border;
import javax.swing.DefaultListModel;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Group;
import javax.swing.GroupLayout.Group;
import javax.swing.UIManager;
import javax.swing.JOptionPane;

import javax.swing.event.ListSelectionListener;
import javax.swing.event.ListSelectionEvent;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.Color;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.Image;
import java.awt.Dimension;

import java.io.IOException;
import java.io.File;
import java.io.PrintWriter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.InputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.FileNotFoundException;

import java.util.List;
import java.util.Iterator;
import java.util.ArrayList;
import java.util.Date;

import org.apache.commons.net.ftp.*;
import org.apache.commons.net.PrintCommandListener;
import org.apache.commons.io.FileUtils;

import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.json.JsonWriter;
import javax.json.JsonReader;
import javax.json.JsonArray;

class RegistryData
{
	List<ItemData> items;
	String str;
	
	public RegistryData()
	{
		items = new ArrayList<ItemData>();
	}
	
	public boolean checkItemName(String s)
	{
		Iterator<ItemData> itemIter = items.iterator();
		while (itemIter.hasNext())
		{
			ItemData item = itemIter.next();
			if (item.name.equals(s))
				return true;
		}
		return false;
	}
	
	public ItemData getItemWithName(String s)
	{
		Iterator<ItemData> itemIter = items.iterator();
		while (itemIter.hasNext())
		{
			ItemData item = itemIter.next();
			if (item.name.equals(s))
				return item;
		}
		return null;
	}
	
	public boolean removeItemWithName(String s)
	{
		for (int i = 0; i < items.size(); ++i)
		{
			ItemData item = items.get(i);
			if (item.name.equals(s))
			{
				items.remove(i);
				return true;
			}
		}
		return false;
	}
	
	public boolean setTo(JsonObject jo)
	{
		items = new ArrayList<ItemData>();
		JsonArray itemsJA = jo.getJsonArray("items");
		for (int i = 0; i < itemsJA.size(); ++i)
		{
			ItemData id = new ItemData();
			id.setTo(itemsJA.getJsonObject(i));
			items.add(id);
		}
		return true;
	}
	
	public JsonObjectBuilder getJsonObjectBuilder()
	{
		JsonObjectBuilder objBuild = Json.createObjectBuilder();
		JsonArrayBuilder arrBuild = Json.createArrayBuilder();
		Iterator<ItemData> itemIter = items.iterator();
		while (itemIter.hasNext())
		{
			ItemData item = itemIter.next();
			arrBuild.add(item.getJsonObjectBuilder());
		}
		objBuild.add("items", arrBuild);
		return objBuild;
	}
	
	public List<ItemData> getOfType(String typ)
	{
		List<ItemData> itemsOfType = new ArrayList<ItemData>();
		Iterator<ItemData> itemIter = items.iterator();
		while (itemIter.hasNext())
		{
			ItemData item = itemIter.next();
			System.out.println(item.type);
			if (item.type.equals(typ))
				itemsOfType.add(item);
		}
		return itemsOfType;
	}
}

class ItemData
{
	String type;
	String name;
	String description;
	List<String> images;
	String button;
	
	public ItemData()
	{
		type = "";
		name = "";
		description = "";
		images = new ArrayList<String>();
		button = "";
	}
	
	public ItemData(String typ, String nme, String desc, List<String> imgs, String butt)
	{
		type = typ;
		name = nme;
		description = desc;
		images = imgs;
		button = butt;
	}
	
	public boolean setTo(JsonObject jo)
	{
		images = new ArrayList<String>();
		JsonArray imagesJA = jo.getJsonArray("images");
		for (int i = 0; i < imagesJA.size(); ++i)
		{
			images.add(imagesJA.getString(i));
		}
		name = jo.getString("name");
		type = jo.getString("type");
		description = jo.getString("description");
		button = jo.getString("button");
		return true;
	}
	
	public JsonObjectBuilder getJsonObjectBuilder()
	{
		JsonObjectBuilder objBuild = Json.createObjectBuilder();
		JsonArrayBuilder arrBuild = Json.createArrayBuilder();
		objBuild.add("name", name);
		objBuild.add("type", type);
		objBuild.add("description", description);
		Iterator<String> imgIter = images.iterator();
		while (imgIter.hasNext())
		{
			String img = imgIter.next();
			arrBuild.add(img);
		}
		objBuild.add("images", arrBuild);
		objBuild.add("button", button);
		return objBuild;
	}
}

class LocalConfig
{
	String site = "";
	String path = "";
	String user = "";
	
	public boolean setTo(JsonObject jo)
	{
		site = jo.getString("site");
		path = jo.getString("path");
		user = jo.getString("user");
		return true;
	}
	
	public JsonObjectBuilder getJsonObjectBuilder()
	{
		JsonObjectBuilder objBuild = Json.createObjectBuilder();
		objBuild.add("site", site);
		objBuild.add("path", path);
		objBuild.add("user", user);
		return objBuild;
	}
}

class ImageInList
{
	String localPath;
	String name;
	
	public ImageInList(String name, String localPath)
	{
		this.localPath = localPath;
		this.name = name;
	}
	
	public ImageInList(File f)
	{
		try
		{
			this.localPath = f.getCanonicalPath();
			this.name = f.getName();
		}catch(IOException e)
		{
			System.out.println(e.toString());
		}
	}
	
	@Override
	public String toString()
	{
		return this.name;
	}
}

class TemplateFeature
{
	String type;
	
	public boolean setTo(JsonObject jo)
	{
		type = jo.getString("type");
		return true;
	}
	
	public JsonObjectBuilder getJsonObjectBuilder()
	{
		JsonObjectBuilder objBuild = Json.createObjectBuilder();
		objBuild.add("type", type);
		return objBuild;
	}
}

class Template
{
	private String name;
	List<TemplateFeature> features;
	JPanel panel;
	
	public Template()
	{
		features = new ArrayList<TemplateFeature>();
	}
	
	public String getTemplateName()
	{
		return name;
	}
	
	public boolean setTo(JsonObject jo)
	{
		features = new ArrayList<TemplateFeature>();
		JsonArray featsJA = jo.getJsonArray("features");
		for (int i = 0; i < featsJA.size(); ++i)
		{
			TemplateFeature tf = new TemplateFeature();
			tf.setTo(featsJA.getJsonObject(i));
			features.add(tf);
		}
		name = jo.getString("name");
		System.out.println(name);
		return true;
	}
	
	public JsonObjectBuilder getJsonObjectBuilder()
	{
		JsonObjectBuilder objBuild = Json.createObjectBuilder();
		return objBuild;
	}
	
	JTextField productNameField;
	JTextArea productDescField;
	DefaultListModel dlmImg;
	JLabel imageLabel;
	JList imageList;
	DefaultListModel dlmImgNone;
	
	public JPanel buildPanel(final MainGUI mainGUI)
	{
		JPanel productEdit = new JPanel();
		
		GroupLayout peLayout = new GroupLayout(productEdit);
		productEdit.setLayout(peLayout);
		peLayout.setAutoCreateGaps(true);
		peLayout.setAutoCreateContainerGaps(true);
		
		JPanel productDescPanel = new JPanel();
		
		GroupLayout descLayout = new GroupLayout(productDescPanel);
		productDescPanel.setLayout(descLayout);
		descLayout.setAutoCreateGaps(true);
		descLayout.setAutoCreateContainerGaps(true);
		
		final JTextField productNameField = new JTextField(20);
		this.productNameField = productNameField;
		productNameField.setSize(100, 30);
		productNameField.setText("");
		productNameField.setEnabled(false);
		JLabel nameLabel = new JLabel("Product Name:");
		//productNamePanel.add(new JLabel("product name:"), BorderLayout.NORTH);
		//productNamePanel.add(productNameField, BorderLayout.NORTH);
		
		final JButton productRenameButton = new JButton("Rename product");
		productRenameButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent actionE)
			{
				productRenameButton.setEnabled(false);
				
				String lastName = productNameField.getText();
				
				if (lastName.length() == 0)
				{
					JOptionPane.showMessageDialog(mainGUI.frame, "You haven't loaded a product!");
				}else
				{
					Object[] possibleValues = {lastName};
					Object selectedValue = JOptionPane.showInputDialog(mainGUI.frame,"Choose one", "Input",JOptionPane.OK_CANCEL_OPTION, null, null, possibleValues[0]);
					String newName = ((String)selectedValue);
					if (newName == null)
					{
						
					}else
					{
						if (newName.length() == 0)
						{
							JOptionPane.showMessageDialog(mainGUI.frame, "Enter a product name!");
						}else
						{
							if (mainGUI.workingRegistry.checkItemName(newName))
							{
								JOptionPane.showMessageDialog(mainGUI.frame, "An item with that name already exists!");
							}else
							{
								ItemData id = new ItemData();
								
								id = mainGUI.workingRegistry.getItemWithName(lastName);
								id.name = newName;
								mainGUI.rewriteRegistry();
								mainGUI.refreshProductsList();
								productNameField.setText(newName);
							}
						}
					}
				}
				productRenameButton.setEnabled(true);
			}
		});
		
		final JTextArea productDescField = new JTextArea(15, 40);
		this.productDescField = productDescField;
		productDescField.setWrapStyleWord(true); 
		productDescField.setLineWrap(true); 
		JScrollPane productDescFieldScrollPane = new JScrollPane(productDescField, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		productDescField.setSize(100, 100);
		productDescField.setText("put description here");
		JLabel descLabel = new JLabel("Description:");
		//productDescPanel.add(, BorderLayout.NORTH);
		//productDescPanel.add(productDescFieldScrollPane, BorderLayout.NORTH);
		
		descLayout.setHorizontalGroup(descLayout.createSequentialGroup()
			.addGroup(descLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
				.addGroup(descLayout.createSequentialGroup()
					.addGroup(descLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
						.addComponent(nameLabel)
					)
					.addGroup(descLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
						.addComponent(productNameField)
					)
					.addGroup(descLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
						.addComponent(productRenameButton)
					)
				)
				.addComponent(descLabel)
				.addComponent(productDescFieldScrollPane)
			)
		);
		descLayout.setVerticalGroup(descLayout.createSequentialGroup()
			.addGroup(descLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
				.addComponent(nameLabel)
				.addComponent(productNameField)
				.addComponent(productRenameButton)
			)
			.addGroup(descLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
				.addComponent(descLabel)
			)
			.addGroup(descLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
				.addComponent(productDescFieldScrollPane)
			)
		);
		
		JPanel imagesPanel = new JPanel();
		
		GroupLayout imgsLayout = new GroupLayout(imagesPanel);
		imagesPanel.setLayout(imgsLayout);
		imgsLayout.setAutoCreateGaps(true);
		imgsLayout.setAutoCreateContainerGaps(true);
	
		final JLabel imageLabel = new JLabel();
		this.imageLabel = imageLabel;
		JPanel imagePanel = new JPanel();
		imagePanel.setMinimumSize(new Dimension(300, 300));
		//final JLabel imageLabel = new JLabel();
		imageLabel.setIcon(new ImageIcon("noImage.png"));
		imagePanel.add(imageLabel);
		//panel.add(label, BorderLayout.CENTER);
		
		final JList imageList = new JList();
		this.imageList = imageList;
		JScrollPane imageListScrollPane = new JScrollPane(imageList, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		final DefaultListModel dlmImgNone = new DefaultListModel();
		this.dlmImgNone = dlmImgNone;
		dlmImgNone.addElement("No Images");
		final DefaultListModel dlmImg = new DefaultListModel();
		this.dlmImg = dlmImg;
		imageList.setMinimumSize(new Dimension(100, 100));
		imageList.setModel(dlmImgNone);
		
		JButton imgUp = new JButton("^");
		JButton imgDown = new JButton("v");
		JButton uploadImg = new JButton("upload");
		JButton removeImg = new JButton("remove");
		
		uploadImg.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent actionE)
			{
				if (!imageList.isSelectionEmpty())
				{
					ImageInList iil = ((ImageInList)imageList.getSelectedValue());
					try
					{
						FTPClient ftp = mainGUI.getLogin();
						if (ftp != null)
						{
							FTPFile[] files = ftp.listFiles("CMSimages");
							if (files.length == 0)
							{
								ftp.makeDirectory("CMSimages");
							}
							
							ftp.addProtocolCommandListener(new PrintCommandListener(new PrintWriter(System.out)));
							ftp.changeWorkingDirectory(mainGUI.localConfig.path);
							ftp.setFileType(FTP.BINARY_FILE_TYPE);
							
							FileInputStream fs = new FileInputStream(new File(iil.localPath));
							System.out.println("uploading image");
							ftp.storeFile("CMSimages/"+iil.name, fs);
							JOptionPane.showMessageDialog(mainGUI.frame, "Uploaded!");
							
							ftp.logout();
							ftp.disconnect();
						}
					}   // end try
					catch( IOException except )
					{
						System.out.println(except.toString());
						JOptionPane.showMessageDialog(mainGUI.frame, "Is your internet working?");
					}
				}
			}
		});
		removeImg.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent actionE)
			{
				if (!imageList.isSelectionEmpty())
				{
					int index = imageList.getSelectedIndex();
					DefaultListModel model = (DefaultListModel) imageList.getModel();
					model.remove(index);
					if (model.getSize() == 0)
						imageList.setModel(dlmImgNone);
					
					ImageIcon newIcon = new ImageIcon("noImage.png");
					imageLabel.setIcon(newIcon);
				}
			}
		});
		
		imagesPanel.add(imagePanel);
		imagesPanel.add(imageListScrollPane);
		
		imgsLayout.setHorizontalGroup(imgsLayout.createSequentialGroup()
			.addGroup(imgsLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
				.addComponent(imagePanel)
				.addGroup(imgsLayout.createSequentialGroup()
					.addGroup(imgsLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
						.addComponent(imgUp)
					)
					.addGroup(imgsLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
						.addComponent(imgDown)
					)
					.addGroup(imgsLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
						.addComponent(uploadImg)
					)
					.addGroup(imgsLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
						.addComponent(removeImg)
					)
				)
			)
			.addGroup(imgsLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
				.addComponent(imageListScrollPane)
			)
		);
		imgsLayout.setVerticalGroup(imgsLayout.createSequentialGroup()
			.addGroup(imgsLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
				.addComponent(imagePanel)
				.addComponent(imageListScrollPane)
			)
			.addGroup(imgsLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
				.addComponent(imgUp)
				.addComponent(imgDown)
				.addComponent(uploadImg)
				.addComponent(removeImg)
			)
		);
		
		imageList.addListSelectionListener(new ListSelectionListener() {
			@Override
			public void valueChanged(ListSelectionEvent arg0) {
				if (!arg0.getValueIsAdjusting()) {
					try
					{
						System.out.println(imageList.getSelectedValue().toString());
						ImageInList iil = ((ImageInList)imageList.getSelectedValue());
						File imgFile = new File(iil.localPath);
						ImageIcon imageIcon = new ImageIcon(imgFile.getCanonicalPath());
						System.out.println(imageIcon.getIconHeight() + ", " + imageIcon.getIconWidth());
						float widthRatio = imageIcon.getIconHeight() < imageIcon.getIconWidth() ? 1 :imageIcon.getIconWidth()/(float)imageIcon.getIconHeight();
						float heightRatio = imageIcon.getIconWidth() < imageIcon.getIconHeight() ? 1 :imageIcon.getIconHeight()/(float)imageIcon.getIconWidth();
						Image img = imageIcon.getImage();
						Image newimg = img.getScaledInstance((int)Math.floor(300*widthRatio), (int)Math.floor(300*heightRatio), Image.SCALE_SMOOTH);
						ImageIcon newIcon = new ImageIcon(newimg);
						imageLabel.setIcon(newIcon);
						
					}catch(Exception e)
					{
						imageList.clearSelection();
					}
				}
			}
		});
		
		new FileDrop( System.out, imageList,  new FileDrop.Listener()
		{   public void filesDropped( File[] files )
			{   for( int i = 0; i < files.length; i++ )
				{   try
					{
						System.out.println(files[i].getCanonicalPath() + "\n");
						//text.append( files[i].getCanonicalPath() + "\n" );
						File imgFile = files[i];
						File source = new File(files[i].getCanonicalPath());
						File desc = new File("CMSimages/"+files[i].getName());
						try {
							FileUtils.copyFile(source, desc);
							imgFile = new File("CMSimages/"+files[i].getName());
						} catch (IOException e) {
							e.printStackTrace();
						}
						
						ImageInList iil = new ImageInList(imgFile);
						dlmImg.addElement(iil);
						imageList.setModel(dlmImg);
						
						imageList.setSelectedValue(iil, true);
						
					}   // end try
					catch( IOException e ) {}
				}   // end for: through each dropped file
			}   // end filesDropped
		}); // end FileDrop.Listener
		
		final JButton uploadButton = new JButton("Upload product");
		uploadButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent actionE)
			{
				uploadButton.setEnabled(false);
				String name = productNameField.getText();
				String desc = productDescField.getText();
				if (name.length() == 0)
				{
					JOptionPane.showMessageDialog(mainGUI.frame, "Enter a product name!");
				}else
				{
					ItemData id = new ItemData();
					boolean rewritingRegistry = false;
					if (mainGUI.workingRegistry.checkItemName(name))
					{
						JOptionPane.showMessageDialog(mainGUI.frame, "There's already a product with that name!");
						int dialogButton = JOptionPane.YES_NO_OPTION;
						int dialogResult = JOptionPane.showConfirmDialog (mainGUI.frame, "Would you like to override product: \""+name+"\" ?", "Warning", dialogButton);
						if (dialogResult == JOptionPane.YES_OPTION)
						{
							rewritingRegistry = true;
							id = mainGUI.workingRegistry.getItemWithName(name);
						}else
						{
							
						}
					}else
					{
						rewritingRegistry = true;
						mainGUI.workingRegistry.items.add(id);
					}
					if (rewritingRegistry)
					{
							id.name = name;
							id.description = desc;
							id.images = new ArrayList<String>();
							DefaultListModel model = (DefaultListModel) imageList.getModel();
							for (int i = 0; i < model.getSize(); ++i)
							{
								try
								{
									ImageInList currImgInLst = (ImageInList)model.get(i);
									id.images.add(currImgInLst.name);
								}catch(ClassCastException e)
								{
									
								}
							}
							
							mainGUI.rewriteRegistry();
						
					}
				}
				uploadButton.setEnabled(true);
			}
		});
		
		peLayout.setHorizontalGroup(peLayout.createSequentialGroup()
			.addGroup(peLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
				.addGroup(peLayout.createSequentialGroup()
					.addGroup(peLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
						.addComponent(productDescPanel)
					)
					.addGroup(peLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
						.addComponent(imagesPanel)
					)
				)
				.addComponent(uploadButton)
			)
		);
		peLayout.setVerticalGroup(peLayout.createSequentialGroup()
			.addGroup(peLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
				.addComponent(productDescPanel)
				.addComponent(imagesPanel)
			)
			.addGroup(peLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
				.addComponent(uploadButton)
			)
		);
		return productEdit;
	}
	
	public String getName()
	{
		return productNameField.getText();
	}
	
	public void rename(String s)
	{
		productNameField.setText(s);
	}
	
	public void reset()
	{
		productNameField.setText("");
		productDescField.setText("put description here");
		dlmImg.clear();
		imageLabel.setIcon(new ImageIcon("noImage.png"));
		imageList.setModel(dlmImgNone);
	}
	
	public void setToDefault(ItemData id)
	{
		id.type = this.name;
	}
	
	public void setTo(MainGUI mainGUI, ItemData id)
	{
		productNameField.setText(id.name);
		productDescField.setText(id.description);
		dlmImg.clear();
		int imagesToDownload = 0;
		Iterator<String> imgdls = id.images.iterator();
		while (imgdls.hasNext())
		{
			String imgStr = imgdls.next();
			String fullImgStr = "CMSimages/"+imgStr;
			File localImgFile = new File(fullImgStr);
			if (localImgFile.exists() && !localImgFile.isDirectory())
			{
			}else
			{
				imagesToDownload++;
			}
		}
		if (imagesToDownload > 0)
			JOptionPane.showMessageDialog(mainGUI.frame, "Downloading "+imagesToDownload+" images, please be patient...\n(press \"OK\" first)");
		Iterator<String> imgs = id.images.iterator();
		while (imgs.hasNext())
		{
			String imgStr = imgs.next();
			String fullImgStr = "CMSimages/"+imgStr;
			File localImgFile = new File(fullImgStr);
			if (localImgFile.exists() && !localImgFile.isDirectory())
			{
			}else
			{
				try
				{
					FTPClient ftp = mainGUI.getLogin();
					if (ftp != null)
					{
						ftp.addProtocolCommandListener(new PrintCommandListener(new PrintWriter(System.out)));
						ftp.changeWorkingDirectory(mainGUI.localConfig.path);
						
						FTPFile[] files = ftp.listFiles(fullImgStr);
						if (files.length == 0)
						{
							JOptionPane.showMessageDialog(mainGUI.frame, "Server is missing: \""+ imgStr+"\" please upload this image");
						}else
						{
							ftp.setFileType(FTP.BINARY_FILE_TYPE);
							
							FileOutputStream outStream = new FileOutputStream(fullImgStr);
							ftp.retrieveFile(fullImgStr, outStream);
						}
						
						ftp.logout();
						ftp.disconnect();
					}
				}   // end try
				catch( IOException except )
				{
					System.out.println(except.toString());
					JOptionPane.showMessageDialog(mainGUI.frame, "Is your internet working?");
				}
			}
			ImageInList iil = new ImageInList(imgStr, fullImgStr);
			dlmImg.addElement(iil);
			imageList.setModel(dlmImg);
		}
		imageLabel.setIcon(new ImageIcon("noImage.png"));
		JOptionPane.showMessageDialog(mainGUI.frame, "You are now editing: "+ name);
	} 
}

class TemplateHolder
{
	List<Template> templates;
	String currentTemplate;
	
	public TemplateHolder()
	{
		templates = new ArrayList<Template>();
	}
	
	public void add(Template t)
	{
		templates.add(t);
	}
	
	public void setTemplate(String s)
	{
		currentTemplate = s;
	}
	
	public Template getCurrentTemplate()
	{
		if (currentTemplate == null)
			return null;
		Iterator<Template> tmplIter = templates.iterator();
		while (tmplIter.hasNext())
		{
			Template nextTempl = tmplIter.next();
			if (nextTempl.getTemplateName().equals(currentTemplate)) 
				return nextTempl;
		}
		return null;
	}
}

class MainGUI
{
	LocalConfig localConfig;
	JList templateList;
	DefaultListModel dlmTemplate;
	TemplateHolder templates;
	JFrame frame;
	RegistryData workingRegistry;
	JList products;
	JTextField ftpField;
	JTextField textField;
	JTextField pathField;
	JPasswordField passField;
	JPanel templatePanel;
	
	static String readFile(String filename)
	{
		File file = new File(filename); //for ex foo.txt
		return readFile(file);
	}
	
	static String readFile(File file)
	{
		String content = null;
		try {
			FileReader reader = new FileReader(file);
			char[] chars = new char[(int) file.length()];
			reader.read(chars);
			content = new String(chars);
			reader.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return content;
	}
	
	public void refreshProductsList()
	{
		DefaultListModel dlm = new DefaultListModel();
		List<ItemData> items = workingRegistry.getOfType(templates.getCurrentTemplate().getTemplateName());
		if (items.size() == 0)
			dlm.addElement("No Products");
		else
		{
			Iterator<ItemData> itemIter = items.iterator();
			while (itemIter.hasNext())
			{
				ItemData item = itemIter.next();
				dlm.addElement(item.name);
			}
		}
		products.setModel(dlm);
	}
	
	public void switchTemplate(String type)
	{
		System.out.println("switching to:"+type);
		templates.setTemplate(type);
		
		templatePanel.removeAll();
		templatePanel.add(templates.getCurrentTemplate().panel);
		templatePanel.setVisible(true);
		templatePanel.revalidate();
		templatePanel.repaint();
		
		frame.pack();
	}
	
	public boolean rewriteRegistry()
	{
		try
		{
			FTPClient ftp = this.getLogin();
			if (ftp != null)
			{
				ftp.addProtocolCommandListener(new PrintCommandListener(new PrintWriter(System.out)));
				ftp.changeWorkingDirectory(localConfig.path);
				//ftp.setFileType(FTP.BINARY_FILE_TYPE);
				ByteArrayOutputStream baos = new ByteArrayOutputStream();
				
				System.out.println("no registry exists... writing");
				StringWriter sw = new StringWriter();
				JsonWriter jw = Json.createWriter(sw);
				jw.write(workingRegistry.getJsonObjectBuilder().build());
				InputStream is = new ByteArrayInputStream(sw.toString().getBytes());
				ftp.storeFile("registry.txt", is);
				
				JOptionPane.showMessageDialog(frame, "Writing new registry!");
				
				ftp.logout();
				ftp.disconnect();
			}
			return true;
		}   // end try
		catch( IOException except )
		{
			System.out.println(except.toString());
			JOptionPane.showMessageDialog(frame, "Is your internet working?");
		}
		return false;
	}
	
	public MainGUI()
	{
		final MainGUI mainGUI = this;
		try
		{
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		}catch(Exception exp)
		{
			System.out.println(exp.toString());
		}
		// local config stuff
		localConfig = new LocalConfig();
		boolean localConfigNeedsWrite = false;
		File localConfFile = new File("localConf.json");
		if (localConfFile.exists() && !localConfFile.isDirectory())
		{
			String fileString = MainGUI.readFile("localConf.json");
			System.out.println(fileString);
			try
			{
				JsonReader jsonReader = Json.createReader(new StringReader(fileString));
				JsonObject jo = jsonReader.readObject();
				jsonReader.close();
				localConfig.setTo(jo);
			}   // end try
			catch( Exception except )
			{
				System.out.println(except.toString());
				System.out.println("localConf corrupted");
				File file2 = new File("localConf_corrupted_"+new Date().getTime()+".json");
				localConfFile.renameTo(file2);
				localConfigNeedsWrite = true;
			}
		}else
		{
			localConfigNeedsWrite = true;
		}
		
		if (localConfigNeedsWrite)
		{
			try
			{
				PrintWriter writer = new PrintWriter("localConf.json", "UTF-8");
				{
					StringWriter sw = new StringWriter();
					JsonWriter jw = Json.createWriter(sw);
					jw.write(localConfig.getJsonObjectBuilder().build());
					writer.print(sw.toString());
					writer.close();
				}
			}catch(IOException exp)
			{
				System.out.println(exp.toString());
			}
		}
		
		// loading templates from local file system and also template list
		templateList = new JList();
		dlmTemplate = new DefaultListModel();
		templateList.setModel(dlmTemplate);
		
		templates = new TemplateHolder();
		//templates.setTemplate("product");
		File templatesDir = new File("templates");
		if (templatesDir.exists() && templatesDir.isDirectory())
		{
			File[] files = templatesDir.listFiles();
			for (int i = 0; i < files.length; i++)
			{
				Template template = new Template();
				String fileString = MainGUI.readFile(files[i]);
				System.out.println("file:"+fileString);
				JsonReader jsonReader = Json.createReader(new StringReader(fileString));
				JsonObject jo = jsonReader.readObject();
				jsonReader.close();
				template.setTo(jo);
				template.panel = template.buildPanel(this);
				templates.add(template);
				dlmTemplate.addElement(template.getTemplateName());
			}
		}
		
		File theDir = new File("CMSimages");
		if (!theDir.exists())
		{
			System.out.println("creating directory: " + "CMSimages");
			boolean result = theDir.mkdir();
			if(result)
			{	
				System.out.println("DIR created");  
			}
		}
		
		frame = new JFrame( "CMS" );
		JPanel panel = new JPanel();
		frame.getContentPane().add(panel, BorderLayout.CENTER);
		//javax.swing.border.TitledBorder dragBorder = new javax.swing.border.TitledBorder( "Drop 'em" );
		
		workingRegistry = new RegistryData();
		
		/* this is an example:
		RegistryData rd = new RegistryData();
		List<String> imgs1 = new ArrayList<String>();
		imgs1.add("img1.jpg");
		imgs1.add("picture of a turtle.png");
		rd.items.add(new ItemData("hi", "what", imgs1, "button"));
		List<String> imgs2 = new ArrayList<String>();
		imgs2.add("img2.jpg");
		imgs2.add("lemur drinking a soda.png");
		rd.items.add(new ItemData("hi2", "what1", imgs2, "bu3tton"));
		
		StringWriter sw = new StringWriter();
		JsonWriter jw = Json.createWriter(sw);
		jw.write(rd.getJsonObjectBuilder().build());
		System.out.println(sw.toString());
		*/
		
		products = new JList();
		DefaultListModel dlm = new DefaultListModel();
		dlm.addElement("Not Connected");
		products.setModel(dlm);
		
		JPanel loginPanel = new JPanel();
		loginPanel.setLayout(new GridLayout(0, 1, 0, 0));
		Border blackLine = BorderFactory.createLineBorder(Color.black);
		loginPanel.setBorder(blackLine);
		
			JPanel sitePanel = new JPanel();
			ftpField = new JTextField(20);
			ftpField.setSize(100, 30);
			ftpField.setText(localConfig.site);
			sitePanel.add(new JLabel("site:"), BorderLayout.NORTH);
			sitePanel.add(ftpField, BorderLayout.NORTH);
			loginPanel.add(sitePanel, BorderLayout.NORTH);
			
			JPanel pathPanel = new JPanel();
			pathField = new JTextField(20);
			pathField.setSize(100, 30);
			pathField.setText(localConfig.path);
			pathPanel.add(new JLabel("path:"), BorderLayout.NORTH);
			pathPanel.add(pathField, BorderLayout.NORTH);
			loginPanel.add(pathPanel, BorderLayout.NORTH);
			
			JPanel unamePanel = new JPanel();
			textField = new JTextField(20);
			textField.setSize(100, 30);
			textField.setText(localConfig.user);
			unamePanel.add(new JLabel("username:"), BorderLayout.NORTH);
			unamePanel.add(textField, BorderLayout.NORTH);
			loginPanel.add(unamePanel, BorderLayout.NORTH);
			
			JPanel passPanel = new JPanel();
			passField = new JPasswordField(20);
			passField.setSize(100, 30);
			passPanel.add(new JLabel("password:"), BorderLayout.NORTH);
			passPanel.add(passField, BorderLayout.NORTH);
			loginPanel.add(passPanel, BorderLayout.NORTH);
			
			JPanel buttonPanel = new JPanel();
			final JButton loginButton = new JButton("login");
			loginButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent actionE)
				{
					loginButton.setEnabled(false);
					mainGUI.login();
					loginButton.setEnabled(true);
				}
			});
			buttonPanel.add(loginButton, BorderLayout.NORTH);
			loginPanel.add(buttonPanel, BorderLayout.NORTH);
		
		
		//split up from above
		JPanel productsPanel = new JPanel();
		JLabel productsTitle = new JLabel("Products");
		JLabel templatesTitle = new JLabel("Templates");
		//productsPanel.setLayout(new GridLayout(0, 1, 0, 0));
		
		JScrollPane productsScrollPane = new JScrollPane(products, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		
		JScrollPane templatesScrollPane = new JScrollPane(templateList, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		//productsScrollPanePanel.add(productsScrollPane);
		//productsPanel.add(new JScrollPane(products), BorderLayout.CENTER);
		JButton newProductButton = new JButton("new product");
		JButton editProductButton = new JButton("edit product");
		JButton deleteProductButton = new JButton("delete product");
		
		GroupLayout playout = new GroupLayout(productsPanel);
		productsPanel.setLayout(playout);
		playout.setAutoCreateGaps(true);
		playout.setAutoCreateContainerGaps(true);
		
		playout.setHorizontalGroup(playout.createSequentialGroup()
			.addGroup(playout.createParallelGroup(GroupLayout.Alignment.LEADING)
				.addComponent(templatesTitle)
				.addComponent(templatesScrollPane)
			)
			.addGroup(playout.createParallelGroup(GroupLayout.Alignment.LEADING)
				.addComponent(productsTitle)
				.addComponent(productsScrollPane)
				
				.addComponent(newProductButton)
				.addComponent(editProductButton)
				.addComponent(deleteProductButton)
			)
		);
		
		playout.setVerticalGroup(playout.createSequentialGroup()
			.addGroup(playout.createParallelGroup(GroupLayout.Alignment.BASELINE)
				.addComponent(productsTitle)
				.addComponent(templatesTitle)
			)
			.addGroup(playout.createParallelGroup(GroupLayout.Alignment.LEADING)
				.addComponent(templatesScrollPane)
				.addGroup(playout.createSequentialGroup()
					.addComponent(productsScrollPane)
					.addGroup(playout.createParallelGroup(GroupLayout.Alignment.LEADING)
						.addComponent(newProductButton)
					).addGroup(playout.createParallelGroup(GroupLayout.Alignment.LEADING)
						.addComponent(editProductButton)
					).addGroup(playout.createParallelGroup(GroupLayout.Alignment.LEADING)
						.addComponent(deleteProductButton)
					)
				)
			)
		);
		
		templateList.addListSelectionListener(new ListSelectionListener() {
			@Override
			public void valueChanged(ListSelectionEvent arg0) {
				if (!arg0.getValueIsAdjusting())
				{
					mainGUI.templateChange();
				}
			}
		});
		
		templatePanel = new JPanel();//templates.getCurrentTemplate().buildPanel(this);
		
		newProductButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent actionE)
			{
				((JButton)actionE.getSource()).setEnabled(false);
				mainGUI.newItem();
				((JButton)actionE.getSource()).setEnabled(true);
			}
		});
		editProductButton.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent actionE)
			{
				((JButton)actionE.getSource()).setEnabled(false);
				mainGUI.editItem();
				((JButton)actionE.getSource()).setEnabled(true);
			}
		});
		deleteProductButton.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent actionE)
			{
				((JButton)actionE.getSource()).setEnabled(false);
				mainGUI.deleteItem();
				((JButton)actionE.getSource()).setEnabled(true);
			}
		});
		
		GroupLayout layout = new GroupLayout(panel);
		panel.setLayout(layout);
		layout.setAutoCreateGaps(true);
		layout.setAutoCreateContainerGaps(true);
		
		layout.setHorizontalGroup(layout.createSequentialGroup()
			.addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
				.addGroup(layout.createSequentialGroup()
					.addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
						.addComponent(loginPanel)
					)
					.addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
						.addComponent(productsPanel)
					)
				)
				.addComponent(templatePanel)
			)
		);
		layout.setVerticalGroup(layout.createSequentialGroup()
			.addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
				.addComponent(loginPanel)
				.addComponent(productsPanel)
			)
			.addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
				.addComponent(templatePanel)
			)
		);
		
		panel.add(loginPanel, BorderLayout.NORTH);
		panel.add(productsPanel, BorderLayout.CENTER);
		
		frame.setBounds( 100, 100, 300, 400 );
		frame.pack();
		frame.setDefaultCloseOperation( frame.EXIT_ON_CLOSE );
		frame.setVisible(true);
	}
	
	public FTPClient getLogin()
	{
		try
		{
			FTPClient ftp = new FTPClient();
			ftp.connect(ftpField.getText());
			ftp.login(textField.getText(), new String(passField.getPassword()));
			int reply = ftp.getReplyCode();
			if(!FTPReply.isPositiveCompletion(reply))
			{
				ftp.disconnect();
				JOptionPane.showMessageDialog(frame, "Wrong password or your localConf file is wrong!");
				return null;
			}else
			{
				return ftp;
			}
		}
		catch( IOException except )
		{
			System.out.println(except.toString());
			JOptionPane.showMessageDialog(frame, "Is your internet working?");
			return null;
		}
	}
	
	public void login()
	{
		try
		{
			FTPClient ftp = this.getLogin();
			if (ftp != null)
			{
				ftp.addProtocolCommandListener(new PrintCommandListener(new PrintWriter(System.out)));
				ftp.changeWorkingDirectory(localConfig.path);
				//ftp.setFileType(FTP.BINARY_FILE_TYPE);
				ByteArrayOutputStream baos = new ByteArrayOutputStream();
				FTPFile[] files = ftp.listFiles("registry.txt");
				boolean writingNew = false;
				if (files.length == 0)
				{
					writingNew = true;
					JOptionPane.showMessageDialog(frame, "No registry!");
				}else
				{
					ftp.retrieveFile("registry.txt", baos);
					String retrievedFile = new String(baos.toByteArray(), "UTF-8");
					
					boolean worked = false;
					try
					{
						JsonReader jsonReader = Json.createReader(new StringReader(retrievedFile));
						JsonObject jo = jsonReader.readObject();
						workingRegistry.setTo(jo);
						worked = true;
					}   // end try
					catch( Exception except )
					{
						System.out.println(except.toString());
					}
					if (worked)
					{
						System.out.println("registry in-tact");
						JOptionPane.showMessageDialog(frame, "Successfully logged in!");
					}else
					{
						System.out.println("registry corrupted");
						writingNew = true;
						ftp.rename("registry.txt", "registry_corrupted_"+new Date().getTime()+".txt");
						JOptionPane.showMessageDialog(frame, "Registry corrupt!");
					}
				}
				if (writingNew)
				{
					System.out.println("no registry exists... writing");
					StringWriter sw = new StringWriter();
					JsonWriter jw = Json.createWriter(sw);
					jw.write(workingRegistry.getJsonObjectBuilder().build());
					InputStream is = new ByteArrayInputStream(sw.toString().getBytes());
					ftp.storeFile("registry.txt", is);
					JOptionPane.showMessageDialog(frame, "Writing new registry!");
				}
				ftp.logout();
				ftp.disconnect();
			}
		}   // end try
		catch( IOException except )
		{
			System.out.println(except.toString());
			JOptionPane.showMessageDialog(frame, "Is your internet working?");
		}
	}
	
	public void templateChange()
	{
		if (templateList.getSelectedValue() == null)
			System.out.println("bad template change (normal usually)");
		else
		{
			try
			{
				System.out.println(templateList.getSelectedValue());
				//templates.setTemplate(templateList.getSelectedValue().toString());
				this.switchTemplate(templateList.getSelectedValue().toString());
				this.refreshProductsList();
				//dlmTemplate.clear();
				templateList.clearSelection();
			}catch(Exception e)
			{
				System.out.println(e.toString());
				templateList.clearSelection();
				throw e;
			}
		}
	}
	
	public void newItem()
	{
		int dialogResult = JOptionPane.showConfirmDialog (frame, "This will clear all values below, continue?\n(This only erases changes before the last upload)", "Warning", JOptionPane.YES_NO_OPTION);
		if (dialogResult == JOptionPane.YES_OPTION)
		{
			Object selectedValue = JOptionPane.showInputDialog(frame, "Enter product name:", "Input", JOptionPane.OK_CANCEL_OPTION);
			String newProduct = ((String)selectedValue);
			
			String lastName = templates.getCurrentTemplate().getName();
			
			if (newProduct == null)
			{
				
			}else
			{
				if (newProduct.length() == 0)
				{
					JOptionPane.showMessageDialog(frame, "Enter a product name!");
				}else
				{
					if (workingRegistry.checkItemName(newProduct))
					{
						JOptionPane.showMessageDialog(frame, "An item with that name already exists!");
					}else
					{
						ItemData id = new ItemData();
						id.name = newProduct;
						
						templates.getCurrentTemplate().rename(newProduct);
						templates.getCurrentTemplate().reset();
						templates.getCurrentTemplate().setToDefault(id);
						
						workingRegistry.items.add(id);
						
						this.rewriteRegistry();
						this.refreshProductsList();
						
						JOptionPane.showMessageDialog(frame, "Item created: \""+newProduct+"\"");
					}
				}
			}
		}else
		{
			
		}
	}
	
	public void deleteItem()
	{
		if (!products.isSelectionEmpty())
		{
			String name = products.getSelectedValue().toString();
			if (workingRegistry.checkItemName(name))
			{
				int dialogResult = JOptionPane.showConfirmDialog (frame, "Are you sure you want to delete \""+name+"\"?", "Warning", JOptionPane.YES_NO_OPTION);
				if (dialogResult != JOptionPane.YES_OPTION)
				{
				}else
				{
					workingRegistry.removeItemWithName(name);
					this.rewriteRegistry();
					this.refreshProductsList();
					if (name.equals(templates.getCurrentTemplate().getName()))
						templates.getCurrentTemplate().reset();
					JOptionPane.showMessageDialog(frame, "\""+name+"\" deleted!");
					
				}
			}else
			{
				
			}
		}
	}
	
	public void editItem()
	{
		if (!products.isSelectionEmpty() && workingRegistry.checkItemName(products.getSelectedValue().toString()))
		{
			int dialogResult = JOptionPane.showConfirmDialog (frame, "This will change all values below, continue?\n(This only erases changes before the last upload)", "Warning", JOptionPane.YES_NO_OPTION);
			if (dialogResult != JOptionPane.YES_OPTION)
			{
			}else
			{
				String name = products.getSelectedValue().toString();
				ItemData id = workingRegistry.getItemWithName(name);
				templates.getCurrentTemplate().setTo(this, id);
			}
		}
	}
}

public class Example
{
	
	/** Runs a sample program that shows dropped files */
	public static void main( String[] args )
	{
		new MainGUI();
	}
	
	public Example()
	{
		
	}
}
