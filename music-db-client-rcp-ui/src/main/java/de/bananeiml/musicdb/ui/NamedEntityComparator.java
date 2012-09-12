package de.bananeiml.musicdb.ui;

import de.bananeiml.musicdb.server.dao.entity.NamedEntity;
import java.util.Comparator;

/**
 *
 * @author Martin Scholl
 */
public class NamedEntityComparator implements Comparator<NamedEntity> {

    @Override
    public int compare(final NamedEntity o1, final NamedEntity o2) {
        if(o1 == null && o2 == null){
            return 0;
        } else if(o1 == null && o2 != null){
            return 1;
        } else if(o1 != null && o2 == null){
            return -1;
        } else {
            return o1.getName().compareTo(o2.getName());
        }
    }
}
