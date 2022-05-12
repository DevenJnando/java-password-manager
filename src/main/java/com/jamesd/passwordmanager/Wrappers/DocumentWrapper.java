package com.jamesd.passwordmanager.Wrappers;

import com.jamesd.passwordmanager.Models.Passwords.DocumentEntry;
import javafx.scene.image.ImageView;

/**
 * Wrapper class for a DocumentEntry object
 */
public class DocumentWrapper extends BaseWrapper {

    private DocumentEntry documentEntry;

    /**
     * Default constructor
     */
    public DocumentWrapper() {

    }

    /**
     * Constructor which instantiates the DocumentEntry object, as well as the ImageView which will contain the
     * icon indicating the file type
     * @param documentEntry
     * @param favicon
     */
    public DocumentWrapper(DocumentEntry documentEntry, ImageView favicon) {
        super(favicon);
        this.documentEntry = documentEntry;
    }

    public DocumentEntry getDocumentEntry() {
        return this.documentEntry;
    }

    public void setDocumentEntry(DocumentEntry documentEntry) {
        this.documentEntry = documentEntry;
    }
}
