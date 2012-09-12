package de.bananeiml.musicdb.ui;

import de.bananeiml.musicdb.server.dao.entity.MixSet;
import de.bananeiml.musicdb.server.dao.entity.Title;
import de.bananeiml.musicdb.ui.SetCreatorTopComponent.SetTransferable;
import java.awt.Image;
import java.awt.datatransfer.Transferable;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.nodes.Node;
import org.openide.nodes.PropertySupport;
import org.openide.nodes.Sheet;
import org.openide.util.Exceptions;
import org.openide.util.ImageUtilities;

/**
 *
 * @author Martin Scholl
 */
public class MixSetNode extends AbstractNode {

    private final transient Image icon;
    private final transient MixSet mixSet;
    
    
    public MixSetNode(final MixSet bean) {
        super(new MixSetChildren(bean));
        
        mixSet = bean;
        icon = ImageUtilities.loadImage("de/bananeiml/musicdb/ui/set_list.png"); // NOI18N
        
        setDisplayName(mixSet.getName());
    }

    @Override
    public Image getIcon(final int type) {
        return icon;
    }

    @Override
    public Image getOpenedIcon(final int type) {
        return icon;
    }

    @Override
    public boolean canCopy() {
        return true;
    }

    @Override
    public Transferable clipboardCopy() throws IOException {
        return new SetTransferable(mixSet);
    }

    @Override
    public boolean canCut() {
        return false;
    }

    @Override
    public boolean canDestroy() {
        return false;
    }

    @Override
    public boolean canRename() {
        return false;
    }

    @Override
    protected Sheet createSheet() {
        final Sheet sheet = Sheet.createDefault();
        try {
            // <editor-fold defaultstate="collapsed" desc="Name property"> 
            final Property<String> nameProp = new PropertySupport<String>(
                    "nameProp", // NOI18N
                    String.class, 
                    "Name", 
                    "The name of the Mix Set", 
                    true, 
                    false) {

                @Override
                public String getValue() throws IllegalAccessException, InvocationTargetException {
                    return mixSet.getName();
                }

                @Override
                public void setValue(final String t) throws IllegalAccessException, 
                        IllegalArgumentException, 
                        InvocationTargetException {
                    throw new UnsupportedOperationException("Not supported yet."); // NOI18N
                }
            };// </editor-fold>
            // <editor-fold defaultstate="collapsed" desc="Description property"> 
            final Property<String> descProp = new PropertySupport<String>(
                    "descProp", // NOI18N
                    String.class, 
                    "Description", 
                    "The description of the Mix Set", 
                    true, 
                    false) {

                @Override
                public String getValue() throws IllegalAccessException, InvocationTargetException {
                    final String desc = mixSet.getDescription();
                    if(desc == null){
                        return "<no description>";
                    } else {
                        return desc;
                    }
                }

                @Override
                public void setValue(final String t) throws IllegalAccessException, 
                        IllegalArgumentException, 
                        InvocationTargetException {
                    throw new UnsupportedOperationException("Not supported yet."); // NOI18N
                }
            };// </editor-fold>
            // <editor-fold defaultstate="collapsed" desc="Id property"> 
            final Property<Long> idProp = new PropertySupport<Long>(
                    "idProp", // NOI18N
                    Long.class, 
                    "Id", 
                    "The internal id of the title", 
                    true, 
                    false) {

                @Override
                public Long getValue() throws IllegalAccessException, InvocationTargetException {
                    return mixSet.getId();
                }

                @Override
                public void setValue(final Long t) throws IllegalAccessException, 
                        IllegalArgumentException, 
                        InvocationTargetException {
                    throw new UnsupportedOperationException("Not supported yet."); // NOI18N
                }
            };// </editor-fold>
            
            final Sheet.Set main = Sheet.createPropertiesSet();
            main.setName("main properties"); // NOI18N
            main.setDisplayName("Mix Set Properties");
            
            final Sheet.Set internal = Sheet.createPropertiesSet();
            internal.setName("internal properties"); // NOI18N
            internal.setDisplayName("Internal properties");
            
            main.put(nameProp);
            main.put(descProp);
            
            internal.put(idProp);
            
            sheet.put(main);
            sheet.put(internal);
        } catch (final Exception ex) {
            Exceptions.printStackTrace(ex);
        }
        
        return sheet;
    }
    
    private static final class MixSetChildren extends Children.Array {
        
        private final transient MixSet mixSet;

        public MixSetChildren(final MixSet mixSet) {
            this.mixSet = mixSet;
        }

        @Override
        protected Collection<Node> initCollection() {
            final List<Title> titles = mixSet.getTitles();
            final List<Node> nodeResult = new ArrayList<Node>(titles.size());
            
            for(final Title title : titles){
                nodeResult.add(new TitleNode(title));
            }
            
            return nodeResult;
        }

        @Override
        public boolean add(Node[] arr) {
            return false;
        }

        @Override
        public boolean remove(Node[] arr) {
            return false;
        }
    }
}
