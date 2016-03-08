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

import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.JPopupMenu;

import org.pathvisio.core.view.VPathwayElement;
import org.pathvisio.desktop.PvDesktop;
import org.pathvisio.desktop.plugin.Plugin;
import org.pathvisio.gui.PathwayElementMenuListener.PathwayElementMenuHook;

/** 
 * 
 * This class is a plug-in for a MAPPBuilder
 * The user can open a file who contains the list of the genes to draw them
 * or he can directly type the list of the genes
 * 
 */
public class MAPPBuilder implements Plugin {

	PvDesktop desktop;
	
	public void init(PvDesktop desktop) {
		this.desktop = desktop;
		desktop.registerMenuAction ("Plugins", mappBuilderAction);
		PathwayElementMenuHook mappBuilderHook = new PathwayElementMenuHook()
		{
			private MAPPBuilderAction mapp_action = new MAPPBuilderAction(false);
			public void pathwayElementMenuHook(VPathwayElement e, JPopupMenu menu) {
				menu.add(mapp_action);
			}
		};
		desktop.addPathwayElementMenuHook(mappBuilderHook);
	}

	public void done() {
		desktop.unregisterMenuAction("Plugins", mappBuilderAction);
	}

	private final MAPPBuilderAction mappBuilderAction = new MAPPBuilderAction(true);
	
	private class MAPPBuilderAction extends AbstractAction {
		
		private boolean n_pv;
		public MAPPBuilderAction(boolean b) {
			this.n_pv = b;
			putValue (NAME, "MAPP Builder");	
		}
		
		public void actionPerformed(ActionEvent e) {	
			MAPPBuilderFrame mappBuilderFrame = new MAPPBuilderFrame(desktop.getFrame(), MAPPBuilder.this, n_pv);
			mappBuilderFrame.setVisible(true);
		}
	}
}
