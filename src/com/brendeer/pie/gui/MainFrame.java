package com.brendeer.pie.gui;

import com.brendeer.pie.core.FilterNode;
import com.brendeer.pie.core.FilterProgram;
import com.brendeer.pie.core.PluginContainer;
import com.brendeer.pie.file.FileReader;
import com.brendeer.pie.file.FileSaver;
import com.brendeer.pie.filter.Filter;
import com.brendeer.pie.filter.FilterFactory;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Point2D;
import java.io.File;
import java.util.List;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenuItem;
import javax.swing.filechooser.FileNameExtensionFilter;

/**
 * This class was generated by the NetBeans Swing JFrame editor. Please be
 * careful when editing this one manually, because changes in some parts of this
 * file will get overwritten easily
 *
 * @author Erik
 */
public class MainFrame extends JFrame {

	private static FileNameExtensionFilter piemlFilter
			= new FileNameExtensionFilter(
					".pieml files", "pieml");

	/**
	 * Creates new form MainFrame
	 */
	private MainFrame() {
		initComponents();
		setExtendedState(JFrame.MAXIMIZED_BOTH);
	}

	public MainFrame(FilterProgram program, boolean refreshProgram) {
		this();
		main(null);
		editPanel1.setProgram(program);
		File origin = program.getOrigin();
		if (origin != null) {
			piemlFileChooser.setCurrentDirectory(origin.getParentFile());
		}
		loadFilterList();
		if (refreshProgram) {
			editPanel1.refreshInputs();
		}
	}

	/**
	 * fill the JPopupMenu with all the plugins that are ready to be used
	 */
	private void loadFilterList() {
		List<String> names = PluginContainer.getAllNames();
		for (String name : names) {
			FilterFactory plugin = PluginContainer.getPlugin(name);
			JMenuItem item = new JMenuItem(plugin.createInstance().getDisplayName()); //todo something better here
			item.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					Filter filter = plugin.createInstance();
					Point2D.Float size = filter.getDisplaySize();
					FilterNode n = new FilterNode(name, filter,
							editPanel1.getPos().x, editPanel1.getPos().y,
							size.x, size.y);
					n.setSelected(true);
					editPanel1.getProgram().addNode(n);
					editPanel1.repaint();
				}
			});
			filterAddingList.add(item);
		}
	}

	/**
	 * display the "open file" - dialogue
	 */
	private void open() {
		piemlFileChooser.setDialogTitle("Open a pie");
		piemlFileChooser.setDialogType(JFileChooser.OPEN_DIALOG);
		int openReturn = piemlFileChooser.showOpenDialog(editPanel1);
		if (openReturn == JFileChooser.APPROVE_OPTION) {
			FilterProgram prog = FileReader.decodeXML(piemlFileChooser.getSelectedFile());
			FilterProgram old = editPanel1.getProgram();
			if (old != null) {
				old.destroy();
			}
			editPanel1.setProgram(prog);
			editPanel1.refreshProgram();
		}
	}

	/**
	 * display the "save file as" - dialogue
	 */
	private void saveAs() {
		piemlFileChooser.setDialogTitle("Save your pie");
		piemlFileChooser.setDialogType(JFileChooser.SAVE_DIALOG);
		int saveReturn = piemlFileChooser.showSaveDialog(editPanel1);
		if (saveReturn == JFileChooser.APPROVE_OPTION) {
			File saveFile = piemlFileChooser.getSelectedFile();
			if (!saveFile.exists()) {
				if (!saveFile.getName().endsWith(".pieml")) {
					//add the suffix if not there
					saveFile = new File(saveFile.getParentFile(), saveFile.getName() + ".pieml");
				}
			}
			FileSaver.saveXML(editPanel1.getProgram(), saveFile);
		}
	}

	/**
	 * This method is called from within the constructor to initialize the form.
	 * WARNING: Do NOT modify this code. The content of this method is always
	 * regenerated by the Form Editor.
	 */
	@SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        filterAddingList = new javax.swing.JPopupMenu();
        piemlFileChooser = new javax.swing.JFileChooser();
        editPanel1 = new com.brendeer.pie.gui.EditPanel();
        jMenuBar1 = new javax.swing.JMenuBar();
        jMenu1 = new javax.swing.JMenu();
        jMenuItem1 = new javax.swing.JMenuItem();
        jMenuItem7 = new javax.swing.JMenuItem();
        jMenuItem2 = new javax.swing.JMenuItem();
        jMenu2 = new javax.swing.JMenu();
        jMenuItem5 = new javax.swing.JMenuItem();
        jMenuItem6 = new javax.swing.JMenuItem();
        jMenu3 = new javax.swing.JMenu();
        jMenuItem3 = new javax.swing.JMenuItem();
        jMenuItem8 = new javax.swing.JMenuItem();
        jMenu4 = new javax.swing.JMenu();
        jMenuItem4 = new javax.swing.JMenuItem();

        piemlFileChooser.setFileFilter(piemlFilter);
        piemlFileChooser.setToolTipText("");

        setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        setTitle("ProgressiveImageEditor");
        setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        setMinimumSize(new java.awt.Dimension(100, 100));
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
        });

        editPanel1.addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
            public void mouseDragged(java.awt.event.MouseEvent evt) {
                editPanel1MouseDragged(evt);
            }
            public void mouseMoved(java.awt.event.MouseEvent evt) {
                editPanel1MouseMoved(evt);
            }
        });
        editPanel1.addMouseWheelListener(new java.awt.event.MouseWheelListener() {
            public void mouseWheelMoved(java.awt.event.MouseWheelEvent evt) {
                editPanel1MouseWheelMoved(evt);
            }
        });
        editPanel1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                editPanel1MouseClicked(evt);
            }
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                editPanel1MouseReleased(evt);
            }
        });

        javax.swing.GroupLayout editPanel1Layout = new javax.swing.GroupLayout(editPanel1);
        editPanel1.setLayout(editPanel1Layout);
        editPanel1Layout.setHorizontalGroup(
            editPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 524, Short.MAX_VALUE)
        );
        editPanel1Layout.setVerticalGroup(
            editPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 350, Short.MAX_VALUE)
        );

        jMenu1.setText("File");

        jMenuItem1.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_O, java.awt.event.InputEvent.CTRL_MASK));
        jMenuItem1.setText("Open...");
        jMenuItem1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem1ActionPerformed(evt);
            }
        });
        jMenu1.add(jMenuItem1);

        jMenuItem7.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_S, java.awt.event.InputEvent.CTRL_MASK));
        jMenuItem7.setText("Save");
        jMenuItem7.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem7ActionPerformed(evt);
            }
        });
        jMenu1.add(jMenuItem7);

        jMenuItem2.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_S, java.awt.event.InputEvent.SHIFT_MASK | java.awt.event.InputEvent.CTRL_MASK));
        jMenuItem2.setText("Save as...");
        jMenuItem2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem2ActionPerformed(evt);
            }
        });
        jMenu1.add(jMenuItem2);

        jMenuBar1.add(jMenu1);

        jMenu2.setText("Edit");

        jMenuItem5.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_A, java.awt.event.InputEvent.CTRL_MASK));
        jMenuItem5.setText("(de)select all");
        jMenuItem5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem5ActionPerformed(evt);
            }
        });
        jMenu2.add(jMenuItem5);

        jMenuItem6.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_X, java.awt.event.InputEvent.CTRL_MASK));
        jMenuItem6.setText("delete selected");
        jMenuItem6.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem6ActionPerformed(evt);
            }
        });
        jMenu2.add(jMenuItem6);

        jMenuBar1.add(jMenu2);

        jMenu3.setText("Render");

        jMenuItem3.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_R, java.awt.event.InputEvent.CTRL_MASK));
        jMenuItem3.setText("Full refresh");
        jMenuItem3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem3ActionPerformed(evt);
            }
        });
        jMenu3.add(jMenuItem3);

        jMenuItem8.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_R, java.awt.event.InputEvent.SHIFT_MASK));
        jMenuItem8.setText("Input refresh");
        jMenuItem8.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem8ActionPerformed(evt);
            }
        });
        jMenu3.add(jMenuItem8);

        jMenuBar1.add(jMenu3);

        jMenu4.setText("Filter");

        jMenuItem4.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_A, java.awt.event.InputEvent.SHIFT_MASK));
        jMenuItem4.setText("add...");
        jMenuItem4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem4ActionPerformed(evt);
            }
        });
        jMenu4.add(jMenuItem4);

        jMenuBar1.add(jMenu4);

        setJMenuBar(jMenuBar1);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(editPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(editPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

	//
	// These methods below here may be altered by content, but not by name
	//

    private void jMenuItem1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem1ActionPerformed
		open();
    }//GEN-LAST:event_jMenuItem1ActionPerformed

    private void jMenuItem2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem2ActionPerformed
		saveAs();
    }//GEN-LAST:event_jMenuItem2ActionPerformed

    private void editPanel1MouseWheelMoved(java.awt.event.MouseWheelEvent evt) {//GEN-FIRST:event_editPanel1MouseWheelMoved
		editPanel1.onMouseWheel(evt);
    }//GEN-LAST:event_editPanel1MouseWheelMoved

    private void editPanel1MouseDragged(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_editPanel1MouseDragged
		editPanel1.onMouseDrag(evt);
    }//GEN-LAST:event_editPanel1MouseDragged

    private void editPanel1MouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_editPanel1MouseReleased
		editPanel1.onMouseUp(evt);
    }//GEN-LAST:event_editPanel1MouseReleased

    private void editPanel1MouseMoved(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_editPanel1MouseMoved
		editPanel1.onMouseMove(evt);
    }//GEN-LAST:event_editPanel1MouseMoved

    private void editPanel1MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_editPanel1MouseClicked
		editPanel1.onMouseClick(evt);
    }//GEN-LAST:event_editPanel1MouseClicked

    private void jMenuItem3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem3ActionPerformed
		editPanel1.refreshProgram();
    }//GEN-LAST:event_jMenuItem3ActionPerformed

    private void jMenuItem4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem4ActionPerformed
		filterAddingList.show(editPanel1, getMousePosition().x, getMousePosition().y);
    }//GEN-LAST:event_jMenuItem4ActionPerformed

    private void jMenuItem5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem5ActionPerformed
		editPanel1.de_selectAll();
    }//GEN-LAST:event_jMenuItem5ActionPerformed

    private void jMenuItem6ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem6ActionPerformed
		editPanel1.deleteSelected();
    }//GEN-LAST:event_jMenuItem6ActionPerformed

    private void jMenuItem7ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem7ActionPerformed
		if (!editPanel1.getProgram().save()) {
			//if the automatic saving of program does not work, try with "save as"
			saveAs();
		}
    }//GEN-LAST:event_jMenuItem7ActionPerformed

    private void formWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosing
		editPanel1.getProgram().destroy();
		System.exit(0);
    }//GEN-LAST:event_formWindowClosing

    private void jMenuItem8ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem8ActionPerformed
		editPanel1.refreshInputs();
    }//GEN-LAST:event_jMenuItem8ActionPerformed

	/**
	 * @param args the command line arguments
	 */
	public static void main(String args[]) {
		/* Set the Nimbus look and feel */
		//<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
		 * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
		 */
		try {
			for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
				if ("Nimbus".equals(info.getName())) {
					javax.swing.UIManager.setLookAndFeel(info.getClassName());
					break;
				}
			}
		} catch (ClassNotFoundException ex) {
			java.util.logging.Logger.getLogger(MainFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
		} catch (InstantiationException ex) {
			java.util.logging.Logger.getLogger(MainFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
		} catch (IllegalAccessException ex) {
			java.util.logging.Logger.getLogger(MainFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
		} catch (javax.swing.UnsupportedLookAndFeelException ex) {
			java.util.logging.Logger.getLogger(MainFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
		}
		//</editor-fold>

	}

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private com.brendeer.pie.gui.EditPanel editPanel1;
    private javax.swing.JPopupMenu filterAddingList;
    private javax.swing.JMenu jMenu1;
    private javax.swing.JMenu jMenu2;
    private javax.swing.JMenu jMenu3;
    private javax.swing.JMenu jMenu4;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JMenuItem jMenuItem1;
    private javax.swing.JMenuItem jMenuItem2;
    private javax.swing.JMenuItem jMenuItem3;
    private javax.swing.JMenuItem jMenuItem4;
    private javax.swing.JMenuItem jMenuItem5;
    private javax.swing.JMenuItem jMenuItem6;
    private javax.swing.JMenuItem jMenuItem7;
    private javax.swing.JMenuItem jMenuItem8;
    private javax.swing.JFileChooser piemlFileChooser;
    // End of variables declaration//GEN-END:variables
}
