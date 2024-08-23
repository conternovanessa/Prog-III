package com.example.progetto_shit.Model;
import java.util.ArrayList;
import java.util.List;

public class EmailObservable {
    private List<EmailObserver> observers = new ArrayList<>();
    private List<String> emails = new ArrayList<>();

    public void addObserver(EmailObserver observer) {
        observers.add(observer);
    }

    public void removeObserver(EmailObserver observer) {
        observers.remove(observer);
    }

    public void notifyObservers() {
        for (EmailObserver observer : observers) {
            observer.update(emails);
        }
    }

    public void addEmail(String email) {
        emails.add(email);
        notifyObservers();
    }

    public List<String> getEmails() {
        return emails;
    }
}
