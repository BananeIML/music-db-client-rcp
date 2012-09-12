package de.bananeiml.musicdb.ui;

import de.bananeiml.musicdb.ui.AlbumRootNode.AllAlbumChildFactory;
import de.bananeiml.musicdb.ui.TitleRootNode.AllTitlesChildFactory;
import java.awt.Image;
import java.util.List;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.ChildFactory;
import org.openide.nodes.Children;
import org.openide.nodes.Node;
import org.openide.util.ImageUtilities;

/**
 *
 * @author Martin Scholl
 */
public class VinylRootNode extends AbstractNode {
    
    private final transient Image icon;

    public VinylRootNode() {
        super(Children.create(new VinylChildrenFactory(), false));
        
        icon = ImageUtilities.loadImage("de/bananeiml/musicdb/ui/vinyl_blue.png"); // NOI18N
        
        setDisplayName("Vinyls"); // NOI18N
    }

    @Override
    public Image getIcon(final int type) {
        return icon;
    }

    @Override
    public Image getOpenedIcon(final int type) {
        return icon;
    }
    
    private static final class VinylChildrenFactory extends ChildFactory<Object> {

        public VinylChildrenFactory() {
        }

        @Override
        protected boolean createKeys(final List<Object> list) {
            list.add(new Object());

            return true;
        }

        @Override
        protected Node createWaitNode() {
            return new ArtistRootNode();
        }

        @Override
        protected Node[] createNodesForKey(final Object key) {
            return new Node[] {
                new ArtistRootNode(),
                new AlbumRootNode(Children.create(new AllAlbumChildFactory(), true)),
                new TitleRootNode(Children.create(new AllTitlesChildFactory(), true)),
                new KeyRootNode(),
                new MixSetRootNode()
            };
        }
    }
}
