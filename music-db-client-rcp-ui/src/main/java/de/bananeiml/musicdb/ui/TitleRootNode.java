package de.bananeiml.musicdb.ui;

import de.bananeiml.musicdb.server.dao.core.DAOManager;
import de.bananeiml.musicdb.server.dao.entity.Album;
import de.bananeiml.musicdb.server.dao.entity.Artist;
import de.bananeiml.musicdb.server.dao.entity.Key;
import de.bananeiml.musicdb.server.dao.entity.Title;
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
public class TitleRootNode extends AbstractNode {

    private final transient Image icon;
    
    public TitleRootNode(final Children children) {
        super(children);
        
        icon = ImageUtilities.loadImage("de/bananeiml/musicdb/ui/folder_music_note.png"); // NOI18N
        
        setDisplayName("Titles"); // NOI18N
    }

    @Override
    public Image getIcon(final int type) {
        return icon;
    }

    @Override
    public Image getOpenedIcon(final int type) {
        return icon;
    }
    
    public static final class AllTitlesChildFactory extends ChildFactory<Title>{
        
        private static final transient Logger LOG = LoggerFactory.getLogger(AllTitlesChildFactory.class);
        
        @Override
        protected boolean createKeys(final List<Title> list) {
            try{
                final DAOManager daoManager = Lookup.getDefault().lookup(DAOManager.class);
                final List<Title> titles = daoManager.getCommonService().getAllEntities(Title.class);
                
                Collections.sort(titles, new NamedEntityComparator());
                list.addAll(titles);
                
                return true;
            }catch(final Exception e){
                LOG.error("could not fetch artists", e); // NOI18N
                
                return false;
            }
        }

        @Override
        protected Node createNodeForKey(final Title key) {
            return new TitleNode(key);
        }
    }
    
    public static final class ArtistTitlesChildrenFactory extends ChildFactory<Title>{
        
        private static final transient Logger LOG = LoggerFactory.getLogger(ArtistTitlesChildrenFactory.class);
        
        private final transient Artist artist;

        public ArtistTitlesChildrenFactory(final Artist artist) {
            this.artist = artist;
        }
        
        @Override
        protected boolean createKeys(final List<Title> list) {
            try{
                final DAOManager daoManager = Lookup.getDefault().lookup(DAOManager.class);
                final List<Title> titles = daoManager.getGeneralService().getTitles(artist);
                
                Collections.sort(titles, new NamedEntityComparator());
                list.addAll(titles);
                
                return true;
            }catch(final Exception e){
                LOG.error("could not fetch titles for artist", e); // NOI18N
                
                return false;
            }
        }

        @Override
        protected Node createNodeForKey(final Title key) {
            return new TitleNode(key);
        }
    }
    
    public static final class AlbumTitlesChildrenFactory extends ChildFactory<Title>{
        
        private static final transient Logger LOG = LoggerFactory.getLogger(AlbumTitlesChildrenFactory.class);
        
        private final transient Album album;

        public AlbumTitlesChildrenFactory(final Album artist) {
            this.album = artist;
        }
        
        @Override
        protected boolean createKeys(final List<Title> list) {
            try{
                final DAOManager daoManager = Lookup.getDefault().lookup(DAOManager.class);
                final List<Title> titles = daoManager.getGeneralService().getTitles(album);
                
                Collections.sort(titles, new NamedEntityComparator());
                list.addAll(titles);
                
                return true;
            }catch(final Exception e){
                LOG.error("could not fetch titles for album", e); // NOI18N
                
                return false;
            }
        }

        @Override
        protected Node createNodeForKey(final Title key) {
            return new TitleNode(key);
        }
    }
    
    public static final class KeyTitlesChildrenFactory extends ChildFactory<Title>{
        
        private static final transient Logger LOG = LoggerFactory.getLogger(KeyTitlesChildrenFactory.class);
        
        private final transient Key key;

        public KeyTitlesChildrenFactory(final Key key) {
            this.key = key;
        }
        
        @Override
        protected boolean createKeys(final List<Title> list) {
            try{
                final DAOManager daoManager = Lookup.getDefault().lookup(DAOManager.class);
                final List<Title> titles = daoManager.getGeneralService().getTitles(key);
                
                Collections.sort(titles, new NamedEntityComparator());
                list.addAll(titles);
                
                return true;
            }catch(final Exception e){
                LOG.error("could not fetch titles for key: " + key, e); // NOI18N
                
                return false;
            }
        }

        @Override
        protected Node createNodeForKey(final Title key) {
            return new TitleNode(key);
        }
    }
}
