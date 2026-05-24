package com.nadia.caslab.command;

// command pattern, interface dasar untuk semua perintah input.
public interface Command {
    void execute();
    void undo();   // untuk fitur undo gerakan (opsional)
}
