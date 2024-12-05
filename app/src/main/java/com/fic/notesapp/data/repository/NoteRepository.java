package com.fic.notesapp.data.repository;

import com.fic.notesapp.domain.model.Category;
import com.fic.notesapp.domain.model.Note;
import com.fic.notesapp.data.remote.api.ApiService;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;

public class NoteRepository {
    private final ApiService apiService;

    public NoteRepository(ApiService apiService) {
        this.apiService = apiService;
    }

    public void createNote(Note note, Callback<Note> callback) {
        Call<Note> call = apiService.createNote(note);
        call.enqueue(callback);
    }

    public void getNotes(Callback<List<Note>> callback) {
        Call<List<Note>> call = apiService.getAllNotes();
        call.enqueue(callback);
    }

    public void deleteNote(int noteId, Callback<Void> callback) {
        Call<Void> call = apiService.deleteNote(noteId);
        call.enqueue(callback);
    }

    public void updateNote(int noteId, Note note, Callback<Note> callback) {
        Call<Note> call = apiService.updateNote(noteId, note);
        call.enqueue(callback);
    }

    public void getCategories(Callback<List<Category>> callback) {
        Call<List<Category>> call = apiService.getCategories();
        call.enqueue(callback);
    }
}
