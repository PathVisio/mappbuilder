package org.pathvisio.mappbuilder;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import javax.swing.JOptionPane;

import org.bridgedb.DataSource;
import org.pathvisio.core.model.DataNodeType;
import org.pathvisio.core.model.ObjectType;
import org.pathvisio.core.model.Pathway;
import org.pathvisio.core.model.PathwayElement;
import org.pathvisio.desktop.PvDesktop;

/**
 * draws a new pathway with the provided data nodes
 * or extends an open pathway with the annotated data nodes
 * @author martina
 *
 */
public class PathwayBuilder {

	private PvDesktop desktop;

	public PathwayBuilder(PvDesktop desktop) {
		this.desktop = desktop;
	}

	/**
	 * creates a new pathway
	 * 
	 * @throws IOException
	 */
	public void createNewPathway(String geneNames, String searchText)
			throws IOException {
		if (desktop.getSwingEngine().canDiscardPathway()) {
			desktop.getSwingEngine().newPathway();
			create_MAPP(65, geneNames, searchText);
		}
	}

	public void drawMAPP(Pathway p, PathwayElement pel, int y, String name, String ID, String db_name) {
		pel = PathwayElement.createPathwayElement(ObjectType.DATANODE);
		pel.setDataNodeType(DataNodeType.GENEPRODUCT);
		pel.setMCenterX(65);
		pel.setMCenterY(y);
		pel.setMHeight(20);
		pel.setMWidth(80);
		pel.setTextLabel(name);
		pel.setElementID(ID);
		DataSource v;
		if (DataSource.getFullNames().contains(db_name)) {
			v = DataSource.getExistingByFullName(db_name);
		} else {
			v = DataSource.getExistingBySystemCode(db_name);
		}
		pel.setDataSource(v);
		p.add(pel);
	}

	public void create_MAPP(int y, String geneNames, String searchText) throws IOException {
		String[] geneList = geneNames.split("\r\n|\r|\n");
		Pathway pv = desktop.getSwingEngine().getEngine().getActivePathway();
		PathwayElement pe = null;
		if (searchText.equals("")) {
			for (int i = 0; i < geneList.length; i++) {
				if (!(geneList[i].equals(""))) {
					String[] att = geneList[i].split("\t");
					if (att.length == 1)
						drawMAPP(pv, pe, y, att[0], att[0], "");
					else if (att.length == 2)
						drawMAPP(pv, pe, y, att[0], att[1], "");
					else if (att.length == 3)
						drawMAPP(pv, pe, y, att[0], att[1], att[2]);
					else if (att.length > 3) {
						if (!(att[0].equals(""))) {
							drawMAPP(pv, pe, y, att[0], att[1], att[2]);
							JOptionPane.showMessageDialog(desktop.getFrame(), "You have more attributs in " + att[0]);
						}
					}
					y += 30;
				}
			}
		} else {
			String file = searchText;
			BufferedReader buff = null;

			buff = new BufferedReader(new FileReader(file));
			String line;
			while ((line = buff.readLine()) != null) {
				if (!(line.equals(""))) {
					String[] att = line.split("\t");
					if (att.length == 1)
						drawMAPP(pv, pe, y, att[0], "", "");
					else if (att.length == 2)
						drawMAPP(pv, pe, y, att[0], att[1], "");
					else if (att.length == 3) {
						if (att[1].equals(""))
							drawMAPP(pv, pe, y, att[0], "", att[2]);
						else
							drawMAPP(pv, pe, y, att[0], att[1], att[2]);
					} else if (att.length > 3) {
						if (!(att[0].equals(""))) {
							drawMAPP(pv, pe, y, att[0], att[1], att[2]);
							JOptionPane.showMessageDialog(desktop.getFrame(), "You have more attributs in " + att[0]);
						}
					}
					y += 30;
				}
			}
			buff.close();
		}
	}
}
