package com.jamesd.passwordmanager.Models.Passwords;

/**
 * Class which models a sensitive document entry retrieved from the database
 */
public class DocumentEntry extends PasswordEntry{

    private String documentDescription;
    private String masterUsername;
    private String documentStorageReference;

    /**
     * Default constructor
     */
    public DocumentEntry() {
        super();
        this.documentDescription = null;
        this.masterUsername = null;
        this.documentStorageReference = null;
    }

    /**
     * Constructor called when creating a new DocumentEntry object
     * @param passwordName Name of the document
     * @param documentDescription Description of what the document is/what it's for
     * @param dateSet The date the document was created/last modified
     * @param documentStorageReference The reference to the document in the user's storage container
     */
    public DocumentEntry(String passwordName, String documentDescription, String masterUsername, String dateSet,
                         String documentStorageReference) {
        super(passwordName, null, dateSet);
        this.documentDescription = documentDescription;
        this.masterUsername = masterUsername;
        this.documentStorageReference = documentStorageReference;
    }

    /**
     * Constructor called when an already existing DocumentEntry has been retrieved from the database and populated into
     * memory
     * @param id ID of the document
     * @param passwordName Name of the document
     * @param documentDescription Description of what the document is/what it's for
     * @param dateSet The date the document was created/last modified
     * @param documentStorageReference The reference to the document in the user's storage container
     */
    public DocumentEntry(String id, String passwordName, String documentDescription, String masterUsername, String dateSet,
                         String documentStorageReference) {
        super(id, passwordName, null, dateSet);
        this.documentDescription = documentDescription;
        this.masterUsername = masterUsername;
        this.documentStorageReference = documentStorageReference;
    }

    /**
     * Gets the description for the DocumentEntry object
     * @return Description of what the document is/what it's for
     */
    public String getDocumentDescription() {
        return documentDescription;
    }

    /**
     * Sets the description for the DocumentEntry object
     * @param documentDescription Description to be assigned
     */
    public void setDocumentDescription(String documentDescription) {
        this.documentDescription = documentDescription;
    }

    /**
     * Gets the reference to the stored document in the user's storage container
     * @return Reference to the storage container blob location
     */
    public String getDocumentStorageReference() {
        return documentStorageReference;
    }

    /**
     * Sets the reference to the stored document in the user's storage container
     * @param documentStorageReference Storage reference to be assigned
     */
    public void setDocumentStorageReference(String documentStorageReference) {
        this.documentStorageReference = documentStorageReference;
    }

    /**
     * Gets the username this document belongs to
     * @return Master username which owns this document
     */
    public String getMasterUsername() {
        return masterUsername;
    }

    /**
     * Sets the username this document belongs to
     * @param masterUsername Master username to be assigned
     */
    public void setMasterUsername(String masterUsername) {
        this.masterUsername = masterUsername;
    }
}
