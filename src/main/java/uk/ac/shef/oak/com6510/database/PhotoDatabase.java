package uk.ac.shef.oak.com6510.database;

import android.arch.persistence.db.SupportSQLiteDatabase;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;
import android.support.annotation.NonNull;

@android.arch.persistence.room.Database(entities = {ImageElement.class}, version = 1, exportSchema = false)

public abstract  class PhotoDatabase extends RoomDatabase {
    public abstract DAO dao();

    private static volatile PhotoDatabase INSTANCE;

    public static  PhotoDatabase getDatabase(final Context context){
        if (INSTANCE == null){
            synchronized (PhotoDatabase.class){
                if (INSTANCE == null){
                    INSTANCE = android.arch.persistence.room.Room.databaseBuilder(context.getApplicationContext(),
                            PhotoDatabase.class, "photo_databsae")
                            .fallbackToDestructiveMigration()
                    .addCallback(sRoomDatabaseCallback).build();
                }
            }
        }
        return  INSTANCE;
    }

    private static RoomDatabase.Callback sRoomDatabaseCallback = new RoomDatabase.Callback() {
        @Override
        public void onOpen(@NonNull SupportSQLiteDatabase db) {
            super.onOpen(db);

        }
    };
}
