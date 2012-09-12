package de.bananeiml.musicdb.ui;

import de.bananeiml.musicdb.server.dao.entity.Artist;
import de.bananeiml.musicdb.ui.AlbumRootNode.ArtistAlbumsChildrenFactory;
import de.bananeiml.musicdb.ui.TitleRootNode.ArtistTitlesChildrenFactory;
import java.awt.Image;
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
public class ArtistNode extends AbstractNode {

    private final transient Image icon;
    private final transient Artist artist;
    
    
    public ArtistNode(final Artist bean) {
        super(new ArtistChildren(bean));
        
        artist = bean;
        icon = ImageUtilities.loadImage("de/bananeiml/musicdb/ui/user_black.png"); // NOI18N
        
        setDisplayName(artist.getName());
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
            // <editor-fold defaultstate="collapsed" desc="Name property"> 
            final Property<String> nameProp = new PropertySupport<String>(
                    "nameProp", // NOI18N
                    String.class, 
                    "Name", 
                    "The name of the title", 
                    true, 
                    false) {

                @Override
                public String getValue() throws IllegalAccessException, InvocationTargetException {
                    return artist.getName();
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
                    return artist.getId();
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
            main.setDisplayName("Album Properties");
            
            final Sheet.Set internal = Sheet.createPropertiesSet();
            internal.setName("internal properties"); // NOI18N
            internal.setDisplayName("Internal properties");
            
            main.put(nameProp);
            
            internal.put(idProp);
            
            sheet.put(main);
            sheet.put(internal);
        } catch (final Exception ex) {
            Exceptions.printStackTrace(ex);
        }
        
        return sheet;
    }
    
    private static final class ArtistChildren extends Children.Array{
        
        private final transient Artist artist;

        public ArtistChildren(final Artist artist) {
            this.artist = artist;
        }

        @Override
        protected Collection<Node> initCollection() {
            final List<Node> nodeResult = new ArrayList<Node>(2);
            nodeResult.add(new AlbumRootNode(Children.create(new ArtistAlbumsChildrenFactory(artist), true)));
            nodeResult.add(new TitleRootNode(Children.create(new ArtistTitlesChildrenFactory(artist), true)));
            
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
