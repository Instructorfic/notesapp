package com.fic.notesapp.ui;

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

public class AddNoteActivity extends AppCompatActivity {

    private EditText etTitle, etContent;
    private Spinner spinnerCategory;
    private Button btnSave;
    private NoteInteractor noteInteractor;
    private List<Category> categoryList;
    private Category selectedCategory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_note);

        etTitle = findViewById(R.id.noteTitle);
        etContent = findViewById(R.id.noteContent);
        spinnerCategory = findViewById(R.id.spinnerCategory);
        btnSave = findViewById(R.id.saveNoteButton);

        ApiService apiService = ApiClient.getRetrofitInstance().create(ApiService.class);
        noteInteractor = new NoteInteractor(new NoteRepository(apiService));

        btnSave.setOnClickListener(v -> saveNote());

        loadCategories();
    }

    private void loadCategories() {
        noteInteractor.getCategories(new Callback<List<Category>>() {
            @Override
            public void onResponse(Call<List<Category>> call, Response<List<Category>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    categoryList = response.body();
                    setupCategorySpinner();
                } else {
                    Toast.makeText(AddNoteActivity.this, "Error al cargar categorías", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Category>> call, Throwable t) {
                Toast.makeText(AddNoteActivity.this, "Error de conexión: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setupCategorySpinner() {
        ArrayAdapter<Category> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, categoryList);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCategory.setAdapter(adapter);

        spinnerCategory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                selectedCategory = categoryList.get(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                selectedCategory = null;
            }
        });
    }
//
    private void saveNote() {
        String title = etTitle.getText().toString().trim();
        String content = etContent.getText().toString().trim();

        if (TextUtils.isEmpty(title)) {
            etTitle.setError("El título es obligatorio");
            return;
        }

        if (TextUtils.isEmpty(content)) {
            etContent.setError("El contenido es obligatorio");
            return;
        }

        if (selectedCategory == null) {
            Toast.makeText(this, "Debe seleccionar una categoría", Toast.LENGTH_SHORT).show();
            return;
        }

        Note newNote = new Note();
        newNote.setTitle(title);
        newNote.setContent(content);
        newNote.setCategory(selectedCategory);

        noteInteractor.createNote(newNote, new Callback<Note>() {
            @Override
            public void onResponse(Call<Note> call, Response<Note> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(AddNoteActivity.this, "Nota agregada exitosamente", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(AddNoteActivity.this, "Error al agregar la nota", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Note> call, Throwable t) {
                Toast.makeText(AddNoteActivity.this, "Error de conexión: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
