package com.fic.notesapp.ui;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.fic.notesapp.R;
import com.fic.notesapp.data.repository.NoteRepository;
import com.fic.notesapp.domain.NoteInteractor;
import com.fic.notesapp.domain.model.Category;
import com.fic.notesapp.domain.model.Note;
import com.fic.notesapp.data.remote.api.ApiClient;
import com.fic.notesapp.data.remote.api.ApiService;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EditNoteActivity extends AppCompatActivity {

    private EditText noteTitleInput, noteContentInput;
    private Button saveButton;
    private Spinner spinnerCategory;
    private NoteInteractor noteInteractor;
    private int noteId;
    private List<Category> categoryList;
    private Category selectedCategory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_note);

        noteTitleInput = findViewById(R.id.noteTitleInput);
        noteContentInput = findViewById(R.id.noteContentInput);
        spinnerCategory = findViewById(R.id.spinnerCategory);
        saveButton = findViewById(R.id.saveButton);

        // Configurar el interactor
        ApiService apiService = ApiClient.getRetrofitInstance().create(ApiService.class);
        noteInteractor = new NoteInteractor(new NoteRepository(apiService));

        Intent intent = getIntent();
        noteId = intent.getIntExtra("NOTE_ID", -1);
        String title = intent.getStringExtra("NOTE_TITLE");
        String content = intent.getStringExtra("NOTE_CONTENT");
        int categoryId = intent.getIntExtra("CATEGORY_ID", -1);

        noteTitleInput.setText(title);
        noteContentInput.setText(content);

        loadCategories(categoryId);

        saveButton.setOnClickListener(v -> saveNote());
    }

    private void loadCategories(int selectedCategoryId) {
        noteInteractor.getCategories(new Callback<List<Category>>() {
            @Override
            public void onResponse(Call<List<Category>> call, Response<List<Category>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    categoryList = response.body();
                    setupCategorySpinner(selectedCategoryId);
                } else {
                    Toast.makeText(EditNoteActivity.this, "Error al cargar categorías", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Category>> call, Throwable t) {
                Toast.makeText(EditNoteActivity.this, "Error de conexión: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setupCategorySpinner(int selectedCategoryId) {
        ArrayAdapter<Category> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, categoryList);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCategory.setAdapter(adapter);

        for (int i = 0; i < categoryList.size(); i++) {
            if (categoryList.get(i).getId() == selectedCategoryId) {
                spinnerCategory.setSelection(i);
                selectedCategory = categoryList.get(i);
                break;
            }
        }

        spinnerCategory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                selectedCategory = categoryList.get(position);
        }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                selectedCategory = null;
            }
            });
    }

    private void saveNote() {
        String title = noteTitleInput.getText().toString().trim();
        String content = noteContentInput.getText().toString().trim();

        if (TextUtils.isEmpty(title)) {
            noteTitleInput.setError("El título es obligatorio");
            return;
        }

        if (TextUtils.isEmpty(content)) {
            noteContentInput.setError("El contenido es obligatorio");
            return;
        }

        Note updatedNote = new Note();
        updatedNote.setId(noteId);
        updatedNote.setTitle(title);
        updatedNote.setContent(content);
        updatedNote.setCategory(selectedCategory);

        noteInteractor.updateNote(noteId, updatedNote, new Callback<Note>() {
            @Override
            public void onResponse(Call<Note> call, Response<Note> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(EditNoteActivity.this, "Nota actualizada exitosamente", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Log.e(EditNoteActivity.class.getSimpleName(), response.raw().toString());
                    Toast.makeText(EditNoteActivity.this, "Error al actualizar la nota", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Note> call, Throwable t) {
                Toast.makeText(EditNoteActivity.this, "Error de conexión: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
