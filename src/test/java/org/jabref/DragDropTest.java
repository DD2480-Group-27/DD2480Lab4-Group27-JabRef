package org.jabref.integration;
import javafx.collections.FXCollections;
import javafx.scene.input.Clipboard;
import org.jabref.gui.ClipBoardManager;
import org.jabref.gui.DialogService;
import org.jabref.gui.LibraryTab;
import org.jabref.gui.StateManager;
import org.jabref.gui.externalfiles.ImportHandler;
import org.jabref.gui.keyboard.KeyBindingRepository;
import org.jabref.gui.maintable.MainTable;
import org.jabref.gui.preferences.GuiPreferences;
import org.jabref.logic.FilePreferences;
import org.jabref.logic.bibtex.FieldPreferences;
import org.jabref.logic.database.DuplicateCheck;
import org.jabref.logic.importer.ImportFormatPreferences;
import org.jabref.logic.util.CurrentThreadTaskExecutor;
import org.jabref.model.database.BibDatabase;
import org.jabref.model.database.BibDatabaseContext;
import org.jabref.model.database.BibDatabaseMode;
import org.jabref.model.entry.BibEntry;
import org.jabref.model.entry.field.StandardField;
import org.jabref.model.entry.types.StandardEntryType;
import org.jabref.model.util.DummyFileUpdateMonitor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Answers;
import org.mockito.Mock;
import org.mockito.Mockito;

import javafx.scene.input.Dragboard;
import javafx.scene.input.DragEvent;
import javafx.scene.input.TransferMode;
import org.mockito.MockitoAnnotations;

import javax.swing.undo.UndoManager;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

public class DragDropTest {

    private MainTable mainTable;
    private ImportHandler importHandler;
    private BibDatabaseContext bibDatabaseContext;
    private BibEntry testEntry;

    /*
    RESEARCH: this is the setup from ImportHandlerTest.
    If we skip the part to set up the GUI and firing the drag and drop event,
    we could directly set up the ImportHandler and call the importFilesInBackground method for the test
     */
    @Mock
    private GuiPreferences preferences;
    @Mock
    private DuplicateCheck duplicateCheck;


//    @BeforeEach
//    void setUp() {
//        MockitoAnnotations.initMocks(this);
//
//        ImportFormatPreferences importFormatPreferences = mock(ImportFormatPreferences.class, Answers.RETURNS_DEEP_STUBS);
//        when(preferences.getImportFormatPreferences()).thenReturn(importFormatPreferences);
//        when(preferences.getFilePreferences()).thenReturn(mock(FilePreferences.class));
//
//        FieldPreferences fieldPreferences = mock(FieldPreferences.class);
//        when(fieldPreferences.getNonWrappableFields()).thenReturn(FXCollections.observableArrayList());
//        when(preferences.getFieldPreferences()).thenReturn(fieldPreferences);
//
//        bibDatabaseContext = mock(BibDatabaseContext.class);
//        BibDatabase bibDatabase = new BibDatabase();
//        when(bibDatabaseContext.getMode()).thenReturn(BibDatabaseMode.BIBTEX);
//        when(bibDatabaseContext.getDatabase()).thenReturn(bibDatabase);
//        when(duplicateCheck.isDuplicate(any(), any(), any())).thenReturn(false);
//        importHandler = new ImportHandler(
//                bibDatabaseContext,
//                preferences,
//                new DummyFileUpdateMonitor(),
//                mock(UndoManager.class),
//                mock(StateManager.class),
//                mock(DialogService.class),
//                new CurrentThreadTaskExecutor());
//    }

    @BeforeEach
    public void setUp() {

        //SETUP THE IMPORT HANDLER
        MockitoAnnotations.initMocks(this);
        ImportFormatPreferences importFormatPreferences = mock(ImportFormatPreferences.class, Answers.RETURNS_DEEP_STUBS);
        when(preferences.getImportFormatPreferences()).thenReturn(importFormatPreferences);
        when(preferences.getFilePreferences()).thenReturn(mock(FilePreferences.class));

        FieldPreferences fieldPreferences = mock(FieldPreferences.class);
        when(fieldPreferences.getNonWrappableFields()).thenReturn(FXCollections.observableArrayList());
        when(preferences.getFieldPreferences()).thenReturn(fieldPreferences);

        bibDatabaseContext = mock(BibDatabaseContext.class);
        BibDatabase bibDatabase = new BibDatabase();
        when(bibDatabaseContext.getMode()).thenReturn(BibDatabaseMode.BIBTEX);
        when(bibDatabaseContext.getDatabase()).thenReturn(bibDatabase);
        when(duplicateCheck.isDuplicate(any(), any(), any())).thenReturn(false);
        importHandler = new ImportHandler(
                bibDatabaseContext,
                preferences,
                new DummyFileUpdateMonitor(),
                mock(UndoManager.class),
                mock(StateManager.class),
                mock(DialogService.class),
                new CurrentThreadTaskExecutor());
//        importHandler = mock(ImportHandler.class);

        // Instantiate MainTable with mocked dependencies
        /*
        RESEARCH: MainTable requires a parameter LibraryTab,
        which has a private constructor, and in which it has a method called createMainTable
        that creates a MainTable. And to create a LibraryTab, one has to call the createLibraryTab() method in LibraryTab class
        which is called in the JabRefFrame class
        Conclusion: if we want to test for the drag and drop through firing the event, we have to instantiate the whole UI.

        !! THE mainTable INSTANTIATION BELOW IS NOT CORRECT (JUST MY TRY)!!
         */
        mainTable = new MainTable(
                null, // model
                new LibraryTab(), //library tab
                //library tab container
                bibDatabaseContext, //database
                preferences, //GUI preferences
                mock(DialogService.class),
                mock(StateManager.class),
                mock(KeyBindingRepository.class),
                mock(ClipBoardManager.class),
                importHandler
        );
    }

    @Test
    public void testHandleOnDragDroppedTableViewWithPdf() {
    // Create a mock DragEvent and Dragboard and set it up
        DragEvent dragEvent = mock(DragEvent.class);
        Dragboard dragboard = mock(Dragboard.class);
        when(dragboard.hasFiles()).thenReturn(true);

        // Simulated the dropped PDF file
        Path pdfFile = Paths.get("resources/pdfs/PdfContentImporter/Bogner2023.pdf"); // matching with the issue
        when(dragboard.getFiles()).thenReturn(Collections.singletonList(pdfFile.toFile()));
        when(dragEvent.getDragboard()).thenReturn(dragboard);

        // Fire the event on the MainTable
        mainTable.fireEvent(dragEvent);
        /* this is expected to call the line
        this.setOnDragDropped(this::handleOnDragDroppedTableView);`in MainTable.java
         */

        // Verify that importFilesInBackground was called
        verify(importHandler).importFilesInBackground(
                Collections.singletonList(pdfFile),
                bibDatabaseContext,
                preferences.getFilePreferences(), // Use null or a mocked FilePreferences
                TransferMode.COPY
        );

        // Check the citation key (should not be Optional.empty, should be RESTful2023)
        /*
        RESEARCH: how to get the file name:
        In the ImportHandler class, there is a method generateKeys.
        --> generateAndSetKey in CitationKeyGenerator
        --> setCitationKey in BibEntry
        --> setField in BibEntry
         */

    }
}

