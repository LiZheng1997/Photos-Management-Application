/*
 * Copyright (c) 2017. This code has been developed by Fabio Ciravegna, The University of Sheffield. All rights reserved. No part of this code can be used without the explicit written permission by the author
 */

package uk.ac.shef.oak.com6510.database;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

@Entity(tableName = "ImageElement")
public class ImageElement implements Parcelable {
    @NonNull
    @PrimaryKey(autoGenerate = true)
    int id;

    @NonNull
    String description;
    @NonNull
    String title;
    @NonNull
    String date;

    @NonNull
    String filePath;
    @NonNull
    String nailPath;

    @NonNull
    Double longitude;

    @NonNull
    Double latitude;

    public ImageElement(@NonNull int id, @NonNull String description, @NonNull String title, @NonNull String date, @NonNull String filePath, @NonNull String nailPath, @NonNull Double longitude, @NonNull Double latitude) {
        this.id = id;
        this.description = description;
        this.title = title;
        this.date = date;
        this.filePath = filePath;
        this.nailPath = nailPath;
        this.longitude = longitude;
        this.latitude = latitude;
    }


    @NonNull
    public int getId() {
        return id;
    }

    public void setId(@NonNull int id) {
        this.id = id;
    }

    @NonNull
    public String getDescription() {
        return description;
    }

    public void setDescription(@NonNull String description) {
        this.description = description;
    }

    @NonNull
    public String getTitle() {
        return title;
    }

    public void setTitle(@NonNull String title) {
        this.title = title;
    }

    @NonNull
    public String getDate() {
        return date;
    }

    public void setDate(@NonNull String date) {
        this.date = date;
    }

    @NonNull
    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(@NonNull String filePath) {
        this.filePath = filePath;
    }

    @NonNull
    public String getNailPath() {
        return nailPath;
    }

    public void setNailPath(@NonNull String nailPath) {
        this.nailPath = nailPath;
    }

    @NonNull
    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(@NonNull Double longitude) {
        this.longitude = longitude;
    }

    @NonNull
    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(@NonNull Double latitude) {
        this.latitude = latitude;
    }

    @Override
    public String toString() {
        return "ImageElement{" +
                "description='" + description + '\'' +
                ", title='" + title + '\'' +
                ", date='" + date + '\'' +
                ", filePath='" + filePath + '\'' +
                ", nailPath='" + nailPath + '\'' +
                ", longitude=" + longitude +
                ", latitude=" + latitude +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(title);
        dest.writeString(description);
        dest.writeString(date);
        dest.writeString(filePath);
        dest.writeString(nailPath);
        dest.writeDouble(latitude);
        dest.writeDouble(longitude);
    }

    public static final Parcelable.Creator<ImageElement> CREATOR = new Parcelable.Creator<ImageElement>(){
        @Override
        public ImageElement createFromParcel(Parcel parcel) {

            return new ImageElement(parcel);
        }
        @Override
        public ImageElement[] newArray(int i) {
            return new ImageElement[i];
        }
};

    public ImageElement(Parcel in) {
        id = in.readInt();
        title = in.readString();
        description = in.readString();
        date = in.readString();
        filePath = in.readString();
        nailPath = in.readString();
        latitude = in.readDouble();
        longitude = in.readDouble();


    }


}
