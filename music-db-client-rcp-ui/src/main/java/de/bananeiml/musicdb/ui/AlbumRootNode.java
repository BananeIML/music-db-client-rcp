package de.bananeiml.musicdb.ui;

import de.bananeiml.musicdb.server.dao.core.DAOManager;
import de.bananeiml.musicdb.server.dao.entity.Album;
import de.bananeiml.musicdb.server.dao.entity.Artist;
import java.awt.Image;
import java.util.Collections;
import java.util.List;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.ChildFactory;
import org.openide.nodes.Children;
import org.openide.nodes.Node;
import org.openide.util.ImageUtilities;
import org.openide.util.Lookup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Martin Scholl
 */
public class AlbumRootNode extends AbstractNode {

    private static final transient Logger LOG = LoggerFactory.getLogger(AlbumRootNode.class);
    
    private final transient Image icon;
    
    public AlbumRootNode(final Children children) {
        super(children);
        
        icon = ImageUtilities.loadImage("de/bananeiml/musicdb/ui/folder_vinyl.png"); // NOI18N
        
        setDisplayName("Albums"); // NOI18N
    }

    @Override
    public Image getIcon(final int type) {
        return icon;
    }

    @Override
    public Image getOpenedIcon(final int type) {
        return icon;
    }
    
    public static final class AllAlbumChildFactory extends ChildFactory<Album>{

        @Override
        protected boolean createKeys(final List<Album> list) {
            try{
                final DAOManager daoManager = Lookup.getDefault().lookup(DAOManager.class);
                final List<Album> album = daoManager.getCommonService().getAllEntities(Album.class);
                
                Collections.sort(album, new NamedEntityComparator());
                list.addAll(album);
                
                return true;
            }catch(final Exception e){
                LOG.error("could not fetch albums", e); // NOI18N
                
                return false;
            }
        }

        @Override
        protected Node createNodeForKey(final Album key) {
            return new AlbumNode(key);
        }
    }
    
    public static final class ArtistAlbumsChildrenFactory extends ChildFactory<Album>{
        
        private static final transient Logger LOG = LoggerFactory.getLogger(ArtistAlbumsChildrenFactory.class);
        
        private final transient Artist artist;

        public ArtistAlbumsChildrenFactory(final Artist artist) {
            this.artist = artist;
        }
        
        @Override
        protected boolean createKeys(final List<Album> list) {
            try{
                final DAOManager daoManager = Lookup.getDefault().lookup(DAOManager.class);
                final List<Album> titles = daoManager.getGeneralService().getAlbums(artist);
                
                Collections.sort(titles, new NamedEntityComparator());
                list.addAll(titles);
                
                return true;
            }catch(final Exception e){
                LOG.error("could not fetch artists", e); // NOI18N
                
                return false;
            }
        }

        @Override
        protected Node createNodeForKey(final Album key) {
            return new AlbumNode(key);
        }
    }
}
