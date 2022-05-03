package com.jamesd.passwordmanager.Wrappers;

import com.jamesd.passwordmanager.Models.Passwords.CreditDebitCardEntry;
import javafx.scene.image.ImageView;

/**
 * Wrapper class for a Credit/Debit card entry
 */
public class CreditDebitCardEntryWrapper extends BaseWrapper {

    private CreditDebitCardEntry creditDebitCardEntry;

    /**
     * Default constructor
     */
    public CreditDebitCardEntryWrapper() {

    }

    /**
     * Constructor which instantiates the CreditDebitCardEntry object to be wrapped, as well as the ImageView favicon
     * @param creditDebitCardEntry
     * @param favicon
     */
    public CreditDebitCardEntryWrapper(CreditDebitCardEntry creditDebitCardEntry, ImageView favicon) {
        super(favicon);
        this.creditDebitCardEntry = creditDebitCardEntry;
    }

    /**
     * Retrieves the CreditDebitCardEntry object
     * @return CreditDebitCardEntry object which has been wrapped by this class
     */
    public CreditDebitCardEntry getCreditDebitCardEntry() {
        return creditDebitCardEntry;
    }

    public void setCreditDebitCardEntry(CreditDebitCardEntry creditDebitCardEntry) {
        this.creditDebitCardEntry = creditDebitCardEntry;
    }
}
