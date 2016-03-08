// MAPPBuilder plugin for PathVisio
// Copyright 2016 BiGCaT Bioinformatics
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
// http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.
//
package org.pathvisio.mappbuilder;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import org.pathvisio.core.model.Pathway;
import org.pathvisio.core.model.PathwayElement;
import org.pathvisio.gui.dialogs.OkCancelDialog;

import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

/**
 * Creation of the MAPPBuilder panel 
 */
class MAPPBuilderFrame extends OkCancelDialog {
	private final MAPPBuilder builder;
	private JTextField searchText;
	private JTextArea geneNamesText;
	private boolean MAPP_new_pv;
	private PathwayBuilder pwyBuilder;
	
	public MAPPBuilderFrame (JFrame parent, MAPPBuilder builder,boolean new_pv) {
		super(parent, "MAPP Builder", parent, true);
		this.builder = builder;
		this.MAPP_new_pv = new_pv;
		setDialogComponent(createDialogPane());
		setSize(500, 350);
		pwyBuilder = new PathwayBuilder(builder.desktop);
	}
	
	protected Component createDialogPane() {
	    FormLayout layout = new FormLayout (
	    		"pref, 4dlu, 150dlu, 4dlu, min",
	    		"40dlu, 1dlu, 20dlu, 1dlu, 100dlu");
	    JPanel panel = new JPanel(layout);
	    CellConstraints cc = new CellConstraints();
	    
	    JLabel searchSource = new JLabel("File to build ");
	    searchText = new JTextField();
	    final JButton browseButton = new JButton("Browse ");
	    final JFileChooser fc = new JFileChooser();
	    
	    browseButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (e.getSource() == browseButton) {
			        int returnVal = fc.showOpenDialog(MAPPBuilderFrame.this);
			        if (returnVal == JFileChooser.APPROVE_OPTION) {
			            File file = fc.getSelectedFile();       
			            searchText.setText(file.toString());
			        } else {
						searchText.setText("");
			        }
			   }
			}
		});
	   
	    panel.add(searchSource, cc.xy(1, 1));
	    panel.add(searchText, cc.xy(3, 1));
	    panel.add(browseButton, cc.xy(5, 1)); 
	    JLabel geneNames = new JLabel("<html> Enter the genes here <br> " +
	    		"(label [tab] ID [tab] DataSource) </html>");
	    geneNamesText = new JTextArea(7,10);
	    JScrollPane scrollingArea = new JScrollPane(geneNamesText);
	    panel.add(geneNames,cc.xy(3, 3));
	    panel.add(scrollingArea , cc.xy(3, 5));
	    geneNamesText.addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent e) {  
				if (e.getButton() == MouseEvent.BUTTON3) {  
					JPopupMenu m = processMouseEvent();
					if(m != null) {
						m.show(geneNamesText, e.getX(), e.getY());
					}
				}  
		    }  		    
	    }
	    );
		return panel;
	}
	
	public JPopupMenu processMouseEvent() {
		JPopupMenu menu = new JPopupMenu();
		JMenuItem copyItem = new JMenuItem("Copy" );
		copyItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent actionevent) {
				geneNamesText.copy();
			}
		});
		menu.add(copyItem);
		JMenuItem pasteItem = new JMenuItem("Paste" );
		pasteItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent actionevent) {
				geneNamesText.paste();
			}
		});
		menu.add(pasteItem);	
		return menu;
	}
	
	
	
	protected void okPressed() {
		try {
			if(this.builder.desktop.getSwingEngine().getEngine().hasVPathway()) {
				 int result = JOptionPane.showConfirmDialog(builder.desktop.getFrame(), "Do you want to extend this pathway?","", JOptionPane.YES_NO_OPTION);
				 if(result == 1) {
					 // do not extend the pathway but create a new one
					 pwyBuilder.createNewPathway(geneNamesText.getText(),searchText.getText());
				 } else {
					 // extend open pathway
					 int y = calculateV();
					 System.out.println(y);
					 pwyBuilder. create_MAPP(y, geneNamesText.getText(),searchText.getText());
				 }
			} else {
				if(MAPP_new_pv == true ){
					pwyBuilder.createNewPathway(geneNamesText.getText(),searchText.getText());
				} else{
					pwyBuilder.create_MAPP(65, geneNamesText.getText(),searchText.getText());
				}
			}
		} catch(IOException e) {
			JOptionPane.showMessageDialog(this, "Error in opening File" + e.getMessage());
		}
		super.okPressed();
	}
	
	/**
	 * checks for pathway element with highest Y value
	 * add new data nodes below the pathway
	 */
	private int calculateV() {
		int y = 65;
		Pathway pathway = this.builder.desktop.getSwingEngine().getEngine().getActivePathway();
		for(PathwayElement e : pathway.getDataObjects()) {
			double currY = e.getMCenterY() + e.getMHeight();
			if(currY > y) {
				y = (int) currY;
			}
		}
		return y+30;
	}

	
}