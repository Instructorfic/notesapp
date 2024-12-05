package com.fic.notesapp.domain;

import com.fic.notesapp.data.repository.NoteRepository;
import com.fic.notesapp.domain.model.Category;
import com.fic.notesapp.domain.model.Note;

import java.util.List;

import retrofit2.Callback;

public class NoteInteractor {

    private final NoteRepository noteRepository;

    public NoteInteractor(NoteRepository noteRepository) {
        this.noteRepository = noteRepository;
    }


    public void createNote(Note note, Callback<Note> callback) {
        noteRepository.createNote(note, callback);
    }

    public void loadNotes(Callback<List<Note>> callback) {
        noteRepository.getNotes(callback);
    }

    public void deleteNote(int noteId, Callback<Void> callback) {
        noteRepository.deleteNote(noteId, callback);
    }

    public void updateNote(int noteId, Note note, Callback<Note> callback) {
        noteRepository.updateNote(noteId, note, callback);
    }

    public void getCategories(Callback<List<Category>> callback) {
        noteRepository.getCategories(callback);
    }
}
