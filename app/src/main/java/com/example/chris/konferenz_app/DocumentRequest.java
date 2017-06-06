package com.example.chris.konferenz_app;

import java.util.ArrayList;

/**
 * Created by Chris on 05.06.2017.
 */

public class DocumentRequest {

    private String status, status_info, recipient, subject;
    private ArrayList<Document> documents;

    public DocumentRequest() {
    }

    public DocumentRequest(String status, String status_info, String recipient, String subject, ArrayList<Document> documents) {
        this.status = status;
        this.status_info = status_info;
        this.recipient = recipient;
        this.subject = subject;
        this.documents = documents;
    }

    public String getStatus() {
        return status;
    }

    public String getStatus_info() {
        return status_info;
    }

    public String getRecipient() {
        return recipient;
    }

    public String getSubject() {
        return subject;
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
