package de.bananeiml.musicdb.ui;

import de.bananeiml.musicdb.server.dao.core.DAOManager;
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
public class ArtistRootNode extends AbstractNode {

    private static final transient Logger LOG = LoggerFactory.getLogger(ArtistRootNode.class);
    
    private final transient Image icon;
    
    public ArtistRootNode() {
        super(Children.create(new ArtistChildrenFactory(), true));
        
        icon = ImageUtilities.loadImage("de/bananeiml/musicdb/ui/folder_user.png"); // NOI18N
        
        setDisplayName("Artists"); // NOI18N
    }

    @Override
    public Image getIcon(final int type) {
        return icon;
    }

    @Override
    public Image getOpenedIcon(final int type) {
        return icon;
    }
    
    public static final class ArtistChildrenFactory extends ChildFactory<Artist>{

        @Override
        protected boolean createKeys(final List<Artist> list) {
            try{
                final DAOManager daoManager = Lookup.getDefault().lookup(DAOManager.class);
                final List<Artist> artists = daoManager.getCommonService().getAllEntities(Artist.class);
                
                Collections.sort(artists, new NamedEntityComparator());
                list.addAll(artists);
                
                return true;
            }catch(final Exception e){
                LOG.error("could not fetch artists", e); // NOI18N
                
                return false;
            }
        }

        @Override
        protected Node createNodeForKey(final Artist key) {
            return new ArtistNode(key);
        }
    }
}
