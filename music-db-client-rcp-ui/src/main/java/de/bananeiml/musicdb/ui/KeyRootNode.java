package de.bananeiml.musicdb.ui;

import de.bananeiml.musicdb.server.dao.core.DAOManager;
import de.bananeiml.musicdb.server.dao.entity.Key;
import java.awt.Image;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import org.openide.nodes.AbstractNode;
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
public class KeyRootNode extends AbstractNode {

    private static final transient Logger LOG = LoggerFactory.getLogger(KeyRootNode.class);
    
    private final transient Image icon;
    
    public KeyRootNode() {
        super(new KeyRootChildren());
        
        icon = ImageUtilities.loadImage("de/bananeiml/musicdb/ui/folder_key.png"); // NOI18N
        
        setDisplayName("Keys"); // NOI18N
    }

    @Override
    public Image getIcon(final int type) {
        return icon;
    }

    @Override
    public Image getOpenedIcon(final int type) {
        return icon;
    }
    
    private static final class KeyRootChildren extends Children.Array{
        
        @Override
        protected Collection<Node> initCollection() {
            final List<Node> nodeResult = new ArrayList<Node>(25);
            
            final DAOManager dao = Lookup.getDefault().lookup(DAOManager.class);
            final List<Key> keys = dao.getCommonService().getAllEntities(Key.class);
            
            Collections.sort(keys, new Comparator<Key>() {

                @Override
                public int compare(final Key o1, final Key o2) {
                    final String keyString1 = o1.getKey();
                    final String keyString2 = o2.getKey();
                    
                    if(keyString1 == null && keyString2 != null){
                        return -1;
                    } else if(keyString1 != null && keyString2 == null){
                        return 1;
                    } else if(keyString1 == null && keyString2 == null){
                        return 0;
                    } else {
                        final int toneComp = o1.getTone() - o2.getTone();
                        if(toneComp == 0){
                            return o1.getMode() - o2.getMode();
                        } else {
                            return toneComp;
                        }
                    }
                }
            });
            
            nodeResult.add(new KeyNode(null));
            for(final Key key : keys){
                nodeResult.add(new KeyNode(key));
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
