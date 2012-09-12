package de.bananeiml.musicdb.ui;

import de.bananeiml.musicdb.server.dao.core.DAOManager;
import de.bananeiml.musicdb.server.dao.entity.Artist;
import de.bananeiml.musicdb.server.dao.entity.Title;
import de.bananeiml.musicdb.server.dao.service.CommonService;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.NoResultException;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileFilter;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.openide.awt.ActionRegistration;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionReferences;
import org.openide.awt.ActionID;
import org.openide.util.Lookup;
import org.openide.util.NbBundle.Messages;
import org.openide.windows.WindowManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ActionID(category = "Tools",
id = "de.bananeiml.musicdb.ui.ImportVinylXlsx")
@ActionRegistration(iconBase = "de/bananeiml/musicdb/ui/xlsx_import.png",
displayName = "#CTL_ImportVinylXlsx")
@ActionReferences({
    @ActionReference(path = "Menu/File", position = 1300, separatorBefore = 1250, separatorAfter = 1350),
    @ActionReference(path = "Toolbars/File", position = 300),
    @ActionReference(path = "Shortcuts", name = "DS-I")
})
@Messages("CTL_ImportVinylXlsx=Import Vinyl Xlsx")
public final class ImportVinylXlsx implements ActionListener {
    private static final transient Logger LOG = LoggerFactory.getLogger(ImportVinylXlsx.class);

    private transient File importFile;
    
    @Override
    public void actionPerformed(final ActionEvent e) {
        if(importFile == null || !importFile.canRead()){
            final JFileChooser chooser = new JFileChooser();
            chooser.setFileFilter(new FileFilter() {

                @Override
                public boolean accept(final File file) {
                    if(file != null && file.canRead()) {
                        return file.getName().endsWith(".xlsx"); // NOI18N
                    } else {
                        return false;
                    }
                }

                @Override
                public String getDescription() {
                    return "Vinyl labels"; // NOI18N
                }
            });
            final int answer = chooser.showDialog(WindowManager.getDefault().getMainWindow(), "Import"); // NOI18N
            if(JFileChooser.APPROVE_OPTION == answer){
                importFile = chooser.getSelectedFile();
            }
        }
        
        // TODO: change, because maybe the file chooser did not read properly or the action was canceled
        try {
            final List<Title> titles = readTitles(importFile);
            insertTitles(titles);
        } catch (final Exception ex) {
            LOG.error("cannot import titles: " + importFile, ex); // NOI18N
        }
    }
    
    private void insertTitles(final List<Title> titles){
        final DAOManager dao = Lookup.getDefault().lookup(DAOManager.class);
        final CommonService cs = dao.getCommonService();
        
        // TODO: fix saving
        for(final Title title : titles){
            Artist artist = title.getArtist();
            try {
                final Artist dbArtist = cs.getEntity(Artist.class, artist.getName());
                title.setArtist(dbArtist);
                
                try {
//                    final List<Title> dbTitles = dao.getGeneralService().getTitles(title.getName());
//                    
//                    boolean exists = false;
//                    for(final Title dbTitle : dbTitles){
//                        if(dbArtist.equals(dbTitle.getArtist())){
//                            
//                        }
//                    }
                } catch (final NoResultException e) {
                
                }
            } catch (final NoResultException e) {
                // the artist is not present yet, so the title cannot be present, too
                title.setArtist(cs.store(artist));
                cs.store(title);
            }
        }
    }
    
    private List<Title> readTitles(final File fileToRead) throws IOException {
        if(LOG.isDebugEnabled()){
            LOG.debug("importing vinyls from xlsx: " + fileToRead); // NOI18N
        }
        
        final List<Title> titles = new ArrayList<Title>(200);
        BufferedInputStream bis = null;
        try{
            bis = new BufferedInputStream(new FileInputStream(fileToRead));
            
            final Workbook wb = new XSSFWorkbook(bis);
            final Sheet sheet = wb.getSheet("labels");

            boolean emptyRow = false;
            
            final int lastRowNum = sheet.getLastRowNum();
            for(int i = 0; i < lastRowNum; ++i){
                final Row artistRow = sheet.getRow(i);
                
                if(isRowEmpty(artistRow)){
                    if(emptyRow){
                        if(LOG.isDebugEnabled()){
                            LOG.debug("found two consequtive empty rows, assuming file finished: row=" + i); // NOI18N
                        }
                        
                        break;
                    }
                    emptyRow = true;
                    
                    continue;
                }
                
                // one row may have been empty, but we're going on so this row is not empty (anymore)
                emptyRow = false;
                
                if(i + 1 > lastRowNum){
                    throw new IllegalStateException("found artist row without title row"); // NOI18N
                } else if(i + 2 > lastRowNum){
                    throw new IllegalStateException("found artist row without key row"); // NOI18N
                }else if(i + 3 > lastRowNum){
                    throw new IllegalStateException("found artist row without bpm row"); // NOI18N
                }
                
                // we're sure that there are at least 4 rows to read from
                final Row titleRow = sheet.getRow(++i);
                final Row keyRow = sheet.getRow(++i);
                final Row bpmRow = sheet.getRow(++i);
                
                for(int j = 0; j < artistRow.getLastCellNum(); ++j){
                    final String artist;
                    final Cell artistCell = artistRow.getCell(j);
                    if(artistCell == null){
                        if(LOG.isWarnEnabled()){
                            LOG.warn("encountered null artist cell at [row=" + i + "|cell=" + j + "]");
                        }
                        continue;
                    } else{
                        artist = artistCell.getStringCellValue();
                    }
                    
                    final String title;
                    final Cell titleCell = titleRow.getCell(j);
                    if(titleCell == null){
                        if(LOG.isWarnEnabled()){
                            LOG.warn("encountered null title cell at [row=" + i + "|cell=" + j + "]");
                        }
                        continue;
                    } else{
                        title = titleCell.getStringCellValue();
                    }
                    
                    if(artist.isEmpty() || title.isEmpty()){
                        if(LOG.isDebugEnabled()){
                            LOG.debug("artist or title is empty, assuming empty block: [row=" + i + "|cell=" + j + "]"); // NOI18N
                        }
                        
                        continue;
                    }
                    
                    final String key = getKey(keyRow.getCell(j));
                    final Integer bpm = getBpm(bpmRow.getCell(j));
                    
                    final DAOManager dao = Lookup.getDefault().lookup(DAOManager.class);
                    final Artist artistEntity = new Artist();
                    artistEntity.setName(artist);
                    final Title titleEntity = new Title();
                    titleEntity.setArtist(artistEntity);
                    titleEntity.setName(title);
                    titleEntity.setKey((key == null || key.isEmpty()) ? null : dao.getCache().getKey(key));
                    titleEntity.setBpm((bpm == null || bpm < 1) ? null : bpm);
                    
                    titles.add(titleEntity);
                    System.out.println(artist + "|" + title);
                }
            }
        } catch(final Exception e){
            final String message = "cannot read from input: " + fileToRead; // NOI18N
            LOG.error(message, e); // NOI18N
            
            throw new IOException(message, e);
        } finally {
            if(bis != null){
                try{
                    bis.close();
                } catch (final IOException ex){
                    if(LOG.isWarnEnabled()){
                        LOG.warn("cannot close inputstream", ex); // NOI18N
                    }
                }
            }
        }
        
        return titles;
    }
    
    private String getKey(final Cell key){
        if(key == null){
            if(LOG.isWarnEnabled()){
                LOG.warn("cannot get bpm for empty cell reference"); // NOI18N
            }
            
            return null;
        }
        
        if(Cell.CELL_TYPE_STRING == key.getCellType()){
            return key.getStringCellValue();
        } else {
            return null;
        }
    }
    
    private Integer getBpm(final Cell bpm){
        if(bpm == null){
            if(LOG.isWarnEnabled()){
                LOG.warn("cannot get bpm for empty cell reference"); // NOI18N
            }
            
            return null;
        }
        
        try{
            if(Cell.CELL_TYPE_STRING == bpm.getCellType()){
                return Integer.parseInt(bpm.getStringCellValue().replace("bpm", "").trim()); // NOI18N
            } else if(Cell.CELL_TYPE_NUMERIC == bpm.getCellType()){
                return Double.valueOf(bpm.getNumericCellValue()).intValue();
            } else if(Cell.CELL_TYPE_BLANK == bpm.getCellType()){
                return null;
            }else {
                throw new IllegalStateException("bpm cell contains illegal value: " + bpm); // NOI18N
            }
        } catch(final NumberFormatException nfe){
            if(LOG.isWarnEnabled()){
                LOG.warn("cannot parse bpm: " + bpm, nfe); // NOI18N
            }
            
            return null;
        }
    }
    
    private boolean isRowEmpty(final Row row){
        for(final Cell cell : row){
            if(Cell.CELL_TYPE_BLANK != cell.getCellType()){
                return false;
            }
        }
        
        return true;
    }
}
