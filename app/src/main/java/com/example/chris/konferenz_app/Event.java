package com.example.chris.konferenz_app;


import java.util.ArrayList;
import java.util.List;

/**
 * Created by Chris on 21.05.2017.
 */
public class Event {
    private int event_id;
    private String id, title, description, author, start, end, street, zip, city, location, url;
    List<Document> documents = new ArrayList<Document>();
    ;


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

    public void addDocument(Document document) {
        if (document != null) documents.add(document);
    }

    public int getEventId() {
        return event_id;
    }

    public String getId() {
        return id == null ? "" : id;
    }

    public String getTitle() {
        return title == null ? "Kein Titel vorhanden" : title;
    }

    public String getDescription() {
        return description == null ? "Keine zusätzlichen Informationen vorhanden" : description;
    }

    public String getAuthor() {
        return author == null ? "" : author;
    }

    public String getStart() {
        return start == null ? "Unbekannt" : start;
    }

    public String getEnd() {
        return end == null ? "Unbekannt" : end;
    }

    public String getStreet() {
        return street == null ? "" : street;
    }

    public String getZip() {
        return zip == null ? "" : zip;
    }

    public String getCity() {
        return city == null ? "" : city;
    }

    public String getLocation() {
        return location == null ? "" : location;
    }

    public String getUrl() {
        return url == null ? "" : url;
    }

    public int getDocumentAmount() {
        if (documents != null)
            return documents.size();
        else return 0;
    }

    public Document getDocument(int i) {
        return documents.get(i);
    }

}
