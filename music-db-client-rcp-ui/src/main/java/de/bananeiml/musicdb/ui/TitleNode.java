package de.bananeiml.musicdb.ui;

import de.bananeiml.musicdb.server.dao.entity.Album;
import de.bananeiml.musicdb.server.dao.entity.Artist;
import de.bananeiml.musicdb.server.dao.entity.Key;
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
import org.openide.nodes.Node.Property;
import org.openide.nodes.PropertySupport;
import org.openide.nodes.Sheet;
import org.openide.util.Exceptions;
import org.openide.util.ImageUtilities;

/**
 *
 * @author Martin Scholl
 */
public class TitleNode extends AbstractNode {

    private final transient Image icon;
    private final transient Title title;
    
    public TitleNode(final Title bean) {
        super(new TitleChildren(bean));
        
        title = bean;
        icon = ImageUtilities.loadImage("de/bananeiml/musicdb/ui/music_note.png"); // NOI18N
        
        setDisplayName(title.getName());
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
        return new SetTransferable(title);
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
                    return title.getName();
                }

                @Override
                public void setValue(final String t) throws IllegalAccessException, 
                        IllegalArgumentException, 
                        InvocationTargetException {
                    throw new UnsupportedOperationException("Not supported yet."); // NOI18N
                }
            };// </editor-fold>
            // <editor-fold defaultstate="collapsed" desc="Artist property"> 
            final Property<String> artistProp = new PropertySupport<String>(
                    "artistProp", // NOI18N
                    String.class, 
                    "Artist", 
                    "The artist of the title", 
                    true, 
                    false) {

                @Override
                public String getValue() throws IllegalAccessException, InvocationTargetException {
                    return title.getArtist().getName();
                }

                @Override
                public void setValue(final String t) throws IllegalAccessException, 
                        IllegalArgumentException, 
                        InvocationTargetException {
                    throw new UnsupportedOperationException("Not supported yet.");
                }
            }; // </editor-fold>
            // <editor-fold defaultstate="collapsed" desc="Key property"> 
            final Property<String> keyProp = new PropertySupport<String>(
                    "keyProp", // NOI18N
                    String.class, 
                    "Key", 
                    "The key of the title", 
                    true, 
                    false) {

                @Override
                public String getValue() throws IllegalAccessException, InvocationTargetException {
                    final Key key = title.getKey();
                    
                    if(key == null){
                        return "<key information not set>";
                    } else{
                        return key.getKey() + " - " + key.getDescription(); // NOI18N
                    }
                }

                @Override
                public void setValue(final String t) throws IllegalAccessException, 
                    IllegalArgumentException, 
                    InvocationTargetException {
                    throw new UnsupportedOperationException("Not supported yet.");
                }
            };// </editor-fold>
            // <editor-fold defaultstate="collapsed" desc="BPM property"> 
            final Property<String> bpmProp = new PropertySupport<String>(
                    "bpmProp", // NOI18N
                    String.class, 
                    "BPM", 
                    "The tempo of the title in BPM", 
                    true, 
                    false) {

                @Override
                public String getValue() throws IllegalAccessException, InvocationTargetException {
                    final Integer bpm = title.getBpm();
                    
                    if(bpm == null){
                        return "<bpm information not set>";
                    } else{
                        return bpm + " BPM"; // NOI18N
                    }
                }

                @Override
                public void setValue(final String t) throws IllegalAccessException, 
                        IllegalArgumentException, 
                        InvocationTargetException {
                    throw new UnsupportedOperationException("Not supported yet."); // NOI18N
                }
            }; // </editor-fold>
            // <editor-fold defaultstate="collapsed" desc="Album property"> 
            final Property<String> albumProp = new PropertySupport<String>(
                    "albumProp", // NOI18N
                    String.class, 
                    "Album", 
                    "This title is part of album", 
                    true, 
                    false) {

                @Override
                public String getValue() throws IllegalAccessException, InvocationTargetException {
                    final Album album = title.getAlbum();
                    
                    if(album == null){
                        return "<this title is not part of an album>";
                    } else {
                        return album.getName();
                    }
                }

                @Override
                public void setValue(final String t) throws IllegalAccessException, 
                        IllegalArgumentException, 
                        InvocationTargetException {
                    throw new UnsupportedOperationException("Not supported yet.");
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
                    return title.getId();
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
            main.setDisplayName("Title Properties");
            
            final Sheet.Set related = Sheet.createPropertiesSet();
            related.setName("related properties"); // NOI18N
            related.setDisplayName("Related title properties");
            
            final Sheet.Set internal = Sheet.createPropertiesSet();
            internal.setName("internal properties"); // NOI18N
            internal.setDisplayName("Internal properties");
            
            main.put(nameProp);
            main.put(artistProp);
            main.put(keyProp);
            main.put(bpmProp);
            
            related.put(albumProp);
            
            internal.put(idProp);
            
            sheet.put(main);
            sheet.put(related);
            sheet.put(internal);
        } catch (final Exception ex) {
            Exceptions.printStackTrace(ex);
        }
        
        return sheet;
    }
    
    private static final class TitleChildren extends Children.Array{
        
        private final transient Title title;

        public TitleChildren(final Title title) {
            this.title = title;
        }

        @Override
        protected Collection<Node> initCollection() {
            final Artist artist = title.getArtist();
            final Album album = title.getAlbum();
            
            final List<Node> nodeResult = new ArrayList<Node>(2);
            if(artist != null) {
                nodeResult.add(new ArtistNode(artist));
            }
            if(album != null) {
                nodeResult.add(new AlbumNode(album));
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
