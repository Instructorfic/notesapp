package com.fic.notesapp.data.remote.api;

import com.fic.notesapp.domain.model.Category;
import com.fic.notesapp.domain.model.Note;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface ApiService {

    @GET("notes")
    Call<List<Note>> getAllNotes();

    @GET("notes/{id}")
    Call<Note> getNoteById(@Path("id") int id);

    @GET("categories")
    Call<List<Category>> getCategories();

    @POST("notes")
    Call<Note> createNote(@Body Note note);

    @PUT("notes/{id}")
    Call<Note> updateNote(@Path("id") int id, @Body Note note);

    @DELETE("notes/{id}")
    Call<Void> deleteNote(@Path("id") int id);
}
