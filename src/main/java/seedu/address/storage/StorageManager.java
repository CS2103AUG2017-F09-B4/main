package seedu.address.storage;

import java.awt.*;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Optional;
import java.util.logging.Logger;

import javax.imageio.ImageIO;

import com.google.common.eventbus.Subscribe;

import seedu.address.commons.core.ComponentManager;
import seedu.address.commons.core.LogsCenter;
import seedu.address.commons.events.model.AddressBookChangedEvent;
import seedu.address.commons.events.storage.DataSavingExceptionEvent;
import seedu.address.commons.exceptions.DataConversionException;
import seedu.address.model.ReadOnlyAddressBook;
import seedu.address.model.ReadOnlyMeetingList;
import seedu.address.model.UserPrefs;

/**
 * Manages storage of AddressBook data in local storage.
 */
public class StorageManager extends ComponentManager implements Storage {

    private static final Logger logger = LogsCenter.getLogger(StorageManager.class);
    private static final String READ_FILE_MESSAGE = "Attempting to read to data file: ";
    private static final String WRITE_FILE_MESSAGE = "Attempting to write to data file: ";
    public static final String CACHE_DIR = "cache/";
    private AddressBookStorage addressBookStorage;
    private MeetingListStorage meetingListStorage;
    private UserPrefsStorage userPrefsStorage;


    public StorageManager(AddressBookStorage addressBookStorage, MeetingListStorage meetingListStorage,
                          UserPrefsStorage userPrefsStorage) {
        super();
        this.addressBookStorage = addressBookStorage;
        this.meetingListStorage = meetingListStorage;
        this.userPrefsStorage = userPrefsStorage;

        // Create cache folder in case it is not there
        File cache = new File(CACHE_DIR);
        if (cache.mkdir()) {
            logger.info("Creating new folder cache/");
        }
    }

    // ================ UserPrefs methods ==============================

    @Override
    public String getUserPrefsFilePath() {
        return userPrefsStorage.getUserPrefsFilePath();
    }

    @Override
    public Optional<UserPrefs> readUserPrefs() throws DataConversionException, IOException {
        return userPrefsStorage.readUserPrefs();
    }

    @Override
    public void saveUserPrefs(UserPrefs userPrefs) throws IOException {
        userPrefsStorage.saveUserPrefs(userPrefs);
    }


    // ================ AddressBook methods ==============================

    @Override
    public String getAddressBookFilePath() {
        return addressBookStorage.getAddressBookFilePath();
    }

    @Override
    public Optional<ReadOnlyAddressBook> readAddressBook() throws DataConversionException, IOException {
        return readAddressBook(addressBookStorage.getAddressBookFilePath());
    }

    @Override
    public Optional<ReadOnlyAddressBook> readAddressBook(String filePath) throws DataConversionException, IOException {
        logger.fine(READ_FILE_MESSAGE + filePath);
        return addressBookStorage.readAddressBook(filePath);
    }

    @Override
    public void saveAddressBook(ReadOnlyAddressBook addressBook) throws IOException {
        saveAddressBook(addressBook, addressBookStorage.getAddressBookFilePath());
    }

    @Override
    public void saveAddressBook(ReadOnlyAddressBook addressBook, String filePath) throws IOException {
        logger.fine(WRITE_FILE_MESSAGE + filePath);
        addressBookStorage.saveAddressBook(addressBook, filePath);
    }

    @Override
    public void backupAddressBook(ReadOnlyAddressBook addressBook) throws IOException {
        addressBookStorage.backupAddressBook(addressBook);
    }

    public Optional<ReadOnlyAddressBook> restoreAddressBook() throws IOException, DataConversionException {
        return addressBookStorage.restoreAddressBook();
    }

    @Override
    @Subscribe
    public void handleAddressBookChangedEvent(AddressBookChangedEvent event) {
        logger.info(LogsCenter.getEventHandlingLogMessage(event, "Local data changed, saving to file"));
        try {
            saveAddressBook(event.data);
        } catch (IOException e) {
            raise(new DataSavingExceptionEvent(e));
        }
    }

    // ============ Meeting List methods ==================
    @Override
    public String getMeetingsFilePath() {
        return meetingListStorage.getMeetingsFilePath();
    }

    @Override
    public Optional<ReadOnlyMeetingList> readMeetingList() throws IOException, DataConversionException {
        return meetingListStorage.readMeetingList();
    }

    @Override
    public Optional<ReadOnlyMeetingList> readMeetingList(String filePath) throws DataConversionException, IOException {
        return meetingListStorage.readMeetingList(filePath);
    }

    @Override
    public void saveMeetingList(ReadOnlyMeetingList meetingList) throws IOException {
        meetingListStorage.saveMeetingList(meetingList);
    }

    @Override
    public void saveMeetingList(ReadOnlyMeetingList meetingList, String filePath) throws IOException {
        meetingListStorage.saveMeetingList(meetingList, filePath);
    }

    // ============= Cache & Download methods =======================

    @Override
    public void saveFileFromUrl(String urlString, String filename) {
        try {
            URL url = new URL(urlString);
            InputStream in = new BufferedInputStream(url.openStream());
            String filePath = CACHE_DIR + filename;

            File file = new File(filePath);
            file.createNewFile();

            OutputStream out = new BufferedOutputStream(new FileOutputStream(filePath));

            for (int i; (i = in.read()) != -1; ) {
                out.write(i);
            }
            in.close();
            out.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
