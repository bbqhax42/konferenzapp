package com.example.chris.konferenz_app;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Chris on 21.05.2017.
 */
public class Event {
   private  int event_id;
    private String id, title, description, author, start, end, street, zip, city, location, url;
    List<Document> documents;


    public Event() {
    }

    public void setEvent_id(int event_id) {
        this.event_id = event_id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public void setStart(String start) {
        this.start = start;
    }

    public void setEnd(String end) {
        this.end = end;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public void setZip(String zip) {
        this.zip = zip;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public void addDocument(Document document){
        documents.add(document);
    }

    public Event(int event_id, String id, String title, String description, String author, String start, String end, String street, String zip, String city, String location, String url, List<Document> documents) {
        this.event_id = event_id;
        this.id=id;
        this.title = title;
        this.description = description;
        this.author = author;
        this.start = start;
        this.end = end;
        this.street = street;
        this.zip = zip;
        this.city = city;
        this.location = location;
        this.url = url;
        this.documents = documents;
    }

    public int getEventId(){
        return event_id;
    }

    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getAuthor() {
        return author;
    }

    public String getStart() {
        return start;
    }

    public String getEnd() {
        return end;
    }

    public String getStreet() {
        return street;
    }

    public String getZip() {
        return zip;
    }

    public String getCity() {
        return city;
    }

    public String getLocation() {
        return location;
    }

    public String getUrl() {
        return url;
    }

    public int getDocumentAmount() {
        if(documents!=null)
            return documents.size();
        else return 0;
    }

    public Document getDocument(int i){
        return documents.get(i);
    }

}
