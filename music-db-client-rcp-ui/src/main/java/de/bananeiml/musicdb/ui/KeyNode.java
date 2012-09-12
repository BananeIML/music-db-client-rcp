package de.bananeiml.musicdb.ui;

import de.bananeiml.musicdb.server.dao.entity.Key;
import de.bananeiml.musicdb.ui.TitleRootNode.KeyTitlesChildrenFactory;
import java.awt.Image;
import java.lang.reflect.InvocationTargetException;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.nodes.PropertySupport;
import org.openide.nodes.Sheet;
import org.openide.util.Exceptions;
import org.openide.util.ImageUtilities;

/**
 *
 * @author Martin Scholl
 */
public class KeyNode extends AbstractNode {

    private final transient Image icon;
    private final transient Key key;
    
    
    public KeyNode(final Key bean) {
        super(Children.create(new KeyTitlesChildrenFactory(bean), true));
        
        key = bean;
        icon = ImageUtilities.loadImage("de/bananeiml/musicdb/ui/key.png"); // NOI18N
        
        if(key == null){
            setDisplayName("undetermined");
        } else{
            setDisplayName(key.getKey());
        }
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
        return false;
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
            // <editor-fold defaultstate="collapsed" desc="Key property"> 
            final Property<String> keyProp = new PropertySupport<String>(
                    "keyProp", // NOI18N
                    String.class, 
                    "Key", 
                    "The key abbreviation", 
                    true, 
                    false) {

                @Override
                public String getValue() throws IllegalAccessException, InvocationTargetException {
                    return key == null ? "undetermined" : key.getKey();
                }

                @Override
                public void setValue(final String t) throws IllegalAccessException, 
                        IllegalArgumentException, 
                        InvocationTargetException {
                    throw new UnsupportedOperationException("Not supported yet."); // NOI18N
                }
            };// </editor-fold>
            // <editor-fold defaultstate="collapsed" desc="Key description property"> 
            final Property<String> descProp = new PropertySupport<String>(
                    "descProp", // NOI18N
                    String.class, 
                    "Description", 
                    "The key description", 
                    true, 
                    false) {

                @Override
                public String getValue() throws IllegalAccessException, InvocationTargetException {
                    return key == null ? "undetermined" : key.getDescription();
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
                    "The internal id of the key", 
                    true, 
                    false) {

                @Override
                public Long getValue() throws IllegalAccessException, InvocationTargetException {
                    return key.getId();
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
            main.setDisplayName("Key Properties");
            
            final Sheet.Set internal = Sheet.createPropertiesSet();
            internal.setName("internal properties"); // NOI18N
            internal.setDisplayName("Internal properties");
            
            main.put(keyProp);
            main.put(descProp);
            
            internal.put(idProp);
            
            sheet.put(main);
            sheet.put(internal);
        } catch (final Exception ex) {
            Exceptions.printStackTrace(ex);
        }
        
        return sheet;
    }
}
