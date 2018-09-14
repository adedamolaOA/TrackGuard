package ade.leke.com.trackguard.db;

import android.provider.BaseColumns;

/**
 * Created by SecureUser on 9/18/2015.
 */
public final class FeedReaderContract {
    // To prevent someone from accidentally instantiating the contract class,
    // give it an empty constructor.

    public FeedReaderContract() {


    }

    public boolean createDB(){
        boolean state = false;


        return state;
    }

    /* Inner class that defines the table contents */
    public static abstract class FeedEntry implements BaseColumns {
        public static final String TABLE_NAME = "contact_list";
        public static final String COLUMN_NAME_Contact_ID = "cid";
        public static final String COLUMN_NAME_NAME = "firstname";
        public static final String COLUMN_NAME_LASTNAME = "lastname";
        public static final String COLUMN_NAME_Mobile_NUmber = "phone";
        public static final String COLUMN_NAME_SIM = "sim";

        public static final String TABLE_NAME_NOTIFICATION = "notification";
        public static final String COLUMN_NAME_CONTACT = "contact";
        public static final String COLUMN_NAME_REQUEST = "request";
        public static final String COLUMN_NAME_DATE = "date";
        public static final String COLUMN_NAME_STATUS = "status";

        public static final String TABLE_NAME_USER = "user";
        public static final String COLUMN_NAME_UFIRSTNAME = "firstname";
        public static final String COLUMN_NAME_ULASTNAME = "lastname";
        public static final String COLUMN_NAME_UMOBILE_NUMBER = "mobile";
        public static final String COLUMN_NAME_UEMAIL = "email";
        public static final String COLUMN_NAME_VERSION = "version";
        public static final String COLUMN_NAME_PASSCODE = "passcode";
        public static final String COLUMN_NAME_USTATUS = "status";


        public static final String TABLE_NAME_PAY = "paydate";
        public static final String COLUMN_NAME_PAY_DATE = "date";
        public static final String COLUMN_NAME_UPDATE_DATE = "update_date";
        public static final String COLUMN_NAME_IS_UPDATE = "isUpdated";
        public static final String COLUMN_NAME_OWING = "amount";
        public static final String COLUMN_NAME_PAY_STATUS = "status";

        public static final String TABLE_NAME_Movement = "movement";
        public static final String COLUMN_NAME_LAT = "lat";
        public static final String COLUMN_NAME_LNG = "lng";
        public static final String COLUMN_NAME_ADDRESS = "address";
        public static final String COLUMN_NAME_MDATE = "date";
        public static final String COLUMN_NAME_UID = "uid";

        public static final String TABLE_NAME_NOTIFICATION_LOCAL = "local_notification";
        public static final String COLUMN_NAME_LUID = "uid";
        public static final String COLUMN_NAME_CONTACT_NUMBER = "contact";
        public static final String COLUMN_NAME_LREQUEST = "request";
        public static final String COLUMN_NAME_MESSAGE = "message";
        public static final String COLUMN_NAME_RESPONSE = "response";
        public static final String COLUMN_NAME_LDATE = "date";
        public static final String COLUMN_NAME_LSTATUS = "status";

        public static final String TABLE_NAME_SETTINGS = "local_settings";
        public static final String COLUMN_NAME_SUID = "uid";
        public static final String COLUMN_NAME_PINTERVAL = "pInterval";
        public static final String COLUMN_NAME_MINTERVAL = "mInterval";
        public static final String COLUMN_NAME_MGS = "mgs";
        public static final String COLUMN_NAME_SMS = "pSMS";
        public static final String COLUMN_NAME_GDARK = "gDark";
        public static final String COLUMN_NAME_NUMBER_OF_SMS = "numberOfSMS";
        public static final String COLUMN_NAME_SSTATUS = "status";

        public static final String TABLE_NAME_PANIC = "panic";
        public static final String COLUMN_NAME_PUID = "uid";
        public static final String COLUMN_NAME_PLAT = "lat";
        public static final String COLUMN_NAME_PLNG = "lng";
        public static final String COLUMN_NAME_PADDRESS = "address";
        public static final String COLUMN_NAME_PDATE = "date";
        public static final String COLUMN_NAME_PSTATUS= "status";

        public static final String TABLE_NAME_THEME = "theme";
        public static final String COLUMN_NAME_TNAME = "name";
        public static final String COLUMN_NAME_PC = "pc";
        public static final String COLUMN_NAME_PCD = "pcd";
        public static final String COLUMN_NAME_TBG = "bg";
        public static final String COLUMN_NAME_TSTATUS = "status";

        public static final String TABLE_NAME_NEWS = "news";
        public static final String COLUMN_NAME_NUID= "uuid";
        public static final String COLUMN_NAME_NCLIENT = "client";
        public static final String COLUMN_NAME_NEWS = "news";
        public static final String COLUMN_NAME_NSUBJECT = "subject";
        public static final String COLUMN_NAME_NIMAGE = "image";
        public static final String COLUMN_NAME_NDATE = "date";
        public static final String COLUMN_NAME_NBulletin = "bulletin";
        public static final String COLUMN_NAME_NAuthor = "author";
        public static final String COLUMN_NAME_NSTATUS = "status";

    }
}
