package de.bananeiml.musicdb.ui;

import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;

/**
 *
 * @author Martin Scholl
 */
public class ErrorNode extends AbstractNode{

    public ErrorNode(final Exception ex) {
        super(Children.LEAF);
        
        setName(ex.getMessage());
    }
}
