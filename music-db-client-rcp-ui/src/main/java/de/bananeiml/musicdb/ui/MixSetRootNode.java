package de.bananeiml.musicdb.ui;

import de.bananeiml.musicdb.server.dao.core.DAOManager;
import de.bananeiml.musicdb.server.dao.entity.MixSet;
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
public class MixSetRootNode extends AbstractNode {

    private static final transient Logger LOG = LoggerFactory.getLogger(MixSetRootNode.class);
    
    private final transient Image icon;
    
    public MixSetRootNode() {
        super(Children.create(new AllMixSetsChildFactory(), true));
        
        icon = ImageUtilities.loadImage("de/bananeiml/musicdb/ui/folder_set.png"); // NOI18N
        
        setDisplayName("Mix Sets"); // NOI18N
    }

    @Override
    public Image getIcon(final int type) {
        return icon;
    }

    @Override
    public Image getOpenedIcon(final int type) {
        return icon;
    }
    
    public static final class AllMixSetsChildFactory extends ChildFactory<MixSet>{

        @Override
        protected boolean createKeys(final List<MixSet> list) {
            try{
                final DAOManager daoManager = Lookup.getDefault().lookup(DAOManager.class);
                final List<MixSet> mixsets = daoManager.getCommonService().getAllEntities(MixSet.class);
                
                Collections.sort(mixsets, new NamedEntityComparator());
                list.addAll(mixsets);
                
                return true;
            }catch(final Exception e){
                LOG.error("could not fetch mixsets", e); // NOI18N
                
                return false;
            }
        }

        @Override
        protected Node createNodeForKey(final MixSet key) {
            return new MixSetNode(key);
        }
    }
}
