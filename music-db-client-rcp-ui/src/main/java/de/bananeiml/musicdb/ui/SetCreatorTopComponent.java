package de.bananeiml.musicdb.ui;

import de.bananeiml.musicdb.server.dao.core.DAOManager;
import de.bananeiml.musicdb.server.dao.entity.Key;
import de.bananeiml.musicdb.server.dao.entity.MixSet;
import de.bananeiml.musicdb.server.dao.entity.Title;
import de.bananeiml.musicdb.server.dao.entity.Title.FittingTitles;
import de.bananeiml.musicdb.server.dao.service.GeneralService;
import java.awt.Component;
import java.awt.EventQueue;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetEvent;
import java.awt.dnd.DropTargetListener;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.ListIterator;
import java.util.Properties;
import javax.swing.DefaultListCellRenderer;
import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import org.openide.util.NbBundle;
import org.openide.windows.TopComponent;
import org.netbeans.api.settings.ConvertAsProperties;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.util.ImageUtilities;
import org.openide.util.Lookup;
import org.openide.util.RequestProcessor;
import org.openide.util.WeakListeners;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Top component which displays something.
 */
@ConvertAsProperties(dtd = "-//de.bananeiml.musicdb.ui//SetCreator//EN",
autostore = false)
@TopComponent.Description(preferredID = "SetCreatorTopComponent",
iconBase = "de/bananeiml/musicdb/ui/set_list.png",
persistenceType = TopComponent.PERSISTENCE_ALWAYS)
@TopComponent.Registration(mode = "editor", openAtStartup = true)
@ActionID(category = "Window", id = "de.bananeiml.musicdb.ui.SetCreatorTopComponent")
@ActionReference(path = "Menu/Window" /*, position = 333 */)
@TopComponent.OpenActionRegistration(displayName = "#CTL_SetCreatorAction",
preferredID = "SetCreatorTopComponent")
public final class SetCreatorTopComponent extends TopComponent {
    
    private final transient SetListeners setL;
    private final transient TitleCellRenderer titleCellRenderer;
    
    public SetCreatorTopComponent() {
        setL = new SetListeners();
        titleCellRenderer = new TitleCellRenderer();
        
        initComponents();
        initComps();
        
        setName(NbBundle.getMessage(SetCreatorTopComponent.class, "CTL_SetCreatorTopComponent")); // NOI18N
        setToolTipText(NbBundle.getMessage(SetCreatorTopComponent.class, "HINT_SetCreatorTopComponent")); // NOI18N
        
        lstSet.addListSelectionListener(WeakListeners.create(ListSelectionListener.class, setL, lstSet));
    }
    
    private void initComps() {
        lstSet.setCellRenderer(titleCellRenderer);
        lstBase.setCellRenderer(titleCellRenderer);
        lstLower.setCellRenderer(titleCellRenderer);
        lstHigher.setCellRenderer(titleCellRenderer);
        lstPusher.setCellRenderer(titleCellRenderer);
        lstOpposite.setCellRenderer(titleCellRenderer);
    }
    
    private void updateFittingKeyTables(final Key key) {
        if(!EventQueue.isDispatchThread()){
            throw new IllegalStateException("may not call updateFittingKeyTables outside of EDT"); // NOI18N
        }
        
        final DefaultListModel baseModel = (DefaultListModel)lstBase.getModel();
        final DefaultListModel lowerModel = (DefaultListModel)lstLower.getModel();
        final DefaultListModel higherModel = (DefaultListModel)lstHigher.getModel();
        final DefaultListModel pusherModel = (DefaultListModel)lstPusher.getModel();
        final DefaultListModel oppModel = (DefaultListModel)lstOpposite.getModel();
        
        baseModel.removeAllElements();
        lowerModel.removeAllElements();
        higherModel.removeAllElements();
        pusherModel.removeAllElements();
        oppModel.removeAllElements();
        
        if(key != null){
            RequestProcessor.getDefault().execute(new Runnable() {

                @Override
                public void run() {
                    final DAOManager dao = Lookup.getDefault().lookup(DAOManager.class);
                    final GeneralService gs = dao.getGeneralService();
                    final FittingTitles ft = gs.getFittingTitles(key);
                    final NamedEntityComparator comp = new NamedEntityComparator();
                    
                    Collections.sort(ft.base, comp);
                    Collections.sort(ft.lower, comp);
                    Collections.sort(ft.higher, comp);
                    Collections.sort(ft.pusher, comp);
                    Collections.sort(ft.opposite, comp);
                    
                    applyFilter(ft);
                    
                    EventQueue.invokeLater(new Runnable() {

                        @Override
                        public void run() {
                            for(final Title title : ft.base){
                                baseModel.addElement(title);
                            }
                            for(final Title title : ft.lower){
                                lowerModel.addElement(title);
                            }
                            for(final Title title : ft.higher){
                                higherModel.addElement(title);
                            }
                            for(final Title title : ft.pusher){
                                pusherModel.addElement(title);
                            }
                            for(final Title title : ft.opposite){
                                oppModel.addElement(title);
                            }
                        }
                    });
                }
                
                private void applyFilter(final FittingTitles titles){
                    final int lowerBound = (Integer)spnLowerBound.getValue();
                    final int upperBound = (Integer)spnUpperBound.getValue();

                    final ListIterator<Title> baseIt = titles.base.listIterator();
                    while(baseIt.hasNext()){
                        final Title title = baseIt.next();
                        if(title.getBpm() != null && 
                                (lowerBound > title.getBpm() || upperBound < title.getBpm())){
                            baseIt.remove();
                        }
                    }

                    final ListIterator<Title> lowerIt = titles.lower.listIterator();
                    while(lowerIt.hasNext()){
                        final Title title = lowerIt.next();
                        if(title.getBpm() != null && 
                                (lowerBound > title.getBpm() || upperBound < title.getBpm())){
                            lowerIt.remove();
                        }
                    }

                    final ListIterator<Title> higherIt = titles.higher.listIterator();
                    while(higherIt.hasNext()){
                        final Title title = higherIt.next();
                        if(title.getBpm() != null && 
                                (lowerBound > title.getBpm() || upperBound < title.getBpm())){
                            higherIt.remove();
                        }
                    }

                    final ListIterator<Title> pusherIt = titles.pusher.listIterator();
                    while(pusherIt.hasNext()){
                        final Title title = pusherIt.next();
                        if(title.getBpm() != null && 
                                (lowerBound > title.getBpm() || upperBound < title.getBpm())){
                            pusherIt.remove();
                        }
                    }

                    final ListIterator<Title> oppIt = titles.opposite.listIterator();
                    while(oppIt.hasNext()){
                        final Title title = oppIt.next();
                        if(title.getBpm() != null && 
                                (lowerBound > title.getBpm() || upperBound < title.getBpm())){
                            oppIt.remove();
                        }
                    }
                }
            });
        }
    }
    
    private void applyMixSet(final MixSet mixSet) {
        if(!EventQueue.isDispatchThread()){
            throw new IllegalStateException("may not call applyMixSet outside of EDT"); // NOI18N
        }
        
        // TODO: ask for save
        
        final DefaultListModel model = (DefaultListModel)lstSet.getModel();
        model.clear();
        for(final Title title : mixSet.getTitles()){
            model.addElement(title);
        }
        
        lstSet.clearSelection();
        
        updateFittingKeyTables(null);
    }
    
    @Override
    public void componentOpened() {
        // TODO add custom code on component opening
    }

    @Override
    public void componentClosed() {
        // TODO add custom code on component closing
    }

    void writeProperties(final Properties p) {
        // better to version settings since initial version as advocated at
        // http://wiki.apidesign.org/wiki/PropertyFiles
        p.setProperty("version", "1.0");
        // TODO store your settings
    }

    void readProperties(final Properties p) {
        String version = p.getProperty("version");
        // TODO read your settings according to their version
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        setLayout(new java.awt.BorderLayout());

        jspSetCreator.setDividerLocation(300);

        pnlSet.setLayout(new java.awt.BorderLayout());

        lstSet.setBorder(javax.swing.BorderFactory.createTitledBorder(NbBundle.getMessage(SetCreatorTopComponent.class, "SetCreatorTopComponent.lstSet.border.title"))); // NOI18N
        lstSet.setModel(new DefaultListModel());
        jScrollPane1.setViewportView(lstSet);

        pnlSet.add(jScrollPane1, java.awt.BorderLayout.CENTER);

        jspSetCreator.setLeftComponent(pnlSet);

        pnlChoices.setLayout(new java.awt.GridBagLayout());

        pnlBpmFilter.setBorder(javax.swing.BorderFactory.createTitledBorder(NbBundle.getMessage(SetCreatorTopComponent.class, "SetCreatorTopComponent.pnlBpmFilter"))); // NOI18N
        pnlBpmFilter.setLayout(new java.awt.GridBagLayout());

        org.openide.awt.Mnemonics.setLocalizedText(lblLowerBound, NbBundle.getMessage(SetCreatorTopComponent.class, "SetCreatorTopComponent.lblLowerBound.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        pnlBpmFilter.add(lblLowerBound, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(lblUpperBound, NbBundle.getMessage(SetCreatorTopComponent.class, "SetCreatorTopComponent.lblUpperBound.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        pnlBpmFilter.add(lblUpperBound, gridBagConstraints);

        spnLowerBound.setMinimumSize(new java.awt.Dimension(50, 20));
        spnLowerBound.setPreferredSize(new java.awt.Dimension(50, 20));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        pnlBpmFilter.add(spnLowerBound, gridBagConstraints);

        spnUpperBound.setMinimumSize(new java.awt.Dimension(50, 20));
        spnUpperBound.setPreferredSize(new java.awt.Dimension(50, 20));
        spnUpperBound.setValue(Integer.valueOf(300));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        pnlBpmFilter.add(spnUpperBound, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(lblBPM1, NbBundle.getMessage(SetCreatorTopComponent.class, "SetCreatorTopComponent.lblBPM1.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 5, 5);
        pnlBpmFilter.add(lblBPM1, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(lblBPM2, NbBundle.getMessage(SetCreatorTopComponent.class, "SetCreatorTopComponent.lblBPM2.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 5, 5);
        pnlBpmFilter.add(lblBPM2, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        pnlChoices.add(pnlBpmFilter, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        pnlChoices.add(jSeparator1, gridBagConstraints);

        pnlSuggestions.setLayout(new java.awt.BorderLayout());

        jSplitPane1.setDividerLocation(350);
        jSplitPane1.setOrientation(javax.swing.JSplitPane.VERTICAL_SPLIT);

        pnlMinorSuggestions.setLayout(new java.awt.GridBagLayout());

        lstPusher.setBorder(javax.swing.BorderFactory.createTitledBorder(NbBundle.getMessage(SetCreatorTopComponent.class, "SetCreatorTopComponent.lstPusher.title.border"))); // NOI18N
        lstPusher.setModel(new DefaultListModel());
        lstPusher.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        jScrollPane5.setViewportView(lstPusher);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        pnlMinorSuggestions.add(jScrollPane5, gridBagConstraints);

        jSeparator4.setOrientation(javax.swing.SwingConstants.VERTICAL);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(10, 0, 10, 0);
        pnlMinorSuggestions.add(jSeparator4, gridBagConstraints);

        lstOpposite.setBorder(javax.swing.BorderFactory.createTitledBorder(NbBundle.getMessage(SetCreatorTopComponent.class, "SetCreatorTopComponent.lstOpposite.title.border"))); // NOI18N
        lstOpposite.setModel(new DefaultListModel());
        lstOpposite.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        jScrollPane6.setViewportView(lstOpposite);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        pnlMinorSuggestions.add(jScrollPane6, gridBagConstraints);

        jSplitPane1.setRightComponent(pnlMinorSuggestions);

        pnlMajorSuggestions.setLayout(new java.awt.GridBagLayout());

        lstLower.setBorder(javax.swing.BorderFactory.createTitledBorder(NbBundle.getMessage(SetCreatorTopComponent.class, "SetCreatorTopComponent.lstLower.title.border"))); // NOI18N
        lstLower.setModel(new DefaultListModel());
        lstLower.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        jScrollPane2.setViewportView(lstLower);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        pnlMajorSuggestions.add(jScrollPane2, gridBagConstraints);

        jSeparator2.setOrientation(javax.swing.SwingConstants.VERTICAL);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(10, 0, 10, 0);
        pnlMajorSuggestions.add(jSeparator2, gridBagConstraints);

        lstBase.setBorder(javax.swing.BorderFactory.createTitledBorder(NbBundle.getMessage(SetCreatorTopComponent.class, "SetCreatorTopComponent.lstBase.title.border"))); // NOI18N
        lstBase.setModel(new DefaultListModel());
        lstBase.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        jScrollPane3.setViewportView(lstBase);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        pnlMajorSuggestions.add(jScrollPane3, gridBagConstraints);

        jSeparator3.setOrientation(javax.swing.SwingConstants.VERTICAL);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(10, 0, 10, 0);
        pnlMajorSuggestions.add(jSeparator3, gridBagConstraints);

        lstHigher.setBorder(javax.swing.BorderFactory.createTitledBorder(NbBundle.getMessage(SetCreatorTopComponent.class, "SetCreatorTopComponent.lstHigher.title.border"))); // NOI18N
        lstHigher.setModel(new DefaultListModel());
        lstHigher.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        jScrollPane4.setViewportView(lstHigher);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        pnlMajorSuggestions.add(jScrollPane4, gridBagConstraints);

        jSplitPane1.setTopComponent(pnlMajorSuggestions);

        pnlSuggestions.add(jSplitPane1, java.awt.BorderLayout.CENTER);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        pnlChoices.add(pnlSuggestions, gridBagConstraints);

        pnlControls.setLayout(new java.awt.GridBagLayout());

        tbaActions.setRollover(true);

        org.openide.awt.Mnemonics.setLocalizedText(jButton1, NbBundle.getMessage(SetCreatorTopComponent.class, "SetCreatorTopComponent.jButton1.text")); // NOI18N
        jButton1.setFocusable(false);
        jButton1.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButton1.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });
        tbaActions.add(jButton1);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.ipadx = 87;
        gridBagConstraints.ipady = 23;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        pnlControls.add(tbaActions, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(lblName, NbBundle.getMessage(SetCreatorTopComponent.class, "SetCreatorTopComponent.lblName.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        pnlControls.add(lblName, gridBagConstraints);

        txtName.setText(NbBundle.getMessage(SetCreatorTopComponent.class, "SetCreatorTopComponent.txtName.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        pnlControls.add(txtName, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(lblDescription, NbBundle.getMessage(SetCreatorTopComponent.class, "SetCreatorTopComponent.lblDescription.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        pnlControls.add(lblDescription, gridBagConstraints);

        txaDescription.setColumns(20);
        txaDescription.setRows(5);
        jScrollPane7.setViewportView(txaDescription);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        pnlControls.add(jScrollPane7, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        pnlChoices.add(pnlControls, gridBagConstraints);

        jSeparator5.setOrientation(javax.swing.SwingConstants.VERTICAL);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        pnlChoices.add(jSeparator5, gridBagConstraints);

        jspSetCreator.setRightComponent(pnlChoices);

        add(jspSetCreator, java.awt.BorderLayout.CENTER);
    }// </editor-fold>//GEN-END:initComponents

private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
    final MixSet ms = new MixSet();
    final List<Title> titles = new ArrayList<Title>(10);

    final Enumeration e = ((DefaultListModel)lstSet.getModel()).elements();
    while(e.hasMoreElements()){
        final Title title = (Title)e.nextElement();
        titles.add(title);
    }
    
    ms.setTitles(titles);
    ms.setName(txtName.getText());
    ms.setDescription(txaDescription.getText());
    
    final DAOManager dao = Lookup.getDefault().lookup(DAOManager.class);
    dao.getCommonService().store(ms);
}//GEN-LAST:event_jButton1ActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private final transient javax.swing.JButton jButton1 = new javax.swing.JButton();
    private final transient javax.swing.JScrollPane jScrollPane1 = new javax.swing.JScrollPane();
    private final transient javax.swing.JScrollPane jScrollPane2 = new javax.swing.JScrollPane();
    private final transient javax.swing.JScrollPane jScrollPane3 = new javax.swing.JScrollPane();
    private final transient javax.swing.JScrollPane jScrollPane4 = new javax.swing.JScrollPane();
    private final transient javax.swing.JScrollPane jScrollPane5 = new javax.swing.JScrollPane();
    private final transient javax.swing.JScrollPane jScrollPane6 = new javax.swing.JScrollPane();
    private final transient javax.swing.JScrollPane jScrollPane7 = new javax.swing.JScrollPane();
    private final transient javax.swing.JSeparator jSeparator1 = new javax.swing.JSeparator();
    private final transient javax.swing.JSeparator jSeparator2 = new javax.swing.JSeparator();
    private final transient javax.swing.JSeparator jSeparator3 = new javax.swing.JSeparator();
    private final transient javax.swing.JSeparator jSeparator4 = new javax.swing.JSeparator();
    private final transient javax.swing.JSeparator jSeparator5 = new javax.swing.JSeparator();
    private final transient javax.swing.JSplitPane jSplitPane1 = new javax.swing.JSplitPane();
    private final transient javax.swing.JSplitPane jspSetCreator = new javax.swing.JSplitPane();
    private final transient javax.swing.JLabel lblBPM1 = new javax.swing.JLabel();
    private final transient javax.swing.JLabel lblBPM2 = new javax.swing.JLabel();
    private final transient javax.swing.JLabel lblDescription = new javax.swing.JLabel();
    private final transient javax.swing.JLabel lblLowerBound = new javax.swing.JLabel();
    private final transient javax.swing.JLabel lblName = new javax.swing.JLabel();
    private final transient javax.swing.JLabel lblUpperBound = new javax.swing.JLabel();
    private final transient javax.swing.JList lstBase = new javax.swing.JList();
    private final transient javax.swing.JList lstHigher = new javax.swing.JList();
    private final transient javax.swing.JList lstLower = new javax.swing.JList();
    private final transient javax.swing.JList lstOpposite = new javax.swing.JList();
    private final transient javax.swing.JList lstPusher = new javax.swing.JList();
    private final transient javax.swing.JList lstSet = new javax.swing.JList();
    private final transient javax.swing.JPanel pnlBpmFilter = new javax.swing.JPanel();
    private final transient javax.swing.JPanel pnlChoices = new javax.swing.JPanel();
    private final transient javax.swing.JPanel pnlControls = new javax.swing.JPanel();
    private final transient javax.swing.JPanel pnlMajorSuggestions = new javax.swing.JPanel();
    private final transient javax.swing.JPanel pnlMinorSuggestions = new javax.swing.JPanel();
    private final transient javax.swing.JPanel pnlSet = new javax.swing.JPanel();
    private final transient javax.swing.JPanel pnlSuggestions = new javax.swing.JPanel();
    private final transient javax.swing.JSpinner spnLowerBound = new javax.swing.JSpinner();
    private final transient javax.swing.JSpinner spnUpperBound = new javax.swing.JSpinner();
    private final transient javax.swing.JToolBar tbaActions = new javax.swing.JToolBar();
    private final transient javax.swing.JTextArea txaDescription = new javax.swing.JTextArea();
    private final transient javax.swing.JTextField txtName = new javax.swing.JTextField();
    // End of variables declaration//GEN-END:variables
    
    private final class SetListeners implements ListSelectionListener, DropTargetListener {

        private final transient Logger log = LoggerFactory.getLogger(SetListeners.class);
        
        private final transient DropTarget dropTarget;
        
        private SetListeners() {
            dropTarget = new DropTarget(lstSet, DnDConstants.ACTION_COPY_OR_MOVE, this, true);
        }
        
        @Override
        public void valueChanged(final ListSelectionEvent e) {
            final Title title = (Title)lstSet.getSelectedValue();
            
            if(title == null){
                updateFittingKeyTables(null);
            } else {
                updateFittingKeyTables(title.getKey());
            }
        }

        @Override
        public void dragEnter(final DropTargetDragEvent dtde) {
            // do nothing
        }

        @Override
        public void dragOver(final DropTargetDragEvent dtde) {
            // do nothing
        }

        @Override
        public void dropActionChanged(final DropTargetDragEvent dtde) {
            // do nothing
        }

        @Override
        public void dragExit(final DropTargetEvent dte) {
            // do nothing
        }

        @Override
        public void drop(final DropTargetDropEvent dtde) {
            final Transferable t = dtde.getTransferable();
            try {
                if(t.isDataFlavorSupported(SetTransferable.TITLE_FLAVOR)){
                    final Object title = t.getTransferData(SetTransferable.TITLE_FLAVOR);
                    ((DefaultListModel)lstSet.getModel()).addElement(title);
                    dtde.dropComplete(true);
                } else if(t.isDataFlavorSupported(SetTransferable.MIXSET_FLAVOR)) {
                    final Object mixSet = t.getTransferData(SetTransferable.MIXSET_FLAVOR);
                    applyMixSet((MixSet)mixSet);
                    dtde.dropComplete(true);
                } else {
                    if(log.isDebugEnabled()){
                        log.debug("rejecting drop, unsupported transferable: " + t); // NOI18N
                    }
                    
                    dtde.rejectDrop();
                }
            } catch (final Exception ex) {
                log.error("cannot drop title", ex); // NOI18N
                dtde.dropComplete(false);
            }
        }
    }
    
    private static final class TitleCellRenderer extends DefaultListCellRenderer{
        
        private final transient ImageIcon icon = 
                ImageUtilities.loadImageIcon("de/bananeiml/musicdb/ui/music_note.png", false); // NOI18N

        @Override
        public Component getListCellRendererComponent(
                final JList list, 
                final Object value, 
                final int index, 
                final boolean isSelected, 
                final boolean cellHasFocus) {
            final Component comp = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            
            if(comp instanceof JLabel){
                final JLabel label = (JLabel)comp;
                label.setIcon(icon);
                
                final Title title = (Title)value;
                final String key;
                if(title.getKey() == null){
                    key = "n/a"; // NOI18N
                } else {
                    if(title.getKey().getKey().length() == 2){
                        key = " " + title.getKey().getKey(); // NOI18N
                    } else {
                        key = title.getKey().getKey();
                    }
                }
                
                label.setText(key + " - " + title.getName() + " - " + title.getArtist().getName()); // NOI18N
            }
            
            return comp;
        }
        
    }
    
    public static final class SetTransferable implements Transferable {
        
        public static final transient DataFlavor TITLE_FLAVOR = new DataFlavor(Title.class, "titleFlavor"); // NOI18N
        public static final transient DataFlavor MIXSET_FLAVOR = new DataFlavor(MixSet.class, "mixsetFlavor"); // NOI18N
        
        private final transient Object payload;
        private final transient DataFlavor flavor;

        public SetTransferable(final Object payload) {
            if(payload == null){
                throw new IllegalArgumentException("payload must not be null"); // NOI18N
            }
            
            if(TITLE_FLAVOR.getRepresentationClass().isAssignableFrom(payload.getClass())){
                flavor = TITLE_FLAVOR;
            } else if(MIXSET_FLAVOR.getRepresentationClass().isAssignableFrom(payload.getClass())){
                flavor = MIXSET_FLAVOR;
            } else {
                throw new IllegalArgumentException("unsupported payload type: " + payload.getClass()); // NOI18N
            }
            
            this.payload = payload;
        }
        
        @Override
        public DataFlavor[] getTransferDataFlavors() {
            return new DataFlavor[] {flavor};
        }

        @Override
        public boolean isDataFlavorSupported(final DataFlavor flavor) {
            return this.flavor.match(flavor);
        }

        @Override
        public Object getTransferData(final DataFlavor flavor) throws UnsupportedFlavorException, IOException {
            if(isDataFlavorSupported(flavor)){
                return payload;
            } else {
                throw new UnsupportedFlavorException(flavor); // NOI18N
            }
        }
    }
}
